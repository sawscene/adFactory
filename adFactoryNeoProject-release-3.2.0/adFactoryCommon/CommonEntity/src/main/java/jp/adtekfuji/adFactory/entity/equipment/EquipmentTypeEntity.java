/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.equipment;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;

/**
 * 設備種別
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "equipmentType")
@XmlAccessorType(XmlAccessType.FIELD)
public class EquipmentTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlTransient
    private LongProperty equipmentTypeIdProperty;
    @XmlTransient
    private ObjectProperty<EquipmentTypeEnum> nameProperty;

    private Long equipmentTypeId;// 設備種別ID
    private EquipmentTypeEnum name;// 設備種別名

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    @XmlElementWrapper(name = "equipmentSettingTemplates")
    @XmlElement(name = "equipmentSettingTemplate")
    private List<EquipmentSettingTemplateInfoEntity> settingTemplateCollection = null;// 設備マスタ設定テンプレート一覧

    /**
     * コンストラクタ
     */
    public EquipmentTypeEntity() {
    }

    /**
     * 設備種別IDプロパティを取得する。
     *
     * @return 
     */
    public LongProperty equipmentTypeIdProperty() {
        if (Objects.isNull(this.equipmentTypeIdProperty)) {
            this.equipmentTypeIdProperty = new SimpleLongProperty(this.equipmentTypeId);
        }
        return this.equipmentTypeIdProperty;
    }

    /**
     * 設備種別名プロパティを取得する。
     *
     * @return 
     */
    public ObjectProperty<EquipmentTypeEnum> nameProperty() {
        if (Objects.isNull(this.nameProperty)) {
            this.nameProperty = new SimpleObjectProperty(this.name);
        }
        return this.nameProperty;
    }

    /**
     * 設備種別IDを取得する。
     *
     * @return 設備種別ID
     */
    public Long getEquipmentTypeId() {
        if (Objects.nonNull(this.equipmentTypeIdProperty)) {
            return this.equipmentTypeIdProperty.get();
        }
        return this.equipmentTypeId;
    }

    /**
     * 設備種別IDを設定する。
     *
     * @param equipmentTypeId 設備種別ID
     */
    public void setEquipmentTypeId(Long equipmentTypeId) {
        if (Objects.nonNull(this.equipmentTypeIdProperty)) {
            this.equipmentTypeIdProperty.set(equipmentTypeId);
        } else {
            this.equipmentTypeId = equipmentTypeId;
        }
    }

    /**
     * 設備種別名を取得する。
     *
     * @return 設備種別名
     */
    public EquipmentTypeEnum getName() {
        if (Objects.nonNull(this.nameProperty)) {
            return this.nameProperty.get();
        }
        return this.name;
    }

    /**
     * 設備種別名を設定する。
     *
     * @param name 設備種別名
     */
    public void setName(EquipmentTypeEnum name) {
        if (Objects.nonNull(this.nameProperty)) {
            this.nameProperty.set(name);
        } else {
            this.name = name;
        }
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * 内部変数を更新する。
     */
    public void updateMember() {
        this.equipmentTypeId = this.getEquipmentTypeId();
        this.name = this.getName();
    }

    /**
     * 設備マスタ設定テンプレート一覧を取得する。
     *
     * @return 設備マスタ設定テンプレート一覧
     */
    public List<EquipmentSettingTemplateInfoEntity> getSettingTemplateCollection() {
        return this.settingTemplateCollection;
    }

    /**
     * 設備マスタ設定テンプレート一覧を設定する。
     *
     * @param settingTemplateCollection 設備マスタ設定テンプレート一覧
     */
    public void setSettingTemplateCollection(List<EquipmentSettingTemplateInfoEntity> settingTemplateCollection) {
        this.settingTemplateCollection = settingTemplateCollection;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.equipmentTypeId);
        return hash;
    }

    /**
     * オブジェクトが等しいかどうかを返す。
     * 
     * @param obj オブジェクト
     * @return true: 等しい、false: 異なる
     */
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
        final EquipmentTypeEntity other = (EquipmentTypeEntity) obj;
        return Objects.equals(this.equipmentTypeId, other.equipmentTypeId);
    }
    
    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("EquipmentTypeEntity{")
                .append("equipmentTypeId=").append(this.equipmentTypeId)
                .append(", name=").append(this.name)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
