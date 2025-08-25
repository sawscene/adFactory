/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 在庫マスタ
 * 
 * @author s-heya
 */
@Entity
@Table(name = "mst_stock")
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
@NamedQueries({
    @NamedQuery(name = "MstStock.find", query = "SELECT o FROM MstStock o WHERE o.location.locationId = :locationId AND o.product.productId = :productId"),
    @NamedQuery(name = "MstStock.sumInStock", query = "SELECT SUM(o.stockNum) FROM MstStock o WHERE o.location.areaName = :areaName AND o.product.productNo = :productNo"),
})
public class MstStock implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "stock_id")
    private Long stockId;
    //@Basic(optional = false)
    //@NotNull
    //@Column(name = "location_id")
    //private long locationId;
    //@Basic(optional = false)
    //@NotNull
    //@Column(name = "product_id")
    //private long prodcutId;
    @Column(name = "stock_num")
    private Integer stockNum;
    @Column(name = "stock_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stockDate;
    @Column(name = "inventory_num")
    private Integer inventoryNum;
    @Column(name = "inventory_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inventoryDate;
    @Basic(optional = false)
    //@NotNull
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;
    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "location_id")
    private MstLocation location;
    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "product_id")
    private MstProduct product;

    /**
     * コンストラクタ
     */
    public MstStock() {
    }

    /**
     * コンストラクタ
     * 
     * @param location 棚マスタ
     * @param product 部品マスタ
     * @param createDate 作成日時
     */
    public MstStock(MstLocation location, MstProduct product, Date createDate) {
        this.location = location;
        this.product = product;
        this.stockNum = 0;
        this.inventoryNum = 0;
        this.createDate = createDate;
    }

    /**
     * 在庫IDを取得する。
     * 
     * @return 在庫ID
     */
    public Long getStockId() {
        return stockId;
    }

    /**
     * 在庫IDを設定する。
     * 
     * @param stockId 在庫ID 
     */
    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    //public long getLocationId() {
    //    return locationId;
    //}
    //
    //public void setLocationId(long locationId) {
    //    this.locationId = locationId;
    //}
    //
    //public long getProdcutId() {
    //    return prodcutId;
    //}
    //
    //public void setProdcutId(long prodcutId) {
    //    this.prodcutId = prodcutId;
    //}

    /**
     * 在庫数を取得する。
     * 
     * @return 
     */
    public Integer getStockNum() {
        return stockNum;
    }

    /**
     * 在庫数を設定する。
     * 
     * @param stockNum 在庫数
     */
    public void setStockNum(Integer stockNum) {
        this.stockNum = stockNum;
    }

    /**
     * 最終入庫日時を取得する。
     * 
     * @return 最終入庫日時
     */
    public Date getStockDate() {
        return stockDate;
    }

    /**
     * 最終入庫日時を設定する。
     * 
     * @param stockDate 最終入庫日時
     */
    public void setStockDate(Date stockDate) {
        this.stockDate = stockDate;
    }

    /**
     * 棚卸在庫数を取得する。
     * 
     * @return 棚卸在庫数
     */
    public Integer getInventoryNum() {
        return inventoryNum;
    }

    /**
     * 棚卸在庫数を設定する。
     * 
     * @param inventoryNum 棚卸在庫数
     */
    public void setInventoryNum(Integer inventoryNum) {
        this.inventoryNum = inventoryNum;
    }

    /**
     * 棚卸実施日を取得する。
     * 
     * @return 棚卸実施日
     */
    public Date getInventoryDate() {
        return inventoryDate;
    }

    /**
     * 棚卸実施日を設定する。
     * 
     * @param inventoryDate  棚卸実施日
     */
    public void setInventoryDate(Date inventoryDate) {
        this.inventoryDate = inventoryDate;
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
     * 棚マスタを取得する。
     * 
     * @return 
     */
    public MstLocation getLocation() {
        return location;
    }

    /**
     * 部品マスタを取得する。
     * 
     * @return 
     */
    public MstProduct getProduct() {
        return product;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (stockId != null ? stockId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MstStock)) {
            return false;
        }
        MstStock other = (MstStock) object;
        return Objects.equals(this.stockId, other.stockId);
    }

    @Override
    public String toString() {
        return new StringBuilder("MstStock{")
            .append("stockId=").append(this.stockId)
            .append(", location=").append(this.location)
            .append(", prodcut=").append(this.product)
            .append(", stockNum=").append(this.stockNum)
            .append(", stockDate=").append(this.stockDate)
            .append(", inventoryNum=").append(this.inventoryNum)
            .append(", inventoryDate=").append(this.inventoryDate)
            .append("}")
            .toString();
    }
}
