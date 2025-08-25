/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.beans.property.StringProperty;
import javafx.scene.control.PasswordField;

/**
 *
 * @author e-mori
 */
public class CellPassField extends AbstractCell {

    private final StringProperty stringData;
    private final Boolean isPassFieldDisable;

    public CellPassField(CellInterface abstractCellInterface, StringProperty stringData) {
        super(abstractCellInterface);
        this.stringData = stringData;
        this.isPassFieldDisable = false;
    }

    public CellPassField(CellInterface abstractCellInterface, StringProperty stringData, Boolean isPassFieldDisable) {
        super(abstractCellInterface);
        this.stringData = stringData;
        this.isPassFieldDisable = isPassFieldDisable;
    }

    @Override
    public void createNode() {
        PasswordField passwordField = new PasswordField();
        stringData.bindBidirectional(passwordField.textProperty());
        passwordField.setDisable(isPassFieldDisable);
        super.setNode(passwordField);
    }
}
