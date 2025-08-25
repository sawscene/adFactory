/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.master;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_role")
@XmlRootElement(name = "role")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "RoleEntity.findAll", query = "SELECT r FROM RoleEntity r"),
    @NamedQuery(name = "RoleEntity.findByRoleId", query = "SELECT r FROM RoleEntity r WHERE r.roleId = :roleId"),
    @NamedQuery(name = "RoleEntity.findByRoleName", query = "SELECT r FROM RoleEntity r WHERE r.roleName = :roleName")})
public class RoleEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "role_id")
    private Long roleId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "role_name")
    private String roleName;
    @XmlElementWrapper(name = "roleAuthorities")
    @XmlElement(name = "roleAuthority")
    @Transient
    private List<RoleAuthorityEntity> roleAuthorityEntities;

    public RoleEntity() {
    }

    public RoleEntity(RoleEntity in) {
        this.roleId = in.roleId;
        this.roleName = in.roleName;
        this.roleAuthorityEntities = in.roleAuthorityEntities;
    }

    public RoleEntity(Long roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<RoleAuthorityEntity> getRoleAuthorityEntities() {
        return roleAuthorityEntities;
    }

    public void setRoleAuthorityEntities(List<RoleAuthorityEntity> roleAuthorityEntities) {
        this.roleAuthorityEntities = roleAuthorityEntities;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roleId != null ? roleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoleEntity)) {
            return false;
        }
        RoleEntity other = (RoleEntity) object;
        if ((this.roleId == null && other.roleId != null) || (this.roleId != null && !this.roleId.equals(other.roleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RoleEntity{" + "roleId=" + roleId + ", roleName=" + roleName + ", roleAuthorityEntities=" + roleAuthorityEntities + '}';
    }

}
