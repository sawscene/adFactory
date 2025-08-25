/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.component;

import adtekfuji.admanagerapp.kanbaneditpluginels.common.KanbanEditPermanenceData;
import adtekfuji.admanagerapp.kanbaneditpluginels.common.KanbanHierarchyTreeCell;
import adtekfuji.admanagerapp.kanbaneditpluginels.common.KanbanLedgerPermanenceData;
import adtekfuji.admanagerapp.kanbaneditpluginels.common.LedgerSheetFactory;
import adtekfuji.admanagerapp.kanbaneditpluginels.common.SelectedKanbanAndHierarchy;
import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.IndexedCheckModel;

/**
 * カンバン画面コントローラー
 *
 * @author e.mori
 */
@FxComponent(id = "KanbanListCompoELS", fxmlPath = "/fxml/admanakanbaneditpluginels/kanban_list_compo.fxml")
public class KanbanListCompoFxController implements Initializable {

    /**
     * カンバンリスト表示用データ
     */
    public class DisplayData {

        private final Long id;
        private final Long workflowId;
        private String kanbanName = "";
        private String workflowName = "";
        private String status = "";
        private String startDate = "";
        private String endDate = "";
        private final KanbanInfoEntity entity;

        /**
         * カンバンリスト表示用データ
         *
         * @param entity
         */
        public DisplayData(KanbanInfoEntity entity) {
            this.id = entity.getKanbanId();
            this.workflowId = entity.getFkWorkflowId();
            this.kanbanName = entity.getKanbanName();
            if (Objects.nonNull(entity.getWorkflowName())) {
                this.workflowName = entity.getWorkflowName();
            }
            this.status = LocaleUtils.getString(entity.getKanbanStatus().getResourceKey());
            if (Objects.nonNull(entity.getStartDatetime())) {
                this.startDate = formatter.format(entity.getStartDatetime());
            }
            if (Objects.nonNull(entity.getCompDatetime())) {
                this.endDate = formatter.format(entity.getCompDatetime());
            }
            this.entity = entity;
        }

        public Long getId() {
            return id;
        }

        public Long getWorkflowId() {
            return workflowId;
        }

        public String getKanbanName() {
            return kanbanName;
        }

        public String getWorkflowName() {
            return workflowName;
        }

        public String getStatus() {
            return status;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public KanbanInfoEntity getEntity() {
            return entity;
        }
    }

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static KanbanHierarchyInfoFacade kanbanHierarchyInfoFacade = new KanbanHierarchyInfoFacade();
    private final static KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
    private final static EquipmentInfoFacade equipmentFacade = new EquipmentInfoFacade();
    private final static OrganizationInfoFacade organizationFacade = new OrganizationInfoFacade();

    private final static long RANGE = 20;
    private final static long ROOT_ID = 0;
    private final static String URI_SPLIT = "/";
    private final static String COMMA_SPLIT = ",";
    private final static CashManager cashManager = CashManager.getInstance();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final KanbanEditPermanenceData kanbanEditPermanenceData = KanbanEditPermanenceData.getInstance();

    private SimpleDateFormat formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
    private long searchMax;
    private long maxLoadSize;
    private static final String SEARCH_FILTER_START_DATE = "search_filter_start_date";
    private static final String SEARCH_FILTER_END_DATE = "search_filter_end_date";
    private static final String SEARCH_FILTER_STATUS = "search_filter_status";
    private Boolean countOverFlag = false;
    private Boolean isDispDialog = false;

