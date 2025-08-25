/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component.els;

import adtekfuji.admanagerapp.kanbaneditplugin.common.Constants;
import adtekfuji.admanagerapp.kanbaneditplugin.common.KanbanEditConfigELS;
import adtekfuji.admanagerapp.kanbaneditplugin.common.KanbanEditPermanenceData;
import adtekfuji.admanagerapp.kanbaneditplugin.common.KanbanPropertyRecordFactory;
import adtekfuji.admanagerapp.kanbaneditplugin.common.SelectedKanbanAndHierarchy;
import adtekfuji.admanagerapp.kanbaneditplugin.controls.ColorTextCell;
import adtekfuji.admanagerapp.kanbaneditplugin.controls.ColorTextCellData;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.KanbanBaseInfoEntity;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.KanbanBaseInfoPropertyEntity;
import adtekfuji.admanagerapp.kanbaneditplugin.externalio.DbAccess;
import adtekfuji.admanagerapp.kanbaneditplugin.externalio.DennoAccess;
import adtekfuji.admanagerapp.kanbaneditplugin.externalio.KanbanBaseInfoAccessELS;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.WorkKanbanTimeReplaceUtils;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.WorkflowProcess;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.serialcommunication.SerialCommunicationListener;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.job.OrderInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanCreateCondition;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.workflow.KanbanPropertyTemplateInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.Locale;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン作成画面コントローラー
 *
 * @author s-heya
 */
