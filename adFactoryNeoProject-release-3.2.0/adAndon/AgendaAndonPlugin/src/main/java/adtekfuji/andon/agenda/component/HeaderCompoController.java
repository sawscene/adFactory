/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.component;

import adtekfuji.andon.agenda.common.AgendaSettings;
import adtekfuji.andon.agenda.common.CallingPool;
import adtekfuji.andon.agenda.common.KanbanStatusConfig;
import adtekfuji.andon.agenda.model.AgendaModel;
import adtekfuji.andon.agenda.model.AgendaPayoutModel;
import adtekfuji.andon.agenda.model.data.Agenda;
import adtekfuji.andon.agenda.model.data.AgendaGroup;
import adtekfuji.andon.agenda.model.data.AgendaPlan;
import adtekfuji.andon.agenda.model.data.AgendaTopic;
import adtekfuji.andon.agenda.model.data.ConfigData;
import adtekfuji.andon.agenda.model.data.CurrentData;
import adtekfuji.andon.agenda.service.AdInterfaceClientService;
import adtekfuji.andon.agenda.service.NoticeCommandListner;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.transform.Scale;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.adinterface.command.CallingNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DelayReasonInfoEntity;
import jp.adtekfuji.adFactory.entity.master.InterruptReasonInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.andon.enumerate.AndonMonitorTypeEnum;
import jp.adtekfuji.andon.enumerate.DisplayModeEnum;
import jp.adtekfuji.andon.enumerate.TimeAxisEnum;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ヘッダーペインのコントローラー
 *
 * @author s-heya
 */
@FxComponent(id = "HeaderCompo", fxmlPath = "/fxml/compo/agenda_header_compo.fxml")
public class HeaderCompoController implements Initializable, ArgumentDelivery, NoticeCommandListner, ComponentHandler {
    
    /**
     * CSV出力用データ
     */
    public class CsvData {

        private String dataName = "";
        private int dataType;
        private String workName = "";
        private String workerName = "";
        private String kanbanStatus = "";
        private String workKanbanStatus = "";
        private Date startDate;
        private Date endDate;
        private String details = "";

        /**
         * CSV出力用データ
         *
         */
        public CsvData() {}

        /**
         * データ名取得
         * @return　データ名
         */
        public String getDataName() {
            return this.dataName;
        }

        /**
         * データ種別取得
         * @return データ種別
         */
        public int getDataType() {
            return this.dataType;
        }

        /**
         * 工程名取得
         * @return 工程名
         */
        public String getWorkName() {
            return this.workName;
        }

        /**
         * 作業者名取得
         * @return 作業者名
         */
        public String getWorkerName() {
            return this.workerName;
        }
                

        /**
         * カンバンステータス取得
         * @return　カンバンステータス
         */
        public String getKanbanStatus() {
            return this.kanbanStatus;
        }
        
        /**
         *　工程カンバンステータス取得
         * @return 工程カンバンステータス
         */
        public String getWorkKanbanStatus() {
            return this.workKanbanStatus;
        }

        /**
         * 開始日時取得
         * @return 開始日時
         */
        public Date getStartDate() {
            return this.startDate;
        }

        /**
         * 終了日時取得
         * @return 終了日時
         */
        public Date getEndDate() {
            return this.endDate;
        }
        
        /**
         * 詳細取得
         * @return 詳細
         */
        public String getDetails() {
            return this.details;
        }
        
        /**
         * データ名設定
         * @param dataName データ名
         */
        public void setDataName(String dataName) {
            this.dataName = dataName;
        }
        
        /**
         * データ種別設定
         * @param dataType データ種別
         */
        public void setDataType(int dataType) {
            this.dataType = dataType;
        }
        
        /**
         * 工程名設定
         * @param workName 工程名
         */
        public void setWorkName(String workName) {
            this.workName = workName;
        }

        /**
         * 作業者名設定
         * @param workerName 作業者名
         */
        public void setWorkerName(String workerName) {
            this.workerName = workerName;
        }

        /**
         * カンバンステータス設定
         * @param kanbanStatus カンバンステータス
         */
        public void setKanbanStatus(String kanbanStatus) {
            this.kanbanStatus = kanbanStatus;
        }
        
