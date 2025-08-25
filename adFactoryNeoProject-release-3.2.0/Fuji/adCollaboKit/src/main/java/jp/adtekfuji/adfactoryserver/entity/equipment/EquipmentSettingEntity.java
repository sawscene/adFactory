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
@Table(name = "mst_equipment_setting")
@XmlRootElement(name = "equipmentSetting")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "EquipmentSettingEntity.findAll", query = "SELECT e FROM EquipmentSettingEntity e ORDER BY e.equipmentSettingOrder"),
    @NamedQuery(name = "EquipmentSettingEntity.findByEquipmentSettingId", query = "SELECT e FROM EquipmentSettingEntity e WHERE e.equipmentSettingId = :equipmentSettingId ORDER BY e.equipmentSettingOrder"),
    @NamedQuery(name = "EquipmentSettingEntity.findByFkMasterId", query = "SELECT e FROM EquipmentSettingEntity e WHERE e.fkMasterId = :fkMasterId ORDER BY e.equipmentSettingOrder"),
    @NamedQuery(name = "EquipmentSettingEntity.findByEquipmentSettingName", query = "SELECT e FROM EquipmentSettingEntity e WHERE e.equipmentSettingName = :equipmentSettingName ORDER BY e.equipmentSettingOrder"),
    @NamedQuery(name = "EquipmentSettingEntity.findByEquipmentSettingType", query = "SELECT e FROM EquipmentSettingEntity e WHERE e.equipmentSettingType = :equipmentSettingType ORDER BY e.equipmentSettingOrder"),
    @NamedQuery(name = "EquipmentSettingEntity.findByEquipmentSettingValue", query = "SELECT e FROM EquipmentSettingEntity e WHERE e.equipmentSettingValue = :equipmentSettingValue ORDER BY e.equipmentSettingOrder"),
    @NamedQuery(name = "EquipmentSettingEntity.findByEquipmentSettingOrder", query = "SELECT e FROM EquipmentSettingEntity e WHERE e.equipmentSettingOrder = :equipmentSettingOrder ORDER BY e.equipmentSettingOrder"),
    @NamedQuery(name = "EquipmentSettingEntity.removeByFkMasterId", query = "DELETE FROM EquipmentSettingEntity e WHERE e.fkMasterId = :fkMasterId")})
public class EquipmentSettingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "equipment_setting_id")
    private Long equipmentSettingId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_master_id")
    private Long fkMasterId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "equipment_setting_name")
    private String equipmentSettingName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "equipment_setting_type")
    private String equipmentSettingType;
    @Size(max = 2147483647)
    @Column(name = "equipment_setting_value")
    private String equipmentSettingValue;
    @Column(name = "equipment_setting_order")
    private Integer equipmentSettingOrder;

    public EquipmentSettingEntity() {
    }

    public EquipmentSettingEntity(EquipmentSettingEntity in) {
        this.fkMasterId = in.fkMasterId;
        this.equipmentSettingName = in.equipmentSettingName;
        this.equipmentSettingType = in.equipmentSettingType;
        this.equipmentSettingValue = in.equipmentSettingValue;
        this.equipmentSettingOrder = in.equipmentSettingOrder;
    }

    public EquipmentSettingEntity(String equipmentSettingName, String equipmentSettingType, String equipmentSettingValue, Integer equipmentSettingOrder) {
        this.equipmentSettingName = equipmentSettingName;
        this.equipmentSettingType = equipmentSettingType;
        this.equipmentSettingValue = equipmentSettingValue;
        this.equipmentSettingOrder = equipmentSettingOrder;
    }

    public Long getEquipmentSettingId() {
        return equipmentSettingId;
    }

    public void setEquipmentSettingId(Long equipmentSettingId) {
        this.equipmentSettingId = equipmentSettingId;
    }

    public Long getFkMasterId() {
        return fkMasterId;
    }

    public void setFkMasterId(Long fkMasterId) {
        this.fkMasterId = fkMasterId;
    }

    public String getEquipmentSettingName() {
        return equipmentSettingName;
    }

    public void setEquipmentSettingName(String equipmentSettingName) {
        this.equipmentSettingName = equipmentSettingName;
    }

    public String getEquipmentSettingType() {
        return equipmentSettingType;
    }

    public void setEquipmentSettingType(String equipmentSettingType) {
        this.equipmentSettingType = equipmentSettingType;
    }

    public String getEquipmentSettingValue() {
        return equipmentSettingValue;
    }

    public void setEquipmentSettingValue(String equipmentSettingValue) {
        this.equipmentSettingValue = equipmentSettingValue;
    }

    public Integer getEquipmentSettingOrder() {
        return equipmentSettingOrder;
    }

    public void setEquipmentSettingOrder(Integer equipmentSettingOrder) {
        this.equipmentSettingOrder = equipmentSettingOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (equipmentSettingId != null ? equipmentSettingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EquipmentSettingEntity)) {
            return false;
        }
        EquipmentSettingEntity other = (EquipmentSettingEntity) object;
        if ((this.equipmentSettingId == null && other.equipmentSettingId != null) || (this.equipmentSettingId != null && !this.equipmentSettingId.equals(other.equipmentSettingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EquipmentSettingEntity{" + "equipmentSettingId=" + equipmentSettingId + ", fkMasterId=" + fkMasterId + ", equipmentSettingName=" + equipmentSettingName + ", equipmentSettingType=" + equipmentSettingType + ", equipmentSettingValue=" + equipmentSettingValue + ", equipmentSettingOrder=" + equipmentSettingOrder + '}';
    }

}
