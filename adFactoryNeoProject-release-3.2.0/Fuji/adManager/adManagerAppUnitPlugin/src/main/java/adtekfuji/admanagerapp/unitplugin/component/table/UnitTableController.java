package adtekfuji.admanagerapp.unitplugin.component.table;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this  file, choose Tools | s
 * and open the  in the editor.
 */
import adtekfuji.admanagerapp.unitplugin.component.UnitListCompoInterface;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javax.xml.bind.JAXBException;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.clientservice.UriConvertUtils;
import jp.adtekfuji.forfujiapp.entity.unit.ConUnitAssociateInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.forfujiapp.utils.CheckerUtilEntity;
import jp.adtekfuji.forfujiapp.utils.UnitCheckerUtils;
import jp.adtekfuji.forfujiapp.utils.WorkKanbanTimeReplaceUtils;
import jp.adtekfuji.forfujiapp.utils.WorkflowProcess;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテーブル
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTableController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final UnitListCompoInterface listCompoInterface;
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    @FXML
    private PropertySaveTableView<UnitTableDataEntity> unitTable;
    @FXML
    private TableColumn unitColumn;
    @FXML
    private TableColumn outsetColumn;
    @FXML
    private TableColumn deliveryColumn;
    @FXML
    private TableColumn updateByColumn;
    @FXML
    private TableColumn updateDateColumn;
    @FXML
    private TableColumn statusColumn;
    @FXML
    private Button craeateButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ToggleButton visibleCompletedButton;
    
    private Long currentHierarchyId;
    
    private ObservableList<UnitTableDataEntity> allDatum = FXCollections.observableArrayList();

    public UnitTableController(UnitListCompoInterface listCompoInterface) {
        this.listCompoInterface = listCompoInterface;
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(UnitTableController.class.getName() + ":initialize start");

        unitTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        this.unitColumn.setCellValueFactory(new PropertyValueFactory("name"));
        this.outsetColumn.setCellValueFactory(new PropertyValueFactory("outsetDateTime"));
        this.deliveryColumn.setCellValueFactory(new PropertyValueFactory("deliveryDateTime"));
        this.updateByColumn.setCellValueFactory(new PropertyValueFactory("updatePersonName"));
        this.updateDateColumn.setCellValueFactory(new PropertyValueFactory("updateDatetime"));
        this.statusColumn.setCellValueFactory(new PropertyValueFactory("status"));
        this.unitTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.unitTable.init("unitTable");

        //工程順ダブルクリック
        this.unitTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                onEditButton(new ActionEvent());
            }
        });
        
        //未完了ユニット表示
        this.visibleCompletedButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                blockUI(true);
                new Thread(new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            updateTable(currentHierarchyId);
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                        }
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        blockUI(false);
                    }

                    @Override
                    protected void failed() {
                        super.failed();
                        blockUI(false);
                    }
                }).start();
            }
            else {
                //ON→OFFのときは再取得ではなくフィルターを行う
                ObservableList<UnitTableDataEntity> filtered = allDatum.stream()
                        .filter(entity -> Objects.equals(entity.getStatus(), LocaleUtils.getString("key.Incomplete")))
                        .collect(Collectors.collectingAndThen(Collectors.toList(), l -> FXCollections.observableArrayList(l)));
                
                unitTable.setItems(filtered);
                unitTable.getSortOrder().add(unitColumn);  
            }
        });

        logger.info(UnitTableController.class.getName() + ":initialize end");
    }

    /**
     * 新規作成ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onCreateButton(ActionEvent event) {
        logger.info(UnitTableController.class.getName() + ":onCreateButton start");

        blockUI(true);
        ButtonType btn = ButtonType.CANCEL;
        try {
            UnitHierarchyInfoEntity hierarchy = this.listCompoInterface.getSelectTree();
            if (Objects.nonNull(hierarchy) && Objects.nonNull(hierarchy.getParentId())) {
                UnitInfoEntity info = new UnitInfoEntity();
                info.setParentId(hierarchy.getUnitHierarchyId());
                info.setParentName(hierarchy.getHierarchyName());

                // 新規作成ダイアログを表示する。
                btn = this.dispCreateDialog(info);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        } finally {
            if (!ButtonType.OK.equals(btn)) {
                blockUI(false);
            }
        }

        logger.info(UnitTableController.class.getName() + ":onCreateButton end");
    }

    /**
     * 新規作成ダイアログを表示する。
     *
     * @param unitInfo
     */
    private ButtonType dispCreateDialog(UnitInfoEntity unitInfo) {
        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "UnitDetailDialog", unitInfo);
        if (!ret.equals(ButtonType.OK)) {
            return ret;
        }

        CheckerUtilEntity check = UnitCheckerUtils.isEmptyUnit(unitInfo);
        if (check.isSuccsess()) {
            // 生産ユニットの新規作成処理
            unitInfo.setFkUpdatePersonId(loginUserInfoEntity.getId());
            unitInfo.setUpdateDatetime(new Date());

            this.createItemThread(unitInfo);
        } else {
            sc.showAlert(check.getAlertType(), LocaleUtils.getString(check.getErrTitle()), LocaleUtils.getString(check.getErrMessage()));

            // 新規作成ダイアログを再表示する。
            ret = this.dispCreateDialog(unitInfo);
        }
        return ret;
    }

    /**
     * 新規作成処理
     *
     */
    private void createItemThread(UnitInfoEntity unitInfo) {
        final UnitInfoEntity registData = unitInfo;
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                String uri = null;
                try {
                    registData.entityUpdate();
                    ResponseEntity entity = RestAPI.registUnit(registData);
                    if (entity.isSuccess()) {
                        uri = entity.getUri();
                    } else {
                        throw new Exception(entity.getErrorType().name());
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    throw ex;
                }
                return uri;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    String uri = this.get();
                    if (Objects.isNull(uri)) {
                        return;
                    }

                    // 詳細画面表示
                    UnitInfoEntity arg = RestAPI.getUnit(UriConvertUtils.getUriToId(uri));
                    updateItemTimes(arg);
                    sc.setComponent("ContentNaviPane", "UnitDetailComp", arg);
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
            }

            @Override
            protected void failed() {
                super.failed();
                try {
                    String err = this.getException().getMessage();
                    if (ServerErrorTypeEnum.INVALID_ARGUMENT.name().equals(err)) {
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.warn.unitKanbanNameIsLong"));
                    }

                    // 新規作成ダイアログを再表示する。
                    dispCreateDialog(registData);
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
            }
        };
        new Thread(task).start();
    }

    /**
     * ユニットの子の更新処理
     *
     * @param parentUnit
     */
    private void updateItemTimes(UnitInfoEntity parentUnit) throws JAXBException {
        Date startTime = parentUnit.getStartDatetime();
        Date parentCompTime = parentUnit.getStartDatetime();
        Date farstWorkTime = null;
        parentUnit.getConUnitAssociateCollection().sort(Comparator.comparing(con -> con.getUnitAssociateOrder()));
        for (ConUnitAssociateInfoEntity conUnit : parentUnit.getConUnitAssociateCollection()) {
            if (Objects.nonNull(conUnit.getFkKanbanId())) {
                KanbanInfoEntity kanban = RestAPI.getKanban(conUnit.getFkKanbanId());
                parentUnit.getUnitPropertyCollection().stream().forEach((unitProp) -> {
                    kanban.getPropertyCollection().stream().filter((kanbanProp) -> (unitProp.getUnitPropertyName().equals(kanbanProp.getKanbanPropertyName()))).forEach((kanbanProp) -> {
                        kanbanProp.setKanbanPropertyValue(unitProp.getUnitPropertyValue());
                    });
                });
                if (Objects.isNull(farstWorkTime)) {
                    updateKanbanTime(kanban, startTime);
                    farstWorkTime = kanban.getStartDatetime();
                } else {
                    // 第一工程目以降は最初の工程の時間と実施する工程の時間の差分をだし現在時刻にタス
                    updateKanbanTime(kanban, new Date(
                            startTime.getTime() + (kanban.getStartDatetime().getTime() - farstWorkTime.getTime())));
                }
                if (parentCompTime.before(kanban.getCompDatetime())) {
                    parentCompTime = kanban.getCompDatetime();
                }
            } else if (Objects.nonNull(conUnit.getFkUnitId())) {
                UnitInfoEntity unit = RestAPI.getUnit(conUnit.getFkUnitId());
                // 第一工程の時間は親ユニットの開始時間に合わせる
                if (Objects.isNull(farstWorkTime)) {
                    unit.setStartDatetime(startTime);
                    unit.setCompDatetime(new Date(
                            startTime.getTime() + (unit.getCompDatetime().getTime() - unit.getStartDatetime().getTime())));
                    farstWorkTime = unit.getStartDatetime();
                } else {
                    // 第一工程目以降は最初の工程の時間と実施する工程の時間の差分をだし現在時刻にタス
                    unit.setStartDatetime(new Date(
                            startTime.getTime() + (unit.getStartDatetime().getTime() - farstWorkTime.getTime())));
                    unit.setCompDatetime(new Date(
                            unit.getStartDatetime().getTime() + (unit.getCompDatetime().getTime() - unit.getStartDatetime().getTime())));
                }
                updateItemTimes(unit);
                if (parentCompTime.before(unit.getCompDatetime())) {
                    parentCompTime = unit.getCompDatetime();
                }
            }
        }
        parentUnit.setCompDatetime(parentCompTime);
        RestAPI.updateUnit(parentUnit);
    }

    /**
     * カンバン更新処理
     *
     */
    private void updateKanbanTime(KanbanInfoEntity kanban, Date startTime) throws JAXBException {
        kanban.getWorkKanbanCollection().addAll(RestAPI.getWorkKanbans(kanban.getKanbanId()));
        kanban.getSeparateworkKanbanCollection().addAll(RestAPI.getSeparateWorkKanbans(kanban.getKanbanId()));
        // カンバンのワークフロー取得
        WorkflowProcess workflowProcess = new WorkflowProcess(RestAPI.getWorkflow(kanban.getFkWorkflowId()).getWorkflowDiaglam());
        List<BreakTimeInfoEntity> breakTimeInfoEntitys = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanban.getWorkKanbanCollection());
        workflowProcess.setBaseTime(kanban, breakTimeInfoEntitys, startTime);
        RestAPI.updateKanban(kanban);
    }

    /**
     * 編集ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onEditButton(ActionEvent event) {
        logger.info(UnitTableController.class.getName() + ":onEditButton start");

        try {
            if (Objects.isNull(unitTable.getSelectionModel().getSelectedItem())) {
                return;
            }

            UnitInfoEntity selected = unitTable.getSelectionModel().getSelectedItem().getUnitInfoEntity();
            UnitHierarchyInfoEntity hierarchy = this.listCompoInterface.getSelectTree();
            if (Objects.nonNull(selected) && Objects.nonNull(hierarchy)) {
                // ツリーテーブル画面に移動
                Platform.runLater(() -> {
                    sc.setComponent("ContentNaviPane", "UnitDetailComp",
                            this.unitTable.getSelectionModel().getSelectedItem().getUnitInfoEntity());
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(UnitTableController.class.getName() + ":onEditButton end");
    }

    /**
     * 削除ボタン押下時処理
     *
     * @param event
     */
    @FXML
    public void onDeleteButton(ActionEvent event) {
        logger.info(UnitTableController.class.getName() + ":onDeleteButton start");

        try {
            if (Objects.isNull(unitTable.getSelectionModel().getSelectedItem())) {
                return;
            }

            UnitInfoEntity item = unitTable.getSelectionModel().getSelectedItem().getUnitInfoEntity();
            UnitHierarchyInfoEntity hierarchy = this.listCompoInterface.getSelectTree();

            if (Objects.nonNull(item) && Objects.nonNull(hierarchy)) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getUnitName());

                if (ret.equals(ButtonType.OK)) {
                    Task<Integer> task = new Task<Integer>() {
                        @Override
                        protected Integer call() throws Exception {
                            try {
                                Platform.runLater(() -> blockUI(true));

                                // ユニットを削除
                                ResponseEntity res = RestAPI.deleteUnit(item);

                                if (Objects.nonNull(res) && res.isSuccess()) {
                                    Platform.runLater(() -> listCompoInterface.updateTable(hierarchy));
                                }
                            } finally {
                                Platform.runLater(() -> blockUI(false));
                            }
                            return 0;
                        }
                    };
                    new Thread(task).start();
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(UnitTableController.class.getName() + ":onDeleteButton end");
    }

    /**
     * ユニットテーブルの更新
     *
     * @param hierarchyId 表示する情報
     */
    public void updateTable(Long hierarchyId) {
        logger.info(UnitTableController.class.getName() + ":updateTable start");

        currentHierarchyId = hierarchyId;
        List<UnitInfoEntity> units = new ArrayList<>();
        if (Objects.nonNull(currentHierarchyId)) {
            //作成画面などから帰ってきたときボタンが作成されてないことがあるためnullの場合はfalse
            units = RestAPI.getUnitByHierarchyId(hierarchyId, Objects.isNull(visibleCompletedButton)
                    ? false
                    : visibleCompletedButton.selectedProperty().getValue());
        }
        
        List<UnitTableDataEntity> list = new ArrayList<>();

        for (UnitInfoEntity unit : units) {
            UnitTableDataEntity data = new UnitTableDataEntity(unit);

            if (Objects.nonNull(unit.getFkUpdatePersonId())) {
                OrganizationInfoEntity organization = CacheUtils.getCacheOrganization(unit.getFkUpdatePersonId());
                data.setUpdatePersonName(organization.getOrganizationName());
            }

            list.add(data);
        }

        Platform.runLater(() -> {
            allDatum = FXCollections.observableArrayList(list);
            unitTable.setItems(FXCollections.observableArrayList(list));
            unitTable.getSortOrder().add(unitColumn);
            logger.info(UnitTableController.class.getName() + ":updateTable end");
        });
    }

    /**
     * ユニットテーブルの初期化
     *
     */
    public void clearTableList() {
        Platform.runLater(() -> {
            allDatum.clear();
            currentHierarchyId = null;
            unitTable.getItems().clear();
            unitTable.getSelectionModel().clearSelection();
        });
    }

    /**
     * 待機状態にする。
     *
     * @param block
     */
    public void blockUI(boolean block) {
        this.listCompoInterface.blockUI(block);
    }
}
