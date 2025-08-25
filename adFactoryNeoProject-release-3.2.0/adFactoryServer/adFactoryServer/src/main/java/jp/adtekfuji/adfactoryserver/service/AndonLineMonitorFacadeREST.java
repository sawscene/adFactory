/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.DateUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.CompCountTypeEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.model.LineManager;
import jp.adtekfuji.adfactoryserver.utility.AggregateLineInfoFacade;
import jp.adtekfuji.adfactoryserver.utility.AggregateMonitorInfoFacade;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
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
import jp.adtekfuji.andon.enumerate.AndonMonitorTypeEnum;
import jp.adtekfuji.andon.enumerate.HorizonAlignmentTypeEnum;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.utility.AndonMonitorLineProductSettingFileAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 進捗モニタ情報REST
 *
 * @author ke.yokoi
 */
@Singleton
@Path("monitor")
public class AndonLineMonitorFacadeREST {

    @Inject
    private AggregateMonitorInfoFacade aggregateMonitorInfoFacade;

    @Inject
    private AggregateLineInfoFacade aggregateLineInfoFacade;

    @EJB
    private AdIntefaceClientFacade adIntefaceFacade;

    private final Logger logger = LogManager.getLogger();

    private final AndonMonitorLineProductSettingFileAccessor fileAccessor = new AndonMonitorLineProductSettingFileAccessor();

    private static final String DEF_DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    /**
     * コンストラクタ
     */
    public AndonLineMonitorFacadeREST() {
    }

    /**
     * テストのセットアップ
     */
    public void setUpTest() {
        this.aggregateMonitorInfoFacade = new AggregateMonitorInfoFacade();
        this.aggregateLineInfoFacade = new AggregateLineInfoFacade();
        this.adIntefaceFacade = new AdIntefaceClientFacade();
    }

    /**
     * 進捗モニタ設定情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @return 進捗モニタ設定情報
     */
    @Lock(LockType.READ)
    public AndonMonitorLineProductSetting getLineSetting(Long monitorId) {
        logger.info("getLineSetting: monitorId={}", monitorId);
        try {
            LineManager lineManager = LineManager.getInstance();
            if (lineManager.containsLineSetting(monitorId)) {
                return lineManager.getLineSetting(monitorId);
            }
            AndonMonitorLineProductSetting setting = this.fileAccessor.load(monitorId);
            lineManager.setLineSetting(monitorId, setting);
            return setting;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 進捗モニタ設定情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return Response(進捗モニタ設定情報を含む)
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/config")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response findConfig(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("findConfig: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニタの設備IDを指定して、進捗モニタ設定ファイルのパスを取得する。
            String filePath = this.fileAccessor.getFilePath(monitorId);

            File file = new File(filePath);

            // ファイルが存在しない場合、デフォルトの進捗モニタ設定を作成して保存する。
            if (!file.exists()) {
                AndonMonitorLineProductSetting setting = AndonMonitorLineProductSetting.create();
                if(LicenseManager.getInstance().isLicenceOption(LicenseOptionType.LiteOption.getName())
                    && !LicenseManager.getInstance().isLicenceOption(LicenseOptionType.MonitorSettingEditor.getName())) {
                    setting.setMonitorType(AndonMonitorTypeEnum.LITE_MONITOR);
                }
                this.fileAccessor.save(monitorId, setting);
            }

            // 進捗モニタ設定ファイルを読み込んで、戻り値にセットする。
            return Response.ok(new FileInputStream(filePath)).build();
        } catch (FileNotFoundException ex) {
            logger.fatal(ex, ex);
            return Response.status(Response.Status.NOT_FOUND).build();
        } finally {
            logger.info("findConfig end.");
        }
    }

