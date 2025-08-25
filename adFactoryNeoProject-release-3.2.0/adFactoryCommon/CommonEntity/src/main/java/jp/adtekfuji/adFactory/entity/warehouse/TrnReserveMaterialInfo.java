/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 在庫引当
 * 
 * @author s-heya
 */
@XmlRootElement(name = "reserveMaterial")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrnReserveMaterialInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private TrnReserveMaterialPK pk;
    @XmlElement
    private String deliveryNo;
    @XmlElement
    private Integer itemNo;
    @XmlElement
    private String materialNo;
    @XmlElement
    private Integer reservedNum;
    @XmlElement
    private String note;
    @XmlElement
    private Date reservedAt;
    @XmlElement
    private String personNo;
    @XmlElement
    private Integer deliveryNum;
    @XmlElement
    private TrnMaterialInfo material;

    /**
     * コンストラクタ
     */
    public TrnReserveMaterialInfo() {
    }

    /**
     * コンストラクタ
     * 
     * @param deliveryNo
     * @param itemNo
     * @param materialNo
     * @param reservedNum
     * @param note
     * @param reservedAt
     * @param personNo 
     */
    public TrnReserveMaterialInfo(String deliveryNo, Integer itemNo, String materialNo, int reservedNum, String note, Date reservedAt, String personNo) {
        this.deliveryNo = deliveryNo;
        this.itemNo = itemNo;
        this.materialNo = materialNo;
        this.reservedNum = reservedNum;
        this.note = note;
        this.reservedAt = reservedAt;
        this.personNo = personNo;
    }

    public TrnReserveMaterialPK getPk() {
        return pk;
    }

    public void setPk(TrnReserveMaterialPK pk) {
        this.pk = pk;
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
     * 引当数を取得する。
     * 
     * @return 引当数
     */
    public int getReservedNum() {
        return reservedNum;
    }

    /**
     * 引当数を設定する。
     * 
     * @param reservedNum 引当数 
     */
    public void setReservedNum(int reservedNum) {
        this.reservedNum = reservedNum;
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
     * 引当日時を取得する。
     * 
     * @return 引当日時 
     */
    public Date getReservedAt() {
        return reservedAt;
    }

    /**
     * 引当日時を設定する。
     * 
     * @param reservedAt 引当日時
     */
    public void setReservedAt(Date reservedAt) {
        this.reservedAt = reservedAt;
    }

    /**
     * 社員番号を取得する。
     * 
     * @return 社員番号
     */
    public String getPersonNo() {
        return personNo;
    }

    /**
     * 社員番号を設定する。
     * 
     * @param personNo 社員番号
     */
    public void setPersonNo(String personNo) {
        this.personNo = personNo;
    }

    /**
     * 出庫数を取得する。
     * 
     * @return 出庫数
     */
    public Integer getDeliveryNum() {
        return deliveryNum;
    }

    /**
     * 資材情報を取得する。
     * 
     * @return 資材情報
     */
    public TrnMaterialInfo getMaterial() {
        return material;
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
        if (!(object instanceof TrnReserveMaterialInfo)) {
            return false;
        }
        TrnReserveMaterialInfo other = (TrnReserveMaterialInfo) object;
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
        return new StringBuilder("TrnReserveMaterial{")
            .append("deliveryNo=").append(this.deliveryNo)
            .append(", itemNo=").append(this.itemNo)
            .append(", materialNo=").append(this.materialNo)
            .append(", reservedNum=").append(this.reservedNum)
            .append(", reservedAt=").append(this.reservedAt)
            .append(", personNo=").append(this.personNo)
            .append(", deliveryNum=").append(this.deliveryNum)
            .append("}")
            .toString();
    }
    
}
