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
@Table(name = "tre_work_hierarchy")
@XmlRootElement(name = "treeWorkHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "TreeWorkHierarchyEntity.countChild", query = "SELECT COUNT(t.treeWorkHierarchyEntityPK.childId) FROM TreeWorkHierarchyEntity t WHERE t.treeWorkHierarchyEntityPK.parentId = :parentId"),
    @NamedQuery(name = "TreeWorkHierarchyEntity.findAll", query = "SELECT t FROM TreeWorkHierarchyEntity t"),
    @NamedQuery(name = "TreeWorkHierarchyEntity.findByParentId", query = "SELECT t FROM TreeWorkHierarchyEntity t WHERE t.treeWorkHierarchyEntityPK.parentId = :parentId"),
    @NamedQuery(name = "TreeWorkHierarchyEntity.findByChildId", query = "SELECT t FROM TreeWorkHierarchyEntity t WHERE t.treeWorkHierarchyEntityPK.childId = :childId"),
    @NamedQuery(name = "TreeWorkHierarchyEntity.removeByChildId", query = "DELETE FROM TreeWorkHierarchyEntity t WHERE t.treeWorkHierarchyEntityPK.childId = :childId")})
public class TreeWorkHierarchyEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TreeWorkHierarchyEntityPK treeWorkHierarchyEntityPK;

    public TreeWorkHierarchyEntity() {
    }

    public TreeWorkHierarchyEntity(TreeWorkHierarchyEntityPK treeWorkHierarchyEntityPK) {
        this.treeWorkHierarchyEntityPK = treeWorkHierarchyEntityPK;
    }

    public TreeWorkHierarchyEntity(long parentId, long childId) {
        this.treeWorkHierarchyEntityPK = new TreeWorkHierarchyEntityPK(parentId, childId);
    }

    public TreeWorkHierarchyEntityPK getTreeWorkHierarchyEntityPK() {
        return treeWorkHierarchyEntityPK;
    }

    public void setTreeWorkHierarchyEntityPK(TreeWorkHierarchyEntityPK treeWorkHierarchyEntityPK) {
        this.treeWorkHierarchyEntityPK = treeWorkHierarchyEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (treeWorkHierarchyEntityPK != null ? treeWorkHierarchyEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TreeWorkHierarchyEntity)) {
            return false;
        }
        TreeWorkHierarchyEntity other = (TreeWorkHierarchyEntity) object;
        if ((this.treeWorkHierarchyEntityPK == null && other.treeWorkHierarchyEntityPK != null) || (this.treeWorkHierarchyEntityPK != null && !this.treeWorkHierarchyEntityPK.equals(other.treeWorkHierarchyEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TreeWorkHierarchyEntity[ treeWorkHierarchyEntityPK=" + treeWorkHierarchyEntityPK + " ]";
    }
    
}
