/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.common;

import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.utility.Tuple;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.scene.layout.VBox;
import javax.xml.bind.JAXBException;
import jp.adtekfuji.bpmn.model.BpmnModel;
import jp.adtekfuji.bpmn.model.BpmnModeler;
import jp.adtekfuji.bpmn.model.entity.BpmnDocument;
import jp.adtekfuji.bpmn.model.entity.BpmnEndEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnProcess;
import jp.adtekfuji.bpmn.model.entity.BpmnSequenceFlow;
import jp.adtekfuji.bpmn.model.entity.BpmnStartEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import jp.adtekfuji.forfujiapp.entity.unittemplate.ConUnitTemplateAssociateInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jp.adtekfuji.javafxcommon.workflowmodel.CellBase;
import jp.adtekfuji.javafxcommon.workflowmodel.EndCell;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelEndCell;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelStartCell;
import jp.adtekfuji.javafxcommon.workflowmodel.StartCell;

/**
 * ユニットテンプレート用ワークフローモデル
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.31.Mon
 */
public class UnitTemplateModel {

    /**
     * BpmnSequenceFlowを時間順にソートするコンパレータ
     */
    class BpmnSequenceFlowComparator implements Comparator<BpmnSequenceFlow> {

        /**
         * BpmnSequenceFlowを比較する。
         *
         * @param flow1
         * @param flow2
         * @return
         */
        @Override
        public int compare(BpmnSequenceFlow flow1, BpmnSequenceFlow flow2) {
            Date startTime1 = getStartTime(flow1.getTargetNode());
            Date startTime2 = getStartTime(flow2.getTargetNode());
            int compare = startTime1.compareTo(startTime2);
            if (0 == compare) {
                int order1 = getWorkflowOrder(flow1.getTargetNode());
                int order2 = getWorkflowOrder(flow2.getTargetNode());
                return order1 <= order2 ? -1 : 1;
            }
            return compare;
        }
    }

    /**
     * BpmnSequenceFlowをゲートウェイと時間順でソートするコンパレータ
     */
    class WorkflowOrderComparator implements Comparator<BpmnSequenceFlow> {

        /**
         * BpmnSequenceFlowを比較する。
         *
         * @param flow1
         * @param flow2
         * @return
         */
        @Override
        public int compare(BpmnSequenceFlow flow1, BpmnSequenceFlow flow2) {
            BpmnNode bpmnNode1 = flow1.getTargetNode();
            BpmnNode bpmnNode2 = flow2.getTargetNode();
            int compare = bpmnNode1.getClass().getSimpleName().compareTo(bpmnNode2.getClass().getSimpleName());
            if (0 == compare) {
                Date startTime1 = getStartTime(bpmnNode1);
                Date startTime2 = getStartTime(bpmnNode2);
                compare = startTime1.compareTo(startTime2);
                if (0 == compare) {
                    int order1 = getWorkflowOrder(flow1.getTargetNode());
                    int order2 = getWorkflowOrder(flow2.getTargetNode());
                    return order1 <= order2 ? -1 : 1;
                }
            }
            return compare;
        }
    }

    private final Logger logger = LogManager.getLogger();
    private final UnitTemplatePane unittemplatePane;
    private final BpmnModel bpmnModel;
    private final UnitTemplateCellDragAndDropEvents dragAndDropEvents
            = new UnitTemplateCellDragAndDropEvents(this, true);
    private BpmnProcess bpmnProcess;
    private long parallelId = 0L;
    private int groupNum;

    private final static String START = "start";
    private final static String END = "end";
    private final static String PARALLEL_ID = "parallelId_";
    private final static String ID = "_id";
    private final static String ID_SPLIT = "_";

    private final WorkflowInfoFacade workflowFacade = new WorkflowInfoFacade();

    /**
     * コンストラクタ
     */
    public UnitTemplateModel() {
        this.unittemplatePane = new UnitTemplatePane();
        this.bpmnModel = BpmnModeler.getModeler();
    }

    /**
     * ユニットテンプレート。
     *
     * @param unittemplatePane
     * @return
     */
    public boolean createWorkflowDiaglam(UnitTemplatePane unittemplatePane) {
        boolean ret = true;
        String diagram = unittemplatePane.getUnitTemplateEntity().getWorkflowDiaglam();
        if (Objects.isNull(diagram) || diagram.isEmpty()) {
            // 新規作成
            BpmnStartEvent start = new BpmnStartEvent(START + ID, START);
            BpmnEndEvent end = new BpmnEndEvent(END + ID, END);
            ret = this.bpmnModel.createModel(start, end);
        } else {
            // 復元
            try {
                BpmnDocument bpmn = BpmnDocument.unmarshal(diagram);
                ret = this.bpmnModel.createModel(bpmn);
            } catch (JAXBException ex) {
                logger.fatal(ex, ex);
                ret = false;
            }
        }

        if (ret) {
            this.bpmnProcess = this.bpmnModel.getBpmnDefinitions().getProcess();
            this.createUnitTemplatePane();
        }

        return ret;
    }

