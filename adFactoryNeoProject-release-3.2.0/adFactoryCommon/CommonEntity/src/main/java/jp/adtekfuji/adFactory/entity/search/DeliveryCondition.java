/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import adtekfuji.rest.LocalDateAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum;

/**
 * 出庫情報 検索条件
 * 
 * @author s-heya
 */
@XmlRootElement(name = "deliveryCondition")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeliveryCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private String deliveryNo;          // 出庫番号
    @XmlElement()
    private boolean equalDeliveryNo = false; // 出庫番号を完全一致で検索するか
    @XmlElement()
    private String orderNo;             // 製造オーダー番号
    @XmlElement()
    private String serialNo;            // 製造番号(ロット番号)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fromDate;         // 予定日(から)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate toDate;           // 予定日(まで)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fromDeliveryDate; // 出庫日(から)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate toDeliveryDate;   // 出庫日(まで)
    @XmlElement()
    private String modelName;            // 機種名
    @XmlElement()
    private String unitNo;            // ユニット番号
    @XmlElementWrapper(name = "statuses")
    @XmlElement(name = "status")
    private List<DeliveryStatusEnum> statuses = null;// 払出ステータス
    @XmlElement()
    private Boolean exactMatch; // 完全一致
    @XmlElement()
    private Integer deliveryRule;
        
    /**
     * コンストラクタ
     */
    public DeliveryCondition() {
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
     * 製造番号
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
     * 製造番号を取得する。
     * 
     * @return 製造番号
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * 製造番号を設定する。
     * 
     * @param serialNo 製造番号 
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * 予定日(から)を取得する。
     * 
     * @return 予定日(から)
     */
    public LocalDate getFromDate() {
        return fromDate;
    }

    /**
     * 予定日(から)を設定する。
     * 
     * @param fromDate 予定日(から)
     */
    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * 予定日(まで)を取得する。
     * 
     * @return 予定日(まで)
     */
    public LocalDate getToDate() {
        return toDate;
    }

    /**
     * 予定日(まで)を設定する。
     * 
     * @param toDate 予定日(まで)
     */
    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    /**
     * 出庫日(から)を取得する。
     * 
     * @return 出庫日(から)
     */
    public LocalDate getFromDeliveryDate() {
        return fromDeliveryDate;
    }

    /**
     * 出庫日(まで)を設定する。
     * 
     * @param fromDeliveryDate 予定日(まで)
     */
    public void setFromDeliveryDate(LocalDate fromDeliveryDate) {
        this.fromDeliveryDate = fromDeliveryDate;
    }

    /**
     * 出庫日(まで)を取得する。
     * 
     * @return 出庫日(まで)
     */
    public LocalDate getToDeliveryDate() {
        return toDeliveryDate;
    }

    /**
     * 出庫日(まで)を設定する。
     * 
     * @param toDeliveryDate 予定日(まで)
     */
    public void setToDeliveryDate(LocalDate toDeliveryDate) {
        this.toDeliveryDate = toDeliveryDate;
    }

    /**
     * 機種名を取得する。
     * 
     * @return 機種名
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * 機種名を設定する。
     * 
     * @param modelName 機種名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
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
     * 払出ステータスを取得する。
     * 
     * @return 払出ステータス
     */
    public List<DeliveryStatusEnum> getStatuses() {
        return statuses;
    }

    /**
     * 払出ステータスを設定する。
     * 
     * @param statuses 払出ステータス
     */
    public void setStatuses(List<DeliveryStatusEnum> statuses) {
        this.statuses = statuses;
    }

    /**
     * 完全一致で検索するかどうかを返す。
     * 
     * @return 
     */
    public Boolean isExactMatch() {
        return exactMatch;
    }

    /**
     * 完全一致で検索するかどうかを設定する。
     * 
     * @param exactMatch true: 完全一致、false: 部分一致
     */
    public void setExactMatch(Boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    /**
     * 出庫ルールを取得する。
     * 
     * @return 出庫ルール
     */
    public Integer getDeliveryRule() {
        return deliveryRule;
    }

    /**
     * 出庫ルールを設定する。
     * 
     * @param deliveryRule 出庫ルール
     */
    public void setDeliveryRule(Integer deliveryRule) {
        this.deliveryRule = deliveryRule;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("DeliveryCondition{")
            .append("deliveryNo=").append(this.deliveryNo)
            .append(", equalDeliveryNo=").append(this.equalDeliveryNo)
            .append(", orderNo=").append(this.orderNo)
            .append(", serialNo=").append(this.serialNo)
            .append(", fromDate=").append(this.fromDate)
            .append(", toDate=").append(this.toDate)
            .append(", fromDeliveryDate=").append(this.fromDeliveryDate)
            .append(", toDeliveryDate=").append(this.toDeliveryDate)
            .append(", unitNo=").append(this.unitNo)
            .append(", deliveryRule=").append(this.deliveryRule)
            .append("}")
            .toString();
    }
}
