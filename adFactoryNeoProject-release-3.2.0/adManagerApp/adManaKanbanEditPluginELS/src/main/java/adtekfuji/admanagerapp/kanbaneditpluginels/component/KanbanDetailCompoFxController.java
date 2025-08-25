/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.component;

import adtekfuji.admanagerapp.kanbaneditpluginels.common.Constants;
import adtekfuji.admanagerapp.kanbaneditpluginels.common.KanbanPropertyRecordFactory;
import adtekfuji.admanagerapp.kanbaneditpluginels.common.SelectedKanbanAndHierarchy;
import adtekfuji.admanagerapp.kanbaneditpluginels.common.WorkKanbanRecordFactory;
import adtekfuji.admanagerapp.kanbaneditpluginels.common.WorkSettingDialogEntity;
import adtekfuji.admanagerapp.kanbaneditpluginels.utils.KanbanCheckerUtils;
import adtekfuji.admanagerapp.kanbaneditpluginels.utils.KanbanDefaultOffsetData;
import adtekfuji.admanagerapp.kanbaneditpluginels.utils.KanbanStartCompDate;
import adtekfuji.admanagerapp.kanbaneditpluginels.utils.WorkGroupPropertyData;
import adtekfuji.admanagerapp.kanbaneditpluginels.utils.WorkKanbanTimeReplaceData;
import adtekfuji.admanagerapp.kanbaneditpluginels.utils.WorkKanbanTimeReplaceUtils;
import adtekfuji.admanagerapp.kanbaneditpluginels.utils.WorkflowProcess;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringTime;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javax.xml.bind.JAXBException;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanCreateCondition;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.WorkGroup;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.plugin.KanbanRegistPreprocessContainer;
import jp.adtekfuji.adFactory.utility.KanbanRegistPreprocessResultEntity;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン詳細画面クラス
 *
 * @author e.mori
 * @version 1.6.1
 * @since 2017.1.11.Wen
 */
@FxComponent(id = "KanbanDetailCompoELS", fxmlPath = "/fxml/admanakanbaneditpluginels/kanban_detail_compo.fxml")
public class KanbanDetailCompoFxController implements Initializable, ArgumentDelivery {

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle RB = LocaleUtils.getBundle("locale.locale");
    private final static LoginUserInfoEntity LOGIN_USER = LoginUserInfoEntity.getInstance();
    private final static KanbanInfoFacade KANBAN_FACADE = new KanbanInfoFacade();

    private KanbanInfoEntity kanbanInfoEntity;
    private WorkflowInfoEntity workflowInfoEntity;
    private WorkflowProcess workflowProcess;
    private final LinkedList<WorkKanbanInfoEntity> workKanbanInfoEntitys = new LinkedList<>();
    private final LinkedList<WorkKanbanInfoEntity> separateworkWorkKanbanInfoEntitys = new LinkedList<>();
    private final LinkedList<KanbanPropertyInfoEntity> kanbanPropertyInfoEntitys = new LinkedList<>();
    private final KanbanDefaultOffsetData defaultOffsetData = new KanbanDefaultOffsetData();

    private final static Long RANGE = 20l;

    private Table workKanbanCustomTable;
    private Table separateworkWorkKanbanCustomTable;
    private Table customPropertyTable;

