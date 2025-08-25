/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

/**
 *
 * @author e-mori
 */
public class CellCheckBox extends AbstractCell {

    private final String text;
    private final BooleanProperty data;
    private ChangeListener<Boolean> actionListner = null;
    private CellChackBoxLisner chackBoxLisner = null;
    private HPos hpos = null;

    public CellCheckBox(Record record, String text, BooleanProperty data) {
        super(record);
        this.text = text;
        this.data = data;
    }
    
    public CellCheckBox(Record record, String text, BooleanProperty data, HPos hpos) {
        super(record);
        this.text = text;
        this.data = data;
        this.hpos = hpos;
    }

    public CellCheckBox actionListner(CellChackBoxLisner chackBoxLisner) {
        this.chackBoxLisner = chackBoxLisner;
        return this;
    }

    /**
     * 選択切り替え時のリスナーを定義する
     * 
     * @param actionListener チェックが切り替わったときのリスナー
     * @return 
     */
    public CellCheckBox actionListener(ChangeListener<Boolean> actionListener) {
        this.actionListner = actionListener;
        return this;
    }

    @Override
    public void createNode() {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(data.get());
        if (Objects.nonNull(chackBoxLisner)) {
            checkBox.setOnAction((ActionEvent event) -> {
                chackBoxLisner.chackAction(this);
            });
        }
        if (Objects.nonNull(actionListner)) {
            checkBox.selectedProperty().addListener(actionListner);
        }
        
        if (Objects.nonNull(this.hpos)) {
            GridPane.setHalignment(checkBox, this.hpos);
        }

        data.bindBidirectional(checkBox.selectedProperty());
        super.setNode(checkBox);
    }

}
