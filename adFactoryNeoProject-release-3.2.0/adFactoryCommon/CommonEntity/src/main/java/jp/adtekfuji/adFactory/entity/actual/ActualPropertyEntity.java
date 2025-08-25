/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.actual;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
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
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;

/**
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "actualProperty")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ActualPropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlTransient
    private LongProperty actualPropIdProperty;
    @XmlTransient
    private LongProperty fkActualIdProperty;
    @XmlTransient
    private StringProperty actualPropNameProperty;
    @XmlTransient
    private ObjectProperty<CustomPropertyTypeEnum> actualPropTypeProperty;
    @XmlTransient
    private StringProperty actualPropValueProperty;
    @XmlTransient
    private IntegerProperty actualPropOrderProperty;

    @XmlElement
    @JsonIgnore
    private Long actualPropId;
    
    @XmlElement
    @JsonIgnore
    private long fkActualId;
    
    @XmlElement
    @JsonProperty("key")
    private String actualPropName;
    
    @XmlElement
    @JsonProperty("type")
    private CustomPropertyTypeEnum actualPropType;
    
    @XmlElement
    @JsonProperty("val")
    private String actualPropValue;
    
    @XmlElement
    @JsonProperty("disp")
    private Integer actualPropOrder;

    public ActualPropertyEntity() {
    }

    public ActualPropertyEntity(String actualPropName, CustomPropertyTypeEnum actualPropType, String actualPropValue, Integer actualPropOrder) {
        this.actualPropName = actualPropName;
        this.actualPropType = actualPropType;
        this.actualPropValue = actualPropValue;
        this.actualPropOrder = actualPropOrder;
    }

    public LongProperty actualPropIdProperty() {
        if (Objects.isNull(actualPropIdProperty)) {
            actualPropIdProperty = new SimpleLongProperty(actualPropId);
        }
        return actualPropIdProperty;
    }

    public LongProperty fkActualIdProperty() {
        if (Objects.isNull(fkActualIdProperty)) {
            fkActualIdProperty = new SimpleLongProperty(fkActualId);
        }
        return fkActualIdProperty;
    }

    public StringProperty actualPropNameProperty() {
        if (Objects.isNull(actualPropNameProperty)) {
            actualPropNameProperty = new SimpleStringProperty(actualPropName);
        }
        return actualPropNameProperty;
    }

    public ObjectProperty<CustomPropertyTypeEnum> actualPropTypeProperty() {
        if (Objects.isNull(actualPropTypeProperty)) {
            actualPropTypeProperty = new SimpleObjectProperty<>(actualPropType);
        }
        return actualPropTypeProperty;
    }

    public StringProperty actualPropValueProperty() {
        if (Objects.isNull(actualPropValueProperty)) {
            actualPropValueProperty = new SimpleStringProperty(actualPropValue);
        }
        return actualPropValueProperty;
    }

    public IntegerProperty actualPropOrderProperty() {
        if (Objects.isNull(actualPropOrderProperty)) {
            actualPropOrderProperty = new SimpleIntegerProperty(actualPropOrder);
        }
        return actualPropOrderProperty;
    }

    public Long getActualPropId() {
        if (Objects.nonNull(actualPropIdProperty)) {
            return actualPropIdProperty.get();
        }
        return actualPropId;
    }

    public void setActualPropId(Long actualPropId) {
        if (Objects.nonNull(actualPropIdProperty)) {
            actualPropIdProperty.set(actualPropId);
        } else {
            this.actualPropId = actualPropId;
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
        if (Objects.nonNull(actualPropNameProperty)) {
            return actualPropNameProperty.get();
        }
        return actualPropName;
    }

    public void setActualPropName(String actualPropName) {
        if (Objects.nonNull(actualPropNameProperty)) {
            actualPropNameProperty.set(actualPropName);
        } else {
            this.actualPropName = actualPropName;
        }
    }

    public CustomPropertyTypeEnum getActualPropType() {
        if (Objects.nonNull(actualPropTypeProperty)) {
            return actualPropTypeProperty.get();
        }
        return actualPropType;
    }

    public void setActualPropType(CustomPropertyTypeEnum actualPropType) {
        if (Objects.nonNull(actualPropTypeProperty)) {
            actualPropTypeProperty.set(actualPropType);
        } else {
            this.actualPropType = actualPropType;
        }
    }

    public String getActualPropValue() {
        if (Objects.nonNull(actualPropValueProperty)) {
            return actualPropValueProperty.get();
        }
        return actualPropValue;
    }

    public void setActualPropValue(String actualPropValue) {
        if (Objects.nonNull(actualPropValueProperty)) {
            actualPropValueProperty.set(actualPropValue);
        } else {
            this.actualPropValue = actualPropValue;
        }
    }

    public Integer getActualPropOrder() {
        if (Objects.nonNull(actualPropOrderProperty)) {
            return actualPropOrderProperty.get();
        }
        return actualPropOrder;
    }

    public void setActualPropOrder(Integer actualPropOrder) {
        if (Objects.nonNull(actualPropOrderProperty)) {
            actualPropOrderProperty.set(actualPropOrder);
        } else {
            this.actualPropOrder = actualPropOrder;
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (actualPropId != null ? actualPropId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ActualPropertyEntity)) {
            return false;
        }
        ActualPropertyEntity other = (ActualPropertyEntity) object;
        if ((this.actualPropId == null && other.actualPropId != null) || (this.actualPropId != null && !this.actualPropId.equals(other.actualPropId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.actual.ActualPropertyEntity[ actualPropId=" + actualPropId + " ]";
    }

}