    private final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
        if (Objects.nonNull(newValue) && newValue.equals(true)) {
            TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
            if (!isDispDialog) {
                blockUI(true);
            }
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        expand(treeItem);
                    } finally {
                        if (!isDispDialog) {
                            Platform.runLater(() -> blockUI(false));
                        }
                    }
                    return null;
                }
            };
            new Thread(task).start();
        }
    };

    @FXML
    private TreeView<KanbanHierarchyInfoEntity> hierarchyTree;
    @FXML
    private Pane Progress;
    @FXML
    private TableView<DisplayData> kanbanList;
    @FXML
    private TableColumn kanbanNameColumn;
    @FXML
    private TableColumn workflowNameColumn;
    @FXML
    private TableColumn statusColumn;
    @FXML
    private TableColumn startTimeColumn;
    @FXML
    private TableColumn endTimeColumn;
    @FXML
    private TextField kanbanNameField;
    @FXML
    private CheckListView<String> statusList;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button delTreeButton;
    @FXML
    private Button editTreeButton;
    @FXML
    private Button createTreeButton;
    @FXML
    private Button deleteKanbanButton;
    @FXML
    private Button moveKanbanButton;
    @FXML
    private Button editKanbanButton;
    @FXML
    private Button createKanbanButton;
    @FXML
    private Button ledgerOutButton;

    /**
     * 初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        kanbanList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
        // ELS版は、adManeApp.properties の設定に関係なく KanbanCreateCompoELS を呼び出すので、設定を作成しない。
//        if (!this.properties.containsKey(Config.COMPO_CREATE_KANBAN)) {
//            this.properties.put(Config.COMPO_CREATE_KANBAN, "KanbanCreateCompoELS");
//        }

        //役割の権限によるボタン無効化.
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            this.delTreeButton.setDisable(true);
            this.editTreeButton.setDisable(true);
            this.createTreeButton.setDisable(true);
            this.createKanbanButton.setDisable(true);
        }
        this.editKanbanButton.setDisable(true);
        this.deleteKanbanButton.setDisable(true);
        this.moveKanbanButton.setDisable(true);
        this.ledgerOutButton.setDisable(true);

        Progress.setVisible(false);

        searchMax = ClientServiceProperty.getSearchMax();
        maxLoadSize = ClientServiceProperty.getRestRangeNum();

        cashManager.setNewCashList(EquipmentInfoEntity.class);
        cashManager.setNewCashList(OrganizationInfoEntity.class);

        //検索条件のステータス項目を描画
        ObservableList<String> stateList = FXCollections.observableArrayList(KanbanStatusEnum.getMessages(rb));
        statusList.setItems(stateList);

        //検索条件の読み込みエラー処理がない(設定値がない場合、日付無、ステータス無)
        if (Objects.nonNull(properties.getProperty(SEARCH_FILTER_STATUS)) && !"".equals(properties.getProperty(SEARCH_FILTER_STATUS))) {
            String[] statusData = properties.getProperty(SEARCH_FILTER_STATUS).split(COMMA_SPLIT);
            IndexedCheckModel<String> cm = statusList.getCheckModel();
            for (String status : statusData) {
                cm.check(LocaleUtils.getString(KanbanStatusEnum.getEnum(status).getResourceKey()));
            }
        }
        if (Objects.nonNull(properties.getProperty(SEARCH_FILTER_START_DATE)) && !"".equals(properties.getProperty(SEARCH_FILTER_START_DATE))) {
            Calendar calendarStartDate = Calendar.getInstance();
            calendarStartDate.setTime(new Date());
            calendarStartDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(properties.getProperty(SEARCH_FILTER_START_DATE)));
            startDatePicker.setValue(LocalDate.of(calendarStartDate.get(Calendar.YEAR), calendarStartDate.get(Calendar.MONTH) + 1, calendarStartDate.get(Calendar.DAY_OF_MONTH)));
        }
        if (Objects.nonNull(properties.getProperty(SEARCH_FILTER_END_DATE)) && !"".equals(properties.getProperty(SEARCH_FILTER_END_DATE))) {
            Calendar calendarEndDate = Calendar.getInstance();
            calendarEndDate.setTime(new Date());
            calendarEndDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(properties.getProperty(SEARCH_FILTER_END_DATE)));
            endDatePicker.setValue(LocalDate.of(calendarEndDate.get(Calendar.YEAR), calendarEndDate.get(Calendar.MONTH) + 1, calendarEndDate.get(Calendar.DAY_OF_MONTH)));
        }

        //エンティティメンバーとバインド
        kanbanNameColumn.setCellValueFactory(new PropertyValueFactory<>("kanbanName"));
        workflowNameColumn.setCellValueFactory(new PropertyValueFactory<>("workflowName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        //階層ツリー選択時処理
        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<KanbanHierarchyInfoEntity>> observable, TreeItem<KanbanHierarchyInfoEntity> oldValue, TreeItem<KanbanHierarchyInfoEntity> newValue) -> {
            if (Objects.nonNull(newValue) && newValue.getValue().getKanbanHierarchyId() != ROOT_ID) {
                // 工程リスト更新
                blockUI(true);
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            searchKanbanData();
                            kanbanEditPermanenceData.setSelectedWorkHierarchy(newValue);
                        } finally {
                            Platform.runLater(() -> {
                                blockUI(false);
                            });
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            } else {
                // リストクリア
                Platform.runLater(() -> {
                    clearKanbanList();
                });
                kanbanEditPermanenceData.setSelectedWorkHierarchy(null);
            }
        });

        // カンバン選択時の処理
        //      ※.複数件選択状態で、alt + クリックで選択解除や再選択した時はイベント発生しない
        this.kanbanList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends DisplayData> observable, DisplayData oldValue, DisplayData newValue) -> {
            if (Objects.isNull(kanbanList.getSelectionModel().getSelectedItems())
                    || kanbanList.getSelectionModel().getSelectedItems().size() < 1) {
                // 未選択
                Platform.runLater(() -> {
                    this.editKanbanButton.setDisable(true);
                    this.deleteKanbanButton.setDisable(true);
                    this.moveKanbanButton.setDisable(true);
                    this.ledgerOutButton.setDisable(true);
                });
            }
        });

        //カンバンクリック
        this.kanbanList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 2) {
                    this.onEditKanban(new ActionEvent());
                } else {
                    if (Objects.nonNull(kanbanList.getSelectionModel().getSelectedItems())
                            && kanbanList.getSelectionModel().getSelectedItems().size() > 0) {
                        // 選択している
                        boolean isDisabledSingle = false;
                        boolean isDisabledMulti = false;
                        if (kanbanList.getSelectionModel().getSelectedItems().size() == 1) {
                            // 1件選択
                            if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                                isDisabledSingle = true;
                                isDisabledMulti = true;
                            }
                        } else {
                            // 複数選択
                            isDisabledSingle = true;
                            if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                                isDisabledMulti = true;
                            }
                        }

                        if (isDisabledSingle) {
                            Platform.runLater(() -> {
                                this.editKanbanButton.setDisable(true);
                                this.deleteKanbanButton.setDisable(true);
                                this.moveKanbanButton.setDisable(true);
                            });
                        } else {
                            Platform.runLater(() -> {
                                this.editKanbanButton.setDisable(false);
                                this.deleteKanbanButton.setDisable(false);
                                this.moveKanbanButton.setDisable(false);
                            });
                        }

                        if (isDisabledMulti) {
                            Platform.runLater(() -> {
                                this.ledgerOutButton.setDisable(true);
                            });
                        } else {
                            Platform.runLater(() -> {
                                this.ledgerOutButton.setDisable(false);
                            });
                        }
                    }
                }
            }
        });

        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    createCashDataThread();

                    if (Objects.isNull(kanbanEditPermanenceData.getKanbanHierarchyRootItem())) {
                        createRoot();
                    } else {
                        Platform.runLater(() -> {
                            hierarchyTree.setRoot(kanbanEditPermanenceData.getKanbanHierarchyRootItem());
                            selectedTreeItem(kanbanEditPermanenceData.getSelectedKanbanHierarchy(), null);
                            searchKanbanData();
                        });
                    }
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
     * (カンバン階層ツリー) キー押下のアクション
     *
     * @param ke
     */
    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        createCashDataThread();
                        createRoot();
                        Platform.runLater(() -> {
                            clearKanbanList();
                        });
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
    }

    /**
     * (カンバン階層ツリー) 編集ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onTreeEdit(ActionEvent event) {
        try {
            TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && !item.getValue().getKanbanHierarchyId().equals(ROOT_ID)) {
                String orgName = item.getValue().getHierarchyName();
                String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
                String hierarchyName = sc.showTextInputDialog(LocaleUtils.getString("key.Edit"), message, LocaleUtils.getString("key.HierarchyName"), orgName);
                if (Objects.isNull(hierarchyName)) {
                    return;
                }
                if (hierarchyName.isEmpty()) {
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                } else if (!orgName.equals(hierarchyName)) {
                    item.getValue().setHierarchyName(hierarchyName);
                    ResponseEntity res = kanbanHierarchyInfoFacade.update(item.getValue());

                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        //updateTreeItemThread(item.getParent(), item.getValue().getKanbanHierarchyId());
                        this.selectedTreeItem(item, null);
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanban"),
                                String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.KanbanHierarch")));
                        // データを戻す
                        item.getValue().setHierarchyName(orgName);
                    } else {
                        // データを戻す
                        item.getValue().setHierarchyName(orgName);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

    }

    /**
     * (カンバン階層ツリー) 削除ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onTreeDelete(ActionEvent event) {
        try {
            TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && !item.getValue().getKanbanHierarchyId().equals(ROOT_ID)) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteMessage"), item.getValue().getHierarchyName());
                if (ret.equals(ButtonType.OK)) {
                    ResponseEntity res = kanbanHierarchyInfoFacade.delete(item.getValue());

                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // 親階層の子階層の個数を更新
                        item.getParent().getValue().setChildCount(item.getParent().getValue().getChildCount() - 1);
                        updateTreeItemThread(item.getParent(), item.getValue().getKanbanHierarchyId());
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanban"),
                                String.format(LocaleUtils.getString("key.FailedToDelete"), LocaleUtils.getString("key.KanbanHierarch")));
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * (カンバン階層ツリー) 新規作成ボタンのアクション
     * @param event
     */
    @FXML
    private void onTreeCreate(ActionEvent event) {
        try {
            TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item)) {
                String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
                String hierarchyName = sc.showTextInputDialog(LocaleUtils.getString("key.NewCreate"), message, LocaleUtils.getString("key.HierarchyName"), "");
                if (Objects.isNull(hierarchyName)) {
                    return;
                }
                if (hierarchyName.isEmpty()) {
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                } else {
                    KanbanHierarchyInfoEntity hierarchy = new KanbanHierarchyInfoEntity();
                    hierarchy.setHierarchyName(hierarchyName);
                    hierarchy.setParentId(item.getValue().getKanbanHierarchyId());

                    ResponseEntity res = kanbanHierarchyInfoFacade.regist(hierarchy);

                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // 親階層の子階層の個数を更新
                        item.getValue().setChildCount(item.getValue().getChildCount() + 1);
                        updateTreeItemThread(item, getUriToHierarcyId(res.getUri()));
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanban"),
                                String.format(LocaleUtils.getString("key.FailedToCreate"), LocaleUtils.getString("key.KanbanHierarch")));
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 編集ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onEditKanban(ActionEvent event) {
        DisplayData data = kanbanList.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(data)) {
            KanbanInfoEntity entity = new KanbanInfoEntity(data.getId(), null, data.getKanbanName(), null);
            SelectedKanbanAndHierarchy selected = new SelectedKanbanAndHierarchy(entity,
                    hierarchyTree.selectionModelProperty().getName());
            sc.setComponent("ContentNaviPane", "KanbanDetailCompoELS", selected);
        }
    }

    /**
     * 移動ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onMoveKanban(ActionEvent event) {
        try {
            DisplayData selectedKanban = kanbanList.getSelectionModel().getSelectedItem();
            TreeItem<KanbanHierarchyInfoEntity> selectedHierarchy = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedKanban) || Objects.isNull(selectedHierarchy)) {
                return;
            }

            Platform.runLater(() -> {
                blockUI(true);
                hierarchyTree.setVisible(false);
                kanbanList.setVisible(false);
            });
            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(hierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"));
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "KanbanHierarchyTreeCompoELS", treeDialogEntity);

            TreeItem<KanbanHierarchyInfoEntity> hierarchy = (TreeItem<KanbanHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (ret.equals(ButtonType.OK) && Objects.nonNull(hierarchy)) {

                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        KanbanInfoEntity item = selectedKanban.getEntity();
                        KanbanInfoEntity kanban = kanbanInfoFacade.find(item.getKanbanId());
                        kanban.setParentId(hierarchy.getValue().getKanbanHierarchyId());
                        kanban.setFkUpdatePersonId(loginUserInfoEntity.getId());
                        kanban.setUpdateDatetime(new Date());

                        ResponseEntity res = kanbanInfoFacade.update(kanban);
                        if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                            hierarchyTree.getSelectionModel().select(hierarchy);
                            searchKanbanData();
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            Platform.runLater(() -> {
                blockUI(false);
                hierarchyTree.setVisible(true);
                kanbanList.setVisible(true);
            });
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onListCreate(ActionEvent event) {
        TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
        if (!((Objects.isNull(item) || (item.getValue().getKanbanHierarchyId() == ROOT_ID)))) {
            KanbanInfoEntity entity = new KanbanInfoEntity();
            entity.setParentId(item.getValue().getKanbanHierarchyId());
            entity.setPropertyCollection(new ArrayList<>());
            entity.setKanbanStatus(KanbanStatusEnum.PLANNING);
            SelectedKanbanAndHierarchy selected = new SelectedKanbanAndHierarchy(entity, item.getValue().getHierarchyName());
            sc.setComponent("ContentNaviPane", "KanbanDetailCompoELS", selected);
        }
    }

    /**
     * 削除ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onDeleteKanban(ActionEvent event) {
        try {
            logger.info("onDeleteKanban start.");

            DisplayData data = kanbanList.getSelectionModel().getSelectedItem();
            if (Objects.isNull(data)) {
                return;
            }
            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteMessage"));
            if (!ret.equals(ButtonType.OK)) {
                return;
            }
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // 削除
                        ResponseEntity responce = kanbanInfoFacade.delete(data.getId());
                        if (null != responce.getErrorType()) switch (responce.getErrorType()) {
                            case NOTFOUND_DELETE:
                                Platform.runLater(() -> {
                                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanDelete"), LocaleUtils.getString("key.ErrNotDeleteWithoutSpecifiedID"));
                                }); break;
                            case THERE_START_NON_DELETABLE:
                                Platform.runLater(() -> {
                                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanDelete"),
                                            String.format(LocaleUtils.getString("key.KanbanDeleteFailed"), data.getKanbanName()));
                                }); break;
                            default:
                                //表示更新.
                                searchKanbanData();
                                break;
                        }
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
        finally {
            logger.info("onDeleteKanban end.");
        }
    }

    /**
     * リスト更新
     *
     * @param event
     */
    @FXML
    private void onUpdateFilter(ActionEvent event) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    searchKanbanData();
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
     * 新規作成ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onCreateKanban(ActionEvent event) {
        try {
            logger.info("onCreateKanban start.");
            TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && item.getValue().getKanbanHierarchyId() != ROOT_ID) {
                SelectedKanbanAndHierarchy selected = new SelectedKanbanAndHierarchy(null, item.getValue().getHierarchyName(), LocaleUtils.getString("key.KanbanContinuousCreate"), item.getValue().getKanbanHierarchyId());

//                // adManeApp.propertiesで指定されたコンポーネントを呼び出す
//                String compoCreateKanban = this.properties.getProperty(Config.COMPO_CREATE_KANBAN);
//                if (!StringUtils.isEmpty(compoCreateKanban) && sc.containsComponent(compoCreateKanban)) {
//                    sc.setComponent("ContentNaviPane", compoCreateKanban, selected);
//                } else {
//                    sc.setComponent("ContentNaviPane", "KanbanCreateCompo", selected);
//                }

                // ELS版は、adManeApp.properties の設定に関係なく KanbanCreateCompoELS を呼び出す。
                sc.setComponent("ContentNaviPane", "KanbanCreateCompoELS", selected);// カンバン作成画面 (ELS)
            }
        }
        finally {
            logger.info("onCreateKanban end.");
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onRegularlyCreate(ActionEvent event) {
        TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(item) && item.getValue().getKanbanHierarchyId() != ROOT_ID) {
            SelectedKanbanAndHierarchy selected = new SelectedKanbanAndHierarchy(null, item.getValue().getHierarchyName(),
                    LocaleUtils.getString("key.KanbanRegularlyCreate"), item.getValue().getKanbanHierarchyId());
            sc.setComponent("ContentNaviPane", "KanbanMultipleRegistCompo", selected);
        }
    }

    /**
     * 帳票出力ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onCreateLedger(ActionEvent event) {
        blockUI(true);
        kanbanList.getSelectionModel().getSelectedItems().stream().forEach(data -> {
            this.createLedger(data, event);
        });
        blockUI(false);
    }

    /**
     * 帳票出力
     *
     * @param data
     * @param event
     */
    private void createLedger(DisplayData data, ActionEvent event) {
        try {
            if (Objects.isNull(data)) {
                return;
            } else if (!data.getStatus().equals(LocaleUtils.getString("key.KanbanStatusCompletion"))) {
                // カンバン 未完了
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.KanbanOutLedger"), LocaleUtils.getString("key.KanbanOutLedgerWorkUnfinished"));
                return;
            }
            //TODO: MVCに沿って通信処理(エラー対応含)を検討すること.他の通信処理も同様.
            WorkflowInfoEntity workflowInfoEntity = getWorkflow(data.getWorkflowId());
            if (Objects.isNull(workflowInfoEntity)) {
                // 工程順 取得失敗
                showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ServerErrTitle"), LocaleUtils.getString("key.ServerReconnectMessage"));
                return;
            } else if (Objects.isNull(workflowInfoEntity.getLedgerPath())) {
                // 帳票テンプレートファイルパス 未登録
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.KanbanOutLedger"), LocaleUtils.getString("key.KanbanOutLedgerPassNothing"));
                return;
            }
            //帳票データ取得処理
            KanbanLedgerPermanenceData ledgerData = new KanbanLedgerPermanenceData();
            ledgerData.setLedgerFilePass(workflowInfoEntity.getLedgerPath());
            ledgerData.setKanbanInfoEntity(getKanban(data.getId()));
            if (Objects.isNull(ledgerData.getKanbanInfoEntity())) {
                // カンバンなし
                showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ServerErrTitle"), LocaleUtils.getString("key.ServerReconnectMessage"));
                return;
            }
            ledgerData.setWorkKanbanInfoEntitys(getWorkKanbans(data.getId()));
            if (Objects.isNull(ledgerData.getWorkKanbanInfoEntitys())) {
                // 工程カンバンなし
                showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ServerErrTitle"), LocaleUtils.getString("key.ServerReconnectMessage"));
                return;
            }
            ledgerData.setSeparateworkWorkKanbanInfoEntitys(getSeparateWorkKanbans(data.getId()));
            if (Objects.isNull(ledgerData.getSeparateworkWorkKanbanInfoEntitys())) {
                // 工程カンバンなし
                showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ServerErrTitle"), LocaleUtils.getString("key.ServerReconnectMessage"));
                return;
            }
            ledgerData.setActualResultInfoEntitys(getActualResults(data.getId()));
            if (Objects.isNull(ledgerData.getActualResultInfoEntitys())) {
                // 実績なし
                showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ServerErrTitle"), LocaleUtils.getString("key.ServerReconnectMessage"));
                return;
            }

            //帳票生成用ファクトリークラスに生成処理を譲渡
            //出力終了の通知
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
            LedgerSheetFactory ledgerSheetFactory
                    = new LedgerSheetFactory(ledgerData, ledgerData.getKanbanInfoEntity().getKanbanName() + "_" + df.format(new Date()));
            switch (ledgerSheetFactory.writeLedgerSheet(event)) {
                case FAILD_NOTPASS:
                    // 帳票テンプレートファイルがない
                    showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanOutLedger"), LocaleUtils.getString("key.KanbanOutLedgerPassErr"));
                    break;
                case FAILD_OTHER:
                    // ファイル指定でキャンセルした
                    break;
                case SUCCESS:
                    List<String> faildReplaceTags = ledgerSheetFactory.isReplaceCheck();
                    if (!faildReplaceTags.isEmpty()) {
                        // 置換できなかったタグがある
                        StringBuilder sb = new StringBuilder();
                        sb.append(LocaleUtils.getString("key.KanbanOutLedgerNoReplaceComp"));
                        sb.append("\n\r");
                        int showTagSize = faildReplaceTags.size();
                        if (showTagSize >= 10) {
                            showTagSize = 10;
                        }
                        for (int index = 0; showTagSize > index; index++) {
                            sb.append(faildReplaceTags.get(index));
                            sb.append("\r");
                        }
                        if (faildReplaceTags.size() > 10) {
                            sb.append("\r");
                            sb.append("...etc");
                        }
                        showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.KanbanOutLedger") + ":" + data.kanbanName, sb.toString());
                        break;
                    }
                    showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.KanbanOutLedger") + ":" + data.kanbanName, LocaleUtils.getString("key.KanbanOutLedgerSuccess"));
                    break;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //帳票出力エラーに対するダイアログの表示
            showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanOutLedger"), LocaleUtils.getString("key.KanbanOutLedgerApplicationErr"));
        }
    }

    /**
     * 警告メッセージを表示する
     *
     * @param type
     * @param title
     * @param message
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            sc.showAlert(type, title, message);
        });
    }

    /**
     * 工程順IDで工程順を取得する
     *
     * @param workflowid
     * @return
     */
    private WorkflowInfoEntity getWorkflow(Long workflowid) {
        WorkflowInfoFacade facade = new WorkflowInfoFacade();
        return facade.find(workflowid);
    }

    /**
     * カンバンIDでカンバンを取得する
     *
     * @param kanbanId
     * @return
     */
    private KanbanInfoEntity getKanban(Long kanbanId) {
        KanbanInfoFacade facade = new KanbanInfoFacade();
        return facade.find(kanbanId);
    }

    /**
     * カンバンIDで工程カンバンを取得する
     *
     * @param kanbanId
     * @return
     */
    private List<WorkKanbanInfoEntity> getWorkKanbans(Long kanbanId) {
        try {
            List<WorkKanbanInfoEntity> entitys = new ArrayList<>();
            WorkKanbanInfoFacade facade = new WorkKanbanInfoFacade();
            Long workkanbanCnt = facade.countFlow(kanbanId);
            for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                entitys.addAll(facade.getRangeFlow(nowCnt, nowCnt + RANGE - 1, kanbanId));
            }
            return entitys;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * カンバンIDで工程カンバンを取得する
     *
     * @param kanbanId
     * @return
     */
    private List<WorkKanbanInfoEntity> getSeparateWorkKanbans(Long kanbanId) {
        try {
            List<WorkKanbanInfoEntity> entitys = new ArrayList<>();
            WorkKanbanInfoFacade facade = new WorkKanbanInfoFacade();
            Long workkanbanCnt = facade.countSeparate(kanbanId);
            for (long nowCnt = 0; nowCnt < workkanbanCnt; nowCnt += RANGE) {
                entitys.addAll(facade.getRangeSeparate(nowCnt, nowCnt + RANGE - 1, kanbanId));
            }
            return entitys;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * カンバンIDで実績を取得する
     *
     * @param kanbanId
     * @return
     */
    private List<ActualResultEntity> getActualResults(Long kanbanId) {
        try {
            List<ActualResultEntity> entitys = new ArrayList<>();
            ActualResultInfoFacade facade = new ActualResultInfoFacade();
            Long actualMax = facade.searchCount(new ActualSearchCondition().kanbanId(kanbanId));
            for (long nowCnt = 0; nowCnt <= actualMax; nowCnt += RANGE) {
                entitys.addAll(facade.searchRange(new ActualSearchCondition().kanbanId(kanbanId), nowCnt, nowCnt + RANGE - 1));
            }
            return entitys;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * ツリーの親階層生成
     *
     */
    private synchronized void createRoot() {
        logger.debug("createRoot start.");
        try {
            kanbanEditPermanenceData.setKanbanHierarchyRootItem(new TreeItem<>(new KanbanHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.Kanban"))));
            TreeItem<KanbanHierarchyInfoEntity> rootItem = kanbanEditPermanenceData.getKanbanHierarchyRootItem();

            long count = kanbanHierarchyInfoFacade.getTopHierarchyCount();
            rootItem.getValue().setChildCount(count);

            for (long from = 0; from < count; from += RANGE) {
                List<KanbanHierarchyInfoEntity> entities = kanbanHierarchyInfoFacade.getTopHierarchyRange(from, from + RANGE - 1);

                entities.stream().forEach((entity) -> {
                    TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    rootItem.getChildren().add(item);
                });
            }

            Platform.runLater(() -> {
                this.hierarchyTree.rootProperty().setValue(rootItem);
                this.hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new KanbanHierarchyTreeCell());
            });

            rootItem.setExpanded(true);
            rootItem.expandedProperty().addListener(this.changeListener);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            reconnection();
        } finally {
            logger.debug("createRoot end.");
        }
    }

    /**
     * ツリー展開
     *
     * @param parentItem 親階層
     */
    private synchronized void expand(TreeItem<KanbanHierarchyInfoEntity> parentItem) {
        try {
            logger.debug("expand: {}", parentItem.getValue());

            parentItem.getChildren().clear();

            long count = parentItem.getValue().getChildCount();

            for (long from = 0; from < count; from += RANGE) {
                List<KanbanHierarchyInfoEntity> entities = kanbanHierarchyInfoFacade.getAffilationHierarchyRange(parentItem.getValue().getKanbanHierarchyId(), from, from + RANGE - 1);

                entities.stream().forEach((entity) -> {
                    TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    parentItem.getChildren().add(item);
                });
            }

            Platform.runLater(() -> {
                if (0 < count) {
                    if (!parentItem.isExpanded()) {
                        parentItem.setExpanded(true);
                    }
                } else {
                    if (parentItem.isExpanded()) {
                        parentItem.setExpanded(false);
                    }
                }
                selectedTreeItem(parentItem, null);
            });
        }
        catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ツリーデータの再取得 引数parentItemの子を再取得する
     *
     * @param parentItem
     * @param kanbanHierarchyId 再取得後に選択するID
     */
    public void updateTreeItemThread(TreeItem<KanbanHierarchyInfoEntity> parentItem, Long kanbanHierarchyId) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    if (Objects.isNull(parentItem.getParent())) {
                        // ルート階層
                        createRoot();
                        blockUI(false);
                    } else {
                        Platform.runLater(() -> {
                            try {
                                expand(parentItem);
                            } finally {
                                blockUI(false);
                            }
                        });
                    }
                } catch (Exception ex) {
                    Platform.runLater(() -> blockUI(false));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 階層を選択状態にする。
     *
     * @param treeItem
     * @param kanbanHierarchyId 未使用
     */
    public void selectedTreeItem(TreeItem<KanbanHierarchyInfoEntity> treeItem, Long kanbanHierarchyId) {
        //Optional<TreeItem<KanbanHierarchyInfoEntity>> find = treeItem.getChildren().stream().
        //        filter(p -> p.getValue().getKanbanHierarchyId().equals(kanbanHierarchyId)).findFirst();
        //
        //if (find.isPresent()) {
        //    hierarchyTree.getSelectionModel().select(find.get());
        //}
        this.hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new KanbanHierarchyTreeCell());
        Platform.runLater(() -> {
            this.hierarchyTree.getSelectionModel().select(treeItem);
            this.hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new KanbanHierarchyTreeCell());
            this.hierarchyTree.requestFocus();
        });
    }

    /**
     * カンバン検索
     *
     */
    private void searchKanbanData() {
        try {

            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())) {
                return;
            }

            String kanbanName = (Objects.isNull(kanbanNameField.getText()) || "".equals(kanbanNameField.getText())) ? null : kanbanNameField.getText();
            Date scheduleStartDay = (Objects.isNull(startDatePicker.getValue())
                    ? null : DateUtils.getBeginningOfDate(Date.from(startDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
            Date scheduleEndDay = (Objects.isNull(endDatePicker.getValue())
                    ? null : DateUtils.getEndOfDate(Date.from(endDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
            List<KanbanStatusEnum> selectStatusData = new ArrayList<>();
            ObservableList<Integer> indices = statusList.getCheckModel().getCheckedIndices();
            int listCnt = statusList.getCheckModel().getCheckedIndices().size();
            if (statusList.getSelectionModel().getSelectedItems() != null && listCnt != 0) {
                for (int i = 0; i < listCnt; i++) {
                    String status = KanbanStatusEnum.getValueText(indices.get(i));
                    selectStatusData.add(KanbanStatusEnum.getEnum(status));
                }
            } else {
                selectStatusData.addAll(Arrays.asList(KanbanStatusEnum.values()));
            }

            if (Objects.nonNull(scheduleEndDay) && Objects.nonNull(scheduleEndDay)) {
                if (0 > DateUtils.differenceOfDate(formatter.format(scheduleEndDay), formatter.format(scheduleStartDay))) {
                    Platform.runLater(() -> {
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.KanbanSearch"), LocaleUtils.getString("key.DateCompErrMessage"));
                    });
                    return;
                }
            }
            KanbanSearchCondition condition
                    = new KanbanSearchCondition().hierarchyId(hierarchyTree.getSelectionModel().getSelectedItem().getValue().getKanbanHierarchyId())
                    .kanbanName(kanbanName).fromDate(scheduleStartDay).toDate(scheduleEndDay).statusList(selectStatusData);

            ObservableList<DisplayData> tableData = FXCollections.observableArrayList();
            Long count = kanbanInfoFacade.countSearch(condition);

            logger.debug("search data:{}", count);
            countOverFlag = false;
            if (count > searchMax) {
                count = searchMax;
                countOverFlag = true;
            }
            for (long cnt = 0; cnt <= count; cnt += maxLoadSize) {
                List<KanbanInfoEntity> kanbans = kanbanInfoFacade.findSearchRange(condition, cnt, cnt + maxLoadSize - 1);
                if (!kanbans.isEmpty()) {
                    kanbans.stream().forEach((e) -> {
                        tableData.add(new DisplayData(e));
                    });
                }
            }

            Platform.runLater(() -> {
                clearKanbanList();
                kanbanList.setItems(tableData);
                kanbanList.getSortOrder().add(kanbanNameColumn);
                if (countOverFlag) {
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.overRangeSearchTitle"), String.format(LocaleUtils.getString("key.overRangeSearchMessage"), searchMax));
                }
            });

            //フィルター条件の更新
            if (Objects.nonNull(scheduleStartDay)) {
                Integer differenceStateDate = DateUtils.differenceOfDate(formatter.format(scheduleStartDay), formatter.format(new Date()));
                properties.setProperty(SEARCH_FILTER_START_DATE, differenceStateDate.toString());
            } else {
                properties.setProperty(SEARCH_FILTER_START_DATE, "");
            }
            if (Objects.nonNull(scheduleEndDay)) {
                Integer differenceEndDate = DateUtils.differenceOfDate(formatter.format(scheduleEndDay), formatter.format(new Date()));
                properties.setProperty(SEARCH_FILTER_END_DATE, differenceEndDate.toString());
            } else {
                properties.setProperty(SEARCH_FILTER_END_DATE, "");
            }

            StringBuilder sb = new StringBuilder();
            for (KanbanStatusEnum status : selectStatusData) {
                sb.append(status.toString());
                sb.append(COMMA_SPLIT);
            }
            properties.setProperty(SEARCH_FILTER_STATUS, sb.toString());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンリストの生成
     *
     * @param entitys
     */
    private void createKanbanListThread(List<KanbanInfoEntity> entitys) {
        List<DisplayData> datas = new ArrayList<>();
        entitys.stream().map((entity) -> new DisplayData(entity)).forEach((data) -> {
            datas.add(data);
        });

        //TODO:工程順名を取得する処理を追加する
        Platform.runLater(() -> {
            ObservableList<DisplayData> list = FXCollections.observableArrayList(datas);
            kanbanList.setItems(list);
            kanbanList.getSortOrder().add(kanbanNameColumn);
        });
    }

    /**
     * カンバンリストの初期化
     *
     */
    private void clearKanbanList() {
        kanbanList.getItems().clear();
        kanbanList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        kanbanList.getSelectionModel().clearSelection();
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        Progress.setVisible(flg);
    }

    /**
     * キャッシュデータ読み込み用スレッド起動
     *
     */
    private void createCashDataThread() {
        try {
            Long eItemCnt = equipmentFacade.count();
            logger.debug("TopHierarchyCnt:{}", eItemCnt);

            for (long nowItemCnt = 0; nowItemCnt < eItemCnt; nowItemCnt += RANGE) {
                List<EquipmentInfoEntity> entitys = equipmentFacade.findRange(nowItemCnt, nowItemCnt + RANGE - 1);
                entitys.stream().forEach((entity) -> {
                    cashManager.setItem(EquipmentInfoEntity.class, entity.getEquipmentId(), entity);
                });
            }

            Long oItemCnt = organizationFacade.count();
            logger.debug("TopHierarchyCnt:{}", eItemCnt);

            for (long nowItemCnt = 0; nowItemCnt < oItemCnt; nowItemCnt += RANGE) {
                List<OrganizationInfoEntity> entitys = organizationFacade.findRange(nowItemCnt, nowItemCnt + RANGE - 1);
                entitys.stream().forEach((entity) -> {
                    cashManager.setItem(OrganizationInfoEntity.class, entity.getOrganizationId(), entity);
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 再接続処理
     *
     */
    public void reconnection() {
        Platform.runLater(() -> {
            sc.showAlert(Alert.AlertType.ERROR, null, LocaleUtils.getString("key.ServerReconnectMessage"));
            sc.setComponent("ContentNaviPane", "KanbanListCompoELS");
        });
    }

    /**
     *
     * @param uri
     * @return
     */
    public static long getUriToHierarcyId(String uri) {
        long ret = 0;
        try {
            String[] split = uri.split(URI_SPLIT);
            if (split.length == 3) {
                ret = Long.parseLong(split[2]);
            }
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return ret;
    }

    /**
     *
     * @param uri
     * @return
     */
    public static long getUriToKanbanId(String uri) {
        long ret = 0;
        try {
            String[] split = uri.split(URI_SPLIT);
            if (split.length == 2) {
                ret = Long.parseLong(split[1]);
            }
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return ret;
    }
}
