/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.equipment;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_equipment_type")
@XmlRootElement(name = "equipmentType")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "EquipmentTypeEntity.findAll", query = "SELECT e FROM EquipmentTypeEntity e"),
    @NamedQuery(name = "EquipmentTypeEntity.findByEquipmentTypeId", query = "SELECT e FROM EquipmentTypeEntity e WHERE e.equipmentTypeId = :equipmentTypeId"),
    @NamedQuery(name = "EquipmentTypeEntity.findByName", query = "SELECT e FROM EquipmentTypeEntity e WHERE e.name = :name")})
public class EquipmentTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "equipment_type_id")
    private Long equipmentTypeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private EquipmentTypeEnum name;
    @XmlElementWrapper(name = "equipmentSettingTemplates")
    @XmlElement(name = "equipmentSettingTemplate")
    @Transient
    private List<EquipmentSettingTemplateEntity> settingTemplateCollection = null;

    public EquipmentTypeEntity() {
    }

    public EquipmentTypeEntity(EquipmentTypeEnum name) {
        this.name = name;
    }

    public Long getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    public EquipmentTypeEnum getName() {
        return name;
    }

    public void setName(EquipmentTypeEnum name) {
        this.name = name;
    }

    public List<EquipmentSettingTemplateEntity> getSettingTemplateCollection() {
        return settingTemplateCollection;
    }

    public void setSettingTemplateCollection(List<EquipmentSettingTemplateEntity> settingTemplateCollection) {
        this.settingTemplateCollection = settingTemplateCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (equipmentTypeId != null ? equipmentTypeId.hashCode() : 0);
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
        return "EquipmentTypeEntity{" + "equipmentTypeId=" + equipmentTypeId + ", name=" + name + ", settingTemplateCollection=" + settingTemplateCollection + '}';
    }

}
