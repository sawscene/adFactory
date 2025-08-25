/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 在庫引当 複合プライマリキー
 * 
 * @author s-heya
 */
@Embeddable
public class TrnReserveMaterialPK implements Serializable {

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 32)
    @Column(name = "delivery_no")
    private String deliveryNo;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "item_no")
    private Integer itemNo;

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 32)
    @Column(name = "material_no")
    private String materialNo;

    /**
     * コンストラクタ
     */
    public TrnReserveMaterialPK() {
    }

    /**
     * コンストラクタ
     * 
     * @param deliveryNo
     * @param itemNo
     * @param materialNo 
     */
    public TrnReserveMaterialPK(String deliveryNo, Integer itemNo, String materialNo) {
        this.deliveryNo = deliveryNo;
        this.itemNo = itemNo;
        this.materialNo = materialNo;
    }

    /**
     * 出庫番号を取得する。
     * 
     * @return 出庫番号
     */
    public String getDeliveryNo() {
        return deliveryNo;
    }

    /**
     * 出庫番号を設定する。
     * 
     * @param deliveryNo 出庫番号
     */
    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
    }

    /**
     * 明細番号を取得する。
     * 
     * @return 明細番号
     */
    public Integer getItemNo() {
        return itemNo;
    }

    /**
     * 明細番号を設定する。
     * 
     * @param itemNo 明細番号
     */
    public void setItemNo(Integer itemNo) {
        this.itemNo = itemNo;
    }

    /**
     * 資材番号を取得する。
     *
     * @return 資材番号
     */
    public String getMaterialNo() {
        return this.materialNo;
    }

    /**
     * 資材番号を設定する。
     *
     * @param materialNo 資材番号
     */
    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (deliveryNo != null ? deliveryNo.hashCode() : 0);
        hash += (int) itemNo;
        hash += (materialNo != null ? materialNo.hashCode() : 0);
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TrnReserveMaterialPK)) {
            return false;
        }
        TrnReserveMaterialPK other = (TrnReserveMaterialPK) object;
        if (!Objects.equals(this.deliveryNo, other.deliveryNo)) {
            return false;
        }
        if (!Objects.equals(this.itemNo, other.itemNo)) {
            return false;
        }
        return Objects.equals(this.materialNo, other.materialNo);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("TrnReserveMaterialPK{")
            .append("deliveryNo=").append(this.deliveryNo)
            .append(", orderNo=").append(this.itemNo)
            .append(", materialNo=").append(this.materialNo)
            .append("}")
            .toString();
    }
    
}
