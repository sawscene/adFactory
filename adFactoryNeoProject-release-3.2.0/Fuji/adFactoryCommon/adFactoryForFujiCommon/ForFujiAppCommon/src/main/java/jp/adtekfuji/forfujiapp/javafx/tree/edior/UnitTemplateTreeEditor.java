/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.tree.edior;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.EntityConstants;
import jp.adtekfuji.forfujiapp.common.UIControlInterface;
import jp.adtekfuji.forfujiapp.common.UnitTemplateEditPermanenceData;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.javafx.tree.UnitTemplateHierarchyTreeCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレート階層生成処理
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTemplateTreeEditor {

    private final Logger logger = LogManager.getLogger();

    private final TreeView<UnitTemplateHierarchyInfoEntity> hierarchyTree;
    private final UnitTemplateEditPermanenceData unitTemplateEditPermanenceData = UnitTemplateEditPermanenceData.getInstance();
    private final UIControlInterface controlInterface;

    private final Image depIcon
            = new Image(getClass().getResourceAsStream("/image/folder.png"));

    private final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
        if (Objects.nonNull(newValue) && newValue.equals(true)) {
            TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        createTreeChildThread(treeItem);
                    } finally {
                        Platform.runLater(() -> blockUI(false));
                    }
                    return null;
                }
            };
            new Thread(task).start();
        }
    };

    public UnitTemplateTreeEditor(TreeView<UnitTemplateHierarchyInfoEntity> hierarchyTree, TreeItem<UnitTemplateHierarchyInfoEntity> rootItem, UIControlInterface controlInterface) {
        this.hierarchyTree = hierarchyTree;
        this.controlInterface = controlInterface;
        if (Objects.isNull(this.unitTemplateEditPermanenceData.getUnitTemplateHierarchyRootItem())
            || !Objects.equals(this.unitTemplateEditPermanenceData.getLoginUserId(), LoginUserInfoEntity.getInstance().getId())) {
            this.unitTemplateEditPermanenceData.setUnitTemplateHierarchyRootItem(rootItem);
            this.unitTemplateEditPermanenceData.setLoginUserId(LoginUserInfoEntity.getInstance().getId());
            createTreeRootThread();
        } else {
            Platform.runLater(() -> {
                this.hierarchyTree.setRoot(this.unitTemplateEditPermanenceData.getUnitTemplateHierarchyRootItem());
                selectedTreeItem(this.unitTemplateEditPermanenceData.getSelectedUnitTemplateHierarchy(), null);
                reRenderTree();
            });
        }
    }

    /**
     * セルデータの適応
     *
     */
    public final void reRenderTree() {
        hierarchyTree.setCellFactory((TreeView<UnitTemplateHierarchyInfoEntity> p) -> new UnitTemplateHierarchyTreeCell());
    }

    /**
     * ツリーの親階層生成
     *
     */
    public final void createTreeRootThread() {
        try {
            blockUI(true);
            TreeItem<UnitTemplateHierarchyInfoEntity> rootItem = this.unitTemplateEditPermanenceData.getUnitTemplateHierarchyRootItem();
            //ツリー設定
            rootItem.getChildren().clear();

            //子階層エンティティの取得
            List<UnitTemplateHierarchyInfoEntity> entitys = RestAPI.getUnitTemplateHierarchyChilds(0L, false);

            entitys.stream().forEach((entity) -> {
                TreeItem<UnitTemplateHierarchyInfoEntity> item = new TreeItem<>(entity, new ImageView(depIcon));
                if (entity.getChildCount() > 0) {
                    item.getChildren().add(new TreeItem<>());
                }
                rootItem.getChildren().add(item);

                //ツリーを開いたらツリー下のデータを取得
                item.expandedProperty().addListener(changeListener);
            });
            //名前でソートする
            rootItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getHierarchyName()));

            rootItem.setExpanded(true);
            rootItem.expandedProperty().addListener(changeListener);

            Platform.runLater(() -> {
                this.hierarchyTree.setRoot(rootItem);
                reRenderTree();
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * ツリーの子階層生成
     *
     * @param parentItem 親階層
     */
    public void createTreeChildThread(TreeItem<UnitTemplateHierarchyInfoEntity> parentItem) {
        try {
            blockUI(true);
            //ツリー設定
            parentItem.getChildren().clear();

            //子階層エンティティの取得
            List<UnitTemplateHierarchyInfoEntity> entitys = RestAPI.getUnitTemplateHierarchyChilds(parentItem.getValue().getUnitTemplateHierarchyId(), false);

            entitys.stream().forEach((entity) -> {
                TreeItem<UnitTemplateHierarchyInfoEntity> item = new TreeItem<>(entity, new ImageView(depIcon));
                if (entity.getChildCount() > 0) {
                    item.getChildren().add(new TreeItem<>());
                }
                parentItem.getChildren().add(item);

                //ツリーを開いたらツリー下のデータを取得
                item.expandedProperty().addListener(changeListener);
            });
            //名前でソートする
            parentItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getHierarchyName()));

            Platform.runLater(() -> {
                reRenderTree();
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * ツリーデータの再取得 引数parentItemの子を再取得する
     *
     * @param parentItem
     * @param unittemplateHierarchyId 再取得後に選択するID
     */
    public void updateTreeItemThread(TreeItem<UnitTemplateHierarchyInfoEntity> parentItem, Long unittemplateHierarchyId) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    if (parentItem.isExpanded()) {
                        createTreeChildThread(parentItem);
                    } else {
                        parentItem.setExpanded(true);
                    }

                    Platform.runLater(() -> {
                        selectedTreeItem(parentItem, unittemplateHierarchyId);
                        reRenderTree();
                    });
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * リストデータの再取得
     *
     * @param treeItem
     */
    public void updateListItemThread(TreeItem<UnitTemplateHierarchyInfoEntity> treeItem) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    treeItem.getValue().getUnitTemplateCollection().clear();
                    UnitTemplateHierarchyInfoEntity hierarchy = RestAPI.getUnitTemplateHierarchy(treeItem.getValue().getUnitTemplateHierarchyId());
                    treeItem.getValue().setUnitTemplateCollection(hierarchy.getUnitTemplateCollection());
                    Platform.runLater(() -> {
                        hierarchyTree.getSelectionModel().select(null);
                        hierarchyTree.getSelectionModel().select(treeItem);
                    });
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

    /**
     * IDが一致するTreeItemを選択する
     *
     * @param parentItem
     * @param unittemplateHierarchyId
     */
    public void selectedTreeItem(TreeItem<UnitTemplateHierarchyInfoEntity> parentItem, Long unittemplateHierarchyId) {
        Platform.runLater(() -> {
            this.hierarchyTree.setCellFactory((TreeView<UnitTemplateHierarchyInfoEntity> o) -> new UnitTemplateHierarchyTreeCell());
            this.hierarchyTree.getSelectionModel().select(parentItem);
            if (Objects.nonNull(unittemplateHierarchyId)) {
                Optional<TreeItem<UnitTemplateHierarchyInfoEntity>> find = parentItem.getChildren().stream().
                        filter(p -> p.getValue().getUnitTemplateHierarchyId().equals(unittemplateHierarchyId)).findFirst();
                if (find.isPresent()) {
                    this.hierarchyTree.getSelectionModel().select(find.get());
                }
            }
            this.hierarchyTree.requestFocus();
        });
    }

    /**
     * 最後に選択したIDをツリー内で再度選択する
     *
     * @param selectHierarchyId
     */
    public void selectedTreeItem(Long selectHierarchyId) {
        blockUI(true);
        TreeItem<UnitTemplateHierarchyInfoEntity> rootItem = unitTemplateEditPermanenceData.getSelectedUnitTemplateHierarchy();
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    if (!selectHierarchyId.equals(EntityConstants.ROOT_ID)) {
                        LinkedList<Long> ids = getParentIdList(selectHierarchyId);
                        Collections.reverse(ids);
                        TreeItem<UnitTemplateHierarchyInfoEntity> item = rootItem;
                        for (Long id : ids) {
                            Optional<TreeItem<UnitTemplateHierarchyInfoEntity>> find = item.getChildren().stream().
                                    filter(p -> p.getValue().getUnitTemplateHierarchyId().equals(id)).findFirst();
                            if (find.isPresent()) {
                                hierarchyTree.getSelectionModel().select(find.get());
                                find.get().setExpanded(true);
                                item = find.get();
                            }
                        }
                    }
                } finally {
                    Platform.runLater(() -> blockUI(false));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 指定された階層IDの最上位の親のIDまで取得する
     *
     * @param selectHierarchyId 階層ID
     * @return 階層の各種ID
     */
    private LinkedList<Long> getParentIdList(Long selectHierarchyId) {
        LinkedList<Long> ids = new LinkedList<>();
        ids.add(selectHierarchyId);
        UnitTemplateHierarchyInfoEntity selectHierarchy = RestAPI.getUnitTemplateHierarchy(selectHierarchyId);
        if (!selectHierarchy.getParentId().equals(EntityConstants.ROOT_ID)) {
            ids.addAll(getParentIdList(selectHierarchy.getParentId()));
        }
        return ids;
    }

    /**
     * UIロック
     *
     * @param flag
     */
    private void blockUI(Boolean flag) {
        controlInterface.blockUI(flag);
    }
}
