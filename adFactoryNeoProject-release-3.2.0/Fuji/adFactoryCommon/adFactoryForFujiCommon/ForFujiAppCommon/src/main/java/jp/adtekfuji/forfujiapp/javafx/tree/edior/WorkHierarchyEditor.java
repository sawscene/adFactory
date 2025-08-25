/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.tree.edior;

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
import javafx.scene.layout.StackPane;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.javafxcommon.treecell.WorkHierarchyTreeCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程階層生成用クラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.01.Mon
 */
public class WorkHierarchyEditor {

    private final Logger logger = LogManager.getLogger();

    private final TreeView<WorkHierarchyInfoEntity> hierarchyTree;
    private final TreeItem<WorkHierarchyInfoEntity> rootItem;
    private final StackPane progressPane;

    private final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
        // ツリー展開時にイベント発生
        if (Objects.nonNull(newValue) && newValue.equals(true)) {
            TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        createTreeBranchs(treeItem);
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
    };

    /**
     * インスタンス生成
     *
     * @param hierarchyTree 画面のTreeViewノード
     * @param rootItem 上記のTreeViewノードの元のなるアイテム
     * @param progressPane 画面の操作制限用Pane
     */
    public WorkHierarchyEditor(TreeView<WorkHierarchyInfoEntity> hierarchyTree, TreeItem<WorkHierarchyInfoEntity> rootItem, StackPane progressPane) {
        logger.info(WorkHierarchyEditor.class.getName() + ":initialize start");

        this.hierarchyTree = hierarchyTree;
        this.progressPane = progressPane;
        this.blockUI(Boolean.FALSE);
        this.rootItem = rootItem;

        logger.info(WorkHierarchyEditor.class.getName() + ":initialize end");
    }

    /**
     * 階層生成処理開始
     * 
     */
    public void start() {
        logger.info(WorkHierarchyEditor.class.getName() + ":start start");
        
        Platform.runLater(() -> {
            this.hierarchyTree.rootProperty().setValue(rootItem);
            hierarchyTree.setCellFactory((TreeView<WorkHierarchyInfoEntity> p) -> new WorkHierarchyTreeCell());
        });

        if (this.rootItem.getChildren().isEmpty()) {
            this.createTreeRoot();
        }
        
        logger.info(WorkHierarchyEditor.class.getName() + ":start end");
    }

    /**
     * ツリーの親階層生成
     *
     */
    private void createTreeRoot() {
        logger.info(WorkHierarchyEditor.class.getName() + ":createTreeRootThread start");
        try {
            //ツリー設定
            this.rootItem.getChildren().clear();
            this.rootItem.setExpanded(false);
            
            //子階層まで読み込み
            List<WorkHierarchyInfoEntity> entitys = RestAPI.getWorkHierarchy(null);
            entitys.stream().forEach((WorkHierarchyInfoEntity entity) -> {
                TreeItem<WorkHierarchyInfoEntity> item = new TreeItem<>(entity);
                if (entity.getChildCount() > 0) {
                    item.getChildren().add(new TreeItem());
                }
                rootItem.getChildren().add(item);
                item.expandedProperty().addListener(WorkHierarchyEditor.this.changeListener);
            });
            //名前でソートする
            rootItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getHierarchyName()));
            
            rootItem.setExpanded(true);
            rootItem.expandedProperty().addListener(WorkHierarchyEditor.this.changeListener);

            Platform.runLater(() -> {
                WorkHierarchyEditor.this.hierarchyTree.setCellFactory((TreeView<WorkHierarchyInfoEntity> p) -> new WorkHierarchyTreeCell());
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(WorkHierarchyEditor.class.getName() + ":createTreeRootThread end");
    }

    /**
     * ツリーの子階層生成
     *
     * @param parentItem 親階層
     */
    private void createTreeBranchs(TreeItem<WorkHierarchyInfoEntity> parentItem) {
        logger.info(WorkHierarchyEditor.class.getName() + ":createTreeBranchsThread start");
        try {
            parentItem.getChildren().clear();

            List<WorkHierarchyInfoEntity> entitys = RestAPI.getWorkHierarchy(parentItem.getValue().getWorkHierarchyId());
            entitys.stream().forEach((WorkHierarchyInfoEntity entity) -> {
                TreeItem<WorkHierarchyInfoEntity> item = new TreeItem<>(entity);
                if (entity.getChildCount() > 0) {
                    item.getChildren().add(new TreeItem());
                }
                parentItem.getChildren().add(item);
                item.expandedProperty().addListener(WorkHierarchyEditor.this.changeListener);
            });

            //名前でソートする
            parentItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getHierarchyName()));
            Platform.runLater(() -> {
                WorkHierarchyEditor.this.hierarchyTree.setCellFactory((TreeView<WorkHierarchyInfoEntity> p) -> new WorkHierarchyTreeCell());
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(WorkHierarchyEditor.class.getName() + ":createTreeBranchsThread end");
    }

    /**
     * 操作制限を行う
     *
     * @param isBlock True:操作不可/False:操作可
     */
    private void blockUI(Boolean isBlock) {
        this.progressPane.setVisible(isBlock);
    }

}
