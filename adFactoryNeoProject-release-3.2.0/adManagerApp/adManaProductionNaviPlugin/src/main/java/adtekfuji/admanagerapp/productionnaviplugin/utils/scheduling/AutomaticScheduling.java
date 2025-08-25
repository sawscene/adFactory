/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils.scheduling;

import adtekfuji.admanagerapp.productionnaviplugin.clientservice.WorkPlanRestAPI;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanKanbanStartCompDate;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.Tuple;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;
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
 * 自動スケジューリング
 *
 * @author (TST)min
 * @version 2.0.0
 * @since 2018/09/28
 */
public class AutomaticScheduling {

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
    
    // スケジューリングの対象（カンバン = 工程順）
    private KanbanInfoEntity kanban;
    private final WorkflowInfoEntity workflow;
    // 対象：工程
    private Map<String, WorkKanbanInfoEntity> workKanbanMap;
    
    // 工程の順番計算プロセスシステム
    private BpmnProcess bpmnProcess;
    private final BpmnModel bpmnModel;
    
    private final WorkPlanRestAPI REST_API = new WorkPlanRestAPI();
    
    // ダウンタイムUTIL
    private WorkerDowntimeUtil downtimeUtil;
    private final int CALCULATE_DAYS = 10;
    
    // 工程順コントローラー
    private FlowUtil flowUtil;
    
