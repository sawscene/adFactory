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
@Table(name = "mst_equipment_property")
@XmlRootElement(name = "equipmentProperty")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "EquipmentPropertyEntity.findAll", query = "SELECT e FROM EquipmentPropertyEntity e ORDER BY e.equipmentPropOrder"),
    @NamedQuery(name = "EquipmentPropertyEntity.findByEquipmentPropId", query = "SELECT e FROM EquipmentPropertyEntity e WHERE e.equipmentPropId = :equipmentPropId ORDER BY e.equipmentPropOrder"),
    @NamedQuery(name = "EquipmentPropertyEntity.findByFkMasterId", query = "SELECT e FROM EquipmentPropertyEntity e WHERE e.fkMasterId = :fkMasterId ORDER BY e.equipmentPropOrder"),
    @NamedQuery(name = "EquipmentPropertyEntity.findByEquipmentPropName", query = "SELECT e FROM EquipmentPropertyEntity e WHERE e.equipmentPropName = :equipmentPropName ORDER BY e.equipmentPropOrder"),
    @NamedQuery(name = "EquipmentPropertyEntity.findByEquipmentPropType", query = "SELECT e FROM EquipmentPropertyEntity e WHERE e.equipmentPropType = :equipmentPropType ORDER BY e.equipmentPropOrder"),
    @NamedQuery(name = "EquipmentPropertyEntity.findByEquipmentPropValue", query = "SELECT e FROM EquipmentPropertyEntity e WHERE e.equipmentPropValue = :equipmentPropValue ORDER BY e.equipmentPropOrder"),
    @NamedQuery(name = "EquipmentPropertyEntity.findByEquipmentPropOrder", query = "SELECT e FROM EquipmentPropertyEntity e WHERE e.equipmentPropOrder = :equipmentPropOrder ORDER BY e.equipmentPropOrder"),
    @NamedQuery(name = "EquipmentPropertyEntity.removeByFkMasterId", query = "DELETE FROM EquipmentPropertyEntity e WHERE e.fkMasterId = :fkMasterId")})
public class EquipmentPropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "equipment_prop_id")
    private Long equipmentPropId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_master_id")
    private Long fkMasterId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "equipment_prop_name")
    private String equipmentPropName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "equipment_prop_type")
    private String equipmentPropType;
    @Size(max = 2147483647)
    @Column(name = "equipment_prop_value")
    private String equipmentPropValue;
    @Column(name = "equipment_prop_order")
    private Integer equipmentPropOrder;

    public EquipmentPropertyEntity() {
    }

    public EquipmentPropertyEntity(EquipmentPropertyEntity in) {
        this.fkMasterId = in.fkMasterId;
        this.equipmentPropName = in.equipmentPropName;
        this.equipmentPropType = in.equipmentPropType;
        this.equipmentPropValue = in.equipmentPropValue;
        this.equipmentPropOrder = in.equipmentPropOrder;
    }

    public EquipmentPropertyEntity(String equipmentPropName, String equipmentPropType, String equipmentPropValue, Integer equipmentPropOrder) {
        this.equipmentPropName = equipmentPropName;
        this.equipmentPropType = equipmentPropType;
        this.equipmentPropValue = equipmentPropValue;
        this.equipmentPropOrder = equipmentPropOrder;
    }

    public Long getEquipmentPropId() {
        return equipmentPropId;
    }

    public void setEquipmentPropId(Long equipmentPropId) {
        this.equipmentPropId = equipmentPropId;
    }

    public Long getFkMasterId() {
        return fkMasterId;
    }

    public void setFkMasterId(Long fkMasterId) {
        this.fkMasterId = fkMasterId;
    }

    public String getEquipmentPropName() {
        return equipmentPropName;
    }

    public void setEquipmentPropName(String equipmentPropName) {
        this.equipmentPropName = equipmentPropName;
    }

    public String getEquipmentPropType() {
        return equipmentPropType;
    }

    public void setEquipmentPropType(String equipmentPropType) {
        this.equipmentPropType = equipmentPropType;
    }

    public String getEquipmentPropValue() {
        return equipmentPropValue;
    }

    public void setEquipmentPropValue(String equipmentPropValue) {
        this.equipmentPropValue = equipmentPropValue;
    }

    public Integer getEquipmentPropOrder() {
        return equipmentPropOrder;
    }

    public void setEquipmentPropOrder(Integer equipmentPropOrder) {
        this.equipmentPropOrder = equipmentPropOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (equipmentPropId != null ? equipmentPropId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EquipmentPropertyEntity)) {
            return false;
        }
        EquipmentPropertyEntity other = (EquipmentPropertyEntity) object;
        if ((this.equipmentPropId == null && other.equipmentPropId != null) || (this.equipmentPropId != null && !this.equipmentPropId.equals(other.equipmentPropId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EquipmentPropertyEntity{" + "equipmentPropId=" + equipmentPropId + ", fkMasterId=" + fkMasterId + ", equipmentPropName=" + equipmentPropName + ", equipmentPropType=" + equipmentPropType + ", equipmentPropValue=" + equipmentPropValue + ", equipmentPropOrder=" + equipmentPropOrder + '}';
    }

}
