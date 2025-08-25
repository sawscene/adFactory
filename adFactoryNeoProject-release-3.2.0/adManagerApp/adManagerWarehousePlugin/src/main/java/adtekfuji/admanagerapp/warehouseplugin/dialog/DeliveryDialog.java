/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.dialog;

import adtekfuji.admanagerapp.warehouseplugin.common.Constants;
import adtekfuji.admanagerapp.warehouseplugin.entity.DeliveryInfo;
import adtekfuji.admanagerapp.warehouseplugin.tablecell.TableNumberCell;
import adtekfuji.admanagerapp.warehouseplugin.tablecell.TableTextCell;
import adtekfuji.clientservice.WarehouseInfoFaced;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.ThreadUtils;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.search.DeliveryCondition;
import jp.adtekfuji.adFactory.entity.warehouse.AvailableInventoryInfo;
import jp.adtekfuji.adFactory.entity.warehouse.MstProductInfo;
import jp.adtekfuji.adFactory.entity.warehouse.ReserveInventoryParamInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryItemInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnReserveMaterialInfo;
import jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.javafxcommon.Config;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 払出指示詳細ダイアログ
 * 
 * @author s-heya
 */
@FxComponent(id = "DeliveryDialog", fxmlPath = "/fxml/warehouseplugin/DeliveryDialog.fxml")
public class DeliveryDialog implements Initializable, ArgumentDelivery, DialogHandler {
    
    private final Logger logger = LogManager.getLogger();
    private final WarehouseInfoFaced faced = new WarehouseInfoFaced();
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
    private List<TrnDeliveryInfo> deliveries;

    private Dialog dialog;
    private ObservableList<TrnDeliveryItemInfo> deliveryItems = FXCollections.observableArrayList();
    private ObservableList<AvailableInventoryInfo> reserveItems = FXCollections.observableArrayList();
    private Map<String, AvailableInventoryInfo> cloneReserveItems;
    private TrnDeliveryItemInfo selected;
    private StringProperty missingProperty = new SimpleStringProperty("0");
    
    @FXML
    private Pane rootPane;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Label deliveryNoLabel;
    @FXML
    private ComboBox areaComboBox;
    @FXML
    private Label modelNameLabel;
    @FXML
    private Label dueDateLabel;
    @FXML
    private Label productNumLabel;
    @FXML
    private Label missingLabel;
    @FXML
    private ComboBox unitNoComboBox;
    @FXML
    private CheckBox reservesCheck;
 
    @FXML
    private PropertySaveTableView<TrnDeliveryItemInfo> deliveryItemList;
    @FXML
    private TableColumn<TrnDeliveryItemInfo, String> unitNoColumn;
    @FXML
    private TableColumn<TrnDeliveryItemInfo, Number> itemNoColumn;
    @FXML
    private TableColumn<TrnDeliveryItemInfo, String> productNoColumn;
    @FXML
    private TableColumn<TrnDeliveryItemInfo, String> productNameColumn;
    @FXML
    private TableColumn<TrnDeliveryItemInfo, String> productTypeColumn;
    @FXML
    private TableColumn<TrnDeliveryItemInfo, String> materialDescColumn;
    @FXML
    private TableColumn<TrnDeliveryItemInfo, Number> requiredQtyColumn;
    @FXML
    private TableColumn<TrnDeliveryItemInfo, String> unitColumn;
    @FXML
    private TableColumn<TrnDeliveryItemInfo, String> locationNoColumn;
    @FXML
    private TableColumn<TrnDeliveryItemInfo, String> reserveColumn;
    @FXML
    private TableColumn<TrnDeliveryItemInfo, Number> pickingQtyColumn;          // ピッキング数
    @FXML
    private TableColumn<TrnDeliveryItemInfo, Number> withdrawQtyColumn;         // 在庫払出数
   
    
    @FXML
    private PropertySaveTableView<AvailableInventoryInfo> reserveList;
    @FXML
    private TableColumn<AvailableInventoryInfo, String> supplyNoColumn;         // 発注番号
    @FXML
    private TableColumn<AvailableInventoryInfo, String> productNumColumn;       // 製番
    @FXML
    private TableColumn<AvailableInventoryInfo, Number> orderingColumn;         // 発注中
    @FXML
    private TableColumn<AvailableInventoryInfo, Number> reservedStockColumn;    // 概引当
    @FXML
    private TableColumn<AvailableInventoryInfo, Number> availableStockColumn;   // 未引当
    @FXML
    private TableColumn<AvailableInventoryInfo, String> reservationColumn;      // 新規引当
    @FXML
    private TableColumn<AvailableInventoryInfo, Number> reservePickColumn;      // ピッキング数
    @FXML
    private TableColumn<AvailableInventoryInfo, String> noteColumn;             // コメント
        
