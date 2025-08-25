/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.component;

import adtekfuji.admanagerapp.warehouseplugin.common.Constants;
import adtekfuji.admanagerapp.warehouseplugin.common.MaterialCsvFactory;
import adtekfuji.admanagerapp.warehouseplugin.controls.MaterialIntegerCell;
import adtekfuji.admanagerapp.warehouseplugin.csv.jcm.InventoryCsvFactory;
import adtekfuji.admanagerapp.warehouseplugin.dialog.AcceptanceDialog;
import adtekfuji.admanagerapp.warehouseplugin.dialog.InventoryConfirmArgument;
import adtekfuji.admanagerapp.warehouseplugin.dialog.InventoryDialogArgument;
import adtekfuji.admanagerapp.warehouseplugin.enumerate.InventoryDialogType;
import adtekfuji.clientservice.WarehouseInfoFaced;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.search.MaterialCondition;
import jp.adtekfuji.adFactory.entity.warehouse.LogStockInfo;
import jp.adtekfuji.adFactory.entity.warehouse.MstLocationInfo;
import jp.adtekfuji.adFactory.entity.warehouse.MstProductInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;
import jp.adtekfuji.adFactory.enumerate.MaterialGroupEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WarehouseEvent;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTreeTableView;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 在庫モニター画面 コントローラー
 * 
 * @author s-heya
 */
@FxComponent(id = "StockView", fxmlPath = "/fxml/warehouseplugin/StockView.fxml")
public class StockViewController implements Initializable, ComponentHandler {
   
    private final Logger logger = LogManager.getLogger();
    private final WarehouseInfoFaced facade = new WarehouseInfoFaced();
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();

    private final TreeItem<TrnMaterialInfo> rootItem = new TreeItem<>(TrnMaterialInfo.create(new MstProductInfo()));
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
            
    private final List<TrnMaterialInfo> materials = new ArrayList();

    private List<String> areaNameList;

    // 表示中の在庫照会の検索条件
    private MaterialCondition materialCondition = null;
    private MaterialGroupEnum searchGrouping;

    private int compTimeout = Integer.valueOf(Constants.INVENTORY_COMP_TIMEOUT_DEF);

    @FXML
    private CheckBox productNoCheck;
    @FXML
    private CheckBox supplyNoCheck;
    @FXML
    private CheckBox orderNoCheck;
    @FXML
    private CheckBox materialNoCheck;
    @FXML
    private CheckBox areaNameCheck;
    @FXML
    private CheckBox locationNoCheck;
    @FXML
    private CheckBox stockDateCheck;

    @FXML
    private TextField productNoField;
    @FXML
    private TextField supplyNoField;
    @FXML
    private TextField orderNoField;
    @FXML
    private TextField materialNoField;
    @FXML
    private ComboBox areaNameField;
    @FXML
    private TextField locationNoField;
    @FXML
    private DatePicker stockDateField;

    @FXML
    private CheckBox outStockCheck;

    @FXML
    private TextField unitNoField;
    @FXML
    private CheckBox unitNoCheck;

    @FXML
    private ComboBox groupingField;

    @FXML
    private CheckBox inventoryCheck; // 棚卸
    @FXML
    private HBox inventoryHBox; // 棚卸フィルター条件
    @FXML
    private CheckBox inventoryUnregisteredCheck; // (棚卸)未実施
    @FXML
    private CheckBox inventoryDifferentCheck; // (棚卸)在庫過不足あり
    @FXML
    private CheckBox inventoryNoDifferentCheck; // (棚卸)在庫過不足なし
    @FXML
    private CheckBox unarrivedOnlyCheck; // 未納入品のみ
    
    @FXML
    private SplitPane splitPane;
    @FXML
    private Pane progressPane;
    @FXML
    private Button searchButton;
    @FXML
    private Button acceptanceButton;
    @FXML
    private Button printButton;
    @FXML
    private Button outputDeliveryListButton;
    @FXML
    private Button outputCsvButton; // CSV出力ボタン
    @FXML
    private Button inventoryStartButton; // 棚卸開始ボタン
    @FXML
    private Button inventoryCancelButton; // 棚卸中止ボタン
    @FXML
    private Button inventoryCompleteButton; // 棚卸完了ボタン

    @FXML
    private PropertySaveTreeTableView<TrnMaterialInfo> stockTable;
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> supplyNoColumn; // 発注番号
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> productNoColumn; // 品目
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> productNameColumn; // 品名
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> specColumn; // 型式・仕様
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> orderNoColumn; // 製造オーダー番号
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> unitNoColumn; // ユニット番号
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> materialNoColumn; // 資材番号
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> serialNoColumn; // 製造番号
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> areaNameColumn; // 区画名
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> locationNoColumn; // 棚番号
    @FXML
    private TreeTableColumn<TrnMaterialInfo, Number> inStockNumColumn; // 在庫数
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> unitColumn; // 単位
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> arrivalNumColumn; // 納入予定数
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> arrivalDateColumn; // 納入予定日
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> stockDateColumn; // 最終入庫日
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> releasedOrderColumn; // 納入予定数
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> inventoryFlagColumn; // 棚卸状態
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> inventoryDiffColumn; // 在庫過不足
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> inventoryNumColumn; // 棚卸在庫数
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> inventoryLocationNoColumn; // 棚番訂正
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> inventoryDateColumn; // 棚卸実施日
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> lotNoColumn; // ロット番号
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> defectNumColumn; // 不良数
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> noteColumn; // 備考

    @FXML
    private PropertySaveTableView<LogStockInfo> logTable;
    @FXML
    private TableColumn<LogStockInfo, String> logEventKindColumn; // 種別
    @FXML
    private TableColumn<LogStockInfo, String> logMaterialNoColumn; // 資材番号
    @FXML
    private TableColumn<LogStockInfo, String> logOrderNoColumn; // 製造番号
    @FXML
    private TableColumn<LogStockInfo, String> logEventNumColumn; // 数量
    @FXML
    private TableColumn<LogStockInfo, String> logInStockNumColumn; // 在庫数
    @FXML
    private TableColumn<LogStockInfo, String> logAreaNameColumn; // 区画名
    @FXML
    private TableColumn<LogStockInfo, String> logLocationNoColumn; // 棚番号
    @FXML
    private TableColumn<LogStockInfo, String> logEventDateColumn; // 日時
    @FXML
    private TableColumn<LogStockInfo, String> logPersonNoColumn; // 担当者
    
