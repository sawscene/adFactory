/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component.lite;

import adtekfuji.admanagerapp.workfloweditplugin.common.SelectedWorkflowAndHierarchy;
import adtekfuji.admanagerapp.workfloweditplugin.common.UriConvertUtils;
import adtekfuji.admanagerapp.workfloweditplugin.common.WorkflowHierarchyEditor;
import adtekfuji.admanagerapp.workfloweditplugin.entity.WorkflowListTableDataEntity;
import adtekfuji.clientservice.WorkflowHierarchyInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.lite.LiteWorkflowInfo;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowSeparateworkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.WorkflowEditPermanenceData;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.dialog.MessageDialog;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程順編集コンポーネント
 *
 * @author kenji.yokoi
 */
@FxComponent(id = "WorkflowEditLite", fxmlPath = "/fxml/admanagerworkfloweditplugin/lite/workflow_list_lite.fxml")
public class WorkflowEditLite implements Initializable, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static WorkflowHierarchyInfoFacade workflowHierarchyInfoFacade = new WorkflowHierarchyInfoFacade();
    private final static WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    private final static Map<Long, TreeItem> treeItems = new HashMap<>();

    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final WorkflowEditPermanenceData permanenceData = WorkflowEditPermanenceData.getInstance();
    private WorkflowHierarchyEditor workflowHierarchyEditor;

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
    private VBox detailPane;
    @FXML
    private Button delListButton;
    @FXML
    private Button copyListButton;
    @FXML
    private Button createListButton;
    @FXML
    private Button moveListButton;
    @FXML
    private TableColumn workColumn;

    /**
     * 工程順詳細
     */
    @FXML
    private AnchorPane workDetailPane;

    @FXML
    private Pane workflowProgress;
    @FXML
    private PropertySaveTableView<WorkflowListTableDataEntity> workflowList;

    public WorkflowEditLite() {
        this.refreshCallback = new SelectedWorkflowAndHierarchy.RefreshCallback() {
            @Override
            public void onRefresh() {
                TreeItem<WorkflowHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
                updateListItemThread(item, 0L);
            }
        };
    }
    
    /**
     * 工程順編集コンポーネントを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        SplitPaneUtils.loadDividerPosition(this.workflowPane, getClass().getSimpleName());
        
        //役割の権限によるボタン無効化.
        if (!this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
            this.delTreeButton.setDisable(true);
            this.editTreeButton.setDisable(true);
            this.createTreeButton.setDisable(true);
            this.moveTreeButton.setDisable(true);
            this.delListButton.setDisable(true);
            this.copyListButton.setDisable(true);
            this.createListButton.setDisable(true);
            this.moveListButton.setDisable(true);
        } else {
            this.delListButton.setDisable(true);
        }

        if (!this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.RIGHT_ACCESS)) {
            this.moveTreeButton.setDisable(true);
        }

        this.workflowProgress.setVisible(false);

        // エンティティメンバーとバインド
        this.workColumn.setCellValueFactory(new PropertyValueFactory("workflowName"));
        this.workflowList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.workflowList.init("workflowList");

        // 階層ツリー選択時処理
        this.hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<WorkflowHierarchyInfoEntity>> observable, TreeItem<WorkflowHierarchyInfoEntity> oldValue, TreeItem<WorkflowHierarchyInfoEntity> newValue) -> {
            Long rootId = this.hierarchyTree.getRoot().getValue().getWorkflowHierarchyId();
            if (Objects.nonNull(newValue) && newValue.getValue().getWorkflowHierarchyId() != rootId) {
                // 工程リスト更新
                this.createWorkflowList(newValue.getValue().getWorkflowInfoCollection());
                this.permanenceData.setSelectedWorkflowHierarchy(newValue);
            } else {
                // 工程リストクリア
                Platform.runLater(() -> {
                    this.clearWorkList();
                });
                this.permanenceData.setSelectedWorkflowHierarchy(null);
            }

            this.workDetailPane.visibleProperty().set(false);

            this.workflowList.setRowFactory(tv -> new TableRow<WorkflowListTableDataEntity>() {
                /**
                 * セルの内容を更新する
                 *
                 * @param item セルの新しいアイテム
                 * @param empty このセルに割り当てられるデータが空かどうか（空:true、空でない:false）
                 */
                @Override
                protected void updateItem(WorkflowListTableDataEntity item, boolean empty) {
                    super.updateItem(item, empty);
                }
            });
        });

        // 工程順テーブル行クリック時処理
        this.workflowList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.onListEdit(new ActionEvent());
            }
        });

        // 工程順テーブルの選択行変更時処理
        this.workflowList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<WorkflowListTableDataEntity> selectedWorks = this.workflowList.getSelectionModel().getSelectedItems();
                if (selectedWorks.isEmpty()) {
                    return;
                }

                // 削除ボタンの有効/無効状態を切り替え
                if (this.loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_WORKFLOW)) {
                    this.delListButton.setDisable(false);
                }

                // 工程順詳細の表示
                WorkflowInfoEntity selectedWorkflow = this.workflowList.getSelectionModel().getSelectedItem().getWorkflowInfoEntity();
                TreeItem<WorkflowHierarchyInfoEntity> hierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
                if (Objects.nonNull(selectedWorkflow) && Objects.nonNull(hierarchy)) {
                    WorkflowInfoEntity workflow = workflowInfoFacade.find(selectedWorkflow.getWorkflowId(), true);
                    SelectedWorkflowAndHierarchy selected = new SelectedWorkflowAndHierarchy(workflow, hierarchy.getValue().getHierarchyName(), refreshCallback);
                    this.workDetailPane.visibleProperty().set(true);
                    sc.setComponent(workDetailPane, "WorkflowDetailLite", selected);
                }
            }
        });

        // キャッシュ情報取得 ツリー情報表示
        this.blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Properties properties = AdProperty.getProperties();
                    String liteTreeName = properties.getProperty(Config.LITE_HIERARCHY_TOP_KEY);
                    WorkflowHierarchyInfoEntity liteWorkflowHierarchy = workflowHierarchyInfoFacade.findHierarchyName(liteTreeName);
                    permanenceData.createLiteWorkfrowHierarchy(liteWorkflowHierarchy.getWorkflowHierarchyId());

                    TreeItem<WorkflowHierarchyInfoEntity> rootItem = permanenceData.getLiteWorkflowHierarchyRootItem();
                    TreeItem<WorkflowHierarchyInfoEntity> selectedItem = permanenceData.getSelectedWorkflowHierarchy();

                    rootItem.getChildren().clear();

                    workflowHierarchyEditor = new WorkflowHierarchyEditor(hierarchyTree, rootItem, workflowProgress, treeItems, true);
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
        blockUI(true);
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
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, rb.getString("key.Delete"), rb.getString("key.DeleteSingleMessage"), item.getValue().getHierarchyName());
                if (ret.equals(ButtonType.OK)) {
                    ResponseEntity res = workflowHierarchyInfoFacade.removeHierarchy(item.getValue());
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {

                        this.workflowHierarchyEditor.selectedTreeItem(item.getParent(), null);
                        this.workflowHierarchyEditor.remove(item.getParent(), item);

                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.RegistOrderProcesses"),
                                String.format(rb.getString("key.FailedToDelete"), rb.getString("key.OrderProcessesHierarch")));
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
                String message = String.format(rb.getString("key.InputMessage"), rb.getString("key.HierarchyName"));
                String hierarchyName = sc.showTextInputDialog(rb.getString("key.Edit"), message, rb.getString("key.HierarchyName"), orgName);
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
                        sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.RegistOrderProcesses"),
                                String.format(rb.getString("key.FailedToUpdate"), rb.getString("key.OrderProcessesHierarch")));

                        // データを戻す
                        item.getValue().setHierarchyName(orgName);
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                        // 排他バージョンが異なる。
                        sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.RegistOrderProcesses"), rb.getString("key.alert.differentVerInfo"));

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
                String message = String.format(rb.getString("key.InputMessage"), rb.getString("key.HierarchyName"));
                String hierarchyName = sc.showTextInputDialog(rb.getString("key.NewCreate"), message, rb.getString("key.HierarchyName"), "");
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
                    sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.RegistOrderProcesses"),
                            String.format(rb.getString("key.FailedToCreate"), rb.getString("key.OrderProcessesHierarch")));
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
            Long rootId = this.hierarchyTree.getRoot().getValue().getWorkflowHierarchyId();
            TreeItem<WorkflowHierarchyInfoEntity> selectedItem = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedItem)) {
                return;
            } else if (selectedItem.getValue().getWorkflowHierarchyId().equals(rootId)) {
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

            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(this.hierarchyTree.getRoot(), rb.getString("key.HierarchyName"), selectedItem);
            treeDialogEntity.setIsUseHierarchy(Boolean.TRUE);
            ButtonType ret = sc.showComponentDialog(rb.getString("key.Move"), "WorkflowHierarchyTreeCompo", treeDialogEntity);

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
                    sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.RegistOrderProcesses"),
                            String.format(rb.getString("key.FailedToUpdate"), rb.getString("key.OrderProcessesHierarch")));

                    //一時削除したデータを元に戻す
                    parentTreeItem.getChildren().add(idx, selectedItem);
                } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                    // 排他バージョンが異なる。
                    sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.RegistOrderProcesses"), rb.getString("key.alert.differentVerInfo"));

                    this.updateTree();
                } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)) {
                    // 移動不可能な階層だった場合、警告表示して階層ツリーを再取得して表示しなおす。
                    sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.Warning"), rb.getString("key.alert.unmovableHierarchy"));
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
     *
     * @param event
     */
    @FXML
    private void onListDelete(ActionEvent event) {
        try {
            if (0 > this.workflowList.getSelectionModel().getSelectedIndex()) {
                return;
            }
            WorkflowInfoEntity item = this.workflowList.getSelectionModel().getSelectedItem().getWorkflowInfoEntity();
            TreeItem<WorkflowHierarchyInfoEntity> hierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && Objects.nonNull(hierarchy)) {
                final WorkflowListTableDataEntity tabaleData = new WorkflowListTableDataEntity(item, true);
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, rb.getString("key.Delete"), rb.getString("key.DeleteSingleMessage"), tabaleData.getWorkflowName());
                if (ret.equals(ButtonType.OK)) {
                    // 工程順が使用されていないかチェックする。
                    if (workflowInfoFacade.existAssignedKanban(item.getWorkflowId())) {
                        // この工程順を使用したカンバンが存在します。\n本当に削除しますか?
                        String message = new StringBuilder()
                                .append(rb.getString("key.warn.deleteUsingWorkflow"))
                                .append("\n\n")
                                .append(tabaleData.getWorkflowName())
                                .toString();

                        MessageDialogEnum.MessageDialogResult dialogResult = MessageDialog.show(
                                sc.getWindow(),
                                rb.getString("key.Delete"),
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

                    LiteWorkflowInfo liteWorkflow = new LiteWorkflowInfo();
                    liteWorkflow.setWorkflow(item);
                    ResponseEntity res = workflowInfoFacade.removeLiteWork(liteWorkflow);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // 工程順マスタのキャッシュを削除する。
                        CacheUtils.removeCacheData(WorkflowInfoEntity.class);
                        this.updateListItemThread(hierarchy, item.getWorkflowId());
                    } else {
                        if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.APPROVAL_APPLY_NON_DELETABLE)) {
                            // この項目は変更申請中のため削除できません
                            sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.DeleteOrderProcesses"), rb.getString("key.DeleteErrExistChangeRequestingData"));
                        } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                            sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.RegistOrderProcesses"),
                                String.format(rb.getString("key.FailedToDelete"), rb.getString("key.OrderProcesses")));
                        }
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
    private void onListCopy(ActionEvent event) {
        try {
            if (0 > this.workflowList.getSelectionModel().getSelectedIndex()) {
                return;
            }
            WorkflowInfoEntity item = this.workflowList.getSelectionModel().getSelectedItem().getWorkflowInfoEntity();
            TreeItem<WorkflowHierarchyInfoEntity> hierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && Objects.nonNull(hierarchy)) {
                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, rb.getString("key.Copy"), rb.getString("key.CopyMessage"));
                if (ret.equals(ButtonType.OK)) {
                    LiteWorkflowInfo liteWorkflow = new LiteWorkflowInfo();
                    liteWorkflow.setWorkflow(item);
                    ResponseEntity res = workflowInfoFacade.copyLiteWork(liteWorkflow);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        this.updateListItemThread(hierarchy, UriConvertUtils.getUriToWorkId(res.getUri()));
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.RegistOrderProcesses"),
                            String.format(rb.getString("key.FailedToChange"), rb.getString("key.OrderProcesses")));
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
        logger.info("onListEdit:Start");
        if (0 > this.workflowList.getSelectionModel().getSelectedIndex()) {
            return;
        }
        WorkflowInfoEntity selectedWorkflow = this.workflowList.getSelectionModel().getSelectedItem().getWorkflowInfoEntity();
        TreeItem<WorkflowHierarchyInfoEntity> hierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(selectedWorkflow) && Objects.nonNull(hierarchy)) {
            WorkflowInfoEntity workflow = workflowInfoFacade.find(selectedWorkflow.getWorkflowId(), true);
            SelectedWorkflowAndHierarchy selected = new SelectedWorkflowAndHierarchy(workflow, hierarchy.getValue().getHierarchyName(), refreshCallback);
            this.workDetailPane.visibleProperty().set(true);
            sc.setComponent(workDetailPane, "WorkflowDetailLite", selected);

            List<ConWorkflowSeparateworkInfoEntity> separateWorks = workflow.getConWorkflowSeparateworkInfoCollection();
            separateWorks.stream().filter((separateWork) -> (Objects.equals(separateWork.getFkWorkflowId(), workflow.getWorkflowId()))).forEachOrdered((separateWork) -> {
                WorkInfoEntity work = CacheUtils.getCacheWork(separateWork.getFkWorkId());
                separateWork.setWorkName(separateWork.getWorkName() + " : " + work.getWorkRev());
            });

        }
        logger.info("onListEdit:End");
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
            SelectedWorkflowAndHierarchy selected = new SelectedWorkflowAndHierarchy(workflow, hierarchy.getValue().getHierarchyName(), refreshCallback);
            this.workDetailPane.visibleProperty().set(true);
            sc.setComponent(workDetailPane, "WorkflowDetailLite", selected);
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void onListMove(ActionEvent event) {
        try {
            if (0 > this.workflowList.getSelectionModel().getSelectedIndex()) {
                return;
            }
//            WorkflowListTableDataEntity selectedWork = workflowList.getSelectionModel().getSelectedItem();
            List<WorkflowListTableDataEntity> selectedWorks = this.workflowList.getSelectionModel().getSelectedItems();
            TreeItem<WorkflowHierarchyInfoEntity> selectedHierarchy = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (selectedWorks.isEmpty() || Objects.isNull(selectedHierarchy)) {
                return;
            }

            sc.blockUI(true);
            this.hierarchyTree.setVisible(false);
            this.workflowList.setVisible(false);
            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(this.hierarchyTree.getRoot(), rb.getString("key.HierarchyName"));
            ButtonType ret = sc.showComponentDialog(rb.getString("key.Move"), "WorkflowHierarchyTreeCompo", treeDialogEntity);
            TreeItem<WorkflowHierarchyInfoEntity> hierarchy = (TreeItem<WorkflowHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (ret.equals(ButtonType.OK) && Objects.nonNull(hierarchy)) {
                for (WorkflowListTableDataEntity selectedWork : selectedWorks) {
                    WorkflowInfoEntity workflow = workflowInfoFacade.find(selectedWork.getWorkflowInfoEntity().getWorkflowId());
                    if (Objects.nonNull(workflow)) {
                        workflow.setFkUpdatePersonId(this.loginUserInfoEntity.getId());
                        workflow.setUpdateDatetime(new Date());
                        workflow.setParentId(hierarchy.getValue().getWorkflowHierarchyId());
                        ResponseEntity res = workflowInfoFacade.updateWork(workflow);
                        if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                            // 工程順マスタのキャッシュを削除する。
                            CacheUtils.removeCacheData(WorkflowInfoEntity.class);
                            selectedHierarchy.getValue().getWorkflowInfoCollection().remove(selectedWork.getWorkflowInfoEntity());
                            // 連続で移動するため処理の終了後updateする
                            //updateListItemThread(hierarchy, selectedWorks.get(0).getWorkflowInfoEntity().getWorkflowId());
                        } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                            sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.RegistOrderProcesses"),
                                String.format(rb.getString("key.FailedToUpdate"), rb.getString("key.OrderProcesses")));
                        }
                    }
                }
                this.updateListItemThread(hierarchy, selectedWorks.get(0).getWorkflowInfoEntity().getWorkflowId());
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
     * 詳細表示処理後の表示更新
     */
    SelectedWorkflowAndHierarchy.RefreshCallback refreshCallback;

    /**
     * リストデータの再取得
     *
     * @param treeItem
     * @param workHierarchyId 再取得後に選択するID
     */
    private void updateListItemThread(TreeItem<WorkflowHierarchyInfoEntity> treeItem, Long workHierarchyId) {
        final long workflowId = Objects.nonNull(workflowList.getSelectionModel().getSelectedItem()) ? 
                workflowList.getSelectionModel().getSelectedItem().getWorkflowInfoEntity().getWorkflowId() : 0L;

        blockUI(true);
        
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    treeItem.getValue().getWorkflowInfoCollection().clear();
                    WorkflowHierarchyInfoEntity hierarchy = workflowHierarchyInfoFacade.find(treeItem.getValue().getWorkflowHierarchyId());
                    treeItem.getValue().setWorkflowInfoCollection(hierarchy.getWorkflowInfoCollection());

                } catch (Exception ex) {
                    logger.fatal(ex, ex);

                } finally {
                    Platform.runLater(() -> { 
                        hierarchyTree.getSelectionModel().clearSelection();
                        hierarchyTree.getSelectionModel().select(treeItem);

                        if (workflowId != 0) {
                            selectedListItem(workflowId);
                        }
                        
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
    private void selectedListItem(Long workflowId) {
        Optional<WorkflowListTableDataEntity> find = this.workflowList.getItems().stream().
                filter(p -> p.getWorkflowInfoEntity().getWorkflowId().equals(workflowId)).findFirst();
        if (find.isPresent()) {
            this.workflowList.getSelectionModel().clearSelection();
            this.workflowList.getSelectionModel().select(find.get());
        }
    }

    /**
     * 工程リストの生成
     *
     * @param workInfoEntitys
     */
    private void createWorkflowList(List<WorkflowInfoEntity> workflowInfoEntitys) {
        List<WorkflowListTableDataEntity> listTabaleDatas = new ArrayList<>();
        for (WorkflowInfoEntity entity : workflowInfoEntitys) {
            WorkflowListTableDataEntity tabaleData = new WorkflowListTableDataEntity(entity, true);
            listTabaleDatas.add(tabaleData);
        }

        Platform.runLater(() -> {
            ObservableList<WorkflowListTableDataEntity> list = FXCollections.observableArrayList(listTabaleDatas);
            this.workflowList.setItems(list);
            this.workflowList.getSortOrder().add(this.workColumn);
            this.delListButton.setDisable(true);
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
}
