/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.approval;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
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
@Entity
@Table(name = "mst_approval_route")
@XmlRootElement(name = "approvalRoute")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 追加時の名前重複チェック
    @NamedQuery(name = "ApprovalRouteEntity.checkAddByName", query = "SELECT COUNT(a.routeId) FROM ApprovalRouteEntity a WHERE a.routeName = :routeName"),
    // 更新時の名前重複チェック
    @NamedQuery(name = "ApprovalRouteEntity.checkUpdateByName", query = "SELECT COUNT(a.routeId) FROM ApprovalRouteEntity a WHERE a.routeName = :routeName AND a.routeId != :routeId"),

    // 承認ルート名を指定して、承認ルート情報を取得する。
    @NamedQuery(name = "ApprovalRouteEntity.findByName", query = "SELECT a FROM ApprovalRouteEntity a WHERE a.routeName = :routeName"),
    // 承認ルート情報一覧を取得する。
    @NamedQuery(name = "ApprovalRouteEntity.findAll", query = "SELECT a FROM ApprovalRouteEntity a ORDER BY a.routeName, a.routeId"),
    // 承認ルート情報の件数を取得する。
    @NamedQuery(name = "ApprovalRouteEntity.countAll", query = "SELECT COUNT(a) FROM ApprovalRouteEntity a"),
})
public class ApprovalRouteEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ルートID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "route_id")
    private Long routeId;

    /**
     * ルート名
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "route_name")
    private String routeName;

    /**
     * 排他用バージョン
     */
    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;

    /**
     * 承認順情報一覧
     */
    @XmlElementWrapper(name = "approvalOrders")
    @XmlElement(name = "approvalOrder")
    @Transient
    private List<ApprovalOrderEntity> approvalOrders = null;

    /**
     * コンストラクタ
     */
    public ApprovalRouteEntity() {
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
    public List<ApprovalOrderEntity> getApprovalOrders() {
        return this.approvalOrders;
    }

    /**
     * 承認順情報一覧を設定する。
     *
     * @param approvalOrders 承認順情報一覧
     */
    public void setApprovalOrders(List<ApprovalOrderEntity> approvalOrders) {
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
        final ApprovalRouteEntity other = (ApprovalRouteEntity) obj;
        if (!Objects.equals(this.routeId, other.routeId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ApprovalRouteEntity{")
                .append("routeId=").append(this.routeId)
                .append(", routeName=").append(this.routeName)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
