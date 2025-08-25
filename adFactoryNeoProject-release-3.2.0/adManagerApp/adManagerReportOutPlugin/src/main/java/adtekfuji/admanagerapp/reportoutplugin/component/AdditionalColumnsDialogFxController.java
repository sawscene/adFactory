/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.reportoutplugin.component;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author nar-nakamura
 */
@FxComponent(id = "AdditionalColumnsDialog", fxmlPath = "/fxml/admanagerreportoutplugin/additional_columns_dialog.fxml")
public class AdditionalColumnsDialogFxController implements Initializable, ArgumentDelivery, DialogHandler {

    private static final Logger logger = LogManager.getLogger();
    private final List<String> DISABLED_ITEMS = Arrays.asList("Multi");

    @FXML
    private TextField propertyNameField;
    @FXML
    private TableView<Map.Entry<String, Boolean>> itemList;
    @FXML
    private TableColumn<Map.Entry<String, Boolean>, Boolean> checkColumn;
    @FXML
    private TableColumn<Map.Entry<String, Boolean>, String> propertyNameColumn;

    private final CheckBox allCheckBox = new CheckBox();

    private final ObservableList<Map.Entry<String, Boolean>> rows = FXCollections.observableArrayList();

    private Dialog dialog;

    private Map<String, Boolean> additionalColumns = new LinkedHashMap();

    public AdditionalColumnsDialogFxController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        itemList.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        // 検索条件
        this.propertyNameField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.onSearchButton(null);
            }
        });

        // 選択
        this.checkColumn.setCellValueFactory(cellData -> {
            Map.Entry<String, Boolean> cellValue = cellData.getValue();
            BooleanProperty property = new SimpleBooleanProperty(cellValue.getValue());

            property.addListener((observable, oldValue, newValue) -> cellValue.setValue(newValue));

            return property;
        });
        this.checkColumn.setCellFactory(CheckBoxTableCell.forTableColumn(this.checkColumn));

        // 項目名
        this.propertyNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<Map.Entry<String, Boolean>, String> param) -> new SimpleObjectProperty(param.getValue().getKey()));

        // 全て選択・解除
        this.checkColumn.setGraphic(allCheckBox);
        this.allCheckBox.setOnAction(event -> selectAllBoxes(event));

        this.itemList.setItems(this.rows);
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof LinkedHashMap) {
            this.additionalColumns = (LinkedHashMap) argument;
            this.updateView("");
        }
    }

    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancelDialog();
        });
    }

    /**
     * 検索ボタンイベント
     *
     * @param event 
     */
    @FXML
    private void onSearchButton(ActionEvent event) {
        this.updateView(propertyNameField.getText());
    }

    /**
     * OKボタンイベント
     *
     * @param event 
     */
    @FXML
    private void onOkButton(ActionEvent event) {
        try {
            this.additionalColumns.entrySet().stream()
                    .filter(o -> DISABLED_ITEMS.contains(o.getKey()))
                    .forEach(o -> o.setValue(Boolean.FALSE));
            
            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * キャンセルボタンイベント
     *
     * @param event 
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
        this.cancelDialog();
    }

    /**
     * キャンセル処理
     */
    private void cancelDialog() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 項目名に検索条件の文字列を含む追加項目リストを表示する。
     *
     * @param propertyName 検索条件
     */
    private void updateView(String propertyName) {
        try {
            this.rows.clear();
            if (propertyName.isEmpty()) {
                this.rows.addAll(this.additionalColumns.entrySet().stream().filter(o -> !DISABLED_ITEMS.contains(o.getKey())).collect(Collectors.toList()));
            } else {
                Set<Map.Entry<String, Boolean>> items = this.additionalColumns.entrySet().stream().
                        filter(p -> !DISABLED_ITEMS.contains(p.getKey()) && p.getKey().contains(propertyName))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                this.rows.addAll(items);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 全て選択・解除
     *
     * @param event 
     */
    public void selectAllBoxes(ActionEvent event) {
        if (event.getSource() instanceof CheckBox) {
            boolean isSelected = ((CheckBox) event.getSource()).isSelected();
            this.selectAllRows(isSelected);
        }
    }

    /**
     * 全ての行を選択または解除する。
     *
     * @param isSelected 
     */
    private void selectAllRows(boolean isSelected) {
        for (Map.Entry<String, Boolean> row : this.rows) {
            row.setValue(isSelected);
        }
        this.itemList.refresh();
    }
}
