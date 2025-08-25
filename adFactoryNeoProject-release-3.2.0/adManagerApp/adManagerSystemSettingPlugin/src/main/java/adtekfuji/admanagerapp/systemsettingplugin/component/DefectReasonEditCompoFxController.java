/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.admanagerapp.systemsettingplugin.common.ReasonTableRow;
import adtekfuji.admanagerapp.systemsettingplugin.entity.ReasonTableData;
import adtekfuji.clientservice.ReasonInfoFacade;
import adtekfuji.clientservice.ReasonCategoryInfoFacede;
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
import jp.adtekfuji.adFactory.entity.master.ReasonCategoryInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ReasonInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.treecell.ReasonCategoryTreeCell;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 不良理由編集画面
 *
 * @author hato
 */
@FxComponent(id = "DefectReasonEditCompo",
        fxmlPath = "/fxml/admanagersystemsettingplugin/defect_reason_edit_compo.fxml")
public class DefectReasonEditCompoFxController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static ReasonCategoryInfoFacede reasonCategoryInfoFacade = new ReasonCategoryInfoFacede();
    private final static ReasonInfoFacade reasonInfoFacade = new ReasonInfoFacade();

    private final SceneContiner sc = SceneContiner.getInstance();
    private ReasonCategoryInfoEntity viewReasonCategoryInfoEntity;

    private final static long ROOT_ID = 0;

    private final static long MAX_ERROR_SUBINFO_LEN = 40;
    private final static String URI_SPLIT = "/";

    private TreeItem<ReasonCategoryInfoEntity> rootItem;

    private final ObservableList<ReasonTableData> tableData = FXCollections.observableArrayList();
    private Long selectedReasonCategoryId;

    @FXML
    private SplitPane defectReasonCategoryPane;
    @FXML
    private TreeView<ReasonCategoryInfoEntity> defectReasonCategoryTree;
    @FXML
    private Button deleteTreeButton;
    @FXML
    private Button editTreeButton;
    @FXML
    private Button createTreeButton;

    @FXML
    private PropertySaveTableView<ReasonTableData> defectReasonList;
    @FXML
    private TableColumn selectedColumn;
    @FXML
    private TableColumn<ReasonTableData, String> defectReasonColumn;

    @FXML
    private Button moveDefectReasonButton;
    @FXML
    private Button addDefectReasonButton;
    @FXML
    private Button deleteDefectReasonButton;
    @FXML
    private Button registDectReasonButton;

    @FXML
    private Pane Progress;

    private CheckBox allCheck;

    public DefectReasonEditCompoFxController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        defectReasonList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        SplitPaneUtils.loadDividerPosition(defectReasonCategoryPane, getClass().getSimpleName());

        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            //役割の権限によるボタン無効化.
            createTreeButton.setDisable(true);
            defectReasonList.setEditable(false);
        }

        defectReasonList.init("defectReasonList");

        Callback<TableColumn<ReasonTableData, String>, TableCell<ReasonTableData, String>> cellFactory
                = (TableColumn<ReasonTableData, String> p) -> new ReasonTableCell();

        defectReasonList.setRowFactory((TableView<ReasonTableData> r) -> new ReasonTableRow());

        defectReasonList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        defectReasonList.getSelectionModel().setCellSelectionEnabled(false);

        allCheck = new CheckBox();
        allCheck.setOnAction((ActionEvent event) -> {
            if (allCheck.isSelected()) {
                for (ReasonTableData row : defectReasonList.getItems()) {
                    row.setSelected(Boolean.TRUE);
                }
            } else {
                for (ReasonTableData row : defectReasonList.getItems()) {
                    row.setSelected(Boolean.FALSE);
                }
            }
        });
        selectedColumn.setGraphic(allCheck);
        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));

        // 理由名 列
        defectReasonColumn.setCellValueFactory((TableColumn.CellDataFeatures<ReasonTableData, String> param)
                -> param.getValue().reasonNameProperty());
        defectReasonColumn.setCellFactory(cellFactory);
        defectReasonColumn.setOnEditCommit((CellEditEvent<ReasonTableData, String> event) -> {
            ReasonTableData item = event.getTableView().getItems().get(event.getTablePosition().getRow());
            item.setReasonName(event.getNewValue());
            item.setIsEdited(true);
            item.setReasonCategoryId(selectedReasonCategoryId);
        });

        //階層ツリー選択時詳細画面表示処理実行
        defectReasonCategoryTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue) && Objects.nonNull(newValue.getValue().getId())) {
                if (!newValue.getValue().getId().equals(selectedReasonCategoryId)) {
                    if (this.isChanged()) {
                        // 「入力内容が保存されていません。保存しますか?」を表示
                        String title = LocaleUtils.getString("key.confirm");
                        String message = LocaleUtils.getString("key.confirm.destroy");

                        ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                                new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                        if (ButtonType.YES == buttonType) {
                            if (!this.registDefectReason()) {
                                Platform.runLater(() -> defectReasonCategoryTree.getSelectionModel().select(oldValue));
                                return;
                            }
                        } else if (ButtonType.CANCEL == buttonType) {
                            defectReasonCategoryTree.getSelectionModel().select(oldValue);
                            return;
                        }
                    }
                    selectedReasonCategoryId = newValue.getValue().getId();
                    viewReasonCategoryInfoEntity = new ReasonCategoryInfoEntity();
                    Platform.runLater(() -> {
                        updateDetailView(selectedReasonCategoryId);
                    });
                }
            } else {
                viewReasonCategoryInfoEntity = null;
                selectedReasonCategoryId = null;
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
                        defectReasonCategoryTree.getRoot().setExpanded(true);
                        blockUI(false);
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * キー押下時のイベント
     *
     * @param event イベント
     */
    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.F5)) {
            defectReasonCategoryTree.getRoot().setExpanded(false);
            runCreateTreeRootThread();
        }
    }

    /**
     * 理由区分 新規作成
     *
     * @param event イベント
     */
    @FXML
    private void onCreateReasonCategory(ActionEvent event) {
        try {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registDefectReason()) {
                        return;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }

            ReasonCategoryInfoEntity reasonCategory = new ReasonCategoryInfoEntity();

            if (this.dispReasonCategoryDialog(reasonCategory)) {
                //理由区分を追加
                ResponseEntity res = reasonCategoryInfoFacade.add(reasonCategory);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    //ツリー更新
                    defectReasonCategoryTree.getRoot().setExpanded(false);
                    updateTreeItemThread(getUriToRegistedItemId(res.getUri()));
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ReasonCategoryEdit"),
                            String.format(LocaleUtils.getString("key.FailedToCreate"), LocaleUtils.getString("key.ReasonCategory")));
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 理由区分 編集
     *
     * @param event イベント
     */
    @FXML
    private void onEditReasonCategory(ActionEvent event) {
        try {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registDefectReason()) {
                        return;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }

            TreeItem<ReasonCategoryInfoEntity> item = defectReasonCategoryTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getValue().getId())) {
                return;
            }

            ReasonCategoryInfoEntity reasonCategory = defectReasonCategoryTree.getSelectionModel().getSelectedItem().getValue();
            String targetReasonCategoryName = reasonCategory.getReasonCategoryName();

            if (this.dispReasonCategoryDialog(reasonCategory)) {
                //理由区分を更新
                ResponseEntity res = reasonCategoryInfoFacade.update(reasonCategory);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    //ツリー更新
                    defectReasonCategoryTree.getRoot().setExpanded(false);
                    updateTreeItemThread(reasonCategory.getId());
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.key.ReasonCategoryEdit"),
                            String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.ReasonCategory")));
                    // データを戻す
                    reasonCategory.setReasonCategoryName(targetReasonCategoryName);
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                    // 排他バージョンが異なる。
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ReasonCategoryEdit"), LocaleUtils.getString("key.alert.differentVerInfo"));
                    //ツリー更新
                    defectReasonCategoryTree.getRoot().setExpanded(false);
                    updateTreeItemThread(reasonCategory.getId());
                } else {
                    // データを戻す
                    reasonCategory.setReasonCategoryName(targetReasonCategoryName);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 理由区分 削除
     *
     * @param event イベント
     */
    @FXML
    private void onDeleteReasonCategory(ActionEvent event) {
        //削除
        try {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registDefectReason()) {
                        return;
                    }
                    this.updateDetailView(selectedReasonCategoryId);
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }

            blockUI(true);
            TreeItem<ReasonCategoryInfoEntity> item = defectReasonCategoryTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getValue().getId())) {
                return;
            }

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION,
                    LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getValue().getReasonCategoryName());
            if (ret.equals(ButtonType.CANCEL)) {
                return;
            }

            ReasonCategoryInfoEntity reasonCategory = defectReasonCategoryTree.getSelectionModel().getSelectedItem().getValue();

            // 理由区分を削除
            ResponseEntity res = reasonCategoryInfoFacade.remove(reasonCategory.getId());
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                //ツリー更新
                defectReasonCategoryTree.getRoot().setExpanded(false);
                updateTreeItemThread(item.getValue().getId());
            } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ReasonCategoryEdit"),
                        String.format(LocaleUtils.getString("key.FailedToDelete"), LocaleUtils.getString("key.ReasonCategory")));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 不良理由 追加
     *
     * @param event 追加ボタン押下
     */
    @FXML
    private void onAddDefectReason(ActionEvent event) {
        try {
            if (Objects.nonNull(selectedReasonCategoryId)) {
                tableData.add(new ReasonTableData());
                defectReasonList.refresh();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 不良理由 削除
     *
     * @param event 削除ボタン押下
     */
    @FXML
    private void onDeleteDefectReason(ActionEvent event) {
        TableColumn sortColumn = null;
        SortType sortType = null;

        try {
            this.blockUI(true);

            // ソートをクリアしないと、例外が発生する
            if (this.defectReasonList.getSortOrder().size() > 0) {
                sortColumn = (TableColumn) this.defectReasonList.getSortOrder().get(0);
                sortType = sortColumn.getSortType();
                this.defectReasonList.getSortOrder().clear();
            }

            List<ReasonTableData> items = this.tableData.stream().filter(o -> o.isSelected()).collect(Collectors.toList());
            for (ReasonTableData item : items) {
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
            this.defectReasonList.refresh();
            if (Objects.nonNull(sortColumn)) {
                this.defectReasonList.getSortOrder().add(sortColumn);
                sortColumn.setSortType(sortType);
                sortColumn.setSortable(true);
            }
            this.blockUI(false);
        }
    }

    /**
     * 不良理由 登録
     *
     * @param event 登録ボタン押下
     */
    @FXML
    private void onRegistDefectReason(ActionEvent event) {
        registDefectReason();
    }

    /**
     * 不良理由の登録・更新・削除
     *
     * @return
     */
    private boolean registDefectReason() {
        boolean ret = true;
        try {
            logger.info("registDefectReason start");
            blockUI(true);

            final boolean isEmpty = tableData.stream()
                    .filter(row -> Objects.isNull(row.getReasonName())
                            || row.getReasonName().isEmpty())
                    .count() > 0;

            if (isEmpty) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return false;
            }

            List<ReasonTableData> deletedList = tableData.stream().filter(p -> p.isDeleted()).collect(Collectors.toList());
            for (ReasonTableData row : deletedList) {
                logger.debug("onRegistDefectReason(remove): isEdited={}, Id={}, ReasonName={}",
                        row.isEdited(), row.getReasonName());
                // 不良理由を削除
                ResponseEntity res = reasonInfoFacade.remove(row.getReasonInfoEntity().getId());
                if (Objects.nonNull(res)
                        && ResponseAnalyzer.getAnalyzeResult(res)) {
                    tableData.remove(row);
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditDefectReason"), String.format(
                            LocaleUtils.getString("key.FailedToDelete") + "\n\n%s:%s",
                            LocaleUtils.getString("key.DefectReason"),
                            LocaleUtils.getString("key.DefectReason"),
                            adtekfuji.utility.StringUtils.getShortName(row.getReasonName(), MAX_ERROR_SUBINFO_LEN)));
                    ret = false;
                } else if (!res.isSuccess()) {
                    if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.EXIST_CHILD_DELETE)) {
                        sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"),
                                LocaleUtils.getString("key.DeleteErrExistUsedHistory"));
                    }
                    ret = false;
                }
            }

            List<ReasonTableData> addedList = tableData.stream().filter(p -> p.isAdded()).collect(Collectors.toList());
            for (ReasonTableData row : addedList) {
                logger.debug("onRegistDefectReason(create): isEdited={}, Id={}, reasonName={}",
                        row.isEdited(), row.getReasonName());
                ReasonInfoEntity reasonInfo = new ReasonInfoEntity(row.getReasonInfoEntity());
                reasonInfo.setReason(row.getReasonName());
                reasonInfo.setReasonCategoryId(row.getReasonCategoryId());
                //不良理由を追加
                ResponseEntity res = reasonInfoFacade.add(reasonInfo);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    ReasonInfoEntity registed
                            = reasonInfoFacade.find(getUriToRegistedItemId(res.getUri()));
                    row.setReasonInfoEntity(registed);
                    row.setIsAdded(false);
                    row.setIsEdited(false);
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditDefectReason"),
                            String.format(LocaleUtils.getString("key.FailedToCreate") + "\n\n%s:%s",
                                    LocaleUtils.getString("key.DefectReason"),
                                    LocaleUtils.getString("key.DefectReason"),
                                    adtekfuji.utility.StringUtils.getShortName(row.getReasonName(), MAX_ERROR_SUBINFO_LEN)));
                    ret = false;
                } else if (!res.isSuccess()) {
                    ret = false;
                }
            }

            List<ReasonTableData> editedList = tableData.stream().filter(p -> !p.isAdded() && p.isEdited()).collect(Collectors.toList());
            for (ReasonTableData row : editedList) {
                logger.debug("onRegistDefectReason(update): isEdited={}, Id={}, reasonName={}", row.isEdited(), row.getReasonName());

                ReasonInfoEntity reasonInfo = new ReasonInfoEntity(row.getReasonInfoEntity());
                reasonInfo.setReason(row.getReasonName());
                reasonInfo.setReasonCategoryId(row.getReasonCategoryId());
                
                //不良理由を更新
                ResponseEntity res = reasonInfoFacade.update(reasonInfo);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    ReasonInfoEntity registed
                            = reasonInfoFacade.find(reasonInfo.getId());
                    row.setReasonInfoEntity(registed);
                    row.setIsEdited(false);
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditDefectReason"),
                            String.format(LocaleUtils.getString("key.FailedToUpdate") + "\n\n%s:%s",
                                    LocaleUtils.getString("key.DefectReason"),
                                    LocaleUtils.getString("key.DefectReason"),
                                    adtekfuji.utility.StringUtils.getShortName(row.getReasonName(), MAX_ERROR_SUBINFO_LEN)));
                    ret = false;
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                    // 排他バージョンが異なる。
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditDefectReason"),
                            new StringBuilder(LocaleUtils.getString("key.alert.differentVerInfo"))
                                    .append("\n\n")
                                    .append(LocaleUtils.getString("key.DefectReason"))
                                    .append(":")
                                    .append(adtekfuji.utility.StringUtils.getShortName(row.getReasonName(), MAX_ERROR_SUBINFO_LEN))
                                    .toString());
                    ret = false;
                } else if (!res.isSuccess()) {
                    ret = false;
                }
            }
            defectReasonList.refresh();
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
        defectReasonCategoryTree.setCellFactory((TreeView<ReasonCategoryInfoEntity> p) -> new ReasonCategoryTreeCell());
    }

    /**
     * ツリーデータの再取得
     *
     * @param reasonCategoryId 再取得後に選択するID
     */
    private void updateTreeItemThread(Long reasonCategoryId) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    TreeItem<ReasonCategoryInfoEntity> root = defectReasonCategoryTree.getRoot();
                    createTreeRootThread();

                    Platform.runLater(() -> {
                        blockUI(false);
                        root.setExpanded(true);
                        selectedTreeItem(root, reasonCategoryId);
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
     * 理由区分IDが一致するTreeItemを選択する
     *
     * @param parentItem 親アイテム
     * @param reasonCategoryId 理由区分ID
     */
    private void selectedTreeItem(TreeItem<ReasonCategoryInfoEntity> parentItem, Long reasonCategoryId) {
        Optional<TreeItem<ReasonCategoryInfoEntity>> find = parentItem.getChildren().stream().
                filter(p -> p.getValue().getId().equals(reasonCategoryId)).findFirst();

        if (find.isPresent()) {
            defectReasonCategoryTree.getSelectionModel().select(find.get());
        }
    }

    /**
     * ツリー表示情報更新処理
     */
    private void createTreeRootThread() {
        logger.debug("createTreeRootThread.");

        if (Objects.isNull(rootItem)) {
            //ツリールート作成
            rootItem = new TreeItem<>(new ReasonCategoryInfoEntity(ROOT_ID, ReasonTypeEnum.TYPE_DEFECT, LocaleUtils.getString("key.ReasonCategory")));
            Platform.runLater(() -> {
                defectReasonCategoryTree.rootProperty().setValue(rootItem);
                reRenderTree();
            });
        }

        rootItem.getChildren().clear();
        rootItem.setExpanded(false);

        // 不良理由区分一覧を取得
        List<ReasonCategoryInfoEntity> entitys = reasonCategoryInfoFacade.findType(ReasonTypeEnum.TYPE_DEFECT);

        entitys.stream().forEach((entity) -> {
            TreeItem<ReasonCategoryInfoEntity> item = new TreeItem<>(entity);
            rootItem.getChildren().add(item);
        });


        //理由区分名でソートする
        rootItem.getChildren().sort(Comparator.comparing(item -> item.getValue().getReasonCategoryName()));

        Platform.runLater(() -> {
            reRenderTree();
        });

        logger.debug("createTreeRootThread end.");
    }

     /**
     * ツリー表示情報更新処理実行スレッド
     */
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
                        defectReasonCategoryTree.getRoot().setExpanded(true);
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
     * @param reasonCategoryId 理由区分ID
     */
    private void updateDetailView(Long reasonCategoryId) {
        if (Objects.isNull(viewReasonCategoryInfoEntity)) {
            return;
        }

        clearDetailView();

        if (reasonCategoryId == 0L) {
            // ルート選択時はボタンを無効化
            editTreeButton.setDisable(true);
            deleteTreeButton.setDisable(true);
            addDefectReasonButton.setDisable(true);
            deleteDefectReasonButton.setDisable(true);
            moveDefectReasonButton.setDisable(true);
            registDectReasonButton.setDisable(true);
            return;
        } else if (LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            // 権限があればボタンを有効化
            editTreeButton.setDisable(false);
            deleteTreeButton.setDisable(false);
            addDefectReasonButton.setDisable(false);
            deleteDefectReasonButton.setDisable(false);
            moveDefectReasonButton.setDisable(false);
            registDectReasonButton.setDisable(false);
        }

        List<ReasonInfoEntity> entities = reasonInfoFacade.findAllByCategoryId(reasonCategoryId);

        if (!entities.isEmpty()) {
            entities.sort(Comparator.<ReasonInfoEntity, String>comparing(data -> data.getReason()));
            entities.stream().forEach((e) -> {
                tableData.add(new ReasonTableData(e));
            });
        }

        reRenderDetailView();

        this.defectReasonList.getSortOrder().clear();

        this.allCheck.setSelected(false);
    }

    /**
     * 詳細画面初期化
     */
    private void clearDetailView() {
        tableData.clear();
        defectReasonList.getSelectionModel().clearSelection();
    }

    /**
     * 詳細画面再描画処理
     */
    private void reRenderDetailView() {
        if (Objects.isNull(viewReasonCategoryInfoEntity)) {
            return;
        }

        defectReasonList.getSelectionModel().clearSelection();

        FilteredList<ReasonTableData> filteredData;
        SortedList<ReasonTableData> sortedData;
        ObservableList<ReasonTableData> dispData;

        filteredData = this.tableData.filtered(o -> !o.isDeleted());

        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(defectReasonList.comparatorProperty());
        dispData = sortedData;

        defectReasonList.setItems(dispData);
    }

    /**
     * 理由区分編集ダイアログを表示する。
     *
     * @param reasonCategory 変更対象の理由区分エンティティ
     * @return OK=trueか取り消し=false
     */
    private boolean dispReasonCategoryDialog(ReasonCategoryInfoEntity reasonCategory) {
        try {
            String orgName = reasonCategory.getReasonCategoryName();
            if (Objects.isNull(orgName)) {
                orgName = "";
            }
            String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.ReasonCategory"));
            String newName = sc.showTextInputDialog(LocaleUtils.getString("key.ReasonCategory"), message, LocaleUtils.getString("key.ReasonCategory"), orgName);

            if (Objects.nonNull(newName)) {
                newName = adtekfuji.utility.StringUtils.trim2(newName);
                if (newName.isEmpty()) {
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                } else if (!Objects.equals(newName, orgName)) {
                    reasonCategory.setReasonCategoryName(newName);
                    reasonCategory.setReasonType(ReasonTypeEnum.TYPE_DEFECT);
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

            SplitPaneUtils.saveDividerPosition(defectReasonCategoryPane, getClass().getSimpleName());

            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registDefectReason()) {
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
            ReasonTableData row = tableData.get(i);
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
     * @param uri 登録結果URI
     * @return 登録したデータのID
     */
    public static long getUriToRegistedItemId(String uri) {
        long ret = 0;
        try {
            String[] split = uri.split(URI_SPLIT);
            ret = Long.parseLong(split[split.length - 1]);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * 不良理由 移動
     *
     * @param event 移動ボタン押下
     */
    @FXML
    private void onMoveDefectReason(ActionEvent event) {
        try {
            if (this.isChanged()) {
                // 「入力内容が保存されていません。保存しますか?」を表示
                String title = LocaleUtils.getString("key.confirm");
                String message = LocaleUtils.getString("key.confirm.destroy");

                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message,
                        new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (ButtonType.YES == buttonType) {
                    if (!this.registDefectReason()) {
                        return;
                    }
                } else if (ButtonType.CANCEL == buttonType) {
                    return;
                }
            }

            List<ReasonTableData> selectedDefectReasons = tableData.stream().filter(p -> p.isSelected() && !p.isAdded()).collect(Collectors.toList());
            TreeItem<ReasonCategoryInfoEntity> selectedCategory = defectReasonCategoryTree.getSelectionModel().getSelectedItem();
            if (selectedDefectReasons.isEmpty() || Objects.isNull(selectedCategory)) {
                return;
            }

            sc.blockUI(true);
            defectReasonCategoryTree.setVisible(false);
            defectReasonList.setVisible(false);
            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(defectReasonCategoryTree.getRoot(), LocaleUtils.getString("key.ReasonCategoryName"));
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "ReasonCategoryTreeCompo", treeDialogEntity);
            TreeItem<ReasonCategoryInfoEntity> category = (TreeItem<ReasonCategoryInfoEntity>) treeDialogEntity.getTreeSelectedItem();

            if (ret.equals(ButtonType.OK) && Objects.nonNull(category)) {
                logger.debug(treeDialogEntity.getTreeSelectedItem());
                for (ReasonTableData selectedDefectReason : selectedDefectReasons) {
                    ReasonInfoEntity reason = selectedDefectReason.getReasonInfoEntity();
                    reason.setReasonCategoryId(category.getValue().getId());

                    ResponseEntity res = reasonInfoFacade.update(reason);

                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // 成功
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditDefectReason"),
                                String.format(LocaleUtils.getString("key.FailedToUpdate") + "\n\n%s:%s",
                                        LocaleUtils.getString("key.DefectReason"),
                                        LocaleUtils.getString("key.DefectReason"),
                                        adtekfuji.utility.StringUtils.getShortName(reason.getReason(), MAX_ERROR_SUBINFO_LEN)));
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                        // 排他バージョンが異なる。
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.EditDefectReason"),
                                new StringBuilder(LocaleUtils.getString("key.alert.differentVerInfo"))
                                        .append("\n\n")
                                        .append(LocaleUtils.getString("key.DefectReason"))
                                        .append(":")
                                        .append(adtekfuji.utility.StringUtils.getShortName(reason.getReason(), MAX_ERROR_SUBINFO_LEN))
                                        .toString());
                    }
                }
                this.updateDetailView(selectedReasonCategoryId);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            sc.blockUI(false);
            defectReasonCategoryTree.setVisible(true);
            defectReasonList.setVisible(true);

        }
    }

    /**
     * テキスト編集セル
     */
    class ReasonTableCell extends TableCell<ReasonTableData, String> {

        private TextField textField;
        private TablePosition<ReasonTableData, ?> tablePos = null;

        /**
         * コンストラクタ
         */
        public ReasonTableCell() {
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
            final TableView<ReasonTableData> table = this.getTableView();
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
            final TableView<ReasonTableData> table = this.getTableView();
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
