/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unit;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * 生産ユニット階層関連付け情報(プライマリーキー)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
@Embeddable
public class ConUnitHierarchyEntityPK implements Serializable {
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_unit_hierarchy_id")
    private long fkUnitHierarchyId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_unit_id")
    private long fkUnitId;

    public ConUnitHierarchyEntityPK() {
    }

    public ConUnitHierarchyEntityPK(long fkUnitHierarchyId, long fkUnitId) {
        this.fkUnitHierarchyId = fkUnitHierarchyId;
        this.fkUnitId = fkUnitId;
    }

    public long getFkUnitHierarchyId() {
        return fkUnitHierarchyId;
    }

    public void setFkUnitHierarchyId(long fkUnitHierarchyId) {
        this.fkUnitHierarchyId = fkUnitHierarchyId;
    }

    public long getFkUnitId() {
        return fkUnitId;
    }

    public void setFkUnitId(long fkUnitId) {
        this.fkUnitId = fkUnitId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) fkUnitHierarchyId;
        hash += (int) fkUnitId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConUnitHierarchyEntityPK)) {
            return false;
        }
        ConUnitHierarchyEntityPK other = (ConUnitHierarchyEntityPK) object;
        if (this.fkUnitHierarchyId != other.fkUnitHierarchyId) {
            return false;
        }
        return this.fkUnitId == other.fkUnitId;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryforfujiserver.entity.unit.ConUnitHierarchyEntitiyPK[ fkUnitHierarchyId=" + fkUnitHierarchyId + ", fkUnitId=" + fkUnitId + " ]";
    }
    
}
