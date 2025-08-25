/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.model;

import adtekfuji.andon.agenda.common.AgendaCompoInterface;
import adtekfuji.andon.agenda.enumerate.PayoutOrderDisplayGroupEnum;
import adtekfuji.andon.agenda.model.data.ConfigData;
import adtekfuji.andon.agenda.model.data.CurrentData;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.clientservice.WarehouseInfoFaced;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import adtekfuji.utility.ThreadUtils;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.DeliveryCondition;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryInfo;
import jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum;
import jp.adtekfuji.andon.enumerate.*;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * アジェンダモデル(払出状況)
 *
 * @author s-heya
 */
public class AgendaPayoutModel {

    private static AgendaPayoutModel instance = null;

    private final Logger logger = LogManager.getLogger();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final CashManager cache = CashManager.getInstance();
    private final ConfigData config = ConfigData.getInstance();
    private final CurrentData currentData = CurrentData.getInstance();

    /** 休日情報(休日マスタ？より) */
    private final List<Date> allHolidays = new ArrayList();
    private final List<Date> holidays = new ArrayList();
    /** フォントや色設定【[システム設定][ステータス一覧の編集]の値】 */
    private List<DisplayedStatusInfoEntity> displayedStatuses;
    /** 画面のコントロール */
    private AgendaCompoInterface controller;
    /** 再描画制御フラグ */
    private boolean isUpdate;
    private Timer refreshTimer;
    private Timer systemTimer;
    private Alert alert;
    private final Object lock = new Object();

    /**
     * 倉庫案内 RESTクラス
     */
    private final WarehouseInfoFaced warehouseInfoFacade = new WarehouseInfoFaced();

    /**
     * 出庫指示情報取得時の1回での取得最大件数
     */
    private final long MAX_LOAD_SIZE = ClientServiceProperty.getRestRangeNum();

    // ヘッダーで定義されるadInterface通知受け取り
    private Consumer<Object> notice;

    /**
     * コンストラクタ
     */
    public AgendaPayoutModel() {
        SceneContiner sc = SceneContiner.getInstance();
        this.isUpdate = false;

        sc.getStage().setOnCloseRequest((WindowEvent we) -> {
            this.refreshTimerCancel();
            this.systemTimerCancel();
        });
    }

    /**
     * インスタンスを取得する。
     *
     * @return 自身のインスタンス
     */
    public static AgendaPayoutModel getInstance() {
        if (Objects.isNull(instance)) {
            instance = new AgendaPayoutModel();
        }
        return instance;
    }

