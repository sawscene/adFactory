/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 製番の行情報
 *
 * @author kentarou.suzuki
 */
public class ControlNoRowInfo {

    private final StringProperty controlNo = new SimpleStringProperty();   // 製番プロパティ
    private final BooleanProperty selected = new SimpleBooleanProperty();   // 選択状態プロパティ
    
    /**
     * コンストラクタ
     */
    public ControlNoRowInfo() {
        this.controlNo.set("");
        this.selected.set(false);
    }

    /**
     * コンストラクタ
     *
     * @param controlNo 製番
     * @param selected true: 選択済み、false: 未選択
     */
    public ControlNoRowInfo(String controlNo, Boolean selected) {
        this.controlNo.set(controlNo);
        this.selected.set(selected);
    }

    /**
     * 選択状態プロパティを取得する。
     * 
     * @return 選択状態プロパティ
     */
    public BooleanProperty selectedProperty() {
        return this.selected;
    }

    /**
     * 選択状態を取得する。
     * 
     * @return true: 選択済み、false: 未選択
     */
    public Boolean isSelected() {
        return this.selected.get();
    }

    /**
     * 選択状態を設定する。
     * 
     * @param selected true: 選択済み、false: 未選択
     */
    public void setSelected(Boolean selected) {
        this.selected.set(selected);
    }

    /**
     * 製番プロパティを取得する。
     * 
     * @return 製番プロパティ
     */
    public StringProperty controlNoProperty() {
        return this.controlNo;
    }

    /**
     * 製番を取得する。
     * 
     * @return true: 選択済み、false: 未選択
     */
    public String getControlNo() {
        return this.controlNo.get();
    }

    /**
     * 製番を設定する。
     * 
     * @param controlNo 製番
     */
    public void setControlNo(String controlNo) {
        this.controlNo.set(controlNo);
    }
}
