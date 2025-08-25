/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.admanagerapp.systemsettingplugin.common.WorkTableRow;
import adtekfuji.admanagerapp.systemsettingplugin.entity.WorkTableData;
import adtekfuji.clientservice.IndirectWorkInfoFacade;
import adtekfuji.clientservice.WorkCategoryInfoFacade;
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
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.indirectwork.WorkCategoryInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.treecell.WorkCategoryTreeCell;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author s-maeda
 */
@FxComponent(id = "WorkCategoryEditCompo",
        fxmlPath = "/fxml/admanagersystemsettingplugin/work_category_edit_compo.fxml")
public class WorkCategoryEditCompoFxController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static IndirectWorkInfoFacade indirectWorkInfoFacade = new IndirectWorkInfoFacade();
    private final static WorkCategoryInfoFacade workCategoryInfoFacade = new WorkCategoryInfoFacade();
    private final SceneContiner sc = SceneContiner.getInstance();
    private WorkCategoryInfoEntity viewWorkCategoryInfoEntity;

    private final static long ROOT_ID = 0;

    private final static long MAX_LOAD_SIZE = 20;
    private final static long MAX_ROLL_HIERARCHY_CNT = 30;
    private final static long MAX_ERROR_SUBINFO_LEN = 40;
    private final static String URI_SPLIT = "/";

    private TreeItem<WorkCategoryInfoEntity> rootItem;

    private final ObservableList<WorkTableData> tableData = FXCollections.observableArrayList();
    private Long selectedWorkCategoryId;

    @FXML
    private SplitPane workCategoryPane;
    @FXML
    private TreeView<WorkCategoryInfoEntity> workCategoryTree;
    @FXML
    private Button deleteTreeButton;
    @FXML
    private Button editTreeButton;
    @FXML
    private Button createTreeButton;

    @FXML
    private Pane workPane;
    @FXML
    private PropertySaveTableView<WorkTableData> workList;
    @FXML
    private TableColumn selectedColumn;
    @FXML
    private TableColumn<WorkTableData, String> workIdColumn;
    @FXML
    private TableColumn<WorkTableData, String> workNameColumn;
    @FXML
    private Button moveWorkButton;
    @FXML
    private Button addWorkButton;
    @FXML
    private Button deleteWorkButton;
    @FXML
    private Button registWorkButton;
    @FXML
    private Pane Progress;

    private CheckBox allCheck;

    private Object UriConvertUtils;

    public WorkCategoryEditCompoFxController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        workList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        SplitPaneUtils.loadDividerPosition(workCategoryPane, getClass().getSimpleName());

        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            //役割の権限によるボタン無効化.
            deleteTreeButton.setDisable(true);
            editTreeButton.setDisable(true);
            createTreeButton.setDisable(true);
            moveWorkButton.setDisable(true);
            addWorkButton.setDisable(true);
            deleteWorkButton.setDisable(true);
            registWorkButton.setDisable(true);
            workList.setEditable(false);
        }

        workList.init("workList");

        Callback<TableColumn<WorkTableData, String>, TableCell<WorkTableData, String>> cellFactory
                = (TableColumn<WorkTableData, String> p) -> new WorkTableCell();

        workList.setRowFactory((TableView<WorkTableData> r) -> new WorkTableRow());

        workList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        workList.getSelectionModel().setCellSelectionEnabled(false);

        allCheck = new CheckBox();
        allCheck.setOnAction((ActionEvent event) -> {
            if (allCheck.isSelected()) {
                for (WorkTableData row : workList.getItems()) {
                    row.setSelected(Boolean.TRUE);
                }
            } else {
                for (WorkTableData row : workList.getItems()) {
                    row.setSelected(Boolean.FALSE);
                }
            }
        });
        selectedColumn.setGraphic(allCheck);
        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));

        // 作業番号 列
        workIdColumn.setCellValueFactory((
                TableColumn.CellDataFeatures<WorkTableData, String> param)
                -> param.getValue().workNumberProperty());
        workIdColumn.setCellFactory(cellFactory);
        workIdColumn.setOnEditCommit((CellEditEvent<WorkTableData, String> event) -> {
            WorkTableData item = event.getTableView().getItems().get(event.getTablePosition().getRow());
            item.setWorkNumber(event.getNewValue());
        });

        // 作業名 列
        workNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<WorkTableData, String> param)
                -> param.getValue().workNameProperty());
        workNameColumn.setCellFactory(cellFactory);
        workNameColumn.setOnEditCommit((CellEditEvent<WorkTableData, String> event) -> {
            WorkTableData item = event.getTableView().getItems().get(event.getTablePosition().getRow());
            item.setWorkName(event.getNewValue());
        });

        //階層ツリー選択時詳細画面表示処理実行
        workCategoryTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue) && Objects.nonNull(newValue.getValue().getWorkCategoryId())) {
                if (!newValue.getValue().getWorkCategoryId().equals(selectedWorkCategoryId)) {
                    if (this.isChanged()) {
                        // 「入力内容が保存されていません。保存しますか?」を表示
                        String title = LocaleUtils.getString("key.confirm");
                        String message = LocaleUtils.getString("key.confirm.destroy");

                        ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE,
                                title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
                        if (ButtonType.YES == buttonType) {
                            if (!this.registIndirectWork()) {
                                Platform.runLater(() -> workCategoryTree.getSelectionModel().select(oldValue));
                                return;
                            }
                        } else if (ButtonType.CANCEL == buttonType) {
                            return;
                        }
                    }
                    selectedWorkCategoryId = newValue.getValue().getWorkCategoryId();
                    viewWorkCategoryInfoEntity = workCategoryInfoFacade.find(selectedWorkCategoryId);
                    Platform.runLater(() -> {
                        updateDetailView(selectedWorkCategoryId);
                    });
                }
            } else {
                viewWorkCategoryInfoEntity = null;
                selectedWorkCategoryId = null;
                Platform.runLater(() -> {
                    clearDetailView();
                });
            }
        });
        
        this.workPane.setDisable(true);
        
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
                        workCategoryTree.getRoot().setExpanded(true);
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
            workCategoryTree.getRoot().setExpanded(false);
            runCreateTreeRootThread();
        }
    }

    /**
     * 作業区分 新規作成
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

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registIndirectWork()) {
                        return;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }

            WorkCategoryInfoEntity workCategory = new WorkCategoryInfoEntity();

            if (this.DispWorkCategoryDialog(workCategory)) {
                //作業区分を追加
                ResponseEntity res = workCategoryInfoFacade.regist(workCategory);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    //ツリー更新
                    workCategoryTree.getRoot().setExpanded(false);
                    updateTreeItemThread(getUriToRegistedItemId(res.getUri()));
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.WorkCategoryEdit"),
                            String.format(LocaleUtils.getString("key.FailedToCreate"), LocaleUtils.getString("key.WorkClassification")));
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 作業区分 編集
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

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registIndirectWork()) {
                        return;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }

            TreeItem<WorkCategoryInfoEntity> item = workCategoryTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getValue().getWorkCategoryId())) {
                return;
            }

            WorkCategoryInfoEntity workCategory = workCategoryTree.getSelectionModel().getSelectedItem().getValue();
            String targetWorkCategoryName = workCategory.getWorkCategoryName();

            if (this.DispWorkCategoryDialog(workCategory)) {
                //作業区分を更新
                ResponseEntity res = workCategoryInfoFacade.update(workCategory);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    //ツリー更新
                    workCategoryTree.getRoot().setExpanded(false);
                    updateTreeItemThread(workCategory.getWorkCategoryId());
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.WorkCategoryEdit"),
                            String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.WorkClassification")));
                    // データを戻す
                    workCategory.setWorkCategoryName(targetWorkCategoryName);
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                    // 排他バージョンが異なる。
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.WorkCategoryEdit"), LocaleUtils.getString("key.alert.differentVerInfo"));
                    //ツリー更新
                    workCategoryTree.getRoot().setExpanded(false);
                    updateTreeItemThread(workCategory.getWorkCategoryId());
                } else {
                    // データを戻す
                    workCategory.setWorkCategoryName(targetWorkCategoryName);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 作業区分 削除
     *
     * @param event 削除ボタン押下
     */
    @FXML
    private void onTreeDelete(ActionEvent event) {
        //削除
        try {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registIndirectWork()) {
                        return;
                    }
                    this.updateDetailView(selectedWorkCategoryId);
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }

            blockUI(true);
            TreeItem<WorkCategoryInfoEntity> item = workCategoryTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getValue().getWorkCategoryId())) {
                return;
            }

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION,
                    LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getValue().getWorkCategoryName());
            if (ret.equals(ButtonType.CANCEL)) {
                return;
            }

            WorkCategoryInfoEntity workCategory = workCategoryTree.getSelectionModel().getSelectedItem().getValue();

            // 作業区分を削除
            ResponseEntity res = workCategoryInfoFacade.delete(workCategory);
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                //ツリー更新
                workCategoryTree.getRoot().setExpanded(false);
                updateTreeItemThread(item.getValue().getWorkCategoryId());
            } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.EXIST_RELATION_DELETE)) {
                sc.showAlert(Alert.AlertType.WARNING,
                        LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.WorkCategory.Delete.RelationExist"));
            } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.WorkCategoryEdit"),
                        String.format(LocaleUtils.getString("key.FailedToDelete"), LocaleUtils.getString("key.WorkClassification")));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 間接作業 追加
     *
     * @param event 追加ボタン押下
     */
    @FXML
    private void onAddWork(ActionEvent event) {
        try {
            if (Objects.nonNull(selectedWorkCategoryId)) {
                tableData.add(new WorkTableData());
                workList.refresh();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 間接作業 削除
     *
     * @param event 削除ボタン押下
     */
    @FXML
    private void onDeleteWork(ActionEvent event) {
        TableColumn sortColumn = null;
        SortType sortType = null;

        try {
            this.blockUI(true);

            // ソートをクリアしないと、例外が発生する
            if (this.workList.getSortOrder().size() > 0) {
                sortColumn = (TableColumn) this.workList.getSortOrder().get(0);
                sortType = sortColumn.getSortType();
                this.workList.getSortOrder().clear();
            }

            List<WorkTableData> items
                    = this.tableData
                    .stream()
                    .filter(WorkTableData::isSelected)
                    .collect(Collectors.toList());

            if (items
                    .stream()
                    .map(WorkTableData::getWorkInfoEntity)
                    .map(IndirectWorkInfoEntity::getIndirectWorkId)
                    .map(indirectWorkInfoFacade::getActiveIndirectWorkCount)
                    .anyMatch(count -> count > 0)) {
                sc.showAlert(
                        Alert.AlertType.ERROR,
                        LocaleUtils.getString("key.DeleteIndirectWork"),
                        LocaleUtils.getString("key.DeleteIndirectWorkDetail"));
                return;
            }



            for (WorkTableData item : items) {
                item.setIsDeleted(true);
                if (item.isAdded()) {
                    tableData.remove(item);
                } else {
                    Platform.runLater(() -> {
                        reRenderDetailView();
                    });
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.workList.refresh();
            if (Objects.nonNull(sortColumn)) {
                this.workList.getSortOrder().add(sortColumn);
                sortColumn.setSortType(sortType);
                sortColumn.setSortable(true);
            }
            this.blockUI(false);
        }
    }

    /**
     * 間接作業 登録
     *
     * @param event 登録ボタン押下
     */
    @FXML
    private void onRegistWork(ActionEvent event) {
        registIndirectWork();
    }

    /**
     * 間接作業の登録・更新・削除
     *
     * @return
     */
    private boolean registIndirectWork() {
        boolean ret = true;
        try {
            logger.info("registIndirectWork start");
            blockUI(true);

            final boolean isEmpty = tableData.stream()
                    .filter(row -> Objects.isNull(row.getWorkNumber())
                            || row.getWorkNumber().isEmpty()
                            || Objects.isNull(row.getWorkName())
                            || row.getWorkName().isEmpty())
                    .count() > 0;

            if (isEmpty) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return false;
            }

            if (this.tableData
                    .stream()
                    .filter(WorkTableData::isDeleted)
                    .map(WorkTableData::getWorkInfoEntity)
                    .map(IndirectWorkInfoEntity::getIndirectWorkId)
                    .map(indirectWorkInfoFacade::getActiveIndirectWorkCount)
                    .anyMatch(count -> count > 0)) {

                this.tableData
                        .stream()
                        .filter(WorkTableData::isDeleted)
                        .forEach(entity -> {
                            entity.setIsDeleted(false);
                        });

                workList.refresh();
                reRenderDetailView();



                sc.showAlert(
                        Alert.AlertType.ERROR,
                        LocaleUtils.getString("key.DeleteIndirectWork"),
                        LocaleUtils.getString("key.DeleteIndirectWorkDetail"));
                return false;
            }


            for (WorkTableData row : tableData) {
                
                if (row.isDeleted()) {
                    logger.debug("onRegistWork(remove): isEdited={}, workId={}, workName={}",
                            row.isEdited(), row.getWorkNumber(), row.getWorkName());
                    // 間接作業を削除
                    ResponseEntity res = indirectWorkInfoFacade.delete(row.getWorkInfoEntity());
                    if (Objects.nonNull(res)
                            && !Objects.equals(res.getErrorType(), ServerErrorTypeEnum.EXIST_CHILD_DELETE)
                            && ResponseAnalyzer.getAnalyzeResult(res)) {
                        tableData.remove(row);
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditIndirectWork"), String.format(
                                LocaleUtils.getString("key.FailedToDelete") + "\n\n%s:%s\n%s:%s", LocaleUtils.getString("key.IndirectWork"),
                                LocaleUtils.getString("key.IndirectWorkNumber"),
                                adtekfuji.utility.StringUtils.getShortName(row.getWorkNumber(), MAX_ERROR_SUBINFO_LEN),
                                LocaleUtils.getString("key.IndirectWorkName"),
                                adtekfuji.utility.StringUtils.getShortName(row.getWorkName(), MAX_ERROR_SUBINFO_LEN)));
                        ret = false;
                    } else if (!res.isSuccess()) {
                        if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.EXIST_CHILD_DELETE)) {
                            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"),
                                    LocaleUtils.getString("key.DeleteErrExistUsedHistory"));
                        }
                        ret = false;
                    }
                    continue;
                }
                if (row.isAdded()) {
                    logger.debug("onRegistWork(create): isEdited={}, workId={}, workName={}",
                            row.isEdited(), row.getWorkNumber(), row.getWorkName());
                    IndirectWorkInfoEntity workInfo = new IndirectWorkInfoEntity(null,
                            row.getWorkNumber(), row.getWorkName(), selectedWorkCategoryId);
                    // 間接作業を追加
                    ResponseEntity res = indirectWorkInfoFacade.regist(workInfo);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        IndirectWorkInfoEntity registed
                                = indirectWorkInfoFacade.find(getUriToRegistedItemId(res.getUri()));
                        row.setWorkInfoEntity(registed);
                        row.setIsCreated(false);
                        row.setIsEdited(false);
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditIndirectWork"),
                                String.format(LocaleUtils.getString("key.FailedToCreate") + "\n\n%s:%s\n%s:%s",
                                        LocaleUtils.getString("key.IndirectWork"),
                                        LocaleUtils.getString("key.IndirectWorkNumber"), adtekfuji.utility.StringUtils.
                                        getShortName(row.getWorkNumber(), MAX_ERROR_SUBINFO_LEN),
                                        LocaleUtils.getString("key.IndirectWorkName"), adtekfuji.utility.StringUtils.
                                        getShortName(row.getWorkName(), MAX_ERROR_SUBINFO_LEN)));
                        ret = false;
                    } else if (!res.isSuccess()) {
                        ret = false;
                    }
                    continue;
                }
                if (row.isEdited()) {
                    logger.debug("onRegistWork(update): isEdited={}, workId={}, workName={}",
                            row.isEdited(), row.getWorkNumber(), row.getWorkName());
                    IndirectWorkInfoEntity workInfo = new IndirectWorkInfoEntity(row.getWorkInfoEntity());
                    workInfo.setWorkNumber(row.getWorkNumber());
                    workInfo.setWorkName(row.getWorkName());
                    // 間接作業を更新
                    ResponseEntity res = indirectWorkInfoFacade.update(workInfo);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        IndirectWorkInfoEntity registed
                                = indirectWorkInfoFacade.find(workInfo.getIndirectWorkId());
                        row.setWorkInfoEntity(registed);
                        row.setIsEdited(false);
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditIndirectWork"),
                                String.format(LocaleUtils.getString("key.FailedToUpdate") + "\n\n%s:%s\n%s:%s",
                                        LocaleUtils.getString("key.IndirectWork"),
                                        LocaleUtils.getString("key.IndirectWorkNumber"), adtekfuji.utility.StringUtils.
                                        getShortName(row.getWorkNumber(), MAX_ERROR_SUBINFO_LEN),
                                        LocaleUtils.getString("key.IndirectWorkName"), adtekfuji.utility.StringUtils.
                                        getShortName(row.getWorkName(), MAX_ERROR_SUBINFO_LEN)));
                        ret = false;
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                        // 排他バージョンが異なる。
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditIndirectWork"),
                                new StringBuilder(LocaleUtils.getString("key.alert.differentVerInfo"))
                                        .append("\n\n")
                                        .append(LocaleUtils.getString("key.IndirectWorkNumber"))
                                        .append(":")
                                            .append(adtekfuji.utility.StringUtils.getShortName(row.getWorkNumber(), MAX_ERROR_SUBINFO_LEN))
                                        .append("\n")
                                        .append(LocaleUtils.getString("key.IndirectWorkName"))
                                        .append(":")
                                        .append(adtekfuji.utility.StringUtils.getShortName(row.getWorkName(), MAX_ERROR_SUBINFO_LEN))
                                        .toString());
                        ret = false;
                    } else if (!res.isSuccess()) {
                        ret = false;
                    }
                }
            }
            workList.refresh();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            ret = false;
        } finally {
            blockUI(false);
        }
        return ret;
    }

    /**
     * ツリーの再描画
     */
    private void reRenderTree() {
        workCategoryTree.setCellFactory((TreeView<WorkCategoryInfoEntity> p) -> new WorkCategoryTreeCell());
    }

    /**
     * ツリーデータの再取得 引数parentItemの子を再取得する
     *
     * @param parentItem
     * @param workCategoryId 再取得後に選択するID
     */
    private void updateTreeItemThread(Long workCategoryId) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    TreeItem<WorkCategoryInfoEntity> root = workCategoryTree.getRoot();
                    createTreeRootThread();

                    Platform.runLater(() -> {
                        blockUI(false);
                        root.setExpanded(true);
                        selectedTreeItem(root, workCategoryId);
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
     * @param workCategoryId
     */
    private void selectedTreeItem(TreeItem<WorkCategoryInfoEntity> parentItem, Long workCategoryId) {
        Optional<TreeItem<WorkCategoryInfoEntity>> find = parentItem.getChildren().stream().
                filter(p -> p.getValue().getWorkCategoryId().equals(workCategoryId)).findFirst();

        if (find.isPresent()) {
            workCategoryTree.getSelectionModel().select(find.get());
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
            rootItem = new TreeItem<>(new WorkCategoryInfoEntity(ROOT_ID, LocaleUtils.getString("key.WorkClassification")));
            Platform.runLater(() -> {
                workCategoryTree.rootProperty().setValue(rootItem);
                reRenderTree();
            });
        }

        rootItem.getChildren().clear();
        rootItem.setExpanded(false);

        //親階層の情報を取得
        long topHierarchyCnt = workCategoryInfoFacade.count();

        logger.debug("The number of information that belongs to the child hierarchy:{}", topHierarchyCnt);
        for (long nowTopHierarchyCnt = 0; nowTopHierarchyCnt <= topHierarchyCnt;) {
            List<WorkCategoryInfoEntity> entitys = workCategoryInfoFacade.
                    findRange(nowTopHierarchyCnt, nowTopHierarchyCnt + MAX_ROLL_HIERARCHY_CNT - 1);

            entitys.stream().forEach((entity) -> {
                TreeItem<WorkCategoryInfoEntity> item = new TreeItem<>(entity);
                rootItem.getChildren().add(item);
            });

            nowTopHierarchyCnt += MAX_ROLL_HIERARCHY_CNT;
        }

        //作業区分名でソートする
        rootItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getWorkCategoryName()));

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
                        workCategoryTree.getRoot().setExpanded(true);
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
     * @param workCategoryId 作業区分
     */
    private void updateDetailView(Long workCategoryId) {
        if (Objects.isNull(viewWorkCategoryInfoEntity)) {
            return;
        }

        this.workPane.setDisable(true);

        clearDetailView();

        if (workCategoryId == 0L) {
            return;
        }

        long countCategorized = indirectWorkInfoFacade.getCountCategorized(workCategoryId);

        for (long nowHierarchyCnt = 0; nowHierarchyCnt < countCategorized; nowHierarchyCnt += MAX_LOAD_SIZE) {
            List<IndirectWorkInfoEntity> entities = indirectWorkInfoFacade.getCategorizedWork(
                    workCategoryId, nowHierarchyCnt, nowHierarchyCnt + MAX_LOAD_SIZE - 1);

            if (!entities.isEmpty()) {
                entities.stream().forEach((e) -> {
                    tableData.add(new WorkTableData(e));
                });
            }
        }

        reRenderDetailView();

        this.workList.getSortOrder().clear();
        this.workList.getSortOrder().add(workIdColumn);

        this.allCheck.setSelected(false);
        this.workPane.setDisable(false);
    }

    /**
     * 詳細画面初期化
     */
    private void clearDetailView() {
        tableData.clear();
        workList.getSelectionModel().clearSelection();
    }

    /**
     * 詳細画面再描画処理
     */
    private void reRenderDetailView() {
        if (Objects.isNull(viewWorkCategoryInfoEntity)) {
            return;
        }

        workList.getSelectionModel().clearSelection();

        FilteredList<WorkTableData> filteredData;
        SortedList<WorkTableData> sortedData;
        ObservableList<WorkTableData> dispData;

        filteredData = this.tableData.filtered(o -> o.getWorkNumber().isEmpty() || !o.isDeleted());

        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(workList.comparatorProperty());
        dispData = sortedData;

        workList.setItems(dispData);
    }

    /**
     * 作業区分編集ダイアログを表示する。
     *
     * @param workCategory
     * @return OK押下？
     */
    private boolean DispWorkCategoryDialog(WorkCategoryInfoEntity workCategory) {
        try {
            String orgName = workCategory.getWorkCategoryName();
            if (Objects.isNull(orgName)) {
                orgName = "";
            }
            String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.WorkCategoryName"));
            String newName = sc.showTextInputDialog(
                    LocaleUtils.getString("key.Edit"), message, LocaleUtils.getString("key.WorkCategoryName"), orgName);

            if (Objects.nonNull(newName)) {
                newName = adtekfuji.utility.StringUtils.trim2(newName);
                if (newName.isEmpty()) {
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                } else if (!Objects.equals(newName, orgName)) {
                    workCategory.setWorkCategoryName(newName);
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

            SplitPaneUtils.saveDividerPosition(workCategoryPane, getClass().getSimpleName());

            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registIndirectWork()) {
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
        boolean isChanged = false;
        for (int i = tableData.size() - 1; i >= 0; i--) {
            WorkTableData row = tableData.get(i);
            if (row.isDeleted() || row.isAdded() || row.isEdited()) {
                isChanged = true;
                break;
            }
        }
        return isChanged;
    }

    /**
     * 登録結果URIから登録したデータのIDを取得する
     *
     * @param uri
     * @return 登録したデータのID
     */
    public static long getUriToRegistedItemId(String uri) {
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
     * 間接作業 移動
     *
     * @param event 移動ボタン押下
     */
    @FXML
    private void onMoveWork(ActionEvent event) {
        try {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registIndirectWork()) {
                        return;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }

            List<WorkTableData> selectedWorks
                    = tableData.stream().filter(p -> p.isSelected()).collect(Collectors.toList());
            TreeItem<WorkCategoryInfoEntity> selectedCategory
                    = workCategoryTree.getSelectionModel().getSelectedItem();
            if (selectedWorks.isEmpty() || Objects.isNull(selectedCategory)) {
                return;
            }

            sc.blockUI(true);
            workCategoryTree.setVisible(false);
            workList.setVisible(false);
            TreeDialogEntity treeDialogEntity
                    = new TreeDialogEntity(workCategoryTree.getRoot(), LocaleUtils.getString("key.HierarchyName"));
            ButtonType ret
                    = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "WorkCategoryTreeCompo", treeDialogEntity);
            TreeItem<WorkCategoryInfoEntity> category
                    = (TreeItem<WorkCategoryInfoEntity>) treeDialogEntity.getTreeSelectedItem();

            if (ret.equals(ButtonType.OK) && Objects.nonNull(category)) {
                logger.debug(treeDialogEntity.getTreeSelectedItem());
                for (WorkTableData selectedWork : selectedWorks) {
                    IndirectWorkInfoEntity work
                            = indirectWorkInfoFacade.find(selectedWork.getWorkInfoEntity().getIndirectWorkId());
                    work.setFkWorkCategoryId(category.getValue().getWorkCategoryId());

                    indirectWorkInfoFacade.update(work);
                }
                this.updateDetailView(selectedWorkCategoryId);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            sc.blockUI(false);
            workCategoryTree.setVisible(true);
            workList.setVisible(true);

        }
    }

    /**
     * テキスト編集セル
     */
    class WorkTableCell extends TableCell<WorkTableData, String> {

        private TextField textField;
        private TablePosition<WorkTableData, ?> tablePos = null;

        /**
         * コンストラクタ
         */
        public WorkTableCell() {
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
            final TableView<WorkTableData> table = this.getTableView();
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

            this.textField.focusedProperty().addListener(
                    (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                        if (!newValue && this.isEditing()) {
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
            final TableView<WorkTableData> table = this.getTableView();
            CellEditEvent editEvent = new CellEditEvent(
                    table, this.tablePos, TableColumn.editCommitEvent(), textField.getText());
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
            } else if (this.isEditing()) {
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

        private String getString() {
            return this.getItem() == null ? "" : this.getItem();
        }
    }
}
