/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.component;

import adtekfuji.admanagerapp.ledgermanagerplugin.common.LedgerHierarchyTreeCell;
import adtekfuji.admanagerapp.ledgermanagerplugin.entity.LedgerFileListTableDataEntity;
import adtekfuji.admanagerapp.ledgermanagerplugin.entity.LedgerListTableDataEntity;
import adtekfuji.clientservice.LedgerFileInfoFacade;
import adtekfuji.clientservice.LedgerHierarchyInfoFacade;
import adtekfuji.clientservice.LedgerInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.ledger.*;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.DisplayLimitTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.TreeDialogEntity;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * FXML Controller class
 *
 * @author yu.nara
 */
@FxComponent(id = "LedgerManagerCompo", fxmlPath = "/fxml/compo/ledger_manager_compo.fxml")
public class LedgerManagerCompoFxController implements Initializable, ComponentHandler {
    public static final String PROPERTY_TAG = "LedgerManager";
    public static final String PROPERTY_FILE = "adFactoryLedgerManager.properties";
    public static final String SELECT_LEDGER_DOWNLOAD_PATH = "select.ledger.download.path";// ダウンロードパス設定

    final String defaultPath = System.getProperty("user.home") + File.separator + "Documents";

    Properties properties;

    private static final String DISABLE_DATE_STYLE = "-fx-background-color: lightgray;";// カレンダーで選択不可な日のスタイル
    private final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmm");
    private final static Logger logger = LogManager.getLogger();
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();

    private final LedgerInfoFacade ledgerInfoFacade = new LedgerInfoFacade();
    private final LedgerHierarchyInfoFacade ledgerHierarchyInfoFacade = new LedgerHierarchyInfoFacade();
    private final LedgerFileInfoFacade ledgerFileInfoFacade = new LedgerFileInfoFacade();
    private final SceneContiner sc = SceneContiner.getInstance();
    private TreeItem<LedgerHierarchyInfoEntity> rootItem;

    @FXML
    private SplitPane ledgerManagerPane;
    @FXML
    private TreeView<LedgerHierarchyInfoEntity> hierarchyTree;
    @FXML
    private Button createTreeButton;
    @FXML
    private Button editTreeButton;
    @FXML
    private Button moveTreeButton;
    @FXML
    private Button delTreeButton;

    @FXML
    private Pane Progress;
    @FXML
    private Pane Progress2;

    @FXML
    private PropertySaveTableView<LedgerListTableDataEntity> ledgerList;

    @FXML
    private TableColumn<LedgerListTableDataEntity, String> ledgerNameColumn;

    @FXML
    private TableColumn<LedgerListTableDataEntity, String> ledgerTypeColumn;

    @FXML
    private TableColumn<LedgerListTableDataEntity, String> updateDateColumn;

    @FXML
    private TableColumn<LedgerListTableDataEntity, String> lastDatetimeColumn;

    @FXML
    private TableColumn<LedgerListTableDataEntity, String> nextDatetimeColumn;

    @FXML
    private PropertySaveTableView<LedgerFileListTableDataEntity> ledgerFileList;
    @FXML
    private TableColumn<LedgerFileListTableDataEntity, String > ledgerFileCreatorNameColumn;
    @FXML
    private TableColumn<LedgerFileListTableDataEntity, String > ledgerFileCreateDateColumn;
    @FXML
    private TableColumn<LedgerFileListTableDataEntity, String > workerNameColumn;
    @FXML
    private TableColumn<LedgerFileListTableDataEntity, String > equipmentNameColumn;
    @FXML
    private TableColumn<LedgerFileListTableDataEntity, String > workStartNameColumn;
    @FXML
    private TableColumn<LedgerFileListTableDataEntity, String > workCompNameColumn;
    @FXML
    private ComboBox<DisplayLimitTypeEnum> displayLimitCombo;
    @FXML
    private Pane specificPeriod;
    @FXML
    private Pane keyArea; // 検索エリア

    @FXML
    DatePicker fromDatePicker;
    @FXML
    DatePicker toDatePicker;
    @FXML
    Button updateFileList;

    @FXML
    private Button editListButton; // 編集
    @FXML
    private Button moveListButton; // 移動
    @FXML
    private Button delListButton; // 削除
    @FXML
    private Button reportOutButton; // 出力
    @FXML
    private Button downloadButton; // ダウンロードボタン

    /**
     * 帳票ファイルテーブルの更新
     */
    void refreshLedgerFileTable() {
        keyArea.getChildren().clear();
        LedgerListTableDataEntity selectedItem = ledgerList.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(selectedItem)) {
            // 帳票ファイルの更新
            selectedItem
                    .getLedgerInfoEntity()
                    .getLedgerCondition()
                    .getKeyTag()
                    .stream()
                    .map(NameValueEntity::getName)
                    .forEach(name -> {
                        Label label = new Label(name);
                        TextField textField = new TextField();
                        textField.setPrefWidth(140.0);
                        HBox hBox = new HBox();
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        hBox.setSpacing(8);
                        hBox.getChildren();
                        hBox.getChildren().add(label);
                        hBox.getChildren().add(textField);
                        keyArea.getChildren().add(hBox);
                    });
        }

        if (Objects.isNull(selectedItem)) {
            this.editListButton.setDisable(true);
            this.moveListButton.setDisable(true);
            this.delListButton.setDisable(true);
            this.reportOutButton.setDisable(true);
            this.ledgerFileList.getItems().clear();
        } else {
            this.editListButton.setDisable(false);
            this.moveListButton.setDisable(false);
            this.delListButton.setDisable(false);
            this.reportOutButton.setDisable(false);

            Platform.runLater(() -> {


            });
        }

