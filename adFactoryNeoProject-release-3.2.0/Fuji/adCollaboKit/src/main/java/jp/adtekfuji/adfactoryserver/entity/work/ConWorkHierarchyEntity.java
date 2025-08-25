/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.work;

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
@Table(name = "con_work_hierarchy")
@XmlRootElement(name = "conWorkHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ConWorkHierarchyEntity.countChild", query = "SELECT COUNT(c.conWorkHierarchyEntityPK.fkWorkId) FROM ConWorkHierarchyEntity c WHERE c.conWorkHierarchyEntityPK.fkWorkHierarchyId =:fkWorkHierarchyId"),
    @NamedQuery(name = "ConWorkHierarchyEntity.findAll", query = "SELECT c FROM ConWorkHierarchyEntity c"),
    @NamedQuery(name = "ConWorkHierarchyEntity.findByFkWorkHierarchyId", query = "SELECT c FROM ConWorkHierarchyEntity c WHERE c.conWorkHierarchyEntityPK.fkWorkHierarchyId = :fkWorkHierarchyId"),
    @NamedQuery(name = "ConWorkHierarchyEntity.findByFkWorkId", query = "SELECT c FROM ConWorkHierarchyEntity c WHERE c.conWorkHierarchyEntityPK.fkWorkId = :fkWorkId"),
    @NamedQuery(name = "ConWorkHierarchyEntity.removeByFkWorkId", query = "DELETE FROM ConWorkHierarchyEntity c WHERE c.conWorkHierarchyEntityPK.fkWorkId = :fkWorkId")})
public class ConWorkHierarchyEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConWorkHierarchyEntityPK conWorkHierarchyEntityPK;

    public ConWorkHierarchyEntity() {
    }

    public ConWorkHierarchyEntity(ConWorkHierarchyEntityPK conWorkHierarchyEntityPK) {
        this.conWorkHierarchyEntityPK = conWorkHierarchyEntityPK;
    }

    public ConWorkHierarchyEntity(long fkWorkHierarchyId, long fkWorkId) {
        this.conWorkHierarchyEntityPK = new ConWorkHierarchyEntityPK(fkWorkHierarchyId, fkWorkId);
    }

    public ConWorkHierarchyEntityPK getConWorkHierarchyEntityPK() {
        return conWorkHierarchyEntityPK;
    }

    public void setConWorkHierarchyEntityPK(ConWorkHierarchyEntityPK conWorkHierarchyEntityPK) {
        this.conWorkHierarchyEntityPK = conWorkHierarchyEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conWorkHierarchyEntityPK != null ? conWorkHierarchyEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConWorkHierarchyEntity)) {
            return false;
        }
        ConWorkHierarchyEntity other = (ConWorkHierarchyEntity) object;
        if ((this.conWorkHierarchyEntityPK == null && other.conWorkHierarchyEntityPK != null) || (this.conWorkHierarchyEntityPK != null && !this.conWorkHierarchyEntityPK.equals(other.conWorkHierarchyEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConWorkHierarchyEntity[ conWorkHierarchyEntityPK=" + conWorkHierarchyEntityPK + " ]";
    }
    
}
