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
@Table(name = "con_workkanban_organization")
@XmlRootElement(name = "conWorkkanbanOrganization")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ConWorkkanbanOrganizationEntity.findAll", query = "SELECT c FROM ConWorkkanbanOrganizationEntity c"),
    @NamedQuery(name = "ConWorkkanbanOrganizationEntity.findByFkWorkkanbanId", query = "SELECT c FROM ConWorkkanbanOrganizationEntity c WHERE c.conWorkkanbanOrganizationEntityPK.fkWorkkanbanId = :fkWorkkanbanId"),
    @NamedQuery(name = "ConWorkkanbanOrganizationEntity.findByFkOrganizationId", query = "SELECT c FROM ConWorkkanbanOrganizationEntity c WHERE c.conWorkkanbanOrganizationEntityPK.fkOrganizationId = :fkOrganizationId"),
    @NamedQuery(name = "ConWorkkanbanOrganizationEntity.findOrganizationIdByWorkkanbanId", query = "SELECT c.conWorkkanbanOrganizationEntityPK.fkOrganizationId FROM ConWorkkanbanOrganizationEntity c WHERE c.conWorkkanbanOrganizationEntityPK.fkWorkkanbanId = :fkWorkkanbanId GROUP BY c.conWorkkanbanOrganizationEntityPK.fkOrganizationId"),
    @NamedQuery(name = "ConWorkkanbanOrganizationEntity.removeByFkWorkkanbanId", query = "DELETE FROM ConWorkkanbanOrganizationEntity c WHERE c.conWorkkanbanOrganizationEntityPK.fkWorkkanbanId = :fkWorkkanbanId"),
    @NamedQuery(name = "ConWorkkanbanOrganizationEntity.findOrganizationId", query = "SELECT c.conWorkkanbanOrganizationEntityPK.fkOrganizationId FROM ConWorkkanbanOrganizationEntity c WHERE c.conWorkkanbanOrganizationEntityPK.fkWorkkanbanId IN :fkWorkkanbanIds GROUP BY c.conWorkkanbanOrganizationEntityPK.fkOrganizationId"),
})
public class ConWorkkanbanOrganizationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConWorkkanbanOrganizationEntityPK conWorkkanbanOrganizationEntityPK;

    public ConWorkkanbanOrganizationEntity() {
    }

    public ConWorkkanbanOrganizationEntity(long fkWorkkanbanId, long fkOrganizationId) {
        this.conWorkkanbanOrganizationEntityPK = new ConWorkkanbanOrganizationEntityPK(fkWorkkanbanId, fkOrganizationId);
    }

    public ConWorkkanbanOrganizationEntityPK getConWorkkanbanOrganizationEntityPK() {
        return conWorkkanbanOrganizationEntityPK;
    }

    public void setConWorkkanbanOrganizationEntityPK(ConWorkkanbanOrganizationEntityPK conWorkkanbanOrganizationEntityPK) {
        this.conWorkkanbanOrganizationEntityPK = conWorkkanbanOrganizationEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conWorkkanbanOrganizationEntityPK != null ? conWorkkanbanOrganizationEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConWorkkanbanOrganizationEntity)) {
            return false;
        }
        ConWorkkanbanOrganizationEntity other = (ConWorkkanbanOrganizationEntity) object;
        if ((this.conWorkkanbanOrganizationEntityPK == null && other.conWorkkanbanOrganizationEntityPK != null) || (this.conWorkkanbanOrganizationEntityPK != null && !this.conWorkkanbanOrganizationEntityPK.equals(other.conWorkkanbanOrganizationEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConWorkkanbanOrganizationEntity{" + "conWorkkanbanOrganizationEntityPK=" + conWorkkanbanOrganizationEntityPK + '}';
    }

}
