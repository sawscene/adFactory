/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.admanagerapp.workfloweditplugin.common.Constants;
import adtekfuji.admanagerapp.workfloweditplugin.common.CustomTreeCell;
import adtekfuji.admanagerapp.workfloweditplugin.common.SelectedWorkflowAndHierarchy;
import adtekfuji.admanagerapp.workfloweditplugin.common.UriConvertUtils;
import adtekfuji.admanagerapp.workfloweditplugin.entity.WorkSettingDialogEntity;
import adtekfuji.admanagerapp.workfloweditplugin.entity.WorkTreeEntity;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.WorkHierarchyInfoFacade;
import adtekfuji.clientservice.WorkInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.TreeCellInterface;
import jp.adtekfuji.javafxcommon.WorkHierarchyTreeEntity;
import jp.adtekfuji.javafxcommon.WorkflowEditPermanenceData;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.workflowmodel.CellBase;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelEndCell;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelStartCell;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkCell;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowCellEvent;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowCellEventListener;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowCellEventNotifier;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowModel;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkflowPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程フロー編集画面
 *
 * @author ta.ito
 */
@FxComponent(id = "WorkflowAssemblyCompo", fxmlPath = "/fxml/compo/workflow_assembly_compo.fxml")
public class WorkflowAssemblyCompoFxController implements Initializable, ArgumentDelivery, ComponentHandler, WorkflowCellEventListener {

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final static WorkHierarchyInfoFacade workHierarchyInfoFacade = new WorkHierarchyInfoFacade();
    private final static WorkInfoFacade workInfoFacade = new WorkInfoFacade();
    private final static WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private final static KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private SelectedWorkflowAndHierarchy selectedWorkflowAndHierarchy = null;
    private final WorkflowEditPermanenceData permanenceData = WorkflowEditPermanenceData.getInstance();
    private WorkHierarchyInfoEntity selectedWorkHierarchy = null;
    private WorkInfoEntity selectedWork = null;
    private final WorkflowModel workflowModel = new WorkflowModel();
    private final static CashManager cache = CashManager.getInstance();
    private final static String LITE_HIERARCHY_TOP_KEY = "LiteHierarchyTop";
    private String liteTreeName;

    private final static long RANGE = 10;
    private final static long ROOT_ID = 0;

    private final List<WorkHierarchyInfoEntity> hierarchyInfos = new LinkedList();

    // 表示されてるセル　比較に用いる
    private WorkflowInfoEntity cloneInitialWorkflow;

    private boolean finalApproveOnly;

