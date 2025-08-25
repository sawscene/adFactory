/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service.workflow;

import adtekfuji.utility.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.job.KanbanProduct;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.service.WorkKanbanEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.bpmn.model.BpmnModelUtility;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class WorkflowModelFacade implements WorkflowInteface {

    private final WorkKanbanEntityFacadeREST workKandanREST;
    private final Map<Long, WorkKanbanEntity> workKanbanHashSet = new HashMap<>();
    private final BpmnModelUtility utility;
    private final Logger logger = LogManager.getLogger();

    private WorkflowModelFacade() {
        this.workKandanREST = null;
        this.utility = null;
    }

    private WorkflowModelFacade(WorkKanbanEntityFacadeREST workKandanREST, List<WorkKanbanEntity> workKanbans, String xml) throws Exception {
        this.workKandanREST = workKandanREST;
        for (WorkKanbanEntity workKanban : workKanbans) {
            workKanbanHashSet.put(workKanban.getWorkId(), workKanban);
        }
        this.utility = new BpmnModelUtility(xml);
    }

    /**
     * createInstance
     *
     * @param workKandanREST WorkKanbanEntityFacadeREST
     * @param workKanbans WorkKanbanEntity collection
     * @param xml XMLデータ
     * @return WorkflowInteface
     * @throws java.lang.Exception
     */
    public static WorkflowInteface createInstance(WorkKanbanEntityFacadeREST workKandanREST, List<WorkKanbanEntity> workKanbans, String xml) throws Exception {
        return new WorkflowModelFacade(workKandanREST, workKanbans, xml);
    }

    /**
     * 工程順を進める。
     *
     * @param workId 工程ID
     * @param serialNumbers シリアル番号
     * @return 
     * @throws java.lang.Exception
     */
    @Override
    public boolean executeWorkflow(Long workId, List<String> serialNumbers) throws Exception {
        try {
            //logger.debug("executeWorkflow start: {}", workId);
            String nodeId;
            if (Objects.isNull(workId)) {
                nodeId = utility.getStartNode().getId();
            } else {
                nodeId = String.valueOf(workId);
            }
            return executeWorkflowImp(nodeId, serialNumbers);
        } finally {
            //logger.debug("executeWorkflow end.");
        }
    }

    private boolean executeWorkflowImp(String workId, List<String> serialNumbers) throws Exception {
        //次の工程を取得.
        List<BpmnNode> nodes = utility.getNextNode(workId);
        for (BpmnNode node : nodes) {
            //logger.debug("Check next node:{}", node);

            if (node instanceof BpmnTask) {
                WorkKanbanEntity next = workKanbanHashSet.get(Long.parseLong(node.getId()));
                if (Objects.isNull(next)) {
                    logger.fatal("Not found work kanban:{}", node.getId());
                    continue;
                }

                if (next.getSkipFlag()) {
                    if (!next.getImplementFlag()) {
                        // スキップする工程を、スキップ解除で実施できるようにするため
                        this.updateImplement(next, serialNumbers);
                    }

                    // 検索した工程がスキップの場合、次の工程を検索
                    executeWorkflow(next.getWorkId(), null);

                } else if (next.getWorkStatus() == KanbanStatusEnum.PLANNED) {
                    // 計画済の場合、実施フラグを更新
                    this.updateImplement(next, serialNumbers);

                } else if (next.getWorkStatus() == KanbanStatusEnum.COMPLETION) {
                    // 検索した工程が完了の場合、次の工程を検索
                    executeWorkflow(next.getWorkId(), null);

                } else {
                    if (next.getImplementFlag() && Objects.nonNull(serialNumbers)) {
                        this.updateImplement(next, serialNumbers);
                    }
                }

            } else if (node instanceof BpmnParallelGateway) {
                boolean isPermit;
                BpmnParallelGateway pair = (BpmnParallelGateway)((BpmnParallelGateway) node).getPairedGateway();
                if (this.utility.isStartGateway((BpmnParallelGateway) node, pair)) {
                    isPermit = executeParallelGateway((BpmnParallelGateway) node, node.getId());
                } else {
                    isPermit = executeGateway(pair, pair.getId());
                }
                
                if (isPermit) {
                    //並列ゲートウェイ内の工程が全て終わっているので、次に進める.
                    executeWorkflowImp(node.getId(), serialNumbers);
                }
            }
        }
        
        return false;
    }

    private boolean executeGateway(BpmnParallelGateway gateway, String workId) throws Exception {
        List<BpmnNode> nodes = utility.getNextNode(workId);
        for (BpmnNode node : nodes) {
            //logger.debug("Check node:{}", node);
            if (node instanceof BpmnTask) {
                WorkKanbanEntity forward = workKanbanHashSet.get(Long.parseLong(node.getId()));
                if (Objects.isNull(forward)) {
                    logger.fatal("Not found work kanban:{}", node.getId());
                    continue;
                }
                
                if (!forward.getSkipFlag() && !KanbanStatusEnum.COMPLETION.equals(forward.getWorkStatus())) {
                    if (StringUtils.isEmpty(forward.getServiceInfo())) {
                        return false;
                    }

                    List<KanbanProduct> products = KanbanProduct.lookupProductList(forward.getServiceInfo());
                    if (!products.stream().filter(o -> KanbanStatusEnum.COMPLETION.equals(o.getStatus())).findFirst().isPresent()) {
                        // 完了したロットが存在しない
                        return false;
                    }
                }

                if (!executeGateway(gateway, node.getId())) {
                    return false;
                }

            } else if (node instanceof BpmnParallelGateway) {
                if (StringUtils.equals(gateway.getPairedGateway().getId(), node.getId())) {
                    return true;
                }

                if (!executeGateway(gateway, node.getId())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    //並列ゲートウェイの場合はペアとなるゲートウェイまでさかのぼって、完了かスキップしていたら次の工程に進める.
    private boolean executeParallelGateway(BpmnParallelGateway gateway, String workId) throws Exception {
        //前の工程を取得.
        List<BpmnNode> nodes = utility.getForwardNode(workId);
        for (BpmnNode node : nodes) {
            //logger.debug("check forward node:{}", node);
            if (node instanceof BpmnTask) {
                WorkKanbanEntity forward = workKanbanHashSet.get(Long.parseLong(node.getId()));
                if (Objects.isNull(forward)) {
                    logger.fatal("not found work kanban:{}", node.getId());
                    continue;
                }

                if (!forward.getSkipFlag() && !KanbanStatusEnum.COMPLETION.equals(forward.getWorkStatus())) {
                    if (StringUtils.isEmpty(forward.getServiceInfo())) {
                        return false;
                    }

                    List<KanbanProduct> products = KanbanProduct.lookupProductList(forward.getServiceInfo());
                    if (!products.stream().filter(o -> KanbanStatusEnum.COMPLETION.equals(o.getStatus())).findFirst().isPresent()) {
                        // 完了したロットが存在しない
                        return false;
                    }
                }
                
                if (!executeParallelGateway(gateway, node.getId())) {
                    return false;
                }

            } else if (node instanceof BpmnParallelGateway) {
                //ペアではないゲートウェイの場合は更に調べる.
                if (!StringUtils.equals(gateway.getPairedGateway().getId(), node.getId())) {
                    if (!executeParallelGateway((BpmnParallelGateway) node, node.getId())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 工程を実施可能状態に更新する。
     * 
     * @param workKanban 工程カンバン情報
     * @param serialNumbers シリアル番号
     */
    private void updateImplement(WorkKanbanEntity workKanban, List<String> serialNumbers) {
        
        try {
            workKanban.setImplementFlag(true);

            if (!StringUtils.isEmpty(workKanban.getServiceInfo())) {
                List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(workKanban.getServiceInfo(), ServiceInfoEntity[].class);
                for (ServiceInfoEntity serviceInfo : serviceInfos) {
                    if (StringUtils.equals(serviceInfo.getService(), ServiceInfoEntity.SERVICE_INFO_PRODUCT)) {
                        List<KanbanProduct> products = KanbanProduct.toKanbanProducts(serviceInfo);
                        
                        if (Objects.isNull(serialNumbers)) {
                            products.stream().forEach(o -> o.setImplement(true));
                        } else {
                            products.stream().filter(o -> serialNumbers.contains(o.getUid()))
                                .forEach(o -> o.setImplement(true));
                        }

                        serviceInfo.setJob(products);
                        workKanban.setServiceInfo(JsonUtils.objectsToJson(serviceInfos));
                        break;
                    }
                }
            }

            workKandanREST.editWorkKanban(workKanban);
        } finally {
            logger.info("updateImplement: workName={}, serialNumbers={}", workKanban.getWorkName(), serialNumbers);
        }
    }
}
