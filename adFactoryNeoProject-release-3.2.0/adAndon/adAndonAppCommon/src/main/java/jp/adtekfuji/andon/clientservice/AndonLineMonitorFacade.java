/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.clientservice;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;
import jakarta.ws.rs.PathParam;
import jp.adtekfuji.andon.entity.DefectEntity;
import jp.adtekfuji.andon.entity.EstimatedTimeInfoEntity;
import jp.adtekfuji.andon.entity.LineTimerControlRequest;
import jp.adtekfuji.andon.entity.MonitorClockInfoEntity;
import jp.adtekfuji.andon.entity.MonitorEquipmentPlanNumInfoEntity;
import jp.adtekfuji.andon.entity.MonitorEquipmentStatusInfoEntity;
import jp.adtekfuji.andon.entity.MonitorLineStatusInfoEntity;
import jp.adtekfuji.andon.entity.MonitorLineTaktInfoEntity;
import jp.adtekfuji.andon.entity.MonitorLineTimerInfoEntity;
import jp.adtekfuji.andon.entity.MonitorPlanDeviatedInfoEntity;
import jp.adtekfuji.andon.entity.MonitorPlanNumInfoEntity;
import jp.adtekfuji.andon.entity.MonitorReasonNumInfoEntity;
import jp.adtekfuji.andon.entity.MonitorTitleInfoEntity;
import jp.adtekfuji.andon.entity.MonitorWorkPlanNumInfoEntity;
import jp.adtekfuji.andon.entity.ProductivityEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 休憩設定取得用RESTクラス
 *
 * @author e-mori
 */
