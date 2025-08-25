/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorworkdeliveryplugin;

import adtekfuji.utility.DateUtils;
import javafx.beans.property.IntegerProperty;
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
    private final IntegerProperty currentGoal = new SimpleIntegerProperty(0);
    private final IntegerProperty actualNum = new SimpleIntegerProperty(0);
    private final IntegerProperty diff = new SimpleIntegerProperty(0);
    private final StringProperty progress = new SimpleStringProperty();
    private final WorkSetting workSetting;
    private final long taktTime;
    private Color fontColor = Color.WHITE;
    private Color backColor = Color.BLACK;

    public RowData(WorkSetting workSetting, long workTime) {
        this.workSetting = workSetting;
        this.name.set(this.workSetting.getTitle());
        this.progress.set(DateUtils.formatTaktTime(0));
        this.taktTime = 0 < this.workSetting.getPlanNum() ? workTime / this.workSetting.getPlanNum() : 0;
    }

    public StringProperty nameProperty() {
        return this.name;
    }

    public IntegerProperty currentGoalProperty() {
        return this.currentGoal;
    }

    public IntegerProperty actualNumProperty() {
        return this.actualNum;
    }

    public IntegerProperty diffProperty() {
        return this.diff;
    }

    public StringProperty progressProperty() {
        return this.progress;
    }

    public WorkSetting getSetting() {
        return this.workSetting;
    }

    public long getTaktTime() {
        return this.taktTime;
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
        return "RowData{" + "workSetting=" + workSetting + ", taktTime=" + taktTime + ", fontColor=" + fontColor + ", backColor=" + backColor + ", name=" + name + ", currentGoal=" + currentGoal +
               ", actualNum=" + actualNum + ", diff=" + diff + ", progress=" + progress + '}';
    }

}
