/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * ロットトレースの検索条件
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "lotTraceCondition")
public class LotTraceCondition implements Serializable {

    @XmlElement()
    private String deliveryNo = null;   // 出庫番号

    @XmlElement()
    private boolean equalDeliveryNo = false; // 出庫番号を完全一致で検索するか

    @XmlElement()
    private Date fromDate = null;       // 日時範囲の先頭

    @XmlElement()
    private Date toDate = null;         // 日時範囲の末尾
   
    @XmlElement()
    private List<String> materialNos = null; // 資材番号

    @XmlElement()
    private boolean equalMaterialNo = false; // 資材番号を完全一致で検索するか

    @XmlElement()
    private String orderNo = null;      // 製造オーダー番号

    @XmlElement()
    private String productNo = null;    // 品目

    @XmlElement()
    private String partsNo = null;     // ロット番号

    @XmlElement()
    private String personName = null;   // 作業者

    @XmlElement()
    private Boolean confirm = null;     // 確認

    @XmlElement()
    private boolean workedOnly = false; // 作業済のみ取得するか

    /**
     * コンストラクタ
     */
    public LotTraceCondition() {
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
     * 出庫番号を設定する。
     *
     * @param deliveryNo 出庫番号
     */
    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
    }

    /**
     * 出庫番号を完全一致で検索するかを取得する。
     *
     * @return 出庫番号を完全一致で検索するか (true:完全一致検索, false:LIKE検索)
     */
    public boolean isEqualDeliveryNo() {
        return equalDeliveryNo;
    }

    /**
     * 出庫番号を完全一致で検索するかを設定する。
     *
     * @param equalDeliveryNo 出庫番号を完全一致で検索するか (true:完全一致検索, false:LIKE検索)
     */
    public void setEqualDeliveryNo(boolean equalDeliveryNo) {
        this.equalDeliveryNo = equalDeliveryNo;
    }

    /**
     * 日時範囲の先頭を取得する。
     *
     * @return 日時範囲の先頭
     */
    public Date getFromDate() {
        return this.fromDate;
    }

    /**
     * 日時範囲の先頭を設定する。
     *
     * @param fromDate 日時範囲の先頭
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * 日時範囲の末尾を取得する。
     *
     * @return 日時範囲の末尾
     */
    public Date getToDate() {
        return this.toDate;
    }

    /**
     * 日時範囲の末尾を設定する。
     *
     * @param toDate 日時範囲の末尾
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * 資材番号を取得する。
     *
     * @return 資材番号
     */
    public List<String> getMaterialNos() {
        return this.materialNos;
    }

    /**
     * 資材番号を設定する。
     *
     * @param materialNos 資材番号
     */
    public void setMaterialNos(List<String> materialNos) {
        this.materialNos = materialNos;
    }

    /**
     * 資材番号を完全一致で検索するかを取得する。
     *
     * @return 資材番号を完全一致で検索するか (true:完全一致検索, false:LIKE検索)
     */
    public boolean isEqualMaterialNo() {
        return this.equalMaterialNo;
    }

    /**
     * 資材番号を完全一致で検索するかを設定する。
     *
     * @param equalMaterialNo 資材番号を完全一致で検索するか (true:完全一致検索, false:LIKE検索)
     */
    public void setEqualMaterialNo(boolean equalMaterialNo) {
        this.equalMaterialNo = equalMaterialNo;
    }

    /**
     * 製造オーダー番号を取得する。
     *
     * @return 製造オーダー番号
     */
    public String getOrderNo() {
        return this.orderNo;
    }

    /**
     * 製造オーダー番号を設定する。
     *
     * @param orderNo 製造オーダー番号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 品目を取得する。
     *
     * @return 品目
     */
    public String getProductNo() {
        return this.productNo;
    }

    /**
     * 品目を設定する。
     *
     * @param productNo 品目
     */
    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    /**
     * ロット番号を取得する。
     *
     * @return ロット番号
     */
    public String getPartsNo() {
        return this.partsNo;
    }

    /**
     * ロット番号を設定する。
     *
     * @param partsNo ロット番号
     */
    public void setPartsNo(String partsNo) {
        this.partsNo = partsNo;
    }

    /**
     * 作業者を取得する。
     *
     * @return 作業者
     */
    public String getPersonName() {
        return this.personName;
    }

    /**
     * 作業者を設定する。
     *
     * @param personName 作業者
     */
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    /**
     * 確認を取得する。
     *
     * @return 確認
     */
    public Boolean getConfirm() {
        return this.confirm;
    }

    /**
     * 確認を設定する。
     *
     * @param confirm 確認
     */
    public void setConfirm(Boolean confirm) {
        this.confirm = confirm;
    }

    /**
     * 作業済のみ取得するかを取得する。
     *
     * @return 作業済のみ取得するか (true:する, false:しない)
     */
    public boolean isWorkedOnly() {
        return this.workedOnly;
    }

    /**
     * 作業済のみ取得するかを設定する。
     *
     * @param workedOnly 作業済のみ取得するか (true:する, false:しない)
     */
    public void setWorkedOnly(boolean workedOnly) {
        this.workedOnly = workedOnly;
    }

    @Override
    public String toString() {
        return new StringBuilder("LotTraceCondition{")
                .append("deliveryNo=").append(this.deliveryNo)
                .append(", equalDeliveryNo=").append(this.equalDeliveryNo)
                .append(", fromDate=").append(this.fromDate)
                .append(", toDate=").append(this.toDate)
                .append(", materialNos=").append(this.materialNos)
                .append(", equalMaterialNo=").append(this.equalMaterialNo)
                .append(", orderNo=").append(this.orderNo)
                .append(", productNo=").append(this.productNo)
                .append(", partsNo=").append(this.partsNo)
                .append(", personName=").append(this.personName)
                .append(", confirm=").append(this.confirm)
                .append(", workedOnly=").append(this.workedOnly)
                .append("}")
                .toString();
    }
}
