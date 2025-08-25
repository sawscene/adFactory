/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.workflowmodel;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.Tuple;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import jakarta.xml.bind.JAXBException;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;
import jp.adtekfuji.adFactory.utility.DataFormatUtil;
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
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.enumeration.MarkerStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程順モデル
 *
 * @author s-heya
 */


public class WorkflowModel {

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
    private final SceneContiner sc = SceneContiner.getInstance();
    private final WorkflowPane workflowPane;
    private final BpmnModel bpmnModel;
    private BpmnProcess bpmnProcess;
    private long parallelId = 0L;
    private int groupNum;
    private boolean draggable = true;
    private boolean editable = true;

    private final List<BpmnSequenceFlow> temporaryFlows = new ArrayList<>();
    private final Date minDate = DateUtils.min();

    private final static String START = "start";
    private final static String END = "end";
    private final static String PARALLEL_ID = "parallelId_";
    private final static String ID = "_id";
    private final static String ID_SPLIT = "_";

    // 並列工程の作業時間をシフトする
    private boolean isShift = false;

    /**
     * コンストラクタ
     */
    public WorkflowModel() {
        this.workflowPane = new WorkflowPane();
        this.bpmnModel = BpmnModeler.getModeler();
        this.isShift = Boolean.valueOf(AdProperty.getProperties().getProperty(Config.SHIFT_TIME, "true"));
    }

    /**
     * 工程順ダイアグラムペインを生成する。
     *
     * @param workflowPane
     * @return
     */
    public boolean createWorkflow(WorkflowPane workflowPane) {
        return createWorkflow(workflowPane, true);
    }

    /**
     * 工程順ダイアグラムペインを生成する。
     *
     * @param workflowPane
     * @param draggable
     * @return
     */
    public boolean createWorkflow(WorkflowPane workflowPane, boolean draggable) {
        return createWorkflow(workflowPane, draggable, draggable);
    }

