/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.equipment;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;

/**
 * 設備マスタ設定テンプレート
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "equipmentSettingTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class EquipmentSettingTemplateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    private EquipmentTypeEnum equipmentType;// 設備タイプ

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    private String settingName;// 設定の名称

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 128)
    private String settingType;// 設定の型

    @Size(max = 2147483647)
    private String settingInitialValue;// 設定の初期値

    @Id
    @Basic(optional = false)
    //@NotNull
    private Integer settingOrder;// 設定の表示順

    /**
     * コンストラクタ
     */
    public EquipmentSettingTemplateEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param equipmentType 設備タイプ
     * @param settingName 設定の名称
     * @param settingType 設定の型
     * @param settingInitialValue 設定の初期値
     * @param settingOrder 設定の表示順
     */
    public EquipmentSettingTemplateEntity(EquipmentTypeEnum equipmentType, String settingName, String settingType, String settingInitialValue, Integer settingOrder) {
        this.equipmentType = equipmentType;
        this.settingName = settingName;
        this.settingType = settingType;
        this.settingInitialValue = settingInitialValue;
        this.settingOrder = settingOrder;
    }

    /**
     * 設備タイプを取得する。
     *
     * @return 設備タイプ
     */
    public EquipmentTypeEnum getEquipmentType() {
        return this.equipmentType;
    }

    /**
     * 設備タイプを設定する。
     *
     * @param equipmentType 設備タイプ
     */
    public void setEquipmentType(EquipmentTypeEnum equipmentType) {
        this.equipmentType = equipmentType;
    }

    /**
     * 設定の名称を取得する。
     *
     * @return 設定の名称
     */
    public String getSettingName() {
        return this.settingName;
    }

    /**
     * 設定の名称を設定する。
     *
     * @param settingName 設定の名称
     */
    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    /**
     * 設定の型を取得する。
     *
     * @return 設定の型
     */
    public String getSettingType() {
        return this.settingType;
    }

    /**
     * 設定の型を設定する。
     *
     * @param settingType 設定の型
     */
    public void setSettingType(String settingType) {
        this.settingType = settingType;
    }

    /**
     * 設定の初期値を取得する。
     *
     * @return 設定の初期値
     */
    public String getSettingInitialValue() {
        return this.settingInitialValue;
    }

    /**
     * 設定の初期値を設定する。
     *
     * @param settingInitialValue 設定の初期値
     */
    public void setSettingInitialValue(String settingInitialValue) {
        this.settingInitialValue = settingInitialValue;
    }

    /**
     * 設定の表示順を取得する。
     *
     * @return 設定の表示順
     */
    public Integer getSettingOrder() {
        return this.settingOrder;
    }

    /**
     * 設定の表示順を設定する。
     *
     * @param settingOrder 設定の表示順
     */
    public void setSettingOrder(Integer settingOrder) {
        this.settingOrder = settingOrder;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.equipmentType);
        hash = 53 * hash + Objects.hashCode(this.settingOrder);
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
        final EquipmentSettingTemplateEntity other = (EquipmentSettingTemplateEntity) obj;
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
        return new StringBuilder("EquipmentSettingTemplateEntity{")
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
