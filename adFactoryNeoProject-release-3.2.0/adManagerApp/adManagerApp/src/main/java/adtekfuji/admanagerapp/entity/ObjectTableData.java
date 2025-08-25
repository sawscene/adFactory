/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.object.ObjectInfoEntity;

/**
 *
 * @author nar-nakamura
 */
public class ObjectTableData {

    private ObjectInfoEntity objectInfoEntity = new ObjectInfoEntity();

    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final StringProperty objectIdProperty = new SimpleStringProperty();
    private final StringProperty objectNameProperty = new SimpleStringProperty();

    private boolean isCreated = false;
    private boolean isEdited = false;
    private boolean isDeleted = false;

    public ObjectTableData() {
        this.objectInfoEntity = new ObjectInfoEntity();
        this.objectIdProperty.setValue("");
        this.objectNameProperty.setValue("");
        this.isCreated = true;
    }

    public ObjectTableData(ObjectInfoEntity entity) {
        this.objectInfoEntity = entity;
        this.objectIdProperty.setValue(entity.getObjectId());
        this.objectNameProperty.setValue(entity.getObjectName());
    }

    public boolean isCreated() {
        return this.isCreated;
    }

    public void setIsCreated(boolean value) {
        this.isCreated = value;
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

   public StringProperty objectIdProperty() {
        return this.objectIdProperty;
    }

    public String getObjectId() {
        return this.objectIdProperty.get();
    }

    public void setObjectId(String value) {
        if (!value.equals(objectIdProperty.get())) {
            this.objectIdProperty.set(value);
            this.isEdited = true;
        }
    }

    public StringProperty objectNameProperty() {
        return this.objectNameProperty;
    }

    public String getObjectName() {
        return this.objectNameProperty.get();
    }

    public void setObjectName(String value) {
        if (!value.equals(objectNameProperty.get())) {
            this.objectNameProperty.set(value);
            this.isEdited = true;
        }
    }

    public ObjectInfoEntity getObjectInfoEntity() {
        return this.objectInfoEntity;
    }

    public void setObjectInfoEntity(ObjectInfoEntity value) {
        this.objectInfoEntity = value;
    }
}
