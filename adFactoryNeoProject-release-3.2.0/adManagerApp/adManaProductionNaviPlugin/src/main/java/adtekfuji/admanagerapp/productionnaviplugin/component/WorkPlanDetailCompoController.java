/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanConstants;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanKanbanPropertyRecordFactory;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleTypeEnum;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanSelectedKanbanAndHierarchy;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanWorkKanbanRecordFactory;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanWorkSettingDialogEntity;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkKanbanTimeReplaceData;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkKanbanTimeReplaceUtils;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanKanbanCheckerUtils;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanKanbanDefaultOffsetData;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanKanbanStartCompDate;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanWorkGroupPropertyData;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanWorkflowProcess;
import adtekfuji.admanagerapp.productionnaviplugin.utils.scheduling.AutomaticScheduling;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.WorkInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringTime;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import static java.util.stream.Collectors.toList;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.comparator.WorkKanbanStartDateComparator;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanCreateCondition;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.WorkGroup;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
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
 * @author property.mori
 * @version 1.6.1
 * @since 2017.1.11.Wen
 */
@FxComponent(id = "WorkPlanDetailCompo", fxmlPath = "/fxml/compo/work_plan_detail_compo.fxml")
public class WorkPlanDetailCompoController implements Initializable, ArgumentDelivery, ComponentHandler {

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
    private final KanbanInfoFacade kanbanFacade = new KanbanInfoFacade();

    private KanbanInfoEntity kanban;
    private WorkflowInfoEntity workflow;
    private WorkPlanWorkflowProcess workflowProcess;
    private AutomaticScheduling automaticScheduling;
    private final LinkedList<WorkKanbanInfoEntity> workKanbanInfoEntitys = new LinkedList<>();
    private final LinkedList<WorkKanbanInfoEntity> separateworkWorkKanbanInfoEntitys = new LinkedList<>();
    private final LinkedList<KanbanPropertyInfoEntity> kanbanPropertyInfoEntitys = new LinkedList<>();
    private final WorkPlanKanbanDefaultOffsetData defaultOffsetData = new WorkPlanKanbanDefaultOffsetData();

    private final static Long RANGE = 20l;