        /**
         * 工程カンバンステータス設定
         * @param workKanbanStatus 工程カンバンステータス
         */
        public void setWorkKanbanStatus(String workKanbanStatus) {
            this.workKanbanStatus = workKanbanStatus;
        }

        /**
         * 開始日時設定
         * @param startDate 開始日時
         */
        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        /**
         * 終了日時設定
         * @param endDate 終了日時
         */
        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        /**
         * 詳細設定
         * @param details 詳細
         */
        public void setDetails(String details) {
            this.details = details;
        }
    }

    private static final Logger logger = LogManager.getLogger();
    private final AgendaModel model = AgendaModel.getInstance();
    private final AgendaPayoutModel modelPayout = AgendaPayoutModel.getInstance();
    private final SceneContiner sc = SceneContiner.getInstance();
    private ResourceBundle rb;// = LocaleUtils.getBundle("locale.locale");
    private final CurrentData currentData = CurrentData.getInstance();
    private final ConfigData config = ConfigData.getInstance();
    private final ToggleGroup group = new ToggleGroup();
    private final AdInterfaceClientService adInterfaceClientService = new AdInterfaceClientService();
    
    private final boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());

    // CSVファイルの設定
    private static final String CSV_CHARSET = "MS932";// エンコード
    private static final String CSV_QUOTE = "\"";// 囲み文字
    private static final String CSV_SEPARATOR = ",";// 区切り文字

    @FXML
    private DatePicker datePicker;
    @FXML
    private Button refreshButton;
    @FXML
    private Button configButton;
    @FXML
    private Button csvButton;
    /** モデル名ラベル項目 */
    @FXML
    private Label modelNameLabel;
    /** モデル名項目 */
    @FXML
    private TextField modelNameField;
    /** 製番ラベル項目 */
    @FXML
    private Label orderNoLabel;
    /** 製番項目 */
    @FXML
    private TextField orderNoField;

    private int prevDisplayId = -1;
    private String modelNameOld = "";
    private String orderNoOld = "";

    /**
     * モニター種別のリスナー
     */
    private final ChangeListener<Boolean> changeListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if (newValue) {
            // 画面表示なしの場合、描画更新は行なわない。
            if (!KanbanStatusConfig.getEnableView()) {
                return;
            }

            this.display();
            sc.getStage().setFullScreen(config.isFullScreen());
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.rb = resources;
        datePicker.setStyle("-fx-font-size: " + config.getHeaderFontSize() + ";");
        refreshButton.setStyle("-fx-font-size: " + config.getHeaderFontSize() + "; -fx-graphic: url('image/refreshicon.PNG');");
        configButton.setStyle("-fx-font-size: " + config.getHeaderFontSize() + "; -fx-graphic: url('image/configicon.PNG');");

        //datePicker.setOnShowing(event -> {
        //    logger.info("datePicker setOnShowing; setUpdate(false)");
        //    model.setUpdate(false);
        //});

        //datePicker.setOnHiding(event -> {
        //    logger.info("datePicker setOnHiding; setUpdate(true)");
        //    model.setUpdate(true);
        //});

        this.modelNameField.setText(config.getModelName());

        datePicker.setOnAction(event -> {
            LocalDate value = datePicker.getValue();
            if (Objects.nonNull(value)) {
                // 表示期間の設定
                if (DisplayModeEnum.PAYOUT_STATUS.equals(config.getMode())) {
                    // 払出状況の場合は表示期間は1日日固定
                    currentData.setDate(value.atStartOfDay());
                } else {
                    switch (currentData.getTimeScale()) {
                        case Day:
                        case HalfDay:
                            currentData.setDays(value.atStartOfDay(), config.getShowDays());
                            break;
                        case Week:
                        case Month:
                            currentData.setMonths(value.atStartOfDay(), config.getShowMonths());
                            break;
                        case Time:
                        default:
                            currentData.setDate(value.atStartOfDay());
                            break;
                    }
                }

                currentData.setKeepTargetDay(currentData.getFromDate());
                currentData.setModelName(this.modelNameField.getText());
                currentData.setOrderNo(this.orderNoField.getText());

                //  画面を更新
                if (DisplayModeEnum.PAYOUT_STATUS.equals(config.getMode())) {
                    modelPayout.refresh();                    
                } else {
                    model.refresh();
                }
            }
        });

        // モデル名項目のEnterキー押下イベント
        modelNameField.setOnAction(event -> {
            // 現状の値を取得
            String value = Objects.nonNull(modelNameField.getText()) ? modelNameField.getText() : "";
            if (!value.equals(modelNameOld)) {
                // 値に変更がある場合、値を検索条件にセット後、画面を更新
                currentData.setModelName(value);
                modelPayout.refresh();
                modelNameOld = value;               // 検索を行ったので古い値を最新の値で更新
            }
        });

        // モデル名項目のフォーカスイベント
        modelNameField.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (newPropertyValue)
                {
                    // Textfield on focus
                    // 変更前の値を保存
                    modelNameOld = Objects.nonNull(modelNameField.getText()) ? modelNameField.getText() : "";
                }
                else
                {
                    // Textfield out focus
                    // 現状の値を取得
                    String value = Objects.nonNull(modelNameField.getText()) ? modelNameField.getText() : "";
                    if (!value.equals(modelNameOld)) {
                        // 値に変更がある場合、値を検索条件にセット後、画面を更新
                        currentData.setModelName(value);
                        modelPayout.refresh();
                        modelNameOld = value;               // 検索を行ったので古い値を最新の値で更新
                    }
                }
            }
        });

        // 製番項目のEnterキー押下イベント
        orderNoField.setOnAction(event -> {
            // 現状の値を取得
            String value = Objects.nonNull(orderNoField.getText()) ? orderNoField.getText() : "";
            if (!value.equals(orderNoOld)) {
                // 値に変更がある場合、値を検索条件にセット後、画面を更新
                currentData.setOrderNo(value);
                modelPayout.refresh();
                orderNoOld = value;               // 検索を行ったので古い値を最新の値で更新
            }
        });

        // モデル名項目のフォーカスイベント
        orderNoField.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (newPropertyValue)
                {
                    // Textfield on focus
                    // 変更前の値を保存
                    orderNoOld = Objects.nonNull(orderNoField.getText()) ? orderNoField.getText() : "";
                }
                else
                {
                    // Textfield out focus
                    // 現状の値を取得
                    String value = Objects.nonNull(orderNoField.getText()) ? orderNoField.getText() : "";
                    if (!value.equals(orderNoOld)) {
                        // 値に変更がある場合、値を検索条件にセット後、画面を更新
                        currentData.setOrderNo(value);
                        modelPayout.refresh();
                        orderNoOld = value;               // 検索を行ったので古い値を最新の値で更新
                    }
                }
            }
        });

        if (DisplayModeEnum.PAYOUT_STATUS.equals(config.getMode())) {
            modelPayout.setNotice(this::notice);
        } else {
            model.setNotice(this::notice);
        }

        Platform.runLater(() -> this.datePicker.setValue(LocalDate.now()));

        currentData.setKeepTargetDay(currentData.getFromDate());
        currentData.setModelName(this.modelNameField.getText());
        currentData.setOrderNo(this.orderNoField.getText());

        this.updateDisplay();

        //リスナー登録.
        adInterfaceClientService.getHandler().setNoticeListner(this);

        //通信開始
        adInterfaceClientService.startService();
    }

    @Override
    public boolean destoryComponent() {
        try {
            //通信終了
            adInterfaceClientService.stopService();
            //モデル破棄
            model.destory();
            modelPayout.destory();
        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        }

        return true;
    }

    @Override
    public void setArgument(Object argument) {
    }

    /**
     * 進捗モニタ設定更新時に通知を受け取り、設定を再取得する
     *
     * @param command adInterfaceから送られる通知
     */
    @Override
    public void notice(Object command) {
        if (command instanceof ResetCommand) {
            logger.info("ResetCommand start.");
            this.noticeResetCommand((ResetCommand)command);
        }
        else if (command instanceof CallingNoticeCommand) {
            logger.info("CallingNoticeCommand start.");
            this.noticeCallingCommand((CallingNoticeCommand)command);
        }
    }

    /**
     * リセットコマンド
     * @param resetCommand 
     */
    private void noticeResetCommand(ResetCommand resetCommand) {
        // 設備IDが一致していない限り更新する必要はない
        Long monitorId = AgendaSettings.getMonitorId();
        if (Objects.isNull(monitorId) || Objects.isNull(resetCommand.getMonitorId()) || !Objects.equals(resetCommand.getMonitorId(), monitorId)) {
            return;
        }

        // キャッシュをクリアしておく。
        CacheUtils.removeCacheData(EquipmentInfoEntity.class);
        CacheUtils.removeCacheData(OrganizationInfoEntity.class);
        CacheUtils.removeCacheData(BreakTimeInfoEntity.class);// AgendaModelの初期化時に再取得される。
        CacheUtils.removeCacheData(DelayReasonInfoEntity.class);// 設定画面表示時に再取得される。
        CacheUtils.removeCacheData(InterruptReasonInfoEntity.class);// 設定画面表示時に再取得される。

        // キャッシュを再取得する。
        CacheUtils.createCacheEquipment(false);
        CacheUtils.createCacheOrganization(false);

        AgendaSettings.load();

        // カンバン進捗情報の設定で「進捗情報CSVファイル出力あり」の場合、進捗モニタ設定の「自動スクロール」を「ON」にする。
        if (KanbanStatusConfig.getEnableKanbanStatusCsv()) {
            AgendaSettings.getAndonSetting().getAgendaMonitorSetting().setAutoScroll(true);
        }

        AgendaSettings.buildConfigData();
        AgendaSettings.buildCurrentData();

        // 自動更新が有効又は払出状況の場合、常に当日を表示日とする
        if (config.isAutoScroll() || DisplayModeEnum.PAYOUT_STATUS.equals(config.getMode())) {
            currentData.setDate(LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.HOURS));
        }

        // モデルを初期化
        if (DisplayModeEnum.PAYOUT_STATUS.equals(config.getMode())) {
            modelPayout.initialize();
        } else {
            model.initialize();
        }

        Platform.runLater(() -> {
            // 取得した設定の日時を画面に反映
            if (Objects.isNull(this.currentData.getFromDate())) {
                this.datePicker.setValue(LocalDate.now());
            } else {
                LocalDate date = this.currentData.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                this.datePicker.setValue(date);
            }
        });

        this.updateDisplay();
    }

    /**
     * 呼び出し通知コマンド
     * @param callingCommand 
     */
    private void noticeCallingCommand(CallingNoticeCommand callingCommand) {
        CallingPool.getInstance().setCaller(callingCommand);

        // コンテンツ部の更新
        if (DisplayModeEnum.PAYOUT_STATUS.equals(config.getMode())) {
            modelPayout.noticeCall();
            modelPayout.refresh();
        } else {
            model.noticeCall();
            model.refresh();
        }
    }

    /**
     * 画面を更新する。
     */
    @FXML
    private void onRefreshAction() {
        logger.info("onRefreshAction start.");
        if (DisplayModeEnum.PAYOUT_STATUS.equals(config.getMode())) {
            modelPayout.refresh();
        } else {
            model.refresh();
        }
    }

    /**
     * 設定ダイアログを表示する。
     */
    @FXML
    private void onUpdateConfig() {
        try {
            model.setUpdate(false);
            modelPayout.setUpdate(false);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialog/agenda_config_dialog.fxml"));
            loader.setLocation(getClass().getResource("/fxml/dialog/agenda_config_dialog.fxml"));
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setResources(rb);
            Parent root = (Parent) loader.load();

            Stage dialogStage = new Stage();

            // 設定を渡してダイアログで変更する
            AgendaSettings.updateAndonSettings();
            ((UpdateConfigDialog) loader.getController()).setAndonSetting(AgendaSettings.getAndonSetting());
            ((UpdateConfigDialog) loader.getController()).setHeaderController(this);
            ((UpdateConfigDialog) loader.getController()).setParent(dialogStage);

            SceneProperties sp = new SceneProperties(dialogStage, AdProperty.getProperties());
            sp.addCssPath("/styles/colorStyles.css");
            sp.addCssPath("/styles/designStyles.css");
            sp.addCssPath("/styles/fontStyles.css");

            dialogStage.setTitle(LocaleUtils.getString("key.UpdateConfigDialog"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(sc.getStage());
            Scene scene = new Scene(root, 700.0, 700.0);
            scene.getStylesheets().addAll(sp.getCsspathes());
            dialogStage.setScene(scene);
            dialogStage.setResizable(true);

            dialogStage.showAndWait();

        } catch (Exception e) {
            logger.fatal(e, e);
        } finally {
            model.clearKanbanBreakTimes();
            model.setUpdate(true);
            modelPayout.setUpdate(true);
        }
    }

    /**
     * 画面の更新する。
     */
    public void updateDisplay() {
        logger.info("updateDisplay start.");

        Platform.runLater(() -> {
            try {
                // 払出状況選択フラフ
                boolean payoutB = DisplayModeEnum.PAYOUT_STATUS.equals(config.getMode());

                if (KanbanStatusConfig.getEnableView()) {
                    // 表示あり
                    AndonMonitorTypeEnum monitorType = config.getMonitorType();
                    this.datePicker.setVisible(monitorType != AndonMonitorTypeEnum.LITE_MONITOR);
                    this.datePicker.setManaged(monitorType != AndonMonitorTypeEnum.LITE_MONITOR);

                    switch (monitorType) {
                        case AGENDA:
                            // 払出状況の場合、向き関係なし。
                            if (payoutB) {
                                sc.setComponent("MainSceneContentPane", "AgendaPayoutStatusCompo");
                                break;   
                            }
                            
                            TimeAxisEnum timeAxis = config.getTimeAxis();
                            switch (timeAxis) {
                                case HorizonAxis:
                                    sc.setComponent("MainSceneContentPane", "AgendaHorizonCompo");
                                    break;   
                                case VerticalAxis:
                                default:
                                    sc.setComponent("MainSceneContentPane", "AgendaCompo");
                                    break;
                            }
                            break;

                        case LITE_MONITOR:
                            
                            DisplayModeEnum mode = config.getMode();
                            switch(mode) {
                                case LINE:
                                case KANBAN:
                                    sc.setComponent("MainSceneContentPane", "AgendaKanbanLite");
                                    break;   
                                case WORKER:
                                    sc.setComponent("MainSceneContentPane", "AgendaWorkerStatusLite");
                                    break;
                                default:
                                    break;
                            }
                            break;
                    }
                }

                // コンテンツ部を更新
                if (payoutB) {
                    // 払出状況が設定され場合のみ検索条件のモデル名に設定情報のモデル名をセット
                    this.modelNameField.setText(config.getModelName());
                    currentData.setModelName(config.getModelName());
                    
                    modelPayout.refresh();
                    modelPayout.setUpdate(true);
                } else {
                    model.refresh();
                    model.setUpdate(true);
                }

                // 画面表示なしの場合、描画更新は行なわない。
                if (!KanbanStatusConfig.getEnableView()) {
                    return;
                }

                // 製品進捗又は払出状況が設定され場合はcsv出力は無効
                csvButton.setDisable(DisplayModeEnum.PRODUCT_PROGRESS.equals(config.getMode()) || payoutB);

                // 払出状況が設定され場合のみモデル名と製番が有効
                this.modelNameLabel.setManaged(payoutB);
                this.modelNameLabel.setVisible(payoutB);
                this.modelNameField.setManaged(payoutB);
                this.modelNameField.setVisible(payoutB);
                this.orderNoLabel.setManaged(payoutB);
                this.orderNoLabel.setVisible(payoutB);
                this.orderNoField.setManaged(payoutB);
                this.orderNoField.setVisible(payoutB);
                
                this.display();

                sc.getStage().focusedProperty().removeListener(changeListener);
                sc.getStage().setFullScreen(config.isFullScreen());

                if (config.isFullScreen()) {
                    sc.getStage().focusedProperty().addListener(changeListener);
                }

            } catch (Exception ex) {
                logger.fatal(ex, ex);
            } finally {
                logger.info("updateDisplay end.");
            }
        });
    }

    /**
     * 指定ディスプレイに表示する。
     */
    private void display() {
        if (!config.isFullScreen()) {
            return;
        }

        int displayId = config.getTargetMonitor();

        if ((prevDisplayId != displayId && displayId > 0)) {

            List<Screen> screens = Screen.getScreens();
            if (screens.size() < displayId) {
                displayId = 1;
            }

            Rectangle2D newBounds = screens.get(displayId - 1).getBounds();
            sc.getStage().setX(newBounds.getMinX() + 1);
            sc.getStage().setY(newBounds.getMinY() + 1);

            Rectangle2D oldBounds = screens.get(prevDisplayId <= 0 ? 0 : prevDisplayId - 1).getBounds();
            Scale scale = new Scale(newBounds.getWidth() / oldBounds.getWidth(), newBounds.getHeight() / oldBounds.getHeight(), 0, 0);
            sc.getStage().getScene().getRoot().getTransforms().add(scale);
            
            prevDisplayId = displayId;

            logger.info("Stage size: x = {}, y = {}, width = {}, height = {}", sc.getStage().getX(), sc.getStage().getY(), sc.getStage().getWidth(), sc.getStage().getHeight());
        }
    }
    
    /**
     * CSVを出力する
     */
    @FXML
    private void onCsvOutput(ActionEvent event) {
        logger.info("onCsvOutput start.");
        
        try {
            // CSV出力先ダイアログ
            final Path outputDir = showOutputDialog(event);
            // CSV出力データ取得
            Map<Long, Agenda> agendas = currentData.getAgendas();
            List<CsvData> list = this.createCsvData(agendas);
            list.stream().sorted((p1, p2) -> {
                int ret = p1.getDataName().compareTo(p2.getDataName());
                ret = ret == 0 ? Integer.compare(p1.getDataType(), p2.getDataType()) : ret;
                return ret == 0 ? p1.getStartDate().compareTo(p2.getStartDate()) : ret;
            });

            Task task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    // CSV出力
                    return outputCsvFile(outputDir.toString(), list);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // 成功
                        sc.showAlert(Alert.AlertType.NONE, LocaleUtils.getString("key.PrintOutCSV"), this.getValue());
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                        // エラー
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.PrintOutCSV"), LocaleUtils.getString("key.alert.systemError"));
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info("onCsvOutput end.");
    }
    
    /**
     * ディレクトリ選択ダイアログを表示する
     *
     * @param event
     * @return
     */
    private Path showOutputDialog(ActionEvent event) {
        logger.info("showOutputDialog start.");
        Node node = (Node) event.getSource();

        Path desktopDir = Paths.get(System.getProperty("user.home"), "Desktop");
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(desktopDir.toFile());
        
        String now = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        String fileName = "Topic" + "_" + now + ".csv";

        return Paths.get(dirChooser.showDialog(node.getScene().getWindow()).getAbsolutePath() + File.separator + fileName);
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
     * CSV出力用データを作成。
     *
     * @param agendas 出力元
     */
    private List<CsvData> createCsvData(Map<Long,Agenda> agendas) {
        logger.info("createCsvData start.");
        List<CsvData> list = new ArrayList<>();
        
        boolean isWorker = this.currentData.getDisplayMode().equals(DisplayModeEnum.WORKER);
        
        try {
            for (Agenda agenda : agendas.values()) {
                for(AgendaPlan plan : agenda.getPlans()) {
                    for(List<AgendaTopic> topics : plan.getTopics().values()) {
                        for(AgendaTopic topic : topics) {
                            CsvData data = new CsvData();
                            data.setDataName(isWorker ? topic.getTitle1() : agenda.getTitle1());
                            data.setDataType(0);
                            data.setWorkName(isWorker ? topic.getTitle3() : topic.getTitle1());
                            data.setWorkerName(isWorker ? agenda.getTitle2() : topic.getTitle2());
                            data.setKanbanStatus(topic.getKanbanStatus().toString());
                            data.setWorkKanbanStatus(topic.getWorkKanbanStatus().toString());
                            data.setStartDate(topic.getPlanStartTime());
                            data.setEndDate(topic.getPlanEndTime());
                            data.setDetails(isWorker ? topic.getTitle2() : agenda.getTitle2());
                            list.add(data);
                        }
                    }
                }

                for(AgendaGroup actuals : agenda.getActuals()){
                    for(AgendaTopic topic : actuals.getTopics()) {
                        CsvData data = new CsvData();
                        data.setDataName(isWorker ? topic.getTitle1() : agenda.getTitle1());
                        data.setDataType(1);
                        data.setWorkName(isWorker ? topic.getTitle3() : topic.getTitle1());
                        data.setWorkerName(isWorker ? agenda.getTitle2() : topic.getTitle2());
                        data.setKanbanStatus(topic.getKanbanStatus().toString());
                        data.setWorkKanbanStatus(topic.getWorkKanbanStatus().toString());
                        data.setStartDate(topic.getActualStartTime());
                        data.setEndDate(topic.getActualEndTime());
                        data.setDetails(isWorker ? topic.getTitle2() : agenda.getTitle2());
                        list.add(data);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }    
        logger.info("createCsvData end.");
        return list;
    }
    
    /**
     * CSVファイルを出力する。
     *
     * @param filePath CSVファイルパス
     * @param data 出力データ
     * @return ダイアログメッセージ
     */
    private String outputCsvFile(String filePath, List<CsvData> list) {
        logger.info("outputCsvFile start. file:{}, len:{}", filePath, list.size());
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            File file = new File(filePath);
            if (!file.isAbsolute()) {
                return String.format(LocaleUtils.getString("key.FailedToOutput"), "CSV");
            }

            File folder = new File(file.getParent());
            if (!folder.exists()) {
                // フォルダがない場合は作成する。
                if (!folder.mkdirs()) {
                    return String.format(LocaleUtils.getString("key.FailedToOutput"), "CSV");
                }
            }

            // CSVファイルに出力する。
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), CSV_CHARSET))) {
                // ヘッダーを出力する。
                StringBuilder title = new StringBuilder();
                title.append(CSV_QUOTE).append(LocaleUtils.getString("key.KanbanName")).append(CSV_QUOTE);// カンバン名
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append(LocaleUtils.getString("key.PlanActual")).append(CSV_QUOTE);// 予定実績
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append(LocaleUtils.getString("key.ProcessName")).append(CSV_QUOTE);// 工程名
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append(LocaleUtils.getString("key.WokerName")).append(CSV_QUOTE);// 作業者名
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append(LocaleUtils.getString("key.KanbanStatus")).append(CSV_QUOTE);// カンバンステータス 
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append(LocaleUtils.getString("key.ProcessStatus")).append(CSV_QUOTE);// 工程ステータス ProcessStatus
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append(LocaleUtils.getString("key.StartTime")).append(CSV_QUOTE);// 開始時間
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append(LocaleUtils.getString("key.CompleteTime")).append(CSV_QUOTE);// 完了時間
                title.append(CSV_SEPARATOR);
                title.append(CSV_QUOTE).append(LocaleUtils.getString("key.MonitorDetail")).append(CSV_QUOTE);// 詳細表示
                title.append(CSV_SEPARATOR);
                
                writer.write(title.toString());
                writer.newLine();

                // データを出力する。
                for (CsvData data : list) {
                    StringBuilder sb = new StringBuilder();

                    // 開始時間
                    String startDatetime = this.formatDatetime(data.getStartDate(), sdf);
                    // 完了時間
                    String compDatetime = this.formatDatetime(data.getEndDate(), sdf);

                    // カンバン名
                    sb.append(CSV_QUOTE).append(data.getDataName()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 予定実績
                    sb.append(CSV_QUOTE).append(data.getDataType()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 工程名
                    sb.append(CSV_QUOTE).append(data.getWorkName()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 作業者名
                    sb.append(CSV_QUOTE).append(data.getWorkerName()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // カンバンステータス
                    sb.append(CSV_QUOTE).append(data.getKanbanStatus()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 工程ステータス
                    sb.append(CSV_QUOTE).append(data.getWorkKanbanStatus()).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 開始時間
                    sb.append(CSV_QUOTE).append(startDatetime).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 完了時間
                    sb.append(CSV_QUOTE).append(compDatetime).append(CSV_QUOTE);
                    sb.append(CSV_SEPARATOR);
                    // 詳細表示
                    sb.append(CSV_QUOTE).append(data.getDetails()).append(CSV_QUOTE);

                    writer.write(sb.toString());
                    writer.newLine();
                }

                writer.close();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return String.format(LocaleUtils.getString("key.FailedToOutput"), "CSV");
        }
        logger.info("outputCsvFile end.");
        return String.format(LocaleUtils.getString("key.OutputSuccess"), "CSV");
    }
}
