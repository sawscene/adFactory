/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.master;

import java.io.Serializable;
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
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;

/**
 * 理由区分マスタ
 *
 * @author y-harada
 */
@Entity
@Table(name = "mst_reason_category")
@XmlRootElement(name = "reasonCategory")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ReasonCategoryEntity.findById", query = "SELECT m FROM ReasonCategoryEntity m WHERE m.reasonCategoryId = :reasonCategoryId"),
    @NamedQuery(name = "ReasonCategoryEntity.findByName", query = "SELECT m FROM ReasonCategoryEntity m WHERE m.reasonCategoryName = :reasonCategoryName AND m.reasonType = :reasonType"),
    @NamedQuery(name = "ReasonCategoryEntity.findByType", query = "SELECT m FROM ReasonCategoryEntity m WHERE m.reasonType = :reasonType"),
    @NamedQuery(name = "ReasonCategoryEntity.findDefaultByType", query = "SELECT m FROM ReasonCategoryEntity m WHERE m.defaultReasonCategory = true AND m.reasonType = :reasonType"),
    @NamedQuery(name = "ReasonCategoryEntity.countById", query = "SELECT COUNT(m.reasonCategoryId) FROM ReasonCategoryEntity m WHERE m.reasonCategoryId = :reasonCategoryId"),
    @NamedQuery(name = "ReasonCategoryEntity.countByName", query = "SELECT COUNT(m.reasonCategoryId) FROM ReasonCategoryEntity m WHERE m.reasonCategoryName = :reasonCategoryName AND m.reasonType = :reasonType"),
    @NamedQuery(name = "ReasonCategoryEntity.countByKey", query = "SELECT COUNT(m.reasonCategoryId) FROM ReasonCategoryEntity m WHERE m.reasonCategoryId != :reasonCategoryId AND m.reasonCategoryName = :reasonCategoryName AND m.reasonType = :reasonType"),
    @NamedQuery(name = "ReasonCategoryEntity.countByType", query = "SELECT COUNT(m.reasonCategoryId) FROM ReasonCategoryEntity m WHERE m.reasonType = :reasonType"),
})
public class ReasonCategoryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "reason_category_id")
    private Long reasonCategoryId;          // 理由区分ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "reason_type")
    private ReasonTypeEnum reasonType;      // 理由種別

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "reason_category_name")
    private String reasonCategoryName;      // 理由区分名

    @Column(name = "default_reason_category")
    private Boolean defaultReasonCategory ;   // デフォルト理由区分

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * コンストラクタ
     */
    public ReasonCategoryEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param reasonType 理由種別
     * @param reasonCategoryName 理由区分名
     */
    public ReasonCategoryEntity(ReasonTypeEnum reasonType, String reasonCategoryName) {
        this.reasonType = reasonType;
        this.reasonCategoryName = reasonCategoryName;
    }

    /**
     * 理由区分IDを取得する。
     *
     * @return 理由区分ID
     */
    public Long getReasonCategoryId() {
        return this.reasonCategoryId;
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
        return this.reasonType;
    }

    /**
     * 理由種別を設定する。
     *
     * @param type 理由種別
     */
    public void setReasonType(ReasonTypeEnum type) {
        this.reasonType = type;
    }

    /**
     * 理由区分名を取得する。
     *
     * @return 理由区分名
     */
    public String getReasonCategoryName() {
        return this.reasonCategoryName;
    }

    /**
     * 理由区分名を設定する。
     *
     * @param reasonCategoryName 理由区分名
     */
    public void setReasonCategoryName(String reasonCategoryName) {
        this.reasonCategoryName = reasonCategoryName;
    }

    /**
     * デフォルト理由区分かどうかを返す。
     * 
     * @return true: デフォルト、false: 非デフォルト
     */
    public Boolean isDefaultReasonCategory() {
        return defaultReasonCategory;
    }

    /**
     * デフォルト理由区分を設定する。
     * 
     * @param defaultReasonCategory true: デフォルト、false: 非デフォルト
     */
    public void setDefaultReasonCaegory(Boolean defaultReasonCategory) {
        this.defaultReasonCategory = defaultReasonCategory;
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.reasonCategoryId);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true:同じである、false:異なる
     */
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
        final ReasonCategoryEntity other = (ReasonCategoryEntity) obj;
        return Objects.equals(this.reasonCategoryId, other.reasonCategoryId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ReasonCategoryEntity{")
                .append("reasonCategoryId=").append(this.reasonCategoryId)
                .append(", reasonType=").append(this.reasonType)
                .append(", reasonCategoryName=").append(this.reasonCategoryName)
                .append(", defaultReasonCategory=").append(this.defaultReasonCategory)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
