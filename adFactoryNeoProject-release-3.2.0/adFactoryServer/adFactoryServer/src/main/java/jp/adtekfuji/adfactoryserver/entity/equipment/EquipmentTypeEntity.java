/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.equipment;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;

/**
 * 設備種別情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_equipment_type")
@XmlRootElement(name = "equipmentType")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "EquipmentTypeEntity.findAll", query = "SELECT e FROM EquipmentTypeEntity e"),
    @NamedQuery(name = "EquipmentTypeEntity.findByName", query = "SELECT e FROM EquipmentTypeEntity e WHERE e.name = :name"),
    @NamedQuery(name = "EquipmentTypeEntity.findById", query = "SELECT e FROM EquipmentTypeEntity e WHERE e.equipmentTypeId = :id")
})
public class EquipmentTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "equipment_type_id")
    private Long equipmentTypeId;// 設備種別ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private EquipmentTypeEnum name;// 設備種別名

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    @XmlElementWrapper(name = "equipmentSettingTemplates")
    @XmlElement(name = "equipmentSettingTemplate")
    @Transient
    private List<EquipmentSettingTemplateEntity> settingTemplateCollection = null;// 設備マスタ設定テンプレート一覧

    /**
     * コンストラクタ
     */
    public EquipmentTypeEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param name 設備種別名
     */
    public EquipmentTypeEntity(EquipmentTypeEnum name) {
        this.name = name;
    }

    /**
     * 設備種別IDを取得する。
     *
     * @return 設備種別ID
     */
    public Long getEquipmentTypeId() {
        return this.equipmentTypeId;
    }

    /**
     * 設備種別IDを設定する。
     *
     * @param equipmentTypeId 設備種別ID
     */
    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    /**
     * 設備種別名を取得する。
     *
     * @return 設備種別名
     */
    public EquipmentTypeEnum getName() {
        return this.name;
    }

    /**
     * 設備種別名を設定する。
     *
     * @param name 設備種別名
     */
    public void setName(EquipmentTypeEnum name) {
        this.name = name;
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
     * 設備マスタ設定テンプレート一覧を取得する。
     *
     * @return 設備マスタ設定テンプレート一覧
     */
    public List<EquipmentSettingTemplateEntity> getSettingTemplateCollection() {
        return this.settingTemplateCollection;
    }

    /**
     * 設備マスタ設定テンプレート一覧を設定する。
     *
     * @param settingTemplateCollection 設備マスタ設定テンプレート一覧
     */
    public void setSettingTemplateCollection(List<EquipmentSettingTemplateEntity> settingTemplateCollection) {
        this.settingTemplateCollection = settingTemplateCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.equipmentTypeId != null ? this.equipmentTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EquipmentTypeEntity)) {
            return false;
        }
        EquipmentTypeEntity other = (EquipmentTypeEntity) object;
        if ((this.equipmentTypeId == null && other.equipmentTypeId != null) || (this.equipmentTypeId != null && !this.equipmentTypeId.equals(other.equipmentTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("EquipmentTypeEntity{")
                .append("equipmentTypeId=").append(this.equipmentTypeId)
                .append(", ")
                .append("name=").append(this.name)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