public class AndonLineMonitorFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String PATH_MONITOR = "/monitor/%d/line";
    private final static String PATH_TITLE = "/title";
    private final static String PATH_CLOCK = "/clock";
    private final static String PATH_DAILY_PLAN = "/daily/plan";
    private final static String PATH_MONTHLY_PLAN = "/monthly/plan";
    private final static String PATH_DAILY_DEVIATED = "/daily/deviated";
    private final static String PATH_MONTHLY_DEVIATED = "/monthly/deviated";
    private final static String PATH_EQUIP_STATUS = "/daily/equipment/status";
    private final static String PATH_DAILY_STATUS = "/daily/status";
    private final static String PATH_DAILY_EQUIP_PLAN = "/daily/equipment/plan";
    private final static String PATH_DAILY_DELAY_NUM = "/daily/delay/count";
    private final static String PATH_DAILY_INTERRUPT_NUM = "/daily/interrupt/count";
    private final static String PATH_DAILY_WORK = "/daily/work";
    private final static String PATH_WORK_PLAN = "/daily/work/plan";

    //private final static String PATH_MONITOR_LINE = "/monitor/line/%d";
    private final static String PATH_DAILY_TAKTTIME = "/daily/takttime";
    private final static String PATH_DAILY_TIMER = "/daily/timer";

    private final static String PATH_DAILY_WORK_PRODUCTIVITY = "/actual/daily/work/productivity?id=";
    private final static String PATH_DAILY_EQUIPMENT_PRODUCTIVITY = "/actual/daily/equipment/productivity?id=";
    private final static String PATH_DAILY_WORK_DEFECT = "/actual/daily/work/defect?id=";
    private final static String PATH_EQUIPMENT_STATUS = "/monitor/%d/line/equipment/status?id=";
    private final static String PATH_ESTIMATED = "/monitor/%d/line/estimated?";

    private final String QUERY_ID = "&id=";

    public AndonLineMonitorFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * タイトルフレーム情報取得
     *
     * @param monitorId
     * @return
     */
    public MonitorTitleInfoEntity getTitleInfo(Long monitorId) {
        logger.info("getTitleInfo:{}", monitorId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_TITLE);
            return (MonitorTitleInfoEntity) restClient.find(sb.toString(), MonitorTitleInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 日時フレーム情報取得
     *
     * @param monitorId
     * @return
     */
    public MonitorClockInfoEntity getClockInfo(Long monitorId) {
        logger.info("getClockInfo:{}", monitorId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_CLOCK);
            return (MonitorClockInfoEntity) restClient.find(sb.toString(), MonitorClockInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 当日計画数実績フレーム情報取得
     *
     * @param monitorId
     * @return
     */
    public MonitorPlanNumInfoEntity getDailyPlanInfo(Long monitorId) {
        logger.info("getDailyPlanInfo:{}", monitorId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_DAILY_PLAN);
            return (MonitorPlanNumInfoEntity) restClient.find(sb.toString(), MonitorPlanNumInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 当月計画数実績フレーム情報取得
     *
     * @param monitorId
     * @return
     */
    public MonitorPlanNumInfoEntity getMonthlyPlanInfo(Long monitorId) {
        logger.info("getMonthlyPlanInfo:{}", monitorId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_MONTHLY_PLAN);
            return (MonitorPlanNumInfoEntity) restClient.find(sb.toString(), MonitorPlanNumInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 日単位生産数進捗フレーム情報取得
     *
     * @param monitorId
     * @return
     */
    public MonitorPlanDeviatedInfoEntity getDailyDeviatedInfo(Long monitorId) {
        logger.info("getDailyDeviatedInfo:{}", monitorId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_DAILY_DEVIATED);
            return (MonitorPlanDeviatedInfoEntity) restClient.find(sb.toString(), MonitorPlanDeviatedInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 月単位生産数進捗フレーム情報取得
     *
     * @param monitorId
     * @return
     */
    public MonitorPlanDeviatedInfoEntity getMonthlyDeviatedInfo(Long monitorId) {
        logger.info("getMonthlyDeviatedInfo:{}", monitorId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_MONTHLY_DEVIATED);
            return (MonitorPlanDeviatedInfoEntity) restClient.find(sb.toString(), MonitorPlanDeviatedInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 指定ラインの日単位の設備ステータス情報を取得
     *
     * @param monitorId
     * @return
     */
    public List<MonitorEquipmentStatusInfoEntity> getDailyEquipmentStatusInfo(@PathParam("id") Long monitorId) {
        logger.info("getDailyEquipmentStatusInfo:{}", monitorId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_EQUIP_STATUS);
            return restClient.find(sb.toString(), new GenericType<List<MonitorEquipmentStatusInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 指定ラインの日単位のライン全体ステータス情報を取得
     *
     * @param monitorId
     * @return
     */
    public MonitorLineStatusInfoEntity getDailyLineStatusInfo(Long monitorId) {
        logger.info("getDailyLineStatusInfo:{}", monitorId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_DAILY_STATUS);
            return (MonitorLineStatusInfoEntity) restClient.find(sb.toString(), MonitorLineStatusInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 指定ラインの日単位の設備生産数情報を取得
     *
     * @param monitorId
     * @return
     */
    public List<MonitorEquipmentPlanNumInfoEntity> getDailyEquipmentPlanInfo(@PathParam("id") Long monitorId) {
        logger.info("getDailyEquipmentStatusInfo:{}", monitorId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_DAILY_EQUIP_PLAN);
            return restClient.find(sb.toString(), new GenericType<List<MonitorEquipmentPlanNumInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 指定モニタの日単位の遅延理由の数を取得
     *
     * @param monitorId
     * @return
     */
    public List<MonitorReasonNumInfoEntity> getDailyDelayReasonInfo(Long monitorId) {
        logger.info("getDailyDelayReasonInfo:{}", monitorId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_DAILY_DELAY_NUM);
            return restClient.find(sb.toString(), new GenericType<List<MonitorReasonNumInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 指定モニタの日単位の中断理由の数を取得
     *
     * @param monitorId
     * @return
     */
    public List<MonitorReasonNumInfoEntity> getDailyInterruptReasonInfo(Long monitorId) {
        logger.info("getDailyInterruptReasonInfo:{}", monitorId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_DAILY_INTERRUPT_NUM);
            return restClient.find(sb.toString(), new GenericType<List<MonitorReasonNumInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 指定ラインの日単位のタクトタイム情報を取得
     *
     * @param lineId
     * @return
     */
    public MonitorLineTaktInfoEntity getDailyTakttimeInfo(Long lineId) {
        logger.info("getDailyTakttimeInfo:{}", lineId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, lineId));
            sb.append(PATH_DAILY_TAKTTIME);
            return (MonitorLineTaktInfoEntity) restClient.find(sb.toString(), MonitorLineTaktInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 指定ラインの日単位のカウントダウン情報を取得
     *
     * @param monitorId
     * @return
     */
    public MonitorLineTimerInfoEntity getDailyTimerInfo(Long monitorId) {
        logger.info("getDailyTimerInfo: {}", monitorId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_DAILY_TIMER);
            return (MonitorLineTimerInfoEntity) restClient.find(sb.toString(), MonitorLineTimerInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 指定ラインの日単位のカウントダウンを制御
     *
     * @param request LineTimerControlRequest
     */
    public void putDailyTimerControlRequest(LineTimerControlRequest request) {
        logger.info("putDailyTimerControlRequest:{}", request);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, request.getMonitorId()));
            sb.append(PATH_DAILY_TIMER);
            restClient.put(sb.toString(), request);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 日別工程計画実績数を取得する。
     *
     * @param monitorId
     * @param pluginName
     * @return
     * @throws Exception
     */
    public MonitorWorkPlanNumInfoEntity getDailyMonitorWorkPlanNum(Long monitorId, String pluginName) throws Exception {
        try {
            logger.info("getDailyMonitorWorkPlanNum start:{},{}", monitorId, pluginName);

            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_DAILY_WORK);
            sb.append("?pluginName=");
            sb.append(RestClient.encode(pluginName));

            return (MonitorWorkPlanNumInfoEntity) restClient.find(sb.toString(), MonitorWorkPlanNumInfoEntity.class);
        }
        finally {
            logger.info("getDailyMonitorWorkPlanNum end.");
        }
    }

    /**
     * 工程別計画実績数を取得する。
     *
     * @param monitorId
     * @return
     * @throws Exception
     */
    public List<MonitorWorkPlanNumInfoEntity> getWorkPlanNum(Long monitorId) throws Exception {
        try {
            logger.info("getWorkPlanNum start:{}", monitorId);

            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_MONITOR, monitorId));
            sb.append(PATH_WORK_PLAN);

            return (List<MonitorWorkPlanNumInfoEntity>) restClient.find(sb.toString(), new GenericType<List<MonitorWorkPlanNumInfoEntity>>() {
            });
        } finally {
            logger.info("getWorkPlanNum end.");
        }
    }

    /**
     * 工程別生産情報を取得する。
     *
     * @param workIds
     * @return
     */
    public List<ProductivityEntity> getDailyWorkProductivity(List<Long> workIds) {
        try {
            logger.info("getDailyWorkProductivity start.");

            if (workIds.isEmpty()) {
                return new ArrayList<>();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(PATH_DAILY_WORK_PRODUCTIVITY);
            sb.append(workIds.get(0));
            for (int ii = 1; ii < workIds.size(); ii++) {
                sb.append(QUERY_ID);
                sb.append(workIds.get(ii));
            }

            return restClient.find(sb.toString(), new GenericType<List<ProductivityEntity>>() {});
        }
        finally {
            logger.info("getDailyWorkProductivity end.");
        }
    }

    /**
     * 設備別生産情報を取得する。
     *
     * @param equipmentIds
     * @return
     */
    public List<ProductivityEntity> getDailyEquipmentProductivity(List<Long> equipmentIds) {
        try {
            logger.info("getDailyEquipmentProductivity start.");

            if (equipmentIds.isEmpty()) {
                return new ArrayList<>();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(PATH_DAILY_EQUIPMENT_PRODUCTIVITY);
            sb.append(equipmentIds.get(0));
            for (int ii = 1; ii < equipmentIds.size(); ii++) {
                sb.append(QUERY_ID);
                sb.append(equipmentIds.get(ii));
            }

            return restClient.find(sb.toString(), new GenericType<List<ProductivityEntity>>() {});
        }
        finally {
            logger.info("getDailyEquipmentProductivity end.");
        }
    }

    /**
     * 工程別不具合情報を取得する。
     *
     * @param workIds
     * @return
     */
    public List<DefectEntity> getDailyWorkDefect(List<Long> workIds) {
        try {
            logger.info("getDailyWorkDefect start.");

            if (workIds.isEmpty()) {
                return new ArrayList<>();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(PATH_DAILY_WORK_DEFECT);
            sb.append(workIds.get(0));
            for (int ii = 1; ii < workIds.size(); ii++) {
                sb.append(QUERY_ID);
                sb.append(workIds.get(ii));
            }

            return restClient.find(sb.toString(), new GenericType<List<DefectEntity>>() {});
        }
        finally {
            logger.info("getDailyWorkDefect end.");
        }
    }

    /**
     * 設備ステータスを取得する。
     *
     * @param monitorId
     * @param equipmentIds
     * @return
     */
    public List<MonitorEquipmentStatusInfoEntity> getEquipmentStatus(Long monitorId, List<Long> equipmentIds) {
        try {
            logger.info("getEquipmentStatus start.");

            if (equipmentIds.isEmpty()) {
                return new ArrayList<>();
            }

            StringBuilder sb = new StringBuilder();

            sb.append(String.format(PATH_EQUIPMENT_STATUS, monitorId));
            sb.append(equipmentIds.get(0));
            for (int ii = 1; ii < equipmentIds.size(); ii++) {
                sb.append(QUERY_ID);
                sb.append(equipmentIds.get(ii));
            }

            return restClient.find(sb.toString(), new GenericType<List<MonitorEquipmentStatusInfoEntity>>() {});
        }
        finally {
            logger.info("getEquipmentStatus end.");
        }
    }

    /**
     * 作業終了予想時間を取得する。
     *
     * @param monitorId
     * @return
     */
    public EstimatedTimeInfoEntity getEstimatedTime(long monitorId) {
         try {
            logger.info("getEstimatedTime start.");

            StringBuilder sb = new StringBuilder();
            sb.append(String.format(PATH_ESTIMATED, monitorId));

            return (EstimatedTimeInfoEntity) this.restClient.find(sb.toString(), EstimatedTimeInfoEntity.class);
        }
        finally {
            logger.info("getEstimatedTime end.");
        }
    }
}
