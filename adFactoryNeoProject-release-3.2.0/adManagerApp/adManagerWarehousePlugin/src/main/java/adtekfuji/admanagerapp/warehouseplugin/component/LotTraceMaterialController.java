/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.component;

import adtekfuji.admanagerapp.warehouseplugin.common.Constants;
import adtekfuji.admanagerapp.warehouseplugin.common.LotTraceCsvFactory;
import adtekfuji.clientservice.WarehouseInfoFaced;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.search.LotTraceCondition;
import jp.adtekfuji.adFactory.entity.search.MaterialCondition;
import jp.adtekfuji.adFactory.entity.warehouse.MstLocationInfo;
import jp.adtekfuji.adFactory.entity.warehouse.MstProductInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnLotTraceInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTreeTableView;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ロットトレース(資材)画面 コントローラー
 *
 * @author nar-nakamura
 */
@FxComponent(id = "LotTraceMaterial", fxmlPath = "/fxml/warehouseplugin/lot_trace_material.fxml")
public class LotTraceMaterialController implements Initializable, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final WarehouseInfoFaced facade = new WarehouseInfoFaced();

    private final TreeItem<TrnMaterialInfo> mainRoot = new TreeItem<>(TrnMaterialInfo.create(new MstProductInfo()));
    private final TreeItem<TrnLotTraceInfo> subRoot = new TreeItem<>(TrnLotTraceInfo.createWorkItem(""));
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final List<TrnLotTraceInfo> lotTraceList = new ArrayList<>();
        
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private CheckBox productNoCheck;
    @FXML
    private TextField productNoField;
    @FXML
    private CheckBox supplyNoCheck;
    @FXML
    private TextField supplyNoField;
    @FXML
    private CheckBox orderNoCheck;
    @FXML
    private TextField orderNoField;
    @FXML
    private CheckBox lotNoCheck;
    @FXML
    private TextField lotNoField;
    @FXML
    private CheckBox materialNoCheck;
    @FXML
    private TextField materialNoField;
    @FXML
    private SplitPane splitPane;

    @FXML
    private PropertySaveTreeTableView<TrnMaterialInfo> mainTable;
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> supplyNoColumn;    // 発注番号
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> orderNoColumn;     // 製造オーダー番号
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> lotNoColumn;    // 製造番号
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> materialNoColumn;  // 資材番号
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> areaNameColumn;    // 区画名
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> locationNoColumn;  // 棚番号
    @FXML
    private TreeTableColumn<TrnMaterialInfo, String> stockNumColumn;    // 在庫数

    @FXML
    private PropertySaveTreeTableView<TrnLotTraceInfo> subTable;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> subKanbanNameColumn;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> subModelNameColumn;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> subQuantityColumn;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> subPersonNameColumn;
    @FXML
    private TreeTableColumn<TrnLotTraceInfo, String> subAssemblyDatetimeColumn;

    @FXML
    private Button outputCsvButton;
    @FXML
    private Pane progressPane;

    /**
     * ロットトレース(資材)画面を初期化する。
     * 
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        mainTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        subTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        // 入力欄を無効にする。
        this.productNoField.setDisable(true);
        this.supplyNoField.setDisable(true);
        this.orderNoField.setDisable(true);
        this.lotNoField.setDisable(true);
        this.materialNoField.setDisable(true);

        // CSV出力ボタンを無効にする。
        this.outputCsvButton.setDisable(true);

        // 検索条件のチェックがONの場合、入力欄を無効にする。
        // 品目
        this.productNoCheck.selectedProperty().addListener((observable, newValue, oldValue) -> {
            this.productNoField.setDisable(newValue);
        });

        // 発注番号
        this.supplyNoCheck.selectedProperty().addListener((observable, newValue, oldValue) -> {
            this.supplyNoField.setDisable(newValue);
        });

        // 製造オーダー番号
        this.orderNoCheck.selectedProperty().addListener((observable, newValue, oldValue) -> {
            this.orderNoField.setDisable(newValue);
        });

        // 製造番号(ロット番号)
        this.lotNoCheck.selectedProperty().addListener((observable, newValue, oldValue) -> {
            this.lotNoField.setDisable(newValue);
        });

        // 資材番号
        this.materialNoCheck.selectedProperty().addListener((observable, newValue, oldValue) -> {
            this.materialNoField.setDisable(newValue);
        });

        SplitPaneUtils.loadDividerPosition(this.splitPane, this.getClass().getSimpleName());

        // 在庫状況一覧を初期化
        this.mainTable.setRowFactory(value -> {
            TreeTableRow<TrnMaterialInfo> row = new TreeTableRow<TrnMaterialInfo>() {
                @Override
                protected void updateItem(TrnMaterialInfo item, boolean empty) {
                    super.updateItem(item, empty);
                }
            };
        
            this.mainTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends TreeItem<TrnMaterialInfo>> item) -> {
                if (row.isFocused()) {
                    row.setStyle(null);
                } else {
                    if (Objects.nonNull(row.getTreeItem())) {
                        row.setStyle(row.getTreeItem().isLeaf()? null : "-fx-background-color:#f2f2f2;");
                    } else {
                        row.setStyle(null);
                    }
                }
            });
        
            return row ;
        });

        // 資材情報の右寄せセル
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

        // ロットトレース情報の右寄せセル
        Callback<TreeTableColumn<TrnLotTraceInfo, String>, TreeTableCell<TrnLotTraceInfo, String>> lotTraceCellRightFactory =
            (final TreeTableColumn<TrnLotTraceInfo, String> param) -> {
                TreeTableCell<TrnLotTraceInfo, String>cell = new TreeTableCell<TrnLotTraceInfo, String>() {
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

        // メインテーブルの行選択でサブテーブルの表示を更新する。
        this.mainTable.getSelectionModel().getSelectedItems().addListener(
            (ListChangeListener.Change<? extends TreeItem<TrnMaterialInfo>> change) -> {
                updateSubTable();
            }
        );

        // ルートツリーを非表示
        this.mainTable.init(StockViewController.class.getSimpleName() + ".mainTable");
        this.mainTable.setShowRoot(false);
        this.subTable.setShowRoot(false);
        
        // 複数行の選択を有効
        this.mainTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.mainTable.getSelectionModel().setCellSelectionEnabled(false);

        // 発注番号
        this.supplyNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                -> new ReadOnlyStringWrapper(o.getValue().getValue().getSupplyNo()));

        this.supplyNoColumn.setCellFactory((TreeTableColumn<TrnMaterialInfo, String> param) -> {
            TreeTableCell cell = new TreeTableCell<TrnMaterialInfo, String>(){
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    try {
                        TreeTableRow<TrnMaterialInfo> row = getTreeTableRow();
                        if (empty) {
                            setText(null);
                            row.setStyle(null);
                        } else {
                            if (Objects.isNull(row.getTreeItem()) || !row.getTreeItem().isLeaf()) {
                                row.setStyle(null);
                            } else if (row.isFocused() || row.isSelected()) {
                                row.setStyle("-fx-background-color: #0096c9; -fx-table-cell-border-color: #21a5d1; -fx-text-background-color: #ffffff;");
                            } else {
                                row.setStyle("-fx-background-color:#f2f2f2;");
                            }
                            setText(item);
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }
            };
            return cell;
        });


        // 製造番号
        this.orderNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                -> new ReadOnlyStringWrapper(o.getValue().getValue().getOrderNo()));
        // ロット番号
        this.lotNoColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                -> new ReadOnlyStringWrapper(o.getValue().getValue().getPartsNo()));
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
        this.stockNumColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getInStockNum()) ? String.valueOf(o.getValue().getValue().getInStockNum()) : null));
        this.stockNumColumn.setCellFactory(treeTableCellRightFactory);

        // カンバン名
        this.subKanbanNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o) 
                -> new ReadOnlyStringWrapper(o.getValue().getValue().getKanbanName()));
        // モデル名
        this.subModelNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o) 
                -> new ReadOnlyStringWrapper(o.getValue().getValue().getModelName()));
        // 数量
        this.subQuantityColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o) 
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getTraceNum()) ? String.valueOf(o.getValue().getValue().getTraceNum()) : null));
        this.subQuantityColumn.setCellFactory(lotTraceCellRightFactory);
        // 作業者名
        this.subPersonNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o) 
                -> new ReadOnlyStringWrapper(o.getValue().getValue().getPersonName()));
        // 組付け日時
        this.subAssemblyDatetimeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TrnLotTraceInfo, String> o)
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getValue().getAssemblyDatetime()) ? 
                        formatter.format(o.getValue().getValue().getAssemblyDatetime()) : null));
        
        // 前回の検索条件を読み込む。
        this.loadSearchConditions();
    }

    /**
     * コンポーネントを破棄する。
     * 
     * @return true:遷移可、false:遷移不可
     */
    @Override
    public boolean destoryComponent() {
        // 検索条件を保存する。
        this.saveSearchConditions();
        return true;
    }

    /**
     * 検索ボタンが押下された。
     * 
     * @param event アクションイベント
     */
    @FXML
    private void onSearch(ActionEvent event) {
        logger.info("onSearch start.");

        try {
            MaterialCondition condition = new MaterialCondition();
            
            // 入庫期間(から)
            if (Objects.nonNull(this.startDatePicker.getValue())) {
                condition.setFromDate(this.startDatePicker.getValue());
            }

            // 入庫期間(まで)
            if (Objects.nonNull(this.endDatePicker.getValue())) {
                condition.setToDate(this.endDatePicker.getValue());
            }

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
            
            // 製造番号
            if (this.lotNoCheck.isSelected()&& !StringUtils.isEmpty(this.lotNoField.getText())) {
                condition.setPartsNo(this.lotNoField.getText());
            }

            // 資材番号
            if (this.materialNoCheck.isSelected()&& !StringUtils.isEmpty(this.materialNoField.getText())) {
                condition.setMaterialNo(this.materialNoField.getText());
            }
            
            // 在庫なしの資材も含める
            condition.setOutStock(Boolean.TRUE);
              
            int range = Integer.parseInt(AdProperty.getProperties().getProperty(StockViewController.class.getName() + ".range", "500"));
            
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        blockUI(true);
                        
                        List<TreeItem<TrnMaterialInfo>> elements = FXCollections.observableArrayList();
                        
                        MstProductInfo product = new MstProductInfo();
                        TreeItem<TrnMaterialInfo> parentItem = new TreeItem(TrnMaterialInfo.create(product));
                        List<TreeItem<TrnMaterialInfo>> subElements = new ArrayList<>();
                        
                        int inStockNum = 0;
                        
                        int count = facade.countMaterials(condition);
                        for (int ii = 0; ii < count; ii += range) {
                            List<TrnMaterialInfo> materialList = facade.searchMaterials(condition, ii, ii + range - 1);

                            for (TrnMaterialInfo material : materialList) {
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
                        }

                        if (!subElements.isEmpty()) {
                            // 品目ツリーに追加
                            parentItem.getValue().setInStockNum(inStockNum);
                            parentItem.getChildren().addAll(subElements);

                            // ルートツリーに追加
                            elements.add(parentItem);
                        }

                        Platform.runLater(() -> {
                            // 最初に選択を消去しないと、完全に消去されません
                            subTable.getSelectionModel().clearSelection();
                            subRoot.getChildren().clear();
                            mainTable.getSelectionModel().clearSelection();
                            mainRoot.getChildren().clear();
                            mainRoot.getChildren().addAll(elements);
                            mainTable.setRoot(mainRoot);
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
                    blockUI(false);
                }
            };

            new Thread(task).start();           
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * CSV出力ボタンが押下された。
     * 
     * @param event アクションイベント
     */
    @FXML
    private void onOutputCsv(ActionEvent event) {
        this.outputCsv();
    }

    /**
     * 検索条件を読み込む。
     */
    private void loadSearchConditions() {
        try {
            AdProperty.load(Constants.UI_PROPERTY_NAME, Constants.UI_PROPERTY_NAME + ".properties");

            String prefix = LotTraceMaterialController.class.getName() + ".";

            Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);

            // 日付範囲
            String startDateProp = properties.getProperty(prefix + "startDatePicker", "");
            if (!StringUtils.isEmpty(startDateProp)) {
                LocalDate startDate = LocalDate.parse(startDateProp, DateTimeFormatter.ISO_DATE);
                this.startDatePicker.setValue(startDate);
            }

            String endDateProp = properties.getProperty(prefix + "endDatePicker", "");
            if (!StringUtils.isEmpty(endDateProp)) {
                LocalDate endDate = LocalDate.parse(endDateProp, DateTimeFormatter.ISO_DATE);
                this.endDatePicker.setValue(endDate);
            }

            // 品目
            this.productNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "productNoCheck", String.valueOf(Boolean.FALSE))));
            this.productNoField.setText(properties.getProperty(prefix + "productNoField"));

            // 発注番号
            this.supplyNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "supplyNoCheck", String.valueOf(Boolean.FALSE))));
            this.supplyNoField.setText(properties.getProperty(prefix + "supplyNoField"));

            // 製造オーダー番号
            this.orderNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "orderNoCheck", String.valueOf(Boolean.FALSE))));
            this.orderNoField.setText(properties.getProperty(prefix + "orderNoField"));

            // 製造番号
            this.lotNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "lotNoCheck", String.valueOf(Boolean.FALSE))));
            this.lotNoField.setText(properties.getProperty(prefix + "lotNoField"));

            // 資材番号
            this.materialNoCheck.setSelected(Boolean.parseBoolean(properties.getProperty(prefix + "materialNoCheck", String.valueOf(Boolean.FALSE))));
            this.materialNoField.setText(properties.getProperty(prefix + "materialNoField"));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 検索条件を保存する。
     */
    private void saveSearchConditions() {
        try {
            String prefix = LotTraceMaterialController.class.getName() + ".";

            Properties properties = AdProperty.getProperties(Constants.UI_PROPERTY_NAME);

            // 日付範囲
            LocalDate startDate = this.startDatePicker.getValue();
            if (Objects.nonNull(startDate)) {
                properties.setProperty(prefix + "startDatePicker", startDate.format(DateTimeFormatter.ISO_DATE));
            } else {
                properties.setProperty(prefix + "startDatePicker", "");
            }

            LocalDate endDate = this.endDatePicker.getValue();
            if (Objects.nonNull(endDate)) {
                properties.setProperty(prefix + "endDatePicker", endDate.format(DateTimeFormatter.ISO_DATE));
            } else {
                properties.setProperty(prefix + "endDatePicker", "");
            }

            // 品目
            properties.setProperty(prefix + "productNoCheck", String.valueOf(this.productNoCheck.isSelected()));
            if (Objects.nonNull(this.productNoField.getText())) {
                properties.setProperty(prefix + "productNoField", this.productNoField.getText());
            }

            // 発注番号
            properties.setProperty(prefix + "supplyNoCheck", String.valueOf(this.supplyNoCheck.isSelected()));
            if (Objects.nonNull(this.supplyNoField.getText())) {
                properties.setProperty(prefix + "supplyNoField", this.supplyNoField.getText());
            }

            // 製造オーダー番号
            properties.setProperty(prefix + "orderNoCheck", String.valueOf(this.orderNoCheck.isSelected()));
            if (Objects.nonNull(this.orderNoField.getText())) {
                properties.setProperty(prefix + "orderNoField", this.orderNoField.getText());
            }

            // ロット番号
            properties.setProperty(prefix + "lotNoCheck", String.valueOf(this.lotNoCheck.isSelected()));
            if (Objects.nonNull(this.lotNoField.getText())) {
                properties.setProperty(prefix + "lotNoField", this.lotNoField.getText());
            }

            // 資材番号
            properties.setProperty(prefix + "materialNoCheck", String.valueOf(this.materialNoCheck.isSelected()));
            if (Objects.nonNull(this.materialNoField.getText())) {
                properties.setProperty(prefix + "materialNoField", this.materialNoField.getText());
            }

            AdProperty.store(Constants.UI_PROPERTY_NAME);

            SplitPaneUtils.saveDividerPosition(this.splitPane, this.getClass().getSimpleName());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 製品一覧を更新する。
     */
    private void updateSubTable() {
        try {
            List<TreeItem<TrnMaterialInfo>> items = mainTable.getSelectionModel().getSelectedItems();

            List<String> materialNos = items.stream().filter(o -> !o.getValue().isParent()).map(o -> o.getValue().getMaterialNo()).collect(Collectors.toList());
            if (materialNos.isEmpty()) {
                subTable.getSelectionModel().clearSelection();
                subRoot.getChildren().clear();
                outputCsvButton.setDisable(true);
                return;
            }
            
            int range = Integer.parseInt(AdProperty.getProperties().getProperty(StockViewController.class.getName() + ".range", "500"));

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        blockUI(true);

                        LotTraceCondition condition = new LotTraceCondition();
                        condition.setEqualMaterialNo(true);
                        condition.setMaterialNos(materialNos);
                        //condition.setConfirm(Boolean.TRUE);
                        
                        lotTraceList.clear();

                        int count = facade.countLotTrace(condition);
                        for (int ii = 0; ii < count; ii += range) {
                            lotTraceList.addAll(facade.searchLotTrace(condition, ii, ii + range - 1));
                        }

                        // 工程名、カンバン名でソート
                        Collections.sort(lotTraceList, (p1, p2) -> {
                            int r = p1.getWorkName().compareTo(p2.getWorkName());
                            if (0 == r) {
                                return p1.getKanbanName().compareTo(p2.getKanbanName());
                            }
                            return r;
                        });

                        List<TreeItem<TrnLotTraceInfo>> elements = FXCollections.observableArrayList();
                        
                        TrnLotTraceInfo parent = TrnLotTraceInfo.createWorkItem("");
                        TreeItem<TrnLotTraceInfo> parentItem = new TreeItem(parent);
                        List<TreeItem<TrnLotTraceInfo>> subElements = new ArrayList<>();
                        
                        for (TrnLotTraceInfo lotTrace : lotTraceList) {
                            if (!StringUtils.equals(parent.getWorkName(), lotTrace.getWorkName())) {
                                if (!subElements.isEmpty()) {
                                    parentItem.getChildren().addAll(subElements);

                                    // ルートツリーに追加
                                    elements.add(parentItem);

                                    subElements.clear();
                                }

                                // 親アイテム(工程名)
                                parent = TrnLotTraceInfo.createWorkItem(lotTrace.getWorkName());
                                parentItem = new TreeItem(parent);
                                parentItem.setExpanded(true);
                            }
                            
                            subElements.add(new TreeItem(lotTrace));
                        }
                        
                        if (!subElements.isEmpty()) {
                            parentItem.getChildren().addAll(subElements);
                            elements.add(parentItem);
                        }

                        Platform.runLater(() -> {
                            // 最初に選択を消去しないと、完全に消去されません
                            subTable.getSelectionModel().clearSelection();
                            subRoot.getChildren().clear();
                            subRoot.getChildren().addAll(elements);
                            subTable.setRoot(subRoot);
                            // CSV出力ボタンを無効化
                            outputCsvButton.setDisable(lotTraceList.isEmpty());
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
                        subTable.getSelectionModel().clearSelection();
                        subRoot.getChildren().clear();
                        outputCsvButton.setDisable(true);
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
     * CSVファイル出力する。
     */
    private void outputCsv() {
        logger.info("outputCsv start.");
        try {
            final LotTraceCsvFactory csvFactory = new LotTraceCsvFactory(this.lotTraceList);

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
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(LocaleUtils.getString("key.PrintOutCSV"));
                        alert.setHeaderText(LocaleUtils.getString("key.FileOutputCompleted"));
                        alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(LocaleUtils.getString("key.FileName") + ": " + csvFactory.getCsvFile().getName())));
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
}