    @FXML
    private Button autoButton;
    @FXML
    private Button releaseAllButton;
    @FXML
    private Button seveButton;
    @FXML
    private Button releaseButton;
    @FXML
    private Button applyButton;
    @FXML
    private Pane progress;

    /**
     * コンストラクタ
     */
    public DeliveryDialog() {
    }
    
    /**
     * 払出指示詳細ダイアログを初期化する。
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            this.autoButton.setDisable(true);           // 自動引当
            this.releaseAllButton.setDisable(true);     // 引当全解除
            this.seveButton.setDisable(true);           // 保存
            this.releaseButton.setDisable(true);        // 引当解除
            this.applyButton.setDisable(true);          // 引当を確定する
            this.reservationColumn.setEditable(false);  // 新規引当修正
            this.noteColumn.setEditable(false);         // コメント
        }

        this.deliveryItemList.init("deliveryItemList");
          
        this.reserveList.setPlaceholder(new Label("引当て可能な在庫がありません"));
        this.reserveList.init("reserveList");

        this.unitNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnDeliveryItemInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getUnitNo()));

        this.itemNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnDeliveryItemInfo, Number> o)
                -> o.getValue().itemNoProperty());

        this.productNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnDeliveryItemInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getProduct().getProductNo()));

        this.productNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnDeliveryItemInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getProduct().getProductName()));

        this.productTypeColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnDeliveryItemInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getProduct().getProperty().get(MstProductInfo.SPEC)));

        this.materialDescColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnDeliveryItemInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getProduct().getProperty().get(MstProductInfo.MATERIAL)));

        this.requiredQtyColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnDeliveryItemInfo, Number> o)
                -> o.getValue().requiredNumProperty());

        this.unitColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnDeliveryItemInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getProduct().getUnit()));

        this.locationNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnDeliveryItemInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getLocationNo((String) this.areaComboBox.getValue())));
        
        this.reserveColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnDeliveryItemInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getReserveStatus()));

        this.pickingQtyColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnDeliveryItemInfo, Number> o)
                -> o.getValue().deliveryNumProperty());

        this.withdrawQtyColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnDeliveryItemInfo, Number> o)
                -> o.getValue().withdrawNumProperty());

        Callback<TableColumn<AvailableInventoryInfo, String>, TableCell<AvailableInventoryInfo, String>> numberCellFactory = (TableColumn<AvailableInventoryInfo, String> p) 
                -> new TableNumberCell<>();
        Callback<TableColumn<AvailableInventoryInfo, String>, TableCell<AvailableInventoryInfo, String>> textCellFactory = (TableColumn<AvailableInventoryInfo, String> p) 
                -> new TableTextCell<>();
        
        this.supplyNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<AvailableInventoryInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getSupplyNo()));

        this.productNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<AvailableInventoryInfo, String> o)
                -> new ReadOnlyStringWrapper(o.getValue().getOrderNo()));

        this.orderingColumn.setCellValueFactory((TableColumn.CellDataFeatures<AvailableInventoryInfo, Number> o) -> o.getValue().orderNumProperty());

        this.reservedStockColumn.setCellValueFactory((TableColumn.CellDataFeatures<AvailableInventoryInfo, Number> o) -> o.getValue().reservedNumProperty());

        this.availableStockColumn.setCellValueFactory((TableColumn.CellDataFeatures<AvailableInventoryInfo, Number> o) -> o.getValue().availableNumProperty());

        this.reservationColumn.setCellValueFactory((TableColumn.CellDataFeatures<AvailableInventoryInfo, String> o) 
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getReservationNum()) ? o.getValue().getReservationNum().toString() : "0"));
                
        this.reservationColumn.setCellFactory(numberCellFactory);
        this.reservationColumn.setOnEditCommit((TableColumn.CellEditEvent<AvailableInventoryInfo, String> event) -> {
            try {
                String oldValue = event.getOldValue();
                String newValue = event.getNewValue();

                AvailableInventoryInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());

                if (StringUtils.isEmpty(newValue)) {
                    item.setReservationNum(0);
                    item.setReservationNum(Integer.parseInt(oldValue));
                } else if (!newValue.equals(oldValue)) {
                    Integer value = Integer.parseInt(newValue);

                    int sum = event.getTableView().getItems().stream()
                            .filter(o -> !item.getMaterialNo().equals(o.getMaterialNo()))
                            .mapToInt(o -> o.getReservationNum())
                            .sum();
                    if (this.selected.getRequiredNum() < (sum + value)) {
                        // 要求数を超過している場合
                        value = this.selected.getRequiredNum() - sum;
                    }

                    if (item.availableNumProperty().get() < value) {
                        // 有効在庫数を超過している場合
                        value = item.availableNumProperty().get();
                    }

                    item.setReservationNum(value);
                }

                reserveList.getSortOrder().clear();
                event.getTableView().refresh();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });
        
        this.reservePickColumn.setCellValueFactory((TableColumn.CellDataFeatures<AvailableInventoryInfo, Number> o) 
                -> o.getValue().deliveryNumProperty());

        this.noteColumn.setCellValueFactory((TableColumn.CellDataFeatures<AvailableInventoryInfo, String> o) 
                -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getNote()) ? o.getValue().getNote() : ""));

        this.noteColumn.setCellFactory(textCellFactory);
        this.noteColumn.setOnEditCommit((TableColumn.CellEditEvent<AvailableInventoryInfo, String> event) -> {
            try {
                String oldValue = event.getOldValue();
                String newValue = event.getNewValue();

                AvailableInventoryInfo item = event.getTableView().getItems().get(event.getTablePosition().getRow());

                if (StringUtils.isEmpty(newValue)) {
                    item.setNote("");
                } else if (!newValue.equals(oldValue)) {
                    item.setNote(newValue);
                }

                event.getTableView().refresh();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });
    }

    /**
     * 
     * 
     * @param argument 
     */
    @Override
    public void setArgument(Object argument) {
       
        if (argument instanceof Object[]) {
            Object[] array = (Object[]) argument;
            Properties properties = AdProperty.getProperties("adManagerUI");
            
            this.deliveries = (List<TrnDeliveryInfo>) array[0];
            
            List<TrnDeliveryItemInfo> items = this.deliveries.stream()
                    .flatMap(o -> o.getDeliveryList().stream())
                    .collect(Collectors.toList());

            this.deliveryItems.addAll(items);
            
            // フィルター
            FilteredList<TrnDeliveryItemInfo> filteredList = new FilteredList<>(this.deliveryItems, o -> reservesCheck.isSelected() ? o.getReserve() != 3 : true);
            SortedList<TrnDeliveryItemInfo> sortedList = new SortedList<>(filteredList);
            
            Comparator<TrnDeliveryItemInfo> comparator = Comparator.comparing(TrnDeliveryItemInfo::getUnitNo)
                    .thenComparing(TrnDeliveryItemInfo::getItemNo);
            sortedList.setComparator(comparator);
            
            this.deliveryItemList.setItems(sortedList);
            //sortedList.comparatorProperty().bind(this.deliveryItemList.comparatorProperty());
            
            this.deliveryItemList.getItems().addListener((ListChangeListener.Change<? extends TrnDeliveryItemInfo> arg) -> {
                this.disableButton();
            });
            
            this.deliveryItemList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            this.deliveryItemList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (Objects.nonNull(newValue)) {
                    this.selected = newValue;
                    onSelectedDeliveryItem(this.selected);
                }
            });

