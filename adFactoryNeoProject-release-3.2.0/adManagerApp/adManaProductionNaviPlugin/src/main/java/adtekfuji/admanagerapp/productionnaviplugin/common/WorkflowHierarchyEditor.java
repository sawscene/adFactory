/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

import adtekfuji.clientservice.WorkflowHierarchyInfoFacade;
import adtekfuji.fxscene.SceneContiner;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
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
                            expand(treeItem);
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

    public WorkflowHierarchyEditor(TreeView<WorkflowHierarchyInfoEntity> hierarchyTree, TreeItem<WorkflowHierarchyInfoEntity> rootItem, Pane progressPane) {
        this.hierarchyTree = hierarchyTree;
        this.rootItem = rootItem;
        WorkflowHierarchyEditor.progressPane = progressPane;
    }

    /**
     * ツリーの親階層生成
     *
     */
    public synchronized void createRoot() {
        logger.debug("createRoot start.");
        try {
            if (this.rootItem.getChildren().isEmpty()) {
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

                Platform.runLater(() -> {
                    this.hierarchyTree.rootProperty().setValue(rootItem);
                    this.hierarchyTree.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell());
                });

                this.rootItem.setExpanded(true);
            } else {
                // 工程順階層を復元
                Platform.runLater(() -> {
                    this.hierarchyTree.rootProperty().setValue(rootItem);
                    this.hierarchyTree.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell());
                });
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.debug("createRoot end.");
    }

    /**
     * ツリーの子階層生成
     *
     * @param parentItem 親階層
     */
    public synchronized void expand(TreeItem<WorkflowHierarchyInfoEntity> parentItem) {
        try {
            logger.debug("expand start.");

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

            Platform.runLater(() -> this.hierarchyTree.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell()));

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
                        createRoot();
                    } else {
                        expand(parentItem);
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
        //progressPane.setVisible(flg);
    }

}
