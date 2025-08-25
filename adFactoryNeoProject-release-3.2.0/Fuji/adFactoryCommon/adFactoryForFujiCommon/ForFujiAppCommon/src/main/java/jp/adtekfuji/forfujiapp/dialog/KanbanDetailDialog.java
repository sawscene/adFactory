/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.dialog;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringTime;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.EntityConstants;
import jp.adtekfuji.forfujiapp.dialog.entity.WorkSettingDialogEntity;
import jp.adtekfuji.forfujiapp.javafx.combobox.factory.KanbanStatusEnumComboBoxCellFactory;
import jp.adtekfuji.forfujiapp.javafx.record.factory.KanbanPropertyRecordFactory;
import jp.adtekfuji.forfujiapp.javafx.record.factory.WorkKanbanRecordFactory;
import jp.adtekfuji.forfujiapp.utils.KanbanCheckerUtils;
import jp.adtekfuji.forfujiapp.utils.KanbanDefaultOffsetData;
import jp.adtekfuji.forfujiapp.utils.WorkKanbanComparator;
import jp.adtekfuji.forfujiapp.utils.WorkKanbanTimeReplaceData;
import jp.adtekfuji.forfujiapp.utils.WorkKanbanTimeReplaceUtils;
import jp.adtekfuji.forfujiapp.utils.WorkflowProcess;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン詳細ダイアログ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.11.Fri
 */