            this.deliveryItemList.setRowFactory((TableView<TrnDeliveryItemInfo> tableView) -> {
                final TableRow<TrnDeliveryItemInfo> row = new TableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {
                    final int index = row.getIndex();
                    
                    if (index >= 0 && index < tableView.getItems().size()) {
                        if (Objects.nonNull(selected) && Objects.nonNull(reserveItems)) {
                            long count = reserveItems.stream()
                                    .filter(o -> !o.equals(cloneReserveItems.get(o.getMaterialNo())))
                                    .count();
                            if (count > 0) {
                                String title = LocaleUtils.getString("key.confirm");
                                String message = LocaleUtils.getString("key.confirm.destroy");
                                            
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                                alert.initOwner(this.dialog.getOwner());
                                alert.initStyle(StageStyle.UTILITY);
                                alert.setTitle(title);
                                alert.setHeaderText(message);

                                ButtonType button = alert.showAndWait().orElse(ButtonType.YES);
                                if (ButtonType.CANCEL.equals(button)) {
                                    event.consume();
                                } else  if (ButtonType.YES.equals(button)) {
                                    event.consume();
                                    saveReserveInventory(selected);
                                    tableView.getSelectionModel().select(index);
                                }
                            }
                        }
                    }
                });
                return row;  
            });

            this.areaComboBox.setItems(FXCollections.observableArrayList(this.faced.findAllAreaName()));
            this.areaComboBox.setValue(properties.getProperty(Config.WAREHOUSE_AREA_NAME_DEFAULT));
            this.areaComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (Objects.nonNull(newValue)) {
                    try {
                        properties.setProperty(Config.WAREHOUSE_AREA_NAME_DEFAULT, this.areaComboBox.getValue().toString());
                        AdProperty.store("adManagerUI");
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                    
                    this.deliveryItemList.refresh();

                    if (Objects.nonNull(this.selected)) {
                        this.onSelectedDeliveryItem(this.selected);
                    } else {
                        this.reserveItems.clear();
                    }
                }
            });
            
            TrnDeliveryInfo delivery = this.deliveries.get(0);

            this.deliveryNoLabel.setText(delivery.getDeliveryNo());
            this.modelNameLabel.setText(delivery.getModelName());
            this.productNumLabel.setText(delivery.getOrderNo());
            
            this.deliveries.stream()
                    .filter(o -> Objects.nonNull(o.getDueDate()))
                    .map(o -> o.getDueDate())
                    .min((o1, o2 ) -> o1.compareTo(o2))
                    .ifPresent(min -> this.dueDateLabel.setText(new SimpleDateFormat("yyyy/MM/dd").format(min)));
            
            this.missingLabel.textProperty().bindBidirectional(this.missingProperty);

            // ユニットNo
            String all = LocaleUtils.getString("key.SelectAll");
            List<String> unitNoList = this.deliveries.stream()
                    .map(o -> o.getUnitNo())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            
            this.unitNoComboBox.setItems(FXCollections.observableArrayList());
            this.unitNoComboBox.getItems().add(all);
            this.unitNoComboBox.getItems().addAll(unitNoList);
            this.unitNoComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (Objects.isNull(newValue) || all.equals((String) newValue)) {
                    filteredList.setPredicate(o -> this.reservesCheck.isSelected() ? !Objects.equals(o.getReserve(), 3) : true);
                } else {
                    filteredList.setPredicate(o -> this.reservesCheck.isSelected() ? Objects.equals(o.getUnitNo(), newValue) && !Objects.equals(o.getReserve(), 3) : Objects.equals(o.getUnitNo(), newValue));
                }
            });
            this.unitNoComboBox.getSelectionModel().select(all);

            this.reservesCheck.setSelected(Boolean.TRUE.toString().equalsIgnoreCase(properties.getProperty(Constants.RESERVES_CHECK_KEY, Constants.RESERVES_CHECK__DEF)));
            this.reservesCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (all.equals(this.unitNoComboBox.getValue())) {
                    filteredList.setPredicate(o -> newValue ? !Objects.equals(o.getReserve(), 3) : true);
                } else {
                    filteredList.setPredicate(o -> newValue ? Objects.equals(o.getUnitNo(), this.unitNoComboBox.getValue()) && !Objects.equals(o.getReserve(), 3) : Objects.equals(o.getUnitNo(), this.unitNoComboBox.getValue()));
                }

                try {
                    properties.setProperty(Constants.RESERVES_CHECK_KEY, newValue.toString());
                    AdProperty.store("adManagerUI");
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });
            
            this.reserveList.setItems(this.reserveItems);

            if (Objects.nonNull(array[1])) {
                DeliveryInfo deliveryInfo = (DeliveryInfo) array[1];
                this.unitNoComboBox.setValue(deliveryInfo.getValue().getUnitNo());
            }
            
            this.updateMissingNum();
            this.disableButton();
        }
    }

    /**
     * 払出品目が選択された。
     * 
     * @param deliveryItem 
     */
    private void onSelectedDeliveryItem(TrnDeliveryItemInfo deliveryItem) {
        
        if (Objects.equals(deliveryItem.getArrange(), 2)) {
            this.reserveItems.clear();
            this.seveButton.setDisable(true);
            this.releaseButton.setDisable(true);
            this.reserveList.setEditable(false);
            return;
        }

        if (Objects.isNull(this.areaComboBox.getValue())) {
            // 区画名を選択してください
            SceneContiner.getInstance().showAlert(Alert.AlertType.WARNING, "", LocaleUtils.getString("warehouse.m001"));
            return;
        }
        
        if (loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            this.deliveries.stream()
                        .filter(o -> StringUtils.equals(o.getDeliveryNo(), deliveryItem.getDeliveryNo()))
                        .findFirst()
                        .ifPresent(o -> {
                            boolean editable = DeliveryStatusEnum.CONFIRM.equals(o.getStatus()) || DeliveryStatusEnum.WAITING.equals(o.getStatus());
                            this.seveButton.setDisable(!editable);
                            this.releaseButton.setDisable(!editable);
                            this.reserveList.setEditable(editable);
                        });
        }

        this.blockUI(true);
        
        Task task = new Task<List<AvailableInventoryInfo>>() {
            @Override
            protected List<AvailableInventoryInfo> call() throws Exception {
                try {
                    return faced.findAvailableInventory(deliveryItem.getDeliveryNo(), deliveryItem.getItemNo(), (String) areaComboBox.getValue());
                } finally {
                    blockUI(false);
                }
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    List<AvailableInventoryInfo> list = this.getValue();

                    cloneReserveItems = new HashMap<>();
                    for (AvailableInventoryInfo o : list) {
                        cloneReserveItems.put(o.getMaterialNo(), o.clone());
                    }
                    
                    reserveItems.clear();
                    reserveItems.addAll(list);

                    reserveList.getSortOrder().clear();
                    reserveList.getSortOrder().add(reservationColumn);
                    reservationColumn.setSortType(TableColumn.SortType.DESCENDING);

                } catch (Exception ex) {
                    logger.fatal(ex, ex);
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
    }
    
    
    /**
     * Dialog を設定する。
     * 
     * @param dialog 
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent event) -> {
            if (!this.closeDialog()) {
                event.consume();
            }
        });
        this.rootPane.setPrefHeight(this.dialog.getOwner().getHeight() - 100.0);
        this.rootPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            this.splitPane.setDividerPositions(0.7);
        });
    }

    /**
     * 自動引当
     * 
     * @param event 
     */
    @FXML
    private void onAutoReservation(ActionEvent event) {
        if (Objects.isNull(this.areaComboBox.getValue())) {
            // 区画名を選択してください
            SceneContiner.getInstance().showAlert(Alert.AlertType.WARNING, "", LocaleUtils.getString("warehouse.m001"));
            return;
        }
        
        List<TrnDeliveryItemInfo> list = this.deliveryItemList.getItems().stream()
                .filter(o -> 
                        this.deliveries.stream()
                                .filter(p -> p.getDeliveryNo().equals(o.getDeliveryNo()) 
                                        && (DeliveryStatusEnum.CONFIRM.equals(p.getStatus()) || DeliveryStatusEnum.WAITING.equals(p.getStatus())))
                                .findFirst()
                                .isPresent()
                )
                .collect(Collectors.toList());
        
        if (list.isEmpty()) {
            // 在庫引当が可能な部品がありません。
            SceneContiner.getInstance().showAlert(Alert.AlertType.WARNING, "", LocaleUtils.getString("warehouse.m002"));
            return;
        }

        if (list.stream()
                .filter(o -> !Objects.equals(o.getArrange(), 2) && Objects.nonNull(o.getReserve()) && o.getReserve() > 0)
                .findFirst()
                .isPresent()) {

            // 引当済みの部品があります。引当を解除して、在庫引当をおこないます。よろしいですか?
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
            alert.initOwner(this.dialog.getOwner());
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(LocaleUtils.getString("key.confirm"));
            alert.setHeaderText(LocaleUtils.getString("warehouse.m003"));

            ButtonType button = alert.showAndWait().orElse(ButtonType.YES);
            if (ButtonType.NO.equals(button)) {
                return;
            }
        }

        List<String> deliveryNos = list.stream()
                .map(o -> o.getDeliveryNo())
                .distinct()
                .collect(Collectors.toList());
        
        this.blockUI(true);

        Task task = new Task<ResponseEntity>() {
            @Override
            protected ResponseEntity call() throws Exception {
                try {
                    return faced.reserveInventoryAuto(deliveryNos, (String) areaComboBox.getValue());
                } finally {
                    blockUI(false);
                }
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    ResponseEntity response = this.getValue();
                    if (response.isSuccess()) {
                        TrnDeliveryItemInfo _selected = deliveryItemList.getSelectionModel().getSelectedItem();

                        updateDeleveryItems();

                        if (Objects.nonNull(_selected)) {
                            deliveryItemList.getSelectionModel().select(_selected);
                        }
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
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
    }
    
    /**
     * 払出品目を更新する。
     */
    private void updateDeleveryItems() throws Exception{
        DeliveryCondition condition = new DeliveryCondition();
        condition.setModelName(modelNameLabel.getText());
        condition.setOrderNo(productNumLabel.getText());

        this.deliveries = this.faced.searchDeliveryRange(condition, null, null);

        List<TrnDeliveryItemInfo> items = this.deliveries.stream()
            .flatMap(o -> o.getDeliveryList().stream())
            .collect(Collectors.toList());

        this.deliveryItems.clear();
        this.deliveryItems.addAll(items);
        this.reserveItems.clear();

        this.updateMissingNum();
    }
    
    /**
     * 引当全解除
     * 
     * @param event 
     */
    @FXML
    private void onReleaseAllReservation(ActionEvent event) {
        // 全ての部品の在庫引当を解除します。よろしいですか?
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.initOwner(this.dialog.getOwner());
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(LocaleUtils.getString("key.confirm"));
        alert.setHeaderText(LocaleUtils.getString("conReleaseAllReserve"));

        ButtonType button = alert.showAndWait().orElse(ButtonType.YES);
        if (ButtonType.NO.equals(button)) {
            return;
        }

        List<String> deliveryNos = this.deliveryItemList.getItems().stream()
                .filter(o -> 
                        this.deliveries.stream()
                                .filter(p -> p.getDeliveryNo().equals(o.getDeliveryNo()) 
                                        && (DeliveryStatusEnum.CONFIRM.equals(p.getStatus()) || DeliveryStatusEnum.WAITING.equals(p.getStatus())))
                                .findFirst()
                                .isPresent()
                )
                .map(o -> o.getDeliveryNo())
                .distinct()
                .collect(Collectors.toList());

        if (deliveryNos.isEmpty()) {
            return;
        }

        this.blockUI(true);

        Task task = new Task<ResponseEntity>() {
            @Override
            protected ResponseEntity call() throws Exception {
                try {
                    
                    return faced.releaseReservation(deliveryNos);
                } finally {
                    blockUI(false);
                }
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    ResponseEntity response = this.getValue();
                    if (response.isSuccess()) {
                        TrnDeliveryItemInfo _selected = deliveryItemList.getSelectionModel().getSelectedItem();

                        updateDeleveryItems();

                        if (Objects.nonNull(_selected)) {
                            deliveryItemList.getSelectionModel().select(_selected);
                        }
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
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
    }
    
    /**
     * 引当解除
     * 
     * @param event 
     */
    @FXML
    private void onReleaseReservation(ActionEvent event) {
        // 選択された部品の在庫引当を解除します。よろしいですか?
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.initOwner(this.dialog.getOwner());
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(LocaleUtils.getString("key.confirm"));
        alert.setHeaderText(LocaleUtils.getString("conReleaseReserve"));

        ButtonType button = alert.showAndWait().orElse(ButtonType.YES);
        if (ButtonType.NO.equals(button)) {
            return;
        }

        this.blockUI(true);

        Task task = new Task<ResponseEntity>() {
            @Override
            protected ResponseEntity call() throws Exception {
                try {
                    if (Objects.isNull(selected)) {
                        return null;
                    }
                    return faced.releaseReservation(selected.getDeliveryNo(), selected.getItemNo());
                } finally {
                    blockUI(false);
                }
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    ResponseEntity response = this.getValue();
                    if (response.isSuccess()) {
                        updateDeleveryItems();
                        deliveryItemList.getSelectionModel().select(selected);
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
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
    }

    /**
     * 保存
     * 
     * @param event 
     */
    @FXML
    private void onSave(ActionEvent event) {
        if (Objects.nonNull(selected)) {
            this.saveReserveInventory(selected);
        }
    }

    /**
     * 在庫引当を保存する。
     * 
     * @param deliveryItem 
     */
    private void saveReserveInventory(TrnDeliveryItemInfo deliveryItem) {
        this.blockUI(true);

        Task task = new Task<ResponseEntity>() {
            @Override
            protected ResponseEntity call() throws Exception {
                try {

                    Date now = new Date();
                    LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
                    
                    List<TrnReserveMaterialInfo> list = reserveItems.stream()
                            //.filter(o -> !o.equals(cloneReserveItems.get(o.getMaterialNo())))
                            .filter(o -> o.getReservationNum() > 0)
                            .map(o -> new TrnReserveMaterialInfo(
                                    deliveryItem.getDeliveryNo(),
                                    deliveryItem.getItemNo(),
                                    o.getMaterialNo(),
                                    o.getReservationNum(),
                                    o.getNote(),
                                    now, 
                                    loginUser.getLoginId())
                            )
                            .collect(Collectors.toList());
                   
                    ReserveInventoryParamInfo param = new ReserveInventoryParamInfo(deliveryItem.getDeliveryNo(), deliveryItem.getItemNo(), (String) areaComboBox.getValue(), list);

                    return faced.reserveInventory(param);

                } finally {
                    blockUI(false);
                }
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    ResponseEntity response = this.getValue();
                    if (response.isSuccess()) {
                        updateDeleveryItems();
                        deliveryItemList.getSelectionModel().select(selected);
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
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
        
        try {
            Thread thread = new Thread(task);
            thread.start();
            ThreadUtils.waitFor(thread, 30000);

        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 閉じるボタンが押下された。
     * 
     * @param event 
     */
    @FXML
    private void onClose(ActionEvent event) {
        this.closeDialog();
    }

    /**
     * 払出指示詳細ダイアログを閉じる。
     */
    private boolean closeDialog() {
        try {
            if (Objects.nonNull(cloneReserveItems)) {
                long count = reserveItems.stream()
                        .filter(o -> !o.equals(cloneReserveItems.get(o.getMaterialNo())))
                        .count();
                if (count > 0) {
                    String title = LocaleUtils.getString("key.confirm");
                    String message = LocaleUtils.getString("key.confirm.destroy");

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                    alert.initOwner(this.dialog.getOwner());
                    alert.initStyle(StageStyle.UTILITY);
                    alert.setTitle(title);
                    alert.setHeaderText(message);

                    ButtonType button = alert.showAndWait().orElse(ButtonType.YES);
                    if (ButtonType.CANCEL.equals(button)) {
                        return false;
                    } else  if (ButtonType.YES.equals(button)) {
                        saveReserveInventory(selected);
                    }
                }
            }
            
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return true;
    }

    /**
     * 在庫引当を表示更新する。
     * 
     * @param event 
     */
    @FXML
    private void onRefresh(ActionEvent event) {
        if (Objects.nonNull(selected)) {
            this.onSelectedDeliveryItem(selected);
        } else {
            this.reserveItems.clear();
        }
    }

    /**
     * 引当を確定する
     * 
     * @param event 
     */
    public void onApply(ActionEvent event) {
        // 在庫引当を確定します。よろしいですか?
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.initOwner(this.dialog.getOwner());
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(LocaleUtils.getString("key.confirm"));
        alert.setHeaderText(LocaleUtils.getString("conReserve"));

        ButtonType button = alert.showAndWait().orElse(ButtonType.YES);
        if (ButtonType.NO.equals(button)) {
            return;
        }

        this.blockUI(true);
        
        List<String> deliveryNos = this.getConfirmDelivery();

        Task task = new Task<ResponseEntity>() {
            @Override
            protected ResponseEntity call() throws Exception {
                return faced.updateDaliveryStatus(deliveryNos, DeliveryStatusEnum.WAITING);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    ResponseEntity response = this.getValue();
                    if (response.isSuccess()) {
                        applyButton.setDisable(true);
                    } else {
                        DialogBox.alert(response.getErrorType());
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
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
            }
        };
        
        try {
            Thread thread = new Thread(task);
            thread.start();
            ThreadUtils.waitFor(thread, 30000);

        } catch (InterruptedException ex) {
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
            progress.setVisible(block);
        });
    }

    /**
     * 欠品数を更新する。
     */
    private void updateMissingNum() {
        int missingNum = this.deliveries.stream()
                .filter(o -> Objects.nonNull(o.getStockOutNum()))
                .mapToInt(o -> o.getStockOutNum())
                .sum();
        this.missingProperty.set(String.valueOf(missingNum));
    }
    
    /**
     * ボタンを無効化する。
     */
    private void disableButton() {
        this.applyButton.setDisable(!loginUser.checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE) || this.getConfirmDelivery().isEmpty());
    }
    
    /**
     * 確認待ち払出指示を取得する。
     * 
     * @return 
     */
    private List<String> getConfirmDelivery() {
        return this.deliveryItemList.getItems().stream()
                .filter(o -> 
                        this.deliveries.stream()
                                .filter(p -> p.getDeliveryNo().equals(o.getDeliveryNo()) 
                                        && (DeliveryStatusEnum.CONFIRM.equals(p.getStatus())))
                                .findFirst()
                                .isPresent()
                )
                .map(o -> o.getDeliveryNo())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 詳細ボタン
     * 
     * @param event 
     */
    public void onDetatil(ActionEvent event) {
        AvailableInventoryInfo item = this.reserveList.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(item)) {
            String[] values = new String[2];
            
            values[0] = item.getSupplyNo();
            if (item.getReservedNum() > 0) {
                List<TrnReserveMaterialInfo> list = this.faced.findReserveMaterials(item.getMaterialNo());
                if (!list.isEmpty()) {
                    values[1] = list.stream()
                           .map(o -> o.getPk().getDeliveryNo() + " (" + o.getPk().getItemNo() + ") : " + o.getReservedNum() 
                                   + (StringUtils.isEmpty(this.selected.getProduct().getUnit()) ? "" : this.selected.getProduct().getUnit()))
                           .sorted()
                           .collect(Collectors.joining("\r\n"));
                }
            }

            ButtonType result = SceneContiner.getInstance().showComponentDialog(LocaleUtils.getString("details"), 
                    "PropertyCompo", values, new ButtonType[]{ButtonType.OK}, (Stage) this.dialog.getDialogPane().getScene().getWindow());
            if (ButtonType.OK == result) {
            }
        }
    }
}

