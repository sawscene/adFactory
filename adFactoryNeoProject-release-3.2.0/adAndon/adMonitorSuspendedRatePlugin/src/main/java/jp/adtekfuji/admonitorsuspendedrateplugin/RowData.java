/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorsuspendedrateplugin;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import jp.adtekfuji.andon.property.WorkSetting;

/**
 * Rowデータ
 *
 * @author s-heya
 */
public class RowData {

    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty suspendedNum = new SimpleIntegerProperty(0);
    private final DoubleProperty suspendedRate = new SimpleDoubleProperty(0.0);
    private final WorkSetting workSetting;
    private int actualNum = 0;
    private Color fontColor = Color.WHITE;
    private Color backColor = Color.BLACK;

    public RowData(WorkSetting workSetting) {
        this.workSetting = workSetting;
        this.name.set(this.workSetting.getTitle());
    }

    public StringProperty nameProperty() {
        return this.name;
    }

    public IntegerProperty suspendedNumProperty() {
        return this.suspendedNum;
    }

    public DoubleProperty suspendedRateProperty() {
        return this.suspendedRate;
    }

    public WorkSetting getSetting() {
        return this.workSetting;
    }

    public int getActualNum() {
        return actualNum;
    }

    public void setActualNum(int actualNum) {
        this.actualNum = actualNum;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public Color getBackColor() {
        return backColor;
    }

    public void setBackColor(Color backColor) {
        this.backColor = backColor;
    }

    @Override
    public String toString() {
        return "RowData{" + "name=" + name + ", suspendedNum=" + suspendedNum + ", suspendedRate=" + suspendedRate + ", workSetting=" + workSetting + ", actualNum=" + actualNum +
                ", fontColor=" + fontColor + ", backColor=" + backColor + '}';
    }
}
