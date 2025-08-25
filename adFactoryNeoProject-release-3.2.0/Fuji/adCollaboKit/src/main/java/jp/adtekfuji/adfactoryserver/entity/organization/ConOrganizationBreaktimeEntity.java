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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "con_organization_breaktime")
@XmlRootElement(name = "conOrganizationBreaktime")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ConOrganizationBreaktimeEntity.findAll", query = "SELECT c FROM ConOrganizationBreaktimeEntity c"),
    @NamedQuery(name = "ConOrganizationBreaktimeEntity.findByFkOrganizationId", query = "SELECT c FROM ConOrganizationBreaktimeEntity c WHERE c.conOrganizationBreaktimeEntityPK.fkOrganizationId = :fkOrganizationId"),
    @NamedQuery(name = "ConOrganizationBreaktimeEntity.findByFkBreaktimeId", query = "SELECT c FROM ConOrganizationBreaktimeEntity c WHERE c.conOrganizationBreaktimeEntityPK.fkBreaktimeId = :fkBreaktimeId"),
    @NamedQuery(name = "ConOrganizationBreaktimeEntity.findBreaktimeId", query = "SELECT c.conOrganizationBreaktimeEntityPK.fkBreaktimeId FROM ConOrganizationBreaktimeEntity c WHERE c.conOrganizationBreaktimeEntityPK.fkOrganizationId = :fkOrganizationId GROUP BY c.conOrganizationBreaktimeEntityPK.fkBreaktimeId"),
    @NamedQuery(name = "ConOrganizationBreaktimeEntity.removeByFkOrganizationId", query = "DELETE FROM ConOrganizationBreaktimeEntity c WHERE c.conOrganizationBreaktimeEntityPK.fkOrganizationId = :fkOrganizationId"),
    @NamedQuery(name = "ConOrganizationBreaktimeEntity.removeByFkBreaktimeId", query = "DELETE FROM ConOrganizationBreaktimeEntity c WHERE c.conOrganizationBreaktimeEntityPK.fkBreaktimeId = :fkBreaktimeId")})
public class ConOrganizationBreaktimeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConOrganizationBreaktimeEntityPK conOrganizationBreaktimeEntityPK;

    public ConOrganizationBreaktimeEntity() {
    }

    public ConOrganizationBreaktimeEntity(long fkOrganizationId, long fkBreaktimeId) {
        this.conOrganizationBreaktimeEntityPK = new ConOrganizationBreaktimeEntityPK(fkOrganizationId, fkBreaktimeId);
    }

    public ConOrganizationBreaktimeEntityPK getConOrganizationBreaktimeEntityPK() {
        return conOrganizationBreaktimeEntityPK;
    }

    public void setConOrganizationBreaktimeEntityPK(ConOrganizationBreaktimeEntityPK conOrganizationBreaktimeEntityPK) {
        this.conOrganizationBreaktimeEntityPK = conOrganizationBreaktimeEntityPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conOrganizationBreaktimeEntityPK != null ? conOrganizationBreaktimeEntityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConOrganizationBreaktimeEntity)) {
            return false;
        }
        ConOrganizationBreaktimeEntity other = (ConOrganizationBreaktimeEntity) object;
        if ((this.conOrganizationBreaktimeEntityPK == null && other.conOrganizationBreaktimeEntityPK != null) || (this.conOrganizationBreaktimeEntityPK != null && !this.conOrganizationBreaktimeEntityPK.equals(other.conOrganizationBreaktimeEntityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.adtekfuji.adfactoryserver.entity.organization.ConOrganizationBreaktimeEntity[ conOrganizationBreaktimeEntityPK=" + conOrganizationBreaktimeEntityPK + " ]";
    }

}
