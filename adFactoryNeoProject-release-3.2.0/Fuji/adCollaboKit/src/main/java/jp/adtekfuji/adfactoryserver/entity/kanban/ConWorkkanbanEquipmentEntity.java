/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

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
@Table(name = "con_workkanban_equipment")
@XmlRootElement(name = "conWorkkanbanEquipment")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ConWorkkanbanEquipmentEntity.findAll", query = "SELECT c FROM ConWorkkanbanEquipmentEntity c"),
    @NamedQuery(name = "ConWorkkanbanEquipmentEntity.findByFkWorkkanbanId", query = "SELECT c FROM ConWorkkanbanEquipmentEntity c WHERE c.conWorkkanbanEquipmentEntityPK.fkWorkkanbanId = :fkWorkkanbanId"),
    @NamedQuery(name = "ConWorkkanbanEquipmentEntity.findByFkEquipmentId", query = "SELECT c FROM ConWorkkanbanEquipmentEntity c WHERE c.conWorkkanbanEquipmentEntityPK.fkEquipmentId = :fkEquipmentId"),
    @NamedQuery(name = "ConWorkkanbanEquipmentEntity.findEquipmentIdByWorkkanbanId", query = "SELECT c.conWorkkanbanEquipmentEntityPK.fkEquipmentId FROM ConWorkkanbanEquipmentEntity c WHERE c.conWorkkanbanEquipmentEntityPK.fkWorkkanbanId = :fkWorkkanbanId GROUP BY c.conWorkkanbanEquipmentEntityPK.fkEquipmentId"),
    @NamedQuery(name = "ConWorkkanbanEquipmentEntity.removeByFkWorkkanbanId", query = "DELETE FROM ConWorkkanbanEquipmentEntity c WHERE c.conWorkkanbanEquipmentEntityPK.fkWorkkanbanId = :fkWorkkanbanId")})
public class ConWorkkanbanEquipmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConWorkkanbanEquipmentEntityPK conWorkkanbanEquipmentEntityPK;

    public ConWorkkanbanEquipmentEntity() {
    }

    public ConWorkkanbanEquipmentEntity(long fkWorkkanbanId, long fkEquipmentId) {
        this.conWorkkanbanEquipmentEntityPK = new ConWorkkanbanEquipmentEntityPK(fkWorkkanbanId, fkEquipmentId);
    }

    public ConWorkkanbanEquipmentEntityPK getConWorkkanbanEquipmentEntityPK() {
        return conWorkkanbanEquipmentEntityPK;
    }

    public void setConWorkkanbanEquipmentEntityPK(ConWorkkanbanEquipmentEntityPK conWorkkanbanEquipmentEntityPK) {
        this.conWorkkanbanEquipmentEntityPK = conWorkkanbanEquipmentEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conWorkkanbanEquipmentEntityPK != null ? conWorkkanbanEquipmentEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConWorkkanbanEquipmentEntity)) {
            return false;
        }
        ConWorkkanbanEquipmentEntity other = (ConWorkkanbanEquipmentEntity) object;
        if ((this.conWorkkanbanEquipmentEntityPK == null && other.conWorkkanbanEquipmentEntityPK != null) || (this.conWorkkanbanEquipmentEntityPK != null && !this.conWorkkanbanEquipmentEntityPK.equals(other.conWorkkanbanEquipmentEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConWorkkanbanEquipmentEntity{" + "conWorkkanbanEquipmentEntityPK=" + conWorkkanbanEquipmentEntityPK + '}';
    }

}
