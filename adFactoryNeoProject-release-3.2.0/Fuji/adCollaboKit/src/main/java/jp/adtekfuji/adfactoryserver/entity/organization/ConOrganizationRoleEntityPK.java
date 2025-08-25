/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.organization;

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
public class ConOrganizationRoleEntityPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_organization_id")
    private long fkOrganizationId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_role_id")
    private long fkRoleId;

    public ConOrganizationRoleEntityPK() {
    }

    public ConOrganizationRoleEntityPK(long fkOrganizationId, long fkRoleId) {
        this.fkOrganizationId = fkOrganizationId;
        this.fkRoleId = fkRoleId;
    }

    public long getFkOrganizationId() {
        return fkOrganizationId;
    }

    public void setFkOrganizationId(long fkOrganizationId) {
        this.fkOrganizationId = fkOrganizationId;
    }

    public long getFkRoleId() {
        return fkRoleId;
    }

    public void setFkRoleId(long fkRoleId) {
        this.fkRoleId = fkRoleId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) fkOrganizationId;
        hash += (int) fkRoleId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConOrganizationRoleEntityPK)) {
            return false;
        }
        ConOrganizationRoleEntityPK other = (ConOrganizationRoleEntityPK) object;
        if (this.fkOrganizationId != other.fkOrganizationId) {
            return false;
        }
        if (this.fkRoleId != other.fkRoleId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.organization.ConOrganizationRoleEntityPK[ fkOrganizationId=" + fkOrganizationId + ", fkRoleId=" + fkRoleId + " ]";
    }
    
}
