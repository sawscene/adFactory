/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.approval;

import java.io.Serializable;
import java.util.Objects;
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
 * 承認順情報
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "mst_approval_order")
@XmlRootElement(name = "approvalOrder")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 承認順情報一覧を取得する。
    @NamedQuery(name = "ApprovalOrderEntity.findAll", query = "SELECT a FROM ApprovalOrderEntity a"),

    // ルートIDを指定して、承認順情報一覧を取得する。
    @NamedQuery(name = "ApprovalOrderEntity.findByRouteId", query = "SELECT a FROM ApprovalOrderEntity a WHERE a.routeId IN :routeIds ORDER BY a.routeId, a.approvalOrder"),
    // ルートIDを指定して、承認順情報を削除する。
    @NamedQuery(name = "ApprovalOrderEntity.removeByRouteId", query = "DELETE FROM ApprovalOrderEntity a WHERE a.routeId = :routeId"),
})
public class ApprovalOrderEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ルートID
     */
    @Id
    @Basic(optional = false)
    @Column(name = "route_id")
    private Long routeId;

    /**
     * 承認順(1～)
     */
    @Id
    @Basic(optional = false)
    @Column(name = "approval_order")
    private Integer approvalOrder;

    /**
     * 組織ID
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "organization_id")
    private Long organizationId;

    /**
     * 最終承認者
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "approval_final")
    private Boolean approvalFinal;

    /**
     * コンストラクタ
     */
    public ApprovalOrderEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param routeId ルートID
     * @param in 承認順情報
     */
    public ApprovalOrderEntity(Long routeId, ApprovalOrderEntity in) {
        this.routeId = routeId;

        this.approvalOrder = in.approvalOrder;
        this.organizationId = in.organizationId;
        this.approvalFinal = in.approvalFinal;
    }

    /**
     * ルートIDを取得する。
     *
     * @return ルートID
     */
    public Long getRouteId() {
        return this.routeId;
    }

    /**
     * ルートIDを設定する。
     *
     * @param routeId ルートID
     */
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    /**
     * 承認順を取得する。
     *
     * @return 承認順(1～)
     */
    public Integer getApprovalOrder() {
        return this.approvalOrder;
    }

    /**
     * 承認順を設定する。
     *
     * @param approvalOrder 承認順(1～)
     */
    public void setApprovalOrder(Integer approvalOrder) {
        this.approvalOrder = approvalOrder;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getOrganizationId() {
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 最終承認者を取得する。
     *
     * @return 最終承認者
     */
    public Boolean getApprovalFinal() {
        return this.approvalFinal;
    }

    /**
     * 最終承認者を設定する。
     *
     * @param approvalFinal 最終承認者
     */
    public void setApprovalFinal(Boolean approvalFinal) {
        this.approvalFinal = approvalFinal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.routeId);
        hash = 23 * hash + Objects.hashCode(this.approvalOrder);
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
        final ApprovalOrderEntity other = (ApprovalOrderEntity) obj;
        if (!Objects.equals(this.routeId, other.routeId)) {
            return false;
        }
        if (!Objects.equals(this.approvalOrder, other.approvalOrder)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ApprovalOrderEntity{")
                .append("routeId=").append(this.routeId)
                .append(", approvalOrder=").append(this.approvalOrder)
                .append(", organizationId=").append(this.organizationId)
                .append(", approvalFinal=").append(this.approvalFinal)
                .append("}")
                .toString();
    }
}
