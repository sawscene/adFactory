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
public class ConWorkkanbanEquipmentEntityPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_workkanban_id")
    private long fkWorkkanbanId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_equipment_id")
    private long fkEquipmentId;

    public ConWorkkanbanEquipmentEntityPK() {
    }

    public ConWorkkanbanEquipmentEntityPK(long fkWorkkanbanId, long fkEquipmentId) {
        this.fkWorkkanbanId = fkWorkkanbanId;
        this.fkEquipmentId = fkEquipmentId;
    }

    public long getFkWorkkanbanId() {
        return fkWorkkanbanId;
    }

    public void setFkWorkkanbanId(long fkWorkkanbanId) {
        this.fkWorkkanbanId = fkWorkkanbanId;
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
        hash += (int) fkWorkkanbanId;
        hash += (int) fkEquipmentId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConWorkkanbanEquipmentEntityPK)) {
            return false;
        }
        ConWorkkanbanEquipmentEntityPK other = (ConWorkkanbanEquipmentEntityPK) object;
        if (this.fkWorkkanbanId != other.fkWorkkanbanId) {
            return false;
        }
        if (this.fkEquipmentId != other.fkEquipmentId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.kanban.ConWorkkanbanEquipmentEntityPK[ fkWorkkanbanId=" + fkWorkkanbanId + ", fkEquipmentId=" + fkEquipmentId + " ]";
    }
    
}
