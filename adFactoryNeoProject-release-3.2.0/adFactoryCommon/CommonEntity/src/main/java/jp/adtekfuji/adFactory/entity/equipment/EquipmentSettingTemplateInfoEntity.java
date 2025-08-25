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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;

/**
 * 設備マスタ設定テンプレート
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "equipmentSettingTemplate")
public class EquipmentSettingTemplateInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlTransient
    private ObjectProperty<EquipmentTypeEnum> equipmentTypeProperty;
    @XmlTransient
    private StringProperty settingNameProperty;
    @XmlTransient
    private ObjectProperty<CustomPropertyTypeEnum> settingTypeProperty;
    @XmlTransient
    private StringProperty settingInitialValueProperty;
    @XmlTransient
    private IntegerProperty settingOrderProperty;

    @XmlElement()
    private EquipmentTypeEnum equipmentType;// 設備タイプ
    @XmlElement()
    private String settingName;// 設定の名称
    @XmlElement()
    private CustomPropertyTypeEnum settingType;// 設定の型
    @XmlElement()
    private String settingInitialValue;// 設定の初期値
    @XmlElement()
    private Integer settingOrder;// 設定の表示順

    /**
     * コンストラクタ
     */
    public EquipmentSettingTemplateInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param equipmentType 設備タイプ
     * @param equipmentSettingName 設定の名称
     * @param equipmentSettingType 設定の型
     * @param equipmentSettingValue 設定の初期値
     * @param equipmentSettingOrder 設定の表示順
     */
    public EquipmentSettingTemplateInfoEntity(EquipmentTypeEnum equipmentType, String equipmentSettingName, CustomPropertyTypeEnum equipmentSettingType, String equipmentSettingValue, Integer equipmentSettingOrder) {
        this.equipmentType = equipmentType;
        this.settingName = equipmentSettingName;
        this.settingType = equipmentSettingType;
        this.settingInitialValue = equipmentSettingValue;
        this.settingOrder = equipmentSettingOrder;
    }

    /**
     * 設備タイププロパティを取得する。
     *
     * @return 設備タイプ
     */
    public ObjectProperty<EquipmentTypeEnum> getEquipmentTypeProperty() {
        if (Objects.isNull(this.equipmentTypeProperty)) {
            this.equipmentTypeProperty = new SimpleObjectProperty(this.equipmentType);
        }
        return this.equipmentTypeProperty;
    }

    /**
     * 設定の名称プロパティを取得する。
     *
     * @return 設定の名称
     */
    public StringProperty settingNameProperty() {
        if (Objects.isNull(this.settingNameProperty)) {
            this.settingNameProperty = new SimpleStringProperty(this.settingName);
        }
        return this.settingNameProperty;
    }

    /**
     * 設定の型プロパティを取得する。
     *
     * @return 設定の型
     */
    public ObjectProperty<CustomPropertyTypeEnum> settingTypeProperty() {
        if (Objects.isNull(this.settingTypeProperty)) {
            this.settingTypeProperty = new SimpleObjectProperty(this.settingType);
        }
        return this.settingTypeProperty;
    }

    /**
     * 設定の初期値プロパティを取得する。
     *
     * @return 設定の初期値
     */
    public StringProperty settingInitialValueProperty() {
        if (Objects.isNull(this.settingInitialValueProperty)) {
            this.settingInitialValueProperty = new SimpleStringProperty(this.settingInitialValue);
        }
        return this.settingInitialValueProperty;
    }

    /**
     * 設定の表示順プロパティを取得する。
     *
     * @return 設定の表示順
     */
    public IntegerProperty settingOrderProperty() {
        if (Objects.isNull(this.settingOrderProperty)) {
            this.settingOrderProperty = new SimpleIntegerProperty(this.settingOrder);
        }
        return this.settingOrderProperty;
    }

    /**
     * 設備タイプを取得する。
     *
     * @return 設備タイプ
     */
    public EquipmentTypeEnum getEquipmentType() {
        if (Objects.nonNull(this.equipmentTypeProperty)) {
            return this.equipmentTypeProperty.get();
        }
        return equipmentType;
    }

    /**
     * 設備タイプを設定する。
     *
     * @param equipmentType 設備タイプ
     */
    public void setEquipmentType(EquipmentTypeEnum equipmentType) {
        if (Objects.nonNull(this.equipmentTypeProperty)) {
            this.equipmentTypeProperty.set(equipmentType);
        } else {
            this.equipmentType = equipmentType;
        }
    }

    /**
     * 設定の名称を取得する。
     *
     * @return 設定の名称
     */
    public String getSettingName() {
        if (Objects.nonNull(this.settingNameProperty)) {
            return this.settingNameProperty.get();
        }
        return this.settingName;
    }

    /**
     * 設定の名称を設定する。
     *
     * @param settingName 設定の名称
     */
    public void setSettingName(String settingName) {
        if (Objects.nonNull(this.settingNameProperty)) {
            this.settingNameProperty.set(settingName);
        } else {
            this.settingName = settingName;
        }
    }

    /**
     * 設定の型を取得する。
     *
     * @return 設定の型
     */
    public CustomPropertyTypeEnum getSettingType() {
        if (Objects.nonNull(this.settingTypeProperty)) {
            return this.settingTypeProperty.get();
        }
        return this.settingType;
    }

    /**
     * 設定の型を設定する。
     *
     * @param settingType 設定の型
     */
    public void setSettingType(CustomPropertyTypeEnum settingType) {
        if (Objects.nonNull(this.settingTypeProperty)) {
            this.settingTypeProperty.set(settingType);
        } else {
            this.settingType = settingType;
        }
    }

    /**
     * 設定の初期値を取得する。
     *
     * @return 設定の初期値
     */
    public String getSettingInitialValue() {
        if (Objects.nonNull(this.settingInitialValueProperty)) {
            return this.settingInitialValueProperty.get();
        }
        return this.settingInitialValue;
    }

    /**
     * 設定の初期値を設定する。
     *
     * @param settingInitialValue 設定の初期値
     */
    public void setSettingInitialValue(String settingInitialValue) {
        if (Objects.nonNull(this.settingInitialValueProperty)) {
            this.settingInitialValueProperty.set(settingInitialValue);
        } else {
            this.settingInitialValue = settingInitialValue;
        }
    }

    /**
     * 設定の表示順を取得する。
     *
     * @return 設定の表示順
     */
    public Integer getSettingOrder() {
        if (Objects.nonNull(this.settingOrderProperty)) {
            return this.settingOrderProperty.get();
        }
        return this.settingOrder;
    }

    /**
     * 設定の表示順を設定する。
     *
     * @param settingOrder 設定の表示順
     */
    public void setSettingOrder(Integer settingOrder) {
        if (Objects.nonNull(this.settingOrderProperty)) {
            this.settingOrderProperty.set(settingOrder);
        } else {
            this.settingOrder = settingOrder;
        }
    }

    /**
     * 内部変数を更新する。
     */
    public void updateMember() {
        this.equipmentType = this.getEquipmentType();
        this.settingName = this.getSettingName();
        this.settingType = this.getSettingType();
        this.settingInitialValue = this.getSettingInitialValue();
        this.settingOrder = this.getSettingOrder();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.equipmentType);
        hash = 97 * hash + Objects.hashCode(this.settingOrder);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EquipmentSettingTemplateInfoEntity other = (EquipmentSettingTemplateInfoEntity) obj;
        if (this.equipmentType != other.equipmentType) {
            return false;
        }
        if (!Objects.equals(this.settingOrder, other.settingOrder)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("EquipmentSettingTemplateInfoEntity{")
                .append("equipmentType=").append(this.equipmentType)
                .append(", ")
                .append("settingName=").append(this.settingName)
                .append(", ")
                .append("settingType=").append(this.settingType)
                .append(", ")
                .append("settingInitialValue=").append(this.settingInitialValue)
                .append(", ")
                .append("settingOrder=").append(this.settingOrder)
                .append("}")
                .toString();
    }
}
