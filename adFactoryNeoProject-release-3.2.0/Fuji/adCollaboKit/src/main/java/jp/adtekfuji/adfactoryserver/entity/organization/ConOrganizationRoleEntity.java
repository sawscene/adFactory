/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.organization;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "con_organization_role")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ConOrganizationRoleEntity.findAll", query = "SELECT c FROM ConOrganizationRoleEntity c"),
    @NamedQuery(name = "ConOrganizationRoleEntity.findByFkOrganizationId", query = "SELECT c FROM ConOrganizationRoleEntity c WHERE c.conOrganizationRoleEntityPK.fkOrganizationId = :fkOrganizationId"),
    @NamedQuery(name = "ConOrganizationRoleEntity.findByFkRoleId", query = "SELECT c FROM ConOrganizationRoleEntity c WHERE c.conOrganizationRoleEntityPK.fkRoleId = :fkRoleId"),
    @NamedQuery(name = "ConOrganizationRoleEntity.findRoleId", query = "SELECT c.conOrganizationRoleEntityPK.fkRoleId FROM ConOrganizationRoleEntity c WHERE c.conOrganizationRoleEntityPK.fkOrganizationId = :fkOrganizationId GROUP BY c.conOrganizationRoleEntityPK.fkRoleId"),
    @NamedQuery(name = "ConOrganizationRoleEntity.removeByFkOrganizationId", query = "DELETE FROM ConOrganizationRoleEntity c WHERE c.conOrganizationRoleEntityPK.fkOrganizationId = :fkOrganizationId"),
    @NamedQuery(name = "ConOrganizationRoleEntity.removeByFkRoleId", query = "DELETE FROM ConOrganizationRoleEntity c WHERE c.conOrganizationRoleEntityPK.fkRoleId = :fkRoleId")})
public class ConOrganizationRoleEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConOrganizationRoleEntityPK conOrganizationRoleEntityPK;

    public ConOrganizationRoleEntity() {
    }

    public ConOrganizationRoleEntity(ConOrganizationRoleEntityPK conOrganizationRoleEntityPK) {
        this.conOrganizationRoleEntityPK = conOrganizationRoleEntityPK;
    }

    public ConOrganizationRoleEntity(long fkOrganizationId, long fkRoleId) {
        this.conOrganizationRoleEntityPK = new ConOrganizationRoleEntityPK(fkOrganizationId, fkRoleId);
    }

    public ConOrganizationRoleEntityPK getConOrganizationRoleEntityPK() {
        return conOrganizationRoleEntityPK;
    }

    public void setConOrganizationRoleEntityPK(ConOrganizationRoleEntityPK conOrganizationRoleEntityPK) {
        this.conOrganizationRoleEntityPK = conOrganizationRoleEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conOrganizationRoleEntityPK != null ? conOrganizationRoleEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConOrganizationRoleEntity)) {
            return false;
        }
        ConOrganizationRoleEntity other = (ConOrganizationRoleEntity) object;
        if ((this.conOrganizationRoleEntityPK == null && other.conOrganizationRoleEntityPK != null) || (this.conOrganizationRoleEntityPK != null && !this.conOrganizationRoleEntityPK.equals(other.conOrganizationRoleEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.organization.ConOrganizationRoleEntity[ conOrganizationRoleEntityPK=" + conOrganizationRoleEntityPK + " ]";
    }

}
