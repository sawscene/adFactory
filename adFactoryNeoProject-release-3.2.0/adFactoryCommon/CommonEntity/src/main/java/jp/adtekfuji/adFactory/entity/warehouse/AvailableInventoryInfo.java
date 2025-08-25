/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 有効在庫情報
 * 
 * @author s-heya
 */
@XmlRootElement(name = "availableInventory")
@XmlAccessorType(XmlAccessType.FIELD)
public class AvailableInventoryInfo implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    @XmlElement
    private String materialNo;

    @XmlElement
    private String supplyNo;

    @XmlElement
    private String orderNo;

    @XmlElement
    private Integer arrivalNum;

    @XmlElement
    private Integer stockNum;

    @XmlElement
    private Integer deliveryNum;

    @XmlElement
    private Integer inStockNum;
    
    @XmlElement
    private Integer reservedNum;
    
    @XmlElement
    private Integer reservationNum;

    @XmlElement
    private String note;
    
    private IntegerProperty orderNumProperty;
    private IntegerProperty reservedNumProperty;
    private IntegerProperty availableNumProperty;
    private IntegerProperty reservationNumProperty;
    private IntegerProperty deliveryNumProperty;

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
     * 発注番号を取得する。
     * 
     * @return 発注番号
     */
    public String getSupplyNo() {
        return supplyNo;
    }

    /**
     * 発注番号を設定する。
     * 
     * @param supplyNo 発注番号
     */
    public void setSupplyNo(String supplyNo) {
        this.supplyNo = supplyNo;
    }

    /**
     * 製造番号を取得する。
     * 
     * @return 製造番号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 製造番号を設定する。
     * 
     * @param orderNo 製造番号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 発注数を取得する。
     * 
     * @return 発注数
     */
    public Integer getArrivalNum() {
        if (Objects.isNull(this.arrivalNum)) {
            return 0;
        }
        return arrivalNum;
    }
   
    /**
     * 入庫数を取得する。
     * 
     * @return 入庫数
     */
    public Integer getStockNum() {
        if (Objects.isNull(this.stockNum)) {
            return 0;
        }
        return stockNum;
    }

    /**
     * 出庫数を取得する。
     * 
     * @return 出庫数
     */
    public Integer getDeliveryNum() {
        if (Objects.isNull(this.deliveryNum)) {
            return 0;
        }
        return deliveryNum;
    }

    /**
     * 現在庫数を取得する。
     * 
     * @return 現在庫数
     */
    public Integer getInStockNum() {
        if (Objects.isNull(this.inStockNum)) {
            return 0;
        }
        return inStockNum;
    }

    /**
     * 概引当数を取得する。
     * 
     * @return 概引当数
     */
    public Integer getReservedNum() {
        if (Objects.isNull(this.reservedNum)) {
            return 0;
        }
        return reservedNum;
    }

    /**
     * 新規引当数を取得する。
     * 
     * @return 
     */
    public Integer getReservationNum() {
        if (Objects.isNull(this.reservationNum)) {
            return 0;
        }
        return reservationNum;
    }

    /**
     * 新規引当数を設定する。
     * 
     * @param reservationNum 新規引当数 
     */
    public void setReservationNum(Integer reservationNum) {
        this.reservationNum = reservationNum;
    }

    /**
     * コメントを取得する。
     * 
     * @return コメント
     */
    public String getNote() {
        return note;
    }

    /**
     * コメントを設定する。
     * 
     * @param note コメント
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * 発注残を取得する。
     * 
     * @return 発注残
     */
    public IntegerProperty orderNumProperty() {
        if (Objects.isNull(this.orderNumProperty)) {
            this.orderNumProperty = new SimpleIntegerProperty(this.getArrivalNum() > this.getStockNum() ? this.getArrivalNum() - this.getStockNum() : 0);
        }
        return this.orderNumProperty;
    }
    
    /**
     * 既引当数を取得する。
     * 
     * @return 概引当数
     */
    public IntegerProperty reservedNumProperty() {
        if (Objects.isNull(this.reservedNumProperty)) {
            this.reservedNumProperty = new SimpleIntegerProperty(this.getReservedNum());
        }
        return this.reservedNumProperty;
    }
    
    /**
     * 有効在庫数を取得する。
     * 
     * @return 有効在庫数
     */
    public IntegerProperty availableNumProperty() {
        if (Objects.isNull(this.availableNumProperty)) {
            this.availableNumProperty = new SimpleIntegerProperty(this.getInStockNum() - this.getReservedNum());
        }
        return this.availableNumProperty;
    }

    /**
     * 新規引当数を取得する。
     * 
     * @return 新規引当数
     */
    public IntegerProperty reservationNumProperty() {
        if (Objects.isNull(this.reservationNumProperty)) {
            this.reservationNumProperty = new SimpleIntegerProperty(this.getReservationNum());
        }
        return this.reservationNumProperty;
    }

    /**
     * 出庫数を取得する。
     * 
     * @return 出庫数 
     */
    public IntegerProperty deliveryNumProperty() {
        if (Objects.isNull(this.deliveryNumProperty)) {
            this.deliveryNumProperty = new SimpleIntegerProperty(this.getDeliveryNum());
        }
        return this.deliveryNumProperty;
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.materialNo);
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
        final AvailableInventoryInfo other = (AvailableInventoryInfo) obj;
        if (!Objects.equals(this.materialNo, other.materialNo)) {
            return false;
        }
        if (!Objects.equals(this.reservationNum, other.reservationNum)) {
            return false;
        }
        return Objects.equals(this.note, other.note);
    }
    
    /**
     * 複製を返す。
     * 
     * @return 複製されたオブジェクト
     */
    @Override
    public AvailableInventoryInfo clone() throws CloneNotSupportedException {
        AvailableInventoryInfo obj = null;
        try {
            obj = (AvailableInventoryInfo) super.clone();
            obj.materialNo = this.materialNo;
            obj.supplyNo = this.supplyNo;
            obj.orderNo = this.orderNo;
            obj.arrivalNum = this.arrivalNum;
            obj.stockNum = this.stockNum;
            obj.deliveryNum = this.deliveryNum;
            obj.inStockNum = this.inStockNum;
            obj.reservedNum = this.reservedNum;
            obj.reservationNum = this.reservationNum;
            obj.note = this.note;
        } catch (CloneNotSupportedException e) {
        }
        return obj;
    }
    
    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("TrnMaterial{")
                .append("materialNo=").append(this.materialNo)
                .append(", supplyNo=").append(this.supplyNo)
                .append(", orderNo=").append(this.orderNo)
                .append(", arrivalNum=").append(this.arrivalNum)
                .append(", stockNum=").append(this.stockNum)
                .append(", deliveryNum=").append(this.deliveryNum)
                .append(", inStockNum=").append(this.inStockNum)
                .append(", reserverdNum=").append(this.reservedNum)
                .append("}").toString();
    }

}