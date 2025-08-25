/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectWorkInfoEntity;

/**
 *
 * @author s-maeda
 */
public class WorkTableData {

    private IndirectWorkInfoEntity workInfoEntity = new IndirectWorkInfoEntity();

    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final StringProperty workNumberProperty = new SimpleStringProperty();
    private final StringProperty workNameProperty = new SimpleStringProperty();

    private boolean isAdded = false;
    private boolean isEdited = false;
    private boolean isDeleted = false;

    public WorkTableData() {
        this.workInfoEntity = new IndirectWorkInfoEntity();
        this.workNumberProperty.setValue("");
        this.workNameProperty.setValue("");
        this.isAdded = true;
    }

    public WorkTableData(IndirectWorkInfoEntity entity) {
        this.workInfoEntity = entity;
        this.workNumberProperty.setValue(entity.getWorkNumber());
        this.workNameProperty.setValue(entity.getWorkName());
    }

    public boolean isAdded() {
        return this.isAdded;
    }

    public void setIsCreated(boolean value) {
        this.isAdded = value;
    }

    public boolean isEdited() {
        return this.isEdited;
    }

    public void setIsEdited(boolean value) {
        this.isEdited = value;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public void setIsDeleted(boolean value) {
        this.isDeleted = value;
    }

    public BooleanProperty selectedProperty() {
        return this.selected;
    }

    public Boolean isSelected() {
        return this.selected.get();
    }

    public void setSelected(Boolean value) {
        this.selected.set(value);
    }

   public StringProperty workNumberProperty() {
        return this.workNumberProperty;
    }

    public String getWorkNumber() {
        return this.workNumberProperty.get();
    }

    public void setWorkNumber(String value) {
        if (!value.equals(workNumberProperty.get())) {
            this.workNumberProperty.set(value);
            this.isEdited = true;
        }
    }

    public StringProperty workNameProperty() {
        return this.workNameProperty;
    }

    public String getWorkName() {
        return this.workNameProperty.get();
    }

    public void setWorkName(String value) {
        if (!value.equals(workNameProperty.get())) {
            this.workNameProperty.set(value);
            this.isEdited = true;
        }
    }

    public IndirectWorkInfoEntity getWorkInfoEntity() {
        return this.workInfoEntity;
    }

    public void setWorkInfoEntity(IndirectWorkInfoEntity value) {
        this.workInfoEntity = value;
    }
}
