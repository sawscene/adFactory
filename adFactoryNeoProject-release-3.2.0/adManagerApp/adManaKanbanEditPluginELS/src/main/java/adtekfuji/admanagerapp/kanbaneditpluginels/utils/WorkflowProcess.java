/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.utils;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.Tuple;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.bpmn.model.BpmnModel;
import jp.adtekfuji.bpmn.model.BpmnModeler;
import jp.adtekfuji.bpmn.model.entity.BpmnDocument;
import jp.adtekfuji.bpmn.model.entity.BpmnEndEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnProcess;
import jp.adtekfuji.bpmn.model.entity.BpmnSequenceFlow;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ワークフロープロセス
 *
 * @author s-heya
 */
public class WorkflowProcess {

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
                int order1 = getWorkKanbanOrder(flow1.getTargetNode());
                int order2 = getWorkKanbanOrder(flow2.getTargetNode());
                return order1 <= order2 ? -1 : 1;
            }
            return compare;
        }
    }

    private final Logger logger = LogManager.getLogger();
    private final BpmnModel bpmnModel;
    private BpmnProcess bpmnProcess;
    private Map<String, WorkKanbanInfoEntity> workKanbanMap;
    private List<BreakTimeInfoEntity> breakTimes;

    /**
     * コンストラクタ
     *
     * @param diagram
     */
    public WorkflowProcess(String diagram) {
        this.bpmnModel = BpmnModeler.getModeler();
        try {
            BpmnDocument bpmn = BpmnDocument.unmarshal(diagram);
            this.bpmnModel.createModel(bpmn);
        } catch (JAXBException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 基準時間を設定する。
     *
     * @param kanban カンバン
     * @param breakTimes 休憩時間リスト
     * @param baseDatetime 基準時間
     * @throws JAXBException
     */
    public void setBaseTime(KanbanInfoEntity kanban, List<BreakTimeInfoEntity> breakTimes, Date baseDatetime) throws JAXBException {
        logger.info("setBaseTime start.");

        List<WorkKanbanInfoEntity> entities = kanban.getWorkKanbanCollection();

        this.workKanbanMap = new HashMap<>();
        for (WorkKanbanInfoEntity entity : entities) {
            workKanbanMap.put(String.valueOf(entity.getFkWorkId()), entity);
        }

        this.breakTimes = breakTimes;
        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

        // 基準時間を設定
        List<BpmnSequenceFlow> flows = this.getNextFlows("start_id", this.bpmnProcess.getSequenceFlowCollection());
        Tuple<Boolean, Date> result = this.shiftTime(flows, null, baseDatetime, true);

        kanban.setStartDatetime(baseDatetime);
        kanban.setCompDatetime(result.getRight());

        logger.info("setBaseTime end: {}, {}", kanban.getStartDatetime(), kanban.getCompDatetime());
    }

    /**
     * 作業時間を更新する。
     *
     * @param kanban カンバン
     * @param workKanban 工程カンバン
     * @param breakTimes 休憩時間リスト
     */
    public void updateTimetable(KanbanInfoEntity kanban, WorkKanbanInfoEntity workKanban, List<BreakTimeInfoEntity> breakTimes) {
        logger.info("updateTimetable start.");

        List<WorkKanbanInfoEntity> entities = kanban.getWorkKanbanCollection();
        this.workKanbanMap = new HashMap<>();
        for (WorkKanbanInfoEntity entity : entities) {
            workKanbanMap.put(String.valueOf(entity.getFkWorkId()), entity);
        }

        this.breakTimes = breakTimes;
        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

        List<BpmnSequenceFlow> flows;
        Date startTime = null;

        if (Objects.nonNull(workKanban)) {
            flows = this.getFlows(String.valueOf(workKanban.getFkWorkId()), this.bpmnProcess.getSequenceFlowCollection());
            startTime = workKanban.getStartDatetime();

            if (flows.get(0).getSourceNode() instanceof BpmnParallelGateway) {
                BpmnParallelGateway gateway = (BpmnParallelGateway) flows.get(0).getSourceNode();
                flows = this.getNextFlows(gateway.getId(), this.bpmnProcess.getSequenceFlowCollection());

                for (BpmnSequenceFlow flow : flows) {
                    Date date = this.getStartTime(flow.getTargetNode());
                    if (date.before(workKanban.getStartDatetime())) {
                        continue;
                    }

                    if (flow.getTargetNode() instanceof BpmnTask) {
                        this.shiftTime(Arrays.asList(flow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime, false);
                        this.shiftTime(Arrays.asList(flow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime, true);
                        WorkKanbanInfoEntity entity = this.workKanbanMap.get(flow.getTargetRef());
                        startTime = entity.getSkipFlag()? entity.getStartDatetime() : entity.getCompDatetime();
                    } else {
                        this.shiftTime(Arrays.asList(flow), (BpmnParallelGateway) gateway.getPairedGateway(), workKanban.getStartDatetime(), false);
                        this.shiftTime(Arrays.asList(flow), (BpmnParallelGateway) gateway.getPairedGateway(), workKanban.getStartDatetime(), true);
                    }
                }

                flows = this.getNextFlows(gateway.getPairedId(), bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTime(flows.get(0), DateUtils.min());

            } else {
                startTime = this.getLastWorkTime(flows.get(0), startTime);
            }

        } else {
            flows = this.getNextFlows("start_id", this.bpmnProcess.getSequenceFlowCollection());
            startTime = kanban.getStartDatetime();
        }

        // 作業時間から休憩を除く
        this.shiftTime(flows, null, startTime, false);
        // 作業時間を更新
        Tuple<Boolean, Date> result = this.shiftTime(flows, null, startTime, true);

        kanban.setCompDatetime(result.getRight());
        logger.info("updateTimetable end: {}, {}", kanban.getStartDatetime(), kanban.getCompDatetime());
    }

    /**
     * 作業時間をシフトする。
     *
     * @param flows
     * @param endNode
     * @param baseTime
     * @param isBreak 休憩時間の扱い方 (true: 休憩時間を加算する false: 休憩時間を減算する)
     * @return
     */
    private Tuple<Boolean, Date> shiftTime(List<BpmnSequenceFlow> flows, BpmnNode endNode, Date baseTime, boolean isBreak) {

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
                WorkKanbanInfoEntity workKanban = this.workKanbanMap.get(bpmnNode.getId());

                if (isBreak) {
                    Date endTime = this.calcWithBreak(startTime, new Date(startTime.getTime() + workKanban.getCompDatetime().getTime() - workKanban.getStartDatetime().getTime()));

                    workKanban.setStartDatetime(startTime);
                    workKanban.setCompDatetime(endTime);
                    workKanban.updateMember();

                    if (!workKanban.getSkipFlag()) {
                        startTime = endTime;
                    }
                } else {
                    Date time = this.calcWithoutBreak(workKanban.getStartDatetime(), workKanban.getCompDatetime());
                    Date endTime = new Date(startTime.getTime() + time.getTime() - workKanban.getStartDatetime().getTime());

                    workKanban.setStartDatetime(startTime);
                    workKanban.setCompDatetime(endTime);
                    workKanban.updateMember();

                    startTime = endTime;
                }

                flows = this.getNextFlows(flows.get(0).getTargetRef(), this.bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTime(flows.get(0), startTime);

            } else if (bpmnNode instanceof BpmnParallelGateway) {
                // ゲートウェイ
                BpmnParallelGateway gateway = (BpmnParallelGateway) bpmnNode;
                Date time = startTime;

                List<BpmnSequenceFlow> nextFlows = this.getNextFlows(bpmnNode.getId(), bpmnProcess.getSequenceFlowCollection());
                for (BpmnSequenceFlow nextFlow : nextFlows) {
                    if (nextFlow.getTargetNode() instanceof BpmnTask) {
                        Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), time, isBreak);
                        if (!result.getLeft()) {
                           return new Tuple(false, startTime);
                        }

                        WorkKanbanInfoEntity workKanban = this.workKanbanMap.get(nextFlow.getTargetRef());
                        time = workKanban.getSkipFlag()? workKanban.getStartDatetime() : workKanban.getCompDatetime();
                    } else {
                        Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime, isBreak);
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
     * 休憩を含めた終了時間を計算する。
     * 参考資料: スキップ検討.vsd
     *
     * @param startDatetime
     * @param endDatetime
     * @return
     */
    private Date calcWithBreak(Date startDatetime, Date endDatetime) {
        Date date = endDatetime;

        long startWorkTime = startDatetime.getTime();
        long endWorkTime = endDatetime.getTime();

        List<BreakTimeInfoEntity> entities = BreaktimeUtil.getAppropriateBreaktimes(this.breakTimes, startDatetime, endDatetime);
        for (BreakTimeInfoEntity entity : entities) {
            long startBreakTime = entity.getStarttime().getTime();
            long endBreakTime = entity.getEndtime().getTime();

            long diff = 0;
            if ((startWorkTime > startBreakTime && startWorkTime < endBreakTime) && (endWorkTime < endBreakTime && endWorkTime > startBreakTime)) {
                // ④ (工程開始 > 休憩開始 and 工程開始 < 休憩終了) and (工程終了 < 休憩終了 and 工程終了 > 休憩開始)
                // 休憩時間を一部加算する
                diff = endBreakTime - startWorkTime;
            } else if (startWorkTime <= startBreakTime && endWorkTime >= endBreakTime) {
                // ① 工程開始 <= 休憩開始 and 工程終了 >= 休憩終了
                // 休憩時間を全部加算する
                diff = endBreakTime - startBreakTime;
            } else if (endWorkTime < endBreakTime && endWorkTime > startBreakTime) {
                // ② 工程終了 < 休憩終了 and 工程終了 > 休憩開始
                // 休憩時間を全部加算する
                diff = endBreakTime - startBreakTime;
            } else if (startWorkTime > startBreakTime && startWorkTime < endBreakTime) {
                // ③ 工程開始 > 休憩開始 and 工程開始 < 休憩終了
                // 休憩時間を一部加算する
                diff = endBreakTime - startWorkTime;
            }

            if (0 != diff) {
                Calendar cal = new Calendar.Builder().setInstant(endWorkTime + diff).build();
                date = cal.getTime();
                break;
            }
        }

        return date;
    }

    /**
     * 休憩を除いた終了時間を計算する。
     * 参考資料: スキップ検討.vsd
     *
     * @param startDatetime
     * @param endDatetime
     * @return
     */
    private Date calcWithoutBreak(Date startDatetime, Date endDatetime) {
        Date date = endDatetime;

        long startWorkTime = startDatetime.getTime();
        long endWorkTime = endDatetime.getTime();

        List<BreakTimeInfoEntity> entities = BreaktimeUtil.getAppropriateBreaktimes(this.breakTimes, startDatetime, endDatetime);
        for (BreakTimeInfoEntity entity : entities) {

            long startBreakTime = entity.getStarttime().getTime();
            long endBreakTime = entity.getEndtime().getTime();
            long diff = 0;

            if (startWorkTime <= startBreakTime && endWorkTime >= endBreakTime) {
                // 工程開始 <= 休憩開始 and 工程終了 >= 休憩終了
                diff = endBreakTime - startBreakTime;
            } else if (startWorkTime > startBreakTime && startWorkTime < endBreakTime) {
                // 工程開始 > 休憩開始 and 工程開始 < 休憩終了
                diff = endBreakTime - startWorkTime;
            }

            if (0 != diff) {
                Calendar cal = new Calendar.Builder().setInstant(endWorkTime - diff).build();
                date = cal.getTime();
                break;
            }
        }

        return date;
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
        } else if (flow.getSourceNode() instanceof BpmnParallelGateway){
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
            WorkKanbanInfoEntity workKanban = this.workKanbanMap.get(bpmnNode.getId());
            if (workKanban.getSkipFlag()) {
                List<BpmnSequenceFlow> previousFlows = this.getFlows(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
                for (BpmnSequenceFlow previouFlow : previousFlows) {
                    Date lastDate = this.getLastWorkTime(previouFlow.getSourceNode(), date);
                    if (date.before(lastDate)) {
                        date = lastDate;
                    }
                }
            } else {
                if (date.before(workKanban.getCompDatetime())) {
                    date = workKanban.getCompDatetime();
                }
            }

        } else if (bpmnNode instanceof BpmnParallelGateway) {
            List<BpmnSequenceFlow> previousFlows = this.getFlows(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
            for (BpmnSequenceFlow previousFlow : previousFlows) {
                Date lastDate = this.getLastWorkTime(previousFlow.getSourceNode(), date);
                if (date.before(lastDate)) {
                    date = lastDate;
                }
            }
        }
        return date;
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
                WorkKanbanInfoEntity workKanban = this.workKanbanMap.get(bpmnNode.getId());
                return workKanban.getStartDatetime();
            }
            return new Date(Long.MAX_VALUE);
        } catch (Exception ex) {
            return new Date(Long.MAX_VALUE);
        }
    }

    /**
     * 工程カンバンオーダーを取得する。
     *
     * @param bpmnNode
     * @return
     */
    private int getWorkKanbanOrder(BpmnNode bpmnNode) {
        try {
            if (bpmnNode instanceof BpmnParallelGateway) {
                List<BpmnSequenceFlow> flows = this.getNextFlows(bpmnNode.getId(), bpmnProcess.getSequenceFlowCollection());
                if (!flows.isEmpty()) {
                    Collections.sort(flows, new BpmnSequenceFlowComparator());
                    return this.getWorkKanbanOrder(flows.get(0).getTargetNode());
                }
            } else {
                WorkKanbanInfoEntity workKanban = this.workKanbanMap.get(bpmnNode.getId());
                return workKanban.getWorkKanbanOrder();
            }
            return Integer.MAX_VALUE;
        } catch (Exception ex) {
            return Integer.MAX_VALUE;
        }
    }
}
