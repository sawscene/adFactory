/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.beans.property.StringProperty;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyEvent;

/**
 * 入力制限パスワードフィールド
 *
 * @author ek.mori
 * @version 1.6.1
 * @since 2017.01.20.Fri
 */
public class CellRegerxPassField extends AbstractCell{

    private final StringProperty stringData;
    private final String regex;

    public CellRegerxPassField(CellInterface abstractCellInterface, String regex, StringProperty stringData) {
        super(abstractCellInterface);
        this.regex = regex;
        this.stringData = stringData;
    }

    @Override
    public void createNode() {
        PasswordField textField = new PasswordField();
        stringData.bind(textField.textProperty());
        textField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            if (!event.getCharacter().matches(regex)) {
                event.consume();
            }
        });
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(regex)) {
                textField.setText(oldValue);
            }
        });
        super.setNode(textField);
    }
}
