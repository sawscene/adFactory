/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.selectcompo;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.object.ObjectInfoEntity;

/**
 *
 * @author nar-nakamura
 */
public class ObjectListItem {

    private ObjectInfoEntity objectInfoEntity = new ObjectInfoEntity();

    private final LongProperty objectTypeIdProperty = new SimpleLongProperty();
    private final StringProperty objectTypeNameProperty = new SimpleStringProperty();
    private final StringProperty objectIdProperty = new SimpleStringProperty();
    private final StringProperty objectNameProperty = new SimpleStringProperty();

    public ObjectListItem(String objectTypeName, ObjectInfoEntity entity) {
        this.objectInfoEntity = entity;
        this.objectTypeIdProperty.setValue(entity.getFkObjectTypeId());
        this.objectTypeNameProperty.setValue(objectTypeName);
        this.objectIdProperty.setValue(entity.getObjectId());
        this.objectNameProperty.setValue(entity.getObjectName());
    }

    public LongProperty objectTypeIdProperty() {
        return this.objectTypeIdProperty;
    }

    public Long getObjectTypeId() {
        return this.objectTypeIdProperty.get();
    }

    public StringProperty objectTypeNameProperty() {
        return this.objectTypeNameProperty;
    }

    public String getObjectTypeName() {
        return this.objectTypeNameProperty.get();
    }

    public StringProperty objectIdProperty() {
        return this.objectIdProperty;
    }

    public String getObjectId() {
        return this.objectIdProperty.get();
    }

    public StringProperty objectNameProperty() {
        return this.objectNameProperty;
    }

    public String getObjectName() {
        return this.objectNameProperty.get();
    }

    public ObjectInfoEntity getObjectInfoEntity() {
        return this.objectInfoEntity;
    }
}
