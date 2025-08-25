/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.AccessTimeout;
import jakarta.ejb.EJB;
import jakarta.ejb.ScheduleExpression;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import jakarta.inject.Inject;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition;
import jp.adtekfuji.adFactory.enumerate.ActualResultDailyEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.common.ServiceConfig;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.chart.TimeLineEntity;
import jp.adtekfuji.adfactoryserver.entity.system.TroubleReportConfig;
import jp.adtekfuji.adfactoryserver.model.warehouse.WarehouseModel;
import jp.adtekfuji.adfactoryserver.service.ActualResultEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.AdIntefaceClientFacade;
import jp.adtekfuji.adfactoryserver.service.EquipmentEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.SystemResource;
import jp.adtekfuji.andon.entity.MonitorLineTimerInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * システムコアサービス
 *
 * @author s-heya
 */
@Singleton
@Startup
public class SystemService {

    private final Logger logger = LogManager.getLogger();

    private static final String DAILY_SCHEDULE = "Daily timer";
    private static final String POLLING_SCHEDULE = "Polling timer";

    /**
     * タイマーサービス
     */
    @Resource
    private TimerService timerService;

    @EJB
    private ActualResultEntityFacadeREST actualResultRest;
    @EJB
    private SystemResource systemResource;
    @EJB
    private AdIntefaceClientFacade adIntefaceFacade;
    @Inject
    private WarehouseModel warehouseModel;
    @Inject
    private AssemblyPartsModel assemblyPartsModel;
    @Inject
    private EquipmentEntityFacadeREST equipmentRest;
    
