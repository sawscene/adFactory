/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

/**
 *
 * @author e-mori
 */
public class CellColorPicker extends AbstractCell {

    private final ObjectProperty<Color> data;

    public CellColorPicker(Record record, ObjectProperty<Color> data) {
        super(record);
        this.data = data;
    }

    //色のバインドができない
    @Override
    public void createNode() {
        if (data.get() == null) {
            data.set(Color.WHITE);
        }
        ColorPicker colorPicker = new ColorPicker(data.get());
        data.bindBidirectional(colorPicker.valueProperty());
        super.setNode(colorPicker);
    }

}