@FxComponent(id = "KanbanDetailDialog", fxmlPath = "/fxml/dialog/kanbanDetailDialog.fxml")
public class KanbanDetailDialog implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private KanbanInfoEntity kanbanInfoEntity;
    private WorkflowProcess workflowProcess;
    private final LinkedList<WorkKanbanInfoEntity> workKanbanInfoEntitys = new LinkedList<>();
    private final KanbanDefaultOffsetData defaultOffsetData = new KanbanDefaultOffsetData();
    private final LinkedList<KanbanPropertyInfoEntity> kanbanPropertyInfoEntitys = new LinkedList<>();

    @FXML
    private AnchorPane base;
    @FXML
    private TextField kanbanNameTextField;
    @FXML
    private TextField workflowNameTextField;
    @FXML
    private Label modelNameLabel;
    @FXML
    private TextField modelNameTextField;
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
    private Pane progressPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // レイアウト設定
        this.base.setPrefSize(sc.getStage().getWidth() - 128, sc.getStage().getHeight() - 128);
    }

    @Override
    public void setArgument(Object argument) {
        blockUI(false);
        try {
            if (argument instanceof KanbanInfoEntity) {
                kanbanInfoEntity = (KanbanInfoEntity) argument;
                runCreateWorkKanbanThread();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ワークカンバン作成用スレッド
     *
     */
    private void runCreateWorkKanbanThread() {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // 工程カンバンは取得済みのためコメント化 s-heya 2017.09.01
                    //kanbanInfoEntity.getWorkKanbanCollection().clear();
                    //kanbanInfoEntity.getSeparateworkKanbanCollection().clear();
                    //kanbanInfoEntity.getWorkKanbanCollection().addAll(RestAPI.getWorkKanbans(kanbanInfoEntity.getKanbanId()));
                    //kanbanInfoEntity.getSeparateworkKanbanCollection().addAll(RestAPI.getSeparateWorkKanbans(kanbanInfoEntity.getKanbanId()));
                    // カンバンのワークフロー取得
                    workflowProcess = new WorkflowProcess(RestAPI.getWorkflow(kanbanInfoEntity.getFkWorkflowId()).getWorkflowDiaglam());
                    Platform.runLater(() -> {
                        updateEditView();
                        workflowNameTextField.setEditable(false);
                        if (kanbanInfoEntity.getKanbanStatus().equals(KanbanStatusEnum.PLANNED)
                                || kanbanInfoEntity.getKanbanStatus().equals(KanbanStatusEnum.WORKING)) {
                            modelNameLabel.setDisable(true);
                            modelNameTextField.setDisable(true);
                            detailPane.setDisable(true);
                        } else {
                            modelNameLabel.setDisable(false);
                            modelNameTextField.setDisable(false);
                            detailPane.setDisable(false);
                        }
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
                kanbanStatusCombo.valueProperty().bindBidirectional(kanbanInfoEntity.kanbanStatusProperty());
                kanbanStatusCombo.setVisible(true);
                kanbanNameTextField.setText(kanbanInfoEntity.getKanbanName());
                kanbanNameTextField.textProperty().bindBidirectional(kanbanInfoEntity.kanbanNameProperty());
                workflowNameTextField.setText(kanbanInfoEntity.getWorkflowName() + " : " + kanbanInfoEntity.getWorkflowRev());
                //workflowNameTextField.textProperty().bindBidirectional(kanbanInfoEntity.workflowNameProperty());
                workflowNameTextField.setDisable(true);
                modelNameLabel.setVisible(true);
                modelNameTextField.setVisible(true);
                modelNameTextField.setText(kanbanInfoEntity.getModelName());
                modelNameTextField.textProperty().bindBidirectional(kanbanInfoEntity.modelNameProperty());
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
        Collections.sort(workKanbanInfoEntitys, new WorkKanbanComparator());

        WorkKanbanTimeReplaceData timeReplaceData = new WorkKanbanTimeReplaceData();
        timeReplaceData.workKanbanInfoEntitys(workKanbanInfoEntitys).referenceStartTime(defaultOffsetData.getStartOffsetTime())
                .breakTimeInfoEntitys(WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(workKanbanInfoEntitys))
                .dateFormat(LocaleUtils.getString("key.DateTimeFormat")).kanbanDefaultOffsetData(defaultOffsetData);

        Table workKanbanCustomTable = new Table(workKanbanInfoPane.getChildren()).isSelectCheckRecord(true).isColumnTitleRecord(true)
                .isChangeDataRowColor(true)
                .title(LocaleUtils.getString("key.OrderProcesses")).styleClass("ContentTitleLabel");
        workKanbanCustomTable.customFooterItem(createWorkKanbanDialogButton(workKanbanCustomTable));
        workKanbanCustomTable.setAbstractRecordFactory(new WorkKanbanRecordFactory(workKanbanCustomTable, workKanbanInfoEntitys, kanbanInfoEntity, this.workflowProcess).timeReplaceData(timeReplaceData));
    }

    /**
     * 工程順番編集ボタン作成
     *
     * @return
     */
    private List<Node> createWorkKanbanDialogButton(Table workKanbanCustomTable) {
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

        return nodes;
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
        LinkedList<WorkKanbanInfoEntity> separateworkWorkKanbanInfoEntitys = new LinkedList<>();
        separateworkWorkKanbanInfoEntitys.clear();
        separateworkWorkKanbanInfoEntitys.addAll(kanbanInfoEntity.getSeparateworkKanbanCollection());
        // データ取得時に、開始時間順にソートされているため
        Collections.sort(separateworkWorkKanbanInfoEntitys, new WorkKanbanComparator());

        Table separateworkWorkKanbanCustomTable = new Table(separateworkFieldPane.getChildren()).isSelectCheckRecord(true).isColumnTitleRecord(true)
                .isChangeDataRowColor(true)
                .title(LocaleUtils.getString("key.AdditionalProcess")).styleClass("ContentTitleLabel");
        separateworkWorkKanbanCustomTable.customFooterItem(createSeparateWorkKanbanDialogButton(separateworkWorkKanbanCustomTable));
        separateworkWorkKanbanCustomTable.setAbstractRecordFactory(new WorkKanbanRecordFactory(separateworkWorkKanbanCustomTable, separateworkWorkKanbanInfoEntitys, kanbanInfoEntity, this.workflowProcess));
    }

    /**
     * 個別編集ボタン作成
     *
     * @return
     */
    private List<Node> createSeparateWorkKanbanDialogButton(Table separateworkWorkKanbanCustomTable) {
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
                SelectDialogEntity selectDialogEntity = new SelectDialogEntity();
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkSingleSelectionCompo", selectDialogEntity);
                if (ret.equals(ButtonType.OK)
                        && Objects.nonNull(selectDialogEntity.getWorks())
                        && !selectDialogEntity.getWorks().isEmpty()) {
                    List<WorkInfoEntity> works = selectDialogEntity.getWorks();
                    WorkInfoEntity work = works.get(0);

                    WorkKanbanInfoEntity workKanbanInfoEntity = WorkKanbanInfoEntity.convertWorkToWorkKanban(work);
                    workKanbanInfoEntity.setFkKanbanId(kanbanInfoEntity.getKanbanId());
                    workKanbanInfoEntity.setFkWorkflowId(EntityConstants.ROOT_ID);
                    workKanbanInfoEntity.setImplementFlag(false);
                    workKanbanInfoEntity.setSumTimes(0L);
                    if (Objects.nonNull(work.getPropertyInfoCollection())) {
                        List<WorkKanbanPropertyInfoEntity> propertyInfoEntitys = new ArrayList<>();
                        for (WorkPropertyInfoEntity property : work.getPropertyInfoCollection()) {
                            propertyInfoEntitys.add(new WorkKanbanPropertyInfoEntity(null, null, property.getWorkPropName(), property.getWorkPropType(), property.getWorkPropValue(), property.getWorkPropOrder()));
                        }
                        workKanbanInfoEntity.setPropertyCollection(propertyInfoEntitys);
                    } else {
                        workKanbanInfoEntity.setPropertyCollection(new ArrayList<>());
                    }

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

        Table customPropertyTable = new Table(propertyFieldPane.getChildren()).isAddRecord(true)
                .isColumnTitleRecord(true).title(LocaleUtils.getString("key.CustomField")).styleClass("ContentTitleLabel");
        customPropertyTable.setAbstractRecordFactory(new KanbanPropertyRecordFactory(customPropertyTable, kanbanPropertyInfoEntitys));

        base.setOnMouseExited((MouseEvent me) -> {
            kanbanInfoEntity.getPropertyCollection().clear();
            int order = 0;
            for (KanbanPropertyInfoEntity entity : kanbanPropertyInfoEntitys) {
                entity.setFkKanbanId(kanbanInfoEntity.getKanbanId());
                entity.updateMember();
                entity.setKanbanPropertyOrder(order);
                kanbanInfoEntity.getPropertyCollection().add(entity);
                order = order + 1;
            }
        });
    }

    /**
     * 追加工程削除処理
     *
     * @param selectEntitys
     */
    private void deleteSeparatework(List<WorkKanbanInfoEntity> selectEntitys) {
        String messgage = selectEntitys.size() > 1
                ? LocaleUtils.getString("key.DeleteMultipleMessage")
                : LocaleUtils.getString("key.DeleteSingleMessage");
        String content = selectEntitys.size() > 1
                ? null
                : selectEntitys.get(0).getWorkName();

        ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), messgage, content);
        if (!ret.equals(ButtonType.OK)) {
            return;
        }
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
                        StringTime.convertMillisToStringTime(entity.getTaktTime()), EntityConstants.DEFAULT_TIME, entity.getSkipFlag(),
                        (Objects.nonNull(entity.getEquipmentCollection())) ? entity.getEquipmentCollection() : new ArrayList<>(),
                        (Objects.nonNull(entity.getOrganizationCollection())) ? entity.getOrganizationCollection() : new ArrayList<>(),
                        (Objects.nonNull(entity.getPropertyCollection())) ? new LinkedList<>(entity.getPropertyCollection()) : new LinkedList<>());

                boolean oldValue = dialogEntity.getSkip();

                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkKanbanSettingDialog", dialogEntity);
                while (ret.equals(ButtonType.OK)) {
                    if (KanbanCheckerUtils.checkEmptyWorkKanbanProp(dialogEntity.getPropertys())) {
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                    } else {
                        break;
                    }
                    ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkKanbanSettingDialog", dialogEntity);
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
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkKanbanSettingDialog", dialogEntity);
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
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        this.base.setDisable(flg);
        this.progressPane.setVisible(flg);
    }
}
