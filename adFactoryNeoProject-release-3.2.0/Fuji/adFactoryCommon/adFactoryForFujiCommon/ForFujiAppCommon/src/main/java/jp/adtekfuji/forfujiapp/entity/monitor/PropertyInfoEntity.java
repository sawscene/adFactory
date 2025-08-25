/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.monitor;

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
 * プロパティ用エンティティ
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty propIdProperty;
    private LongProperty fkActualIdProperty;
    private StringProperty propNameProperty;
    private ObjectProperty<CustomPropertyTypeEnum> propTypeProperty;
    private StringProperty propValueProperty;
    private IntegerProperty propOrderProperty;

    @XmlElement
    private Long propId;
    @XmlElement
    private long fkActualId;
    @XmlElement
    private String propName;
    @XmlElement
    private CustomPropertyTypeEnum propType;
    @XmlElement
    private String propValue;
    @XmlElement
    private Integer propOrder;

    public PropertyInfoEntity() {
    }

    public PropertyInfoEntity(String propName, String propValue) {
        this.propName = propName;
        this.propValue = propValue;
    }

    public PropertyInfoEntity(String propName, CustomPropertyTypeEnum propType, String propValue, Integer propOrder) {
        this.propName = propName;
        this.propType = propType;
        this.propValue = propValue;
        this.propOrder = propOrder;
    }

    public LongProperty propIdProperty() {
        if (Objects.isNull(propIdProperty)) {
            propIdProperty = new SimpleLongProperty(propId);
        }
        return propIdProperty;
    }

    public LongProperty fkActualIdProperty() {
        if (Objects.isNull(fkActualIdProperty)) {
            fkActualIdProperty = new SimpleLongProperty(fkActualId);
        }
        return fkActualIdProperty;
    }

    public StringProperty propNameProperty() {
        if (Objects.isNull(propNameProperty)) {
            propNameProperty = new SimpleStringProperty(propName);
        }
        return propNameProperty;
    }

    public ObjectProperty<CustomPropertyTypeEnum> propTypeProperty() {
        if (Objects.isNull(propTypeProperty)) {
            propTypeProperty = new SimpleObjectProperty<>(propType);
        }
        return propTypeProperty;
    }

    public StringProperty propValueProperty() {
        if (Objects.isNull(propValueProperty)) {
            propValueProperty = new SimpleStringProperty(propValue);
        }
        return propValueProperty;
    }

    public IntegerProperty propOrderProperty() {
        if (Objects.isNull(propOrderProperty)) {
            propOrderProperty = new SimpleIntegerProperty(propOrder);
        }
        return propOrderProperty;
    }

    public Long getActualPropId() {
        if (Objects.nonNull(propIdProperty)) {
            return propIdProperty.get();
        }
        return propId;
    }

    public void setActualPropId(Long propId) {
        if (Objects.nonNull(propIdProperty)) {
            propIdProperty.set(propId);
        } else {
            this.propId = propId;
        }
    }

    public long getFkActualId() {
        if (Objects.nonNull(fkActualIdProperty)) {
            return fkActualIdProperty.get();
        }
        return fkActualId;
    }

    public void setFkActualId(long fkActualId) {
        if (Objects.nonNull(fkActualIdProperty)) {
            fkActualIdProperty.set(fkActualId);
        } else {
            this.fkActualId = fkActualId;
        }
    }

    public String getActualPropName() {
        if (Objects.nonNull(propNameProperty)) {
            return propNameProperty.get();
        }
        return propName;
    }

    public void setActualPropName(String propName) {
        if (Objects.nonNull(propNameProperty)) {
            propNameProperty.set(propName);
        } else {
            this.propName = propName;
        }
    }

    public CustomPropertyTypeEnum getActualPropType() {
        if (Objects.nonNull(propTypeProperty)) {
            return propTypeProperty.get();
        }
        return propType;
    }

    public void setActualPropType(CustomPropertyTypeEnum propType) {
        if (Objects.nonNull(propTypeProperty)) {
            propTypeProperty.set(propType);
        } else {
            this.propType = propType;
        }
    }

    public String getActualPropValue() {
        if (Objects.nonNull(propValueProperty)) {
            return propValueProperty.get();
        }
        return propValue;
    }

    public void setActualPropValue(String propValue) {
        if (Objects.nonNull(propValueProperty)) {
            propValueProperty.set(propValue);
        } else {
            this.propValue = propValue;
        }
    }

    public Integer getActualPropOrder() {
        if (Objects.nonNull(propOrderProperty)) {
            return propOrderProperty.get();
        }
        return propOrder;
    }

    public void setActualPropOrder(Integer propOrder) {
        if (Objects.nonNull(propOrderProperty)) {
            propOrderProperty.set(propOrder);
        } else {
            this.propOrder = propOrder;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (propId != null ? propId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PropertyInfoEntity)) {
            return false;
        }
        PropertyInfoEntity other = (PropertyInfoEntity) object;
        return !((this.propId == null && other.propId != null) || (this.propId != null && !this.propId.equals(other.propId)));
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.forfujicommon.entity.monitor.PropertyEntity[ propId=" + propId + " ]";
    }

}
