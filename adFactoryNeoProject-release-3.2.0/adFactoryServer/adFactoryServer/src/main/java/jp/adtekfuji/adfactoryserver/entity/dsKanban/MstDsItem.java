/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.dsKanban;

import java.io.Serializable;
import java.util.Date;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 品番マスタ情報
 * 
 * @author s-heya
 */
@Entity
@Table(name = "mst_dsitem")
@XmlRootElement(name = "dsItem")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "MstDsItem.countByCategory", query = "SELECT COUNT(m.productId) FROM MstDsItem m WHERE m.category = :category"),
    @NamedQuery(name = "MstDsItem.countByCategoryAndProductNo", query = "SELECT COUNT(m.productId) FROM MstDsItem m WHERE m.category = :category AND m.productNo LIKE :productNo"),
    @NamedQuery(name = "MstDsItem.findByCategory", query = "SELECT m FROM MstDsItem m WHERE m.category = :category ORDER BY m.productNo"),
    @NamedQuery(name = "MstDsItem.findByCategoryAndProductNo", query = "SELECT m FROM MstDsItem m WHERE m.category = :category AND m.productNo LIKE :productNo ORDER BY m.productNo"),
    @NamedQuery(name = "MstDsItem.findByProductNo", query = "SELECT m FROM MstDsItem m WHERE m.category = :category AND m.productNo = :productNo ORDER BY m.productNo"),
    @NamedQuery(name = "MstDsItem.findByProductId", query = "SELECT m FROM MstDsItem m WHERE m.productId IN :productIds"),
    @NamedQuery(name = "MstDsItem.count", query = "SELECT COUNT(m.productId) FROM MstDsItem m WHERE m.category = :category AND m.productNo = :productNo"),
    @NamedQuery(name = "MstDsItem.deleteByProductId", query = "DELETE FROM MstDsItem m WHERE m.productId IN :productIds"),
})
public class MstDsItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "product_id")
    private Long productId;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "category")
    private Integer category;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "product_no")
    private String productNo;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "spec")
    private String spec;

    @Column(name = "unit")
    private String unit;

    @Column(name = "location1")
    private String location1;

    @Column(name = "location2")
    private String location2;

    @Column(name = "bom", length = 30000)
    private String bom;

    @Column(name = "workflow1")
    private Long workflow1;

    @Column(name = "workflow2")
    private Long workflow2;

    @Column(name = "property", length = 30000)
    private String property;

    @Column(name = "update_person_id")
    private Long updatePersonId;

    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;

    @Version
    @Column(name = "ver_info")
    private Integer verInfo;

    /**
     * コンストラクタ
     */
    public MstDsItem() {
    }

    /**
     * コンストラクタ
     * 
     * @param category
     * @param productNo
     * @param productName
     * @param spec
     * @param location1
     * @param location2
     * @param bom
     * @param workflow1
     * @param workflow2 
     */
    public MstDsItem(Integer category, String productNo, String productName, String spec, String location1, String location2, String bom, Long workflow1, Long workflow2) {
        this.category = category;
        this.productNo = productNo;
        this.productName = productName;
        this.spec = spec;
        this.location1 = location1;
        this.location2 = location2;
        this.bom = bom;
        this.workflow1 = workflow1;
        this.workflow2 = workflow2;
    }

    /**
     * 品番マスタIDを取得する。
     * 
     * @return 品番マスタID
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * 品番マスタIDを設定する。
     * 
     * @param productId 品番マスタID
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * 区分を取得する。
     * 
     * @return 区分 1: 補給生産、2:検査
     */
    public Integer getCategory() {
        return category;
    }

    /**
     * 区分を設定する。
     * 
     * @param category 区分 
     */
    public void setCategory(Integer category) {
        this.category = category;
    }

    /**
     * 品番を取得する。
     * 
     * @return 品番
     */
    public String getProductNo() {
        return productNo;
    }

    /**
     * 品番を設定する。
     * 
     * @param productNo 品番
     */
    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    /**
     * 品名を取得する。
     * 
     * @return 品名
     */
    public String getProductName() {
        return productName;
    }

    /**
     * 品名を設定する。
     * 
     * @param productName 品名 
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * 車種・タイプを取得する。
     * 
     * @return 車種・タイプ
     */
    public String getSpec() {
        return spec;
    }

    /**
     * 車種・タイプを設定する。
     * 
     * @param spec 車種・タイプ
     */
    public void setSpec(String spec) {
        this.spec = spec;
    }

    /**
     * 単位を取得する。
     * 
     * @return 単位
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 単位を設定する。
     * 
     * @param unit 単位
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * ロケーション1を取得する。
     * 
     * @return ロケーション1
     */
    public String getLocation1() {
        return location1;
    }

    /**
     * ロケーション1を設定する。
     * 
     * @param location1 ロケーション1
     */
    public void setLocation1(String location1) {
        this.location1 = location1;
    }

    /**
     * ロケーション2を取得する。
     * 
     * @return ロケーション2
     */
    public String getLocation2() {
        return location2;
    }

    /**
     * 
     * @param location2を設定する。
     */
    public void setLocation2(String location2) {
        this.location2 = location2;
    }

    /**
     * 構成部品を取得する。
     * 
     * @return 構成部品
     */
    public String getBom() {
        return bom;
    }

    /**
     * 構成部品を設定する。
     * 
     * @param bom 構成部品
     */
    public void setBom(String bom) {
        this.bom = bom;
    }

    /**
     * 工程順1を取得する。
     * 
     * @return 工程順1
     */
    public Long getWorkflow1() {
        return workflow1;
    }

    /**
     * 工程順1を設定する。
     * 
     * @param workflow1 工程順1
     */
    public void setWorkflow1(Long workflow1) {
        this.workflow1 = workflow1;
    }

    /**
     * 工程順2を取得する。
     * 
     * @return 工程順2
     */
    public Long getWorkflow2() {
        return workflow2;
    }

    /**
     * 工程順2を設定する。
     * 
     * @param workflow2 工程順2
     */
    public void setWorkflow2(Long workflow2) {
        this.workflow2 = workflow2;
    }

    /**
     * プロパティーを取得する。
     * 
     * @return プロパティー
     */
    public String getProperty() {
        return property;
    }

    /**
     * プロパティーを設定する。
     * 
     * @param property プロパティー
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * 更新者(組織ID)を取得する。
     *
     * @return 更新者(組織ID)
     */
    public Long getUpdatePersonId() {
        return this.updatePersonId;
    }

    /**
     * 更新者(組織ID)を設定する。
     *
     * @param updatePersonId 更新者(組織ID)
     */
    public void setUpdatePersonId(Long updatePersonId) {
        this.updatePersonId = updatePersonId;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDatetime() {
        return this.updateDatetime;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDatetime 更新日時
     */
    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    /**
     * バージョン情報を取得する。
     * 
     * @return バージョン情報
     */
    public Integer getVerInfo() {
        return verInfo;
    }

    /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productId != null ? productId.hashCode() : 0);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param object オブジェクト
     * @return true: 等しい、false: 異なる
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MstDsItem)) {
            return false;
        }
        MstDsItem other = (MstDsItem) object;
        return Objects.equals(this.productId, other.productId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("MstDsItem{")
                .append("productId=").append(this.productId)
                .append(", category=").append(this.category)
                .append(", productNo=").append(this.productNo)
                .append(", productName=").append(this.productName)
                .append(", spec=").append(this.spec)
                .append(", unit=").append(this.unit)
                .append(", location1=").append(this.location1)
                .append(", location2=").append(this.location2)
                .append(", workflow1=").append(this.workflow1)
                .append(", workflow2=").append(this.workflow2)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
    
}