    /**
     * 直列工程を追加する。
     *
     * @param frontCell
     * @param cell
     * @return
     */
    public boolean add(CellBase frontCell, UnitTemplateCell cell) {
        boolean ret = false;

        if (frontCell.getParent() instanceof VBox) {

//            BpmnTask task = new BpmnTask(String.valueOf(cell.getUnitTemplateAssociate().getFkWorkId()), cell.getUnitTemplateName());
            // TODO:可能性実験　これでだめなら再構築の方法を別途考えるしかない
            BpmnTask task;
            if (Objects.isNull(cell.getUnitTemplateAssociate().getFkUnitTemplateId())) {
                task = new BpmnTask(String.valueOf(cell.getUnitTemplateAssociate().getFkWorkflowId()), cell.getUnitTemplateName());
            } else {
                task = new BpmnTask(String.valueOf(cell.getUnitTemplateAssociate().getFkUnitTemplateId()), cell.getUnitTemplateName());

            }
            if (frontCell instanceof ParallelStartCell) {
                ret = this.bpmnModel.addNextNode(((ParallelStartCell) frontCell).getParallelEndCell().getBpmnNode(), task);
                if (ret) {
                    cell.setBpmnNode(task);
                    dragAndDropEvents.configureCellDrop(cell);
                    this.unittemplatePane.addNextCell(((ParallelStartCell) frontCell).getParallelEndCell(), cell);
                }

            } else {
                ret = this.bpmnModel.addNextNode(frontCell.getBpmnNode(), task);
                if (ret) {
                    cell.setBpmnNode(task);
                    dragAndDropEvents.configureCellDrop(cell);
                    this.unittemplatePane.addNextCell(frontCell, cell);
                }
            }
        }

        return ret;
    }

    /**
     * 並列工程を追加する。
     *
     * @param gateway
     * @param cell
     * @return
     */
    public boolean add(ParallelStartCell gateway, UnitTemplateCell cell) {
//        BpmnTask task = new BpmnTask(String.valueOf(cell.getUnitTemplateAssociate().getFkWorkId()), cell.getUnitTemplateName());
        // TODO:可能性実験　これでだめなら再構築の方法を別途考えるしかない
        BpmnTask task;
        if (Objects.isNull(cell.getUnitTemplateAssociate().getFkUnitTemplateId())) {
            task = new BpmnTask(String.valueOf(cell.getUnitTemplateAssociate().getFkWorkflowId()), cell.getUnitTemplateName());
        } else {
            task = new BpmnTask(String.valueOf(cell.getUnitTemplateAssociate().getFkUnitTemplateId()), cell.getUnitTemplateName());
        }

        boolean ret = this.bpmnModel.addParallelNode((BpmnParallelGateway) gateway.getBpmnNode(), task);
        if (ret) {
            cell.setBpmnNode(task);
            dragAndDropEvents.configureCellDrop(cell);
            dragAndDropEvents.configurePararellCellDrop(gateway);
            this.unittemplatePane.addParallelCell(gateway, -1, cell);
        }
        return ret;
    }

    /**
     * 並列工程を追加する。
     *
     * @param gateway
     * @param index
     * @param cell
     * @return
     */
    public boolean addWithUpdateTimetable(ParallelStartCell gateway, int index, UnitTemplateCell cell) {
//        BpmnTask task = new BpmnTask(String.valueOf(cell.getUnitTemplateAssociate().getFkWorkId()), cell.getUnitTemplateName());
        // TODO:可能性実験　これでだめなら再構築の方法を別途考えるしかない
        BpmnTask task;
        if (Objects.isNull(cell.getUnitTemplateAssociate().getFkUnitTemplateId())) {
            task = new BpmnTask(String.valueOf(cell.getUnitTemplateAssociate().getFkWorkflowId()), cell.getUnitTemplateName());
        } else {
            task = new BpmnTask(String.valueOf(cell.getUnitTemplateAssociate().getFkUnitTemplateId()), cell.getUnitTemplateName());
        }

        boolean ret = this.bpmnModel.addParallelNode((BpmnParallelGateway) gateway.getBpmnNode(), task);
        if (ret) {
            ConUnitTemplateAssociateInfoEntity unittemplateAssociate = cell.getUnitTemplateAssociate();
            cell.setBpmnNode(task);
            dragAndDropEvents.configureCellDrop(cell);

            dragAndDropEvents.configurePararellCellDrop(gateway);
            this.unittemplatePane.addParallelCell(gateway, index, cell);
            this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

            Date baseTime = new Date(unittemplateAssociate.getStandardStartTime().getTime() + unittemplateAssociate.getStandardEndTime().getTime() - unittemplateAssociate.getStandardStartTime().getTime());
            this.shiftTime(gateway, index + 1, baseTime);
        }
        return ret;
    }

