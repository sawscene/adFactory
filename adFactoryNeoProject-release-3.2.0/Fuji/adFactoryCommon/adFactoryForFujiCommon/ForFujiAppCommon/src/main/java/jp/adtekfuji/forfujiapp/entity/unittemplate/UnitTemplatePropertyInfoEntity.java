/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.unittemplate;

import java.io.Serializable;
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
 * ユニットテンプレートプロパティ情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.26.Wen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "unittemplateProperty")
public class UnitTemplatePropertyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty unitTemplatePropertyIdProperty;
    private LongProperty fkUnitTemplateIdProperty;
    private StringProperty unitTemplatePropertyNameProperty;
    private ObjectProperty<CustomPropertyTypeEnum> unitTemplatePropertyTypeProperty;
    private StringProperty unitTemplatePropertyValueProperty;
    private IntegerProperty unitTemplatePropertyOrderProperty;

    @XmlElement(required = true)
    private Long unitTemplatePropertyId;
    @XmlElement
    private Long fkUnitTemplateId;
    @XmlElement
    private String unitTemplatePropertyName;
    @XmlElement
    private CustomPropertyTypeEnum unitTemplatePropertyType;
    @XmlElement
    private String unitTemplatePropertyValue;
    @XmlElement
    private Integer unitTemplatePropertyOrder;

    public UnitTemplatePropertyInfoEntity() {
    }

    public UnitTemplatePropertyInfoEntity(Long unitTemplatePropertyId, Long fkUnitTemplateId, String unitTemplatePropertyName, CustomPropertyTypeEnum unitTemplatePropertyType, String unitTemplatePropertyValue, Integer unitTemplatePropertyOrder) {
        this.unitTemplatePropertyId = unitTemplatePropertyId;
        this.fkUnitTemplateId = fkUnitTemplateId;
        this.unitTemplatePropertyName = unitTemplatePropertyName;
        this.unitTemplatePropertyType = unitTemplatePropertyType;
        this.unitTemplatePropertyValue = unitTemplatePropertyValue;
        this.unitTemplatePropertyOrder = unitTemplatePropertyOrder;
    }

    public UnitTemplatePropertyInfoEntity(UnitTemplatePropertyInfoEntity in) {
        this.fkUnitTemplateId = in.fkUnitTemplateId;
        this.unitTemplatePropertyName = in.unitTemplatePropertyName;
        this.unitTemplatePropertyType = in.unitTemplatePropertyType;
        this.unitTemplatePropertyValue = in.unitTemplatePropertyValue;
        this.unitTemplatePropertyOrder = in.unitTemplatePropertyOrder;
    }

    public LongProperty unitTemplatePropertyIdProperty() {
        if (Objects.isNull(unitTemplatePropertyIdProperty)) {
            unitTemplatePropertyIdProperty = new SimpleLongProperty(unitTemplatePropertyId);
        }
        return unitTemplatePropertyIdProperty;
    }

    public LongProperty fkUnitTemplateIdProperty() {
        if (Objects.isNull(fkUnitTemplateIdProperty)) {
            fkUnitTemplateIdProperty = new SimpleLongProperty(fkUnitTemplateId);
        }
        return fkUnitTemplateIdProperty;
    }

    public StringProperty unitTemplatePropertyNameProperty() {
        if (Objects.isNull(unitTemplatePropertyNameProperty)) {
            unitTemplatePropertyNameProperty = new SimpleStringProperty(unitTemplatePropertyName);
        }
        return unitTemplatePropertyNameProperty;
    }

    public ObjectProperty<CustomPropertyTypeEnum> unitTemplatePropertyTypeProperty() {
        if (Objects.isNull(unitTemplatePropertyTypeProperty)) {
            unitTemplatePropertyTypeProperty = new SimpleObjectProperty<>(unitTemplatePropertyType);
        }
        return unitTemplatePropertyTypeProperty;
    }

    public StringProperty unitTemplatePropertyValueProperty() {
        if (Objects.isNull(unitTemplatePropertyValueProperty)) {
            unitTemplatePropertyValueProperty = new SimpleStringProperty(unitTemplatePropertyValue);
        }
        return unitTemplatePropertyValueProperty;
    }

    public IntegerProperty unitTemplatePropertyOrderProperty() {
        if (Objects.isNull(unitTemplatePropertyOrderProperty)) {
            unitTemplatePropertyOrderProperty = new SimpleIntegerProperty(unitTemplatePropertyOrder);
        }
        return unitTemplatePropertyOrderProperty;
    }

    public Long getUnitTemplatePropertyId() {
        if (Objects.nonNull(unitTemplatePropertyIdProperty)) {
            return unitTemplatePropertyIdProperty.get();
        }
        return unitTemplatePropertyId;
    }

    public void setUnitTemplatePropertyId(Long unitTemplatePropertyId) {
        if (Objects.nonNull(unitTemplatePropertyIdProperty)) {
            unitTemplatePropertyIdProperty.set(unitTemplatePropertyId);
        } else {
            this.unitTemplatePropertyId = unitTemplatePropertyId;
        }
    }

    public Long FkUnitTemplateIdProperty() {
        if (Objects.nonNull(fkUnitTemplateIdProperty)) {
            return fkUnitTemplateIdProperty.get();
        }
        return fkUnitTemplateId;
    }

    public void setFkMasterId(Long fkUnitTemplateId) {
        if (Objects.nonNull(fkUnitTemplateIdProperty)) {
            fkUnitTemplateIdProperty.set(fkUnitTemplateId);
        } else {
            this.fkUnitTemplateId = fkUnitTemplateId;
        }
    }

    public String getUnitTemplatePropertyName() {
        if (Objects.nonNull(unitTemplatePropertyNameProperty)) {
            return unitTemplatePropertyNameProperty.get();
        }
        return unitTemplatePropertyName;
    }

    public void setUnitTemplatePropertyName(String unitTemplatePropertyName) {
        if (Objects.nonNull(unitTemplatePropertyNameProperty)) {
            unitTemplatePropertyNameProperty.set(unitTemplatePropertyName);
        } else {
            this.unitTemplatePropertyName = unitTemplatePropertyName;
        }
    }

    public CustomPropertyTypeEnum getUnitTemplatePropertyType() {
        if (Objects.nonNull(unitTemplatePropertyTypeProperty)) {
            return unitTemplatePropertyTypeProperty.get();
        }
        return unitTemplatePropertyType;
    }

    public void setUnitTemplatePropertyType(CustomPropertyTypeEnum unitTemplatePropertyType) {
        if (Objects.nonNull(unitTemplatePropertyTypeProperty)) {
            unitTemplatePropertyTypeProperty.set(unitTemplatePropertyType);
        } else {
            this.unitTemplatePropertyType = unitTemplatePropertyType;
        }
    }

    public String getUnitTemplatePropertyValue() {
        if (Objects.nonNull(unitTemplatePropertyValueProperty)) {
            return unitTemplatePropertyValueProperty.get();
        }
        return unitTemplatePropertyValue;
    }

    public void setUnitTemplatePropertyValue(String unitTemplatePropertyValue) {
        if (Objects.nonNull(unitTemplatePropertyValueProperty)) {
            unitTemplatePropertyValueProperty.set(unitTemplatePropertyValue);
        } else {
            this.unitTemplatePropertyValue = unitTemplatePropertyValue;
        }
    }

    public Integer getUnitTemplatePropertyOrder() {
        if (Objects.nonNull(unitTemplatePropertyOrderProperty)) {
            return unitTemplatePropertyOrderProperty.get();
        }
        return unitTemplatePropertyOrder;
    }

    public void setUnitTemplatePropertyOrder(Integer unitTemplatePropertyOrder) {
        if (Objects.nonNull(unitTemplatePropertyOrderProperty)) {
            unitTemplatePropertyOrderProperty.set(unitTemplatePropertyOrder);
        } else {
            this.unitTemplatePropertyOrder = unitTemplatePropertyOrder;
        }
    }

    public void updateMember() {
        this.unitTemplatePropertyId = getUnitTemplatePropertyId();
        this.fkUnitTemplateId = FkUnitTemplateIdProperty();
        this.unitTemplatePropertyName = getUnitTemplatePropertyName();
        this.unitTemplatePropertyType = getUnitTemplatePropertyType();
        this.unitTemplatePropertyValue = getUnitTemplatePropertyValue();
        this.unitTemplatePropertyOrder = getUnitTemplatePropertyOrder();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.unitTemplatePropertyId ^ (this.unitTemplatePropertyId >>> 32));
        hash = 83 * hash + (int) (this.fkUnitTemplateId ^ (this.fkUnitTemplateId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.unitTemplatePropertyName);
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
        final UnitTemplatePropertyInfoEntity other = (UnitTemplatePropertyInfoEntity) obj;
        if (!Objects.equals(this.getUnitTemplatePropertyId(), other.getUnitTemplatePropertyId())) {
            return false;
        }
        if (!Objects.equals(this.FkUnitTemplateIdProperty(), other.FkUnitTemplateIdProperty())) {
            return false;
        }
        return Objects.equals(this.getUnitTemplatePropertyName(), other.getUnitTemplatePropertyName());
    }

    @Override
    public String toString() {
        return "UnitTemplatePropertyInfoEntity{" + "unitTemplatePropertyId=" + getUnitTemplatePropertyId() + ", fkUnitTemplateId=" + FkUnitTemplateIdProperty() + ", unitTemplatePropertyName=" + getUnitTemplatePropertyName() + ", unitTemplatePropertyType=" + getUnitTemplatePropertyType() + ", unitTemplatePropertyValue=" + getUnitTemplatePropertyValue() + ", unitTemplatePropertyOrder=" + getUnitTemplatePropertyOrder() + '}';
    }
}