    /**
     * モデルを初期化する。
     */
    public void initialize() {
        try {
            CacheUtils.createCacheHoliday(true);

            // 休日情報を取得し変数に保存
            this.allHolidays.clear();
            List<HolidayInfoEntity> holidayList = cache.getItemList(HolidayInfoEntity.class, new ArrayList());
            for (HolidayInfoEntity holiday : holidayList) {
                if (!this.allHolidays.contains(holiday.getHolidayDate())) {
                    this.allHolidays.add(holiday.getHolidayDate());
                }
            }

            cache.setNewCashList(OrganizationInfoEntity.class);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * インスタンス破棄
     */
    public void destory() {
    }

    /**
     * コントローラーを設定する。
     *
     * @param controller コントローラー
     */
    public void setController(AgendaCompoInterface controller) {
        this.controller = controller;
        // 呼出し処理の更新
        this.noticeCall();
    }

    /**
     * 再描画制御フラグを取得する。
     * 
     * @return 再描画制御フラグ
     */
    public boolean isUpdate() {
        return this.isUpdate;
    }

    /**
     * 再描画制御フラグを設定する。
     * 
     * @param isUpdate 再描画制御フラグ
     */
    public void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    /**
     * システムタイマーを取得する。 (現在のシステムタイマーを破棄して、新規作成したシステムタイマーを返す)
     *
     * @return システムタイマー
     */
    public Timer getSystemTimer() {
        this.systemTimerCancel();
        this.systemTimer = new Timer();
        return this.systemTimer;
    }

    /**
     * システムタイマーをキャンセル(破棄)する。
     */
    private void systemTimerCancel() {
        if (Objects.nonNull(this.systemTimer)) {
            this.systemTimer.cancel();
            this.systemTimer.purge();
            this.systemTimer = null;
        }
    }

    /**
     * 休日を取得する。
     *
     * @return
     */
    public List<Date> getHolidays() {
        return this.holidays;
    }

    /**
     * 表示ステータス設定情報を取得する。
     *
     * @return
     */
    public List<DisplayedStatusInfoEntity> getDisplayedStatuses() {
        return this.displayedStatuses;
    }

    /**
     * HeaderCompoで定義されるadInterfaceからの通知受け取り時処理
     *
     * @param notice
     */
    public void setNotice(Consumer<Object> notice) {
        this.notice = notice;
    }

    /**
     * 出庫指示情報を再取得する。
     */
    synchronized private void updateData() {
        logger.info("updateData start.");
        this.hideAlert();

        Map<PayoutOrderDisplayGroupEnum, List<TrnDeliveryInfo>> deliveryInfos = new HashMap<>();

        // 休日を勘案して検索対象日を設定
        this.setToDateExceptHoliday(this.currentData.getFromDate(), this.allHolidays);

        if (DisplayModeEnum.PAYOUT_STATUS == this.currentData.getDisplayMode()) {
            // 払出状況
            deliveryInfos = this.updateTrnDeliveryInfoData();
        } else {
            // 払出状況以外はエラー
            logger.info("updateData Not PAYOUT_STATUS.");
        }

        this.currentData.setDeliveryInfos(deliveryInfos);

        logger.info("updateData end.");
    }

    /**
     * サーバーより出庫指示情報を取得
     *
     * @return 出庫指示情報
     */
    private Map<PayoutOrderDisplayGroupEnum, List<TrnDeliveryInfo>> updateTrnDeliveryInfoData() {
        logger.info("updateTrnDeliveryInfoData start.");

        // [完了予定遅れ]の文字色と背景色の取得はここでやるのが正しいのか？

        Map<PayoutOrderDisplayGroupEnum, List<TrnDeliveryInfo>> infos = new HashMap<>();
        List<TrnDeliveryInfo> trnDeliveryInfos = new ArrayList<>();

        // 対象日
        LocalDate startDay = Objects.isNull(this.currentData.getFromDate()) ? null : convertToLocalDateViaSqlDate(this.currentData.getFromDate());
        LocalDate endDay = Objects.isNull(this.currentData.getToDate()) ? null : convertToLocalDateViaSqlDate(this.currentData.getToDate());

        // 払出待ち、ピッキング中
        DeliveryCondition condition = new DeliveryCondition();
        condition.setDeliveryRule(2);
        if (!StringUtils.isEmpty(this.currentData.getModelName())) {
            condition.setModelName(this.currentData.getModelName());
        }
        if (!StringUtils.isEmpty(this.currentData.getOrderNo())) {
            condition.setOrderNo(this.currentData.getOrderNo());
        }
        condition.setStatuses(Arrays.asList(DeliveryStatusEnum.WORKING, DeliveryStatusEnum.SUSPEND, DeliveryStatusEnum.PICKED));

        long max = warehouseInfoFacade.countDelivery(condition);
        if (max > 0) {
            for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
                List<TrnDeliveryInfo> deliveries = warehouseInfoFacade.searchDeliveryRange(condition, count, count + MAX_LOAD_SIZE - 1);
                trnDeliveryInfos.addAll(deliveries);
            }

            // 払出待ち
            List<TrnDeliveryInfo> waitingInfos = trnDeliveryInfos.stream()
                    .filter(info -> Objects.equals(DeliveryStatusEnum.PICKED, info.getStatus()))
                    .collect(Collectors.toList());
            infos.put(PayoutOrderDisplayGroupEnum.PAYOUT_WAITING, waitingInfos);

            // ピッキング中
            List<TrnDeliveryInfo> pickingInfos = trnDeliveryInfos.stream()
                    .filter(info -> Objects.equals(DeliveryStatusEnum.WORKING, info.getStatus())
                            || Objects.equals(DeliveryStatusEnum.SUSPEND, info.getStatus()))
                    .collect(Collectors.toList());
            infos.put(PayoutOrderDisplayGroupEnum.PICKING, pickingInfos);

        } else {
            infos.put(PayoutOrderDisplayGroupEnum.PAYOUT_WAITING, new ArrayList<>());
            infos.put(PayoutOrderDisplayGroupEnum.PICKING, new ArrayList<>());
        }
        
        // 払出完了
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.currentData.getToDate());
        cal.add(Calendar.DATE, -1 * this.config.getPayoutCompleteDisplayDays());
        Instant instant = convertToLocalDateViaSqlDate(cal.getTime()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        LocalDate fromDeliveryDate = convertToLocalDateViaSqlDate(Date.from(instant));
        
        condition = new DeliveryCondition();
        condition.setDeliveryRule(2);
        condition.setFromDeliveryDate(fromDeliveryDate);
        condition.setToDeliveryDate(endDay);
        if (!StringUtils.isEmpty(this.currentData.getModelName())) {
            condition.setModelName(this.currentData.getModelName());
        }
        if (!StringUtils.isEmpty(this.currentData.getOrderNo())) {
            condition.setOrderNo(this.currentData.getOrderNo());
        }
        condition.setStatuses(Arrays.asList(DeliveryStatusEnum.COMPLETED));

        trnDeliveryInfos = new ArrayList<>();
        max = warehouseInfoFacade.countDelivery(condition);
        if (max > 0) {
            for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
                List<TrnDeliveryInfo> deliveries = warehouseInfoFacade.searchDeliveryRange(condition, count, count + MAX_LOAD_SIZE - 1);
                trnDeliveryInfos.addAll(deliveries);
            }

            infos.put(PayoutOrderDisplayGroupEnum.PAYOUT_COMPLETE, trnDeliveryInfos);
        } else {
            infos.put(PayoutOrderDisplayGroupEnum.PAYOUT_COMPLETE, new ArrayList<>());
        }

        // 受付
        condition = new DeliveryCondition();
        condition.setDeliveryRule(2);
        condition.setToDate(endDay);
        if (!StringUtils.isEmpty(this.currentData.getModelName())) {
            condition.setModelName(this.currentData.getModelName());
        }
        if (!StringUtils.isEmpty(this.currentData.getOrderNo())) {
            condition.setOrderNo(this.currentData.getOrderNo());
        }
        condition.setStatuses(Arrays.asList(DeliveryStatusEnum.WAITING, DeliveryStatusEnum.CONFIRM));

        trnDeliveryInfos = new ArrayList<>();
        max = warehouseInfoFacade.countDelivery(condition);
        if (max > 0) {
            for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
                List<TrnDeliveryInfo> deliveries = warehouseInfoFacade.searchDeliveryRange(condition, count, count + MAX_LOAD_SIZE - 1);
                trnDeliveryInfos.addAll(deliveries);
            }

            infos.put(PayoutOrderDisplayGroupEnum.RECEPTION, trnDeliveryInfos);
        } else {
            // 検索結果が０件の場合は各グループに空リストを作成
            // 受付：WAITING: 払出待ち or CONFIRM: 確認待ち
            infos.put(PayoutOrderDisplayGroupEnum.RECEPTION, new ArrayList<>());
        }
        
