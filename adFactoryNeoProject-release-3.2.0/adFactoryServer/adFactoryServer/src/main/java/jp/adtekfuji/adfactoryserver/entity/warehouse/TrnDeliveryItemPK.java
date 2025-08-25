/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 出庫指示アイテム複合プライマリキー
 * 
 * @author s-heya
 */
@Embeddable
@JsonIgnoreProperties(ignoreUnknown=true)
public class TrnDeliveryItemPK implements Serializable {

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 32)
    @Column(name = "delivery_no")
    @JsonProperty("deliveryNo")
    private String deliveryNo;
    @Basic(optional = false)
    //@NotNull
    @Column(name = "item_no")
    @JsonProperty("no")
    private Integer itemNo;

    /**
     * コンストラクタ
     */
    public TrnDeliveryItemPK() {
    }

    /**
     * コンストラクタ
     * 
     * @param deliveryNo 出庫指示番号
     * @param itemNo 明細番号
     */
    public TrnDeliveryItemPK(String deliveryNo, int itemNo) {
        this.deliveryNo = deliveryNo;
        this.itemNo = itemNo;
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
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (deliveryNo != null ? deliveryNo.hashCode() : 0);
        hash += (int) itemNo;
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
        if (!(object instanceof TrnDeliveryItemPK)) {
            return false;
        }
        TrnDeliveryItemPK other = (TrnDeliveryItemPK) object;
        if (!Objects.equals(this.deliveryNo, other.deliveryNo)) {
            return false;
        }
        return Objects.equals(this.itemNo, other.itemNo);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "TrnDeliveryItemPK{" + "deliveryNo=" + deliveryNo + ", itemNo=" + itemNo + '}';
    }
}
