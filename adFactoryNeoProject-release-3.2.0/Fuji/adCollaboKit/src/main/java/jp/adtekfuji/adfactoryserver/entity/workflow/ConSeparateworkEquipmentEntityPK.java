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
public class ConSeparateworkEquipmentEntityPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_work_id")
    private long fkWorkId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_equipment_id")
    private long fkEquipmentId;

    public ConSeparateworkEquipmentEntityPK() {
    }

    public ConSeparateworkEquipmentEntityPK(long fkWorkId, long fkEquipmentId) {
        this.fkWorkId = fkWorkId;
        this.fkEquipmentId = fkEquipmentId;
    }

    public long getFkWorkId() {
        return fkWorkId;
    }

    public void setFkWorkId(long fkWorkId) {
        this.fkWorkId = fkWorkId;
    }

    public long getFkEquipmentId() {
        return fkEquipmentId;
    }

    public void setFkEquipmentId(long fkEquipmentId) {
        this.fkEquipmentId = fkEquipmentId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) fkWorkId;
        hash += (int) fkEquipmentId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConSeparateworkEquipmentEntityPK)) {
            return false;
        }
        ConSeparateworkEquipmentEntityPK other = (ConSeparateworkEquipmentEntityPK) object;
        if (this.fkWorkId != other.fkWorkId) {
            return false;
        }
        if (this.fkEquipmentId != other.fkEquipmentId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.workflow.ConSeparateworkEquipmentEntityPK[ fkWorkId=" + fkWorkId + ", fkEquipmentId=" + fkEquipmentId + " ]";
    }
    
}
