/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.component;

import adtekfuji.admanagerapp.entity.ObjectTableData;
import adtekfuji.clientservice.ObjectInfoFacade;
import adtekfuji.clientservice.ObjectTypeInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.object.ObjectInfoEntity;
import jp.adtekfuji.adFactory.entity.object.ObjectTypeInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.treecell.ObjectTypeTreeCell;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author nar-nakamura
 */
@FxComponent(id = "ObjectEditCompo", fxmlPath = "/fxml/compo/object_edit_compo.fxml")
public class ObjectEditCompoFxController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static ObjectInfoFacade objectInfoFacade = new ObjectInfoFacade();
    private final static ObjectTypeInfoFacade objectTypeInfoFacade = new ObjectTypeInfoFacade();
    private final SceneContiner sc = SceneContiner.getInstance();
    private ObjectTypeInfoEntity viewObjectTypeInfoEntity;
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final static long MAX_LOAD_SIZE = 20;
    private final static long MAX_ROLL_HIERARCHY_CNT = 30;
    private final static long MAX_ERROR_SUBINFO_LEN = 40;
    private final static String URI_SPLIT = "/";

    private TreeItem<ObjectTypeInfoEntity> rootItem;

    private final ObservableList<ObjectTableData> tableData = FXCollections.observableArrayList();
    private Long selectedObjectTypeId;
    private String searchIdBuf;
    private String searchNameBuf;

    @FXML
    private SplitPane objectPane;
    @FXML
    private TreeView<ObjectTypeInfoEntity> objectTypeTree;
    @FXML
    private Button deleteTreeButton;
    @FXML
    private Button editTreeButton;
    @FXML
    private Button createTreeButton;

    @FXML
    private TextField searchObjectId;
    @FXML
    private TextField searchObjectName;

    @FXML
    private PropertySaveTableView<ObjectTableData> objectList;
    @FXML
    private TableColumn selectedColumn;
    @FXML
    private TableColumn<ObjectTableData, String> objectIdColumn;
    @FXML
    private TableColumn<ObjectTableData, String> objectNameColumn;

    @FXML
    private Button createObjectButton;
    @FXML
    private Button deleteObjectButton;
    @FXML
    private Button registObjectButton;

    @FXML
    private Pane Progress;
    private Object UriConvertUtils;

    public ObjectEditCompoFxController() {
        this.searchNameBuf = "";
        this.searchIdBuf = "";
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        objectList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        SplitPaneUtils.loadDividerPosition(objectPane, getClass().getSimpleName());

        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            //役割の権限によるボタン無効化.
            deleteTreeButton.setDisable(true);
            editTreeButton.setDisable(true);
            createTreeButton.setDisable(true);
            registObjectButton.setDisable(true);
            deleteObjectButton.setDisable(true);
            createObjectButton.setDisable(true);
            objectList.setEditable(false);
        }
        objectList.init("objectList");

        Callback<TableColumn<ObjectTableData, String>, TableCell<ObjectTableData, String>>
                cellFactory = (TableColumn<ObjectTableData, String> p) -> new ObjectTableCell();

        objectList.setRowFactory((TableView<ObjectTableData> r) -> new ObjectTableRow());

        objectList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        objectList.getSelectionModel().setCellSelectionEnabled(false);

        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));

        // モノID 列
        objectIdColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObjectTableData, String> param) -> param.getValue().objectIdProperty());
        objectIdColumn.setCellFactory(cellFactory);
        objectIdColumn.setOnEditCommit((CellEditEvent<ObjectTableData, String> event) -> {
            ObjectTableData item = event.getTableView().getItems().get(event.getTablePosition().getRow());
            item.setObjectId(event.getNewValue());
        });

        // モノ名 列
        objectNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObjectTableData, String> param) -> param.getValue().objectNameProperty());
        objectNameColumn.setCellFactory(cellFactory);
        objectNameColumn.setOnEditCommit((CellEditEvent<ObjectTableData, String> event) -> {
            ObjectTableData item = event.getTableView().getItems().get(event.getTablePosition().getRow());
            item.setObjectName(event.getNewValue());
        });

        //階層ツリー選択時詳細画面表示処理実行
        objectTypeTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue) && Objects.nonNull(newValue.getValue().getObjectTypeId())) {
                if (!newValue.getValue().getObjectTypeId().equals(selectedObjectTypeId)) {
                    if (this.isChanged()) {
                        // 「入力内容が保存されていません。保存しますか?」を表示
                        String title = LocaleUtils.getString("key.confirm");
                        String message = LocaleUtils.getString("key.confirm.destroy");

                        ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
                        if (ButtonType.YES == buttonType) {
                            if (!this.onRegistObject(null)) {
                                Platform.runLater(() -> objectTypeTree.getSelectionModel().select(oldValue));
                                return;
                            }
                        } else if (ButtonType.CANCEL == buttonType) {
                            return;
                        }
                    }
                    selectedObjectTypeId = newValue.getValue().getObjectTypeId();
                    viewObjectTypeInfoEntity = objectTypeInfoFacade.find(selectedObjectTypeId);
                    Platform.runLater(() -> {
                        updateDetailView(selectedObjectTypeId);
                    });
                }
            } else {
                viewObjectTypeInfoEntity = null;
                selectedObjectTypeId = null;
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

    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            objectTypeTree.getRoot().setExpanded(false);
            runCreateTreeRootThread();
        }
    }

    /**
     * モノ種別 新規作成
     *
     * @param event 新規作成ボタン押下
     */
    @FXML
    private void onTreeCreate(ActionEvent event) {
        try {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.onRegistObject(null)) {
                        return;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }
            
            ObjectTypeInfoEntity objectType = new ObjectTypeInfoEntity();

            if (this.DispObjectTypeDialog(objectType)) {
                //モノ種別を追加
                ResponseEntity res = objectTypeInfoFacade.regist(objectType);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    //ツリー更新
                    objectTypeTree.getRoot().setExpanded(false);
                    updateTreeItemThread(getUriToObjectTypeId(res.getUri()));
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ObjectEdit"),
                            String.format(LocaleUtils.getString("key.FailedToCreate"), LocaleUtils.getString("key.ObjectType")));
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * モノ種別 編集
     *
     * @param event 編集ボタン押下
     */
    @FXML
    private void onTreeEdit(ActionEvent event) {
        try {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.onRegistObject(null)) {
                        return;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }
            
            TreeItem<ObjectTypeInfoEntity> item = objectTypeTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getValue().getObjectTypeId())) {
                return;
            }

            ObjectTypeInfoEntity objectType = objectTypeTree.getSelectionModel().getSelectedItem().getValue();
            String targetObjectTypeName = objectType.getObjectTypeName();

            if (this.DispObjectTypeDialog(objectType)) {
                //モノ種別を更新
                ResponseEntity res = objectTypeInfoFacade.update(objectType);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    //ツリー更新
                    objectTypeTree.getRoot().setExpanded(false);
                    updateTreeItemThread(objectType.getObjectTypeId());
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ObjectEdit"),
                            String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.ObjectType")));
                    // データを戻す
                    objectType.setObjectTypeName(targetObjectTypeName);
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                    // 排他バージョンが異なる。
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ObjectEdit"), LocaleUtils.getString("key.alert.differentVerInfo"));
                    //ツリー更新
                    objectTypeTree.getRoot().setExpanded(false);
                    updateTreeItemThread(objectType.getObjectTypeId());
                } else {
                    // データを戻す
                    objectType.setObjectTypeName(targetObjectTypeName);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * モノ種別 削除
     *
     * @param event 削除ボタン押下
     */
    @FXML
    private void onTreeDelete(ActionEvent event) {
        //削除
        try {
            blockUI(true);
            TreeItem<ObjectTypeInfoEntity> item = objectTypeTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getValue().getObjectTypeId())) {
                return;
            }

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getValue().getObjectTypeName());
            if (ret.equals(ButtonType.CANCEL)) {
                return;
            }

            ObjectTypeInfoEntity objectType = objectTypeTree.getSelectionModel().getSelectedItem().getValue();

            // モノ種別を削除
            ResponseEntity res = objectTypeInfoFacade.delete(objectType);
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                //ツリー更新
                objectTypeTree.getRoot().setExpanded(false);
                updateTreeItemThread(item.getValue().getObjectTypeId());
            } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ObjectEdit"),
                        String.format(LocaleUtils.getString("key.FailedToDelete"), LocaleUtils.getString("key.ObjectType")));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
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
     * モノマスタ 新規作成
     *
     * @param event 新規作成ボタン押下
     */
    @FXML
    private void onCreateObject(ActionEvent event) {
        try {
            if(Objects.nonNull(selectedObjectTypeId)) {
                tableData.add(new ObjectTableData());
                objectList.refresh();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * モノマスタ 削除
     *
     * @param event 削除ボタン押下
     */
    @FXML
    private void onDeleteObject(ActionEvent event) {
        TableColumn sortColumn  = null;
        SortType sortType = null;

        try {
            this.blockUI(true);

            // ソートをクリアしないと、例外が発生する
            if (this.objectList.getSortOrder().size() > 0) {
                sortColumn = (TableColumn) this.objectList.getSortOrder().get(0);
                sortType = sortColumn.getSortType();
                this.objectList.getSortOrder().clear();
            }

            List<ObjectTableData> items = this.tableData.stream().filter(o -> o.isSelected()).collect(Collectors.toList());
            for (ObjectTableData item: items) {
                item.setIsDeleted(true);
                if (item.isCreated()) {
                    tableData.remove(item);
                } else {
                    Platform.runLater(() -> {
                        filterDetailView(searchIdBuf, searchNameBuf);
                    });
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.objectList.refresh();
            if (Objects.nonNull(sortColumn)) {
                this.objectList.getSortOrder().add(sortColumn);
                sortColumn.setSortType(sortType);
                sortColumn.setSortable(true);
            }
            this.blockUI(false);
        }
    }

    /**
     * モノマスタ 登録
     *
     * @param event 登録ボタン押下
     */
    @FXML
    private boolean onRegistObject(ActionEvent event) {
        boolean ret = true;
        try {
            blockUI(true);

            final boolean isEmpty = tableData.stream()
                    .filter(row -> Objects.isNull(row.getObjectId())
                            || row.getObjectId().isEmpty()
                            || Objects.isNull(row.getObjectName())
                            || row.getObjectName().isEmpty())
                    .count() > 0;

            if (isEmpty) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return false;
            }

            for (int i = tableData.size() - 1; i >= 0; i--) {
                ObjectTableData row = tableData.get(i);

                if (row.isDeleted()) {
                    logger.debug("onRegistObject(remove): isEdited={}, objectId={}, objectName={}", row.isEdited(), row.getObjectId(), row.getObjectName());
                    // モノを削除
                    ResponseEntity res = objectInfoFacade.delete(row.getObjectInfoEntity());
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        tableData.remove(i);
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ObjectEdit"),
                                String.format(LocaleUtils.getString("key.FailedToDelete") + "\n\n%s:%s\n%s:%s", LocaleUtils.getString("key.Object"),
                                        LocaleUtils.getString("key.ObjectId"), adtekfuji.utility.StringUtils.getShortName(row.getObjectId(), MAX_ERROR_SUBINFO_LEN),
                                        LocaleUtils.getString("key.ObjectName"), adtekfuji.utility.StringUtils.getShortName(row.getObjectName(), MAX_ERROR_SUBINFO_LEN)));
                        ret = false;
                    }
                } else if (row.isCreated()) {
                    logger.debug("onRegistObject(create): isEdited={}, objectId={}, objectName={}", row.isEdited(), row.getObjectId(), row.getObjectName());
                    ObjectInfoEntity objectInfo = new ObjectInfoEntity(row.getObjectId(), selectedObjectTypeId, row.getObjectName());
                    objectInfo.setUpdatePersonId(loginUserInfoEntity.getId());
                    //モノを追加
                    ResponseEntity res = objectInfoFacade.regist(objectInfo);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        ObjectInfoEntity registed = objectInfoFacade.find(getUriToObjectId(res.getUri()), objectInfo.getFkObjectTypeId());
                        row.setObjectInfoEntity(registed);
                        row.setIsCreated(false);
                        row.setIsEdited(false);
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ObjectEdit"),
                                String.format(LocaleUtils.getString("key.FailedToCreate") + "\n\n%s:%s\n%s:%s", LocaleUtils.getString("key.Object"),
                                        LocaleUtils.getString("key.ObjectId"), adtekfuji.utility.StringUtils.getShortName(row.getObjectId(), MAX_ERROR_SUBINFO_LEN),
                                        LocaleUtils.getString("key.ObjectName"), adtekfuji.utility.StringUtils.getShortName(row.getObjectName(), MAX_ERROR_SUBINFO_LEN)));
                        ret = false;
                    }
                } else if (row.isEdited()) {
                    logger.debug("onRegistObject(update): isEdited={}, objectId={}, objectName={}", row.isEdited(), row.getObjectId(), row.getObjectName());
                    ObjectInfoEntity target = row.getObjectInfoEntity();

                    ObjectInfoEntity objectInfo = new ObjectInfoEntity(target);
                    objectInfo.setObjectId(row.getObjectId());
                    objectInfo.setFkObjectTypeId(selectedObjectTypeId);
                    objectInfo.setObjectName(row.getObjectName());

                    target.setUpdatePersonId(loginUserInfoEntity.getId());
                    //モノを更新
                    ResponseEntity res = objectInfoFacade.update(target.getObjectId(), target.getFkObjectTypeId(), objectInfo);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        row.setIsEdited(false);
                        row.setObjectInfoEntity(objectInfo);
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ObjectEdit"),
                                String.format(LocaleUtils.getString("key.FailedToUpdate") + "\n\n%s:%s\n%s:%s", LocaleUtils.getString("key.Object"),
                                        LocaleUtils.getString("key.ObjectId"), adtekfuji.utility.StringUtils.getShortName(row.getObjectId(), MAX_ERROR_SUBINFO_LEN),
                                        LocaleUtils.getString("key.ObjectName"), adtekfuji.utility.StringUtils.getShortName(row.getObjectName(), MAX_ERROR_SUBINFO_LEN)));
                        ret = false;
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                        // 排他バージョンが異なる。
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditKanbanTitle"),
                                new StringBuilder(LocaleUtils.getString("key.alert.differentVerInfo"))
                                        .append("\n\n")
                                        .append(LocaleUtils.getString("key.ObjectId"))
                                        .append(":")
                                        .append(adtekfuji.utility.StringUtils.getShortName(row.getObjectId(), MAX_ERROR_SUBINFO_LEN))
                                        .append("\n")
                                        .append(LocaleUtils.getString("key.ObjectName"))
                                        .append(":")
                                        .append(adtekfuji.utility.StringUtils.getShortName(row.getObjectName(), MAX_ERROR_SUBINFO_LEN))
                                        .toString());
                        ret = false;
                    } else if (!res.isSuccess()) {
                        ret = false;
                    }
                }
            }
            objectList.refresh();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
        return ret;
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
     * @param parentItem
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
     */
    private void updateDetailView(Long objectTypeId) {
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
                    tableData.add(new ObjectTableData(e));
                });
            }
        }

        filterDetailView("", "");

        this.objectList.getSortOrder().clear();
        this.objectList.getSortOrder().add(objectIdColumn);
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

        FilteredList<ObjectTableData> filteredData;
        SortedList<ObjectTableData> sortedData;
        ObservableList<ObjectTableData> dispData;

        if (!searchId.isEmpty() && !searchName.isEmpty()) {
            filteredData = this.tableData.filtered(o -> o.getObjectId().isEmpty() || (!o.isDeleted() && o.getObjectId().contains(searchId) && o.getObjectName().contains(searchName)));
        } else if (!searchId.isEmpty() && searchName.isEmpty()) {
            filteredData = this.tableData.filtered(o -> o.getObjectId().isEmpty() || (!o.isDeleted() && o.getObjectId().contains(searchId)));
        } else if (searchId.isEmpty() && !searchName.isEmpty()) {
            filteredData = this.tableData.filtered(o -> o.getObjectId().isEmpty() || (!o.isDeleted() && o.getObjectName().contains(searchName)));
        } else {
            filteredData = this.tableData.filtered(o -> o.getObjectId().isEmpty() || !o.isDeleted());
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
     * モノ種別編集ダイアログを表示する。
     *
     * @param objectType
     * @return OK押下？
     */
    private boolean DispObjectTypeDialog(ObjectTypeInfoEntity objectType) {
        try {
                String orgName = objectType.getObjectTypeName();
                if (Objects.isNull(orgName)) {
                    orgName = "";
                }
                String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.ObjectTypeName"));
                String newName = sc.showTextInputDialog(LocaleUtils.getString("key.Edit"), message, LocaleUtils.getString("key.ObjectTypeName"), orgName);

                if (Objects.nonNull(newName)) {
                    newName = adtekfuji.utility.StringUtils.trim2(newName);
                    if (newName.isEmpty()) {
                        sc.showAlert(Alert.AlertType.WARNING, message, message);
                    } else if (!Objects.equals(newName, orgName)) {
                        objectType.setObjectTypeName(newName);
                        return true;
                    }
                }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return false;
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
     * コンポーネントが破棄される前に呼び出される。破棄できない場合は、falseを返す。
     *
     * @return
     */
    @Override
    public boolean destoryComponent() {
        try {
            logger.info("destoryComponent start.");

            SplitPaneUtils.saveDividerPosition(objectPane, getClass().getSimpleName());

            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.onRegistObject(null)) {
                        return false;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return false;
                }
            }

            return true;
        } finally {
            logger.info("destoryComponent end.");
        }
    }

    /**
     * 内容が変更されたかどうかを返す。
     *
     * @return
     */
    private boolean isChanged() {
        // 編集権限なしは常に無変更
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            return false;
        }

        boolean isChanged = false;
        for (int i = tableData.size() - 1; i >= 0; i--) {
            ObjectTableData row = tableData.get(i);
            if (row.isDeleted() || row.isCreated() || row.isEdited()) {
                isChanged = true;
                break;
            }
        }
        return isChanged;
    }

    /**
     * モノ種別登録結果URIからモノ種別IDを取得する。
     * 
     * @param uri モノ種別登録結果URI
     * @return モノ種別ID
     */
    public static long getUriToObjectTypeId(String uri) {
        long ret = 0;
        try {
            String[] split = uri.split(URI_SPLIT);
            if (split.length == 2) {
                ret = Long.parseLong(split[1]);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * モノ登録結果URIからモノIDを取得する。
     * 
     * @param uri モノ登録結果URI
     * @return モノID
     */
    public static String getUriToObjectId(String uri) {
        String ret = null;
        try {
            String[] split = uri.split(URI_SPLIT);
            if (split.length == 2) {
                ret = split[1];
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * テキスト編集セル
     */
    class ObjectTableCell extends TableCell<ObjectTableData, String> {

        private TextField textField;
        private TablePosition<ObjectTableData, ?> tablePos = null;

        /**
         * コンストラクタ
         */
        public ObjectTableCell() {
        }

        /**
         * 編集開始
         */
        @Override
        public void startEdit() {
            if (this.isEmpty()) {
                return;
            }

            super.startEdit();

            // 編集中のセル
            final TableView<ObjectTableData> table = this.getTableView();
            this.tablePos = table.getEditingCell();

            this.textField = new TextField(this.getString());
            this.textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

            this.textField.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    commitEdit(textField.getText());
                } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                    super.cancelEdit();
                    this.setText(this.getString());
                    this.setGraphic(null);
                }
            });

            this.textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (!newValue && this.isEditing()){
                    commitEdit(textField.getText());
                }
            });

            this.setText(null);
            this.setGraphic(this.textField);
            this.textField.selectAll();
        }

        /**
         * 編集キャンセル
         */
        @Override
        public void cancelEdit() {
            // EditCommitイベントを発生
            final TableView<ObjectTableData> table = this.getTableView();
            CellEditEvent editEvent = new CellEditEvent(table, this.tablePos, TableColumn.editCommitEvent(), textField.getText());
            Event.fireEvent(getTableColumn(), editEvent);

            super.cancelEdit();

            this.setText(textField.getText());
            this.setGraphic(null);

            table.edit(-1, null);
        }

        /**
         * 編集完了
         *
         * @param newValue
         */
        @Override
        public void commitEdit(String newValue) {
            super.commitEdit(newValue);
            this.setText(newValue);
        }

        /**
         * セル更新
         */
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                this.setText(null);
                this.setGraphic(null);
            } else {
                if (this.isEditing()) {
                    if (this.textField != null) {
                        this.textField.setText(this.getString());
                    }
                    this.setText(null);
                    this.setGraphic(this.textField);
                } else {
                    this.setText(this.getString());
                    this.setGraphic(null);
                }
            }
        }

        private String getString() {
            return this.getItem() == null ? "" : this.getItem();
        }
    }
}