    /**
     * 工程順ダイアグラムペインを生成する。
     *
     * @param workflowPane
     * @param draggable 工程の追加・移動を可能とするか
     * @param editable チェックボックスを表示し工程の編集を可能とするか
     * @return
     */
    public boolean createWorkflow(WorkflowPane workflowPane, boolean draggable, boolean editable) {
        boolean ret;
        this.draggable = draggable;
        this.editable = editable;
        String diagram = workflowPane.getWorkflowEntity().getWorkflowDiaglam();
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
            this.createWorkflowPane();
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
    public boolean add(CellBase frontCell, WorkCell cell) {
        boolean ret = false;

        if (frontCell.getParent() instanceof VBox) {
            BpmnTask task = new BpmnTask(String.valueOf(cell.getWorkflowWork().getFkWorkId()), cell.getWorkName());

            if (frontCell instanceof ParallelStartCell) {
                ret = this.bpmnModel.addNextNode(((ParallelStartCell) frontCell).getParallelEndCell().getBpmnNode(), task);
                if (ret) {
                    cell.setBpmnNode(task);
                    this.workflowPane.addNextCell(((ParallelStartCell) frontCell).getParallelEndCell(), cell);
                }

            } else {
                ret = this.bpmnModel.addNextNode(frontCell.getBpmnNode(), task);
                if (ret) {
                    cell.setBpmnNode(task);
                    this.workflowPane.addNextCell(frontCell, cell);
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
    public boolean add(ParallelStartCell gateway, WorkCell cell) {
        BpmnTask task = new BpmnTask(String.valueOf(cell.getWorkflowWork().getFkWorkId()), cell.getWorkName());
        boolean ret = this.bpmnModel.addParallelNode((BpmnParallelGateway) gateway.getBpmnNode(), task);
        if (ret) {
            cell.setBpmnNode(task);
            this.workflowPane.addParallelCell(gateway, -1, cell);
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
    public boolean addWithUpdateTimetable(CellBase frontCell, WorkCell cell) {
        BpmnTask task = new BpmnTask(String.valueOf(cell.getWorkflowWork().getFkWorkId()), cell.getWorkName());
        boolean ret = this.bpmnModel.addNextNode(frontCell.getBpmnNode(), task);
        if (ret) {
            cell.setBpmnNode(task);

            this.workflowPane.addNextCell(frontCell, cell);
            this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

            //開始時間終了時間の更新
            Date baseTime = getLastWorkTime(frontCell.getBpmnNode(), minDate);
            this.shiftTime(frontCell, 0, baseTime);
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
    public boolean addWithUpdateTimetable(ParallelStartCell gateway, int index, WorkCell cell) {
        BpmnTask task = new BpmnTask(String.valueOf(cell.getWorkflowWork().getFkWorkId()), cell.getWorkName());
        boolean ret = this.bpmnModel.addParallelNode((BpmnParallelGateway) gateway.getBpmnNode(), task);
        if (ret) {
            cell.setBpmnNode(task);

            this.workflowPane.addParallelCell(gateway, index, cell);
            this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

            // 開始時間・完了時間を更新
            ConWorkflowWorkInfoEntity frontWorkflowWork = ((WorkCell) gateway.getFirstRow().get(index - 1)).getWorkflowWork();

            Date startDate = null;
            if (SchedulePolicyEnum.PriorityParallel == workflowPane.getWorkflowEntity().getSchedulePolicy()) {
                startDate = frontWorkflowWork.getSkipFlag() ? frontWorkflowWork.getStandardStartTime() : frontWorkflowWork.getStandardEndTime();
            } else {
                startDate = frontWorkflowWork.getStandardStartTime();
            }
            
            Date endDate = new Date(startDate.getTime() + cell.getWorkflowWork().getTaktTime());

            cell.getWorkflowWork().setStandardStartTime(startDate);
            cell.getWorkflowWork().setStandardEndTime(endDate);

            this.shiftTime(gateway, index + 1, endDate);
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
                this.workflowPane.addNextCell(frontCell, gateway1, gateway2);
            }
        }
        return ret;
    }

    /**
     * 工程を直列位置に移動する。
     *
     * @param frontCell
     * @param cell
     * @return
     */
    public boolean move(CellBase frontCell, WorkCell cell) {
        boolean ret = false;

        if (frontCell.getParent() instanceof VBox) {
            BpmnTask task = (BpmnTask) cell.getBpmnNode();

            if (frontCell instanceof ParallelStartCell) {
                ret = this.bpmnModel.moveNextNode(((ParallelStartCell) frontCell).getParallelEndCell().getBpmnNode(), task);
                if (ret) {
                    this.workflowPane.removeCell(cell);
                    this.workflowPane.addNextCell(((ParallelStartCell) frontCell).getParallelEndCell(), cell);
                }

            } else {
                ret = this.bpmnModel.moveNextNode(frontCell.getBpmnNode(), task);
                if (ret) {
                    this.workflowPane.removeCell(cell);
                    this.workflowPane.addNextCell(frontCell, cell);
                }
            }
        }

        return ret;
    }

    /**
     * 工程を直列位置に移動する。
     *
     * @param frontCell
     * @param cell
     * @return
     */
    public boolean moveWithUpdateTimetable(CellBase frontCell, WorkCell cell) {
        boolean ret = this.bpmnModel.moveNextNode(frontCell.getBpmnNode(), (BpmnTask) cell.getBpmnNode());
        if (ret) {
            this.workflowPane.removeCell(cell);
            this.workflowPane.addNextCell(frontCell, cell);

            // 開始時間・完了時間を更新
            this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
            List<BpmnSequenceFlow> flows = this.getNextFlows("start_id", this.bpmnProcess.getSequenceFlowCollection());
            this.shiftTime(flows, null, minDate);
        }

        return ret;
    }

    /**
     * 工程を並列位置に移動する。
     *
     * @param gateway
     * @param cell
     * @return
     */
    public boolean move(ParallelStartCell gateway, WorkCell cell) {
        BpmnTask task = (BpmnTask) cell.getBpmnNode();
        boolean ret = this.bpmnModel.moveParallelNode((BpmnParallelGateway) gateway.getBpmnNode(), task);
        if (ret) {
            this.workflowPane.removeCell(cell);
            this.workflowPane.addParallelCell(gateway, -1, cell);
        }
        return ret;
    }

    /**
     * 工程を並列位置に移動する。
     *
     * @param gateway
     * @param index
     * @param cell
     * @return
     */
    public boolean moveWithUpdateTimetable(ParallelStartCell gateway, int index, WorkCell cell) {
        boolean ret = this.bpmnModel.moveParallelNode((BpmnParallelGateway) gateway.getBpmnNode(), (BpmnTask) cell.getBpmnNode());
        if (ret) {
            this.workflowPane.removeCell(cell);
            this.workflowPane.addParallelCell(gateway, index, cell);

            // 開始時間・完了時間を更新
            this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
            List<BpmnSequenceFlow> flows = this.getNextFlows("start_id", this.bpmnProcess.getSequenceFlowCollection());
            this.shiftTime(flows, null, minDate);
        }
        return ret;
    }

    /**
     * ゲートウェイを移動する。
     *
     * @param frontCell
     * @param gateway1
     * @param gateway2
     * @return
     */
    public boolean moveGateway(CellBase frontCell, ParallelStartCell gateway1, ParallelEndCell gateway2) {
        boolean ret = false;
        if (frontCell.getParent() instanceof VBox) {
            ret = this.bpmnModel.moveNextNode(frontCell.getBpmnNode(), (BpmnParallelGateway) gateway1.getBpmnNode(), (BpmnParallelGateway) gateway2.getBpmnNode());
            if (ret) {
                this.workflowPane.removeCell(gateway1, gateway2);
                this.workflowPane.addNextCell(frontCell, gateway1, gateway2);
            }
        }
        return ret;
    }

    /**
     * 並列作業を直列位置に移動する。
     *
     * @param frontCell
     * @param startGateway
     * @param endGateway
     * @return
     */
    public boolean moveWithUpdateTimetable(CellBase frontCell, ParallelStartCell startGateway, ParallelEndCell endGateway) {
        BpmnParallelGateway start = (BpmnParallelGateway) startGateway.getBpmnNode();
        BpmnParallelGateway end = (BpmnParallelGateway) endGateway.getBpmnNode();

        boolean ret = this.bpmnModel.moveNextNode(frontCell.getBpmnNode(), start, end);
        if (ret) {
            this.workflowPane.removeCell(startGateway, endGateway);
            this.workflowPane.addNextCell(frontCell, startGateway, endGateway);

            // 開始時間・完了時間を更新
            this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
            List<BpmnSequenceFlow> flows = this.getNextFlows("start_id", this.bpmnProcess.getSequenceFlowCollection());
            this.shiftTime(flows, null, minDate);
        }

        return ret;
    }

    /**
     * 並列作業を並列位置に移動する。
     *
     * @param gateway
     * @param index
     * @param startGateway
     * @param endGateway
     * @return
     */
    public boolean moveWithUpdateTimetable(ParallelStartCell gateway, int index, ParallelStartCell startGateway, ParallelEndCell endGateway) {
        BpmnParallelGateway start = (BpmnParallelGateway) startGateway.getBpmnNode();
        BpmnParallelGateway end = (BpmnParallelGateway) endGateway.getBpmnNode();

        boolean ret = this.bpmnModel.moveParallelNode((BpmnParallelGateway) gateway.getBpmnNode(), start, end);
        if (ret) {
            this.workflowPane.removeCell(startGateway, endGateway);
            this.workflowPane.addParallelCell(gateway, index, startGateway, endGateway);

            // 開始時間・完了時間を更新
            this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
            List<BpmnSequenceFlow> flows = this.getNextFlows("start_id", this.bpmnProcess.getSequenceFlowCollection());
            this.shiftTime(flows, null, minDate);
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
            this.workflowPane.removeCell(cell);
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
            this.workflowPane.removeCell(gateway1, gateway2);
        }
        return ret;
    }

    /**
     * 工程順ダイアグラムペインを取得する。
     *
     * @return
     */
    public WorkflowPane getWorkflowPane() {
        return this.workflowPane;
    }

    /**
     * 工程順エンティティを取得する。
     *
     * @return
     */
    public WorkflowInfoEntity getWorkflow() {
        try {
            String xml = this.bpmnModel.getBpmnDefinitions().marshal();
            this.workflowPane.getWorkflowEntity().setWorkflowDiaglam(xml);
        } catch (JAXBException ex) {
            logger.fatal(ex, ex);
        }
        return workflowPane.getWorkflowEntity();
    }

    /**
     * ワークフロー図を取得する。
     *
     * @return
     */
    public String getWorkflowDiaglam() {
        try {
            return this.bpmnModel.getBpmnDefinitions().marshal();
        } catch (JAXBException ex) {
            logger.fatal(ex, ex);
        }
        return null;
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

        final ConWorkflowWorkInfoEntity workflowWork = ((WorkCell) cell).getWorkflowWork();
        final ParallelStartCell parallelStartCell = this.getParallelStartCell(cell.getBpmnNode());

        List<BpmnSequenceFlow> nextFlows = this.getNextFlows(cell.getBpmnNode().getId(), this.bpmnProcess.getSequenceFlowCollection());

        // 並列工程の開始時間を取得
        Date baseTime = null;
        List<BpmnSequenceFlow> parallelFlows;
        if (Objects.nonNull(parallelStartCell)) {
            parallelFlows = this.getNextFlows(parallelStartCell.getBpmnNode().getId(), this.bpmnProcess.getSequenceFlowCollection());
            if (!parallelFlows.isEmpty()) {
                baseTime = this.getStartTime(parallelFlows.get(0).getTargetNode());
            }
        }

        if (!this.bpmnModel.removeNode(cell.getBpmnNode())) {
            return false;
        }

        this.workflowPane.removeCell(cell);
        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

        if (Objects.nonNull(parallelStartCell)) {
            if (this.shiftTime(parallelStartCell, 0, baseTime)) {
                return true;
            }
        }

        Date startTime = workflowWork.getStandardStartTime();
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
                if (SchedulePolicyEnum.PriorityParallel == workflowPane.getWorkflowEntity().getSchedulePolicy()) {

                    for (BpmnSequenceFlow flow : sorted) {
                        if (flow.getTargetNode() instanceof BpmnTask) {
                            this.shiftTime(Arrays.asList(flow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime);
                            ConWorkflowWorkInfoEntity workflowWork = this.getWork(flow.getTargetRef());
                            startTime = workflowWork.getSkipFlag() ? workflowWork.getStandardStartTime() : workflowWork.getStandardEndTime();
                        } else {
                            this.shiftTime(Arrays.asList(flow), (BpmnParallelGateway) gateway.getPairedGateway(), baseTime);
                        }
                    }

                } else {
                    for (BpmnSequenceFlow flow : sorted) {
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
                ConWorkflowWorkInfoEntity workflowWork = this.getWork(bpmnNode.getId());
                logger.info("Work: {}", workflowWork.getWorkName());

                Date endTime = new Date(startTime.getTime() + workflowWork.getStandardEndTime().getTime() - workflowWork.getStandardStartTime().getTime());
                workflowWork.setStandardStartTime(startTime);
                workflowWork.setStandardEndTime(endTime);
                workflowWork.updateMember();

                if (!workflowWork.getSkipFlag()) {
                    startTime = endTime;
                }

                flows = this.getNextFlows(flows.get(0).getTargetRef(), this.bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTime(flows.get(0), startTime);

            } else if (bpmnNode instanceof BpmnParallelGateway) {
                // ゲートウェイ
                BpmnParallelGateway gateway = (BpmnParallelGateway) bpmnNode;

                if (SchedulePolicyEnum.PriorityParallel == workflowPane.getWorkflowEntity().getSchedulePolicy()) {
                    Date time = startTime;

                    List<BpmnSequenceFlow> nextFlows = this.getNextFlows(bpmnNode.getId(), bpmnProcess.getSequenceFlowCollection());
                    for (BpmnSequenceFlow nextFlow : nextFlows) {
                        if (nextFlow.getTargetNode() instanceof BpmnTask) {
                            Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), time);
                            if (!result.getLeft()) {
                                return new Tuple(false, startTime);
                            }

                            ConWorkflowWorkInfoEntity workflowWork = this.getWork(nextFlow.getTargetRef());
                            time = workflowWork.getSkipFlag() ? workflowWork.getStandardStartTime() : workflowWork.getStandardEndTime();
                        } else {
                            Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime);
                            if (!result.getLeft()) {
                                return new Tuple(false, startTime);
                            }
                        }
                    }

                } else {
                    List<BpmnSequenceFlow> nextFlows = this.getNextFlows(bpmnNode.getId(), bpmnProcess.getSequenceFlowCollection());
                    for (BpmnSequenceFlow nextFlow : nextFlows) {
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
     */
    public void updateTimetable(WorkCell workCell, long diff, boolean isEdited) {
        try {

            if (isEdited) {
                final ParallelStartCell parallelStartCell = this.getParallelStartCell(workCell.getBpmnNode());
                final ConWorkflowWorkInfoEntity workflowWork = ((WorkCell) workCell).getWorkflowWork();

                if (Objects.nonNull(parallelStartCell)) {
                    List<CellBase> cells = parallelStartCell.getFirstRow();
                    int index = cells.indexOf(workCell);
                    this.shiftTime(parallelStartCell, index, workflowWork.getStandardStartTime());
                } else {
                    Date baseTime = workflowWork.getSkipFlag() ? workflowWork.getStandardStartTime() : workflowWork.getStandardEndTime();
                    this.shiftTime(workCell, 0, baseTime);
                }
                return;
            }

            this.bpmnProcess = this.bpmnModel.getBpmnDefinitions().getProcess();
            this.shiftTime(workCell, 0, workCell.getWorkflowWork().getStandardEndTime());
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
            ConWorkflowWorkInfoEntity workflowWork = this.getWork(bpmnNode.getId());
            if (workflowWork.getSkipFlag()) {
                List<BpmnSequenceFlow> previousFlows = this.getFlows(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
                for (BpmnSequenceFlow previouFlow : previousFlows) {
                    Date lastDate = this.getLastWorkTime(previouFlow.getSourceNode(), date);
                    if (date.before(lastDate)) {
                        date = lastDate;
                    }
                }
            } else if (date.before(workflowWork.getStandardEndTime())) {
                date = workflowWork.getStandardEndTime();
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
     * 工程順オーダーを更新する。
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
     * 工程順オーダーを更新する。
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
                ConWorkflowWorkInfoEntity workflowWork = this.getWork(bpmnNode.getId());
                logger.info("Work: {}, {}", workflowWork.getWorkName(), nextOrder);

                bpmnNode.setName(workflowWork.getWorkName());
                workflowWork.setWorkflowOrder(nextOrder);
                workflowWork.updateMember();

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
                nextOrder = (this.groupNum * 10000) + 1;
            }

            if (!flows.isEmpty() && flows.get(0).getTargetNode() instanceof BpmnTask) {
                // 直列工程の場合、最終桁の値を統一化
                nextOrder = (this.groupNum * 10000) + (nextOrder % 10000);
            } else {
                nextOrder = (this.groupNum * 10000) + 1;
            }

            this.groupNum++;
        }

        return true;
    }

    /**
     * 工程順ダイアグラムペインを生成する。
     */
    private void createWorkflowPane() {
        List<BpmnStartEvent> startEventCollection = this.bpmnProcess.getStartEventCollection();
        List<BpmnEndEvent> endEventCollection = this.bpmnProcess.getEndEventCollection();
        List<BpmnSequenceFlow> sequenceFlowCollection = this.bpmnProcess.getSequenceFlowCollection();
        if (startEventCollection.size() != 1 || endEventCollection.size() != 1) {
            return;
        }

        BpmnStartEvent startEvent = startEventCollection.get(0);
        BpmnEndEvent endEvent = endEventCollection.get(0);

        StartCell startCell = new StartCell(this.draggable);
        startCell.setBpmnNode(startEvent);
        EndCell endCell = new EndCell();
        endCell.setBpmnNode(endEvent);
        this.workflowPane.createPane(startCell, endCell);

        if (draggable) {
            startCell.setOnDragDropped((DragEvent ev) -> {
                System.out.println("DragDropped");
                dropToWorkCell(ev);
            });
        }

        if (sequenceFlowCollection.size() == 1) {
            return;
        }

        this.restore(startEvent);

        this.parallelId = this.getMaxParallelId(this.bpmnProcess.getParallelGatewayCollection()) + 1;
    }

    /**
     * 工程順ダイアグラムを復元する。
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
     * 工程順ダイアグラムを復元する。
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
                ConWorkflowWorkInfoEntity workflow = this.getWork(target.getId());
                if (Objects.nonNull(workflow)) {
                    WorkCell cell = createWorkCell(workflow);
                    cell.setBpmnNode(target);
                    if (sourceCell instanceof ParallelStartCell) {
                        ret = workflowPane.addParallelCell((ParallelStartCell) sourceCell, -1, cell);
                    } else {
                        ret = workflowPane.addNextCell(sourceCell, cell);
                    }
                }

            } else if (target instanceof BpmnParallelGateway) {
                ParallelStartCell parallelStart = createParallelStartCell();
                ParallelEndCell parallelEnd = createParallelEndCell(parallelStart);
                parallelStart.setBpmnNode(target);
                parallelEnd.setBpmnNode(((BpmnParallelGateway) target).getPairedGateway());
                if (sourceCell instanceof ParallelStartCell) {
                    ret = this.workflowPane.addParallelCell((ParallelStartCell) sourceCell, -1, parallelStart, parallelEnd);
                } else {
                    ret = this.workflowPane.addNextCell(sourceCell, parallelStart, parallelEnd);
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
    private ConWorkflowWorkInfoEntity getWork(String workId) {
        try {
            WorkflowInfoEntity workflow = workflowPane.getWorkflowEntity();
            Optional<ConWorkflowWorkInfoEntity> workflowWorkOpt = workflow.getConWorkflowWorkInfoCollection().stream().filter(p -> String.valueOf(p.getFkWorkId()).equals(workId)).findFirst();
            if (workflowWorkOpt.isPresent()) {
                return workflowWorkOpt.get();
            }

            Optional<CellBase> cellOpt = workflowPane.getCellList().stream().filter(o -> {
                if (o instanceof WorkCell) {
                    ConWorkflowWorkInfoEntity workflowWork = ((WorkCell) o).getWorkflowWork();
                    return String.valueOf(workflowWork.getFkWorkId()).equals(workId);
                }
                return false;
            }).findFirst();

            if (cellOpt.isPresent()) {
                return ((WorkCell) cellOpt.get()).getWorkflowWork();
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
            Optional<CellBase> opt = workflowPane.getCellList().stream().filter(p -> p.getBpmnNode().getId().equals(id)).findFirst();
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
    public ParallelStartCell getParallelStartCell(WorkCell cell) {
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
                ConWorkflowWorkInfoEntity workflowWork = this.getWork(bpmnNode.getId());
                return workflowWork.getStandardStartTime();
            }
            return new Date(Long.MAX_VALUE);
        } catch (Exception ex) {
            return new Date(Long.MAX_VALUE);
        }
    }

    /**
     * 工程順オーダーを取得する。
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
                ConWorkflowWorkInfoEntity workflowWork = this.getWork(bpmnNode.getId());
                return workflowWork.getWorkflowOrder();
            }
            return Integer.MAX_VALUE;
        } catch (Exception ex) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 工程エンティティからセルを生成する
     *
     * @param work
     * @return
     */
    public WorkCell createWorkCell(WorkInfoEntity work) {
        // 開始時間終了時間の設定
        Date startDate = minDate;
        Date endDate = new Date(startDate.getTime() + work.getTaktTime());

        // 工程順工程関連付け
        ConWorkflowWorkInfoEntity workflowWork = new ConWorkflowWorkInfoEntity();
        workflowWork.setFkWorkflowId(this.workflowPane.getWorkflowEntity().getWorkflowId());
        workflowWork.setFkWorkId(work.getWorkId());
        workflowWork.setWorkName(work.getWorkName() + " : " + work.getWorkRev());
        workflowWork.setStandardStartTime(startDate);
        workflowWork.setStandardEndTime(endDate);

        return createWorkCell(workflowWork);
    }

    /**
     * 工程関連付けデータからセルを生成する
     *
     * @param workflowWork
     * @return
     */
    public WorkCell createWorkCell(ConWorkflowWorkInfoEntity workflowWork) {

        WorkCell cell = new WorkCell(workflowWork, workflowWork.getWorkName(), draggable, editable, getCurrentScale());

        if (draggable) {
            cell.setOnDragDetected((MouseEvent ev) -> {
                System.out.println("DragDetected");
                startDragWorkCell(ev);
            });
            cell.setOnDragDone((DragEvent ev) -> {
                System.out.println("DragDone");
            });
            cell.setOnDragDropped((DragEvent ev) -> {
                System.out.println("DragDropped");
                dropToWorkCell(ev);
            });
        }
        return cell;
    }

    /**
     * 並列作業開始セルを生成する
     *
     * @return
     */
    public ParallelStartCell createParallelStartCell() {

        ParallelStartCell cell = new ParallelStartCell(draggable);

        if (draggable) {
            cell.setOnDragDetected((MouseEvent ev) -> {
                System.out.println("DragDetected");
                startDragParallelCell(ev);
            });
            cell.setOnDragDone((DragEvent ev) -> {
                System.out.println("DragDone");
            });
            cell.setOnDragDropped((DragEvent ev) -> {
                System.out.println("DragDropped");
                dropToParallelStartCell(ev);
            });
        }
        return cell;
    }

    /**
     * 並列作業終了セルを生成する
     *
     * @param startCell
     * @return
     */
    public ParallelEndCell createParallelEndCell(ParallelStartCell startCell) {

        ParallelEndCell cell = new ParallelEndCell(startCell, draggable);

        if (draggable) {
            cell.setOnDragDropped((DragEvent ev) -> {
                System.out.println("DragDropped");
                dropToParallelEndCell(ev);
            });
        }
        return cell;
    }

    /**
     * 工程セルのドラッグを開始する
     *
     * @param event
     */
    private void startDragWorkCell(MouseEvent event) {
        try {
            if (!(event.getSource() instanceof Node)) {
                return;
            }
            Node node = (Node) event.getSource();
            while (!(node instanceof WorkCell)) {
                if (Objects.isNull(node.getParent())) {
                    return;
                }
                node = node.getParent();
            }

            // コピーが使用できるドラッグボードを生成
            WorkCell cell = (WorkCell) node;
            Dragboard dragboard = cell.startDragAndDrop(TransferMode.COPY);

            // コンテントをドラッグボードに保持させる
            ClipboardContent content = new ClipboardContent();
            DataFormat dataFormat = DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class);
            WorkflowFlowCellEntity flowCell
                    = new WorkflowFlowCellEntity(cell.getBpmnNode().getId());
            content.put(dataFormat, flowCell);
            dragboard.setContent(content);

            // ドラッグ中は半透明にし、ドラッグをしていることを分かるようにする
            WorkCell dragView = new WorkCell(cell.getWorkflowWork(), cell.getWorkName(), false);
            dragView.setOpacity(0.6);
            dragView.getStylesheets().addAll(sc.getSceneProperties().getCsspathes());
            Scene scene = new Scene(dragView);
            dragboard.setDragView(dragView.snapshot(null, null));
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 並列作業セルのドラッグを開始する
     *
     * @param event
     */
    private void startDragParallelCell(MouseEvent event) {
        try {
            if (!(event.getSource() instanceof Node)) {
                return;
            }
            Node node = (Node) event.getSource();
            while (!(node instanceof ParallelStartCell)) {
                if (Objects.isNull(node.getParent())) {
                    return;
                }
                node = node.getParent();
            }

            // コピーが使用できるドラッグボードを生成
            ParallelStartCell parallelStart = (ParallelStartCell) node;
            Dragboard dragboard = parallelStart.startDragAndDrop(TransferMode.COPY);

            // コンテントをドラッグボードに保持させる
            ClipboardContent content = new ClipboardContent();
            DataFormat dataFormat = DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class);
            WorkflowFlowCellEntity flowCell = new WorkflowFlowCellEntity(parallelStart.getBpmnNode().getId());
            content.put(dataFormat, flowCell);
            dragboard.setContent(content);

            // ドラッグ中は半透明にし、ドラッグをしていることを分かるようにする 
            WorkflowPane dragView = new WorkflowPane();

            ParallelStartCell start = new ParallelStartCell(draggable);
            ParallelEndCell end = new ParallelEndCell(start, draggable);
            dragView.createPane(start, end);
            duplicateParallelPaneView(dragView, start, parallelStart);

            dragView.setOpacity(0.6);
            dragView.getStylesheets().addAll(sc.getSceneProperties().getCsspathes());
            Scene scene = new Scene(dragView);
            dragboard.setDragView(dragView.snapshot(null, null));
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * DragView用セル複製処理
     *
     * @param pane
     * @param dest
     * @param src
     */
    private void duplicateParallelPaneView(WorkflowPane pane, CellBase dest, CellBase src) {
        if (src instanceof WorkCell) {
            // ワークセルの関連複製(直列方向)
            WorkCell srcWorkCell = (WorkCell) src;
            WorkCell destWorkCell = (WorkCell) dest;

            CellBase cell = srcWorkCell.getNextCell();
            if (cell instanceof WorkCell) {
                WorkCell work = new WorkCell(new ConWorkflowWorkInfoEntity(), ((WorkCell) cell).getWorkName(), false);
                pane.addNextCell(destWorkCell, work);
                duplicateParallelPaneView(pane, work, (WorkCell) cell);

            } else if (cell instanceof ParallelStartCell) {
                ParallelStartCell start = new ParallelStartCell(false);
                ParallelEndCell end = new ParallelEndCell(start, false);
                pane.addNextCell(destWorkCell, start, end);
                duplicateParallelPaneView(pane, start, (ParallelStartCell) cell);

            }
        } else if (src instanceof ParallelStartCell) {
            // 並列作業セルの関連複製(並列方向)
            ParallelStartCell srcParaCell = (ParallelStartCell) src;
            ParallelStartCell destParaCell = (ParallelStartCell) dest;

            for (CellBase cell : srcParaCell.getFirstRow()) {
                if (cell instanceof WorkCell) {
                    WorkCell work = new WorkCell(new ConWorkflowWorkInfoEntity(), ((WorkCell) cell).getWorkName(), false);
                    pane.addParallelCell(destParaCell, -1, work);
                    duplicateParallelPaneView(pane, work, (WorkCell) cell);

                } else if (cell instanceof ParallelStartCell) {
                    ParallelStartCell start = new ParallelStartCell(false);
                    ParallelEndCell end = new ParallelEndCell(start, false);
                    pane.addParallelCell(destParaCell, -1, start, end);
                    duplicateParallelPaneView(pane, start, (ParallelStartCell) cell);

                }
            }
        }
    }

    /**
     * 工程セルへのドロップ処理
     *
     * @param event
     */
    private void dropToWorkCell(DragEvent event) {
        try {
            if (!(event.getSource() instanceof CellBase)) {
                // ドラッグ失敗を返す
                event.setDropCompleted(false);
                return;
            }
            CellBase frontCell = (CellBase) event.getSource();

            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkInfoEntity.class))) {
                WorkInfoEntity work = (WorkInfoEntity) dragboard
                        .getContent(DataFormatUtil.getDataFormat(WorkInfoEntity.class));
                // 新規工程の追加
                switch (frontCell.getStatus()) {
                    case BOTTOM:
                        addSerial(frontCell, work);
                        break;

                    case RIGHT:
                        addParallel(frontCell, work);
                        break;
                }
                // ドラッグ成功を返す
                event.setDropCompleted(true);

            } else if (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class))) {
                CellBase cell = (CellBase) findCellBase(((WorkflowFlowCellEntity) dragboard
                        .getContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class))).getCellId());
                if (cell instanceof WorkCell) {
                    // 既存工程の移動
                    switch (frontCell.getStatus()) {
                        case BOTTOM:
                            moveSerial(frontCell, (WorkCell) cell);
                            break;

                        case RIGHT:
                            moveParallel(frontCell, (WorkCell) cell);
                            break;
                    }
                    // ドラッグ成功を返す
                    event.setDropCompleted(true);
                } else if (cell instanceof ParallelStartCell) {
                    // 並列作業の移動
                    switch (frontCell.getStatus()) {
                        case BOTTOM:
                            moveSerial(frontCell, (ParallelStartCell) cell);
                            break;

                        case RIGHT:
                            moveParallel(frontCell, (ParallelStartCell) cell);
                            break;
                    }
                    // ドラッグ成功を返す
                    event.setDropCompleted(true);
                }

            } else {
                // ドラッグ失敗を返す
                event.setDropCompleted(false);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            // ドラッグ失敗を返す
            event.setDropCompleted(false);
        }
    }

    /**
     * 並列作業開始セルへのドロップ処理
     *
     * @param event
     */
    private void dropToParallelStartCell(DragEvent event) {
        try {
            if (!(event.getSource() instanceof ParallelStartCell)) {
                // ドラッグ失敗を返す
                event.setDropCompleted(false);
                return;
            }

            ParallelStartCell frontCell = (ParallelStartCell) event.getSource();
            //並列作業開始セル直下の右端セルを取得
            List<CellBase> firstRowCells = ((ParallelStartCell) event.getSource()).getFirstRow();
            WorkCell rightEndCell = (WorkCell) firstRowCells.get(firstRowCells.size() - 1);

            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkInfoEntity.class))) {
                WorkInfoEntity work = (WorkInfoEntity) dragboard
                        .getContent(DataFormatUtil.getDataFormat(WorkInfoEntity.class));
                // 新規工程の追加
                if (frontCell.getStatus().equals(MarkerStatus.BOTTOM)) {
                    addParallel(rightEndCell, work);
                }
                // ドラッグ成功を返す
                event.setDropCompleted(true);

            } else if (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class))) {
                CellBase cell = (CellBase) findCellBase(((WorkflowFlowCellEntity) dragboard
                        .getContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class))).getCellId());
                if (cell instanceof WorkCell) {
                    // 既存工程の移動
                    if (frontCell.getStatus().equals(MarkerStatus.BOTTOM)) {
                        moveParallel(rightEndCell, (WorkCell) cell);
                    }
                    // ドラッグ成功を返す
                    event.setDropCompleted(true);
                } else if (cell instanceof ParallelStartCell) {
                    // 既存工程の移動
                    if (frontCell.getStatus().equals(MarkerStatus.BOTTOM)) {
                        moveParallel(rightEndCell, (ParallelStartCell) cell);
                    }
                    // ドラッグ成功を返す
                    event.setDropCompleted(true);
                }

            } else {
                // ドラッグ失敗を返す
                event.setDropCompleted(false);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            // ドラッグ失敗を返す
            event.setDropCompleted(false);
        }
    }

    /**
     * 並列作業終了セルへのドロップ処理
     *
     * @param event
     */
    private void dropToParallelEndCell(DragEvent event) {
        try {
            if (!(event.getSource() instanceof ParallelEndCell)) {
                // ドラッグ失敗を返す
                event.setDropCompleted(false);
                return;
            }
            ParallelEndCell frontCell = (ParallelEndCell) event.getSource();

            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkInfoEntity.class))) {
                WorkInfoEntity work = (WorkInfoEntity) dragboard
                        .getContent(DataFormatUtil.getDataFormat(WorkInfoEntity.class));
                // 新規工程の追加
                if (frontCell.getStatus().equals(MarkerStatus.BOTTOM)) {
                    addSerial(frontCell, work);
                }
                // ドラッグ成功を返す
                event.setDropCompleted(true);

            } else if (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class))) {
                CellBase cell = (CellBase) findCellBase(((WorkflowFlowCellEntity) dragboard
                        .getContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class))).getCellId());
                if (cell instanceof WorkCell) {
                    // 既存工程の移動
                    if (frontCell.getStatus().equals(MarkerStatus.BOTTOM)) {
                        moveSerial(frontCell, (WorkCell) cell);
                    }
                    // ドラッグ成功を返す
                    event.setDropCompleted(true);
                } else if (cell instanceof ParallelStartCell) {
                    // 並列作業の移動
                    if (frontCell.getStatus().equals(MarkerStatus.BOTTOM)) {
                        moveSerial(frontCell, (ParallelStartCell) cell);
                    }
                    // ドラッグ成功を返す
                    event.setDropCompleted(true);
                }

            } else {
                // ドラッグ失敗を返す
                event.setDropCompleted(false);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            // ドラッグ失敗を返す
            event.setDropCompleted(false);
        }
    }

    /**
     * 工程セルの直列追加
     *
     * @param frontCell
     * @param work
     */
    private void addSerial(CellBase frontCell, WorkInfoEntity work) {
        this.bpmnProcess = this.bpmnModel.getBpmnDefinitions().getProcess();
        if (Objects.isNull(frontCell)
                || Objects.isNull(work)
                || this.bpmnProcess.getTaskCollection().stream().filter(p -> p.getId().equals(work.getWorkId().toString())).count() != 0) {
            return;
        }

        //工程セル作成
        WorkCell cell = this.createWorkCell(work);

        //工程セル追加
        if (this.addWithUpdateTimetable(frontCell, cell)) {
            this.workflowPane.getWorkflowEntity().setConWorkflowWorkInfoCollection(
                    this.workflowPane
                            .getCellList()
                            .stream()
                            .filter(item -> item instanceof WorkCell)
                            .map(item -> (WorkCell) item)
                            .map(WorkCell::getWorkflowWork)
                            .collect(Collectors.toList()));
            cell.setSelected(true);
        }

        this.updateTimetable(cell, cell.getWorkflowWork().getTaktTime(), false);
        this.updateWorkflowOrder();
    }

    /**
     * 工程セルへの既存工程セル直列移動
     *
     * @param frontCell
     * @param cell
     */
    private void moveSerial(CellBase frontCell, WorkCell cell) {
        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
        if (Objects.isNull(frontCell) || Objects.isNull(cell)
                || this.bpmnProcess.getTaskCollection().stream().filter(p -> p.getId().equals(cell.getBpmnNode().getId())).count() != 1) {
            return;
        }

        //工程セル移動
        if (this.moveWithUpdateTimetable(frontCell, cell)) {
            this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
            this.removeUnnecessaryCell();
            this.updateWorkflowOrder();
            cell.setSelected(true);
        }
        this.updateWorkflowOrder();
    }

    /**
     * 工程セルへの並列作業セルの直列移動
     *
     * @param frontCell
     * @param cell
     */
    private void moveSerial(CellBase frontCell, ParallelStartCell gateway) {
        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
        if (Objects.isNull(frontCell) || Objects.isNull(gateway)
                || this.bpmnProcess.getParallelGatewayCollection().stream().filter(p -> p.getId().equals(gateway.getBpmnNode().getId())).count() != 1) {
            return;
        }

        //並列作業移動
        if (this.moveWithUpdateTimetable(frontCell, gateway, gateway.getParallelEndCell())) {
            this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
            this.removeUnnecessaryCell();
            this.updateWorkflowOrder();

            gateway.setSelected(true);
        }
        this.updateWorkflowOrder();
    }

    /**
     * 工程セルの並列追加
     *
     * @param frontCell
     * @param work
     */
    private void addParallel(CellBase frontCell, WorkInfoEntity work) {
        this.bpmnProcess = this.bpmnModel.getBpmnDefinitions().getProcess();
        if (Objects.isNull(frontCell)
                || Objects.isNull(work)
                || this.bpmnProcess.getTaskCollection().stream()
                .filter(p -> p.getId().equals(work.getWorkId().toString())).count() != 0) {
            return;
        }

        // 工程セル作成
        WorkCell cell = this.createWorkCell(work);

        // 選択セルが並列作業開始セル直下か否か確認
        WorkCell selectedCell = (WorkCell) frontCell;
        CellBase previousCell = this.workflowPane.getPreviousCell(selectedCell);

        if (Objects.nonNull(previousCell) && !(previousCell instanceof ParallelStartCell)) {
            // 並列作業開始セル直下でない場合、新規に並列作業を作成し、そこへ追加する。
            ParallelStartCell parallelStartCell = createParallelStartCell();
            ParallelEndCell parallelEndCell = createParallelEndCell(parallelStartCell);

            if (this.addGateway(previousCell, parallelStartCell, parallelEndCell)
                    && this.move(parallelStartCell, selectedCell)
                    && this.addWithUpdateTimetable(parallelStartCell, 1, cell)) {
                this.updateWorkflowOrder();
                workflowPane.getWorkflowEntity().getConWorkflowWorkInfoCollection().add(cell.getWorkflowWork());
                cell.setSelected(true);
            }
        } else {
            // 並列作業開始セル直下の場合、選択セルのインデックスを確認、その右に追加する。
            ParallelStartCell parallelStartCell = this.getParallelStartCell(selectedCell);
            List<CellBase> cells = parallelStartCell.getFirstRow();
            int index = cells.indexOf(selectedCell) + 1;
            if (this.addWithUpdateTimetable(parallelStartCell, index, cell)) {
                this.updateWorkflowOrder();
                workflowPane.getWorkflowEntity().getConWorkflowWorkInfoCollection().add(cell.getWorkflowWork());
                cell.setSelected(true);
            }
        }

        this.updateTimetable(cell, cell.getWorkflowWork().getTaktTime(), false);
        this.updateWorkflowOrder();
    }

    /**
     * 工程セルへの既存工程セル並列移動
     *
     * @param frontCell
     * @param cell
     */
    private void moveParallel(CellBase frontCell, WorkCell cell) {
        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
        if (Objects.isNull(frontCell) || Objects.isNull(cell)
                || this.bpmnProcess.getTaskCollection().stream()
                .filter(p -> p.getId().equals(cell.getBpmnNode().getId())).count() != 1) {
            return;
        }

        //移動先セルが並列作業開始セル直下か否か確認
        WorkCell frontWorkCell = (WorkCell) frontCell;
        CellBase previousCell = this.workflowPane.getPreviousCell(frontWorkCell);

        if (Objects.nonNull(previousCell) && !(previousCell instanceof ParallelStartCell)) {
            //並列作業開始セル直下でない場合、新規に並列作業を作成し、そこに移動する。
            ParallelStartCell parallelStartCell = createParallelStartCell();
            ParallelEndCell parallelEndCell = createParallelEndCell(parallelStartCell);

            if (this.addGateway(previousCell, parallelStartCell, parallelEndCell)
                    && this.move(parallelStartCell, frontWorkCell)
                    && this.moveWithUpdateTimetable(parallelStartCell, 1, cell)) {
                this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
                this.removeUnnecessaryCell();
                this.updateWorkflowOrder();
                cell.setSelected(true);
            }
        } else {
            //並列作業開始セル直下の場合、選択セルのインデックスを確認、その右に移動する。
            ParallelStartCell parallelStartCell = this.getParallelStartCell(frontWorkCell);
            List<CellBase> cells = parallelStartCell.getFirstRow();
            int index = cells.indexOf(frontWorkCell) + 1;
            if (this.moveWithUpdateTimetable(parallelStartCell, index, cell)) {
                this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
                this.removeUnnecessaryCell();
                this.updateWorkflowOrder();
                cell.setSelected(true);
            }
        }

        //this.updateTimetable(cell, cell.getWorkflowWork().getTaktTime(), false);
        //this.updateWorkflowOrder();
    }

    /**
     * 工程セルへの並列作業開始セル並列移動
     *
     * @param frontCell
     * @param cell
     */
    private void moveParallel(CellBase frontCell, ParallelStartCell gateway) {
        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
        if (Objects.isNull(frontCell) || Objects.isNull(gateway)
                || this.bpmnProcess.getParallelGatewayCollection().stream()
                .filter(p -> p.getId().equals(gateway.getBpmnNode().getId())).count() != 1) {
            return;
        }

        // 移動先セルが並列作業開始セル直下か否か確認
        WorkCell frontWorkCell = (WorkCell) frontCell;
        CellBase previousCell = this.workflowPane.getPreviousCell(frontWorkCell);
        WorkCell firstWorkCell = this.workflowPane.getFirstWorkCell(gateway);

        if (Objects.nonNull(previousCell) && !(previousCell instanceof ParallelStartCell)) {
            // 並列作業開始セル直下でない場合、新規に並列作業を作成し、そこに移動する。
            ParallelStartCell parallelStartCell = createParallelStartCell();
            ParallelEndCell parallelEndCell = createParallelEndCell(parallelStartCell);

            if (this.addGateway(previousCell, parallelStartCell, parallelEndCell)
                    && this.move(parallelStartCell, frontWorkCell)
                    && this.moveWithUpdateTimetable(parallelStartCell, 1, gateway, gateway.getParallelEndCell())) {
                this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
                this.removeUnnecessaryCell();
                this.updateWorkflowOrder();
                gateway.setSelected(true);
            }
        } else {
            // 並列作業開始セル直下の場合、選択セルのインデックスを確認、その右に移動する。
            ParallelStartCell parallelStartCell = this.getParallelStartCell(frontWorkCell);
            List<CellBase> cells = parallelStartCell.getFirstRow();
            int index = cells.indexOf(frontWorkCell) + 1;
            if (this.moveWithUpdateTimetable(parallelStartCell, index, gateway, gateway.getParallelEndCell())) {
                this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
                this.removeUnnecessaryCell();
                this.updateWorkflowOrder();
                gateway.setSelected(true);
            }
        }

        //WorkCell cell = this.workflowPane.getFirstWorkCell(this.getParallelStartCell(firstWorkCell));
        //this.updateTimetable(cell, cell.getWorkflowWork().getTaktTime(), false);
        //this.updateWorkflowOrder();
    }

    /**
     * 不要なParallelCellを削除
     */
    public void removeUnnecessaryCell() {

        // 空となったParallelCell
        List<ParallelStartCell> emptyCells = new ArrayList<>();
        // 直列工程となったParallelCell
        List<ParallelStartCell> changeCells = new ArrayList<>();
        // ParallelCell直下のParallelCell
        List<ParallelStartCell> overlapCells = new ArrayList<>();

        // 不要となったParallelCellを削除
        do {
            emptyCells.clear();
            changeCells.clear();
            overlapCells.clear();

            for (CellBase cell : workflowPane.getCellList()) {
                if (cell instanceof ParallelStartCell) {
                    ParallelStartCell parallelCell = (ParallelStartCell) cell;
                    ObservableList<Node> children = parallelCell.getParallelPane().getChildren();

                    if (children.isEmpty()) {
                        emptyCells.add(parallelCell);
                    }

                    if (children.size() == 1) {
                        changeCells.add(parallelCell);
                    }

                    if (parallelCell.getPrevCell() instanceof ParallelStartCell) {
                        ParallelStartCell prev = (ParallelStartCell) parallelCell.getPrevCell();
                        if (prev.getParallelEndCell() == parallelCell.getParallelEndCell().getNextCell()) {
                            overlapCells.add(parallelCell);
                        }
                    }
                }
            }

            // 空となったParallelCellを削除
            for (ParallelStartCell emptyCell : emptyCells) {
                remove(emptyCell, emptyCell.getParallelEndCell());
            }

            // 直列工程となったParallelCellを再構築
            for (ParallelStartCell changeCell : changeCells) {
                List<CellBase> cells = changeCell.getCells();

                for (ListIterator iterator = cells.listIterator(cells.size()); iterator.hasPrevious();) {
                    CellBase cell = (CellBase) iterator.previous();
                    if (cell instanceof WorkCell) {
                        move(changeCell.getParallelEndCell(), (WorkCell) cell);
                    } else if (cell instanceof ParallelStartCell) {
                        moveGateway(changeCell.getParallelEndCell(),
                                (ParallelStartCell) cell, ((ParallelStartCell) cell).getParallelEndCell());
                    }
                }
                remove(changeCell, changeCell.getParallelEndCell());
            }

            // ParallelCell直下のParallelCellを統合
            for (ParallelStartCell overlapCell : overlapCells) {
                ParallelStartCell parent = (ParallelStartCell) overlapCell.getPrevCell();
                int index = parent.getFirstRow().indexOf(overlapCell);
                
                CellBase prev;
                CellBase curr;
                CellBase next = null;
                List<CellBase> firstRowCells = overlapCell.getFirstRow();
                for (CellBase cell : firstRowCells) {
                    curr = cell;
                    if (curr instanceof ParallelStartCell) {
                        ParallelStartCell startCell = (ParallelStartCell) curr;
                        next = startCell.getParallelEndCell().getNextCell();
                        moveWithUpdateTimetable(parent, index, startCell, startCell.getParallelEndCell());
                    } else if (curr instanceof WorkCell) {
                        WorkCell workCell = (WorkCell) curr;
                        next = workCell.getNextCell();
                        moveWithUpdateTimetable(parent, index, workCell);
                    }
                    prev = curr;
                    curr = next;

                    while (!Objects.equals(next, overlapCell.getParallelEndCell())) {
                        if (curr instanceof ParallelStartCell) {
                            ParallelStartCell startCell = (ParallelStartCell) curr;
                            next = startCell.getParallelEndCell().getNextCell();
                            moveWithUpdateTimetable(prev, startCell, startCell.getParallelEndCell());
                        } else if (curr instanceof WorkCell) {
                            WorkCell workCell = (WorkCell) curr;
                            next = workCell.getNextCell();
                            moveWithUpdateTimetable(prev, workCell);
                        }
                        prev = curr;
                        curr = next;
                    }

                    index++;
                }

                remove(overlapCell, overlapCell.getParallelEndCell());
            }

            updateWorkflowOrder();

        } while (!emptyCells.isEmpty() || !changeCells.isEmpty()); // 削除対象が無くなるまで繰り返す

        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
    }

    /**
     * 各セルの拡大率を設定する。
     *
     * @param value 1.0を基準とした拡大率
     */
    public void setScale(double value) {
        this.workflowPane.setScale(value);
    }

    /**
     * 工程セルの設備名の表示有無を設定する。
     *
     * @param value trueの場合設備名を表示する
     */
    public void setVisibleEquipment(Boolean value) {
        this.workflowPane.setVisibleEquipment(value);
    }

    /**
     * 工程セルの組織名の表示有無を設定する。
     *
     * @param value trueの場合組織名を表示する
     */
    public void setVisibleOrganization(Boolean value) {
        this.workflowPane.setVisibleOrganization(value);
    }

    /**
     * 任意のセルの拡大率をもとにして現在の拡大率を調べる。
     *
     * @return
     */
    private double getCurrentScale() {
        return this.workflowPane.getCellList().stream()
                .findAny()
                .map(base -> base.getScale())
                .orElse(1.0);
    }
}
