/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.selectcompo;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.WorkflowHierarchyInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import adtekfuji.locale.LocaleUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.treecell.WorkflowHierarchyTreeCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

/**
 * 工程順選択ダイアログ
 *
 * @author y-nakai
 */
@FxComponent(id = "WorkflowMultiSelectionCompo", fxmlPath = "/fxml/compo/workflow_multi_selection_compo.fxml")
public class WorkflowMultiSelectionCompoFxController implements Initializable, ArgumentDelivery {

    private final static long ROOT_ID = 0;
    private final static long RANGE = 20;

    private final Logger logger = LogManager.getLogger();
    private TreeItem<WorkflowHierarchyInfoEntity> rootItem = new TreeItem<>(new WorkflowHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.OrderProcessesHierarch")));

    private WorkflowHierarchyInfoFacade workflowHierarchyInfoFacade;
    private TreeItem<WorkflowHierarchyInfoEntity> selectedWorkflowHierarchy;
    private SelectDialogEntity settingDialogEntity;

    private boolean showLatestOnly = true;
    private boolean finalApproveOnly = false;

    private final ChangeListener<Boolean> changeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
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
    private TreeView<WorkflowHierarchyInfoEntity> hierarchyTree;
    @FXML
    private ListView<WorkflowInfoEntity> itemList;
    @FXML
    private ListView<WorkflowInfoEntity> selectedItemList;
    @FXML
    private Pane progressPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private RadioButton showLatestOnlyRButton;
    @FXML
    private ToggleGroup showingToggleGroup;

    /**
     * 工程順選択コンポーネントを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.itemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.selectedItemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        rootItem = new TreeItem<>(new WorkflowHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.OrderProcessesHierarch")));

        Callback<ListView<WorkflowInfoEntity>, ListCell<WorkflowInfoEntity>> cellFactory = (ListView<WorkflowInfoEntity> param) -> new ListItemCell();
        itemList.setCellFactory(cellFactory);
        selectedItemList.setCellFactory(cellFactory);

        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<WorkflowHierarchyInfoEntity>> observable, TreeItem<WorkflowHierarchyInfoEntity> oldValue, TreeItem<WorkflowHierarchyInfoEntity> newValue) -> {
            if (Objects.nonNull(newValue) && newValue.getValue().getWorkflowHierarchyId() != ROOT_ID) {
                updateListView(newValue.getValue().getWorkflowInfoCollection());
            } else {
                itemList.getItems().clear();
                itemList.getSelectionModel().clearSelection();
            }
            this.selectedWorkflowHierarchy = newValue;
        });

        this.showingToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            showLatestOnly = newValue.equals(showLatestOnlyRButton);
            this.updateListView();
        });
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectDialogEntity) {
            this.settingDialogEntity = (SelectDialogEntity) argument;

            this.selectedItemList.getItems().clear();
            if (Objects.nonNull(settingDialogEntity.getUribase())) {
                workflowHierarchyInfoFacade = new WorkflowHierarchyInfoFacade(settingDialogEntity.getUribase());
            } else {
                workflowHierarchyInfoFacade = new WorkflowHierarchyInfoFacade();
            }
            this.selectedItemList.getItems().addAll(this.settingDialogEntity.getWorkflows());
            this.settingDialogEntity.workflowsProperty().bind(this.selectedItemList.itemsProperty());

            boolean isLicensedApproval = ClientServiceProperty.isLicensed(LicenseOptionType.ApprovalOption.getName());
            if (isLicensedApproval) {
                // 承認機能が有効な場合、未承認は非表示。
                this.finalApproveOnly = true;

                // Ctrl + Shift + L or M キーで未承認の表示/非表示を切り替える。
                this.stackPane.setOnKeyPressed((KeyEvent event) -> {
                    if (event.isControlDown() && event.isShiftDown()) {
                        if (Objects.equals(event.getCode(), KeyCode.L)) {
                            // 未承認を表示する。
                            logger.info("Pressed CTRL + SHIFT + L Key.");
                            this.finalApproveOnly = false;
                            this.updateListView();
                        } else if (Objects.equals(event.getCode(), KeyCode.M)) {
                            // 未承認を非表示にする。
                            logger.info("Pressed CTRL + SHIFT + M Key.");
                            this.finalApproveOnly = true;
                            this.updateListView();
                        } 
                    }
                });
            } else {
                // 承認機能が無効の場合、未承認も表示する。
                this.finalApproveOnly = false;
            }

            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        createRoot();
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
    }

    @FXML
    private void OnAdd(ActionEvent event) {
        for (WorkflowInfoEntity item : this.itemList.getSelectionModel().getSelectedItems()) {
            if (!this.selectedItemList.getItems().contains(item)) {
                this.selectedItemList.getItems().add(item);
            }
        }
    }

    @FXML
    private void OnRemove(ActionEvent event) {
        if (Objects.nonNull(this.selectedItemList.getSelectionModel().getSelectedIndices())) {
            this.selectedItemList.getSelectionModel()
                                 .getSelectedIndices()
                                 .stream()
                                 .sorted(Comparator.reverseOrder())
                                 .mapToInt(Integer::intValue)
                                 .forEach(index -> this.selectedItemList.getItems().remove(index));
        }
    }

    /**
     * ListView表示用セル
     *
     */
    class ListItemCell extends ListCell<WorkflowInfoEntity> {

        @Override
        protected void updateItem(WorkflowInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setText(item.getWorkflowName() + " : " + item.getWorkflowRev().toString());
            } else {
                setText("");
            }
        }
    }

