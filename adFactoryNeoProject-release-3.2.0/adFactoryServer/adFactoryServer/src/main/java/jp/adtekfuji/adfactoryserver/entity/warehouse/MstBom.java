/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * 部品構成マスタ
 * 
 * @author s-heya
 */
@Entity
@Table(name = "mst_bom")
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
@NamedQueries({
    @NamedQuery(name = "MstBom.find", query = "SELECT o FROM MstBom o JOIN FETCH o.child WHERE o.parentId = :parentId AND o.unitNo = :unitNo AND o.child.productId = :childId"),
    //@NamedQuery(name = "MstBom.find", query = "SELECT o FROM MstBom o WHERE o.parentId = :parentId AND o.childId = :childId"),
    @NamedQuery(name = "MstBom.findByParentId", query = "SELECT o FROM MstBom o WHERE o.parentId = :parentId"),
    @NamedQuery(name = "MstBom.findNnecessity", query = "SELECT o FROM MstBom o WHERE o.updateDate < :updateDate"),
    // 子部品として使用されている製品数を返す
    @NamedQuery(name = "MstBom.countByChildId", query = "SELECT COUNT(o) FROM MstBom o JOIN FETCH o.child WHERE o.child.productId = :childId"),
})
@Cacheable(false)
public class MstBom implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "bom_id")
    private Long bomId;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "parent_id")
    private long parentId;

    //@Basic(optional = false)
    //@NotNull
    //@Column(name = "child_id")
    //private long childId;

    @ManyToOne
    @JoinColumn(name = "child_id", referencedColumnName = "product_id")
    private MstProduct child;

    @Column(name = "required_num")
    @JsonProperty("reqNum")
    private Integer requiredNum;

    @Column(name = "unit_no")
    private String unitNo;
    @Basic(optional = false)
    //@NotNull

    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Transient
    @XmlTransient
    @JsonProperty("prodNo")
    private String jsonProductNo;

    @Transient
    @XmlTransient
    @JsonProperty("partsNo")
    private String jsonPartsNo;

    /**
     * コンストラクタ
     */
    public MstBom() {
    }

    /**
     * コンストラクタ
     * 
     * @param parentId 親部品ID
     * @param unitNo ユニット番号
     * @param child 子部品
     * @param requiredNum 要求数
     * @param createDate 作成日時
     */
    public MstBom(Long parentId, String unitNo, MstProduct child, Integer requiredNum, Date createDate) {
        this.parentId = parentId;
        this.unitNo = unitNo;
        this.child = child;
        this.requiredNum = requiredNum;
        this.createDate = createDate;
    }
    
    /**
     * 部品構成IDを取得する。
     * 
     * @return 
     */
    public Long getBomId() {
        return bomId;
    }

    /**
     * 親部品IDを取得する。
     * 
     * @return 親部品ID
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * 親部品IDを設定する。
     * 
     * @param parentId 親部品ID
     */
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    /**
     * 要求数を取得する。
     * 
     * @return 要求数
     */
    public Integer getRequiredNum() {
        return requiredNum;
    }

    /**
     * 要求数を設定する。
     * 
     * @param requiredNum 要求数
     */
    public void setRequiredNum(Integer requiredNum) {
        this.requiredNum = requiredNum;
    }

    /**
     * ユニット番号を取得する。
     * 
     * @return ユニット番号
     */
    public String getUnitNo() {
        return unitNo;
    }

    /**
     * ユニット番号を設定する。
     * 
     * @param unitNo ユニット番号 
     */
    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    /**
     * 作成日時を取得する。
     * 
     * @return 作成日時
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 作成日時を設定する。
     * 
     * @param createDate 作成日時
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 更新日時を取得する。
     * 
     * @return 更新日時
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * 更新日時を設定する。
     * 
     * @param updateDate 更新日時
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 子部品情報を取得する。
     * 
     * @return 
     */
    public MstProduct getChild() {
        return child;
    }

    /**
     * 親品目を取得する。
     * 
     * @return 
     */
    public String getJsonProductNo() {
        return jsonProductNo;
    }

    /**
     * 小品目を取得する。
     * 
     * @return 
     */
    public String getJsonPartsNo() {
        return jsonPartsNo;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.bomId);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true: 一致する、false: 一致しない
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
        final MstBom other = (MstBom) obj;
        return Objects.equals(this.bomId, other.bomId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現 
     */
    @Override
    public String toString() {
        return new StringBuilder("MstBom{")
            .append("bomId=").append(this.bomId)
            .append(", parentId=").append(this.parentId)
            .append(", child=").append(this.child)
            .append(", requiredNum=").append(this.requiredNum)
            .append(", unitNo=").append(this.unitNo)
            .append("}")
            .toString();
    }
}
