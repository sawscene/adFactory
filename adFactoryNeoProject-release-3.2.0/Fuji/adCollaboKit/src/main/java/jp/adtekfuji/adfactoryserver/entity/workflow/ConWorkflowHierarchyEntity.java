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
@Table(name = "con_workflow_hierarchy")
@XmlRootElement(name = "conWorkflowHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ConWorkflowHierarchyEntity.countChild", query = "SELECT COUNT(c.conWorkflowHierarchyEntityPK.fkWorkflowId) FROM ConWorkflowHierarchyEntity c WHERE c.conWorkflowHierarchyEntityPK.fkWorkflowHierarchyId = :fkWorkflowHierarchyId"),
    @NamedQuery(name = "ConWorkflowHierarchyEntity.findAll", query = "SELECT c FROM ConWorkflowHierarchyEntity c"),
    @NamedQuery(name = "ConWorkflowHierarchyEntity.findByFkWorkflowHierarchyId", query = "SELECT c FROM ConWorkflowHierarchyEntity c WHERE c.conWorkflowHierarchyEntityPK.fkWorkflowHierarchyId = :fkWorkflowHierarchyId"),
    @NamedQuery(name = "ConWorkflowHierarchyEntity.findByFkWorkflowId", query = "SELECT c FROM ConWorkflowHierarchyEntity c WHERE c.conWorkflowHierarchyEntityPK.fkWorkflowId = :fkWorkflowId"),
    @NamedQuery(name = "ConWorkflowHierarchyEntity.removeByFkWorkflowId", query = "DELETE FROM ConWorkflowHierarchyEntity c WHERE c.conWorkflowHierarchyEntityPK.fkWorkflowId = :fkWorkflowId")})
public class ConWorkflowHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConWorkflowHierarchyEntityPK conWorkflowHierarchyEntityPK;

    public ConWorkflowHierarchyEntity() {
    }

    public ConWorkflowHierarchyEntity(ConWorkflowHierarchyEntityPK conWorkflowHierarchyEntityPK) {
        this.conWorkflowHierarchyEntityPK = conWorkflowHierarchyEntityPK;
    }

    public ConWorkflowHierarchyEntity(long fkWorkflowHierarchyId, long fkWorkflowId) {
        this.conWorkflowHierarchyEntityPK = new ConWorkflowHierarchyEntityPK(fkWorkflowHierarchyId, fkWorkflowId);
    }

    public ConWorkflowHierarchyEntityPK getConWorkflowHierarchyEntityPK() {
        return conWorkflowHierarchyEntityPK;
    }

    public void setConWorkflowHierarchyEntityPK(ConWorkflowHierarchyEntityPK conWorkflowHierarchyEntityPK) {
        this.conWorkflowHierarchyEntityPK = conWorkflowHierarchyEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conWorkflowHierarchyEntityPK != null ? conWorkflowHierarchyEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConWorkflowHierarchyEntity)) {
            return false;
        }
        ConWorkflowHierarchyEntity other = (ConWorkflowHierarchyEntity) object;
        if ((this.conWorkflowHierarchyEntityPK == null && other.conWorkflowHierarchyEntityPK != null) || (this.conWorkflowHierarchyEntityPK != null && !this.conWorkflowHierarchyEntityPK.equals(other.conWorkflowHierarchyEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowHierarchyEntity[ conWorkflowHierarchyEntityPK=" + conWorkflowHierarchyEntityPK + " ]";
    }

}
