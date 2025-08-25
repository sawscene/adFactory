/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

/**
 *
 * @author e-mori
 */
public class CellRemoveButton extends AbstractCell {


    public CellRemoveButton(CellInterface abstractCellInterface) {
        super(abstractCellInterface);
    }

    @Override
    public void createNode() {
        Button removeButton = new Button("-");
        removeButton.setOnAction((ActionEvent e) -> {
            super.getCellInterface().removeCell();
        });
        super.setNode(removeButton);
    }

}
