/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ke.yokoi
 */
@Embeddable
public class TreeWorkflowHierarchyEntityPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "parent_id")
    private long parentId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "child_id")
    private long childId;

    public TreeWorkflowHierarchyEntityPK() {
    }

    public TreeWorkflowHierarchyEntityPK(long parentId, long childId) {
        this.parentId = parentId;
        this.childId = childId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) parentId;
        hash += (int) childId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TreeWorkflowHierarchyEntityPK)) {
            return false;
        }
        TreeWorkflowHierarchyEntityPK other = (TreeWorkflowHierarchyEntityPK) object;
        if (this.parentId != other.parentId) {
            return false;
        }
        if (this.childId != other.childId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.workflow.TreeWorkflowHierarchyEntityPK[ parentId=" + parentId + ", childId=" + childId + " ]";
    }
    
}
