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
@Table(name = "con_work_organization")
@XmlRootElement(name = "conWorkOrganization")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ConWorkOrganizationEntity.findAll", query = "SELECT c FROM ConWorkOrganizationEntity c"),
    @NamedQuery(name = "ConWorkOrganizationEntity.findByFkWorkId", query = "SELECT c FROM ConWorkOrganizationEntity c WHERE c.conWorkOrganizationEntityPK.fkWorkId = :fkWorkId"),
    @NamedQuery(name = "ConWorkOrganizationEntity.findByFkOrganizationId", query = "SELECT c FROM ConWorkOrganizationEntity c WHERE c.conWorkOrganizationEntityPK.fkOrganizationId = :fkOrganizationId"),
    @NamedQuery(name = "ConWorkOrganizationEntity.findOrganizationId", query = "SELECT c.conWorkOrganizationEntityPK.fkOrganizationId FROM ConWorkOrganizationEntity c WHERE c.conWorkOrganizationEntityPK.fkWorkId = :fkWorkId ORDER BY c.conWorkOrganizationEntityPK.fkOrganizationId"),
    @NamedQuery(name = "ConWorkOrganizationEntity.removeByFkWorkId", query = "DELETE FROM ConWorkOrganizationEntity c WHERE c.conWorkOrganizationEntityPK.fkWorkId = :fkWorkId")})
public class ConWorkOrganizationEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConWorkOrganizationEntityPK conWorkOrganizationEntityPK;

    public ConWorkOrganizationEntity() {
    }

    public ConWorkOrganizationEntity(ConWorkOrganizationEntityPK conWorkOrganizationEntityPK) {
        this.conWorkOrganizationEntityPK = conWorkOrganizationEntityPK;
    }

    public ConWorkOrganizationEntity(long fkWorkId, long fkOrganizationId) {
        this.conWorkOrganizationEntityPK = new ConWorkOrganizationEntityPK(fkWorkId, fkOrganizationId);
    }

    public ConWorkOrganizationEntityPK getConWorkOrganizationEntityPK() {
        return conWorkOrganizationEntityPK;
    }

    public void setConWorkOrganizationEntityPK(ConWorkOrganizationEntityPK conWorkOrganizationEntityPK) {
        this.conWorkOrganizationEntityPK = conWorkOrganizationEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conWorkOrganizationEntityPK != null ? conWorkOrganizationEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConWorkOrganizationEntity)) {
            return false;
        }
        ConWorkOrganizationEntity other = (ConWorkOrganizationEntity) object;
        if ((this.conWorkOrganizationEntityPK == null && other.conWorkOrganizationEntityPK != null) || (this.conWorkOrganizationEntityPK != null && !this.conWorkOrganizationEntityPK.equals(other.conWorkOrganizationEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.work.ConWorkOrganizationEntity[ conWorkOrganizationEntityPK=" + conWorkOrganizationEntityPK + " ]";
    }
    
}
