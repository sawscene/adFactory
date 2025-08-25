/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

import adtekfuji.rest.LocalDateTimeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 入出庫実績情報
 *
 * @author s-heya
 */
@XmlRootElement(name = "logStock")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogStockInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Long eventId;
    @XmlElement
    private Integer eventKind;
    @XmlElement
    private String materialNo;
    @XmlElement
    private String supplyNo;
    @XmlElement
    private String deliveryNo;
    @XmlElement
    private Integer itemNo;
    @XmlElement
    private String orderNo;
    @XmlElement
    private String productNo;
    @XmlElement
    private String productName;
    @XmlElement
    private String areaName;
    @XmlElement
    private String locationNo;
    @XmlElement
    private Integer eventNum;
    @XmlElement
    private String personNo;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime eventDate;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime createDate;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime updateDate;
    @XmlElement
    private String serialNo;
    @XmlElement
    private Integer category;
    @XmlElement
    private Integer inStockNum;
    @XmlElement
    private Integer adjustment;
    @XmlTransient
    private TrnMaterialInfo material;
    @XmlTransient
    private String unitNo;
    @XmlElement
    private Integer requestNum;
    @XmlElement
    private String partsNo;
    @XmlElement
    private String note;
    
    /**
     * イベント番号を取得する。
     *
     * @return イベント番号
     */
    public Long getEventId() {
        return this.eventId;
    }

    /**
     * イベント種別を取得する。
     *
     * @return イベント種別
     */
    public Integer getEventKind() {
        return this.eventKind;
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
     * 納入番号を取得する。
     *
     * @return 納入番号
     */
    public String getSupplyNo() {
        return this.supplyNo;
    }

    /**
     * 出庫番号を取得する。
     *
     * @return 出庫番号
     */
    public String getDeliveryNo() {
        return this.deliveryNo;
    }

    /**
     * 明細番号を取得する。
     *
     * @return 明細番号
     */
    public Integer getItemNo() {
        return this.itemNo;
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getOrderNo() {
        if (Objects.isNull(this.orderNo)) {
            return "";
        }
        return this.orderNo;
    }

    /**
     * 品目を取得する。
     *
     * @return 品目
     */
    public String getProductNo() {
        if (Objects.isNull(this.productNo)) {
            return "";
        }
        return this.productNo;
    }
    
    /**
     * 品名を取得する。
     * 
     * @return 品名
     */
    public String getProductName() {
        if (Objects.isNull(this.productName)) {
            return "";
        }
        return this.productName;
    }
    /**
     * 区画名を取得する。
     *
     * @return 区画名
     */
    public String getAreaName() {
        return this.areaName;
    }

    /**
     * 棚番号を取得する。
     *
     * @return 棚番号
     */
    public String getLocationNo() {
        return this.locationNo;
    }

    /**
     * イベント数を取得する。
     *
     * @return イベント数
     */
    public Integer getEventNum() {
        return this.eventNum;
    }

    /**
     * 社員番号を取得する。
     *
     * @return 社員番号
     */
    public String getPersonNo() {
        return this.personNo;
    }

    /**
     * イベント日時を取得する。
     *
     * @return イベント日時
     */
    public LocalDateTime getEventDate() {
        return this.eventDate;
    }

    /**
     * 作成日時を取得する。
     *
     * @return 作成日時
     */
    public LocalDateTime getCreateDate() {
        return this.createDate;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public LocalDateTime getUpdateDate() {
        return this.updateDate;
    }

    /**
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public String getSerialNo() {
        return this.serialNo;
    }

    /**
     * 手配区分を取得する。
     *
     * @return 手配区分
     */
    public Integer getCategory() {
        return this.category;
    }

    /**
     * 在庫数を取得する。
     *
     * @return 在庫数
     */
    public Integer getInStockNum() {
        return this.inStockNum;
    }

    /**
     * 在庫調整を取得する。
     *
     * @return 在庫調整
     */
    public Integer getAdjustment() {
        return this.adjustment;
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
     * 資材情報を設定する。
     * 
     * @param material 資材情報 
     */
    public void setMaterial(TrnMaterialInfo material) {
        this.material = material;
    }
    
    /**
     * ユニット番号を取得する。
     * @return ユニット番号
     */
    public String getUnitNo() {
        if (Objects.isNull(this.unitNo)) {
            return "";
        }
        return unitNo;
    }

    /**
     * ユニット番号を設定する。
     * @param unitNo ユニット番号
     */
    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    /**
     * 要求数を取得する。
     * 
     * @return 要求数
     */
    public Integer getRequestNum() {
        return requestNum;
    }

    /**
     * 要求数を設定する。
     * 
     * @param requestNum 要求数 
     */
    public void setRequestNum(Integer requestNum) {
        this.requestNum = requestNum;
    }

    /**
     * 部品番号を取得する。
     * 
     * @return 部品番号
     */
    public String getPartsNo() {
        return partsNo;
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
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eventId != null ? eventId.hashCode() : 0);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param object オブジェクト
     * @return true; 同じである、false: 異なる
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LogStockInfo)) {
            return false;
        }
        LogStockInfo other = (LogStockInfo) object;
        return Objects.equals(this.eventId, other.eventId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("LogStock{")
                .append("eventId=").append(this.eventId)
                .append(", category=").append(this.category)
                .append(", eventKind=").append(this.eventKind)
                .append(", materialNo=").append(this.materialNo)
                .append(", supplyNo=").append(this.supplyNo)
                .append(", deliveryNo=").append(this.deliveryNo)
                .append(", itemNo=").append(this.itemNo)
                .append(", orderNo=").append(this.orderNo)
                .append(", serialNo=").append(this.serialNo)
                .append(", productNo=").append(this.productNo)
                .append(", areaName=").append(this.areaName)
                .append(", locationNo=").append(this.locationNo)
                .append(", eventNum=").append(this.eventNum)
                .append(", personNo=").append(this.personNo)
                .append(", eventDate=").append(this.eventDate)
                .append(", inStockNum=").append(this.inStockNum)
                .append(", adjustment=").append(this.adjustment)
                .append("}")
                .toString();
    }
}
