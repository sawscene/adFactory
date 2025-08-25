/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.equipmenteditplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import jp.adtekfuji.javafxcommon.PropertyBindEntity;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellSwitchButton;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * 校正情報テーブル作成ファクトリ
 *
 * @author fu-kato
 */
public class CalibrationSettingRecordFactory extends AbstractRecordFactory<Map<String, Property>> {
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    // 無効にする都合上セルをフィールドに置く
    private CellSwitchButton cellSwitch;
    private CellDatePicker cellDatePicker;
    private CellTermField cellTerm;
    private CellLabeledTextField cellWarning;

    private final Node disableTarget;
    private boolean editable;

    public CalibrationSettingRecordFactory(Table table, LinkedList<Map<String, Property>> entities, Node disableTarget, boolean editable) {
        super(table, entities);
        this.disableTarget = disableTarget;
        this.editable = editable;
    }

    @Override
    protected Record createRecord(Map<String, Property> entity) {
        Record record = new Record(super.getTable(), false);

        final LinkedList<AbstractCell> cells = new LinkedList<>();

        // Mapを擬似的なペア<String, Property>として作成したのでMapに格納されているのは常にひとつのみ
        final String key = entity.entrySet().stream().map(entry -> entry.getKey()).findAny().get();
        final Property value = entity.entrySet().stream().map(entry -> entry.getValue()).findAny().get();

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

        switch (key) {
            case "enableCalib": {
                cells.add(new CellLabel(record, new SimpleStringProperty(LocaleUtils.getString("key.CalibEnable"))).setPrefWidth(200.0).addStyleClass("ContentTextBox"));

                cellSwitch = new CellSwitchButton(record, ((BooleanProperty) value));
                cells.add(cellSwitch.setPrefWidth(200.0));

                cellSwitch.switchOnProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    cellDatePicker.disableProperty().setValue(!newValue);
                    cellTerm.disableProperty().setValue(!newValue);
                    cellWarning.disableProperty().setValue(!newValue);
                    disableTarget.disableProperty().setValue(!(editable && newValue));
                });

                break;
            }
            case "calibDate": {
                cells.add(new CellLabel(record, new SimpleStringProperty(LocaleUtils.getString("key.CalibNextDate"))).setPrefWidth(200.0).addStyleClass("ContentTextBox"));

                cellDatePicker = new CellDatePicker(record, (ObjectProperty<LocalDate>) value);
                cellDatePicker.setPrefWidth(200.0).addStyleClass("ContentTextBox").setDisable(!cellSwitch.booleanProperty().getValue());
                cells.add(cellDatePicker);
                break;
            }
            case "warningDays":
                cells.add(new CellLabel(record, new SimpleStringProperty(LocaleUtils.getString("key.CalibWarningDays"))).setPrefWidth(200.0).addStyleClass("ContentTextBox"));

                cellWarning = new CellLabeledTextField(record, (StringProperty) value, LocaleUtils.getString("key.CalibWarningDaysUnit"));
                cellWarning.setPrefWidth(200.0).addStyleClass("ContentTextBox").setDisable(!cellSwitch.booleanProperty().getValue());
                cells.add(cellWarning);
                break;

            case "cycleType":
                if (Objects.isNull(cellTerm)) {
                    cells.add(new CellLabel(record, new SimpleStringProperty(LocaleUtils.getString("key.CalibTerm"))).setPrefWidth(200.0).addStyleClass("ContentTextBox"));
                    cellTerm = new CellTermField(record);
                    cellTerm.setPrefWidth(200.0).addStyleClass("ContentTextBox").setDisable(!cellSwitch.booleanProperty().getValue());
                    cells.add(cellTerm);
                }
                cellTerm.setCycleType((ObjectProperty) value);
                break;

            case "cycleValue":
                if (Objects.isNull(cellTerm)) {
                    cells.add(new CellLabel(record, new SimpleStringProperty(LocaleUtils.getString("key.CalibTerm"))).setPrefWidth(200.0).addStyleClass("ContentTextBox"));
                    cellTerm = new CellTermField(record);
                    cellTerm.setPrefWidth(200.0).addStyleClass("ContentTextBox").setDisable(!cellSwitch.booleanProperty().getValue());
                    cells.add(cellTerm);
                }
                cellTerm.setCycleValue((StringProperty) value);
                break;

            case "prevCalibDate": {
                cells.add(new CellLabel(record, new SimpleStringProperty(LocaleUtils.getString("key.CalibLastDate"))).setPrefWidth(200.0).addStyleClass("ContentTextBox"));
                cells.add(new CellTextField(record, (StringProperty) value, true).setPrefWidth(240.0).addStyleClass("ContentTextBox"));
                break;
            }
            case "calibInspector":
                cells.add(new CellLabel(record, new SimpleStringProperty(LocaleUtils.getString("key.CalibPerson"))).setPrefWidth(200.0).addStyleClass("ContentTextBox"));
                cells.add(new CellTextField(record, (StringProperty) value, true).setPrefWidth(240.0).addStyleClass("ContentTextBox"));
                break;
        }

        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return PropertyBindEntity.class;
    }

}
