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
 * ロットトレースの複合キー
 * 
 * @author s-heya
 */
@Embeddable
public class TrnLotTracePK implements Serializable {

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 32)
    @Column(name = "delivery_no")
    private String deliveryNo;
    @Basic(optional = false)
    //@NotNull
    @Column(name = "item_no")
    private int itemNo;
    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 32)
    @Column(name = "material_no")
    private String materialNo;
    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_kanban_id")
    private Long workKanbanId;

    /**
     * コンストラクタ
     */
    public TrnLotTracePK() {
    }

    /**
     * コンストラクタ
     * 
     * @param deliveryNo 出図番号
     * @param itemNo 明細番号
     * @param materialNo 資材番号
     * @param workKanbanId カンバンID
     */
    public TrnLotTracePK(String deliveryNo, int itemNo, String materialNo, Long workKanbanId) {
        this.deliveryNo = deliveryNo;
        this.itemNo = itemNo;
        this.materialNo = materialNo;
        this.workKanbanId = workKanbanId;
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
    public int getItemNo() {
        return itemNo;
    }

    /**
     * 明細番号を設定する。
     * 
     * @param itemNo 明細番号
     */
    public void setItemNo(int itemNo) {
        this.itemNo = itemNo;
    }

    /**
     * 資材番号を取得する。
     * 
     * @return 資材番号
     */
    public String getMaterialNo() {
        return materialNo;
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
     * 工程カンバンIDを取得する。
     *
     * @return 工程カンバンID
     */
    public Long getWorkKanbanId() {
        return this.workKanbanId;
    }

    /**
     * 工程カンバンIDを設定する。
     *
     * @param workKanbanId 工程カンバンID
     */
    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.deliveryNo);
        hash = 97 * hash + this.itemNo;
        hash = 97 * hash + Objects.hashCode(this.materialNo);
        hash = 97 * hash + Objects.hashCode(this.workKanbanId);
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
        final TrnLotTracePK other = (TrnLotTracePK) obj;
        if (this.itemNo != other.itemNo) {
            return false;
        }
        if (!Objects.equals(this.deliveryNo, other.deliveryNo)) {
            return false;
        }
        if (!Objects.equals(this.materialNo, other.materialNo)) {
            return false;
        }
        return Objects.equals(this.workKanbanId, other.workKanbanId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("TrnLotTracePK{")
                .append("deliveryNo=").append(this.deliveryNo)
                .append(", itemNo=").append(this.itemNo)
                .append(", materialNo=").append(this.materialNo)
                .append(", workKanbanId=").append(this.workKanbanId)
                .append("}")
                .toString();
    }
    
}
