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
 * 組織・作業区分関連付け情報
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "con_organization_work_category")
@XmlRootElement(name = "conOrganizationWorkCategory")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 組織IDを指定して、作業区分ID一覧を取得する。
    @NamedQuery(name = "ConOrganizationWorkCategoryEntity.findWorkCategoryId", query = "SELECT c.workCategoryId FROM ConOrganizationWorkCategoryEntity c WHERE c.organizationId = :organizationId"),
    // 作業区分IDを指定して、組織・作業区分関連付け情報を取得する。
    @NamedQuery(name = "ConOrganizationWorkCategoryEntity.findByWorkCategoryId", query = "SELECT c FROM ConOrganizationWorkCategoryEntity c WHERE c.workCategoryId = :workCategoryId"),
    // 組織IDを指定して、組織・作業区分関連付け情報を削除する。
    @NamedQuery(name = "ConOrganizationWorkCategoryEntity.removeByOrganizationId", query = "DELETE FROM ConOrganizationWorkCategoryEntity c WHERE c.organizationId = :organizationId")
})
public class ConOrganizationWorkCategoryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "organization_id")
    private long organizationId;// 組織ID

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_category_id")
    private long workCategoryId;// 作業区分ID

    /**
     * コンストラクタ
     */
    public ConOrganizationWorkCategoryEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param organizationId 組織ID
     * @param workCategoryId 作業区分ID
     */
    public ConOrganizationWorkCategoryEntity(long organizationId, long workCategoryId) {
        this.organizationId = organizationId;
        this.workCategoryId = workCategoryId;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public long getFkOrganizationId() {
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setFkOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 作業区分IDを取得する。
     *
     * @return 作業区分ID
     */
    public long getFkWorkCategoryId() {
        return this.workCategoryId;
    }

    /**
     * 作業区分IDを設定する。
     *
     * @param workCategoryId 作業区分ID
     */
    public void setFkWorkCategoryId(long workCategoryId) {
        this.workCategoryId = workCategoryId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (this.organizationId ^ (this.organizationId >>> 32));
        hash = 29 * hash + (int) (this.workCategoryId ^ (this.workCategoryId >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConOrganizationWorkCategoryEntity other = (ConOrganizationWorkCategoryEntity) obj;
        if (this.organizationId != other.organizationId) {
            return false;
        }
        if (this.workCategoryId != other.workCategoryId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ConOrganizationWorkCategoryEntity{")
                .append("organizationId=").append(this.organizationId)
                .append(", ")
                .append("workCategoryId=").append(this.workCategoryId)
                .append("}")
                .toString();
    }
}
