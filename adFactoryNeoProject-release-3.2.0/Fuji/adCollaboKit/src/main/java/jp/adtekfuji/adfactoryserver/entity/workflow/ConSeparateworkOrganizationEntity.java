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
@Table(name = "con_separatework_organization")
@XmlRootElement(name = "conSeparateworkOrganization")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ConSeparateworkOrganizationEntity.findAll", query = "SELECT c FROM ConSeparateworkOrganizationEntity c"),
    @NamedQuery(name = "ConSeparateworkOrganizationEntity.findByFkWorkId", query = "SELECT c FROM ConSeparateworkOrganizationEntity c WHERE c.conSeparateworkOrganizationEntityPK.fkWorkId = :fkWorkId"),
    @NamedQuery(name = "ConSeparateworkOrganizationEntity.findByFkOrganizationId", query = "SELECT c FROM ConSeparateworkOrganizationEntity c WHERE c.conSeparateworkOrganizationEntityPK.fkOrganizationId = :fkOrganizationId"),
    @NamedQuery(name = "ConSeparateworkOrganizationEntity.findOrganizationId", query = "SELECT c.conSeparateworkOrganizationEntityPK.fkOrganizationId FROM ConSeparateworkOrganizationEntity c WHERE c.conSeparateworkOrganizationEntityPK.fkWorkId = :fkWorkId ORDER BY c.conSeparateworkOrganizationEntityPK.fkOrganizationId"),
    @NamedQuery(name = "ConSeparateworkOrganizationEntity.removeByFkWorkId", query = "DELETE FROM ConSeparateworkOrganizationEntity c WHERE c.conSeparateworkOrganizationEntityPK.fkWorkId = :fkWorkId")})
public class ConSeparateworkOrganizationEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConSeparateworkOrganizationEntityPK conSeparateworkOrganizationEntityPK;

    public ConSeparateworkOrganizationEntity() {
    }

    public ConSeparateworkOrganizationEntity(ConSeparateworkOrganizationEntityPK conSeparateworkOrganizationEntityPK) {
        this.conSeparateworkOrganizationEntityPK = conSeparateworkOrganizationEntityPK;
    }

    public ConSeparateworkOrganizationEntity(long fkWorkId, long fkOrganizationId) {
        this.conSeparateworkOrganizationEntityPK = new ConSeparateworkOrganizationEntityPK(fkWorkId, fkOrganizationId);
    }

    public ConSeparateworkOrganizationEntityPK getConSeparateworkOrganizationEntityPK() {
        return conSeparateworkOrganizationEntityPK;
    }

    public void setConSeparateworkOrganizationEntityPK(ConSeparateworkOrganizationEntityPK conSeparateworkOrganizationEntityPK) {
        this.conSeparateworkOrganizationEntityPK = conSeparateworkOrganizationEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conSeparateworkOrganizationEntityPK != null ? conSeparateworkOrganizationEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConSeparateworkOrganizationEntity)) {
            return false;
        }
        ConSeparateworkOrganizationEntity other = (ConSeparateworkOrganizationEntity) object;
        if ((this.conSeparateworkOrganizationEntityPK == null && other.conSeparateworkOrganizationEntityPK != null) || (this.conSeparateworkOrganizationEntityPK != null && !this.conSeparateworkOrganizationEntityPK.equals(other.conSeparateworkOrganizationEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.workflow.ConSeparateworkOrganizationEntity[ conSeparateworkOrganizationEntityPK=" + conSeparateworkOrganizationEntityPK + " ]";
    }
    
}
