/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component.lite;

import adtekfuji.admanagerapp.kanbaneditplugin.common.KanbanEditPermanenceData;
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
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.Locale;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン作成画面コントローラー
 *
 * @author kenji.yokoi
 */
@FxComponent(id = "KanbanCreateLite", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/lite/kanban_create_lite.fxml")
public class KanbanCreateLite implements Initializable, SerialCommunicationListener, ArgumentDelivery {

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

    private SelectedKanbanAndHierarchy kanbanHierarchy;
    private WorkflowInfoEntity workflow;

    private final StringProperty kanbanName = new SimpleStringProperty();
    private final StringProperty modelName = new SimpleStringProperty();

    private final String defaultWorkflowName = this.properties.getProperty(Config.DEFAULT_LITE_WORKFLOW);
    private final String defaultWorkflowRev = this.properties.getProperty(Config.DEFAULT_LITE_WORKFLOW_REV);

    private static final String CHARSET = "UTF-8";
    private static final Long RANGE = 20l;
    private static final String BARCODE_CREATE_KANBAN = "ADD_PLAN_INFO";
    private final SimpleDateFormat DELIVERY_DATE_PATTERN = new SimpleDateFormat(rb.getString("key.DateFormat"));

    @FXML
    private RestrictedTextField kanbanNameTextField;
    @FXML
    private TextField workflowTextField;
    @FXML
    private RestrictedTextField modelNameTextField;
    @FXML
    private DatePicker deliveryDatePicker;
    @FXML
    private Button createButton;
    @FXML
    private Pane progressPane;

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
            if (!this.properties.containsKey(Config.LOT_PRODUCTION_DEFAULT)) {
                this.properties.put(Config.LOT_PRODUCTION_DEFAULT, "false");
            }

            // 工程順
            if (Objects.nonNull(defaultWorkflowName) && !defaultWorkflowName.isEmpty()
                    && Objects.nonNull(defaultWorkflowRev) && !defaultWorkflowRev.isEmpty()) {
                this.workflowTextField.setText(defaultWorkflowName);
            }

            // カンバン名
            this.kanbanNameTextField.textProperty().bindBidirectional(this.kanbanName);
            this.kanbanNameTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                this.validCreateKanban();
            });
            this.kanbanNameTextField.focusedProperty().addListener(changeListener);

            // モデル名
            this.modelNameTextField.textProperty().bindBidirectional(this.modelName);

            // 納期
            this.deliveryDatePicker.setValue(null);

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

                logger.info("KanbanMultipleRegistCompo:{}", rb.getString("key.KanbanContinuousCreate"));

                boolean useBarcode = StringUtils.parseBoolean(this.properties.getProperty(Config.USE_BARCODE));
                if (useBarcode) {
                    try {
                        kanbanEditPermanenceData.connectSerialComm(this);
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.ConnectionErrorTitle"), rb.getString("key.SerialCommErrUnconnection"));
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

            this.workflowTextField.setText(this.workflow.getWorkflowName());
            this.modelNameTextField.setText(this.workflow.getModelName());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.workflowTextField.setText("");
            this.modelNameTextField.setText("");
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

            SelectDialogEntity<WorkflowInfoEntity> selectDialogEntity = new SelectDialogEntity().liteHierarchyOnly();
            ButtonType ret = sc.showComponentDialog(rb.getString("key.OrderProcesses"), "WorkflowSingleSelectionCompo", selectDialogEntity, true);
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
                            kanbanNameTextField.requestFocus();
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
     * キャンセルボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onClose(ActionEvent event) {
        try {
            logger.info("onClose start.");

            if (Objects.nonNull(this.workflow) && Objects.nonNull(this.workflow.getWorkflowId())) {
                properties.put(Config.DEFAULT_LITE_WORKFLOW, this.workflow.getWorkflowName());
                properties.put(Config.DEFAULT_LITE_WORKFLOW_REV, String.valueOf(this.workflow.getWorkflowRev()));
            }

            kanbanEditPermanenceData.disconnectSerialComm();

            sc.setComponent("ContentNaviPane", "LiteKanbanListCompo");
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
                            deliveryDatePicker.setValue(null);

                            createButton.setDisable(true);
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
     * カンバンを登録する。
     *
     * @return カンバンの登録に成功した場合true、失敗した場合falseを返す。
     * @throws Exception
     */
    private boolean registKanban() throws Exception {
        KanbanInfoEntity kanban = new KanbanInfoEntity();

        kanban.setKanbanName(kanbanName.get());
        kanban.setModelName(modelName.get());// モデル名
        kanban.setFkWorkflowId(workflow.getWorkflowId());
        kanban.setWorkflowName(workflow.getWorkflowName());
        kanban.setParentId(kanbanHierarchy.getHierarchyId());
        kanban.setProductionType(0);

        // ※プロパティはregistで登録されないので、後で追加する。
        kanban.setStartDatetime(new Date());
        kanban.setFkUpdatePersonId(loginUser.getId());
        kanban.setUpdateDatetime(new Date());

        //納期の更新
        Optional<KanbanPropertyInfoEntity> find = this.propertyList.stream()
            .filter(prop -> Objects.equals(prop.getKanbanPropertyName(), rb.getString("key.DeliveryTime"))).findFirst();
        if (find.isPresent() && deliveryDatePicker.getValue() == null) {
            this.propertyList.remove(find.get());
        } else if (deliveryDatePicker.getValue() != null) {
            String date = DELIVERY_DATE_PATTERN.format(DateUtils.toDate(deliveryDatePicker.getValue()));
            if (find.isPresent()) {
                find.get().setKanbanPropertyValue(date);
            } else {
                propertyList.add(new KanbanPropertyInfoEntity(
                    null, null, rb.getString("key.DeliveryTime"), CustomPropertyTypeEnum.TYPE_DATE, date, this.propertyList.size()));
            }
        }

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

        if (this.kanbanName.isEmpty().get()) {
            this.createButton.setDisable(true);
            return false;
        }

        this.createButton.setDisable(false);
        return true;
    }

    /**
     * 「カンバンを作成しました」を表示する
     */
    public void infoCreateKanban() {
        try {
            String title = rb.getString(Locale.APPLICATION_TITLE);
            String message = rb.getString(Locale.INFO_CREATE_KANBAN);
            String details = rb.getString("key.KanbanName") + ": " + this.kanbanName.get();
            String okButtonText = rb.getString(Locale.OK);
            DialogBox.Show(sc.getStage().getScene().getWindow(), title, message, details, okButtonText, null, DialogBox.DialogType.INFOMATION, 2000L);
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }
}
