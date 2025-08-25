/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

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
@Table(name = "con_separatework_equipment")
@XmlRootElement(name = "conSeparateworkEquipment")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ConSeparateworkEquipmentEntity.findAll", query = "SELECT c FROM ConSeparateworkEquipmentEntity c"),
    @NamedQuery(name = "ConSeparateworkEquipmentEntity.findByFkWorkId", query = "SELECT c FROM ConSeparateworkEquipmentEntity c WHERE c.conSeparateworkEquipmentEntityPK.fkWorkId = :fkWorkId"),
    @NamedQuery(name = "ConSeparateworkEquipmentEntity.findByFkEquipmentId", query = "SELECT c FROM ConSeparateworkEquipmentEntity c WHERE c.conSeparateworkEquipmentEntityPK.fkEquipmentId = :fkEquipmentId"),
    @NamedQuery(name = "ConSeparateworkEquipmentEntity.findEquipmentId", query = "SELECT c.conSeparateworkEquipmentEntityPK.fkEquipmentId FROM ConSeparateworkEquipmentEntity c WHERE c.conSeparateworkEquipmentEntityPK.fkWorkId = :fkWorkId ORDER BY c.conSeparateworkEquipmentEntityPK.fkEquipmentId"),
    @NamedQuery(name = "ConSeparateworkEquipmentEntity.removeByFkWorkId", query = "DELETE FROM ConSeparateworkEquipmentEntity c WHERE c.conSeparateworkEquipmentEntityPK.fkWorkId = :fkWorkId")})
public class ConSeparateworkEquipmentEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConSeparateworkEquipmentEntityPK conSeparateworkEquipmentEntityPK;

    public ConSeparateworkEquipmentEntity() {
    }

    public ConSeparateworkEquipmentEntity(ConSeparateworkEquipmentEntityPK conSeparateworkEquipmentEntityPK) {
        this.conSeparateworkEquipmentEntityPK = conSeparateworkEquipmentEntityPK;
    }

    public ConSeparateworkEquipmentEntity(long fkWorkId, long fkEquipmentId) {
        this.conSeparateworkEquipmentEntityPK = new ConSeparateworkEquipmentEntityPK(fkWorkId, fkEquipmentId);
    }

    public ConSeparateworkEquipmentEntityPK getConSeparateworkEquipmentEntityPK() {
        return conSeparateworkEquipmentEntityPK;
    }

    public void setConSeparateworkEquipmentEntityPK(ConSeparateworkEquipmentEntityPK conSeparateworkEquipmentEntityPK) {
        this.conSeparateworkEquipmentEntityPK = conSeparateworkEquipmentEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conSeparateworkEquipmentEntityPK != null ? conSeparateworkEquipmentEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConSeparateworkEquipmentEntity)) {
            return false;
        }
        ConSeparateworkEquipmentEntity other = (ConSeparateworkEquipmentEntity) object;
        if ((this.conSeparateworkEquipmentEntityPK == null && other.conSeparateworkEquipmentEntityPK != null) || (this.conSeparateworkEquipmentEntityPK != null && !this.conSeparateworkEquipmentEntityPK.equals(other.conSeparateworkEquipmentEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.workflow.ConSeparateworkEquipmentEntity[ conSeparateworkEquipmentEntityPK=" + conSeparateworkEquipmentEntityPK + " ]";
    }
    
}
