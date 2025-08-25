/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;

/**
 *
 * @author ta.ito
 */
public class CellTextArea extends AbstractCell {

    private final StringProperty stringData;

    public CellTextArea(CellInterface abstractCellInterface, StringProperty stringData) {
        super(abstractCellInterface);
        this.stringData = stringData;
    }

    @Override
    public void createNode() {
        TextArea textArea = new TextArea(stringData.get());
        stringData.bindBidirectional(textArea.textProperty());
        super.setNode(textArea);
    }
}
