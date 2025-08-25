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
import adtekfuji.locale.LocaleUtils;
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
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
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

/**
 * 工程順選択ダイアログ
 *
 * @author e-mori
 */
@FxComponent(id = "WorkflowSingleSelectionCompo", fxmlPath = "/fxml/compo/workflow_single_selection_compo.fxml")
public class WorkflowSingleSelectionCompoFxController implements Initializable, ArgumentDelivery {

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
    private Label itemElementName;
    @FXML
    private TextField selectedItemName;
    @FXML
    private Pane progressPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private RadioButton showLatestOnlyRButton;
    @FXML
    private RadioButton showAllRButton;
    @FXML
    private ToggleGroup showingToggleGroup;
    @FXML
    private CheckBox enableLatestOnly;

    /**
     * 工程順選択コンポーネントを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootItem = new TreeItem<>(new WorkflowHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.OrderProcessesHierarch")));
      
      
        itemElementName.setText(LocaleUtils.getString("key.OrderProcessesName"));
        Callback<ListView<WorkflowInfoEntity>, ListCell<WorkflowInfoEntity>> cellFactory = (ListView<WorkflowInfoEntity> param) -> new ListItemCell();
        this.itemList.setCellFactory(cellFactory);

        this.hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<WorkflowHierarchyInfoEntity>> observable, TreeItem<WorkflowHierarchyInfoEntity> oldValue, TreeItem<WorkflowHierarchyInfoEntity> newValue) -> {
            if (Objects.nonNull(newValue) && newValue.getValue().getWorkflowHierarchyId() != ROOT_ID) {
                updateListView(newValue.getValue().getWorkflowInfoCollection());
            } else {
                selectedItemName.setText("");
                itemList.getItems().clear();
                itemList.getSelectionModel().clearSelection();
            }
            this.selectedWorkflowHierarchy = newValue;
        });

        this.itemList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends WorkflowInfoEntity> observable, WorkflowInfoEntity oldValue, WorkflowInfoEntity newValue) -> {
            if (Objects.nonNull(newValue)) {
                if (this.settingDialogEntity.isVisibleUseLatestRev()) {
                    selectedItemName.setText(createWorkflowName(newValue, !enableLatestOnly.isSelected()));
                } else {
                    // Liteモードは版数非表示
                    if (Objects.nonNull(this.settingDialogEntity.getLiteHierarchyOnly()) && this.settingDialogEntity.getLiteHierarchyOnly()) {
                        selectedItemName.setText(createWorkflowName(newValue, false));
                    } else {
                        selectedItemName.setText(createWorkflowName(newValue, true));
                    }
                }
                settingDialogEntity.getWorkflows().clear();
                settingDialogEntity.getWorkflows().add(newValue);
            } else {
                settingDialogEntity.getWorkflows().clear();
            }
        });

        this.showingToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            this.showLatestOnly = newValue.equals(this.showLatestOnlyRButton);
            this.updateListView();
        });

        this.enableLatestOnly.selectedProperty().addListener(this::changedEnableLatestOnly);
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectDialogEntity) {
            this.settingDialogEntity = (SelectDialogEntity) argument;

            if (Objects.nonNull(settingDialogEntity.getUribase())) {
                this.workflowHierarchyInfoFacade = new WorkflowHierarchyInfoFacade(settingDialogEntity.getUribase());
            } else {
                this.workflowHierarchyInfoFacade = new WorkflowHierarchyInfoFacade();
            }

            this.enableLatestOnly.setSelected(Objects.isNull(this.settingDialogEntity.isUseLatestRev())
                    ? false
                    : this.settingDialogEntity.isUseLatestRev());

            // 「常に最新版を使用する」チェックボックスを表示しない場合、コントロールを削除する。
            if (!this.settingDialogEntity.isVisibleUseLatestRev()) {
                this.enableLatestOnly.selectedProperty().removeListener(this::changedEnableLatestOnly);
                this.enableLatestOnly.setVisible(false);
                this.enableLatestOnly.setManaged(false);
            }

            // Liteモードは版数切替なし
            if (Objects.nonNull(settingDialogEntity.getLiteHierarchyOnly()) && settingDialogEntity.getLiteHierarchyOnly()) {
                this.showLatestOnlyRButton.setVisible(false);
                this.showAllRButton.setVisible(false);
            }

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

            this.blockUI(true);

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

    /**
     * ListView表示用セル
     *
     */
    class ListItemCell extends ListCell<WorkflowInfoEntity> {

