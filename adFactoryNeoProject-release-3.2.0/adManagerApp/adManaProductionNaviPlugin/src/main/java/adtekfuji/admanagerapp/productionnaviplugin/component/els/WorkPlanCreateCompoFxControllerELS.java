/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component.els;

import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanKanbanEditConfigELS;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanKanbanEditPermanenceData;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanKanbanPropertyRecordFactory;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanSelectedKanbanAndHierarchy;
import adtekfuji.admanagerapp.productionnaviplugin.controls.WorkPlanColorTextCell;
import adtekfuji.admanagerapp.productionnaviplugin.controls.WorkPlanColorTextCellData;
import adtekfuji.admanagerapp.productionnaviplugin.entity.KanbanBaseInfoEntity;
import adtekfuji.admanagerapp.productionnaviplugin.entity.KanbanBaseInfoPropertyEntity;
import adtekfuji.admanagerapp.productionnaviplugin.externalio.WorkPlanKanbanBaseInfoAccessELS;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkKanbanTimeReplaceUtils;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanWorkflowProcess;
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
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.workflow.KanbanPropertyTemplateInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
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
 * @author (TST)min
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "WorkPlanCreateCompoELS", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/els/work_plan_create_compo_els.fxml")
public class WorkPlanCreateCompoFxControllerELS implements Initializable, SerialCommunicationListener, ArgumentDelivery, ListChangeListener  {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final Properties properties = AdProperty.getProperties();
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
    private final KanbanInfoFacade kanbanFacade = new KanbanInfoFacade();
    private final WorkKanbanInfoFacade workKanbanFacade = new WorkKanbanInfoFacade();
    private final WorkflowInfoFacade workflowFacade = new WorkflowInfoFacade();
    private final WorkPlanKanbanEditPermanenceData kanbanEditPermanenceData = WorkPlanKanbanEditPermanenceData.getInstance();
    private final LinkedList<KanbanPropertyInfoEntity> propertyList = new LinkedList<>();
    private final ObservableList<WorkPlanColorTextCellData> serialList = FXCollections.observableArrayList();

    private final WorkPlanKanbanBaseInfoAccessELS kanbanBaseInfoAccess = new WorkPlanKanbanBaseInfoAccessELS();// カンバン基本情報取得用クラス

    private WorkPlanSelectedKanbanAndHierarchy kanbanHierarchy;
    private WorkflowInfoEntity workflow;
    private WorkPlanKanbanPropertyRecordFactory propertyFactory;

    private final StringProperty kanbanName = new SimpleStringProperty();
    private final StringProperty instructionCode = new SimpleStringProperty();
    private final StringProperty serialNumberTo = new SimpleStringProperty();
    private final StringProperty serialNumberFrom = new SimpleStringProperty();
    private final IntegerProperty serialCount = new SimpleIntegerProperty(0);
    private final IntegerProperty instructionLot = new SimpleIntegerProperty();

    private static final Long RANGE = 20l;
    private static final Color COLOR_SKIP = Color.GREEN;
    private static final Color COLOR_NG = Color.RED;

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
    private ListView<WorkPlanColorTextCellData> serialListView;
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

    private boolean IsReadAvailable;

