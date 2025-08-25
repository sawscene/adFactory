/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils.fuji;

import adtekfuji.admanagerapp.productionnaviplugin.entity.fuji.ImportOrder;
import adtekfuji.admanagerapp.productionnaviplugin.entity.fuji.UnitTemplateInfo;
import adtekfuji.admanagerapp.productionnaviplugin.jdbc.adfactoryforfujidb.AdFactoryForFujiDbAccessor;
import adtekfuji.utility.StringUtils;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import jp.adtekfuji.bpmn.model.BpmnModel;
import jp.adtekfuji.bpmn.model.BpmnModeler;
import jp.adtekfuji.bpmn.model.entity.BpmnDocument;
import jp.adtekfuji.bpmn.model.entity.BpmnEndEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnExclusiveGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnInclusiveGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnProcess;
import jp.adtekfuji.bpmn.model.entity.BpmnSequenceFlow;
import jp.adtekfuji.bpmn.model.entity.BpmnStartEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import jp.adtekfuji.bpmn.model.entity.BpmnTerminateEndEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレートのワークフロープロセス
 *
 * @author nar-nakamura
 */
public class UnitWorkflowProcess {

    private final Logger logger = LogManager.getLogger();

    private final List<String> UNIT_TEMPLATE_DELIMITER = Arrays.asList("@", "＠");

    private final long lineNo;
    private final ImportOrder importOrder;
    private final List<String> unitTemplateNames = new LinkedList();

    private UnitTemplateInfo unitTemplate;
    private BpmnProcess bpmnProcess;

    /**
     * コンストラクタ
     *
     * @param lineNo 行番号
     * @param importOrder 計画情報インポート用データ
     */
    public UnitWorkflowProcess(long lineNo, ImportOrder importOrder) {
        this.lineNo = lineNo;
        this.importOrder = importOrder;

        // ユニットテンプレート名
        for (String delimiter : UNIT_TEMPLATE_DELIMITER) {
            String templateName = new StringBuilder()
                    .append(this.importOrder.getProductName())// 品名
                    .append(delimiter)
                    .append(this.importOrder.getProcessName())// 工程名
                    .toString();
            this.unitTemplateNames.add(templateName);
        }
    }

    /**
     * 初期情報を元に、クラス変数にデータを読み込む。
     */
    public void readData() {
        if (this.setUnitTemplate()) {
            this.setBpmnProcess();
        }
    }

