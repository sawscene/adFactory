/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.WarehouseEvent;

/**
 * 入出庫実績情報
 *
 * @author s-heya
 */
@Entity
@Table(name = "log_stock")
@XmlRootElement(name = "logStock")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "LogStock.find", query = "SELECT o FROM LogStock o WHERE o.eventId = :eventId"),
    @NamedQuery(name = "LogStock.findByMaterialNo", query = "SELECT o FROM LogStock o WHERE o.materialNo IN :materialNos ORDER BY o.materialNo, o.eventDate"),
    @NamedQuery(name = "LogStock.findByEventKind", query = "SELECT o FROM LogStock o WHERE o.materialNo = :materialNo AND o.eventKind = :eventKind ORDER BY o.eventId"),
    @NamedQuery(name = "LogStock.findBySynced", query = "SELECT o FROM LogStock o WHERE o.synced = :synced ORDER BY o.eventDate"),
    @NamedQuery(name = "LogStock.updateSynced", query = "UPDATE LogStock o SET o.synced = :synced WHERE o.eventId IN :eventIds"),
    @NamedQuery(name = "LogStock.lastByMaterialNo", query = "SELECT o FROM LogStock o WHERE o.materialNo = :materialNo ORDER BY o.eventId DESC"),
    @NamedQuery(name = "LogStock.lastByMaterialNoAndEventKind", query = "SELECT o FROM LogStock o WHERE o.materialNo = :materialNo AND o.eventKind = :eventKind ORDER BY o.eventId DESC"),
})
public class LogStock implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * イベント番号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "event_id")
    private Long eventId;

    /**
     * イベント種別
     */
    @Column(name = "event_kind")
    private Short eventKind;

    /**
     * 資材番号
     */
    @Size(max = 32)
    @Column(name = "material_no")
    private String materialNo;

    /**
     * 納入番号
     */
    @Size(max = 32)
    @Column(name = "supply_no")
    private String supplyNo;

    /**
     * 出庫番号
     */
    @Size(max = 32)
    @Column(name = "delivery_no")
    private String deliveryNo;

    /**
     * 明細番号
     */
    @Column(name = "item_no")
    private Integer itemNo;

    /**
     * 製造番号
     */
    @Size(max = 32)
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 品目
     */
    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 32)
    @Column(name = "product_no")
    private String productNo;

    /**
     * 品名
     */
    @Column(name = "product_name")
    private String productName;
    
    /**
     * 区画名
     */
    @Basic(optional = false)
    @Size(max = 32)
    @Column(name = "area_name")
    private String areaName;

    /**
     * 棚番号
     */
    @Basic(optional = false)
    @Size(max = 32)
    @Column(name = "location_no")
    private String locationNo;

    /**
     * 数量
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "event_num")
    private Integer eventNum;

    /**
     * 社員番号
     */
    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "person_no")
    private String personNo;

    /**
     * イベント日時
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "event_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;

    /**
     * 作成日時
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    /**
     * 更新日時
     */
    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    /**
     * シリアル番号
     */
    @Size(max = 32)
    @Column(name = "serial_no")
    private String serialNo;

    /**
     * 手配区分
     */
    @Column(name = "category")
    private Short category;

    /**
     * 同期フラグ
     */
    @Column(name = "synced")
    private Boolean synced = false;

    /**
     * 在庫数
     */
    @Column(name = "in_stock_num")
    private Integer inStockNum;

    /**
     * 在庫調整
     */
    @Column(name = "adjustment")
    private Integer adjustment;

    /**
     * 要求数
     */
    @Column(name = "request_num")
    private Integer requestNum;

    /**
     * 部品番号
     */
    @Column(name = "parts_no")
    private String partsNo;

    /**
     * コメント
     */
    @Column(name = "note")
    private String note;

    /**
     * コンストラクタ
     */
    public LogStock() {
    }

    /**
     * 受入検査実績情報を作成する。
     * 
     * @param meterial 資材情報
     * @param eventNum 不良品数
     * @param note コメント
     * @param personNo 社員番号
     * @param eventDate 実施日時
     * @return 実績情報
     */
    public static LogStock createInspectionLog(TrnMaterial meterial, Integer eventNum, String note, String personNo, Date eventDate) {
        LogStock logStock = new LogStock();
        logStock.eventKind = WarehouseEvent.INSPECTION.getId();
        logStock.materialNo = meterial.getMaterialNo();
        logStock.supplyNo = meterial.getSupplyNo();
        logStock.itemNo = meterial.getItemNo();
        logStock.orderNo = meterial.getOrderNo();
        logStock.productNo = meterial.getProduct().getProductNo();
        logStock.productName = meterial.getProduct().getProductName();
        if (Objects.nonNull(meterial.getLocation())) {
            logStock.areaName = meterial.getLocation().getAreaName();
            logStock.locationNo = meterial.getLocation().getLocationNo();
        }
        logStock.eventNum = eventNum;
        logStock.personNo = personNo;
        logStock.eventDate = eventDate;
        logStock.createDate = eventDate;
        logStock.serialNo = meterial.getSerialNo();
        logStock.category = meterial.getCategory();
        logStock.inStockNum = meterial.getInStockNum();
        logStock.requestNum = meterial.getInStockNum();
        logStock.partsNo = meterial.getPartsNo();
        logStock.note = note;
        logStock.adjustment = null;

        return logStock;
    }

    /**
     * 入庫実績情報を作成する。
     *
     * @param eventKind イベント種別
     * @param meterial 資材情報
     * @param eventNum 入庫数
     * @param personNo 社員番号
     * @param eventDate 入庫日時
     * @return 入出庫実績情報
     */
    public static LogStock createEntryLog(Short eventKind, TrnMaterial meterial, Integer eventNum, String personNo, Date eventDate) {
        LogStock logStock = new LogStock();
        logStock.eventKind = eventKind;
        logStock.materialNo = meterial.getMaterialNo();
        logStock.supplyNo = meterial.getSupplyNo();
        logStock.itemNo = meterial.getItemNo();
        logStock.orderNo = meterial.getOrderNo();
        logStock.productNo = meterial.getProduct().getProductNo();
        logStock.productName = meterial.getProduct().getProductName();
        if (Objects.nonNull(meterial.getLocation())) {
            logStock.areaName = meterial.getLocation().getAreaName();
            logStock.locationNo = meterial.getLocation().getLocationNo();
        }
        logStock.eventNum = eventNum;
        logStock.personNo = personNo;
        logStock.eventDate = eventDate;
        logStock.createDate = eventDate;
        logStock.serialNo = meterial.getSerialNo();
        logStock.category = meterial.getCategory();
        logStock.inStockNum = meterial.getInStockNum();
        logStock.partsNo = meterial.getPartsNo();
        logStock.adjustment = null;

        return logStock;
    }

    /**
     * 出庫実績情報を作成する。
     *
     * @param eventKind イベント種別
     * @param meterial 資材情報
     * @param deliveryItem 出庫指示アイテム情報
     * @param eventNum 出庫数
     * @param personNo 社員番号
     * @param eventDate 入庫日時
     * @return 入出庫実績情報
     */
    public static LogStock createLeaveLog(Short eventKind, TrnMaterial meterial, TrnDeliveryItem deliveryItem, Integer eventNum, String personNo, Date eventDate) {
        LogStock logStock = new LogStock();
        logStock.eventKind = eventKind;
        logStock.materialNo = meterial.getMaterialNo();
        logStock.supplyNo = meterial.getSupplyNo();
        logStock.deliveryNo = deliveryItem.getPK().getDeliveryNo();
        logStock.itemNo = deliveryItem.getPK().getItemNo();
        logStock.orderNo = deliveryItem.getOrderNo();
        logStock.productNo = meterial.getProduct().getProductNo();
        logStock.productName = meterial.getProduct().getProductName();
        if (Objects.nonNull(meterial.getLocation())) {
            logStock.areaName = meterial.getLocation().getAreaName();
            logStock.locationNo = meterial.getLocation().getLocationNo();
        }
        logStock.eventNum = eventNum;
        logStock.personNo = personNo;
        logStock.eventDate = eventDate;
        logStock.createDate = eventDate;
        logStock.serialNo = deliveryItem.getSerialNo();
        logStock.category = meterial.getCategory();
        logStock.inStockNum = meterial.getInStockNum();
        logStock.partsNo = meterial.getPartsNo();
        logStock.adjustment = null;

        return logStock;
    }

    /**
     * 出荷払出実績情報を作成する。
     *
     * @param eventKind イベント種別
     * @param meterial 資材情報
     * @param deliveryItem 出庫指示アイテム情報
     * @param eventNum 出庫数
     * @param personNo 社員番号
     * @param eventDate 出庫日時
     * @param createDate 作成日
     * @return 入出庫実績情報
     */
    public static LogStock createShippingLog(Short eventKind, TrnMaterial meterial, TrnDeliveryItem deliveryItem, Integer eventNum, String personNo, Date eventDate, Date createDate) {
        LogStock logStock = new LogStock();
        logStock.eventKind = eventKind;
        logStock.materialNo = meterial.getMaterialNo();
        logStock.supplyNo = meterial.getSupplyNo();
        logStock.deliveryNo = deliveryItem.getPK().getDeliveryNo();
        logStock.itemNo = deliveryItem.getPK().getItemNo();
        logStock.orderNo = deliveryItem.getOrderNo();
        logStock.productNo = meterial.getProduct().getProductNo();
        logStock.productName = meterial.getProduct().getProductName();
        if (Objects.nonNull(meterial.getLocation())) {
            logStock.areaName = meterial.getLocation().getAreaName();
            logStock.locationNo = meterial.getLocation().getLocationNo();
        }
        logStock.eventNum = eventNum;
        logStock.personNo = personNo;
        logStock.eventDate = eventDate;
        logStock.createDate = createDate;
        logStock.serialNo = deliveryItem.getSerialNo();
        logStock.category = meterial.getCategory();
        logStock.inStockNum = meterial.getInStockNum();
        logStock.requestNum = deliveryItem.getRequiredNum();
        logStock.partsNo = meterial.getPartsNo();
        logStock.adjustment = null;

        return logStock;
    }

    /**
     * 棚卸実績情報を作成する。
     *
     * @param warehouseEvent イベント種別
     * @param meterial 資材情報
     * @param personNo 社員番号
     * @param eventDate 棚卸完了日時
     * @param adjustment 在庫調整　※棚卸のみ
     * @return 入出庫実績情報
     */
    public static LogStock createInventoryLog(WarehouseEvent warehouseEvent, TrnMaterial meterial, String personNo, Date eventDate, Integer adjustment) {
        LogStock logStock = new LogStock();

        logStock.eventKind = warehouseEvent.getId();
        logStock.materialNo = meterial.getMaterialNo();
        logStock.supplyNo = meterial.getSupplyNo();
        logStock.deliveryNo = null;
        logStock.itemNo = meterial.getItemNo();
        logStock.orderNo = null;
        logStock.productNo = meterial.getProduct().getProductNo();
        logStock.productName = meterial.getProduct().getProductName();
        if (Objects.equals(warehouseEvent, WarehouseEvent.INVENTORY_IMPL)) {
            // 棚卸実施
            if (Objects.nonNull(meterial.getInventoryLocation())) {
                logStock.areaName = meterial.getInventoryLocation().getAreaName();
                logStock.locationNo = meterial.getInventoryLocation().getLocationNo();
            } else if (Objects.nonNull(meterial.getLocation())) {
                logStock.areaName = meterial.getLocation().getAreaName();
                logStock.locationNo = meterial.getLocation().getLocationNo();
            }

            // 棚卸在庫数を数量にセットする。
            logStock.eventNum = meterial.getInventoryNum();
        } else {
            // 棚卸
            if (Objects.nonNull(meterial.getLocation())) {
                logStock.areaName = meterial.getLocation().getAreaName();
                logStock.locationNo = meterial.getLocation().getLocationNo();
            }

            // 在庫数を数量にセットする。
            logStock.eventNum = meterial.getInStockNum();
        }

        logStock.personNo = personNo;
        logStock.eventDate = eventDate;
        logStock.createDate = eventDate;
        logStock.serialNo = meterial.getSerialNo();
        logStock.category = meterial.getCategory();
        logStock.inStockNum = meterial.getInStockNum();
        logStock.partsNo = meterial.getPartsNo();
        logStock.adjustment = adjustment;

        return logStock;
    }

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
    public Short getEventKind() {
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
        return this.orderNo;
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
     * 品目を取得する。
     *
     * @return 品目
     */
    public String getProductNo() {
        return this.productNo;
    }

    /**
     * 品名を取得する。
     * 
     * @return 品名 
     */
    public String getProductName() {
        return productName;
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
    public Date getEventDate() {
        return this.eventDate;
    }

    /**
     * 作成日時を取得する。
     *
     * @return 作成日時
     */
    public Date getCreateDate() {
        return this.createDate;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDate() {
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
    public Short getCategory() {
        return this.category;
    }

    /**
     * 外部システムと同期されたかを返す。
     *
     * @return 同期フラグ
     */
    public Boolean isSynced() {
        return this.synced;
    }

    /**
     * 同期フラグを設定する。
     *
     * @param synced 同期フラグ true:同期済、false:未同期
     */
    public void setSynced(Boolean synced) {
        this.synced = synced;
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
     * 在庫数を設定する。
     *
     * @param inStockNum 在庫数
     */
    public void setInStockNum(Integer inStockNum) {
        this.inStockNum = inStockNum;
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
     * 在庫調整を設定する。
     *
     * @param adjustment 在庫調整
     */
    public void setAdjustment(Integer adjustment) {
        this.adjustment = adjustment;
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
     * 部品番号を設定する。
     * 
     * @param partsNo 部品番号
     */
    public void setPartsNo(String partsNo) {
        this.partsNo = partsNo;
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
        if (!(object instanceof LogStock)) {
            return false;
        }
        LogStock other = (LogStock) object;
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
                .append(", synced=").append(this.synced)
                .append(", inStockNum=").append(this.inStockNum)
                .append(", adjustment=").append(this.adjustment)
                .append("}")
                .toString();
    }
}
