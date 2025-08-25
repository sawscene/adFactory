/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import adtekfuji.utility.DateUtils;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.scene.paint.Color;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.inject.Inject;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition;
import jp.adtekfuji.adFactory.enumerate.ActualResultDailyEnum;
import jp.adtekfuji.adFactory.enumerate.CompCountTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.master.BreaktimeEntity;
import jp.adtekfuji.adfactoryserver.entity.master.DisplayedStatusEntity;
import jp.adtekfuji.adfactoryserver.entity.view.ReportOutEntity;
import jp.adtekfuji.adfactoryserver.model.EquipmentRuntimeData;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.model.LineManager;
import jp.adtekfuji.adfactoryserver.service.ActualResultEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.DisplayedStatusEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.EquipmentEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.WorkKanbanEntityFacadeREST;
import jp.adtekfuji.andon.entity.EstimatedTimeInfoEntity;
import jp.adtekfuji.andon.entity.MonitorEquipmentPlanNumInfoEntity;
import jp.adtekfuji.andon.entity.MonitorEquipmentStatusInfoEntity;
import jp.adtekfuji.andon.entity.MonitorLineStatusInfoEntity;
import jp.adtekfuji.andon.entity.MonitorLineTimerInfoEntity;
import jp.adtekfuji.andon.entity.MonitorPlanDeviatedInfoEntity;
import jp.adtekfuji.andon.entity.MonitorPlanNumInfoEntity;
import jp.adtekfuji.andon.entity.MonitorReasonNumInfoEntity;
import jp.adtekfuji.andon.entity.MonitorWorkPlanNumInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import jp.adtekfuji.andon.property.WorkSetting;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 進捗モニタ情報処理
 *
 * @author ke.yokoi
 */
public class AggregateMonitorInfoFacade {

    @Inject
    private WorkKanbanEntityFacadeREST workKanbanRest;
    @Inject
    private ActualResultEntityFacadeREST actualResultRest;
    @Inject
    private EquipmentEntityFacadeREST equipmentRest;
    @Inject
    private DisplayedStatusEntityFacadeREST statusRest;

    private final Logger logger = LogManager.getLogger();
    private final EquipmentRuntimeData equipmentCallRuntimeData = EquipmentRuntimeData.getInstance();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    /**
     * コンストラクタ
     */
    public AggregateMonitorInfoFacade() {
    }

    /**
     * 指定ライン(設備)の子設備の設備ID一覧を取得する。(指定した設備IDを含む)
     *
     * @param lineId ラインの設備ID
     * @return 設備ID一覧
     */
    private List<Long> getEquipmentIdCollection(Long lineId) {
        List<Long> equipmenIds = new ArrayList();
        equipmenIds.add(lineId);

        // 設備IDを指定して、子設備の設備ID一覧を取得する。
        List<Long> childIds = this.equipmentRest.findChildIds(lineId);
        if (!childIds.isEmpty()) {
            equipmenIds.addAll(childIds);
        }

        return equipmenIds;
    }

    /**
     * ステータス表示マスタ一覧を取得する。
     *
     * @return ステータス表示マスタ一覧
     */
    private Map<StatusPatternEnum, DisplayedStatusEntity> getStatuses() {
        Map<StatusPatternEnum, DisplayedStatusEntity> statuses = new HashMap();
        for (DisplayedStatusEntity s : this.statusRest.findAll(null)) {
            statuses.put(s.getStatusName(), s);
        }
        return statuses;
    }

    /**
     * 指定ラインの日単位の生産数情報を取得する。
     *
     * @param setting
     * @return
     */
    @Lock(LockType.READ)
    public MonitorPlanNumInfoEntity getDailyPlanInfo(AndonMonitorLineProductSetting setting) {
        logger.info("getDailyPlanInfo: lineId={}", setting.getLineId());

        // 指定ライン(設備)の子設備の設備ID一覧を取得する。(指定した設備IDを含む)
        List<Long> equipmentIds = this.getEquipmentIdCollection(setting.getLineId());
        Date now = new Date();

        // 当日の実績数を取得
        Long result = this.actualResultRest.getLineProduct(equipmentIds, DateUtils.getBeginningOfDate(now), DateUtils.getEndOfDate(now), setting.getModelName());
        Integer actualNum = Integer.parseInt(String.valueOf(result));

        MonitorPlanNumInfoEntity planNumInfo = new MonitorPlanNumInfoEntity().planNum(setting.getDailyPlanNum()).actualNum(actualNum).unit(setting.getUnit());
        return planNumInfo;
    }

    /**
     * 指定した期間の生産数情報を取得する。(対象設備を巡回した数をカウント)
     *
     * @param setting 進捗モニタ設定
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @return 指定した期間の生産数情報 (計画数は未設定)
     */
    @Lock(LockType.READ)
    public MonitorPlanNumInfoEntity getPlanNumInfoEquip(AndonMonitorLineProductSetting setting, Date fromDate, Date toDate) {
        logger.info("getPlanNumInfoEquip: lineId={}, from={}, to={}", setting.getLineId(), fromDate, toDate);

        int actualNum = 0;
        if (Objects.nonNull(setting.getWorkEquipmentCollection())) {
            // 対象設備毎に実績の完了数合計を取得する。
            List<Integer> compNums = new ArrayList();
            for (WorkEquipmentSetting workEquipment : setting.getWorkEquipmentCollection()) {
                int compNum = this.actualResultRest.getTargetCompletedNum(CompCountTypeEnum.EQUIPMENT, workEquipment.getEquipmentIds(), fromDate, toDate, setting.getModelName());
                compNums.add(compNum);
            }

            // 完了数リストの最小値が、対象設備を巡回した数となる。
            Optional<Integer> opt = compNums.stream().min((a, b) -> a.compareTo(b));
            if (opt.isPresent()) {
                actualNum = opt.get();
            }
        }

        MonitorPlanNumInfoEntity planNumInfo = new MonitorPlanNumInfoEntity().actualNum(actualNum).unit(setting.getUnit());
        return planNumInfo;
    }