    /**
     * ゲートウェイを追加する。
     *
     * @param frontCell
     * @param gateway1
     * @param gateway2
     * @return
     */
    public boolean addGateway(CellBase frontCell, ParallelStartCell gateway1, ParallelEndCell gateway2) {
        boolean ret = false;
        if (frontCell.getParent() instanceof VBox) {
            String id = PARALLEL_ID + String.valueOf(this.parallelId++);
            BpmnParallelGateway start = new BpmnParallelGateway(id, id, null);

            id = PARALLEL_ID + String.valueOf(this.parallelId++);
            BpmnParallelGateway end = new BpmnParallelGateway(id, id, start);

            ret = this.bpmnModel.addNextNode(frontCell.getBpmnNode(), start, end);
            if (ret) {
                gateway1.setBpmnNode(start);
                gateway2.setBpmnNode(end);
                this.unittemplatePane.addNextCell(frontCell, gateway1, gateway2);
            }
        }
        return ret;
    }

    /**
     * 工程を削除する。
     *
     * @param cell
     * @return
     */
    public boolean remove(CellBase cell) {
        boolean ret = this.bpmnModel.removeNode(cell.getBpmnNode());
        if (ret) {
            this.unittemplatePane.removeCell(cell);
        }
        return ret;
    }

    /**
     * ゲートウェイを削除する。
     *
     * @param gateway1
     * @param gateway2
     * @return
     */
    public boolean remove(CellBase gateway1, CellBase gateway2) {
        boolean ret = this.bpmnModel.removeNode((BpmnParallelGateway) gateway1.getBpmnNode(), (BpmnParallelGateway) gateway2.getBpmnNode());
        if (ret) {
            this.unittemplatePane.removeCell(gateway1, gateway2);
        }
        return ret;
    }

    /**
     * ユニットテンプレートダイアグラムペインを取得する。
     *
     * @return
     */
    public UnitTemplatePane getUnitTemplatePane() {
        return this.unittemplatePane;
    }

    /**
     * ユニットテンプレートエンティティを取得する。
     *
     * @return
     */
    public UnitTemplateInfoEntity getUnitTemplate() {
        try {
            String xml = this.bpmnModel.getBpmnDefinitions().marshal();
            this.unittemplatePane.getUnitTemplateEntity().setWorkflowDiaglam(xml);
        } catch (JAXBException ex) {
            logger.fatal(ex, ex);
        }
        return unittemplatePane.getUnitTemplateEntity();
    }

    /**
     * セルを削除する。
     *
     * @param cell
     * @return
     */
    public boolean removeWithUpdateTimetable(CellBase cell) {

        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
        this.updateWorkflowOrder();

        final ConUnitTemplateAssociateInfoEntity unittemplateAssociate = ((UnitTemplateCell) cell).getUnitTemplateAssociate();
        final ParallelStartCell parallelStartCell = this.getParallelStartCell(cell.getBpmnNode());

        List<BpmnSequenceFlow> nextFlows = this.getNextFlows(cell.getBpmnNode().getId(), this.bpmnProcess.getSequenceFlowCollection());

        // 並列工程の開始時間を取得
        Date baseTime = null;
        List<BpmnSequenceFlow> parallelFlows = new LinkedList();
        if (Objects.nonNull(parallelStartCell)) {
            parallelFlows = this.getNextFlows(parallelStartCell.getBpmnNode().getId(), this.bpmnProcess.getSequenceFlowCollection());
            if (!parallelFlows.isEmpty()) {
                baseTime = this.getStartTime(parallelFlows.get(0).getTargetNode());
            }
        }

        if (!this.bpmnModel.removeNode(cell.getBpmnNode())) {
            return false;
        }

        this.unittemplatePane.removeCell(cell);
        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

        if (Objects.nonNull(parallelStartCell)) {
            if (this.shiftTime(parallelStartCell, 0, baseTime)) {
                return true;
            }
        }

        Date startTime = unittemplateAssociate.getStandardStartTime();
        startTime = this.getLastWorkTime(nextFlows.get(0), startTime);
        this.shiftTime(nextFlows, null, startTime);
        return true;
    }

