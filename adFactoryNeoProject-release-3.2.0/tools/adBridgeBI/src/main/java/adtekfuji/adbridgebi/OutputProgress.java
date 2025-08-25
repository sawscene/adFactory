/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi;

import adtekfuji.adbridgebi.common.AdBridgeBIConfig;
import adtekfuji.adbridgebi.entity.AdFactoyWorkEntity;
import adtekfuji.adbridgebi.entity.KanbanDateProgressEntity;
import adtekfuji.adbridgebi.entity.KanbanWorkProgressEntity;
import adtekfuji.adbridgebi.entity.WorkProgressEntity;
import adtekfuji.adbridgebi.jdbc.DbConnector;
import adtekfuji.adbridgebi.jdbc.adfactorydb.AdFactoryWorkAccessor;
import adtekfuji.adbridgebi.jdbc.progressdb.KanbanDateProgressAccessor;
import adtekfuji.adbridgebi.jdbc.progressdb.KanbanWorkProgressAccessor;
import adtekfuji.adbridgebi.jdbc.progressdb.ProgressDbConnector;
import adtekfuji.adbridgebi.jdbc.progressdb.WorkProgressAccessor;
import adtekfuji.utility.DateUtils;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 進捗情報出力
 *
 * @author nar-nakamura
 */
public class OutputProgress {

    private final Logger logger = LogManager.getLogger();

    private static OutputProgress instance = null;

    private static final int UPDATE_INTERVAL_MSEC = AdBridgeBIConfig.getInstance().getUpdateInterval() * 1000 * 60;
    private static final int MAX_SEQNO = 999;

    private final AdFactoryWorkAccessor workAccessor = new AdFactoryWorkAccessor();
    private final KanbanWorkProgressAccessor kwpAccessor= new KanbanWorkProgressAccessor();
    private final KanbanDateProgressAccessor kdpAccessor= new KanbanDateProgressAccessor();
    private final WorkProgressAccessor wpAccessor= new WorkProgressAccessor();

    private final List<KanbanWorkProgressEntity> kwpEntities = new ArrayList();// カンバン進捗情報(工程基準)一覧
    private final List<KanbanDateProgressEntity> kdpEntities = new ArrayList();// カンバン進捗情報(日付基準)一覧
    private final List<WorkProgressEntity> wpEntities = new ArrayList();// 個別進捗情報一覧
    private final List<String> deleteProgressNos = new ArrayList();
    private List<Long> deleteKanbanIds;

    private Timer timer;
    private Date nextFromDate = null;// 次回の先頭日時

    private final Map<Long, Integer> kanbanProgressNoMap;// カンバンIDとプログレスNoのマップ

    /**
     * インスタンスを取得する。
     *
     * @return インスタンス
     */
    public static OutputProgress getInstance() {
        if (Objects.isNull(instance)) {
            instance = new OutputProgress();
        }
        return instance;
    }

    /**
     * コンストラクタ
     */
    private OutputProgress() {
        // カンバンIDとプログレスNoのマップを生成
        this.kanbanProgressNoMap = this.kwpAccessor.createKanbanProgressNoMap();
    }

    /**
     * 進捗情報出力を開始する。
     */
    public void start() {
        logger.info("start.");
        try {
            this.stop();
            this.timer = new Timer();

            // 進捗情報を出力する。
            output();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            // 次の進捗情報出力の遅延実行タイマーを設定する。
            setDelayedOutputTimer(UPDATE_INTERVAL_MSEC);
        }
    }

