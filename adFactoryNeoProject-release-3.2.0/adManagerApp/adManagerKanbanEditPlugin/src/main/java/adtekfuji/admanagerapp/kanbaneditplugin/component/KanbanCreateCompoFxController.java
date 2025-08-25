/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component;

import adtekfuji.admanagerapp.kanbaneditplugin.common.KanbanEditPermanenceData;
import adtekfuji.admanagerapp.kanbaneditplugin.common.KanbanPropertyRecordFactory;
import adtekfuji.admanagerapp.kanbaneditplugin.common.SelectedKanbanAndHierarchy;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.serialcommunication.SerialCommunicationListener;
import adtekfuji.utility.DataValidator;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import static java.util.stream.Collectors.toCollection;
import java.util.stream.Stream;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.job.KanbanProduct;
import jp.adtekfuji.adFactory.entity.kanban.KanbanCreateCondition;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ProductionTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.Locale;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.controls.TextCell;
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
@FxComponent(id = "KanbanCreateCompo", fxmlPath = "/fxml/compo/kanban_create_compo.fxml")
public class KanbanCreateCompoFxController implements Initializable, SerialCommunicationListener, ArgumentDelivery, ListChangeListener {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final Properties properties = AdProperty.getProperties();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
    private final KanbanInfoFacade kanbanFacade = new KanbanInfoFacade();
    private final WorkKanbanInfoFacade workKanbanFacade = new WorkKanbanInfoFacade();
    private final WorkflowInfoFacade workflowFacade = new WorkflowInfoFacade();
    private final KanbanEditPermanenceData kanbanEditPermanenceData = KanbanEditPermanenceData.getInstance();
    private final LinkedList<KanbanPropertyInfoEntity> propertyList = new LinkedList<>();
    private final ObservableList<String> serialList = FXCollections.observableArrayList();

    private SelectedKanbanAndHierarchy kanbanHierarchy;
    private WorkflowInfoEntity workflow;
    private KanbanPropertyRecordFactory propertyFactory;

    private final StringProperty kanbanName = new SimpleStringProperty();
    private final StringProperty modelName = new SimpleStringProperty();
    private final StringProperty productionNumber = new SimpleStringProperty();
    private final StringProperty serialNumber = new SimpleStringProperty();
    private final IntegerProperty lotSize = new SimpleIntegerProperty(1);

    private final String defaultWorkflowName = this.properties.getProperty(Config.DEFAULT_WORKFLOW);
    private final String defaultWorkflowRev = this.properties.getProperty(Config.DEFAULT_WORKFLOW_REV);
    private final String defaultProductionType = this.properties.getProperty(Config.PRODUCTION_TYPE_KEY, Config.PRODUCTION_TYPE_DEFAULT.name());

    private static final String CHARSET = "UTF-8";
    private static final Long RANGE = 20l;
    private static final String BARCODE_CREATE_KANBAN = "ADD_PLAN_INFO";

    @FXML
    private RestrictedTextField kanbanNameTextField;
    @FXML
    private TextField workflowTextField;
    @FXML
    private RestrictedTextField modelNameTextField;
    @FXML
    private RestrictedTextField productionNumberTextField;
    @FXML
    private TextField serialTextField;
    @FXML
    private ListView<String> serialListView;
    @FXML
    private TextField lotSizeTextField;
    @FXML
    private Pane propertyPane;
    @FXML
    private Button createButton;
    @FXML
    private Pane progressPane;
    @FXML
    private ComboBox<ProductionTypeEnum> lotTypeCombo;
    @FXML
    private Button addSerialButton;

