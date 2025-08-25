/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.admanagerapp.workfloweditplugin.common.Constants;
import adtekfuji.admanagerapp.workfloweditplugin.common.SelectedWorkAndHierarchy;
import adtekfuji.admanagerapp.workfloweditplugin.common.UriConvertUtils;
import adtekfuji.admanagerapp.workfloweditplugin.entity.WorkListTableDataEntity;
import adtekfuji.clientservice.AccessHierarchyInfoFacade;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.WorkHierarchyInfoFacade;
import adtekfuji.clientservice.WorkInfoFacade;
import adtekfuji.clientservice.common.Paths;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.rest.RestClient;
import adtekfuji.utility.StringUtils;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
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
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.WorkHierarchyEditor;
import jp.adtekfuji.javafxcommon.WorkflowEditPermanenceData;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.controls.ResettableTextField;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import jp.adtekfuji.javafxcommon.dialog.MessageDialog;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum;
import jp.adtekfuji.javafxcommon.selectcompo.AccessAuthSettingEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程編集コンポーネント
 *
 * @author ta.ito
 */
@FxComponent(id = "WorkEditCompo", fxmlPath = "/fxml/compo/work_list_compo.fxml")
public class WorkEditCompoFxController implements Initializable, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static WorkHierarchyInfoFacade workHierarchyInfoFacade = new WorkHierarchyInfoFacade();
    private final static WorkInfoFacade workInfoFacade = new WorkInfoFacade();
    private final static  Map<Long, TreeItem> treeItems = new HashMap<>();

    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final WorkflowEditPermanenceData permanenceData = WorkflowEditPermanenceData.getInstance();
    private WorkHierarchyEditor workHierarchyEditor;

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
    private SplitPane workPane;
    @FXML
    private TreeView<WorkHierarchyInfoEntity> hierarchyTree;
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
    private Pane workProgress;

    @FXML
    private PropertySaveTableView<WorkListTableDataEntity> workList;

    @FXML
    private HBox approvePane;

    /**
     * 検索フィールド
     */
    @FXML
    private ResettableTextField searchField;
        
    /**
     * 工程編集コンポーネントを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.permanenceData.updateTitle();

        SplitPaneUtils.loadDividerPosition(this.workPane, getClass().getSimpleName());

        delListButton.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onListDelete);

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
            WorkEditButton(true);
        }

        if (!this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.RIGHT_ACCESS)) {
            this.hierarchyBtnArea.getItems().remove(this.authButton);
            this.moveTreeButton.setDisable(true);
        }

        this.workProgress.setVisible(false);

        //エンティティメンバーとバインド
        this.workColumn.setCellValueFactory(new PropertyValueFactory("workName"));
        this.updateByColumn.setCellValueFactory(new PropertyValueFactory("updatePersonName"));
        this.updateDateColumn.setCellValueFactory(new PropertyValueFactory("updateDatetime"));
        this.approvalStateColumn.setCellValueFactory(new PropertyValueFactory("approvalState"));
        this.approvalDatetimeColumn.setCellValueFactory(new PropertyValueFactory("approvalDatetime"));

        this.workList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        this.workList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.workList.init("workList");

        this.latestCheckBox.setSelected(true);

        if (!isLicensedApproval) {
            // 承認機能オプションが無効
            this.approvePane.setVisible(false);
            this.approvePane.setManaged(false);
            // this.approveButton.setVisible(false);
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
            this.approvalWebURL = 
                    AdProperty.getProperties().getProperty(Constants.SERVER_ADDRESS_KEY, Constants.SERVER_ADDRESS_DEFAULT) +
                    AdProperty.getProperties().getProperty(Constants.APPROVAL_WEB_URL_KEY, Constants.APPROVAL_WEB_URL_DEFAULT) +
                    "&open=" + Constants.URL_OPEN_KEY;
        }

        // 階層選択時
        this.hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<WorkHierarchyInfoEntity>> observable, TreeItem<WorkHierarchyInfoEntity> oldValue, TreeItem<WorkHierarchyInfoEntity> newValue) -> {
            if (StringUtils.isEmpty(this.searchField.getText())) {
                if (Objects.nonNull(newValue) && newValue.getValue().getWorkHierarchyId() != ROOT_ID) {
                    this.permanenceData.setSelectedWorkHierarchy(newValue);
                    this.createWorkList(newValue.getValue().getWorkInfoCollection());
                    WorkEditButton(true);
                } else {
                    this.permanenceData.setSelectedWorkHierarchy(null);
                    Platform.runLater(() ->  this.clearWorkList());
                }
            } else {
                // フィルタリング
                this.onSearch(null);
            }
        });

        // 最新版のみを表示
        this.latestCheckBox.setOnAction((ActionEvent event) -> {
            if (StringUtils.isEmpty(this.searchField.getText())) {
                TreeItem<WorkHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
                if (Objects.nonNull(item) && Objects.nonNull(item.getParent())) {
                    this.createWorkList(item.getValue().getWorkInfoCollection());
                }
            } else {
                this.onSearch(null);
            }
        });

        this.workList.setRowFactory(tableView -> {
            TableRow<WorkListTableDataEntity> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    // 工程編集画面を開く
                    this.onListEdit(new ActionEvent());
                }
            });

            //if (row.isEmpty()) {
            //    buildTooltip(row, row.getItem());
            //} else {
            //    row.setTooltip(null);
            //}

            return row ;
        });

        //工程テーブルの選択行変更時処理
        this.workList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<WorkListTableDataEntity> selectedWorks = this.workList.getSelectionModel().getSelectedItems();
                if (selectedWorks.isEmpty()) {
                    return;
                }
               
                //if (selectedWorks.size() == 1) {
                //    WorkInfoEntity workflow = selectedWorks.get(0).getWorkInfoEntity();
                //    TreeItem<WorkHierarchyInfoEntity> hierarchy = WorkEditCompoFxController.findTreeItem(permanenceData.getWorkHierarchyRootItem(), workflow.getParentId());
                //    hierarchyTree.getSelectionModel().select(hierarchy);
                //}
                
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
                            .map(p -> p.getWorkInfoEntity())
                            .filter(p -> ApprovalStatusEnum.APPLY.equals(p.getApprovalState()))
                            .count();

                    if (this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW) && applyCount == 0) {
                        // 編集権限あり、かつ、申請中のデータがないなら表示(有効)
                        delListButtonDisabled = false;
                    }
                }
                WorkEditButton(delListButtonDisabled);

                // 承認/申請状況ボタンの有効/無効状態を切り替え
                boolean approveButtonDisabled = true;
                if (selectedWorks.size() == 1) {
                    WorkInfoEntity selectedItem = selectedWorks.get(0).getWorkInfoEntity();

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
            TreeItem<WorkHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && item.getValue().getWorkHierarchyId() != ROOT_ID) {
                this.createWorkList(item.getValue().getWorkInfoCollection());
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
        
        // ツリー情報表示
        this.blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    TreeItem<WorkHierarchyInfoEntity> rootItem = permanenceData.getWorkHierarchyRootItem();
                    TreeItem<WorkHierarchyInfoEntity> selectedItem = permanenceData.getSelectedWorkHierarchy();

                    rootItem.getChildren().clear();

                    workHierarchyEditor = new WorkHierarchyEditor(hierarchyTree, rootItem, workProgress, treeItems);
                    workHierarchyEditor.createRoot(Objects.nonNull(selectedItem) ? selectedItem.getValue().getWorkHierarchyId() : null);
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

            if (Objects.equals(event.getSource(), this.workList)) {
                TreeItem<WorkHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
                if (Objects.nonNull(item)) {
                    this.updateListItemThread(item, null);
                }
                return;
            }

            this.updateTree();
        }
    }

    /**
     * ツリーを更新する。
     */
    private void updateTree() {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    TreeItem<WorkHierarchyInfoEntity> selectedItem = hierarchyTree.getSelectionModel().getSelectedItem();
                    workHierarchyEditor.createRoot(Objects.nonNull(selectedItem) ? selectedItem.getValue().getWorkHierarchyId() : null);
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
            TreeItem<WorkHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && Objects.nonNull(item.getParent())) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getValue().getHierarchyName());
                if (ret.equals(ButtonType.OK)) {
                    ResponseEntity res = workHierarchyInfoFacade.removeHierarchy(item.getValue());
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {

                        this.workHierarchyEditor.selectedTreeItem(item.getParent(), null);
                        this.workHierarchyEditor.remove(item.getParent(), item);

                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistProcess"),
                                String.format(LocaleUtils.getString("key.FailedToDelete"), LocaleUtils.getString("key.ProcessHierarch")));
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
            TreeItem<WorkHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && item.getValue().getWorkHierarchyId() != 0) {
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
                    ResponseEntity res = workHierarchyInfoFacade.updateHierarchy(item.getValue());
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        WorkHierarchyInfoEntity value = workHierarchyInfoFacade.find(item.getValue().getWorkHierarchyId());
                        item.setValue(null);
                        item.setValue(value);

                        this.workHierarchyEditor.selectedTreeItem(item, null);

                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistProcess"),
                                String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.ProcessHierarch")));
                        // データを戻す
                        item.getValue().setHierarchyName(orgName);
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                        // 排他バージョンが異なる。
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistProcess"), LocaleUtils.getString("key.alert.differentVerInfo"));

                        WorkHierarchyInfoEntity value = workHierarchyInfoFacade.find(item.getValue().getWorkHierarchyId());
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
            TreeItem<WorkHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item)) {
                String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
                String hierarchyName = sc.showTextInputDialog(LocaleUtils.getString("key.NewCreate"), message, LocaleUtils.getString("key.HierarchyName"), "");
                if (Objects.isNull(hierarchyName)) {
                    return;
                } else if (hierarchyName.isEmpty()) {
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                    return;
                }

                WorkHierarchyInfoEntity hierarchy = new WorkHierarchyInfoEntity();
                hierarchy.setHierarchyName(hierarchyName);
                hierarchy.setParentId(item.getValue().getWorkHierarchyId());

                if (!item.isExpanded()) {
                    item.setExpanded(true);
                    if (0 < item.getValue().getChildCount()) {
                        this.workHierarchyEditor.expand(item, null);
                    }
                }

                ResponseEntity res = workHierarchyInfoFacade.registHierarchy(hierarchy);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {

                    hierarchy = workHierarchyInfoFacade.findURI(res.getUri());
                    TreeItem<WorkHierarchyInfoEntity> newItem = this.workHierarchyEditor.add(item, hierarchy);
                    this.workHierarchyEditor.selectedTreeItem(newItem, null);

                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistProcess"),
                            String.format(LocaleUtils.getString("key.FailedToCreate"), LocaleUtils.getString("key.ProcessHierarch")));
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 階層を別の階層に移動させる処理
     *
     * @param event
     */
    @FXML
    private void onTreeMove(ActionEvent event) {
        try {
            TreeItem<WorkHierarchyInfoEntity> selectedItem = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedItem)) {
                return;
            } else if (selectedItem.getValue().getWorkHierarchyId().equals(ROOT_ID)) {
                return;
            }
            WorkHierarchyInfoEntity selected = selectedItem.getValue();
            sc.blockUI(true);
            this.hierarchyTree.setVisible(false);
            this.workList.setVisible(false);

            TreeItem<WorkHierarchyInfoEntity> parentTreeItem = selectedItem.getParent();
            //移動先として自分を表示させないように一時削除
            int idx = parentTreeItem.getChildren().indexOf(selectedItem);
            parentTreeItem.getChildren().remove(selectedItem);

            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(this.hierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"), selectedItem);
            treeDialogEntity.setIsUseHierarchy(Boolean.TRUE);

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "WorkHierarchyTreeCompo", treeDialogEntity);
            TreeItem<WorkHierarchyInfoEntity> dest = (TreeItem<WorkHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (ret.equals(ButtonType.OK) && Objects.nonNull(dest)) {
                logger.debug(treeDialogEntity.getTreeSelectedItem());
                selected.setParentId(dest.getValue().getWorkHierarchyId());

                if (!dest.isExpanded()) {
                    dest.setExpanded(true);
                    if (0 < dest.getValue().getChildCount()) {
                        this.workHierarchyEditor.expand(dest, null);
                    }
                }

                ResponseEntity res = workHierarchyInfoFacade.updateHierarchy(selected);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    WorkHierarchyInfoEntity value = workHierarchyInfoFacade.find(selected.getWorkHierarchyId());
                    selectedItem.setValue(null);
                    selectedItem.setValue(value);

                    // 階層を追加して、選択状態にする。
                    TreeItem<WorkHierarchyInfoEntity> newItme = this.workHierarchyEditor.add(dest, selectedItem);
                    this.workHierarchyEditor.selectedTreeItem(newItme, null);
                } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistProcess"),
                            String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.ProcessHierarch")));

                    //一時削除したデータを元に戻す
                    parentTreeItem.getChildren().add(idx, selectedItem);
                } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                    // 排他バージョンが異なる。
                    //sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.RegistProcess"), LocaleUtils.getString("key.alert.differentVerInfo"));

                    this.updateTree();
                } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)) {
                    // 移動不可能な階層だった場合、警告表示して階層ツリーを再取得表示する。
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
            this.hierarchyTree.setVisible(true);
            this.workList.setVisible(true);
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
            TreeItem<WorkHierarchyInfoEntity> selectedItem = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedItem)) {
                return;
            } else if (selectedItem.getValue().getWorkHierarchyId().equals(ROOT_ID)) {
                return;
            }
            WorkHierarchyInfoEntity selected = selectedItem.getValue();
            //ダイアログに表示させるデータを設定
            AccessHierarchyTypeEnum type = AccessHierarchyTypeEnum.WorkHierarchy;
            long id = selected.getWorkHierarchyId();
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

                // 工程階層を再構築
                this.blockUI(true);
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            TreeItem<WorkHierarchyInfoEntity> rootItem = permanenceData.getWorkHierarchyRootItem();
                            rootItem.getChildren().clear();
                            workHierarchyEditor.createRoot(id);
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
            WorkInfoEntity item = this.workList.getSelectionModel().getSelectedItem().getWorkInfoEntity();
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
        try {
            List<WorkListTableDataEntity> items = this.workList.getSelectionModel().getSelectedItems();
            if (Objects.isNull(items) || items.isEmpty()) {
                return;
            }

            if(items.size()==1) {
                deleteWork(items.get(0).getWorkInfoEntity());
            } else {
                boolean isForced = event.isAltDown() && event.isControlDown() && event.isShiftDown();
                deleteWorks(items, isForced);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    private void deleteWork(WorkInfoEntity item) {
        if (Objects.isNull(item)) {
            return;
        }
        ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getWorkName());
        if (!ret.equals(ButtonType.OK)) {
            return;
        }

        if (workInfoFacade.existAssignedWorkflow((item.getWorkId()))) {
            // この工程順を使用したカンバンが存在します。\n本当に削除しますか?
            String message = new StringBuilder()
                    .append(LocaleUtils.getString("key.warn.deleteUsingWork"))
                    .append("\n\n")
                    .append(item.getWorkName())
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
        ResponseEntity res = workInfoFacade.removeWork(item);
        if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
            // 工程マスタのキャッシュを削除する。
            CacheUtils.removeCacheData(WorkInfoEntity.class);
            // キャッシュディレクトリを削除
            File file = new File(Paths.CLIENT_CACHE_PDOC + File.separator + String.valueOf(item.getWorkId()));
            this.deleteFile(file);

            TreeItem<WorkHierarchyInfoEntity> hierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(hierarchy)) {
                updateListItemThread(hierarchy, Collections.singletonList(item.getWorkId()));
            }
            WorkEditButton(true);
        } else {
            //TODO:エラー時の処理
            if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.APPROVAL_APPLY_NON_DELETABLE)) {
                // この項目は変更申請中のため削除できません
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.DeleteProcess"), LocaleUtils.getString("key.DeleteErrExistChangeRequestingData"));
            }
        }
    }

    private void deleteWorks(List<WorkListTableDataEntity> items, boolean isForced)
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
                    Map<Boolean, List<WorkInfoEntity>> data;
                    List<WorkInfoEntity> workInfoEntities
                            = items
                            .stream()
                            .filter(Objects::nonNull)
                            .map(WorkListTableDataEntity::getWorkInfoEntity)
                            .collect(toList());

                    if (!isForced) {
                        data = workInfoEntities
                                .stream()
                                .collect(Collectors.groupingBy(p -> workInfoFacade.existAssignedWorkflow(p.getWorkId())));
                    } else {
                        data = new HashMap<>();
                        data.put(false, workInfoEntities);
                    }

                    // 工程順で使用していない工程は削除
                    if (Objects.nonNull(data.get(false))) {
                        data.get(false).forEach(item -> {
                            final ResponseEntity res = workInfoFacade.removeWork(item);
                            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                                // キャッシュディレクトリを削除
                                File file = new File(Paths.CLIENT_CACHE_PDOC + File.separator + item.getWorkId());
                                deleteFile(file);
                            }
                        });
                    }

                    // リストの更新
                    TreeItem<WorkHierarchyInfoEntity> treeItem = hierarchyTree.getSelectionModel().getSelectedItem();
                    if (Objects.isNull(treeItem)) {
                        return new ArrayList<>();
                    }

                    treeItem.getValue().getWorkInfoCollection().clear();
                    WorkHierarchyInfoEntity hierarchy = workHierarchyInfoFacade.find(treeItem.getValue().getWorkHierarchyId());
                    treeItem.getValue().setWorkInfoCollection(hierarchy.getWorkInfoCollection());

                    hierarchyTree.getSelectionModel().select(null);
                    hierarchyTree.getSelectionModel().select(treeItem);

                    List<Long> workIds = items.stream()
                            .filter(Objects::nonNull)
                            .map(WorkListTableDataEntity::getWorkInfoEntity)
                            .map(WorkInfoEntity::getWorkId)
                            .collect(Collectors.toList());
                    Platform.runLater(() -> selectedListItem(workIds));

                    // カンバンにて使用して削除できなかった工程を選択状態にする
                    return data.get(true).stream()
                            .filter(Objects::nonNull)
                            .map(WorkInfoEntity::getWorkId)
                            .collect(toList());

                } finally {
                    blockUI(false);
                }
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (this.getValue().isEmpty()) {
                    return;
                }

                // 選択したカンバンのうち、ｎ件は作業実績があるため削除できませんでした。
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.DeleteOrderProcesses"),
                        String.format(LocaleUtils.getString("key.warn.FailedDeleteWorks"), this.getValue().size()));
            }

        };
        new Thread(task).start();
        WorkEditButton(true);
    }


    /**
     *
     * @param event
     */
    @FXML
    private void onListCopy(ActionEvent event) {
        try {
            List<WorkInfoEntity> items = this.workList.getSelectionModel().getSelectedItems().stream().map(WorkListTableDataEntity::getWorkInfoEntity).collect(toList());
            TreeItem<WorkHierarchyInfoEntity> hierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (!items.isEmpty()) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Copy"), LocaleUtils.getString("key.CopyMessage"));
                if (ret.equals(ButtonType.OK)) {
                    List<Long> workIds
                            = items
                            .stream()
                            .map(workInfoFacade::copyWork)
                            .filter(Objects::nonNull)
                            .filter(ResponseAnalyzer::getAnalyzeResult)
                            .map(ResponseEntity::getUri)
                            .map(UriConvertUtils::getUriToWorkId)
                            .collect(toList());

                    if (Objects.nonNull(hierarchy) && !workIds.isEmpty()) {
                        updateListItemThread(hierarchy, workIds);
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
    private void onListEdit(ActionEvent event) {
        if (0 > this.workList.getSelectionModel().getSelectedIndex()) {
            return;
        }

        WorkInfoEntity selectedWork = this.workList.getSelectionModel().getSelectedItem().getWorkInfoEntity();
        if (Objects.nonNull(selectedWork)) {
            WorkInfoEntity work = workInfoFacade.find(selectedWork.getWorkId(), false, true);
            WorkHierarchyInfoEntity hierarchy = workHierarchyInfoFacade.find(work.getParentId());
            
            SelectedWorkAndHierarchy selected = new SelectedWorkAndHierarchy(work, hierarchy.getHierarchyName());
            this.permanenceData.setSelectedWorkId(work.getWorkId());

            if (ClientServiceProperty.isLicensed("@Traceability")) {
                sc.setComponent("ContentNaviPane", "TabbedWorkCompo", selected);
            } else {
                sc.setComponent("ContentNaviPane", "WorkDetailCompo", selected);
            }
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onListCreate(ActionEvent event) {
        TreeItem<WorkHierarchyInfoEntity> hierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(hierarchy) && Objects.nonNull(hierarchy.getParent())) {
            WorkInfoEntity work = new WorkInfoEntity();
            work.setParentId(hierarchy.getValue().getWorkHierarchyId());
            work.setTaktTime(0);
            SelectedWorkAndHierarchy selected = new SelectedWorkAndHierarchy(work, hierarchy.getValue().getHierarchyName());

            if (ClientServiceProperty.isLicensed("@Traceability")) {
                sc.setComponent("ContentNaviPane", "TabbedWorkCompo", selected);
            } else {
                sc.setComponent("ContentNaviPane", "WorkDetailCompo", selected);
            }
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onListMove(ActionEvent event) {
        try {
            List<WorkListTableDataEntity> selectedWorks = this.workList.getSelectionModel().getSelectedItems();

            TreeItem<WorkHierarchyInfoEntity> selectedHierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
            //if (selectedWorks.isEmpty() || Objects.isNull(selectedHierarchy)) {
            //    return;
            //}

            sc.blockUI(true);
            this.hierarchyTree.setVisible(false);
            this.workList.setVisible(false);
            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(hierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"));
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "WorkHierarchyTreeCompo", treeDialogEntity);
            TreeItem<WorkHierarchyInfoEntity> hierarchy = (TreeItem<WorkHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (ret.equals(ButtonType.OK) && Objects.nonNull(hierarchy)) {
                logger.debug(treeDialogEntity.getTreeSelectedItem());
                for (WorkListTableDataEntity selectedWork : selectedWorks) {
                    WorkInfoEntity work = workInfoFacade.find(selectedWork.getWorkInfoEntity().getWorkId());
                    work.setParentId(hierarchy.getValue().getWorkHierarchyId());
                    work.setUpdatePersonId(this.loginUserInfoEntity.getId());
                    work.setUpdateDatetime(new Date());

                    ResponseEntity res = workInfoFacade.updateWork(work);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // 工程マスタのキャッシュを削除する。
                        CacheUtils.removeCacheData(WorkInfoEntity.class);
                        if (Objects.nonNull(selectedHierarchy)) {
                            selectedHierarchy.getValue().getWorkInfoCollection().remove(selectedWork.getWorkInfoEntity());
                        }

                    } else {
                        //TODO:エラー時の処理
                    }
                }

                List<Long> workIds
                        = selectedWorks
                        .stream()
                        .map(WorkListTableDataEntity::getWorkInfoEntity)
                        .map(WorkInfoEntity::getWorkId)
                        .collect(toList());
                updateListItemThread(hierarchy, workIds);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            sc.blockUI(false);
            this.hierarchyTree.setVisible(true);
            this.workList.setVisible(true);
        }
    }

    /**
     * リストデータの再取得
     *
     * @param treeItem
     * @param workId 再取得後に選択するID
     */
    private void updateListItemThread(TreeItem<WorkHierarchyInfoEntity> treeItem, List<Long> workIds) {
        if (treeItem.getValue().getWorkHierarchyId() == ROOT_ID) {
            onSearch(null);
            return;
        }

        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    treeItem.getValue().getWorkInfoCollection().clear();
                    WorkHierarchyInfoEntity hierarchy = workHierarchyInfoFacade.find(treeItem.getValue().getWorkHierarchyId());
                    treeItem.getValue().setWorkInfoCollection(hierarchy.getWorkInfoCollection());
                    Platform.runLater(() -> {
                        // 工程一覧を再更新
                        hierarchyTree.getSelectionModel().select(null);
                        hierarchyTree.getSelectionModel().select(treeItem);
                        Platform.runLater(() -> selectedListItem(workIds));
                    });
                } finally {
                    Platform.runLater(() -> {
                        blockUI(false);
                        workList.requestFocus();
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * workList内でIDが一致するworkを選択する
     *
     * @param workId
     */
    private void selectedListItem(List<Long> workIds) {
        if (Objects.isNull(workIds) || workIds.isEmpty()) {
            return;
        }

        Set<Long> workIdSet = new HashSet<>(workIds);
        this.workList
                .getItems()
                .stream()
                .filter(p -> workIdSet.contains(p.getWorkInfoEntity().getWorkId()))
                .forEach(workId -> this.workList.getSelectionModel().select(workId));
    }

    private void selectedWorkItem() {
        final Long workId = this.permanenceData.getSelectedWorkId();
        if (Objects.isNull(workId)) {
            return;
        }

        Optional<WorkListTableDataEntity> workListTableDataEntity
                = this.workList.getItems()
                .stream()
                .filter(p -> Objects.equals(p.getWorkInfoEntity().getWorkId(), workId))
                .findFirst();

        if (!workListTableDataEntity.isPresent()) {
            return;
        }

        this.workList.getSelectionModel().select(workListTableDataEntity.get());
        this.workList.scrollTo(workListTableDataEntity.get());
        this.permanenceData.setSelectedWorkId(null);
    }

    /**
     * 工程リストの生成
     *
     * @param workInfoEntitys
     */
    private void createWorkList(List<WorkInfoEntity> workInfoEntitys) {
        List<WorkListTableDataEntity> listTabaleDatas = new ArrayList<>();
        List<WorkInfoEntity> displayWorks = getDisplayWorkList(workInfoEntitys);

        for (WorkInfoEntity entity : displayWorks) {
            WorkListTableDataEntity tabaleData = new WorkListTableDataEntity(entity);
            if (Objects.nonNull(entity.getUpdatePersonId())) {
                OrganizationInfoEntity person = CacheUtils.getCacheOrganization(entity.getUpdatePersonId());
                if (Objects.nonNull(person)) {
                    tabaleData.setUpdatePersonName(person.getOrganizationName());
                }
            }
            listTabaleDatas.add(tabaleData);
        }

        Platform.runLater(() -> {
            ObservableList<WorkListTableDataEntity> list = FXCollections.observableArrayList(listTabaleDatas);
            this.workList.setItems(list);
            this.workList.getSortOrder().add(this.workColumn);
            this.selectedWorkItem();
            this.approveButton.setDisable(true);
        });
    }

    /**
     * 工程リストの初期化
     *
     */
    private void clearWorkList() {
        this.workList.getItems().clear();
        this.workList.getSelectionModel().clearSelection();
    }

    /**
     * 表示用の工程リストを取得する。
     * 
     * @param workInfoEntities 工程リスト
     * @return 表示用の工程リスト
     */
    private List<WorkInfoEntity> getDisplayWorkList(List<WorkInfoEntity> workInfoEntities) {
        if (this.latestCheckBox.isSelected()) {
            // 最新のみチェックあり：最新リビジョンのみの工程情報一覧を返却
            Map<String, Optional<WorkInfoEntity>> grpByNameMaxRev = workInfoEntities
                    .stream()
                    .collect(Collectors.groupingBy(WorkInfoEntity::getWorkName, 
                            Collectors.maxBy(Comparator.comparingLong(WorkInfoEntity::getWorkRev))));

            return grpByNameMaxRev.values()
                    .stream()
                    .filter(opt -> opt.isPresent())
                    .map(opt -> opt.get())
                    .collect(Collectors.toList());
        } else {
            // 最新のみチェックなし：全リビジョンの工程情報一覧を返却
            return new ArrayList<>(workInfoEntities);
        }
    }

    /**
     * ファイル及び、ディレクトリを削除する
     *
     * @param file
     */
    private void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                this.deleteFile(files[i]);
            }
            file.delete();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean destoryComponent() {
        SplitPaneUtils.saveDividerPosition(this.workPane, getClass().getSimpleName());
        return true;
    }

    /**
     * 操作を無効にする。
     *
     * @param block true: 無効化、false: 有効化
     */
    private void blockUI(boolean block) {
        sc.blockUI("ContentNaviPane", block);
        workProgress.setVisible(block);
    }

    /**
     * 工程を検索する。
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

            Task task = new Task<List<WorkInfoEntity>>() {
                @Override
                protected List<WorkInfoEntity> call() throws Exception {
                    TreeItem<WorkHierarchyInfoEntity> hierarchy = hierarchyTree.getSelectionModel().getSelectedItem();
                    List<WorkInfoEntity> entities = workHierarchyInfoFacade.searchWork(searchField.getText(), hierarchy.getValue().getWorkHierarchyId(), latestCheckBox.isSelected());
                    return entities;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        createWorkList(this.getValue());

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
     * ツールチップを構築する。
     *
     * @param control 構築先
     * @param pane 表示内容
     */
    private void buildTooltip(Control control, WorkListTableDataEntity item) {
        try {
            Tooltip toolTip = TooltipBuilder.build(control);
            toolTip.setMaxWidth(300);
            control.setTooltip(toolTip);
            control.setOnMouseEntered(event -> {
                if (Objects.isNull(toolTip.getGraphic())) {
                    VBox vbox = new VBox();
                    vbox.setMaxWidth(300);
                    vbox.getChildren().add(new Label(item.getWorkInfoEntity().getParentName()));
                    toolTip.setGraphic(vbox);
                }
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 
     * @param item
     * @param hierarchyId
     * @return 
     */
    private static TreeItem findTreeItem(TreeItem<WorkHierarchyInfoEntity> item, long hierarchyId) {
        if (Objects.nonNull(item) 
                && Objects.nonNull(item.getValue()) 
                && item.getValue().getWorkHierarchyId().equals(hierarchyId)) {
            return item;
        }
        for (TreeItem<WorkHierarchyInfoEntity> child : item.getChildren()) {
            TreeItem<WorkHierarchyInfoEntity> _item = findTreeItem(child, hierarchyId);
            if (Objects.nonNull(_item)) {
               return _item;
            }
        }
        return null;
    }
    
    private void WorkEditButton(boolean disable){
        this.delListButton.setDisable(disable);
        this.editListButton.setDisable(disable);
        this.copyListButton.setDisable(disable);
        this.moveListButton.setDisable(disable);
    }
    
}
