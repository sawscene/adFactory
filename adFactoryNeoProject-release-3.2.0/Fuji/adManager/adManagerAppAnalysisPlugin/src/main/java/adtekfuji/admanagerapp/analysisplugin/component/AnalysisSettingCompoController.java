/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.component;

import adtekfuji.admanagerapp.analysisplugin.common.AnalysisWorkFilterData;
import adtekfuji.admanagerapp.analysisplugin.common.AnalysisWorkFilterData.TimeUnitEnum;
import adtekfuji.admanagerapp.analysisplugin.javafx.CheckTableData;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

/**
 * 設定画面クラス(TODO:動的生成にリファクタすること(設定が増えると後々可読性が悪くなる))
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.01.Mon
 */
@FxComponent(id = "AnalysisSettingCompo", fxmlPath = "/fxml/compo/analysisSettingCompo.fxml")
public class AnalysisSettingCompoController implements Initializable, ArgumentDelivery {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * 時間単位設定のコンボボックスリスト用CellFactory
     * 
     */
    class TimeUnitComboCellFactory extends ListCell<TimeUnitEnum> {

        @Override
        protected void updateItem(TimeUnitEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(TimeUnitEnum.getLocale(rb, item));
            }
        }
    }

    private AnalysisWorkFilterData analysisWorkFilterData;

    @FXML
    private TextField analyistTactFilterS;
    @FXML
    private TextField analyistTactFilterE;
    @FXML
    private TableView<CheckTableData> delayTableView;
    @FXML
    private TableColumn<CheckTableData, Boolean> delaySelectColumn;
    @FXML
    private TableColumn<CheckTableData, String> delayNameColumn;
    @FXML
    private ComboBox<AnalysisWorkFilterData.TimeUnitEnum> timeUnitCombo;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // レイアウト設定        

        delayTableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        CheckBox tableCheck = new CheckBox();
        this.analyistTactFilterE.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches("\\d")) {
                event.consume();
            }
        });
        this.analyistTactFilterS.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches("\\d")) {
                event.consume();
            }
        });
        tableCheck.setOnAction((ActionEvent ActionEvent) -> {
            if (tableCheck.isSelected()) {
                for (CheckTableData data : delayTableView.getItems()) {
                    data.setIsSelect(Boolean.TRUE);
                }
            } else {
                for (CheckTableData data : delayTableView.getItems()) {
                    data.setIsSelect(Boolean.FALSE);
                }
            }
        });

        this.delaySelectColumn.setGraphic(tableCheck);
        this.delaySelectColumn.setCellValueFactory(new PropertyValueFactory<>("isSelect"));
        this.delayNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.delaySelectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(delaySelectColumn));
        this.delaySelectColumn.setEditable(true);
        this.delayTableView.setEditable(true);

        Callback<ListView<TimeUnitEnum>, ListCell<TimeUnitEnum>> timeUnitComboCellFactory = (ListView<TimeUnitEnum> param) -> new TimeUnitComboCellFactory();
        this.timeUnitCombo.setCellFactory(timeUnitComboCellFactory);
        this.timeUnitCombo.setButtonCell(new TimeUnitComboCellFactory());
        this.timeUnitCombo.setItems(FXCollections.observableArrayList(Arrays.asList(AnalysisWorkFilterData.TimeUnitEnum.values())));
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof AnalysisWorkFilterData) {
            this.analysisWorkFilterData = (AnalysisWorkFilterData) argument;

            this.analyistTactFilterE.textProperty().bindBidirectional(this.analysisWorkFilterData.filterTactTimeEarliestProperty());
            this.analyistTactFilterS.textProperty().bindBidirectional(this.analysisWorkFilterData.filterTactTimeSlowestProperty());
            this.delayTableView.itemsProperty().bindBidirectional(this.analysisWorkFilterData.filterDelayReasonProperty());
            this.timeUnitCombo.setValue(this.analysisWorkFilterData.getTimeUnit());
            this.timeUnitCombo.valueProperty().bindBidirectional(this.analysisWorkFilterData.timeunitProperty());
            this.timeUnitCombo.setDisable(false);
        }
    }

}
