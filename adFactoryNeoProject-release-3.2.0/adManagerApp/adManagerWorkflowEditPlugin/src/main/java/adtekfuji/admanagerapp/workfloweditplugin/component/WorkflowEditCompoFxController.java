/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.admanagerapp.workfloweditplugin.common.Constants;
import adtekfuji.admanagerapp.workfloweditplugin.common.SelectedWorkflowAndHierarchy;
import adtekfuji.admanagerapp.workfloweditplugin.common.UriConvertUtils;
import adtekfuji.admanagerapp.workfloweditplugin.common.WorkflowHierarchyEditor;
import adtekfuji.admanagerapp.workfloweditplugin.entity.WorkflowListTableDataEntity;
import adtekfuji.clientservice.AccessHierarchyInfoFacade;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.WorkflowHierarchyInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.rest.RestClient;
import adtekfuji.utility.StringUtils;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import javafx.application.Platform;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.approval.ApprovalInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowSeparateworkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowDataCheckInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.WorkflowEditPermanenceData;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.controls.ResettableTextField;
import jp.adtekfuji.javafxcommon.dialog.MessageDialog;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum;
import jp.adtekfuji.javafxcommon.selectcompo.AccessAuthSettingEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程順編集コンポーネント
 *
 * @author ta.ito
 */