    /**
     * 進捗モニタ設定情報を更新する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param value 書き込み内容
     * @param authId 認証ID
     * @return
     */
    @PUT
    @Path("{id}/line/config")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updateConfig(@PathParam("id") Long monitorId, String value, @QueryParam("authId") Long authId) {
        logger.info("updateConfig: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニタの設備IDを指定して、進捗モニタ設定ファイルのパスを取得する。
            String filePath = this.fileAccessor.getFilePath(monitorId);

            // 進捗モニタ設定ファイルを更新する。
            File file = new File(filePath);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                writer.write(value);
            }

            // 進捗モニタの設備IDを指定して、進捗モニタ設定ファイルを読み込む。
            AndonMonitorLineProductSetting setting = this.fileAccessor.load(monitorId);
            // ラインマネージャの進捗モニタ設定を更新する。
            LineManager.getInstance().setLineSetting(monitorId, setting);

            // 進捗モニタにリセットコマンドを通知する。
            this.adIntefaceFacade.notice(new ResetCommand(monitorId));

            return Response.ok().build();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
            return Response.status(Response.Status.NOT_MODIFIED).build();
        } finally {
            logger.info("updateConfig end.");
        }
    }

    /**
     * 進捗モニタ設定情報を削除する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("{id}/line/config")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response deleteConfig(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("deleteConfig: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニタの設備IDを指定して、進捗モニタ設定を削除する。
            this.fileAccessor.remove(monitorId);
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定ラインのタイトル情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return タイトル情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/title")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public MonitorTitleInfoEntity getTitleInfo(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getTitleInfo: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // TODO: 水平位置は進捗モニタ設定画面に未実装のため、左寄せ固定とする。
            MonitorTitleInfoEntity titleInfo = new MonitorTitleInfoEntity().title(setting.getTitle()).horizonAlignment(HorizonAlignmentTypeEnum.ALIGN_LEFT);
            return titleInfo;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定ラインの時計情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return 時計情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/clock")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public MonitorClockInfoEntity getClockInfo(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getClockInfo: monitorId={}, authId={}", monitorId, authId);
        try {
            // TODO: 日時フォーマットは進捗モニタ設定画面に未実装のため、固定とする。
            SimpleDateFormat sdf = new SimpleDateFormat(DEF_DATETIME_FORMAT);

            // TODO: 水平位置は進捗モニタ設定画面に未実装のため、右寄せ固定とする。
            MonitorClockInfoEntity clockInfo = new MonitorClockInfoEntity().clockFormat(sdf.toPattern()).horizonAlignment(HorizonAlignmentTypeEnum.ALIGN_RIGHT);
            return clockInfo;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定ラインの当日の生産数情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return 生産数情報(当日分)
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/daily/plan")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public MonitorPlanNumInfoEntity getDailyPlanInfo(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getDailyPlanInfo: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            if (Objects.isNull(setting.getCompCountType())) {
                setting.setCompCountType(CompCountTypeEnum.KANBAN);
            }

            Date now = new Date();
            Date fromDate = DateUtils.getBeginningOfDate(now);
            Date toDate = DateUtils.getEndOfDate(now);

            MonitorPlanNumInfoEntity planNum;
            switch (setting.getCompCountType()) {
                case EQUIPMENT:// 対象設備を巡回した数をカウント
                    // 指定した期間の生産数情報を取得する。(対象設備を巡回した数をカウント)
                    planNum = this.aggregateMonitorInfoFacade.getPlanNumInfoEquip(setting, fromDate, toDate);
                    planNum.setPlanNum(setting.getDailyPlanNum());
                    break;
                case WORK:// 対象工程を巡回した数をカウント
                    // 指定した期間の生産数情報を取得する。(対象工程を巡回した数をカウント)
                    planNum = this.aggregateMonitorInfoFacade.getPlanNumInfoWork(setting, fromDate, toDate);
                    planNum.setPlanNum(setting.getDailyPlanNum());
                    break;
                case KANBAN:// 完了したカンバン数をカウント
                default:
                    // 指定ラインの日単位の生産数情報を取得する。
                    planNum = this.aggregateMonitorInfoFacade.getDailyPlanInfo(setting);
                    break;
            }

            if (Objects.nonNull(setting.getLineTakt())) {
                planNum.setLineTakt(setting.getLineTakt().toSecondOfDay() * 1000L);
            }

            return planNum;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定ラインの当月の生産数情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return 生産数情報(当月分)
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/monthly/plan")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public MonitorPlanNumInfoEntity getMonthlyPlanInfo(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getMonthlyPlanInfo:{}", monitorId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            if (Objects.isNull(setting.getCompCountType())) {
                setting.setCompCountType(CompCountTypeEnum.KANBAN);
            }

            Date now = new Date();
            Date fromDate = DateUtils.getBeginningOfMonth(now);
            Date toDate = DateUtils.getEndOfMonth(now);

            MonitorPlanNumInfoEntity planNum;
            switch (setting.getCompCountType()) {
                case EQUIPMENT:// 対象設備を巡回した数をカウント
                    // 指定した期間の生産数情報を取得する。(対象設備を巡回した数をカウント)
                    planNum = this.aggregateMonitorInfoFacade.getPlanNumInfoEquip(setting, fromDate, toDate);
                    planNum.setPlanNum(setting.getMontlyPlanNum());
                    break;
                case WORK:// 対象工程を巡回した数をカウント
                    // 指定した期間の生産数情報を取得する。(対象工程を巡回した数をカウント)
                    planNum = this.aggregateMonitorInfoFacade.getPlanNumInfoWork(setting, fromDate, toDate);
                    planNum.setPlanNum(setting.getMontlyPlanNum());
                    break;
                case KANBAN:// 完了したカンバン数をカウント
                default:
                    // 指定ラインの月単位の生産数情報を取得する。
                    planNum = this.aggregateMonitorInfoFacade.getMonthlyPlanInfo(setting);
                    break;
            }

            return planNum;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定ラインの当日の生産進捗情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return 生産進捗情報(当日分)
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/daily/deviated")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public MonitorPlanDeviatedInfoEntity getDailyDeviatedInfo(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getDailyDeviatedInfo: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // 指定ラインの日単位の生産進捗情報を取得する。
            return this.aggregateMonitorInfoFacade.getDailyDeviatedInfo(setting, new Date());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定ラインの当月の生産進捗情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return 生産進捗情報(当月分)
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/monthly/deviated")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public MonitorPlanDeviatedInfoEntity getMonthlyDeviatedInfo(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getMonthlyDeviatedInfo: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // 指定ラインの月単位の生産進捗情報を取得する。
            return this.aggregateMonitorInfoFacade.getMonthlyDeviatedInfo(setting, new Date());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定ラインの日単位の設備ステータス情報一覧を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return 設備ステータス情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/daily/equipment/status")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<MonitorEquipmentStatusInfoEntity> getDailyEquipmentStatusInfo(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getDailyEquipmentStatusInfo: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // 指定ラインの日単位の設備ステータス情報一覧を取得する。
            return this.aggregateMonitorInfoFacade.getDailyEquipmentStatusInfo(setting);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定ラインの日単位のライン全体ステータス情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return ライン全体ステータス情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/daily/status")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public MonitorLineStatusInfoEntity getDailyStatusInfo(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getDailyStatusInfo: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // 指定ラインの日単位のライン全体ステータス情報を取得する。
            return this.aggregateMonitorInfoFacade.getDailyStatusInfo(setting);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定ラインの日単位の設備生産数情報一覧を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return 設備生産数情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/daily/equipment/plan")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<MonitorEquipmentPlanNumInfoEntity> getDailyEquipmentPlanInfo(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getDailyEquipmentPlanInfo: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // 指定ラインの日単位の設備生産数情報一覧を取得する。
            return this.aggregateMonitorInfoFacade.getDailyEquipmentPlanInfo(setting);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定モニタの日単位の遅延理由回数情報一覧を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return 遅延理由回数情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/daily/delay/count")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<MonitorReasonNumInfoEntity> getDailyDelayReasonInfo(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getDailyDelayReasonInfo: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // 指定モニタの日単位の遅延理由回数情報一覧を取得する。
            return this.aggregateMonitorInfoFacade.getDailyDelayReasonInfo(setting);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定モニタの日単位の中断理由回数情報一覧を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return 中断理由回数情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/daily/interrupt/count")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<MonitorReasonNumInfoEntity> getDailyInterruptReasonInfo(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getDailyInterruptReasonInfo: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // 指定モニタの日単位の中断理由回数情報一覧を取得する。
            return this.aggregateMonitorInfoFacade.getDailyInterruptReasonInfo(setting);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定ラインの日単位のタクトタイム情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return タクトタイム情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/daily/takttime")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public MonitorLineTaktInfoEntity getDailyTakttimeInfo(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getDailyTakttimeInfo: monitorId={}, authId={}", monitorId, authId);
        try {
            // 指定ラインの日単位のタクトタイム情報を取得する。
            return this.aggregateLineInfoFacade.getDailyTakttimeInfo(monitorId);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定ラインの日単位のカウントダウン情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @param now 日付
     * @return カウントダウン情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/daily/timer")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public MonitorLineTimerInfoEntity getDailyTimerInfo(@PathParam("id") Long monitorId, @QueryParam("now") Date now, @QueryParam("authId") Long authId) {
        logger.info("getDailyTimerInfo: monitorId={}, now={}, authId={}", monitorId, now, authId);
        try {
            // 日付の指定がない場合は当日とする。
            if (Objects.isNull(now)) {
                now = new Date();
            }

            // 指定ラインの日単位のカウントダウン情報を取得する。
            return this.aggregateLineInfoFacade.getDailyTimerInfo(monitorId, now);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定ラインの日単位のカウントダウンを制御する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param request 制御要求情報
     * @param authId 認証ID
     * @return 結果
     */
    @PUT
    @Path("{id}/line/daily/timer")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response putDailyTimerControlRequest(@PathParam("id") Long monitorId, LineTimerControlRequest request, @QueryParam("authId") Long authId) {
        logger.info("putDailyTimerControlRequest:{}", request);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // 指定ラインの日単位のカウントダウンを制御する。
            this.aggregateLineInfoFacade.postDailyTimerControlRequest(monitorId, request, new Date(), setting);

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 日別工程計画実績数を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param pluginName プラグイン名
     * @param authId 認証ID
     * @return 工程計画実績数情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/daily/work")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public MonitorWorkPlanNumInfoEntity getDailyMonitorWorkPlanNum(@PathParam("id") Long monitorId, @QueryParam("pluginName") String pluginName, @QueryParam("authId") Long authId) {
        logger.info("getDailyMonitorWorkPlanNum: monitorId={}, pluginName={}, authId={}", monitorId, pluginName, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // 日別工程計画実績数を取得する。
            return this.aggregateMonitorInfoFacade.getDailyWorkPlanNum(setting, pluginName);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        } finally {
            logger.info("getDailyMonitorWorkPlanNum end.");
        }
    }

    /**
     * 工程別計画実績数を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return 工程計画実績数情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}/line/daily/work/plan")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<MonitorWorkPlanNumInfoEntity> getMonitorWorkPlanNum(@PathParam("id") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getMonitorWorkPlanNum: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // 工程別計画実績数を取得する。
            return this.aggregateMonitorInfoFacade.getWorkPlanNum(setting);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        } finally {
            logger.info("getMonitorWorkPlanNum end.");
        }
    }

    /**
     * 設備ステータスを取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param equipmentIds 設備ID一覧
     * @param authId 認証ID
     * @return 設備ステータス情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("{monitorId}/line/equipment/status")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<MonitorEquipmentStatusInfoEntity> getEquipmentStatus(@PathParam("monitorId") Long monitorId, @QueryParam("id") List<Long> equipmentIds, @QueryParam("authId") Long authId) {
        logger.info("getEquipmentStatus: monitorId={}, equipmentIds={}, authId={}", monitorId, equipmentIds, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // 設備ID一覧を指定して、設備ステータス情報一覧を取得する。
            return this.aggregateMonitorInfoFacade.getEquipmentStatus(setting, equipmentIds);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        } finally {
            logger.info("getEquipmentStatus end.");
        }
    }

    /**
     * 作業終了予想時間を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param authId 認証ID
     * @return 作業終了予想時間情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{monitorId}/line/estimated")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public EstimatedTimeInfoEntity getEstimatedTime(@PathParam("monitorId") Long monitorId, @QueryParam("authId") Long authId) {
        logger.info("getEstimatedTime: monitorId={}, authId={}", monitorId, authId);
        try {
            // 進捗モニター設定を取得する。
            AndonMonitorLineProductSetting setting = this.getLineSetting(monitorId);

            // 作業終了予想時間情報を取得する。
            return this.aggregateMonitorInfoFacade.getEstimatedTime(setting, new Date());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        } finally {
            logger.info("getEstimatedTime end.");
        }
    }
}
