/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.equipment;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_equipment_setting_template")
@XmlRootElement(name = "equipmentSettingTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "EquipmentSettingTemplateEntity.findAll", query = "SELECT e FROM EquipmentSettingTemplateEntity e"),
    @NamedQuery(name = "EquipmentSettingTemplateEntity.findByEquipmentSettingTemplateId", query = "SELECT e FROM EquipmentSettingTemplateEntity e WHERE e.equipmentSettingTemplateId = :equipmentSettingTemplateId"),
    @NamedQuery(name = "EquipmentSettingTemplateEntity.findByFkMasterId", query = "SELECT e FROM EquipmentSettingTemplateEntity e WHERE e.fkMasterId = :fkMasterId"),
    @NamedQuery(name = "EquipmentSettingTemplateEntity.findBySettingName", query = "SELECT e FROM EquipmentSettingTemplateEntity e WHERE e.settingName = :settingName"),
    @NamedQuery(name = "EquipmentSettingTemplateEntity.findBySettingType", query = "SELECT e FROM EquipmentSettingTemplateEntity e WHERE e.settingType = :settingType"),
    @NamedQuery(name = "EquipmentSettingTemplateEntity.findBySettingInitialValue", query = "SELECT e FROM EquipmentSettingTemplateEntity e WHERE e.settingInitialValue = :settingInitialValue"),
    @NamedQuery(name = "EquipmentSettingTemplateEntity.findBySettingOrder", query = "SELECT e FROM EquipmentSettingTemplateEntity e WHERE e.settingOrder = :settingOrder"),
    @NamedQuery(name = "EquipmentSettingTemplateEntity.removeByFkMasterId", query = "DELETE FROM EquipmentSettingTemplateEntity e WHERE e.fkMasterId = :fkMasterId")})
public class EquipmentSettingTemplateEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "equipment_setting_template_id")
    private Long equipmentSettingTemplateId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_master_id")
    private Long fkMasterId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "setting_name")
    private String settingName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "setting_type")
    private String settingType;
    @Size(max = 2147483647)
    @Column(name = "setting_initial_value")
    private String settingInitialValue;
    @Column(name = "setting_order")
    private Integer settingOrder;

    public EquipmentSettingTemplateEntity() {
    }

    public EquipmentSettingTemplateEntity(Long fkMasterId, String settingName, String settingType, String settingInitialValue, Integer settingOrder) {
        this.fkMasterId = fkMasterId;
        this.settingName = settingName;
        this.settingType = settingType;
        this.settingInitialValue = settingInitialValue;
        this.settingOrder = settingOrder;
    }

    public Long getEquipmentSettingTemplateId() {
        return equipmentSettingTemplateId;
    }

    public void setEquipmentSettingTemplateId(Long equipmentSettingTemplateId) {
        this.equipmentSettingTemplateId = equipmentSettingTemplateId;
    }

    public Long getFkMasterId() {
        return fkMasterId;
    }

    public void setFkMasterId(Long fkMasterId) {
        this.fkMasterId = fkMasterId;
    }

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public String getSettingType() {
        return settingType;
    }

    public void setSettingType(String settingType) {
        this.settingType = settingType;
    }

    public String getSettingInitialValue() {
        return settingInitialValue;
    }

    public void setSettingInitialValue(String settingInitialValue) {
        this.settingInitialValue = settingInitialValue;
    }

    public Integer getSettingOrder() {
        return settingOrder;
    }

    public void setSettingOrder(Integer settingOrder) {
        this.settingOrder = settingOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (equipmentSettingTemplateId != null ? equipmentSettingTemplateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EquipmentSettingTemplateEntity)) {
            return false;
        }
        EquipmentSettingTemplateEntity other = (EquipmentSettingTemplateEntity) object;
        if ((this.equipmentSettingTemplateId == null && other.equipmentSettingTemplateId != null) || (this.equipmentSettingTemplateId != null && !this.equipmentSettingTemplateId.equals(other.equipmentSettingTemplateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EquipmentSettingTemplateEntity{" + "equipmentSettingTemplateId=" + equipmentSettingTemplateId + ", fkMasterId=" + fkMasterId + ", settingName=" + settingName + ", settingType=" + settingType + ", settingInitialValue=" + settingInitialValue + ", settingOrder=" + settingOrder + '}';
    }

}
