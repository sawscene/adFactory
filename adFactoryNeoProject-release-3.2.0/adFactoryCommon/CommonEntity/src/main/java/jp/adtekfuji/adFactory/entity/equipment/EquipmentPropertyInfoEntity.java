/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.equipment;

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
 * 設備プロパティ情報
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "equipmentProperty")
@JsonIgnoreProperties(ignoreUnknown=true)
public class EquipmentPropertyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlTransient
    private LongProperty equipmentPropIdProperty;
    @XmlTransient
    private LongProperty fkMasterIdProperty;
    @XmlTransient
    private StringProperty equipmentPropNameProperty;
    @XmlTransient
    private ObjectProperty<CustomPropertyTypeEnum> equipmentPropTypeProperty;
    @XmlTransient
    private StringProperty equipmentPropValueProperty;
    @XmlTransient
    private IntegerProperty equipmentPropOrderProperty;

    @XmlElement(required = true)
    @JsonIgnore
    private Long equipmentPropId;
    
    @XmlElement()
    @JsonIgnore
    private Long fkMasterId;
    
    @XmlElement()
    @JsonProperty("key")
    private String equipmentPropName;
    
    @XmlElement()
    @JsonProperty("type")
    private CustomPropertyTypeEnum equipmentPropType;
    
    @XmlElement()
    @JsonProperty("val")
    private String equipmentPropValue;
    
    @XmlElement()
    @JsonProperty("disp")
    private Integer equipmentPropOrder;

    public EquipmentPropertyInfoEntity() {
    }

    public EquipmentPropertyInfoEntity(Long equipmentPropId, Long fkMasterId, String equipmentPropName, CustomPropertyTypeEnum equipmentPropType, String equipmentPropValue, Integer equipmentPropOrder) {
        this.equipmentPropId = equipmentPropId;
        this.fkMasterId = fkMasterId;
        this.equipmentPropName = equipmentPropName;
        this.equipmentPropType = equipmentPropType;
        this.equipmentPropValue = equipmentPropValue;
        this.equipmentPropOrder = equipmentPropOrder;
    }

    public EquipmentPropertyInfoEntity(Long equipmentPropId, Long fkMasterId, String equipmentPropName) {
        this.equipmentPropId = equipmentPropId;
        this.fkMasterId = fkMasterId;
        this.equipmentPropName = equipmentPropName;
    }

    public LongProperty equipmentPropIdProperty() {
        if (Objects.isNull(equipmentPropIdProperty)) {
            equipmentPropIdProperty = new SimpleLongProperty(equipmentPropId);
        }
        return equipmentPropIdProperty;
    }

    public LongProperty fkMasterIdProperty() {
        if (Objects.isNull(fkMasterIdProperty)) {
            fkMasterIdProperty = new SimpleLongProperty(fkMasterId);
        }
        return fkMasterIdProperty;
    }

    public StringProperty equipmentPropNameProperty() {
        if (Objects.isNull(equipmentPropNameProperty)) {
            equipmentPropNameProperty = new SimpleStringProperty(equipmentPropName);
        }
        return equipmentPropNameProperty;
    }

    public ObjectProperty<CustomPropertyTypeEnum> equipmentPropTypeProperty() {
        if (Objects.isNull(equipmentPropTypeProperty)) {
            equipmentPropTypeProperty = new SimpleObjectProperty<>(equipmentPropType);
        }
        return equipmentPropTypeProperty;
    }

    public StringProperty equipmentPropValueProperty() {
        if (Objects.isNull(equipmentPropValueProperty)) {
            equipmentPropValueProperty = new SimpleStringProperty(equipmentPropValue);
        }
        return equipmentPropValueProperty;
    }

    public IntegerProperty equipmentPropOrderProperty() {
        if (Objects.isNull(equipmentPropOrderProperty)) {
            equipmentPropOrderProperty = new SimpleIntegerProperty(equipmentPropOrder);
        }
        return equipmentPropOrderProperty;
    }

    public Long getEquipmentPropId() {
        if (Objects.nonNull(equipmentPropIdProperty)) {
            return equipmentPropIdProperty.get();
        }
        return equipmentPropId;
    }

    public void setEquipmentPropId(Long equipmentPropId) {
        if (Objects.nonNull(equipmentPropIdProperty)) {
            equipmentPropIdProperty.set(equipmentPropId);
        } else {
            this.equipmentPropId = equipmentPropId;
        }
    }

    public Long getFkMasterId() {
        if (Objects.nonNull(fkMasterIdProperty)) {
            return fkMasterIdProperty.get();
        }
        return fkMasterId;
    }

    public void setFkMasterId(Long fkMasterId) {
        if (Objects.nonNull(fkMasterIdProperty)) {
            fkMasterIdProperty.set(fkMasterId);
        } else {
            this.fkMasterId = fkMasterId;
        }
    }

    public String getEquipmentPropName() {
        if (Objects.nonNull(equipmentPropNameProperty)) {
            return equipmentPropNameProperty.get();
        }
        return equipmentPropName;
    }

    public void setEquipmentPropName(String equipmentPropName) {
        if (Objects.nonNull(equipmentPropNameProperty)) {
            equipmentPropNameProperty.set(equipmentPropName);
        } else {
            this.equipmentPropName = equipmentPropName;
        }
    }

    public CustomPropertyTypeEnum getEquipmentPropType() {
        if (Objects.nonNull(equipmentPropTypeProperty)) {
            return equipmentPropTypeProperty.get();
        }
        return equipmentPropType;
    }

    public void setEquipmentPropType(CustomPropertyTypeEnum equipmentPropType) {
        if (Objects.nonNull(equipmentPropTypeProperty)) {
            equipmentPropTypeProperty.set(equipmentPropType);
        } else {
            this.equipmentPropType = equipmentPropType;
        }
    }

    public String getEquipmentPropValue() {
        if (Objects.nonNull(equipmentPropValueProperty)) {
            return equipmentPropValueProperty.get();
        }
        return equipmentPropValue;
    }

    public void setEquipmentPropValue(String equipmentPropValue) {
        if (Objects.nonNull(equipmentPropValueProperty)) {
            equipmentPropValueProperty.set(equipmentPropValue);
        } else {
            this.equipmentPropValue = equipmentPropValue;
        }
    }

    public Integer getEquipmentPropOrder() {
        if (Objects.nonNull(equipmentPropOrderProperty)) {
            return equipmentPropOrderProperty.get();
        }
        return equipmentPropOrder;
    }

    public void setEquipmentPropOrder(Integer equipmentPropOrder) {
        if (Objects.nonNull(equipmentPropOrderProperty)) {
            equipmentPropOrderProperty.set(equipmentPropOrder);
        } else {
            this.equipmentPropOrder = equipmentPropOrder;
        }
    }

    public void updateMember() {
        this.equipmentPropId = getEquipmentPropId();
        this.fkMasterId = getFkMasterId();
        this.equipmentPropName = getEquipmentPropName();
        this.equipmentPropType = getEquipmentPropType();
        this.equipmentPropValue = getEquipmentPropValue();
        this.equipmentPropOrder = getEquipmentPropOrder();
    }

    /**
     * TODO: 詳細仕様に合わせて実装
     *
     * @return
     */
    @Override
    public int hashCode() {
        return super.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * TODO: 詳細仕様に合わせて実装
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * TODO: 詳細仕様に合わせて実装
     *
     * @return
     */
    @Override
    public String toString() {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * 表示される情報をコピーする
     * 
     * @return 
     */
    @Override
    public EquipmentPropertyInfoEntity clone() {
        EquipmentPropertyInfoEntity entity = new EquipmentPropertyInfoEntity();
        
        entity.setEquipmentPropName(getEquipmentPropName());
        entity.setEquipmentPropType(getEquipmentPropType());
        entity.setEquipmentPropValue(getEquipmentPropValue());
        
        //以下のものは表示されないがソートなどで用いられるためコピー
        entity.setEquipmentPropId(getEquipmentPropId());
        entity.setEquipmentPropOrder(getEquipmentPropOrder());
        entity.setFkMasterId(getFkMasterId());
        
        return entity;
    }
    
    /**
     * 表示される情報が一致するか調べる
     * 
     * @param other
     * @return 
     */
    public boolean equalsDisplayInfo(EquipmentPropertyInfoEntity other) {
        if(Objects.equals(getEquipmentPropName(), other.getEquipmentPropName())
                && Objects.equals(getEquipmentPropType(), other.getEquipmentPropType())
                && Objects.equals(getEquipmentPropValue(), other.getEquipmentPropValue())) {
            return true;
        }
        
        return false;
    }
}
