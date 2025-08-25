/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import adtekfuji.rest.LocalDateAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jp.adtekfuji.adFactory.enumerate.MaterialGroupEnum;

/**
 * 資材情報 検索条件
 *
 * @author s-heya
 */
@XmlRootElement(name = "materialCondition")
@XmlAccessorType(XmlAccessType.FIELD)
public class MaterialCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private String productNo;           // 品目
    @XmlElement()
    private String supplyNo;            // 発注番号
    @XmlElement()
    private String orderNo;             // 製造オーダー番号
    @XmlElement()
    private String materialNo;          // 資材番号
    @XmlElement()
    private String areaName;            // 区画名
    @XmlElement()
    private String locationNo;          // 棚番号
    @XmlElement()
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate stockDate;        // 入庫日
    @XmlElement()
    private Boolean outStock;           // 在庫なしを含む
    @XmlElement()
    private String serialNo;            // 製造番号(ロット番号)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fromDate;         // 日付範囲(から)
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate toDate;           // 日付範囲(まで)
    @XmlElement()
    private Boolean inventory;              // 棚卸
    @XmlElement()
    private Boolean inventoryUnregistered;  // (棚卸)未実施
    @XmlElement()
    private Boolean inventoryDifferent;     // (棚卸)在庫過不足あり
    @XmlElement()
    private Boolean inventoryNoDifferent;   // (棚卸)在庫過不足なし
    @XmlElement()
    private String unitNo;                  // ユニット番号
    @XmlElement()
    private MaterialGroupEnum groupBy;      // 分類項目
    @XmlElement()
    private Boolean unarrivedOnly;          // 未納入品のみ
    @XmlElement()
    private Boolean inspected;              // 検査実施済
    @XmlElement()
    private String partsNo = null;     // ロット番号

    /**
     * コンストラクタ
     */
    public MaterialCondition() {
    }
    
    /**
     * コンストラクタ
     *
     * @param productNo 品目
     * @param supplyNo 発注番号
     * @param orderNo 製造オーダー番号
     * @param materialNo 資材番号
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @param stockDate 入庫日
     * @param outStock 在庫がない資材を含めるかどうか
     */
    public MaterialCondition(String productNo, String supplyNo, String orderNo, String materialNo, String areaName, String locationNo, LocalDate stockDate, Boolean outStock) {
        this.productNo = productNo;
        this.supplyNo = supplyNo;
        this.orderNo = orderNo;
        this.materialNo = materialNo;
        this.areaName = areaName;
        this.locationNo = locationNo;
        this.stockDate = stockDate;
        this.outStock = outStock;
    }

    /**
     * 検索条件を返す。
     * 
     * @param materialNo 資材番号
     * @return 検索条件
     */
    public static MaterialCondition materialNo(String materialNo) {
        MaterialCondition condition = new MaterialCondition();
        condition.setMaterialNo(materialNo);
        return condition;
    }

    /**
     * 検索条件を返す。
     * 
     * @param supplyNo 発注番号
     * @return 検索条件
     */
    public static MaterialCondition supplyNo(String supplyNo) {
        MaterialCondition condition = new MaterialCondition();
        condition.setSupplyNo(supplyNo);
        return condition;
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
     * 発注番号を取得する。
     *
     * @return 発注番号
     */
    public String getSupplyNo() {
        return this.supplyNo;
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
     * 製造オーダー番号
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
     * 区画名を取得する。
     *
     * @return 区画名
     */
    public String getAreaName() {
        return this.areaName;
    }

    /**
     * 区画名を設定する。
     *
     * @param areaName 区画名
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
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
     * 棚番号を設定する。
     *
     * @param locationNo 棚番号
     */
    public void setLocationNo(String locationNo) {
        this.locationNo = locationNo;
    }

    /**
     * 入庫日を取得する。
     *
     * @return 入庫日
     */
    public LocalDate getStockDate() {
        return this.stockDate;
    }

    /**
     * 入庫日を設定する。
     *
     * @param stockDate 入庫日
     */
    public void setStockDate(LocalDate stockDate) {
        this.stockDate = stockDate;
    }

    /**
     * 在庫がない資材を含めるかどうかを返す。
     *
     * @return 在庫がない資材を含めるかどうか
     */
    public Boolean getOutStock() {
        return this.outStock;
    }

    /**
     * 在庫がない資材を含めるかどうかを設定する。
     *
     * @param outStock 在庫がない資材を含めるかどうか
     */
    public void setOutStock(Boolean outStock) {
        this.outStock = outStock;
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getSerialNo() {
        return this.serialNo;
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
     * 日付範囲(から)を取得する。
     *
     * @return 日付範囲(から)
     */
    public LocalDate getFromDate() {
        return this.fromDate;
    }

    /**
     * 日付範囲(から)を設定する。
     *
     * @param fromDate 日付範囲(から)
     */
    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * 日付範囲(まで)を取得する。
     *
     * @return 日付範囲(まで)
     */
    public LocalDate getToDate() {
        return this.toDate;
    }

    /**
     * 日付範囲(まで)を設定する。
     *
     * @param toDate 日付範囲(まで)
     */
    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    /**
     * 棚卸を取得する。
     *
     * @return 棚卸
     */
    public Boolean getInventory() {
        return this.inventory;
    }

    /**
     * 棚卸を設定する。
     *
     * @param inventory 棚卸
     */
    public void setInventory(Boolean inventory) {
        this.inventory = inventory;
    }

    /**
     * (棚卸)未実施を取得する。
     *
     * @return (棚卸)未実施
     */
    public Boolean getInventoryUnregistered() {
        return this.inventoryUnregistered;
    }

    /**
     * (棚卸)未実施を設定する。
     *
     * @param inventoryUnregistered (棚卸)未実施
     */
    public void setInventoryUnregistered(Boolean inventoryUnregistered) {
        this.inventoryUnregistered = inventoryUnregistered;
    }

    /**
     * (棚卸)在庫過不足ありを取得する。
     *
     * @return (棚卸)在庫過不足あり
     */
    public Boolean getInventoryDifferent() {
        return this.inventoryDifferent;
    }

    /**
     * (棚卸)在庫過不足ありを設定する。
     *
     * @param inventoryDifferent (棚卸)在庫過不足あり
     */
    public void setInventoryDifferent(Boolean inventoryDifferent) {
        this.inventoryDifferent = inventoryDifferent;
    }

    /**
     * (棚卸)在庫過不足なしを取得する。
     *
     * @return (棚卸)在庫過不足なし
     */
    public Boolean getInventoryNoDifferent() {
        return this.inventoryNoDifferent;
    }

    /**
     * (棚卸)在庫過不足なしを設定する。
     *
     * @param inventoryNoDifferent (棚卸)在庫過不足なし
     */
    public void setInventoryNoDifferent(Boolean inventoryNoDifferent) {
        this.inventoryNoDifferent = inventoryNoDifferent;
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
     * 分類項目を取得する。
     * 
     * @return 分類項目
     */
    public MaterialGroupEnum getGroupBy() {
        return groupBy;
    }

    /**
     * 分類項目を設定する。
     * 
     * @param groupBy 分類項目を
     */
    public void setGroupBy(MaterialGroupEnum groupBy) {
        this.groupBy = groupBy;
    }

    /**
     * 未納入品のみフラグを取得する。
     * 
     * @return 未納入品のみフラグ
     */
    public Boolean getUnarrivedOnly() {
        return unarrivedOnly;
    }

    /**
     * 未納入品のみフラグを設定する。
     * 
     * @param unarrivedOnly 未納入品のみフラグ
     */
    public void setUnarrivedOnly(Boolean unarrivedOnly) {
        this.unarrivedOnly = unarrivedOnly;
    }

    public Boolean getInspected() {
        return inspected;
    }

    public void setInspected(Boolean inspected) {
        this.inspected = inspected;
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
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("MaterialCondition{")
                .append("productNo=").append(this.productNo)
                .append(", supplyNo=").append(this.supplyNo)
                .append(", orderNo=").append(this.orderNo)
                .append(", materialNo=").append(this.materialNo)
                .append(", areaName=").append(this.areaName)
                .append(", locationNo=").append(this.locationNo)
                .append(", stockDate=").append(this.stockDate)
                .append(", serialNo=").append(this.serialNo)
                .append(", fromDate=").append(this.fromDate)
                .append(", toDate=").append(this.toDate)
                .append(", inventory=").append(this.inventory)
                .append(", inventoryUnregistered=").append(this.inventoryUnregistered)
                .append(", inventoryDifferent=").append(this.inventoryDifferent)
                .append(", inventoryNoDifferent=").append(this.inventoryNoDifferent)
                .append(", unitNo=").append(this.unitNo)
                .append(", groupBy=").append(this.groupBy)
                .append(", unarrivedOnly=").append(this.unarrivedOnly)
                .append(", partsNo=").append(this.partsNo)
                .append("}")
                .toString();
    }
}
