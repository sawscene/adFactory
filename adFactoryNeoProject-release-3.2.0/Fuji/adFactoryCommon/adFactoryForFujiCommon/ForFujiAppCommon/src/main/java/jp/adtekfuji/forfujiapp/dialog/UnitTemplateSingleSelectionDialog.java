/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.dialog;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
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
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import jp.adtekfuji.forfujiapp.clientservice.UnitTemplateHierarchyInfoFacade;
import jp.adtekfuji.forfujiapp.clientservice.UnitTemplateInfoFacade;
import jp.adtekfuji.forfujiapp.dialog.entity.SelectDialogEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.forfujiapp.javafx.tree.UnitTemplateHierarchyTreeCell;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレート選択ダイアログ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.09.Wen
 */
@FxComponent(id = "UnitTemplateSingleSelectionCompo", fxmlPath = "/fxml/dialog/unittemplateSingleSelectionDialog.fxml")
public class UnitTemplateSingleSelectionDialog implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private UnitTemplateHierarchyInfoFacade unittemplateHierarchyInfoFacade;
    private UnitTemplateInfoFacade unitTemplateInfoFacade;

    private SelectDialogEntity<UnitTemplateInfoEntity> settingDialogEntity;
    private final static long ROOT_ID = 0;
    private final static long MAX_LOAD_SIZE = 20;

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
    private TreeView<UnitTemplateHierarchyInfoEntity> hierarchyTree;
    @FXML
    private ListView<UnitTemplateInfoEntity> itemList;
    @FXML
    private Label itemElementName;
    @FXML
    private TextField selectedItemName;
    @FXML
    private Pane progressPane;
    @FXML
    private StackPane stackPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //エンティティメンバーとバインド

        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<UnitTemplateHierarchyInfoEntity>> observable, TreeItem<UnitTemplateHierarchyInfoEntity> oldValue, TreeItem<UnitTemplateHierarchyInfoEntity> newValue) -> {
            if (Objects.nonNull(newValue) && newValue.getValue().getUnitTemplateHierarchyId() != ROOT_ID) {
                updateListView(newValue.getValue().getUnitTemplateCollection());
            } else {
                clearList();
                selectedItemName.setText("");
            }
        });

        itemList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends UnitTemplateInfoEntity> observable, UnitTemplateInfoEntity oldValue, UnitTemplateInfoEntity newValue) -> {
            if (Objects.nonNull(newValue)) {
                this.selectedItemName.setText(newValue.getUnitTemplateName());
                this.settingDialogEntity.setItem(newValue);

                //final long unitTemplateId = newValue.getUnitTemplateId();
                //blockUI(true);
                //Task task = new Task<Void>() {
                //    @Override
                //    protected Void call() throws Exception {
                //        try {
                //            UnitTemplateInfoEntity template = getUnitTemplate(unitTemplateId);
                //            selectedItemName.setText(template.getUnitTemplateName());
                //            settingDialogEntity.setItem(template);
                //        } catch (Exception ex) {
                //            logger.fatal(ex, ex);
                //        } finally {
                //            Platform.runLater(() -> blockUI(false));
                //        }
                //        return null;
                //    }
                //};
                //new Thread(task).start();
            } else {
                settingDialogEntity.setItem(null);
            }
        });
    }

    /**
     * ユニットテンプレートの取得
     *
     * @param selectId
     * @return
     */
    private UnitTemplateInfoEntity getUnitTemplate(long id) {
        UnitTemplateInfoEntity result = new UnitTemplateInfoEntity();
        boolean isContinue = true;
        try {
            while (isContinue) {
                try {
                    result = unitTemplateInfoFacade.find(id);
                    break;
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    isContinue = DialogBox.alert(ex);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * ListView表示用セル
     *
     */
    class ListItemCell extends ListCell<UnitTemplateInfoEntity> {

        @Override
        protected void updateItem(UnitTemplateInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setText(item.getUnitTemplateName());
            } else {
                setText("");
            }
        }
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectDialogEntity) {
            settingDialogEntity = (SelectDialogEntity) argument;
            if (Objects.nonNull(settingDialogEntity.getUribase())) {
                unittemplateHierarchyInfoFacade = new UnitTemplateHierarchyInfoFacade(settingDialogEntity.getUribase());
                unitTemplateInfoFacade = new UnitTemplateInfoFacade(settingDialogEntity.getUribase());
            } else {
                unittemplateHierarchyInfoFacade = new UnitTemplateHierarchyInfoFacade();
                unitTemplateInfoFacade = new UnitTemplateInfoFacade();
            }
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        itemElementName.setText(LocaleUtils.getString("key.unittemplate"));
                        createTreeRootThread();
                        Callback<ListView<UnitTemplateInfoEntity>, ListCell<UnitTemplateInfoEntity>> cellFactory = (ListView<UnitTemplateInfoEntity> param) -> new ListItemCell();
                        itemList.setCellFactory(cellFactory);
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
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        stackPane.setDisable(flg);
        progressPane.setVisible(flg);
    }

    /**
     * ツリーの親階層生成
     *
     */
    private void createTreeRootThread() {
        logger.debug("createTreeRoot start.");
        try {
            TreeItem<UnitTemplateHierarchyInfoEntity> rootItem = new TreeItem<>(new UnitTemplateHierarchyInfoEntity(ROOT_ID, null, LocaleUtils.getString("key.unittemplate")));
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
                        hierarchyCnt = unittemplateHierarchyInfoFacade.findTreeCount(null);
                    }
                    logger.debug("TopHierarchyCnt:{}", hierarchyCnt);
                    isCount = true;
                    for (; nowHierarchyCnt < hierarchyCnt; nowHierarchyCnt += MAX_LOAD_SIZE) {
                        List<UnitTemplateHierarchyInfoEntity> entitys
                                = unittemplateHierarchyInfoFacade.findTreeRange(null, nowHierarchyCnt, nowHierarchyCnt + MAX_LOAD_SIZE - 1, true);

                        entitys.stream().forEach((entity) -> {
                            TreeItem<UnitTemplateHierarchyInfoEntity> item = new TreeItem<>(entity);
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
    private void createTreeBranchsThread(TreeItem<UnitTemplateHierarchyInfoEntity> parentItem) {
        try {
            parentItem.getChildren().clear();
            parentItem.setExpanded(false);

            //親階層が保有する情報の数をカウント
            boolean isCount = false;
            boolean isContinue = true;
            long hierarchyCnt = 0;
            long nowHierarchyCnt = 0;

            while (isContinue) {
                try {
                    if (!isCount) {
                        hierarchyCnt = unittemplateHierarchyInfoFacade.findTreeCount(parentItem.getValue().getUnitTemplateHierarchyId());
                    }
                    logger.debug("TopHierarchyCnt:{}", hierarchyCnt);
                    isCount = true;
                    for (; nowHierarchyCnt < hierarchyCnt; nowHierarchyCnt += MAX_LOAD_SIZE) {
                        List<UnitTemplateHierarchyInfoEntity> entities
                                = unittemplateHierarchyInfoFacade.findTreeRange(parentItem.getValue().getUnitTemplateHierarchyId(), nowHierarchyCnt, nowHierarchyCnt + MAX_LOAD_SIZE - 1, true);

                        entities.stream().forEach((entity) -> {
                            TreeItem<UnitTemplateHierarchyInfoEntity> item = new TreeItem<>(entity);
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
        hierarchyTree.setCellFactory((TreeView<UnitTemplateHierarchyInfoEntity> p) -> new UnitTemplateHierarchyTreeCell());
    }

    private void clearList() {
        itemList.getItems().clear();
        itemList.getSelectionModel().clearSelection();
    }

    /**
     * リスト更新
     *
     * @param entities
     */
    private void updateListView(List<UnitTemplateInfoEntity> entities) {
        itemList.getItems().clear();
        entities.sort(Comparator.comparing(o -> o.getUnitTemplateName()));
        itemList.setItems(FXCollections.observableArrayList(entities));
    }
}
