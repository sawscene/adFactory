/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author ke.yokoi
 */
public class CellNumericField extends AbstractCell {

    private final IntegerProperty integerData;
    private final StringProperty stringData;
    private ChangeListener<String> actionListner = null;
    private final TextField textField = new TextField();

    public CellNumericField(CellInterface abstractCellInterface, IntegerProperty integerData) {
        super(abstractCellInterface);
        this.integerData = integerData;
        this.stringData = new SimpleStringProperty();
    }

    public CellNumericField actionListner(ChangeListener<String> actionListner) {
        this.actionListner = actionListner;
        return this;
    }

    public CellNumericField(CellInterface abstractCellInterface, StringProperty stringData) {
        super(abstractCellInterface);
        this.integerData = new SimpleIntegerProperty(Integer.valueOf(stringData.get()));
        this.stringData = stringData;
    }

    @Override
    public void createNode() {
        textField.setText(String.valueOf(integerData.get()));
        stringData.bind(textField.textProperty());
        if (Objects.nonNull(this.actionListner)) {
            textField.textProperty().addListener(this.actionListner);
        }
        textField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            integerData.set(Integer.valueOf(newValue));
        });
        textField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches("\\d")) {
                event.consume();
            }
        });
        super.setNode(textField);
    }

    public BooleanProperty disableProperty() {
        return textField.disableProperty();
    }
}