@FxComponent(id = "WorkflowEditCompo", fxmlPath = "/fxml/compo/workflow_list_compo.fxml")
public class WorkflowEditCompoFxController implements Initializable, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static WorkflowHierarchyInfoFacade workflowHierarchyInfoFacade = new WorkflowHierarchyInfoFacade();
    private final static WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private final static  Map<Long, TreeItem> treeItems = new HashMap<>();

    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final WorkflowEditPermanenceData permanenceData = WorkflowEditPermanenceData.getInstance();
    private WorkflowHierarchyEditor workflowHierarchyEditor;

    private final static long ROOT_ID = 0;

    /**
     * 承認機能オプションが有効か
     */
    private final boolean isLicensedApproval = ClientServiceProperty.isLicensed(LicenseOptionType.ApprovalOption.getName());

    /**
     * 承認WebページのURL
     */
    private String approvalWebURL;

    @FXML
    private SplitPane workflowPane;
    @FXML
    private TreeView<WorkflowHierarchyInfoEntity> hierarchyTree;
    @FXML
    private ToolBar hierarchyBtnArea;
    @FXML
    private Button delTreeButton;
    @FXML
    private Button editTreeButton;
    @FXML
    private Button createTreeButton;
    @FXML
    private Button moveTreeButton;
    @FXML
    private Button authButton;
    @FXML
    private VBox detailPane;
    @FXML
    private Button delListButton;
    @FXML
    private Button copyListButton;
    @FXML
    private Button editListButton;
    @FXML
    private Button createListButton;
    @FXML
    private Button moveListButton;
    @FXML
    private TableColumn workColumn;
    @FXML
    private TableColumn updateByColumn;
    @FXML
    private TableColumn updateDateColumn;

    /**
     * 最新のみ表示チェックボックス
     */
    @FXML
    private CheckBox latestCheckBox;

    /**
     * 承認/申請状況ボタン
     */
    @FXML
    private Button approveButton;

    /**
     * 承認列
     */
    @FXML
    private TableColumn approvalStateColumn;

    /**
     * 承認日時列
     */
    @FXML
    private TableColumn approvalDatetimeColumn;

    @FXML
    private Pane workflowProgress;
    
    @FXML
    private PropertySaveTableView<WorkflowListTableDataEntity> workflowList;

    @FXML
    private HBox approvePane;

    /**
     * 検索フィールド
     */
    @FXML
    private ResettableTextField searchField;

    /**
     * 工程順編集コンポーネントを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        workflowList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        permanenceData.updateTitle();

        SplitPaneUtils.loadDividerPosition(this.workflowPane, getClass().getSimpleName());
        
        delListButton.addEventHandler( MouseEvent.MOUSE_PRESSED , this::onListDelete );

        //役割の権限によるボタン無効化.
        if (!this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
            this.delTreeButton.setDisable(true);
            this.editTreeButton.setDisable(true);
            this.createTreeButton.setDisable(true);
            this.moveTreeButton.setDisable(true);
            this.authButton.setDisable(true);
            this.delListButton.setDisable(true);
            this.copyListButton.setDisable(true);
            this.editListButton.setDisable(true);
            this.createListButton.setDisable(true);
            this.moveListButton.setDisable(true);
        } else {
            WorkflowEditButton(true);
        }

        if (!this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.RIGHT_ACCESS)) {
            this.hierarchyBtnArea.getItems().remove(this.authButton);
            this.moveTreeButton.setDisable(true);
        }

        this.workflowProgress.setVisible(false);

        //エンティティメンバーとバインド
        this.workColumn.setCellValueFactory(new PropertyValueFactory("workflowName"));
        //editionColumn.setCellValueFactory(new PropertyValueFactory("workflowRevision"));// 使いどころがないため、版名を削除
        this.updateByColumn.setCellValueFactory(new PropertyValueFactory("updatePersonName"));
        this.updateDateColumn.setCellValueFactory(new PropertyValueFactory("updateDatetime"));
        this.approvalStateColumn.setCellValueFactory(new PropertyValueFactory("approvalState"));
        this.approvalDatetimeColumn.setCellValueFactory(new PropertyValueFactory("approvalDatetime"));
        this.workflowList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.workflowList.init("workflowList");

        this.latestCheckBox.setSelected(true);

        if (!isLicensedApproval) {
            // 承認機能オプションが無効
            this.approvePane.setVisible(false);
            this.approvePane.setManaged(false);
            this.approvalStateColumn.setVisible(false);
            this.approvalDatetimeColumn.setVisible(false);
        } else {
            // 承認機能オプションが有効
            this.approveButton.setVisible(true);
            this.approveButton.setDisable(true);
            if (!this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.APPROVAL_KANBAN)) {
                this.approveButton.setText(LocaleUtils.getString("key.ApprovalCondition"));
            }

            this.approvalStateColumn.setVisible(true);
            this.approvalDatetimeColumn.setVisible(true);

            // 承認WebページのURLを取得
            this.approvalWebURL
                    = AdProperty.getProperties().getProperty(Constants.SERVER_ADDRESS_KEY, Constants.SERVER_ADDRESS_DEFAULT)
                    + AdProperty.getProperties().getProperty(Constants.APPROVAL_WEB_URL_KEY, Constants.APPROVAL_WEB_URL_DEFAULT)
                    + "&open=" + Constants.URL_OPEN_KEY;
        }

        // 階層ツリー選択時処理
        this.hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<WorkflowHierarchyInfoEntity>> observable, TreeItem<WorkflowHierarchyInfoEntity> oldValue, TreeItem<WorkflowHierarchyInfoEntity> newValue) -> {
            if (StringUtils.isEmpty(this.searchField.getText())) {
                if (Objects.nonNull(newValue) && newValue.getValue().getWorkflowHierarchyId() != ROOT_ID) {
                    // 工程リスト更新
                    this.createWorkflowList(newValue.getValue().getWorkflowInfoCollection());
                    this.permanenceData.setSelectedWorkflowHierarchy(newValue);
                    WorkflowEditButton(true);
                } else {
                    // 工程リストクリア
                    Platform.runLater(() -> this.clearWorkList());
                    this.permanenceData.setSelectedWorkflowHierarchy(null);
                }
            } else {
                this.onSearch(null);
            }

            workflowList.setRowFactory(tableView -> {
                TableRow<WorkflowListTableDataEntity> row = new TableRow<WorkflowListTableDataEntity>() {
                    /**
                     * セルの内容を更新する
                     *
                     * @param item セルの新しいアイテム
                     * @param empty このセルに割り当てられるデータが空かどうか（空:true、空でない:false）
                     */
                    @Override
                    protected void updateItem(WorkflowListTableDataEntity item, boolean empty) {
                        super.updateItem(item, empty);
                        if (Objects.nonNull(item)) {
                            // コンテキストメニューを表示する
                            createContextMenu(item.getWorkflowInfoEntity()).ifPresent(this::setContextMenu);
                        }
                    }
                };

                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        // 工程順編集画面を開く
                        this.onListEdit(new ActionEvent());
                    }
                });

                return row ;
            });
        });

        // 最新のみを表示チェックボックスチェック時処理
        this.latestCheckBox.setOnAction((ActionEvent event) -> {
            if (StringUtils.isEmpty(this.searchField.getText())) {
                TreeItem<WorkflowHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
                if (Objects.nonNull(item) && Objects.nonNull(item.getParent())) {
                    this.createWorkflowList(item.getValue().getWorkflowInfoCollection());
                }
            } else {
                this.onSearch(null);
            }
        });
       
        //工程順テーブル行ダブルクリック時処理
        //this.workflowList.setOnMouseClicked((MouseEvent event) -> {
        //    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
        //        this.onListEdit(new ActionEvent());
        //    }
        //});

        //工程順テーブルの選択行変更時処理
        this.workflowList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<WorkflowListTableDataEntity> selectedWorks = this.workflowList.getSelectionModel().getSelectedItems();
                if (selectedWorks.isEmpty()) {
                    return;
                }
                
                // 削除ボタンの有効/無効状態を切り替え
                boolean delListButtonDisabled = true;
                if (!isLicensedApproval) {
                    // 承認機能オプションが無効
                    if (this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
                        delListButtonDisabled = false;
                    }
                } else {
                    // 承認機能オプションが有効

                    // 申請中の件数を取得
                    long applyCount = selectedWorks.stream()
                            .map(p -> p.getWorkflowInfoEntity())
                            .filter(p -> ApprovalStatusEnum.APPLY.equals(p.getApprovalState()))
                            .count();

                    if (this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW) && applyCount == 0) {
                        // 編集権限あり、かつ、申請中のデータがないなら表示(有効)
                        delListButtonDisabled = false;
                    }
                }
                WorkflowEditButton(delListButtonDisabled);

                // 承認/申請状況ボタンの有効/無効状態を切り替え
                boolean approveButtonDisabled = true;
                if (selectedWorks.size() == 1) {
                    WorkflowInfoEntity selectedItem = selectedWorks.get(0).getWorkflowInfoEntity();

                    if (isLicensedApproval) {
                        // 承認機能オプションが有効
                        if (Objects.nonNull(selectedItem.getApprovalId())) {
                            // 申請IDが存在する
                            if (!ApprovalStatusEnum.UNAPPROVED.equals(selectedItem.getApprovalState())) {
                                // 未承認以外
                                ApprovalInfoEntity approvalInfo = selectedItem.getApproval();
                                if (!this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.APPROVAL_KANBAN)) {
                                    // 承認権限がない場合は、ログインユーザ＝申請者なら表示(有効)
                                    if (this.loginUserInfoEntity.getId().equals(approvalInfo.getRequestorId())) {
                                        approveButtonDisabled = false;
                                    }
                                } else {
                                    // 承認権限がある場合は、ログインユーザが承認フローに存在するか、または、ログインユーザ＝申請者なら表示(有効)
                                    long approvalFlowCount = approvalInfo.getApprovalFlows().stream()
                                            .filter(p -> this.loginUserInfoEntity.getId().equals(p.getApproverId()))
                                            .count();
                                    if (approvalFlowCount > 0 || this.loginUserInfoEntity.getId().equals(approvalInfo.getRequestorId())) {
                                        approveButtonDisabled = false;
                                    }
                                }
                            }
                        }
                    }
                }
                this.approveButton.setDisable(approveButtonDisabled);
            }
        });


        // 検索リセット
        this.searchField.getReseyButton().addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
            TreeItem<WorkflowHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && item.getValue().getWorkflowHierarchyId() != ROOT_ID) {
                this.createWorkflowList(item.getValue().getWorkflowInfoCollection());
            } else {
                Platform.runLater(() -> this.clearWorkList());
            }
            this.searchField.getTextField().clear();
        });
        
        // 検索
        this.searchField.getTextField().addEventFilter(KeyEvent.KEY_RELEASED, (event) -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                this.onSearch(null);
            }
        });
        
        // キャッシュ情報取得 ツリー情報表示
        this.blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    TreeItem<WorkflowHierarchyInfoEntity> rootItem = permanenceData.getWorkflowHierarchyRootItem();
                    TreeItem<WorkflowHierarchyInfoEntity> selectedItem = permanenceData.getSelectedWorkflowHierarchy();

                    rootItem.getChildren().clear();

                    workflowHierarchyEditor = new WorkflowHierarchyEditor(hierarchyTree, rootItem, workflowProgress, treeItems, false);
                    workflowHierarchyEditor.createRoot(Objects.nonNull(selectedItem) ? selectedItem.getValue().getWorkflowHierarchyId() : null);

                } finally {
                    Platform.runLater(() -> blockUI(false));
                }
                return null;
            }
        };
        new Thread(task).start();
  
    }

    /**
     * キー押下処理
     * 
     * @param event キーイベント
     */
    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (KeyCode.F5.equals(event.getCode())) {
            event.consume();
            if (Objects.equals(event.getSource(), this.workflowList)) {
                TreeItem<WorkflowHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
                if (Objects.nonNull(item)) {
                    this.updateListItemThread(item, null);
                    return;
                }
            }
            
            this.updateTree();
        }
    }

    /**
     * 工程順階層を更新する。
     */
    private void updateTree() {
        this.blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    TreeItem<WorkflowHierarchyInfoEntity> selectedItem = hierarchyTree.getSelectionModel().getSelectedItem();
                    workflowHierarchyEditor.createRoot(Objects.nonNull(selectedItem) ? selectedItem.getValue().getWorkflowHierarchyId() : null);
                } finally {
                    Platform.runLater(() -> { 
                        blockUI(false);
                        hierarchyTree.requestFocus();
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onTreeDelete(ActionEvent event) {
        try {
            TreeItem<WorkflowHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && Objects.nonNull(item.getParent())) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getValue().getHierarchyName());
                if (ret.equals(ButtonType.OK)) {
                    ResponseEntity res = workflowHierarchyInfoFacade.removeHierarchy(item.getValue());
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {

                        this.workflowHierarchyEditor.selectedTreeItem(item.getParent(), null);
                        this.workflowHierarchyEditor.remove(item.getParent(), item);

                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistOrderProcesses"),
                                String.format(LocaleUtils.getString("key.FailedToDelete"), LocaleUtils.getString("key.OrderProcessesHierarch")));
                    }
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
    private void onTreeEdit(ActionEvent event) {
        try {
            TreeItem<WorkflowHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && item.getValue().getWorkflowHierarchyId() != 0) {
                String orgName = item.getValue().getHierarchyName();
                String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
                String hierarchyName = sc.showTextInputDialog(LocaleUtils.getString("key.Edit"), message, LocaleUtils.getString("key.HierarchyName"), orgName);
                if (Objects.isNull(hierarchyName)) {
                    return;
                } else if (hierarchyName.isEmpty()) {
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                    return;
                }

                if (!orgName.equals(hierarchyName)) {
                    item.getValue().setHierarchyName(hierarchyName);
                    ResponseEntity res = workflowHierarchyInfoFacade.updateHierarchy(item.getValue());
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        WorkflowHierarchyInfoEntity value = workflowHierarchyInfoFacade.find(item.getValue().getWorkflowHierarchyId());
                        item.setValue(null);
                        item.setValue(value);

                        this.workflowHierarchyEditor.selectedTreeItem(item, null);

                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistOrderProcesses"),
                                String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.OrderProcessesHierarch")));

                        // データを戻す
                        item.getValue().setHierarchyName(orgName);
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                        // 排他バージョンが異なる。
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistOrderProcesses"), LocaleUtils.getString("key.alert.differentVerInfo"));

                        WorkflowHierarchyInfoEntity value = workflowHierarchyInfoFacade.find(item.getValue().getWorkflowHierarchyId());
                        item.setValue(null);
                        item.setValue(value);
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
     *
     * @param event
     */
    @FXML
    private void onTreeCreate(ActionEvent event) {
        try {
            TreeItem<WorkflowHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item)) {
                String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
                String hierarchyName = sc.showTextInputDialog(LocaleUtils.getString("key.NewCreate"), message, LocaleUtils.getString("key.HierarchyName"), "");
                if (Objects.isNull(hierarchyName)) {
                    return;
                } else if (hierarchyName.isEmpty()) {
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                    return;
                }

                WorkflowHierarchyInfoEntity hierarchy = new WorkflowHierarchyInfoEntity();
                hierarchy.setHierarchyName(hierarchyName);
                hierarchy.setParentId(item.getValue().getWorkflowHierarchyId());

                if (!item.isExpanded()) {
                    item.setExpanded(true);
                    if (0 < item.getValue().getChildCount()) {
                        this.workflowHierarchyEditor.expand(item, null);
                    }
                }

                ResponseEntity res = workflowHierarchyInfoFacade.registHierarchy(hierarchy);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {

                    hierarchy = workflowHierarchyInfoFacade.findURI(res.getUri());
                    TreeItem<WorkflowHierarchyInfoEntity> newItem = this.workflowHierarchyEditor.add(item, hierarchy);
                    this.workflowHierarchyEditor.selectedTreeItem(newItem, null);

                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistOrderProcesses"),
                            String.format(LocaleUtils.getString("key.FailedToCreate"), LocaleUtils.getString("key.OrderProcessesHierarch")));
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程順階層を別の工程順階層に移動させる処理
     *
     * @param event
     */
    @FXML
    private void onTreeMove(ActionEvent event) {
        try {
            TreeItem<WorkflowHierarchyInfoEntity> selectedItem = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedItem)) {
                return;
            } else if (selectedItem.getValue().getWorkflowHierarchyId().equals(ROOT_ID)) {
                return;
            }
            WorkflowHierarchyInfoEntity selected = selectedItem.getValue();
            sc.blockUI(true);
            this.hierarchyTree.setVisible(false);
            this.workflowList.setVisible(false);

            TreeItem<WorkflowHierarchyInfoEntity> parentTreeItem = selectedItem.getParent();
            //移動先として自分を表示させないように一時削除
            int idx = parentTreeItem.getChildren().indexOf(selectedItem);
            parentTreeItem.getChildren().remove(selectedItem);

            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(this.hierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"), selectedItem);
            treeDialogEntity.setIsUseHierarchy(Boolean.TRUE);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "WorkflowHierarchyTreeCompo", treeDialogEntity);

            TreeItem<WorkflowHierarchyInfoEntity> dest = (TreeItem<WorkflowHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (ret.equals(ButtonType.OK) && Objects.nonNull(dest)) {
                logger.debug(treeDialogEntity.getTreeSelectedItem());
                selected.setParentId(dest.getValue().getWorkflowHierarchyId());

                if (!dest.isExpanded()) {
                    dest.setExpanded(true);
                    if (0 < dest.getValue().getChildCount()) {
                        this.workflowHierarchyEditor.expand(dest, null);
                    }
                }

                ResponseEntity res = workflowHierarchyInfoFacade.updateHierarchy(selected);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    WorkflowHierarchyInfoEntity value = workflowHierarchyInfoFacade.find(selected.getWorkflowHierarchyId());
                    selectedItem.setValue(null);
                    selectedItem.setValue(value);

                    // 階層を追加して、選択状態にする。
                    TreeItem<WorkflowHierarchyInfoEntity> newItme = this.workflowHierarchyEditor.add(dest, selectedItem);
                    this.workflowHierarchyEditor.selectedTreeItem(newItme, null);
                } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistOrderProcesses"),
                            String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.OrderProcessesHierarch")));

                    //一時削除したデータを元に戻す
                    parentTreeItem.getChildren().add(idx, selectedItem);
                } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                    // 排他バージョンが異なる。
                    //sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistOrderProcesses"), LocaleUtils.getString("key.alert.differentVerInfo"));

                    this.updateTree();
                } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)) {
                    // 移動不可能な階層だった場合、警告表示して階層ツリーを再取得して表示しなおす。
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.unmovableHierarchy"));
                    this.updateTree();
                } else {
                    //一時削除したデータを元に戻す
                    parentTreeItem.getChildren().add(idx, selectedItem);
                }
            } else {
                //一時削除したデータを元に戻す
                parentTreeItem.getChildren().add(idx, selectedItem);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            sc.blockUI(false);
            hierarchyTree.setVisible(true);
            workflowList.setVisible(true);
        }
    }

    /**
     * アクセス権設定
     *
     * @param event アクセス権設定ボタン押下
     */
    @FXML
    private void onAuth(ActionEvent event) {
        try {
            TreeItem<WorkflowHierarchyInfoEntity> selectedItem = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedItem)) {
                return;
            } else if (selectedItem.getValue().getWorkflowHierarchyId().equals(ROOT_ID)) {
                return;
            }
            WorkflowHierarchyInfoEntity selected = selectedItem.getValue();
            //ダイアログに表示させるデータを設定
            AccessHierarchyTypeEnum type = AccessHierarchyTypeEnum.WorkflowHierarchy;
            long id = selected.getWorkflowHierarchyId();
            AccessHierarchyInfoFacade accessHierarchyInfoFacade = new AccessHierarchyInfoFacade();
            long count = accessHierarchyInfoFacade.getCount(type, id);
            long range = 100;
            List<OrganizationInfoEntity> deleteList = new ArrayList();
            for (long from = 0; from <= count; from += range) {
                List<OrganizationInfoEntity> entities = accessHierarchyInfoFacade.getRange(type, id, from, from + range - 1);
                deleteList.addAll(entities);
            }
            AccessAuthSettingEntity accessAuthSettingEntity
                    = new AccessAuthSettingEntity(selected.getHierarchyName(), deleteList);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.EditedAuth"), "AccessAuthSettingCompo", accessAuthSettingEntity);
            if (ret.equals(ButtonType.OK)) {
                List<OrganizationInfoEntity> registList = accessAuthSettingEntity.getAuthOrganizations();
                for (int i = 0; i < registList.size(); i++) {
                    OrganizationInfoEntity o = registList.get(i);
                    if (deleteList.contains(o)) {
                        deleteList.remove(o);
                        registList.remove(o);
                        i--;
                    }
                }
                if (!deleteList.isEmpty()) {
                    accessHierarchyInfoFacade.delete(type, id, deleteList);
                }
                if (!registList.isEmpty()) {
                    accessHierarchyInfoFacade.regist(type, id, registList);
                }

                // 工程順階層を再構築
                this.blockUI(true);
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            TreeItem<WorkflowHierarchyInfoEntity> rootItem = permanenceData.getWorkflowHierarchyRootItem();
                            rootItem.getChildren().clear();
                            workflowHierarchyEditor.createRoot(id);
                        } finally {
                            Platform.runLater(() -> blockUI(false));
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 承認/申請状況ボタン押下時のアクション
     *
     * @param event イベント
     */
    @FXML
    private void onApproveButton(ActionEvent event) {
        try {
            WorkflowInfoEntity item = this.workflowList.getSelectionModel().getSelectedItem().getWorkflowInfoEntity();
            if (Objects.nonNull(item) && Objects.nonNull(item.getApprovalId())) {
                logger.info("onApproveButton: loginId={}, item={}", this.loginUserInfoEntity.getLoginId(), item);

                // 承認画面のURLを指定して、デフォルトブラウザを起動
                Desktop desktop = Desktop.getDesktop();
                String uriString = String.format(this.approvalWebURL,
                        item.getApprovalId(), RestClient.encode(this.loginUserInfoEntity.getLoginId()));
                desktop.browse(new URI(uriString));
            }
        } catch (IOException | URISyntaxException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onListDelete(MouseEvent event) {
        logger.info("onListDelete");

        List<WorkflowListTableDataEntity> datas = this.workflowList.getSelectionModel().getSelectedItems();
        if (Objects.isNull(datas) || datas.isEmpty()) {
            return;
        }

        if (datas.size() == 1) {
            deleteWorkflow(datas.get(0).getWorkflowInfoEntity());
        } else {
            boolean isForced = event.isAltDown() && event.isControlDown() && event.isShiftDown();
            deleteWorkflows(datas, isForced);
        }
    }

    private void deleteWorkflow(WorkflowInfoEntity item) {
        if (Objects.isNull(item)) {
            return;
        }

        final WorkflowListTableDataEntity tableData = new WorkflowListTableDataEntity(item, false);
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), tableData.getWorkflowName());
        if (!ret.equals(ButtonType.OK)) {
            return;
        }

        // 工程順が使用されていないかチェックする。
        if (workflowInfoFacade.existAssignedKanban(item.getWorkflowId())) {
            // この工程順を使用したカンバンが存在します。\n本当に削除しますか?
            String message = new StringBuilder()
                                .append(LocaleUtils.getString("key.warn.deleteUsingWorkflow"))
                    .append("\n\n")
                    .append(tableData.getWorkflowName())
                    .toString();

            MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(
                    sc.getWindow(),
                                LocaleUtils.getString("key.Delete"),
                    message,
                    MessageDialogEnum.MessageDialogType.Warning,
                    MessageDialogEnum.MessageDialogButtons.YesNo,
                    3.0,
                    "#ff0000",
                    "#ffffff"
            );
            if (!dialogResult.equals(MessageDialogEnum.MessageDialogResult.Yes)) {
                return;
            }
        }

        // 削除
        ResponseEntity res = workflowInfoFacade.removeWork(item);

        if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
            TreeItem<WorkflowHierarchyInfoEntity> hierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(hierarchy)) {
                // 工程順マスタのキャッシュを削除する。
                CacheUtils.removeCacheData(WorkflowInfoEntity.class);
                this.updateListItemThread(hierarchy, Collections.singletonList(item.getWorkflowId()));
            }
            WorkflowEditButton(true);
        } else {
            //TODO:エラー時の処理
            if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.APPROVAL_APPLY_NON_DELETABLE)) {
            	// この項目は変更申請中のため削除できません
                            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.DeleteOrderProcesses"), LocaleUtils.getString("key.DeleteErrExistChangeRequestingData"));
            }
        }
    }

    private void deleteWorkflows(List<WorkflowListTableDataEntity> items, boolean isForced)
    {
        ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteMultipleMessage"), null);
        if (!ret.equals(ButtonType.OK)) {
            return;
        }

        Task task = new Task<List<Long>>() {
            @Override
            protected List<Long> call() {
                try {
                    blockUI(true);
                    Map<Boolean, List<WorkflowInfoEntity>> data;

                    List<WorkflowInfoEntity> workflowInfoEntities
                            = items
                            .stream()
                            .filter(Objects::nonNull)
                            .map(WorkflowListTableDataEntity::getWorkflowInfoEntity)
                            .collect(toList());

                    if (!isForced) {
                        data
                                = workflowInfoEntities
                                .stream()
                                .collect(groupingBy(p -> workflowInfoFacade.existAssignedKanban(p.getWorkflowId())));
                    } else {
                        data = new HashMap<>();
                        data.put(false, workflowInfoEntities);
                    }

                    // カンバンで使用していない工程は削除
                    if(Objects.nonNull(data.get(false))) {
                        data.get(false).forEach(workflowInfoFacade::removeWork);
						// 工程順マスタのキャッシュを削除する。
                		CacheUtils.removeCacheData(WorkflowInfoEntity.class);
                    }

                    // リスト更新
                    TreeItem<WorkflowHierarchyInfoEntity> treeItem = hierarchyTree.getSelectionModel().getSelectedItem();
                    if (Objects.isNull(treeItem)) {
                        return new ArrayList<>();
                    }

                    treeItem.getValue().getWorkflowInfoCollection().clear();
                    WorkflowHierarchyInfoEntity hierarchy = workflowHierarchyInfoFacade.find(treeItem.getValue().getWorkflowHierarchyId());
                    treeItem.getValue().setWorkflowInfoCollection(hierarchy.getWorkflowInfoCollection());

                    hierarchyTree.getSelectionModel().select(null);
                    hierarchyTree.getSelectionModel().select(treeItem);

                    // カンバンにて使用して削除できなかった工程を選択状態にする
                    return data.get(true).stream()
                            .filter(Objects::nonNull)
                            .map(WorkflowInfoEntity::getWorkflowId)
                            .collect(toList());

                } finally {
                    blockUI(false);
                }
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if(this.getValue().isEmpty()){
                    return;
                }

                // 選択したカンバンのうち、ｎ件は作業実績があるため削除できませんでした。
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.DeleteOrderProcesses"),
                        String.format(LocaleUtils.getString("key.warn.FailedDeleteWorkflows"), this.getValue().size()));
            }
        };

        new Thread(task).start();
        WorkflowEditButton(true);
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onListCopy(ActionEvent event) {
        try {
            WorkflowInfoEntity item = this.workflowList.getSelectionModel().getSelectedItem().getWorkflowInfoEntity();
            List<WorkflowInfoEntity> items
                    = this.workflowList
                    .getSelectionModel()
                    .getSelectedItems()
                    .stream()
                    .map(WorkflowListTableDataEntity::getWorkflowInfoEntity)
                    .collect(toList());

            if (!items.isEmpty()) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Copy"), LocaleUtils.getString("key.CopyMessage"));
                if (ret.equals(ButtonType.OK)) {
                    List<Long> workflowIds
                            = items
                            .stream()
                            .map(workflowInfoFacade::copyWork)
                            .filter(Objects::nonNull)
                            .filter(ResponseAnalyzer::getAnalyzeResult)
                            .map(ResponseEntity::getUri)
                            .map(UriConvertUtils::getUriToWorkId)
                            .collect(toList());

                    TreeItem<WorkflowHierarchyInfoEntity> hierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
                    if (Objects.nonNull(hierarchy)) {
                        this.updateListItemThread(hierarchy, workflowIds);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }


    private void selectedWorkflowItem() {
        final Long workflowId = this.permanenceData.getSelectedWorkflowId();
        if (Objects.isNull(workflowId)) {
            return;
        }

        Optional<WorkflowListTableDataEntity> workflowListTableDataEntity
                = this.workflowList.getItems()
                .stream()
                .filter(p -> Objects.equals(p.getWorkflowInfoEntity().getWorkflowId(), workflowId))
                .findFirst();

        if (!workflowListTableDataEntity.isPresent()) {
            return;
        }

        this.workflowList.getSelectionModel().select(workflowListTableDataEntity.get());
        this.workflowList.scrollTo(workflowListTableDataEntity.get());
        this.permanenceData.setSelectedWorkflowId(null);
    }

    /**
     * 工程順の編集
     * 
     * @param event アクションイベント
     */
    @FXML
    private void onListEdit(ActionEvent event) {
        if (0 > this.workflowList.getSelectionModel().getSelectedIndex()) {
            return;
        }

        WorkflowInfoEntity selectedWorkflow = this.workflowList.getSelectionModel().getSelectedItem().getWorkflowInfoEntity();
        if (Objects.nonNull(selectedWorkflow)) {
            WorkflowInfoEntity workflow = workflowInfoFacade.find(selectedWorkflow.getWorkflowId(), true);
            WorkflowHierarchyInfoEntity hierarchy = workflowHierarchyInfoFacade.find(workflow.getParentId());

            SelectedWorkflowAndHierarchy selected = new SelectedWorkflowAndHierarchy(workflow, hierarchy.getHierarchyName(), null);
            this.permanenceData.setSelectedWorkflowId(workflow.getWorkflowId());

            sc.setComponent("ContentNaviPane", "WorkflowDetailCompo", selected);

            List<ConWorkflowSeparateworkInfoEntity> separateWorks = workflow.getConWorkflowSeparateworkInfoCollection();
            separateWorks.stream().filter((separateWork) -> (Objects.equals(separateWork.getFkWorkflowId(), workflow.getWorkflowId()))).forEachOrdered((separateWork) -> {
                WorkInfoEntity work = CacheUtils.getCacheWork(separateWork.getFkWorkId());
                separateWork.setWorkName(separateWork.getWorkName() + " : " + work.getWorkRev());
            });
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onListCreate(ActionEvent event) {
        TreeItem<WorkflowHierarchyInfoEntity> hierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(hierarchy) && Objects.nonNull(hierarchy.getParent())) {
            WorkflowInfoEntity workflow = new WorkflowInfoEntity();
            workflow.setParentId(hierarchy.getValue().getWorkflowHierarchyId());
            SelectedWorkflowAndHierarchy selected = new SelectedWorkflowAndHierarchy(workflow, hierarchy.getValue().getHierarchyName(), null);
            sc.setComponent("ContentNaviPane", "WorkflowDetailCompo", selected);
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onListMove(ActionEvent event) {
        try {
            List<WorkflowListTableDataEntity> selectedWorks = this.workflowList.getSelectionModel().getSelectedItems();
            TreeItem<WorkflowHierarchyInfoEntity> selectedHierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (selectedWorks.isEmpty() || Objects.isNull(selectedHierarchy)) {
                return;
            }

            sc.blockUI(true);
            this.hierarchyTree.setVisible(false);
            this.workflowList.setVisible(false);

            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(this.hierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"));
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "WorkflowHierarchyTreeCompo", treeDialogEntity);
            TreeItem<WorkflowHierarchyInfoEntity> hierarchy = (TreeItem<WorkflowHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (ret.equals(ButtonType.OK) && Objects.nonNull(hierarchy)) {
                for (WorkflowListTableDataEntity selectedWork : selectedWorks) {
                    WorkflowInfoEntity workflow = workflowInfoFacade.find(selectedWork.getWorkflowInfoEntity().getWorkflowId());
                    workflow.setFkUpdatePersonId(this.loginUserInfoEntity.getId());
                    workflow.setUpdateDatetime(new Date());
                    workflow.setParentId(hierarchy.getValue().getWorkflowHierarchyId());

                    ResponseEntity res = workflowInfoFacade.updateWork(workflow);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // 工程順マスタのキャッシュを削除する。
                        CacheUtils.removeCacheData(WorkflowInfoEntity.class);

                        selectedHierarchy.getValue().getWorkflowInfoCollection().remove(selectedWork.getWorkflowInfoEntity());
                        // 連続で移動するため処理の終了後updateする
//                        updateListItemThread(hierarchy, selectedWorks.get(0).getWorkflowInfoEntity().getWorkflowId());
                    } else {
                        //TODO:エラー時の処理
                    }
                }
                List<Long> workflowIds
                        = selectedWorks
                        .stream()
                        .map(WorkflowListTableDataEntity::getWorkflowInfoEntity)
                        .map(WorkflowInfoEntity::getWorkflowId)
                        .collect(toList());
                this.updateListItemThread(hierarchy, workflowIds);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            sc.blockUI(false);
            this.hierarchyTree.setVisible(true);
            this.workflowList.setVisible(true);
        }
    }

    /**
     * リストデータの再取得
     *
     * @param treeItem
     * @param workflowIds 再取得後に選択するID
     */
    private void updateListItemThread(TreeItem<WorkflowHierarchyInfoEntity> treeItem, List<Long> workflowIds) {
        if (treeItem.getValue().getWorkflowHierarchyId() == ROOT_ID) {
            onSearch(null);
            return;
        }
                    
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    treeItem.getValue().getWorkflowInfoCollection().clear();
                    WorkflowHierarchyInfoEntity hierarchy = workflowHierarchyInfoFacade.find(treeItem.getValue().getWorkflowHierarchyId());
                    treeItem.getValue().setWorkflowInfoCollection(hierarchy.getWorkflowInfoCollection());
                    Platform.runLater(() -> {
                        // 工程順一覧を再更新
                        hierarchyTree.getSelectionModel().select(null);
                        hierarchyTree.getSelectionModel().select(treeItem);
                        Platform.runLater(() -> selectedListItem(workflowIds));
                    });
                } finally {
                    Platform.runLater(() -> { 
                        blockUI(false);
                        workflowList.requestFocus();
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * workflowList内でIDが一致するworkflowを選択する
     *
     * @param workflowId
     */
    private void selectedListItem(List<Long> workflowIds) {
        if (Objects.isNull(workflowIds) || workflowIds.isEmpty()) {
            return;
        }

        Set<Long> workflowIdSet = new HashSet<>(workflowIds);
        this.workflowList
                .getItems()
                .stream()
                .filter(p -> workflowIdSet.contains(p.getWorkflowInfoEntity().getWorkflowId()))
                .forEach(item -> this.workflowList.getSelectionModel().select(item));
    }

    /**
     * 工程リストの生成
     *
     * @param workInfoEntitys
     */
    private void createWorkflowList(List<WorkflowInfoEntity> workflowInfoEntitys) {
        List<WorkflowListTableDataEntity> listTabaleDatas = new ArrayList<>();
        List<WorkflowInfoEntity> displayWorkflows = getDisplayWorkflowList(workflowInfoEntitys);
        for (WorkflowInfoEntity entity : displayWorkflows) {
            WorkflowListTableDataEntity tabaleData = new WorkflowListTableDataEntity(entity, false);
            if (Objects.nonNull(entity.getFkUpdatePersonId())) {
                OrganizationInfoEntity person = CacheUtils.getCacheOrganization(entity.getFkUpdatePersonId());
                if (Objects.nonNull(person)) {
                    tabaleData.setUpdatePersonName(person.getOrganizationName());
                }
            }
            listTabaleDatas.add(tabaleData);
        }

        Platform.runLater(() -> {
            ObservableList<WorkflowListTableDataEntity> list = FXCollections.observableArrayList(listTabaleDatas);
            this.workflowList.setItems(list);
            this.workflowList.getSortOrder().add(this.workColumn);
            selectedWorkflowItem();
            this.approveButton.setDisable(true);
        });
    }

    /**
     * 工程リストの初期化
     *
     */
    private void clearWorkList() {
        this.workflowList.getItems().clear();
        this.workflowList.getSelectionModel().clearSelection();
    }

    /**
     * 表示用の工程順リストを取得する。
     *
     * @param workflowInfoEntities 工程順リスト
     * @return 表示用の工程順リスト
     */
    private List<WorkflowInfoEntity> getDisplayWorkflowList(List<WorkflowInfoEntity> workflowInfoEntities) {
        if (this.latestCheckBox.isSelected()) {
            // 最新のみチェックあり：最新リビジョンのみの工程順情報一覧を返却
            Map<String, Optional<WorkflowInfoEntity>> grpByNameMaxRev = workflowInfoEntities
                    .stream()
                    .collect(Collectors.groupingBy(WorkflowInfoEntity::getWorkflowName,
                            Collectors.maxBy(Comparator.comparingLong(WorkflowInfoEntity::getWorkflowRev))));

            return grpByNameMaxRev.values()
                    .stream()
                    .filter(opt -> opt.isPresent())
                    .map(opt -> opt.get())
                    .collect(Collectors.toList());
        } else {
            // 最新のみチェックなし：全リビジョンの工程順情報一覧を返却
            return new ArrayList<>(workflowInfoEntities);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean destoryComponent() {
        SplitPaneUtils.saveDividerPosition(this.workflowPane, getClass().getSimpleName());
        return true;
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        this.workflowProgress.setVisible(flg);
    }
    
    /**
     * コンテキストメニューを生成する
     *
     * @param
     * @return コンテキストメニュー
     */
    private Optional<ContextMenu> createContextMenu(WorkflowInfoEntity entity) {

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        MenuItem menuItem = new MenuItem(LocaleUtils.getString("key.DataCheck"));

        if (Objects.isNull(entity)) {
            return Optional.empty();
        }
        logger.info("createContextMenu:Start");
        contextMenu.setOnAction(event -> {
            startDataCheck(entity.getWorkflowId());
        });

        contextMenu.getItems().add(menuItem);
        return Optional.of(contextMenu);
    }

    /**
     * データチェック (別タスクにて実施)
     *
     * @param workflowId
     */
    private void startDataCheck(final Long workflowId) {
        if (Objects.isNull(workflowId)) {
            logger.info("workflowId is Null");
            return;
        }

        try {
            blockUI(true);
            List<WorkflowDataCheckInfoEntity> workflows = workflowInfoFacade.checkWorkflow(workflowId);
            if (Objects.nonNull(workflows) && !workflows.isEmpty()) {
                sc.showDialog(LocaleUtils.getString("key.DataCheck"), "WorkflowDataCheckDialog", workflows);
            } else {
                sc.showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.DataCheck"), LocaleUtils.getString("key.DataCheckMessage"));
            }
        } finally {
            blockUI(false);
        }
    }

    /**
     * 工程順を検索する。
     * 
     * @param event アクションイベント
     */    
    @FXML
    private void onSearch(ActionEvent event) {
        if (StringUtils.isEmpty(this.searchField.getText())) {
            return;
        }

        try {
            blockUI(true);

            Task task = new Task<List<WorkflowInfoEntity>>() {
                @Override
                protected List<WorkflowInfoEntity> call() throws Exception {
                    TreeItem<WorkflowHierarchyInfoEntity> hierarchy = hierarchyTree.getSelectionModel().getSelectedItem();
                    List<WorkflowInfoEntity> entities = workflowHierarchyInfoFacade.searchWorkflow(searchField.getText(), hierarchy.getValue().getWorkflowHierarchyId(), latestCheckBox.isSelected());
                    return entities;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        createWorkflowList(this.getValue());

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
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

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }        
    }

    /**
     * 
     * @param item
     * @param hierarchyId
     * @return 
     */
    private static TreeItem findTreeItem(TreeItem<WorkflowHierarchyInfoEntity> item, long hierarchyId) {
        if (Objects.nonNull(item) 
                && Objects.nonNull(item.getValue()) 
                && item.getValue().getWorkflowHierarchyId().equals(hierarchyId)) {
            return item;
        }
        for (TreeItem<WorkflowHierarchyInfoEntity> child : item.getChildren()) {
            TreeItem<WorkflowHierarchyInfoEntity> _item = findTreeItem(child, hierarchyId);
            if (Objects.nonNull(_item)) {
               return _item;
            }
        }
        return null;
    }
    
    private void WorkflowEditButton(boolean disable){
        this.delListButton.setDisable(disable);
        this.editListButton.setDisable(disable);
        this.copyListButton.setDisable(disable);
        this.moveListButton.setDisable(disable);
    }
}
