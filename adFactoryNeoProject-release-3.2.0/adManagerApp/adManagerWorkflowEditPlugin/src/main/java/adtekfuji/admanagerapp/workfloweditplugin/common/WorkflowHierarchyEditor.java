/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.common;

import adtekfuji.clientservice.WorkflowHierarchyInfoFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.javafxcommon.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ta.ito
 */
public class WorkflowHierarchyEditor {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private static final WorkflowHierarchyInfoFacade workflowHierarchyInfoFacade = new WorkflowHierarchyInfoFacade();

    private static final long RANGE = 10;

    private final TreeView<WorkflowHierarchyInfoEntity> hierarchyTree;
    private final TreeItem<WorkflowHierarchyInfoEntity> rootItem;
    private static Pane progressPane;
    private boolean useLiteHierarchy = false;   //Lite階層指定
    
    private final Map<Long, TreeItem> treeItems;
    private final ObjectProperty<TreeItem<WorkflowHierarchyInfoEntity>> selectedProperty = new SimpleObjectProperty<>();

    private final ChangeListener changeListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            if (Objects.nonNull(newValue) && newValue.equals(true)) {
                TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
                blockUI(true);
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            expand(treeItem, null);
                            if (Objects.isNull(treeItem.getParent())) {
                                treeItem.expandedProperty().removeListener(changeListener);
                            }
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

    public WorkflowHierarchyEditor(TreeView<WorkflowHierarchyInfoEntity> hierarchyTree, TreeItem<WorkflowHierarchyInfoEntity> rootItem, Pane progressPane, Map<Long, TreeItem> treeItems, boolean useLiteHierarchy) {
        this.hierarchyTree = hierarchyTree;
        this.rootItem = rootItem;
        WorkflowHierarchyEditor.progressPane = progressPane;
        this.treeItems = treeItems;
        this.useLiteHierarchy = useLiteHierarchy;
    }

    /**
     * ツリーの親階層生成
     *
     * @param workflowHierarchyId 選択済の工程順階層
     */
    public synchronized void createRoot(Long workflowHierarchyId) {
        logger.debug("createRoot start.");
        try {
            long count = 0L;
            // Lite工程階層を取得.
            WorkflowHierarchyInfoEntity liteWorkflowHierarchy = new WorkflowHierarchyInfoEntity(0L, "");
            if (this.useLiteHierarchy) {
                Properties properties = AdProperty.getProperties();
                String liteTreeName = properties.getProperty(Config.LITE_HIERARCHY_TOP_KEY);
                liteWorkflowHierarchy = workflowHierarchyInfoFacade.findHierarchyName(liteTreeName);
                if (Objects.isNull(liteWorkflowHierarchy.getWorkflowHierarchyId())) {
                    throw new NullPointerException();
                }
            }

            if (!this.useLiteHierarchy) {
                count = workflowHierarchyInfoFacade.getTopHierarchyCount();
            } else {
                count = workflowHierarchyInfoFacade.getAffilationHierarchyCount(liteWorkflowHierarchy.getWorkflowHierarchyId());
            }

            this.rootItem.getChildren().clear();
            this.rootItem.getValue().setChildCount(count);
            this.rootItem.setExpanded(false);

            for (long from = 0; from < count; from += RANGE) {
                List<WorkflowHierarchyInfoEntity> entities;
                if (!this.useLiteHierarchy) {
                    entities = workflowHierarchyInfoFacade.getTopHierarchyRange(from, from + RANGE - 1, true);
                } else {
                    entities = workflowHierarchyInfoFacade.getAffilationHierarchyRange(liteWorkflowHierarchy.getWorkflowHierarchyId(), from, from + RANGE - 1, true);
                }

                entities.stream().forEach((entity) -> {
                    TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(entity);

                    if (this.treeItems.containsKey(entity.getWorkflowHierarchyId())) {
                        item.setExpanded(this.treeItems.get(entity.getWorkflowHierarchyId()).isExpanded());
                    }
                    this.treeItems.put(entity.getWorkflowHierarchyId(), item);
                    
                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }

                    item.expandedProperty().addListener(this.changeListener);
                    this.rootItem.getChildren().add(item);

                    if (entity.getWorkflowHierarchyId().equals(workflowHierarchyId)) {
                        this.selectedProperty.setValue(item);
                    }
                    
                    if (item.isExpanded()) {
                        this.expand(item, workflowHierarchyId);
                    }
                });
            }

            Platform.runLater(() -> {
                this.hierarchyTree.rootProperty().setValue(rootItem);
                this.rootItem.setExpanded(true);
                this.hierarchyTree.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell());

                if (Objects.nonNull(this.selectedProperty.get())) {
                    this.hierarchyTree.getSelectionModel().select(selectedProperty.get());
                    this.hierarchyTree.requestFocus();
                    return;
                }
                
                this.hierarchyTree.getSelectionModel().select(rootItem);
            });

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.debug("createRoot end.");
    }

