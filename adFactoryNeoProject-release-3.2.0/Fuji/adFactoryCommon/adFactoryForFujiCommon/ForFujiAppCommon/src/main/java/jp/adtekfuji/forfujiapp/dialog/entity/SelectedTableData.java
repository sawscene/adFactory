/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.dialog.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * 複数選択用テーブルデータ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.7.Wen
 */
public class SelectedTableData<E> {

    private SimpleBooleanProperty isSelect = new SimpleBooleanProperty(Boolean.FALSE);
    private String name;
    private E item;

    public SelectedTableData(String name, E item) {
        this.name = name;
        this.item = item;
    }

    public BooleanProperty isSelectProperty() {
        return isSelect;
    }

    public void setIsSelectProperty(SimpleBooleanProperty property) {
        this.isSelect = property;
    }

    public Boolean getIsSelect() {
        return isSelect.getValue();
    }

    public String getName() {
        return name;
    }

    public E getItem() {
        return item;
    }

    public void setIsSelect(Boolean isSelect) {
        this.isSelect.setValue(isSelect);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItem(E item) {
        this.item = item;
    }

}
