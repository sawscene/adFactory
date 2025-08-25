/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;

/**
 *
 * @author e-mori
 */
public class CellRecordCheckBox extends AbstractCell implements PropertyChangeListener {

    //CheckBox recordCheckBox;
    private final BooleanProperty data;

    public CellRecordCheckBox(CellInterface abstractCellInterface, BooleanProperty data) {
        super(abstractCellInterface);
        this.data = data;
    }

    @Override
    public void createNode() {
        CheckBox checkBox = new CheckBox();
        data.bindBidirectional(checkBox.selectedProperty());
        super.setNode(checkBox);
    }

    @Override
    public void onSelected(boolean isSelected) {
        this.data.setValue(isSelected);
    }

    @Override
    public void onChange() {

    }
}