    /**
     * ツリーの子階層生成
     *
     * @param parentItem 親階層
     * @param workflowHierarchyId
     */
    public synchronized void expand(TreeItem<WorkflowHierarchyInfoEntity> parentItem, Long workflowHierarchyId) {
        try {
            logger.debug("expand start.");

            parentItem.getChildren().clear();
            long count = parentItem.getValue().getChildCount();
 
            for (long from = 0; from < count; from += RANGE) {
                List<WorkflowHierarchyInfoEntity> entities = workflowHierarchyInfoFacade.getAffilationHierarchyRange(parentItem.getValue().getWorkflowHierarchyId(), from, from + RANGE - 1, true);

                entities.stream().forEach((entity) -> {
                    TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(entity);

                    if (this.treeItems.containsKey(entity.getWorkflowHierarchyId())) {
                        item.setExpanded(this.treeItems.get(entity.getWorkflowHierarchyId()).isExpanded());
                    }
                    this.treeItems.put(entity.getWorkflowHierarchyId(), item);

                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }

                    item.expandedProperty().addListener(this.changeListener);
                    parentItem.getChildren().add(item);

                    if (entity.getWorkflowHierarchyId().equals(workflowHierarchyId)) {
                        this.selectedProperty.setValue(item);
                    }

                    if (item.isExpanded()) {
                        this.expand(item, workflowHierarchyId);
                    }
                });
            }

            //Platform.runLater(() -> this.hierarchyTree.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell()));
            Platform.runLater(() -> {
                this.hierarchyTree.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell());
                if (Objects.nonNull(this.selectedProperty.get())) {
                    this.hierarchyTree.getSelectionModel().select(this.selectedProperty.get());
                    this.hierarchyTree.requestFocus();
                }
            });
        
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.debug("expand end.");
        }
    }

    /**
     * ツリーデータの再取得 引数parentItemの子を再取得する
     *
     * @param parentItem
     * @param workflowHierarchyId 再取得後に選択するID
     */
    public void updateTreeItemThread(TreeItem<WorkflowHierarchyInfoEntity> parentItem, Long workflowHierarchyId) {
        this.blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    if (Objects.isNull(parentItem.getParent()) && !parentItem.getValue().getWorkflowHierarchyId().equals(0l)) {
                        createRoot(workflowHierarchyId);
                    } else {
                        expand(parentItem, workflowHierarchyId);
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
     * 階層を選択状態にする。
     *
     * @param treeItem
     * @param workflowHierarchyId 未使用
     */
    public void selectedTreeItem(TreeItem<WorkflowHierarchyInfoEntity> treeItem, Long workflowHierarchyId) {
        Platform.runLater(() -> {
            this.hierarchyTree.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell());
            this.hierarchyTree.getSelectionModel().select(treeItem);
            this.hierarchyTree.requestFocus();
        });
    }

    /**
     * 階層を追加する。
     *
     * @param parentItem
     * @param hierarchy
     * @return
     */
    public TreeItem<WorkflowHierarchyInfoEntity> add(TreeItem<WorkflowHierarchyInfoEntity> parentItem, WorkflowHierarchyInfoEntity hierarchy) {
        TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(hierarchy);
        if (hierarchy.getChildCount() > 0) {
            item.getChildren().add(new TreeItem());
        }
        item.expandedProperty().addListener(this.changeListener);
        parentItem.getChildren().add(item);
        parentItem.getValue().setChildCount(parentItem.getValue().getChildCount() + 1);
        parentItem.getChildren().sort(Comparator.comparing(t -> t.getValue().getHierarchyName()));
        return item;
    }

    /**
     * 階層を追加する。
     *
     * @param parentItem
     * @param item
     * @return
     */
    public TreeItem<WorkflowHierarchyInfoEntity> add(TreeItem<WorkflowHierarchyInfoEntity> parentItem, TreeItem<WorkflowHierarchyInfoEntity> item) {
        item.expandedProperty().addListener(this.changeListener);
        parentItem.getChildren().add(item);
        parentItem.getValue().setChildCount(parentItem.getValue().getChildCount() + 1);
        Platform.runLater(() -> parentItem.getChildren().sort(Comparator.comparing(t -> t.getValue().getHierarchyName())));
        return item;
    }

    /**
     * 階層を削除する。
     *
     * @param parentItem
     * @param item
     */
    public void remove(TreeItem<WorkflowHierarchyInfoEntity> parentItem, TreeItem<WorkflowHierarchyInfoEntity> item) {
        item.expandedProperty().removeListener(this.changeListener);
        parentItem.getChildren().remove(item);
        parentItem.getValue().setChildCount(parentItem.getValue().getChildCount() - 1);
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
