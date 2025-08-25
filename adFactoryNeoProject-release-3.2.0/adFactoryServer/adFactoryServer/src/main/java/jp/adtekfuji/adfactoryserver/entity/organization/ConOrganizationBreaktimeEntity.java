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
 * 組織・休憩関連付け情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "con_organization_breaktime")
@XmlRootElement(name = "conOrganizationBreaktime")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ConOrganizationBreaktimeEntity.findAll", query = "SELECT c FROM ConOrganizationBreaktimeEntity c"),
    // 組織IDを指定して、休憩ID一覧を取得する。
    @NamedQuery(name = "ConOrganizationBreaktimeEntity.findBreaktimeId", query = "SELECT c.breaktimeId FROM ConOrganizationBreaktimeEntity c WHERE c.organizationId = :organizationId GROUP BY c.breaktimeId"),
    // 組織ID一覧を指定して、休憩ID一覧を取得する。
    @NamedQuery(name = "ConOrganizationBreaktimeEntity.findBreaktimeIdByOrganizationIds", query = "SELECT c.breaktimeId FROM ConOrganizationBreaktimeEntity c WHERE c.organizationId IN :organizationIds GROUP BY c.breaktimeId"),
    // 組織IDを指定して、組織・休憩関連付け情報を削除する。
    @NamedQuery(name = "ConOrganizationBreaktimeEntity.removeByOrganizationId", query = "DELETE FROM ConOrganizationBreaktimeEntity c WHERE c.organizationId = :organizationId"),
    // 休憩IDを指定して、組織・休憩関連付け情報を削除する。
    @NamedQuery(name = "ConOrganizationBreaktimeEntity.removeByBreaktimeId", query = "DELETE FROM ConOrganizationBreaktimeEntity c WHERE c.breaktimeId = :breaktimeId"),
})
public class ConOrganizationBreaktimeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "organization_id")
    private long organizationId;// 組織ID

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "breaktime_id")
    private long breaktimeId;// 休憩ID

    /**
     * コンストラクタ
     */
    public ConOrganizationBreaktimeEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param organizationId 組織ID
     * @param breaktimeId 休憩ID
     */
    public ConOrganizationBreaktimeEntity(long organizationId, long breaktimeId) {
        this.organizationId = organizationId;
        this.breaktimeId = breaktimeId;
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
     * 休憩IDを取得する。
     *
     * @return 休憩ID
     */
    public long getBreaktimeId() {
        return this.breaktimeId;
    }

    /**
     * 休憩IDを設定する。
     *
     * @param breaktimeId 休憩ID
     */
    public void setBreaktimeId(long breaktimeId) {
        this.breaktimeId = breaktimeId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (int) (this.organizationId ^ (this.organizationId >>> 32));
        hash = 53 * hash + (int) (this.breaktimeId ^ (this.breaktimeId >>> 32));
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
        final ConOrganizationBreaktimeEntity other = (ConOrganizationBreaktimeEntity) obj;
        if (this.organizationId != other.organizationId) {
            return false;
        }
        if (this.breaktimeId != other.breaktimeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ConOrganizationBreaktimeEntity{")
                .append("organizationId=").append(this.organizationId)
                .append(", ")
                .append("breaktimeId=").append(this.breaktimeId)
                .append("}")
                .toString();
    }
}
