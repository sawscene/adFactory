/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.workflowmodel;


import adtekfuji.locale.LocaleUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

/**
 *
 * @author ta.ito
 */
public class EndCell extends CellBase {

    private CellBase prevCell = null;

    public EndCell() {
        super(false);

        Label label = new Label(LocaleUtils.getString("key.EndFlow"));
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(72.0, 72.0);
        label.getStyleClass().add("cellBorder");
        cellPane.getChildren().add(0, label);
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void setChecked(boolean value) {
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public void setSelected(boolean selectedCellBase) {
    }

    @Override
    public CellBase getPrevCell() {
        return prevCell;
    }

    @Override
    public void setPrevCell(CellBase prevCell) {
        this.prevCell = prevCell;
    }

    @Override
    public void setScale(double value) {
        Label label = (Label) cellPane.getChildren().get(0);
        label.setPrefSize(72.0 * value, 72.0 * value);
    }

    @Override
    public double getScale() {
        Label label = (Label) cellPane.getChildren().get(0);
        return label.getPrefHeight() / 72.0;
    }
}
