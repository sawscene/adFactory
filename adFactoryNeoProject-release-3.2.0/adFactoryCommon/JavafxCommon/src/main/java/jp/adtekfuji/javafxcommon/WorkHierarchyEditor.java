/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon;

import adtekfuji.clientservice.WorkHierarchyInfoFacade;
import adtekfuji.fxscene.SceneContiner;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ta.ito
 */
public class WorkHierarchyEditor {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private static final WorkHierarchyInfoFacade workHierarchyInfoFacade = new WorkHierarchyInfoFacade();

    private static final long RANGE = 10;

    private final TreeView<WorkHierarchyInfoEntity> hierarchyTree;
    private final TreeItem<WorkHierarchyInfoEntity> rootItem;
    private static Pane progressPane;
    private final Map<Long, TreeItem> treeItems;
    private final ObjectProperty<TreeItem<WorkHierarchyInfoEntity>> selectedProperty = new SimpleObjectProperty<>();

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
                            expand(treeItem, null);
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

    /**
     *
     * @param hierarchyTree
     * @param rootItem
     * @param progressPane
     */
    public WorkHierarchyEditor(TreeView<WorkHierarchyInfoEntity> hierarchyTree, TreeItem<WorkHierarchyInfoEntity> rootItem, Pane progressPane, Map<Long, TreeItem> treeItems) {
        this.hierarchyTree = hierarchyTree;
        this.rootItem = rootItem;
        WorkHierarchyEditor.progressPane = progressPane;
        this.treeItems = treeItems;
    }

    /**
     * ツリーの親階層生成
     * 
     * @param workHierarchyId 選択済の工程階層
     */
    public synchronized void createRoot(Long workHierarchyId) {
        try {
            logger.debug("createRoot start.");

            long count = workHierarchyInfoFacade.getTopHierarchyCount();
            this.rootItem.getChildren().clear();
            this.rootItem.getValue().setChildCount(count);
            this.rootItem.setExpanded(false);

            for (long from = 0; from < count; from += RANGE) {
                List<WorkHierarchyInfoEntity> entities = workHierarchyInfoFacade.getTopHierarchyRange(from, from + RANGE - 1, true, false);

                entities.stream().forEach((entity) -> {
                    TreeItem<WorkHierarchyInfoEntity> item = new TreeItem<>(entity);

                    if (this.treeItems.containsKey(entity.getWorkHierarchyId())) {
                        item.setExpanded(this.treeItems.get(entity.getWorkHierarchyId()).isExpanded());
                    }
                    this.treeItems.put(entity.getWorkHierarchyId(), item);

                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    this.rootItem.getChildren().add(item);

                    if (entity.getWorkHierarchyId().equals(workHierarchyId)) {
                        this.selectedProperty.setValue(item);
                    }

                    if (item.isExpanded()) {
                        this.expand(item, workHierarchyId);
                    }
                });
            }

            Platform.runLater(() -> {
                this.hierarchyTree.rootProperty().setValue(rootItem);
                this.rootItem.setExpanded(true);
                this.hierarchyTree.setCellFactory((TreeView<WorkHierarchyInfoEntity> o) -> new WorkHierarchyTreeCell());

                if (Objects.nonNull(this.selectedProperty.get())) {
                    this.hierarchyTree.getSelectionModel().select(selectedProperty.get());
                    this.hierarchyTree.requestFocus();
                    return;
                }

                this.hierarchyTree.getSelectionModel().select(rootItem);
            });

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.debug("createRoot end.");
        }
    }

