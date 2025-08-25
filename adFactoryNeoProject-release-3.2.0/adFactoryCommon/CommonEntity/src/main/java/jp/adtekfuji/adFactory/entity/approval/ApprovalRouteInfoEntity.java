/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.approval;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 承認ルート情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "approvalRoute")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApprovalRouteInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ルートID
     */
    @XmlElement(required = true)
    private Long routeId;

    /**
     * ルート名
     */
    @XmlElement()
    private String routeName;

    /**
     * 排他用バージョン
     */
    @XmlElement()
    private Integer verInfo = 1;

    /**
     * 承認順情報一覧
     */
    @XmlElementWrapper(name = "approvalOrders")
    @XmlElement(name = "approvalOrder")
    private List<ApprovalOrderInfoEntity> approvalOrders = null;

    /**
     * コンストラクタ
     */
    public ApprovalRouteInfoEntity() {
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
     * ルート名を取得する。
     *
     * @return ルート名
     */
    public String getRouteName() {
        return this.routeName;
    }

    /**
     * ルート名を設定する。
     *
     * @param routeName ルート名
     */
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    /**
     * 排他用バージョンを取得する。
     *
     * @return 排他用バージョン
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バージョンを設定する。
     *
     * @param verInfo 排他用バージョン
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * 承認順情報一覧を取得する。
     *
     * @return 承認順情報一覧
     */
    public List<ApprovalOrderInfoEntity> getApprovalOrders() {
        return this.approvalOrders;
    }

    /**
     * 承認順情報一覧を設定する。
     *
     * @param approvalOrders 承認順情報一覧
     */
    public void setApprovalOrders(List<ApprovalOrderInfoEntity> approvalOrders) {
        this.approvalOrders = approvalOrders;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.routeId);
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
        final ApprovalRouteInfoEntity other = (ApprovalRouteInfoEntity) obj;
        if (!Objects.equals(this.routeId, other.routeId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ApprovalRouteInfoEntity{")
                .append("routeId=").append(this.routeId)
                .append(", routeName=").append(this.routeName)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