    private final ChangeListener<Boolean> changeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                TreeItem<TreeCellInterface> treeItem = (TreeItem<TreeCellInterface>) ((BooleanProperty) observable).getBean();
                blockUI(true);
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            expand(treeItem);
                        } finally {
                            Platform.runLater(() -> blockUI(false));
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        }
    };

    @FXML
    private Button addSeriesButton;
    @FXML
    private Button addParallelButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button applyButton;
    @FXML
    private Button createWorkflowButton;
    @FXML
    private Button deleteWorkButton;
    @FXML
    private Button editWorkButton;
    @FXML
    private Button allCheckButton;
    @FXML
    private Button allUncheckButton;
    @FXML
    private Pane assemblyProgress;
    @FXML
    private TreeView<TreeCellInterface> workTree;
    @FXML
    private Label workflowName;
    @FXML
    private GridPane zoomPane;
    @FXML
    private ScrollPane workflowScrollPane;
    @FXML
    private Slider zoomSlider;
    @FXML
    private ToggleButton visibleOrganizationToggleButton;
    @FXML
    private ToggleButton visibleEquipmentToggleButton;
    @FXML
    private ToggleButton visibleScaleToggleButton;

    private ScrollBar workflowVBar;
    private ScrollBar workflowHBar;

    private enum ScrollDirection {
        NEUTRAL,
        UPPER,
        LOWER,
        LEFT,
        RIGHT
    }

    private ScrollDirection workflowVDirection = ScrollDirection.NEUTRAL;
    private ScrollDirection workflowHDirection = ScrollDirection.NEUTRAL;

    //ワークフローペインのドラッグ時スクロール処理
    private final Timeline workflowScrollTimer = new Timeline(new KeyFrame(Duration.millis(100.0), ActionEvent -> {

        if (Objects.isNull(workflowVBar) || Objects.isNull(workflowHBar)) {
            return;
        }

        switch (workflowVDirection) {
            case UPPER:
                workflowVBar.decrement();
                break;
            case LOWER:
                workflowVBar.increment();
                break;
        }

        switch (workflowHDirection) {
            case LEFT:
                workflowHBar.decrement();
                break;
            case RIGHT:
                workflowHBar.increment();
                break;
        }
    }));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (!this.properties.containsKey(Config.SHIFT_TIME)) {
            this.properties.setProperty(Config.SHIFT_TIME, "true");
        }

        //Lite階層名を取得しておく
        Properties properties = AdProperty.getProperties();
        this.liteTreeName = properties.getProperty(LITE_HIERARCHY_TOP_KEY);

        permanenceData.updateTitle();

        //役割の権限によるボタン無効化.
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
            addSeriesButton.setDisable(true);
            addParallelButton.setDisable(true);
            createWorkflowButton.setDisable(true);
            deleteWorkButton.setDisable(true);
            editWorkButton.setDisable(true);
            allCheckButton.setDisable(true);
            allUncheckButton.setDisable(true);
            applyButton.setDisable(true);
        }

        assemblyProgress.setVisible(false);

        //ツリー選択時処理
        workTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<TreeCellInterface>> observable, TreeItem<TreeCellInterface> oldValue, TreeItem<TreeCellInterface> newValue) -> {
            if (Objects.nonNull(newValue) && newValue.getValue().getHierarchyId() != ROOT_ID
                    && newValue.getValue().getEntity() instanceof WorkInfoEntity) {
                selectedWork = (WorkInfoEntity) newValue.getValue().getEntity();
                selectedWorkHierarchy = (WorkHierarchyInfoEntity) newValue.getParent().getValue().getEntity();
            } else {
                selectedWork = null;
                selectedWorkHierarchy = null;
            }
        });

        boolean isLicensedApproval = ClientServiceProperty.isLicensed(LicenseOptionType.ApprovalOption.getName());
        if (isLicensedApproval) {
            // 承認機能が有効な場合、未承認は非表示。
            this.finalApproveOnly = true;

            // Ctrl + Shift + L or M キーで未承認の表示/非表示を切り替える。
            this.sc.getStage().getScene().setOnKeyPressed((KeyEvent event) -> {
                if (event.isControlDown() && event.isShiftDown()) {
                    if (Objects.equals(event.getCode(), KeyCode.L)) {
                        // 未承認を表示する。
                        logger.info("Pressed CTRL + SHIFT + L Key.");
                        this.finalApproveOnly = false;
                        this.updateTree();
                    } else if (Objects.equals(event.getCode(), KeyCode.M)) {
                        // 未承認を非表示にする。
                        logger.info("Pressed CTRL + SHIFT + M Key.");
                        this.finalApproveOnly = true;
                        this.updateTree();
                    } 
                }
            });
        } else {
            // 承認機能が無効の場合、未承認も表示する。
            this.finalApproveOnly = false;
        }

        //ツリー情報表示
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    createRoot();
                    Platform.runLater(() -> {
                        loadProperties();
                    });
                } finally {
                    Platform.runLater(() -> blockUI(false));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.isControlDown() && ke.getCode().equals(KeyCode.V)) {
            onAddSerial(null);
        }
        if (ke.isControlDown() && ke.getCode().equals(KeyCode.H)) {
            onAddParallel(null);
        }
        if (ke.getCode().equals(KeyCode.F5)) {
            workTree.getRoot().setExpanded(false);
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        createRoot();
                    } finally {
                        Platform.runLater(() -> blockUI(false));
                    }
                    return null;
                }
            };
            new Thread(task).start();
        }
    }

    /**
     * 直列の工程を追加する
     *
     * @param event
     */
    @FXML
    private void onAddSerial(ActionEvent event) {
        try {
            if (Objects.isNull(workflowModel.getWorkflowPane().getSelectedCellBase())
                    || Objects.isNull(selectedWork)
                    || selectedWorkflowAndHierarchy.getWorkflowInfo().getConWorkflowWorkInfoCollection()
                    .stream().filter(p -> p.getFkWorkId().equals(selectedWork.getWorkId())).count() != 0) {
                return;
            }

            // 実績があると編集不可になりボタンが無効なるためそれに合わせる 
            if (addSeriesButton.isDisable()) {
                return;
            }

            //開始時間終了時間の設定
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = sdf.parse("1970-01-01 00:00:00+00");

            if (workflowModel.getWorkflowPane().getSelectedCellBase() instanceof WorkCell) {
                WorkCell previousCell = (WorkCell) workflowModel.getWorkflowPane().getSelectedCellBase();
                ConWorkflowWorkInfoEntity workflowWork = previousCell.getWorkflowWork();
                startDate = workflowWork.getSkipFlag() ? workflowWork.getStandardStartTime() : workflowWork.getStandardEndTime();
            } else if (workflowModel.getWorkflowPane().getSelectedCellBase() instanceof ParallelStartCell) {
                // ParallelCell内の一番大きい時間を取得する
                WorkCell previousCell = workflowModel.getWorkflowPane().getLastWorkCell((ParallelStartCell) workflowModel.getWorkflowPane().getSelectedCellBase());
                if (Objects.nonNull(previousCell)) {
                    ConWorkflowWorkInfoEntity workflowWork = previousCell.getWorkflowWork();
                    startDate = workflowWork.getSkipFlag() ? workflowWork.getStandardStartTime() : workflowWork.getStandardEndTime();
                }
            }

            Date endDate = new Date(startDate.getTime() + selectedWork.getTaktTime());

            // 工程セル
            WorkCell workCell = this.workflowModel.createWorkCell(selectedWork);
            ConWorkflowWorkInfoEntity workflowWork = workCell.getWorkflowWork();
            workflowWork.setStandardStartTime(startDate);
            workflowWork.setStandardEndTime(endDate);

            if (workflowModel.add(workflowModel.getWorkflowPane().getSelectedCellBase(), workCell)) {
                selectedWorkflowAndHierarchy.getWorkflowInfo().getConWorkflowWorkInfoCollection().add(workflowWork);
                workCell.setSelected(true);
            }

            this.workflowModel.updateTimetable(workCell, workflowWork.getTaktTime(), false);
            this.workflowModel.updateWorkflowOrder();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    @FXML
    private void onAddParallel(ActionEvent event) {
        if (Objects.isNull(workflowModel.getWorkflowPane().getSelectedCellBase())
                || Objects.isNull(selectedWork)
                || selectedWorkflowAndHierarchy.getWorkflowInfo().getConWorkflowWorkInfoCollection().stream().filter(p -> p.getFkWorkId().equals(selectedWork.getWorkId())).count() != 0) {
            return;
        }

        // 実績があると編集不可になりボタンが無効なるためそれに合わせる 
        if (addParallelButton.isDisable()) {
            return;
        }

        // 作業順序
        SchedulePolicyEnum schedulePolicy = workflowModel.getWorkflowPane().getWorkflowEntity().getSchedulePolicy();

        //工程セル作成
        WorkCell workCell = workflowModel.createWorkCell(selectedWork);
        ConWorkflowWorkInfoEntity workflowWork = workCell.getWorkflowWork();

        if (workflowModel.getWorkflowPane().getSelectedCellBase() instanceof WorkCell) {
            ParallelStartCell parallelStartCell = this.workflowModel.createParallelStartCell();
            ParallelEndCell parallelEndCell = this.workflowModel.createParallelEndCell(parallelStartCell);

            WorkCell selectedCell = (WorkCell) workflowModel.getWorkflowPane().getSelectedCellBase();
            CellBase previousCell = workflowModel.getWorkflowPane().getPreviousCell(selectedCell);

            // 開始時間と終了時間の設定
            ConWorkflowWorkInfoEntity selectedWorkflowWork = selectedCell.getWorkflowWork();

            Date startDate = selectedWorkflowWork.getStandardStartTime();
            if (SchedulePolicyEnum.PriorityParallel == schedulePolicy) {
                startDate = selectedWorkflowWork.getSkipFlag() ? startDate : selectedWorkflowWork.getStandardEndTime();
            }
            workflowWork.setStandardStartTime(startDate);
            workflowWork.setStandardEndTime(new Date(startDate.getTime() + selectedWork.getTaktTime()));

            if (Objects.nonNull(previousCell)
                    && workflowModel.remove(selectedCell)
                    && workflowModel.addGateway(previousCell, parallelStartCell, parallelEndCell)
                    && workflowModel.add(parallelStartCell, selectedCell)
                    && workflowModel.add(parallelStartCell, workCell)) {
                selectedWorkflowAndHierarchy.getWorkflowInfo().getConWorkflowWorkInfoCollection().add(workflowWork);
                workCell.setSelected(true);

                this.workflowModel.updateTimetable(workCell, workflowWork.getTaktTime(), false);
                this.workflowModel.updateWorkflowOrder();

            } else {
                // 挿入
                parallelStartCell = workflowModel.getParallelStartCell(selectedCell);
                List<CellBase> cells = parallelStartCell.getFirstRow();
                int index = cells.indexOf(selectedCell) + 1;
                if (workflowModel.addWithUpdateTimetable(parallelStartCell, index, workCell)) {
                    this.workflowModel.updateWorkflowOrder();
                    selectedWorkflowAndHierarchy.getWorkflowInfo().getConWorkflowWorkInfoCollection().add(workflowWork);
                    workCell.setSelected(true);
                }
            }

        } else if (workflowModel.getWorkflowPane().getSelectedCellBase() instanceof ParallelStartCell) {
            ParallelStartCell parallelStartCell = (ParallelStartCell) workflowModel.getWorkflowPane().getSelectedCellBase();

            List<CellBase> cells = parallelStartCell.getFirstRow().stream()
                    .filter(o -> o instanceof WorkCell)
                    .collect(Collectors.toList());
            WorkCell beforeCell = (WorkCell) cells.get(cells.size() - 1);

            Date startDate = beforeCell.getWorkflowWork().getStandardStartTime();
            if (SchedulePolicyEnum.PriorityParallel == schedulePolicy) {
                startDate = beforeCell.getWorkflowWork().getStandardEndTime();
            }
            workflowWork.setStandardStartTime(startDate);
            workflowWork.setStandardEndTime(new Date(startDate.getTime() + selectedWork.getTaktTime()));

            if (workflowModel.add(parallelStartCell, workCell)) {
                selectedWorkflowAndHierarchy.getWorkflowInfo().getConWorkflowWorkInfoCollection().add(workflowWork);
                workCell.setSelected(true);

                this.workflowModel.updateTimetable(workCell, workflowWork.getTaktTime(), false);
                this.workflowModel.updateWorkflowOrder();
            }
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        logger.info("onCancel:start");
        ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Cancel"), LocaleUtils.getString("key.EditCancel"));
        if (ret.equals(ButtonType.OK)) {
            if (destoryComponent()) {
                //変更が検出されないよう現在の値をコピーする
                cloneInitialWorkflow = workflowModel.getWorkflow();

                WorkflowInfoEntity workflow = workflowInfoFacade.find(selectedWorkflowAndHierarchy.getWorkflowInfo().getWorkflowId(), true);
                selectedWorkflowAndHierarchy.setWorkflow(workflow);
                sc.setComponent("ContentNaviPane", "WorkflowDetailCompo", selectedWorkflowAndHierarchy);
            }
        }
    }

    @FXML
    private void onWorkflowSave(ActionEvent event) {
        logger.info("onWorkflowSave:Start");
        registWorkflowAssembly(true);
    }

    /**
     * 工程順の変更内容を適用する
     *
     * @param event
     */
    @FXML
    private void onApply(ActionEvent event) {
        logger.info("onApply:Start");
        registWorkflowAssembly(false);
    }

    /**
     * 登録を実施する
     *
     * @param isTrans 前の画面に戻るかどうか
     */
    private void registWorkflowAssembly(boolean isTrans) {
        logger.info("registWorkflowAssembly:Start");
        blockUI(true);
        Task task = new Task<ResponseEntity>() {
            @Override
            protected ResponseEntity call() throws Exception {
                return updateWorkflowThread();
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                boolean isForcedReturn = false;
                try {
                    ResponseEntity res = this.getValue();

                    if (!ResponseAnalyzer.getAnalyzeResult(res)) {
                        if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                            // 排他バージョンが異なる。
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistOrderProcesses"), LocaleUtils.getString("key.alert.differentVerInfo"));
                        }
                        isForcedReturn = true;
                        return;
                    }

                    // 保存に成功して画面移動なしの場合、情報を更新する。
                    if (Objects.nonNull(res) && res.isSuccess()) {
                        updateView(cloneInitialWorkflow);
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    if (isTrans || isForcedReturn) {
                        sc.setComponent("ContentNaviPane", "WorkflowEditCompo");
                    }
                    blockUI(false);
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

                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistOrderProcesses"), LocaleUtils.getString("key.alert.systemError"));
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                    logger.info("registWorkflowAssembly:End");
                }
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void onWorkDelete(ActionEvent event) {
        List<CellBase> checkedCells = workflowModel.getWorkflowPane().getCheckedCells();
        if (checkedCells.isEmpty()) {
            return;
        }

        final String messgage = checkedCells.size() > 1
                ? LocaleUtils.getString("key.DeleteMultipleMessage")
                : LocaleUtils.getString("key.DeleteSingleMessage");
        final String content = checkedCells.size() > 1
                ? null
                : checkedCells.get(0).getBpmnNode().getName();

        ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), messgage, content);
        if (ret.equals(ButtonType.OK)) {
            //チェックされている工程の削除
            for (CellBase cell : checkedCells) {
                if (cell instanceof WorkCell) {
                    ConWorkflowWorkInfoEntity workflowWork = ((WorkCell) cell).getWorkflowWork();
                    workflowModel.removeWithUpdateTimetable(cell);
                }
            }

            selectedWorkflowAndHierarchy
                    .getWorkflowInfo()
                    .setConWorkflowWorkInfoCollection(
                            workflowModel
                                    .getWorkflowPane()
                                    .getCellList()
                                    .stream()
                                    .filter(cell -> cell instanceof WorkCell)
                                    .map(cell -> (WorkCell) cell)
                                    .filter(cell -> !cell.isChecked())
                                    .map(WorkCell::getWorkflowWork)
                                    .collect(Collectors.toList()));
            workflowModel.removeUnnecessaryCell();
        }
    }

    @FXML
    private void onWorkEdit(ActionEvent event) {
        List<CellBase> selected = workflowModel.getWorkflowPane().getCellList().stream().filter(p -> p.isChecked()).collect(Collectors.toList());

        if (selected.size() > 1) {
            WorkSettingDialogEntity workSetting = new WorkSettingDialogEntity();
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkflowAssociationSettingCompo", workSetting);
            if (ret.equals(ButtonType.OK)) {
                blockUI(true);
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            selected.stream().filter(p -> p instanceof WorkCell).forEach((cell) -> {
                                apply((WorkCell) cell, workSetting);
                                Platform.runLater(() -> reRenderWorkCell((WorkCell) cell));
                            });
                        } finally {
                            Platform.runLater(() -> blockUI(false));
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        } else if (selected.size() == 1) {
            WorkCell workCell = (WorkCell) selected.get(0);
            ConWorkflowWorkInfoEntity workflowWork = workCell.getWorkflowWork();

            List<EquipmentInfoEntity> equipments = new ArrayList<>();
            List<OrganizationInfoEntity> organizations = new ArrayList<>();

            if (Objects.nonNull(workflowWork.getEquipmentCollection())) {
                workflowWork.getEquipmentCollection().stream().forEach((entity) -> {
                    equipments.add(CacheUtils.getCacheEquipment(entity));
                });
            }

            if (Objects.nonNull(workflowWork.getOrganizationCollection())) {
                workflowWork.getOrganizationCollection().stream().forEach((entity) -> {
                    organizations.add(CacheUtils.getCacheOrganization(entity));
                });
            }

            WorkInfoEntity work = getWorkEntity(workflowWork.getFkWorkId());

            WorkSettingDialogEntity dialogEntity = new WorkSettingDialogEntity(work.getTaktTime(), workflowWork.getStandardStartTime(), workflowWork.getStandardEndTime(), workflowWork.getSkipFlag(), equipments, organizations, true, false, new LinkedList<>(workflowWork.getSchedule()));

            ButtonType ret = sc.showComponentDialog(work.getWorkName() + " : " + work.getWorkRev(), "WorkflowAssociationSettingCompo", dialogEntity);
            if (ret.equals(ButtonType.OK)) {
                blockUI(true);
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            apply(workCell, dialogEntity);
                            Platform.runLater(() -> reRenderWorkCell(workCell));
                        } finally {
                            Platform.runLater(() -> blockUI(false));
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        }
    }

    @FXML
    private void onAllCheck(ActionEvent event) {
        for (CellBase cell : workflowModel.getWorkflowPane().getCellList()) {
            cell.setChecked(true);
        }
    }

    @FXML
    private void onAllUncheck(ActionEvent event) {
        for (CellBase cell : workflowModel.getWorkflowPane().getCellList()) {
            cell.setChecked(false);
        }
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectedWorkflowAndHierarchy) {
            selectedWorkflowAndHierarchy = (SelectedWorkflowAndHierarchy) argument;

            WorkflowInfoEntity workflow = selectedWorkflowAndHierarchy.getWorkflowInfo();
            if (Objects.isNull(workflow.getConWorkflowWorkInfoCollection())) {
                workflow.setConWorkflowWorkInfoCollection(new ArrayList<>());
            }

            updateView(workflow);

            WorkflowCellEventNotifier.addWorkflowCellEventListener(this);

            zoomPane.visibleProperty().bind(visibleScaleToggleButton.selectedProperty());

            zoomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                workflowModel.setScale(newValue.doubleValue());
            });

            workflowScrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
                if (event.isControlDown()) {
                    double value = zoomSlider.getValue();
                    double unit = zoomSlider.getMajorTickUnit();
                    zoomSlider.setValue(event.getDeltaY() > 0 ? value + unit : value - unit);
                    event.consume();
                }
            });

            visibleEquipmentToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
                workflowModel.setVisibleEquipment(newValue);
            });

            visibleOrganizationToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
                workflowModel.setVisibleOrganization(newValue);
            });

            //ワークフローペインからのドラッグ終了時、座標監視とスクロールを止める
            workflowScrollPane.setOnDragDone(event -> {
                workflowScrollPane.getScene().setOnDragOver(null);
                workflowScrollTimer.stop();
                System.out.println("DragDone");
            });

            //ワークフローペイン上へドラッグした際、スクロールの設定を行う
            workflowScrollPane.setOnDragEntered(event -> {
                System.out.println("DragEntered");

                workflowScrollTimer.stop();

                //ワークフローペインの、ウィンドウ上での上下左右端座標を取得
                final double upperEnd = workflowScrollPane.getLocalToSceneTransform().getTy();
                final double lowerEnd = upperEnd + workflowScrollPane.getHeight();
                final double leftEnd = workflowScrollPane.getLocalToSceneTransform().getTx();
                final double rightEnd = leftEnd + workflowScrollPane.getWidth();

                //ウィンドウ上のマウス座標を監視し、スクロール方向を決める
                workflowScrollPane.getScene().setOnDragOver(dragEvent -> {
                    if (upperEnd > dragEvent.getY()) {
                        workflowVDirection = ScrollDirection.UPPER;
                    } else if (lowerEnd < dragEvent.getY()) {
                        workflowVDirection = ScrollDirection.LOWER;
                    } else {
                        workflowVDirection = ScrollDirection.NEUTRAL;
                    }

                    if (leftEnd > dragEvent.getX()) {
                        workflowHDirection = ScrollDirection.LEFT;
                    } else if (rightEnd < dragEvent.getX()) {
                        workflowHDirection = ScrollDirection.RIGHT;
                    } else {
                        workflowHDirection = ScrollDirection.NEUTRAL;
                    }
                });

                workflowScrollTimer.setCycleCount(Timeline.INDEFINITE);
            });

            //ドラッグ時、ワークフローペイン上からカーソルが離脱した際、スクロール処理を開始する
            workflowScrollPane.setOnDragExited(event -> {
                System.out.println("DragExited");
                workflowScrollTimer.play();
            });

            //工程ツリーからのドラッグ終了時、座標監視とスクロールを止める
            workTree.setOnDragDone(event -> {
                workflowScrollPane.getScene().setOnDragOver(null);
                workflowScrollTimer.stop();
                System.out.println("DragDone");
            });
        }
    }

    private void updateView(WorkflowInfoEntity workflow) {
        WorkflowPane pane = workflowModel.getWorkflowPane();

        // カンバンに使用されているか調査
        KanbanSearchCondition condition = new KanbanSearchCondition();
        condition.setWorkflowIdCollection(Arrays.asList(workflow.getWorkflowId()));
        Long countOfUsing = kanbanInfoFacade.countSearch(condition);
        boolean used = Objects.nonNull(countOfUsing) && (0 < countOfUsing);

        pane.setWorkflowEntity(workflow);
        logger.info("createWorkflow:Start");

        // 工程順ワークフローにおいては常に編集(組織・設備・時刻等)を可能とする
        workflowModel.createWorkflow(pane, !used, true);

        logger.info("createWorkflow:End");

        List<CellBase> cslls = workflowModel.getWorkflowPane().getCellList();
        List<WorkInfoEntity> works = new ArrayList();

        // 工程を取得
        long count = workInfoFacade.getWorkCountByWorkflow(workflow.getWorkflowId());
        for (long from = 0; from < count; from += RANGE) {
            works.addAll(workInfoFacade.getWorkRangeByWorkflow(workflow.getWorkflowId(), from, from + RANGE - 1));
        }

        // 工程名を反映
        cslls.stream().filter(p -> p instanceof WorkCell).forEach((entity) -> {
            WorkCell workCell = (WorkCell) entity;
            Long workId = workCell.getWorkflowWork().getFkWorkId();
            Optional<WorkInfoEntity> opt = works.stream().filter(o -> o.getWorkId().equals(workId)).findFirst();
            if (opt.isPresent()) {
                String workName = opt.get().getWorkName() + " : " + opt.get().getWorkRev();
                workCell.getWorkflowWork().setWorkName(workName);
                workCell.setWorkNameLabelText(workName);
            }

            this.reRenderWorkCell(workCell);
        });

        //最初に表示されたワークフローをコピー
        cloneInitialWorkflow = workflowModel.getWorkflow().clone();

        Platform.runLater(() -> {
            workflowName.setText(workflow.getWorkflowName() + " : " + workflow.getWorkflowRev().toString());
            workflowScrollPane.setContent(pane);

            addSeriesButton.setDisable(used);
            addParallelButton.setDisable(used);
            deleteWorkButton.setDisable(used);

            //ワークフローペインのスクロールバー取得
            Set<Node> bars = workflowScrollPane.lookupAll(".scroll-bar");
            workflowVBar = (ScrollBar) bars.stream().filter(p -> ((ScrollBar) p).getOrientation().equals(Orientation.VERTICAL)).findFirst().get();
            workflowHBar = (ScrollBar) bars.stream().filter(p -> ((ScrollBar) p).getOrientation().equals(Orientation.HORIZONTAL)).findFirst().get();
        });
    }

    /**
     * ツリーの親階層生成
     *
     */
    private synchronized void createRoot() {
        logger.debug("createRoot start.");
        try {
            this.hierarchyInfos.clear();

            long count = workHierarchyInfoFacade.getTopHierarchyCount();

            for (long from = 0; from < count; from += RANGE) {
                List<WorkHierarchyInfoEntity> entities = workHierarchyInfoFacade.getTopHierarchyRange(from, from + RANGE - 1, true, false);
                entities = entities.stream().filter(s -> !s.getHierarchyName().equals(this.liteTreeName)).collect(Collectors.toList());

                this.hierarchyInfos.addAll(entities);
            }

            this.updateTree();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.debug("createRoot end.");
    }

    /**
     * 工程ツリーの表示を更新する。
     */
    private void updateTree() {

        List<TreeItem<TreeCellInterface>> treeItems
                = this.hierarchyInfos
                .stream()
                .map(entity -> {
                    // 階層配下の工程一覧から、表示する工程一覧を取得する。
                    List<WorkInfoEntity> showWorkItems = this.getShowWorkItems(entity);

                    WorkHierarchyInfoEntity dispItem = new WorkHierarchyInfoEntity(entity);
                    dispItem.setWorkInfoCollection(showWorkItems);

                    TreeItem<TreeCellInterface> item = new TreeItem<>(new WorkHierarchyTreeEntity(dispItem));
                    if (!showWorkItems.isEmpty() || entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem<>());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    return item;
                })
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            TreeItem<TreeCellInterface> rootItem = permanenceData.getWorkAndHierarchyRootItem();

            if (rootItem.getChildren().isEmpty()) {
                rootItem.expandedProperty().addListener(this.changeListener);
            }

            rootItem.getChildren().clear();
            rootItem.getValue().setChildCount(this.hierarchyInfos.size());

            if (this.hierarchyInfos.isEmpty()) {
                // リスナーが重複するため削除
                rootItem.expandedProperty().removeListener(this.changeListener);
            }

            rootItem.getChildren().setAll(treeItems);
            this.workTree.rootProperty().setValue(rootItem);
            this.workTree.setCellFactory((TreeView<TreeCellInterface> o) -> new CustomTreeCell());

            rootItem.setExpanded(true);
        });
    }

    /**
     * ツリーの子階層生成
     *
     * @param parentItem 親階層
     */
    private synchronized void expand(TreeItem<TreeCellInterface> parentItem) {
        try {
            parentItem.getChildren().clear();

            // 子階層を追加
            long count = parentItem.getValue().getChildCount();
            List<WorkHierarchyInfoEntity> entities = workHierarchyInfoFacade.getAffilationHierarchyRange(parentItem.getValue().getHierarchyId(), null, null, true, false);
            List<TreeItem<TreeCellInterface>> treeItemsIfs
                    = entities
                    .stream()
                    .filter(s -> !s.getHierarchyName().equals(this.liteTreeName))
                    .map(entity -> {
                        // 階層配下の工程一覧から、表示する工程一覧を取得する。
                        List<WorkInfoEntity> showWorkItems = this.getShowWorkItems(entity);

                        WorkHierarchyInfoEntity dispItem = new WorkHierarchyInfoEntity(entity);
                        dispItem.setWorkInfoCollection(showWorkItems);

                        TreeItem<TreeCellInterface> item = new TreeItem<>(new WorkHierarchyTreeEntity(dispItem));
                        if (!showWorkItems.isEmpty() || entity.getChildCount() > 0) {
                            item.getChildren().add(new TreeItem<>());
                        }
                        item.expandedProperty().addListener(this.changeListener);
                        return item;
                    })
                    .collect(Collectors.toList());

            // 工程を追加
            WorkHierarchyInfoEntity workHierarchyInfo = (WorkHierarchyInfoEntity) parentItem.getValue().getEntity();
            if (Objects.nonNull(workHierarchyInfo.getWorkInfoCollection())) {
                workHierarchyInfo.getWorkInfoCollection()
                        .stream()
                        .map(WorkTreeEntity::new)
                        .map(TreeItem<TreeCellInterface>::new)
                        .forEach(treeItemsIfs::add);
            }

            Platform.runLater(() -> {
                parentItem.getChildren().addAll(treeItemsIfs);
                this.workTree.setCellFactory((TreeView<TreeCellInterface> o) -> new CustomTreeCell());
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程順を保存する
     */
    private ResponseEntity updateWorkflowThread() {
        ResponseEntity res = null;
        try {
            // 工程順オーダーを更新
            this.workflowModel.updateWorkflowOrder();

            WorkflowInfoEntity workflow = workflowModel.getWorkflow();
            workflow.setFkUpdatePersonId(loginUserInfoEntity.getId());
            workflow.setUpdateDatetime(new Date());

            res = workflowInfoFacade.updateWork(workflow);

            // 保存に成功した場合、データを再取得する。
            if (Objects.nonNull(res) && res.isSuccess()) {
                Long workflowId = workflow.getWorkflowId();
                if (Objects.nonNull(res.getUri())) {
                    workflowId = UriConvertUtils.getUriToWorkflowId(res.getUri());
                }

                if (Objects.nonNull(workflowId)) {
                    cloneInitialWorkflow = workflowInfoFacade.find(workflowId);
                    workflowModel.getWorkflow().setVerInfo(cloneInitialWorkflow.getVerInfo());
                }
            } 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return res;
    }

    private void reRenderWorkCell(WorkCell cell) {
        if (Objects.nonNull(cell.getWorkflowWork().getOrganizationCollection())) {
            cell.setOrganizationLabelText(getOrganizationNames(cell.getWorkflowWork().getOrganizationCollection()));
        }
        if (Objects.nonNull(cell.getWorkflowWork().getEquipmentCollection())) {
            cell.setEquipmentLabelText(getEquipmentNames(cell.getWorkflowWork().getEquipmentCollection()));
        }
    }

    private String getEquipmentNames(List<Long> list) {
        StringBuilder sb = new StringBuilder();
        list.forEach((id) -> {
            EquipmentInfoEntity equipment = CacheUtils.getCacheEquipment(id);
            if (Objects.nonNull(equipment)) {
                sb.append(equipment.getEquipmentName());
                sb.append(' ');
            }
        });

        return sb.toString();
    }

    private String getOrganizationNames(List<Long> list) {
        StringBuilder sb = new StringBuilder();
        list.forEach((id) -> {
            OrganizationInfoEntity organization = CacheUtils.getCacheOrganization(id);
            if (Objects.nonNull(organization)) {
                sb.append(organization.getOrganizationName());
                sb.append(' ');
            }
        });

        return sb.toString();
    }

    private WorkInfoEntity getWorkEntity(long id) {
        try {
            cache.setNewCashList(WorkInfoEntity.class);
            if (!cache.isItem(WorkInfoEntity.class, id)) {
                WorkInfoEntity work = workInfoFacade.find(id);
                cache.setItem(WorkInfoEntity.class, id, work);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return (WorkInfoEntity) cache.getItem(WorkInfoEntity.class, id);
    }

    /**
     * 工程の設定内容を適用する
     *
     * @param editEntitys 編集対象のデータ
     * @param workSetting 編集内容
     */
    private void apply(WorkCell workCell, WorkSettingDialogEntity workSetting) {
        try {
            ConWorkflowWorkInfoEntity workflowWork = workCell.getWorkflowWork();
            boolean isUpdated = false;

            if (workSetting.isEditSingle()) {
                // 基準作業日
                if (Objects.nonNull(workSetting.getStandardDay())) {
                    LocalTime startTime = DateUtils.toLocalTime(workflowWork.getStandardStartTime());
                    LocalTime endTime = DateUtils.toLocalTime(workflowWork.getStandardEndTime());
                    long minutes = ChronoUnit.MINUTES.between(startTime, endTime);

                    LocalDateTime startDatetime = LocalDateTime.of(DateUtils.toLocalDate(DateUtils.min()), startTime).plusDays(workSetting.getStandardDay() - 1);
                    LocalDateTime endDatetime = startDatetime.plusMinutes(minutes);

                    workflowWork.setStandardStartTime(DateUtils.toDate(startDatetime));
                    workflowWork.setStandardEndTime(DateUtils.toDate(endDatetime));
                    isUpdated = true;
                }

                // 基準作業時間
                if (Objects.nonNull(workSetting.getStartTime())) {
                    LocalDate date = DateUtils.toLocalDate(workflowWork.getStandardStartTime());
                    LocalTime time = DateUtils.toLocalTime(workSetting.getStartTime());
                    LocalDateTime datetime = LocalDateTime.of(date, time);

                    workflowWork.setStandardStartTime(DateUtils.toDate(datetime));
                    isUpdated = true;
                }

                if (Objects.nonNull(workSetting.getEndTime())) {
                    LocalDate date = DateUtils.toLocalDate(workflowWork.getStandardEndTime());
                    LocalTime time = DateUtils.toLocalTime(workSetting.getEndTime());
                    LocalDateTime datetime = LocalDateTime.of(date, time);

                    workflowWork.setStandardEndTime(DateUtils.toDate(datetime));
                    isUpdated = true;
                }
            }

            // スキップ
            if (Objects.nonNull(workSetting.getSkip())) {
                workflowWork.setSkipFlag(workSetting.getSkip());
                isUpdated = true;
            }

            // 組織
            if (Objects.nonNull(workSetting.getOrganizations())) {
                List<Long> ids = new ArrayList<>();
                workSetting.getOrganizations().stream().forEach((organization) -> {
                    ids.add(organization.getOrganizationId());
                });
                workflowWork.setOrganizationCollection(ids);
            }

            // 設備
            if (Objects.nonNull(workSetting.getEquipments())) {
                List<Long> ids = new ArrayList<>();
                workSetting.getEquipments().stream().forEach((equipment) -> {
                    ids.add(equipment.getEquipmentId());
                });
                workflowWork.setEquipmentCollection(ids);
            }

            // スケジュール
            if (Objects.nonNull(workSetting.getSchedule())) {
                workflowWork.setSchedule(workSetting.getSchedule());
            }

            workflowWork.updateMember();

            if (isUpdated) {
                long diff = workflowWork.getSkipFlag() ? -(workflowWork.getTaktTime()) : 0;
                this.workflowModel.updateTimetable(workCell, diff, true);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        assemblyProgress.setVisible(flg);
    }

    /**
     * 変更を調べる
     *
     * @return 変更が存在したらtrue
     */
    private boolean isChanged() {
        // 編集権限なしは常に無変更
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
            return false;
        }

        if (cloneInitialWorkflow.equalsDisplayInfo(workflowModel.getWorkflow())) {
            return false;
        }
        return true;
    }

    /**
     * 設定を設定ファイルに保存する
     */
    private void saveProperties() {
        try {
            boolean isSelectedEquip = this.visibleEquipmentToggleButton.isSelected();
            boolean isSelectedOrg = this.visibleOrganizationToggleButton.isSelected();
            boolean isVisibleScale = this.visibleScaleToggleButton.isSelected();
            double scale = this.zoomSlider.getValue();

            properties.setProperty(Constants.VISIBLE_EQUIPMENT_KEY, String.valueOf(isSelectedEquip));
            properties.setProperty(Constants.VISIBLE_ORGANIZATION_KEY, String.valueOf(isSelectedOrg));
            properties.setProperty(Constants.VISIBLE_WORKFLOW_SCALE_KEY, String.valueOf(isVisibleScale));
            properties.setProperty(Constants.WORKFLOW_SCALE_KEY, String.valueOf(scale));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 設定を設定ファイルから読み込む
     */
    private void loadProperties() {
        try {
            String showEquipStr = properties.getProperty(Constants.VISIBLE_EQUIPMENT_KEY, Constants.VISIBLE_EQUIPMENT_DEFAULT);
            String showOrgStr = properties.getProperty(Constants.VISIBLE_ORGANIZATION_KEY, Constants.VISIBLE_ORGANIZATION_DEFAULT);
            String showScaleStr = properties.getProperty(Constants.VISIBLE_WORKFLOW_SCALE_KEY, Constants.VISIBLE_WORKFLOW_SCALE_DEFAULT);
            String scaleStr = properties.getProperty(Constants.WORKFLOW_SCALE_KEY, Constants.WORKFLOW_SCALE_DEFAULT);

            double scale = Double.valueOf(scaleStr);
            scale = scale > 10.0 ? 10.0 : scale < 1.0 ? 1.0 : scale;

            this.visibleEquipmentToggleButton.setSelected(Boolean.valueOf(showEquipStr));
            this.visibleOrganizationToggleButton.setSelected(Boolean.valueOf(showOrgStr));
            this.visibleScaleToggleButton.setSelected(Boolean.valueOf(showScaleStr));
            this.zoomSlider.setValue(Double.valueOf(scaleStr));

            // 現状bindしていないため最初のみ手動で設定
            this.workflowModel.setVisibleEquipment(Boolean.valueOf(showEquipStr));
            this.workflowModel.setVisibleOrganization(Boolean.valueOf(showOrgStr));
            this.workflowModel.setScale(scale);

        } catch (Exception ex) {
            logger.fatal(ex, ex);

            this.workflowModel.setVisibleEquipment(true);
            this.workflowModel.setVisibleOrganization(true);
            this.workflowModel.setScale(1.0);
        }
    }

    /**
     * 画面破棄時に内容に変更がないか調べて変更があるなら保存する
     *
     * @return
     */
    @Override
    public boolean destoryComponent() {
        WorkflowCellEventNotifier.removeWorkflowCellEventListener(this);

        saveProperties();

        //キャンセル以外の画面遷移で変更が存在してるか確認
        if (isChanged()) {
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            if (ButtonType.YES == buttonType) {
                registWorkflowAssembly(false);
            } else if (ButtonType.CANCEL == buttonType) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onWorkflowCellDoubleClicked(WorkflowCellEvent event) {
        if (Objects.nonNull(event.getSouce()) && (event.getSouce() instanceof WorkCell)) {
            this.onAllUncheck(null);
            ((WorkCell) event.getSouce()).setChecked(true);
            this.onWorkEdit(null);
        }
    }

    /**
     * 階層配下の工程一覧から、表示する工程一覧を取得する。
     *
     * @param hierarchy 階層
     * @return 表示する工程一覧
     */
    private List<WorkInfoEntity> getShowWorkItems(WorkHierarchyInfoEntity hierarchy) {
        if (this.finalApproveOnly) {
            // 階層配下の工程一覧から最終承認済のみ抽出する。
            return hierarchy.getWorkInfoCollection().stream()
                    .filter(p -> Objects.equals(p.getApprovalState(), ApprovalStatusEnum.FINAL_APPROVE))
                    .collect(Collectors.toList());
        } else {
            return hierarchy.getWorkInfoCollection();
        }
    }
}