    /**
     * フォーカスが解除されたら入力内容を検証する
     */
    private final ChangeListener<Boolean> changeListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if (!newValue) {
            this.validCreateKanban();
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

            // ELS版はカンバン名入力欄は使用しない。
            this.kanbanNameHbox.setDisable(true);
            this.kanbanNameTextField.setDisable(true);

            // 作業指示コードラベル
            String codeLabelText = WorkPlanKanbanEditConfigELS.getIndtructionCodeLabelText();
            if (codeLabelText.isEmpty()) {
                codeLabelText = LocaleUtils.getString("key.InstructionCode");
                WorkPlanKanbanEditConfigELS.setIndtructionCodeLabelText(codeLabelText);
            }
            this.instructionCodeLabel.setText(codeLabelText);

            // 作業指示コード入力欄
            this.instructionCodeTextField.textProperty().bindBidirectional(this.instructionCode);
            this.instructionCodeTextField.focusedProperty().addListener(this.changeListener);
            this.instructionCodeTextField.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    this.onReadInstruction(null);
                }
            });

            // 先頭シリアル番号入力欄
            this.serialFromTextField.textProperty().bindBidirectional(this.serialNumberFrom);
            this.serialFromTextField.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    this.serialToTextField.requestFocus();
                }
            });

            // 末尾シリアル番号入力欄
            this.serialToTextField.textProperty().bindBidirectional(this.serialNumberTo);
            this.serialToTextField.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    this.onAddSerial(null);
                }
            });

            // シリアル番号リスト
            this.serialListView.setItems(this.serialList);
            this.serialListView.fixedCellSizeProperty().set(30.0);
            this.serialListView.setCellFactory(new Callback<ListView<WorkPlanColorTextCellData>, ListCell<WorkPlanColorTextCellData>>() {
                @Override
                public ListCell<WorkPlanColorTextCellData> call(ListView<WorkPlanColorTextCellData> param) {
                    return new WorkPlanColorTextCell(param);
                }
            });
            this.serialListView.setEditable(true);

            this.serialList.addListener(this);

            // シリアル番号の数
            this.serialCountTextField.textProperty().bindBidirectional(this.serialCount, new NumberStringConverter());
            this.serialCountTextField.focusedProperty().addListener(this.changeListener);
            // ロット数量
            this.instructionLotTextField.textProperty().bindBidirectional(this.instructionLot, new NumberStringConverter());
            this.instructionLotTextField.focusedProperty().addListener(this.changeListener);

            // 追加情報
            this.updateProperty(null);

            // カンバン基本情報の取得機能が使用可能か
            this.IsReadAvailable = this.kanbanBaseInfoAccess.IsAvailable();
            this.readInstructionButton.setDisable(!IsReadAvailable);
            if (!this.IsReadAvailable) {
                // 作業指示情報の読み込みが使用できません
                DialogBox.warn("key.Warning", "key.ReadInstructionNotAvailable");
            }
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
        if (argument instanceof WorkPlanSelectedKanbanAndHierarchy) {
            this.kanbanHierarchy = (WorkPlanSelectedKanbanAndHierarchy) argument;
            this.createButton.setDisable(true);

            logger.debug("KanbanMultipleRegistCompo:{}", LocaleUtils.getString("key.KanbanContinuousCreate"));

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
            this.serialCount.set(this.serialList.size());
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
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.OrderProcesses"), "WorkflowSingleSelectionCompo", selectDialogEntity);
            if (ret.equals(ButtonType.OK) && !selectDialogEntity.getWorkflows().isEmpty()) {
                WorkflowInfoEntity selected = selectDialogEntity.getWorkflows().get(0);

                this.blockUI(true);

                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            workflow = workflowFacade.find(selected.getWorkflowId());
                            Platform.runLater(() -> {
                                workflowTextField.setText(workflow.getWorkflowName());

                                // カンバンプロパティ
                                List<KanbanPropertyInfoEntity> list = new ArrayList<>();
                                workflow.getKanbanPropertyTemplateInfoCollection().stream().forEach(o -> {
                                    list.add(new KanbanPropertyInfoEntity(null, null, o.getKanbanPropName(), o.getKanbanPropType(), o.getKanbanPropInitialValue(), o.getKanbanPropOrder()));
                                });

                                updateProperty(list);
                            });
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                        } finally {
                            Platform.runLater(() -> {
                                blockUI(false);
                                instructionCodeTextField.requestFocus();
                                validCreateKanban();
                            });
                        }
                        return null;
                    }
                };

                new Thread(task).start();
            }
        }
        catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        finally {
            logger.info("onSelectWorkflow end.");
        }
    }

    /**
     * 読込ボタンのアクション
     *
     *      ※作業指示コードでカンバン基本情報を取得して画面にセットする。
     * @param event 
     */
    @FXML
    private void onReadInstruction(ActionEvent event) {
        Platform.runLater(() -> {
            try {
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

                // 情報をクリアする。
                this.ClearInfo();

                // ----------
                // カンバン基本情報取得
                KanbanBaseInfoEntity kanbanBase = this.kanbanBaseInfoAccess.GetKanbanBaseInfo(this.instructionCode.get());
                if (Objects.isNull(kanbanBase)) {
                    // 読み込めませんでした
                    DialogBox.warn("key.Warning", "key.KanbanBaseInfoNotRead");
                    this.instructionCodeTextField.requestFocus();
                    return;
                }

                // ----------
                // 取得した情報を画面にセットする。

                // 工程順名
                if (Objects.nonNull(kanbanBase.getWorkflowName())) {
                    // 工程順を取得
                    this.workflow = workflowFacade.findName(URLEncoder.encode(kanbanBase.getWorkflowName(), "UTF-8"));
                    if (Objects.isNull(this.workflow) || Objects.isNull(this.workflow.getWorkflowId())) {
                        // 存在しない工程順名です
                        DialogBox.warn("key.Warning", "key.WorkflowNotExist", kanbanBase.getWorkflowName());
                        this.instructionCodeTextField.requestFocus();
                        return;
                    }

                    this.workflowTextField.setText(kanbanBase.getWorkflowName());
                }

                // ロット数量
                if (Objects.nonNull(kanbanBase.getLotQuantity())) {
                    this.instructionLot.set(kanbanBase.getLotQuantity());
                }

                // カンバンプロパティ
                if (Objects.nonNull(kanbanBase.getPropertyCollection()) && !kanbanBase.getPropertyCollection().isEmpty()) {
                    List<KanbanPropertyInfoEntity> props = new ArrayList<>();
                    int propOrder = 1;// ※カンバン基本情報と工程順の両方のプロパティをセットするため、オーダー順は設定しなおす。

                    // カンバン基本情報のカンバンプロパティ
                    kanbanBase.getPropertyCollection().sort(Comparator.comparing(item -> item.getKanbanPropertyOrder()));
                    for (KanbanBaseInfoPropertyEntity baseProp : kanbanBase.getPropertyCollection()) {
                        props.add(new KanbanPropertyInfoEntity(null, null, baseProp.getKanbanPropertyName(), baseProp.getKanbanPropertyType(), baseProp.getKanbanPropertyValue(), propOrder++));
                    }

                    // 工程順のカンバンプロパティ
                    this.workflow.getKanbanPropertyTemplateInfoCollection().sort(Comparator.comparing(item -> item.getKanbanPropOrder()));
                    for (KanbanPropertyTemplateInfoEntity tempProp : this.workflow.getKanbanPropertyTemplateInfoCollection()) {
                        // カンバン基本情報にないプロパティのみ追加する。
                        if (props.stream().filter(p -> p.getKanbanPropertyName().equals(tempProp.getKanbanPropName())).count() == 0) {
                            props.add(new KanbanPropertyInfoEntity(null, null, tempProp.getKanbanPropName(), tempProp.getKanbanPropType(), tempProp.getKanbanPropInitialValue(), propOrder++));
                        }
                    }

                    this.updateProperty(props);
                }

                // 先頭シリアル入力欄にフォーカス移動。
                this.serialFromTextField.requestFocus();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                this.instructionCodeTextField.requestFocus();
            }
        });
    }

     /**
     * シリアル番号の追加ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onAddSerial(ActionEvent event) {
        Platform.runLater(() -> {
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
            Optional<WorkPlanColorTextCellData> toSerialData = this.serialList.stream().filter(item -> findSerial.equals(item.getText())).findFirst();// 末尾シリアル

            if (fromSerial.equals(toSerial)) {
                // 先頭シリアルと末尾シリアルが同じ場合はそのまま追加する。
                if (!toSerialData.isPresent()) {
                    this.serialList.add(new WorkPlanColorTextCellData(toSerial));
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
                            this.serialList.add(new WorkPlanColorTextCellData(serialNumber));
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
        });
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

            properties.put(Config.DEFAULT_WORKFLOW, this.workflowTextField.getText());

            kanbanEditPermanenceData.disconnectSerialComm();

            sc.setComponent("ContentNaviPane", "WorkPlanChartCompo");
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

                if (this.instructionCodeTextField.isFocused()) {
                    // 作業指示書
                    this.instructionCode.set(message);
                    this.onReadInstruction(null);
                } else if (this.serialFromTextField.isFocused()) {
                    // 先頭シリアル番号
                    this.serialNumberFrom.set(message);
                    this.serialToTextField.requestFocus();
                } else if (this.serialToTextField.isFocused()) {
                    // 末尾シリアル番号
                    this.serialNumberTo.set(message);
                    this.onAddSerial(null);
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
            }

            // ロット数量がシリアル番号リストの数と異なる場合は警告する。
            if (this.serialList.size() != this.serialCount.getValue()) {
                // ロット数量がシリアル番号の数と異なりますが、保存しますか?
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.LotNumberDifferent");
                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[] { ButtonType.YES, ButtonType.NO }, ButtonType.NO);
                if (ButtonType.YES != buttonType) {
                    return;
                }
            }

            this.blockUI(true);

            Task task;
            task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        logger.info("createKanban thread start.");
                        createKanbanThread();
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
        }
        finally {
            logger.info("createKanban end.");
        }
    }

    /**
     * 
     * @throws Exception 
     */
    private void createKanbanThread() throws Exception {
        List<String> okList = new ArrayList<>();
        List<String> ngList = new ArrayList<>();
        List<String> skipList = new ArrayList<>();

        // 全シリアル共通の情報
        Long workflowId = workflow.getWorkflowId();
        String workflowName = workflow.getWorkflowName();
        Long hierarchyId = kanbanHierarchy.getHierarchyId();
        Long loginUserId = loginUser.getId();
        Date updateDatetime = new Date();

        List<KanbanPropertyInfoEntity> props = copyProperty(propertyList);

        // プロパティの注番をサブカンバン名にする。
        String kanbanSubName = null;
        Optional<KanbanPropertyInfoEntity> porderProp = this.propertyList.stream().filter(prop -> Objects.equals(prop.getKanbanPropertyName(), LocaleUtils.getString("key.Porder"))).findFirst();
        if (porderProp.isPresent()) {
            kanbanSubName = porderProp.get().getKanbanPropertyValue();
        }

        // プロパティ表示順の最大値から、次に追加するプロパティの表示順を設定
        int propOrder = 1;
        Optional<KanbanPropertyInfoEntity> lastProp = props.stream().max(Comparator.comparingInt(p -> p.getKanbanPropertyOrder()));
        if (lastProp.isPresent()) {
            propOrder = lastProp.get().getKanbanPropertyOrder() + 1;
        }

        // シリアル番号をプロパティに追加
        String serialNumberPropertyKey = LocaleUtils.getString("key.SerialNumber");
        KanbanPropertyInfoEntity serialProp = new KanbanPropertyInfoEntity(null, null, serialNumberPropertyKey, CustomPropertyTypeEnum.TYPE_STRING, "", propOrder++);
        props.add(serialProp);

        // ロット数量をプロパティに追加
        KanbanPropertyInfoEntity lotSizeProp = new KanbanPropertyInfoEntity(null, null, LocaleUtils.getString("key.LotSize"), CustomPropertyTypeEnum.TYPE_INTEGER, instructionLot.getValue().toString(), propOrder++);
        props.add(lotSizeProp);

        // シリアル番号毎にカンバンを作成する。
        for (WorkPlanColorTextCellData listItem : serialList) {
            String serialNumber = listItem.getText();
            boolean isCreate = false;
            boolean isUpdate = false;
            KanbanInfoEntity kanban = new KanbanInfoEntity();
            try {
                // 対象シリアル番号のカンバンが登録済かチェックする。
                KanbanSearchCondition condition = new KanbanSearchCondition()
                        .kanbanName(serialNumber)
                        .workflowId(workflow.getWorkflowId());
                List<KanbanInfoEntity> findKanbans = kanbanFacade.findSearch(condition);
                if (!findKanbans.isEmpty()) {
                    Optional<KanbanInfoEntity> findKanban = findKanbans.stream().filter(k -> serialNumber.equals(k.getKanbanName())).findFirst();
                    if (findKanban.isPresent()) {
                        // 登録済のためスキップ
                        skipList.add(serialNumber);
                        continue;
                    }
                }

                // 全シリアル共通の情報をセット (プロパティは後で追加)
                kanban.setKanbanName(serialNumber);
                kanban.setKanbanSubname(kanbanSubName);
                kanban.setFkWorkflowId(workflowId);
                kanban.setWorkflowName(workflowName);
                kanban.setParentId(hierarchyId);
                kanban.setFkUpdatePersonId(loginUserId);
                kanban.setUpdateDatetime(updateDatetime);

                // カンバンを登録
                ResponseEntity response = kanbanFacade.regist(kanban);
                if (!response.isSuccess()) {
                    // 登録失敗
                    ngList.add(serialNumber);
                    continue;
                }
                isCreate = true;

                kanban = kanbanFacade.findURI(response.getUri());
                kanban.getWorkKanbanCollection().clear();
                kanban.getSeparateworkKanbanCollection().clear();

                Long workkanbanCnt = workKanbanFacade.countFlow(kanban.getKanbanId());
                for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                    kanban.getWorkKanbanCollection().addAll(workKanbanFacade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
                }

                Long separateCnt = workKanbanFacade.countSeparate(kanban.getKanbanId());
                for (long nowCnt = 0; nowCnt < separateCnt; nowCnt += RANGE) {
                    kanban.getSeparateworkKanbanCollection().addAll(workKanbanFacade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
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
                WorkPlanWorkflowProcess workflowProcess = new WorkPlanWorkflowProcess(workflow);
                List<BreakTimeInfoEntity> breakTimes = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanban.getWorkKanbanCollection());
                List<HolidayInfoEntity> holidays = WorkKanbanTimeReplaceUtils.getHolidays(kanban.getStartDatetime());
                workflowProcess.setBaseTime(kanban, breakTimes, new Date(), holidays);

                // 計画済みにして登録
                kanban.setKanbanStatus(KanbanStatusEnum.PLANNED);
                kanban.setUpdateDatetime(new Date());
                response = kanbanFacade.update(kanban);
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
                kanbanFacade.delete(kanban.getKanbanId());
            }
        }

        if (skipList.isEmpty() && ngList.isEmpty()) {
            // 全て成功
            Platform.runLater(() -> {
                // 情報をクリアする。
                this.ClearInfo();
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
                    this.serialList.add(new WorkPlanColorTextCellData(targetSerialNumber, COLOR_SKIP));
                });

                // 失敗したシリアル
                ngList.stream().forEach(targetSerialNumber -> {
                    this.serialList.add(new WorkPlanColorTextCellData(targetSerialNumber, COLOR_NG));
                });

                // カンバンが作成できなかったシリアル番号があります
                DialogBox.warn("key.Warning", "key.SerialNumberKanbanNotCreate");
                this.instructionCodeTextField.requestFocus();
            });
        }
    }

    /**
     * 入力項目を検証して、問題がなければ作成ボタンを有効にする
     *
     * @return
     */
    private boolean validCreateKanban() {

        // 工程順名
        if (Objects.isNull(this.workflow)) {
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
            this.propertyFactory = new WorkPlanKanbanPropertyRecordFactory(table, this.propertyList);
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
    private void ClearInfo() {
        this.workflowTextField.setText("");
        this.workflow = null;
        this.serialList.clear();
        this.instructionLot.set(0);
        this.updateProperty(null);
    }
}