    /**
     * 指定した期間の生産数情報を取得する。(対象工程を巡回した数をカウント)
     *
     * @param setting 進捗モニタ設定
     * @param fromDate 日時範囲の先頭
     * @param toDate 日時範囲の末尾
     * @return 指定した期間の生産数情報 (計画数は未設定)
     */
    @Lock(LockType.READ)
    public MonitorPlanNumInfoEntity getPlanNumInfoWork(AndonMonitorLineProductSetting setting, Date fromDate, Date toDate) {
        logger.info("getPlanNumInfoWork: lineId={}, from={}, to={}", setting.getLineId(), fromDate, toDate);

        int actualNum = 0;
        if (Objects.nonNull(setting.getWorkCollection())) {
            // 対象工程毎に実績の完了数合計を取得する。
            List<Integer> compNums = new ArrayList();
            for (WorkSetting work : setting.getWorkCollection()) {
                int compNum = this.actualResultRest.getTargetCompletedNum(CompCountTypeEnum.WORK, work.getWorkIds(), fromDate, toDate, setting.getModelName());
                compNums.add(compNum);
            }

            // 完了数リストの最小値が、対象工程を巡回した数となる。
            Optional<Integer> opt = compNums.stream().min((a, b) -> a.compareTo(b));
            if (opt.isPresent()) {
                actualNum = opt.get();
            }
        }

        MonitorPlanNumInfoEntity planNumInfo = new MonitorPlanNumInfoEntity().actualNum(actualNum).unit(setting.getUnit());
        return planNumInfo;
    }

    /**
     * 指定ラインの月単位の生産数情報を取得する。
     *
     * @param setting 進捗モニタ設定
     * @return 当月の生産数情報
     */
    @Lock(LockType.READ)
    public MonitorPlanNumInfoEntity getMonthlyPlanInfo(AndonMonitorLineProductSetting setting) {
        logger.info("getMonthlyPlanInfo: lineId={}", setting.getLineId());

        // 指定ライン(設備)の子設備の設備ID一覧を取得する。(指定した設備IDを含む)
        List<Long> equipmentIds = this.getEquipmentIdCollection(setting.getLineId());
        Date now = new Date();

        // ライン(設備ID一覧)・モデル名・日時範囲を指定して、生産数を取得する。
        Long result = this.actualResultRest.getLineProduct(equipmentIds, DateUtils.getBeginningOfMonth(now), DateUtils.getEndOfMonth(now), setting.getModelName());
        Integer actualNum = Integer.parseInt(String.valueOf(result));

        MonitorPlanNumInfoEntity planNumInfo = new MonitorPlanNumInfoEntity().planNum(setting.getMontlyPlanNum()).actualNum(actualNum).unit(setting.getUnit());
        return planNumInfo;
    }

