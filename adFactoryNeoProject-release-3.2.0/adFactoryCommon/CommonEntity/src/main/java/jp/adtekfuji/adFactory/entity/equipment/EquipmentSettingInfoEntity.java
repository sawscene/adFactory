/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.equipment;

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
 * 設備設定項目
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "equipmentInfo")
public class EquipmentSettingInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlTransient
    private LongProperty equipmentSettingIdProperty;
    @XmlTransient
    private LongProperty fkMasterIdProperty;
    @XmlTransient
    private StringProperty equipmentSettingNameProperty;
    @XmlTransient
    private ObjectProperty<CustomPropertyTypeEnum> equipmentSettingTypeProperty;
    @XmlTransient
    private StringProperty equipmentSettingValueProperty;
    @XmlTransient
    private IntegerProperty equipmentSettingOrderProperty;

    @XmlElement(required = true)
    private Long equipmentSettingId;
    @XmlElement()
    private Long fkMasterId;
    @XmlElement()
    private String equipmentSettingName;
    @XmlElement()
    private CustomPropertyTypeEnum equipmentSettingType;
    @XmlElement()
    private String equipmentSettingValue;
    @XmlElement()
    private Integer equipmentSettingOrder;

    public EquipmentSettingInfoEntity() {
    }

    public EquipmentSettingInfoEntity(Long equipmentSettingId, Long fkMasterId, String equipmentSettingName, CustomPropertyTypeEnum equipmentSettingType, String equipmentSettingValue, Integer equipmentSettingOrder) {
        this.equipmentSettingId = equipmentSettingId;
        this.fkMasterId = fkMasterId;
        this.equipmentSettingName = equipmentSettingName;
        this.equipmentSettingType = equipmentSettingType;
        this.equipmentSettingValue = equipmentSettingValue;
        this.equipmentSettingOrder = equipmentSettingOrder;
    }

    public LongProperty equipmentSettingIdProperty() {
        if (Objects.isNull(equipmentSettingIdProperty)) {
            equipmentSettingIdProperty = new SimpleLongProperty(equipmentSettingId);
        }
        return equipmentSettingIdProperty;
    }

    public LongProperty fkMasterIdProperty() {
        if (Objects.isNull(fkMasterIdProperty)) {
            fkMasterIdProperty = new SimpleLongProperty(fkMasterId);
        }
        return fkMasterIdProperty;
    }

    public StringProperty equipmentSettingNameProperty() {
        if (Objects.isNull(equipmentSettingNameProperty)) {
            equipmentSettingNameProperty = new SimpleStringProperty(equipmentSettingName);
        }
        return equipmentSettingNameProperty;
    }

    public ObjectProperty<CustomPropertyTypeEnum> equipmentSettingTypeProperty() {
        if (Objects.isNull(equipmentSettingTypeProperty)) {
            equipmentSettingTypeProperty = new SimpleObjectProperty<>(equipmentSettingType);
        }
        return equipmentSettingTypeProperty;
    }

    public StringProperty equipmentSettingValueProperty() {
        if (Objects.isNull(equipmentSettingValueProperty)) {
            equipmentSettingValueProperty = new SimpleStringProperty(equipmentSettingValue);
        }
        return equipmentSettingValueProperty;
    }

    public IntegerProperty equipmentSettingOrderProperty() {
        if (Objects.isNull(equipmentSettingOrderProperty)) {
            equipmentSettingOrderProperty = new SimpleIntegerProperty(equipmentSettingOrder);
        }
        return equipmentSettingOrderProperty;
    }

    public Long getEquipmentSettingId() {
        if (Objects.nonNull(equipmentSettingIdProperty)) {
            return equipmentSettingIdProperty.get();
        }
        return equipmentSettingId;
    }

    public void setEquipmentSettingId(Long equipmentSettingId) {
        if (Objects.nonNull(equipmentSettingIdProperty)) {
            equipmentSettingIdProperty.set(equipmentSettingId);
        } else {
            this.equipmentSettingId = equipmentSettingId;
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

    public String getEquipmentSettingName() {
        if (Objects.nonNull(equipmentSettingNameProperty)) {
            return equipmentSettingNameProperty.get();
        }
        return equipmentSettingName;
    }

    public void setEquipmentSettingName(String equipmentSettingName) {
        if (Objects.nonNull(equipmentSettingNameProperty)) {
            equipmentSettingNameProperty.set(equipmentSettingName);
        } else {
            this.equipmentSettingName = equipmentSettingName;
        }
    }

    public CustomPropertyTypeEnum getEquipmentSettingType() {
        if (Objects.nonNull(equipmentSettingTypeProperty)) {
            return equipmentSettingTypeProperty.get();
        }
        return equipmentSettingType;
    }

    public void setEquipmentSettingType(CustomPropertyTypeEnum equipmentSettingType) {
        if (Objects.nonNull(equipmentSettingTypeProperty)) {
            equipmentSettingTypeProperty.set(equipmentSettingType);
        } else {
            this.equipmentSettingType = equipmentSettingType;
        }
    }

    public String getEquipmentSettingValue() {
        if (Objects.nonNull(equipmentSettingValueProperty)) {
            return equipmentSettingValueProperty.get();
        }
        return equipmentSettingValue;
    }

    public void setEquipmentSettingValue(String equipmentSettingValue) {
        if (Objects.nonNull(equipmentSettingValueProperty)) {
            equipmentSettingValueProperty.set(equipmentSettingValue);
        } else {
            this.equipmentSettingValue = equipmentSettingValue;
        }
    }

    public Integer getEquipmentSettingOrder() {
        if (Objects.nonNull(equipmentSettingOrderProperty)) {
            return equipmentSettingOrderProperty.get();
        }
        return equipmentSettingOrder;
    }

    public void setEquipmentSettingOrder(Integer equipmentSettingOrder) {
        if (Objects.nonNull(equipmentSettingOrderProperty)) {
            equipmentSettingOrderProperty.set(equipmentSettingOrder);
        } else {
            this.equipmentSettingOrder = equipmentSettingOrder;
        }
    }

    public void updateMember() {
        this.equipmentSettingId = getEquipmentSettingId();
        this.fkMasterId = getFkMasterId();
        this.equipmentSettingName = getEquipmentSettingName();
        this.equipmentSettingType = getEquipmentSettingType();
        this.equipmentSettingValue = getEquipmentSettingValue();
        this.equipmentSettingOrder = getEquipmentSettingOrder();
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
     * 表示される情報が一致するか調べる
     * 
     * @param other
     * @return 
     */
    public boolean equalsDisplayInfo(EquipmentSettingInfoEntity other) {
        if(Objects.equals(getEquipmentSettingName(), other.getEquipmentSettingName())
                && Objects.equals(getEquipmentSettingType(), other.getEquipmentSettingType())) {
            //タイプがTYPE_BOOLEANのとき"false"が""となってる場合があるため"false"にしてから比較
            String leftValue = getEquipmentSettingValue();
            String rightValue = other.getEquipmentSettingValue();
            if(Objects.equals(getEquipmentSettingType(), CustomPropertyTypeEnum.TYPE_BOOLEAN) && Objects.equals(getEquipmentSettingValue(), "")) {
                leftValue = "false";
            }
            if(Objects.equals(other.getEquipmentSettingType(), CustomPropertyTypeEnum.TYPE_BOOLEAN) && Objects.equals(other.getEquipmentSettingValue(), "")) {
                rightValue = "false";
            }
            if(Objects.equals(leftValue, rightValue)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 表示される情報をコピーする
     *
     * @return
     */
    @Override
    public EquipmentSettingInfoEntity clone() {
        EquipmentSettingInfoEntity entity = new EquipmentSettingInfoEntity();
        entity.setEquipmentSettingName(getEquipmentSettingName());
        entity.setEquipmentSettingType(getEquipmentSettingType());
        entity.setEquipmentSettingValue(getEquipmentSettingValue());
        //以下表示されないもの
        entity.setEquipmentSettingId(getEquipmentSettingId());
        entity.setFkMasterId(getFkMasterId());
        entity.setEquipmentSettingOrder(getEquipmentSettingOrder());
        return entity;
    }
}
