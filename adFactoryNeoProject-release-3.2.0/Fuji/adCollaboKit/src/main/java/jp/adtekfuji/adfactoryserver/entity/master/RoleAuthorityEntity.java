/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.master;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_role_authority")
@XmlRootElement(name = "roleAuthority")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "RoleAuthorityEntity.findAll", query = "SELECT r FROM RoleAuthorityEntity r"),
    @NamedQuery(name = "RoleAuthorityEntity.findByRoleAuthorityId", query = "SELECT r FROM RoleAuthorityEntity r WHERE r.roleAuthorityId = :roleAuthorityId"),
    @NamedQuery(name = "RoleAuthorityEntity.findByFkRoleId", query = "SELECT r FROM RoleAuthorityEntity r WHERE r.fkRoleId = :fkRoleId"),
    @NamedQuery(name = "RoleAuthorityEntity.findByAuthorityType", query = "SELECT r FROM RoleAuthorityEntity r WHERE r.authorityType = :authorityType"),
    @NamedQuery(name = "RoleAuthorityEntity.findByAuthorityEnable", query = "SELECT r FROM RoleAuthorityEntity r WHERE r.authorityEnable = :authorityEnable"),
    @NamedQuery(name = "RoleAuthorityEntity.removeByFkRoleId", query = "DELETE FROM RoleAuthorityEntity r WHERE r.fkRoleId = :fkRoleId")})
public class RoleAuthorityEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "role_authority_id")
    private Long roleAuthorityId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_role_id")
    private Long fkRoleId;
    //@Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @NotNull
    @Column(name = "authority_type")
    private String authorityType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "authority_enable")
    private Boolean authorityEnable;

    public RoleAuthorityEntity() {
    }

    public RoleAuthorityEntity(Long roleAuthorityId) {
        this.roleAuthorityId = roleAuthorityId;
    }

    public RoleAuthorityEntity(Long roleAuthorityId, Long fkRoleId, RoleAuthorityType authorityType, Boolean authorityEnable) {
        this.roleAuthorityId = roleAuthorityId;
        this.fkRoleId = fkRoleId;
        this.authorityType = authorityType.getName();
        this.authorityEnable = authorityEnable;
    }

    public Long getRoleAuthorityId() {
        return roleAuthorityId;
    }

    public void setRoleAuthorityId(Long roleAuthorityId) {
        this.roleAuthorityId = roleAuthorityId;
    }

    public Long getFkRoleId() {
        return fkRoleId;
    }

    public void setFkRoleId(Long fkRoleId) {
        this.fkRoleId = fkRoleId;
    }

    public String getAuthorityType() {
        return authorityType;
    }

    public void setAuthorityType(String authorityType) {
        this.authorityType = authorityType;
    }

    public Boolean getAuthorityEnable() {
        return authorityEnable;
    }

    public void setAuthorityEnable(Boolean authorityEnable) {
        this.authorityEnable = authorityEnable;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roleAuthorityId != null ? roleAuthorityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoleAuthorityEntity)) {
            return false;
        }
        RoleAuthorityEntity other = (RoleAuthorityEntity) object;
        if ((this.roleAuthorityId == null && other.roleAuthorityId != null) || (this.roleAuthorityId != null && !this.roleAuthorityId.equals(other.roleAuthorityId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RoleAuthorityEntity{" + "roleAuthorityId=" + roleAuthorityId + ", fkRoleId=" + fkRoleId + ", authorityName=" + authorityType + ", authorityEnable=" + authorityEnable + '}';
    }

}
