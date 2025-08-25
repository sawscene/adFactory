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
@Table(name = "tre_workflow_hierarchy")
@XmlRootElement(name = "treeWorkflowHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "TreeWorkflowHierarchyEntity.countChild", query = "SELECT COUNT(t.treeWorkflowHierarchyEntityPK.childId) FROM TreeWorkflowHierarchyEntity t WHERE t.treeWorkflowHierarchyEntityPK.parentId = :parentId"),
    @NamedQuery(name = "TreeWorkflowHierarchyEntity.findAll", query = "SELECT t FROM TreeWorkflowHierarchyEntity t"),
    @NamedQuery(name = "TreeWorkflowHierarchyEntity.findByParentId", query = "SELECT t FROM TreeWorkflowHierarchyEntity t WHERE t.treeWorkflowHierarchyEntityPK.parentId = :parentId"),
    @NamedQuery(name = "TreeWorkflowHierarchyEntity.findByChildId", query = "SELECT t FROM TreeWorkflowHierarchyEntity t WHERE t.treeWorkflowHierarchyEntityPK.childId = :childId"),
    @NamedQuery(name = "TreeWorkflowHierarchyEntity.removeByChildId", query = "DELETE FROM TreeWorkflowHierarchyEntity t WHERE t.treeWorkflowHierarchyEntityPK.childId = :childId")})
public class TreeWorkflowHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TreeWorkflowHierarchyEntityPK treeWorkflowHierarchyEntityPK;

    public TreeWorkflowHierarchyEntity() {
    }

    public TreeWorkflowHierarchyEntity(TreeWorkflowHierarchyEntityPK treeWorkflowHierarchyEntityPK) {
        this.treeWorkflowHierarchyEntityPK = treeWorkflowHierarchyEntityPK;
    }

    public TreeWorkflowHierarchyEntity(long parentId, long childId) {
        this.treeWorkflowHierarchyEntityPK = new TreeWorkflowHierarchyEntityPK(parentId, childId);
    }

    public TreeWorkflowHierarchyEntityPK getTreeWorkflowHierarchyEntityPK() {
        return treeWorkflowHierarchyEntityPK;
    }

    public void setTreeWorkflowHierarchyEntityPK(TreeWorkflowHierarchyEntityPK treeWorkflowHierarchyEntityPK) {
        this.treeWorkflowHierarchyEntityPK = treeWorkflowHierarchyEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (treeWorkflowHierarchyEntityPK != null ? treeWorkflowHierarchyEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TreeWorkflowHierarchyEntity)) {
            return false;
        }
        TreeWorkflowHierarchyEntity other = (TreeWorkflowHierarchyEntity) object;
        if ((this.treeWorkflowHierarchyEntityPK == null && other.treeWorkflowHierarchyEntityPK != null) || (this.treeWorkflowHierarchyEntityPK != null && !this.treeWorkflowHierarchyEntityPK.equals(other.treeWorkflowHierarchyEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.workflow.TreeWorkflowHierarchyEntity[ treeWorkflowHierarchyEntityPK=" + treeWorkflowHierarchyEntityPK + " ]";
    }

}