        @Override
        protected void updateItem(WorkflowInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                // Liteモードは版数非表示
                if (Objects.nonNull(settingDialogEntity.getLiteHierarchyOnly()) && settingDialogEntity.getLiteHierarchyOnly()) {
                    setText(item.getWorkflowName());
                } else {
                    setText(item.getWorkflowName() + " : " + item.getWorkflowRev().toString());
                }
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

        this.updateListView(this.selectedWorkflowHierarchy.getValue().getWorkflowInfoCollection());
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

            // Lite工程階層を取得.
            WorkflowHierarchyInfoEntity workflowHierarchy = new WorkflowHierarchyInfoEntity(0L, "");
            if (Boolean.TRUE.equals(this.settingDialogEntity.getLiteHierarchyOnly())) {
                Properties properties = AdProperty.getProperties();
                String liteTreeName = properties.getProperty(Config.LITE_HIERARCHY_TOP_KEY);
                workflowHierarchy = this.workflowHierarchyInfoFacade.findHierarchyName(liteTreeName);
                if (Objects.isNull(workflowHierarchy.getWorkflowHierarchyId())) {
                    throw new NullPointerException();
                }
            }

            long count = 0;
            if (Objects.isNull(this.settingDialogEntity.getLiteHierarchyOnly()) || !settingDialogEntity.getLiteHierarchyOnly()) {
                count = workflowHierarchyInfoFacade.getTopHierarchyCount();
            } else {
                count = workflowHierarchyInfoFacade.getAffilationHierarchyCount(workflowHierarchy.getWorkflowHierarchyId());
            }
            this.rootItem.getChildren().clear();
            this.rootItem.getValue().setChildCount(count);

            for (long from = 0; from < count; from += RANGE) {
                List<WorkflowHierarchyInfoEntity> entities;
                if (Objects.isNull(this.settingDialogEntity.getLiteHierarchyOnly()) || !settingDialogEntity.getLiteHierarchyOnly()) {
                    entities = workflowHierarchyInfoFacade.getTopHierarchyRange(from, from + RANGE - 1, true);
                } else {
                    entities = workflowHierarchyInfoFacade.getAffilationHierarchyRange(workflowHierarchy.getWorkflowHierarchyId(), from, from + RANGE - 1);
                }

                entities.stream().forEach((entity) -> {
                    TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    this.rootItem.getChildren().add(item);
                });
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
     * 表示用の工程順名を取得する。
     *
     * @param item 工程順
     * @param showRev 版数を表示する？ (true:する, false:しない)
     * @return 表示用の工程順名
     */
    private String createWorkflowName(WorkflowInfoEntity item, boolean showRev) {
        if (Objects.isNull(item) || Objects.isNull(item.getWorkflowName())) {
            return "";
        } else if (Objects.isNull(item.getWorkflowRev())) {
            return item.getWorkflowName();
        } else {
            return item.getWorkflowName()
                    + (!showRev
                            ? ""
                            : " : " + item.getWorkflowRev().toString());
        }
    }

    /**
     * 「常に最新版を使用する」チェックボックス変更イベント
     *
     * @param o
     * @param oldValue
     * @param newValue 
     */
    private void changedEnableLatestOnly(ObservableValue o, Boolean oldValue, Boolean newValue) {
        settingDialogEntity.setUseLatestRev(newValue);
        settingDialogEntity.getWorkflows().stream().findAny().ifPresent(workflow -> {
            selectedItemName.setText(createWorkflowName((WorkflowInfoEntity) workflow, !newValue));
        });
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