    /**
     * リスト更新
     */
    private void updateListView() {
        if (Objects.isNull(this.selectedWorkflowHierarchy)) {
            return;
        }

        updateListView(this.selectedWorkflowHierarchy.getValue().getWorkflowInfoCollection());
    }

    /**
     * リスト更新
     *
     * @param entitys
     */
    private void updateListView(List<WorkflowInfoEntity> workflows) {
        List<WorkflowInfoEntity> showItems;

        itemList.getItems().clear();

        List<WorkflowInfoEntity> targetWorks;
        if (finalApproveOnly) {
            targetWorks = workflows.stream()
                    .filter(p -> Objects.equals(p.getApprovalState(), ApprovalStatusEnum.FINAL_APPROVE))
                    .collect(Collectors.toList());
        } else {
            targetWorks = workflows;
        }

        if (showLatestOnly) {
            Map<String, Optional<WorkflowInfoEntity>> latestRevMap = targetWorks.stream()
                    .collect(Collectors.groupingBy(WorkflowInfoEntity::getWorkflowName,
                            Collectors.maxBy(Comparator.comparingLong(WorkflowInfoEntity::getWorkflowRev))));
            showItems = latestRevMap.values().stream()
                    .filter(p -> p.isPresent())
                    .map(p -> p.get())
                    .collect(Collectors.toList());
        } else {
            showItems = targetWorks;
        }

        showItems.sort(Comparator.comparing(workflow -> workflow.getWorkflowName()));
        itemList.setItems(FXCollections.observableArrayList(showItems));
    }

    /**
     * リストの初期化
     *
     */
    private void clearWorkflowList() {
        itemList.getItems().clear();
        itemList.getSelectionModel().clearSelection();
    }

    /**
     * ツリーの親階層生成
     *
     */
    private synchronized void createRoot() {
        try {
            logger.debug("createRoot start.");

            long count = workflowHierarchyInfoFacade.getTopHierarchyCount();
            this.rootItem.getChildren().clear();
            this.rootItem.getValue().setChildCount(count);

            for (long from = 0; from < count; from += RANGE) {
                List<WorkflowHierarchyInfoEntity> entities = workflowHierarchyInfoFacade.getTopHierarchyRange(from, from + RANGE - 1, true);

                entities.stream().forEach((entity) -> {
                    TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    this.rootItem.getChildren().add(item);
                });
            }

            //Lite階層も取得する
            Properties properties = AdProperty.getProperties();
            String liteTreeName = properties.getProperty(Config.LITE_HIERARCHY_TOP_KEY, "");
            WorkflowHierarchyInfoEntity liteHierarchy = null;
            if (Strings.isNotBlank(liteTreeName)) {
                liteHierarchy = this.workflowHierarchyInfoFacade.findHierarchyName(liteTreeName);
            }
            if (Objects.nonNull(liteHierarchy) && Objects.nonNull(liteHierarchy.getWorkflowHierarchyId())) {
                count = workflowHierarchyInfoFacade.getAffilationHierarchyCount(liteHierarchy.getWorkflowHierarchyId());
                this.rootItem.getValue().setChildCount(this.rootItem.getValue().getChildCount() + count);
                for (long from = 0; from < count; from += RANGE) {
                    List<WorkflowHierarchyInfoEntity> entities = workflowHierarchyInfoFacade.getAffilationHierarchyRange(liteHierarchy.getWorkflowHierarchyId(), from, from + RANGE - 1, true);

                    entities.stream().forEach((entity) -> {
                        TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(entity);
                        if (entity.getChildCount() > 0) {
                            item.getChildren().add(new TreeItem());
                        }
                        item.expandedProperty().addListener(this.changeListener);
                        this.rootItem.getChildren().add(item);
                    });
                }
            }

            
            // 名前でソートする
            rootItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getHierarchyName()));

            Platform.runLater(() -> {
                this.hierarchyTree.rootProperty().setValue(rootItem);
                this.hierarchyTree.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell());
            });

            this.rootItem.setExpanded(true);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.debug("createRoot end.");
        }
    }

    /**
     * ツリーの子階層生成
     *
     * @param parentItem 親階層
     */
    private synchronized void expand(TreeItem<WorkflowHierarchyInfoEntity> parentItem) {
        try {
            parentItem.getChildren().clear();
            long count = parentItem.getValue().getChildCount();

            for (long from = 0; from < count; from += RANGE) {
                List<WorkflowHierarchyInfoEntity> entities = workflowHierarchyInfoFacade.getAffilationHierarchyRange(parentItem.getValue().getWorkflowHierarchyId(), from, from + RANGE - 1, true);

                entities.stream().forEach((entity) -> {
                    TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    parentItem.getChildren().add(item);
                });
            }
            // 名前でソートする
            parentItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getHierarchyName()));

            Platform.runLater(() -> this.hierarchyTree.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell()));
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
        stackPane.setDisable(flg);
        progressPane.setVisible(flg);
    }
}