    /**
     * コンストラクタ
     *
     * @param kanbanId
     */
    public AutomaticScheduling(Long kanbanId) {
        this.kanban = REST_API.searchKanban(kanbanId);
        
        this.workflow = REST_API.searchWorkflow(kanban.getFkWorkflowId());
        this.bpmnModel = BpmnModeler.getModeler();
        
        this.flowUtil = new FlowUtil(kanban, workflow);
        
        // 作業時間枠
        LocalTime openTime;
        LocalTime closeTime;
        
        if ((Objects.nonNull(this.workflow.getOpenTime()) && Objects.nonNull(this.workflow.getCloseTime()))
                && (this.workflow.getOpenTime().compareTo(this.workflow.getCloseTime()) != 0)) {
            openTime = DateUtils.toLocalTime(this.workflow.getOpenTime());
            closeTime = DateUtils.toLocalTime(this.workflow.getCloseTime());
        } else {
            openTime = null;
            closeTime = null;
        }
        
        downtimeUtil = new WorkerDowntimeUtil(openTime, closeTime, CALCULATE_DAYS);
        
        try {
            BpmnDocument bpmn = BpmnDocument.unmarshal(workflow.getWorkflowDiaglam());
            this.bpmnModel.createModel(bpmn);
        } catch (JAXBException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * コンストラクタ
     *
     * @param kanban
     */
    public AutomaticScheduling(KanbanInfoEntity kanban) {
        this.kanban = kanban;
        
        WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
        this.workflow = workflowInfoFacade.find(kanban.getFkWorkflowId());
        this.bpmnModel = BpmnModeler.getModeler();
        
        this.flowUtil = new FlowUtil(kanban, workflow);
        
        // 作業時間枠
        LocalTime openTime;
        LocalTime closeTime;
        
        if ((Objects.nonNull(this.workflow.getOpenTime()) && Objects.nonNull(this.workflow.getCloseTime()))
                && (this.workflow.getOpenTime().compareTo(this.workflow.getCloseTime()) != 0)) {
            openTime = DateUtils.toLocalTime(this.workflow.getOpenTime());
            closeTime = DateUtils.toLocalTime(this.workflow.getCloseTime());
        } else {
            openTime = null;
            closeTime = null;
        }
        
        downtimeUtil = new WorkerDowntimeUtil(openTime, closeTime, CALCULATE_DAYS);
        
        try {
            BpmnDocument bpmn = BpmnDocument.unmarshal(workflow.getWorkflowDiaglam());
            this.bpmnModel.createModel(bpmn);
        } catch (JAXBException ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 作業時間を移動する。
     *
     * @param workKanbanId 対象工程
     * @param width 工程時間の長さ
     * @param movedLength 移動長さ
     * @param isTrackingON 追従可否
     * @throws Exception
     */
    public void dragTime(Long workKanbanId, double width, double movedLength, boolean isTrackingON) throws Exception {
        logger.info("<< :workKanban(工程) time move start");
        Boolean isSeparate = false;
        //工程検索
        WorkKanbanInfoEntity datasWorkKanban = null;
        if(Objects.nonNull(kanban.getWorkKanbanCollection()) && kanban.getWorkKanbanCollection().size() > 0){
            Optional<WorkKanbanInfoEntity> data = kanban.getWorkKanbanCollection().stream().filter( d -> d.getWorkKanbanId().equals(workKanbanId)).findFirst();
            if (data.isPresent()) {
                datasWorkKanban = data.get();
            }                
        }

        WorkKanbanInfoEntity datasWorkSeparate = null;
        if(Objects.nonNull(kanban.getWorkKanbanCollection()) && kanban.getSeparateworkKanbanCollection().size() > 0){
            Optional<WorkKanbanInfoEntity> data = kanban.getSeparateworkKanbanCollection().stream().filter( d -> d.getWorkKanbanId().equals(workKanbanId)).findFirst();
            if (data.isPresent()) {
                isSeparate = true;
                datasWorkSeparate = data.get();
            }                
        }

        WorkKanbanInfoEntity workKanban = !isSeparate ? datasWorkKanban : datasWorkSeparate;
        
        // カンバンステータスを計画中にする(update準備)
        KanbanStatusEnum status = kanban.getKanbanStatus();
        kanban.setKanbanStatus(KanbanStatusEnum.PLANNING);
        REST_API.updateKanban(kanban);

        //移動された新しいstart時間計算
        LocalDateTime startTime = LocalDateTime.ofInstant(workKanban.getStartDatetime().toInstant(), ZoneOffset.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(workKanban.getCompDatetime().toInstant(), ZoneOffset.systemDefault());
        long diffTime = ChronoUnit.MILLIS.between(startTime, endTime);
        long movedTime = (long)((diffTime * movedLength)/width);
        Date newStartTime = new Date(workKanban.getStartDatetime().getTime()+movedTime);
        
        //移動された新しいend時間計算
        Date[] result = this.calcWithDowntime(workKanban, newStartTime);
       
        workKanban.setStartDatetime(result[0]);
        workKanban.setCompDatetime(result[1]);
        
        //工程の追従
        if(isTrackingON) {
            changeByTracking(workKanban.getFkWorkId(), isSeparate, result[1]);
        }
        
        // 設定変更とカンバンステータスを元に戻す
        kanban.setKanbanStatus(status);
        REST_API.updateKanban(kanban);

        logger.info(":workKanban(工程) time move end >>");
    }
    
    /**
     * カンバンスケジューリング
     *
     * @throws Exception
     */
    public void schedulingKanban() throws Exception {
        logger.debug(":schedulingKanban start");
        Date startDateTime = WorkPlanKanbanStartCompDate.getWorkKanbanStartDateTime(kanban.getWorkKanbanCollection());
        this.changeByTracking(-1L, false, startDateTime);
    }
    
    /**
     * 追従して次の工程時間変更
     *
     * @param workKanbanId 追従対象工程
     * @param startDateTime 次の工程スタット時間
     * @param isSeparate
     * @throws Exception
     */
    private void changeByTracking(Long workKanbanId, Boolean isSeparate, Date startDateTime) throws Exception {
        logger.debug("--::changeFollowingTimes start:: *trackingON*");

        this.bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();
        
        if(!isSeparate) {
            this.workKanbanMap = new HashMap<>();
            for (WorkKanbanInfoEntity workKanban : kanban.getWorkKanbanCollection()) {
                this.workKanbanMap.put(String.valueOf(workKanban.getFkWorkId()), workKanban);
            }

            //指定された工程から変更スタット
            String shiftedStartId = "start_id";
            if(!workKanbanId.equals(-1L)) {
                shiftedStartId = String.valueOf(workKanbanId);
            }

            // 基準時間を設定
            List<BpmnSequenceFlow> flows = this.getNextFlows(shiftedStartId, this.bpmnProcess.getSequenceFlowCollection());
            Tuple<Boolean, Date> result = this.shiftTime(flows, null, startDateTime);
            startDateTime = result.getRight();
        }
        
        // 基準時間を設定
        Boolean existSeparate = false;
        this.workKanbanMap = new HashMap<>();
        for (WorkKanbanInfoEntity workKanban : kanban.getSeparateworkKanbanCollection()) {
           this.workKanbanMap.put(String.valueOf(workKanban.getFkWorkId()), workKanban);
           existSeparate = true;
        }
        if(existSeparate) {
            this.bpmnProcess.setSequenceFlowCollection(this.flowUtil.getSeparateFlows());
            
            //指定された工程から変更スタット
            String shiftedStartId = "start_id";
            if(isSeparate) {
                shiftedStartId = String.valueOf(workKanbanId);
            }
            
            List<BpmnSequenceFlow> flows = this.getNextFlows(shiftedStartId, this.bpmnProcess.getSequenceFlowCollection());
            Tuple<Boolean, Date> result = this.shiftTime(flows, null, startDateTime);
            startDateTime = result.getRight();
        }
        
        //カンバンのstartDateとcompleteDate保存
        List<WorkKanbanInfoEntity> listWorkKanban = new ArrayList<>();
        listWorkKanban.addAll(kanban.getWorkKanbanCollection());
        listWorkKanban.addAll(kanban.getSeparateworkKanbanCollection());
        listWorkKanban.sort(Comparator.comparing(item -> item.getStartDatetime()));
        
        kanban.setStartDatetime(listWorkKanban.get(0).getStartDatetime());
        kanban.setCompDatetime(startDateTime);

        logger.info("changeFollowingTimes end: {}, {}", kanban.getStartDatetime(), kanban.getCompDatetime()+"::--");

    }
    
    /**
     * 作業時間をシフトする。
     *
     * @param flows
     * @param endNode
     * @param startTime
     * //@param isBreak 休憩時間の扱い方 (true: 休憩時間を加算する false: 休憩時間を減算する)
     * @return
     * @throws Exception
     */
    private Tuple<Boolean, Date> shiftTime(List<BpmnSequenceFlow> flows, BpmnNode endNode, Date startTime) throws Exception {
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
                
                //Date endTime = startTime;
                Date[] result = {startTime, startTime};
                if (!workKanban.getSkipFlag()) {
                    // 計画完了時間に休憩時間を加算
                    result = this.calcWithDowntime(workKanban, startTime);
                }
                workKanban.setStartDatetime(result[0]);
                workKanban.setCompDatetime(result[1]);

                if (!workKanban.getSkipFlag()) {
                    startTime = result[1];
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
                            Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), time);
                            if (!result.getLeft()) {
                               return new Tuple(false, startTime);
                            }

                            WorkKanbanInfoEntity workKanban = this.workKanbanMap.get(nextFlow.getTargetRef());
                            time = workKanban.getSkipFlag()? workKanban.getStartDatetime() : workKanban.getCompDatetime();
                        } else {
                            Tuple<Boolean, Date> result = this.shiftTime(Arrays.asList(nextFlow), (BpmnParallelGateway) gateway.getPairedGateway(), startTime);
                            if (!result.getLeft()) {
                               return new Tuple(false, startTime);
                            }
                        }
                    }

                } else {
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
     * 移動時間計算。
     *
     * @param workKanbanId 対象工程
     * @param newStartTime スタット時間
     */
    private Date[] calcWithDowntime(WorkKanbanInfoEntity workKanban, Date newStartTime) {
        
        Date[] result = new Date[2];
        
        //タクトタイム
        int taktTime = workKanban.getTaktTime();
        //修旅時間
        Date newEndTime = newStartTime;
        
        //作業者のダウンタイムデータ
        List<WorkerDowntimeData> workers = downtimeUtil.getData(workKanban, newEndTime);
        
        // Setting 5minutes(for improving calculated result accuracy)
        newEndTime = getFiveMinutesNewEndTime(newEndTime);
        LocalDateTime settingBeforeTime = LocalDateTime.ofInstant(newStartTime.toInstant(), ZoneOffset.systemDefault());
        LocalDateTime settingAfterTime = LocalDateTime.ofInstant(newEndTime.toInstant(), ZoneOffset.systemDefault());
        // setting時間 5分
        long settingTimes = ChronoUnit.MILLIS.between(settingBeforeTime, settingAfterTime);
        for(WorkerDowntimeData worker : workers) {
            if(Objects.nonNull(worker.isDowntime(null, newStartTime))) {
                taktTime -= settingTimes;
            }
        }
        
        // 5分間できる作業の量
        long FIVE_MINUTES_MILLIS = 300000L;
        
        // newStartTimeセッティングを一回だけ行うためのプラグ
//        boolean newStartTimeFlg = true;
        newStartTime = newEndTime;
        // ルプ
        while(taktTime > 0) {
            // workflow単位、作業者の同じダウンタイムジャンププラグ
            boolean jumpChkFlg = false;
            for(WorkerDowntimeData worker : workers) {
                DowntimeData nowDowntime = worker.isDowntime(null, newEndTime);
                if(Objects.isNull(nowDowntime)) {
                    //タクトタイム減らす。
                    taktTime -= FIVE_MINUTES_MILLIS;
                    
                    //新しいスタット時間(最初の一回だけ)
//                    if(newStartTimeFlg) {
//                        newStartTime = newEndTime;
//                        newStartTimeFlg = false;
//                    }
                } else {
                    //作業者共通のダウンタイムならば飛び越える    
                    Date jumpChkTime = nowDowntime.getEndtime();
                    // ダウンタイムのEnd時間が全部同じ場合だけジャンププラグ
                    jumpChkFlg = true;
                    for(WorkerDowntimeData jumpChk : workers) {
                        DowntimeData jumpChkData = jumpChk.isDowntime(null, newEndTime);
                        // 一つでも違うEnd時間があればジャンプ対象から外す。
                       if (Objects.isNull(jumpChkData)) {
                            jumpChkFlg = false;
                            break;
                        } else {
                            if(jumpChkTime.compareTo(jumpChkData.getEndtime()) != 0) {
                                jumpChkFlg = false;
                                break;
                            }
                        }
                    }
                    // ジャンプ
                    if(jumpChkFlg) {
                        newEndTime = org.apache.commons.lang3.time.DateUtils.addSeconds(jumpChkTime, 1);
                        break;
                    }
                }
            }
            
            // ジャンプしなかった場合
            if(!jumpChkFlg) {
                newEndTime = org.apache.commons.lang3.time.DateUtils.addMinutes(newEndTime, 5);
            }
        }
        
        // 結果時間保存
        result[0] = newStartTime;
        result[1] = newEndTime;
        
        return result;
    }
    
    /**
     * スタッと時間を５分単位にセット（計算結果の精度を高めるため）。
     *
     * @param newEndTime 対象時間
     * @return ５分単位になった新しい時間
     * @throws Exception
     */
    private Date getFiveMinutesNewEndTime(Date newEndTime) {
        int minutesSecond = newEndTime.getMinutes()*60+newEndTime.getSeconds();
        Calendar result = Calendar.getInstance();
        result.setTime(org.apache.commons.lang3.time.DateUtils.truncate(newEndTime, Calendar.HOUR_OF_DAY));
        
        if(0 < minutesSecond && minutesSecond <= 5*60) {
            result.add(Calendar.MINUTE, 5);
        } else if(5*60 < minutesSecond && minutesSecond <= 10*60) {
            result.add(Calendar.MINUTE, 10);
        } else if(10*60 < minutesSecond && minutesSecond <= 15*60) {
            result.add(Calendar.MINUTE, 15);
        } else if(15*60 < minutesSecond && minutesSecond <= 20*60) {
            result.add(Calendar.MINUTE, 20);
        } else if(20*60 < minutesSecond && minutesSecond <= 25*60) {
            result.add(Calendar.MINUTE, 25);
        } else if(25*60 < minutesSecond && minutesSecond <= 30*60) {
            result.add(Calendar.MINUTE, 30);
        } else if(30*60 < minutesSecond && minutesSecond <= 35*60) {
            result.add(Calendar.MINUTE, 35);
        } else if(35*60 < minutesSecond && minutesSecond <= 40*60) {
            result.add(Calendar.MINUTE, 40);
        } else if(40*60 < minutesSecond && minutesSecond <= 45*60) {
            result.add(Calendar.MINUTE, 45);
        } else if(45*60 < minutesSecond && minutesSecond <= 50*60) {
            result.add(Calendar.MINUTE, 50);
        } else if(50*60 < minutesSecond && minutesSecond <= 55*60) {
            result.add(Calendar.MINUTE, 55);
        } else if(55*60 < minutesSecond && minutesSecond <= 60*60-1) {
            result.add(Calendar.MINUTE, 60);
        }
        
        return result.getTime();
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
     *　指定した工程ID以降の工程をスケジューリングする。
     * 
     * @param sourceId 工程ID
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
