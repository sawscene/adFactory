/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.equipment;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "tre_equipment_hierarchy")
@XmlRootElement(name = "equipmentHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "EquipmentHierarchyEntity.findAll", query = "SELECT e FROM EquipmentHierarchyEntity e"),
    @NamedQuery(name = "EquipmentHierarchyEntity.findByParentId", query = "SELECT e FROM EquipmentHierarchyEntity e WHERE e.equipmentHierarchyEntityPK.parentId = :parentId ORDER BY e.equipmentHierarchyEntityPK.childId ASC"),
    @NamedQuery(name = "EquipmentHierarchyEntity.findByChildId", query = "SELECT e FROM EquipmentHierarchyEntity e WHERE e.equipmentHierarchyEntityPK.childId = :childId ORDER BY e.equipmentHierarchyEntityPK.parentId DESC"),
    @NamedQuery(name = "EquipmentHierarchyEntity.removeByChildId", query = "DELETE FROM EquipmentHierarchyEntity e WHERE e.equipmentHierarchyEntityPK.childId = :childId"),
    @NamedQuery(name = "EquipmentHierarchyEntity.countChild", query = "SELECT COUNT(e.equipmentHierarchyEntityPK.parentId) FROM EquipmentHierarchyEntity e WHERE e.equipmentHierarchyEntityPK.parentId = :parentId")})
public class EquipmentHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected EquipmentHierarchyEntityPK equipmentHierarchyEntityPK;

    public EquipmentHierarchyEntity() {
    }

    public EquipmentHierarchyEntity(EquipmentHierarchyEntityPK equipmentHierarchyEntityPK) {
        this.equipmentHierarchyEntityPK = equipmentHierarchyEntityPK;
    }

    public EquipmentHierarchyEntity(long parentId, long childId) {
        this.equipmentHierarchyEntityPK = new EquipmentHierarchyEntityPK(parentId, childId);
    }

    public EquipmentHierarchyEntityPK getEquipmentHierarchyEntityPK() {
        return equipmentHierarchyEntityPK;
    }

    public void setEquipmentHierarchyEntityPK(EquipmentHierarchyEntityPK equipmentHierarchyEntityPK) {
        this.equipmentHierarchyEntityPK = equipmentHierarchyEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (equipmentHierarchyEntityPK != null ? equipmentHierarchyEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EquipmentHierarchyEntity)) {
            return false;
        }
        EquipmentHierarchyEntity other = (EquipmentHierarchyEntity) object;
        if ((this.equipmentHierarchyEntityPK == null && other.equipmentHierarchyEntityPK != null) || (this.equipmentHierarchyEntityPK != null && !this.equipmentHierarchyEntityPK.equals(other.equipmentHierarchyEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "EquipmentHierarchyEntity[ equipmentHierarchyEntityPK=" + equipmentHierarchyEntityPK + " ]";
    }

}
