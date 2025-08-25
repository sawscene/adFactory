/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringTime;
import adtekfuji.utility.Tuple;
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
import jakarta.xml.bind.JAXBException;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
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

    public static final long DAY_MILLIS = 86400000L;

    private final Logger logger = LogManager.getLogger();
    private final WorkflowInfoEntity workflow;
    private final BpmnModel bpmnModel;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    private final long defaultTime = DateUtils.min().getTime();
    private BpmnProcess bpmnProcess;
    private Map<String, WorkKanbanInfoEntity> workKanbanMap;
    private Map<Long, ConWorkflowWorkInfoEntity> workflowWorkMap;
    private List<BreakTimeInfoEntity> breakTimes;
    private List<HolidayInfoEntity> holidays;

    private static final String NODE_START_ID = "start_id";// 開始ノードID
    private static final String NODE_END_ID = "end_id";// 終了ノードID

    private static final long DAY_TIME = 1000 * 60 * 60 * 24;

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
     * カンバンに計画開始時間を設定する。
     *
     * @param kanban カンバン
     * @param breakTimes 休憩時間リスト
     * @param baseDatetime 計画開始時間
     * @param holidays 休日
     * @throws Exception
     */
    public void setBaseTime(KanbanInfoEntity kanban, List<BreakTimeInfoEntity> breakTimes, Date baseDatetime, List<HolidayInfoEntity> holidays) throws Exception {

        logger.info("setBaseTime start: " + baseDatetime);

        List<WorkKanbanInfoEntity> entities = kanban.getWorkKanbanCollection();

        this.workKanbanMap = new HashMap<>();
        for (WorkKanbanInfoEntity entity : entities) {
            this.workKanbanMap.put(String.valueOf(entity.getFkWorkId()), entity);
        }

        this.workflowWorkMap = new HashMap<>();
        for (ConWorkflowWorkInfoEntity entity : this.workflow.getConWorkflowWorkInfoCollection()) {
            this.workflowWorkMap.put(entity.getFkWorkId(), entity);
        }

        //for (ConWorkflowWorkInfoEntity workflowWork : this.workflow.getConWorkflowWorkInfoCollection()) {
        //    WorkKanbanInfoEntity workKanban = this.workKanbanMap.get(String.valueOf(workflowWork.getFkWorkId()));
        //    workKanban.setStartDatetime(workflowWork.getStandardStartTime());
        //    workKanban.setCompDatetime(workflowWork.getStandardEndTime());
        //}
        this.breakTimes = breakTimes;
        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

        // 作業時間外
        if (!this.isDefaultWorkTime()) {
            try {
                LocalTime startTime = this.closeTime;
                LocalTime endTime = this.openTime;

                LocalDate localDate = DateUtils.toLocalDate(DateUtils.min());
                Date startOverTime = DateUtils.toDate(localDate, startTime);
                Date endOverTime = (startTime.compareTo(endTime) > 0) ? DateUtils.toDate(localDate.plusDays(1), endTime) : DateUtils.toDate(localDate, endTime);

                this.breakTimes.add(new BreakTimeInfoEntity(0L, "", startOverTime, endOverTime));
            } catch (Exception ex) {
                logger.warn(ex, ex);
            }
        }

        // 休日
        this.holidays = holidays;

        // 追加の工程カンバンを更新
        Date endTime = baseDatetime;
        for (WorkKanbanInfoEntity workKanban : kanban.getSeparateworkKanbanCollection()) {
            final Date time = workKanban.getWorkStatus().isWorkKanbanUpdatableStatus
                    ? this.updateWorkKanban(workKanban, baseDatetime)
                    : workKanban.getCompDatetime();

            if (endTime.before(time)) {
                endTime = time;
            }
        }

        if (kanban.getProductionType() != 1) {
            // 基準時間を設定
            List<BpmnSequenceFlow> flows = this.getNextFlows(NODE_START_ID, this.bpmnProcess.getSequenceFlowCollection());
            Tuple<Boolean, Date> result = this.shiftTime(flows, null, baseDatetime, null);
            if (endTime.before(result.getRight())) {
                endTime = result.getRight();
            }

        } else {
            Date time = this.updateTimetable(kanban, baseDatetime, 1, 1);
            if (Objects.nonNull(time) && Objects.nonNull(endTime) && endTime.before(time)) {
                endTime = time;
            }
        }

        kanban.setStartDatetime(baseDatetime);
        kanban.setCompDatetime(endTime);

        logger.info("setBaseTime end: {}, {}", kanban.getStartDatetime(), kanban.getCompDatetime());
    }

    /**
     * カンバンに計画開始時間を設定する。
     *
     * @param kanban カンバン
     * @param breakTimes 休憩時間リスト
     * @param baseDatetime 計画開始時間
     * @param holidays 休日
     * @param workKanban 工程
     * @throws Exception
     */
    public void setBaseTime(KanbanInfoEntity kanban, List<BreakTimeInfoEntity> breakTimes, Date baseDatetime, List<HolidayInfoEntity> holidays, WorkKanbanInfoEntity workKanban) throws Exception {

        logger.info("setBaseTime start: " + baseDatetime);

        List<WorkKanbanInfoEntity> entities = kanban.getWorkKanbanCollection();

        this.workKanbanMap = new HashMap<>();
        for (WorkKanbanInfoEntity entity : entities) {
            this.workKanbanMap.put(String.valueOf(entity.getFkWorkId()), entity);
        }

        this.workflowWorkMap = new HashMap<>();
        for (ConWorkflowWorkInfoEntity entity : this.workflow.getConWorkflowWorkInfoCollection()) {
            this.workflowWorkMap.put(entity.getFkWorkId(), entity);
        }

        this.breakTimes = breakTimes;
        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

        // 作業時間外
        if (!this.isDefaultWorkTime()) {
            try {
                LocalTime startTime = this.closeTime;
                LocalTime endTime = this.openTime;

                LocalDate localDate = DateUtils.toLocalDate(DateUtils.min());
                Date startOverTime = DateUtils.toDate(localDate, startTime);
                Date endOverTime = (startTime.compareTo(endTime) > 0) ? DateUtils.toDate(localDate.plusDays(1), endTime) : DateUtils.toDate(localDate, endTime);

                this.breakTimes.add(new BreakTimeInfoEntity(0L, "", startOverTime, endOverTime));
            } catch (Exception ex) {
                logger.warn(ex, ex);
            }
        }

        // 休日
        this.holidays = holidays;

        Date endTime;

        if (kanban.getProductionType() != 1) {
            List<BpmnSequenceFlow> flows = this.getFlows(String.valueOf(workKanban.getFkWorkId()), this.bpmnProcess.getSequenceFlowCollection());
            if (flows.isEmpty()) {
                return;
            }

            Tuple<Boolean, Date> result;
            BpmnNode bpmnNode = flows.get(0).getSourceNode();
            if (bpmnNode instanceof BpmnParallelGateway) {
                // 並列工程の場合
                BpmnSequenceFlow flow = flows.get(0);
                flows = this.getFlows(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
                result = this.shiftTime(flows, null, baseDatetime, flow);
            } else {
                result = this.shiftTime(flows, null, baseDatetime, null);
            }

            endTime = result.getRight();

        } else {
            endTime = this.updateTimetable(kanban, baseDatetime, workKanban.getWorkKanbanOrder(), workKanban.getSerialNumber());
        }

        kanban.setStartDatetime(kanban.getWorkKanbanCollection().get(0).getStartDatetime());
        kanban.setCompDatetime(endTime);

        logger.info("setBaseTime end: {}, {}", kanban.getStartDatetime(), kanban.getCompDatetime());
    }

    /**
     * 作業時間をシフトする。
     *
     * @param flows
     * @param endNode
     * @param baseTime
     * @param target カンバン編集画面で並列工程の中から選択された工程
     * @return
     * @throws Exception
     */
    private Tuple<Boolean, Date> shiftTime(List<BpmnSequenceFlow> flows, BpmnNode endNode, Date baseTime, final BpmnSequenceFlow target) throws Exception {
        if (Objects.isNull(endNode)) {
            // 開始時間が就業後の場合、翌日の始業時刻にシフトする。
            baseTime = this.checkAfterWork(baseTime);
            // 開始日時が休日の場合、翌営業日の始業時刻にシフトする。
            baseTime = this.checkHoliday(baseTime, true);
        }

        Date startTime = baseTime;
        BpmnSequenceFlow flow = target;

        // 開始日時の始業時間
        long baseOpenTime = DateUtils.toDate(DateUtils.toLocalDate(baseTime), this.openTime).getTime();

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
                Date endTime;
                if(workKanban.getWorkStatus().isWorkKanbanUpdatableStatus) {


                    // 計画時間を初期化
                    ConWorkflowWorkInfoEntity workflowWork = this.workflowWorkMap.get(workKanban.getFkWorkId());


                    workKanban.setStartDatetime(workflowWork.getStandardStartTime());
                    workKanban.setCompDatetime(workflowWork.getStandardEndTime());

                    // 工程カンバンに割り当てられた組織の休憩と休日
                    List<Long> breaktimeIds = WorkKanbanTimeReplaceUtils.getWorkKanbanBreaktimeIds(workKanban);
                    List<BreakTimeInfoEntity> workBreaktimes;
                    if (breaktimeIds.isEmpty()) {
                        workBreaktimes = this.breakTimes.stream()
                                .filter(p -> p.getBreaktimeId().equals(0L))
                                .collect(Collectors.toList());
                    } else {
                        workBreaktimes = this.breakTimes.stream()
                                .filter(p -> p.getBreaktimeId().equals(0L) || breaktimeIds.contains(p.getBreaktimeId()))
                                .collect(Collectors.toList());
                    }

                    if (!this.isDefaultWorkTime()) {
                        long holidayTime = getHolidayTime(baseTime, startTime);
                        long diff = (workKanban.getStartDatetime().getTime() - this.defaultTime + holidayTime) / DAY_MILLIS * DAY_MILLIS;

                        long time;
                        if (diff < DAY_MILLIS) {
                            // 1日目の工程はカンバン開始日時から開始する。
                            time = startTime.getTime() + diff;
                        } else {
                            // 2日目以降の工程は始業時刻から開始する。
                            time = baseOpenTime + diff + ((workKanban.getStartDatetime().getHours() * 3600 + workKanban.getStartDatetime().getMinutes() * 60) * 1000L);
                        }

                        startTime = (startTime.getTime() < time) ? new Date(time) : startTime;
                    }

                    // 開始日時の休日チェック
                    startTime = checkHoliday(startTime, true);

                    logger.info("Work: {}, {}, {}", workKanban.getWorkName(), workKanban.getStartDatetime(), startTime);

                    // 計画完了時間に休憩時間を加算
                    long workTime = workKanban.getCompDatetime().getTime() - workKanban.getStartDatetime().getTime();
                    endTime = this.calcWithBreak(startTime, new Date(startTime.getTime() + workTime), -1, workBreaktimes);

                    // 完了日時の休日チェック
                    endTime = checkHoliday(endTime, false);

                    workKanban.setStartDatetime(startTime);
                    workKanban.setCompDatetime(endTime);
                } else {
                    endTime = workKanban.getCompDatetime();
                }

                if (!workKanban.getSkipFlag() && !(workKanban.getWorkStatus() == KanbanStatusEnum.COMPLETION || workKanban.getWorkStatus() == KanbanStatusEnum.SUSPEND)) {
                    startTime = endTime;
                }


                flows = this.getNextFlows(flows.get(0).getTargetRef(), this.bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTime(flows.get(0), startTime);

            } else if (bpmnNode instanceof BpmnParallelGateway) {
                // ゲートウェイ
                BpmnParallelGateway gateway = (BpmnParallelGateway) bpmnNode;
                List<BpmnSequenceFlow> nextFlows = this.getNextFlows(bpmnNode.getId(), bpmnProcess.getSequenceFlowCollection());

                if (SchedulePolicyEnum.PriorityParallel == workflow.getSchedulePolicy()) {
                    // 並列工程優先の場合
                    Date time = startTime;

                    for (BpmnSequenceFlow nextFlow : nextFlows) {
                        if (Objects.nonNull(flow) && flow != nextFlow) {
                            // 対象工程より前方の工程は除外
                            continue;
                        }

                        flow = null;

                        if (nextFlow.getTargetNode() instanceof BpmnTask) {
                            Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), time, flow);
                            if (!result.getLeft()) {
                                return new Tuple(false, startTime);
                            }

                            WorkKanbanInfoEntity workKanban = this.workKanbanMap.get(nextFlow.getTargetRef());
                            if (workKanban.getSkipFlag() || workKanban.getWorkStatus() == KanbanStatusEnum.COMPLETION || workKanban.getWorkStatus() == KanbanStatusEnum.SUSPEND) {
                                // 工程カンバンがスキップ or 完了 or 中止の場合
                                time = workKanban.getStartDatetime();
                            } else {
                                time = workKanban.getCompDatetime();
                            }

                            if (!this.isDefaultWorkTime()) {
                                // 開始時間が作業時間外となる場合、翌日にシフトする
                                if (0 >= this.closeTime.compareTo(DateUtils.toLocalTime(time))) {
                                    LocalDate nextDay = DateUtils.toLocalDate(time).plusDays(1);
                                    time = DateUtils.toDate(nextDay, openTime);
                                }
                            }
                        } else {
                            Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime, flow);
                            if (!result.getLeft()) {
                                return new Tuple(false, startTime);
                            }
                        }
                    }

                } else {
                    // 直列工程優先の場合

                    for (BpmnSequenceFlow nextFlow : nextFlows) {
                        if (Objects.nonNull(flow) && flow != nextFlow) {
                            // 対象工程より前方の工程は除外
                            continue;
                        }

                        flow = null;

                        Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime, flow);
                        if (!result.getLeft()) {
                            return new Tuple(false, startTime);
                        }
                    }
                }

                flows = this.getNextFlows(gateway.getPairedId(), bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTime(flows.get(0), startTime);
            }

            // 次ノードが終了ノードの場合、startTimeがカンバン完了日時となるため、翌日シフトのチェックなし
            if (!this.isDefaultWorkTime() && !NODE_END_ID.equals(flows.get(0).getTargetRef())) {
                // 開始時間が作業時間外となる場合、翌日にシフトする
                if (0 >= this.closeTime.compareTo(DateUtils.toLocalTime(startTime))) {
                    LocalDate nextDay = DateUtils.toLocalDate(startTime).plusDays(1);
                    startTime = DateUtils.toDate(nextDay, openTime);
                }
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
    private Date calcWithBreak(Date startDatetime, Date endDatetime, int days, List<BreakTimeInfoEntity> workBreaktimes) {

        Date result = endDatetime;

        long startTime = startDatetime.getTime();
        long endTime = endDatetime.getTime();
        Date date = null;
        long breakTime = 0;

        List<BreakTimeInfoEntity> entities = BreaktimeUtil.getAppropriateBreaktimes(workBreaktimes, startDatetime, endDatetime, days);
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
            result = this.calcWithBreak(date, new Date(endTime + breakTime), 0, workBreaktimes);
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
    private Date calcWithoutBreak(Date startDatetime, Date endDatetime, List<BreakTimeInfoEntity> workBreaktimes) {
        Date date = endDatetime;

        long startWorkTime = startDatetime.getTime();
        long endWorkTime = endDatetime.getTime();

        List<BreakTimeInfoEntity> entities = BreaktimeUtil.getAppropriateBreaktimes(workBreaktimes, startDatetime, endDatetime);
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
            if (workKanban.getSkipFlag() || workKanban.getWorkStatus() == KanbanStatusEnum.COMPLETION || workKanban.getWorkStatus() == KanbanStatusEnum.SUSPEND) {
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
        if (Objects.isNull(this.closeTime) || Objects.isNull(this.openTime)) {
            return true;
        }
        return this.openTime.equals(LocalTime.of(0, 0)) && this.closeTime.equals(LocalTime.of(0, 0));
    }

    /**
     * 工程カンバンの計画をオフセット時間分ずらす。
     *
     * @param workKanbans 工程カンバン一覧
     * @param isStartTimeOffset 基本開始時間変更か？ (true:基本開始時間変更, false:オフセット)
     * @param offsetTime オフセット時間
     * @param breakTimes 休憩時間一覧
     * @param holidays 休日一覧
     * @throws Exception
     */
    public void offsetWorkKanbans(List<WorkKanbanInfoEntity> workKanbans, boolean isStartTimeOffset, String offsetTime, List<BreakTimeInfoEntity> breakTimes, List<HolidayInfoEntity> holidays) throws Exception {
        Date baseDatetime = workKanbans.get(0).getStartDatetime();

        this.breakTimes = breakTimes;

        // 作業時間外
        if (!this.isDefaultWorkTime()) {
            try {
                LocalTime startOffTime = this.closeTime;
                LocalTime endOffTime = this.openTime;

                LocalDate localDate = DateUtils.toLocalDate(baseDatetime);
                Date startOverTime = DateUtils.toDate(localDate, startOffTime);
                Date endOverTime = (startOffTime.compareTo(endOffTime) > 0) ? DateUtils.toDate(localDate.plusDays(1), endOffTime) : DateUtils.toDate(localDate, endOffTime);

                this.breakTimes.add(new BreakTimeInfoEntity(0L, "", startOverTime, endOverTime));
            } catch (Exception ex) {
                logger.warn(ex, ex);
            }
        }

        // 休日
        this.holidays = holidays;

        for (WorkKanbanInfoEntity workKanban : workKanbans) {
            Date startTime = StringTime.getFixedDate(workKanban.getStartDatetime(), offsetTime);
            long lastTime = startTime.getTime();

            // 工程カンバンに割り当てられた組織の休憩と休日
            List<Long> breaktimeIds = WorkKanbanTimeReplaceUtils.getWorkKanbanBreaktimeIds(workKanban);
            List<BreakTimeInfoEntity> workBreaktimes;
            if (breaktimeIds.isEmpty()) {
                workBreaktimes = breakTimes.stream()
                        .filter(p -> p.getBreaktimeId().equals(0L))
                        .collect(Collectors.toList());
            } else {
                workBreaktimes = breakTimes.stream()
                        .filter(p -> p.getBreaktimeId().equals(0L) || breaktimeIds.contains(p.getBreaktimeId()))
                        .collect(Collectors.toList());
            }

            workKanban.setStartDatetime(startTime);

            if (!this.isDefaultWorkTime()) {
                long diff = (workKanban.getStartDatetime().getTime() - lastTime) / DAY_MILLIS * DAY_MILLIS;
                long time = (diff < DAY_MILLIS) ? startTime.getTime() + diff : baseDatetime.getTime() + diff + ((workKanban.getStartDatetime().getHours() * 3600 + workKanban.getStartDatetime().getMinutes() * 60) * 1000L);
                startTime = (startTime.getTime() < time) ? new Date(time) : startTime;
            }

            // 開始日時の休日チェック
            startTime = checkHoliday(startTime, true);

            // 計画完了時間に休憩時間を加算
            Date endTime = this.calcWithBreak(startTime, new Date(startTime.getTime() + workKanban.getTaktTime()), -1, workBreaktimes);

            // 完了日時の休日チェック
            endTime = checkHoliday(endTime, false);

            workKanban.setStartDatetime(startTime);
            workKanban.setCompDatetime(endTime);
        }
    }

    /**
     * カンバンの計画開始日時を更新する。
     *
     * @param kanban カンバン
     * @param baseDatetime
     * @param order
     * @param serialNumber
     * @return
     */
    private Date updateTimetable(KanbanInfoEntity kanban, Date baseDatetime, int order, int serialNumber) {
        logger.info("updateTimetable start.");

        Date nextTime = baseDatetime;
        Date endTime = null;
        long lastTime = defaultTime;

        try {
            for (ConWorkflowWorkInfoEntity workflowWork : this.workflow.getConWorkflowWorkInfoCollection()) {
                Date startTime = nextTime;

                // タクトタイム
                long taktTime = workflowWork.getStandardEndTime().getTime() - workflowWork.getStandardStartTime().getTime();

                List<WorkKanbanInfoEntity> workKanbans = kanban.getWorkKanbanCollection().stream().filter(p -> p.getFkWorkId().equals(workflowWork.getFkWorkId())).collect(Collectors.toList());
                Collections.sort(workKanbans, ((a, b) -> a.getSerialNumber().compareTo(b.getSerialNumber())));

                // 工程カンバンに割り当てられた組織の休憩と休日
                List<Long> breaktimeIds = WorkKanbanTimeReplaceUtils.getWorkKanbanBreaktimeIds(workKanbans.get(0));
                List<BreakTimeInfoEntity> workBreaktimes;
                if (breaktimeIds.isEmpty()) {
                    workBreaktimes = this.breakTimes.stream()
                            .filter(p -> p.getBreaktimeId().equals(0L))
                            .collect(Collectors.toList());
                } else {
                    workBreaktimes = this.breakTimes.stream()
                            .filter(p -> p.getBreaktimeId().equals(0L) || breaktimeIds.contains(p.getBreaktimeId()))
                            .collect(Collectors.toList());
                }

                for (WorkKanbanInfoEntity workKanban : workKanbans) {

                    if (Objects.isNull(workKanban.getWorkKanbanOrder())
                            || workKanban.getWorkKanbanOrder() < order
                            || Objects.isNull(workKanban.getSerialNumber())
                            || workKanban.getSerialNumber() < serialNumber) {
                        continue;
                    }

                    workKanban.setStartDatetime(workflowWork.getStandardStartTime());
                    workKanban.setCompDatetime(workflowWork.getStandardEndTime());

                    if (!this.isDefaultWorkTime()) {
                        // 開始日時に作業日を反映
                        long diff = (workKanban.getStartDatetime().getTime() - lastTime) / DAY_MILLIS * DAY_MILLIS;
                        long time = (diff < DAY_MILLIS) ? startTime.getTime() + diff : baseDatetime.getTime() + diff + ((workKanban.getStartDatetime().getHours() * 3600 + workKanban.getStartDatetime().getMinutes() * 60) * 1000L);
                        startTime = (startTime.getTime() < time) ? new Date(time) : startTime;
                        lastTime = lastTime + diff;
                    }

                    // 開始日時の休日チェック
                    startTime = this.checkHoliday(startTime, true);

                    // 完了時間を算出
                    endTime = this.calcWithBreak(startTime, new Date(startTime.getTime() + taktTime), -1, workBreaktimes);

                    // 完了日時の休日チェック
                    endTime = checkHoliday(endTime, false);

                    workKanban.setStartDatetime(startTime);
                    workKanban.setCompDatetime(endTime);

                    // 次号機の開始日時
                    if (!workKanban.getSkipFlag() && !(workKanban.getWorkStatus() == KanbanStatusEnum.COMPLETION || workKanban.getWorkStatus() == KanbanStatusEnum.SUSPEND)) {
                        startTime = endTime;
                    }

                    if (!this.isDefaultWorkTime()) {
                        // 開始時間が作業時間外となる場合、翌日にシフトする
                        if (0 >= this.closeTime.compareTo(DateUtils.toLocalTime(startTime))) {
                            LocalDate nextDay = DateUtils.toLocalDate(startTime).plusDays(1);
                            startTime = DateUtils.toDate(nextDay, openTime);
                        }
                    }

                    // 次工程の開始日時
                    if (workKanban.getSerialNumber() == serialNumber) {
                        nextTime = startTime;
                    }
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("updateTimetable end.");
        }

        return endTime;
    }

    /**
     * 工程カンバンに計画開始日時を更新する。
     *
     * @param workKanban
     */
    private Date updateWorkKanban(WorkKanbanInfoEntity workKanban, Date baseDatetime) {
        // タクトタイム
        long taktTime = workKanban.getCompDatetime().getTime() - workKanban.getStartDatetime().getTime();

        // 工程カンバンに割り当てられた組織の休憩と休日
        List<Long> breaktimeIds = WorkKanbanTimeReplaceUtils.getWorkKanbanBreaktimeIds(workKanban);
        List<BreakTimeInfoEntity> workBreaktimes;
        if (breaktimeIds.isEmpty()) {
            workBreaktimes = this.breakTimes.stream()
                    .filter(p -> p.getBreaktimeId().equals(0L))
                    .collect(Collectors.toList());
        } else {
            workBreaktimes = this.breakTimes.stream()
                    .filter(p -> p.getBreaktimeId().equals(0L) || breaktimeIds.contains(p.getBreaktimeId()))
                    .collect(Collectors.toList());
        }

        // 開始日時の休日チェック
        Date startTime = this.checkHoliday(baseDatetime, true);

        // 完了時間を算出
        Date endTime = this.calcWithBreak(startTime, new Date(startTime.getTime() + taktTime), -1, workBreaktimes);

        // 完了日時の休日チェック
        endTime = this.checkHoliday(endTime, false);

        workKanban.setStartDatetime(startTime);
        workKanban.setCompDatetime(endTime);

        return endTime;
    }

    /**
     * 指定した日時範囲の休日数をチェックして、休日数分のtime値(ms)を返す。
     *
     * @param startDate 日付範囲の先頭
     * @param endDate 日付範囲の末尾
     * @return 休日数分のtime値(ms)
     */
    private long getHolidayTime(Date startDate, Date endDate) {
        long holidayTime = 0;
        for (long time = startDate.getTime(); time <= endDate.getTime(); time += DAY_TIME) {
            holidayTime += getHolidayTime(new Date(time));
        }

        return holidayTime;
    }

    /**
     * 指定日時が休日かどうかチェックして、休日の場合は1日分のtime値(ms)を返す。
     *
     * @param date 日時
     * @return (平日：0, 休日：1日のtime値(ms))
     */
    private long getHolidayTime(Date date) {
        // 休日が未登録の場合は「0」を返す。
        if (Objects.isNull(this.holidays) || this.holidays.isEmpty()) {
            return 0;
        }

        // 指定日の0時に変換する。
        Date dateStart = DateUtils.getBeginningOfDate(date);

        // 休日を日付順にソートしてチェックする。
        for (HolidayInfoEntity holiday : this.holidays.stream().sorted(Comparator.comparing(p -> p.getHolidayDate())).collect(Collectors.toList())) {
            Date holidayStart = DateUtils.getBeginningOfDate(holiday.getHolidayDate());
            if (holidayStart.equals(dateStart)) {
                // 休日
                return DAY_TIME;
            } else if (holidayStart.after(dateStart)) {
                // 休日が対象日時より後の場合、以降の休日はチェック不要。
                break;
            }
        }

        return 0;
    }

    /**
     * 指定日時の時刻が終業後かチェックして、終業後の場合は翌日の始業時刻を、そうでない場合は指定日時をそのまま返す。
     *
     * @param date 日時
     * @return 日時が終業後:翌日の始業時刻, その他:指定日時
     */
    private Date checkAfterWork(Date date) {
        Date result = date;

        // 開始時間が終業後の場合、翌日の始業時刻にシフトする。
        LocalTime localBaseTime = DateUtils.toLocalTime(date);
        if (Objects.nonNull(this.closeTime)
                && !this.closeTime.equals(this.openTime)
                && (this.closeTime.equals(localBaseTime) || this.closeTime.isBefore(localBaseTime))) {
            LocalDate nextDay = DateUtils.toLocalDate(date).plusDays(1);
            result = DateUtils.toDate(nextDay, this.openTime);
        }

        return result;
    }
}
