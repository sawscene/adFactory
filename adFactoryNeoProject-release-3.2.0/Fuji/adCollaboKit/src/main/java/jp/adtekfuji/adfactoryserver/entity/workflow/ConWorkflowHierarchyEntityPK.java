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
public class ConWorkflowHierarchyEntityPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_workflow_hierarchy_id")
    private long fkWorkflowHierarchyId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_workflow_id")
    private long fkWorkflowId;

    public ConWorkflowHierarchyEntityPK() {
    }

    public ConWorkflowHierarchyEntityPK(long fkWorkflowHierarchyId, long fkWorkflowId) {
        this.fkWorkflowHierarchyId = fkWorkflowHierarchyId;
        this.fkWorkflowId = fkWorkflowId;
    }

    public long getFkWorkflowHierarchyId() {
        return fkWorkflowHierarchyId;
    }

    public void setFkWorkflowHierarchyId(long fkWorkflowHierarchyId) {
        this.fkWorkflowHierarchyId = fkWorkflowHierarchyId;
    }

    public long getFkWorkflowId() {
        return fkWorkflowId;
    }

    public void setFkWorkflowId(long fkWorkflowId) {
        this.fkWorkflowId = fkWorkflowId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) fkWorkflowHierarchyId;
        hash += (int) fkWorkflowId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConWorkflowHierarchyEntityPK)) {
            return false;
        }
        ConWorkflowHierarchyEntityPK other = (ConWorkflowHierarchyEntityPK) object;
        if (this.fkWorkflowHierarchyId != other.fkWorkflowHierarchyId) {
            return false;
        }
        if (this.fkWorkflowId != other.fkWorkflowId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowHierarchyEntityPK[ fkWorkflowHierarchyId=" + fkWorkflowHierarchyId + ", fkWorkflowId=" + fkWorkflowId + " ]";
    }
    
}