        // フォントや色設定を取得【[システム設定][ステータス一覧の編集]の値】
        String uri = config.getAdFactoryServerURI();
        DisplayedStatusInfoFacade displayedStatusFacede = new DisplayedStatusInfoFacade(uri);
        this.displayedStatuses = displayedStatusFacede.findAll();

        logger.info("updateTrnDeliveryInfoData end.");
        return infos;
    }

    /**
     * DateをLocalDateに変換
     *
     * @param dateToConvert 変換対象
     * @return
     */
    private LocalDate convertToLocalDateViaSqlDate(Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

    /**
     * 画面を更新する。
     */
    public void refresh() {
        logger.info("refresh start. config={}", this.config);

        try {
            this.refreshTimerCancel();

            if (Objects.isNull(this.config.getUpdateInterval()) || this.config.getUpdateInterval() == 0) {
                logger.info("refresh timer has cancelled by updateInterval.");
                return;
            }

            this.refresh(0);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 画面を更新する。
     *
     * @param delay 遅延時間
     */
    private void refresh(long delay) {
        synchronized (this.lock) {
            try {
                if (Objects.nonNull(refreshTimer)) {
                    // refreshTimer.cancel();
                    logger.info("In process updateTas.");
                    return;
                }

                TimerTask updateTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            logger.info("updateTask start.");

                            if (isUpdate()) {
                                // データ更新
                                updateData();

                                // 画面側の更新処理を呼び出す
                                if (Objects.nonNull(controller)) {
                                    ThreadUtils.joinFXThread(() -> {
                                        boolean isUpdateTimeDrawing = false;
                                        if (delay != 0) {
                                            isUpdateTimeDrawing = true;
                                        }
                                        logger.debug(String.format("updateTask  delay=%1$d / isUpdateTimeDrawing=%2$s", delay, isUpdateTimeDrawing));
                                        controller.updateDisplay(isUpdateTimeDrawing);
                                        return null;
                                    });
                                }
                            }

                        } catch (Exception ex) {
                            logger.fatal(ex, ex);

                        } finally {
                            logger.info("updateTask cancel.");
                            refreshTimer.cancel();
                            refreshTimer = null;

                            refresh(config.getUpdateIntervalMillisec());
                            logger.info("updateTask end.");
                        }
                    }

                    @Override
                    public boolean cancel() {
                        logger.info("Schedule has been canceled.");
                        return super.cancel();
                    }
                };

                // タイマー再開(指定されたタスクを指定された遅延後に実行)
                refreshTimer = new Timer();
                refreshTimer.schedule(updateTask, delay);

            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    /**
     * アラートを表示する。
     */
    public void showAlert() {
        SceneContiner sc = SceneContiner.getInstance();
        Platform.runLater(() -> {
            try {
                logger.info("showAlert start.");

                if (this.alert != null) {
                    this.alert.close();
                }

                this.alert = new Alert(Alert.AlertType.ERROR);
                this.alert.initOwner(sc.getStage());
                this.alert.initStyle(StageStyle.UTILITY);
                this.alert.setTitle(LocaleUtils.getString("key.ConnectionErrorTitle"));
                this.alert.getDialogPane().setHeaderText(LocaleUtils.getString("key.ConnectionErrorTitle"));
                this.alert.getDialogPane().setContentText(LocaleUtils.getString("key.ConnectionErrorContent"));
                this.alert.getDialogPane().getButtonTypes().remove(ButtonType.CANCEL);
                this.alert.show();
            } finally {
                logger.info("showAlert end.");
            }
        });
    }

    /**
     * アラートを消去する。
     */
    public void hideAlert() {
        Platform.runLater(() -> {
            try {
                logger.info("hideAlert start.");
                if (this.alert != null) {
                    this.alert.close();
                }
            } finally {
                logger.info("hideAlert end.");
            }
        });
    }

    /**
     * 更新タイマーを停止する。
     */
    private void refreshTimerCancel() {
        synchronized (this.lock) {
            if (Objects.nonNull(this.refreshTimer)) {
                this.refreshTimer.cancel();
                this.refreshTimer.purge();
                this.refreshTimer = null;
            }
        }
    }

    /**
     * 指定したフォーマットで日時文字列を取得する。
     *
     * @param date 日時
     * @param sdf 日時フォーマット
     * @return 日時文字列
     */
    private String formatDatetime(Date date, SimpleDateFormat sdf) {
        String result = "";
        if (Objects.nonNull(date)) {
            result = sdf.format(date);
        }
        return result;
    }

    /**
     * 休日を除いた場合の検索対象日(TO)設定
     *
     * @param from 日時
     * @param holidays 休日対象日リスト
     */
    private void setToDateExceptHoliday(Date from, List<Date> holidays) {

        this.holidays.clear();
        
    }

    /**
     * 呼出し
     */
    public void noticeCall() {
        // 画面を更新する
        try {
            ThreadUtils.joinFXThread(() -> {
                controller.updateDisplay();  // コンテンツ部の更新
                return null;
            });
        } catch (Exception ex) {
        }

        // 呼出音を鳴らさない。
    }

}
