/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this  file, choose Tools | s
 * and open the  in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.unit;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;

/**
 * ユニットプロパティ情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.26.Wen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "unitProperty")
public class UnitPropertyInfoEntity {

    private static final long serialVersionUID = 1L;

    private LongProperty unitPropertyIdProperty;
    private LongProperty fkUnitIdProperty;
    private StringProperty unitPropertyNameProperty;
    private ObjectProperty<CustomPropertyTypeEnum> unitPropertyTypeProperty;
    private StringProperty unitPropertyValueProperty;
    private IntegerProperty unitPropertyOrderProperty;

    @XmlElement(required = true)
    private Long unitPropertyId;
    @XmlElement
    private Long fkUnitId;
    @XmlElement
    private String unitPropertyName;
    @XmlElement
    private CustomPropertyTypeEnum unitPropertyType;
    @XmlElement
    private String unitPropertyValue;
    @XmlElement
    private Integer unitPropertyOrder;

    public UnitPropertyInfoEntity() {
    }

    public UnitPropertyInfoEntity(Long unitPropertyId, Long fkUnitId, String unitPropertyName, CustomPropertyTypeEnum unitPropertyType, String unitPropertyValue, Integer unitPropertyOrder) {
        this.unitPropertyId = unitPropertyId;
        this.fkUnitId = fkUnitId;
        this.unitPropertyName = unitPropertyName;
        this.unitPropertyType = unitPropertyType;
        this.unitPropertyValue = unitPropertyValue;
        this.unitPropertyOrder = unitPropertyOrder;
    }

    public UnitPropertyInfoEntity(UnitPropertyInfoEntity in) {
        this.fkUnitId = in.fkUnitId;
        this.unitPropertyName = in.unitPropertyName;
        this.unitPropertyType = in.unitPropertyType;
        this.unitPropertyValue = in.unitPropertyValue;
        this.unitPropertyOrder = in.unitPropertyOrder;
    }

    public LongProperty unitPropertyIdProperty() {
        if (Objects.isNull(unitPropertyIdProperty)) {
            unitPropertyIdProperty = new SimpleLongProperty(unitPropertyId);
        }
        return unitPropertyIdProperty;
    }

    public LongProperty fkUnitIdProperty() {
        if (Objects.isNull(fkUnitIdProperty)) {
            fkUnitIdProperty = new SimpleLongProperty(fkUnitId);
        }
        return fkUnitIdProperty;
    }

    public StringProperty unitPropertyNameProperty() {
        if (Objects.isNull(unitPropertyNameProperty)) {
            unitPropertyNameProperty = new SimpleStringProperty(unitPropertyName);
        }
        return unitPropertyNameProperty;
    }

    public ObjectProperty<CustomPropertyTypeEnum> unitPropertyTypeProperty() {
        if (Objects.isNull(unitPropertyTypeProperty)) {
            unitPropertyTypeProperty = new SimpleObjectProperty<>(unitPropertyType);
        }
        return unitPropertyTypeProperty;
    }

    public StringProperty unitPropertyValueProperty() {
        if (Objects.isNull(unitPropertyValueProperty)) {
            unitPropertyValueProperty = new SimpleStringProperty(unitPropertyValue);
        }
        return unitPropertyValueProperty;
    }

    public IntegerProperty unitPropertyOrderProperty() {
        if (Objects.isNull(unitPropertyOrderProperty)) {
            unitPropertyOrderProperty = new SimpleIntegerProperty(unitPropertyOrder);
        }
        return unitPropertyOrderProperty;
    }

    public Long getUnitPropertyId() {
        if (Objects.nonNull(unitPropertyIdProperty)) {
            return unitPropertyIdProperty.get();
        }
        return unitPropertyId;
    }

    public void setUnitPropertyId(Long unitPropertyId) {
        if (Objects.nonNull(unitPropertyIdProperty)) {
            unitPropertyIdProperty.set(unitPropertyId);
        } else {
            this.unitPropertyId = unitPropertyId;
        }
    }

    public Long FkUnitIdProperty() {
        if (Objects.nonNull(fkUnitIdProperty)) {
            return fkUnitIdProperty.get();
        }
        return fkUnitId;
    }

    public void setFkMasterId(Long fkUnitId) {
        if (Objects.nonNull(fkUnitIdProperty)) {
            fkUnitIdProperty.set(fkUnitId);
        } else {
            this.fkUnitId = fkUnitId;
        }
    }

    public String getUnitPropertyName() {
        if (Objects.nonNull(unitPropertyNameProperty)) {
            return unitPropertyNameProperty.get();
        }
        return unitPropertyName;
    }

    public void setUnitPropertyName(String unitPropertyName) {
        if (Objects.nonNull(unitPropertyNameProperty)) {
            unitPropertyNameProperty.set(unitPropertyName);
        } else {
            this.unitPropertyName = unitPropertyName;
        }
    }

    public CustomPropertyTypeEnum getUnitPropertyType() {
        if (Objects.nonNull(unitPropertyTypeProperty)) {
            return unitPropertyTypeProperty.get();
        }
        return unitPropertyType;
    }

    public void setUnitPropertyType(CustomPropertyTypeEnum unitPropertyType) {
        if (Objects.nonNull(unitPropertyTypeProperty)) {
            unitPropertyTypeProperty.set(unitPropertyType);
        } else {
            this.unitPropertyType = unitPropertyType;
        }
    }

    public String getUnitPropertyValue() {
        if (Objects.nonNull(unitPropertyValueProperty)) {
            return unitPropertyValueProperty.get();
        }
        return unitPropertyValue;
    }

    public void setUnitPropertyValue(String unitPropertyValue) {
        if (Objects.nonNull(unitPropertyValueProperty)) {
            unitPropertyValueProperty.set(unitPropertyValue);
        } else {
            this.unitPropertyValue = unitPropertyValue;
        }
    }

    public Integer getUnitPropertyOrder() {
        if (Objects.nonNull(unitPropertyOrderProperty)) {
            return unitPropertyOrderProperty.get();
        }
        return unitPropertyOrder;
    }

    public void setUnitPropertyOrder(Integer unitPropertyOrder) {
        if (Objects.nonNull(unitPropertyOrderProperty)) {
            unitPropertyOrderProperty.set(unitPropertyOrder);
        } else {
            this.unitPropertyOrder = unitPropertyOrder;
        }
    }

    public void updateMember() {
        this.unitPropertyId = getUnitPropertyId();
        this.fkUnitId = FkUnitIdProperty();
        this.unitPropertyName = getUnitPropertyName();
        this.unitPropertyType = getUnitPropertyType();
        this.unitPropertyValue = getUnitPropertyValue();
        this.unitPropertyOrder = getUnitPropertyOrder();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.unitPropertyId ^ (this.unitPropertyId >>> 32));
        hash = 83 * hash + (int) (this.fkUnitId ^ (this.fkUnitId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.unitPropertyName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UnitPropertyInfoEntity other = (UnitPropertyInfoEntity) obj;
        if (!Objects.equals(this.getUnitPropertyId(), other.getUnitPropertyId())) {
            return false;
        }
        if (!Objects.equals(this.FkUnitIdProperty(), other.FkUnitIdProperty())) {
            return false;
        }
        return Objects.equals(this.getUnitPropertyName(), other.getUnitPropertyName());
    }

    @Override
    public String toString() {
        return "UnitPropertyInfoEntity{" + "unitPropertyId=" + getUnitPropertyId() + ", fkUnitId=" + FkUnitIdProperty() + ", unitPropertyName=" + getUnitPropertyName() + ", unitPropertyType=" + getUnitPropertyType() + ", unitPropertyValue=" + getUnitPropertyValue() + ", unitPropertyOrder=" + getUnitPropertyOrder() + '}';
    }
}