    /**
     * 並列工程の作業時間をシフトする。
     *
     * @param parallelStartCell
     * @param index
     * @param baseTime
     * @return
     */
    private boolean shiftTime(CellBase cell, int index, Date baseTime) {
        if (cell instanceof ParallelStartCell) {
            ParallelStartCell parallelStartCell = (ParallelStartCell) cell;
            BpmnParallelGateway gateway = (BpmnParallelGateway) parallelStartCell.getBpmnNode();

            List<BpmnSequenceFlow> flows = this.getNextFlows(gateway.getId(), this.bpmnProcess.getSequenceFlowCollection());
            if (!flows.isEmpty()) {

                // 表示内容に合わせる
                List<BpmnSequenceFlow> sorted = new LinkedList();
                List<CellBase> cells = parallelStartCell.getFirstRow();
                for (int ii = index; ii < cells.size(); ii++) {
                    BpmnNode bpmnNode = cells.get(ii).getBpmnNode();
                    Optional<BpmnSequenceFlow> opt = flows.stream().filter(o -> bpmnNode.equals(o.getTargetNode())).findFirst();
                    if (opt.isPresent()) {
                        sorted.add(opt.get());
                    }
                }

                Date startTime = baseTime;

                // 最初の行を処理する
                for (BpmnSequenceFlow flow : sorted) {
                    if (flow.getTargetNode() instanceof BpmnTask) {
                        this.shiftTime(Arrays.asList(flow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime);
                        ConUnitTemplateAssociateInfoEntity unittemplateAssociate = this.getWork(flow.getTargetRef());
//                        startTime = unittemplateAssociate.getSkipFlag() ? unittemplateAssociate.getStandardStartTime() : unittemplateAssociate.getStandardEndTime();
                        startTime = unittemplateAssociate.getStandardEndTime();
                    } else {
                        this.shiftTime(Arrays.asList(flow), (BpmnParallelGateway) gateway.getPairedGateway(), baseTime);
                    }
                }

                flows = this.getNextFlows(gateway.getPairedId(), bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTime(flows.get(0), startTime);
                this.shiftTime(flows, null, startTime);
                return true;
            }
        } else {
            List<BpmnSequenceFlow> flows = this.getNextFlows(cell.getBpmnNode().getId(), bpmnProcess.getSequenceFlowCollection());
            Date startTime = this.getLastWorkTime(flows.get(0), baseTime);
            this.shiftTime(flows, null, startTime);
            return true;
        }

        return false;
    }

    /**
     * 作業時間をシフトする。
     *
     * @param flows
     * @param endNode
     * @param baseTime
     * @return
     */
    private Tuple<Boolean, Date> shiftTime(List<BpmnSequenceFlow> flows, BpmnNode endNode, Date baseTime) {

        Date startTime = baseTime;

        while (!flows.isEmpty()) {

            final BpmnNode bpmnNode = flows.get(0).getTargetNode();

            if (bpmnNode.equals(endNode)) {
                break;
            }

            if (bpmnNode instanceof BpmnEndEvent) {
                // 終了イベント
                return new Tuple(false, startTime);

            } else if (bpmnNode instanceof BpmnTask) {
                // タスク
                ConUnitTemplateAssociateInfoEntity unittemplateAssociate = this.getWork(bpmnNode.getId());

                // TODO:可能性実験　これでだめなら再構築の方法を別途考えるしかない
                if (Objects.isNull(unittemplateAssociate.getUnitTemplateAssociationId())) {
                    logger.info("Workflow: {}", unittemplateAssociate.getWorkflowName());
                } else {
                    logger.info("UnitTemplate: {}", unittemplateAssociate.getUnitTemplateName());
                }

                Date endTime = new Date(startTime.getTime() + unittemplateAssociate.getStandardEndTime().getTime() - unittemplateAssociate.getStandardStartTime().getTime());
                unittemplateAssociate.setStandardStartTime(startTime);
                unittemplateAssociate.setStandardEndTime(endTime);
                unittemplateAssociate.updateMember();

//                if (!unittemplateAssociate.getSkipFlag()) {
//                    startTime = endTime;
//                }
                flows = this.getNextFlows(flows.get(0).getTargetRef(), this.bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTime(flows.get(0), startTime);

            } else if (bpmnNode instanceof BpmnParallelGateway) {
                // ゲートウェイ
                BpmnParallelGateway gateway = (BpmnParallelGateway) bpmnNode;
                Date time = startTime;

                List<BpmnSequenceFlow> nextFlows = this.getNextFlows(bpmnNode.getId(), bpmnProcess.getSequenceFlowCollection());
                for (BpmnSequenceFlow nextFlow : nextFlows) {
                    if (nextFlow.getTargetNode() instanceof BpmnTask) {
                        Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), time);
                        if (!result.getLeft()) {
                            return new Tuple(false, startTime);
                        }

                        ConUnitTemplateAssociateInfoEntity unittemplateAssociate = this.getWork(nextFlow.getTargetRef());
//                        time = unittemplateAssociate.getSkipFlag() ? unittemplateAssociate.getStandardStartTime() : unittemplateAssociate.getStandardEndTime();
                        time = unittemplateAssociate.getStandardEndTime();
                    } else {
                        Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime);
                        if (!result.getLeft()) {
                            return new Tuple(false, startTime);
                        }
                    }
                }

                flows = this.getNextFlows(gateway.getPairedId(), bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTime(flows.get(0), startTime);
            }
        }

        return new Tuple(true, startTime);
    }

    /**
     * タイムテーブルを更新する。
     *
     * @param workCell
     * @param diff (未使用)
     * @param isEdited
     * @param isShift (未使用)
     */
    public void updateTimetable(UnitTemplateCell workCell, long diff, boolean isEdited, boolean isShift) {
        try {
            if (isEdited) {
                final ParallelStartCell parallelStartCell = this.getParallelStartCell(workCell.getBpmnNode());
                final ConUnitTemplateAssociateInfoEntity unittemplateAssociate = ((UnitTemplateCell) workCell).getUnitTemplateAssociate();

                if (Objects.nonNull(parallelStartCell)) {
                    List<CellBase> cells = parallelStartCell.getFirstRow();
                    int index = cells.indexOf(workCell);
                    this.shiftTime(parallelStartCell, index, unittemplateAssociate.getStandardStartTime());
                } else {
//                    Date baseTime = unittemplateAssociate.getSkipFlag() ? unittemplateAssociate.getStandardStartTime() : unittemplateAssociate.getStandardEndTime();
                    Date baseTime = unittemplateAssociate.getStandardEndTime();
                    this.shiftTime(workCell, 0, baseTime);
                }
                return;
            }

            this.bpmnProcess = this.bpmnModel.getBpmnDefinitions().getProcess();
            this.shiftTime(workCell, 0, workCell.getUnitTemplateAssociate().getStandardEndTime());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 前工程の終了時間を取得する。
     *
     * @param flow
     * @param date
     * @return
     */
    private Date getLastWorkTime(BpmnSequenceFlow flow, Date date) {
        if (flow.getTargetNode() instanceof BpmnParallelGateway) {
            return this.getLastWorkTime(flow.getTargetNode(), date);
        } else if (flow.getSourceNode() instanceof BpmnParallelGateway) {
            return this.getLastWorkTime(flow.getSourceNode(), date);
        }
        return date;
    }

    /**
     * 前工程の終了時間を取得する。
     *
     * @param bpmnNode
     * @param date
     * @return
     */
    private Date getLastWorkTime(BpmnNode bpmnNode, Date date) {
        if (bpmnNode instanceof BpmnTask) {
            ConUnitTemplateAssociateInfoEntity unittemplateAssociate = this.getWork(bpmnNode.getId());
//            if (unittemplateAssociate.getSkipFlag()) {
//                List<BpmnSequenceFlow> previousFlows = this.getFlows(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
//                for (BpmnSequenceFlow previouFlow : previousFlows) {
//                    Date lastDate = this.getLastWorkTime(previouFlow.getSourceNode(), date);
//                    if (date.before(lastDate)) {
//                        date = lastDate;
//                    }
//                }
//            } else {
//                if (date.before(unittemplateAssociate.getStandardEndTime())) {
//                    date = unittemplateAssociate.getStandardEndTime();
//                }
//            }
            if (date.before(unittemplateAssociate.getStandardEndTime())) {
                date = unittemplateAssociate.getStandardEndTime();
            }
        } else if (bpmnNode instanceof BpmnParallelGateway) {
            List<BpmnSequenceFlow> previousFlows = this.getFlows(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
            for (BpmnSequenceFlow previouFlow : previousFlows) {
                Date lastDate = this.getLastWorkTime(previouFlow.getSourceNode(), date);
                if (date.before(lastDate)) {
                    date = lastDate;
                }
            }
        }
        return date;
    }

    /**
     * ユニットテンプレートオーダーを更新する。
     */
    public void updateWorkflowOrder() {
        try {
            this.groupNum = 1;

            this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
            List<BpmnSequenceFlow> flows = this.getNextFlows(START + ID, this.bpmnProcess.getSequenceFlowCollection());

            int nextOrder = this.groupNum * 10000 + 1;
            this.groupNum++;
            this.updateWorkflowOrder(flows, null, nextOrder);
        } catch (Exception ex) {

        }
    }

    /**
     * ユニットテンプレートオーダーを更新する。
     *
     * @param flows
     * @param endNode
     * @param workflowOrder
     * @return
     */
    private boolean updateWorkflowOrder(List<BpmnSequenceFlow> flows, BpmnNode endNode, int workflowOrder) {

        int nextOrder = workflowOrder;

        while (!flows.isEmpty()) {

            final BpmnNode bpmnNode = flows.get(0).getTargetNode();

            if (bpmnNode.equals(endNode)) {
                if (workflowOrder < nextOrder) {
                    this.groupNum--;
                }
                break;
            }

            if (bpmnNode instanceof BpmnEndEvent) {
                // 終了イベント
                return false;

            } else if (bpmnNode instanceof BpmnTask) {
                // タスク
                ConUnitTemplateAssociateInfoEntity unittemplateAssociate = this.getWork(bpmnNode.getId());
//                logger.info("Work: {}, {}", unittemplateAssociate.getWorkName(), nextOrder);
                if (Objects.isNull(unittemplateAssociate.getUnitTemplateAssociationId())) {
                    logger.info("Workflow: {}, {}", unittemplateAssociate.getWorkflowName(), nextOrder);
                } else {
                    logger.info("UnitTemplate: {}, {}", unittemplateAssociate.getUnitTemplateName(), nextOrder);
                }

                unittemplateAssociate.setUnitTemplateAssociateOrder(nextOrder);
                unittemplateAssociate.updateMember();

                flows = this.getNextFlows(flows.get(0).getTargetRef(), this.bpmnProcess.getSequenceFlowCollection());

            } else if (bpmnNode instanceof BpmnParallelGateway) {
                // ゲートウェイ
                BpmnParallelGateway gateway = (BpmnParallelGateway) bpmnNode;

                List<BpmnSequenceFlow> nextFlows = this.getNextFlows(bpmnNode.getId(), bpmnProcess.getSequenceFlowCollection());
                Collections.sort(nextFlows, new WorkflowOrderComparator());
                for (BpmnSequenceFlow nextFlow : nextFlows) {
                    if (!this.updateWorkflowOrder(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), nextOrder++)) {
                        return false;
                    }

                    if (nextFlow.getTargetNode() instanceof BpmnParallelGateway && this.findCellBase(nextFlow.getTargetRef()) instanceof ParallelStartCell) {
                        nextOrder = this.groupNum * 10000 + 1;
                        this.groupNum++;
                    }
                }

                flows = this.getNextFlows(gateway.getPairedId(), bpmnProcess.getSequenceFlowCollection());

            }

            nextOrder = this.groupNum * 10000 + 1;
            this.groupNum++;
        }

        return true;
    }

    /**
     * ユニットテンプレートダイアグラムペインを生成する。
     */
    private void createUnitTemplatePane() {
        List<BpmnStartEvent> startEventCollection = this.bpmnProcess.getStartEventCollection();
        List<BpmnEndEvent> endEventCollection = this.bpmnProcess.getEndEventCollection();
        List<BpmnSequenceFlow> sequenceFlowCollection = this.bpmnProcess.getSequenceFlowCollection();
        if (startEventCollection.size() != 1 || endEventCollection.size() != 1) {
            return;
        }

        BpmnStartEvent startEvent = startEventCollection.get(0);
        BpmnEndEvent endEvent = endEventCollection.get(0);

        StartCell startCell = new StartCell();
        startCell.setBpmnNode(startEvent);
        dragAndDropEvents.configureStartCellDrop(startCell);
        EndCell endCell = new EndCell();
        endCell.setBpmnNode(endEvent);
        this.unittemplatePane.createPane(startCell, endCell);

        if (sequenceFlowCollection.size() == 1) {
            return;
        }

        this.restore(startEvent);

        this.parallelId = this.getMaxParallelId(this.bpmnProcess.getParallelGatewayCollection()) + 1;
    }

    /**
     * ユニットテンプレートダイアグラムを復元する。
     *
     * @param start
     */
    private void restore(BpmnStartEvent start) {
        List<BpmnSequenceFlow> flows = this.getNextFlows(start.getId(), this.bpmnProcess.getSequenceFlowCollection());

        while (true) {
            if (flows.isEmpty()) {
                break;
            }

            if (flows.size() == 1) {
                if (flows.get(0).getTargetNode() instanceof BpmnEndEvent) {
                    break;
                } else {
                    this.restoreCell(flows.get(0).getSourceNode(), flows.get(0).getTargetNode());
                    flows = this.getNextFlows(flows.get(0).getTargetRef(), this.bpmnProcess.getSequenceFlowCollection());
                }
            } else {
                BpmnParallelGateway startPara = (BpmnParallelGateway) flows.get(0).getSourceNode();
                if (Objects.nonNull(startPara)) {
                    this.restore(flows, (BpmnParallelGateway) startPara.getPairedGateway());
                    flows = this.getNextFlows(startPara.getPairedId(), this.bpmnProcess.getSequenceFlowCollection());
                }
            }
        }
    }

    /**
     * ユニットテンプレートダイアグラムを復元する。
     *
     * @param flows
     * @param endGateway
     */
    private void restore(List<BpmnSequenceFlow> flows, BpmnParallelGateway endGateway) {
        Collections.sort(flows, new BpmnSequenceFlowComparator());

        for (BpmnSequenceFlow flow : flows) {
            this.restoreCell(flow.getSourceNode(), flow.getTargetNode());
            List<BpmnSequenceFlow> nextFlows = this.getNextFlows(flow.getTargetRef(), this.bpmnProcess.getSequenceFlowCollection());
            this.restoreCell(nextFlows, endGateway);
        }
    }

    /**
     * セルを復元する。
     *
     * @param flows
     * @param endGateway
     */
    private void restoreCell(List<BpmnSequenceFlow> flows, BpmnParallelGateway endGateway) {
        while (true) {
            if (flows.isEmpty()) {
                break;
            }

            if (flows.size() == 1) {
                this.restoreCell(flows.get(0).getSourceNode(), flows.get(0).getTargetNode());
                if (flows.get(0).getTargetNode().equals(endGateway)) {
                    break;
                }
                flows = this.getNextFlows(flows.get(0).getTargetRef(), this.bpmnProcess.getSequenceFlowCollection());
            } else {
                BpmnParallelGateway startPara = (BpmnParallelGateway) flows.get(0).getSourceNode();
                if (Objects.nonNull(startPara)) {
                    restore(flows, (BpmnParallelGateway) startPara.getPairedGateway());
                    flows = this.getNextFlows(startPara.getPairedId(), this.bpmnProcess.getSequenceFlowCollection());
                }
            }
        }
    }

    /**
     * セルを復元する。
     *
     * @param source
     * @param target
     * @return
     */
    private boolean restoreCell(BpmnNode source, BpmnNode target) {
        boolean ret = false;

        CellBase targetCell = this.findCellBase(target.getId());
        if (Objects.nonNull(targetCell)) {
            return ret;
        }

        CellBase sourceCell = this.findCellBase(source.getId());
        if (Objects.nonNull(sourceCell)) {
            if (target instanceof BpmnTask) {
                // 工程セルを追加
                ConUnitTemplateAssociateInfoEntity workflow = this.getWork(target.getId());
                Integer rev = null;
                String cellName;
                if (Objects.isNull(workflow.getFkUnitTemplateId())) {
                    rev = workflowFacade.find(Long.valueOf(target.getId())).getWorkflowRev();
                    cellName = workflow.getWorkflowName();
                    target = new BpmnTask(target.getId(), cellName);
                } else {
                    cellName = workflow.getUnitTemplateName();
                }

                if (Objects.nonNull(workflow)) {
                    if (sourceCell instanceof ParallelStartCell) {
                        UnitTemplateCell cell = new UnitTemplateCell(workflow, cellName, rev);
                        dragAndDropEvents.configureCellDrop(cell);
                        cell.setBpmnNode(target);
                        ret = unittemplatePane.addParallelCell((ParallelStartCell) sourceCell, -1, cell);
                    } else {
                        UnitTemplateCell cell = new UnitTemplateCell(workflow, cellName, rev);
                        dragAndDropEvents.configureCellDrop(cell);
                        cell.setBpmnNode(target);
                        ret = unittemplatePane.addNextCell(sourceCell, cell);
                    }
                }

            } else if (target instanceof BpmnParallelGateway) {
                ParallelStartCell parallelStart = new ParallelStartCell();
                dragAndDropEvents.configurePararellCellDrop(parallelStart);
                // 後でイベント注入
                ParallelEndCell parallelEnd = new ParallelEndCell(parallelStart);
                // 後でイベント注入
                parallelStart.setBpmnNode(target);
                parallelEnd.setBpmnNode(((BpmnParallelGateway) target).getPairedGateway());
                if (sourceCell instanceof ParallelStartCell) {
                    ret = this.unittemplatePane.addParallelCell((ParallelStartCell) sourceCell, -1, parallelStart, parallelEnd);
                } else {
                    ret = this.unittemplatePane.addNextCell(sourceCell, parallelStart, parallelEnd);
                }
            }
        }

        return ret;
    }

    /**
     * ParallelIdを取得する。
     *
     * @param gatewayList
     * @return
     */
    private long getMaxParallelId(List<BpmnParallelGateway> gatewayList) {
        long maxId = 0;
        for (BpmnParallelGateway gateway : gatewayList) {
            String[] split = gateway.getId().split(ID_SPLIT);
            if (split.length > 1) {
                long id = Long.parseLong(split[1]);
                if (maxId < id) {
                    maxId = id;
                }
            }
        }
        return maxId;
    }

    /**
     * 工程を取得する。
     *
     * @param workId
     * @return
     */
    private ConUnitTemplateAssociateInfoEntity getWork(String workId) {
        try {
            UnitTemplateInfoEntity unittemlate = unittemplatePane.getUnitTemplateEntity();
            // TODO:ダイアグラムないで管理できるIDの種類は1つ
            // 引数をターゲットに変更
            // 工程順の存在確認.有ったらそのまま返す。
            Optional<ConUnitTemplateAssociateInfoEntity> unittemplateAssociateWorkflowOpt = unittemlate.getConUnitTemplateAssociateCollection().stream().filter(p -> String.valueOf(p.getFkWorkflowId()).equals(workId)).findFirst();
            if (unittemplateAssociateWorkflowOpt.isPresent()) {
                return unittemplateAssociateWorkflowOpt.get();
            }
            // 工程順がなかった場合ユニットテンプレートとしてあるか確認する。あった場合はそのまま返す
            Optional<ConUnitTemplateAssociateInfoEntity> unittemplateAssociateTempOpt = unittemlate.getConUnitTemplateAssociateCollection().stream().filter(p -> String.valueOf(p.getFkUnitTemplateId()).equals(workId)).findFirst();
            if (unittemplateAssociateTempOpt.isPresent()) {
                return unittemplateAssociateTempOpt.get();
            }

            // 工程順の存在確認.有ったらそのまま返す。
            Optional<CellBase> workflowCellOpt = unittemplatePane.getCellList().stream().filter(o -> {
                if (o instanceof UnitTemplateCell) {
                    ConUnitTemplateAssociateInfoEntity unittemplateAssociate = ((UnitTemplateCell) o).getUnitTemplateAssociate();
                    return String.valueOf(unittemplateAssociate.getFkWorkflowId()).equals(workId);
                }
                return false;
            }).findFirst();

            if (workflowCellOpt.isPresent()) {
                return ((UnitTemplateCell) workflowCellOpt.get()).getUnitTemplateAssociate();
            }

            // 工程順がなかった場合ユニットテンプレートとしてあるか確認する。あった場合はそのまま返す
            Optional<CellBase> tmpCellOpt = unittemplatePane.getCellList().stream().filter(o -> {
                if (o instanceof UnitTemplateCell) {
                    ConUnitTemplateAssociateInfoEntity unittemplateAssociate = ((UnitTemplateCell) o).getUnitTemplateAssociate();
                    return String.valueOf(unittemplateAssociate.getFkUnitTemplateId()).equals(workId);
                }
                return false;
            }).findFirst();

            if (tmpCellOpt.isPresent()) {
                return ((UnitTemplateCell) tmpCellOpt.get()).getUnitTemplateAssociate();
            }

        } catch (NullPointerException ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 次のシーケンスフローを取得する。
     *
     * @param sourceId
     * @param flows
     * @return
     */
    private List<BpmnSequenceFlow> getNextFlows(String sourceId, List<BpmnSequenceFlow> flows) {
        try {
            List<BpmnSequenceFlow> nextFlows = flows.stream().filter(p -> p.getSourceRef().equals(sourceId)).collect(Collectors.toList());
            Collections.sort(nextFlows, new BpmnSequenceFlowComparator());
            return nextFlows;
        } catch (NullPointerException ex) {
            logger.fatal(ex, ex);
        }
        return new LinkedList();
    }

    /**
     * シーケンスフローを取得する。
     *
     * @param targetId
     * @param flows
     * @return
     */
    private List<BpmnSequenceFlow> getFlows(String targetId, List<BpmnSequenceFlow> flows) {
        try {
            return flows.stream().filter(p -> p.getTargetRef().equals(targetId)).collect(Collectors.toList());
        } catch (NullPointerException ex) {
            logger.fatal(ex, ex);
        }
        return new LinkedList();
    }

    /**
     * CellBaseを検索する。
     *
     * @param id
     * @return
     */
    private CellBase findCellBase(String id) {
        try {
            Optional<CellBase> opt = unittemplatePane.getCellList().stream().filter(p -> p.getBpmnNode().getId().equals(id)).findFirst();
            if (opt.isPresent()) {
                return opt.get();
            }
        } catch (NullPointerException ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * ゲートウェイ(始端)を取得する。
     *
     * @param cell
     * @return
     */
    public ParallelStartCell getParallelStartCell(UnitTemplateCell cell) {
        return getParallelStartCell(cell.getBpmnNode());
    }

    /**
     * ゲートウェイ(始端)を取得する。
     *
     * @param cell
     * @return
     */
    private ParallelStartCell getParallelStartCell(BpmnNode bpmnNode) {
        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
        List<BpmnSequenceFlow> flows = this.getFlows(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
        for (BpmnSequenceFlow flow : flows) {
            CellBase cellBase = this.findCellBase(flow.getSourceRef());
            if (cellBase instanceof ParallelStartCell) {
                return (ParallelStartCell) cellBase;
            }
        }
        return null;
    }

    /**
     * 開始時間を取得する。
     *
     * @param bpmnNode
     * @return
     */
    private Date getStartTime(BpmnNode bpmnNode) {
        try {
            if (bpmnNode instanceof BpmnParallelGateway) {
                List<BpmnSequenceFlow> flows = this.getNextFlows(bpmnNode.getId(), bpmnProcess.getSequenceFlowCollection());
                if (!flows.isEmpty()) {
                    Collections.sort(flows, new BpmnSequenceFlowComparator());
                    return this.getStartTime(flows.get(0).getTargetNode());
                }
            } else {
                ConUnitTemplateAssociateInfoEntity unittemplateAssociate = this.getWork(bpmnNode.getId());
                return unittemplateAssociate.getStandardStartTime();
            }
            return new Date(Long.MAX_VALUE);
        } catch (Exception ex) {
            return new Date(Long.MAX_VALUE);
        }
    }

    /**
     * ユニットテンプレートオーダーを取得する。
     *
     * @param bpmnNode
     * @return
     */
    private int getWorkflowOrder(BpmnNode bpmnNode) {
        try {
            if (bpmnNode instanceof BpmnParallelGateway) {
                List<BpmnSequenceFlow> flows = this.getNextFlows(bpmnNode.getId(), bpmnProcess.getSequenceFlowCollection());
                if (!flows.isEmpty()) {
                    Collections.sort(flows, new BpmnSequenceFlowComparator());
                    return this.getWorkflowOrder(flows.get(0).getTargetNode());
                }
            } else {
                ConUnitTemplateAssociateInfoEntity unittemplateAssociate = this.getWork(bpmnNode.getId());
                return unittemplateAssociate.getUnitTemplateAssociateOrder();
            }
            return Integer.MAX_VALUE;
        } catch (Exception ex) {
            return Integer.MAX_VALUE;
        }
    }

}
