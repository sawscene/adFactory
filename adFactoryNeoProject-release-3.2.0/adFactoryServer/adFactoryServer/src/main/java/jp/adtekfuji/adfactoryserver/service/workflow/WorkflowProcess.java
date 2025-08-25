/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service.workflow;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.Tuple;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.xml.bind.JAXBException;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.adfactoryserver.entity.holiday.HolidayEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.service.OrganizationEntityFacadeREST;
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

    private OrganizationEntityFacadeREST organizationRest;

    /**
     *
     * @param organizationRest
     */
    public void setOrganizationRest(OrganizationEntityFacadeREST organizationRest) {
        this.organizationRest = organizationRest;
    }

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

    /**
     * BpmnSequenceFlowを時間順にソートするコンパレータ
     */
    class BpmnSequenceFlowComparatorForWorkflow implements Comparator<BpmnSequenceFlow> {

        /**
         * BpmnSequenceFlowを比較する。
         *
         * @param flow1
         * @param flow2
         * @return
         */
        @Override
        public int compare(BpmnSequenceFlow flow1, BpmnSequenceFlow flow2) {
            Date startTime1 = getStartTimeForWorkflow(flow1.getTargetNode());
            Date startTime2 = getStartTimeForWorkflow(flow2.getTargetNode());
            int compare = startTime1.compareTo(startTime2);
            if (0 == compare) {
                int order1 = getWorkflowOrder(flow1.getTargetNode());
                int order2 = getWorkflowOrder(flow2.getTargetNode());
                return order1 <= order2 ? -1 : 1;
            }
            return compare;
        }
    }

    private static final long DAY_MILLIS = 86400000L;

    private final Logger logger = LogManager.getLogger();
    private final WorkflowEntity workflow;
    private final BpmnModel bpmnModel;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    private final long defaultTime = DateUtils.min().getTime();
    private BpmnProcess bpmnProcess;
    private Map<String, WorkKanbanEntity> workKanbanMap;
    private Map<String, ConWorkflowWorkEntity> workflowWorkMap;
    private List<BreakTimeInfoEntity> breakTimes;
    private List<HolidayEntity> holidays;

    private static final String NODE_START_ID = "start_id";// 開始ノードID
    private static final String NODE_END_ID = "end_id";// 終了ノードID

    private static final long DAY_TIME = 1000 * 60 * 60 * 24;

    /**
     * コンストラクタ
     *
     * @param workflow
     */
    public WorkflowProcess(WorkflowEntity workflow) {
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
     * カンバンに計画開始日時を設定する。
     *
     * @param kanban カンバン
     * @param breakTimes 休憩時間リスト
     * @param baseDatetime 計画開始時間
     * @param holidays 休日
     * @throws Exception
     */
    public void setBaseTime(KanbanEntity kanban, List<BreakTimeInfoEntity> breakTimes, Date baseDatetime, List<HolidayEntity> holidays) throws Exception {
        logger.info("setBaseTime: kanbanId={}, baseDatetime={}", kanban.getKanbanId(), baseDatetime);

        List<WorkKanbanEntity> entities = kanban.getWorkKanbanCollection();

        this.workKanbanMap = new HashMap<>();
        for (WorkKanbanEntity entity : entities) {
            this.workKanbanMap.put(String.valueOf(entity.getWorkId()), entity);
        }

        this.breakTimes = breakTimes;
        this.bpmnProcess = this.bpmnModel.getBpmnDefinitions().getProcess();

        // 作業時間外
        if (!this.isDefaultWorkTime()) {
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

        // 追加の工程カンバンを更新
        Date endTime = baseDatetime;
        for (WorkKanbanEntity workKanban : kanban.getSeparateworkKanbanCollection()) {
            Date time = this.updateWorkKanban(workKanban, baseDatetime);
            if (endTime.before(time)) {
                endTime = time;
            }
        }

        if (kanban.getProductionType() != 1) {
            for (ConWorkflowWorkEntity workflowWork : this.workflow.getConWorkflowWorkCollection()) {
                WorkKanbanEntity workKanban = this.workKanbanMap.get(String.valueOf(workflowWork.getWorkId()));
                workKanban.setStartDatetime(workflowWork.getStandardStartTime());
                workKanban.setCompDatetime(workflowWork.getStandardEndTime());
            }

            // 基準時間を設定
            List<BpmnSequenceFlow> flows = this.getNextFlows(NODE_START_ID, this.bpmnProcess.getSequenceFlowCollection());
            Tuple<Boolean, Date> result = this.shiftTime(flows, null, baseDatetime, Objects.nonNull(kanban.getLotQuantity()) ? kanban.getLotQuantity() : 1);
            if (endTime.before(result.getRight())) {
                endTime = result.getRight();
            }

        } else {
            // ロット1個流し生産
            Date time = this.updatePlan(kanban, baseDatetime);
            if (endTime.before(time)) {
                endTime = time;
            }
        }

        kanban.setStartDatetime(baseDatetime);
        kanban.setCompDatetime(endTime);

        logger.info("setBaseTime end: {}, {}", kanban.getStartDatetime(), kanban.getCompDatetime());
    }

    /**
     * ロット1個流しカンバンの計画時間を更新する。(工程単位で作業する場合)
     * 
     * 作業順: 工程A #1 → 工程A #2 → 工程B #1 → 工程B #2 → 工程C #1 → 工程C #2
     *
     * @param kanban カンバン
     * @param baseDatetime
     * @return カンバンの計画完了時間
     */
    private Date updatePlan(KanbanEntity kanban, Date baseDatetime) {
        logger.info("updatePlan2 start.");

        List<Date> nextTimes = new ArrayList<>();
        Date endTime = baseDatetime;

        try {
            for (ConWorkflowWorkEntity workflowWork : this.workflow.getConWorkflowWorkCollection()) {
                // タクトタイム
                long taktTime = workflowWork.getStandardEndTime().getTime() - workflowWork.getStandardStartTime().getTime();

                List<WorkKanbanEntity> workKanbans = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkId().equals(workflowWork.getWorkId())).collect(Collectors.toList());
                Collections.sort(workKanbans, ((a, b) -> a.getSerialNumber().compareTo(b.getSerialNumber())));

                // 工程カンバンに割り当てられた組織の休憩と休日
                List<Long> breaktimeIds = this.getWorkKanbanBreaktimeIds(workKanbans.get(0));
                List<BreakTimeInfoEntity> workBreaktimes;
                if (breaktimeIds.isEmpty()) {
                    workBreaktimes = this.breakTimes.stream().filter(p -> p.getBreaktimeId().equals(0L)).collect(Collectors.toList());
                } else {
                    workBreaktimes = this.breakTimes.stream().filter(p -> p.getBreaktimeId().equals(0L) || breaktimeIds.contains(p.getBreaktimeId())).collect(Collectors.toList());
                }

                for (WorkKanbanEntity workKanban : workKanbans) {

                    Date startTime;

                    if (nextTimes.size() >= workKanban.getSerialNumber()){
                        startTime = nextTimes.get(workKanban.getSerialNumber() - 1);
                        if (endTime.after(startTime)) {
                            startTime = endTime;
                        }
                        
                    } else {
                        startTime = endTime;
                        nextTimes.add(baseDatetime);
                    }
                    
                    workKanban.setStartDatetime(workflowWork.getStandardStartTime());
                    workKanban.setCompDatetime(workflowWork.getStandardEndTime());

                    if (!this.isDefaultWorkTime()) {
                        // 開始時間が作業時間外となる場合、翌日にシフトする
                        if (0 >= this.closeTime.compareTo(DateUtils.toLocalTime(startTime))) {
                            LocalDate nextDay = DateUtils.toLocalDate(startTime).plusDays(1);
                            startTime = DateUtils.toDate(nextDay, this.openTime);
                        }

                        // 開始日時に作業日を反映
                        //long diff = (workKanban.getStartDatetime().getTime() - dailyTime) / DAY_MILLIS * DAY_MILLIS;
                        //long time = (diff < DAY_MILLIS) ? startTime.getTime() + diff : baseDatetime.getTime() + diff + ((workKanban.getStartDatetime().getHours() * 3600 + workKanban.getStartDatetime().getMinutes() * 60) * 1000L);
                        //startTime = (startTime.getTime() < time) ? new Date(time) : startTime;
                        //dailyTime = dailyTime + diff;
                    }

                    // 開始日時の休日チェック
                    startTime = this.checkHoliday(startTime, true);

                    if (!workKanban.getSkipFlag()) {
                        // 完了時間を算出
                        endTime = this.calcWithBreak(startTime, new Date(startTime.getTime() + taktTime), -1, workBreaktimes);
                        // 完了日時の休日チェック
                        endTime = this.checkHoliday(endTime, false);
                    } else {
                        endTime = startTime;
                    }

                    workKanban.setStartDatetime(startTime);
                    workKanban.setCompDatetime(endTime);
                
                    nextTimes.set(workKanban.getSerialNumber() - 1, endTime);
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("updatePlanForFastest end.");
        }

        return nextTimes.stream().max(Date::compareTo).get();
    }

    /**
     * ロット1個流しカンバンの計画時間を更新する。(シリアル番号単位で作業する場合)
     * 
     * 作業順: 工程A #1 → 工程B #1 → 工程C #1 → 工程A #2 → 工程B #2 → 工程C #2
     *
     * @param kanban カンバン
     * @param baseDatetime
     * @return カンバンの計画完了時間
     */
    private Date updatePlan2(KanbanEntity kanban, Date baseDatetime) {
        logger.info("updatePlan start.");

        Date endTime = baseDatetime;
        Map<Long, ConWorkflowWorkEntity> map = this.workflow.getConWorkflowWorkCollection().stream().collect(Collectors.toMap(o -> o.getWorkId(), o -> o));
        
        try {
            for (int i = 1 ;; i++) {

                final int serialNumber = i;
                List<WorkKanbanEntity> workKanbans = kanban.getWorkKanbanCollection().stream()
                        .filter(p -> p.getSerialNumber() == serialNumber).collect(Collectors.toList());
                
                if (workKanbans.isEmpty()) {
                    break;
                }
                
                Collections.sort(workKanbans, ((a, b) -> a.getWorkKanbanOrder().compareTo(b.getWorkKanbanOrder())));

                // 工程カンバンに割り当てられた組織の休憩と休日
                List<Long> breaktimeIds = this.getWorkKanbanBreaktimeIds(workKanbans.get(0));
                List<BreakTimeInfoEntity> workBreaktimes;
                if (breaktimeIds.isEmpty()) {
                    workBreaktimes = this.breakTimes.stream().filter(p -> p.getBreaktimeId().equals(0L)).collect(Collectors.toList());
                } else {
                    workBreaktimes = this.breakTimes.stream().filter(p -> p.getBreaktimeId().equals(0L) || breaktimeIds.contains(p.getBreaktimeId())).collect(Collectors.toList());
                }

                for (WorkKanbanEntity workKanban : workKanbans) {

                    ConWorkflowWorkEntity con = map.get(workKanban.getWorkId());
                    long taktTime = con.getStandardEndTime().getTime() - con.getStandardStartTime().getTime();
                    Date startTime = endTime;
                    
                    workKanban.setStartDatetime(con.getStandardStartTime());
                    workKanban.setCompDatetime(con.getStandardEndTime());

                    if (!this.isDefaultWorkTime()) {
                        // 開始時間が作業時間外となる場合、翌日にシフトする
                        if (0 >= this.closeTime.compareTo(DateUtils.toLocalTime(startTime))) {
                            LocalDate nextDay = DateUtils.toLocalDate(startTime).plusDays(1);
                            startTime = DateUtils.toDate(nextDay, this.openTime);
                        }

                        // 開始日時に作業日を反映
                        //long diff = (workKanban.getStartDatetime().getTime() - dailyTime) / DAY_MILLIS * DAY_MILLIS;
                        //long time = (diff < DAY_MILLIS) ? startTime.getTime() + diff : baseDatetime.getTime() + diff + ((workKanban.getStartDatetime().getHours() * 3600 + workKanban.getStartDatetime().getMinutes() * 60) * 1000L);
                        //startTime = (startTime.getTime() < time) ? new Date(time) : startTime;
                        //dailyTime = dailyTime + diff;
                    }

                    // 開始日時の休日チェック
                    startTime = this.checkHoliday(startTime, true);

                    if (!workKanban.getSkipFlag()) {
                        // 完了時間を算出
                        endTime = this.calcWithBreak(startTime, new Date(startTime.getTime() + taktTime), -1, workBreaktimes);
                        // 完了日時の休日チェック
                        endTime = this.checkHoliday(endTime, false);
                    } else {
                        endTime = startTime;
                    }

                    workKanban.setStartDatetime(startTime);
                    workKanban.setCompDatetime(endTime);
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("updatePlan end.");
        }

        return endTime;
    }
   /**
     * ロット1個流しカンバンの計画時間を更新する。(工程を同時に作業する場合)
     *
     * @param kanban カンバン
     * @param baseDatetime
     * @return カンバンの計画完了時間
     */
    private Date updatePlanForFastest(KanbanEntity kanban, Date baseDatetime) {
        logger.info("updatePlanForFastest start.");

        Date endTime = baseDatetime;

        try {
            for (ConWorkflowWorkEntity workflowWork : this.workflow.getConWorkflowWorkCollection()) {
                // タクトタイム
                long taktTime = workflowWork.getStandardEndTime().getTime() - workflowWork.getStandardStartTime().getTime();

                List<WorkKanbanEntity> workKanbans = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkId().equals(workflowWork.getWorkId())).collect(Collectors.toList());
                Collections.sort(workKanbans, ((a, b) -> a.getSerialNumber().compareTo(b.getSerialNumber())));

                // 工程カンバンに割り当てられた組織の休憩と休日
                List<Long> breaktimeIds = this.getWorkKanbanBreaktimeIds(workKanbans.get(0));
                List<BreakTimeInfoEntity> workBreaktimes;
                if (breaktimeIds.isEmpty()) {
                    workBreaktimes = this.breakTimes.stream().filter(p -> p.getBreaktimeId().equals(0L)).collect(Collectors.toList());
                } else {
                    workBreaktimes = this.breakTimes.stream().filter(p -> p.getBreaktimeId().equals(0L) || breaktimeIds.contains(p.getBreaktimeId())).collect(Collectors.toList());
                }

                for (WorkKanbanEntity workKanban : workKanbans) {

                    Date startTime = endTime;
                   
                    workKanban.setStartDatetime(workflowWork.getStandardStartTime());
                    workKanban.setCompDatetime(workflowWork.getStandardEndTime());

                    if (!this.isDefaultWorkTime()) {
                        // 開始時間が作業時間外となる場合、翌日にシフトする
                        if (0 >= this.closeTime.compareTo(DateUtils.toLocalTime(startTime))) {
                            LocalDate nextDay = DateUtils.toLocalDate(startTime).plusDays(1);
                            startTime = DateUtils.toDate(nextDay, this.openTime);
                        }

                        // 開始日時に作業日を反映
                        //long diff = (workKanban.getStartDatetime().getTime() - dailyTime) / DAY_MILLIS * DAY_MILLIS;
                        //long time = (diff < DAY_MILLIS) ? startTime.getTime() + diff : baseDatetime.getTime() + diff + ((workKanban.getStartDatetime().getHours() * 3600 + workKanban.getStartDatetime().getMinutes() * 60) * 1000L);
                        //startTime = (startTime.getTime() < time) ? new Date(time) : startTime;
                        //dailyTime = dailyTime + diff;
                    }

                    // 開始日時の休日チェック
                    startTime = this.checkHoliday(startTime, true);

                    if (!workKanban.getSkipFlag()) {
                        // 完了時間を算出
                        endTime = this.calcWithBreak(startTime, new Date(startTime.getTime() + taktTime), -1, workBreaktimes);
                        // 完了日時の休日チェック
                        endTime = this.checkHoliday(endTime, false);
                    } else {
                        endTime = startTime;
                    }

                    workKanban.setStartDatetime(startTime);
                    workKanban.setCompDatetime(endTime);
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("updatePlanForFastest end.");
        }

        return endTime;
    }

    /**
     * 工程カンバンに計画開始日時を更新する。
     *
     * @param workKanban
     */
    private Date updateWorkKanban(WorkKanbanEntity workKanban, Date baseDatetime) {
        // タクトタイム
        long taktTime = workKanban.getCompDatetime().getTime() - workKanban.getStartDatetime().getTime();

        // 工程カンバンに割り当てられた組織の休憩と休日
        List<Long> breaktimeIds = this.getWorkKanbanBreaktimeIds(workKanban);
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
     * 工程順の作業時間を更新する。
     *
     * @param workflow 工程順
     * @param work 工程
     */
    public void updateTimetable(WorkflowEntity workflow, WorkEntity work) {
        logger.info("updateTimetable start.");

        List<ConWorkflowWorkEntity> entities = workflow.getConWorkflowWorkCollection();
        this.workflowWorkMap = new HashMap<>();
        for (ConWorkflowWorkEntity entity : entities) {
            this.workflowWorkMap.put(String.valueOf(entity.getWorkId()), entity);
        }

        this.bpmnProcess = this.bpmnModel.getBpmnDefinitions().getProcess();

        List<BpmnSequenceFlow> flows;
        Date startTime = null;

        if (Objects.nonNull(work)) {
            ConWorkflowWorkEntity workflowWork = this.workflowWorkMap.get(String.valueOf(work.getWorkId()));
            workflowWork.setStandardEndTime(new Date(workflowWork.getStandardStartTime().getTime() + work.getTaktTime()));

            flows = this.getFlows(String.valueOf(work.getWorkId()), this.bpmnProcess.getSequenceFlowCollection());
            startTime = workflowWork.getStandardStartTime();

            if (flows.get(0).getSourceNode() instanceof BpmnParallelGateway) {
                BpmnParallelGateway gateway = (BpmnParallelGateway) flows.get(0).getSourceNode();
                flows = this.getNextFlowsForWorkflow(gateway.getId(), this.bpmnProcess.getSequenceFlowCollection());

                String nextId = gateway.getPairedId();
                for (BpmnSequenceFlow flow : flows) {
                    Date date = this.getStartTimeForWorkflow(flow.getTargetNode());
                    if (date.before(workflowWork.getStandardStartTime())) {
                        continue;
                    }

                    if (flow.getTargetNode() instanceof BpmnTask) {
                        ConWorkflowWorkEntity entity = this.workflowWorkMap.get(flow.getTargetRef());
                        this.shiftTimeForWorkflow(Arrays.asList(flow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime);
                        startTime = entity.getSkipFlag() ? entity.getStandardStartTime() : entity.getStandardEndTime();
                    } else {
                        this.shiftTimeForWorkflow(Arrays.asList(flow), (BpmnParallelGateway) gateway.getPairedGateway(), workflowWork.getStandardStartTime());
                    }

                    nextId = flow.getTargetRef();
                }

                flows = this.getNextFlowsForWorkflow(nextId, this.bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTimeForWorkflow(flows.get(0), startTime);

            } else {
                startTime = this.getLastWorkTimeForWorkflow(flows.get(0), startTime);
            }

        } else {
            flows = this.getNextFlowsForWorkflow(NODE_START_ID, this.bpmnProcess.getSequenceFlowCollection());
            startTime = this.getStartTimeForWorkflow(flows.get(0).getTargetNode());
        }

        this.shiftTimeForWorkflow(flows, null, startTime);

        logger.info("updateTimetable end.");
    }

    /**
     * 作業時間をシフトする。
     *
     * @param flows
     * @param endNode
     * @param baseTime
     * @param lotQauntity
     * @return
     * @throws Exception
     */
    private Tuple<Boolean, Date> shiftTime(List<BpmnSequenceFlow> flows, BpmnNode endNode, Date baseTime, int lotQuantity) throws Exception {
        if (Objects.isNull(endNode)) {
            // 開始時間が就業後の場合、翌日の始業時刻にシフトする。
            baseTime = this.checkAfterWork(baseTime);
            // 開始日時が休日の場合、翌営業日の始業時刻にシフトする。
            baseTime = this.checkHoliday(baseTime, true);
        }

        Date startTime = baseTime;

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
                WorkKanbanEntity workKanban = this.workKanbanMap.get(bpmnNode.getId());

                // 工程カンバンに割り当てられた組織の休憩と休日
                List<Long> breaktimeIds = this.getWorkKanbanBreaktimeIds(workKanban);
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

                logger.debug("Work: {}, {}, {}", workKanban.getWorkName(), workKanban.getStartDatetime(), startTime);

                // 計画完了時間に休憩時間を加算
                long taktTime = this.getWorkTaktTime(workKanban.getWorkId(), workKanban.getTaktTime()) * lotQuantity;// タクトタイム
                Date endTime = this.calcWithBreak(startTime, new Date(startTime.getTime() + taktTime), -1, workBreaktimes);

                // 完了日時の休日チェック
                endTime = checkHoliday(endTime, false);

                workKanban.setStartDatetime(startTime);
                workKanban.setCompDatetime(endTime);

                if (!workKanban.getSkipFlag() && !(workKanban.getWorkStatus() == KanbanStatusEnum.COMPLETION || workKanban.getWorkStatus() == KanbanStatusEnum.SUSPEND)) {
                    startTime = endTime;
                }

                flows = this.getNextFlows(flows.get(0).getTargetRef(), this.bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTime(flows.get(0), startTime);

            } else if (bpmnNode instanceof BpmnParallelGateway) {
                // ゲートウェイ
                BpmnParallelGateway gateway = (BpmnParallelGateway) bpmnNode;
                List<BpmnSequenceFlow> nextFlows = this.getNextFlows(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());

                if (SchedulePolicyEnum.PriorityParallel == this.workflow.getSchedulePolicy()) {
                    // 並列工程優先の場合
                    Date time = startTime;

                    for (BpmnSequenceFlow nextFlow : nextFlows) {
                        if (nextFlow.getTargetNode() instanceof BpmnTask) {
                            Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), time, lotQuantity);
                            if (!result.getLeft()) {
                                return new Tuple(false, startTime);
                            }

                            WorkKanbanEntity workKanban = this.workKanbanMap.get(nextFlow.getTargetRef());
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
                                    time = DateUtils.toDate(nextDay, this.openTime);
                                }
                            }
                        } else {
                            Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime, lotQuantity);
                            if (!result.getLeft()) {
                                return new Tuple(false, startTime);
                            }
                        }
                    }

                } else {
                    // 直列工程優先の場合

                    for (BpmnSequenceFlow nextFlow : nextFlows) {
                        Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime, lotQuantity);
                        if (!result.getLeft()) {
                            return new Tuple(false, startTime);
                        }
                    }
                }

                flows = this.getNextFlows(gateway.getPairedId(), this.bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTime(flows.get(0), startTime);
            }

            // 次ノードが終了ノードの場合、startTimeがカンバン完了日時となるため、翌日シフトのチェックなし
            if (!this.isDefaultWorkTime() && !NODE_END_ID.equals(flows.get(0).getTargetRef())) {
                // 開始時間が作業時間外となる場合、翌日にシフトする
                if (0 >= this.closeTime.compareTo(DateUtils.toLocalTime(startTime))) {
                    LocalDate nextDay = DateUtils.toLocalDate(startTime).plusDays(1);
                    startTime = DateUtils.toDate(nextDay, this.openTime);
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
            for (HolidayEntity holiday : this.holidays.stream().sorted(Comparator.comparing(p -> p.getHolidayDate())).collect(Collectors.toList())) {
                Date holidayStart = DateUtils.getBeginningOfDate(holiday.getHolidayDate());
                Date holidayEnd = DateUtils.getEndOfDate(holiday.getHolidayDate());
                if ((holidayStart.before(date) || holidayStart.equals(date))
                        && (holidayEnd.after(date) || holidayEnd.equals(date))) {
                    // 休日の場合は翌日にする。
                    LocalDateTime nextDay = DateUtils.toLocalDateTime(date).plusDays(1);
                    if (isStartDate) {
                        // 開始日時の場合は時刻も変更する。
                        date = DateUtils.toDate(nextDay.toLocalDate(), this.openTime);
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
     * 休憩を含めた終了時間を計算する。
     * 参考資料: スキップ検討.vsd
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
     * 休憩を除いた終了時間を計算する。
     * 参考資料: スキップ検討.vsd
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
            WorkKanbanEntity workKanban = this.workKanbanMap.get(bpmnNode.getId());
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
                List<BpmnSequenceFlow> flows = this.getNextFlows(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
                if (!flows.isEmpty()) {
                    Collections.sort(flows, new BpmnSequenceFlowComparator());
                    return this.getStartTime(flows.get(0).getTargetNode());
                }
            } else {
                WorkKanbanEntity workKanban = this.workKanbanMap.get(bpmnNode.getId());
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
                List<BpmnSequenceFlow> flows = this.getNextFlows(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
                if (!flows.isEmpty()) {
                    Collections.sort(flows, new BpmnSequenceFlowComparator());
                    return this.getWorkKanbanOrder(flows.get(0).getTargetNode());
                }
            } else {
                WorkKanbanEntity workKanban = this.workKanbanMap.get(bpmnNode.getId());
                return workKanban.getWorkKanbanOrder();
            }
            return Integer.MAX_VALUE;
        } catch (Exception ex) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 工程順の作業時間をシフトする。
     *
     * @param flows
     * @param endNode
     * @param baseTime
     * @return
     */
    private Tuple<Boolean, Date> shiftTimeForWorkflow(List<BpmnSequenceFlow> flows, BpmnNode endNode, Date baseTime) {

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
                ConWorkflowWorkEntity workflowWork = this.workflowWorkMap.get(bpmnNode.getId());

                Date endTime = new Date(startTime.getTime() + workflowWork.getStandardEndTime().getTime() - workflowWork.getStandardStartTime().getTime());
                workflowWork.setStandardStartTime(startTime);
                workflowWork.setStandardEndTime(endTime);
                if (!workflowWork.getSkipFlag()) {
                    startTime = endTime;
                }

                flows = this.getNextFlowsForWorkflow(flows.get(0).getTargetRef(), this.bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTimeForWorkflow(flows.get(0), startTime);

            } else if (bpmnNode instanceof BpmnParallelGateway) {
                // ゲートウェイ
                BpmnParallelGateway gateway = (BpmnParallelGateway) bpmnNode;
                List<BpmnSequenceFlow> nextFlows = this.getNextFlowsForWorkflow(bpmnNode.getId(), bpmnProcess.getSequenceFlowCollection());

                if (SchedulePolicyEnum.PriorityParallel == this.workflow.getSchedulePolicy()) {
                    Date time = startTime;

                    for (BpmnSequenceFlow nextFlow : nextFlows) {

                        if (nextFlow.getTargetNode() instanceof BpmnTask) {
                            Tuple<Boolean, Date> result = this.shiftTimeForWorkflow(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), time);
                            if (!result.getLeft()) {
                                return new Tuple(false, startTime);
                            }

                            ConWorkflowWorkEntity workflowWork = this.workflowWorkMap.get(nextFlow.getTargetRef());
                            time = workflowWork.getSkipFlag() ? workflowWork.getStandardStartTime() : workflowWork.getStandardEndTime();

                        } else {
                            Tuple<Boolean, Date> result = this.shiftTimeForWorkflow(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime);
                            if (!result.getLeft()) {
                                return new Tuple(false, startTime);
                            }
                        }
                    }

                } else {
                    for (BpmnSequenceFlow nextFlow : nextFlows) {
                        Tuple<Boolean, Date> result = this.shiftTimeForWorkflow(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime);
                        if (!result.getLeft()) {
                            return new Tuple(false, startTime);
                        }
                    }
                }

                flows = this.getNextFlowsForWorkflow(gateway.getPairedId(), this.bpmnProcess.getSequenceFlowCollection());
                startTime = this.getLastWorkTimeForWorkflow(flows.get(0), startTime);
            }
        }

        return new Tuple(true, startTime);
    }

    /**
     * 次のシーケンスフローを取得する。
     *
     * @param sourceId
     * @param flows
     * @return
     */
    private List<BpmnSequenceFlow> getNextFlowsForWorkflow(String sourceId, List<BpmnSequenceFlow> flows) {
        try {
            List<BpmnSequenceFlow> nextFlows = flows.stream().filter(p -> p.getSourceRef().equals(sourceId)).collect(Collectors.toList());
            Collections.sort(nextFlows, new BpmnSequenceFlowComparatorForWorkflow());
            return nextFlows;
        } catch (NullPointerException ex) {
            logger.fatal(ex, ex);
        }
        return new LinkedList();
    }

    /**
     * 前工程の終了時間を取得する。
     *
     * @param flow
     * @param date
     * @return
     */
    private Date getLastWorkTimeForWorkflow(BpmnSequenceFlow flow, Date date) {
        if (flow.getTargetNode() instanceof BpmnParallelGateway) {
            return this.getLastWorkTimeForWorkflow(flow.getTargetNode(), date);
        } else if (flow.getSourceNode() instanceof BpmnParallelGateway) {
            return this.getLastWorkTimeForWorkflow(flow.getSourceNode(), date);
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
    private Date getLastWorkTimeForWorkflow(BpmnNode bpmnNode, Date date) {
        if (bpmnNode instanceof BpmnTask) {
            ConWorkflowWorkEntity workflowWork = this.workflowWorkMap.get(bpmnNode.getId());
            if (workflowWork.getSkipFlag()) {
                List<BpmnSequenceFlow> previousFlows = this.getFlows(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
                for (BpmnSequenceFlow previouFlow : previousFlows) {
                    Date lastDate = this.getLastWorkTimeForWorkflow(previouFlow.getSourceNode(), date);
                    if (date.before(lastDate)) {
                        date = lastDate;
                    }
                }
            } else {
                if (date.before(workflowWork.getStandardEndTime())) {
                    date = workflowWork.getStandardEndTime();
                }
            }

        } else if (bpmnNode instanceof BpmnParallelGateway) {
            List<BpmnSequenceFlow> previousFlows = this.getFlows(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
            for (BpmnSequenceFlow previouFlow : previousFlows) {
                Date lastDate = this.getLastWorkTimeForWorkflow(previouFlow.getSourceNode(), date);
                if (date.before(lastDate)) {
                    date = lastDate;
                }
            }
        }
        return date;
    }

    /**
     * 開始時間を取得する。
     *
     * @param bpmnNode
     * @return
     */
    private Date getStartTimeForWorkflow(BpmnNode bpmnNode) {
        try {
            if (bpmnNode instanceof BpmnParallelGateway) {
                List<BpmnSequenceFlow> flows = this.getNextFlowsForWorkflow(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
                if (!flows.isEmpty()) {
                    Collections.sort(flows, new BpmnSequenceFlowComparatorForWorkflow());
                    return this.getStartTimeForWorkflow(flows.get(0).getTargetNode());
                }
            } else {
                ConWorkflowWorkEntity workflowWork = this.workflowWorkMap.get(bpmnNode.getId());
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
                List<BpmnSequenceFlow> flows = this.getNextFlowsForWorkflow(bpmnNode.getId(), this.bpmnProcess.getSequenceFlowCollection());
                if (!flows.isEmpty()) {
                    Collections.sort(flows, new BpmnSequenceFlowComparatorForWorkflow());
                    return this.getWorkflowOrder(flows.get(0).getTargetNode());
                }
            } else {
                ConWorkflowWorkEntity workflowWork = this.workflowWorkMap.get(bpmnNode.getId());
                return workflowWork.getWorkflowOrder();
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
     * 工程カンバンに割り当てられた組織の休憩時間ID一覧を取得する。
     *
     * @param workKanban 工程カンバン
     * @return 休憩時間ID一覧
     */
    private List<Long> getWorkKanbanBreaktimeIds(WorkKanbanEntity workKanban) {
        if (Objects.isNull(workKanban) || Objects.isNull(workKanban.getOrganizationCollection()) || workKanban.getOrganizationCollection().isEmpty()) {
            return new ArrayList();
        }

        return this.organizationRest.getBreaktimes(workKanban.getOrganizationCollection());
    }

    /**
     * 工程順の標準時間からタクトタイムを取得する。
     *
     * @param workId 工程ID
     * @param defaultTaktTime デフォルトのタクトタイム (工程のタクトタイム)
     * @return
     */
    private long getWorkTaktTime(long workId, long defaultTaktTime) {
        long taktTime = defaultTaktTime;

        Optional<ConWorkflowWorkEntity> opt = this.workflow.getConWorkflowWorkCollection().stream().filter(p -> p.getWorkId().equals(workId)).findFirst();
        ConWorkflowWorkEntity conWork;
        if (opt.isPresent()) {
            conWork = opt.get();
        } else {
            return taktTime;
        }

        if (Objects.isNull(conWork.getStandardStartTime()) || Objects.isNull(conWork.getStandardEndTime())) {
            return taktTime;
        }

        // 工程順の標準時間からタクトタイムを計算する。
        if (conWork.getStandardStartTime().after(conWork.getStandardEndTime())) {
            taktTime = conWork.getStandardStartTime().getTime() + (24 * 60 * 60 * 1000) - conWork.getStandardEndTime().getTime();
        } else {
            taktTime = conWork.getStandardEndTime().getTime() - conWork.getStandardStartTime().getTime();
        }
        return taktTime;
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
        for (HolidayEntity holiday : this.holidays.stream().sorted(Comparator.comparing(p -> p.getHolidayDate())).collect(Collectors.toList())) {
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
