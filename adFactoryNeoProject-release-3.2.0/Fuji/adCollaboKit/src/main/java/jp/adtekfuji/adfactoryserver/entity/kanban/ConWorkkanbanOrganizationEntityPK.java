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
public class ConWorkkanbanOrganizationEntityPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_workkanban_id")
    private long fkWorkkanbanId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_organization_id")
    private long fkOrganizationId;

    public ConWorkkanbanOrganizationEntityPK() {
    }

    public ConWorkkanbanOrganizationEntityPK(long fkWorkkanbanId, long fkOrganizationId) {
        this.fkWorkkanbanId = fkWorkkanbanId;
        this.fkOrganizationId = fkOrganizationId;
    }

    public long getFkWorkkanbanId() {
        return fkWorkkanbanId;
    }

    public void setFkWorkkanbanId(long fkWorkkanbanId) {
        this.fkWorkkanbanId = fkWorkkanbanId;
    }

    public long getFkOrganizationId() {
        return fkOrganizationId;
    }

    public void setFkOrganizationId(long fkOrganizationId) {
        this.fkOrganizationId = fkOrganizationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) fkWorkkanbanId;
        hash += (int) fkOrganizationId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConWorkkanbanOrganizationEntityPK)) {
            return false;
        }
        ConWorkkanbanOrganizationEntityPK other = (ConWorkkanbanOrganizationEntityPK) object;
        if (this.fkWorkkanbanId != other.fkWorkkanbanId) {
            return false;
        }
        if (this.fkOrganizationId != other.fkOrganizationId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.kanban.ConWorkkanbanOrganizationEntityPK[ fkWorkkanbanId=" + fkWorkkanbanId + ", fkOrganizationId=" + fkOrganizationId + " ]";
    }
    
}
