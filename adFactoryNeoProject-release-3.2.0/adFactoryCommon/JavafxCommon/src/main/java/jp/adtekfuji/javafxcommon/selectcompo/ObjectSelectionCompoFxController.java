/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.selectcompo;

import adtekfuji.clientservice.ObjectInfoFacade;
import adtekfuji.clientservice.ObjectTypeInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import adtekfuji.locale.LocaleUtils;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.object.ObjectInfoEntity;
import jp.adtekfuji.adFactory.entity.object.ObjectTypeInfoEntity;
import jp.adtekfuji.javafxcommon.treecell.ObjectTypeTreeCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author nar-nakamura
 */
@FxComponent(id = "ObjectSelectionCompo", fxmlPath = "/fxml/compo/object_selection_compo.fxml")
public class ObjectSelectionCompoFxController implements Initializable, ArgumentDelivery {

    private final static Logger logger = LogManager.getLogger();
    private final static ObjectInfoFacade objectInfoFacade = new ObjectInfoFacade();
    private final static ObjectTypeInfoFacade objectTypeInfoFacade = new ObjectTypeInfoFacade();
    private final SceneContiner sc = SceneContiner.getInstance();
    private ObjectTypeInfoEntity viewObjectTypeInfoEntity;

    private final static long MAX_LOAD_SIZE = 20;
    private final static long MAX_ROLL_HIERARCHY_CNT = 30;
    private TreeItem<ObjectTypeInfoEntity> rootItem;

    private final ObservableList<ObjectListItem> tableData = FXCollections.observableArrayList();
    private final ObservableList<ObjectListItem> selectedData = FXCollections.observableArrayList();
    private Long selectedObjectTypeId;
    private String searchIdBuf;
    private String searchNameBuf;

    private SelectDialogEntity settingDialogEntity;

    @FXML
    private TreeView<ObjectTypeInfoEntity> objectTypeTree;

    @FXML
    private TextField searchObjectId;
    @FXML
    private TextField searchObjectName;

    @FXML
    private TableView<ObjectListItem> objectList;
    @FXML
    private TableColumn<ObjectListItem, String> objectIdColumn;
    @FXML
    private TableColumn<ObjectListItem, String> objectNameColumn;

    @FXML
    private ListView<ObjectInfoEntity> itemList;
    @FXML
    private TableView<ObjectListItem> selectedItemList;
    @FXML
    private TableColumn<ObjectListItem, String> selectedObjectTypeNameColumn;
    @FXML
    private TableColumn<ObjectListItem, String> selectedObjectIdColumn;
    @FXML
    private TableColumn<ObjectListItem, String> selectedObjectNameColumn;

    @FXML
    private Button searchButton;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;

    @FXML
    private Pane Progress;

    /**
     * コンストラクタ
     */
    public ObjectSelectionCompoFxController() {
        this.searchNameBuf = "";
        this.searchIdBuf = "";
        this.itemList = new ListView();
    }

