/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.scene.control.Label;

/**
 *
 * @author ke.yokoi
 */
public class CellAutoNumberLabel extends AbstractCell {

    private final Label label = new Label();
    private final String labelName;

    public CellAutoNumberLabel(Record record, String labelName, int number) {
        super(record);
        this.labelName = labelName;
        label.setText(labelName + String.valueOf(number));
    }

    public void updateNumber(int number) {
        label.setText(labelName + String.valueOf(number));
    }

    @Override
    public void createNode() {
        super.setNode(label);
    }

}
