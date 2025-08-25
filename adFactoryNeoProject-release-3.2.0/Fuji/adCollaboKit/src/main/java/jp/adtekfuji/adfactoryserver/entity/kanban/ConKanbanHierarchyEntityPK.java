/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

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
public class ConKanbanHierarchyEntityPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_kanban_hierarchy_id")
    private long fkKanbanHierarchyId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_kanban_id")
    private long fkKanbanId;

    public ConKanbanHierarchyEntityPK() {
    }

    public ConKanbanHierarchyEntityPK(long fkKanbanHierarchyId, long fkKanbanId) {
        this.fkKanbanHierarchyId = fkKanbanHierarchyId;
        this.fkKanbanId = fkKanbanId;
    }

    public long getFkKanbanHierarchyId() {
        return fkKanbanHierarchyId;
    }

    public void setFkKanbanHierarchyId(long fkKanbanHierarchyId) {
        this.fkKanbanHierarchyId = fkKanbanHierarchyId;
    }

    public long getFkKanbanId() {
        return fkKanbanId;
    }

    public void setFkKanbanId(long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) fkKanbanHierarchyId;
        hash += (int) fkKanbanId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConKanbanHierarchyEntityPK)) {
            return false;
        }
        ConKanbanHierarchyEntityPK other = (ConKanbanHierarchyEntityPK) object;
        if (this.fkKanbanHierarchyId != other.fkKanbanHierarchyId) {
            return false;
        }
        if (this.fkKanbanId != other.fkKanbanId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.kanban.ConKanbanHierarchyEntityPK[ fkKanbanHierarchyId=" + fkKanbanHierarchyId + ", fkKanbanId=" + fkKanbanId + " ]";
    }
    
}
