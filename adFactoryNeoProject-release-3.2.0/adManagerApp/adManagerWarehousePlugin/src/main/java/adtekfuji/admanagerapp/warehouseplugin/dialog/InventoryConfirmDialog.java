/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.dialog;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 棚卸確認ダイアログ
 *
 * @author nar-nakamura
 */
@FxComponent(id = "InventoryConfirmDialog", fxmlPath = "/fxml/warehouseplugin/inventory_confirm_dialog.fxml")
public class InventoryConfirmDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final ObservableList<TrnMaterialInfo> rows = FXCollections.observableArrayList();

    private Dialog dialog;

    @FXML
    private Label infoLabel;

    @FXML
    private PropertySaveTableView<TrnMaterialInfo> tableView;
    @FXML
    private TableColumn<TrnMaterialInfo, String> supplyNoColumn; // 発注番号
    @FXML
    private TableColumn<TrnMaterialInfo, String> productNoColumn; // 品目
    @FXML
    private TableColumn<TrnMaterialInfo, String> productNameColumn; // 品名
    @FXML
    private TableColumn<TrnMaterialInfo, String> arrivalNumColumn; // 納入予定数
    @FXML
    private TableColumn<TrnMaterialInfo, String> inStockNumColumn; // 在庫数
    @FXML
    private TableColumn<TrnMaterialInfo, String> inventoryDiffColumn; // 在庫過不足
    @FXML
    private TableColumn<TrnMaterialInfo, String> inventoryNumColumn; // 棚卸在庫数
    @FXML
    private TableColumn<TrnMaterialInfo, String> locationNoColumn; // 棚番号
    @FXML
    private TableColumn<TrnMaterialInfo, String> inventoryLocationNoColumn; // 棚番訂正
    @FXML
    private TableColumn<TrnMaterialInfo, String> inventoryDateColumn; // 棚卸実施日

    /**
     * コンストラクタ
     */
    public InventoryConfirmDialog() {
    }

    /**
     * 初期化
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            tableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

            this.tableView.init("InventoryConfirmDialog");

            Callback<TableColumn<TrnMaterialInfo, String>, TableCell<TrnMaterialInfo, String>> tableCellRightFactory =
                (final TableColumn<TrnMaterialInfo, String> param) -> {
                    TableCell<TrnMaterialInfo, String>cell = new TableCell<TrnMaterialInfo, String>() {
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

            // 発注番号
            this.supplyNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(o.getValue().getSupplyNo()));

            // 品目
            this.productNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(o.getValue().getProduct().getProductNo()));

            // 品名
            this.productNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(o.getValue().getProduct().getProductName()));

            // 納入予定数
            this.arrivalNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getArrivalNum()) ? String.valueOf(o.getValue().getArrivalNum()) : null));
            this.arrivalNumColumn.setCellFactory(tableCellRightFactory);

            // 在庫数
            this.inStockNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getInStockNum()) ? String.valueOf(o.getValue().getInStockNum()) : "0"));
            this.inStockNumColumn.setCellFactory(tableCellRightFactory);

            // 在庫過不足
            this.inventoryDiffColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(this.getInventoryDiff(o.getValue())));
            this.inventoryDiffColumn.setCellFactory(tableCellRightFactory);

            // 棚卸在庫数
            this.inventoryNumColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getInventoryNum()) ? String.valueOf(o.getValue().getInventoryNum()) : null));
            this.inventoryNumColumn.setCellFactory(tableCellRightFactory);

            // 棚番号
            this.locationNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnMaterialInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getLocation()) ? o.getValue().getLocation().getLocationNo() : null));

            // 棚番訂正
            this.inventoryLocationNoColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnMaterialInfo, String> o)
                    -> new ReadOnlyStringWrapper(Objects.nonNull(o.getValue().getInventoryLocation()) ? o.getValue().getInventoryLocation().getLocationNo() : null));

            // 棚卸実施日
            this.inventoryDateColumn.setCellValueFactory((TableColumn.CellDataFeatures<TrnMaterialInfo, String> o) 
                    -> new ReadOnlyStringWrapper(this.getInventoryDate(o.getValue())));

            this.rows.clear();
            this.tableView.setItems(this.rows);

            // 未実施の行は背景色を変更する。
            this.tableView.setRowFactory(value -> {
                TableRow<TrnMaterialInfo> row = new TableRow<TrnMaterialInfo>() {
                    @Override
                    protected void updateItem(TrnMaterialInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        if (Objects.isNull(item)) {
                            setStyle(null);
                        } else if (isFocused() || isSelected()) {
                            setStyle("-fx-background-color: #0096c9; -fx-table-cell-border-color: #21a5d1; -fx-text-background-color: #ffffff;");
                        } else {
                            setStyle(Objects.isNull(item.getInventoryNum())? "-fx-background-color: #deb887; -fx-table-cell-border-color: #deb887; -fx-text-background-color: #000000;" : "-fx-background-color: #f2f2f2; -fx-text-background-color: #000000;");
                        }
                    }
                };

                row.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    TrnMaterialInfo item = row.getItem();
                    if (Objects.isNull(item)) {
                        row.setStyle(null);
                    } else if (newValue) {
                        row.setStyle("-fx-background-color: #0096c9; -fx-table-cell-border-color: #21a5d1; -fx-text-background-color: #ffffff;");
                    } else {
                        row.setStyle(Objects.isNull(item.getInventoryNum())? "-fx-background-color: #deb887; -fx-table-cell-border-color: #deb887; -fx-text-background-color: #000000;" : "-fx-background-color: #f2f2f2; -fx-text-background-color: #000000;");
                    }
                });

                return row;
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 引数を設定する。
     *
     * @param argument 引数
     */
    @Override
    public void setArgument(Object argument) {
        try {
            if (argument instanceof InventoryConfirmArgument) {
                InventoryConfirmArgument confirmArgument = (InventoryConfirmArgument) argument;

                String message = new StringBuilder(LocaleUtils.getString("key.InventoryConfirm.Message")).append("\n")
                        .append(LocaleUtils.getString("key.Area")).append(": ").append(confirmArgument.getAreaName()).append("\n\n")
                        .append(LocaleUtils.getString("key.InventoryConfirm.Description")).append("\n\n")
                        .append(LocaleUtils.getString("key.InventoryConfirm.ConfirmMessage"))
                        .toString();
                this.infoLabel.setText(message);

                this.rows.clear();
                if (Objects.nonNull(confirmArgument.getMaterials())) {
                    this.rows.addAll(confirmArgument.getMaterials());
                }

                this.tableView.setItems(this.rows);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ダイアログを設定する。
     *
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;

        final Window window = this.dialog.getDialogPane().getScene().getWindow();
        Stage stage = (Stage) window;
        stage.setMinHeight(400.0);
        stage.setMinWidth(600.0);

        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.closeDialog(ButtonType.CANCEL);
        });
    }

    /**
     * OKボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onOkButton(ActionEvent event) {
        this.closeDialog(ButtonType.OK);
    }

    /**
     * キャンセルボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
        this.closeDialog(ButtonType.CANCEL);
    }

    /**
     * ダイアログを閉じる。
     *
     * @param buttonType ボタン種別
     */
    private void closeDialog(ButtonType buttonType) {
        try {
            this.dialog.setResult(buttonType);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 在庫過不足に表示する文字列を取得する。
     *
     * @param material 資材情報
     * @return 在庫過不足に表示する文字列
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
     * 棚卸実施日に表示する文字列を取得する。
     *
     * @param material 資材情報
     * @return 棚卸実施日に表示する文字列
     */
    private String getInventoryDate(TrnMaterialInfo material) {
        try {
            if (Objects.isNull(material.getInventoryNum())) {
                return LocaleUtils.getString("key.NotImplemented"); // 未実施
            }

            // 棚卸実施日
            if (Objects.nonNull(material.getInventoryDate())) {
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                return dateFormat.format(material.getInventoryDate());
            } else {
                return null;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }
}