    /**
     * 進捗情報出力の遅延実行タイマーを設定する。
     *
     * @param interval 遅延時間(msec)
     */
    private void setDelayedOutputTimer(long interval) {
        logger.info("setDelayedOutputTimer.");
        try {
            // stop()で停止済の場合はタイマー設定しない。
            if (Objects.isNull(this.timer)) {
                logger.info("timer is stopped.");
                return;
            }

            this.timer = new Timer();
            this.timer.schedule(new TimerTask(){
                @Override
                public void run() {
                    long startTime = System.currentTimeMillis();
                    try {
                        this.cancel();

                        // 進捗情報を出力する。
                        output();

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        // 次の出力までの待機時間(ms)
                        long nextInterval = UPDATE_INTERVAL_MSEC - (System.currentTimeMillis() - startTime);
                        if (nextInterval < 0) {
                            nextInterval = 0;
                        }

                        // 次の進捗情報出力の遅延実行タイマーを設定する。
                        setDelayedOutputTimer(nextInterval);
                    }
                }

                @Override
                public boolean cancel() {
                    return super.cancel();
                }
            }, interval);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 進捗情報出力を停止する。
     */
    public void stop() {
        logger.info("stop.");
        try {
            if (Objects.nonNull(this.timer)) {
                this.timer.cancel();
                this.timer.purge();
                this.timer = null;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 進捗情報を出力する。
     */
    private void output() {
        logger.info("output start.");
        try {
            // 現在日時
            Date now = new Date();
            // 本日
            Date todayBegin = DateUtils.getBeginningOfDate(now);
            Date todayEnd = DateUtils.getEndOfDate(now);
            // 昨日
            Date yesterday = this.dateAddDays(todayBegin, -1);

            if (Objects.nonNull(nextFromDate) && !todayBegin.equals(DateUtils.getBeginningOfDate(nextFromDate))) {
                // 前回の出力日時から日が変わった
                nextFromDate = null;
            }

            boolean isAll = Objects.isNull(nextFromDate);
            //if (isAll) {
            //    kanbanProgressNoMap.clear();
            //}

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            // adFactoryの工程情報を取得する。
            List<AdFactoyWorkEntity> adFactoryWorks = workAccessor.getAdFactoryWorks(nextFromDate);

            // 2019/10/03 ユーザーの要望により休憩時間は登録しない。
            // adFactoryの休憩時間情報を取得する。
            //String breaktimeNamesValue = AdBridgeBIConfig.getInstance().getBreaktimeNames();
            //List<String> breaktimeNames = Arrays.asList(breaktimeNamesValue.split(","));
            //List<BreakTimeInfoEntity> breaktimes = workAccessor.getBreakTimes(breaktimeNames);
            //List<BreakTimeInfoEntity> breaktimes = new ArrayList<>();

            // 更新対象のカンバンIDリストの作成
            List<Long> kanbanIds = new ArrayList<>(adFactoryWorks.stream()
                    .map(AdFactoyWorkEntity::getKanbanId)
                    .collect(Collectors.toSet()));
            
            // 削除対象のカンバンIDリストの作成
            deleteKanbanIds = new ArrayList<>(kanbanProgressNoMap.keySet());
            deleteKanbanIds.removeAll(kanbanIds);
            deleteProgressNos.clear();
            for (Long kanbanId : deleteKanbanIds) {
                deleteProgressNos.add(String.valueOf(kanbanProgressNoMap.get(kanbanId)));
            }
            
            // 個別進捗の取得
            Map<Long, List<WorkProgressEntity>> workProgressMap = this.workAccessor.getWorkProgress(kanbanIds);

            KanbanWorkProgressEntity kwpEntity = null;// カンバン進捗情報(工程基準)
            KanbanDateProgressEntity kdpEntity = null;// カンバン進捗情報(日付基準)
            WorkProgressEntity wpEntity = null;// 個別進捗情報
            //List<WorkProgressEntity> wpBreaktimes;// 個別進捗情報の休憩時間
            
            // カンバン進捗情報(日付基準)のヘッダー
            List<Date> kdpDates = new LinkedList();
            KanbanDateProgressEntity kdpHeader = new KanbanDateProgressEntity();
            kdpHeader.setProgressNo("0");
            kdpHeader.setKanbanName("カンバン");
            Date kdpHeaderDate = yesterday;
            for (int dayIndex = 0; dayIndex < KanbanDateProgressEntity.MAX_WORK; dayIndex++) {
                kdpDates.add(kdpHeaderDate);

                kdpHeader.setWorkName(dayIndex, dateFormat.format(kdpHeaderDate));
                kdpHeader.setStatus(dayIndex, "");

                kdpHeaderDate = this.dateAddDays(kdpHeaderDate, 1);
            }

            // カンバン進捗情報(日付基準)のヘッダーは、全て出力時のみ更新する。
            if (isAll) {
                kdpEntities.add(kdpHeader);
            }

            long befKanbanId = -1;
            int befProgressNo = 0;

            int kwpWorkIndex = 0;// カンバン進捗情報(工程基準)の工程インデックス(0～19)
            int wpPlanOrder = 1;
            int wpActualOrder = 1;

            Date kanbanStartDatetime = null;
            Date kanbanCompDatetime = null;

            List<KanbanStatusEnum> workingStatusEnumList = Arrays.asList(KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND);// 仕掛中(作業中,一時中断)
            List<String> plannedStatusList = Arrays.asList("1", "11", "21");// 計画中
            List<String> workingStatusList = Arrays.asList("2", "12", "22", "3", "13", "23");// 仕掛中(作業中,一時中断)

            for (AdFactoyWorkEntity work : adFactoryWorks) {
//                // プログレスNoを取得する
//                progressNo = this.getKanbanProgressNo(work.getKanbanId(), work.getGroupNo());
//                if (-1 == progressNo) {
//                    continue;
//                }

                long kanbanId = work.getKanbanId();
                int groupNo = work.getGroupNo();

                int oldProgressNo = 0;
                int progressNo = -1;
                int seqNo = -1;

                if (kanbanProgressNoMap.containsKey(kanbanId)) {
                    oldProgressNo = kanbanProgressNoMap.get(kanbanId);
                    seqNo = oldProgressNo - groupNo;
                }

                if (seqNo < 1 || seqNo > 999) {
                    seqNo = this.kwpAccessor.createSeqNo(groupNo);
                    if (MAX_SEQNO < seqNo) {
                        // MAX_SEQNO超過の場合は、出力対象外とする
                        continue;
                    }
                }

                progressNo = work.getGroupNo() + seqNo;
                kanbanProgressNoMap.put(kanbanId, progressNo);

                if (oldProgressNo > 0
                        && oldProgressNo != progressNo) {
                    // 古いprogresNoのデータは削除する。
                    deleteProgressNos.add(String.valueOf(oldProgressNo));
                }

                if (befKanbanId != work.getKanbanId()) {

                    // 個別進捗(実績)
                    if (workProgressMap.containsKey(work.getKanbanId())) {
                        List<WorkProgressEntity> entities = workProgressMap.get(work.getKanbanId());
                        for (WorkProgressEntity entity : entities) {

                            Date startDate = entity.getStartDatetime();
                            Date compDate = entity.getCompDatetime();

                            if (Objects.isNull(compDate)) {
                                compDate = now;
                            }

                            Date originalCompDate = compDate;

                            boolean isNext = true;
                            while (isNext){
                                if (compDate.after(DateUtils.getEndOfDate(startDate))) {
                                    // 日またぎ
                                    compDate = DateUtils.getEndOfDate(startDate);
                                } else {
                                    isNext = false;
                                }
                            
                                wpEntity = new WorkProgressEntity();

                                wpEntity.setProgressNo(String.valueOf(progressNo));
                                wpEntity.setProgressType("2");
                                wpEntity.setProgressOrder(String.valueOf(wpActualOrder));
                                wpEntity.setProgressDate(dateFormat.format(startDate));
                                wpEntity.setStartTime(timeFormat.format(startDate));
                                wpEntity.setCompTime(timeFormat.format(compDate));
                                wpEntity.setWorkName(entity.getWorkName());
                                wpEntity.setKanbanId(entity.getKanbanId());

                                wpEntities.add(wpEntity);
                                wpActualOrder++;
                                
                                if (isNext) {
                                    startDate = this.dateAddDays(DateUtils.getBeginningOfDate(startDate), 1);// 翌日の0時
                                    compDate = originalCompDate;
                                }
                            }
                        }
                    }

                    // カンバンの計画・実績の開始日時
                    if (Objects.isNull(work.getActualStartDatetime())
                            || Objects.isNull(work.getStartDatetime())
                            || work.getStartDatetime().before(work.getActualStartDatetime())) {
                        kanbanStartDatetime = work.getStartDatetime();
                    } else {
                        kanbanStartDatetime = work.getActualStartDatetime();
                    }
                    // カンバンの計画・実績の完了日時
                    if (Objects.isNull(work.getActualCompDatetime())
                            || Objects.isNull(work.getCompDatetime())
                            || work.getCompDatetime().after(work.getActualCompDatetime())) {
                        kanbanCompDatetime = work.getCompDatetime();
                    } else {
                        kanbanCompDatetime = work.getActualCompDatetime();
                    }

                    // カンバン進捗情報(工程基準)
                    if (Objects.nonNull(kwpEntity)) {
                        kwpEntities.add(kwpEntity);
                    }
                    // カンバン進捗情報(日付基準)
                    if (Objects.nonNull(kdpEntity)) {
                        kdpEntities.add(kdpEntity);
                    }
                    
                    // 個別進捗情報に休憩情報を追加する。
                    //if (Objects.nonNull(wpEntity) && !breaktimes.isEmpty()) {
                    //    // カンバンの計画・実績期間の休憩情報(個別進捗情報)を作成する。
                    //    wpBreaktimes = this.createBreaktimeWorkProgress(kanbanStartDatetime, kanbanCompDatetime, breaktimes, befProgressNo, wpPlanOrder, wpActualOrder);
                    //    wpEntities.addAll(wpBreaktimes);
                    //}

                    // 次のカンバンの進捗情報作成に移行する。
                    Date reserveDatetime = null;
                    if (Objects.nonNull(work.getDeadLine()) && Objects.nonNull(work.getReserveDays())) {
                        reserveDatetime = DateUtils.getEndOfDate(dateAddDays(work.getDeadLine(), work.getReserveDays()));
                    }

                    // 中日程ステータスを取得する
                    String kanbanStatus = this.getKanbanStatus(now, work.getDeadLine(), reserveDatetime);
                    
                    // カンバン進捗情報(工程基準)
                    kwpEntity = new KanbanWorkProgressEntity();
                    kwpEntity.setProgressNo(String.valueOf(progressNo));
                    kwpEntity.setKanbanName(work.getKanbanName());
                    kwpEntity.setGroupNo(work.getGroupNo());
//                    kwpEntity.setSeqNo(progressNo - work.getGroupNo());
                    kwpEntity.setSeqNo(seqNo);
                    kwpEntity.setKanbanId(work.getKanbanId());
                    kwpEntity.setKanbanStatus(kanbanStatus);
                    kwpEntity.setModelName(work.getModelName());
                    kwpEntity.setProjectNo(work.getProjectNo());
                    kwpEntity.setUserName(work.getUserName());

                    // カンバン進捗情報(日付基準)
                    kdpEntity = new KanbanDateProgressEntity();
                    kdpEntity.setProgressNo(String.valueOf(progressNo));
                    kdpEntity.setKanbanName(work.getKanbanName());
                    kdpEntity.setKanbanStatus(kanbanStatus);
                    kdpEntity.setModelName(work.getModelName());
                    kdpEntity.setProjectNo(work.getProjectNo());
                    kdpEntity.setUserName(work.getUserName());

                    kwpWorkIndex = 0;
                    wpPlanOrder = 1;
                    wpActualOrder = 1;
                }

                // 完了予定日の翌日
                Date deadDatetime = null;
                if (Objects.nonNull(work.getDeadLine()) && Objects.nonNull(work.getReserveDays())) {
                    deadDatetime = DateUtils.getEndOfDate(work.getDeadLine());
                }

                // 進捗ステータス
                String status = getStatus(work.getWorkStatus(), now, work.getStartDatetime(), work.getCompDatetime(), work.getActualCompDatetime(), deadDatetime);

                // カンバン進捗情報(工程基準)
                if (kwpWorkIndex < KanbanWorkProgressEntity.MAX_WORK) {
                    // 工程名
                    kwpEntity.setWorkName(kwpWorkIndex, work.getWorkName());
                    // 計画開始日付 (工程カンバンの開始予定日時 (yyyy/MM/dd))
                    kwpEntity.setStartDate(kwpWorkIndex, dateFormat.format(work.getStartDatetime()));
                    // ステータス
                    kwpEntity.setStatus(kwpWorkIndex, status);
                    // 本日フラグ (0：違う，1：本日)
                    if (todayBegin.after(work.getStartDatetime()) || todayEnd.before(work.getStartDatetime())) {
                        kwpEntity.setTodayFlg(kwpWorkIndex, "0");// 違う
                    } else {
                        kwpEntity.setTodayFlg(kwpWorkIndex, "1");// 本日
                    }

                    kwpWorkIndex++;
                }

                // カンバン進捗情報(日付基準)
                for (int i = 0; i < KanbanDateProgressEntity.MAX_WORK; i++) {
                    Date kdpDate = kdpDates.get(i);
                    Date planStartDate = DateUtils.getBeginningOfDate(work.getStartDatetime());
                    Date planCompDate = DateUtils.getEndOfDate(work.getCompDatetime());
                    if (planStartDate.equals(kdpDate) || (planStartDate.before(kdpDate) && planCompDate.after(kdpDate))) {
                        // 進捗の優先順位は「仕掛中 > 日の最初の計画済 > 日の最後の完了」
                        String oldStatus = kdpEntity.getStatus(i);
                        if (workingStatusList.contains(oldStatus)) {
                            // 進捗に仕掛中がセット済の場合、進捗は更新しない。
                            continue;
                        } else if (plannedStatusList.contains(oldStatus) && !workingStatusEnumList.contains(work.getWorkStatus())) {
                            // 進捗に計画済がセット済で、対象工程が仕掛中ではないの場合、進捗は更新しない。
                            continue;
                        }

                        // 工程名
                        kdpEntity.setWorkName(i, work.getWorkName());
                        // ステータス
                        kdpEntity.setStatus(i, status);
                    }
                }

                // 個別進捗(計画)
                Date startDate = work.getStartDatetime();
                Date compDate = work.getCompDatetime();

                if (Objects.isNull(compDate)) {
                    compDate = now;
                }

                Date originalCompDate = compDate;

                boolean isNext = true;
                while (isNext){
                    if (compDate.after(DateUtils.getEndOfDate(startDate))) {
                        // 日またぎ
                        compDate = DateUtils.getEndOfDate(startDate);
                    } else {
                        isNext = false;
                    }

                    wpEntity = new WorkProgressEntity();

                    wpEntity.setProgressNo(String.valueOf(progressNo));
                    wpEntity.setProgressType("1");
                    wpEntity.setProgressOrder(String.valueOf(wpPlanOrder));
                    wpEntity.setProgressDate(dateFormat.format(startDate));
                    wpEntity.setStartTime(timeFormat.format(startDate));
                    wpEntity.setCompTime(timeFormat.format(compDate));
                    wpEntity.setWorkName(work.getWorkName());

                    wpEntities.add(wpEntity);
                    wpPlanOrder++;

                    if (isNext) {
                        startDate = this.dateAddDays(DateUtils.getBeginningOfDate(startDate), 1);// 翌日の0時
                        compDate = originalCompDate;
                    }
                }

                befKanbanId = work.getKanbanId();
                befProgressNo = progressNo;
            }
            
            // カンバン進捗情報(工程基準)
            if (Objects.nonNull(kwpEntity)) {
                kwpEntities.add(kwpEntity);
            }
            // カンバン進捗情報(日付基準)
            if (Objects.nonNull(kdpEntity)) {
                kdpEntities.add(kdpEntity);
            }
            // 個別進捗情報に休憩情報を追加する。
            //if (Objects.nonNull(wpEntity) && !breaktimes.isEmpty()) {
            //    // カンバンの計画・実績期間の休憩情報(個別進捗情報)を作成する。
            //    wpBreaktimes = this.createBreaktimeWorkProgress(kanbanStartDatetime, kanbanCompDatetime, breaktimes, progressNo, wpPlanOrder, wpActualOrder);
            //    wpEntities.addAll(wpBreaktimes);
            //}

            // 進捗情報を出力する。
            if (this.outputProgressData(isAll)) {
                nextFromDate = now;// 次回の先頭日時
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            kwpEntities.clear();
            kdpEntities.clear();
            wpEntities.clear();
            logger.info("output end.");
        }
    }

//    /**
//     * カンバンIDに対応した progressNo を取得する。
//     *
//     * @param kanbanId カンバンID
//     * @param groupNo グループNo
//     * @return progressNo
//     */
//    private int getKanbanProgressNo(long kanbanId, int groupNo) {
//        int progressNo;
//        if (kanbanProgressNoMap.containsKey(kanbanId)) {
//            progressNo = kanbanProgressNoMap.get(kanbanId);
//        } else {
//            int seqNo = this.kwpAccessor.createSeqNo(groupNo);
//            if (MAX_SEQNO < seqNo) {
//                // MAX_SEQNO超過の場合は、出力対象外とする
//                return -1;
//            }
//            progressNo = groupNo + seqNo;
//            kanbanProgressNoMap.put(kanbanId, progressNo);
//        }
//        return progressNo;
//    }

    /**
     * 指定した日付期間の休憩情報(個別進捗情報)を作成する。
     *
     * @param startDatetime 
     * @param compDatetime 
     * @param breaktimes 休憩
     * @param progressNo No
     * @param wpPlanOrder カンバン内の順(計画)
     * @param wpActualOrder カンバン内の順(実績)
     * @return 
     */
    private List<WorkProgressEntity> createBreaktimeWorkProgress(Date startDatetime, Date compDatetime, List<BreakTimeInfoEntity> breaktimes, int progressNo, int wpPlanOrder, int wpActualOrder) {
        List<WorkProgressEntity> wpBreaktimes = new ArrayList();

        if (Objects.isNull(startDatetime)) {
            return wpBreaktimes;
        }

        if (Objects.isNull(compDatetime)) {
            compDatetime = startDatetime;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        WorkProgressEntity wpBreaktime;

        Date beginDate = DateUtils.getBeginningOfDate(startDatetime);
        Date endDate = DateUtils.getEndOfDate(compDatetime);
        Date dt = beginDate;

        while(endDate.after(dt)) {
            dt = this.dateAddDays(dt, 1);

            for (BreakTimeInfoEntity breaktime : breaktimes) {
                // 個別進捗情報(計画)の休憩情報
                wpBreaktime = new WorkProgressEntity();

                wpBreaktime.setProgressNo(String.valueOf(progressNo));
                wpBreaktime.setProgressType(String.valueOf("1"));
                wpBreaktime.setProgressOrder(String.valueOf(wpPlanOrder));
                wpBreaktime.setProgressDate(dateFormat.format(dt));
                wpBreaktime.setStartTime(timeFormat.format(breaktime.getStarttime()));
                wpBreaktime.setCompTime(timeFormat.format(breaktime.getEndtime()));
                wpBreaktime.setWorkName(breaktime.getBreaktimeName());

                wpBreaktimes.add(wpBreaktime);
                wpPlanOrder++;

                // 個別進捗情報(実績)の休憩情報
                wpBreaktime = new WorkProgressEntity();

                wpBreaktime.setProgressNo(String.valueOf(progressNo));
                wpBreaktime.setProgressType(String.valueOf("2"));
                wpBreaktime.setProgressOrder(String.valueOf(wpActualOrder));
                wpBreaktime.setProgressDate(dateFormat.format(dt));
                wpBreaktime.setStartTime(timeFormat.format(breaktime.getStarttime()));
                wpBreaktime.setCompTime(timeFormat.format(breaktime.getEndtime()));
                wpBreaktime.setWorkName(breaktime.getBreaktimeName());

                wpBreaktimes.add(wpBreaktime);
                wpActualOrder++;
            }
        }

        return wpBreaktimes;
    }

    /**
     * 進捗ステータスを取得する。
     *
     * @param workStatus 工程ステータス
     * @param now 現在日時
     * @param startDatetime 工程開始予定日時
     * @param compDatetime 工程完了予定日時
     * @param actualCompDatetime 工程完了日時
     * @param reserveDatetime 予備日の0時
     * @return 進捗ステータス (1:計画通り・未着手, 11:遅れ発生・未着手, 21:予備日・未着手)
     */
    private String getStatus(KanbanStatusEnum workStatus, Date now, Date startDatetime, Date compDatetime, Date actualCompDatetime, Date reserveDatetime) {
        switch (workStatus) {
            case PLANNED:
                return getStatusPlanned(now, startDatetime, reserveDatetime);
            case WORKING:
                return getStatusWorking(now, compDatetime, reserveDatetime);
            case SUSPEND:
                return getStatusSuspend(now, compDatetime, reserveDatetime);
            case COMPLETION:
                return getStatusCompletion(actualCompDatetime, compDatetime, reserveDatetime);
            default:
                return "";
        }
    }

    /**
     * 未着手の進捗ステータスを取得する。
     *
     * @param now 現在日時
     * @param startDatetime 工程開始予定日時
     * @param reserveDatetime 予備日の0時
     * @return 進捗ステータス (2:計画通り・作業中, 12:遅れ発生・作業中, 22:予備日・作業中)
     */
    private String getStatusPlanned(Date now, Date startDatetime, Date reserveDatetime) {
        if (Objects.nonNull(reserveDatetime) && now.after(reserveDatetime)) {
            // 現在日時が予備日以後 (遅れ)
            return "21";
        } else if (now.after(startDatetime) || now.equals(startDatetime)) {
            // 現在日時が開始予定日時以後
            return "11";
        } else {
            // 現在日時が開始予定日時より前
            return "1";
        }
    }

    /**
     * 作業中の進捗ステータスを取得する。
     *
     * @param now 現在日時
     * @param compDatetime 工程完了予定日時
     * @param reserveDatetime 予備日の0時
     * @return 進捗ステータス (3:計画通り・中断, 13:遅れ発生・中断, 23:予備日・中断)
     */
    private String getStatusWorking(Date now, Date compDatetime, Date reserveDatetime) {
        if (Objects.nonNull(reserveDatetime) && now.after(reserveDatetime)) {
            // 現在日時が予備日以後 (遅れ)
            return "22";
        } else if (now.after(compDatetime) || now.equals(compDatetime)) {
            // 現在日時が完了予定日時以後
            return "12";
        } else {
            // 現在日時が完了予定日時より前
            return "2";
        }
    }

    /**
     * 一時中断の進捗ステータスを取得する。
     *
     * @param now 現在日時
     * @param compDatetime 工程完了予定日時
     * @param reserveDatetime 予備日の0時
     * @return 進捗ステータス
     */
    private String getStatusSuspend(Date now, Date compDatetime, Date reserveDatetime) {
        if (Objects.nonNull(reserveDatetime) && now.after(reserveDatetime)) {
            // 現在日時が予備日以後 (遅れ)
            return "23";
        } else if (now.after(compDatetime) || now.equals(compDatetime)) {
            // 現在日時が完了予定日時以後
            return "13";
        } else {
            // 現在日時が完了予定日時より前
            return "3";
        }
    }

    /**
     * 作業完了の進捗ステータスを取得する。
     *
     * @param actualCompDatetime 工程完了日時
     * @param compDatetime 工程完了予定日時
     * @param reserveDatetime 予備日の0時
     * @return 進捗ステータス (4:計画通り・完了, 14:遅れ発生・完了, 24:予備日・完了)
     */
    private String getStatusCompletion(Date actualCompDatetime, Date compDatetime, Date reserveDatetime) {
        if (Objects.isNull(actualCompDatetime)
                || actualCompDatetime.before(compDatetime) || actualCompDatetime.equals(compDatetime)) {
            // 工程完了日時が完了予定日時以前
            return "4";
        } else if (Objects.isNull(reserveDatetime) || actualCompDatetime.after(compDatetime)) {
            // 工程完了日時が完了予定日時より後
            return "14";
        } else {
            // 工程完了日時が予備日以後
            return "24";
        }
    }

    /**
     * 日時に日数を加算する。
     *
     * @param date 日時
     * @param days 日数
     * @return 日数加算後の日時
     */
    private Date dateAddDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * 進捗情報を出力する。
     *
     * @param isAll
     * @return 
     */
    private boolean outputProgressData(boolean isAll) {
        boolean result = false;
        DbConnector progressDb = ProgressDbConnector.getInstance();
        try {
            if (Objects.isNull(progressDb.getConnection())) {
                progressDb.openDB();
            }

            progressDb.getConnection().setAutoCommit(false);// 自動コミット解除 (トランザクション開始)

            if (isAll) {
                logger.info("remove all.");
                kdpAccessor.removeAll();
                kwpAccessor.removeDummy();
            }

            // カンバン進捗情報(工程基準)一覧を登録する。
            if (!kwpEntities.isEmpty()) {
                kwpAccessor.add(kwpEntities);
            }
            // カンバン進捗情報(日付基準)一覧を登録する。
            if (!kdpEntities.isEmpty()) {
                kdpAccessor.add(kdpEntities);
            }
            // 個別進捗情報一覧を登録する。
            if (!wpEntities.isEmpty()) {
                wpAccessor.add(wpEntities);
            }

            if (!deleteProgressNos.isEmpty()) {
                // 削除対象のカンバンの進捗情報を削除
                kwpAccessor.remove(deleteProgressNos);
                if (!isAll) {
                    kdpAccessor.remove(deleteProgressNos);
                }
                wpAccessor.remove(deleteProgressNos);
            }

            progressDb.getConnection().commit();// コミット

            result = true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            try {
                progressDb.getConnection().rollback();// ロールバック
            } catch (SQLException sqlEx) {
                logger.fatal(sqlEx, sqlEx);
            }
        } finally {
            progressDb.closeDB();

            for (Long kanbanId : this.deleteKanbanIds) {
                if (this.kanbanProgressNoMap.containsKey(kanbanId)) {
                    this.kanbanProgressNoMap.remove(kanbanId);
                }
            }
        }
        return result;
    }

    /**
     * 中日程ステータスを取得する。
     * 
     * @param now
     * @param compDatetime
     * @param reserveDatetime
     * @return 
     */
    private String getKanbanStatus(Date now, Date compDatetime, Date reserveDatetime) {
        if (Objects.isNull(compDatetime)) {
            return "";
        }
        
        Date date = DateUtils.getEndOfDate(compDatetime);
                
        if (now.before(date) || now.equals(date)) {
            // 予定通り
            return "1";
        } else if (Objects.nonNull(reserveDatetime) && (now.before(reserveDatetime) || now.equals(reserveDatetime))) {
            // 予備日
            return "2";
        } else {
            // 遅れ
            return "3";
        }
    }
}
