/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 *
 * @author ke.yokoi
 */
public class CellButton<T> extends AbstractCell {

    private final Button button = new Button();

    public CellButton(Record record, StringProperty label, EventHandler<ActionEvent> action, Object userData) {
        super(record);
        button.textProperty().bindBidirectional(label);
        button.setOnAction(action);
        button.setUserData(userData);
    }

    @Override
    public void createNode() {
        super.setNode(button);
    }

}
