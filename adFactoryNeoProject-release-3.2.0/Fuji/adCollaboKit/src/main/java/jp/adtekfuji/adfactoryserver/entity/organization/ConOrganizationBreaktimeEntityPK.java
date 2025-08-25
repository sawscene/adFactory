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
public class ConOrganizationBreaktimeEntityPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_organization_id")
    private long fkOrganizationId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_breaktime_id")
    private long fkBreaktimeId;

    public ConOrganizationBreaktimeEntityPK() {
    }

    public ConOrganizationBreaktimeEntityPK(long fkOrganizationId, long fkBreaktimeId) {
        this.fkOrganizationId = fkOrganizationId;
        this.fkBreaktimeId = fkBreaktimeId;
    }

    public long getFkOrganizationId() {
        return fkOrganizationId;
    }

    public void setFkOrganizationId(long fkOrganizationId) {
        this.fkOrganizationId = fkOrganizationId;
    }

    public long getFkBreaktimeId() {
        return fkBreaktimeId;
    }

    public void setFkBreaktimeId(long fkBreaktimeId) {
        this.fkBreaktimeId = fkBreaktimeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) fkOrganizationId;
        hash += (int) fkBreaktimeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConOrganizationBreaktimeEntityPK)) {
            return false;
        }
        ConOrganizationBreaktimeEntityPK other = (ConOrganizationBreaktimeEntityPK) object;
        if (this.fkOrganizationId != other.fkOrganizationId) {
            return false;
        }
        if (this.fkBreaktimeId != other.fkBreaktimeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConOrganizationBreaktimeEntityPK[ fkOrganizationId=" + fkOrganizationId + ", fkBreaktimeId=" + fkBreaktimeId + " ]";
    }

}
