/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.organization;

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
@Table(name = "tre_organization_hierarchy")
@XmlRootElement(name = "organizationHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "OrganizationHierarchyEntity.findAll", query = "SELECT o FROM OrganizationHierarchyEntity o"),
    @NamedQuery(name = "OrganizationHierarchyEntity.findByParentId", query = "SELECT o FROM OrganizationHierarchyEntity o WHERE o.organizationHierarchyEntityPK.parentId = :parentId"),
    @NamedQuery(name = "OrganizationHierarchyEntity.findByChildId", query = "SELECT o FROM OrganizationHierarchyEntity o WHERE o.organizationHierarchyEntityPK.childId = :childId"),
    @NamedQuery(name = "OrganizationHierarchyEntity.removeByChildId", query = "DELETE FROM OrganizationHierarchyEntity o WHERE o.organizationHierarchyEntityPK.childId = :childId"),
    @NamedQuery(name = "OrganizationHierarchyEntity.countChild", query = "SELECT COUNT(o.organizationHierarchyEntityPK.parentId) FROM OrganizationHierarchyEntity o WHERE o.organizationHierarchyEntityPK.parentId = :parentId")})
public class OrganizationHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected OrganizationHierarchyEntityPK organizationHierarchyEntityPK;

    public OrganizationHierarchyEntity() {
    }

    public OrganizationHierarchyEntity(OrganizationHierarchyEntityPK organizationHierarchyEntityPK) {
        this.organizationHierarchyEntityPK = organizationHierarchyEntityPK;
    }

    public OrganizationHierarchyEntity(long parentId, long childId) {
        this.organizationHierarchyEntityPK = new OrganizationHierarchyEntityPK(parentId, childId);
    }

    public OrganizationHierarchyEntityPK getOrganizationHierarchyEntityPK() {
        return organizationHierarchyEntityPK;
    }

    public void setOrganizationHierarchyEntityPK(OrganizationHierarchyEntityPK organizationHierarchyEntityPK) {
        this.organizationHierarchyEntityPK = organizationHierarchyEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (organizationHierarchyEntityPK != null ? organizationHierarchyEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrganizationHierarchyEntity)) {
            return false;
        }
        OrganizationHierarchyEntity other = (OrganizationHierarchyEntity) object;
        if ((this.organizationHierarchyEntityPK == null && other.organizationHierarchyEntityPK != null) || (this.organizationHierarchyEntityPK != null && !this.organizationHierarchyEntityPK.equals(other.organizationHierarchyEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrganizationHierarchyEntity[ organizationHierarchyEntityPK=" + organizationHierarchyEntityPK + " ]";
    }

}
