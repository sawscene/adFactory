/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author ke.yokoi
 */
public class CellRegexTextField extends AbstractCell {

    public enum RegexType {
        STRING,
        CHARACTER;
    }

    private final StringProperty stringData;
    private final String regex;
    private RegexType regexType = RegexType.STRING;

    public CellRegexTextField(CellInterface abstractCellInterface, String regex, StringProperty stringData) {
        super(abstractCellInterface);
        this.regex = regex;
        this.stringData = stringData;
    }

    public CellRegexTextField(CellInterface abstractCellInterface, String regex, StringProperty stringData, RegexType regexType) {
        super(abstractCellInterface);
        this.regex = regex; 
        this.regexType = regexType;
        this.stringData = stringData;
    }

    @Override
    public void createNode() {
        TextField textField = new TextField(stringData.get());
        stringData.bind(textField.textProperty());
        switch (regexType) {
            case STRING:
                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches(regex)) {
                        textField.setText(oldValue);
                    }
                });
                break;
            case CHARACTER:
            default:
                textField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
                    if (!event.getCharacter().matches(regex)) {
                        event.consume();
                    }
                });
                break;
        }
        super.setNode(textField);
    }
}
