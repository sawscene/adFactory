/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.organization;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 組織・役割関連付け情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "con_organization_role")
@XmlRootElement(name = "conOrganizationRole")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 組織IDを指定して、役割ID一覧を取得する。
    @NamedQuery(name = "ConOrganizationRoleEntity.findRoleId", query = "SELECT c.roleId FROM ConOrganizationRoleEntity c WHERE c.organizationId = :organizationId GROUP BY c.roleId"),
    // 組織IDを指定して、組織・役割関連付け情報を削除する。
    @NamedQuery(name = "ConOrganizationRoleEntity.removeByOrganizationId", query = "DELETE FROM ConOrganizationRoleEntity c WHERE c.organizationId = :organizationId"),
    // 役割IDを指定して、組織・役割関連付け情報を削除する。
    @NamedQuery(name = "ConOrganizationRoleEntity.removeByRoleId", query = "DELETE FROM ConOrganizationRoleEntity c WHERE c.roleId = :roleId"),})
public class ConOrganizationRoleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "organization_id")
    private long organizationId;// 組織ID

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "role_id")
    private long roleId;// 役割ID

    /**
     * コンストラクタ
     */
    public ConOrganizationRoleEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param organizationId 組織ID
     * @param roleId 役割ID
     */
    public ConOrganizationRoleEntity(long organizationId, long roleId) {
        this.organizationId = organizationId;
        this.roleId = roleId;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public long getOrganizationId() {
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 役割IDを取得する。
     *
     * @return 役割ID
     */
    public long getRoleId() {
        return this.roleId;
    }

    /**
     * 役割IDを設定する。
     *
     * @param roleId 役割ID
     */
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (int) (this.organizationId ^ (this.organizationId >>> 32));
        hash = 59 * hash + (int) (this.roleId ^ (this.roleId >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConOrganizationRoleEntity other = (ConOrganizationRoleEntity) obj;
        if (this.organizationId != other.organizationId) {
            return false;
        }
        if (this.roleId != other.roleId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ConOrganizationRoleEntity{")
                .append("organizationId=").append(this.organizationId)
                .append(", ")
                .append("roleId=").append(this.roleId)
                .append("}")
                .toString();
    }
}
