/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service.workflow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.service.WorkKanbanEntityFacadeREST;
import jp.adtekfuji.bpmn.model.BpmnModelUtility;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ロット生産用ワークフローモデル
 *
 * @author s-heya
 */
public class LotWorkflowModelFacede implements WorkflowInteface {
    private final Logger logger = LogManager.getLogger();
    private final WorkKanbanEntityFacadeREST workKandanREST;
    private final KanbanEntity kanban;
    private final BpmnModelUtility utility;

    /**
     * コンストラクタ
     */
    private LotWorkflowModelFacede() {
        this.workKandanREST = null;
        this.kanban = null;
        this.utility = null;
    }

    /**
     * コンストラクタ
     *
     * @param workKandanREST
     * @param workKanbans
     * @param xml
     * @throws Exception
     */
    private LotWorkflowModelFacede(WorkKanbanEntityFacadeREST workKandanREST, KanbanEntity kanban, String xml) throws Exception {
        this.workKandanREST = workKandanREST;
        this.kanban = kanban;
        this.utility = new BpmnModelUtility(xml);
    }

    /**
     * LotWorkflowModelFacedeオブジェクトを生成する。
     *
     * @param workKandanREST
     * @param kanban
     * @param xml
     * @return WorkflowInteface
     * @throws java.lang.Exception
     */
    public static WorkflowInteface createInstance(WorkKanbanEntityFacadeREST workKandanREST, KanbanEntity kanban, String xml) throws Exception {
        return new LotWorkflowModelFacede(workKandanREST, kanban, xml);
    }

    /**
     * 工程順を進める。
     *
     * @param workId 工程ID
     * @param serialNumbers シリアル番号
     * @throws Exception
     */
    @Override
    public boolean executeWorkflow(Long workId, List<String> serialNumbers) throws Exception {
        try {
            logger.info("executeWorkflow : {}, {}", workId, serialNumbers);

            String nodeId = Objects.nonNull(workId) ?  String.valueOf(workId) : null;
            return this.executeWorkflowImp(nodeId, serialNumbers);
        } finally {
            logger.info("executeWorkflow end.");
        }
    }