    /**
     * ツリー展開
     *
     * @param parentItem 親階層
     * @param workHierarchyId
     */
    public synchronized void expand(TreeItem<WorkHierarchyInfoEntity> parentItem, Long workHierarchyId) {
        try {
            logger.debug("expand start: {}", parentItem.getValue());

            parentItem.getChildren().clear();

            long count = parentItem.getValue().getChildCount();

            for (long from = 0; from <= count; from += RANGE) {
                List<WorkHierarchyInfoEntity> entities = workHierarchyInfoFacade.getAffilationHierarchyRange(parentItem.getValue().getWorkHierarchyId(), from, from + RANGE - 1, true, false);

                entities.stream().forEach((entity) -> {
                    TreeItem<WorkHierarchyInfoEntity> item = new TreeItem<>(entity);

                    if (this.treeItems.containsKey(entity.getWorkHierarchyId())) {
                        item.setExpanded(this.treeItems.get(entity.getWorkHierarchyId()).isExpanded());
                    }
                    this.treeItems.put(entity.getWorkHierarchyId(), item);

                    if (entity.getChildCount() > 0) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    parentItem.getChildren().add(item);

                    if (entity.getWorkHierarchyId().equals(workHierarchyId)) {
                        this.selectedProperty.setValue(item);
                    }

                    if (item.isExpanded()) {
                        this.expand(item, workHierarchyId);
                    }
                });
            }

            Platform.runLater(() -> {
                this.hierarchyTree.setCellFactory((TreeView<WorkHierarchyInfoEntity> o) -> new WorkHierarchyTreeCell());
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
     * @param workHierarchyId 再取得後に選択するID
     */
    public void updateTreeItemThread(TreeItem<WorkHierarchyInfoEntity> parentItem, Long workHierarchyId) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    if (Objects.isNull(parentItem.getParent()) && !parentItem.getValue().getWorkHierarchyId().equals(0l)) {
                        createRoot(workHierarchyId);
                    } else {
                        expand(parentItem, workHierarchyId);
                    }
                    Platform.runLater(() -> {
                        parentItem.setExpanded(true);
                        selectedTreeItem(parentItem, workHierarchyId);
                    });
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
     * @param workHierarchyId 未使用
     */
    public void selectedTreeItem(TreeItem<WorkHierarchyInfoEntity> treeItem, Long workHierarchyId) {
        Platform.runLater(() -> {
            this.hierarchyTree.setCellFactory((TreeView<WorkHierarchyInfoEntity> o) -> new WorkHierarchyTreeCell());
            this.hierarchyTree.getSelectionModel().select(treeItem);
            this.hierarchyTree.requestFocus();
        });
    }

    /**
     * TreeItemを強制的に展開する。
     *
     * @param treeItem
     */
    public void forceExpand(TreeItem<WorkHierarchyInfoEntity> treeItem) {
        Platform.runLater(() -> {
            treeItem.setExpanded(true);

            WorkHierarchyInfoEntity value = treeItem.getValue();
            treeItem.setValue(null);
            treeItem.setValue(value);
        });
    }

    /**
     * 階層を追加する。
     *
     * @param parentItem
     * @param hierarchy
     * @return
     */
    public TreeItem<WorkHierarchyInfoEntity> add(TreeItem<WorkHierarchyInfoEntity> parentItem, WorkHierarchyInfoEntity hierarchy) {
        TreeItem<WorkHierarchyInfoEntity> item = new TreeItem<>(hierarchy);
        if (hierarchy.getChildCount() > 0) {
            item.getChildren().add(new TreeItem());
        }
        item.expandedProperty().addListener(this.changeListener);
        parentItem.getChildren().add(item);
        parentItem.getValue().setChildCount(parentItem.getValue().getChildCount() + 1);
        Platform.runLater(() -> parentItem.getChildren().sort(Comparator.comparing(t -> t.getValue().getHierarchyName())));
        return item;
    }

    /**
     * 階層を追加する。
     *
     * @param parentItem
     * @param item
     * @return
     */
    public TreeItem<WorkHierarchyInfoEntity> add(TreeItem<WorkHierarchyInfoEntity> parentItem, TreeItem<WorkHierarchyInfoEntity> item) {
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
    public void remove(TreeItem<WorkHierarchyInfoEntity> parentItem, TreeItem<WorkHierarchyInfoEntity> item) {
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