    /**
     * フォーカスが解除されたら入力内容を検証する
     */
    private final ChangeListener<Boolean> changeListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if (!newValue) {
            this.validCreateKanban();
        }
    };

    /**
     * ロット数量のフォーカスが解除されたら入力内容を検証する
     */
    private final ChangeListener<Boolean> changeListener_LotSize = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if (!newValue) {
            this.validCreateKanban();
            if (!DataValidator.isValid(lotSizeTextField.getText(), DataValidator.MATCH_NUMBER, true)) {
                DialogBox.warn(Locale.ALERT_INPUT_VALIDATION, Locale.ALERT_INPUT_VALIDATION_DETAILS, LocaleUtils.getString("key.LotSize"));
                lotSizeTextField.requestFocus();
            }
        }
    };

    /**
     *
     */
    private final Callback<ListView<ProductionTypeEnum>, ListCell<ProductionTypeEnum>> productionTypeCellFactory = new Callback() {
        @Override
        public Object call(Object param) {
            return new ListCell<ProductionTypeEnum>() {
                @Override
                protected void updateItem(ProductionTypeEnum item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setText(LocaleUtils.getString(item.getResourceKey()));
                    }
                }
            };
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

            if (!this.properties.containsKey(Config.USE_BARCODE)) {
                this.properties.put(Config.USE_BARCODE, "false");
            }
            if (!this.properties.containsKey(Config.LOT_PRODUCTION_DEFAULT)) {
                this.properties.put(Config.LOT_PRODUCTION_DEFAULT, "false");
            }

            // 工程順
            if (Objects.nonNull(defaultWorkflowName) && !defaultWorkflowName.isEmpty()
                    && Objects.nonNull(defaultWorkflowRev) && !defaultWorkflowRev.isEmpty()) {
                this.workflowTextField.setText(defaultWorkflowName + " : " + defaultWorkflowRev);
            }

            // カンバン名
            this.kanbanNameTextField.textProperty().bindBidirectional(this.kanbanName);
            this.kanbanNameTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                this.validCreateKanban();
            });
            this.kanbanNameTextField.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    this.serialTextField.requestFocus();
                }
            });

            this.kanbanNameTextField.focusedProperty().addListener(changeListener);

            // モデル名
            this.modelNameTextField.textProperty().bindBidirectional(this.modelName);
            // 製造番号
            this.productionNumberTextField.textProperty().bindBidirectional(this.productionNumber);

            // ロット生産
            this.lotTypeCombo.focusTraversableProperty().set(false);
            this.lotTypeCombo.setItems(Stream.of(ProductionTypeEnum.values())
                // ロット1個流し生産 及び ロット流し生産を非表示
                .filter(o -> !(Objects.equals(o, ProductionTypeEnum.LOT) || Objects.equals(o, ProductionTypeEnum.LOT_ONE_PIECE2)))
                .collect(toCollection(FXCollections::observableArrayList)));

            this.lotTypeCombo.setCellFactory(productionTypeCellFactory);
            this.lotTypeCombo.setButtonCell(productionTypeCellFactory.call(null));
            this.lotTypeCombo.getSelectionModel().selectedItemProperty().addListener(this::onChangedProductionType);
            boolean lotProductionDefault = Boolean.valueOf(this.properties.getProperty(Config.LOT_PRODUCTION_DEFAULT));
            
            this.serialTextField.setDisable(lotProductionDefault);
            this.serialListView.setDisable(lotProductionDefault);
            this.addSerialButton.setDisable(lotProductionDefault);

            // シリアル番号
            this.serialTextField.textProperty().bindBidirectional(this.serialNumber);
            this.serialTextField.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    this.onAdd(null);
                }
            });

            // シリアル番号リスト
            this.serialListView.setItems(this.serialList);
            this.serialListView.fixedCellSizeProperty().set(30.0);
            this.serialListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @Override
                public ListCell<String> call(ListView<String> param) {
                    return new TextCell(param);
                }
            });
            this.serialListView.setEditable(true);

            this.serialList.addListener(this);

            // ロット数量
            this.lotSizeTextField.textProperty().bindBidirectional(lotSize, new NumberStringConverter());
            this.lotSizeTextField.focusedProperty().addListener(changeListener_LotSize);
            this.lotSizeTextField.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    // 入力チェック
                    if (!DataValidator.isValid(lotSizeTextField.getText(), DataValidator.MATCH_NUMBER, true)) {
                        this.lotSizeTextField.focusedProperty().removeListener(changeListener_LotSize);
                        DialogBox.warn(Locale.ALERT_INPUT_VALIDATION, Locale.ALERT_INPUT_VALIDATION_DETAILS, LocaleUtils.getString("key.LotSize"));
                        this.lotSizeTextField.focusedProperty().addListener(changeListener_LotSize);
                    }
                }
            });

            // 生産タイプの初期値をセットする。
            try {
                ProductionTypeEnum productionType = ProductionTypeEnum.valueOf(defaultProductionType);
                this.lotTypeCombo.setValue(productionType);
            } catch (Exception ex) {
                this.lotTypeCombo.setValue(Config.PRODUCTION_TYPE_DEFAULT);
            }

            // 追加情報
            this.updateProperty(null);
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
        boolean isCancel = false;
        try {
            blockUI(true);
            if (argument instanceof SelectedKanbanAndHierarchy) {
                this.kanbanHierarchy = (SelectedKanbanAndHierarchy) argument;
                this.createButton.setDisable(true);

                logger.info("KanbanMultipleRegistCompo:{}", LocaleUtils.getString("key.KanbanContinuousCreate"));

                boolean useBarcode = StringUtils.parseBoolean(this.properties.getProperty(Config.USE_BARCODE));
                if (useBarcode) {
                    try {
                        kanbanEditPermanenceData.connectSerialComm(this);
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.ConnectionErrorTitle"), LocaleUtils.getString("key.SerialCommErrUnconnection"));
                        });
                    }
                }

                if (!StringUtils.isEmpty(this.workflowTextField.getText())) {
                    // 工程順を取得する。取得に失敗した場合、工程順選択ダイアログを表示する。
                    Task task = new Task<WorkflowInfoEntity>() {
                        @Override
                        protected WorkflowInfoEntity call() throws Exception {
                            return workflowFacade.findName(URLEncoder.encode(defaultWorkflowName, CHARSET), Integer.valueOf(defaultWorkflowRev));
                        }

                        @Override
                        protected void succeeded() {
                            super.succeeded();
                            try {
                                setDefaultWorkflow(this.getValue());
                            } finally {
                                if (!StringUtils.isEmpty(workflowTextField.getText())) {
                                    serialTextField.requestFocus();
                                    validCreateKanban();
                                } else {
                                    onSelectWorkflow(null);
                                }
                                blockUI(false);
                            }
                        }

                        @Override
                        protected void failed() {
                            super.failed();
                            if (Objects.nonNull(this.getException())) {
                                logger.fatal(this.getException(), this.getException());
                            }
                            workflowTextField.setText("");
                            modelNameTextField.setText("");
                            productionNumberTextField.setText("");
                            blockUI(false);
                        }
                    };
                    new Thread(task).start();
                } else {
                    Platform.runLater(() -> {
                        onSelectWorkflow(null);
                    });
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            isCancel = true;
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }
    }

    /**
     *
     * @param defaultWorkflow
     */
    private void setDefaultWorkflow(WorkflowInfoEntity defaultWorkflow) {
        try {
            this.workflow = defaultWorkflow;

            if (Objects.isNull(this.workflow) || Objects.isNull(this.workflow.getWorkflowId())) {
                this.workflowTextField.setText("");
                this.modelNameTextField.setText("");
                return;
            }

            this.workflowTextField.setText(this.workflow.getWorkflowName() + " : " + this.workflow.getWorkflowRev().toString());
            this.modelNameTextField.setText(this.workflow.getModelName());

            // カンバンプロパティ
            List<KanbanPropertyInfoEntity> list = new ArrayList<>();
            this.workflow.getKanbanPropertyTemplateInfoCollection().stream().forEach(o -> {
                list.add(new KanbanPropertyInfoEntity(null, null, o.getKanbanPropName(), o.getKanbanPropType(), o.getKanbanPropInitialValue(), o.getKanbanPropOrder()));
            });

            this.updateProperty(list);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.workflowTextField.setText("");
            this.modelNameTextField.setText("");
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

            // シリアル番号が追加されたらロット数量を更新
            this.lotSize.set(this.serialList.size());
            validCreateKanban();

            Collections.sort(this.serialList);
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
        logger.info("onSelectWorkflow");
        boolean isCancel = false;
        try {
            blockUI(true);

            SelectDialogEntity<WorkflowInfoEntity> selectDialogEntity = new SelectDialogEntity();
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.OrderProcesses"), "WorkflowSingleSelectionCompo", selectDialogEntity, true);
            if (ret.equals(ButtonType.OK) && !selectDialogEntity.getWorkflows().isEmpty()) {
                WorkflowInfoEntity selected = selectDialogEntity.getWorkflows().get(0);

                if (StringUtils.isEmpty(selected.getWorkflowDiaglam())) {
                    DialogBox.warn(Locale.ALERT_NO_WORK, Locale.ALERT_WORKFLOW_ERROR_DETAILS);
                    isCancel = true;
                    return;
                }

                Task task = new Task<WorkflowInfoEntity>() {
                    @Override
                    protected WorkflowInfoEntity call() throws Exception {
                        return workflowFacade.find(selected.getWorkflowId());
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        try {
                            setDefaultWorkflow(this.getValue());
                        } finally {
                            if (isLotProduction()) {
                                kanbanNameTextField.requestFocus();
                            } else {
                                serialTextField.requestFocus();
                            }
                            validCreateKanban();
                            blockUI(false);
                        }
                    }

                    @Override
                    protected void failed() {
                        super.failed();
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                        blockUI(false);
                    }
                };
                new Thread(task).start();
            } else {
                isCancel = true;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            isCancel = true;
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }
    }

    /**
     * シリアル番号の追加ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onAdd(ActionEvent event) {
        Platform.runLater(() -> {
            if (StringUtils.isEmpty(this.serialNumber.get())) {
                lotSizeTextField.requestFocus();
                return;
            }
            
            List<String> serial = new ArrayList();
            if (!",".contains(this.serialNumber.get())) {
                String[] inputValue = this.serialNumber.get().split(",");
                
                if (inputValue.length == 3) {
                   String moji = StringUtils.trimLeft(inputValue[0]); // 任意の文字列
                   String strStartNumber = inputValue[1].trim();  // 開始番号
                   String strEndNumber = inputValue[2].trim();    // 終了番号
                   
                   // 開始番号、終了番号が数値か
                   String pattern = "[+-]?\\d+";
                   if (strStartNumber.matches(pattern)
                           && strEndNumber.matches(pattern)) {
                       String keta = String.valueOf(strEndNumber.length());
                       int startNum = Integer.parseInt(strStartNumber);
                       int endNum = Integer.parseInt(strEndNumber);
                       
                       for (; startNum <= endNum; startNum++) {
                           serial.add(moji + String.format ("%0" + keta + "d", startNum));
                       }
                   }
                }
            }
            
            if (serial.isEmpty()) {
                serial.add(this.serialNumber.get());
            }

            for (int i = 0; serial.size() > i; i++) {
                int ii = serialList.indexOf(serial.get(i));
                if (ii < 0) {
                    serialList.add(serial.get(i));
                    ii = serialList.size() - 1;
                    serialListView.scrollTo(serialList.size() - 1);
                    serialListView.getSelectionModel().select(ii);
                    serialListView.getFocusModel().focus(ii);
                    serialTextField.requestFocus();
                    this.serialNumber.set("");
                } else {
                    serialListView.scrollTo(ii);
                    serialListView.getSelectionModel().select(ii);
                    serialListView.getFocusModel().focus(ii);
                }
            }
        });
    }

    /**
     * すべて消去ボタンのアクション
     * 
     * @param event 
     */
    @FXML
    private void onClear(ActionEvent event) {
        this.serialList.clear();
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

            if (Objects.nonNull(this.workflow) && Objects.nonNull(this.workflow.getWorkflowId())) {
                properties.put(Config.DEFAULT_WORKFLOW, this.workflow.getWorkflowName());
                properties.put(Config.DEFAULT_WORKFLOW_REV, String.valueOf(this.workflow.getWorkflowRev()));
            }

            // 生産タイプ
            if (Objects.nonNull(lotTypeCombo.getValue())) {
                properties.put(Config.PRODUCTION_TYPE_KEY, lotTypeCombo.getValue().name());
            }

            kanbanEditPermanenceData.disconnectSerialComm();

            sc.setComponent("ContentNaviPane", "KanbanListCompo");
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

            this.createKanban();
        } finally {
            logger.info("onCreate end.");
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

                if (message.equals(BARCODE_CREATE_KANBAN)) {
                    // カンバンを登録
                    this.createKanban();
                    return;
                }

                if (this.kanbanNameTextField.isFocused()) {
                    this.kanbanName.set(message);
                } else {
                    this.serialNumber.set(message);
                    this.onAdd(null);
                }

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
     * 操作をロックする
     *
     * @param block
     */
    private void blockUI(Boolean block) {
        sc.blockUI("ContentNaviPane", block);
        this.progressPane.setVisible(block);
    }

    /**
     * カンバンを作成する
     */
    private void createKanban() {
        try {
            logger.info("createKanban start.");

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

                // 値が空欄の場合は型チェックしない。
                if (StringUtils.isEmpty(property.getKanbanPropertyValue())) {
                    continue;
                }

                // 型に応じてチェックを行なう。
                switch (property.getKanbanPropertyType()) {
                    case TYPE_INTEGER:
                        if (!StringUtils.isInteger(property.getKanbanPropertyValue())) {
                            // 追加情報の値が項目のタイプと異なります
                            DialogBox.warn(Locale.WARN_INPUT_REQUIRED, "key.alert.differentTypeValue", property.getKanbanPropertyName());
                            return;
                        }
                        break;
                    case TYPE_DATE:
                        if (!StringUtils.isDate(property.getKanbanPropertyValue(), "uuuu/M/d", true)) {
                            // 追加情報の値が項目のタイプと異なります
                            DialogBox.warn(Locale.WARN_INPUT_REQUIRED, "key.alert.differentTypeValue", property.getKanbanPropertyName());
                            return;
                        }
                        break;
                }
            }

            this.blockUI(true);

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        logger.info("createKanban thread start.");

                        if (!registKanban()) {
                            return null;
                        }

                        Platform.runLater(() -> {
                            blockUI(false);

                            infoCreateKanban();

                            kanbanName.set("");
                            productionNumberTextField.setText("");// 製造番号
                            serialTextField.setText("");
                            serialList.clear();
                            lotSize.set(1);

                            createButton.setDisable(true);
                            serialTextField.requestFocus();
                        });
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
     * ロット生産(1個流しロット生産 または ひとり屋台生産)かどうか
     *
     * @return 1個流しロット生産またはひとり屋台生産の場合true
     */
    private boolean isLotProduction() {
        return Objects.equals(ProductionTypeEnum.LOT_ONE_PIECE, lotTypeCombo.getSelectionModel().getSelectedItem())
                || Objects.equals(ProductionTypeEnum.LOT_ONE_PIECE2, lotTypeCombo.getSelectionModel().getSelectedItem());
    }

    /**
     * カンバンを登録する。
     *
     * @return カンバンの登録に成功した場合true、失敗した場合falseを返す。
     * @throws Exception
     */
    private boolean registKanban() throws Exception {
        if (lotTypeCombo.getSelectionModel().isEmpty()) {
            return false;
        }

        switch (lotTypeCombo.getSelectionModel().getSelectedItem()) {
            case ONE_PIECE:
                return registOnePieceFlowProduction();

            case LOT_ONE_PIECE:
                return registOnePieceLotProduction(1);

            case LOT_ONE_PIECE2:
                return registOnePieceLotProduction(3);

            case LOT:
            default:
                // 簡易作成画面ではロット生産カンバンの作成は不可
                return false;
        }
    }

    /**
     * ロット生産カンバンを登録する。{@link ProductionTypeEnum} の各数字のうち1, 3のみに対応
     *
     * @param カンバン作成タイプ
     * @return 登録に成功した場合true、失敗した場合false
     * @throws Exception
     */
    private boolean registOnePieceLotProduction(int productionType) throws Exception {
        KanbanCreateCondition condition = new KanbanCreateCondition(
                kanbanName.get(), workflow.getWorkflowId(), kanbanHierarchy.getHierarchyId(),
                loginUser.getLoginId(), true, lotSize.get(),
                new Date(), new ArrayList<>(),
                productionType);
        ResponseEntity response = kanbanFacade.createConditon(condition);
        if (!response.isSuccess()) {
            DialogBox.alert(response.getErrorType());
            return false;
        }
        KanbanInfoEntity kanban = kanbanFacade.findURI(response.getUri());

        // createConditonで設定されない情報をセットする。
        kanban.setModelName(modelName.get());// モデル名
        kanban.setProductionNumber(productionNumber.get());// 製造番号
        kanban.setPropertyCollection(copyProperty(propertyList));

        // 追加情報の表示順を更新
        int order = 0;
        for (KanbanPropertyInfoEntity property : kanban.getPropertyCollection()) {
            property.setKanbanPropertyOrder(order++);
        }

        // 計画済みにして登録
        kanban.setKanbanStatus(KanbanStatusEnum.PLANNED);
        kanban.setUpdateDatetime(new Date());
        response = kanbanFacade.update(kanban);
        if (!response.isSuccess()) {
            DialogBox.alert(response.getErrorType());
            return false;
        }

        return true;
    }

    /**
     * 一個流しカンバンを登録する。
     *
     * @return 登録に成功した場合true、失敗した場合false
     * @throws Exception
     */
    private boolean registOnePieceFlowProduction() throws Exception {
        KanbanInfoEntity kanban = new KanbanInfoEntity();

        if (StringUtils.isEmpty(kanbanName.get())) {
            // カンバン名を設定
            int ii = serialList.size();
            if (1 == ii) {
                kanbanName.set(serialList.get(0));
            } else if (1 < ii) {
                kanbanName.set(serialList.get(0) + "-" + serialList.get(ii - 1));
            } else {
                throw new Exception("Invalid operation.");
            }
        }

        kanban.setKanbanName(kanbanName.get());
        kanban.setModelName(modelName.get());// モデル名
        kanban.setProductionNumber(productionNumber.get());// 製造番号
        kanban.setFkWorkflowId(workflow.getWorkflowId());
        kanban.setWorkflowName(workflow.getWorkflowName());
        kanban.setParentId(kanbanHierarchy.getHierarchyId());
        kanban.setProductionType(0);
        kanban.setLotQuantity(this.lotSize.get());

        // ※プロパティはregistで登録されないので、後で追加する。
        kanban.setStartDatetime(new Date());
        kanban.setFkUpdatePersonId(loginUser.getId());
        kanban.setUpdateDatetime(new Date());

        ResponseEntity response = kanbanFacade.regist(kanban);
        if (!response.isSuccess()) {
            DialogBox.alert(response.getErrorType());
            return false;
        }

        KanbanInfoEntity kanbanInfoEntity = kanbanFacade.findURI(response.getUri());
        kanbanInfoEntity.getWorkKanbanCollection().clear();
        kanbanInfoEntity.getSeparateworkKanbanCollection().clear();

        Long workkanbanCnt = workKanbanFacade.countFlow(kanbanInfoEntity.getKanbanId());
        for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
            kanbanInfoEntity.getWorkKanbanCollection().addAll(workKanbanFacade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanbanInfoEntity.getKanbanId()));
        }

        Long separateCnt = workKanbanFacade.countSeparate(kanbanInfoEntity.getKanbanId());
        for (long nowCnt = 0; nowCnt < separateCnt; nowCnt += RANGE) {
            kanbanInfoEntity.getSeparateworkKanbanCollection().addAll(workKanbanFacade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanbanInfoEntity.getKanbanId()));
        }

        // プロパティ
        kanbanInfoEntity.setPropertyCollection(copyProperty(copyProperty(propertyList)));

        // 追加情報の表示順を更新
        int order = 0;
        for (KanbanPropertyInfoEntity property : kanbanInfoEntity.getPropertyCollection()) {
            property.setKanbanPropertyOrder(order++);
        }
        
        if (!this.serialList.isEmpty()) {
            // シリアル番号をプロパティに追加
            StringBuilder value = new StringBuilder();
            serialList.stream().forEach(o -> {
                value.append(o);
                value.append("|");

            });

            if (value.length() > 0) {
                value.deleteCharAt(value.length() - 1);
            }

            KanbanPropertyInfoEntity serialProp = new KanbanPropertyInfoEntity(null, null, LocaleUtils.getString("key.SerialNumber"), CustomPropertyTypeEnum.TYPE_STRING, value.toString(), order++);
            kanbanInfoEntity.getPropertyCollection().add(serialProp);
    
            int orderNumber = 1;
            List<KanbanProduct> kanbanProducts = new LinkedList<>();
            for (int i = 0; i < serialList.size(); i++) {
                KanbanProduct kanbanProduct = new KanbanProduct();
                kanbanProduct.setUid(serialList.get(i));
                kanbanProduct.setDefect(null);
                kanbanProduct.setOrderNumber(orderNumber++);
                kanbanProduct.setStatus(KanbanStatusEnum.PLANNED);
                kanbanProduct.setImplement(false);

                kanbanProducts.add(kanbanProduct);
            }

            ServiceInfoEntity serviceInfo = new ServiceInfoEntity();
            serviceInfo.setService(ServiceInfoEntity.SERVICE_INFO_PRODUCT);
            serviceInfo.setJob(kanbanProducts);

            kanbanInfoEntity.setServiceInfo(JsonUtils.objectsToJson(Arrays.asList(serviceInfo))); // サービス情報 (JSON)
            kanbanInfoEntity.setLotQuantity(this.serialList.size());

            for (WorkKanbanInfoEntity workKanban : kanbanInfoEntity.getWorkKanbanCollection()) {
                workKanban.setServiceInfo(kanbanInfoEntity.getServiceInfo());
            }

            for (WorkKanbanInfoEntity workKanban : kanbanInfoEntity.getSeparateworkKanbanCollection()) {
                workKanban.setServiceInfo(kanbanInfoEntity.getServiceInfo());
            }
        } else {
            kanbanInfoEntity.setLotQuantity(this.lotSize.getValue());
        }
        
        // ロット数量をプロパティに追加
        if (kanban.getLotQuantity() > 1) {
            KanbanPropertyInfoEntity lotSizeProp = new KanbanPropertyInfoEntity(null, null, LocaleUtils.getString("key.LotSize"), CustomPropertyTypeEnum.TYPE_INTEGER, lotSize.getValue().toString(), order++);
            kanbanInfoEntity.getPropertyCollection().add(lotSizeProp);
        }
        
        // 基準時間を設定
        //WorkflowProcess workflowProcess = new WorkflowProcess(workflow.getWorkflowDiaglam());
        //List<BreakTimeInfoEntity> breaktimeList = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanbanInfoEntity.getWorkKanbanCollection());
        //workflowProcess.planning(kanbanInfoEntity, breaktimeList, new Date());
        // 計画済みにして登録
        kanbanInfoEntity.setKanbanStatus(KanbanStatusEnum.PLANNED);
        kanbanInfoEntity.setUpdateDatetime(new Date());
        response = kanbanFacade.update(kanbanInfoEntity);
        if (!response.isSuccess()) {
            DialogBox.alert(response.getErrorType());
            return false;
        }

        return true;
    }

    /**
     * 入力項目を検証して、問題がなければ作成ボタンを有効にする
     *
     * @return
     */
    private boolean validCreateKanban() {

        if (Objects.isNull(this.workflow) || Objects.isNull(this.workflow.getWorkflowName())) {
            this.createButton.setDisable(true);
            return false;
        }

        //if (this.lotTypeCombo.getSelectionModel().isEmpty()) {
        //    this.createButton.setDisable(true);
        //    return false;
        //}

        if (!this.isLotProduction()) {
            if ((StringUtils.isEmpty(this.kanbanName.get()) && this.serialList.isEmpty())
                    || this.serialList.size() > 50) {
                this.createButton.setDisable(true);
                return false;
            }
        } else {
            if (this.kanbanName.isEmpty().get()) {
                this.createButton.setDisable(true);
                return false;
            }
            if (!(this.lotSize.get() > 0)) {
                this.createButton.setDisable(true);
                return false;
            }
        }

        this.createButton.setDisable(false);
        return true;
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
            String details = LocaleUtils.getString("key.KanbanName") + ": " + this.kanbanName.get();
            String okButtonText = LocaleUtils.getString(Locale.OK);
            DialogBox.Show(sc.getStage().getScene().getWindow(), title, message, details, okButtonText, null, DialogBox.DialogType.INFOMATION, 2000L);
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 生産タイプ切り替えのアクション
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void onChangedProductionType(ObservableValue<? extends ProductionTypeEnum> observable, ProductionTypeEnum oldValue, ProductionTypeEnum newValue) {
        boolean selectedVal = isLotProduction();
        this.serialTextField.setDisable(selectedVal);
        this.serialListView.setDisable(selectedVal);
        this.addSerialButton.setDisable(selectedVal);

        if (selectedVal) {
            this.kanbanNameTextField.requestFocus();
        } else {
            this.serialTextField.requestFocus();
        }

        this.validCreateKanban();
    }
}