    private Table workKanbanCustomTable;
    private Table separateworkWorkKanbanCustomTable;
    private Table customPropertyTable;
    private WorkPlanScheduleTypeEnum workPlanScheduleType;
    
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
    private Label productionNumberLabel;
    @FXML
    private RestrictedTextField productionNumberTextField;
    @FXML
    private Button orderProcessesSelectButton;
    @FXML
    private ComboBox<KanbanStatusEnum> kanbanStatusCombo;
    @FXML
    private VBox workKanbanInfoPane;
    @FXML
    private VBox separateworkFieldPane;
    @FXML
    private VBox propertyFieldPane;
    @FXML
    private VBox detailPane;
    @FXML
    private Button registButton;
//    @FXML
//    private Button applyButton;
    @FXML
    private Button schedulingButton;
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
//            applyButton.setDisable(true);
            schedulingButton.setDisable(true);
            orderProcessesSelectButton.setDisable(true);
            kanbanStatusCombo.setDisable(true);// カンバンステータスの変更を無効にする。
        }

        modelNameLabel.setVisible(false);
        modelNameTextField.setVisible(false);
    }

    /**
     * 
     * @param argument 
     */
    @Override
    public void setArgument(Object argument) {
        blockUI(false);
        try {
            if (argument instanceof WorkPlanSelectedKanbanAndHierarchy) {
                WorkPlanSelectedKanbanAndHierarchy selectedKanbanAndHierarchy = (WorkPlanSelectedKanbanAndHierarchy) argument;

                if (loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                    registButton.setDisable(true);
//                    applyButton.setDisable(true);
                    schedulingButton.setDisable(true);
                }

                this.kanban = selectedKanbanAndHierarchy.getKanbanInfo();
                this.workPlanScheduleType = selectedKanbanAndHierarchy.getWorkPlanScheduleType();
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
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.OrderProcesses"), "WorkflowSingleSelectionCompo", selectDialogEntity);
                if (ret.equals(ButtonType.OK) && !selectDialogEntity.getWorkflows().isEmpty()) {
                    workflow = selectDialogEntity.getWorkflows().get(0);

                    if (StringUtils.isEmpty(workflow.getWorkflowDiaglam())) {
                        DialogBox.warn(Locale.ALERT_NO_WORK, Locale.ALERT_WORKFLOW_ERROR_DETAILS);
                        return;
                    }

                    workflowNameTextField.setText(workflow.getWorkflowName()
                            + " : " + workflow.getWorkflowRev().toString());
                    this.kanban.setKanbanName(kanbanNameTextField.getText());
                    this.kanban.setWorkflowName(workflow.getWorkflowName());
                    this.kanban.setFkWorkflowId(workflow.getWorkflowId());
                    this.kanban.setFkUpdatePersonId(loginUser.getId());
                    this.kanban.setUpdateDatetime(new Date());
                    this.kanban.setProductionType(0);

                    // モデル名
                    this.kanban.setModelName(workflow.getModelName());
                    this.modelNameTextField.setText(this.kanban.getModelName());

                    this.productionNumberTextField.setText(this.kanban.getProductionNumber());// 製造番号

                    showWorkKanbanStarttimeOffsetDialog();
                    ResponseEntity responseEntity;

                    if (this.defaultOffsetData.getCheckLotProduction()) {
                        KanbanCreateCondition condition = new KanbanCreateCondition(this.kanban.getKanbanName(), this.kanban.getFkWorkflowId(), this.kanban.getParentId(),
                                loginUser.getLoginId(), this.defaultOffsetData.getCheckOnePieceFlow(), this.defaultOffsetData.getLotQuantity(),
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
//                        applyButton.setDisable(false);
                        schedulingButton.setDisable(false);
                    }

                    runCreateWorkKanbanThread(responseEntity.getUri(), true);
                }
            } else {
                Platform.runLater(() -> {
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.EditKanbanTitle"), String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.KanbanName")));
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
            if (Objects.isNull(workPlanScheduleType) || workPlanScheduleType.equals(WorkPlanScheduleTypeEnum.KANBAN_WP_SCHEDULE)) {
                sc.setComponent("ContentNaviPane", "WorkPlanChartCompo", workPlanScheduleType);
            } else if (workPlanScheduleType.equals(WorkPlanScheduleTypeEnum.ORGANIZATION_WP_SCHEDULE)) {
                sc.setComponent("ContentNaviPane", "WorkerCompo", workPlanScheduleType);
            }
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
     * 工程時間スケジューリング
     *
     * @param event
     */
    @FXML
    private void onScheduling(ActionEvent event) {
        logger.info("onScheduling:Start");
        schedulingKanban();
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
        Task task = new Task<ResponseEntity>() {
            @Override
            protected ResponseEntity call() throws Exception {
                try {
                    logger.info("registThread:Start");

                    //保存前処理
                    KanbanRegistPreprocessContainer plugin = KanbanRegistPreprocessContainer.getInstance();
                    if (Objects.nonNull(plugin)) {
                        KanbanRegistPreprocessResultEntity resultEntity = plugin.kanbanRegistPreprocess(kanban);
                        if (!resultEntity.getResult()) {
                            Platform.runLater(() -> {
                                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanbanTitle"), LocaleUtils.getString(resultEntity.getResultMessage()));
                            });
                            return null;
                        }
                    }

                    // 現在のカンバンステータスを保持し、計画中にする。
                    KanbanStatusEnum status = kanban.getKanbanStatus();
                    kanban.setKanbanStatus(KanbanStatusEnum.PLANNING);
                    ResponseEntity responseEntity = kanbanFacade.update(kanban);
                    if( !ResponseAnalyzer.getAnalyzeResult(responseEntity) ){
                        if (responseEntity.getErrorType().equals(ServerErrorTypeEnum.SERVER_FETAL)) {
                                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanbanTitle"),
                                        String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.Kanban") + LocaleUtils.getString("key.Status")));
                            return null;
                        }
                    }

                    //保存処理実行
                    responseEntity = kanbanFacade.update(kanban);
                    if (!ResponseAnalyzer.getAnalyzeResult(responseEntity)) {
                        if (responseEntity.getErrorType().equals(ServerErrorTypeEnum.SERVER_FETAL)) {
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanbanTitle"),
                                    String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.Kanban") + LocaleUtils.getString("key.Status")));
                        }
                    }
                    
                    // ステータスを戻す
                    kanban.setKanbanStatus(status);
                    responseEntity = kanbanFacade.update(kanban);
                    if( ResponseAnalyzer.getAnalyzeResult(responseEntity) ){
                        if (isTrans) {
                            //非update時つまり新規作成時には元の画面に戻す
                            Platform.runLater(() -> {
                                if (Objects.isNull(workPlanScheduleType) || workPlanScheduleType.equals(WorkPlanScheduleTypeEnum.KANBAN_WP_SCHEDULE)) {
                                    sc.setComponent("ContentNaviPane", "WorkPlanChartCompo", workPlanScheduleType);
                                } else if (workPlanScheduleType.equals(WorkPlanScheduleTypeEnum.ORGANIZATION_WP_SCHEDULE)) {
                                    sc.setComponent("ContentNaviPane", "WorkerCompo", workPlanScheduleType);
                                }
                            });
                        }
                    }else{
                        if (responseEntity.getErrorType().equals(ServerErrorTypeEnum.SERVER_FETAL)) {
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanbanTitle"),
                                    String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.Kanban") + LocaleUtils.getString("key.Status")));
                            return null;
                        }
                    }
                    
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    logger.info("registThread:End");
                    Platform.runLater(() -> {
                        blockUI(false);
                        //更新時カンバンステータスが変更可能なものになったら有効にする
                        settingKanbanEditDisable(kanban.getKanbanStatus());
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 
     */
    private void schedulingKanban() {
        blockUI(true);
        try {
            logger.info("schedulingKanban:Start");
            //未入力判定
            if (!registPreCheck()) {
                return;
            }
            updateKanbanInfo();
            
            this.automaticScheduling = new AutomaticScheduling(kanban);
            this.automaticScheduling.schedulingKanban();
            updateKanbanInfo();
            
            //表示されたもののクローンを作成　登録ボタンで画面遷移が発生するため
            cloneKanban = this.kanban.clone();
            
            registThread(true);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            Platform.runLater(() -> blockUI(false));
        } finally {
            logger.info("registKanban:End");
        }
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
                setText(LocaleUtils.getString(item.getResourceKey()));
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
                workflowNameTextField.setText(this.kanban.getWorkflowName()
                        + " : " + this.kanban.getWorkflowRev().toString());
                modelNameLabel.setVisible(true);
                modelNameTextField.setVisible(true);
                modelNameTextField.setText(this.kanban.getModelName());
                productionNumberTextField.setText(this.kanban.getProductionNumber());// 製造番号

                createWorkKanbanTable();
                createSeparateWorkTable();
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
        // データ取得時に、開始時間順にソートされているため
        //workKanbanInfoEntitys.sort(Comparator.comparing(this.kanban -> this.kanban.getWorkKanbanOrder()));

        WorkKanbanTimeReplaceData timeReplaceData = new WorkKanbanTimeReplaceData();
        timeReplaceData.workKanbanInfoEntitys(workKanbanInfoEntitys).referenceStartTime(defaultOffsetData.getStartOffsetTime())
                .breakTimeInfoEntitys(WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(workKanbanInfoEntitys))
                .dateFormat(LocaleUtils.getString("key.DateTimeFormat")).kanbanDefaultOffsetData(defaultOffsetData);

        workKanbanCustomTable = new Table(workKanbanInfoPane.getChildren())
                .isSelectCheckRecord(true)
                .isColumnTitleRecord(true)
                .isChangeDataRowColor(true)
                .title(LocaleUtils.getString("key.OrderProcesses"))
                .customFooterItem(createEditButton())
                .styleClass("ContentTitleLabel");
        workKanbanCustomTable.setAbstractRecordFactory(new WorkPlanWorkKanbanRecordFactory(workKanbanCustomTable, workKanbanInfoEntitys, this.kanban, this.workflowProcess));
    }

    /**
     * 追加工程編集テーブル作成
     *
     */
    private void createSeparateWorkTable() {
        if (!Objects.nonNull(this.kanban.getSeparateworkKanbanCollection())) {
            this.kanban.setSeparateworkKanbanCollection(new ArrayList<>());
        }
        separateworkFieldPane.getChildren().clear();
        separateworkWorkKanbanInfoEntitys.clear();
        separateworkWorkKanbanInfoEntitys.addAll(this.kanban.getSeparateworkKanbanCollection());
        // データ取得時に、開始時間順にソートされているため
        //separateworkWorkKanbanInfoEntitys.sort(Comparator.comparing(separatework -> separatework.getWorkKanbanOrder()));

        separateworkWorkKanbanCustomTable = new Table(separateworkFieldPane.getChildren()).isSelectCheckRecord(true).isColumnTitleRecord(true)
                .isChangeDataRowColor(true)
                .title(LocaleUtils.getString("key.AdditionalProcess")).customFooterItem(createSeparateWorkKanbanDialogButton()).styleClass("ContentTitleLabel");
        separateworkWorkKanbanCustomTable.setAbstractRecordFactory(new WorkPlanWorkKanbanRecordFactory(separateworkWorkKanbanCustomTable, separateworkWorkKanbanInfoEntitys, this.kanban, this.workflowProcess));
    }

    /**
     * プロパティ編集テーブル作成
     *
     */
    private void createKanbanPropertyTable() {
        if (!Objects.nonNull(this.kanban.getPropertyCollection())) {
            this.kanban.setPropertyCollection(new ArrayList<>());
        }
        propertyFieldPane.getChildren().clear();
        kanbanPropertyInfoEntitys.clear();
        kanbanPropertyInfoEntitys.addAll(this.kanban.getPropertyCollection());
        kanbanPropertyInfoEntitys.sort((Comparator.comparing(e -> e.getKanbanPropertyOrder())));

        customPropertyTable = new Table(propertyFieldPane.getChildren()).isAddRecord(true)
                .isColumnTitleRecord(true).title(LocaleUtils.getString("key.CustomField")).styleClass("ContentTitleLabel");
        customPropertyTable.setAbstractRecordFactory(new WorkPlanKanbanPropertyRecordFactory(customPropertyTable, kanbanPropertyInfoEntitys));
    }

    /**
     * 工程カンバン編集ボタンを生成する。
     *
     * @return
     */
    private List<Node> createEditButton() {
        Button button = new Button(LocaleUtils.getString("key.EditCheckProcess"));
        button.getStyleClass().add("ContentButton");

        button.setOnAction(event -> {
            try {
                List<WorkKanbanInfoEntity> workKanabns = workKanbanCustomTable.getCheckedRecordItems();
                this.showWorkKanbanSetting(workKanabns);
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                this.createWorkKanbanTable();
            }
        });

        if (!this.loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            button.setDisable(true);
        }

        return Arrays.asList(button);
    }

    /**
     * 個別編集ボタン作成
     *
     * @return
     */
    private List<Node> createSeparateWorkKanbanDialogButton() {
        List<Node> nodes = new ArrayList<>();

        HBox separateButtonPane = new HBox();
        //ボタン作成
        Button showEditSeparateDialog = new Button(LocaleUtils.getString("key.EditCheckProcess"));
        showEditSeparateDialog.getStyleClass().add("ContentButton");
        Button showAddWorkDialog = new Button(LocaleUtils.getString("key.AddAdditionalWork"));
        showAddWorkDialog.getStyleClass().add("ContentButton");
        Button showDeleteSeparateDialog = new Button(LocaleUtils.getString("key.DeleteAdditionalWork"));
        showDeleteSeparateDialog.getStyleClass().add("ContentButton");
        separateButtonPane.getChildren().addAll(showAddWorkDialog, showDeleteSeparateDialog, showEditSeparateDialog);
        separateButtonPane.getStyleClass().add("ContentHBox");
        nodes.add(separateButtonPane);
        //役割の権限によるボタン無効化.
        if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            showEditSeparateDialog.setDisable(true);
            showAddWorkDialog.setDisable(true);
            showDeleteSeparateDialog.setDisable(true);
        }

        showEditSeparateDialog.setOnAction((ActionEvent actionEvent) -> {
            try {
                List<WorkKanbanInfoEntity> editEntitys = separateworkWorkKanbanCustomTable.getCheckedRecordItems();
                showWorkKanbanSetting(editEntitys);
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                createSeparateWorkTable();
            }
        });
        showAddWorkDialog.setOnAction((ActionEvent actionEvent) -> {
            try {
                SelectDialogEntity selectDialogEntity = new SelectDialogEntity();
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkSingleSelectionCompo", selectDialogEntity);
                if (ret.equals(ButtonType.OK)
                        && Objects.nonNull(selectDialogEntity.getWorks())
                        && !selectDialogEntity.getWorks().isEmpty()) {
                    List<WorkInfoEntity> works = selectDialogEntity.getWorks();
                    WorkInfoEntity work = works.get(0);

                    work = (new WorkInfoFacade()).find(work.getWorkId()); // 工程選択ダイアログでは追加情報が取得できないため改めて取得
                    WorkKanbanInfoEntity workKanbanInfoEntity = WorkKanbanInfoEntity.convertWorkToWorkKanban(work);
                    workKanbanInfoEntity.setFkKanbanId(this.kanban.getKanbanId());
                    workKanbanInfoEntity.setFkWorkflowId(WorkPlanConstants.SEPARATE_WORKFLOW_ID);
                    workKanbanInfoEntity.setImplementFlag(false);
                    workKanbanInfoEntity.setSumTimes(0L);
                    if (Objects.nonNull(work.getPropertyInfoCollection())) {
                        List<WorkKanbanPropertyInfoEntity> propertyInfoEntitys = new ArrayList<>();
                        for (WorkPropertyInfoEntity property : filterPropertyInfo(work)) {
                            propertyInfoEntitys.add(new WorkKanbanPropertyInfoEntity(null, null, property.getWorkPropName(), property.getWorkPropType(), property.getWorkPropValue(), property.getWorkPropOrder()));
                        }
                        workKanbanInfoEntity.setPropertyCollection(propertyInfoEntitys);
                    } else {
                        workKanbanInfoEntity.setPropertyCollection(new ArrayList<>());
                    }

                    //表示順を設定する
                    //Optional<WorkKanbanInfoEntity> max = this.kanban.getSeparateworkKanbanCollection().stream().max(Comparator.comparing(separatework -> separatework.getWorkKanbanOrder()));
                    //if (max.isPresent()) {
                    //    workKanbanInfoEntity.setWorkKanbanOrder(max.get().getWorkKanbanOrder() + 1);
                    //}
                    this.kanban.getSeparateworkKanbanCollection().add(workKanbanInfoEntity);
                    createSeparateWorkTable();
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                createSeparateWorkTable();
            }
        });

        //バラ工程の削除
        showDeleteSeparateDialog.setOnAction((ActionEvent event) -> {
            try {
                List<WorkKanbanInfoEntity> editEntitys = separateworkWorkKanbanCustomTable.getCheckedRecordItems();
                if (!editEntitys.isEmpty()) {
                    deleteSeparatework(editEntitys);
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });
        return nodes;
    }

    /**
     * 追加工程の追加情報プロパティのうち追加情報のみ(トレーサビリティ以外)のものを取得する
     *
     * @param entity 取得元となる工程
     * @return
     */
    private List<WorkPropertyInfoEntity> filterPropertyInfo(WorkInfoEntity entity) {
        return entity.getPropertyInfoCollection().stream()
                .filter(info -> Objects.isNull(info.getWorkPropCategory()) || WorkPropertyCategoryEnum.INFO.equals(info.getWorkPropCategory()))
                .collect(toList());
    }

    /**
     * 追加工程削除処理
     *
     * @param selectEntitys
     */
    private void deleteSeparatework(List<WorkKanbanInfoEntity> selectEntitys) {
        final String messgage = selectEntitys.size() > 1
                ? LocaleUtils.getString("key.DeleteMultipleMessage")
                : LocaleUtils.getString("key.DeleteSingleMessage");
        final String content = selectEntitys.size() > 1
                ? null
                : selectEntitys.get(0).getWorkName();
        ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), messgage, content);
        if (!ret.equals(ButtonType.OK)) {
            return;
        }
        //
        List<WorkKanbanInfoEntity> separeteWorks = this.kanban.getSeparateworkKanbanCollection();
        for (WorkKanbanInfoEntity work : selectEntitys) {
            separeteWorks.removeIf(entity -> Objects.equals(work.getFkWorkId(), entity.getFkWorkId()));
        }
        createSeparateWorkTable();
    }

    /**
     * 工程カンバン設定ダイアログを表示する。
     *
     * @param workKanbans
     */
    public void showWorkKanbanSetting(List<WorkKanbanInfoEntity> workKanbans) {
        if (workKanbans.isEmpty()) {
            return;
        }

        WorkKanbanTimeReplaceData timeReplaceData = new WorkKanbanTimeReplaceData();
        timeReplaceData.workKanbanInfoEntitys(workKanbanInfoEntitys).referenceStartTime(defaultOffsetData.getStartOffsetTime())
                .breakTimeInfoEntitys(WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(workKanbanInfoEntitys))
                .dateFormat(LocaleUtils.getString("key.DateTimeFormat")).kanbanDefaultOffsetData(defaultOffsetData);

        if (workKanbans.size() == 1) {
            // 単一選択の場合
            WorkKanbanInfoEntity entity = workKanbans.get(0);
            WorkPlanWorkSettingDialogEntity<WorkKanbanPropertyInfoEntity> workSetting = new WorkPlanWorkSettingDialogEntity(
                    StringTime.convertMillisToStringTime(entity.getTaktTime()), WorkPlanConstants.DEFAULT_OFFSETTIME, entity.getSkipFlag(),
                    (Objects.nonNull(entity.getEquipmentCollection())) ? entity.getEquipmentCollection() : new ArrayList<>(),
                    (Objects.nonNull(entity.getOrganizationCollection())) ? entity.getOrganizationCollection() : new ArrayList<>(),
                    (Objects.nonNull(entity.getPropertyCollection())) ? new LinkedList<>(entity.getPropertyCollection()) : new LinkedList<>());

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkPlanWorkKanbanSettingCompo", workSetting);
            while (ret.equals(ButtonType.OK)) {
                if (WorkPlanKanbanCheckerUtils.checkEmptyWorkKanbanProp(workSetting.getProperties())) {
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                } else {
                    break;
                }
                ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkPlanWorkKanbanSettingCompo", workSetting);
            }

            if (ret.equals(ButtonType.OK)) {
                workSetting.Update();
                this.configWorkKanban(workKanbans, timeReplaceData, workSetting);
                this.createWorkKanbanTable();
                this.createSeparateWorkTable();
            }

        } else {
            // 複数選択の場合
            WorkPlanWorkSettingDialogEntity<WorkKanbanPropertyInfoEntity> workSetting = new WorkPlanWorkSettingDialogEntity();
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkPlanWorkKanbanSettingCompo", workSetting);
            if (ret.equals(ButtonType.OK)) {
                workSetting.Update();
                this.configWorkKanban(workKanbans, timeReplaceData, workSetting);
                this.createWorkKanbanTable();
                this.createSeparateWorkTable();
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
                    workflowProcess = new WorkPlanWorkflowProcess(workflow);

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
//                            applyButton.setDisable(false);
                            schedulingButton.setDisable(false);
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
            SimpleDateFormat dateSdf = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
            defaultOffsetData.setStartOffsetTime(new Date());
            defaultOffsetData.setCheckOffsetWorkingHours(false);

            // 始業終業情報の取得
            if ("".equals(properties.getProperty(WorkPlanConstants.OPENING_DATE_TIME, ""))) {
                defaultOffsetData.setOpeningTime(new Date());
            } else {
                defaultOffsetData.setOpeningTime(dateSdf.parse(properties.getProperty(WorkPlanConstants.OPENING_DATE_TIME)));
            }
            if ("".equals(properties.getProperty(WorkPlanConstants.CLOSING_DATE_TIME, ""))) {
                defaultOffsetData.setClosingTime(new Date());
            } else {
                defaultOffsetData.setClosingTime(dateSdf.parse(properties.getProperty(WorkPlanConstants.CLOSING_DATE_TIME)));
            }
            if ("".equals(properties.getProperty(WorkPlanConstants.LOT_QUANTITY, ""))) {
                defaultOffsetData.setLotQuantity(WorkPlanConstants.DEFAULT_LOT_QUANTITY);
            } else {
                defaultOffsetData.setLotQuantity(Integer.valueOf(properties.getProperty(WorkPlanConstants.LOT_QUANTITY)));
            }

            // カンバン作成ダイアログ
            if (ButtonType.OK != sc.showDialog(LocaleUtils.getString("key.MakedKanban"), "WorkPlanWorkKanbanStarttimeOffsetCompo", defaultOffsetData)) {
                return;
            }

            for (WorkPlanWorkGroupPropertyData group : defaultOffsetData.getWorkGroupProps()) {
                group.update();
                defaultOffsetData.getWorkGroups().add(new WorkGroup(group.getQauntity(), group.getStartTime()));
            }

            // 始業終業情報の更新
            if (Objects.nonNull(defaultOffsetData.getOpeningTime())) {
                properties.setProperty(WorkPlanConstants.OPENING_DATE_TIME, dateSdf.format(defaultOffsetData.getOpeningTime()));
            } else {
                properties.setProperty(WorkPlanConstants.OPENING_DATE_TIME, "");
            }
            if (Objects.nonNull(defaultOffsetData.getClosingTime())) {
                properties.setProperty(WorkPlanConstants.CLOSING_DATE_TIME, dateSdf.format(defaultOffsetData.getClosingTime()));
            } else {
                properties.setProperty(WorkPlanConstants.CLOSING_DATE_TIME, "");
            }
            if (Objects.nonNull(defaultOffsetData.getLotQuantity())) {
                properties.setProperty(WorkPlanConstants.LOT_QUANTITY, String.valueOf(defaultOffsetData.getLotQuantity()));
            } else {
                properties.setProperty(WorkPlanConstants.LOT_QUANTITY, "");
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
        if (WorkPlanKanbanCheckerUtils.checkEmptyKanban(kanbanNameTextField.getText(), kanbanPropertyInfoEntitys)
                || Objects.isNull(this.kanban.getWorkKanbanCollection())
                || Objects.isNull(this.kanban.getSeparateworkKanbanCollection())) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
            Platform.runLater(() -> {
                blockUI(false);
            });
            return false;
        }
        switch (WorkPlanKanbanCheckerUtils.validItems(this.kanban)) {
            case TIME_COMP_ERR:
                Platform.runLater(() -> {
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.TimeFormatErrMessage"));
                    blockUI(false);
                });
                return false;
            case DATE_COMP_ERR:
                Platform.runLater(() -> {
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DateCompErrMessage"));
                    blockUI(false);
                });
                return false;
            case SUCCSESS:
                logger.info("Clear KanbanData Validation.");
                break;
        }

        // 追加工程の型チェック
        if (Objects.nonNull(this.kanban.getPropertyCollection())) {
            for (KanbanPropertyInfoEntity entity : this.kanban.getPropertyCollection()) {
                // 値が空欄の場合はチェックしない。
                if (adtekfuji.utility.StringUtils.isEmpty(entity.getKanbanPropertyValue())) {
                    continue;
                }

                // 型に応じてチェックを行なう。
                switch (entity.getKanbanPropertyType()) {
                    case TYPE_INTEGER:
                        if (!StringUtils.isInteger(entity.getKanbanPropertyValue())) {
                            Platform.runLater(() -> {
                                // 追加情報の値が項目のタイプと異なります
                                String message = new StringBuilder(LocaleUtils.getString("key.alert.differentTypeValue"))
                                        .append(" (").append(entity.getKanbanPropertyName()).append(")")
                                        .toString();
                                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message);
                                blockUI(false);
                            });
                            return false;
                        }
                        break;
                    case TYPE_DATE:
                        if (!StringUtils.isDate(entity.getKanbanPropertyValue(), "uuuu/M/d", true)) {
                            Platform.runLater(() -> {
                                // 追加情報の値が項目のタイプと異なります
                                String message = new StringBuilder(LocaleUtils.getString("key.alert.differentTypeValue"))
                                        .append(" (").append(entity.getKanbanPropertyName()).append(")")
                                        .toString();
                                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), message);
                                blockUI(false);
                            });
                            return false;
                        }
                        break;
                }
            }
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
//        if (status.equals(KanbanStatusEnum.PLANNED)
//                || status.equals(KanbanStatusEnum.WORKING)) {
        // 完了以外編集可能
        if (status.equals(KanbanStatusEnum.COMPLETION)) {
            modelNameLabel.setDisable(true);
            modelNameTextField.setDisable(true);
            productionNumberLabel.setDisable(true);
            productionNumberTextField.setDisable(true);
            detailPane.setDisable(true);
        } else {
            modelNameLabel.setDisable(false);
            modelNameTextField.setDisable(false);
            productionNumberLabel.setDisable(false);
            productionNumberTextField.setDisable(false);
            detailPane.setDisable(false);
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
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

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
        this.kanban.setProductionNumber(this.productionNumberTextField.getText());// 製造番号

        //カンバンプロパティ更新
        this.kanban.getPropertyCollection().clear();
        this.kanban.setKanbanStatus(kanbanStatusCombo.getSelectionModel().getSelectedItem());
        this.kanban.setFkUpdatePersonId(loginUser.getId());
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

        this.kanban.setStartDatetime(WorkPlanKanbanStartCompDate.getWorkKanbanStartDateTime(workKanbans));
        this.kanban.setCompDatetime(WorkPlanKanbanStartCompDate.getWorkKanbanCompDateTime(workKanbans));

        this.kanban.updateMember();
    }

    /**
     *
     * @param workKanbans
     * @param data
     * @param workSetting
     */
    private void configWorkKanban(List<WorkKanbanInfoEntity> workKanbans, WorkKanbanTimeReplaceData data, WorkPlanWorkSettingDialogEntity<WorkKanbanPropertyInfoEntity> workSetting) {

        try {
            boolean isUpdate = false;// 他の工程カンバンの計画に影響する項目を変更したか？

            int selectCount = workKanbans.size();

            // 選択されている工程カンバンの情報を更新する。
            for (WorkKanbanInfoEntity workKanban : workKanbans) {
                // タクトタイム
                if (Objects.nonNull(workSetting.getTaktTime()) && !workSetting.getTaktTime().isEmpty() && !StringUtils.equals(workSetting.getTaktTime(), "00:00:00")) {
                    workKanban.taktTimeProperty().unbind();
                    workKanban.setTaktTime(Long.valueOf(StringTime.convertStringTimeToMillis(workSetting.getTaktTime())).intValue());

                    // 1件選択で変更あり、または複数選択
                    if ((selectCount == 1 && workSetting.isUpdateTaktTime())
                            || selectCount > 1) {
                        isUpdate = true;
                    }
                }

                // 組織
                if (!workSetting.getOrganizations().isEmpty()) {
                    List<Long> list = new ArrayList<>();
                    workSetting.getOrganizations().stream().forEach(e -> list.add(e.getOrganizationId()));
                    workKanban.setOrganizationCollection(list);

                    // 1件選択で変更あり、または複数選択
                    if ((selectCount == 1 && workSetting.isUpdateOrganization())
                            || selectCount > 1) {
                        isUpdate = true;
                    }
                }

                // 設備
                if (!workSetting.getEquipments().isEmpty()) {
                    List<Long> list = new ArrayList<>();
                    workSetting.getEquipments().stream().forEach(e -> list.add(e.getEquipmentId()));
                    workKanban.setEquipmentCollection(list);
                }

                // スキップ
                if (Objects.nonNull(workSetting.getSkip())) {
                    workKanban.setSkipFlag(workSetting.getSkip());

                    // 1件選択で変更あり、または複数選択
                    if ((selectCount == 1 && workSetting.isUpdateSkip())
                            || selectCount > 1) {
                        isUpdate = true;
                    }
                }
            }

            // 1件選択時のみプロパティを更新する。
            if (workKanbans.size() == 1) {
                WorkKanbanInfoEntity workKanban = workKanbans.get(0);
                // カスタムフィールド
                int order = 0;
                if (Objects.nonNull(workKanban.getWorkKanbanId())) {
                    for (WorkKanbanPropertyInfoEntity property : workSetting.getProperties()) {
                        property.setFkMasterId(workKanban.getWorkKanbanId());
                        property.updateMember();
                        property.setWorkKanbanPropOrder(order);
                        order = order + 1;
                    }
                } else {
                    for (WorkKanbanPropertyInfoEntity e : workSetting.getProperties()) {
                        e.updateMember();
                        e.setWorkKanbanPropOrder(order);
                        order = order + 1;
                    }
                }
                workKanban.setPropertyCollection(workSetting.getProperties());
            }

            boolean isAll = false;
            if (workKanbans.size() == this.kanban.getWorkKanbanCollection().size()) {
                isAll = true;
            }

            // 全ての工程カンバンを選択しているか、
            // 他の工程カンバンの計画に影響する項目を変更した場合、カンバン全体の計画日時を更新する。
            if (isAll || isUpdate) {
                Date baseDatetime;
                if (isAll) {
                    if (workSetting.isStartTimeOffset()) {
                        // 基本開始時間変更
                        if (StringUtils.isEmpty(workSetting.getOffsetTime())) {
                            baseDatetime = this.kanban.getStartDatetime();
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            baseDatetime = sdf.parse(workSetting.getOffsetTime());
                        }
                    } else {
                        // オフセット
                        baseDatetime = StringTime.getFixedDate(this.kanban.getStartDatetime(), workSetting.getOffsetTime());
                    }
                } else {
                    baseDatetime = this.kanban.getStartDatetime();
                }

                // 休憩時間
                List<BreakTimeInfoEntity> breakTimes = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(this.kanban.getWorkKanbanCollection());
                // 休日
                List<HolidayInfoEntity> holidays = WorkKanbanTimeReplaceUtils.getHolidays(baseDatetime);

                // カンバンの基準時間を更新
                this.workflowProcess.setBaseTime(this.kanban, breakTimes, baseDatetime, holidays);
            }

            // 全選択でない場合、選択した工程カンバンの計画日時を更新する。
            if (!isAll) {
                if (!StringUtils.isEmpty(workSetting.getOffsetTime()) && !StringUtils.equals(workSetting.getOffsetTime(), "00:00:00")) {
                    // 選択した工程カンバンを開始日時順にソートする。
                    Collections.sort(workKanbans, new WorkKanbanStartDateComparator());

                    WorkKanbanInfoEntity workKanban = workKanbans.get(0);
                    Date baseDatetime = workKanban.getStartDatetime();

                    // 休憩時間
                    List<BreakTimeInfoEntity> breakTimes = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(this.kanban.getWorkKanbanCollection());

                    String offsetTime;
                    if (workSetting.isStartTimeOffset()) {
                        // 基本開始時間
                        Calendar startDate = new Calendar.Builder().setInstant(baseDatetime).build();
                        startDate.set(Calendar.MILLISECOND, 0);
                        long differenceTime = StringTime.convertStringToDate(workSetting.getOffsetTime(), data.getDateFormat()).getTime() - startDate.getTimeInMillis();
                        offsetTime = StringTime.convertMillisToStringTime(differenceTime);
                    } else {
                        // オフセット
                        offsetTime = workSetting.getOffsetTime();
                    }

                    // 休日 (オフセット後の開始日時以降)
                    Date targetDate = StringTime.getFixedDate(baseDatetime, offsetTime);
                    List<HolidayInfoEntity> holidays = WorkKanbanTimeReplaceUtils.getHolidays(targetDate);

                    Optional<ConWorkflowWorkInfoEntity> opt = this.workflow.getConWorkflowWorkInfoCollection().stream().
                            filter(p -> p.getFkWorkId().equals(workKanban.getFkWorkId())).findFirst();

                    if (opt.isPresent()) {
                        // 標準作業日が設定されている場合、予め計画開始時間に反映して、帳尻を合わせる。
                        ConWorkflowWorkInfoEntity workflowWork = opt.get();
                        long days = (workflowWork.getStandardStartTime().getTime() - DateUtils.min().getTime()) / WorkPlanWorkflowProcess.DAY_MILLIS;
                        logger.info("Plan start time: targetDate={}, days={}", targetDate, days);
                        targetDate = new Date(targetDate.getTime() - days * WorkPlanWorkflowProcess.DAY_MILLIS);
                    }

                    // 工程カンバンの計画をオフセット時間分ずらす。
                    //this.workflowProcess.offsetWorkKanbans(workKanbans, workSetting.isStartTimeOffset(), offsetTime, breakTimes, holidays);
                    this.workflowProcess.setBaseTime(this.kanban, breakTimes, targetDate, holidays, workKanban);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * カンバンステータス変更のリスナー
     *
     * @author HN)y-harada
     */
    private final ChangeListener<KanbanStatusEnum> changeStatusListener = (ObservableValue<? extends KanbanStatusEnum> observable, KanbanStatusEnum oldValue, KanbanStatusEnum newValue) -> {
        try {
            if (Objects.nonNull(newValue) && !Objects.equals(oldValue, newValue)) {

                // カンバン編集画面に変更内容がある場合、カンバンステータス変更APIを呼び出す前に「入力内容が保存されていません」を表示する。
                if (this.isChanged(false)) {
                    String title = LocaleUtils.getString("key.confirm");
                    String message = LocaleUtils.getString("key.confirm.change");

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
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.ChangeStatusPlanning");

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
                    String title = LocaleUtils.getString("key.confirm");
                    String message = LocaleUtils.getString("key.ChangeStatusWorking");

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
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ChangeStatus"), String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.Status")));
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
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ChangeStatus"), String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.Status")));
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
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ChangeStatus"), String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.Status")));
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
        List<KanbanStatusEnum> list = new ArrayList<KanbanStatusEnum>();

        list.addAll(Arrays.asList(KanbanStatusEnum.values()));
        
        // OTHER以降のステータスを削除
        int index = list.indexOf(KanbanStatusEnum.OTHER);
        for (int ii = list.size() - 1; ii >= index; ii--) {
            list.remove(ii);
        }
        
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
}
