/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.dialog.entity;

import java.util.List;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ユニット階層ツリー
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.09.Wen
 */
public class SelectDialogEntity<E> {

    private ObjectProperty<ObservableList<E>> multiSelectItemsProperty;
    private ObjectProperty<E> singleSelectItemProperty;

    private ObservableList<E> multiSelectItems = FXCollections.observableArrayList();
    private E singleSelectItem = null;

    private String uribase = null;

    public SelectDialogEntity() {
    }
    
    public SelectDialogEntity(SelectDialogEntity<E> in){
        this.singleSelectItem = in.singleSelectItem;
        this.multiSelectItems = in.multiSelectItems;
        this.uribase = in.uribase;
    }

    public SelectDialogEntity multiSelectItems(ObservableList<E> multiSelectItems) {
        this.multiSelectItems = multiSelectItems;
        return this;
    }

    public SelectDialogEntity singleSelectItem(E singleSelectItem) {
        this.singleSelectItem = singleSelectItem;
        return this;
    }

    public SelectDialogEntity uri(String uriBase) {
        this.uribase = uriBase;
        return this;
    }

    public ObjectProperty<ObservableList<E>> multiSelectItemsProperty() {
        if (Objects.isNull(multiSelectItemsProperty)) {
            multiSelectItemsProperty = new SimpleObjectProperty<>(multiSelectItems);
        }
        return multiSelectItemsProperty;
    }

    public ObjectProperty<E> singleSelectItemProperty() {
        if (Objects.isNull(singleSelectItemProperty)) {
            singleSelectItemProperty = new SimpleObjectProperty<>(singleSelectItem);
        }
        return singleSelectItemProperty;
    }

    public List<E> getItems() {
        if (Objects.nonNull(multiSelectItemsProperty)) {
            return multiSelectItemsProperty.get();
        }
        return multiSelectItems;
    }

    public void setItems(ObservableList<E> multiSelectItems) {
        if (Objects.nonNull(multiSelectItemsProperty)) {
            multiSelectItemsProperty.set(multiSelectItems);
        } else {
            this.multiSelectItems = multiSelectItems;
        }
    }

    public E getItem() {
        if (Objects.nonNull(singleSelectItemProperty)) {
            return singleSelectItemProperty.get();
        }
        return singleSelectItem;
    }

    public void setItem(E singleSelectItem) {
        if (Objects.nonNull(singleSelectItemProperty)) {
            singleSelectItemProperty.set(singleSelectItem);
        } else {
            this.singleSelectItem = singleSelectItem;
        }
    }

    public String getUribase() {
        return uribase;
    }

    public void setUribase(String uribase) {
        this.uribase = uribase;
    }
}
