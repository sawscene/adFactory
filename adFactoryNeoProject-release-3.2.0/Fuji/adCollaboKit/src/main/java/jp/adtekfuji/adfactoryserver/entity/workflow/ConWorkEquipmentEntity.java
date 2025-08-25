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
@Table(name = "con_work_equipment")
@XmlRootElement(name = "conWorkEquipment")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ConWorkEquipmentEntity.findAll", query = "SELECT c FROM ConWorkEquipmentEntity c"),
    @NamedQuery(name = "ConWorkEquipmentEntity.findByFkWorkId", query = "SELECT c FROM ConWorkEquipmentEntity c WHERE c.conWorkEquipmentEntityPK.fkWorkId = :fkWorkId"),
    @NamedQuery(name = "ConWorkEquipmentEntity.findByFkEquipmentId", query = "SELECT c FROM ConWorkEquipmentEntity c WHERE c.conWorkEquipmentEntityPK.fkEquipmentId = :fkEquipmentId"),
    @NamedQuery(name = "ConWorkEquipmentEntity.findEquipmentId", query = "SELECT c.conWorkEquipmentEntityPK.fkEquipmentId FROM ConWorkEquipmentEntity c WHERE c.conWorkEquipmentEntityPK.fkWorkId = :fkWorkId ORDER BY c.conWorkEquipmentEntityPK.fkEquipmentId"),
    @NamedQuery(name = "ConWorkEquipmentEntity.removeByFkWorkId", query = "DELETE FROM ConWorkEquipmentEntity c WHERE c.conWorkEquipmentEntityPK.fkWorkId = :fkWorkId")})
public class ConWorkEquipmentEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConWorkEquipmentEntityPK conWorkEquipmentEntityPK;

    public ConWorkEquipmentEntity() {
    }

    public ConWorkEquipmentEntity(ConWorkEquipmentEntityPK conWorkEquipmentEntityPK) {
        this.conWorkEquipmentEntityPK = conWorkEquipmentEntityPK;
    }

    public ConWorkEquipmentEntity(long fkWorkId, long fkEquipmentId) {
        this.conWorkEquipmentEntityPK = new ConWorkEquipmentEntityPK(fkWorkId, fkEquipmentId);
    }

    public ConWorkEquipmentEntityPK getConWorkEquipmentEntityPK() {
        return conWorkEquipmentEntityPK;
    }

    public void setConWorkEquipmentEntityPK(ConWorkEquipmentEntityPK conWorkEquipmentEntityPK) {
        this.conWorkEquipmentEntityPK = conWorkEquipmentEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conWorkEquipmentEntityPK != null ? conWorkEquipmentEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConWorkEquipmentEntity)) {
            return false;
        }
        ConWorkEquipmentEntity other = (ConWorkEquipmentEntity) object;
        if ((this.conWorkEquipmentEntityPK == null && other.conWorkEquipmentEntityPK != null) || (this.conWorkEquipmentEntityPK != null && !this.conWorkEquipmentEntityPK.equals(other.conWorkEquipmentEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.work.ConWorkEquipmentEntity[ conWorkEquipmentEntityPK=" + conWorkEquipmentEntityPK + " ]";
    }
    
}