        // 帳票選択時に帳票ファイル一覧を表示
        refreshLedgerFileList();
    }

    /**
     * ソート用のキーを作成
     * @param entity 帳票ファイル情報
     * @return ソート機
     */
    String createSortedKey(LedgerFileInfoEntity entity) {
        return entity.getUpdateDatetime() + "####"
                + entity.getFromDate() + "####"
                + entity.getToDate();
    }

    /**
     * 帳票ファイルリストの更新
     */
    void refreshLedgerFileList() {
        LedgerListTableDataEntity newValue = this.ledgerList.getSelectionModel().getSelectedItem();
        List<LedgerFileListTableDataEntity> newFileValue = this.ledgerFileList.getSelectionModel().getSelectedItems();
        this.updateFileList.setDisable(Objects.isNull(newValue));

        boolean notFileSelected = Objects.isNull(newFileValue) || newFileValue.isEmpty();
        this.downloadButton.setDisable(notFileSelected);


        if (Objects.nonNull(newValue)) {
            Platform.runLater(() -> {
                try {
                    this.blockLedgerFileUI(true);

                    List<NameValueEntity> keyValues
                            = newValue
                            .getLedgerInfoEntity()
                            .getLedgerCondition()
                            .getKeyTag();

                    LedgerFileSearchEntity entity = new LedgerFileSearchEntity(newValue.getLedgerInfoEntity().getLedgerId());
                    if (!keyArea.getChildren().isEmpty()) {
                        List<NameValueEntity> keywords = new ArrayList<>();
                        for (int n = 0; n < keyValues.size(); ++n) {
                            TextField textField = (TextField) ((HBox) keyArea.getChildren().get(n)).getChildren().get(1);
                            NameValueEntity nameValueEntity = new NameValueEntity(keyValues.get(n).getValue(), textField.getText());
                            if (StringUtils.nonEmpty(nameValueEntity.getValue())) {
                                keywords.add(nameValueEntity);
                            }
                        }
                        if (!keywords.isEmpty()) {
                            entity.setKeywords(keywords);
                        }
                    }

                    switch (displayLimitCombo.getValue()) {
                        default:
                        case Display10:
                            entity.setLimit(10);
                            break;
                        case Display50:
                            entity.setLimit(50);
                            break;
                        case Display100:
                            entity.setLimit(100);
                            break;
                        case DisplayPeriod:
                            entity.setFromDatetime(DateUtils.getBeginningOfDate(fromDatePicker.getValue()));
                            entity.setToDatetime(DateUtils.getEndOfDate(toDatePicker.getValue()));
                            break;
                    }

                    this.ledgerFileList.getColumns().clear();
                    // 帳票リストの更新
                    this.ledgerList
                            .getSelectionModel()
                            .getSelectedItem()
                            .getLedgerInfoEntity()
                            .getLedgerCondition()
                            .getKeyTag()
                            .stream()
                            .filter(item -> !item.isEmpty())
                            .forEach(item -> {
                                TableColumn<LedgerFileListTableDataEntity, String> addColumn = new TableColumn<>(item.getName());
                                addColumn.setCellValueFactory(f -> f.getValue().getAddColumnProperty(map -> map.getOrDefault(item.getValue(), "")));
                                addColumn.setStyle("-fx-alignment: center-right;");
                                this.ledgerFileList.getColumns().add(addColumn);
                            });

                    this.ledgerFileList.getColumns().add(ledgerFileCreateDateColumn);
                    this.ledgerFileList.getColumns().add(workerNameColumn);
                    this.ledgerFileList.getColumns().add(equipmentNameColumn);
                    this.ledgerFileList.getColumns().add(ledgerFileCreatorNameColumn);
                    this.ledgerFileList.getColumns().add(workStartNameColumn);
                    this.ledgerFileList.getColumns().add(workCompNameColumn);

                    // 帳票リストの更新
                    List<LedgerFileInfoEntity> ledgerFileInfoEntities = ledgerFileInfoFacade.findChildren(entity);
                    if (Objects.nonNull(ledgerFileInfoEntities)) {
                        List<LedgerFileListTableDataEntity> entities
                                = ledgerFileInfoEntities
                                .stream()
                                .sorted(comparing(this::createSortedKey))
                                .map(LedgerFileListTableDataEntity::new)
                                .collect(toList());
                        this.ledgerFileList.setItems(FXCollections.observableArrayList(entities));
                    }
                } finally {
                    this.blockLedgerFileUI(false);
                }
            });
        } else {
            Platform.runLater(() -> {
                this.ledgerFileList.getColumns().clear();
                this.ledgerFileList.getColumns().add(ledgerFileCreateDateColumn);
                this.ledgerFileList.getColumns().add(workerNameColumn);
                this.ledgerFileList.getColumns().add(equipmentNameColumn);
                this.ledgerFileList.getColumns().add(ledgerFileCreatorNameColumn);
                this.ledgerFileList.getColumns().add(workStartNameColumn);
                this.ledgerFileList.getColumns().add(workCompNameColumn);
            });
        }
    }

    public void onUpdateFileList() {
        refreshLedgerFileList();
    }

    static class DisplayLimitComboBoxCellFactory extends ListCell<DisplayLimitTypeEnum> {
        @Override
        protected void updateItem(DisplayLimitTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                switch(item) {
                    case Display10:
                        setText(String.format(LocaleUtils.getString("key.LatestCount"), 10));
                        return;
                    case Display50:
                        setText(String.format(LocaleUtils.getString("key.LatestCount"), 50));
                        return;
                    case Display100:
                        setText(String.format(LocaleUtils.getString("key.LatestCount"), 100));
                        return;
                    case DisplayPeriod:
                        setText(LocaleUtils.getString("key.SpecificPeriod"));
                        return;
                }
            }
        }
    }

    /**
     * コンポーネントを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            AdProperty.load(PROPERTY_TAG, PROPERTY_FILE);
            properties = AdProperty.getProperties(PROPERTY_TAG);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        // 帳票階層のポップアップ表示設定
        ContextMenu hierarchyTreeContextMenu = new ContextMenu();
        MenuItem createHierarchyTree = new MenuItem(LocaleUtils.getString("key.NewCreate"));
        createHierarchyTree.setOnAction(e-> this.onTreeCreate());
        hierarchyTreeContextMenu.getItems().add(createHierarchyTree);

        MenuItem editHierarchyTree = new MenuItem(LocaleUtils.getString("key.Edit"));
        editHierarchyTree.setOnAction(e -> this.onTreeEdit());
        hierarchyTreeContextMenu.getItems().add(editHierarchyTree);

        MenuItem moveHierarchyTree = new MenuItem(LocaleUtils.getString("key.Move"));
        moveHierarchyTree.setOnAction(e -> this.onTreeMove());
        hierarchyTreeContextMenu.getItems().add(moveHierarchyTree);

        MenuItem deleteHierarchyTree = new MenuItem(LocaleUtils.getString("key.Delete"));
        deleteHierarchyTree.setOnAction(e -> this.onTreeDelete());
        hierarchyTreeContextMenu.getItems().add(deleteHierarchyTree);

        hierarchyTree.setOnContextMenuRequested((ContextMenuEvent event) -> {
            boolean isRoot = rootItem.equals(hierarchyTree.getSelectionModel().getSelectedItem());
            editHierarchyTree.setDisable(isRoot);
            moveHierarchyTree.setDisable(isRoot);
            deleteHierarchyTree.setDisable(isRoot);
            hierarchyTreeContextMenu.show(sc.getWindow(), event.getScreenX(), event.getScreenY());
        });

        // 帳票一覧の表示設定
        this.ledgerList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        this.ledgerList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.ledgerList.init("ledgerList");
        this.ledgerNameColumn.setCellValueFactory(f->f.getValue().ledgerNameProperty());
        this.ledgerTypeColumn.setCellValueFactory(f -> f.getValue().ledgerTypeProperty());
        this.updateDateColumn.setCellValueFactory(f -> f.getValue().updateDatetimeProperty());
        this.nextDatetimeColumn.setCellValueFactory(f -> f.getValue().nextDatetimeProperty());
        this.lastDatetimeColumn.setCellValueFactory(f -> f.getValue().lastDatetimeProperty());
        this.ledgerList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    refreshLedgerFileTable();
                });

        this.ledgerFileList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        this.ledgerFileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.ledgerFileList.init("ledgerFileList");
        this.ledgerFileList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    boolean notFileSelected = Objects.isNull(newValue);
                    this.downloadButton.setDisable(notFileSelected);
                });


        this.ledgerFileCreateDateColumn.setCellValueFactory(f->f.getValue().ledgerFileCreateDateProperty());
        this.workerNameColumn.setCellValueFactory(f->f.getValue().workerProperty());
        this.equipmentNameColumn.setCellValueFactory(f->f.getValue().equipmentProperty());
        this.ledgerFileCreatorNameColumn.setCellValueFactory(f->f.getValue().ledgerFileCreatorProperty());
        this.workStartNameColumn.setCellValueFactory(f->f.getValue().workStartDateProperty());
        this.workCompNameColumn.setCellValueFactory(f->f.getValue().workEndDateProperty());

        // 帳票リスト表示設定
        this.hierarchyTree
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((ObservableValue<? extends TreeItem<LedgerHierarchyInfoEntity>> observable,
                              TreeItem<LedgerHierarchyInfoEntity> oldValue,
                              TreeItem<LedgerHierarchyInfoEntity> newValue) -> {
                    if (Objects.nonNull(newValue)) {
                        // 工程リスト更新
                        LedgerHierarchyInfoEntity ledgerHierarchyInfoEntity = newValue.getValue();
                        if (Objects.isNull(ledgerHierarchyInfoEntity.getLedgerInfoEntities())) {
                            ledgerHierarchyInfoEntity.setLedgerInfoEntities(ledgerInfoFacade.findChildren(ledgerHierarchyInfoEntity.getHierarchyId()));
                        }

                        List<LedgerListTableDataEntity> listTableColumns
                                = ledgerHierarchyInfoEntity
                                .getLedgerInfoEntities()
                                .stream()
                                .map(LedgerListTableDataEntity::new)
                                .collect(toList());
                        this.ledgerList.setItems(FXCollections.observableArrayList(listTableColumns));

                        this.ledgerList.getSortOrder().add(this.ledgerNameColumn);
                    } else {
                        // 工程リストクリア
                        this.ledgerList.getItems().clear();
                    }

                    if(Objects.isNull(newValue)) {
                        this.createTreeButton.setDisable(true);
                        this.editTreeButton.setDisable(true);
                        this.moveTreeButton.setDisable(true);
                        this.delTreeButton.setDisable(true);
                    } else {
                        if (newValue.getValue().getHierarchyId() == 0) {
                            this.createTreeButton.setDisable(false);
                            this.editTreeButton.setDisable(true);
                            this.moveTreeButton.setDisable(true);
                            this.delTreeButton.setDisable(true);
                        } else {
                            this.createTreeButton.setDisable(false);
                            this.editTreeButton.setDisable(false);
                            this.moveTreeButton.setDisable(false);
                            this.delTreeButton.setDisable(false);
                        }
                    }
                });

        // 帳票リストのダブルクリック対応
        this.ledgerList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!(event.getTarget() instanceof TableColumnHeader)
                        && Objects.nonNull(event.getSource())
                        && event.getClickCount() == 2) {
                    this.onReportOut();
                }
            }
        });

        // 帳票ファイルリストのダブルクリック対応
        this.ledgerFileList.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!(event.getTarget() instanceof TableColumnHeader)
                        && Objects.nonNull(event.getSource())
                        && event.getClickCount() == 2) {
                    this.downloadAndOpen();
                }
            }
        });

        // 帳票のポップアップ表示設定
        ContextMenu ledgerListContextMenu = new ContextMenu();
        MenuItem createLedger = new MenuItem(LocaleUtils.getString("key.NewCreate"));
        createLedger.setOnAction(e-> this.onListCreate());
        ledgerListContextMenu.getItems().add(createLedger);

        MenuItem editLedger = new MenuItem(LocaleUtils.getString("key.Edit"));
        editLedger.setOnAction(e -> this.onListEdit());
        ledgerListContextMenu.getItems().add(editLedger);

        MenuItem moveLedger = new MenuItem(LocaleUtils.getString("key.Move"));
        moveLedger.setOnAction(e -> this.onListMove());
        ledgerListContextMenu.getItems().add(moveLedger);

        MenuItem deleteLedger = new MenuItem(LocaleUtils.getString("key.Delete"));
        deleteLedger.setOnAction(e -> this.onListDelete());
        ledgerListContextMenu.getItems().add(deleteLedger);

        MenuItem reportOut = new MenuItem(LocaleUtils.getString("key.OutLedgerTitle"));
        reportOut.setOnAction(e -> this.onReportOut());
        ledgerListContextMenu.getItems().add(reportOut);

        ledgerList.setOnContextMenuRequested((ContextMenuEvent event) -> {
            boolean isNoSelected = ledgerList.getSelectionModel().getSelectedItems().isEmpty();
            editLedger.setDisable(isNoSelected);
            moveLedger.setDisable(isNoSelected);
            deleteLedger.setDisable(isNoSelected);
            reportOut.setDisable(isNoSelected);

            ledgerListContextMenu.show(sc.getWindow(), event.getScreenX(), event.getScreenY());
        });


        // 帳票ファイルリストのポップアップ
        ContextMenu ledgerFileListContextMenu = new ContextMenu();
        MenuItem deleteLedgerFile = new MenuItem(LocaleUtils.getString("key.Delete"));
        deleteLedgerFile.setOnAction(e-> this.onDeleteFile());
        ledgerFileListContextMenu.getItems().add(deleteLedgerFile);

        MenuItem downloadLedgerFile = new MenuItem(LocaleUtils.getString("key.Download"));
        downloadLedgerFile.setOnAction(e -> this.onDownload());
        ledgerFileListContextMenu.getItems().add(downloadLedgerFile);


        ledgerFileList.setOnContextMenuRequested((ContextMenuEvent event) -> {
            boolean isNoSelected = ledgerFileList.getSelectionModel().getSelectedItems().isEmpty();
            deleteLedgerFile.setDisable(isNoSelected);
            downloadLedgerFile.setDisable(isNoSelected);
            ledgerFileListContextMenu.show(sc.getWindow(), event.getScreenX(), event.getScreenY());
        });

        Callback<ListView<DisplayLimitTypeEnum>, ListCell<DisplayLimitTypeEnum>> comboCellFactory = (ListView<DisplayLimitTypeEnum> param) -> new LedgerManagerCompoFxController.DisplayLimitComboBoxCellFactory();
        displayLimitCombo.setButtonCell(new LedgerManagerCompoFxController.DisplayLimitComboBoxCellFactory());
        displayLimitCombo.setCellFactory(comboCellFactory);

        displayLimitCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            // 表示件数が変更された場合
            // 期間表示に変更
            boolean isSpecificPeriod = DisplayLimitTypeEnum.DisplayPeriod.equals(newValue);
            specificPeriod.setVisible(isSpecificPeriod);
            specificPeriod.setManaged(isSpecificPeriod);
            // 更新
            refreshLedgerFileList();
        });

        Platform.runLater(() -> {
            displayLimitCombo.setItems(FXCollections.observableArrayList(DisplayLimitTypeEnum.values()));
            displayLimitCombo.setVisible(true);
            displayLimitCombo.setValue(DisplayLimitTypeEnum.Display10);
        });

        // 開始日には、終了日より後の日は選択できない。
        this.fromDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (Objects.isNull(date)
                                || Objects.isNull(toDatePicker.getValue())
                                || !date.isAfter(toDatePicker.getValue())) {
                            return;
                        }
                        setDisable(true);
                        setStyle(DISABLE_DATE_STYLE);
                    }
                };
            }
        });

        // 終了日には、開始日より前の日は選択できない。
        this.toDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (Objects.isNull(date)
                                || Objects.isNull(fromDatePicker.getValue())
                                || !date.isBefore(fromDatePicker.getValue())) {
                            return;
                        }
                        setDisable(true);
                        setStyle(DISABLE_DATE_STYLE);
                    }
                };
            }
        });

        Date now = new Date();
        fromDatePicker.setValue(DateUtils.toLocalDate(now));
        toDatePicker.setValue(DateUtils.toLocalDate(now));

        // ツリーのルートノードを生成する。
        createRoot();
    }


    /**
     * ツリーの削除
     */
    @FXML
    private void onTreeDelete() {
        try {
            TreeItem<LedgerHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item) || Objects.isNull(item.getParent())) {
                return;
            }
            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getValue().getHierarchyName());
            if (!ret.equals(ButtonType.OK)) {
                return;
            }

            ResponseEntity res = ledgerHierarchyInfoFacade.remove(item.getValue().getHierarchyId());
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                this.hierarchyTree.getSelectionModel().select(item.getParent());
                item.expandedProperty().removeListener(this.changeListener);
                item.getParent().getChildren().remove(item);
            }else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR,
                        String.format(LocaleUtils.getString("key.DeleteOf")
                                , LocaleUtils.getString("key.LedgerHierarchy")),
                        String.format(LocaleUtils.getString(LocaleUtils.getString("key.FailedOf")),
                                String.format(LocaleUtils.getString("key.DeleteOf"),
                                        LocaleUtils.getString("key.LedgerHierarchy"))));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ツリーの作成
     *
     */
    @FXML
    private void onTreeCreate() {
        try {
            TreeItem<LedgerHierarchyInfoEntity> item = hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(item)) {
                return;
            }
            String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
            String hierarchyName = sc.showTextInputDialog(LocaleUtils.getString("key.NewCreate"), message, LocaleUtils.getString("key.HierarchyName"), "");
            if (Objects.isNull(hierarchyName)) {
                return;
            } else if (hierarchyName.isEmpty()) {
                sc.showAlert(Alert.AlertType.WARNING, message, message);
                return;
            }

            LedgerHierarchyInfoEntity hierarchy = new LedgerHierarchyInfoEntity();
            hierarchy.setHierarchyName(hierarchyName);
            hierarchy.setParentHierarchyId(item.getValue().getHierarchyId());

            if (!item.isExpanded()) {
                item.setExpanded(true);
            }
            ResponseEntity res = ledgerHierarchyInfoFacade.register(hierarchy);
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                hierarchy = ledgerHierarchyInfoFacade.findURI(res.getUri());
                item.getChildren().add(new TreeItem<>(hierarchy));
                item.getChildren().sort(comparing(t -> t.getValue().getHierarchyName()));
            } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR,
                        String.format(LocaleUtils.getString("key.CreateOf")
                                , LocaleUtils.getString("key.LedgerHierarchy")),
                        String.format(LocaleUtils.getString(LocaleUtils.getString("key.FailedOf")),
                                String.format(LocaleUtils.getString("key.CreateOf"),
                                        LocaleUtils.getString("key.LedgerHierarchy"))));
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ツリー名の変更
     */
    @FXML
    private void onTreeEdit() {
        TreeItem<LedgerHierarchyInfoEntity> item = this.hierarchyTree.getSelectionModel().getSelectedItem();
        if (Objects.isNull(item) || item.getValue().getHierarchyId() == 0) {
            return;
        }

        final String oldHierarchyName = item.getValue().getHierarchyName();
        String message = String.format(LocaleUtils.getString("key.InputMessage"), LocaleUtils.getString("key.HierarchyName"));
        String newHierarchyName = sc.showTextInputDialog(LocaleUtils.getString("key.Edit"), message, LocaleUtils.getString("key.HierarchyName"), oldHierarchyName);
        if (StringUtils.isEmpty(newHierarchyName)) {
            return;
        }

        if (StringUtils.equals(oldHierarchyName, newHierarchyName)) {
            return;
        }

        item.getValue().setHierarchyName(newHierarchyName);
        ResponseEntity res = ledgerHierarchyInfoFacade.update(item.getValue());
        if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
            LedgerHierarchyInfoEntity value = ledgerHierarchyInfoFacade.find(item.getValue().getHierarchyId());
            item.setValue(value);
        } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
            sc.showAlert(Alert.AlertType.ERROR,
                    String.format(LocaleUtils.getString("key.EditOf")
                            , LocaleUtils.getString("key.LedgerHierarchy")),
                    String.format(LocaleUtils.getString(LocaleUtils.getString("key.FailedOf")),
                            String.format(LocaleUtils.getString("key.EditOf"),
                                    LocaleUtils.getString("key.LedgerHierarchy"))));
            // データを戻す
            item.getValue().setHierarchyName(oldHierarchyName);
        } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
            // 排他バージョンが異なる。
            sc.showAlert(Alert.AlertType.ERROR,
                    String.format(LocaleUtils.getString("key.EditOf")
                            , LocaleUtils.getString("key.LedgerHierarchy")),
                            LocaleUtils.getString("key.alert.differentVerInfo"));
            LedgerHierarchyInfoEntity value = ledgerHierarchyInfoFacade.find(item.getValue().getHierarchyId());
            item.setValue(null);
            item.setValue(value);
        } else {
            // データを戻す
            item.getValue().setHierarchyName(oldHierarchyName);
        }
        this.hierarchyTree.getSelectionModel().select(item);
    }

    /**
     * ツリーの展開イベント
     * @param treeItem ツリーアイテム
     */
    static void doExpand(TreeItem<LedgerHierarchyInfoEntity> treeItem) {
        if (treeItem.isExpanded()) { return;}
        doExpand(treeItem.getParent());
        treeItem.setExpanded(true);
    }

    /**
     * ツリーの移動
     */
    @FXML
    private void onTreeMove() {
        try {
            sc.blockUI(true);
            this.hierarchyTree.setVisible(false);
            this.ledgerList.setVisible(false);

            TreeItem<LedgerHierarchyInfoEntity> selectedItem = this.hierarchyTree.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedItem) || selectedItem.getValue().getHierarchyId() == 0) {
                return;
            }

            TreeItem<LedgerHierarchyInfoEntity> parentTreeItem = selectedItem.getParent();
            //移動先として自分を表示させないように一時削除
            int idx = parentTreeItem.getChildren().indexOf(selectedItem);
            parentTreeItem.getChildren().remove(selectedItem);

            LedgerHierarchyInfoEntity selected = selectedItem.getValue();
            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(this.hierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"), selectedItem);
            treeDialogEntity.setIsUseHierarchy(Boolean.TRUE);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "LedgerHierarchyTreeCompo", treeDialogEntity);

            TreeItem<LedgerHierarchyInfoEntity> dest = (TreeItem<LedgerHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (!ret.equals(ButtonType.OK) || Objects.isNull(dest)) {
                // 失敗したので元に戻す。
                parentTreeItem.getChildren().add(idx, selectedItem);
                this.hierarchyTree.getSelectionModel().select(selectedItem);
                return;
            }

            selected.setParentHierarchyId(dest.getValue().getHierarchyId());

            // 更新処理
            ResponseEntity res = ledgerHierarchyInfoFacade.update(selected);
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                LedgerHierarchyInfoEntity value = ledgerHierarchyInfoFacade.find(selected.getHierarchyId());
                selectedItem.setValue(value);
                dest.getChildren().add(selectedItem);
                dest.getChildren().setAll(dest.getChildren().sorted(comparing(item->item.getValue().getHierarchyName())));
                doExpand(dest);
                this.hierarchyTree.getSelectionModel().select(selectedItem);
            } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR,
                        String.format(LocaleUtils.getString("key.MoveOf")
                                , LocaleUtils.getString("key.LedgerHierarchy")),
                        String.format(LocaleUtils.getString(LocaleUtils.getString("key.FailedOf")),
                                String.format(LocaleUtils.getString("key.MoveOf"),
                                        LocaleUtils.getString("key.LedgerHierarchy"))));
                //一時削除したデータを元に戻す
                parentTreeItem.getChildren().add(idx, selectedItem);
            } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                // 排他バージョンが異なる。
                sc.showAlert(Alert.AlertType.ERROR,
                        String.format(LocaleUtils.getString("key.MoveOf"),
                                LocaleUtils.getString("key.LedgerHierarchy")),
                        LocaleUtils.getString("key.alert.differentVerInfo"));
                this.createRoot();
            } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.UNMOVABLE_HIERARCHY)) {
                // 移動不可能な階層だった場合、警告表示して階層ツリーを再取得して表示しなおす。
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.unmovableHierarchy"));
                this.createRoot();
            } else {
                //一時削除したデータを元に戻す
                parentTreeItem.getChildren().add(idx, selectedItem);
            }
        } finally {
            this.ledgerList.setVisible(true);
            this.hierarchyTree.setVisible(true);
            sc.blockUI(false);
        }
    }


    /**
     * ツリーの子階層生成
     * @param parentItems 親階層
     */
    public synchronized void buildChildren(List<TreeItem<LedgerHierarchyInfoEntity>> parentItems) {
        try {
            List<Long> searchIds
                    = parentItems
                    .stream()
                    .map(TreeItem::getValue)
                    .map(LedgerHierarchyInfoEntity::getHierarchyId)
                    .collect(toList());

            Map<Long, List<LedgerHierarchyInfoEntity>> ledgerHierarchyInfoEntityMap
                    = ledgerHierarchyInfoFacade
                    .findChildren(searchIds)
                    .stream()
                    .collect(groupingBy(LedgerHierarchyInfoEntity::getParentHierarchyId, toList()));

            parentItems.forEach(parentItem -> {
                        List<LedgerHierarchyInfoEntity> ledgerHierarchyInfoEntities = ledgerHierarchyInfoEntityMap.getOrDefault(parentItem.getValue().getHierarchyId(), new ArrayList<>());
                        List<TreeItem<LedgerHierarchyInfoEntity>> items = ledgerHierarchyInfoEntities
                                .stream()
                                .map(TreeItem::new)
                                .peek(treeItem -> treeItem.expandedProperty().addListener(this.changeListener))
                                .collect(toList());
                        parentItem.getChildren().setAll(items);
                    });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.debug("expand end.");
        }
    }

    /**
     * ツリーの変更
     */
    private final ChangeListener<Boolean> changeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue observable, Boolean oldValue, Boolean newValue) {
            if (Objects.isNull(newValue) || newValue.equals(false)) {
                return;
            }

            TreeItem<LedgerHierarchyInfoEntity> treeItem = (TreeItem<LedgerHierarchyInfoEntity>) ((BooleanProperty) observable).getBean();
            blockUI(true);
            Task task = new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        buildChildren(treeItem.getChildren());
                        treeItem.expandedProperty().removeListener(changeListener);
                    } finally {
                        Platform.runLater(() -> blockUI(false));
                    }
                    return null;
                }
            };
            new Thread(task).start();
        }
    };


    /**
     * ツリールートを生成
     */
    private void createRoot() {

        try {
            blockUI(true);

            // ルートが存在しない場合は新規作成する。
            if (Objects.isNull(this.rootItem)) {
                this.rootItem = new TreeItem<>();
            }

            // ルート要素を作成
            this.rootItem.getChildren().clear();
            LedgerHierarchyInfoEntity root = new LedgerHierarchyInfoEntity(0L, LocaleUtils.getString("key.Ledger"));
            this.rootItem.setValue(root);
            this.buildChildren(Collections.singletonList(this.rootItem));
            this.rootItem.expandedProperty().addListener(this.changeListener);

            Platform.runLater(() -> {
                this.hierarchyTree.rootProperty().setValue(rootItem);
                this.hierarchyTree.setCellFactory((TreeView<LedgerHierarchyInfoEntity> o) -> new LedgerHierarchyTreeCell());
                this.hierarchyTree.getSelectionModel().select(rootItem);
                this.rootItem.setExpanded(true);
            });

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
            logger.debug("createRoot end.");
        }
    }

    /**
     * リストの追加
     */
    public void onListCreate() {
        LedgerHierarchyInfoEntity item = this.hierarchyTree.getSelectionModel().getSelectedItem().getValue();
        if (Objects.isNull(item)) {
            return;
        }

        try {
            blockUI(true);
            LedgerInfoEntity newLedgerInfoEntity = new LedgerInfoEntity(item.getHierarchyId(), loginUser.getId());
            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.createNew"), "LedgerRegisterDialog", newLedgerInfoEntity);
            if (!ButtonType.OK.equals(ret)) {
                return;
            }

            ResponseEntity res = ledgerInfoFacade.register(newLedgerInfoEntity);
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                final LedgerInfoEntity ledgerInfoEntity = ledgerInfoFacade.findURI(res.getUri());
                item.getLedgerInfoEntities().add(ledgerInfoEntity);
                List<LedgerListTableDataEntity> listTableColumns
                        = item
                        .getLedgerInfoEntities()
                        .stream()
                        .map(LedgerListTableDataEntity::new)
                        .collect(toList());

                this.ledgerList.getItems().setAll(listTableColumns);
                this.ledgerList
                        .getItems()
                        .stream()
                        .filter(entity->Objects.equals(entity.getLedgerInfoEntity().getLedgerId(), ledgerInfoEntity.getLedgerId()))
                        .findFirst()
                        .ifPresent(entity -> this.ledgerList.getSelectionModel().select(entity));
            } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR,
                        String.format(LocaleUtils.getString("key.CreateOf")
                                , LocaleUtils.getString("key.Ledger")),
                        String.format(LocaleUtils.getString(LocaleUtils.getString("key.FailedOf")),
                                String.format(LocaleUtils.getString("key.CreateOf"),
                                        LocaleUtils.getString("key.Ledger"))));
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }


    /**
     * 帳票出力
     */
    public void onReportOut() {

        LedgerListTableDataEntity ledgerListTableDataEntity = this.ledgerList.getSelectionModel().getSelectedItem();
        if (Objects.isNull(ledgerListTableDataEntity)) {
            return;
        }

        ButtonType ret = sc.showDialog(LocaleUtils.getString("key.OutLedgerTitle"), "InspectionHistorySelectionDialog", ledgerListTableDataEntity.getLedgerInfoEntity(), sc.getStage(), true);
        if (!ButtonType.OK.equals(ret)) {
            return;
        }

        // 更新
        final LedgerInfoEntity newItem = ledgerInfoFacade.find(ledgerListTableDataEntity.getLedgerInfoEntity().getLedgerId());
        ledgerListTableDataEntity.getLedgerInfoEntity().apply(newItem);
        this.refreshLedgerFileList();
    }

    /**
     * リストの階層の移動
     */
    public void onListMove() {
        LedgerListTableDataEntity item = this.ledgerList.getSelectionModel().getSelectedItem();
        if (Objects.isNull(item)) {
            return;
        }

        TreeItem<LedgerHierarchyInfoEntity> selectedItem = this.hierarchyTree.getSelectionModel().getSelectedItem();
        if (Objects.isNull(selectedItem)) {
            return;
        }

        try {
            sc.blockUI(true);
            this.hierarchyTree.setVisible(false);
            this.ledgerList.setVisible(false);

            LedgerInfoEntity ledgerInfoEntity = (LedgerInfoEntity) item.getLedgerInfoEntity().clone();
            TreeDialogEntity treeDialogEntity = new TreeDialogEntity(this.hierarchyTree.getRoot(), LocaleUtils.getString("key.HierarchyName"), selectedItem);
            treeDialogEntity.setIsUseHierarchy(Boolean.TRUE);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Move"), "LedgerHierarchyTreeCompo", treeDialogEntity);

            TreeItem<LedgerHierarchyInfoEntity> dest = (TreeItem<LedgerHierarchyInfoEntity>) treeDialogEntity.getTreeSelectedItem();
            if (!ret.equals(ButtonType.OK) || Objects.isNull(dest)) {
                return;
            }

            ledgerInfoEntity.setParentHierarchyId(dest.getValue().getHierarchyId());
            ResponseEntity res = ledgerInfoFacade.update(ledgerInfoEntity);
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                LedgerInfoEntity value = ledgerInfoFacade.find(ledgerInfoEntity.getLedgerId());
                selectedItem.getValue().getLedgerInfoEntities().removeIf(entity -> Objects.equals(entity.getLedgerId(), value.getLedgerId()));
                if (Objects.nonNull(dest.getValue().getLedgerInfoEntities())) {
                    dest.getValue().getLedgerInfoEntities().add(value);
                }
                this.hierarchyTree.getSelectionModel().select(dest);
            } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR,
                        String.format(LocaleUtils.getString("key.MoveOf")
                                , LocaleUtils.getString("key.Ledger")),
                        String.format(LocaleUtils.getString(LocaleUtils.getString("key.FailedOf")),
                                String.format(LocaleUtils.getString("key.MoveOf"),
                                        LocaleUtils.getString("key.Ledger"))));
            } else if (Objects.nonNull(res) && Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                // 排他バージョンが異なる。
                sc.showAlert(Alert.AlertType.ERROR,
                        String.format(LocaleUtils.getString("key.MoveOf"),
                                LocaleUtils.getString("key.Ledger")),
                        LocaleUtils.getString("key.alert.differentVerInfo"));

                List<LedgerInfoEntity> ledgerInfoEntities = this.ledgerInfoFacade.findChildren(selectedItem.getValue().getHierarchyId());
                selectedItem.getValue().setLedgerInfoEntities(ledgerInfoEntities);

                List<LedgerListTableDataEntity> listTableColumns
                        = ledgerInfoEntities
                        .stream()
                        .map(LedgerListTableDataEntity::new)
                        .collect(toList());
                this.ledgerList.setItems(FXCollections.observableArrayList(listTableColumns));
            }
        } finally {
            this.ledgerList.setVisible(true);
            this.hierarchyTree.setVisible(true);
            sc.blockUI(false);
        }
    }

    /**
     * 帳票の編集
     */
    public void onListEdit() {
        LedgerListTableDataEntity item = this.ledgerList.getSelectionModel().getSelectedItem();
        if (Objects.isNull(item)) {
            return;
        }

        try {
            blockUI(true);
            LedgerInfoEntity ledgerInfoEntity = (LedgerInfoEntity) item.getLedgerInfoEntity().clone();
            ButtonType ret = sc.showDialog(LocaleUtils.getString("key.ChangeSetting"), "LedgerRegisterDialog", ledgerInfoEntity);
            if (!ButtonType.OK.equals(ret)) {
                return;
            }

            ledgerInfoEntity.setUpdatePersonId(loginUser.getId());
            ResponseEntity res = ledgerInfoFacade.update(ledgerInfoEntity);
            if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
                final LedgerInfoEntity newItem = ledgerInfoFacade.find(ledgerInfoEntity.getLedgerId());
                item.apply(newItem);
                refreshLedgerFileTable();
            } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                sc.showAlert(Alert.AlertType.ERROR,
                        String.format(LocaleUtils.getString("key.EditOf"),
                                LocaleUtils.getString("key.Ledger")),
                        String.format(LocaleUtils.getString(LocaleUtils.getString("key.FailedOf")),
                                String.format(LocaleUtils.getString("key.EditOf"),
                                        LocaleUtils.getString("key.Ledger"))));
            }else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                // 排他バージョンが異なる。
                sc.showAlert(Alert.AlertType.ERROR,
                        String.format(LocaleUtils.getString("key.EditOf"),
                                LocaleUtils.getString("key.Ledger")),
                        LocaleUtils.getString("key.alert.differentVerInfo"));
                LedgerHierarchyInfoEntity ledgerHierarchyInfoEntity = this.hierarchyTree.getSelectionModel().getSelectedItem().getValue();
                if (Objects.isNull(ledgerHierarchyInfoEntity)) {
                    this.ledgerList.getItems().clear();
                    return;
                }
                List<LedgerInfoEntity> ledgerInfoEntities = ledgerInfoFacade.findChildren(ledgerHierarchyInfoEntity.getHierarchyId());
                ledgerHierarchyInfoEntity.setLedgerInfoEntities(ledgerInfoEntities);

                List<LedgerListTableDataEntity> listTableColumns
                        = ledgerInfoEntities
                        .stream()
                        .map(LedgerListTableDataEntity::new)
                        .collect(toList());
                ledgerList.getItems().setAll(listTableColumns);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 帳票の削除
     */
    public void onListDelete() {
        LedgerListTableDataEntity item = this.ledgerList.getSelectionModel().getSelectedItem();
        if (Objects.isNull(item)) {
            return;
        }

        ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"), item.getLedgerInfoEntity().getLedgerName());
        if (!ret.equals(ButtonType.OK)) {
            return;
        }

        ResponseEntity res = ledgerInfoFacade.remove(item.getLedgerInfoEntity().getLedgerId());
        if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {
            this.hierarchyTree
                    .getSelectionModel()
                    .getSelectedItem()
                    .getValue()
                    .getLedgerInfoEntities().removeIf(entity -> Objects.equals(entity.getLedgerId(), item.getLedgerInfoEntity().getLedgerId()));
            this.ledgerList.getItems().remove(item);
        }else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
            sc.showAlert(Alert.AlertType.ERROR,
                    String.format(LocaleUtils.getString("key.DeleteOf"),
                            LocaleUtils.getString("key.Ledger")),
                    String.format(LocaleUtils.getString(LocaleUtils.getString("key.FailedOf")),
                            String.format(LocaleUtils.getString("key.DeleteOf"),
                                    LocaleUtils.getString("key.Ledger"))));
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

    private void blockLedgerFileUI(Boolean flg) {
        this.ledgerFileList.setDisable(flg);
        Progress2.setVisible(flg);
    }

    /**
     * コンポーネントが破棄される前に呼び出される。破棄できない場合は、falseを返す。 ほかの画面に遷移するとき変更が存在するなら保存するか確認する
     *
     * @return true
     */
    @Override
    public boolean destoryComponent() {
        SplitPaneUtils.saveDividerPosition(ledgerManagerPane, getClass().getSimpleName());
        return true;
    }

    /**
     * ダウンロード＆起動
     */
    public void downloadAndOpen() {
        try {
            blockUI(true);
            LedgerFileListTableDataEntity selectedItem = ledgerFileList.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedItem)) {
                return;
            }

            LedgerListTableDataEntity ledgerListTableDataEntity = ledgerList.getSelectionModel().getSelectedItem();
            if (Objects.isNull(ledgerListTableDataEntity)) {
                return;
            }

            LedgerFileInfoEntity ledgerFileInfoEntity = selectedItem.getLedgerFileInfoEntity();
            File file = ledgerFileInfoFacade.downloadFileData(ledgerFileInfoEntity.getLedgerFileId());
            if (Objects.nonNull(file)) {
                LedgerInfoEntity ledgerInfoEntity = ledgerListTableDataEntity.getLedgerInfoEntity();
                final String baseFileName = ledgerFileInfoEntity.getFilePath();
                final String extension = baseFileName.substring(baseFileName.lastIndexOf("."));

                File toFile = new File(file.toPath() + extension);
                Files.move(file.toPath(), toFile.toPath());

                // assoc .xlsx
                // ftype Excel.Sheet.8
                String[] command = { "cmd", "/c", "start", toFile.getPath() };
                Runtime runtime = Runtime.getRuntime();
                runtime.exec(command);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            sc.showAlert(Alert.AlertType.ERROR,
                    String.format(LocaleUtils.getString("key.DownloadOf")
                            , LocaleUtils.getString("key.LedgerFile")),
                    String.format(LocaleUtils.getString(LocaleUtils.getString("key.FailedOf")),
                            String.format(LocaleUtils.getString("key.DownloadOf"),
                                    LocaleUtils.getString("key.LedgerFile"))));
        } finally {
            blockUI(false);
        }
    }

    /**
     * ファイル名に接尾辞を挿入する
     * @param filename 対象ファイル名
     * @param suffix 接尾辞
     * @return インデックスを付与したファイル名
     */
    private String addSuffix(String filename, String suffix)
    {
        int index = filename.lastIndexOf(".");
        return filename.substring(0, index) + suffix + filename.substring(index);
    }

    /**
     * ダウンロード
     */
    public void onDownload() {
        try {
            blockUI(true);
            List<LedgerFileListTableDataEntity> selectedItems = ledgerFileList.getSelectionModel().getSelectedItems();
            if (Objects.isNull(selectedItems) || selectedItems.isEmpty()) {
                return;
            }

            LedgerListTableDataEntity ledgerListTableDataEntity = ledgerList.getSelectionModel().getSelectedItem();
            if (Objects.isNull(ledgerListTableDataEntity)) {
                return;
            }


            File path = null;
            if (Objects.nonNull(properties)) {
                File fol = new File(properties.getProperty(SELECT_LEDGER_DOWNLOAD_PATH, this.defaultPath));
                if (fol.exists() && fol.isDirectory()) {
                    path = fol;
                }
            }

            if (Objects.isNull(path)){
                path = Paths.get(System.getProperty("user.home"), "Desktop").toFile();
            }

            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setInitialDirectory(path);
            File selectedFile = dirChooser.showDialog(sc.getStage().getScene().getWindow());
            if (Objects.isNull(selectedFile)) {
                return;
            }

            if(Objects.nonNull(properties)) {
                properties.setProperty(SELECT_LEDGER_DOWNLOAD_PATH, selectedFile.getPath());
                AdProperty.store(PROPERTY_TAG);
            }

            for (LedgerFileListTableDataEntity entity : selectedItems) {
                LedgerFileInfoEntity ledgerFileInfoEntity = entity.getLedgerFileInfoEntity();
                File file = ledgerFileInfoFacade.downloadFileData(ledgerFileInfoEntity.getLedgerFileId());
                if (Objects.nonNull(file)) {
                    LedgerInfoEntity ledgerInfoEntity = ledgerListTableDataEntity.getLedgerInfoEntity();
                    final String baseFileName = ledgerFileInfoEntity.getFilePath();
                    final String extension = baseFileName.substring(baseFileName.lastIndexOf("."));
                    String newFileName = new ArrayList<String>() {{
                        if (StringUtils.nonEmpty(ledgerFileInfoEntity.getKeyword())) {
                            JsonUtils.jsonToObjects(ledgerFileInfoEntity.getKeyword(), NameValueEntity[].class)
                                    .stream()
                                    .map(NameValueEntity::getValue)
                                    .forEach(this::add);
                        }
                        add(ledgerInfoEntity.getLedgerName());
                    }}.stream().filter(StringUtils::nonEmpty).collect(Collectors.joining("_")) + extension;

                    File toFile = new File(selectedFile.getPath() + "/" + newFileName);
                    long index = 0;
                    while(toFile.exists()) {
                        toFile = new File(selectedFile.getPath() + "/" + addSuffix(newFileName, "(" + index + ")"));
                        ++index;
                    }
                    Files.move(file.toPath(), toFile.toPath());
                } else {
                    // エラー処理
                    sc.showAlert(Alert.AlertType.ERROR,
                            String.format(LocaleUtils.getString("key.DownloadOf")
                                    , LocaleUtils.getString("key.LedgerFile")),
                            String.format(LocaleUtils.getString(LocaleUtils.getString("key.FailedOf")),
                                    String.format(LocaleUtils.getString("key.DownloadOf"),
                                            LocaleUtils.getString("key.LedgerFile"))));
                    return;
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            sc.showAlert(Alert.AlertType.ERROR,
                    String.format(LocaleUtils.getString("key.DownloadOf")
                            , LocaleUtils.getString("key.LedgerFile")),
                    String.format(LocaleUtils.getString(LocaleUtils.getString("key.FailedOf")),
                            String.format(LocaleUtils.getString("key.DownloadOf"),
                                    LocaleUtils.getString("key.LedgerFile"))));
        } finally {
            blockUI(false);
        }
    }

    public void onDeleteFile() {
        try {
            blockUI(true);
            List<LedgerFileListTableDataEntity> ledgerFileListTableDataEntities = this.ledgerFileList.getSelectionModel().getSelectedItems();
            if (Objects.isNull(ledgerFileListTableDataEntities)) {
                return;
            }

            ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Delete"), LocaleUtils.getString("key.DeleteSingleMessage"));
            if (ret.equals(ButtonType.OK)) {
                for (LedgerFileListTableDataEntity entity : ledgerFileListTableDataEntities) {
                    ResponseEntity res = this.ledgerFileInfoFacade.remove(entity.getLedgerFileInfoEntity().getLedgerFileId());
                    if (Objects.nonNull(res) && ResponseAnalyzer.getAnalyzeResult(res)) {

                    } else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.SERVER_FETAL)) {
                        sc.showAlert(Alert.AlertType.ERROR,
                                String.format(LocaleUtils.getString("key.DeleteOf"),
                                        LocaleUtils.getString("key.LedgerFile")),
                                String.format(LocaleUtils.getString(LocaleUtils.getString("key.FailedOf")),
                                        String.format(LocaleUtils.getString("key.DeleteOf"),
                                                LocaleUtils.getString("key.LedgerFile"))));
                        return;
                    }else if (Objects.equals(res.getErrorType(), ServerErrorTypeEnum.DIFFERENT_VER_INFO)) {
                        // 排他バージョンが異なる。
                        sc.showAlert(Alert.AlertType.ERROR,
                                String.format(LocaleUtils.getString("key.DeleteOf"),
                                        LocaleUtils.getString("key.LedgerFile")),
                                LocaleUtils.getString("key.alert.differentVerInfo"));
                        LedgerHierarchyInfoEntity ledgerHierarchyInfoEntity = this.hierarchyTree.getSelectionModel().getSelectedItem().getValue();
                        if (Objects.isNull(ledgerHierarchyInfoEntity)) {
                            this.ledgerList.getItems().clear();
                            return;
                        }
                        List<LedgerInfoEntity> ledgerInfoEntities = ledgerInfoFacade.findChildren(ledgerHierarchyInfoEntity.getHierarchyId());
                        ledgerHierarchyInfoEntity.setLedgerInfoEntities(ledgerInfoEntities);

                        List<LedgerListTableDataEntity> listTableColumns
                                = ledgerInfoEntities
                                .stream()
                                .map(LedgerListTableDataEntity::new)
                                .collect(toList());
                        ledgerList.getItems().setAll(listTableColumns);
                        return;
                    }
                }
            }
        } finally {
            this.refreshLedgerFileList();
            blockUI(false);
        }
    }
}