    /**
     * モノダイアログを初期化する。
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);

        objectList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        selectedItemList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        // モノリスト
        objectList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        objectList.getSelectionModel().setCellSelectionEnabled(false);

        objectIdColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObjectListItem, String> param) -> param.getValue().objectIdProperty());
        objectIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        objectNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObjectListItem, String> param) -> param.getValue().objectNameProperty());
        objectNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // 選択リスト
        selectedItemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedItemList.getSelectionModel().setCellSelectionEnabled(false);

        selectedObjectNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObjectListItem, String> param) -> param.getValue().objectTypeNameProperty());
        selectedObjectNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        selectedObjectTypeNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObjectListItem, String> param) -> param.getValue().objectTypeNameProperty());
        selectedObjectTypeNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        selectedObjectIdColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObjectListItem, String> param) -> param.getValue().objectIdProperty());
        selectedObjectIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        selectedObjectNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObjectListItem, String> param) -> param.getValue().objectNameProperty());
        selectedObjectNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

    }

    /**
     * 引数を設定する。
     * 
     * @param argument 
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectDialogEntity) {
            settingDialogEntity = (SelectDialogEntity) argument;
            itemList.getItems().addAll(settingDialogEntity.getObjects());
            settingDialogEntity.objectsProperty().bind(itemList.itemsProperty());
            selectedData.clear();

            //階層ツリー選択時詳細画面表示処理実行
            objectTypeTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<ObjectTypeInfoEntity>> observable, TreeItem<ObjectTypeInfoEntity> oldValue, TreeItem<ObjectTypeInfoEntity> newValue) -> {
                if (Objects.nonNull(newValue) && Objects.nonNull(newValue.getValue().getObjectTypeId())) {
                    selectedObjectTypeId = newValue.getValue().getObjectTypeId();
                    viewObjectTypeInfoEntity = objectTypeInfoFacade.find(selectedObjectTypeId);
                    Platform.runLater(() -> {
                        updateDetailView(selectedObjectTypeId, newValue.getValue().getObjectTypeName());
                    });
                } else {
                    viewObjectTypeInfoEntity = null;
                    Platform.runLater(() -> {
                        clearDetailView();
                    });
                }
            });

            //ツリー情報表示処理実行
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        createTreeRootThread();
                        Platform.runLater(() -> {
                            try {
                                createSelectionPane();
                            } catch (Exception ex) {
                                logger.fatal(ex, ex);
                            } finally {
                                blockUI(false);
                            }
                        });

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        Platform.runLater(() -> {
                            blockUI(false);
                        });
                    }
                    return null;
                }
            };
            new Thread(task).start();
        }
    }

    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            objectTypeTree.getRoot().setExpanded(false);
            runCreateTreeRootThread();
        }
    }

    /**
     * モノマスタ 検索
     *
     * @param event 検索ボタン押下
     */
    @FXML
    private void onSearch(ActionEvent event) {
        try {
            blockUI(true);
            searchIdBuf = this.searchObjectId.getText();
            searchNameBuf = this.searchObjectName.getText();

            Platform.runLater(() -> {
                filterDetailView(searchIdBuf, searchNameBuf);
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 追加
     *
     * @param event 追加ボタン押下
     */
    @FXML
    private void onAdd(ActionEvent event) {
        try {
            blockUI(true);
            objectList.getSelectionModel().getSelectedItems().stream().forEach(src -> {
                for (ObjectListItem dst : selectedData) {
                    if (Objects.equals(dst.getObjectId(), src.getObjectId())
                            && Objects.equals(dst.getObjectTypeId(), src.getObjectTypeId())) {
                        return;
                    }
                }
                selectedData.add(src);
                itemList.getItems().add(src.getObjectInfoEntity());
            });
            selectedItemList.setItems(selectedData);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 削除
     *
     * @param event 削除ボタン押下
     */
    @FXML
    private void onRemove(ActionEvent event) {
        try {
            blockUI(true);
            selectedItemList.getSelectionModel().getSelectedItems().forEach(dat -> {
                itemList.getItems().remove(dat.getObjectInfoEntity());
            });

            selectedData.removeAll(selectedItemList.getSelectionModel().getSelectedItems());
            selectedItemList.getSelectionModel().clearSelection();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * ツリーの再描画
     */
    private void reRenderTree() {
        objectTypeTree.setCellFactory((TreeView<ObjectTypeInfoEntity> p) -> new ObjectTypeTreeCell());
    }

    /**
     * ツリーデータの再取得 引数parentItemの子を再取得する
     *
     * @param objectTypeId 再取得後に選択するID
     */
    private void updateTreeItemThread(Long objectTypeId) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    TreeItem<ObjectTypeInfoEntity> root = objectTypeTree.getRoot();
                    createTreeRootThread();

                    Platform.runLater(() -> {
                        blockUI(false);
                        root.setExpanded(true);
                        selectedTreeItem(root, objectTypeId);
                    });
                } catch (Exception ex) {
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
     * @param objectTypeId
     */
    private void selectedTreeItem(TreeItem<ObjectTypeInfoEntity> parentItem, Long objectTypeId) {
        Optional<TreeItem<ObjectTypeInfoEntity>> find = parentItem.getChildren().stream().
                filter(p -> p.getValue().getObjectTypeId().equals(objectTypeId)).findFirst();

        if (find.isPresent()) {
            objectTypeTree.getSelectionModel().select(find.get());
        }
    }

    /**
     * ツリー表示情報更新処理
     *
     */
    private void createTreeRootThread() {
        logger.debug("updateTree.");

        if (Objects.isNull(rootItem)) {
            //ツリールート作成
            rootItem = new TreeItem<>(new ObjectTypeInfoEntity(null, LocaleUtils.getString("key.ObjectType")));
            Platform.runLater(() -> {
                objectTypeTree.rootProperty().setValue(rootItem);
                reRenderTree();
            });
        }

        rootItem.getChildren().clear();
        rootItem.setExpanded(false);

        //親階層の情報を取得
        long topHierarchyCnt = objectTypeInfoFacade.count();

        logger.debug("The number of information that belongs to the child hierarchy:{}", topHierarchyCnt);
        for (long nowTopHierarchyCnt = 0; nowTopHierarchyCnt <= topHierarchyCnt;) {
            List<ObjectTypeInfoEntity> entitys = objectTypeInfoFacade.findRange(nowTopHierarchyCnt, nowTopHierarchyCnt + MAX_ROLL_HIERARCHY_CNT - 1);

            entitys.stream().forEach((entity) -> {
                TreeItem<ObjectTypeInfoEntity> item = new TreeItem<>(entity);
                rootItem.getChildren().add(item);
            });

            nowTopHierarchyCnt += MAX_ROLL_HIERARCHY_CNT;
        }

        //名前でソートする
        rootItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getObjectTypeName()));

        Platform.runLater(() -> {
            reRenderTree();
        });

        logger.debug("updateTree end.");
    }

    private void runCreateTreeRootThread() {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    createTreeRootThread();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
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
     * 詳細画面更新処理
     *
     * @param objectTypeId モノ種別
     * @param objectTypeName モノ種別名
     */
    private void updateDetailView(Long objectTypeId, String objectTypeName) {
        if (Objects.isNull(viewObjectTypeInfoEntity)) {
            return;
        }

        clearDetailView();

        if (objectTypeId == 0L) {
            return;
        }

        long affilationHierarchyCnt = objectInfoFacade.getAffilationHierarchyCount(objectTypeId);

        for (long nowHierarchyCnt = 0; nowHierarchyCnt < affilationHierarchyCnt; nowHierarchyCnt += MAX_LOAD_SIZE) {
            List<ObjectInfoEntity> entities = objectInfoFacade.getAffilationHierarchyRange(objectTypeId, nowHierarchyCnt, nowHierarchyCnt + MAX_LOAD_SIZE - 1);

            if (!entities.isEmpty()) {
                entities.stream().forEach((e) -> {
                    tableData.add(new ObjectListItem(objectTypeName, e));
                });
            }
        }

        filterDetailView("", "");

        objectList.getSortOrder().clear();
        objectList.getSortOrder().add(objectIdColumn);
    }

    /**
     * 詳細画面更新処理
     *
     * @param searchId 検索条件モノID
     * @param searchName 検索条件モノ名
     */
    private void filterDetailView(String searchId, String searchName) {
        if (Objects.isNull(viewObjectTypeInfoEntity)) {
            return;
        }

        objectList.getSelectionModel().clearSelection();

        FilteredList<ObjectListItem> filteredData;
        SortedList<ObjectListItem> sortedData;
        ObservableList<ObjectListItem> dispData;

        if (!searchId.isEmpty() && !searchName.isEmpty()) {
            filteredData = this.tableData.filtered(o -> o.getObjectId().contains(searchId) && o.getObjectName().contains(searchName));
        } else if (!searchId.isEmpty() && searchName.isEmpty()) {
            filteredData = this.tableData.filtered(o -> o.getObjectId().contains(searchId));
        } else if (searchId.isEmpty() && !searchName.isEmpty()) {
            filteredData = this.tableData.filtered(o -> o.getObjectName().contains(searchName));
        } else {
            filteredData = this.tableData.filtered(o -> !o.getObjectId().isEmpty());
        }

        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(objectList.comparatorProperty());
        dispData = sortedData;

        objectList.setItems(dispData);
    }

    /**
     * 詳細画面初期化
     */
    private void clearDetailView() {
        searchObjectId.clear();
        searchObjectName.clear();
        tableData.clear();
        objectList.getSelectionModel().clearSelection();
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        Progress.setVisible(flg);
    }

    /**
     * 選択リスト作成
     */
    private void createSelectionPane() {
        // 階層ツリーrootの取得
        TreeItem<ObjectTypeInfoEntity> root = objectTypeTree.getRoot();
        // 選択リスト作成
        itemList.getItems().forEach(entity -> {
            String objectTypeName = "";
            // 階層ツリーから階層情報を取得する。
            Optional<TreeItem<ObjectTypeInfoEntity>> find = root.getChildren().stream()
                    .filter(p -> p.getValue().getObjectTypeId().equals(entity.getFkObjectTypeId())).findFirst();
            if (find.isPresent()) {
                // 階層情報が取得できた場合、モノ種別名を取得する。
                TreeItem<ObjectTypeInfoEntity> treeItem = find.get();
                ObjectTypeInfoEntity objectTypeInfoEntity = treeItem.getValue();
                objectTypeName = objectTypeInfoEntity.getObjectTypeName();
            }
            selectedData.add(new ObjectListItem(objectTypeName, entity));
        });
        selectedItemList.setItems(selectedData);
    }
}
