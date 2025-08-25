/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.dialog;

import jp.adtekfuji.forfujiapp.dialog.entity.SelectDialogEntity;
import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.javafx.tree.KanbanHierarchyTreeCell;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン階層選択ダイアログ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.09.Wen
 */
@FxComponent(id = "KanbanHierarchySingleSelectionCompo", fxmlPath = "/fxml/dialog/kanbanHierarchySingleSelectionDialog.fxml")
public class KanbanHierarchySingleSelectionDialog implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private SelectDialogEntity<KanbanHierarchyInfoEntity> settingDialogEntity;
    private final static long ROOT_ID = 0;
    private final static long MAX_LOAD_SIZE = 20;

    private KanbanHierarchyInfoFacade kanbanHierarchyInfoFacade;

    private final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
        if (Objects.nonNull(newValue) && newValue.equals(true)) {
            TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        treeItem.getChildren().stream().forEach((child) -> {
                            createTreeBranchsThread((TreeItem) child);
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
    };

    @FXML
    private TreeView<KanbanHierarchyInfoEntity> hierarchyTree;
    @FXML
    private Label itemElementName;
    @FXML
    private TextField selectedItemName;
    @FXML
    private StackPane stackPane;
    @FXML
    private Pane progressPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<KanbanHierarchyInfoEntity>> observable, TreeItem<KanbanHierarchyInfoEntity> oldValue, TreeItem<KanbanHierarchyInfoEntity> newValue) -> {
            if (Objects.nonNull(newValue) && newValue.getValue().getKanbanHierarchyId() != ROOT_ID) {
                selectedItemName.setText(newValue.getValue().getHierarchyName());
                settingDialogEntity.getItem().setHierarchyName(newValue.getValue().getHierarchyName());
                settingDialogEntity.getItem().setKanbanHierarchyId(newValue.getValue().getKanbanHierarchyId());
            } else {
                Platform.runLater(() -> {
                    selectedItemName.setText("");
                });
            }
        });
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectDialogEntity) {
            settingDialogEntity = (SelectDialogEntity) argument;
            if (Objects.nonNull(settingDialogEntity.getUribase())) {
                kanbanHierarchyInfoFacade = new KanbanHierarchyInfoFacade(settingDialogEntity.getUribase());
            } else {
                kanbanHierarchyInfoFacade = new KanbanHierarchyInfoFacade();
            }
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        itemElementName.setText(LocaleUtils.getString("key.KanbanHierarch"));
                        createTreeRootThread();
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                    return null;
                }
            };
            new Thread(task).start();
        }
    }

    /**
     * ツリーの親階層生成
     *
     */
    private void createTreeRootThread() {
        logger.debug("createTreeRoot start.");
        try {
            TreeItem<KanbanHierarchyInfoEntity> rootItem = new TreeItem<>(new KanbanHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.KanbanHierarch")));
            Platform.runLater(() -> {
                hierarchyTree.rootProperty().setValue(rootItem);
                reRenderingTree();
            });

            //階層を開いたときのイベントを追加
            rootItem.expandedProperty().addListener(changeListener);

            Boolean isCount = false;
            Boolean isContinue = true;
            long hierarchyCnt = 0;
            long nowHierarchyCnt = 0;
            while (isContinue) {
                try {
                    //親階層の情報を取得
                    if (!isCount) {
                        hierarchyCnt = kanbanHierarchyInfoFacade.getTopHierarchyCount();
                    }
                    logger.debug("TopHierarchyCnt:{}", hierarchyCnt);
                    isCount = true;
                    for (; nowHierarchyCnt < hierarchyCnt; nowHierarchyCnt += MAX_LOAD_SIZE) {
                        List<KanbanHierarchyInfoEntity> entitys
                                = kanbanHierarchyInfoFacade.getTopHierarchyRange(nowHierarchyCnt, nowHierarchyCnt + MAX_LOAD_SIZE - 1);

                        entitys.stream().forEach((entity) -> {
                            TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);
                            rootItem.getChildren().add(item);

                            //階層を開いたときのイベントを追加
                            item.expandedProperty().addListener(changeListener);
                        });
                    }

                    //階層名でソートする
                    rootItem.getChildren().sort(Comparator.comparing(treeItem -> treeItem.getValue().getHierarchyName()));
                    Platform.runLater(() -> {
                        reRenderingTree();
                    });
                    break;
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.debug("createTreeRoot end.");
    }

    /**
     * ツリーの子階層生成
     *
     * @param parentItem 親階層
     */
    private void createTreeBranchsThread(TreeItem<KanbanHierarchyInfoEntity> parentItem) {
        try {
            parentItem.getChildren().clear();
            parentItem.setExpanded(false);

            //親階層が保有する情報の数をカウント
            Boolean isCount = false;
            Boolean isContinue = true;
            long hierarchyCnt = 0;
            long nowHierarchyCnt = 0;
            while (isContinue) {
                try {
                    if (!isCount) {
                        hierarchyCnt = kanbanHierarchyInfoFacade.getAffilationHierarchyCount(parentItem.getValue().getKanbanHierarchyId());
                    }
                    logger.debug("TopHierarchyCnt:{}", hierarchyCnt);
                    isCount = true;
                    for (; nowHierarchyCnt < hierarchyCnt; nowHierarchyCnt += MAX_LOAD_SIZE) {
                        List<KanbanHierarchyInfoEntity> entitys
                                = kanbanHierarchyInfoFacade.getAffilationHierarchyRange(parentItem.getValue().getKanbanHierarchyId(), nowHierarchyCnt, nowHierarchyCnt + MAX_LOAD_SIZE - 1);

                        entitys.stream().forEach((entity) -> {
                            TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);
                            parentItem.getChildren().add(item);

                            //ツリーを開いたらツリー下のデータを取得
                            item.expandedProperty().addListener(changeListener);
                        });
                    }

                    //名前でソートする
                    parentItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getHierarchyName()));

                    Platform.runLater(() -> {
                        reRenderingTree();
                    });
                    break;
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ツリーの再描画
     */
    private void reRenderingTree() {
        hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> p) -> new KanbanHierarchyTreeCell());
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