@FxComponent(id = "KanbanCreateCompoELS", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/els/kanban_create_compo_els.fxml")
public class KanbanCreateCompoFxControllerELS implements Initializable, SerialCommunicationListener, ArgumentDelivery, ListChangeListener {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final Properties properties = AdProperty.getProperties();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
    private final KanbanInfoFacade kanbanFacade = new KanbanInfoFacade();
    private final WorkKanbanInfoFacade workKanbanFacade = new WorkKanbanInfoFacade();
    private final WorkflowInfoFacade workflowFacade = new WorkflowInfoFacade();
    private final KanbanEditPermanenceData kanbanEditPermanenceData = KanbanEditPermanenceData.getInstance();
    private final LinkedList<KanbanPropertyInfoEntity> propertyList = new LinkedList();
    private final ObservableList<ColorTextCellData> serialList = FXCollections.observableArrayList();

    private DbAccess dbAccess;
    private KanbanBaseInfoEntity kanbanBase = null;
    private final ObservableList<KanbanBaseInfoEntity> kanbanBaseInfoList = FXCollections.observableArrayList();// ロット生産のカンバン基本情報リスト

    private WorkflowInfoEntity lotWorkflow = new WorkflowInfoEntity();// ロット生産の工程順
    private boolean isEditMode = false; // 編集モード (「カンバンの編集」ボタンから遷移」)

    private SelectedKanbanAndHierarchy kanbanHierarchy;
    private WorkflowInfoEntity workflow;
    private KanbanPropertyRecordFactory propertyFactory;
    private KanbanInfoEntity kanbanInfo;

    private final StringProperty instructionCode = new SimpleStringProperty();
    private final StringProperty serialNumberTo = new SimpleStringProperty();
    private final StringProperty serialNumberFrom = new SimpleStringProperty();
    private final IntegerProperty serialCount = new SimpleIntegerProperty(0);
    private final IntegerProperty instructionLot = new SimpleIntegerProperty();

    private static final Long RANGE = 20l;
    private static final Color COLOR_SKIP = Color.GREEN;
    private static final Color COLOR_NG = Color.RED;
    private static final int MAX_PORDER_NUM = 5;
    private static final int MAX_SERIAL_NUM = 50;

    private static final String SERVICE_INFO_LOT = "els";

    @FXML
    private HBox kanbanNameHbox;

    @FXML
    private TextField kanbanNameTextField;
    @FXML
    private TextField workflowTextField;
    @FXML
    private Label instructionCodeLabel;
    @FXML
    private TextField instructionCodeTextField;
    @FXML
    private Button readInstructionButton;
    @FXML
    private TextField serialFromTextField;
    @FXML
    private TextField serialToTextField;
    @FXML
    private ListView<ColorTextCellData> serialListView;
    @FXML
    private TextField serialCountTextField;
    @FXML
    private TextField instructionLotTextField;
    @FXML
    private Pane propertyPane;
    @FXML
    private Button createButton;
    @FXML
    private Pane progressPane;

    @FXML
    private GridPane kanbanSettingGrid;
    @FXML
    private CheckBox lotProductCheckBox;
    @FXML
    private TextField lotProductTextField;
    @FXML
    private Button lotProuctSelectButton;
    @FXML
    private Button porderDeleteButton;
    @FXML
    private HBox totalHBox;
    @FXML
    private Label totalSummary;

    @FXML
    private TableView<KanbanBaseInfoEntity> lotProductTableView;
    @FXML
    private TableColumn<KanbanBaseInfoEntity, String> porderTableColumn;
    @FXML
    private TableColumn<KanbanBaseInfoEntity, String> kikakuKatasikiTableColumn;
    @FXML
    private TableColumn<KanbanBaseInfoEntity, Number> lvolTableColumn;
    @FXML
    private TableColumn<KanbanBaseInfoEntity, Number> remainTableColumn;

    private boolean IsReadAvailable;

    /**
     * 注番一覧が追加・削除されたタイミングで合計値を設定する
     */
    private final ListChangeListener listChangeListener = (Change c) -> {
        // 指示数の合計表示を更新する。
        this.dispTotal();
    };

    /**
     * 指示数の合計表示を更新する。
     */
    private void dispTotal() {
        try {
            int totalLvol = 0;
            int totalRem = 0;
            for (KanbanBaseInfoEntity kanbanBaseInfo : this.kanbanBaseInfoList) {
                OrderInfoEntity orderInfo = kanbanBaseInfo.getOrderInfo();
                totalLvol += orderInfo.getLvol();
                totalRem += orderInfo.getRem();
            }

            String total = new StringBuilder()
                    .append(String.valueOf(totalLvol))
                    .append("  /  ")
                    .append(String.valueOf(totalRem))
                    .toString();
            this.totalSummary.setText(total);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 注番一覧の項目を選択されたタイミングでのアクション
     */
    private final EventHandler<MouseEvent> eventHandlerForTableView = (MouseEvent event) -> {
        try {
            this.serialList.removeListener(this);

            if (this.lotProductTableView.getItems().isEmpty()) {
                return;
            }
            // 選択した行の取得
            int currentRow = this.lotProductTableView.getSelectionModel().getSelectedIndex();

            this.selectTableViewRow(currentRow);

            // 指示数の合計表示を更新する。
            this.dispTotal();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.serialList.addListener(this);
        }
    };

    /**
     * フォーカスが解除されたら入力内容を検証する
     */
    private final ChangeListener<Boolean> changeListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if (!newValue) {
            if (Objects.isNull(kanbanInfo)) {
                // 入力項目をチェックする。
                this.validCreateKanban();
            } else {
                if (!kanbanInfo.getKanbanName().equals(this.kanbanNameTextField.getText())) {
                    // カンバン名に変更があった場合、登録ボタンを活性にする。
                    this.createButton.setDisable(false);
                }
            }
        }
    };

    /**
     * カンバン作成画面を初期化する
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("initialize start.");

            if (StringUtils.equals(this.properties.getProperty("integration"), "denno")) {
                this.dbAccess = new DennoAccess();
            } else {
                this.dbAccess = new KanbanBaseInfoAccessELS();
            }
            
            lotProductTableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

            if (!this.properties.containsKey(Config.USE_BARCODE)) {
                this.properties.put(Config.USE_BARCODE, "false");
            }

            // ELS版はカンバン名入力欄は使用しない。
            this.kanbanNameHbox.setDisable(true);
            this.kanbanNameTextField.setDisable(true);
            this.lotProductTextField.setDisable(true);

            // 作業指示コードラベル
            String codeLabelText = KanbanEditConfigELS.getIndtructionCodeLabelText();
            if (codeLabelText.isEmpty()) {
                codeLabelText = LocaleUtils.getString("key.InstructionCode");
                KanbanEditConfigELS.setIndtructionCodeLabelText(codeLabelText);
            }

            this.instructionCodeLabel.setText(codeLabelText);

            // カンバン名
            this.kanbanNameTextField.focusedProperty().addListener(this.changeListener);
            this.kanbanNameTextField.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    // 作業指示コード入力欄にフォーカス移動する。
                    this.instructionCodeTextField.requestFocus();
                }
            });

            // 作業指示コード入力欄
            this.instructionCodeTextField.textProperty().bindBidirectional(this.instructionCode);
            this.instructionCodeTextField.focusedProperty().addListener(this.changeListener);
            this.instructionCodeTextField.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    // 作業指示コードでカンバン基本情報を取得して画面にセットする。
                    this.readInstruction();
                }
            });

            // 先頭シリアル番号入力欄
            this.serialFromTextField.textProperty().bindBidirectional(this.serialNumberFrom);
            this.serialFromTextField.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    // 末尾シリアル番号入力欄にフォーカス移動する。
                    this.serialToTextField.requestFocus();
                }
            });

            // 末尾シリアル番号入力欄
            this.serialToTextField.textProperty().bindBidirectional(this.serialNumberTo);
            this.serialToTextField.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    // シリアル番号を追加する。
                    this.addSerial();
                }
            });

            // シリアル番号リスト
            this.serialListView.setItems(this.serialList);
            this.serialListView.fixedCellSizeProperty().set(30.0);
            this.serialListView.setCellFactory(new Callback<ListView<ColorTextCellData>, ListCell<ColorTextCellData>>() {
                @Override
                public ListCell<ColorTextCellData> call(ListView<ColorTextCellData> param) {
                    return new ColorTextCell(param);
                }
            });

            this.serialListView.setEditable(true);

            this.serialList.addListener(this);

            // シリアル番号の数
            this.serialCountTextField.textProperty().bindBidirectional(this.serialCount, new NumberStringConverter());
            this.serialCountTextField.focusedProperty().addListener(this.changeListener);

            // 指示数変更イベント
            this.serialCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    if (newValue.isEmpty()
                            || !this.lotProductCheckBox.isSelected()
                            || this.lotProductTableView.getSelectionModel().isEmpty()) {
                        return;
                    }

                    KanbanBaseInfoEntity kanbanBaseInfo = this.lotProductTableView.getSelectionModel().getSelectedItem();
                    OrderInfoEntity orderInfo = kanbanBaseInfo.getOrderInfo();
                    orderInfo.setLvol(this.serialCount.get());

                    // 指示数の合計表示を更新する。
                    this.dispTotal();

                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });

            // ロット数量
            this.instructionLotTextField.textProperty().bindBidirectional(this.instructionLot, new NumberStringConverter());
            this.instructionLotTextField.focusedProperty().addListener(this.changeListener);

            // 追加情報
            this.updateProperty(null);

            this.porderTableColumn.setCellValueFactory((TableColumn.CellDataFeatures<KanbanBaseInfoEntity, String> param) -> param.getValue().getOrderInfo().porderProperty());
            this.kikakuKatasikiTableColumn.setCellValueFactory((TableColumn.CellDataFeatures<KanbanBaseInfoEntity, String> param) -> param.getValue().getOrderInfo().kikakuKatasikiProperty());
            this.lvolTableColumn.setCellValueFactory((TableColumn.CellDataFeatures<KanbanBaseInfoEntity, Number> param) -> param.getValue().getOrderInfo().lvolProperty());
            this.remainTableColumn.setCellValueFactory((TableColumn.CellDataFeatures<KanbanBaseInfoEntity, Number> param) -> param.getValue().getOrderInfo().remProperty());

            // 注番一覧が追加・削除された時に指示数と残り台数の合計値を設定するリスナーを登録
            this.lotProductTableView.getItems().addListener(this.listChangeListener);
            // 注番一覧の項目を選択した時のリスナーを登録
            this.lotProductTableView.setOnMouseClicked(this.eventHandlerForTableView);

            // カンバン基本情報の取得機能が使用可能か
            this.IsReadAvailable = this.dbAccess.IsAvailable();
            this.readInstructionButton.setDisable(!this.IsReadAvailable);
            if (!this.IsReadAvailable) {
                // 作業指示情報の読み込みが使用できません
                DialogBox.warn("key.Warning", "key.ReadInstructionNotAvailable");
            }

            this.loadProperties();
            this.setDisableControl(this.lotProductCheckBox.isSelected());

            this.lotProductTableView.setItems(this.kanbanBaseInfoList);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.progressPane.setVisible(false);
            logger.info("initialize end.");
        }
    }

    /**
     * パラメータを設定する
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectedKanbanAndHierarchy) {
            this.kanbanHierarchy = (SelectedKanbanAndHierarchy) argument;
            this.createButton.setDisable(true);

            logger.info("KanbanMultipleRegistCompo:{}", LocaleUtils.getString("key.KanbanContinuousCreate"));

            boolean useBarcode = StringUtils.parseBoolean(this.properties.getProperty(Config.USE_BARCODE));
            if (useBarcode) {
                try {
                    this.kanbanEditPermanenceData.connectSerialComm(this);
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.ConnectionErrorTitle"), LocaleUtils.getString("key.SerialCommErrUnconnection"));
                    });
                }
            }
        } else if (argument instanceof KanbanInfoEntity) {
            try {
                this.serialList.removeListener(this);
                this.isEditMode = true;

                KanbanInfoEntity kanban = (KanbanInfoEntity) argument;
                this.kanbanInfo = kanban;

                // サービス情報
                List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(kanban.getServiceInfo(), ServiceInfoEntity[].class);
                Optional<ServiceInfoEntity> serviceInfoOpt = serviceInfos.stream()
                        .filter(p -> Objects.equals(p.getService(), SERVICE_INFO_LOT))
                        .findFirst();

                ServiceInfoEntity serviceInfo;
                if (serviceInfoOpt.isPresent()) {
                    serviceInfo = serviceInfoOpt.get();
                } else {
                    serviceInfo = new ServiceInfoEntity();
                }

                // カンバン名
                this.kanbanNameTextField.setText(kanban.getKanbanName());
                // 工程順
                this.lotProductTextField.setText(kanban.getWorkflowName());
                // チェックボックス
                this.lotProductCheckBox.setSelected(true);
                // 注番一覧
                List<LinkedHashMap<String, Object>> linkedHashMaps = (List<LinkedHashMap<String, Object>>) serviceInfo.getJob();
                for (LinkedHashMap<String, Object> linkedHashMap : linkedHashMaps) {
                    // LinkedHashMap型からOrderInfoEntity型へ変換
                    OrderInfoEntity orderInfo = new OrderInfoEntity(linkedHashMap);

                    KanbanBaseInfoEntity kanbanBaseInfo = new KanbanBaseInfoEntity();
                    kanbanBaseInfo.setOrderInfo(orderInfo);

                    // 追加情報
                    int propOrder = 1;
                    List<KanbanBaseInfoPropertyEntity> kanbanBaseList = new ArrayList<>();
                    KanbanBaseInfoPropertyEntity kanbanBase = new KanbanBaseInfoPropertyEntity("PORDER", CustomPropertyTypeEnum.TYPE_STRING, orderInfo.getPorder(), propOrder++);
                    kanbanBaseList.add(kanbanBase);
                    kanbanBase = new KanbanBaseInfoPropertyEntity("HINMEI", CustomPropertyTypeEnum.TYPE_STRING, orderInfo.getHinmei(), propOrder++);
                    kanbanBaseList.add(kanbanBase);
                    kanbanBase = new KanbanBaseInfoPropertyEntity("KIKAKU_KATASIKI", CustomPropertyTypeEnum.TYPE_STRING, orderInfo.getKikakuKatasiki(), propOrder++);
                    kanbanBaseList.add(kanbanBase);
                    kanbanBase = new KanbanBaseInfoPropertyEntity("SYANAI_ZUBAN", CustomPropertyTypeEnum.TYPE_STRING, orderInfo.getSyanaiZuban(), propOrder++);
                    kanbanBaseList.add(kanbanBase);
                    kanbanBase = new KanbanBaseInfoPropertyEntity("SYANAI_COMMENT", CustomPropertyTypeEnum.TYPE_STRING, orderInfo.getSyanaiComment(), propOrder++);
                    kanbanBaseList.add(kanbanBase);
                    kanbanBase = new KanbanBaseInfoPropertyEntity("KBAN", CustomPropertyTypeEnum.TYPE_STRING, orderInfo.getKban(), propOrder++);
                    kanbanBaseList.add(kanbanBase);
                    kanbanBaseInfo.setPropertyCollection(kanbanBaseList);

                    this.kanbanBaseInfoList.add(kanbanBaseInfo);
                }

                // 最初の行を選択させる
                int selectRow = 0;
                this.lotProductTableView.getSelectionModel().select(selectRow);
                this.selectTableViewRow(selectRow);

                // すべての項目を編集不可にする
                for (int index = 0; index < this.kanbanSettingGrid.getChildren().size(); index++) {
                    // 注番一覧の注番選択は可能にする
                    // 注番一覧はTableViewで表示しているため、TableView以外の部品はDisable
                    if (!this.kanbanSettingGrid.getChildren().get(index).getClass().equals(TableView.class)) {
                        this.kanbanSettingGrid.getChildren().get(index).setDisable(true);
                    }
                }

                // カンバン名を有効にする
                this.kanbanNameTextField.setDisable(false);
                this.kanbanNameHbox.setDisable(false);

                // カンバンの登録ボタンを無効化
                this.createButton.setDisable(true);
            } finally {
                this.serialList.addListener(this);
            }
        }
    }

    /**
     * シリアル番号リストが変更された。
     *
     * @param change
     */
    @Override
    public void onChanged(ListChangeListener.Change change) {
        try {
            this.serialList.removeListener(this);

            // シリアル番号が追加されたらシリアル番号の数を更新
            if (this.lotProductCheckBox.isSelected()) {
                int currentRow = this.lotProductTableView.getSelectionModel().getSelectedIndex();
                if (currentRow == -1) {
                    return;
                }

                KanbanBaseInfoEntity kanbanBaseInfo = this.kanbanBaseInfoList.get(currentRow);
                OrderInfoEntity orderInfo = kanbanBaseInfo.getOrderInfo();

                if (this.serialList.isEmpty()) {
                    orderInfo.setSn(null);
                } else {
                    List<String> serialLists = new ArrayList<>();
                    for (int index = 0; index < this.serialList.size(); index++) {
                        serialLists.add(this.serialList.get(index).getText());
                    }

                    orderInfo.setSn(serialLists);
                    orderInfo.setLvol(serialLists.size());
                }

                this.serialCountTextField.setText(String.valueOf(orderInfo.getLvol()));
            } else {
                this.serialCount.set(this.serialList.size());
            }

            // 入力項目をチェックする。
            this.validCreateKanban();

            this.serialList.sort(Comparator.comparing(item -> item.getText()));
        } finally {
            this.serialList.addListener(this);
        }
    }

    /**
     * 工程順の選択ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onSelectWorkflow(ActionEvent event) {
        try {
            logger.info("onSelectWorkflow start.");

            SelectDialogEntity<WorkflowInfoEntity> selectDialogEntity = new SelectDialogEntity();
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.OrderProcesses"), "WorkflowSingleSelectionCompo", selectDialogEntity, true);
            if (ret.equals(ButtonType.OK) && !selectDialogEntity.getWorkflows().isEmpty()) {
                WorkflowInfoEntity selected = selectDialogEntity.getWorkflows().get(0);
                if (!verifyWorkflow(selected.getWorkflowName())) {
                    return;
                }

                this.blockUI(true);

                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            workflow = workflowFacade.find(selected.getWorkflowId());
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                        } finally {
                            Platform.runLater(() -> {
                                try {
                                    if (Objects.nonNull(workflow) && Objects.nonNull(workflow.getWorkflowName())) {
                                        workflowTextField.setText(workflow.getWorkflowName());
                                    }

                                    // カンバンプロパティ
                                    List<KanbanPropertyInfoEntity> props = new LinkedList<>();
                                    int propOrder = 1;// ※カンバン基本情報と工程順の両方のプロパティをセットするため、オーダー順は設定しなおす。

                                    // カンバン基本情報のカンバンプロパティ
                                    if (Objects.nonNull(kanbanBase)
                                            && Objects.nonNull(kanbanBase.getPropertyCollection())
                                            && !kanbanBase.getPropertyCollection().isEmpty()) {
                                        kanbanBase.getPropertyCollection().sort(Comparator.comparing(item -> item.getKanbanPropertyOrder()));
                                        for (KanbanBaseInfoPropertyEntity baseProp : kanbanBase.getPropertyCollection()) {
                                            props.add(new KanbanPropertyInfoEntity(null, null, baseProp.getKanbanPropertyName(), baseProp.getKanbanPropertyType(), baseProp.getKanbanPropertyValue(), propOrder++));
                                        }
                                    }

                                    // 工程順のカンバンプロパティ
                                    if (!lotProductCheckBox.isSelected()
                                            && Objects.nonNull(workflow)
                                            && Objects.nonNull(workflow.getKanbanPropertyTemplateInfoCollection())
                                            && !workflow.getKanbanPropertyTemplateInfoCollection().isEmpty()) {
                                        workflow.getKanbanPropertyTemplateInfoCollection().sort(Comparator.comparing(item -> item.getKanbanPropOrder()));
                                        for (KanbanPropertyTemplateInfoEntity tempProp : workflow.getKanbanPropertyTemplateInfoCollection()) {
                                            // カンバン基本情報にないプロパティのみ追加する。
                                            if (props.stream().filter(p -> p.getKanbanPropertyName().equals(tempProp.getKanbanPropName())).count() == 0) {
                                                props.add(new KanbanPropertyInfoEntity(null, null, tempProp.getKanbanPropName(), tempProp.getKanbanPropType(), tempProp.getKanbanPropInitialValue(), propOrder++));
                                            }
                                        }
                                    }

                                    updateProperty(props);

                                } catch (Exception ex) {
                                    logger.fatal(ex, ex);
                                }

                                blockUI(false);
                                instructionCodeTextField.requestFocus();
                                // 入力項目をチェックする。
                                validCreateKanban();
                            });
                        }
                        return null;
                    }
                };

                new Thread(task).start();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onSelectWorkflow end.");
        }
    }

    /**
     * 読込ボタンのアクション
     *
     * ※作業指示コードでカンバン基本情報を取得して画面にセットする。
     *
     * @param event
     */
    @FXML
    private void onReadInstruction(ActionEvent event) {
        try {
            // 作業指示コードでカンバン基本情報を取得して画面にセットする。
            this.readInstruction();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 作業指示コードでカンバン基本情報を取得して画面にセットする。
     */
    private void readInstruction () {
        logger.info("readInstruction start.");
        try {
            this.serialList.removeListener(this);

            // カンバン基本情報の取得機能が無効な場合は何もしない。
            if (!this.IsReadAvailable) {
                this.serialFromTextField.requestFocus();
                return;
            }
            // 作業指示コードが未入力の場合は何もしない。
            if (StringUtils.isEmpty(this.instructionCode.get())) {
                this.instructionCodeTextField.requestFocus();
                return;
            }

            // 注番データは最大5個なので、注番一覧に5個登録されていないかをチェック
            if (this.lotProductTableView.getItems().size() >= MAX_PORDER_NUM) {
                DialogBox.warn("key.Warning", "key.alert.instructionInput.overMaxValue");
                this.instructionCodeTextField.requestFocus();
                return;
            }

            // 情報をクリアする。
            this.clearInfo();

            // ----------
            // カンバン基本情報取得
            kanbanBase = this.dbAccess.GetKanbanBaseInfo(this.instructionCode.get());
            if (Objects.isNull(kanbanBase)) {
                // 読み込めませんでした
                DialogBox.warn("key.Warning", "key.KanbanBaseInfoNotRead");
                this.instructionCodeTextField.requestFocus();
                return;
            }

            // 2件目以降の作業指示コード読み込みでは、規格・型式または部門が1件目と異なる注番情報は追加不可。
            if (!this.lotProductTableView.getItems().isEmpty()) {
                OrderInfoEntity firstOrder = this.lotProductTableView.getItems().get(0).getOrderInfo();
                OrderInfoEntity baseOrder = kanbanBase.getOrderInfo();

                // 部門の異なる注番情報は追加不可。
                if (!Objects.equals(firstOrder.getKbumoName(), baseOrder.getKbumoName())) {
                    DialogBox.warn("key.Warning", "key.warn.orderInfo.different.kikakuKatasiki");
                    this.instructionCodeTextField.requestFocus();
                    return;
                }
                
                // 注番情報の規格・型式が異なる場合、警告を表示。
                if (!Objects.equals(firstOrder.getKikakuKatasiki(), baseOrder.getKikakuKatasiki())) {
                        ButtonType buttonType = sc.showMessageBox(
                                Alert.AlertType.WARNING,
                                "規格・型式の確認",
                                "規格・型式が異なります。登録しますか?",
                                new ButtonType[]{ButtonType.YES, ButtonType.NO},
                                ButtonType.NO
                        );
                        if (!ButtonType.YES.equals(buttonType)) {
                            return;
                        }
                }
            }

            // ----------
            // 取得した情報を画面にセットする。
            // 工程順名
            if (Objects.nonNull(kanbanBase.getWorkflowName())) {
                // 工程順を取得
                this.workflow = this.workflowFacade.findName(URLEncoder.encode(kanbanBase.getWorkflowName(), "UTF-8"));

                if (Objects.nonNull(this.workflow) && Objects.nonNull(this.workflow.getWorkflowName())) {
                    this.workflowTextField.setText(this.workflow.getWorkflowName());
                }
            }

            // ロット数量
            if (Objects.nonNull(kanbanBase.getLotQuantity())) {
                this.instructionLot.set(kanbanBase.getLotQuantity());
            }

            // カンバンプロパティ
            List<KanbanPropertyInfoEntity> props = new LinkedList<>();
            int propOrder = 1;// ※カンバン基本情報と工程順の両方のプロパティをセットするため、オーダー順は設定しなおす。

            // カンバン基本情報のカンバンプロパティ
            if (Objects.nonNull(kanbanBase)
                    && Objects.nonNull(kanbanBase.getPropertyCollection())
                    && !kanbanBase.getPropertyCollection().isEmpty()) {
                kanbanBase.getPropertyCollection().sort(Comparator.comparing(item -> item.getKanbanPropertyOrder()));
                for (KanbanBaseInfoPropertyEntity baseProp : kanbanBase.getPropertyCollection()) {
                    props.add(new KanbanPropertyInfoEntity(null, null, baseProp.getKanbanPropertyName(), baseProp.getKanbanPropertyType(), baseProp.getKanbanPropertyValue(), propOrder++));
                }
            }

            // 工程順のカンバンプロパティ
            if (!this.lotProductCheckBox.isSelected()
                    && Objects.nonNull(workflow)
                    && Objects.nonNull(workflow.getKanbanPropertyTemplateInfoCollection())
                    && !workflow.getKanbanPropertyTemplateInfoCollection().isEmpty()) {
                workflow.getKanbanPropertyTemplateInfoCollection().sort(Comparator.comparing(item -> item.getKanbanPropOrder()));
                for (KanbanPropertyTemplateInfoEntity tempProp : workflow.getKanbanPropertyTemplateInfoCollection()) {
                    // カンバン基本情報にないプロパティのみ追加する。
                    if (props.stream().filter(p -> p.getKanbanPropertyName().equals(tempProp.getKanbanPropName())).count() == 0) {
                        props.add(new KanbanPropertyInfoEntity(null, null, tempProp.getKanbanPropName(), tempProp.getKanbanPropType(), tempProp.getKanbanPropInitialValue(), propOrder++));
                    }
                }
            }

            this.updateProperty(props);

            if (this.lotProductCheckBox.isSelected()) {
                OrderInfoEntity baseOrder = kanbanBase.getOrderInfo();

                // 1件目の場合、規格を照合
                if (this.kanbanBaseInfoList.isEmpty() 
                        && Objects.nonNull(this.lotWorkflow)
                        && !verifyWorkflow(this.lotWorkflow.getWorkflowName())) {
                    return;
                }
                
                // 同じ作業指示書を読み込んだ場合
                // 今回読み込んだ情報
                String porder = baseOrder.getPorder(); // 注番
                String kban = kanbanBase.getPropertyCollection().get(5).getKanbanPropertyValue(); // 工程番号
                int instructionIndex = isExistInstruction(porder, kban);
                if (instructionIndex != -1) {
                    // 同じ作業指示書を読み込んだ

                    // 登録している行を選択する
                    this.lotProductTableView.getSelectionModel().select(instructionIndex);
                    // シリアル番号と追加情報の更新
                    this.selectTableViewRow(instructionIndex);

                    DialogBox.warn("key.Warning", "key.warn.instruction.exist");
                    this.instructionCodeTextField.requestFocus();
                    return;
                }

                // 注番情報を設定する
                OrderInfoEntity orderInfo = new OrderInfoEntity(baseOrder);
                // 不良数
                orderInfo.setDefect(0);
                // 残り台数
                int remain = getRemainCount(orderInfo.getPorder());
                if (remain >= 0) {
                    // 残数0以上の場合
                    orderInfo.setRem(remain);
                } else {
                    orderInfo.setRem(orderInfo.getKvol());
                }
                // 指示数
                orderInfo.setLvol(orderInfo.getRem());

                kanbanBase.setOrderInfo(orderInfo);

                // カンバン基本情報リストに情報を追加する。
                this.kanbanBaseInfoList.add(kanbanBase);

                // 注番一覧の追加した行を選択
                int maxTableViewRow = this.lotProductTableView.getItems().size(); // 追加した行
                this.lotProductTableView.getSelectionModel().select(maxTableViewRow - 1); // 0オリジンのため -1
                this.selectTableViewRow(this.lotProductTableView.getSelectionModel().getSelectedIndex());

                // 指示数の合計表示を更新する。
                this.dispTotal();
            }

            // 先頭シリアル入力欄にフォーカス移動。
            this.serialFromTextField.requestFocus();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.instructionCodeTextField.requestFocus();
        } finally {
            this.serialList.addListener(this);
        }
    }

    /**
     * シリアル番号の追加ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onAddSerial(ActionEvent event) {
        try {
            // シリアル番号を追加する。
            this.addSerial();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * シリアル番号を追加する。
     */
    private void addSerial() {
        logger.info("addSerial start.");

        // シリアル番号が未入力の場合、先頭シリアルの入力欄にフォーカス移動
        if (StringUtils.isEmpty(this.serialNumberFrom.get()) && StringUtils.isEmpty(this.serialNumberTo.get())) {
            DialogBox.warn("key.Warning", "key.SerialNumberMissing");
            // 先頭シリアル入力欄にフォーカス移動。
            this.serialFromTextField.requestFocus();
            return;
        }

        // 入力されたシリアル番号
        String fromSerial = this.serialNumberFrom.get();
        String toSerial = this.serialNumberTo.get();

        // シリアル番号が先頭か末尾の片方のみ入力されている場合はもう片方にも同じシリアル番号をセットする。
        if (StringUtils.isEmpty(fromSerial)) {
            fromSerial = toSerial;
        } else if (StringUtils.isEmpty(toSerial)) {
            toSerial = fromSerial;
        }

        String findSerial = toSerial;
        Optional<ColorTextCellData> toSerialData = this.serialList.stream().filter(item -> findSerial.equals(item.getText())).findFirst();// 末尾シリアル

        if (fromSerial.equals(toSerial)) {
            // 先頭シリアルと末尾シリアルが同じ場合はそのまま追加する。
            if (!toSerialData.isPresent()) {
                this.serialList.add(new ColorTextCellData(toSerial));
                toSerialData = this.serialList.stream().filter(item -> findSerial.equals(item.getText())).findFirst();// 末尾シリアル

                // シリアル番号入力欄をクリアする。
                this.serialNumberFrom.set("");
                this.serialNumberTo.set("");
            }
        } else {
            // シリアル番号を前半文字列部と後半数値部に分解する。
            String[] sepCodeFrom = this.spritSerialNumber(fromSerial);
            String[] sepCodeTo = this.spritSerialNumber(toSerial);

            // シリアル番号の前半文字列部が同じで、かつ後半数値部の桁数が同じ場合のみ、シリアル番号の範囲追加を行なう。
            if (sepCodeFrom[0].equals(sepCodeTo[0]) && sepCodeFrom[1].length() == sepCodeTo[1].length()) {
                int fromNum = StringUtils.parseInteger(sepCodeFrom[1]);
                int toNum = StringUtils.parseInteger(sepCodeTo[1]);
                if (fromNum > toNum) {
                    int bufNum = fromNum;
                    fromNum = toNum;
                    toNum = bufNum;
                }

                // シリアル番号のフォーマット
                String serialFormat = "%s%0" + Integer.toString(sepCodeFrom[1].length()) + "d";

                // シリアル番号をリストに追加
                for (int serNum = fromNum; serNum <= toNum; serNum++) {
                    // 追加するシリアル番号
                    String serialNumber = String.format(serialFormat, sepCodeFrom[0], serNum);

                    // リストに無い場合のみ追加する。
                    long itemCount = this.serialList.stream().filter(item -> serialNumber.equals(item.getText())).count();
                    if (itemCount == 0) {
                        this.serialList.add(new ColorTextCellData(serialNumber));
                    }
                }
                toSerialData = this.serialList.stream().filter(item -> findSerial.equals(item.getText())).findFirst();// 末尾シリアル

                // シリアル番号入力欄をクリアする。
                this.serialNumberFrom.set("");
                this.serialNumberTo.set("");
            } else {
                // シリアル番号のフォーマットが異なります
                DialogBox.warn("key.Warning", "key.SerialNumberFormatError");
            }
        }

        // シリアルリストは入力した末尾シリアルにフォーカス移動する。
        if (toSerialData.isPresent()) {
            this.serialListView.scrollTo(toSerialData.get());
            this.serialListView.getSelectionModel().select(toSerialData.get());
            this.serialListView.getFocusModel().focus(this.serialListView.getSelectionModel().getSelectedIndex());
        }

        // 先頭シリアル入力欄にフォーカス移動。
        this.serialFromTextField.requestFocus();

        // 指示数の合計表示を更新する。
        this.dispTotal();
    }

    /**
     * キャンセルボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onClose(ActionEvent event) {
        try {
            logger.info("onClose start.");

            boolean isClose = true;
            if (Objects.isNull(kanbanInfo)) {
                // 入力項目をチェックする。
                this.saveProperties();

                this.properties.put(Config.DEFAULT_WORKFLOW, this.workflowTextField.getText());

                this.kanbanEditPermanenceData.disconnectSerialComm();
            } else {
                if (!kanbanInfo.getKanbanName().equals(this.kanbanNameTextField.getText())) {
                    // カンバン名に変更があった場合、「入力内容が保存されていません。保存しますか?」を表示
                    String title = LocaleUtils.getString("key.confirm");
                    String message = LocaleUtils.getString("key.confirm.destroy");

                    ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                    if (Objects.equals(buttonType, ButtonType.YES)) {
                        this.updateKanban();
                        isClose = false;
                    } else if (Objects.equals(buttonType, ButtonType.CANCEL)) {
                        isClose = false;
                    }
                }
            }

            if (isClose) {
                sc.setComponent("ContentNaviPane", "KanbanListCompo");
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onClose end.");
        }
    }

    /**
     * 作成ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onCreate(ActionEvent event) {
        try {
            logger.info("onCreate start.");

            if (Objects.isNull(kanbanInfo)) {
                // カンバンを作成する。
                this.createKanban();
            } else {
                // カンバンを更新する。
                this.updateKanban();
            }
        } finally {
            logger.info("onCreate end.");
        }
    }

    /**
     * ロット生産 チェックボックスのアクション
     *
     * @param event
     */
    @FXML
    private void onLotProductChanged(ActionEvent event) {
        logger.info("onLotProductChanged start.");
        try {
            boolean isClear = false;
            boolean isSelected = this.lotProductCheckBox.isSelected();
            if (!isSelected) {
                // 入力途中かどうか判定
                // TableViewの要素数が0より大きければ入力途中判定とする
                if (!this.lotProductTableView.getItems().isEmpty() || this.isEditingCommonItems()) {
                    DialogBox.Status isOK = DialogBox.question("key.confirm", "key.confirm.dataDestruction");
                    if (DialogBox.Status.OK == isOK) {
                        // OKボタン押下
                        isClear = true;
                        this.lotProductTableView.getItems().clear();
                        // ロット生産用工程情報のクリア
                        this.kanbanBaseInfoList.clear();
                        // ロット生産の工程順のクリア
                        this.lotWorkflow = new WorkflowInfoEntity();
                        this.lotProductTextField.setText("");
                    } else {
                        // キャンセルボタン押下
                        this.lotProductCheckBox.setSelected(true);
                        isSelected = true;
                    }
                }
            } else {
                isClear = true;
            }

            if (isClear) {
                this.workflowTextField.setText("");
                this.kanbanNameTextField.setText("");
                this.instructionCodeTextField.setText("");
                this.serialList.clear();
                this.serialCount.set(0);
                this.updateProperty(null);
            }

            this.setDisableControl(isSelected);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 共通項目を編集中かどうかを取得する。
     *
     * @return 共通項目を編集中かどうか ()
     */
    private boolean isEditingCommonItems() {
        if (!StringUtils.isEmpty(this.workflowTextField.getText())
                || !StringUtils.isEmpty(this.kanbanNameTextField.getText())
                || !StringUtils.isEmpty(this.instructionCodeTextField.getText())
                || !this.propertyList.isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * 工程順を検証する。
     * 注番情報の規格・型式が異なる場合、警告を表示する。
     * 
     * @param workflowName 工程順名
     * @return true: 使用可、false: 使用不可
     */
    private boolean verifyWorkflow(String workflowName) {
        boolean warning = false;

        if (!warning) {
            if (!this.kanbanBaseInfoList.isEmpty()) {
                OrderInfoEntity order = this.lotProductTableView.getItems().get(0).getOrderInfo();
                if (!workflowName.startsWith(order.getKikakuKatasiki())) {
                    warning = true;
                }
            } else if (Objects.nonNull(this.kanbanBase)) {
                if (!workflowName.startsWith(this.kanbanBase.getOrderInfo().getKikakuKatasiki())) {
                    warning = true;
                }
            }
        }

        if (warning) {
            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.WARNING, "工程順、規格・型式の確認",
                    "規格・型式が異なります。登録しますか?", new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
            if (!ButtonType.YES.equals(buttonType)) {
                return false;
            }
        }
        
        return true;
    }
    
    
    /**
     * ロット生産用の工程順の選択ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onSelectLotWorkflow(ActionEvent event) {
        try {
            logger.info("onSelectLotWorkflow start.");

            SelectDialogEntity<WorkflowInfoEntity> selectDialogEntity = new SelectDialogEntity();
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.OrderProcesses"), "WorkflowSingleSelectionCompo", selectDialogEntity, true);
            if (ret.equals(ButtonType.OK) && !selectDialogEntity.getWorkflows().isEmpty()) {
                WorkflowInfoEntity selected = selectDialogEntity.getWorkflows().get(0);
                if (!verifyWorkflow(selected.getWorkflowName())) {
                    return;
                }
                
                this.blockUI(true);

                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            lotWorkflow = workflowFacade.find(selected.getWorkflowId());
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                        } finally {
                            Platform.runLater(() -> {
                                try {
                                    lotProductTextField.setText(lotWorkflow.getWorkflowName());
                                } catch (Exception ex) {
                                    logger.fatal(ex, ex);
                                }

                                blockUI(false);
                                instructionCodeTextField.requestFocus();
                                // 入力項目をチェックする。
                                validCreateKanban();
                            });
                        }
                        return null;
                    }
                };

                new Thread(task).start();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onSelectLotWorkflow end.");
        }
    }

    /**
     * 注番削除ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onPorderDelete(ActionEvent event) {
        logger.info("onPorderDelete start.");
        try {
            this.serialList.removeListener(this);

            if (this.lotProductTableView.getItems().isEmpty()) {
                return;
            }

            int currentRow = this.lotProductTableView.getSelectionModel().getSelectedIndex();
            this.kanbanBaseInfoList.remove(currentRow);

            // 画面表示のクリア
            this.updateProperty(null);
            this.serialList.clear();

            // 削除後、選択中の情報を表示する
            if (!this.lotProductTableView.getItems().isEmpty()) {
                this.lotProductTableView.getSelectionModel().select(currentRow);
                this.selectTableViewRow(this.lotProductTableView.getSelectionModel().getSelectedIndex());
            }

            // 指示数の合計表示を更新する。
            this.dispTotal();

            // 入力項目をチェックする。
            this.validCreateKanban();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.serialList.addListener(this);
        }
    }

    /**
     * バーコード読み取りのアクション
     *
     * @param message
     */
    @Override
    public void listner(String message) {
        Platform.runLater(() -> {
            try {
                logger.info("Input the barcode:{}", message);

                if (this.instructionCodeTextField.isFocused()) {
                    // 作業指示書
                    this.instructionCode.set(message);
                    // 作業指示コードでカンバン基本情報を取得して画面にセットする。
                    this.readInstruction();
                } else if (this.serialFromTextField.isFocused()) {
                    // 先頭シリアル番号
                    this.serialNumberFrom.set(message);
                    this.serialToTextField.requestFocus();
                } else if (this.serialToTextField.isFocused()) {
                    // 末尾シリアル番号
                    this.serialNumberTo.set(message);
                    // シリアル番号を追加する。
                    this.addSerial();
                }

                // 入力項目をチェックする。
                this.validCreateKanban();

            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });
    }

    /**
     * カンバンプロパティをコピーする
     *
     * @param source
     * @return
     */
    private List<KanbanPropertyInfoEntity> copyProperty(List<KanbanPropertyInfoEntity> source) {
        List<KanbanPropertyInfoEntity> copy = new ArrayList<>();
        source.stream().forEach(entity -> {
            copy.add(new KanbanPropertyInfoEntity(entity));
        });
        return copy;
    }

    /**
     * 操作をロックする。
     *
     * @param block
     */
    private void blockUI(Boolean block) {
        sc.blockUI("ContentNaviPane", block);
        this.progressPane.setVisible(block);
    }

    /**
     * カンバンを更新する。
     */
    private void updateKanban() {
        logger.info("updateKanban start.");
        
        if (!this.kanbanNameTextField.getText().equals(this.kanbanInfo.getKanbanName())) {
            this.kanbanInfo.setKanbanName(this.kanbanNameTextField.getText());
            // カンバン名が変更されている場合、更新
            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
                    //保存処理実行
                    return kanbanFacade.update(kanbanInfo);
                }
            };
            new Thread(task).start();
        }
        sc.setComponent("ContentNaviPane", "KanbanListCompo");
    }
    
    /**
     * カンバンを作成する。
     */
    private void createKanban() {
        try {
            logger.info("createKanban start.");

            // 入力項目をチェックする。
            if (!this.validCreateKanban()) {
                // アラート
                DialogBox.warn(Locale.WARN_INPUT_REQUIRED, Locale.WARN_INPUT_REQUIRED_DETAILS);
                return;
            }

            for (KanbanPropertyInfoEntity property : this.propertyList) {
                property.updateMember();
                if (StringUtils.isEmpty(property.getKanbanPropertyName()) || Objects.isNull(property.getKanbanPropertyType())) {
                    // アラート
                    DialogBox.warn(Locale.WARN_INPUT_REQUIRED, Locale.WARN_INPUT_REQUIRED_DETAILS);
                    return;
                }
            }

            boolean isLot = this.lotProductCheckBox.isSelected();

            // １個流しでロット数量がシリアル番号リストの数と異なるか、
            // ロット流しで指示数とシリアル番号の数が異なる注番がある場合は警告する。
            boolean isLotNumErr = false;
            if (isLot) {
                for (KanbanBaseInfoEntity kanbanBaseInfo : this.kanbanBaseInfoList) {
                    OrderInfoEntity orderInfo = kanbanBaseInfo.getOrderInfo();
                    if (Objects.isNull(orderInfo.getSn()) || orderInfo.getSn().isEmpty()) {
                        // シリアル番号の指定がない場合は警告なし。
                        continue;
                    }

                    if (!Objects.equals(orderInfo.getLvol(), orderInfo.getSn().size())) {
                        // 指示数とシリアル番号の数が異なる。
                        isLotNumErr = true;
                    }
                }

                if (Objects.nonNull(this.workflow)
                        && Objects.nonNull(this.workflow.getWorkflowName())
                        && Objects.nonNull(this.lotWorkflow)
                        && Objects.nonNull(this.lotWorkflow.getWorkflowName())) {
                    // 工程順名の比較文字数
                    int compareNum = KanbanEditConfigELS.getWorkflowNameCompareNum();

                    // 工程順名の先頭文字列
                    String lotFlow;
                    if (this.lotWorkflow.getWorkflowName().length() > compareNum) {
                        lotFlow = this.lotWorkflow.getWorkflowName().substring(0, compareNum);
                    } else {
                        lotFlow = this.lotWorkflow.getWorkflowName();
                    }

                    // 1個流しの工程順名の先頭文字列
                    String onePieceFlow;
                    if (this.workflow.getWorkflowName().length() > compareNum) {
                        onePieceFlow = this.workflow.getWorkflowName().substring(0, compareNum);
                    } else {
                        onePieceFlow = this.workflow.getWorkflowName();
                    }

                    // 工程順名の先頭の文字列が、1個流しの工程順名の先頭の文字列と異なる場合、警告を表示する。
                    if (!StringUtils.equals(lotFlow, onePieceFlow)) {
                        // 1個流しとロット生産の工程順名が一致していません。\nカンバンを登録しますか？
                        ButtonType buttonType = sc.showMessageBox(
                                Alert.AlertType.WARNING,
                                LocaleUtils.getString("key.RegistKanban"),// カンバンの登録
                                LocaleUtils.getString("key.warn.notMatch.workflow"),
                                new ButtonType[]{ButtonType.YES, ButtonType.NO},
                                ButtonType.NO
                        );
                        if (!ButtonType.YES.equals(buttonType)) {
                            return;
                        }
                    }
                }
            } else if (this.serialList.size() != this.serialCount.getValue()) {
                // １個流しでロット数量がシリアル番号リストの数と異なる。
                isLotNumErr = true;
            }

            if (isLotNumErr) {
                // ロット数量がシリアル番号の数と異なりますが、保存しますか?
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.LotNumberDifferent");
                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
                if (!ButtonType.YES.equals(buttonType)) {
                    return;
                }
            }

            OrderInfoEntity orderInfo;
            String judgKikakuKatasiki;
            String judgWorkflowName = this.workflowTextField.getText();

            if (judgWorkflowName.length() > 4) {
                // 工程順名の先頭4文字を切り出し
                judgWorkflowName = judgWorkflowName.substring(0, 4);
            }

            for (int i = 0; i < this.lotProductTableView.getItems().size(); i++) {
                orderInfo = this.lotProductTableView.getItems().get(i).getOrderInfo();
                judgKikakuKatasiki = orderInfo.getKikakuKatasiki();

                if (judgKikakuKatasiki.length() > 4) {
                    // 規格・型式の先頭4文字を切り出し
                    judgKikakuKatasiki = judgKikakuKatasiki.substring(0, 4);
                }

                if (!StringUtils.equals(judgWorkflowName, judgKikakuKatasiki)) {
                    // 工程順と規格・型式の先頭4文字を照合し、異なっている場合、警告を表示。
                    ButtonType buttonType = sc.showMessageBox(
                            Alert.AlertType.WARNING,
                            "規格・型式の確認",
                            "規格・型式が異なりますが登録しますか",
                            new ButtonType[]{ButtonType.YES, ButtonType.NO},
                            ButtonType.NO
                    );
                    if (!ButtonType.YES.equals(buttonType)) {
                        return;
                    }
                }
            }
            
            this.blockUI(true);

            Task task;
            task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        logger.info("createKanban thread start.");
                        if (isLot) {
                            createLotProductKanbanThread();
                        } else {
                            createKanbanThread();
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        Platform.runLater(() -> {
                            DialogBox.alert(ex);
                        });
                    } finally {
                        Platform.runLater(() -> {
                            blockUI(false);
                        });
                        logger.info("createKanban thread end.");
                    }
                    return null;
                }
            };

            new Thread(task).start();
        } finally {
            logger.info("createKanban end.");
        }
    }

    /**
     * カンバンを作成する。
     *
     * @throws Exception
     */
    private void createKanbanThread() throws Exception {
        List<String> okList = new ArrayList<>();
        List<String> ngList = new ArrayList<>();
        List<String> skipList = new ArrayList<>();

        // 全シリアル共通の情報
        Long workflowId = this.workflow.getWorkflowId();
        String workflowName = this.workflow.getWorkflowName();
        String modelName = this.workflow.getModelName();
        Long hierarchyId = this.kanbanHierarchy.getHierarchyId();
        Long loginUserId = this.loginUser.getId();
        Date updateDatetime = new Date();

        List<KanbanPropertyInfoEntity> props = copyProperty(this.propertyList);

        // プロパティの注番をサブカンバン名にする。
        String kanbanSubName = null;
        Optional<KanbanPropertyInfoEntity> porderProp = this.propertyList.stream().filter(prop -> Objects.equals(prop.getKanbanPropertyName(), LocaleUtils.getString("key.Porder"))).findFirst();
        if (porderProp.isPresent()) {
            kanbanSubName = porderProp.get().getKanbanPropertyValue();
        }

        // プロパティ表示順を更新する。
        int propOrder = 0;
        for (KanbanPropertyInfoEntity property : props) {
            property.setKanbanPropertyOrder(propOrder++);
        }

        // シリアル番号をプロパティに追加
        String serialNumberPropertyKey = LocaleUtils.getString("key.SerialNumber");
        KanbanPropertyInfoEntity serialProp = new KanbanPropertyInfoEntity(null, null, serialNumberPropertyKey, CustomPropertyTypeEnum.TYPE_STRING, "", propOrder++);
        props.add(serialProp);

        // ロット数量をプロパティに追加
        KanbanPropertyInfoEntity lotSizeProp = new KanbanPropertyInfoEntity(null, null, LocaleUtils.getString("key.LotSize"), CustomPropertyTypeEnum.TYPE_INTEGER, this.instructionLot.getValue().toString(), propOrder++);
        props.add(lotSizeProp);

        // シリアル番号毎にカンバンを作成する。
        for (ColorTextCellData listItem : this.serialList) {
            String serialNumber = listItem.getText();
            boolean isCreate = false;
            boolean isUpdate = false;
            KanbanInfoEntity kanban = new KanbanInfoEntity();
            try {
                // 対象シリアル番号のカンバンが登録済かチェックする。
                KanbanSearchCondition condition = new KanbanSearchCondition()
                        .kanbanName(serialNumber)
                        .workflowId(this.workflow.getWorkflowId());
                List<KanbanInfoEntity> findKanbans = this.kanbanFacade.findSearch(condition);
                if (!findKanbans.isEmpty()) {
                    Optional<KanbanInfoEntity> findKanban = findKanbans.stream().filter(k -> serialNumber.equals(k.getKanbanName())).findFirst();
                    if (findKanban.isPresent()) {
                        // 登録済のためスキップ
                        skipList.add(serialNumber);
                        continue;
                    }
                }

                // 全シリアル共通の情報をセット (プロパティは後で追加)
                kanban.setKanbanName(serialNumber);// カンバン名 (シリアル番号)
                kanban.setKanbanSubname(kanbanSubName);// サブカンバン名 (注文番号)
                kanban.setFkWorkflowId(workflowId);// 工程順ID
                kanban.setWorkflowName(workflowName);// 工程順名
                kanban.setModelName(modelName);// モデル名
                kanban.setParentId(hierarchyId);// カンバン階層ID
                kanban.setFkUpdatePersonId(loginUserId);// 更新者
                kanban.setUpdateDatetime(updateDatetime);// 更新日時

                // カンバンを登録
                ResponseEntity response = this.kanbanFacade.regist(kanban);
                if (!response.isSuccess()) {
                    // 登録失敗
                    ngList.add(serialNumber);
                    continue;
                }
                isCreate = true;

                kanban = this.kanbanFacade.findURI(response.getUri());
                kanban.getWorkKanbanCollection().clear();
                kanban.getSeparateworkKanbanCollection().clear();

                Long workkanbanCnt = this.workKanbanFacade.countFlow(kanban.getKanbanId());
                for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                    kanban.getWorkKanbanCollection().addAll(this.workKanbanFacade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
                }

                Long separateCnt = this.workKanbanFacade.countSeparate(kanban.getKanbanId());
                for (long nowCnt = 0; nowCnt < separateCnt; nowCnt += RANGE) {
                    kanban.getSeparateworkKanbanCollection().addAll(this.workKanbanFacade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
                }

                // プロパティ
                kanban.setPropertyCollection(copyProperty(props));

                // プロパティのシリアル番号の値のみ更新
                Optional<KanbanPropertyInfoEntity> findKanbanProp = kanban.getPropertyCollection().stream()
                        .filter(p -> serialNumberPropertyKey.equals(p.getKanbanPropertyName())).findFirst();
                if (findKanbanProp.isPresent()) {
                    KanbanPropertyInfoEntity kanbanProp = findKanbanProp.get();
                    kanbanProp.setKanbanPropertyValue(serialNumber);
                }

                // 基準時間を設定
                WorkflowProcess workflowProcess = new WorkflowProcess(this.workflow);
                List<BreakTimeInfoEntity> breakTimes = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanban.getWorkKanbanCollection());
                Date baseDatetime = new Date();
                List<HolidayInfoEntity> holidays = WorkKanbanTimeReplaceUtils.getHolidays(baseDatetime);
                workflowProcess.setBaseTime(kanban, breakTimes, baseDatetime, holidays);

                // 計画済みにして登録
                kanban.setKanbanStatus(KanbanStatusEnum.PLANNED);
                kanban.setUpdateDatetime(new Date());
                response = this.kanbanFacade.update(kanban);
                if (response.isSuccess()) {
                    // 登録成功
                    isUpdate = true;
                    okList.add(serialNumber);
                } else {
                    // 登録失敗
                    ngList.add(serialNumber);
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                // 登録失敗
                ngList.add(serialNumber);
            }

            // 作成に成功して更新に失敗した場合は削除する。
            if (isCreate && !isUpdate && Objects.nonNull(kanban.getKanbanId())) {
                this.kanbanFacade.delete(kanban.getKanbanId());
            }
        }

        if (skipList.isEmpty() && ngList.isEmpty()) {
            // 全て成功
            Platform.runLater(() -> {
                // 情報をクリアする。
                this.clearInfo();
                this.instructionCode.set("");
                this.infoCreateKanban();
                this.instructionCodeTextField.requestFocus();
            });
        } else {
            // スキップまたは失敗ありの場合、シリアル番号リストをスキップ・失敗したシリアルのみにする。
            Platform.runLater(() -> {
                this.serialList.clear();

                // スキップしたシリアル
                skipList.stream().forEach(targetSerialNumber -> {
                    this.serialList.add(new ColorTextCellData(targetSerialNumber, COLOR_SKIP));
                });

                // 失敗したシリアル
                ngList.stream().forEach(targetSerialNumber -> {
                    this.serialList.add(new ColorTextCellData(targetSerialNumber, COLOR_NG));
                });

                // カンバンが作成できなかったシリアル番号があります
                DialogBox.warn("key.Warning", "key.SerialNumberKanbanNotCreate");
                this.instructionCodeTextField.requestFocus();
            });
        }
    }

    /**
     * ロット生産カンバンを作成する。
     *
     * @throws Exception
     */
    private void createLotProductKanbanThread() throws Exception {
        // ロット生産用カンバンを作成
        if (!this.createLotProductKanban()) {
            return;
        }

        Platform.runLater(() -> {
            // 情報をクリアする。
            this.clearInfo();
            this.instructionCode.set("");

            // メッセージを表示する。
            this.infoCreateKanban();

            this.kanbanNameTextField.setText("");
            this.kanbanNameTextField.requestFocus();

            // ロット生産用工程情報をクリアする。
            this.kanbanBaseInfoList.clear();
            // 指示数の合計表示を更新する。
            this.dispTotal();

            // 工程順はクリアしない。
        });
    }

    /**
     * 入力項目を検証して、問題がなければ作成ボタンを有効にする
     *
     * @return
     */
    private boolean validCreateKanban() {
        try {
            // 「カンバンの編集」ボタンで遷移した時
            if (isEditMode) {
                this.createButton.setDisable(true);
                return false;
            }

            if (this.lotProductCheckBox.isSelected()) {
                // ロット生産の工程順
                if (Objects.isNull(this.lotWorkflow) || Objects.isNull(this.lotWorkflow.getWorkflowId())) {
                    this.createButton.setDisable(true);
                    return false;
                }

                // 注番情報
                if (this.kanbanBaseInfoList.isEmpty()) {
                    this.createButton.setDisable(true);
                    return false;
                }

                if (StringUtils.isEmpty(this.kanbanNameTextField.getText())) {
                    this.createButton.setDisable(true);
                    return false;
                }

                if (StringUtils.isEmpty(this.lotProductTextField.getText())) {
                    this.createButton.setDisable(true);
                    return false;
                }

                // シリアル番号
                boolean isExistSerial = false;
                for (KanbanBaseInfoEntity kanbanBaseInfo : this.kanbanBaseInfoList) {
                    // 注番で50個以上シリアル番号が追加されていた場合、カンバン登録ボタンを無効化
                    List<String> serials = kanbanBaseInfo.getOrderInfo().getSn();
                    if (Objects.nonNull(serials)) {
                        if (!serials.isEmpty()) {
                            isExistSerial = true;
                        }

                        if (serials.size() > MAX_SERIAL_NUM) {
                            // シリアル番号が50以上の場合
                            this.createButton.setDisable(true);
                            return false;
                        }
                    }
                }

                // 工程順 (シリアルが登録されている場合のみ必須)
                if (isExistSerial
                        && (Objects.isNull(this.workflow) || Objects.isNull(this.workflow.getWorkflowId()))) {
                    this.createButton.setDisable(true);
                    return false;
                }
            } else {
                // 工程順
                if (Objects.isNull(this.workflow) || Objects.isNull(this.workflow.getWorkflowId())) {
                    this.createButton.setDisable(true);
                    return false;
                }

                // 指示コード
                if (StringUtils.isEmpty(this.instructionCode.get())) {
                    this.createButton.setDisable(true);
                    return false;
                }

                // シリアル番号・ロット数量
                if (this.serialList.isEmpty() || this.serialCount.getValue() != this.instructionLot.getValue()) {
                    this.createButton.setDisable(true);
                    return false;
                }
            }

            this.createButton.setDisable(false);
            return true;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        }
    }

    /**
     * 追加情報(プロパティ)を表示する
     *
     * @param properties
     */
    private void updateProperty(List<KanbanPropertyInfoEntity> properties) {
        try {
            logger.info("updateProperty start.");

            this.propertyPane.getChildren().clear();
            this.propertyList.clear();

            if (Objects.nonNull(properties) && !properties.isEmpty()) {
                this.propertyList.addAll(properties);
                this.propertyList.sort((Comparator.comparing(kanban -> kanban.getKanbanPropertyOrder())));

                Optional<KanbanPropertyInfoEntity> find = null;

                // プロパティからシリアル番号を排除
                find = this.propertyList.stream().filter(prop -> Objects.equals(prop.getKanbanPropertyName(), LocaleUtils.getString("key.SerialNumber"))).findFirst();
                if (find.isPresent()) {
                    this.propertyList.remove(find.get());
                }

                // プロパティからロット数量を排除
                find = this.propertyList.stream().filter(prop -> Objects.equals(prop.getKanbanPropertyName(), LocaleUtils.getString("key.LotSize"))).findFirst();
                if (find.isPresent()) {
                    this.propertyList.remove(find.get());
                }
            }

            Table table = new Table(this.propertyPane.getChildren());
            table.isAddRecord(true);
            table.isColumnTitleRecord(true);
            table.title(LocaleUtils.getString("key.CustomField"));
            table.styleClass("ContentTitleLabel");
            this.propertyFactory = new KanbanPropertyRecordFactory(table, this.propertyList);
            table.setAbstractRecordFactory(this.propertyFactory);
        } finally {
            logger.info("updateProperty end.");
        }
    }

    /**
     * 「カンバンを作成しました」を表示する
     */
    public void infoCreateKanban() {
        try {
            String title = LocaleUtils.getString(Locale.APPLICATION_TITLE);
            String message = LocaleUtils.getString(Locale.INFO_CREATE_KANBAN);
            String details = " ";
            String okButtonText = LocaleUtils.getString(Locale.OK);
            DialogBox.Show(sc.getStage().getScene().getWindow(), title, message, details, okButtonText, null, DialogBox.DialogType.INFOMATION, 2000L);
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * シリアル番号を前半文字列部と後半数値部に分解する。
     *
     * @param serialNumber
     * @return
     */
    private String[] spritSerialNumber(String serialNumber) {
        String[] result = new String[2];
        String[] buf = serialNumber.split("");
        int pos = -1;
        for (int i = buf.length - 1; i >= 0; i--) {
            if (!buf[i].matches("[0-9]")) {
                pos = i;
                break;
            }
        }
        if (pos < 0) {
            // 全て数値
            result[0] = "";
            result[1] = serialNumber;
        } else if (pos == buf.length - 1) {
            // 全て文字列
            result[0] = serialNumber;
            result[1] = "";
        } else {
            result[0] = serialNumber.substring(0, pos + 1);
            result[1] = serialNumber.substring(pos + 1);
        }
        return result;
    }

    /**
     * 表示をクリアする。
     */
    private void clearInfo() {
        this.kanbanBase = null;

        this.workflowTextField.setText("");
        this.workflow = null;
        this.serialList.clear();
        this.instructionLot.set(0);
        this.updateProperty(null);

        // 指示数の合計表示を更新する。
        this.dispTotal();
    }

    /**
     * 選択した行の注番の追加情報とシリアル番号情報を表示する。
     *
     * @param currentRow 注番リストの選択行インデックス
     */
    private void selectTableViewRow(int currentRow) {
        // カンバンプロパティを表示
        List<KanbanPropertyInfoEntity> props = new ArrayList<>();
        int propOrder = 0;
        this.kanbanBaseInfoList.get(currentRow).getPropertyCollection().sort(Comparator.comparing(item -> item.getKanbanPropertyOrder()));
        for (KanbanBaseInfoPropertyEntity baseProp : this.kanbanBaseInfoList.get(currentRow).getPropertyCollection()) {
            props.add(new KanbanPropertyInfoEntity(null, null, baseProp.getKanbanPropertyName(), baseProp.getKanbanPropertyType(), baseProp.getKanbanPropertyValue(), propOrder++));
        }

        updateProperty(props);

        // シリアル番号の情報を表示
        this.serialList.clear();
        KanbanBaseInfoEntity kanbanBaseInfo = this.kanbanBaseInfoList.get(currentRow);
        OrderInfoEntity orderInfo = kanbanBaseInfo.getOrderInfo();
        if (Objects.nonNull(orderInfo.getSn())) {
            for (String serial : orderInfo.getSn()) {
                this.serialList.add(new ColorTextCellData(serial));
            }
        }

        this.serialCountTextField.setText(String.valueOf(this.kanbanBaseInfoList.get(currentRow).getOrderInfo().getLvol()));
        this.instructionLotTextField.setText(String.valueOf(this.kanbanBaseInfoList.get(currentRow).getOrderInfo().getRem()));
    }

    /**
     * 前回のロット生産画面の状態を読み込む。
     */
    private void loadProperties() {
        logger.info("loadProperties");
        try {
            String confFile = new StringBuilder(Constants.PROPERTY_NAME)
                    .append(Constants.PROPERTIES_EXT)
                    .toString();

            String path = new StringBuilder(System.getenv("ADFACTORY_HOME")).append(File.separator).append("conf").toString();

            boolean isNewFile = false;
            File file = new File(path, confFile);
            if (!file.exists()) {
                isNewFile = true;
            }

            AdProperty.load(Constants.PROPERTY_NAME, confFile);
            Properties props = AdProperty.getProperties(Constants.PROPERTY_NAME);

            if (isNewFile) {
                // 保存する。
                AdProperty.store(Constants.PROPERTY_NAME);
            }

            // ロット生産チェックボックスの選択
            boolean isLotProductSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_LOT_SELECTED, String.valueOf(false)));
            this.lotProductCheckBox.setSelected(isLotProductSelected);

            // ロット生産の工程順
            String prevLotWorkflowId = props.getProperty(Constants.SEARCH_LOT_WORK_FLOW, "");
            if (StringUtils.isEmpty(prevLotWorkflowId)) {
                this.lotWorkflow = new WorkflowInfoEntity();
            } else {
                this.lotWorkflow = this.workflowFacade.find(StringUtils.parseLong(prevLotWorkflowId));
            }

            this.lotProductTextField.setText(this.lotWorkflow.getWorkflowName());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ロット生産画面の状態を保存する。
     */
    private void saveProperties() {
        logger.info("seveProperties");
        try {
            Properties props = AdProperty.getProperties(Constants.PROPERTY_NAME);

            // ロット生産チェックボックスの選択
            props.setProperty(Constants.SEARCH_LOT_SELECTED, String.valueOf(this.lotProductCheckBox.isSelected()));

            // ロット生産の工程順
            if (Objects.isNull(this.lotWorkflow) || Objects.isNull(this.lotWorkflow.getWorkflowId())) {
                props.setProperty(Constants.SEARCH_LOT_WORK_FLOW, "");
            } else {
                props.setProperty(Constants.SEARCH_LOT_WORK_FLOW, String.valueOf(this.lotWorkflow.getWorkflowId()));
            }

            // 保存する。
            AdProperty.store(Constants.PROPERTY_NAME);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * コントロールのVisibleとDisableを切り替える
     *
     * @param isSelected ロット生産のチェックボックスのオン・オフ
     */
    private void setDisableControl(boolean isSelected) {
        // setDisableに渡すために、isDisableを反転する。
        boolean isVisible = isSelected;

        // setDisableへ渡すための変数
        // Visibleとは異なり、true/falseを反転させる
        boolean isDisible = !isSelected;

        // オフの時 編集可能にする
        this.kanbanNameTextField.setDisable(isDisible);
        this.kanbanNameHbox.setDisable(isDisible);

        // オンの時 編集可能にする
        this.lotProuctSelectButton.setDisable(isDisible);
        this.lotProductTableView.setDisable(isDisible);
        this.porderDeleteButton.setDisable(isDisible);
        this.lotProuctSelectButton.setDisable(isDisible);
        this.totalHBox.setDisable(isDisible);

        // オンの時 表示する
        this.lotProductTextField.setVisible(isVisible);
        this.lotProuctSelectButton.setVisible(isVisible);
        this.lotProductTableView.setVisible(isVisible);
        this.porderDeleteButton.setVisible(isVisible);
        this.lotProuctSelectButton.setVisible(isVisible);
        this.totalHBox.setVisible(isVisible);

        for (int row = 4; row <= 6; row++) {
            if (isSelected) {
                this.kanbanSettingGrid.getRowConstraints().get(row).setPrefHeight(Region.USE_COMPUTED_SIZE);
            } else {
                this.kanbanSettingGrid.getRowConstraints().get(row).setPrefHeight(0);
            }
        }
    }

    /**
     * ロット生産用カンバンの作成をする
     */
    private boolean createLotProductKanban() {
        try {
            KanbanInfoEntity kanban;
            ResponseEntity response;

            // ロット数量
            int lotNum = 0;
            List<OrderInfoEntity> orderInfos = this.kanbanBaseInfoList.stream()
                    .map(p -> p.getOrderInfo())
                    .collect(Collectors.toCollection(() -> new LinkedList<>()));

            for (OrderInfoEntity orderInfo : orderInfos) {
                orderInfo.setWorkflowId(this.workflow.getWorkflowId());
                lotNum += orderInfo.getLvol();
            }

            KanbanCreateCondition condition = new KanbanCreateCondition(
                    this.kanbanNameTextField.getText(), this.lotWorkflow.getWorkflowId(), this.kanbanHierarchy.getHierarchyId(),
                    this.loginUser.getLoginId(), true, lotNum,
                    new Date(), null,
                    2);
            response = this.kanbanFacade.createConditon(condition);
            if (!response.isSuccess()) {
                DialogBox.alert(response.getErrorType());
                return false;
            }
            kanban = this.kanbanFacade.findURI(response.getUri());

            // createConditonで設定されない情報をセットする。
            kanban.setModelName(this.lotWorkflow.getModelName());// モデル名
            // kanban.setPropertyCollection(copyProperty(propertyList));

            // サービス情報
            ServiceInfoEntity serviceInfo = new ServiceInfoEntity();
            serviceInfo.setService(SERVICE_INFO_LOT);
            serviceInfo.setJob(orderInfos);

            kanban.setServiceInfo(JsonUtils.objectsToJson(Arrays.asList(serviceInfo))); // サービス情報 (JSON)

            // 追加情報の表示順を更新
            int order = 0;
            for (KanbanPropertyInfoEntity property : kanban.getPropertyCollection()) {
                property.setKanbanPropertyOrder(order++);
            }

            // 計画済みにして登録
            kanban.setKanbanStatus(KanbanStatusEnum.PLANNED);
            kanban.setUpdateDatetime(new Date());
            response = this.kanbanFacade.update(kanban);
            if (!response.isSuccess()) {
                DialogBox.alert(response.getErrorType());
                return false;
            }
        } catch (Exception ex) {
            logger.error(ex);
            Platform.runLater(() -> {
                DialogBox.alert(ex);
            });
            return false;
        }

        return true;
    }

    /**
     * 同じ作業指示書を読み込んでいるかどうかチェックする関数。
     *
     * @param porder 注番
     * @param kban 工程番号
     * @return -1: 未読込 それ以外: 読込済み
     */
    private int isExistInstruction(String porder, String kban) {
        int index = 0;
        for (KanbanBaseInfoEntity kanbanBaseInfo : this.kanbanBaseInfoList) {
            OrderInfoEntity orderInfo = kanbanBaseInfo.getOrderInfo();
            if (porder.equals(orderInfo.getPorder()) && kban.equals(orderInfo.getKban())) {
                return index;
            }
            index++;
        }

        return -1;
    }

    /**
     * 指定した注番の残り台数を取得する
     *
     * @param porder 注番
     * @return 残り台数
     */
    private Integer getRemainCount(String porder) {
        return Integer.valueOf(this.kanbanFacade.findRemain(porder));
    }
}
