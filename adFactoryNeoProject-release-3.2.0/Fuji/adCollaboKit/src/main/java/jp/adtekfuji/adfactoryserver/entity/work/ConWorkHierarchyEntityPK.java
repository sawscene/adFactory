/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.work;

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
public class ConWorkHierarchyEntityPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_work_hierarchy_id")
    private long fkWorkHierarchyId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_work_id")
    private long fkWorkId;

    public ConWorkHierarchyEntityPK() {
    }

    public ConWorkHierarchyEntityPK(long fkWorkHierarchyId, long fkWorkId) {
        this.fkWorkHierarchyId = fkWorkHierarchyId;
        this.fkWorkId = fkWorkId;
    }

    public long getFkWorkHierarchyId() {
        return fkWorkHierarchyId;
    }

    public void setFkWorkHierarchyId(long fkWorkHierarchyId) {
        this.fkWorkHierarchyId = fkWorkHierarchyId;
    }

    public long getFkWorkId() {
        return fkWorkId;
    }

    public void setFkWorkId(long fkWorkId) {
        this.fkWorkId = fkWorkId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) fkWorkHierarchyId;
        hash += (int) fkWorkId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConWorkHierarchyEntityPK)) {
            return false;
        }
        ConWorkHierarchyEntityPK other = (ConWorkHierarchyEntityPK) object;
        if (this.fkWorkHierarchyId != other.fkWorkHierarchyId) {
            return false;
        }
        if (this.fkWorkId != other.fkWorkId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConWorkHierarchyEntityPK[ fkWorkHierarchyId=" + fkWorkHierarchyId + ", fkWorkId=" + fkWorkId + " ]";
    }
    
}