    /**
     * 工程順を進める。
     *
     * @param nodeId ノードID
     * @param serialNumbers シリアル番号
     * @throws Exception
     */
    private boolean executeWorkflowImp(String nodeId, List<String> serialNumbers) throws Exception {
        List<BpmnNode> nodes;
        if (Objects.isNull(nodeId)) {
            nodes = this.utility.getNextNode(this.utility.getStartNode().getId());
        } else {
            nodes = this.utility.getNextNode(nodeId);
        }
        
        if (nodes.isEmpty()) {
            // ワークフロー終了
            logger.info("Workflow finished.");
            return true;
        }
        
        if (Objects.nonNull(nodeId) && Objects.nonNull(serialNumbers) && !serialNumbers.isEmpty()) {
            int serialNumber = Integer.parseInt(serialNumbers.get(0));

            for (BpmnNode node : nodes) {
                if (node instanceof BpmnTask) {
                    Long nextId = Long.parseLong(node.getId());
                    WorkKanbanEntity next = this.workKandanREST.findBySerial(this.kanban.getKanbanId(), nextId, serialNumber);
                    if (Objects.isNull(next)) {
                        continue;
                    }

                    if (next.getSkipFlag()) {
                        // 検索した工程がスキップの場合は、さらに次の工程を検索
                        executeWorkflow(next.getWorkId(), serialNumbers);
                        this.workKandanREST.updateImplementFlag(this.kanban.getKanbanId(), nextId, serialNumber);
                    } else {
                        // 実施フラグを更新
                        this.workKandanREST.updateImplementFlag(this.kanban.getKanbanId(), nextId, serialNumber);
                    }

                } else if (node instanceof BpmnParallelGateway) {
                    List<WorkKanbanEntity> workKanbans = workKandanREST.findBySerial(this.kanban.getKanbanId(), serialNumber);
                    if (Objects.isNull(workKanbans)) {
                        continue;
                    }

                    Map<Long, WorkKanbanEntity> workKanbanMap = new HashMap<>();
                    for (WorkKanbanEntity workKanban : workKanbans) {
                        workKanbanMap.put(workKanban.getWorkId(), workKanban);
                    }

                    // 並列工程が全て完了していれば、次の工程に進める。
                    boolean isCompleted;
                    BpmnParallelGateway pair = (BpmnParallelGateway)((BpmnParallelGateway) node).getPairedGateway();
                    if (this.utility.isStartGateway((BpmnParallelGateway) node, pair)) {
                        isCompleted = this.checkForward((BpmnParallelGateway) node, node.getId(), workKanbanMap);
                    } else {
                        isCompleted = this.checkNext(pair, pair.getId(), workKanbanMap);
                    }

                    if (isCompleted) {
                        this.executeWorkflowImp(node.getId(), serialNumbers);
                    }
                }
            }
        } else {
            // カンバンステータスが計画中から計画済みに遷移した場合
            for (BpmnNode node : nodes) {
                if (node instanceof BpmnTask) {
                    Long nextId = Long.parseLong(node.getId());
                    // 実施フラグを更新
                    this.workKandanREST.updateImplementFlag(this.kanban.getKanbanId(), nextId, null);

                } else if (node instanceof BpmnParallelGateway) {
                    for (int i = 0; i < this.kanban.getLotQuantity(); i++) {
                        this.executeWorkflowImp(node.getId(), Arrays.asList(String.valueOf(i + 1)));
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * 並列工程が全て完了しているかを返す。
     * 
     * @param gateway
     * @param nodeId
     * @param workKanbanMap
     * @return
     * @throws Exception 
     */
    private boolean checkNext(BpmnParallelGateway gateway, String nodeId, Map<Long, WorkKanbanEntity> workKanbanMap) throws Exception {
        List<BpmnNode> nodes = utility.getNextNode(nodeId);
        for (BpmnNode node : nodes) {
            if (node instanceof BpmnTask) {
                WorkKanbanEntity forward = workKanbanMap.get(Long.parseLong(node.getId()));
                if (Objects.isNull(forward)) {
                    logger.fatal("Not found work kanban:{}", node.getId());
                    continue;
                }
                if (!forward.getSkipFlag() && !(forward.getWorkStatus() == KanbanStatusEnum.COMPLETION)) {
                    return false;
                }
                if (!this.checkNext(gateway, node.getId(), workKanbanMap)) {
                    return false;
                }
            } else if (node instanceof BpmnParallelGateway) {
                if (!gateway.getPairedGateway().getId().equals(node.getId())) {
                    if (!this.checkNext((BpmnParallelGateway) node, node.getId(), workKanbanMap)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 並列工程が全て完了しているかを返す。
     * 
     * @param gateway
     * @param nodeId
     * @param workKanbanMap
     * @return
     * @throws Exception 
     */
    private boolean checkForward(BpmnParallelGateway gateway, String nodeId, Map<Long, WorkKanbanEntity> workKanbanMap) throws Exception {
        List<BpmnNode> nodes = utility.getForwardNode(nodeId);
        for (BpmnNode node : nodes) {
            if (node instanceof BpmnTask) {
                WorkKanbanEntity forward = workKanbanMap.get(Long.parseLong(node.getId()));
                if (Objects.isNull(forward)) {
                    logger.fatal("Not found work kanban:{}", node.getId());
                    continue;
                }
                if (!forward.getSkipFlag() && !(forward.getWorkStatus() == KanbanStatusEnum.COMPLETION)) {
                    return false;
                }
                if (!this.checkForward(gateway, node.getId(), workKanbanMap)) {
                    return false;
                }
            } else if (node instanceof BpmnParallelGateway) {
                if (!gateway.getPairedGateway().getId().equals(node.getId())) {
                    if (!this.checkForward((BpmnParallelGateway) node, node.getId(), workKanbanMap)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
