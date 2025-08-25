/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component.lite;

import adtekfuji.admanagerapp.kanbaneditplugin.common.Constants;
import adtekfuji.admanagerapp.kanbaneditplugin.common.KanbanHierarchyTreeCell;
import adtekfuji.admanagerapp.kanbaneditplugin.common.SelectedKanbanAndHierarchy;
import adtekfuji.admanagerapp.kanbaneditplugin.controls.CheckStatusList;
import adtekfuji.admanagerapp.kanbaneditplugin.controls.CheckTextField;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.DisplayData;
import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.LabelInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import jp.adtekfuji.javafxcommon.dialog.MessageDialog;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogButtons;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogResult;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogType;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.IndexedCheckModel;

/**
 * カンバン画面コントローラー
 *
 * @author kenji.yokoi
 */
@FxComponent(id = "LiteKanbanListCompo", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/lite/kanban_list_lite.fxml")
public class KanbanListLite implements Initializable, ComponentHandler {

    private final Properties properties = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static final KanbanHierarchyInfoFacade kanbanHierarchyInfoFacade = new KanbanHierarchyInfoFacade();
    private static final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();

    private static final long RANGE = 20;
    private static final String URI_SPLIT = "/";
    private static final String SEARCH_FILTER_START_DATE = "search_filter_start_date";
    private static final String SEARCH_FILTER_END_DATE = "search_filter_end_date";
    private static final Map<Long, TreeItem> treeItems = new HashMap<>();
    private static Long rootHierarchyId = 0L;

    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    private final KanbanEditPermanenceDataLite kanbanEditPermanenceData = KanbanEditPermanenceDataLite.getInstance();
    private final ObjectProperty<TreeItem<KanbanHierarchyInfoEntity>> selectedProperty = new SimpleObjectProperty<>();
    private final SimpleDateFormat formatter = new SimpleDateFormat(rb.getString("key.DateTimeFormat"));
    private final Set<Object> blockUIs = new HashSet();
    private Boolean isDispDialog = false;
    private Long selectedHierarchyId;

    /**
     * QRコードを使用するか(true:使用する, false:使用しない)
     */
    private boolean useQRCode = false;

    private final List<DisplayData> kanbanMasterList = new LinkedList<>();
    private final TableColumn kanbanNameColumn = new TableColumn(rb.getString("key.KanbanName"));
    private final TableColumn workflowNameColumn = new TableColumn(rb.getString("key.WorkflowName"));
    private final TableColumn modelNameColumn = new TableColumn(rb.getString("key.ModelName"));
    private final TableColumn statusColumn = new TableColumn(rb.getString("key.Status"));
    private final TableColumn startTimeColumn = new TableColumn(rb.getString("key.WorkStartTime"));
    private final TableColumn endTimeColumn = new TableColumn(rb.getString("key.WorkEndTime"));

    /**
     * ツリーノード開閉イベントリスナー
     */
    private final ChangeListener expandedListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            // ノードを開いたら子ノードの情報を再取得して表示する。
            if (Objects.nonNull(newValue) && newValue.equals(true)) {
                TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
                expand(treeItem, null);
            }
        }
    };

    @FXML
    private SplitPane kanbanPane;
    @FXML
    private TreeView<KanbanHierarchyInfoEntity> hierarchyTree;
    @FXML
    private Pane Progress;
    @FXML
    private PropertySaveTableView<DisplayData> kanbanList;
    @FXML
    private CheckTextField kanbanNameField;
    @FXML
    private CheckTextField modelNameField;
    @FXML
    private CheckStatusList statusList;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ToolBar hierarchyBtnArea;
    @FXML
    private Button delTreeButton;
    @FXML
    private Button editTreeButton;
    @FXML
    private Button moveTreeButton;
    @FXML
    private Button createTreeButton;
    @FXML
    private Button deleteKanbanButton;
    @FXML
    private Button moveKanbanButton;
    @FXML
    private Button editKanbanButton;
    @FXML
    private Button createKanbanButton;

    /**
     * 初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        SplitPaneUtils.loadDividerPosition(kanbanPane, getClass().getSimpleName());

        // QRコードを使用するか
        this.useQRCode = Boolean.parseBoolean(properties.getProperty(Constants.USE_QRCODE, Constants.USE_QRCODE_DEF));
        // カンバン階層で選択されている階層IDを初期化
        this.selectedHierarchyId = Long.valueOf(0);

        //役割の権限によるボタン無効化.
        if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
            this.delTreeButton.setDisable(true);
            this.editTreeButton.setDisable(true);
            this.moveTreeButton.setDisable(true);
            this.createTreeButton.setDisable(true);
            this.createKanbanButton.setDisable(true);
        }

        this.editKanbanButton.setDisable(true);
        this.deleteKanbanButton.setDisable(true);
        this.moveKanbanButton.setDisable(true);

        Progress.setVisible(false);

        this.loadProperties();

        // カンバン一覧の列幅を設定する。
        kanbanNameColumn.setPrefWidth(180.0);
        workflowNameColumn.setPrefWidth(180.0);
        modelNameColumn.setPrefWidth(120.0);
        statusColumn.setPrefWidth(100.0);
        startTimeColumn.setPrefWidth(150.0);
        endTimeColumn.setPrefWidth(150.0);

        // カンバン一覧に列をを追加する。
        this.kanbanList.getColumns().add(kanbanNameColumn);
        this.kanbanList.getColumns().add(workflowNameColumn);
        this.kanbanList.getColumns().add(modelNameColumn);
        this.kanbanList.getColumns().add(statusColumn);
        this.kanbanList.getColumns().add(startTimeColumn);
        this.kanbanList.getColumns().add(endTimeColumn);

        //カンバン一覧の列設定を自動保存
        kanbanList.init("kanbanList");

        if (Objects.nonNull(properties.getProperty(SEARCH_FILTER_START_DATE)) && !"".equals(properties.getProperty(SEARCH_FILTER_START_DATE))) {
            Calendar calendarStartDate = Calendar.getInstance();
            calendarStartDate.setTime(new Date());
            calendarStartDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(properties.getProperty(SEARCH_FILTER_START_DATE)));
            startDatePicker.setValue(LocalDate.of(calendarStartDate.get(Calendar.YEAR), calendarStartDate.get(Calendar.MONTH) + 1, calendarStartDate.get(Calendar.DAY_OF_MONTH)));
        }

        if (Objects.nonNull(properties.getProperty(SEARCH_FILTER_END_DATE)) && !"".equals(properties.getProperty(SEARCH_FILTER_END_DATE))) {
            Calendar calendarEndDate = Calendar.getInstance();
            calendarEndDate.setTime(new Date());
            calendarEndDate.add(Calendar.DAY_OF_MONTH, Integer.parseInt(properties.getProperty(SEARCH_FILTER_END_DATE)));
            endDatePicker.setValue(LocalDate.of(calendarEndDate.get(Calendar.YEAR), calendarEndDate.get(Calendar.MONTH) + 1, calendarEndDate.get(Calendar.DAY_OF_MONTH)));
        }

        //エンティティメンバーとバインド
        kanbanNameColumn.setCellValueFactory(new PropertyValueFactory<>("kanbanName"));
        kanbanNameColumn.setCellFactory(column -> createKanbanTableLabelCell());
        workflowNameColumn.setCellValueFactory(new PropertyValueFactory<>("workflowName"));
        modelNameColumn.setCellValueFactory(new PropertyValueFactory<>("modelName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        kanbanList.setRowFactory(tv -> new TableRow<DisplayData>() {

            /**
             * セルの内容を更新する
             *
             * @param item セルの新しいアイテム
             * @param empty このセルに割り当てられるデータが空かどうか（空:true、空でない:false）
             */
            @Override
            protected void updateItem(DisplayData item, boolean empty) {
                super.updateItem(item, empty);
                if (Objects.nonNull(item)) {
                    // カンバンの詳細表示ポップアップ   // TODO: この処理があると、リストのスクロール時にエラーが発生する。
                    buildTooltip(this, item);
                    this.setContextMenu(createContextMenu(item));
                } else {
                    if (Objects.nonNull(this.getTooltip())) {
                        this.setTooltip(null);
                    }
                    if (Objects.nonNull(this.getContextMenu())) {
                        this.setContextMenu(null);
                    }
                }
            }
        });

        //階層ツリー選択時処理
        hierarchyTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<KanbanHierarchyInfoEntity>> observable, TreeItem<KanbanHierarchyInfoEntity> oldValue, TreeItem<KanbanHierarchyInfoEntity> newValue) -> {
            if (Objects.nonNull(newValue)) {
                kanbanEditPermanenceData.setSelectedWorkHierarchy(newValue);
                // 別スレッドでカンバンを検索して、カンバンリストを更新する。
                searchKanbanDataTask();
            }
        });

        // カンバン選択時の処理
        //      ※.複数件選択状態で、alt + クリックで選択解除や再選択した時はイベント発生しない
        this.kanbanList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends DisplayData> observable, DisplayData oldValue, DisplayData newValue) -> {
            if (Objects.isNull(kanbanList.getSelectionModel().getSelectedItems())
                    || kanbanList.getSelectionModel().getSelectedItems().size() < 1) {
                // 未選択
                this.editKanbanButton.setDisable(true);
                this.deleteKanbanButton.setDisable(true);
                this.moveKanbanButton.setDisable(true);
            }
        });

        //カンバンクリック
        this.kanbanList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 2) {
                    this.onEditKanban(new ActionEvent());
                } else {
                    // カンバン操作ボタンの有効状態を設定する。
                    setStateKanbanButtons();
                }
            }
        });

        hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new KanbanHierarchyTreeCell());

        // 階層ツリー表示
        this.updateTree(false);
    }

    /**
     * 画面破棄時に内容に変更がないか調べて変更があるなら保存する
     *
     * @return 保存に成功したとき、または変更が存在しなかった場合true<br>ダイアログでキャンセルが押された場合false
     */
    @Override
    public boolean destoryComponent() {
        this.saveProperties();

        SplitPaneUtils.saveDividerPosition(kanbanPane, getClass().getSimpleName());

        return true;
    }


    private TableCell createKanbanTableLabelCell() {
        return new TableCell() {

            {
                this.setMinHeight(Region.USE_PREF_SIZE);
                this.setPrefHeight(Constants.KANBAN_LABELS_CELL_HEIGHT);
                this.setPadding(Constants.KANBAN_LABEL_CELL_PADDING);
            }

            /**
             * セルの内容を更新する
             *
             * @param item セルの新しいアイテム
             * @param empty このセルに割り当てられるデータが空かどうか（空:true、空でない:false）
             */
            @Override
            public void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                this.setText((String)item);
            }
        };
    }

    /**
     * コンテキストメニューを生成する
     *
     * @param displayData カンバンリスト表示用データ
     * @return コンテキストメニュー
     */
    private ContextMenu createContextMenu(DisplayData displayData) {
        ContextMenu menu = new ContextMenu ();
        if (this.useQRCode) {
            menu.getItems().add(createQRCodePrintMenu());
        }
        return menu;
    }

    /**
     * QRコード印刷メニューを生成する
     *
     * @return QRコード印刷メニュー
     */
    private MenuItem createQRCodePrintMenu() {
        final MenuItem QRCodePrint = new MenuItem();
        Label QRCodeMenuNameLabel = new Label(rb.getString("key.QRCodePrint"));
        QRCodeMenuNameLabel.setMinWidth(Constants.KANBAN_LABEL_MENU_NAME_MIN_WIDTH);
        QRCodePrint.setGraphic(QRCodeMenuNameLabel);
        QRCodePrint.setOnAction(event -> {
            this.onQRCodePrintMenu(event);
        });
        return QRCodePrint;
    }

    /**
     * QRコード印刷メニューのアクション
     *
     * @param event
     */
    private void onQRCodePrintMenu(ActionEvent event) {
        logger.info("onChangeTracebilityButton start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);
            DisplayData data = kanbanList.getSelectionModel().getSelectedItem();
            if (Objects.isNull(data)) {
                return;
            }
            KanbanInfoEntity kanban = getKanban(data.getId());

            // QRコード印刷ダイアログを表示する。
            sc.showDialog(rb.getString("key.QRCodePrint"), "QRCodePrintDialog", kanban, sc.getStage(), false);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(obj, false);
        }
    }

    /**
     * (カンバン階層ツリー) キー押下のアクション
     *
     * @param ke
     */
    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            this.updateTree(true);
        }
    }

    /**
     * (カンバン階層ツリー) 編集ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onTreeEdit(ActionEvent event) {
        try {
            TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && !item.getValue().getKanbanHierarchyId().equals(rootHierarchyId)) {
                String oldName = item.getValue().getHierarchyName();
                boolean oldFlag = item.getValue().getPartitionFlag();

                KanbanHierarchyInfoEntity newValue = new KanbanHierarchyInfoEntity();
                newValue.setHierarchyName(oldName);
                newValue.setPartitionFlag(oldFlag);
                ButtonType ret = sc.showComponentDialog(rb.getString("key.Edit"), "KanbanHierarchyEditDialogCompo", newValue);
                if (ret != ButtonType.OK) {
                    return;
                }
                if (newValue.getHierarchyName().isEmpty()) {
                    String message = String.format(rb.getString("key.InputMessage"), rb.getString("key.HierarchyName"));
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                } else if (!oldName.equals(newValue.getHierarchyName()) || (oldFlag != newValue.getPartitionFlag())) {
                    item.getValue().setHierarchyName(newValue.getHierarchyName());
                    item.getValue().setPartitionFlag(newValue.getPartitionFlag());

                    ResponseEntity res = kanbanHierarchyInfoFacade.update(item.getValue());

                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {

                        KanbanHierarchyInfoEntity value = item.getValue();
                        item.setValue(null);
                        item.setValue(value);

                        this.selectedTreeItem(item, null);

                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.EditKanban"),
                                String.format(rb.getString("key.FailedToUpdate"), rb.getString("key.KanbanHierarch")));
                        // データを戻す
                        item.getValue().setHierarchyName(oldName);
                        item.getValue().setPartitionFlag(oldFlag);
                    } else {
                        // データを戻す
                        item.getValue().setHierarchyName(oldName);
                        item.getValue().setPartitionFlag(oldFlag);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

    }

    /**
     * (カンバン階層ツリー) 移動ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onTreeMove(ActionEvent event) {
        try {
            blockUI(true);
            TreeItem<KanbanHierarchyInfoEntity> selectedItem = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedItem)) {
                return;
            } else if (selectedItem.getValue().getKanbanHierarchyId().equals(rootHierarchyId)) {
                return;
            }

            KanbanHierarchyInfoEntity selected = selectedItem.getValue();
            hierarchyTree.setVisible(false);
            kanbanList.setVisible(false);
            isDispDialog = true;
            // カンバン階層で選択されている階層IDを保持
            this.selectedHierarchyId = selected.getKanbanHierarchyId();

            TreeItem<KanbanHierarchyInfoEntity> parentTreeItem = selectedItem.getParent();
            //移動先として自分を表示させないように一時削除
            int idx = parentTreeItem.getChildren().indexOf(selectedItem);
            parentTreeItem.getChildren().remove(selectedItem);

            // ダイアログに表示するデータを設定
            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(this.hierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"), selectedItem);
            treeDialogEntity.setIsUseHierarchy(Boolean.TRUE);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "KanbanHierarchyTreeCompo", treeDialogEntity);

            TreeItem<KanbanHierarchyInfoEntity> hierarchy = (TreeItem<KanbanHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            this.selectedHierarchyId = Long.valueOf(0);
            if (ret.equals(ButtonType.OK) && Objects.nonNull(hierarchy)) {
                selected.setParentId(hierarchy.getValue().getKanbanHierarchyId());

                // カンバン階層を更新
                ResponseEntity res = kanbanHierarchyInfoFacade.update(selected);
                if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                    // ツリー更新し、移動したカンバン階層を選択する
                    if (!hierarchy.isExpanded()) {
                        hierarchy.setExpanded(true);
                    }
                    this.createRoot(selected.getKanbanHierarchyId());
                } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                    // サーバー処理エラーの場合、エラー表示して一時削除したデータを元に戻す
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Error"),
                            String.format(LocaleUtils.getString("key.FailedToUpdate"), LocaleUtils.getString("key.KanbanHierarch")));
                    parentTreeItem.getChildren().add(idx, selectedItem);
                } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)) {
                    // 移動不可能な階層だった場合、警告表示して階層ツリーを再取得して表示しなおす
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.unmovableHierarchy"));
                    this.hierarchyTree.getRoot().setExpanded(false);
                    this.hierarchyTree.getRoot().getChildren().clear();
                    this.createRoot(null);
                } else {
                    // 一時削除したデータを元に戻す
                    parentTreeItem.getChildren().add(idx, selectedItem);
                }
            } else {
                // 一時削除したデータを元に戻す
                parentTreeItem.getChildren().add(idx, selectedItem);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            hierarchyTree.setVisible(true);
            kanbanList.setVisible(true);
            isDispDialog = false;
            blockUI(false);
        }
    }

    /**
     * (カンバン階層ツリー) 削除ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onTreeDelete(ActionEvent event) {
        try {
            TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item) && !item.getValue().getKanbanHierarchyId().equals(rootHierarchyId)) {
                TreeItem<KanbanHierarchyInfoEntity> parentItem = item.getParent();

                ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, rb.getString("key.Delete"), rb.getString("key.DeleteSingleMessage"), item.getValue().getHierarchyName());
                if (ret.equals(ButtonType.OK)) {
                    ResponseEntity res = kanbanHierarchyInfoFacade.delete(item.getValue());

                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // ツリー更新し、親階層を選択する
                        createRoot(parentItem.getValue().getKanbanHierarchyId());
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.EditKanban"),
                                String.format(rb.getString("key.FailedToDelete"), rb.getString("key.KanbanHierarch")));
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * (カンバン階層ツリー) 新規作成ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onTreeCreate(ActionEvent event) {
        try {
            TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(item)) {
                // カンバン階層編集ダイアログを表示する。
                KanbanHierarchyInfoEntity hierarchy = new KanbanHierarchyInfoEntity(null, "");
                ButtonType ret = sc.showComponentDialog(rb.getString("key.NewCreate"), "KanbanHierarchyEditDialogCompo", hierarchy);
                if (ret != ButtonType.OK) {
                    return;
                }

                if (hierarchy.getHierarchyName().isEmpty()) {
                    String message = String.format(rb.getString("key.InputMessage"), rb.getString("key.HierarchyName"));
                    sc.showAlert(Alert.AlertType.WARNING, message, message);
                } else {
                    hierarchy.setParentId(item.getValue().getKanbanHierarchyId());

                    ResponseEntity res = kanbanHierarchyInfoFacade.regist(hierarchy);
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                        // ツリー更新し、新規作成したカンバン階層を選択する
                        if (!item.isExpanded()) {
                            item.setExpanded(true);
                        }
                        this.createRoot(getUriToHierarcyId(res.getUri()));
                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.EditKanban"),
                                String.format(rb.getString("key.FailedToCreate"), rb.getString("key.KanbanHierarch")));
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 編集ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onEditKanban(ActionEvent event) {
        // 休日情報がキャッシュされていない場合は読み込む。
        CacheUtils.createCacheHoliday(true);

        DisplayData data = kanbanList.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(data)) {
            KanbanInfoEntity entity = new KanbanInfoEntity(data.getId(), null, data.getKanbanName(), null);
            SelectedKanbanAndHierarchy selected = new SelectedKanbanAndHierarchy(entity,
                    hierarchyTree.selectionModelProperty().getName());
            sc.setComponent("ContentNaviPane", "KanbanDetailLite", selected);
        }
    }

    /**
     * 移動ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onMoveKanban(ActionEvent event) {
        boolean isCancel = false;
        try {
            blockUI(true);

            DisplayData selectedKanban = kanbanList.getSelectionModel().getSelectedItem();
            TreeItem<KanbanHierarchyInfoEntity> selectedHierarchy = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedKanban) || Objects.isNull(selectedHierarchy)) {
                isCancel = true;
                return;
            }

            hierarchyTree.setVisible(false);
            kanbanList.setVisible(false);
            isDispDialog = true;

            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(hierarchyTree.getRoot(), rb.getString("key.HierarchyName"));
            ButtonType ret = sc.showComponentDialog(rb.getString("key.Move"), "KanbanHierarchyTreeCompo", treeDialogEntity);

            TreeItem<KanbanHierarchyInfoEntity> hierarchy = (TreeItem<KanbanHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (ret.equals(ButtonType.OK) && Objects.nonNull(hierarchy)) {

                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            KanbanInfoEntity item = selectedKanban.getEntity();
                            KanbanInfoEntity kanban = kanbanInfoFacade.find(item.getKanbanId());
                            kanban.setParentId(hierarchy.getValue().getKanbanHierarchyId());
                            kanban.setFkUpdatePersonId(loginUserInfoEntity.getId());
                            kanban.setUpdateDatetime(new Date());

                            ResponseEntity res = kanbanInfoFacade.update(kanban);
                            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                                hierarchyTree.getSelectionModel().select(hierarchy);
                            } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                                Platform.runLater(() -> {
                                    sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.EditKanban"),
                                        String.format(rb.getString("key.FailedToUpdate"), rb.getString("key.Kanban")));
                                });
                            }
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                        }
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        hierarchyTree.setVisible(true);
                        kanbanList.setVisible(true);
                        isDispDialog = false;
                        blockUI(false);
                    }
                };
                new Thread(task).start();
            } else {
                isCancel = true;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            hierarchyTree.setVisible(true);
            kanbanList.setVisible(true);
            isDispDialog = false;
            blockUI(false);
        } finally {
            if (isCancel) {
                hierarchyTree.setVisible(true);
                kanbanList.setVisible(true);
                isDispDialog = false;
                blockUI(false);
            }
        }
    }

    /**
     * 削除ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onDeleteKanban(ActionEvent event) {
        try {
            logger.info("onDeleteKanban start.");

            List<DisplayData> datas = kanbanList.getSelectionModel().getSelectedItems();
            if (Objects.isNull(datas) || datas.isEmpty()) {
                return;
            }

            final String messgage = datas.size() > 1
                    ? rb.getString("key.DeleteMultipleMessage")
                    : rb.getString("key.DeleteSingleMessage");
            final String content = datas.size() > 1
                    ? null
                    : datas.get(0).getKanbanName();

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, rb.getString("key.Delete"), messgage, content);
            if (!ret.equals(ButtonType.OK)) {
                return;
            }

            // カンバンを削除する。
            if (datas.size() == 1) {
                deleteKanban(datas.get(0));
            } else {
                deleteKanbans(datas);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onDeleteKanban end.");
        }
    }

    /**
     * 複数のカンバンを削除する。
     *
     * @param datas 対象データ
     */
    private void deleteKanbans(List<DisplayData> datas) {
        blockUI(true);
        Task task = new Task<List<Long>>() {
            @Override
            protected List<Long> call() throws Exception {
                List<Long> skipList = new ArrayList();

                for (DisplayData data : datas) {
                    ResponseEntity responce = kanbanInfoFacade.delete(data.getId());
                    if (Objects.isNull(responce.getErrorType())) {
                        continue;
                    }

                    switch (responce.getErrorType()) {
                        case SUCCESS:// 成功
                        case NOTFOUND_DELETE:// 存在しない
                            break;
                        case THERE_START_NON_DELETABLE:// 実績あり、削除不可
                            skipList.add(data.getId());
                            break;
                        default:// その他エラー
                            throw new Exception(responce.getErrorType().name());
                    }
                }

                searchKanbanData(false);

                return skipList;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    if (!this.getValue().isEmpty()) {
                        // 削除できなかったカンバンを選択状態にする。
                        kanbanList.getSelectionModel().clearSelection();
                        boolean isFirstSkip = true;
                        for (Long kanbanId : this.getValue()) {
                            Optional<DisplayData> opt = kanbanList.getItems().stream().filter(p -> kanbanId.equals(p.getId())).findFirst();
                            if (opt.isPresent()) {
                                kanbanList.getSelectionModel().select(opt.get());
                                if (isFirstSkip) {
                                    kanbanList.scrollTo(opt.get());
                                    isFirstSkip = false;
                                }
                            }
                        }

                        // カンバン操作ボタンの有効状態を設定する。
                        setStateKanbanButtons();

                        // 選択したカンバンのうち、ｎ件は作業実績があるため削除できませんでした。
                        sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.KanbanDelete"),
                                String.format(rb.getString("key.warn.FailedDeleteKanbans"), this.getValue().size()));
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
            }

            @Override
            protected void failed() {
                super.failed();
                try {
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    // エラー
                    sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.KanbanDelete"), rb.getString("key.alert.systemError"));
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
            }
        };
        new Thread(task).start();
    }

    /**
     * カンバンを削除する。
     *
     * @param data 対象データ
     */
    private void deleteKanban(DisplayData data) {
        blockUI(true);
        Task task = new Task<ResponseEntity>() {
            @Override
            protected ResponseEntity call() throws Exception {
                return kanbanInfoFacade.delete(data.getId());
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                boolean isEnd = true;
                try {
                    // カンバン削除の結果処理
                    isEnd = deleteKanbanResultProcess(data, this.getValue());
                } finally {
                    if (isEnd) {
                        blockUI(false);
                    }
                }
            }

            @Override
            protected void failed() {
                super.failed();
                blockUI(false);
            }
        };
        new Thread(task).start();
    }

    /**
     * カンバン削除の結果処理を行なう。
     *
     * @param data 削除対象
     * @param responce 削除結果
     */
    private boolean deleteKanbanResultProcess(DisplayData data, ResponseEntity responce) {
        boolean ret = true;
        if (null == responce.getErrorType()) {
            return ret;
        }

        switch (responce.getErrorType()) {
            case THERE_START_NON_DELETABLE:
                KanbanInfoEntity kanban = data.getEntity();
                if (!loginUserInfoEntity.getRoleAuthCollection()
                        .contains(RoleAuthorityTypeEnum.DELETE_ACTUAL.getName())) {
                    // 実績削除権限がない場合は、メッセージを表示して終了(権限の種別がSYSTEM_ADMINであっても例外扱いしない)。
                    String message = rb.getString("key.warn.noPermitionDeleteActual");
                    sc.showMessageBox(Alert.AlertType.ERROR, rb.getString("key.KanbanDelete"),
                            message, new ButtonType[]{ButtonType.OK}, ButtonType.OK);

                } else if (loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
                    String message;
                    if (kanban.getKanbanStatus() == KanbanStatusEnum.WORKING || kanban.getKanbanStatus() == KanbanStatusEnum.SUSPEND) {
                        message = String.format(rb.getString("key.warn.forcedDeleteKanban1"), data.getKanbanName(), data.getStatus());
                    } else {
                        message = String.format(rb.getString("key.warn.forcedDeleteKanban2"), data.getKanbanName(), data.getStatus());
                    }

                    MessageDialogResult dialogResult = MessageDialog.show(sc.getWindow(), rb.getString("key.KanbanDelete"), message,
                            MessageDialogType.Warning, MessageDialogButtons.YesNo, 3.0, "#ff0000", "#ffffff");
                    if (!dialogResult.equals(MessageDialogResult.Yes)) {
                        return ret;
                    }
                    ret = false;

                    // カンバンを実績ごと削除する。
                    forcedDeleteKanban(data);
                } else {
                    // リソース編集権限がない場合、工程実績のあるカンバンは削除できない。
                    sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.KanbanDelete"),
                            String.format(rb.getString("key.KanbanDeleteFailed"), data.getKanbanName()));
                }
                break;
            case NOTFOUND_DELETE:
            default:
                // リストを更新する。
                onUpdateFilter(null);
                break;
        }
        return ret;
    }

    /**
     * カンバンを強制削除する。(工程実績も削除する)
     *
     * @param data 削除対象
     */
    private void forcedDeleteKanban(DisplayData data) {
        Task task = new Task<ResponseEntity>() {
            @Override
            protected ResponseEntity call() throws Exception {
                return kanbanInfoFacade.deleteForced(data.getId());
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    // カンバン強制削除の結果処理
                    forcedDeleteKanbanResultProcess(data, this.getValue());
                } finally {
                    blockUI(false);
                }
            }

            @Override
            protected void failed() {
                super.failed();
                if (Objects.nonNull(this.getException())) {
                    logger.fatal(this.getException(), this.getException());
                }
                blockUI(false);
            }
        };
        new Thread(task).start();
    }

    /**
     * カンバン強制削除の結果処理を行なう。
     *
     * @param data 削除対象
     * @param responce 削除結果
     */
    private void forcedDeleteKanbanResultProcess(DisplayData data, ResponseEntity responce) {
        if (Objects.isNull(responce.getErrorType())) {
            return;
        }

        switch (responce.getErrorType()) {
            case THERE_START_NON_DELETABLE:
                sc.showAlert(Alert.AlertType.ERROR, rb.getString("key.KanbanDelete"),
                        String.format(rb.getString("key.KanbanDeleteFailed"), data.getKanbanName()));
                break;
            case NOTFOUND_DELETE:
            default:
                // リストを更新する。
                onUpdateFilter(null);
                break;
        }
    }

    /**
     * リスト更新
     *
     * @param event
     */
    @FXML
    private void onUpdateFilter(ActionEvent event) {
        // 別スレッドでカンバンを検索して、カンバンリストを更新する。
        searchKanbanDataTask();
    }

    /**
     * 新規作成ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onCreateKanban(ActionEvent event) {
        // 休日情報がキャッシュされていない場合は読み込む。
        CacheUtils.createCacheHoliday(true);

        // 選択階層
        TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();

        // 階層未選択の場合は何もしない。
        if (Objects.isNull(item) || Objects.isNull(item.getValue())) {
            return;
        }

        // ルートを選択している場合は何もしない。
        KanbanHierarchyInfoEntity hierarchy = item.getValue();
        if (hierarchy.getKanbanHierarchyId() == this.rootHierarchyId) {
            return;
        }

        String hierarchyName = hierarchy.getHierarchyName();
        long hierarchyId = hierarchy.getKanbanHierarchyId();

        SelectedKanbanAndHierarchy selected = new SelectedKanbanAndHierarchy(null, hierarchyName, rb.getString("key.KanbanContinuousCreate"), hierarchyId);

        // カンバン作成画面を表示する。
        sc.setComponent("ContentNaviPane", "KanbanCreateLite", selected);
    }

    /**
     * カンバンIDでカンバンを取得する
     *
     * @param kanbanId
     * @return
     */
    private KanbanInfoEntity getKanban(Long kanbanId) {
        KanbanInfoFacade facade = new KanbanInfoFacade();
        return facade.find(kanbanId);
    }

    /**
     * ツリーの親階層生成
     *
     * @param selectedId 生成後に選択状態にするカンバン階層ID
     */
    private synchronized void createRoot(Long selectedId) {
        logger.debug("createRoot start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);

            // Lite階層を取得
            String liteTreeName = properties.getProperty(Config.LITE_HIERARCHY_TOP_KEY);
            KanbanHierarchyInfoEntity liteWorkHierarchy = kanbanHierarchyInfoFacade.findHierarchyName(liteTreeName);
            if (Objects.isNull(liteWorkHierarchy.getKanbanHierarchyId())) {
                throw new NullPointerException();
            }

            this.rootHierarchyId = liteWorkHierarchy.getKanbanHierarchyId();
        
            kanbanEditPermanenceData.setKanbanHierarchyRootItem(new TreeItem<>(new KanbanHierarchyInfoEntity(liteWorkHierarchy.getKanbanHierarchyId(), rb.getString("key.Kanban"))));
            TreeItem<KanbanHierarchyInfoEntity> rootItem = kanbanEditPermanenceData.getKanbanHierarchyRootItem();

            Task task = new Task<List<KanbanHierarchyInfoEntity>>() {
                @Override
                protected List<KanbanHierarchyInfoEntity> call() throws Exception {
                    // 子組織の件数を取得する。
                    long count = kanbanHierarchyInfoFacade.getAffilationHierarchyCount(liteWorkHierarchy.getKanbanHierarchyId());

                    List<KanbanHierarchyInfoEntity> hierarchies = new ArrayList();
                    for (long from = 0; from <= count; from += RANGE) {
                        List<KanbanHierarchyInfoEntity> entities = kanbanHierarchyInfoFacade.getAffilationHierarchyRange(liteWorkHierarchy.getKanbanHierarchyId(), from, from + RANGE - 1);
                        hierarchies.addAll(entities);
                    }
                    return hierarchies;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        long count = this.getValue().size();
                        rootItem.getValue().setChildCount(count);

                        this.getValue().stream().forEach((entity) -> {
                            TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);

                            // ツリー展開状態を復元
                            if (treeItems.containsKey(entity.getKanbanHierarchyId())) {
                                item.setExpanded(treeItems.get(entity.getKanbanHierarchyId()).isExpanded());
                            }
                            treeItems.put(entity.getKanbanHierarchyId(), item);

                            if (entity.getChildCount() > 0) {
                                item.getChildren().add(new TreeItem());
                            }
                            item.expandedProperty().addListener(expandedListener);
                            rootItem.getChildren().add(item);

                            if (entity.getKanbanHierarchyId().equals(selectedId)) {
                                selectedProperty.setValue(item);
                            }

                            if (item.isExpanded()) {
                                expand(item, selectedId);
                            }
                        });

                        hierarchyTree.rootProperty().setValue(rootItem);

                        Platform.runLater(() -> {
                            hierarchyTree.rootProperty().setValue(rootItem);
                            rootItem.setExpanded(true);
                            hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new KanbanHierarchyTreeCell());

                            if (Objects.nonNull(selectedProperty.get())) {
                                hierarchyTree.getSelectionModel().select(selectedProperty.get());
                                hierarchyTree.requestFocus();
                                return;
                            }

                            hierarchyTree.getSelectionModel().select(rootItem);
                        });

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    blockUI(obj, false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
    }

    /**
     * ツリー展開
     *
     * @param parentItem 親階層
     * @param selectedId 展開後に選択状態にするカンバン階層ID
     */
    private synchronized void expand(TreeItem<KanbanHierarchyInfoEntity> parentItem, Long selectedId) {
        logger.debug("expand: parentItem={}", parentItem.getValue());
        Object obj = new Object();
        try {
            if (!isDispDialog) {
                blockUI(obj, true);
            }

            parentItem.getChildren().clear();

            final long parentId = parentItem.getValue().getKanbanHierarchyId();

            Task task = new Task<List<KanbanHierarchyInfoEntity>>() {
                @Override
                protected List<KanbanHierarchyInfoEntity> call() throws Exception {
                    // 子階層の件数を取得する。
                    long count = kanbanHierarchyInfoFacade.getAffilationHierarchyCount(parentId);

                    List<KanbanHierarchyInfoEntity> hierarchies = new ArrayList();
                    for (long from = 0; from <= count; from += RANGE) {
                        List<KanbanHierarchyInfoEntity> entities = kanbanHierarchyInfoFacade.getAffilationHierarchyRange(parentId, from, from + RANGE - 1);
                        hierarchies.addAll(entities);
                    }
                    return hierarchies;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        this.getValue().stream().forEach((entity) -> {
                            TreeItem<KanbanHierarchyInfoEntity> item = new TreeItem<>(entity);
                            if (selectedHierarchyId.equals(item.getValue().getKanbanHierarchyId())) {
                                // カンバン階層で選択されている階層は移動ダイアログには表示しない
                                return;
                            }

                            if (treeItems.containsKey(entity.getKanbanHierarchyId())) {
                                // ツリー展開状態を復元
                                item.setExpanded(treeItems.get(entity.getKanbanHierarchyId()).isExpanded());
                            }
                            treeItems.put(entity.getKanbanHierarchyId(), item);

                            if (entity.getChildCount() > 0) {
                                item.getChildren().add(new TreeItem());
                            }
                            item.expandedProperty().addListener(expandedListener);
                            parentItem.getChildren().add(item);

                            if (entity.getKanbanHierarchyId().equals(selectedId)) {
                                selectedProperty.setValue(item);
                            }

                            if (item.isExpanded()) {
                                expand(item, selectedId);
                            }
                        });

                        Platform.runLater(() -> {
                            hierarchyTree.setCellFactory((TreeView<KanbanHierarchyInfoEntity> o) -> new KanbanHierarchyTreeCell());
                            if (Objects.nonNull(selectedProperty.get())) {
                                hierarchyTree.getSelectionModel().select(selectedProperty.get());
                                hierarchyTree.requestFocus();
                            }
                        });

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        if (!isDispDialog) {
                            blockUI(obj, false);
                        }
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    blockUI(obj, false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
    }

    /**
     * IDが一致するTreeItemを選択する。(存在しない場合は親を選択)
     *
     * @param parentItem 選択状態にするノードの親ノード
     * @param selectedId 選択状態にするノードのカンバン階層ID (更新するノードの子ノードのみ指定可。nullの場合は更新したノードを選択。)
     */
    private void selectedTreeItem(TreeItem<KanbanHierarchyInfoEntity> parentItem, Long selectedId) {
        if (Objects.isNull(parentItem)) {
            parentItem = this.hierarchyTree.getRoot();
        }

        Optional<TreeItem<KanbanHierarchyInfoEntity>> find = parentItem.getChildren().stream().
                filter(p -> Objects.nonNull(p.getValue())
                && Objects.nonNull(p.getValue().getKanbanHierarchyId())
                && p.getValue().getKanbanHierarchyId().equals(selectedId)).findFirst();

        if (find.isPresent()) {
            this.hierarchyTree.getSelectionModel().select(find.get());
        } else {
            this.hierarchyTree.getSelectionModel().select(parentItem);
        }
        this.hierarchyTree.scrollTo(this.hierarchyTree.getSelectionModel().getSelectedIndex());// 選択ノードが見えるようスクロール
    }

    /**
     * 別スレッドでカンバンを検索して、カンバンリストを更新する。
     */
    private void searchKanbanDataTask() {
        logger.debug("searchKanbanDataTask start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    searchKanbanData(true);
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    blockUI(obj, false);
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    blockUI(obj, false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
    }

    /**
     * カンバン検索
     *
     * @param isDispWarning 警告メッセージ表示 (true:する, false:しない)
     */
    private void searchKanbanData(boolean isDispWarning) {
        try {
            // カンバン階層ID
            if (Objects.isNull(hierarchyTree.getSelectionModel().getSelectedItem())) {
                return;
            }
            long parentId = hierarchyTree.getSelectionModel().getSelectedItem().getValue().getKanbanHierarchyId();

            // カンバン名
            String kanbanName = (StringUtils.isEmpty(kanbanNameField.getText())) ? null : kanbanNameField.getText();

            // モデル名
            String modelName = (StringUtils.isEmpty(modelNameField.getText())) ? null : modelNameField.getText();

            // カンバンステータス
            List<KanbanStatusEnum> selectStatusData =  statusList.isSelected() ? statusList.getStatus() : new ArrayList<>();

            if (parentId == rootHierarchyId
                && StringUtils.isEmpty(kanbanName)
                && StringUtils.isEmpty(modelName)
                && selectStatusData.isEmpty()) {
                // トップ階層の場合、検索条件が指定されていないと更新しない
                kanbanEditPermanenceData.setSelectedWorkHierarchy(null);
                this.clearKanbanList();
                return;
            }

            // 作業予定日
            Date scheduleStartDay = (Objects.isNull(startDatePicker.getValue())
                    ? null : DateUtils.getBeginningOfDate(Date.from(startDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
            Date scheduleEndDay = (Objects.isNull(endDatePicker.getValue())
                    ? null : DateUtils.getEndOfDate(Date.from(endDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));

            if (Objects.nonNull(scheduleStartDay) && Objects.nonNull(scheduleEndDay)) {
                if (0 > DateUtils.differenceOfDate(formatter.format(scheduleEndDay), formatter.format(scheduleStartDay))) {
                    if (isDispWarning) {
                        Platform.runLater(() -> {
                            // 開始日時が終了日時より遅い。
                            sc.showAlert(Alert.AlertType.WARNING, rb.getString("key.KanbanSearch"), rb.getString("key.DateCompErrMessage"));
                        });
                    }
                    return;
                }
            }

            ObservableList<DisplayData> tableData = FXCollections.observableArrayList();

            // 指定されたカンバン階層IDのカンバン情報一覧を取得する。
            List<KanbanInfoEntity> kanbans = kanbanInfoFacade.findByParentId(parentId, kanbanName, modelName, scheduleStartDay, scheduleEndDay, selectStatusData);

            List<DisplayData> displayDatas = new LinkedList();
            if (!kanbans.isEmpty()) {
                kanbans.stream().forEach((e) -> {
                    if (Objects.nonNull(e.getFkUpdatePersonId())) {
                        OrganizationInfoEntity person = CacheUtils.getCacheOrganization(e.getFkUpdatePersonId());
                        if (Objects.nonNull(person)) {
                            e.setUpdatePerson(person.getOrganizationName());
                        }
                    }

                    displayDatas.add(new DisplayData(e, true));
                });
            }
            tableData.addAll(displayDatas);

            Platform.runLater(() -> {
                this.kanbanMasterList.clear();
                this.kanbanMasterList.addAll(displayDatas);

                clearKanbanList();
                kanbanList.setItems(tableData);
                kanbanList.getSortOrder().add(kanbanNameColumn);
            });

            //フィルター条件の更新
            //this.saveProperties();
            if (Objects.nonNull(scheduleStartDay)) {
                Integer differenceStateDate = DateUtils.differenceOfDate(formatter.format(scheduleStartDay), formatter.format(new Date()));
                properties.setProperty(SEARCH_FILTER_START_DATE, differenceStateDate.toString());
            } else {
                properties.setProperty(SEARCH_FILTER_START_DATE, "");
            }
            if (Objects.nonNull(scheduleEndDay)) {
                Integer differenceEndDate = DateUtils.differenceOfDate(formatter.format(scheduleEndDay), formatter.format(new Date()));
                properties.setProperty(SEARCH_FILTER_END_DATE, differenceEndDate.toString());
            } else {
                properties.setProperty(SEARCH_FILTER_END_DATE, "");
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンリストの初期化
     *
     */
    private void clearKanbanList() {
        kanbanList.getItems().clear();
        kanbanList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        kanbanList.getSelectionModel().clearSelection();
    }

    /**
     * カンバン操作ボタンの有効状態を設定する。
     */
    private void setStateKanbanButtons() {
        List<DisplayData> datas = kanbanList.getSelectionModel().getSelectedItems();
        if (Objects.nonNull(datas) && !datas.isEmpty()) {
            boolean isDisabledSingle = false;
            boolean isDisabledMulti = false;

            boolean isMulti;
            if (datas.size() == 1) {
                // 1件選択
                isMulti = false;
                if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                    isDisabledSingle = true;
                    isDisabledMulti = true;
                }
            } else {
                // 複数選択
                isMulti = true;
                isDisabledSingle = true;
                if (!loginUserInfoEntity.checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN)) {
                    isDisabledMulti = true;
                }
            }

            if (isDisabledSingle) {
                Platform.runLater(() -> {
                    this.editKanbanButton.setDisable(true);
                    this.moveKanbanButton.setDisable(true);
                });
            } else {
                Platform.runLater(() -> {
                    this.editKanbanButton.setDisable(false);
                    this.moveKanbanButton.setDisable(false);
                });
            }

            if (isDisabledMulti) {
                Platform.runLater(() -> {
                    this.deleteKanbanButton.setDisable(true);
                });
            } else {
                Platform.runLater(() -> {
                    this.deleteKanbanButton.setDisable(false);
                });
            }
        } else {
            // 未選択
            Platform.runLater(() -> {
                this.editKanbanButton.setDisable(true);
                this.deleteKanbanButton.setDisable(true);
                this.moveKanbanButton.setDisable(true);
            });
        }
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
     *
     * @param obj
     * @param flg
     */
    private void blockUI(Object obj, Boolean flg) {
        if (flg) {
            blockUIs.add(obj);
        } else {
            blockUIs.remove(obj);
        }

        blockUI(!blockUIs.isEmpty());
    }

    /**
     * 再接続処理
     *
     */
    public void reconnection() {
        Platform.runLater(() -> {
            sc.showAlert(Alert.AlertType.ERROR, null, rb.getString("key.ServerReconnectMessage"));
            sc.setComponent("ContentNaviPane", "LiteKanbanListCompo");
        });
    }

    /**
     *
     * @param uri
     * @return
     */
    public static long getUriToHierarcyId(String uri) {
        long ret = 0;
        try {
            String[] split = uri.split(URI_SPLIT);
            if (split.length == 3) {
                ret = Long.parseLong(split[2]);
            }
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return ret;
    }

    /**
     *
     * @param uri
     * @return
     */
    public static long getUriToKanbanId(String uri) {
        long ret = 0;
        try {
            String[] split = uri.split(URI_SPLIT);
            if (split.length == 2) {
                ret = Long.parseLong(split[1]);
            }
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return ret;
    }

    /**
     * 前回の検索条件を読み込む。
     */
    private void loadProperties() {
        logger.info("loadProperties");
        try {
            String confFile = new StringBuilder(Constants.PROPERTY_NAME)
                    .append(Constants.PROPERTIES_EXT)
                    .toString();

            String path = new StringBuilder(System.getenv("ADFACTORY_HOME")).append(File.separator).append("conf").toString();

            boolean isNewFile = false;
            File file = new File(path, confFile);
            if (!file.exists()) {
                isNewFile = true;
            }

            AdProperty.load(Constants.PROPERTY_NAME, confFile);
            Properties props = AdProperty.getProperties(Constants.PROPERTY_NAME);

            if (isNewFile) {
                // 保存する。
                AdProperty.store(Constants.PROPERTY_NAME);
            }

            // カンバン名の選択
            boolean kanbanSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_KANBAN_NAME_SELECTED, String.valueOf(false)));
            this.kanbanNameField.setSelected(kanbanSelected);

            // カンバン名
            String kanbanName = props.getProperty(Constants.SEARCH_KANBAN_NAME);
            this.kanbanNameField.setText(kanbanName);

            // 機種名の選択
            boolean modelNameSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_MODEL_NAME_SELECTED, String.valueOf(false)));
            this.modelNameField.setSelected(modelNameSelected);

            // 機種名
            String modelName = props.getProperty(Constants.SEARCH_MODEL_NAME);
            this.modelNameField.setText(modelName);

            // ステータスの選択
            boolean statusSelected = Boolean.valueOf(props.getProperty(Constants.SEARCH_STATUS_SELECTED, String.valueOf(false)));
            this.statusList.setSelected(statusSelected);

            // ステータス
            String propStatus = props.getProperty(Constants.SEARCH_STATUS, "");
            String[] statuses = propStatus.split(",");
            IndexedCheckModel<String> cm = this.statusList.getCheckModel();
            for (String status : statuses) {
                if (status.trim().isEmpty()) {
                    continue;
                }
                cm.check(rb.getString(KanbanStatusEnum.getEnum(status).getResourceKey()));
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 検索条件を保存する。
     */
    private void saveProperties() {
        logger.info("seveProperties");
        try {
            Properties props = AdProperty.getProperties(Constants.PROPERTY_NAME);

            // カンバン名の選択
            props.setProperty(Constants.SEARCH_KANBAN_NAME_SELECTED, String.valueOf(this.kanbanNameField.isSelected()));

            // カンバン名
            String propKanbanName = this.kanbanNameField.getText();
            if (Objects.isNull(propKanbanName)) {
                propKanbanName = "";
            }
            props.setProperty(Constants.SEARCH_KANBAN_NAME, propKanbanName);

            // 機種名の選択
            props.setProperty(Constants.SEARCH_MODEL_NAME_SELECTED, String.valueOf(this.modelNameField.isSelected()));

            // 機種名
            String propModelName = this.modelNameField.getText();
            if (Objects.isNull(propModelName)) {
                propModelName = "";
            }
            props.setProperty(Constants.SEARCH_MODEL_NAME, propModelName);

            // ステータスの選択
            props.setProperty(Constants.SEARCH_STATUS_SELECTED, String.valueOf(this.statusList.isSelected()));

            // ステータス
            String propStatus = "";
            List<String> statuses = new ArrayList();
            ObservableList<Integer> indices = this.statusList.getCheckModel().getCheckedIndices();
            if (Objects.nonNull(this.statusList.getStatus())) {
                indices.stream().forEach(item -> {
                    statuses.add(KanbanStatusEnum.getValueText(item));
                });
                propStatus = String.join(",", statuses);
            }
            props.setProperty(Constants.SEARCH_STATUS, propStatus);

            // 保存する。
            AdProperty.store(Constants.PROPERTY_NAME);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * キャッシュに情報を読み込み、組織ツリーを更新する。
     */
    private void updateTree(boolean isRefresh) {
        logger.info("updateTree start.");
        Object obj = new Object();
        try {
            blockUI(obj, true);

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if (isRefresh) {
                        CacheUtils.removeCacheData(EquipmentInfoEntity.class);
                        CacheUtils.removeCacheData(OrganizationInfoEntity.class);
                        CacheUtils.removeCacheData(HolidayInfoEntity.class);
                        CacheUtils.removeCacheData(LabelInfoEntity.class);
                    }

                    CacheUtils.createCacheEquipment(true);
                    CacheUtils.createCacheOrganization(true);
                    CacheUtils.createCacheHoliday(true);
                    CacheUtils.createCacheLabel(true);
                    CacheUtils.createCacheKanbanHierarchy(true);

                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        if (Objects.isNull(kanbanEditPermanenceData.getKanbanHierarchyRootItem())) {
                            createRoot(null);
                        } else {
                            hierarchyTree.rootProperty().setValue(kanbanEditPermanenceData.getKanbanHierarchyRootItem());
                            if (isRefresh) {
                                TreeItem<KanbanHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
                                createRoot(item.getValue().getKanbanHierarchyId());
                            } else {
                                selectedTreeItem(kanbanEditPermanenceData.getSelectedKanbanHierarchy(), null);
                            }
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                        logger.info("updateTree end.");
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(obj, false);
                    }
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(obj, false);
        }
    }

    /**
     * カンバン詳細表示ツールチップの表示Pane作成
     *
     * @param data カンバン表示エンティティ
     * @return 表示Pane
     */
    private Pane createKanbanInfo(DisplayData data) {
        VBox vbox = new VBox();
        final double vboxWidth = 500.0;
        vbox.setMaxWidth(vboxWidth);

        // カンバン名
        vbox.getChildren().add(new Label(data.getKanbanName()));

        // ラベル名
        List<LabelInfoEntity> availableLabels = data.getLabelIds().stream()
                .map(labelId -> CacheUtils.getCacheLabel(labelId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!availableLabels.isEmpty()) {
            final String labelNames = availableLabels.stream()
                    .sorted(Comparator.comparingLong(LabelInfoEntity::getLabelPriority))
                    .map(label -> label.getLabelName())
                    .collect(Collectors.joining(Constants.KANBAN_LABELS_DELIMITER_IN_TOOLTIP));
            Label labelNamesLabel = new Label(labelNames);
            labelNamesLabel.setMinWidth(vboxWidth);
            labelNamesLabel.setMaxWidth(vboxWidth);
            labelNamesLabel.setWrapText(true);
            vbox.getChildren().add(labelNamesLabel);
        }

        return vbox;
    }

    /**
     * ツールチップを構築する。
     *
     * @param control 構築先
     * @param pane 表示内容
     */
    private void buildTooltip(Control control, DisplayData item) {
        try {
            Tooltip toolTip = TooltipBuilder.build(control);
            toolTip.setMaxWidth(500);
            toolTip.setGraphic(createKanbanInfo(item));
        } catch (Exception ex) {
        }
    }
}
