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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.UIControlInterface;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.javafx.tree.cell.TreeCellInterface;
import jp.adtekfuji.forfujiapp.javafx.tree.cell.UnitTemplateAndWorkflowTreeCell;
import jp.adtekfuji.forfujiapp.javafx.tree.entity.UnitTemplateHierarchyTreeEntity;
import jp.adtekfuji.forfujiapp.javafx.tree.entity.UnitTemplateTreeEntity;
import jp.adtekfuji.forfujiapp.javafx.tree.entity.WorkflowHierarchyTreeEntity;
import jp.adtekfuji.forfujiapp.javafx.tree.entity.WorkflowTreeEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレートと工程順のツリー生成処理
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTemplateAndWorkflowTreeEditor {

    private final Logger logger = LogManager.getLogger();

    private final TreeView<TreeCellInterface> workflowTree;
    private final TreeItem<TreeCellInterface> workflowRootItem;
    private final TreeView<TreeCellInterface> templateTree;
    private final TreeItem<TreeCellInterface> templateRootItem;
    private final UIControlInterface controlInterface;

    private final Image folderIcon
            = new Image(getClass().getResourceAsStream("/image/folder.png"));
    private final Image fileIcon
            = new Image(getClass().getResourceAsStream("/image/file.png"));

    private final ChangeListener workflowTreeChangeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
        if (Objects.nonNull(newValue) && newValue.equals(true)) {
            TreeItem<TreeCellInterface> treeItem = (TreeItem<TreeCellInterface>) ((BooleanProperty) observable).getBean();
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        if (treeItem.getValue().getEntity() instanceof WorkflowHierarchyInfoEntity) {
                            createWorkflowTreeBranchsThread(treeItem);
                        }
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

    private final ChangeListener templateTreeChangeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
        if (Objects.nonNull(newValue) && newValue.equals(true)) {
            TreeItem<TreeCellInterface> treeItem = (TreeItem<TreeCellInterface>) ((BooleanProperty) observable).getBean();
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        if (treeItem.getValue().getEntity() instanceof UnitTemplateHierarchyInfoEntity) {
                            createUnitTemplateTreeBranchsThread(treeItem);
                        }
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

    public UnitTemplateAndWorkflowTreeEditor(TreeView<TreeCellInterface> workflowTree, TreeItem<TreeCellInterface> workflowRootItem,
            TreeView<TreeCellInterface> templateTree, TreeItem<TreeCellInterface> templateRootItem, UIControlInterface controlInterface) {
        this.workflowTree = workflowTree;
        this.workflowRootItem = workflowRootItem;
        this.templateTree = templateTree;
        this.templateRootItem = templateRootItem;
        this.controlInterface = controlInterface;

        Platform.runLater(() -> {
            this.workflowTree.rootProperty().setValue(this.workflowRootItem);
            reWorkflowRenderTree();
            this.templateTree.rootProperty().setValue(this.templateRootItem);
            reUnitTemplateRenderTree();
        });

        if (this.workflowRootItem.getChildren().isEmpty()) {
            createWorkflowTreeRootThread();
        }
        if (this.templateRootItem.getChildren().isEmpty()) {
            createUnitTemplateTreeRootThread();
        }
    }

    /**
     * ツリーの再描画
     */
    private void reWorkflowRenderTree() {
        workflowTree.setCellFactory((TreeView<TreeCellInterface> p) -> new UnitTemplateAndWorkflowTreeCell());
    }

    /**
     * ツリーの再描画
     */
    private void reUnitTemplateRenderTree() {
        templateTree.setCellFactory((TreeView<TreeCellInterface> p) -> new UnitTemplateAndWorkflowTreeCell());
    }

    /**
     * 工程順ツリーの親階層生成
     *
     */
    private void createWorkflowTreeRootThread() {
        logger.debug("createTreeRoot start.");
        try {
            blockUI(true);
            //ツリー設定
            workflowRootItem.getChildren().clear();

            //子階層エンティティを取得
            List<WorkflowHierarchyInfoEntity> entitys = RestAPI.getWorkflowHierarchyChilds(null);

            entitys.stream().forEach((entity) -> {
                TreeItem<TreeCellInterface> item = new TreeItem<>(new WorkflowHierarchyTreeEntity(entity), new ImageView(folderIcon));
                if ((entity.getChildCount() > 0) || !(entity.getWorkflowInfoCollection().isEmpty())) {
                    item.getChildren().add(new TreeItem<>());
                }
                workflowRootItem.getChildren().add(item);

                //ツリーを開いたらツリー下のデータを取得
                item.expandedProperty().addListener(workflowTreeChangeListener);
            });
            //名前でソートする
            workflowRootItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getName()));

            workflowRootItem.setExpanded(true);
            workflowRootItem.expandedProperty().addListener(workflowTreeChangeListener);

            //ルートの描画
            Platform.runLater(() -> {
                workflowTree.setRoot(workflowRootItem);
                reWorkflowRenderTree();
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }

        logger.debug("createTreeRoot end.");
    }

    /**
     * ユニットテンプレートツリーの親階層生成
     *
     */
    private void createUnitTemplateTreeRootThread() {
        logger.debug("createTreeRoot start.");
        try {
            blockUI(true);
            //ツリー設定
            templateRootItem.getChildren().clear();

            //子階層エンティティを取得
            List<UnitTemplateHierarchyInfoEntity> entitys = RestAPI.getUnitTemplateHierarchyChilds(0L, true);

            entitys.stream().forEach((entity) -> {
                TreeItem<TreeCellInterface> item = new TreeItem<>(new UnitTemplateHierarchyTreeEntity(entity), new ImageView(folderIcon));
                if ((entity.getChildCount() > 0) || !(entity.getUnitTemplateCollection().isEmpty())) {
                    item.getChildren().add(new TreeItem<>());
                }
                templateRootItem.getChildren().add(item);

                //ツリーを開いたらツリー下のデータを取得
                item.expandedProperty().addListener(templateTreeChangeListener);
            });
            //名前でソートする
            templateRootItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getName()));

            templateRootItem.setExpanded(true);
            templateRootItem.expandedProperty().addListener(templateTreeChangeListener);

            //ルートの描画
            Platform.runLater(() -> {
                templateTree.setRoot(templateRootItem);
                reUnitTemplateRenderTree();
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }

        logger.debug("createTreeRoot end.");
    }

    /**
     * 工程順ツリーの子階層生成
     *
     * @param parentItem 親階層
     */
    private void createWorkflowTreeBranchsThread(TreeItem<TreeCellInterface> parentItem) {
        try {
            blockUI(true);
            parentItem.getChildren().clear();

            //子階層エンティティを取得
            List<WorkflowHierarchyInfoEntity> entitys = RestAPI.getWorkflowHierarchyChilds(parentItem.getValue().getHierarchyId());

            entitys.stream().forEach((entity) -> {
                TreeItem<TreeCellInterface> item = new TreeItem<>(new WorkflowHierarchyTreeEntity(entity), new ImageView(folderIcon));
                if ((entity.getChildCount() > 0) || !(entity.getWorkflowInfoCollection().isEmpty())) {
                    item.getChildren().add(new TreeItem<>());
                }
                parentItem.getChildren().add(item);

                //ツリーを開いたらツリー下のデータを取得
                item.expandedProperty().addListener(workflowTreeChangeListener);
            });

            //名前でソートする
            parentItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getName()));

            WorkflowHierarchyInfoEntity workHierarchyInfo = (WorkflowHierarchyInfoEntity) parentItem.getValue().getEntity();
            workHierarchyInfo.getWorkflowInfoCollection().stream().sorted(Comparator.comparing(workflowInfo -> workflowInfo.getWorkflowName()))
                    .forEach((workflowInfo) -> {
                        workflowInfo = RestAPI.getWorkflow(workflowInfo.getWorkflowId());
                        TreeItem<TreeCellInterface> item = new TreeItem<>(new WorkflowTreeEntity(workflowInfo), new ImageView(fileIcon));
                        parentItem.getChildren().add(item);
                    });

            Platform.runLater(() -> {
                reWorkflowRenderTree();
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * ユニットテンプレートツリーの子階層生成
     *
     * @param parentItem 親階層
     */
    private void createUnitTemplateTreeBranchsThread(TreeItem<TreeCellInterface> parentItem) {
        try {
            blockUI(true);
            parentItem.getChildren().clear();

            //子階層エンティティを取得
            List<UnitTemplateHierarchyInfoEntity> entitys = RestAPI.getUnitTemplateHierarchyChilds(parentItem.getValue().getHierarchyId(), true);

            entitys.stream().forEach((entity) -> {
                TreeItem<TreeCellInterface> item = new TreeItem<>(new UnitTemplateHierarchyTreeEntity(entity), new ImageView(folderIcon));
                if ((entity.getChildCount() > 0) || !(entity.getUnitTemplateCollection().isEmpty())) {
                    item.getChildren().add(new TreeItem<>());
                }
                parentItem.getChildren().add(item);

                //ツリーを開いたらツリー下のデータを取得
                item.expandedProperty().addListener(templateTreeChangeListener);
            });

            //名前でソートする
            parentItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getName()));

            UnitTemplateHierarchyInfoEntity tempHierarchyInfo = (UnitTemplateHierarchyInfoEntity) parentItem.getValue().getEntity();
            tempHierarchyInfo.getUnitTemplateCollection().stream().sorted(Comparator.comparing(tempInfo -> tempInfo.getUnitTemplateName()))
                    .forEach((tempInfo) -> {
                        tempInfo.setTactTime(RestAPI.getUnitTemplateTactTime(tempInfo.getUnitTemplateId()));
                        TreeItem<TreeCellInterface> item = new TreeItem<>(new UnitTemplateTreeEntity(tempInfo), new ImageView(fileIcon));
                        parentItem.getChildren().add(item);
                    });

            Platform.runLater(() -> {
                reUnitTemplateRenderTree();
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
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
