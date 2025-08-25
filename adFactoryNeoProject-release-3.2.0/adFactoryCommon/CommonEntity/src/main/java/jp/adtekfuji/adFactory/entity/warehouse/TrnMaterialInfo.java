/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

import adtekfuji.rest.LocalDateTimeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 資材情報
 *
 * @author s-heya
 */
@XmlRootElement(name = "material")
@XmlAccessorType(XmlAccessType.FIELD)
public class TrnMaterialInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 資材番号の接頭語 (購入品)
    public static final String SUPPLY_PREFIX = "$$";
    // 資材番号の接頭語 (加工品)
    public static final String ORDER_PREFIX = "&&";

    private BooleanProperty inventoryConfirmProperty;

    @XmlElement
    private String materialNo;
    @XmlElement
    private String supplyNo;
    @XmlElement
    private Integer itemNo;
    @XmlElement
    private String orderNo;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime arrivalPlan;
    @XmlElement
    private Integer arrivalNum;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime arrivalDate;
    @XmlElement
    private Integer stockNum;
    @XmlElement
    private Integer deliveryNum;
    @XmlElement
    private Integer inStockNum;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime stockDate;
    @XmlTransient
    private Map<String, String> property;
    @XmlElement
    private Integer verInfo;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime createDate;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime updateDate;
    @XmlElement
    private MstProductInfo product;
    @XmlElement
    private MstLocationInfo location;
    @XmlElement
    private String serialNo;
    @XmlElement
    private String partsNo;
    @XmlElement
    private Short category;
    @XmlElement
    private Integer inventoryNum;
    @XmlElement
    private MstLocationInfo inventoryLocation;
    @XmlElement
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime inventoryDate;
    @XmlElement
    private String inventoryPersonNo;
    @XmlElement
    private Boolean inventoryFlag = false;
    @XmlTransient
    private boolean parent = false;
    @XmlElement
    private String sepc;
    @XmlElement
    private String unitNo;
    @XmlElement
    private String note;
    @XmlElement
    private Integer defectNum;
    @XmlElement
    private Date inspectedAt;
    @XmlElement
    private Integer branchNo;

    /**
     * コンストラクタ
     */
    public TrnMaterialInfo() {
    }

    /**
     * 資材情報を生成する。
     *
     * @param product 部品マスタ
     * @return 親情報
     */
    public static TrnMaterialInfo create(MstProductInfo product) {
        TrnMaterialInfo material = new TrnMaterialInfo();
        material.product = product;
        material.setSupplyNo(product.getProductNo());
        material.setOrderNo(product.getProductName());
        material.parent = true;
        return material;
    }

    /**
     * 資材情報を生成する。
     *
     * @param unitNo ユニット番号
     * @return 親情報
     */
    public static TrnMaterialInfo unitoNo(String unitNo) {
        TrnMaterialInfo material = new TrnMaterialInfo();
        material.product = new MstProductInfo();
        material.setSupplyNo(unitNo);
        material.parent = true;
        return material;
    }

    /**
     * 在庫数プロパティを取得する。
     *
     * @return 在庫数
     */
    public IntegerProperty inStockNumProperty() {
        if (Objects.isNull(this.inStockNum)) {
            this.inStockNum = 0;
        }
        return new SimpleIntegerProperty(this.inStockNum);
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
     * 納入番号を取得する。
     *
     * @return 納入番号
     */
    public String getSupplyNo() {
        return this.supplyNo;
    }

    /**
     * 納入番号を設定する。
     *
     * @param supplyNo 納入番号
     */
    public void setSupplyNo(String supplyNo) {
        this.supplyNo = supplyNo;
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
     * 明細番号を設定する。
     *
     * @param itemNo 明細番号
     */
    public void setItemNo(Integer itemNo) {
        this.itemNo = itemNo;
    }

    /**
     * 製造オーダー番号を取得する。
     *
     * @return 製造番号
     */
    public String getOrderNo() {
        return this.orderNo;
    }

    /**
     * 製造オーダー番号を設定する。
     *
     * @param orderNo 製造番号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    //public long getProductId() {
    //    return this.productId;
    //}
    //
    //public void setProductId(long productId) {
    //    this.productId = productId;
    //}
    //
    //public BigInteger getLocationId() {
    //    return this.locationId;
    //}
    //
    //public void setLocationId(BigInteger locationId) {
    //    this.locationId = locationId;
    //}

    /**
     * 納入予定日を取得する。
     *
     * @return 納入予定日
     */
    public LocalDateTime getArrivalPlan() {
        return this.arrivalPlan;
    }

    /**
     * 納入予定日を設定する。
     *
     * @param arrivalPlan 納入予定日
     */
    public void setArrivalPlan(LocalDateTime arrivalPlan) {
        this.arrivalPlan = arrivalPlan;
    }

    /**
     * 納入予定数を取得する。
     *
     * @return 納入予定数
     */
    public Integer getArrivalNum() {
        return this.arrivalNum;
    }

    /**
     * 納入予定数を設定する。
     *
     * @param arrivalNum 納入予定数を設定する。
     */
    public void setArrivalNum(Integer arrivalNum) {
        this.arrivalNum = arrivalNum;
    }

    /**
     * 納入日時を取得する。
     *
     * @return 納入日時を取得する。
     */
    public LocalDateTime getArrivalDate() {
        return this.arrivalDate;
    }

    /**
     * 納入日時を設定する。
     *
     * @param arrivalDate 納入日時
     */
    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    /**
     * 入庫数を取得する。
     *
     * @return 入庫数
     */
    public Integer getStockNum() {
        return this.stockNum;
    }

    /**
     * 入庫数を設定する。
     *
     * @param stockNum 入庫数
     */
    public void setStockNum(Integer stockNum) {
        this.stockNum = stockNum;
    }

    /**
     * 出庫数を取得する。
     *
     * @return 出庫数
     */
    public Integer getDeliveryNum() {
        return this.deliveryNum;
    }

    /**
     * 出庫数を設定する。
     *
     * @param deliveryNum 出庫数
     */
    public void setDeliveryNum(Integer deliveryNum) {
        this.deliveryNum = deliveryNum;
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
     * 最終入庫日時を取得する。
     *
     * @return 最終入庫日時
     */
    public LocalDateTime getStockDate() {
        return this.stockDate;
    }

    /**
     * 最終入庫日時を設定する。
     *
     * @param stockDate 最終入庫日時
     */
    public void setStockDate(LocalDateTime stockDate) {
        this.stockDate = stockDate;
    }

    /**
     * プロパティを取得する。
     *
     * @return プロパティ
     */
    public Map<String, String> getProperty() {
        return this.property;
    }

    /**
     * プロパティを設定する。
     *
     * @param property プロパティ
     */
    public void setProperty(Map<String, String> property) {
        this.property = property;
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
     * 作成日時を設定する。
     *
     * @param createDate 作成日時
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
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
     * 更新日時を設定する。
     *
     * @param updateDate 更新日時
     */
    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 部品マスタを取得する。
     *
     * @return
     */
    public MstProductInfo getProduct() {
        return this.product;
    }

    /**
     * 部品マスタを設定する。
     *
     * @param product 部品マスタ
     */
    public void setProduct(MstProductInfo product) {
        this.product = product;
    }

    /**
     * 保管棚を取得する。
     *
     * @return 保管棚
     */
    public MstLocationInfo getLocation() {
        return this.location;
    }

    /**
     * 保管棚を設定する。
     *
     * @param location 保管棚
     */
    public void setLocation(MstLocationInfo location) {
        this.location = location;
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
     * 部品番号を取得する。
     *
     * @return 部品番号
     */
    public String getPartsNo() {
        return this.partsNo;
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
     * 手配区分を取得する。
     *
     * @return 手配区分
     */
    public Short getCategory() {
        return this.category;
    }

    /**
     * 手配区分を設定する。
     *
     * @param category 手配区分
     */
    public void setCategory(Short category) {
        this.category = category;
    }

    /**
     * 棚卸在庫数を取得する。
     *
     * @return 棚卸在庫数
     */
    public Integer getInventoryNum() {
        return this.inventoryNum;
    }

    /**
     * 棚卸在庫数を設定する。
     *
     * @param inventoryNum 棚卸在庫数
     */
    public void setInventoryNum(Integer inventoryNum) {
        this.inventoryNum = inventoryNum;
    }

    /**
     * 棚番訂正を取得する。
     *
     * @return 棚番訂正
     */
    public MstLocationInfo getInventoryLocation() {
        return this.inventoryLocation;
    }

    /**
     * 棚番訂正を設定する。
     *
     * @param inventoryLocation 棚番訂正
     */
    public void setInventoryLocation(MstLocationInfo inventoryLocation) {
        this.inventoryLocation = inventoryLocation;
    }

    /**
     * 棚卸実施日時を取得する。
     *
     * @return 棚卸実施日時
     */
    public LocalDateTime getInventoryDate() {
        return this.inventoryDate;
    }

    /**
     * 棚卸実施日時を設定する。
     *
     * @param inventoryDate 棚卸実施日時
     */
    public void setInventoryDate(LocalDateTime inventoryDate) {
        this.inventoryDate = inventoryDate;
    }

    /**
     * 棚卸実施者を取得する。
     *
     * @return 棚卸実施者
     */
    public String getInventoryPersonNo() {
        return this.inventoryPersonNo;
    }

    /**
     * 棚卸実施者を設定する。
     *
     * @param inventoryPersonNo 棚卸実施者
     */
    public void setInventoryPersonNo(String inventoryPersonNo) {
        this.inventoryPersonNo = inventoryPersonNo;
    }

    /**
     * 棚卸実施を取得する。
     *
     * @return 棚卸実施
     */
    public Boolean getInventoryFlag() {
        return this.inventoryFlag;
    }

    /**
     * 棚卸実施を設定する。
     *
     * @param inventoryFlag 棚卸実施
     */
    public void setInventoryFlag(Boolean inventoryFlag) {
        this.inventoryFlag = inventoryFlag;
    }

    /**
     * 型式・仕様を取得する。
     * @return 型式・仕様
     */
    public String getSepc() {
        if (Objects.nonNull(this.property)) {
            return this.property.get("Spec");
        }
        return sepc;
    }

    /**
     * 型式・仕様を取得する。
     * @param sepc 型式・仕様
     */
    public void setSepc(String sepc) {
        this.sepc = sepc;
    }

    /**
     * ユニット番号を取得する。
     * @return ユニット番号
     */
    public String getUnitNo() {
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
     * 備考を取得する。 
     * @return 備考
     */
    public String getNote() {
        return note;
    }

    /**
     * 備考を設定する。
     * @param note 備考
     */
    public void setNote(String note) {
        this.note = note;
    }

   /**
     * 不良数を取得する。
     * 
     * @return  不良数
     */
    public Integer getDefectNum() {
        return defectNum;
    }

    /**
     * 検査実施日時を取得する。
     * 
     * @return 検査実施日時
     */
    public Date getInspectedAt() {
        return inspectedAt;
    }

    /**
     * 親項目かどうかを返す。
     *
     * @return
     */
    public boolean isParent() {
        return this.parent;
    }

    /**
     * 末尾番号を取得する。
     *
     * @return 末尾番号
     */
    public Integer getBranchNo() {
        if (Objects.isNull(this.branchNo)) {
            return 0;
        }
        return this.branchNo;
    }

    /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.materialNo != null ? this.materialNo.hashCode() : 0);
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
        if (!(object instanceof TrnMaterialInfo)) {
            return false;
        }
        TrnMaterialInfo other = (TrnMaterialInfo) object;
        return Objects.equals(this.materialNo, other.materialNo);
    }

    @Override
    public String toString() {
        return new StringBuilder("TrnMaterial{")
                .append("materialNo=").append(this.materialNo)
                .append(", category=").append(this.category)
                .append(", supplyNo=").append(this.supplyNo)
                .append(", itemNo=").append(this.itemNo)
                .append(", orderNo=").append(this.orderNo)
                .append(", serialNo=").append(this.serialNo)
                .append(", product=").append(this.product)
                .append(", location=").append(this.location)
                .append(", arrivalPlan=").append(this.arrivalPlan)
                .append(", arrivalNum=").append(this.arrivalNum)
                .append(", arrivalDate=").append(this.arrivalDate)
                .append(", stockNum=").append(this.stockNum)
                .append(", deliveryNum=").append(this.deliveryNum)
                .append(", inStockNum=").append(this.inStockNum)
                .append(", stockDate=").append(this.stockDate)
                .append(", property=").append(this.property)
                .append(", inventoryNum=").append(this.inventoryNum)
                .append(", inventoryLocation=").append(this.inventoryLocation)
                .append(", inventoryDate=").append(this.inventoryDate)
                .append(", inventoryPersonNo=").append(this.inventoryPersonNo)
                .append(", inventoryFlag=").append(this.inventoryFlag)
                .append(", defectNum=").append(this.defectNum)
                .append(", inspectedAt=").append(this.inspectedAt)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