    /**
     * 指定ラインの日単位の生産進捗情報を取得する。
     *
     * @param setting 進捗モニタ設定
     * @param now 日付
     * @return 指定した日付の生産数情報
     */
    @Lock(LockType.READ)
    public MonitorPlanDeviatedInfoEntity getDailyDeviatedInfo(AndonMonitorLineProductSetting setting, Date now) {
        logger.info("getDailyDeviatedInfo: lineId={}, now={}", setting.getLineId(), now);

        if (Objects.isNull(setting.getCompCountType())) {
            setting.setCompCountType(CompCountTypeEnum.KANBAN);
        }

        Date fromDate = DateUtils.getBeginningOfDate(now);
        Date toDate = DateUtils.getEndOfDate(now);

        MonitorPlanNumInfoEntity planInfo;
        switch (setting.getCompCountType()) {
            case EQUIPMENT:// 対象設備を巡回した数をカウント
                planInfo = this.getPlanNumInfoEquip(setting, fromDate, toDate);
                planInfo.setPlanNum(setting.getDailyPlanNum());
                break;
            case WORK:// 対象工程を巡回した数をカウント
                planInfo = this.getPlanNumInfoWork(setting, fromDate, toDate);
                planInfo.setPlanNum(setting.getDailyPlanNum());
                break;
            case KANBAN:// 完了したカンバン数をカウント
            default:
                // 指定ラインの日単位の生産数情報を取得
                planInfo = this.getDailyPlanInfo(setting);
                break;
        }

        // 計画数
        int planNum = planInfo.getPlanNum();

        // カンバン完了数
        int actualNum = planInfo.getActualNum();

        // 開始時間・完了時間
        Date startWorkTime = this.getStartWorkTime(setting);
        LocalTime endLocalTime = setting.getEndWorkTime();
        Date endWorkTime = Date.from(endLocalTime.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
                
        if (setting.isFollowStart()) {
            long workTime = setting.getLineTakt().toSecondOfDay() * 1000L * planNum;
            Date endTime = new Date(startWorkTime.getTime() + workTime);
            List<BreakTimeInfoEntity> breakTimes = BreaktimeUtil.getAppropriateBreaktimes(setting.getBreaktimes(), startWorkTime, endTime);
            endWorkTime = BreaktimeUtil.getEndTimeWithBreak(breakTimes, startWorkTime, endTime);
            endLocalTime = DateUtils.toLocalTime(endWorkTime);
            logger.info("StartTime={}, EndTime={}", startWorkTime, endWorkTime);
        }
    
        if (endLocalTime.isBefore(LocalTime.now())) {
            now = DateUtils.toDate(LocalDate.now(), endLocalTime);
        }
 
        // 当日の作業時間
        long sumWork = BreaktimeUtil.getDiffTime(setting.getBreaktimes(), startWorkTime, endWorkTime) / 1000;

        long sumNowtime = 0;
        if (startWorkTime.before(new Date())) {
            // 現在までの実作業時間
            sumNowtime = BreaktimeUtil.getDiffTime(setting.getBreaktimes(), startWorkTime, now) / 1000;
        }

        // 1秒あたりの生産数
        double planNumPerSeconds = (double) planNum / (double) sumWork;

        // 現在までの完了予定数
        int nowPlanNum = (int) (planNumPerSeconds * sumNowtime);

        // 進捗： 完了数 - 現在までの完了予定数
        int deviatedNum = actualNum - nowPlanNum;

        // 進捗時間 (予定数が0より大きい場合のみ計算)
        long deviatedTime = 0;
        if (planNum > 0) {
            deviatedTime = (long) ((double) deviatedNum * ((double) sumWork / (double) planNum));
        }

        // 注意・警告判定
        Color fontColor = Color.WHITE;
        Color backColor = Color.BLACK;

        double deviatedPar = 0.0;
        if (deviatedNum < 0) {
            // 遅れている数が、現在までの完了予定数の何パーセントか
            deviatedPar = ((double) -deviatedNum / (double) nowPlanNum) * 100.0;
        }

        if (setting.getWarningRetentionParcent() <= deviatedPar) {
            // 警告
            fontColor = setting.getWarningFontColor();
            backColor = setting.getWarningBackColor();
        } else if (setting.getCautionRetentionParcent() <= deviatedPar) {
            // 注意
            fontColor = setting.getCautionFontColor();
            backColor = setting.getCautionBackColor();
        }

        logger.info("DailyDeviatedInfo: lineId={}, planNum={}, actualNum={}, sumWork={}, sumNowtime={}, deviatedNum={}, startWorkTime={}",
                setting.getLineId(), planNum, actualNum, sumWork, sumNowtime, deviatedNum, startWorkTime);

        MonitorPlanDeviatedInfoEntity entity = new MonitorPlanDeviatedInfoEntity()
                .planDeviatedNum(deviatedNum).planDeviatedTime(deviatedTime)
                .timeFormat(this.timeFormat.toPattern()).fontColor(fontColor.toString()).backColor(backColor.toString())
                .unit(planInfo.getUnit());

        // 作業結果
        if (LicenseManager.getInstance().isLicenceOption(LicenseOptionType.LineTimer.getName()) && setting.isAutoCountdown()) {
            LineManager lineManager = LineManager.getInstance();
            if (lineManager.containsLineTimer(setting.getMonitorId())) {
                MonitorLineTimerInfoEntity lineTimer = lineManager.getLineTimer(setting.getMonitorId());

                if (0 < lineTimer.getCycle() && 0 < lineTimer.getCompTime()) {
                    Date end = new Date(startWorkTime.getTime() + (setting.getLineTakt().toSecondOfDay() * lineTimer.getCycle() * 1000L));
                    List<BreaktimeEntity> breakTimes = BreakTimeUtils.getBreakInWork2(setting.getBreaktimes(), startWorkTime, end);
                    Date plan = BreakTimeUtils.getEndTimeWithBreak(breakTimes, startWorkTime, end);
                    entity.setWorkResult((plan.getTime() - lineTimer.getCompTime()) / 1000);
                }
            }
        }

        return entity;
    }

    /**
     * 指定ラインの月単位の生産進捗情報を取得する。
     *
     * @param setting 進捗モニタ設定
     * @param now 日付
     * @return 指定した日付を含む月の生産進捗情報
     */
    @Lock(LockType.READ)
    public MonitorPlanDeviatedInfoEntity getMonthlyDeviatedInfo(AndonMonitorLineProductSetting setting, Date now) {
        logger.info("getMonthlyDeviatedInfo: lineId={}, now={}", setting.getLineId(), now);

        if (Objects.isNull(setting.getCompCountType())) {
            setting.setCompCountType(CompCountTypeEnum.KANBAN);
        }

        Date fromDate = DateUtils.getBeginningOfMonth(now);
        Date toDate = DateUtils.getEndOfMonth(now);

        MonitorPlanNumInfoEntity planInfo;
        switch (setting.getCompCountType()) {
            case EQUIPMENT:// 対象設備を巡回した数をカウント
                planInfo = this.getPlanNumInfoEquip(setting, fromDate, toDate);
                planInfo.setPlanNum(setting.getMontlyPlanNum());
                break;
            case WORK:// 対象工程を巡回した数をカウント
                planInfo = this.getPlanNumInfoWork(setting, fromDate, toDate);
                planInfo.setPlanNum(setting.getMontlyPlanNum());
                break;
            case KANBAN:// 完了したカンバン数をカウント
            default:
                // 指定ラインの月単位の生産数情報を取得
                planInfo = this.getMonthlyPlanInfo(setting);
                break;
        }

        // 月の計画数
        int planNum = planInfo.getPlanNum();

        // カンバン完了数
        int actualNum = planInfo.getActualNum();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        // 今月の日数
        int workDays = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1).getActualMaximum(Calendar.DAY_OF_MONTH);
        // 今月の今日までの日数
        int nowDays = calendar.get(Calendar.DAY_OF_MONTH);

        // DayOfWeekは月曜日が「1」で日曜日が「7」なので、Calendarの曜日に変換する。
        List<Integer> weekDays = new ArrayList();
        for (DayOfWeek dayOfWeek : setting.getWeekdays()) {
            int weekValue = dayOfWeek.getValue() + 1;
            if (weekValue > 7) {
                weekValue = 1;
            }
            weekDays.add(weekValue);
        }

        int restDays = 0;// 休日数
        int befRestDays = 0;// 指定日までの休日数
        for (int loop = 1; loop <= workDays; loop++) {
            Calendar day = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), loop);
            int week = day.get(Calendar.DAY_OF_WEEK);
            if (weekDays.contains(week)) {
                restDays++;
                if (nowDays >= loop) {
                    befRestDays++;
                }
            }
        }

        // 今月の稼働日数
        workDays -= restDays;
        // 今月の今日までの稼働日数
        nowDays -= befRestDays;

        // 一日当たりの完了予定数
        double planNumPerDay = (double) planNum / (double) workDays;

        // 前日までの完了予定数
        double pastPlanNum = planNumPerDay * (double) (nowDays - 1);

        // 作業開始時間
        Date startWorkTime = this.getStartWorkTime(setting);
        // 休憩時間
        long sumBreaktime = 0;
        for (BreakTimeInfoEntity breaktime : setting.getBreaktimes()) {
            LocalTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(breaktime.getStarttime().getTime()), ZoneId.systemDefault()).toLocalTime();
            LocalTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(breaktime.getEndtime().getTime()), ZoneId.systemDefault()).toLocalTime();
            sumBreaktime += ChronoUnit.SECONDS.between(start, end);
        }

        Long sumWork = ChronoUnit.SECONDS.between(startWorkTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime(), setting.getEndWorkTime()) - sumBreaktime;

        long sumNowtime = 0;
        if (startWorkTime.before(new Date())) {
            // 現在までの実作業時間
            sumNowtime = BreaktimeUtil.getDiffTime(setting.getBreaktimes(), startWorkTime, now) / 1000;
        }

        // 1秒あたりの生産数
        double planNumPerSeconds = (double) planNumPerDay / (double) sumWork;

        // 現在までの完了予定数
        int nowPlanNum = (int) ((planNumPerSeconds * sumNowtime) + pastPlanNum);

        // 進捗： 完了数 - 現在までの完了予定数
        int deviatedNum = actualNum - nowPlanNum;

        // 注意・警告判定.
        Color fontColor = Color.WHITE;
        Color backColor = Color.BLACK;

        double deviatedPar = 0.0;
        if (deviatedNum < 0) {
            // 遅れている数が、現在までの完了予定数の何パーセントか
            deviatedPar = ((double) -deviatedNum / (double) nowPlanNum) * 100.0;
        }

        if (setting.getWarningRetentionParcent() <= deviatedPar) {
            // 警告
            fontColor = setting.getWarningFontColor();
            backColor = setting.getWarningBackColor();
        } else if (setting.getCautionRetentionParcent() <= deviatedPar) {
            // 注意
            fontColor = setting.getCautionFontColor();
            backColor = setting.getCautionBackColor();
        }
        logger.info("MonthlyDeviatedInfo: lineId={}, planNum={}, actualNum={}, workDays={}, nowDays={}, nowPlanNum={}, deviatedNum={}",
                setting.getLineId(), planNum, actualNum, workDays, nowDays, nowPlanNum, deviatedNum);

        MonitorPlanDeviatedInfoEntity planProgressInfo = new MonitorPlanDeviatedInfoEntity()
                .planDeviatedNum(deviatedNum).planDeviatedTime(0L)
                .timeFormat(null).fontColor(fontColor.toString()).backColor(backColor.toString())
                .unit(planInfo.getUnit());

        return planProgressInfo;
    }

    /**
     * 指定ラインの日単位の設備ステータス情報一覧を取得する。
     *
     * @param setting 進捗モニタ設定
     * @return 設備ステータス情報一覧
     */
    @Lock(LockType.READ)
    public List<MonitorEquipmentStatusInfoEntity> getDailyEquipmentStatusInfo(AndonMonitorLineProductSetting setting) {
        logger.info("getDailyEquipmentStatusInfo: lineId={}", setting.getLineId());

        Map<StatusPatternEnum, DisplayedStatusEntity> statuses = getStatuses();
        List<MonitorEquipmentStatusInfoEntity> equipmentStatusInfos = new ArrayList();
        Date now = new Date();

        String modelName = null;
        if (Objects.nonNull(setting.getModelName()) || !setting.getModelName().isEmpty()) {
            modelName = setting.getModelName();
        }

        for (WorkEquipmentSetting workEquip : setting.getWorkEquipmentCollection()) {
            DisplayedStatusEntity allStatus = statuses.get(StatusPatternEnum.COMP_NORMAL);

            // 呼び出し中かどうかを取得する。
            boolean isCall = false;
            for (Long equipId : workEquip.getEquipmentIds()) {
                if (this.equipmentCallRuntimeData.checkCall(equipId)) {
                    isCall = true;
                    break;
                }
            }

            if (isCall) {
                allStatus = statuses.get(StatusPatternEnum.CALLING);
            } else if (Objects.nonNull(setting.getBreaktimes()) && BreaktimeUtil.isBreaktime(setting.getBreaktimes(), now)) {
                allStatus = statuses.get(StatusPatternEnum.BREAK_TIME);
            } else {
                for (Long equipId : workEquip.getEquipmentIds()) {
                    // 工程の最後の実績を取得
                    ActualSearchCondition lastConditon = new ActualSearchCondition()
                            .modelName(modelName)
                            .equipmentList(Arrays.asList(equipId))
                            .resultDailyEnum(ActualResultDailyEnum.DAILY);
                    ActualResultEntity actual = this.actualResultRest.findLastActualResult(lastConditon, null);
                    if (Objects.isNull(actual)) {
                        continue;
                    }

                    // 工程の最後の工程カンバンを取得
                    WorkKanbanEntity workKanban = this.workKanbanRest.find(actual.getWorkKanbanId(), null);

                    // 実績取得
                    ActualSearchCondition farst2Conditon = new ActualSearchCondition()
                            .kanbanId(actual.getKanbanId())
                            .workKanbanList(Arrays.asList(actual.getWorkKanbanId()))
                            .statusList(Arrays.asList(KanbanStatusEnum.WORKING))
                            .resultDailyEnum(ActualResultDailyEnum.DAILY);
                    ActualResultEntity actual1 = this.actualResultRest.findFirstActualResult(farst2Conditon, null);

                    ActualSearchCondition last2Conditon = new ActualSearchCondition()
                            .kanbanId(workKanban.getKanbanId())
                            .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId()))
                            .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
                            .resultDailyEnum(ActualResultDailyEnum.DAILY);
                    ActualResultEntity actual2 = this.actualResultRest.findLastActualResult(last2Conditon, null);
                    Date actualStart = Objects.nonNull(actual1) ? actual1.getImplementDatetime() : null;
                    Date actualEnd = Objects.nonNull(actual2) ? actual2.getImplementDatetime() : null;

                    StatusPatternEnum pattern = StatusPatternEnum.getStatusPattern(
                            workKanban.getKanbanStatus(), workKanban.getWorkStatus(), workKanban.getStartDatetime(), workKanban.getCompDatetime(), actualStart, actualEnd, now);
                    allStatus = statuses.get(StatusPatternEnum.compareStatus(allStatus.getStatusName(), pattern));

                    logger.info("workKanban:{},{},{},{},{} status:{}", workKanban.getWorkKanbanId(), workKanban.getWorkStatus(), workKanban.getKanbanName(), workKanban.getWorkflowName(), workKanban.getWorkName(), allStatus.getStatusName());
                }
            }

            logger.info("daily equipment status: lineId:{}, order:{}, title:{}, status:{}",
                    setting.getLineId(), workEquip.getOrder(), workEquip.getTitle(), allStatus.getStatusName());

            equipmentStatusInfos.add(new MonitorEquipmentStatusInfoEntity()
                    .order(workEquip.getOrder()).name(workEquip.getTitle())
                    .fontColor(allStatus.getFontColor()).backColor(allStatus.getBackColor()));
        }
        return equipmentStatusInfos;
    }

    /**
     * 設備ID一覧を指定して、設備ステータス情報一覧を取得する。
     *
     * @param setting 進捗モニタ設定
     * @param equipmentIds 設備ID一覧
     * @return 設備ステータス情報一覧
     */
    @Lock(LockType.READ)
    public List<MonitorEquipmentStatusInfoEntity> getEquipmentStatus(final AndonMonitorLineProductSetting setting, final List<Long> equipmentIds) {
        logger.info("getDailyEquipmentStatusInfo: lineId={}, equipmentIds={}", setting.getLineId(), equipmentIds);

        List<MonitorEquipmentStatusInfoEntity> entities = new ArrayList();
        Map<StatusPatternEnum, DisplayedStatusEntity> statuses = this.getStatuses();

        Date now = new Date();

        String modelName = null;
        if (Objects.nonNull(setting.getModelName()) || !setting.getModelName().isEmpty()) {
            modelName = setting.getModelName();
        }

        for (Long equipmentId : equipmentIds) {
            DisplayedStatusEntity status = statuses.get(StatusPatternEnum.PLAN_NORMAL);
            Boolean called = this.equipmentCallRuntimeData.checkCall(equipmentId);

            if (called) {
                status = statuses.get(StatusPatternEnum.CALLING);

            } else if (Objects.nonNull(setting.getBreaktimes()) && BreaktimeUtil.isBreaktime(setting.getBreaktimes(), now)) {
                status = statuses.get(StatusPatternEnum.BREAK_TIME);

            } else {
                ActualSearchCondition conditon = new ActualSearchCondition()
                        .modelName(modelName)
                        .equipmentList(Arrays.asList(equipmentId))
                        .resultDailyEnum(ActualResultDailyEnum.DAILY);
                ActualResultEntity actual = this.actualResultRest.findLastActualResult(conditon, null);

                if (Objects.nonNull(actual)) {
                    switch (actual.getActualStatus()) {
                        case WORKING:
                            status = statuses.get(StatusPatternEnum.WORK_NORMAL);
                            break;
                        case INTERRUPT:
                            status = statuses.get(StatusPatternEnum.INTERRUPT_NORMAL);
                            break;
                        case SUSPEND:
                            status = statuses.get(StatusPatternEnum.SUSPEND_NORMAL);
                            break;
                        case COMPLETION:
                            status = statuses.get(StatusPatternEnum.COMP_NORMAL);
                            break;
                    }
                }
            }

            entities.add(new MonitorEquipmentStatusInfoEntity()
                    .status(status.getStatusName()).equipmentId(equipmentId)
                    .fontColor(status.getFontColor()).backColor(status.getBackColor()).called(called));
        }

        return entities;
    }

    /**
     * 指定ラインの日単位のライン全体ステータス情報を取得する。
     *
     * @param setting 進捗モニタ設定
     * @return ライン全体ステータス情報
     */
    @Lock(LockType.READ)
    public MonitorLineStatusInfoEntity getDailyStatusInfo(AndonMonitorLineProductSetting setting) {
        logger.info("getDailyStatusInfo: lineId={}", setting.getLineId());

        Date now = new Date();
        Map<StatusPatternEnum, DisplayedStatusEntity> statuses = getStatuses();

        // 指定ライン(設備)の子設備の設備ID一覧を取得する。(指定した設備IDを含む)
        List<Long> equipIds = this.getEquipmentIdCollection(setting.getLineId());

        // 呼び出し中かどうかを取得する。
        boolean isCall = false;
        for (Long equipId : equipIds) {
            if (this.equipmentCallRuntimeData.checkCall(equipId)) {
                isCall = true;
                break;
            }
        }

        DisplayedStatusEntity allStatus = statuses.get(StatusPatternEnum.COMP_NORMAL);
        if (isCall) {
            allStatus = statuses.get(StatusPatternEnum.CALLING);
        } else if (Objects.nonNull(setting.getBreaktimes()) && BreaktimeUtil.isBreaktime(setting.getBreaktimes(), now)) {
            allStatus = statuses.get(StatusPatternEnum.BREAK_TIME);
        } else {
            // 計画取得
            Date fromDate = DateUtils.getBeginningOfDate(now);
            Date toDate = DateUtils.getEndOfDate(now);
            KanbanSearchCondition kanbanSearchCondition = new KanbanSearchCondition()
                    .equipmentList(equipIds).skipFlag(false)
                    .statusList(Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND, KanbanStatusEnum.INTERRUPT, KanbanStatusEnum.COMPLETION))
                    .fromDate(fromDate).toDate(toDate);
            List<WorkKanbanEntity> workKanbans = this.workKanbanRest.searchWorkKanban(kanbanSearchCondition, null, null, null);

            for (WorkKanbanEntity workKanban : workKanbans) {
                // 実績取得
                ActualSearchCondition farstConditon = new ActualSearchCondition()
                        .kanbanId(workKanban.getKanbanId())
                        .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId()))
                        .statusList(Arrays.asList(KanbanStatusEnum.WORKING))
                        .resultDailyEnum(ActualResultDailyEnum.DAILY);

                ActualResultEntity actual1 = this.actualResultRest.findFirstActualResult(farstConditon, null);
                ActualSearchCondition lastConditon = new ActualSearchCondition()
                        .kanbanId(workKanban.getKanbanId())
                        .workKanbanList(Arrays.asList(workKanban.getWorkKanbanId()))
                        .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
                        .resultDailyEnum(ActualResultDailyEnum.DAILY);

                ActualResultEntity actual2 = this.actualResultRest.findLastActualResult(lastConditon, null);
                Date actualStart = Objects.nonNull(actual1) ? actual1.getImplementDatetime() : null;
                Date actualEnd = Objects.nonNull(actual2) ? actual2.getImplementDatetime() : null;

                StatusPatternEnum pattern = StatusPatternEnum.getStatusPattern(workKanban.getKanbanStatus(), workKanban.getWorkStatus(), workKanban.getStartDatetime(), workKanban.getCompDatetime(), actualStart, actualEnd, now);
                allStatus = statuses.get(StatusPatternEnum.compareStatus(allStatus.getStatusName(), pattern));

                logger.info("workKanban:{},{},{},{},{} status:{}", workKanban.getWorkKanbanId(), workKanban.getWorkStatus(), workKanban.getKanbanName(), workKanban.getWorkflowName(), workKanban.getWorkName(), allStatus.getStatusName());
            }
        }

        logger.info("daily all status: lineId:{}, status:{}", setting.getLineId(), allStatus.getStatusName());

        MonitorLineStatusInfoEntity lineStatusInfo = new MonitorLineStatusInfoEntity()
                .status(Objects.nonNull(allStatus.getNotationName()) ? allStatus.getNotationName() : "").fontColor(allStatus.getFontColor()).backColor(allStatus.getBackColor())
                .melodyFilePath(allStatus.getMelodyPath()).melodyReplay(allStatus.getMelodyRepeat());

        return lineStatusInfo;
    }

    /**
     * 指定ラインの日単位の設備生産数情報一覧を取得する。
     *
     * @param setting 進捗モニタ設定
     * @return 設備生産数情報一覧
     */
    @Lock(LockType.READ)
    public List<MonitorEquipmentPlanNumInfoEntity> getDailyEquipmentPlanInfo(AndonMonitorLineProductSetting setting) {
        List<MonitorEquipmentPlanNumInfoEntity> equipmentPlanInfos = new ArrayList();
        Date now = new Date();
        Date fromDate = DateUtils.getBeginningOfDate(now);
        Date toDate = DateUtils.getEndOfDate(now);

        for (WorkEquipmentSetting workEquip : setting.getWorkEquipmentCollection()) {
            // 計画数取得
            int planNum = setting.getDailyPlanNum();

            // 実績情報の完成数を集計
            int actualNum = this.actualResultRest.sumByEquipmentId(workEquip.getEquipmentIds(), Arrays.asList(KanbanStatusEnum.COMPLETION), setting.getModelName(), now);
            
            //中断ステータスの場合は中断時間の計算へ.
            KanbanSearchCondition kanbanSearchCondition = new KanbanSearchCondition()
                    .equipmentList(workEquip.getEquipmentIds()).skipFlag(false)
                    .statusList(Arrays.asList(KanbanStatusEnum.SUSPEND, KanbanStatusEnum.INTERRUPT))
                    .fromDate(fromDate).toDate(toDate)
                    .modelName(setting.getModelName());

            List<WorkKanbanEntity> workKanbans = this.workKanbanRest.searchWorkKanban(kanbanSearchCondition, null, null, null);

            Date suspendTime = null;
            for (WorkKanbanEntity workKanban : workKanbans) {
                //中断時間取得.
                ReportOutSearchCondition condition2 = new ReportOutSearchCondition()
                        .equipmentIdList(workEquip.getEquipmentIds())
                        .kanbanIdList(Arrays.asList(workKanban.getKanbanId()))
                        .workKanbanIdList(Arrays.asList(workKanban.getWorkKanbanId()))
                        .statusList(Arrays.asList(KanbanStatusEnum.SUSPEND, KanbanStatusEnum.INTERRUPT))
                        .fromDate(fromDate)
                        .toDate(toDate)
                        .modelName(setting.getModelName());

                List<ReportOutEntity> actuals = this.actualResultRest.searchReportOut(condition2, null, null, null);
                for (ReportOutEntity actual : actuals) {
                    if (Objects.isNull(suspendTime) || actual.getImplementDatetime().before(suspendTime)) {
                        suspendTime = actual.getImplementDatetime();
                    }
                }
            }
            logger.info("daily equip plan: lineId:{}, order:{}, planNum:{}, actualNum:{}. suspendTime:{}", setting.getLineId(), workEquip.getOrder(), planNum, actualNum, suspendTime);
            equipmentPlanInfos.add(new MonitorEquipmentPlanNumInfoEntity()
                    .order(workEquip.getOrder()).planNum(planNum).actualNum(actualNum).suspendTime(suspendTime));
        }
        return equipmentPlanInfos;
    }

    /**
     * 指定モニタの日単位の遅延理由回数情報一覧を取得する。
     *
     * @param setting 進捗モニタ設定
     * @return 理由回数情報一覧
     */
    @Lock(LockType.READ)
    public List<MonitorReasonNumInfoEntity> getDailyDelayReasonInfo(AndonMonitorLineProductSetting setting) {
        List<MonitorReasonNumInfoEntity> reasonNumInfos = new ArrayList();

        // 指定ライン(設備)の子設備の設備ID一覧を取得する。(指定した設備IDを含む)
        List<Long> equipIds = getEquipmentIdCollection(setting.getLineId());

        Date now = new Date();
        Date fromDate = DateUtils.getBeginningOfDate(now);
        Date toDate = DateUtils.getEndOfDate(now);
        //実績数取得.
        if (Objects.nonNull(setting.getDelayReasonCollection())) {
            for (String reason : setting.getDelayReasonCollection()) {
                ReportOutSearchCondition condition = new ReportOutSearchCondition()
                        .equipmentIdList(equipIds)
                        .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
                        .fromDate(fromDate)
                        .toDate(toDate)
                        .delayReason(reason)
                        .modelName(setting.getModelName());

                int actualNum = Integer.parseInt(this.actualResultRest.countReportOut(condition, null));
                
                logger.info("daily delay reason num: lineId:{}, reason:{}, actualNum:{}", setting.getLineId(), reason, actualNum);
                reasonNumInfos.add(new MonitorReasonNumInfoEntity(reason, actualNum));
            }
        }
        return reasonNumInfos;
    }

    /**
     * 指定モニタの日単位の中断理由回数情報一覧を取得する。
     *
     * @param setting 進捗モニタ設定
     * @return 理由回数情報一覧
     */
    @Lock(LockType.READ)
    public List<MonitorReasonNumInfoEntity> getDailyInterruptReasonInfo(AndonMonitorLineProductSetting setting) {
        List<MonitorReasonNumInfoEntity> reasonNumInfos = new ArrayList();

        // 指定ライン(設備)の子設備の設備ID一覧を取得する。(指定した設備IDを含む)
        List<Long> equipIds = getEquipmentIdCollection(setting.getLineId());

        Date now = new Date();
        Date fromDate = DateUtils.getBeginningOfDate(now);
        Date toDate = DateUtils.getEndOfDate(now);
        //実績数取得.
        if (Objects.nonNull(setting.getInterruptReasonCollection())) {
            for (String reason : setting.getInterruptReasonCollection()) {
                ReportOutSearchCondition condition = new ReportOutSearchCondition()
                        .equipmentIdList(equipIds)
                        .statusList(Arrays.asList(KanbanStatusEnum.INTERRUPT, KanbanStatusEnum.SUSPEND))
                        .fromDate(fromDate)
                        .toDate(toDate)
                        .interruptReason(reason)
                        .modelName(setting.getModelName());

                int actualNum = Integer.parseInt(this.actualResultRest.countReportOut(condition, null));
                logger.info("daily interrupt num: lineId:{}, reason:{}, actualNum:{}", setting.getLineId(), reason, actualNum);
                reasonNumInfos.add(new MonitorReasonNumInfoEntity(reason, actualNum));
            }
        }
        return reasonNumInfos;
    }

    /**
     * 日別工程計画実績数を取得する。
     *
     * @param setting 進捗モニタ設定
     * @param pluginName プラグイン名
     * @return 工程計画実績数情報
     */
    @Lock(LockType.READ)
    public MonitorWorkPlanNumInfoEntity getDailyWorkPlanNum(AndonMonitorLineProductSetting setting, String pluginName) {
        // 該当するプラグイン名の対象工程情報を取得する。
        Optional<WorkSetting> optional = setting.getWorkCollection().stream()
                .filter(o -> StringUtils.equals(o.getPluginName(), pluginName))
                .findFirst();
        if (!optional.isPresent()) {
            return new MonitorWorkPlanNumInfoEntity();
        }

        MonitorWorkPlanNumInfoEntity entity = new MonitorWorkPlanNumInfoEntity(optional.get());

        // 当日の実績数を取得する。
        Date now = new Date();
        //Date fromDate = DateUtils.getBeginningOfDate(now);
        //Date toDate = DateUtils.getEndOfDate(now);

        //ReportOutSearchCondition condition = new ReportOutSearchCondition()
        //        .workIdList(entity.getWorkIds())
        //        .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
        //        .fromDate(fromDate)
        //        .toDate(toDate)
        //        .modelName(setting.getModelName())
        //        .distinctWorkKanban(true);

        // 最後の実績情報の完成数を取得
        //int actualNum = Integer.parseInt(this.actualResultRest.countReportOut(condition, null));
        
        // 実績情報の完成数を集計
        int actualNum = this.actualResultRest.sumByWorkId(entity.getWorkIds(), Arrays.asList(KanbanStatusEnum.COMPLETION), setting.getModelName(), now);
        entity.setActualNum(actualNum);

        if (setting.getUseDailyPlanNum()) {
            entity.setPlanNum(setting.getDailyPlanNum());
        }

        return entity;
    }

    /**
     * 工程別計画実績数を取得する。
     * 
     * @param setting 進捗モニタ設定
     * @return
     */
    @Lock(LockType.READ)
    public List<MonitorWorkPlanNumInfoEntity> getWorkPlanNum(AndonMonitorLineProductSetting setting) {

        final Date now = new Date();
        final Date from = DateUtils.getBeginningOfDate(now);
        final Date to = DateUtils.getEndOfDate(now);

        final List<MonitorWorkPlanNumInfoEntity> entities = setting.getWorkCollection().stream()
                .map(work -> new MonitorWorkPlanNumInfoEntity(work))
                .collect(Collectors.toList());

        for (MonitorWorkPlanNumInfoEntity entity : entities) {
            //final ReportOutSearchCondition condition = new ReportOutSearchCondition()
            //        .workIdList(entity.getWorkIds())
            //        .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
            //        .fromDate(from)
            //        .toDate(to)
            //        .modelName(setting.getModelName())
            //        .distinctWorkKanban(true);

            // 最後の実績情報の完成数を取得
            //final int actualNum = Integer.parseInt(this.actualResultRest.countReportOut(condition, null));

            // 実績情報の完成数を集計
            int actualNum = this.actualResultRest.sumByWorkId(entity.getWorkIds(), Arrays.asList(KanbanStatusEnum.COMPLETION), setting.getModelName(), now);

            entity.setActualNum(actualNum);

            if (setting.getUseDailyPlanNum()) {
                entity.setPlanNum(setting.getDailyPlanNum());
            }
        }

        return entities;
    }

    /**
     * 作業終了予想時間情報を取得する。
     *
     * @param setting 進捗モニタ設定
     * @param now 日付
     * @return 作業終了予想時間情報
     */
    @Lock(LockType.READ)
    public EstimatedTimeInfoEntity getEstimatedTime(AndonMonitorLineProductSetting setting, Date now) {
        Date estimatedTime = null;
        int remaining;

        LineManager lineManager = LineManager.getInstance();
        if (lineManager.containsLineTimer(setting.getMonitorId())
                && (Objects.isNull(setting.getModelName()) || setting.getModelName().isEmpty())) {
            MonitorLineTimerInfoEntity lineTimer = lineManager.getLineTimer(setting.getMonitorId());
            remaining = setting.getDailyPlanNum() - lineTimer.getCycle();
            
            if (0 < lineTimer.getCompTime()) {
                estimatedTime = new Date(lineTimer.getCompTime());
            }
            
        } else {
            Date fromDate = DateUtils.getBeginningOfDate(now);
            Date toDate = DateUtils.getEndOfDate(now);

            if (Objects.isNull(setting.getCompCountType())) {
                setting.setCompCountType(CompCountTypeEnum.KANBAN);
            }

            MonitorPlanNumInfoEntity planNum;
            switch (setting.getCompCountType()) {
                case EQUIPMENT:// 対象設備を巡回した数をカウント
                    // 指定した期間の生産数情報を取得する。(対象設備を巡回した数をカウント)
                    planNum = this.getPlanNumInfoEquip(setting, fromDate, toDate);
                    planNum.setPlanNum(setting.getDailyPlanNum());
                    break;
                case WORK:// 対象工程を巡回した数をカウント
                    // 指定した期間の生産数情報を取得する。(対象工程を巡回した数をカウント)
                    planNum = this.getPlanNumInfoWork(setting, fromDate, toDate);
                    planNum.setPlanNum(setting.getDailyPlanNum());
                    break;
                case KANBAN:// 完了したカンバン数をカウント
                default:
                    // 指定ラインの日単位の生産数情報を取得する。
                    planNum = this.getDailyPlanInfo(setting);
                    break;
            }

            remaining = setting.getDailyPlanNum() - planNum.getActualNum();
        }

        if (0 < remaining) {
            Date endTime = new Date(now.getTime() + (setting.getLineTakt().toSecondOfDay() * remaining * 1000L));
            List<BreaktimeEntity> breakTimes = BreakTimeUtils.getBreakInWork2(setting.getBreaktimes(), now, endTime);
            estimatedTime = BreakTimeUtils.getEndTimeWithBreak(breakTimes, now, endTime);
        }
        
        // 作業終了予定時間
        Date startWorkTime = Date.from(setting.getStartWorkTime().atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
        Date endWorkTime = new Date(startWorkTime.getTime() + (setting.getLineTakt().toSecondOfDay() * setting.getDailyPlanNum() * 1000L));
        List<BreaktimeEntity> breakTimes = BreakTimeUtils.getBreakInWork2(setting.getBreaktimes(), startWorkTime, endWorkTime);
        Date scheduledTime = BreakTimeUtils.getEndTimeWithBreak(breakTimes, startWorkTime, endWorkTime);

        EstimatedTimeInfoEntity entity = new EstimatedTimeInfoEntity();
        entity.setScheduledTime(scheduledTime);
        entity.setEstimatedTime(estimatedTime);
        entity.setRemaining(remaining);
        return entity;
    }

    /**
     * 作業開始時間を取得する。
     *
     * @param setting 進捗モニタ設定
     * @return 作業開始時間
     */
    @Lock(LockType.READ)
    private Date getStartWorkTime(AndonMonitorLineProductSetting setting) {
        Date startWorkTime = Date.from(setting.getStartWorkTime().atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
        if (setting.isFollowStart()) {
            if (setting.getTodayStartTime().after(startWorkTime)) {
                startWorkTime = setting.getTodayStartTime();
            }
        }
        return startWorkTime;
    }

    public void setWorkKanbanRest(WorkKanbanEntityFacadeREST workKanbanRest) {
        this.workKanbanRest = workKanbanRest;
    }

    public void setActualResultRest(ActualResultEntityFacadeREST actualResultRest) {
        this.actualResultRest = actualResultRest;
    }

    public void setEquipmentRest(EquipmentEntityFacadeREST equipmentRest) {
        this.equipmentRest = equipmentRest;
    }

    public void setStatusRest(DisplayedStatusEntityFacadeREST statusRest) {
        this.statusRest = statusRest;
    }
}