    /**
     * 区画名(ComboBox)にフォーカスが当たったときに、区画名一覧を取得する
     */
    private final ChangeListener<Boolean> focusedListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if (newValue) {
            this.areaNameList = this.facade.findAllAreaName();
            for (String areaName : this.areaNameList) {
                if (!this.areaNameField.getItems().contains(areaName)) {
                    this.areaNameField.getItems().add(areaName);
                }
            }
            this.areaNameField.focusedProperty().removeListener(StockViewController.this.focusedListener);
        }
    };

    /**
     * 在庫照会画面を初期化する。
     * 
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {

            this.stockTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
            this.logTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

            String prefix = StockViewController.class.getName() + ".";

            if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
                this.stockTable.setEditable(false);
                this.inventoryStartButton.setDisable(true); // 棚卸開始ボタン
                this.inventoryCancelButton.setDisable(true); // 棚卸中止ボタン
                this.inventoryCompleteButton.setDisable(true); // 棚卸完了ボタン
            }

            // 初期値を設定する
            AdProperty.load(Constants.UI_PROPERTY_NAME, Constants.UI_PROPERTY_NAME + ".properties");
            Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);
            this.productNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "productNoCheck", String.valueOf(Boolean.FALSE))));
            this.supplyNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "supplyNoCheck", String.valueOf(Boolean.FALSE))));
            this.orderNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "orderNoCheck", String.valueOf(Boolean.FALSE))));
            this.materialNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "materialNoCheck", String.valueOf(Boolean.FALSE))));
            this.areaNameCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "areaNameCheck", String.valueOf(Boolean.FALSE))));
            this.locationNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "locationNoCheck", String.valueOf(Boolean.FALSE))));
            this.stockDateCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "stockDateCheck", String.valueOf(Boolean.FALSE))));
            this.unitNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "unitNoCheck", String.valueOf(Boolean.FALSE))));

            this.productNoField.setText(properties.getProperty(prefix + "productNoField"));
            this.supplyNoField.setText(properties.getProperty(prefix + "supplyNoField"));
            this.orderNoField.setText(properties.getProperty(prefix + "orderNoField"));
            this.materialNoField.setText(properties.getProperty(prefix + "materialNoField"));
            this.unitNoField.setText(properties.getProperty(prefix + "unitNoField"));

            Callback<ListView<MaterialGroupEnum>, ListCell<MaterialGroupEnum>> cellFactory 
                    = (ListView<MaterialGroupEnum> param) -> new ListCell<MaterialGroupEnum>() {
                @Override
                protected void updateItem(MaterialGroupEnum item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        setText(LocaleUtils.getString(item.getResourceKey()));
                    }
                }
            };
            
            ObservableList<MaterialGroupEnum> list = FXCollections.observableArrayList(MaterialGroupEnum.values());
            this.groupingField.getItems().addAll(list);
            this.groupingField.setValue(MaterialGroupEnum.valueOf(properties.getProperty(prefix + "groupingField", MaterialGroupEnum.PRODUCT.name())));

            this.groupingField.setButtonCell(cellFactory.call(null));
            this.groupingField.setCellFactory(cellFactory);
        
            String areaName = properties.getProperty(prefix + "areaNameField");
            if (!StringUtils.isEmpty(areaName)) {
                this.areaNameField.getItems().add(areaName);
                this.areaNameField.setValue(areaName);
            }

            this.locationNoField.setText(properties.getProperty(prefix + "locationNoField"));

            String stockDate = properties.getProperty(prefix + "stockDateField");
            if (!StringUtils.isEmpty(stockDate)) {
                try {
                    this.stockDateField.setValue(LocalDate.parse(stockDate, DateTimeFormatter.ISO_DATE));
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }

            this.outStockCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "outStockCheck", String.valueOf(Boolean.FALSE))));

            // 棚卸
            this.inventoryCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "inventoryCheck", String.valueOf(Boolean.FALSE))));
            this.inventoryUnregisteredCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "inventoryUnregisteredCheck", String.valueOf(Boolean.FALSE))));
            this.inventoryDifferentCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "inventoryDifferentCheck", String.valueOf(Boolean.FALSE))));
            this.inventoryNoDifferentCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "inventoryNoDifferentCheck", String.valueOf(Boolean.FALSE))));

            // 未納入品のみ
            this.unarrivedOnlyCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "unarrivedOnlyCheck", String.valueOf(Boolean.FALSE))));

            // コントロールを初期化する
            this.productNoCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                this.productNoField.setDisable(!newValue);
            });

            this.supplyNoCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                this.supplyNoField.setDisable(!newValue);
            });

            this.orderNoCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                this.orderNoField.setDisable(!newValue);
            });

            this.materialNoCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                this.materialNoField.setDisable(!newValue);
            });

            this.areaNameCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                this.areaNameField.setDisable(!newValue);
            });

            this.locationNoCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                this.locationNoField.setDisable(!newValue);
            });

            this.stockDateCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                this.stockDateField.setDisable(!newValue);
            });
            
            this.unitNoCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                this.unitNoField.setDisable(!newValue);
            });

            // 棚卸
            this.inventoryCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                // 棚卸のフィルタ条件
                this.inventoryHBox.setDisable(!newValue);
            });

            // 棚卸：未実施
            this.inventoryUnregisteredCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                this.dispMaterials(null);
            });

            // 棚卸：在庫過不足あり
            this.inventoryDifferentCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                this.dispMaterials(null);
            });

            // 棚卸：在庫過不足なし
            this.inventoryNoDifferentCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                this.dispMaterials(null);
            });
            
            // 検索条件テキストフィールドの有効・無効
            this.productNoField.setDisable(!this.productNoCheck.isSelected()); // 品目
            this.supplyNoField.setDisable(!this.supplyNoCheck.isSelected()); // 発注番号
            this.orderNoField.setDisable(!this.orderNoCheck.isSelected()); // 製造オーダー番号
            this.materialNoField.setDisable(!this.materialNoCheck.isSelected()); // 資材番号
            this.areaNameField.setDisable(!this.areaNameCheck.isSelected()); // 区画名
            this.locationNoField.setDisable(!this.locationNoCheck.isSelected()); // 棚番号
            this.stockDateField.setDisable(!this.stockDateCheck.isSelected()); // 最終入庫日
            this.unitNoField.setDisable(!this.unitNoCheck.isSelected()); // ユニット番号

            // 棚卸のフィルタ条件の有効・無効
            this.inventoryHBox.setDisable(!this.inventoryCheck.isSelected());

            this.areaNameField.focusedProperty().addListener(this.focusedListener);

            SplitPaneUtils.loadDividerPosition(this.splitPane, this.getClass().getSimpleName());

            // 在庫状況一覧を初期化
            this.stockTable.setRowFactory(value -> {
                TreeTableRow<TrnMaterialInfo> row = new TreeTableRow<TrnMaterialInfo>() {
                    @Override
                    protected void updateItem(TrnMaterialInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        if (Objects.isNull(item) || !getTreeItem().isLeaf()) {
                            setStyle(null);
                        } else if (isFocused() || isSelected()) {
                            setStyle("-fx-background-color: #0096c9; -fx-table-cell-border-color: #21a5d1; -fx-text-background-color: #ffffff;");
                        } else {
                            setStyle((item.getArrivalNum() > item.getStockNum())? "-fx-background-color: #deb887; -fx-table-cell-border-color: #deb887; -fx-text-background-color: #000000;" : "-fx-background-color: #f2f2f2; -fx-text-background-color: #000000;");
                        }
                    }
                };

                row.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (Objects.nonNull(row.getTreeItem()) && row.getTreeItem().isLeaf()) {
                        if (newValue) {
                            row.setStyle("-fx-background-color: #0096c9; -fx-table-cell-border-color: #21a5d1; -fx-text-background-color: #ffffff;");
                        } else {
                            row.setStyle(null);
                        }
                    }
                });
                
                return row;
            });
            
            this.stockTable.getSelectionModel().selectedIndexProperty().addListener((observable, newValue, oldValue) -> {
                this.stockTable.refresh();
            });

            Callback<TreeTableColumn<TrnMaterialInfo, String>, TreeTableCell<TrnMaterialInfo, String>> treeTableCellRightFactory =
                (final TreeTableColumn<TrnMaterialInfo, String> param) -> {
                    TreeTableCell<TrnMaterialInfo, String>cell = new TreeTableCell<TrnMaterialInfo, String>() {
                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                                return;
                            }
                            this.setText(item);
                        }
                    };
                    cell.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
                    cell.setAlignment(Pos.BASELINE_RIGHT);
                    cell.setPadding(new Insets(0, 6, 0, 0));
                    return cell;
            };

            Callback<TreeTableColumn<TrnMaterialInfo, Number>, TreeTableCell<TrnMaterialInfo, Number>> treeTableIntegerCellFactory =
                    (TreeTableColumn<TrnMaterialInfo, Number> p) -> new MaterialIntegerCell();

            this.stockTable.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener.Change<? extends TreeItem<TrnMaterialInfo>> change) -> {
                    updateLogTable();
                    List<TreeItem<TrnMaterialInfo>> datas = this.stockTable.getSelectionModel().getSelectedItems();
                    if (datas.size() > 0){
                        this.printButton.setDisable(false);
                    }else{
                        this.printButton.setDisable(true);
                    }
                }
            );

            Properties warehhouseProp = AdProperty.getProperties(Constants.WAREHOUSE_PROPERTY_NAME);

            // 棚卸完了のタイムアウト時間(ms)
            this.compTimeout = Integer.valueOf(warehhouseProp.getProperty(Constants.INVENTORY_COMP_TIMEOUT_KEY, Constants.INVENTORY_COMP_TIMEOUT_DEF));
            if (!warehhouseProp.containsKey(Constants.INVENTORY_COMP_TIMEOUT_KEY)) {
                warehhouseProp.setProperty(Constants.INVENTORY_COMP_TIMEOUT_KEY, Constants.INVENTORY_COMP_TIMEOUT_DEF);
                AdProperty.store(Constants.WAREHOUSE_PROPERTY_NAME);
            }

            //Boolean enableAccept = Boolean.valueOf(warehhouseProp.getProperty(Constants.ENABLE_ACCEPT_KEY, Constants.ENABLE_ACCEPT_DEF));
            //this.acceptanceButton.setManaged(enableAccept);
            //this.printButton.setManaged(enableAccept); // 標準は非表示

            //if (enableAccept) {
                this.stockTable.setOnMouseClicked((MouseEvent event) -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (event.getClickCount() == 2) {
                            // 受入ダイアログを表示する。
                            List<TreeItem<TrnMaterialInfo>> datas = this.stockTable.getSelectionModel().getSelectedItems();
                            if(Objects.nonNull(datas.get(0).getValue().getMaterialNo())){
                                ButtonType buttonType = sc.showDialog(LocaleUtils.getString("key.Accepted"), "AcceptanceDialog", datas, (Stage) ((Node) event.getSource()).getScene().getWindow());
                                if (ButtonType.OK.equals(buttonType)){
                                    onSearch(new ActionEvent());
                                }
                            }
                        }
                    }
                });
            //}
            
            // ルートツリーを非表示
            this.stockTable.init(StockViewController.class.getSimpleName() + ".stockTable");
            this.stockTable.setShowRoot(false);

            // 複数行の選択を有効
            this.stockTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            this.stockTable.getSelectionModel().setCellSelectionEnabled(false);

            // 発注番号
            this.supplyNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(o.getValue().getValue().getSupplyNo()));
            // 品目
            this.productNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(o.getValue().getValue().getProduct().getProductNo()));
            // 品名
            this.productNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(o.getValue().getValue().getProduct().getProductName()));
            // 型式
            this.specColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(o.getValue().getValue().getSepc()));
            // 製造番号
            this.orderNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(o.getValue().getValue().getOrderNo()));
            // ユニット番号
            this.unitNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(o.getValue().getValue().getUnitNo()));
            // シリアル番号
            this.serialNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(o.getValue().getValue().getSerialNo()));
            // 資材番号
            this.materialNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(o.getValue().getValue().getMaterialNo()));
            // 区画名
            this.areaNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getLocation()) ? o.getValue().getValue().getLocation().getAreaName() : null));
            // 棚番号
            this.locationNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getLocation()) ? o.getValue().getValue().getLocation().getLocationNo() : null));

            // 在庫数
            this.inStockNumColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, Number> o) -> o.getValue().getValue().inStockNumProperty());
            this.inStockNumColumn.setCellFactory(treeTableIntegerCellFactory);
            this.inStockNumColumn.setOnEditCommit((TreeTableColumn.CellEditEvent<TrnMaterialInfo, Number> event) -> {
                if (Objects.equals(event.getOldValue(), event.getNewValue())) {
                    return;
                }

                Integer value = Objects.isNull(event.getNewValue()) ? 0 : event.getNewValue().intValue();

                TrnMaterialInfo item = event.getRowValue().getValue();
                item.setInStockNum(value);

                // 在庫数を更新する。
                this.updateInStockNum(item.getMaterialNo(), value);
            });
            
            // 単位
            this.unitColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(o.getValue().getValue().isParent() ? "" : o.getValue().getValue().getProduct().getUnit()));

            // 納入予定数
            this.arrivalNumColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getArrivalNum()) ? String.valueOf(o.getValue().getValue().getArrivalNum()) : null));
            this.arrivalNumColumn.setCellFactory(treeTableCellRightFactory);

            // 発注残
            this.releasedOrderColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getArrivalNum()) ? String.valueOf(o.getValue().getValue().getArrivalNum() - o.getValue().getValue().getStockNum()) : null));
            this.releasedOrderColumn.setCellFactory(treeTableCellRightFactory);
            
            // 納入予定日
            this.arrivalDateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getArrivalPlan())? dateFormat.format(o.getValue().getValue().getArrivalPlan()) : null));
            // 最終入庫日
            this.stockDateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getStockDate()) ? dateFormat.format(o.getValue().getValue().getStockDate()) : null));

            // 棚卸状態
            this.inventoryFlagColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getInventoryFlag()) && o.getValue().getValue().getInventoryFlag() ? LocaleUtils.getString("key.Inventory.Working") : null));

            // 在庫過不足
            this.inventoryDiffColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(this.getInventoryDiff(o.getValue().getValue())));
            this.inventoryDiffColumn.setCellFactory(treeTableCellRightFactory);
            // 棚卸数
            this.inventoryNumColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getInventoryNum()) ? String.valueOf(o.getValue().getValue().getInventoryNum()) : null));
            this.inventoryNumColumn.setCellFactory(treeTableCellRightFactory);
            // 棚番訂正
            this.inventoryLocationNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getInventoryLocation()) ? o.getValue().getValue().getInventoryLocation().getLocationNo() : null));
            // 棚卸実施日
            this.inventoryDateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getInventoryDate()) ? dateFormat.format(o.getValue().getValue().getInventoryDate()) : null));

            // ロット番号
            this.lotNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o)
                    -> new ReadOnlyStringWrapper(o.getValue().getValue().getPartsNo()));
            // 不良数
            this.defectNumColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getDefectNum()) ? String.valueOf(o.getValue().getValue().getDefectNum()) : null));

            this.noteColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o)
                    -> new ReadOnlyStringWrapper(o.getValue().getValue().getNote()));
            
            Callback<TableColumn<LogStockInfo, String>, TableCell<LogStockInfo, String>> tableCellRightFactory =
                (final TableColumn<LogStockInfo, String> param) -> {
                    TableCell<LogStockInfo, String>cell = new TableCell<LogStockInfo, String>() {
                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                                return;
                            }
                            this.setText(item);
                        }
                    };
                    cell.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
                    cell.setAlignment(Pos.CENTER_RIGHT);
                    cell.setPadding(new Insets(0, 6, 0, 0));
                    return cell;
            };

            this.logTable.init(StockViewController.class.getSimpleName() + ".logTable");

            // 種別
            this.logEventKindColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                    -> new ReadOnlyStringWrapper(this.getEventKindName(o.getValue().getEventKind())));
            // 資材番号
            this.logMaterialNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                    -> new ReadOnlyStringWrapper(o.getValue().getMaterialNo()));
            // 製造番号
            this.logOrderNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                    -> new ReadOnlyStringWrapper(o.getValue().getOrderNo()));
            // 数量
            this.logEventNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getEventNum()) ? String.valueOf(o.getValue().getEventNum()) : null));
            this.logEventNumColumn.setCellFactory(tableCellRightFactory);
            // 在庫数
            this.logInStockNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getInStockNum()) ? String.valueOf(o.getValue().getInStockNum()) : null));
            this.logInStockNumColumn.setCellFactory(tableCellRightFactory);
            // 区画名
            this.logAreaNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                    -> new ReadOnlyStringWrapper(o.getValue().getAreaName()));
            // 棚番号
            this.logLocationNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                    -> new ReadOnlyStringWrapper(o.getValue().getLocationNo()));
            // 日時
            this.logEventDateColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                    -> new ReadOnlyStringWrapper(dateTimeFormat.format(o.getValue().getEventDate())));
            // 担当者
            this.logPersonNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<LogStockInfo, String> o)
                    -> new ReadOnlyStringWrapper(o.getValue().getPersonNo()));

            this.areaNameList = this.facade.findAllAreaName();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 種別に表示する文字列を取得する。
     *
     * @param eventKind イベント種別
     * @return 種別に表示する文字列
     */
    private String getEventKindName(Integer eventKind) {
        if (Objects.isNull(eventKind)) {
            return null;
        }
        WarehouseEvent event = WarehouseEvent.valueOf(eventKind);
        return Objects.nonNull(event) ? event.getMessage(rb) : "";
    }

    /**
     * 棚卸差異を取得する。
     *
     * @param material 資材情報
     * @return 棚卸差異
     */
    private String getInventoryDiff(TrnMaterialInfo material) {
        try {
            if (Objects.isNull(material.getInStockNum())
                    || Objects.isNull(material.getInventoryNum())) {
                return null;
            }

            Integer diff = material.getInventoryNum() - material.getInStockNum();
            if (diff > 0) {
                return new StringBuilder()
                        .append(diff)
                        .append(" ")
                        .append(LocaleUtils.getString("key.Inventory.Large")) // 超過
                        .toString();
            } else if (diff < 0) {
                return new StringBuilder()
                        .append(-diff)
                        .append(" ")
                        .append(LocaleUtils.getString("key.Inventory.Small")) // 不足
                        .toString();
            } else {
                return null;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * コンポーネントを破棄する。
     * 
     * @return true:遷移可、false:遷移不可
     */
    @Override
    public boolean destoryComponent() {
        try {
            String prefix = StockViewController.class.getName() + ".";

            Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);
            properties.setProperty(prefix + "productNoCheck", String.valueOf(this.productNoCheck.isSelected()));
            properties.setProperty(prefix + "supplyNoCheck", String.valueOf(this.supplyNoCheck.isSelected()));
            properties.setProperty(prefix + "orderNoCheck", String.valueOf(this.orderNoCheck.isSelected()));
            properties.setProperty(prefix + "materialNoCheck", String.valueOf(this.materialNoCheck.isSelected()));
            properties.setProperty(prefix + "areaNameCheck", String.valueOf(this.areaNameCheck.isSelected()));
            properties.setProperty(prefix + "locationNoCheck", String.valueOf(this.locationNoCheck.isSelected()));
            properties.setProperty(prefix + "stockDateCheck", String.valueOf(this.stockDateCheck.isSelected()));
            properties.setProperty(prefix + "unitNoCheck", String.valueOf(this.unitNoCheck.isSelected()));

            if (Objects.nonNull(productNoField.getText())) {
                properties.setProperty(prefix + "productNoField", productNoField.getText());
            }
            
            if (Objects.nonNull(supplyNoField.getText())) {
                properties.setProperty(prefix + "supplyNoField", supplyNoField.getText());
            }
            
            if (Objects.nonNull(orderNoField.getText())) {
                properties.setProperty(prefix + "orderNoField", orderNoField.getText());
            }
            
            if (Objects.nonNull(materialNoField.getText())) {
                properties.setProperty(prefix + "materialNoField", materialNoField.getText());
            }
            
            if (Objects.nonNull(areaNameField.getValue())) {
                properties.setProperty(prefix + "areaNameField", String.valueOf(areaNameField.getValue()));
            }
            
            if (Objects.nonNull(locationNoField.getText())) {
                properties.setProperty(prefix + "locationNoField", locationNoField.getText());
            }

            if (Objects.nonNull(stockDateField.getValue())) {
                properties.setProperty(prefix + "stockDateField", stockDateField.getValue().format(DateTimeFormatter.ISO_DATE));
            }
            
            if (Objects.nonNull(this.unitNoField.getText())) {
                properties.setProperty(prefix + "unitNoField", this.unitNoField.getText());
            }
            
            if (Objects.nonNull(this.groupingField.getValue())) {
                properties.setProperty(prefix + "groupingField", ((MaterialGroupEnum) this.groupingField.getValue()).name());
            }
                
            properties.setProperty(prefix + "outStockCheck", String.valueOf(this.outStockCheck.isSelected()));

            properties.setProperty(prefix + "inventoryCheck", String.valueOf(this.inventoryCheck.isSelected()));
            properties.setProperty(prefix + "inventoryUnregisteredCheck", String.valueOf(this.inventoryUnregisteredCheck.isSelected()));
            properties.setProperty(prefix + "inventoryDifferentCheck", String.valueOf(this.inventoryDifferentCheck.isSelected()));
            properties.setProperty(prefix + "inventoryNoDifferentCheck", String.valueOf(this.inventoryNoDifferentCheck.isSelected()));

            properties.setProperty(prefix + "unarrivedOnlyCheck", String.valueOf(this.unarrivedOnlyCheck.isSelected()));

            AdProperty.store();

            SplitPaneUtils.saveDividerPosition(this.splitPane, this.getClass().getSimpleName());

        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return true;
    }
     
    /**
     * 検索ボタン
     * 
     * @param event 
     */
    @FXML
    private void onSearch(ActionEvent event) {
        try {
            blockUI(true);

            stockTable.getSelectionModel().clearSelection();
            rootItem.getChildren().clear();
            stockTable.setRoot(rootItem);
            logTable.getItems().clear();

            final MaterialGroupEnum grouping = (MaterialGroupEnum) this.groupingField.getValue();
            final MaterialCondition condition = new MaterialCondition();

            // 品目
            if (this.productNoCheck.isSelected() && !StringUtils.isEmpty(this.productNoField.getText())) {
                condition.setProductNo(this.productNoField.getText());
            }

            // 発注番号
            if (this.supplyNoCheck.isSelected() && !StringUtils.isEmpty(this.supplyNoField.getText())) {
                condition.setSupplyNo(this.supplyNoField.getText());
            }

            // 製造オーダー番号
            if (this.orderNoCheck.isSelected()&& !StringUtils.isEmpty(this.orderNoField.getText())) {
                condition.setOrderNo(this.orderNoField.getText());
            }

            // 資材番号
            if (this.materialNoCheck.isSelected()&& !StringUtils.isEmpty(this.materialNoField.getText())) {
                condition.setMaterialNo(this.materialNoField.getText());
            }

            // 区画名
            if (this.areaNameCheck.isSelected() && Objects.nonNull(this.areaNameField.getValue())) {
                condition.setAreaName(String.valueOf(this.areaNameField.getValue()));
            }

            // 棚番号
            if (this.locationNoCheck.isSelected()&& !StringUtils.isEmpty(this.locationNoField.getText())) {
                condition.setLocationNo(this.locationNoField.getText());
            }

            // 最終入庫日
            if (this.stockDateCheck.isSelected()&& Objects.nonNull(this.stockDateField.getValue())) {
                condition.setStockDate(this.stockDateField.getValue());
            }

            // ユニット番号
            if (this.unitNoCheck.isSelected()&& Objects.nonNull(this.unitNoField.getText())) {
                condition.setUnitNo(this.unitNoField.getText());
            }

            // 在庫がない資材を含める
            condition.setOutStock(this.outStockCheck.isSelected());

            // 棚卸
            condition.setInventory(this.inventoryCheck.isSelected());
            condition.setInventoryUnregistered(true);
            condition.setInventoryDifferent(true);
            condition.setInventoryNoDifferent(true);

            condition.setUnarrivedOnly(this.unarrivedOnlyCheck.isSelected());

            this.materialCondition = condition;
            this.searchGrouping = grouping;
            this.search(condition, grouping);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     * 在庫一覧を検索する。
     *
     * @param condition 検索条件
     * @param grouping 分類
     */
    private void search(MaterialCondition condition, MaterialGroupEnum grouping) {
        try {
            Task task = new Task<List<TrnMaterialInfo>>() {
                @Override
                protected List<TrnMaterialInfo> call() throws Exception {
                    return searchMaterials(condition);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        materials.clear();
                        materials.addAll(this.getValue());

                        dispMaterials(grouping);

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
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };

            new Thread(task).start();           

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     * 資材情報を取得する。
     *
     * @param condition 検索条件
     * @return 資材情報一覧
     */
    private List<TrnMaterialInfo> searchMaterials(MaterialCondition condition) {
        List<TrnMaterialInfo> infos = new LinkedList();

        int range = Integer.parseInt(AdProperty.getProperties().getProperty(StockViewController.class.getName() + ".range", "500"));

        int count = this.facade.countMaterials(condition);
        for (int ii = 0; ii < count; ii += range) {
            List<TrnMaterialInfo> materialList = this.facade.searchMaterials(condition, ii, ii + range - 1);
            infos.addAll(materialList);
        }

        return infos;
    }

    /**
     * 在庫リストを表示する。
     *
     * @param grouping 分類
     */
    private void dispMaterials(MaterialGroupEnum grouping) {
        logger.info("dispMaterials: grouping={}", grouping);

        // 分類がnullの場合、検索条件から取得する。
        if (Objects.isNull(grouping)) {
            grouping = this.searchGrouping;
        }

        // 棚卸
        boolean isInventory = false;
        if (Objects.nonNull(this.materialCondition)) {
            isInventory = this.materialCondition.getInventory();
        }

        // 棚卸：未実施
        boolean isUnregistered = this.inventoryUnregisteredCheck.isSelected();
        // 棚卸：在庫過不足あり
        boolean isDifferent = this.inventoryDifferentCheck.isSelected();
        // 棚卸：在庫過不足なし
        boolean isNoDifferent = this.inventoryNoDifferentCheck.isSelected();

        // 在庫情報を棚卸の条件で絞り込む。
        List<TrnMaterialInfo> dispMaterials = new LinkedList();
        if (!isInventory || (isInventory && isUnregistered && isDifferent && isNoDifferent)) {
            // すべて
            dispMaterials = this.materials;
        } else {
            for (TrnMaterialInfo material : this.materials) {
                if ((isUnregistered && Objects.isNull(material.getInventoryNum()))
                        || (isDifferent && !Objects.isNull(material.getInventoryNum()) && !Objects.equals(material.getInventoryNum(), material.getInStockNum()))
                        || (isNoDifferent && !Objects.isNull(material.getInventoryNum()) && Objects.equals(material.getInventoryNum(), material.getInStockNum()))) {
                    dispMaterials.add(material);
                }
            }
        }

        List<TreeItem<TrnMaterialInfo>> elements;
        if (Objects.equals(grouping, MaterialGroupEnum.PRODUCT)) {
            elements = createElementsByProduct(dispMaterials);

            this.productNoColumn.setVisible(false);
            this.productNameColumn.setVisible(false);
        } else {
            elements = createElementsByUnit(dispMaterials);

            this.productNoColumn.setVisible(true);
            this.productNameColumn.setVisible(true);
        }

        this.stockTable.getSelectionModel().clearSelection();
        this.rootItem.getChildren().clear();
        this.rootItem.getChildren().addAll(elements);
        this.stockTable.setRoot(rootItem);
        this.logTable.getItems().clear();

        this.stockTable.requestLayout();

        if (dispMaterials.isEmpty()) {
            outputDeliveryListButton.setDisable(true);
            outputCsvButton.setDisable(true);
        } else {
            outputDeliveryListButton.setDisable(false);
            outputCsvButton.setDisable(false);
        }
    }

    /**
     * 分類が品目の場合の在庫リストの要素を作成する。
     *
     * @param dispMaterials 表示する資材情報
     * @return 在庫リストの要素
     */
    private List<TreeItem<TrnMaterialInfo>> createElementsByProduct(List<TrnMaterialInfo> dispMaterials) {
        List<TreeItem<TrnMaterialInfo>> elements = FXCollections.observableArrayList();

        int inStockNum = 0;

        MstProductInfo product = new MstProductInfo();
        TreeItem<TrnMaterialInfo> parentItem = new TreeItem(TrnMaterialInfo.create(product));
        List<TreeItem<TrnMaterialInfo>> subElements = new ArrayList<>();

        for (TrnMaterialInfo material : dispMaterials) {
            if (!StringUtils.equals(product.getProductNo(), material.getProduct().getProductNo())) {
                if (!subElements.isEmpty()) {
                    // 品目ツリーに追加
                    parentItem.getValue().setInStockNum(inStockNum);
                    parentItem.getChildren().addAll(subElements);

                    // ルートツリーに追加
                    elements.add(parentItem);

                    subElements.clear();
                    inStockNum = 0;
                }

                product = material.getProduct();
                parentItem = new TreeItem(TrnMaterialInfo.create(product));
                parentItem.setExpanded(true);
            }

            if (Objects.isNull(material.getLocation())) {
                material.setLocation(new MstLocationInfo("", ""));
            }

            inStockNum += material.getInStockNum();
            subElements.add(new TreeItem(material));
        }

        if (!subElements.isEmpty()) {
            // 品目ツリーに追加
            parentItem.getValue().setInStockNum(inStockNum);
            parentItem.getChildren().addAll(subElements);

            // ルートツリーに追加
            elements.add(parentItem);
        }

        return elements;
    }

    /**
     * 分類がユニット番号の場合の在庫リストの要素を作成する。
     *
     * @param dispMaterials 表示する資材情報
     * @return 在庫リストの要素
     */
    private List<TreeItem<TrnMaterialInfo>> createElementsByUnit(List<TrnMaterialInfo> dispMaterials) {
        List<TreeItem<TrnMaterialInfo>> elements = FXCollections.observableArrayList();

        int inStockNum = 0;

        TrnMaterialInfo parent = new TrnMaterialInfo();
        TreeItem<TrnMaterialInfo> parentItem = new TreeItem(TrnMaterialInfo.unitoNo(""));
        List<TreeItem<TrnMaterialInfo>> subElements = new ArrayList<>();

        for (TrnMaterialInfo material : dispMaterials) {
            material.setUnitNo(StringUtils.isEmpty(material.getUnitNo()) ? "None" : material.getUnitNo());

            if (!StringUtils.equals(parent.getUnitNo(), material.getUnitNo())) {
                if (!subElements.isEmpty()) {
                    // ツリーに追加
                    parentItem.getValue().setInStockNum(inStockNum);
                    parentItem.getChildren().addAll(subElements);

                    // ルートツリーに追加
                    elements.add(parentItem);

                    subElements.clear();
                    inStockNum = 0;
                }

                parent = material;
                parentItem = new TreeItem(TrnMaterialInfo.unitoNo(parent.getUnitNo()));
                parentItem.setExpanded(true);
            }

            if (Objects.isNull(parent.getLocation())) {
                parent.setLocation(new MstLocationInfo("", ""));
            }

            inStockNum += material.getInStockNum();
            subElements.add(new TreeItem(material));
        }

        if (!subElements.isEmpty()) {
            // 品目ツリーに追加
            parentItem.getValue().setInStockNum(inStockNum);
            parentItem.getChildren().addAll(subElements);

            // ルートツリーに追加
            elements.add(parentItem);
        }

        return elements;
    }

    /**
     * 前ページ
     * 
     * @param event 
     */
    @FXML
    private void onPrev(ActionEvent event) {
    }

    /**
     * 次ページ
     * 
     * @param event 
     */
    @FXML
    private void onNext(ActionEvent event) {
    }

    /**
     * ラベル印刷
     * 
     * @param event 
     */
    @FXML
    private void onPrint(ActionEvent event) {
        try{
            List<TreeItem<TrnMaterialInfo>> list = this.stockTable.getSelectionModel().getSelectedItems();

            ButtonType buttonType = sc.showDialog(LocaleUtils.getString("key.Printed"), "PrintDialog", 
                    list.stream().map(o -> o.getValue()).collect(Collectors.toList()),
                    (Stage) ((Node) event.getSource()).getScene().getWindow());

        }catch(Exception ex){
            logger.fatal(ex, ex);
        }
    }

    /**
     * 払出品リスト出力
     * 
     * @param event イベント
     */
    @FXML
    private void onDeliveryList(ActionEvent event) {
        try{
            ButtonType buttonType = sc.showDialog(LocaleUtils.getString("key.outputDeliveryList"), "OutputDeliveryListDialog", this.materials, (Stage) ((Node) event.getSource()).getScene().getWindow());

        }catch(Exception ex){
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 在庫リスト出力
     * 
     * @param event 
     */
    @FXML
    private void onStockCsv(ActionEvent event) {
        this.outputInventoryCsv();
    }
    /**
     * CSV出力
     *
     * @param event 
     */
    @FXML
    private void onOutputCsv(ActionEvent event) {
        this.outputCsv();
    }

    /**
     * 棚卸開始
     * 
     * @param event 
     */
    @FXML
    private void onInventoryStart(ActionEvent event) {
        this.inventoryStart(null, null);
    }

    /**
     * 棚卸中止
     * 
     * @param event 
     */
    @FXML
    private void onInventoryCancel(ActionEvent event) {
        this.inventoryCancel();
    }

    /**
     * 棚卸完了
     * 
     * @param event 
     */
    @FXML
    private void onInventoryComplete(ActionEvent event) {
        this.inventoryComplete();
    }

    /**
     * 受入ボタンが押下された。
     * 
     * @param event アクションイベント
     */
    @FXML
    private void onAccept(ActionEvent event) {
        logger.info("onAccept start.");

        try {
            List<TreeItem<TrnMaterialInfo>> items = this.stockTable.getSelectionModel().getSelectedItems();
            if (!items.isEmpty() && Objects.nonNull(items.get(0).getValue().getMaterialNo())){
                sc.showDialog(LocaleUtils.getString("key.Accepted"), "AcceptanceDialog", items, (Stage) ((Node) event.getSource()).getScene().getWindow());
            } else {
                sc.showDialog(LocaleUtils.getString("key.Accepted"), "AcceptanceDialog", null, (Stage) ((Node) event.getSource()).getScene().getWindow());
            }
            
            if (AcceptanceDialog.isRequestUpdate()) {
                // 一覧を更新
                this.onSearch(new ActionEvent());
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 履歴を更新する。
     */
    private void updateLogTable() {
        try {
            List<TreeItem<TrnMaterialInfo>> items = stockTable.getSelectionModel().getSelectedItems();
            if (items.isEmpty()) {
                logTable.getItems().clear();
                return;
            }
            
            List<String> materialNos = items.stream().filter(o -> !o.getValue().isParent()).map(o -> o.getValue().getMaterialNo()).collect(Collectors.toList());
            if (materialNos.isEmpty()) {
                logTable.getItems().clear();
                return;
            }

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        blockUI(true);
                        
                        List<LogStockInfo> logList = facade.findLogStock(materialNos);
                        Platform.runLater(() -> {
                            logTable.setItems(FXCollections.observableArrayList(logList));
                        });

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    blockUI(false);
                }

                @Override
                protected void failed() {
                    super.failed();
                    Platform.runLater(() -> {
                        logTable.getItems().clear();
                    });
                    blockUI(false);
                }
            };

            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 操作を無効にする。
     * 
     * @param block true:操作を無効にする、false:操作を有効にする
     */
    private void blockUI(boolean block) {
        Platform.runLater(() -> {
            SceneContiner.getInstance().blockUI("ContentNaviPane", block);
            progressPane.setVisible(block);
        });
    }

    /**
     * CSVファイルを出力する。
     */
    private void outputCsv() {
        logger.info("outputCsv start.");
        try {
            if (materials.isEmpty()) {
                return;
            }

            final MaterialCsvFactory csvFactory = new MaterialCsvFactory(materials);

            boolean isChoice = csvFactory.choiceFile();
            if (!isChoice) {
                return;
            }

            blockUI(true);

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    csvFactory.outputCsv();
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {

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
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };

            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     * 棚卸を開始する。
     *
     * @param targetAreaName 区画名
     * @param reset 棚卸結果を消去して開始する？
     */
    private void inventoryStart(String targetAreaName, Boolean reset) {
        try {
            blockUI(true);

            final String title = LocaleUtils.getString("key.Inventory.Start"); // 棚卸開始

            if (Objects.isNull(reset)) {
                // 棚卸開始ダイアログを表示する。
                InventoryDialogArgument dialogArgument = new InventoryDialogArgument(InventoryDialogType.START, this.areaNameList);
                ButtonType buttonType = sc.showDialog(title, "InventoryDialog", dialogArgument, sc.getStage());
                if (!Objects.equals(buttonType, ButtonType.OK)) {
                    blockUI(false);
                    return;
                }

                targetAreaName = dialogArgument.getSelectedAreaName();
            }

            final String areaName = targetAreaName;
            final Long authId = this.loginUser.getId();

            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
                    // 指定した区画の棚卸を開始する。
                    return facade.inventoryStart(areaName, reset, authId);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    boolean isEnd = true; 
                    try {
                        // 棚卸の開始結果を処理する。
                        boolean isStart = inventoryStartResultProcess(title, areaName, this.getValue());
                        if (isStart) {
                            // 検索条件で、区画を選択していないか、該当区画を選択している場合、在庫一覧を再取得する。
                            isEnd = afterInventoryProc(areaName);
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        if (isEnd) {
                            blockUI(false);
                        }
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        // 棚卸の開始でエラーが発生しました。
                        showInventoryException(title, areaName, LocaleUtils.getString("key.Inventory.StartErrorMessage"), this.getException());

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };

            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     * 棚卸の開始結果を処理する。
     *
     * @param title ダイアログのタイトル
     * @param areaName 区画名
     * @param result 棚卸の開始結果
     * @return 完了か？(true: 完了, false:継続)
     */
    private boolean inventoryStartResultProcess(String title, String areaName, ResponseEntity result) {
        AlertType alertType;
        String alertMessage;
        String alertDetails;

        if (result.isSuccess()) {
            // 棚卸を開始しました。
            alertType = AlertType.INFORMATION;
            alertMessage = LocaleUtils.getString("key.Inventory.StartMessage");
            alertDetails = null;

        } else if (Objects.equals(result.getErrorType(), ServerErrorTypeEnum.EXIST_INVENTORY_RESULT)) {
            // 棚卸結果が存在します。
            alertType = AlertType.CONFIRMATION;
            alertMessage = new StringBuilder(LocaleUtils.getString("key.Inventory.ExistInventoryResult")).append("\n")
                    .append(LocaleUtils.getString("key.Area")).append(": ").append(areaName)
                    .toString();
            alertDetails = LocaleUtils.getString("key.Inventory.DeleteInventoryResult");

            boolean reset;
            ButtonType buttonType = sc.showMessageBox(alertType, title, alertMessage, alertDetails,
                    new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            if (Objects.equals(buttonType, ButtonType.YES)) {
                // 消去して開始する。
                reset = true;
            } else if (Objects.equals(buttonType, ButtonType.NO)) {
                // 消去せずに開始する。
                reset = false;
            } else {
                // キャンセルする。
                return true;
            }

            // 棚卸結果を消去するかどうかを指定して、棚卸を開始する。
            inventoryStart(areaName, reset);
            return false;

        } else {
            String exMessage = null;
            if (Objects.nonNull(result.getErrorType())) {
                exMessage = result.getErrorType().name();
            }

            // 棚卸の開始でエラーが発生しました。
            alertType = AlertType.ERROR;
            alertMessage = LocaleUtils.getString("key.Inventory.StartErrorMessage");
            alertDetails = new StringBuilder()
                    .append(LocaleUtils.getString("key.ErrorDetail")).append(":\n")
                    .append(exMessage)
                    .toString();
        }

        showInventoryMessage(alertType, title, areaName, alertMessage, alertDetails);
        return true;
    }

    /**
     * 棚卸を中止する。
     */
    private void inventoryCancel() {
        try {
            blockUI(true);

            final String title = LocaleUtils.getString("key.Inventory.Cancel"); // 棚卸中止

            // 棚卸中止ダイアログを表示する。
            InventoryDialogArgument dialogArgument = new InventoryDialogArgument(InventoryDialogType.CANCEL, this.areaNameList);
            ButtonType buttonType = sc.showDialog(title, "InventoryDialog", dialogArgument, sc.getStage());
            if (!Objects.equals(buttonType, ButtonType.OK)) {
                blockUI(false);
                return;
            }

            final String areaName = dialogArgument.getSelectedAreaName();
            final Long authId = this.loginUser.getId();

            // 本当に棚卸を中止しますか？
            String confirmMessage = new StringBuilder(LocaleUtils.getString("key.Inventory.Cancel.ConfirmMessage")).append("\n")
                    .append(LocaleUtils.getString("key.Area")).append(": ").append(areaName)
                    .toString();
            buttonType = sc.showMessageBox(AlertType.CONFIRMATION, title, confirmMessage, new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
            if (!Objects.equals(buttonType, ButtonType.YES)) {
                blockUI(false);
                return;
            }

            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
                    // 指定した区画の棚卸を中止する。
                    return facade.inventoryCancel(areaName, authId);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    boolean isEnd = true;
                    try {
                        ResponseEntity result = this.getValue();

                        AlertType alertType;
                        String alertMessage;
                        String alertDetails;

                        if (result.isSuccess()) {
                            // 棚卸を中止しました。
                            alertType = AlertType.INFORMATION;
                            alertMessage = LocaleUtils.getString("key.Inventory.CancelMessage");
                            alertDetails = null;
                        } else if (Objects.equals(result.getErrorType(), ServerErrorTypeEnum.NOT_START_INVENTORY)) {
                            // 棚卸を開始していません。
                            alertType = AlertType.WARNING;
                            alertMessage = LocaleUtils.getString("key.Inventory.NotStart");
                            alertDetails = null;
                        } else {
                            String exMessage = null;
                            if (Objects.nonNull(result.getErrorType())) {
                                exMessage = result.getErrorType().name();
                            }

                            // 棚卸の中止でエラーが発生しました。
                            alertType = AlertType.ERROR;
                            alertMessage = LocaleUtils.getString("key.Inventory.CancelErrorMessage");
                            alertDetails = new StringBuilder()
                                    .append(LocaleUtils.getString("key.ErrorDetail")).append(":\n")
                                    .append(exMessage)
                                    .toString();
                        }

                        showInventoryMessage(alertType, title, areaName, alertMessage, alertDetails);

                        // 検索条件で、区画を選択していないか、該当区画を選択している場合、在庫一覧を再取得する。
                        isEnd = afterInventoryProc(areaName);

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        if (isEnd) {
                            blockUI(false);
                        }
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        // 棚卸の中止でエラーが発生しました。
                        showInventoryException(title, areaName, LocaleUtils.getString("key.Inventory.Cancel.ErrorMessage"), this.getException());

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };

            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     * 棚卸を完了する。
     */
    private void inventoryComplete() {
        try {
            blockUI(true);

            final String title = LocaleUtils.getString("key.Inventory.Complete"); // 棚卸完了

            // 棚卸完了ダイアログを表示する。
            InventoryDialogArgument dialogArgument = new InventoryDialogArgument(InventoryDialogType.COMPLETE, this.areaNameList);
            ButtonType buttonType = sc.showDialog(title, "InventoryDialog", dialogArgument, sc.getStage());
            if (!Objects.equals(buttonType, ButtonType.OK)
                    && !Objects.equals(buttonType, ButtonType.CANCEL)) {
                blockUI(false);
                return;
            }

            final String areaName = dialogArgument.getSelectedAreaName();

            // 指定した区画の資材情報を取得する。
            final int searchRange = Integer.parseInt(AdProperty.getProperties().getProperty(StockViewController.class.getName() + ".range", "500"));

            Task task = new Task<List<TrnMaterialInfo>>() {
                @Override
                protected List<TrnMaterialInfo> call() throws Exception {
                    List<TrnMaterialInfo> infos = new LinkedList();

                    MaterialCondition condition = new MaterialCondition();
                    condition.setAreaName(areaName);
                    condition.setOutStock(true);
                    condition.setInventory(true);
                    condition.setInventoryUnregistered(true);
                    condition.setInventoryDifferent(true);
                    condition.setInventoryNoDifferent(true);

                    int count = facade.countMaterials(condition);
                    for (int i = 0; i < count; i += searchRange) {
                        List<TrnMaterialInfo> materialList = facade.searchMaterials(condition, i, i + searchRange - 1);
                        infos.addAll(materialList);
                    }

                    return infos;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();

                    boolean isUnblock = true;
                    try {
                        List<TrnMaterialInfo> infos = this.getValue();
                        if (infos.isEmpty()) {
                            // 棚卸を開始していません。
                            showInventoryMessage(AlertType.WARNING, title, areaName, LocaleUtils.getString("key.Inventory.NotStart"), null);
                            return;
                        }

                        // 確認が必要な資材を取得する。
                        // ・在庫があるが、棚卸未実施
                        // ・在庫過不足あり (在庫なしで棚卸未実施の資材は除く)
                        List<TrnMaterialInfo> confirmMaterials = infos.stream()
                                .filter(p -> (p.getInStockNum() > 0 && Objects.isNull(p.getInventoryNum()))
                                        || (!Objects.equals(p.getInStockNum(), p.getInventoryNum())
                                                && (p.getInStockNum() > 0 || Objects.nonNull(p.getInventoryNum()))))
                                .collect(Collectors.toList());

                        if (confirmMaterials.isEmpty()) {
                            // 在庫過不足の資材はありませんでした。本当に棚卸を完了しますか？
                            String confirmMessage = new StringBuilder(LocaleUtils.getString("key.Inventory.Complete.Message")).append("\n")
                                    .append(LocaleUtils.getString("key.Area")).append(": ").append(areaName).append("\n\n")
                                    .append(LocaleUtils.getString("key.Inventory.Complete.ConfirmMessage"))
                                    .toString();
                            ButtonType buttonType = sc.showMessageBox(AlertType.CONFIRMATION, title, confirmMessage, new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
                            if (!Objects.equals(buttonType, ButtonType.YES)) {
                                return;
                            }
                        } else {
                            // 棚卸確認ダイアログを表示する。
                            InventoryConfirmArgument confirmArgument = new InventoryConfirmArgument(areaName, confirmMaterials);
                            ButtonType buttonType = sc.showDialog(title, "InventoryConfirmDialog", confirmArgument, sc.getStage(), true);
                            if (!Objects.equals(buttonType, ButtonType.OK)) {
                                return;
                            }
                        }

                        // 棚卸完了
                        isUnblock = false;
                        inventoryCompleteProc(areaName, infos);

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        if (isUnblock) {
                            blockUI(false);
                        }
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        // 棚卸の完了でエラーが発生しました。
                        showInventoryException(title, areaName, LocaleUtils.getString("key.Inventory.CompleteErrorMessage"), this.getException());

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };

            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     * 棚卸完了の処理を行なう。
     *
     * @param areaName 区画名
     * @param infos 区画の資材情報一覧　※CSV出力で使用する。
     */
    private void inventoryCompleteProc(String areaName, List<TrnMaterialInfo> infos) {
        try {
            blockUI(true);

            final String title = LocaleUtils.getString("key.Inventory.Complete"); // 棚卸完了

            final Long authId = this.loginUser.getId();

            // 在庫があるか、棚卸実施した資材のみCSV出力する。
            List<TrnMaterialInfo> csvInfos = infos.stream()
                    .filter(p -> p.getInStockNum() > 0 || Objects.nonNull(p.getInventoryNum()))
                    .collect(Collectors.toList());

            final MaterialCsvFactory csvFactory = new MaterialCsvFactory(csvInfos);

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = new StringBuilder("material_")
                    .append(df.format(new Date()))
                    .append(".csv")
                    .toString();

            File csvFile = Paths.get(System.getProperty("user.home"), "Desktop", fileName).toFile();
            csvFactory.setCsvFile(csvFile);

            final int timeout = this.compTimeout;

            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
                    // 棚卸結果をCSV出力する。
                    csvFactory.outputCsv();

                    // 指定した区画の棚卸を完了する。
                    return facade.inventoryComplete(areaName, authId, timeout);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    boolean isEnd = true;
                    try {
                        ResponseEntity result = this.getValue();

                        AlertType alertType;
                        String alertMessage;
                        String alertDetails;

                        if (result.isSuccess()) {
                            // 棚卸を完了しました。
                            alertType = AlertType.INFORMATION;
                            alertMessage = LocaleUtils.getString("key.Inventory.CompleteMessage");
                            alertDetails = null;
                        } else {
                            String exMessage = null;
                            if (Objects.nonNull(result.getErrorType())) {
                                exMessage = result.getErrorType().name();
                            }

                            // 棚卸の完了でエラーが発生しました。
                            alertType = AlertType.ERROR;
                            alertMessage = LocaleUtils.getString("key.Inventory.CompleteErrorMessage");
                            alertDetails = new StringBuilder()
                                    .append(LocaleUtils.getString("key.ErrorDetail")).append(":\n")
                                    .append(exMessage)
                                    .toString();
                        }

                        showInventoryMessage(alertType, title, areaName, alertMessage, alertDetails);

                        // 検索条件で、区画を選択していないか、該当区画を選択している場合、在庫一覧を再取得する。
                        isEnd = afterInventoryProc(areaName);

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        if (isEnd) {
                            blockUI(false);
                        }
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        // 棚卸の完了でエラーが発生しました。
                        showInventoryException(title, areaName, LocaleUtils.getString("key.Inventory.CompleteErrorMessage"), this.getException());

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };

            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     * 棚卸のエラーダイアログを表示する。
     *
     * @param title タイトル
     * @param areaName 区画名
     * @param message メッセージ
     * @param exception エラー
     */
    private void showInventoryException(String title, String areaName, String message, Throwable exception) {
        String exMessage = null;
        if (Objects.nonNull(exception)) {
            exMessage = exception.getMessage();
        }

        String alertDetails = new StringBuilder()
                .append(LocaleUtils.getString("key.ErrorDetail")).append(":\n")
                .append(exMessage)
                .toString();

        this.showInventoryMessage(AlertType.ERROR, title, areaName, message, alertDetails);
    }

    /**
     * 棚卸のメッセージダイアログを表示する。
     *
     * @param alertType アラート種別
     * @param title タイトル
     * @param areaName 区画名
     * @param message メッセージ
     * @param alertDetails 詳細
     */
    private void showInventoryMessage(AlertType alertType, String title, String areaName, String message, String alertDetails) {
        String alertMessage = new StringBuilder(message).append("\n")
                .append(LocaleUtils.getString("key.Area")).append(": ").append(areaName)
                .toString();

        sc.showAlert(alertType, title, alertMessage, alertDetails);
    }

    /**
     * 在庫数を更新する。
     *
     * @param materialNo 資材番号
     * @param inStockNum 在庫数
     */
    private void updateInStockNum(String materialNo, int inStockNum) {
        try {
            blockUI(true);

            final Long authId = this.loginUser.getId();

            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
                    // 指定した資材情報の在庫数を更新する。
                    return facade.updateMaterialInStock(materialNo, inStockNum, authId);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        ResponseEntity result = this.getValue();

                        if (result.isSuccess()) {
                            return;
                        }

                        String exMessage = null;
                        if (Objects.nonNull(result.getErrorType())) {
                            exMessage = result.getErrorType().name();
                        }

                        // 在庫数の更新でエラーが発生しました。
                        String alertMessage = new StringBuilder(LocaleUtils.getString("key.UpdateStockError"))
                                .toString();
                        String alertDetails = new StringBuilder()
                                .append(LocaleUtils.getString("key.ErrorDetail")).append(":\n")
                                .append(exMessage)
                                .toString();

                        sc.showAlert(AlertType.ERROR, "", alertMessage, alertDetails);

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        // 資材リストを更新する。
                        onSearch(new ActionEvent());

                        blockUI(false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        String exMessage = null;
                        if (Objects.nonNull(this.getException())) {
                            exMessage = this.getException().getMessage();
                        }

                        // 在庫数の更新でエラーが発生しました。
                        String alertMessage = new StringBuilder(LocaleUtils.getString("key.UpdateStockError"))
                                .toString();
                        String alertDetails = new StringBuilder()
                                .append(LocaleUtils.getString("key.ErrorDetail")).append(":\n")
                                .append(exMessage)
                                .toString();

                        sc.showAlert(AlertType.ERROR, "", alertMessage, alertDetails);

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };

            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     * 棚卸操作(開始・中止・完了)後の在庫一覧再検索を行なう。
     *
     * @param areaName 棚卸操作を行なった区画名
     * @return ブロックを解除するか？(true:解除する, false:解除しない)
     */
    private boolean afterInventoryProc(String areaName) {
        // 検索を行なっていない場合は何もしない。
        if (Objects.isNull(this.materialCondition)) {
            return true;
        }

        // 検索条件の区画が、未指定か棚卸の開始・中止・完了を行なった区画で検索を行なっていた場合、同じ条件で再検索を行なう。
        String searchAreaName = this.materialCondition.getAreaName();
        if (StringUtils.isEmpty(searchAreaName)
                || StringUtils.equals(String.valueOf(searchAreaName), areaName) ) {
            // 前回と同じ条件で検索する。
            this.search(this.materialCondition, this.searchGrouping);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 在庫リストを出力する。
     */
    private void outputInventoryCsv() {
        logger.info("outputInventoryCsv start.");
        try {
            if (materials.isEmpty()) {
                return;
            }

            final InventoryCsvFactory factory = new InventoryCsvFactory(this.materials);

            boolean isChoice = factory.choiceFile();
            if (!isChoice) {
                return;
            }

            blockUI(true);

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    factory.outputCsv();
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(LocaleUtils.getString("key.PrintOutCSV"));
                        alert.setHeaderText(LocaleUtils.getString("key.FileOutputCompleted"));
                        alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(LocaleUtils.getString("key.FileName") + ": " + factory.getCsvFile().getName())));
                        alert.showAndWait();

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

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(LocaleUtils.getString("key.PrintOutCSV"));
                        alert.setHeaderText(LocaleUtils.getString("key.FileOutputErrorOccured"));
                        alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(LocaleUtils.getString("key.ErrorDetail") + ": " + this.getException().getLocalizedMessage())));
                        alert.showAndWait();

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };

            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }
}
