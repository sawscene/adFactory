/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component.lite;

import adtekfuji.admanagerapp.kanbaneditplugin.common.Constants;
import adtekfuji.admanagerapp.kanbaneditplugin.common.SelectedKanbanAndHierarchy;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.KanbanCheckerUtils;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.KanbanDefaultOffsetData;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.KanbanStartCompDate;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.WorkGroupPropertyData;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.WorkKanbanTimeReplaceData;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.WorkKanbanTimeReplaceUtils;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.WorkflowProcess;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.entity.kanban.KanbanCreateCondition;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.WorkGroup;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.plugin.KanbanRegistPreprocessContainer;
import jp.adtekfuji.adFactory.utility.KanbanRegistPreprocessResultEntity;
import jp.adtekfuji.javafxcommon.Locale;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import jp.adtekfuji.javafxcommon.dialog.MessageDialog;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン詳細画面クラス
 *
 * @author kenji.yokoi
 */
@FxComponent(id = "KanbanDetailLite", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/lite/kanban_detail_lite.fxml")
public class KanbanDetailLite implements Initializable, ArgumentDelivery, ComponentHandler {

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
    private final KanbanInfoFacade kanbanFacade = new KanbanInfoFacade();

    private KanbanInfoEntity kanban;
    private WorkflowInfoEntity workflow;
    private WorkflowProcess workflowProcess;
    private final LinkedList<WorkKanbanInfoEntity> workKanbanInfoEntitys = new LinkedList<>();
    private final LinkedList<KanbanPropertyInfoEntity> kanbanPropertyInfoEntitys = new LinkedList<>();
    private final KanbanDefaultOffsetData defaultOffsetData = new KanbanDefaultOffsetData();

    private final static Long RANGE = 20l;
    private final SimpleDateFormat DELIVERY_DATE_PATTERN = new SimpleDateFormat(rb.getString("key.DateFormat"));
    private final static String PICK_LITE_WORK_NAME_REGEX_KEY = "PickLiteWorkNameRegex";
    private String pickLiteWorkNameRegex;

    private Table workKanbanCustomTable;

    //最初に表示された情報のクローン　変更確認に使う
    private KanbanInfoEntity cloneKanban;

    @FXML
    private RestrictedTextField kanbanNameTextField;
    @FXML
    private TextField workflowNameTextField;
    @FXML
    private Label modelNameLabel;
    @FXML
    private RestrictedTextField modelNameTextField;
    @FXML
    private Button orderProcessesSelectButton;
    @FXML
    private ComboBox<KanbanStatusEnum> kanbanStatusCombo;
    @FXML
    private VBox workKanbanInfoPane;
    @FXML
    private DatePicker deliveryDatePicker;
    @FXML
    private Button registButton;
    @FXML
    private Button applyButton;
    @FXML
    private Pane progressPane;

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //役割の権限によるボタン無効化.
        if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            registButton.setDisable(true);
            applyButton.setDisable(true);
            orderProcessesSelectButton.setDisable(true);
            kanbanStatusCombo.setDisable(true);// カンバンステータスの変更を無効にする。
        }

        modelNameLabel.setVisible(false);
        modelNameTextField.setVisible(false);
        // 納期
        this.deliveryDatePicker.setValue(null);
        //プロパティから取得
        this.pickLiteWorkNameRegex = properties.getProperty(PICK_LITE_WORK_NAME_REGEX_KEY);
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        blockUI(false);
        try {
            if (argument instanceof SelectedKanbanAndHierarchy) {
                SelectedKanbanAndHierarchy selectedKanbanAndHierarchy = (SelectedKanbanAndHierarchy) argument;

                if (loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                    registButton.setDisable(true);
                    applyButton.setDisable(true);
                }

                this.kanban = selectedKanbanAndHierarchy.getKanbanInfo();
                if (Objects.nonNull(this.kanban)) {
                    // 編集の場合
                    this.modelNameLabel.setVisible(true);
                    this.modelNameTextField.setVisible(true);
                    runCreateWorkKanbanThread(String.format("kanban/%s", this.kanban.getKanbanId().toString()), false);
                } else {
                    // 新規作成の場合
                    this.modelNameLabel.setVisible(false);
                    this.modelNameTextField.setVisible(false);
                    this.kanban = new KanbanInfoEntity();
                    this.kanban.setParentId(selectedKanbanAndHierarchy.getHierarchyId());
                    this.kanban.setPropertyCollection(new ArrayList<>());
                    this.kanban.setKanbanStatus(KanbanStatusEnum.PLANNING);

                    //表示されたもののクローンを作成　破棄時に変更の確認を行う
                    cloneKanban = this.kanban.clone();
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onSelectWorkflow(ActionEvent event) {
        try {
            if (!kanbanNameTextField.getText().isEmpty()) {
                SelectDialogEntity<WorkflowInfoEntity> selectDialogEntity = new SelectDialogEntity();
                ButtonType ret = sc.showComponentDialog(rb.getString("key.OrderProcesses"), "WorkflowSingleSelectionCompo", selectDialogEntity);
                if (ret.equals(ButtonType.OK) && !selectDialogEntity.getWorkflows().isEmpty()) {
                    workflow = selectDialogEntity.getWorkflows().get(0);

                    if (StringUtils.isEmpty(workflow.getWorkflowDiaglam())) {
                        DialogBox.warn(Locale.ALERT_NO_WORK, Locale.ALERT_WORKFLOW_ERROR_DETAILS);
                        return;
                    }

                    workflowNameTextField.setText(workflow.getWorkflowName());
                    this.kanban.setKanbanName(kanbanNameTextField.getText());
                    this.kanban.setWorkflowName(workflow.getWorkflowName());
                    this.kanban.setFkWorkflowId(workflow.getWorkflowId());
                    this.kanban.setFkUpdatePersonId(loginUser.getId());
                    this.kanban.setUpdateDatetime(new Date());
                    this.kanban.setProductionType(0);

                    // モデル名
                    this.kanban.setModelName(workflow.getModelName());
                    this.modelNameTextField.setText(this.kanban.getModelName());

                    showWorkKanbanStarttimeOffsetDialog();
                    ResponseEntity responseEntity;

                    if (this.defaultOffsetData.isLot()) {
                        KanbanCreateCondition condition = new KanbanCreateCondition(this.kanban.getKanbanName(), this.kanban.getFkWorkflowId(), this.kanban.getParentId(),
                                loginUser.getLoginId(), this.defaultOffsetData.isOnePieceLot(), this.defaultOffsetData.getLotQuantity(),
                                this.defaultOffsetData.getStartOffsetTime(), new ArrayList<>(this.defaultOffsetData.getWorkGroups()), 1);
                        responseEntity = kanbanFacade.createConditon(condition);
                    } else {
                        this.kanban.setStartDatetime(this.defaultOffsetData.getStartOffsetTime());
                        responseEntity = kanbanFacade.regist(this.kanban);
                    }

                    if (!ResponseAnalyzer.getAnalyzeResult(responseEntity)) {
                        return;
                    }

                    if (loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                        registButton.setDisable(false);
                        applyButton.setDisable(false);
                    }

                    runCreateWorkKanbanThread(responseEntity.getUri(), true);
                }
            } else {
                Platform.runLater(() -> {
                    sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.EditKanbanTitle"), String.format(rb.getString("key.InputMessage"), rb.getString("key.KanbanName")));
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onCancel(ActionEvent event) {
        logger.info("onCancel:Start");
        if (destoryComponent()) {
            cloneKanban = null;
            sc.setComponent("ContentNaviPane", "LiteKanbanListCompo");
        }
    }

    /**
     * カンバンの変更内容を適用する
     *
     * @param event
     */
    @FXML
    private void onApply(ActionEvent event) {
        logger.info("onApply:Start");
        registKanban(false);
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onRegist(ActionEvent event) {
        logger.info("onRegist:Start");
        registKanban(true);
    }

    /**
     * 保存を実施する
     *
     * @param isTrans 保存後に画面をもどすかどうか
     */
    private boolean registKanban(boolean isTrans) {
        blockUI(true);

        try {
            logger.info("registKanban:Start");
            //未入力判定
            if (!registPreCheck()) {
                return false;
            }

            updateKanbanInfo();

            //表示されたもののクローンを作成　登録ボタンで画面遷移が発生するため
            cloneKanban = this.kanban.clone();

            registThread(isTrans);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            Platform.runLater(() -> blockUI(false));
        } finally {
            logger.info("registKanban:End");
        }

        return true;
    }

    /**
     * 実際に保存を実施する
     *
     * @param isTrans
     */
    private void registThread(boolean isTrans) {
        logger.info("registThread:Start");

        //保存前処理
        KanbanRegistPreprocessContainer plugin = KanbanRegistPreprocessContainer.getInstance();
        if (Objects.nonNull(plugin)) {
            KanbanRegistPreprocessResultEntity resultEntity = plugin.kanbanRegistPreprocess(kanban);
            if (!resultEntity.getResult()) {
                sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.EditKanbanTitle"), rb.getString(resultEntity.getResultMessage()));
                return;
            }
        }

        Task task = new Task<ResponseEntity>() {
            @Override
            protected ResponseEntity call() throws Exception {
                //保存処理実行
                return kanbanFacade.update(kanban);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                boolean isUpdateView = false;
                try {
                    ResponseEntity res = this.getValue();

                    if (ResponseAnalyzer.getAnalyzeResult(res)) {
                        if (isTrans) {
                            //非update時つまり新規作成時には元の画面に戻す
                            sc.setComponent("ContentNaviPane", "LiteKanbanListCompo");
                        } else {
                            // 排他用バージョンを取得しなおす。
                            KanbanInfoEntity newKanban = kanbanFacade.find(kanban.getKanbanId());
                            kanban.setVerInfo(newKanban.getVerInfo());
                            cloneKanban = kanban.clone();
                        }
                    } else {
                        if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                            sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.EditKanbanTitle"),
                                    String.format(rb.getString("key.FailedToUpdate"), rb.getString("key.Kanban") + rb.getString("key.Status")));
                        } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                            // 排他バージョンが異なる。
                            sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.EditKanbanTitle"), rb.getString("key.alert.differentVerInfo"));
                            // 再取得
                            isUpdateView = true;
                        }
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    if (isUpdateView) {
                        runCreateWorkKanbanThread(String.format("kanban/%s", kanban.getKanbanId().toString()), false);
                    } else {
                        blockUI(false);
                        //更新時カンバンステータスが変更可能なものになったら有効にする
                        settingKanbanEditDisable(kanban.getKanbanStatus());
                    }
                    logger.info("registWorkflowAssembly:End");
                }
            }

            @Override
            protected void failed() {
                super.failed();
                try {
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }

                    sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.EditKanbanTitle"), rb.getString("key.alert.systemError"));
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                    logger.info("registThread:End");
                }
            }
        };
        new Thread(task).start();
    }

    /**
     * プロパティ情報型表示用セルクラス
     *
     */
    class KanbanStatusEnumComboBoxCellFactory extends ListCell<KanbanStatusEnum> {

        @Override
        protected void updateItem(KanbanStatusEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(rb.getString(item.getResourceKey()));
            }
        }
    }

    /**
     * 画面更新用処理
     *
     */
    private void updateEditView() {
        try {
            Callback<ListView<KanbanStatusEnum>, ListCell<KanbanStatusEnum>> comboCellFactory = (ListView<KanbanStatusEnum> param) -> new KanbanStatusEnumComboBoxCellFactory();
            kanbanStatusCombo.setButtonCell(new KanbanStatusEnumComboBoxCellFactory());
            kanbanStatusCombo.setCellFactory(comboCellFactory);
            Platform.runLater(() -> {
                kanbanStatusCombo.valueProperty().removeListener(changeStatusListener);
                kanbanStatusCombo.setItems(FXCollections.observableArrayList(displayKanbanStatusList(this.kanban.getKanbanStatus())));
                kanbanStatusCombo.setValue(this.kanban.getKanbanStatus());
                kanbanStatusCombo.valueProperty().addListener(changeStatusListener);
                kanbanStatusCombo.setVisible(true);
                kanbanNameTextField.setText(this.kanban.getKanbanName());
                workflowNameTextField.setText(this.kanban.getWorkflowName());
                modelNameLabel.setVisible(true);
                modelNameTextField.setVisible(true);
                modelNameTextField.setText(this.kanban.getModelName());

                createWorkKanbanTable();
                createKanbanPropertyTable();
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程カンバンリストを生成する。
     *
     */
    private void createWorkKanbanTable() {
        if (!Objects.nonNull(this.kanban.getWorkKanbanCollection())) {
            this.kanban.setWorkKanbanCollection(new ArrayList<>());
        }
        workKanbanInfoPane.getChildren().clear();
        workKanbanInfoEntitys.clear();
        workKanbanInfoEntitys.addAll(this.kanban.getWorkKanbanCollection());

        WorkKanbanTimeReplaceData timeReplaceData = new WorkKanbanTimeReplaceData();
        timeReplaceData.workKanbanInfoEntitys(workKanbanInfoEntitys).referenceStartTime(defaultOffsetData.getStartOffsetTime())
                .breakTimeInfoEntitys(WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(workKanbanInfoEntitys))
                .dateFormat(rb.getString("key.DateTimeFormat")).kanbanDefaultOffsetData(defaultOffsetData);

        workKanbanCustomTable = new Table(workKanbanInfoPane.getChildren())
                .isColumnTitleRecord(true)
                .isChangeDataRowColor(true)
                .title(rb.getString("key.OrderProcesses"))
                .styleClass("ContentTitleLabel");
        workKanbanCustomTable.setAbstractRecordFactory(
            new WorkKanbanRecordFactoryLite(workKanbanCustomTable, workKanbanInfoEntitys,
                this.kanban, this.workflowProcess, this.onCompActionEvent)
        );
    }

    /**
     * プロパティ編集テーブル作成
     *
     */
    private void createKanbanPropertyTable() {
        if (!Objects.nonNull(this.kanban.getPropertyCollection())) {
            this.kanban.setPropertyCollection(new ArrayList<>());
        }
        kanbanPropertyInfoEntitys.clear();
        kanbanPropertyInfoEntitys.addAll(this.kanban.getPropertyCollection());
        kanbanPropertyInfoEntitys.sort((Comparator.comparing(e -> e.getKanbanPropertyOrder())));

        // 納期
        Optional<KanbanPropertyInfoEntity> find = null;
        find = this.kanbanPropertyInfoEntitys.stream().filter(prop -> Objects.equals(prop.getKanbanPropertyName(), rb.getString("key.DeliveryTime"))).findFirst();
        if (find.isPresent()) {
            try {
                Date date = DELIVERY_DATE_PATTERN.parse(find.get().getKanbanPropertyValue());
                deliveryDatePicker.setValue(DateUtils.toLocalDate(date));
            } catch (ParseException ex) {
            }
        }
    }

    /**
     * ワークカンバン作成用スレッド
     *
     */
    private void runCreateWorkKanbanThread(String uri, boolean isNewCreate) {
        logger.info("runCreateWorkKanbanThread: start.");
        this.blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    kanban = kanbanFacade.findURI(uri);
                    kanban.getWorkKanbanCollection().clear();
                    kanban.getSeparateworkKanbanCollection().clear();

                    WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
                    workflow = workflowInfoFacade.find(kanban.getFkWorkflowId());
                    workflowProcess = new WorkflowProcess(workflow);

                    WorkKanbanInfoFacade workKanbanInfoFacade = new WorkKanbanInfoFacade();

                    // 工程順
                    Long workkanbanCnt = workKanbanInfoFacade.countFlow(kanban.getKanbanId());
                    for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                        kanban.getWorkKanbanCollection().addAll(workKanbanInfoFacade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
                    }

                    // 追加工程
                    Long separateCnt = workKanbanInfoFacade.countSeparate(kanban.getKanbanId());
                    for (long nowCnt = 0; nowCnt < separateCnt; nowCnt += RANGE) {
                        kanban.getSeparateworkKanbanCollection().addAll(workKanbanInfoFacade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanban.getKanbanId()));
                    }

                    cloneKanban = kanban.clone();

                    Platform.runLater(() -> {
                        updateEditView();

                        if (loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                            registButton.setDisable(false);
                            applyButton.setDisable(false);
                        }

                        workflowNameTextField.setEditable(false);
                        orderProcessesSelectButton.setDisable(true);
                        settingKanbanEditDisable(kanban.getKanbanStatus());

                        blockUI(false);
                    });
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    Platform.runLater(() -> blockUI(false));
                } finally {
                    logger.info("runCreateWorkKanbanThread: end.");
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 開始時間設定ダイアログ表示
     *
     */
    private void showWorkKanbanStarttimeOffsetDialog() {
        try {
            SimpleDateFormat dateSdf = new SimpleDateFormat(rb.getString("key.DateTimeFormat"));
            defaultOffsetData.setStartOffsetTime(new Date());
            defaultOffsetData.setCheckOffsetWorkingHours(false);

            // 始業終業情報の取得
            if ("".equals(properties.getProperty(Constants.OPENING_DATE_TIME, ""))) {
                defaultOffsetData.setOpeningTime(new Date());
            } else {
                defaultOffsetData.setOpeningTime(dateSdf.parse(properties.getProperty(Constants.OPENING_DATE_TIME)));
            }
            if ("".equals(properties.getProperty(Constants.CLOSING_DATE_TIME, ""))) {
                defaultOffsetData.setClosingTime(new Date());
            } else {
                defaultOffsetData.setClosingTime(dateSdf.parse(properties.getProperty(Constants.CLOSING_DATE_TIME)));
            }
            if ("".equals(properties.getProperty(Constants.LOT_QUANTITY, ""))) {
                defaultOffsetData.setLotQuantity(Constants.DEFAULT_LOT_QUANTITY);
            } else {
                defaultOffsetData.setLotQuantity(Integer.valueOf(properties.getProperty(Constants.LOT_QUANTITY)));
            }

            // カンバン作成ダイアログ
            if (ButtonType.OK != sc.showDialog(rb.getString("key.MakedKanban"), "WorkKanbanStarttimeOffsetCompo", defaultOffsetData)) {
                return;
            }

            for (WorkGroupPropertyData group : defaultOffsetData.getWorkGroupProps()) {
                group.update();
                defaultOffsetData.getWorkGroups().add(new WorkGroup(group.getQauntity(), group.getStartTime()));
            }

            // 始業終業情報の更新
            if (Objects.nonNull(defaultOffsetData.getOpeningTime())) {
                properties.setProperty(Constants.OPENING_DATE_TIME, dateSdf.format(defaultOffsetData.getOpeningTime()));
            } else {
                properties.setProperty(Constants.OPENING_DATE_TIME, "");
            }
            if (Objects.nonNull(defaultOffsetData.getClosingTime())) {
                properties.setProperty(Constants.CLOSING_DATE_TIME, dateSdf.format(defaultOffsetData.getClosingTime()));
            } else {
                properties.setProperty(Constants.CLOSING_DATE_TIME, "");
            }
            if (Objects.nonNull(defaultOffsetData.getLotQuantity())) {
                properties.setProperty(Constants.LOT_QUANTITY, String.valueOf(defaultOffsetData.getLotQuantity()));
            } else {
                properties.setProperty(Constants.LOT_QUANTITY, "");
            }
        } catch (ParseException | NumberFormatException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 保存前処理
     *
     * @return
     */
    private Boolean registPreCheck() {
        //未入力判定
        if (KanbanCheckerUtils.checkEmptyKanban(kanbanNameTextField.getText(), kanbanPropertyInfoEntitys)
                || Objects.isNull(this.kanban.getWorkKanbanCollection())
                || Objects.isNull(this.kanban.getSeparateworkKanbanCollection())) {
            sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.Warning"), rb.getString("key.NotInputMessage"));
            Platform.runLater(() -> {
                blockUI(false);
            });
            return false;
        }
        switch (KanbanCheckerUtils.validItems(this.kanban)) {
            case TIME_COMP_ERR:
                Platform.runLater(() -> {
                    sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.Warning"), rb.getString("key.TimeFormatErrMessage"));
                    blockUI(false);
                });
                return false;
            case DATE_COMP_ERR:
                Platform.runLater(() -> {
                    sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.Warning"), rb.getString("key.DateCompErrMessage"));
                    blockUI(false);
                });
                return false;
            case SUCCSESS:
                logger.info("Clear KanbanData Validation.");
                break;
        }

        return true;
    }

    /**
     * カンバンステータスに合わせて編集可能か設定する
     *
     * @param status カンバンステータス
     */
    private void settingKanbanEditDisable(KanbanStatusEnum status) {
        //カンバンステータスが"計画済み"または"作業中"の場合、編集不可にする
        if (status.equals(KanbanStatusEnum.PLANNED) || status.equals(KanbanStatusEnum.WORKING)) {
            modelNameLabel.setDisable(true);
            modelNameTextField.setDisable(true);
        } else {
            modelNameLabel.setDisable(false);
            modelNameTextField.setDisable(false);
        }
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        progressPane.setVisible(flg);
    }

    /**
     * 変更されたか確認する
     *
     * @return
     */
    private boolean isChanged() {
        return isChanged(true);
    }

    /**
     * 変更されたか確認する
     *
     * @param isCheckStatus カンバンステータスの変更をチェックするか(true = チェックする)
     * @return
     */
    private boolean isChanged(boolean isCheckStatus) {
        // カンバン作成の権限がない場合、変更なしの扱いとする。(変更内容を保存しない)
        if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            return false;
        }

        updateKanbanInfo(); //現在の状態にEntityを更新

        if (Objects.isNull(cloneKanban)
                || Objects.isNull(this.kanban)) {
            return false;
        }

        //変更の確認なので一致したらfalseを返す　
        if (cloneKanban.equalsDisplayInfo(this.kanban, isCheckStatus)) {
            return false;
        }

        return true;
    }

    /**
     * 画面破棄時に内容に変更がないか調べて変更があるなら保存する
     *
     * @return 保存に成功したとき、または変更が存在しなかった場合true<br>ダイアログでキャンセルが押された場合false
     */
    @Override
    public boolean destoryComponent() {
        if (isChanged()) {
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = rb.getString("key.confirm");
            String message = rb.getString("key.confirm.destroy");

            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            if (ButtonType.YES == buttonType) {
                return registKanban(false);
            } else if (ButtonType.CANCEL == buttonType) {
                return false;
            }
        }

        return true;
    }

    /**
     * フィールドに記述されている現在の情報を更新する
     *
     */
    private void updateKanbanInfo() {

        //カンバン名の更新
        this.kanban.setKanbanName(kanbanNameTextField.getText());

        //新規作成時、以下の追加工程などは存在しないため更新しない
        if (Objects.isNull(this.kanban.getPropertyCollection())
                || Objects.isNull(this.kanban.getWorkKanbanCollection())
                || Objects.isNull(this.kanban.getSeparateworkKanbanCollection())) {
            return;
        }

        this.kanban.setModelName(this.modelNameTextField.getText());

        //カンバンプロパティ更新
        this.kanban.getPropertyCollection().clear();
        this.kanban.setKanbanStatus(kanbanStatusCombo.getSelectionModel().getSelectedItem());
        this.kanban.setFkUpdatePersonId(loginUser.getId());
        //納期の更新
        Optional<KanbanPropertyInfoEntity> find = this.kanbanPropertyInfoEntitys.stream()
            .filter(prop -> Objects.equals(prop.getKanbanPropertyName(), rb.getString("key.DeliveryTime"))).findFirst();
        if (find.isPresent() && deliveryDatePicker.getValue() == null) {
            this.kanbanPropertyInfoEntitys.remove(find.get());
        } else if (deliveryDatePicker.getValue() != null) {
            String date = DELIVERY_DATE_PATTERN.format(DateUtils.toDate(deliveryDatePicker.getValue()));
            if (find.isPresent()) {
                find.get().setKanbanPropertyValue(date);
            } else {
                kanbanPropertyInfoEntitys.add(new KanbanPropertyInfoEntity(
                    null, null, rb.getString("key.DeliveryTime"), CustomPropertyTypeEnum.TYPE_DATE, date, this.kanbanPropertyInfoEntitys.size()));
            }
        }
        
        int order = 0;
        for (KanbanPropertyInfoEntity entity : kanbanPropertyInfoEntitys) {
            entity.setFkKanbanId(this.kanban.getKanbanId());
            entity.updateMember();
            entity.setKanbanPropertyOrder(order);

            // プロパティ値は、2行目以降に改行とスペースしかない場合、2行目以降は削除する。
            entity.setKanbanPropertyValue(this.deleteUnnecessaryCharacters(entity.getKanbanPropertyValue()));

            this.kanban.getPropertyCollection().add(entity);
            order = order + 1;
        }
        //工程順更新
        for (WorkKanbanInfoEntity entity : this.kanban.getWorkKanbanCollection()) {
            entity.updateMember();
            for (WorkKanbanPropertyInfoEntity proEntity : entity.getPropertyCollection()) {
                proEntity.updateMember();
            }
        }
        //追加工程更新
        for (WorkKanbanInfoEntity entity : this.kanban.getSeparateworkKanbanCollection()) {
            entity.updateMember();
            for (WorkKanbanPropertyInfoEntity proEntity : entity.getPropertyCollection()) {
                proEntity.updateMember();
            }
        }

        //工程カンバン更新
        List<WorkKanbanInfoEntity> workKanbans = new ArrayList<>();
        workKanbans.addAll(this.kanban.getWorkKanbanCollection());
        workKanbans.addAll(this.kanban.getSeparateworkKanbanCollection());

        this.kanban.setStartDatetime(KanbanStartCompDate.getWorkKanbanStartDateTime(workKanbans));
        this.kanban.setCompDatetime(KanbanStartCompDate.getWorkKanbanCompDateTime(workKanbans));

        this.kanban.updateMember();
    }

    /**
     * カンバンステータス変更のリスナー
     *
     * @author HN)y-harada
     */
    private final ChangeListener<KanbanStatusEnum> changeStatusListener = (ObservableValue<? extends KanbanStatusEnum> observable, KanbanStatusEnum oldValue, KanbanStatusEnum newValue) -> {
        Platform.runLater(() -> {
            try {
                if (Objects.nonNull(newValue) && !Objects.equals(oldValue, newValue)) {

                    // カンバン編集画面に変更内容がある場合、カンバンステータス変更APIを呼び出す前に「入力内容が保存されていません」を表示する。
                    if (this.isChanged(false)) {
                        String title = rb.getString("key.confirm");
                        String message = rb.getString("key.confirm.change");

                        ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.CLOSE}, ButtonType.CLOSE);
                        this.setKanbanStatusComboValue(oldValue);
                        return;
                    }

                    if (this.updateableStatus(newValue)) {
                        this.runChangeStatusThread(oldValue);
                    } else {
                        this.setKanbanStatusComboValue(oldValue);
                    }
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });
    };

    /**
     * カンバンステータスが更新可能かどうかを返す。
     * 
     * @param oldStatus 変更前のカンバンステータス
     * @param newStatus 変更後のカンバンステータス
     * @return true:更新可能、false:更新不可
     */
    private boolean updateableStatus(KanbanStatusEnum newStatus) {

        boolean apply = true;
        boolean confirm = false;
   
        try {
            KanbanInfoEntity nowKanban = kanbanFacade.find(kanban.getKanbanId());
            logger.info("updateableStatus: oldStatus={}, newStatus={}", nowKanban.getKanbanStatus(), newStatus);

            switch (newStatus) {
                case PLANNING:
                    switch (nowKanban.getKanbanStatus()) {
                        case PLANNING:
                            apply = false;
                            break;
                        case PLANNED:
                            break;
                        case WORKING:
                        case SUSPEND:
                            // 問い合わせが必要
                            confirm = true;
                            break;
                        case COMPLETION:
                        case INTERRUPT:
                            break;
                    }
                    break;

                case PLANNED:
                    switch (nowKanban.getKanbanStatus()) {
                        case PLANNING:
                        case PLANNED:
                            break;
                        case WORKING:
                        case SUSPEND:
                            // 問い合わせが必要
                            confirm = true;
                            break;
                        case COMPLETION:
                        case INTERRUPT:
                            // 問い合わせが必要
                            confirm = true;
                            break;
                    }
                    break;

                case COMPLETION:
                    switch (nowKanban.getKanbanStatus()) {
                        case PLANNING:
                        case PLANNED:
                            apply = false;
                            break;
                        case WORKING:
                        case SUSPEND:
                            // 問い合わせが必要
                            confirm = true;
                            break;
                        case COMPLETION:
                        case INTERRUPT:
                            apply = false;
                            break;
                    }
                    break;

                case INTERRUPT:
                    switch (nowKanban.getKanbanStatus()) {
                        case PLANNING:
                        case PLANNED:
                            break;
                        case WORKING:
                        case SUSPEND:
                            // 更新可能か
                            confirm = true;
                            break;
                        case COMPLETION:
                        case INTERRUPT:
                            apply = false;
                            break;
                    }
                    break;
            }
            
            if (apply && KanbanStatusEnum.PLANNING.equals(newStatus)) {
                String title = rb.getString("key.confirm");
                String message = rb.getString("key.ChangeStatusPlanning");

                apply = false;

                MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(sc.getWindow(), title, message,
                        MessageDialogEnum.MessageDialogType.Warning, MessageDialogEnum.MessageDialogButtons.YesNo, 3.0, "#ff0000", "#ffffff");
                if (MessageDialogEnum.MessageDialogResult.Yes.equals(dialogResult)) {
                    apply = true;
                }
            }

            if (apply && confirm) {
                boolean exists1 = nowKanban.getWorkKanbanCollection().stream()
                        .anyMatch(p -> KanbanStatusEnum.WORKING.equals(p.getWorkStatus()));

                boolean exists2 = nowKanban.getSeparateworkKanbanCollection().stream()
                        .anyMatch(p -> KanbanStatusEnum.WORKING.equals(p.getWorkStatus()));
                
                if (exists1 || exists2) {
                    String title = rb.getString("key.confirm");
                    String message = rb.getString("key.ChangeStatusWorking");

                    apply = false;

                    MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(sc.getWindow(), title, message,
                            MessageDialogEnum.MessageDialogType.Warning, MessageDialogEnum.MessageDialogButtons.YesNo, 3.0, "#ff0000", "#ffffff");
                    if (MessageDialogEnum.MessageDialogResult.Yes.equals(dialogResult)) {
                        apply = true;
                    }
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex);
        }
        
        return apply;
    }

    /**
     * カンバンステータス変更スレッド
     *
     * @author HN)y-harada
     */
    private void runChangeStatusThread(KanbanStatusEnum oldValue) {
        try {
            blockUI(true);

            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
                    // カンバンのステータスを変更する。
                    return kanbanFacade.updateStatus(Arrays.asList(kanban.getKanbanId()), kanbanStatusCombo.getValue(), true, loginUser.getId());
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    // 処理結果
                    ResponseEntity res = this.getValue();
                    if (Objects.isNull(res) || Objects.isNull(res.getErrorType())) {
                        Platform.runLater(() -> {
                            sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.ChangeStatus"), String.format(rb.getString("key.FailedToUpdate"), rb.getString("key.Status")));
                            setKanbanStatusComboValue(oldValue);
                            blockUI(false);
                        });
                        return;
                    }

                    if (res.isSuccess()) {
                        // カンバンステータス変更APIの戻り値が SUCCESS の場合、カンバン情報を再取得して、画面を更新する。
                        runCreateWorkKanbanThread(String.format("kanban/%s", kanban.getKanbanId().toString()), false);
                    } else {
                        DialogBox.alert(res.getErrorType());
                        Platform.runLater(() -> {
                            sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.ChangeStatus"), String.format(rb.getString("key.FailedToUpdate"), rb.getString("key.Status")));
                            setKanbanStatusComboValue(oldValue);
                            blockUI(false);
                        });
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                        Platform.runLater(() -> {
                            setKanbanStatusComboValue(oldValue);
                            blockUI(false);
                        });
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);

            // エラー
            sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.ChangeStatus"), String.format(rb.getString("key.FailedToUpdate"), rb.getString("key.Status")));
            Platform.runLater(() -> {
                setKanbanStatusComboValue(oldValue);
                blockUI(false);
            });

        }
    }

    /**
     * ステータスドロップダウンリストに表示するカンバンステータスのListを返す
     *
     * ※表示するカンバンステータス→現在のカンバンステータスに基づいた変更可能ステータス＋現在のカンバンステータス
     * 要求仕様書ver1.9.6のカンバン操作シート参照
     *
     * @author HN)y-harada
     * @param nowStatus 現在のカンバンステータス
     * @return List<KanbanStatusEnum> 表示するカンバンステータスリスト
     */
    private List<KanbanStatusEnum> displayKanbanStatusList(KanbanStatusEnum nowStatus) {
        logger.info("displayStatusList start.");
        List<KanbanStatusEnum> list = new ArrayList<>();

        list.addAll(Arrays.asList(KanbanStatusEnum.values()));
        
        //ステータス毎に変更不可のものを除く
        switch (nowStatus) {
            case PLANNING:
            case PLANNED:
            case INTERRUPT:
                list.remove(list.indexOf(KanbanStatusEnum.WORKING));
                list.remove(list.indexOf(KanbanStatusEnum.SUSPEND));
                list.remove(list.indexOf(KanbanStatusEnum.COMPLETION));
                break;
            case WORKING:
                list.remove(list.indexOf(KanbanStatusEnum.SUSPEND));
                break;
            case SUSPEND:
                list.remove(list.indexOf(KanbanStatusEnum.WORKING));
                break;
            case COMPLETION:
                list.remove(list.indexOf(KanbanStatusEnum.WORKING));
                list.remove(list.indexOf(KanbanStatusEnum.SUSPEND));
                list.remove(list.indexOf(KanbanStatusEnum.INTERRUPT));
                break;
        }

        logger.info("displayStatusList end.");
        return list;
    }

    /**
     * リスナーに反応しないようにカンバンステータスドロップダウンリストの値を変更する
     *
     * @author HN)y-harada
     */
    private void setKanbanStatusComboValue(KanbanStatusEnum value) {
        kanbanStatusCombo.valueProperty().removeListener(changeStatusListener);
        kanbanStatusCombo.setValue(value);
        kanbanStatusCombo.valueProperty().addListener(changeStatusListener);
    }

    /**
     * 2行目以降に改行とスペースしかない場合は1行目のみ返し、他の文字がある場合はそのまま返す。
     *
     * @param value 入力文字列
     * @return 出力文字列
     */
    private String deleteUnnecessaryCharacters(String value) {
        if (Objects.isNull(value)) {
            return  value;
        }

        List<String> rows = Arrays.asList(value.split("\n"));
        if (rows.size() >= 2) {
            for (String row : rows) {
                String s = row.replaceAll(" ", "").replaceAll("　", "");
                if (!s.isEmpty()) {
                    return value;
                }
            }
        }

        return rows.get(0);
    }

    /**
     * 完了アクション
     */
    private final EventHandler onCompActionEvent = (EventHandler) (Event event) -> {
        try {
            Button cellButton = (Button) event.getSource();
            WorkKanbanInfoEntity entity = (WorkKanbanInfoEntity) cellButton.getUserData();
                    
            Long equipmentId = this.getRootEquipmentId();

            //工程名だけ抽出
            String workName = entity.getWorkName();
            try
            {
                Matcher m = Pattern.compile(this.pickLiteWorkNameRegex).matcher(workName);
                if (m.find()) {
                    workName = m.group(1);
                }
            } catch(Exception ex) {
            }
            boolean apply = false;
            KanbanStatusEnum status = entity.getWorkStatus();
            if (status == KanbanStatusEnum.WORKING) {
                // 作業中のときの完了確認
                String title = rb.getString("key.confirm");
                String message = rb.getString("key.ConfirmationCompWorkingProcess");
                MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(
                    sc.getWindow(), title, String.format(message, workName),
                    MessageDialogEnum.MessageDialogType.Warning, MessageDialogEnum.MessageDialogButtons.YesNo, 3.0, "#ff0000", "#ffffff");
                if (MessageDialogEnum.MessageDialogResult.Yes.equals(dialogResult)) {
                    apply = true;
                }
            } else if (status != KanbanStatusEnum.COMPLETION) {
                // 完了確認
                String title = rb.getString("key.confirm");
                String message = rb.getString("key.ConfirmationCompProcess");
                MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(
                    sc.getWindow(), title, String.format(message, workName),
                    MessageDialogEnum.MessageDialogType.Question, MessageDialogEnum.MessageDialogButtons.YesNo, 3.0, "#ff0000", "#ffffff");
                if (MessageDialogEnum.MessageDialogResult.Yes.equals(dialogResult)) {
                    apply = true;
                }
            }
            if (apply) {
                // 実績登録
                this.actualThread(equipmentId, entity);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    };

    /**
     * 実績登録
     * 
     * @param equipmentId
     * @param workKanban 
     */
    private void actualThread(Long equipmentId, WorkKanbanInfoEntity workKanban) {
        blockUI(true);
        this.getRootEquipmentId();
        // 工程を同時刻で完了とし、ログイン中のアカウント、設備はルートで実績を残す。
        ActualProductReportEntity report = new ActualProductReportEntity(
            0L, workKanban.getFkKanbanId(), workKanban.getWorkKanbanId(),
            rb.getString("key.adManagerAppTitle"), new Date(), KanbanStatusEnum.WORKING, null, null);
        report.setEquipmentId(equipmentId);
        report.setOrganizationId(LoginUserInfoEntity.getInstance().getId());
        Task task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                Boolean bRet = true;
                try {
                    Long tid = 0L;
                    ActualProductReportResult result;
                    // 実績開始を送る
                    if (!workKanban.getWorkStatus().equals(KanbanStatusEnum.WORKING)) {
                        result = kanbanFacade.report(report);
                        if (!ResponseAnalyzer.getAnalyzeActualProductReportResult(result)) {
                            logger.info("start report err!! :{}", result);
                            bRet = false;
                        }
                        tid = result.getNextTransactionID();
                    }
                    // 実績完了を送る
                    if (bRet) {
                        report.setTransactionId(tid);
                        report.setStatus(KanbanStatusEnum.COMPLETION);
                        result = kanbanFacade.report(report);
                        if (!ResponseAnalyzer.getAnalyzeActualProductReportResult(result)) {
                            logger.info("comp report err!! :{}", result);
                            bRet = false;
                        }
                    }
                    if (bRet) {
                        // カンバン再取得
                        runCreateWorkKanbanThread(String.format("kanban/%s", kanban.getKanbanId().toString()), false);
                    }
                } finally {
                    blockUI(false);
                }
                return bRet;
            }
        };
        new Thread(task).start();
    }
    
    /**
     * 端末ルートを取得
     * @return 
     */
    private Long getRootEquipmentId() {
        // adManagerからの実績送信時の設備は固定値（設備ID=0）を使用する。
        return 0L;
    }
}