    /**
     * システムコアサービスを初期化する。
     */
    @PostConstruct
    public void initialize() {
        try {
            // 障害レポートの設定ファイルがない場合は新規作成する。
            TroubleReportConfig troubleReportConfig = TroubleReportConfig.getInstance();
            troubleReportConfig.load();

            // 不良内容CSVファイルを読み込む。
            DefectReasonManager defectReasonManager = DefectReasonManager.getInstance();
            defectReasonManager.load();

            // ライン管理を初期化
            this.initializeLine();

            // 保存期間が経過したファイルを削除
            this.cleanupHistory();

            // 1日毎の処理を実行するためのタイマーをセットする。
            this.setDailyTimer();
            // ポーリングタイマーをセットする。
            this.setPollingTimer();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 1日毎の処理を実行するためのタイマーをセットする。
     */
    private void setDailyTimer() {
        try {
            int hour = 0;
            int min = 0;

            try {
                String resetTime = FileManager.getInstance().getSystemProperties().getProperty(Constants.RESET_TIME, null);
                if (!StringUtils.isEmpty(resetTime)) {
                    String[] values = resetTime.split(":");
                    if (values.length > 0) {
                        hour = Integer.parseInt(values[0]);
                    }

                    if (values.length > 1) {
                        min = Integer.parseInt(values[1]);
                    }
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }

            ScheduleExpression schedule = new ScheduleExpression();
            schedule.hour(hour).minute(min).second(0);

            TimerConfig timerConfig = new TimerConfig();
            timerConfig.setPersistent(false);
            timerConfig.setInfo(DAILY_SCHEDULE);

            this.timerService.createCalendarTimer(schedule, timerConfig);
            logger.info("daily timer set: hour={}, minute={}", hour, min);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ポーリングタイマーをセットする。
     */
    private void setPollingTimer() {
        try {
            boolean enablePartsTrace = this.assemblyPartsModel.getEnablePartsTrace();
            if (!enablePartsTrace) {
                logger.info("polling timer not set: enablePartsTrace={}", enablePartsTrace);
                return;
            }

            long pollingInterval = ServiceConfig.getInstance().getPollingInterval() * 60 * 1000;

            TimerConfig timerConfig = new TimerConfig();
            timerConfig.setPersistent(false);
            timerConfig.setInfo(POLLING_SCHEDULE);

            this.timerService.createIntervalTimer(0, pollingInterval, timerConfig);
            logger.info("polling timer set: pollingInterval={}", pollingInterval);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * スケジュール処理
     *
     * @param timer
     */
    @Timeout
    @AccessTimeout(value=0)
    public void timerTimeout(Timer timer) {
        try {
            String timerInfo = (String) timer.getInfo();
            switch (timerInfo) {
                case DAILY_SCHEDULE:
                    // 1日毎の処理を実行する。
                    this.dailyTask();
                    break;
                case POLLING_SCHEDULE:
                    // ポーリング間隔毎の処理を実行する。
                    this.pollingTask();
                    break;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 1日毎の処理を実行する。
     *
     * @throws Exception 
     */
    private void dailyTask() throws Exception {
        logger.info("dailyTask");

        // ライン管理を初期化する。
        this.initializeLine();

        // 倉庫案内のバッチ処理を実行する。
        this.warehouseModel.doBatch();

        // 保存期間が経過したファイルを削除する。
        this.cleanupHistory();
    }

    /**
     * ポーリング間隔毎の処理を実行する。
     *
     * @throws Exception 
     */
    private void pollingTask() throws Exception {
        logger.info("pollingTask");

        // 使用部品モデルのバッチ処理を実行する。
        this.assemblyPartsModel.doBatch();
    }

    /**
     * ライン管理を初期化する。
     *
     * @throws Exception
     */
    private void initializeLine() throws Exception {
        try {
            logger.info("initializeLine start.");

            LineManager lineManager = LineManager.getInstance();
            Date now = new Date();
            Date fromDate = DateUtils.getBeginningOfDate(now);
            Date toDate = DateUtils.getEndOfDate(now);

            for (AndonMonitorLineProductSetting setting : lineManager.getMonitorSettings()) {
                if (setting.isFollowStart()) {
                    Set<Long> equipIds = new HashSet<>();
                    for (WorkEquipmentSetting workEquipment : setting.getWorkEquipmentCollection()) {
                        equipIds.addAll(workEquipment.getEquipmentIds());
                    }

                    Date date = null;
                    if (StringUtils.isEmpty(setting.getModelName())) {
                        ActualSearchCondition condition = new ActualSearchCondition()
                                .equipmentList(new ArrayList<>(equipIds))
                                .statusList(Arrays.asList(KanbanStatusEnum.WORKING))
                                .resultDailyEnum(ActualResultDailyEnum.DAILY);

                        // 指定した日付単位の最初の工程実績情報を取得する。
                        ActualResultEntity entity = this.actualResultRest.findFirstActualResult(condition, null);
                        if (Objects.nonNull(entity) && Objects.nonNull(entity.getImplementDatetime())) {
                            date = entity.getImplementDatetime();
                        }
                    } else {
                        // モデル名あり
                        // ライン(設備ID一覧)・モデル名・日時範囲を指定して、タイムライン情報(VIEW)を取得する。
                        TimeLineEntity entity = this.actualResultRest.getFirstTimeLine(new ArrayList<>(equipIds), setting.getModelName(), fromDate, toDate);
                        if (Objects.nonNull(entity) && Objects.nonNull(entity.getImplementDatetime())) {
                            date = entity.getImplementDatetime();
                        }
                    }

                    if (Objects.nonNull(date)) {
                        Date startWorkTime = Date.from(setting.getStartWorkTime().atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
                        if (date.after(startWorkTime)) {
                            setting.setTodayStartTime(date);
                        }
                    } else {
                        setting.setTodayStartTime(new Date(0L));
                    }
                } else {
                    Date startWorkTime = Date.from(setting.getStartWorkTime().atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
                    setting.setTodayStartTime(startWorkTime);
                }
                
                MonitorLineTimerInfoEntity lineTimer;
                if (lineManager.containsLineTimer(setting.getMonitorId())) {
                    lineTimer = lineManager.getLineTimer(setting.getMonitorId());
                    lineManager.removeLineTimer(setting.getMonitorId());
                } else {
                    lineTimer = new MonitorLineTimerInfoEntity();
                }

                lineTimer.setCycle(0);
                lineTimer.delivered().clear();

                switch (setting.getCompCountType()) {
                    case EQUIPMENT:
                        int cycle = Integer.MAX_VALUE;
                        for (WorkEquipmentSetting workEquipment : setting.getWorkEquipmentCollection()) {
                            if (!workEquipment.getEquipmentIds().isEmpty()) {
                                long id = workEquipment.getEquipmentIds().get(0);

                                ReportOutSearchCondition condition = new ReportOutSearchCondition()
                                        .equipmentIdList(workEquipment.getEquipmentIds())
                                        .statusList(Arrays.asList(KanbanStatusEnum.COMPLETION))
                                        .fromDate(fromDate)
                                        .toDate(toDate)
                                        .modelName(setting.getModelName());

                                int actualNum = Integer.parseInt(this.actualResultRest.countReportOut(condition, null));

                                lineTimer.delivered().put(id, actualNum);
                                logger.info("Delivered: {},{}", id, actualNum);

                                cycle = Math.min(cycle, actualNum);
                            }
                        }
                        
                        if (cycle < Integer.MAX_VALUE) {
                            lineTimer.setCycle(cycle);
                            lineManager.setLineTimer(setting.getMonitorId(), lineTimer);
                        }
                        break;

                    case WORK:
                        if (Objects.nonNull(setting.getWorkCollection())) {
                            Set<Long> workIds = setting.getWorkCollection().stream().flatMap(o -> o.getWorkIds().stream()).collect(Collectors.toSet());
                            int actualNum = this.actualResultRest.getActualNum(setting.getCompCountType(), new ArrayList<>(workIds), DateUtils.getBeginningOfDate(now), DateUtils.getEndOfDate(now), setting.getModelName());
                            if (actualNum > 0) {
                                lineTimer.setCycle(actualNum);
                                lineManager.setLineTimer(setting.getMonitorId(), lineTimer);
                            }
                        }   
                        break;

                    case KANBAN:
                    default:
                        List<Long> equipmentIds = this.equipmentRest.findChildIds(setting.getLineId());
                        equipmentIds.add(setting.getLineId());
                        Long result = this.actualResultRest.getLineProduct(equipmentIds, DateUtils.getBeginningOfDate(now), DateUtils.getEndOfDate(now), setting.getModelName());
                        int actualNum = Integer.parseInt(String.valueOf(result));
                        if (actualNum > 0) {
                            lineTimer.setCycle(actualNum);
                            lineManager.setLineTimer(setting.getMonitorId(), lineTimer);
                        }
                        break;
                }
            }

            // リセット通知
            this.adIntefaceFacade.notice(new ResetCommand());
        } finally {
            logger.info("initializeLine end.");
        }
    }

    /**
     * 保存期間が経過したファイルを削除する。
     */
    private void cleanupHistory() {
        try {
            logger.info("cleanupHistory start.");

            // 保存期間が経過したファイルを削除する
            FileManager fileManager = FileManager.getInstance();
            String days = fileManager.getSystemProperties().getProperty("importHistoryDays", "15");
            Date targetDate = org.apache.commons.lang3.time.DateUtils.addDays(new Date(), -Integer.parseInt(days));

            List<File> files = fileManager.listFile(FileManager.Data.Import, "");
            for (File file : files) {
                Date date = new Date(file.lastModified());
                if (date.before(targetDate)) {
                    file.delete();
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("cleanupHistory end.");
        }
    }
}