    /**
     * ユニット情報をデータベースから取得して、クラス変数にセットする。
     */
    private boolean setUnitTemplate() {
        boolean result = false;
        try {
            AdFactoryForFujiDbAccessor forFujiDb = new AdFactoryForFujiDbAccessor();
            for (String templateName : this.unitTemplateNames) {
                this.unitTemplate = forFujiDb.getUnitTemplate(templateName);
                if (Objects.nonNull(this.unitTemplate)) {
                    result = true;
                    break;
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return result;
    }

    /**
     * ユニットテンプレートのワークフロー図からBPMNプロセスを取得して、クラス変数にセットする。
     */
    private void setBpmnProcess() {
        BpmnModel bpmnModel = BpmnModeler.getModeler();
        try {
            BpmnDocument bpmn = BpmnDocument.unmarshal(this.unitTemplate.getWorkflowDiaglam());
            bpmnModel.createModel(bpmn);
        } catch (JAXBException ex) {
            logger.fatal(ex, ex);
        }

        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
    }

    /**
     * 行番号を取得する。
     *
     * @return 行番号
     */
    public long getLineNo() {
        return this.lineNo;
    }

    /**
     * 計画情報インポート用データを取得する。
     *
     * @return 計画情報インポート用データ
     */
    public ImportOrder getImportOrder() {
        return this.importOrder;
    }

    /**
     * ユニットテンプレート名を取得する。
     *
     * @return ユニットテンプレート名
     */
    public String getUnitTemplateName() {
        if (Objects.nonNull(this.unitTemplate)) {
            // ユニットテンプレート読み込み済の場合、読み込んだ名前を返す。
            return this.unitTemplate.getUnitTemplateName();
        } else {
            // ユニットテンプレート未読み込みの場合、最初の読み込み候補の名前を返す。
            return this.unitTemplateNames.get(0);
        }
    }

    /**
     * ユニットテンプレート情報を取得する。
     *
     * @return ユニットテンプレート情報
     */
    public UnitTemplateInfo getUnitTemplateInfo() {
        return this.unitTemplate;
    }

    /**
     * BPMNプロセスを取得する。
     *
     * @return BPMNプロセス
     */
    public BpmnProcess getBpmnProcess() {
        return this.bpmnProcess;
    }

    /**
     * BPMN開始イベント一覧を取得する。
     *
     * @return BPMN開始イベント一覧
     */
    public List<BpmnStartEvent> getStartEventCollection() {
        return this.bpmnProcess.getStartEventCollection();
    }

    /**
     * ノードを取得する。
     *
     * @param id　ノードID
     * @return BPMNノード
     */
    public BpmnNode getNode(String id) {

        if (Objects.nonNull(this.bpmnProcess.getStartEventCollection())) {
            Optional<BpmnStartEvent> opt = this.bpmnProcess.getStartEventCollection().stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst();
            if (opt.isPresent()) {
                return opt.get();
            }
        }

        if (Objects.nonNull(this.bpmnProcess.getEndEventCollection())) {
            Optional<BpmnEndEvent> opt = this.bpmnProcess.getEndEventCollection().stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst();
            if (opt.isPresent()) {
                return opt.get();
            }
        }

        if (Objects.nonNull(this.bpmnProcess.getTerminateEndEventCollection())) {
            Optional<BpmnTerminateEndEvent> opt = this.bpmnProcess.getTerminateEndEventCollection().stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst();
            if (opt.isPresent()) {
                return opt.get();
            }
        }

        if (Objects.nonNull(this.bpmnProcess.getTaskCollection())) {
            Optional<BpmnTask> opt = this.bpmnProcess.getTaskCollection().stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst();
            if (opt.isPresent()) {
                return opt.get();
            }
        }

        if (Objects.nonNull(this.bpmnProcess.getParallelGatewayCollection())) {
            Optional<BpmnParallelGateway> opt = this.bpmnProcess.getParallelGatewayCollection().stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst();
            if (opt.isPresent()) {
                return opt.get();
            }
        }

        if (Objects.nonNull(this.bpmnProcess.getExclusiveGatewayCollection())) {
            Optional<BpmnExclusiveGateway> opt = this.bpmnProcess.getExclusiveGatewayCollection().stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst();
            if (opt.isPresent()) {
                return opt.get();
            }
        }

        if (Objects.nonNull(this.bpmnProcess.getInclusiveGatewayCollection())) {
            Optional<BpmnInclusiveGateway> opt = this.bpmnProcess.getInclusiveGatewayCollection().stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst();
            if (opt.isPresent()) {
                return opt.get();
            }
        }

        return null;
    }

    /**
     * 指定ノードの次のシーケンス一覧を取得する。
     *
     * @param id ノードID
     * @return 次のシーケンス一覧
     */
    public List<BpmnSequenceFlow> getNextFlows(String id) {
        return this.bpmnProcess.getSequenceFlowCollection().stream()
                .filter(p -> p.getSourceRef().equals(id))
                .collect(Collectors.toList());
    }

    /**
     * 指定ノードの前のシーケンス一覧を取得する。
     *
     * @param id ノードID
     * @return 前のシーケンス一覧
     */
    public List<BpmnSequenceFlow> getPrevFlows(String id) {
        return this.bpmnProcess.getSequenceFlowCollection().stream()
                .filter(p -> p.getTargetRef().equals(id))
                .collect(Collectors.toList());
    }

    /**
     * 指定ノードの進捗フラグを取得する。
     *
     * @param id ノードID
     * @return 進捗フラグ(0:なし, 1:着工, 2:完工, 3:着工完工)
     */
    public int getProgressFlag(String id) {
        String firstTaskId = getFirstTaskId();
        String lastTaskId = getLastTaskId();
        if (StringUtils.equals(id, firstTaskId) && StringUtils.equals(id, lastTaskId)) {
            return 3;
        } else if (StringUtils.equals(id, lastTaskId)) {
            return 2;
        } else if (StringUtils.equals(id, firstTaskId)) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 先頭タスクのノードIDを取得する。
     *
     * @return 先頭タスクのノードID
     */
    private String getFirstTaskId() {
        BpmnStartEvent startEvent = this.bpmnProcess.getStartEventCollection().get(0);
        return this.getNextTaskId(startEvent.getId());
    }

    /**
     * 指定ノードの次のタスクIDを取得する。
     *
     * @param id ノードID
     * @return 次のタスクID
     */
    private String getNextTaskId(String id) {
        final BpmnNode bpmnNode = this.getNode(id);

        if (bpmnNode instanceof BpmnEndEvent) {
            return null;
        } else if (bpmnNode instanceof BpmnTask) {
            return id;
        }

        return this.getNextTaskId(this.getNextFlows(id).get(0).getTargetRef());
    }

    /**
     * 最終タスクのノードIDを取得する。
     *
     * @return ノードID
     */
    private String getLastTaskId() {
        BpmnEndEvent endEvent = this.bpmnProcess.getEndEventCollection().get(0);
        return this.getPrevTaskId(endEvent.getId());
    }

    /**
     * 指定ノードの前のタスクIDを取得する。
     *
     * @param id ノードID
     * @return 前のタスクID
     */
    private String getPrevTaskId(String id) {
        final BpmnNode bpmnNode = this.getNode(id);
        
        if (bpmnNode instanceof BpmnStartEvent) {
            return null;
        } else if (bpmnNode instanceof BpmnTask) {
            return id;
        }

        return this.getPrevTaskId(this.getPrevFlows(id).get(0).getSourceRef());
    }
}
