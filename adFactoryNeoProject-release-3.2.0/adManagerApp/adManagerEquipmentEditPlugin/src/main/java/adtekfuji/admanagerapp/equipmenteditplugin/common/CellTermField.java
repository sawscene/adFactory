/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.equipmenteditplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.util.Optional;
import java.util.ResourceBundle;
import jp.adtekfuji.adFactory.enumerate.TermUnitEnum;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.enumeration.Verifier;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.CellInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 数値をテキストフィールドで、その単位(日・週・月・年)をコンボボックスで入力するセル
 *
 * @author fu-kato
 */
class CellTermField extends AbstractCell {
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");;

    private final static Logger logger = LogManager.getLogger();
    private StringProperty valueProperty;
    private ObjectProperty<TermUnitEnum> typeProperty;

    private final GridPane grid = new GridPane();

    public CellTermField(CellInterface cell) {
        super(cell);
    }

    public CellTermField(CellInterface cell, StringProperty valueProperty) {
        super(cell);
        this.valueProperty = valueProperty;
    }

    void setCycleValue(StringProperty equipmentSettingValueProperty) {
        this.valueProperty = equipmentSettingValueProperty;
    }

    void setCycleType(ObjectProperty equipmentSettingValueProperty) {
        this.typeProperty = equipmentSettingValueProperty;
    }

    @Override
    public void createNode() {
        final RestrictedTextField text = new RestrictedTextField(String.valueOf(this.valueProperty.getValue()));
        text.setVerifier(Verifier.NATURAL_ONLY);
        text.setPrefWidth(170);
        text.setMinLimit(1);
        text.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches("\\d")) {
                event.consume();
            }
        });
        text.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            valueProperty.setValue(newValue);
        });

        final ComboBox<TermUnitEnum> combo = new ComboBox<>();
        combo.getItems().addAll(TermUnitEnum.values());
        combo.getSelectionModel().select(Optional.ofNullable(typeProperty.getValue()).orElse(TermUnitEnum.DAYLY));
        combo.valueProperty().addListener((ObservableValue<? extends TermUnitEnum> observable, TermUnitEnum oldValue, TermUnitEnum newValue) -> {
            this.typeProperty.setValue(newValue);
        });
        combo.setConverter(new StringConverter<TermUnitEnum>() {
            @Override
            public String toString(TermUnitEnum object) {
                return LocaleUtils.getString(object.getValue());
            }

            @Override
            public TermUnitEnum fromString(String string) {
                return null;
            }
        });

        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        c1.setPercentWidth(50);
        c2.setPercentWidth(50);

        grid.getChildren().clear();
        grid.getColumnConstraints().addAll(c1, c2);
        grid.add(text, 0, 0);
        grid.add(combo, 1, 0);

        super.setNode(grid);
    }

    public BooleanProperty disableProperty() {
        return grid.disableProperty();
    }
}
