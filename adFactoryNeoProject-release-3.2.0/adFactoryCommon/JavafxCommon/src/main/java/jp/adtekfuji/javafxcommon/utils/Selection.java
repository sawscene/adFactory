/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.utils;

import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * データを選択するためのクラス
 *
 * @author s-heya
 * @param <E>
 */
public class Selection<E> {

    private final SimpleBooleanProperty selected = new SimpleBooleanProperty(Boolean.FALSE);
    private String name;
    private E value;
    private IntegerProperty id;

    public Selection(boolean selected, String name, E value, Integer id) {
        this.selected.setValue(selected);
        this.name = name;
        this.value = value;
        this.id = new SimpleIntegerProperty(id);
    }

    public Selection(boolean selected, String name, E value) {
        this.selected.setValue(selected);
        this.name = name;
        this.value = value;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public Boolean isSelected() {
        return this.selected.getValue();
    }

    public void setSelected(Boolean selected) {
        this.selected.setValue(selected);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public IntegerProperty idProperty() {
        if (Objects.isNull(this.id)) {
            this.id = new SimpleIntegerProperty(0);
        }
        return this.id;
    }

    public int getId() {
        return this.idProperty().get();
    }

    public void setId(Integer id) {
        this.idProperty().set(id);
    }

    @Override
    public String toString() {
        return "RowData{" + "selected=" + selected + ", name=" + name + ", value=" + value + ", id=" + id + '}';
    }
}
