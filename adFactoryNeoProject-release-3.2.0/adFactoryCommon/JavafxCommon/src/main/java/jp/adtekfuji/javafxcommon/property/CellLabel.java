/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

/**
 *
 * @author e-mori
 */
public class CellLabel extends AbstractCell {

    private final String text;

    public CellLabel(Record record, StringProperty textProperty) {
        super(record);
        this.text = textProperty.get();
    }

    public CellLabel(Record record, String text) {
        super(record);
        this.text = text;
    }

    @Override
    public void createNode() {
        Label label = new Label(text);
        super.setNode(label);
    }

}
