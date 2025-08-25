/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.organization;

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
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;

/**
 * 組織・理由区分関連付け
 * 
 * @author s-heya
 */
@Entity
@Table(name = "con_organization_reason")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ConOrganizationReasonEntity.findByOrganizationId", query = "SELECT c FROM ConOrganizationReasonEntity c WHERE c.organizationId = :organizationId"),
    @NamedQuery(name = "ConOrganizationReasonEntity.findByReasonCategoryId", query = "SELECT c FROM ConOrganizationReasonEntity c WHERE c.reasonCategoryId = :reasonCategoryId"),
    @NamedQuery(name = "ConOrganizationReasonEntity.findReasonCategoryId", query = "SELECT c.reasonCategoryId FROM ConOrganizationReasonEntity c WHERE c.organizationId = :organizationId"),
    @NamedQuery(name = "ConOrganizationReasonEntity.removeByOrganizationId", query = "DELETE FROM ConOrganizationReasonEntity c WHERE c.organizationId = :organizationId"),
    @NamedQuery(name = "ConOrganizationReasonEntity.countByReasonCategoryId", query = "SELECT COUNT(c.reasonCategoryId) FROM ConOrganizationReasonEntity c WHERE c.reasonCategoryId = :reasonCategoryId"),
})
public class ConOrganizationReasonEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "organization_id")
    private Long organizationId;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "reason_category_id")
    private Long reasonCategoryId;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "reason_type")
    private ReasonTypeEnum reasonType;

    /**
     * コンストラクタ
     */
    public ConOrganizationReasonEntity() {
    }

    /**
     * コンストラクタ
     * 
     * @param organizationId 組織ID
     * @param reasonCategoryId 理由区分ID
     * @param reasonType 理由種別
     */
    public ConOrganizationReasonEntity(Long organizationId, Long reasonCategoryId, ReasonTypeEnum reasonType) {
        this.organizationId = organizationId;
        this.reasonCategoryId = reasonCategoryId;
        this.reasonType = reasonType;
    }

    /**
     * 組織IDを取得する。
     * 
     * @return 組織ID
     */
    public Long getOrganizationId() {
        return organizationId;
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
     * 理由区分IDを取得する。
     * 
     * @return 理由区分ID
     */
    public Long getReasonCategoryId() {
        return reasonCategoryId;
    }

    /**
     * 理由区分IDを設定する。
     * 
     * @param reasonCategoryId 理由区分ID 
     */
    public void setReasonCategoryId(Long reasonCategoryId) {
        this.reasonCategoryId = reasonCategoryId;
    }

    /**
     * 理由種別を取得する。
     * 
     * @return 理由種別
     */
    public ReasonTypeEnum getReasonType() {
        return reasonType;
    }

    /**
     * 理由種別を設定する。
     * 
     * @param reasonType 理由種別
     */
    public void setReasonType(ReasonTypeEnum reasonType) {
        this.reasonType = reasonType;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += organizationId;
        hash += reasonCategoryId;
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param object オブジェクト
     * @return true:同じである、false:異なる
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ConOrganizationReasonEntity)) {
            return false;
        }
        ConOrganizationReasonEntity other = (ConOrganizationReasonEntity) object;
        if (!Objects.equals(this.organizationId, other.organizationId)) {
            return false;
        }
        return Objects.equals(this.reasonCategoryId, other.reasonCategoryId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ConOrganizationReasonEntity{")
            .append(", organizationId=").append(this.organizationId)
            .append(", reasonCategoryId=").append(this.reasonCategoryId)
            .append(", reasonType=").append(this.reasonType)
            .append("}")
            .toString();
    }
    
}
