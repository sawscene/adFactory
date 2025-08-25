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
@Table(name = "tre_kanban_hierarchy")
@XmlRootElement(name = "treeKanbanHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "TreeKanbanHierarchyEntity.countChild", query = "SELECT COUNT(t.treeKanbanHierarchyEntityPK.childId) FROM TreeKanbanHierarchyEntity t WHERE t.treeKanbanHierarchyEntityPK.parentId = :parentId"),
    @NamedQuery(name = "TreeKanbanHierarchyEntity.findAll", query = "SELECT t FROM TreeKanbanHierarchyEntity t"),
    @NamedQuery(name = "TreeKanbanHierarchyEntity.findByParentId", query = "SELECT t FROM TreeKanbanHierarchyEntity t WHERE t.treeKanbanHierarchyEntityPK.parentId = :parentId"),
    @NamedQuery(name = "TreeKanbanHierarchyEntity.findByChildId", query = "SELECT t FROM TreeKanbanHierarchyEntity t WHERE t.treeKanbanHierarchyEntityPK.childId = :childId"),
    //@NamedQuery(name = "TreeKanbanHierarchyEntity.removeByParentId", query = "DELETE FROM TreeKanbanHierarchyEntity t WHERE t.treeKanbanHierarchyEntityPK.parentId = :parentId")
    @NamedQuery(name = "TreeKanbanHierarchyEntity.removeByChildId", query = "DELETE FROM TreeKanbanHierarchyEntity t WHERE t.treeKanbanHierarchyEntityPK.childId = :childId")})
public class TreeKanbanHierarchyEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TreeKanbanHierarchyEntityPK treeKanbanHierarchyEntityPK;

    public TreeKanbanHierarchyEntity() {
    }

    public TreeKanbanHierarchyEntity(long parentId, long childId) {
        this.treeKanbanHierarchyEntityPK = new TreeKanbanHierarchyEntityPK(parentId, childId);
    }

    public TreeKanbanHierarchyEntityPK getTreeKanbanHierarchyEntityPK() {
        return treeKanbanHierarchyEntityPK;
    }

    public void setTreeKanbanHierarchyEntityPK(TreeKanbanHierarchyEntityPK treeKanbanHierarchyEntityPK) {
        this.treeKanbanHierarchyEntityPK = treeKanbanHierarchyEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (treeKanbanHierarchyEntityPK != null ? treeKanbanHierarchyEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TreeKanbanHierarchyEntity)) {
            return false;
        }
        TreeKanbanHierarchyEntity other = (TreeKanbanHierarchyEntity) object;
        if ((this.treeKanbanHierarchyEntityPK == null && other.treeKanbanHierarchyEntityPK != null) || (this.treeKanbanHierarchyEntityPK != null && !this.treeKanbanHierarchyEntityPK.equals(other.treeKanbanHierarchyEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.kanban.TreeKanbanHierarchyEntity[ treeKanbanHierarchyEntityPK=" + treeKanbanHierarchyEntityPK + " ]";
    }
    
}
