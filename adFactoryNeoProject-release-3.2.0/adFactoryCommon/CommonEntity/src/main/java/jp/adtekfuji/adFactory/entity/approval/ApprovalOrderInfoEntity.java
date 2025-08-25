/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.approval;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 承認順情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "approvalOrder")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApprovalOrderInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ルートID
     */
    @XmlElement()
    private Long routeId;

    /**
     * 承認順(1～)
     */
    @XmlElement()
    private Integer approvalOrder;

    /**
     * 組織ID
     */
    @XmlElement()
    private Long organizationId;

    /**
     * 最終承認者
     */
    @XmlElement()
    private Boolean approvalFinal;

    /**
     * コンストラクタ
     */
    public ApprovalOrderInfoEntity() {
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
        final ApprovalOrderInfoEntity other = (ApprovalOrderInfoEntity) obj;
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
        return new StringBuilder("ApprovalOrderInfoEntity{")
                .append("routeId=").append(this.routeId)
                .append(", approvalOrder=").append(this.approvalOrder)
                .append(", organizationId=").append(this.organizationId)
                .append(", approvalFinal=").append(this.approvalFinal)
                .append("}")
                .toString();
    }
}
