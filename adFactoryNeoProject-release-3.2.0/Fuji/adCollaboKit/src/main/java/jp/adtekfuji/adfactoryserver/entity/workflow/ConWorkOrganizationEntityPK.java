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
public class ConWorkOrganizationEntityPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_work_id")
    private long fkWorkId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_organization_id")
    private long fkOrganizationId;

    public ConWorkOrganizationEntityPK() {
    }

    public ConWorkOrganizationEntityPK(long fkWorkId, long fkOrganizationId) {
        this.fkWorkId = fkWorkId;
        this.fkOrganizationId = fkOrganizationId;
    }

    public long getFkWorkId() {
        return fkWorkId;
    }

    public void setFkWorkId(long fkWorkId) {
        this.fkWorkId = fkWorkId;
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
        hash += (int) fkWorkId;
        hash += (int) fkOrganizationId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConWorkOrganizationEntityPK)) {
            return false;
        }
        ConWorkOrganizationEntityPK other = (ConWorkOrganizationEntityPK) object;
        if (this.fkWorkId != other.fkWorkId) {
            return false;
        }
        if (this.fkOrganizationId != other.fkOrganizationId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.work.ConWorkOrganizationEntityPK[ fkWorkId=" + fkWorkId + ", fkOrganizationId=" + fkOrganizationId + " ]";
    }
    
}
