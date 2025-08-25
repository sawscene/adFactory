/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin.util;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.Tuple;
import jakarta.xml.bind.JAXBException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;
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
public class WorkPlanWorkflowProcess {

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

    private static final long DAY_MILLIS = 86400000L;

    private final Logger logger = LogManager.getLogger();
    private final WorkflowInfoEntity workflow;
    private final BpmnModel bpmnModel;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    private long defaultTime;
    private BpmnProcess bpmnProcess;
    private Map<String, WorkKanbanInfoEntity> workKanbanMap;
    private List<BreakTimeInfoEntity> breakTimes;
    private List<HolidayInfoEntity> holidays;

    /**
     * コンストラクタ
     *
     * @param workflow
     */
    public WorkPlanWorkflowProcess(WorkflowInfoEntity workflow) {
        this.workflow = workflow;
        this.bpmnModel = BpmnModeler.getModeler();

        if (Objects.nonNull(this.workflow.getOpenTime()) && Objects.nonNull(this.workflow.getCloseTime())) {
            this.openTime = DateUtils.toLocalTime(this.workflow.getOpenTime());
            this.closeTime = DateUtils.toLocalTime(this.workflow.getCloseTime());
        } else {
            this.openTime = null;
            this.closeTime = null;
        }

        try {
            BpmnDocument bpmn = BpmnDocument.unmarshal(workflow.getWorkflowDiaglam());
            this.bpmnModel.createModel(bpmn);
        } catch (JAXBException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンに基準時間を設定する。
     *
     * @param kanban カンバン
     * @param breakTimes 休憩時間リスト
     * @param baseDatetime 基準時間
     * @param holidays 休日
     * @throws Exception
     */
    public void setBaseTime(KanbanInfoEntity kanban, List<BreakTimeInfoEntity> breakTimes, Date baseDatetime, List<HolidayInfoEntity> holidays) throws Exception {

        this.defaultTime = DateUtils.min().getTime();
        logger.info("setBaseTime start: " + new Date(this.defaultTime));

        List<WorkKanbanInfoEntity> entities = kanban.getWorkKanbanCollection();

        this.workKanbanMap = new HashMap<>();
        for (WorkKanbanInfoEntity entity : entities) {
            this.workKanbanMap.put(String.valueOf(entity.getFkWorkId()), entity);
        }

        for (ConWorkflowWorkInfoEntity workflowWork : this.workflow.getConWorkflowWorkInfoCollection()) {
            WorkKanbanInfoEntity workKanban = this.workKanbanMap.get(String.valueOf(workflowWork.getFkWorkId()));
            workKanban.setStartDatetime(workflowWork.getStandardStartTime());
            workKanban.setCompDatetime(workflowWork.getStandardEndTime());
        }

        this.breakTimes = breakTimes;
        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

        // 作業時間外
        if (Objects.nonNull(this.closeTime) && Objects.nonNull(this.openTime) && !isDefaultWorkTime()) {
            try {
                LocalTime startTime = this.closeTime;
                LocalTime endTime = this.openTime;

                LocalDate localDate = DateUtils.toLocalDate(baseDatetime);
                Date startOverTime = DateUtils.toDate(localDate, startTime);
                Date endOverTime = (startTime.compareTo(endTime) > 0) ? DateUtils.toDate(localDate.plusDays(1), endTime) : DateUtils.toDate(localDate, endTime);

                this.breakTimes.add(new BreakTimeInfoEntity(0L, "", startOverTime, endOverTime));
            } catch (Exception ex) {
                logger.warn(ex, ex);
            }
        }

        // 休日
        this.holidays = holidays;

        // 基準時間を設定
        List<BpmnSequenceFlow> flows = this.getNextFlows("start_id", this.bpmnProcess.getSequenceFlowCollection());
        Tuple<Boolean, Date> result = this.shiftTime(flows, null, baseDatetime, true);

        kanban.setStartDatetime(baseDatetime);
        kanban.setCompDatetime(result.getRight());

        logger.info("setBaseTime end: {}, {}", kanban.getStartDatetime(), kanban.getCompDatetime());
    }

    /**
     * 作業時間をシフトする。
     *
     * @param flows
     * @param endNode
     * @param baseTime
     * @param isBreak 休憩時間の扱い方 (true: 休憩時間を加算する false: 休憩時間を減算する)
     * @return
     * @throws Exception
     */
    private Tuple<Boolean, Date> shiftTime(List<BpmnSequenceFlow> flows, BpmnNode endNode, Date baseTime, boolean isBreak) throws Exception {

        Date startTime = baseTime;
        long lastTime = defaultTime;

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
                    long diff = (workKanban.getStartDatetime().getTime() - lastTime) / DAY_MILLIS * DAY_MILLIS;
                    long time = (diff < DAY_MILLIS) ? startTime.getTime() + diff : baseTime.getTime() + diff + ((workKanban.getStartDatetime().getHours() * 3600 + workKanban.getStartDatetime().getMinutes() * 60) * 1000L);
                    startTime = (startTime.getTime() < time) ? new Date(time) : startTime;

                    // 開始日時の休日チェック
                    startTime = checkHoliday(startTime, true);

                    lastTime = lastTime + diff;

                    logger.info("Work: {}, {}, {}", workKanban.getWorkName(), workKanban.getStartDatetime(), startTime);

                    // 計画完了時間に休憩時間を加算
                    Date endTime = this.calcWithBreak(startTime, new Date(startTime.getTime() + workKanban.getTaktTime()), -1);

                    // 完了日時の休日チェック
                    endTime = checkHoliday(endTime, false);

                    workKanban.setStartDatetime(startTime);
                    workKanban.setCompDatetime(endTime);

                    if (!workKanban.getSkipFlag()) {
                        startTime = endTime;
                    }

                    if (Objects.nonNull(this.openTime) && Objects.nonNull(this.closeTime) && !isDefaultWorkTime()) {
                        // 開始時間が作業時間外となる場合、翌日にシフトする
                        if (0 >= this.closeTime.compareTo(DateUtils.toLocalTime(startTime))) {
                            LocalDate nextDay = DateUtils.toLocalDate(startTime).plusDays(1);
                            startTime = DateUtils.toDate(nextDay, openTime);
                        }
                    }

                } else {
                    Date time = this.calcWithoutBreak(workKanban.getStartDatetime(), workKanban.getCompDatetime());
                    Date endTime = new Date(startTime.getTime() + time.getTime() - workKanban.getStartDatetime().getTime());

                    workKanban.setStartDatetime(startTime);
                    workKanban.setCompDatetime(endTime);

                    startTime = endTime;
                }

                flows = this.getNextFlows(flows.get(0).getTargetRef(), this.bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTime(flows.get(0), startTime);

            } else if (bpmnNode instanceof BpmnParallelGateway) {
                // ゲートウェイ
                BpmnParallelGateway gateway = (BpmnParallelGateway) bpmnNode;
                List<BpmnSequenceFlow> nextFlows = this.getNextFlows(bpmnNode.getId(), bpmnProcess.getSequenceFlowCollection());

                if (SchedulePolicyEnum.PriorityParallel == workflow.getSchedulePolicy()) {
                    Date time = startTime;

                    for (BpmnSequenceFlow nextFlow : nextFlows) {
                        if (nextFlow.getTargetNode() instanceof BpmnTask) {
                            Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), time, isBreak);
                            if (!result.getLeft()) {
                                return new Tuple(false, startTime);
                            }

                            WorkKanbanInfoEntity workKanban = this.workKanbanMap.get(nextFlow.getTargetRef());
                            time = workKanban.getSkipFlag() ? workKanban.getStartDatetime() : workKanban.getCompDatetime();
                        } else {
                            Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime, isBreak);
                            if (!result.getLeft()) {
                                return new Tuple(false, startTime);
                            }
                        }
                    }

                } else {
                    for (BpmnSequenceFlow nextFlow : nextFlows) {
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
     * 対象日時が休日かチェックして、休日の場合は翌営業日を返し、違う場合は対象日時をそのまま返す。
     *
     * @param targetDate 対象日時
     * @param isStartDate 開始日時？
     * @return
     */
    private Date checkHoliday(Date targetDate, boolean isStartDate) {
        Date date = targetDate;
        if (Objects.nonNull(this.holidays) && !this.holidays.isEmpty()) {
            for (HolidayInfoEntity holiday : this.holidays.stream().sorted(Comparator.comparing(p -> p.getHolidayDate())).collect(Collectors.toList())) {
                Date holidayStart = DateUtils.getBeginningOfDate(holiday.getHolidayDate());
                Date holidayEnd = DateUtils.getEndOfDate(holiday.getHolidayDate());
                if ((holidayStart.before(date) || holidayStart.equals(date))
                        && (holidayEnd.after(date) || holidayEnd.equals(date))) {
                    // 休日の場合は翌日にする。
                    LocalDateTime nextDay = DateUtils.toLocalDateTime(date).plusDays(1);
                    if (isStartDate) {
                        // 開始日時の場合は時刻も変更する。
                        date = DateUtils.toDate(nextDay.toLocalDate(), openTime);
                    } else {
                        date = DateUtils.toDate(nextDay);
                    }
                } else if (holidayStart.after(date)) {
                    // 休日が対象日時より後の場合、以降の休日はチェック不要。
                    break;
                }
            }
        }
        return date;
    }

    /**
     * 休憩を含めた終了時間を計算する。 参考資料: スキップ検討.vsd
     *
     * @param startDatetime
     * @param endDatetime
     * @param days
     * @return
     */
    private Date calcWithBreak(Date startDatetime, Date endDatetime, int days) {

        Date result = endDatetime;

        long startTime = startDatetime.getTime();
        long endTime = endDatetime.getTime();
        Date date = null;
        long breakTime = 0;

        List<BreakTimeInfoEntity> entities = BreaktimeUtil.getAppropriateBreaktimes(this.breakTimes, startDatetime, endDatetime, days);
        for (BreakTimeInfoEntity entity : entities) {
            long startBreakTime = entity.getStarttime().getTime();
            long endBreakTime = entity.getEndtime().getTime();

            if ((startTime > startBreakTime && startTime < endBreakTime) && (endTime < endBreakTime && endTime > startBreakTime)) {
                // ④ (工程開始 > 休憩開始 and 工程開始 < 休憩終了) and (工程終了 < 休憩終了 and 工程終了 > 休憩開始)
                // 休憩時間を一部加算する
                breakTime += endBreakTime - startTime;
                date = entity.getEndtime();
            } else if (startTime <= startBreakTime && endTime >= endBreakTime) {
                // ① 工程開始 <= 休憩開始 and 工程終了 >= 休憩終了
                // 休憩時間を全部加算する
                breakTime += endBreakTime - startBreakTime;
                date = entity.getEndtime();
            } else if (endTime < endBreakTime && endTime > startBreakTime) {
                // ② 工程終了 < 休憩終了 and 工程終了 > 休憩開始
                // 休憩時間を全部加算する
                breakTime += endBreakTime - startBreakTime;
                date = entity.getEndtime();
            } else if (startTime > startBreakTime && startTime < endBreakTime) {
                // ③ 工程開始 > 休憩開始 and 工程開始 < 休憩終了
                // 休憩時間を一部加算する
                breakTime += endBreakTime - startTime;
                date = entity.getEndtime();
            }
        }

        if (0 != breakTime) {
            result = this.calcWithBreak(date, new Date(endTime + breakTime), 0);
        }

        return result;
    }

    /**
     * 休憩を除いた終了時間を計算する。 参考資料: スキップ検討.vsd
     *
     * @param startDatetime
     * @param endDatetime
     * @return
     */
    private Date calcWithoutBreak(Date startDatetime, Date endDatetime) {
        Date date = endDatetime;

        long startWorkTime = startDatetime.getTime();
        long endWorkTime = endDatetime.getTime();

//        List<BreakTimeInfoEntity> entities = BreaktimeUtil.getAppropriateBreaktimes(this.breakTimes, startDatetime, endDatetime, -1);
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

    /**
     * 作業時間枠がデフォルトの[00:00-00:00]であるか調べる
     *
     * @return デフォルトの場合true
     */
    private boolean isDefaultWorkTime() {
        return this.openTime.equals(LocalTime.of(0, 0)) && this.closeTime.equals(LocalTime.of(0, 0));
    }
}
