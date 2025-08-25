/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.Objects;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author ke.yokoi
 */
public class CellNumericDoubleField extends AbstractCell {

    private final DoubleProperty doubleData;
    private final StringProperty stringData;
    private ChangeListener<String> actionListener = null;
    private final TextField textField = new TextField();

    public CellNumericDoubleField(CellInterface abstractCellInterface, DoubleProperty doubleData) {
        super(abstractCellInterface);
        this.doubleData = doubleData;
        this.stringData = new SimpleStringProperty();
    }

    public CellNumericDoubleField actionListener(ChangeListener<String> actionListener) {
        this.actionListener = actionListener;
        return this;
    }

    public CellNumericDoubleField(CellInterface abstractCellInterface, StringProperty stringData) {
        super(abstractCellInterface);
        this.doubleData = new SimpleDoubleProperty(Double.valueOf(stringData.get()));
        this.stringData = stringData;
    }

    @Override
    public void createNode() {
        textField.setText(String.valueOf(doubleData.get()));
        stringData.bind(textField.textProperty());
        if (Objects.nonNull(this.actionListener)) {
            textField.textProperty().addListener(this.actionListener);
        }
        textField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            doubleData.set(Double.valueOf(newValue));
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