    @FXML
    private TextField kanbanNameTextField;
    @FXML
    private TextField workflowNameTextField;
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
    @FXML
    private Pane progressPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //役割の権限によるボタン無効化.
        if (!LOGIN_USER.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            registButton.setDisable(true);
            orderProcessesSelectButton.setDisable(true);
        }
    }

    @Override
    public void setArgument(Object argument) {
        blockUI(false);
        try {
            if (argument instanceof SelectedKanbanAndHierarchy) {
                SelectedKanbanAndHierarchy selectedKanbanAndHierarchy = (SelectedKanbanAndHierarchy) argument;

                if (LOGIN_USER.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                    registButton.setDisable(true);
                }

                kanbanInfoEntity = selectedKanbanAndHierarchy.getKanbanInfo();
                if (Objects.nonNull(kanbanInfoEntity)) {
                    // 編集の場合
                    runCreateWorkKanbanThread(String.format("kanban/%s", kanbanInfoEntity.getKanbanId().toString()), false);
                } else {
                    // 新規作成の場合
                    kanbanInfoEntity = new KanbanInfoEntity();
                    kanbanInfoEntity.setParentId(selectedKanbanAndHierarchy.getHierarchyId());
                    kanbanInfoEntity.setPropertyCollection(new ArrayList<>());
                    kanbanInfoEntity.setKanbanStatus(KanbanStatusEnum.PLANNING);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    @FXML
    private void onSelectWorkflow(ActionEvent event) {
        try {
            if (!kanbanNameTextField.getText().isEmpty()) {
                SelectDialogEntity<WorkflowInfoEntity> selectDialogEntity = new SelectDialogEntity();
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.OrderProcesses"), "WorkflowSingleSelectionCompo", selectDialogEntity);
                if (ret.equals(ButtonType.OK) && !selectDialogEntity.getWorkflows().isEmpty()) {
                    workflowInfoEntity = selectDialogEntity.getWorkflows().get(0);

                    workflowNameTextField.setText(workflowInfoEntity.getWorkflowName());
                    kanbanInfoEntity.setKanbanName(kanbanNameTextField.getText());
                    kanbanInfoEntity.setWorkflowName(workflowInfoEntity.getWorkflowName());
                    kanbanInfoEntity.setFkWorkflowId(workflowInfoEntity.getWorkflowId());
                    kanbanInfoEntity.setFkUpdatePersonId(LOGIN_USER.getId());
                    kanbanInfoEntity.setUpdateDatetime(new Date());

                    showWorkKanbanStarttimeOffsetDialog();
                    ResponseEntity responseEntity;

                    if (defaultOffsetData.getCheckLotProduction()) {
                        KanbanCreateCondition condition = new KanbanCreateCondition(
                                kanbanInfoEntity.getKanbanName(), kanbanInfoEntity.getFkWorkflowId(), kanbanInfoEntity.getParentId(),
                                LOGIN_USER.getName(), defaultOffsetData.getCheckOnePieceFlow(), defaultOffsetData.getLotQuantity(),
                                defaultOffsetData.getStartOffsetTime(), new ArrayList<>(defaultOffsetData.getWorkGroups()));
                        responseEntity = KANBAN_FACADE.createConditon(condition);
                    } else {
                        responseEntity = KANBAN_FACADE.regist(kanbanInfoEntity);
                    }

                    if (!ResponseAnalyzer.getAnalyzeResult(responseEntity)) {
                        return;
                    }

                    if (LOGIN_USER.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                        registButton.setDisable(false);
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

    @FXML
    private void onCancel(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "KanbanListCompoELS");
    }

    @FXML
    private void onRegist(ActionEvent event) {
        blockUI(true);

        try {
            //未入力判定
            if (!registPreCheck()) {
                return;
            }

            //カンバン名の更新
            kanbanInfoEntity.setKanbanName(kanbanNameTextField.getText());

            //カンバンプロパティ更新
            kanbanInfoEntity.getPropertyCollection().clear();
            kanbanInfoEntity.setKanbanStatus(kanbanStatusCombo.getSelectionModel().getSelectedItem());
            kanbanInfoEntity.setFkUpdatePersonId(LOGIN_USER.getId());
            int order = 0;
            for (KanbanPropertyInfoEntity entity : kanbanPropertyInfoEntitys) {
                entity.setFkKanbanId(kanbanInfoEntity.getKanbanId());
                entity.updateMember();
                entity.setKanbanPropertyOrder(order);
                kanbanInfoEntity.getPropertyCollection().add(entity);
                order = order + 1;
            }
            //工程順更新
            for (WorkKanbanInfoEntity entity : kanbanInfoEntity.getWorkKanbanCollection()) {
                entity.updateMember();
                for (WorkKanbanPropertyInfoEntity proEntity : entity.getPropertyCollection()) {
                    proEntity.updateMember();
                }
            }
            //追加工程更新
            for (WorkKanbanInfoEntity entity : kanbanInfoEntity.getSeparateworkKanbanCollection()) {
                entity.updateMember();
                for (WorkKanbanPropertyInfoEntity proEntity : entity.getPropertyCollection()) {
                    proEntity.updateMember();
                }
            }

            //工程カンバン更新
            List<WorkKanbanInfoEntity> workKanbans = new ArrayList<>();
            workKanbans.addAll(kanbanInfoEntity.getWorkKanbanCollection());
            workKanbans.addAll(kanbanInfoEntity.getSeparateworkKanbanCollection());

            kanbanInfoEntity.setStartDatetime(KanbanStartCompDate.getWorkKanbanStartDateTime(workKanbans));
            kanbanInfoEntity.setCompDatetime(KanbanStartCompDate.getWorkKanbanCompDateTime(workKanbans));

            kanbanInfoEntity.updateMember();

            registThread();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            Platform.runLater(() -> blockUI(false));
        }
    }

    private void registThread() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    //保存前処理
                    KanbanRegistPreprocessContainer plugin = KanbanRegistPreprocessContainer.getInstance();
                    if (Objects.nonNull(plugin)) {
                        KanbanRegistPreprocessResultEntity resultEntity = plugin.kanbanRegistPreprocess(kanbanInfoEntity);
                        if (!resultEntity.getResult()) {
                            Platform.runLater(() -> {
                                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanbanTitle"), LocaleUtils.getString(resultEntity.getResultMessage()));
                            });
                            return null;
                        }
                    }

                    //保存処理実行
                    ResponseEntity responseEntity = KANBAN_FACADE.update(kanbanInfoEntity);
                    if (ResponseAnalyzer.getAnalyzeResult(responseEntity)) {
                        Platform.runLater(() -> {
                            sc.setComponent("ContentNaviPane", "KanbanListCompoELS");
                        });
                    } else //TODO: データ保存失敗のため保存処理中止
                     if (responseEntity.getErrorType().equals(ServerErrorTypeEnum.SERVER_FETAL)) {
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanbanTitle"),
                                    String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.Kanban") + LocaleUtils.getString("key.Status")));
                        }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    Platform.runLater(() -> blockUI(false));
                }
                return null;
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
                kanbanStatusCombo.setItems(FXCollections.observableArrayList(Arrays.asList(KanbanStatusEnum.values())));
                kanbanStatusCombo.setValue(kanbanInfoEntity.getKanbanStatus());
                kanbanStatusCombo.setVisible(true);
                kanbanNameTextField.setText(kanbanInfoEntity.getKanbanName());
                workflowNameTextField.setText(kanbanInfoEntity.getWorkflowName());
                createWorkflowTable();
                createSeparateWorkTable();
                createKanbanPropertyTable();
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程順番編集テーブル作成
     *
     */
    private void createWorkflowTable() {
        if (!Objects.nonNull(kanbanInfoEntity.getWorkKanbanCollection())) {
            kanbanInfoEntity.setWorkKanbanCollection(new ArrayList<>());
        }
        workKanbanInfoPane.getChildren().clear();
        workKanbanInfoEntitys.clear();
        workKanbanInfoEntitys.addAll(kanbanInfoEntity.getWorkKanbanCollection());
        // データ取得時に、開始時間順にソートされているため
        //workKanbanInfoEntitys.sort(Comparator.comparing(kanban -> kanban.getWorkKanbanOrder()));

        WorkKanbanTimeReplaceData timeReplaceData = new WorkKanbanTimeReplaceData();
        timeReplaceData.workKanbanInfoEntitys(workKanbanInfoEntitys).referenceStartTime(defaultOffsetData.getStartOffsetTime())
                .breakTimeInfoEntitys(WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(workKanbanInfoEntitys))
                .dateFormat(LocaleUtils.getString("key.DateTimeFormat")).kanbanDefaultOffsetData(defaultOffsetData);

        workKanbanCustomTable = new Table(workKanbanInfoPane.getChildren()).isSelectCheckRecord(true).isColumnTitleRecord(true)
                .title(LocaleUtils.getString("key.OrderProcesses")).customFooterItem(createWorkKanbanDialogButton()).styleClass("ContentTitleLabel");
        workKanbanCustomTable.setAbstractRecordFactory(new WorkKanbanRecordFactory(workKanbanCustomTable, workKanbanInfoEntitys, kanbanInfoEntity, this.workflowProcess));
    }

    /**
     * 追加工程編集テーブル作成
     *
     */
    private void createSeparateWorkTable() {
        if (!Objects.nonNull(kanbanInfoEntity.getSeparateworkKanbanCollection())) {
            kanbanInfoEntity.setSeparateworkKanbanCollection(new ArrayList<>());
        }
        separateworkFieldPane.getChildren().clear();
        separateworkWorkKanbanInfoEntitys.clear();
        separateworkWorkKanbanInfoEntitys.addAll(kanbanInfoEntity.getSeparateworkKanbanCollection());
        // データ取得時に、開始時間順にソートされているため
        //separateworkWorkKanbanInfoEntitys.sort(Comparator.comparing(separatework -> separatework.getWorkKanbanOrder()));

        separateworkWorkKanbanCustomTable = new Table(separateworkFieldPane.getChildren()).isSelectCheckRecord(true).isColumnTitleRecord(true)
                .title(LocaleUtils.getString("key.AdditionalProcess")).customFooterItem(createSeparateWorkKanbanDialogButton()).styleClass("ContentTitleLabel");
        separateworkWorkKanbanCustomTable.setAbstractRecordFactory(new WorkKanbanRecordFactory(separateworkWorkKanbanCustomTable, separateworkWorkKanbanInfoEntitys, kanbanInfoEntity, this.workflowProcess));
    }

    /**
     * プロパティ編集テーブル作成
     *
     */
    private void createKanbanPropertyTable() {
        if (!Objects.nonNull(kanbanInfoEntity.getPropertyCollection())) {
            kanbanInfoEntity.setPropertyCollection(new ArrayList<>());
        }
        propertyFieldPane.getChildren().clear();
        kanbanPropertyInfoEntitys.clear();
        kanbanPropertyInfoEntitys.addAll(kanbanInfoEntity.getPropertyCollection());
        kanbanPropertyInfoEntitys.sort((Comparator.comparing(kanban -> kanban.getKanbanPropertyOrder())));

        customPropertyTable = new Table(propertyFieldPane.getChildren()).isAddRecord(true)
                .isColumnTitleRecord(true).title(LocaleUtils.getString("key.CustomField")).styleClass("ContentTitleLabel");
        customPropertyTable.setAbstractRecordFactory(new KanbanPropertyRecordFactory(customPropertyTable, kanbanPropertyInfoEntitys));
    }

    /**
     * 工程順番編集ボタン作成
     *
     * @return
     */
    private List<Node> createWorkKanbanDialogButton() {
        List<Node> nodes = new ArrayList<>();
        //ボタン作成
        Button showEditWorkDialog = new Button(LocaleUtils.getString("key.EditCheckProcess"));
        showEditWorkDialog.getStyleClass().add("ContentButton");
        nodes.add(showEditWorkDialog);
        showEditWorkDialog.setOnAction((ActionEvent actionEvent) -> {
            try {
                List<WorkKanbanInfoEntity> editEntitys = workKanbanCustomTable.getCheckedRecordItems();
                showWorkKanbanSettingDialog(editEntitys);
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                createWorkflowTable();
            }
        });
        //役割の権限によるボタン無効化.
        if (!LOGIN_USER.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            showEditWorkDialog.setDisable(true);
        }

        return nodes;
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
        if (!LOGIN_USER.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            showEditSeparateDialog.setDisable(true);
            showAddWorkDialog.setDisable(true);
            showDeleteSeparateDialog.setDisable(true);
        }

        showEditSeparateDialog.setOnAction((ActionEvent actionEvent) -> {
            try {
                List<WorkKanbanInfoEntity> editEntitys = separateworkWorkKanbanCustomTable.getCheckedRecordItems();
                showWorkKanbanSettingDialog(editEntitys);
            } catch (Exception ex) {
                logger.fatal(ex, ex);
                createSeparateWorkTable();
            }
        });
        showAddWorkDialog.setOnAction((ActionEvent actionEvent) -> {
            try {
                Map<Long, List<Map>> test = new HashMap<>();
                WorkInfoEntity entity = new WorkInfoEntity();
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkSelectionCompoELS", entity);
                if (ret.equals(ButtonType.OK) && Objects.nonNull(entity.getWorkId())) {
                    WorkKanbanInfoEntity workKanbanInfoEntity = WorkKanbanInfoEntity.convertWorkToWorkKanban(entity);
                    workKanbanInfoEntity.setFkKanbanId(kanbanInfoEntity.getKanbanId());
                    workKanbanInfoEntity.setFkWorkflowId(Constants.SEPARATE_WORKFLOW_ID);
                    workKanbanInfoEntity.setImplementFlag(false);
                    workKanbanInfoEntity.setSumTimes(0);
                    if (Objects.nonNull(entity.getPropertyInfoCollection())) {
                        List<WorkKanbanPropertyInfoEntity> propertyInfoEntitys = new ArrayList<>();
                        for (WorkPropertyInfoEntity property : entity.getPropertyInfoCollection()) {
                            propertyInfoEntitys.add(new WorkKanbanPropertyInfoEntity(null, null, property.getWorkPropName(), property.getWorkPropType(), property.getWorkPropValue(), property.getWorkPropOrder()));
                        }
                        workKanbanInfoEntity.setPropertyCollection(propertyInfoEntitys);
                    } else {
                        workKanbanInfoEntity.setPropertyCollection(new ArrayList<>());
                    }

                    //表示順を設定する
                    //Optional<WorkKanbanInfoEntity> max = kanbanInfoEntity.getSeparateworkKanbanCollection().stream().max(Comparator.comparing(separatework -> separatework.getWorkKanbanOrder()));
                    //if (max.isPresent()) {
                    //    workKanbanInfoEntity.setWorkKanbanOrder(max.get().getWorkKanbanOrder() + 1);
                    //}
                    kanbanInfoEntity.getSeparateworkKanbanCollection().add(workKanbanInfoEntity);
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
     * 追加工程削除処理
     *
     * @param selectEntitys
     */
    private void deleteSeparatework(List<WorkKanbanInfoEntity> selectEntitys) {
        ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteMessage"));
        if (!ret.equals(ButtonType.OK)) {
            return;
        }
        //
        List<WorkKanbanInfoEntity> separeteWorks = kanbanInfoEntity.getSeparateworkKanbanCollection();
        for (WorkKanbanInfoEntity work : selectEntitys) {
            separeteWorks.remove(work);
        }
        createSeparateWorkTable();
    }

    /**
     * 工程カンバン設定ダイアログを表示する。
     *
     * @param editEntitys
     */
    public void showWorkKanbanSettingDialog(List<WorkKanbanInfoEntity> editEntitys) {
        if (!editEntitys.isEmpty()) {
            //TODO: 単体の編集の場合設定画面に編集対象の情報を表示する
            WorkKanbanTimeReplaceData timeReplaceData = new WorkKanbanTimeReplaceData();
            timeReplaceData.workKanbanInfoEntitys(workKanbanInfoEntitys).referenceStartTime(defaultOffsetData.getStartOffsetTime())
                    .breakTimeInfoEntitys(WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(workKanbanInfoEntitys))
                    .dateFormat(LocaleUtils.getString("key.DateTimeFormat")).kanbanDefaultOffsetData(defaultOffsetData);

            if (editEntitys.size() <= 1) {
                WorkKanbanInfoEntity entity = editEntitys.get(0);
                WorkSettingDialogEntity<WorkKanbanPropertyInfoEntity> dialogEntity = new WorkSettingDialogEntity(
                        StringTime.convertMillisToStringTime(entity.getTaktTime()), Constants.DEFAULT_OFFSETTIME, entity.getSkipFlag(),
                        (Objects.nonNull(entity.getEquipmentCollection())) ? entity.getEquipmentCollection() : new ArrayList<>(),
                        (Objects.nonNull(entity.getOrganizationCollection())) ? entity.getOrganizationCollection() : new ArrayList<>(),
                        (Objects.nonNull(entity.getPropertyCollection())) ? new LinkedList<>(entity.getPropertyCollection()) : new LinkedList<>());

                boolean oldValue = dialogEntity.getSkip();

                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkKanbanSettingCompoELS", dialogEntity);
                while (ret.equals(ButtonType.OK)) {
                    if (KanbanCheckerUtils.checkEmptyWorkKanbanProp(dialogEntity.getPropertys())) {
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                    } else {
                        break;
                    }
                    ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkKanbanSettingCompoELS", dialogEntity);
                }
                if (ret.equals(ButtonType.OK)) {
                    //各工程に設定された工程情報を上書きする
                    WorkKanbanTimeReplaceUtils.batchEditWorkKanban(editEntitys, timeReplaceData, dialogEntity);

                    if (oldValue != dialogEntity.getSkip()) {
                        List<BreakTimeInfoEntity> breakTimes = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanbanInfoEntity.getWorkKanbanCollection());
                        this.workflowProcess.updateTimetable(kanbanInfoEntity, entity, breakTimes);
                    }

                    createWorkflowTable();
                    createSeparateWorkTable();
                }
            } else {
                WorkSettingDialogEntity<WorkKanbanPropertyInfoEntity> dialogEntity = new WorkSettingDialogEntity();
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkKanbanSettingCompoELS", dialogEntity);
                if (ret.equals(ButtonType.OK)) {
                    //各工程に設定された工程情報を上書きする
                    WorkKanbanTimeReplaceUtils.batchEditWorkKanban(editEntitys, timeReplaceData, dialogEntity);

                    if (Objects.nonNull(dialogEntity.getSkip())) {
                        List<BreakTimeInfoEntity> breakTimes = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanbanInfoEntity.getWorkKanbanCollection());
                        this.workflowProcess.updateTimetable(kanbanInfoEntity, editEntitys.get(0), breakTimes);
                    }

                    createWorkflowTable();
                    createSeparateWorkTable();
                }
            }
        }
    }

    /**
     * ワークカンバン作成用スレッド
     *
     */
    private void runCreateWorkKanbanThread(String uri, boolean isNewCreate) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    kanbanInfoEntity = KANBAN_FACADE.findURI(uri);
                    kanbanInfoEntity.getWorkKanbanCollection().clear();
                    kanbanInfoEntity.getSeparateworkKanbanCollection().clear();

                    WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
                    workflowInfoEntity = workflowInfoFacade.find(kanbanInfoEntity.getFkWorkflowId());
                    workflowProcess = new WorkflowProcess(workflowInfoEntity.getWorkflowDiaglam());

                    WorkKanbanInfoFacade workKanbanInfoFacade = new WorkKanbanInfoFacade();

                    Long workkanbanCnt = workKanbanInfoFacade.countFlow(kanbanInfoEntity.getKanbanId());
                    for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                        kanbanInfoEntity.getWorkKanbanCollection().addAll(workKanbanInfoFacade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanbanInfoEntity.getKanbanId()));
                    }

                    Long separateCnt = workKanbanInfoFacade.countSeparate(kanbanInfoEntity.getKanbanId());
                    for (long nowCnt = 0; nowCnt < separateCnt; nowCnt += RANGE) {
                        kanbanInfoEntity.getSeparateworkKanbanCollection().addAll(workKanbanInfoFacade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanbanInfoEntity.getKanbanId()));
                    }

                    Platform.runLater(() -> {
                        if (isNewCreate) {
                            List<BreakTimeInfoEntity> breakTimeInfoEntitys = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanbanInfoEntity.getWorkKanbanCollection());
                            try {
                                if (!defaultOffsetData.getCheckLotProduction()) {
                                    workflowProcess.setBaseTime(kanbanInfoEntity, breakTimeInfoEntitys, defaultOffsetData.getStartOffsetTime());
                                }
                            } catch (JAXBException ex) {
                                logger.fatal(ex, ex);
                            }
                        }
                        updateEditView();
                        if (LOGIN_USER.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                            registButton.setDisable(false);
                        }
                        workflowNameTextField.setEditable(false);
                        orderProcessesSelectButton.setDisable(true);
                        settingKanbanEditDisable(kanbanInfoEntity.getKanbanStatus());
                        blockUI(false);
                    });
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    Platform.runLater(() -> {
                        blockUI(false);
                    });
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
            if (ButtonType.OK != sc.showDialog(LocaleUtils.getString("key.MakedKanban"), "WorkKanbanStarttimeOffsetCompoELS", defaultOffsetData)) {
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
        if (KanbanCheckerUtils.checkEmptyKanban(kanbanNameTextField.getText(), kanbanPropertyInfoEntitys)) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
            Platform.runLater(() -> {
                blockUI(false);
            });
            return false;
        }
        switch (KanbanCheckerUtils.validItems(kanbanInfoEntity)) {
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
        return true;
    }

    /**
     * カンバンステータスに合わせて編集可能か設定する
     *
     * @param status カンバンステータス
     */
    private void settingKanbanEditDisable(KanbanStatusEnum status) {
        //カンバンステータスが"計画済み"または"作業中"の場合、編集不可にする
        if (status.equals(KanbanStatusEnum.PLANNED)
                || status.equals(KanbanStatusEnum.WORKING)) {
            detailPane.setDisable(true);
        } else {
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
}
