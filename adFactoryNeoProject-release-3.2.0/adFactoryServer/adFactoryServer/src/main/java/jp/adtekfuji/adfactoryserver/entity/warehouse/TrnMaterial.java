/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adfactoryserver.utility.JsonMapConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 資材情報
 *
 * @author s-heya
 */
@Entity
@Table(name = "trn_material")
@XmlRootElement(name = "material")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
@NamedQueries({
    @NamedQuery(name = "TrnMaterial.find", query = "SELECT o FROM TrnMaterial o WHERE o.materialNo = :materialNo"),
    @NamedQuery(name = "TrnMaterial.findBySupplyNo", query = "SELECT o FROM TrnMaterial o WHERE o.supplyNo = :supplyNo"),
    @NamedQuery(name = "TrnMaterial.findByOrderNo", query = "SELECT o FROM TrnMaterial o WHERE o.orderNo = :orderNo"),
    @NamedQuery(name = "TrnMaterial.findByProductId", query = "SELECT o FROM TrnMaterial o WHERE o.product.productId = :productId"),
    @NamedQuery(name = "TrnMaterial.findInStock", query = "SELECT o FROM TrnMaterial o WHERE o.location.areaName = :areaName AND o.product.productId IN :productIds AND o.inStockNum > 0 ORDER BY o.product.productId, o.arrivalDate"),
    @NamedQuery(name = "TrnMaterial.deleteAllByProductId", query = "DELETE FROM TrnMaterial o WHERE o.product.productId = :productId"),
    @NamedQuery(name = "TrnMaterial.maxBranchNo", query = "SELECT MAX(o.branchNo) FROM TrnMaterial o WHERE o.supplyNo LIKE :supplyNo"),
    // 部品番号を取得する
    @NamedQuery(name = "TrnMaterial.maxPartsNo", query = "SELECT MAX(o.partsNo) FROM TrnMaterial o WHERE o.product.productId = :productId"),
    // すべての棚の棚卸実施中の資材情報を取得する。
    @NamedQuery(name = "TrnMaterial.findInventoryByAllLocation", query = "SELECT o FROM TrnMaterial o WHERE o.inventoryFlag = TRUE AND o.location IS NOT NULL ORDER BY o.location.locationId, o.product.productId"),
    // 区画名を指定して、棚卸実施中の資材情報を取得する。
    @NamedQuery(name = "TrnMaterial.findInventoryByAreaName", query = "SELECT o FROM TrnMaterial o WHERE o.inventoryFlag = TRUE AND o.location IS NOT NULL AND o.location.areaName = :areaName ORDER BY o.location.locationId, o.product.productId"),
    // すべての棚の棚卸実施フラグを更新する。
    @NamedQuery(name = "TrnMaterial.updateAllInventoryFlag", query = "UPDATE TrnMaterial o SET o.inventoryFlag = :inventoryFlag WHERE o.location IS NOT NULL"),
    // すべての棚の棚卸実施フラグを更新する。(棚卸在庫数・棚番訂正を消去する)
    @NamedQuery(name = "TrnMaterial.updateAllInventoryFlagAndInit", query = "UPDATE TrnMaterial o SET o.inventoryFlag = :inventoryFlag, o.inventoryNum = NULL, o.inventoryLocation = NULL WHERE o.location IS NOT NULL"),
    // 区画名を指定して、棚卸実施フラグを更新する。
    @NamedQuery(name = "TrnMaterial.updateAreaInventoryFlag", query = "UPDATE TrnMaterial o SET o.inventoryFlag = :inventoryFlag WHERE o.location IS NOT NULL AND o.location.areaName = :areaName"),
    // 区画名を指定して、棚卸実施フラグを更新する。(棚卸在庫数・棚番訂正を消去する)
    @NamedQuery(name = "TrnMaterial.updateAreaInventoryFlagAndInit", query = "UPDATE TrnMaterial o SET o.inventoryFlag = :inventoryFlag, o.inventoryNum = NULL, o.inventoryLocation = NULL WHERE o.location IS NOT NULL AND o.location.areaName = :areaName"),
    // 区画名を指定して、入出庫が完了した資材情報を削除する。(予定数入庫済で在庫なしの資材情報)
    @NamedQuery(name = "TrnMaterial.deleteCompMaterialByAreaName", query = "DELETE FROM TrnMaterial o WHERE o.location IS NOT NULL AND o.location.areaName = :areaName AND o.stockNum >= o.arrivalNum AND o.inStockNum <= 0"),
    // 区画名・日時を指定して、入出庫が完了した資材情報を削除する。(予定数入庫済で在庫なしの資材情報)
    @NamedQuery(name = "TrnMaterial.deleteCompMaterialByAreaNameDate", query = "DELETE FROM TrnMaterial o WHERE o.updateDate <= :deleteDate AND o.location IS NOT NULL AND o.location.areaName = :areaName AND o.stockNum >= o.arrivalNum AND o.inStockNum <= 0"),
    // 区画名を指定して、最新の棚卸実績日時を取得する。(棚卸在庫数が入力されている情報がある場合のみ日時が返る)
    @NamedQuery(name = "TrnMaterial.lastInventoryDateByAreaName", query = "SELECT MAX(o.inventoryDate) FROM TrnMaterial o WHERE o.inventoryNum IS NOT NULL AND o.location IS NOT NULL AND o.location.areaName = :areaName"),
    //  区画名を指定して、棚卸実施中の資材情報を取得する。
    @NamedQuery(name = "TrnMaterial.findInventoryByAreaName2", query = "SELECT o FROM TrnMaterial o WHERE o.inventoryFlag = TRUE AND o.inventoryNum IS NOT NULL AND o.location IS NOT NULL AND o.location.areaName = :areaName ORDER BY o.location.locationId, o.product.productId"),
})
@Cacheable(false)
public class TrnMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    // 資材番号の接頭語 (購入品)
    public static final String SUPPLY_PREFIX = "$$";
    // 資材番号の接頭語 (加工品)
    public static final String ORDER_PREFIX = "&&";

    private static final Logger logger = LogManager.getLogger();

    /**
     * 資材番号
     */
    @Id
    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 32)
    @Column(name = "material_no")
    private String materialNo;

    /**
     * 納入番号
     */
    @Size(max = 32)
    @Column(name = "supply_no")
    @JsonProperty("supplyNo")
    private String supplyNo;

    /**
     * 明細番号
     */
    @Column(name = "item_no")
    @JsonProperty("no")
    private Integer itemNo;

    /**
     * 製造オーダー番号
     */
    @Size(max = 32)
    @Column(name = "order_no")
    @JsonProperty("orderNo")
    private String orderNo;

    //@Basic(optional = false)
    //@NotNull
    //@Column(name = "product_id")
    //private long productId;
    //@Column(name = "location_id")
    //private BigInteger locationId;

    /**
     * 納入予定日
     */
    @Column(name = "arrival_plan")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("arrPlan")
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date arrivalPlan;

    /**
     * 納入予定数
     */
    @Column(name = "arrival_num")
    @JsonProperty("arrNum")
    private Integer arrivalNum;

    /**
     * 納入日時
     */
    @Column(name = "arrival_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date arrivalDate;

    /**
     * 入庫数
     */
    @Column(name = "stock_num")
    private Integer stockNum;

    /**
     * 出庫数
     */
    @Column(name = "delivery_num")
    private Integer deliveryNum;

    /**
     * 在庫数
     */
    @Column(name = "in_stock_num")
    @JsonProperty("inStockNum")
    private Integer inStockNum;

    /**
     * 最終入庫日時
     */
    @Column(name = "stock_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stockDate;

    /**
     * プロパティ
     */
    @Column(name = "property", length = 30000)
    @Convert(converter = JsonMapConverter.class)
    @XmlTransient
    @JsonIgnore
    private Map<String, String> property;

    /**
     * 排他用バージョン
     */
    @Version
    @Column(name = "ver_info")
    private Integer verInfo;

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
     * 部品情報
     */
    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "product_id")
    private MstProduct product;

    /**
     * 保管棚
     */
    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "location_id")
    private MstLocation location;

    /**
     * 製造番号
     */
    @Size(max = 32)
    @Column(name = "serial_no")
    private String serialNo;

    /**
     * 部品番号
     */
    @Size(max = 32)
    @Column(name = "parts_no")
    private String partsNo;

    /**
     * 手配区分
     */
    @Column(name = "category")
    @JsonProperty("category")
    private Short category;

    /**
     * 末尾番号
     */
    @Column(name = "branch_no")
    private Integer branchNo;

    /**
     * 棚卸在庫数
     */
    @Column(name = "inventory_num")
    private Integer inventoryNum;

    /**
     * 棚番訂正
     */
    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "inventory_location_id")
    private MstLocation inventoryLocation;

    /**
     * 棚卸実施日時
     */
    @Column(name = "inventory_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inventoryDate;

    /**
     * 棚卸実施者
     */
    @Column(name = "inventory_person_no")
    private String inventoryPersonNo;

    /**
     * 棚卸実施
     */
    @Column(name = "inventory_flag")
    private Boolean inventoryFlag = false;

    /**
     * 型式・仕様
     */
    @Column(name = "spec")
    private String sepc;
    
    /**
     * ユニット番号
     */
    @Column(name = "unit_no")
    @JsonIgnore
    private String unitNo;
    
    /**
     * 不良数
     */
    @Column(name = "defect_num")
    @JsonIgnore
    private Integer defectNum;
    
    /**
     * 検査実施日時
     */
    @Column(name = "inspected_at")
    @JsonIgnore
    private Date inspectedAt;
        
    /**
     * 備考
     */
    @Column(name = "note")
    @JsonIgnore
    private String note;
    
    @Transient
    @XmlTransient
    @JsonProperty("prodNo")
    private String jsonProductNo;

    @Transient
    @XmlTransient
    @JsonProperty("prodName")
    private String jsonProductName;

    @Transient
    @XmlTransient
    @JsonProperty("mod")
    private Short jsonModifyFlag;

    @Transient
    @XmlTransient
    @JsonProperty("del")
    private Short jsonDeleteFlag;

    /**
     * 現品ラベルのQRコードに入っている仕分数「QTY=#」
     */
    @Transient
    @XmlTransient
    private Integer sortNum;
    
    // NEXASで不要であれば削除する
    //@Transient
    //@XmlTransient
    //@JsonProperty("areaNo")
    //private String jsonAreaNo;
    
    @Transient
    @XmlTransient
    @JsonProperty("areaName")
    private String jsonAreaName;

    @Transient
    @XmlTransient
    @JsonProperty("locNo")
    private String jsonLocNo;
    
    @Transient
    @XmlTransient
    private int requiredNum;
   
    @Transient
    @JsonProperty("material")
    private String jsonMaterial;    // 材質
    
    @Transient
    @JsonProperty("vendor")
    private String jsonVendor;      // メーカー
    
    @Transient
    @JsonProperty("spec")
    private String jsonSpec;        // 規格・型式
    
    @Transient
    @JsonProperty("note")
    private String jsonNote;        // 備考

    @Transient
    @JsonProperty("unitNo")
    private String jsonUnitNo;       // ユニット番号

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "material_no")
    @XmlTransient // JAXB にて無限ループに陥るため
    private List<TrnReserveMaterial> reserveMaterials;

    /**
     * 資材番号 Comparator
     */
    public static final Comparator<TrnMaterial> materialNoComparator = (p1, p2) -> {
        String value1 = Objects.isNull(p1.getMaterialNo()) ? "" : p1.getMaterialNo();
        String value2 = Objects.isNull(p2.getMaterialNo()) ? "" : p2.getMaterialNo();
        return value1.compareTo(value2);
    };

    /**
     * コンストラクタ
     */
    public TrnMaterial() {
    }

    /**
     * コンストラクタ
     *
     * @param materialNo 資材番号
     * @param supplyNo 納入番号
     * @param category 手配区分
     * @param createDate 作成日時
     */
    public TrnMaterial(String materialNo, String supplyNo, Short category, Date createDate) {
        this.materialNo = materialNo;
        this.supplyNo = supplyNo;
        this.arrivalNum = 0;
        this.stockNum = 0;
        this.deliveryNum = 0;
        this.inStockNum = 0;
        this.category = category;
        this.createDate = createDate;
        this.branchNo = 0;
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

    //public long getProductId() {
    //    return productId;
    //}
    //
    //public void setProductId(long productId) {
    //    this.productId = productId;
    //}
    //
    //public BigInteger getLocationId() {
    //    return locationId;
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
    public Date getArrivalPlan() {
        return this.arrivalPlan;
    }

    /**
     * 納入予定日を設定する。
     *
     * @param arrivalPlan 納入予定日
     */
    public void setArrivalPlan(Date arrivalPlan) {
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
    public Date getArrivalDate() {
        return this.arrivalDate;
    }

    /**
     * 納入日時を設定する。
     *
     * @param arrivalDate 納入日時
     */
    public void setArrivalDate(Date arrivalDate) {
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
    public Date getStockDate() {
        return this.stockDate;
    }

    /**
     * 最終入庫日時を設定する。
     *
     * @param stockDate 最終入庫日時
     */
    public void setStockDate(Date stockDate) {
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
     * 排他用バージョンを取得する。
     *
     * @return 排他用バージョン
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バージョンを設定する。
     *
     * @param verInfo 排他用バージョン
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
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
     * 作成日時を設定する。
     *
     * @param createDate 作成日時
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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
     * 更新日時を設定する。
     *
     * @param updateDate 更新日時
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 部品マスタを取得する。
     *
     * @return
     */
    public MstProduct getProduct() {
        return this.product;
    }

    /**
     * 部品マスタを設定する。
     *
     * @param product 部品マスタ
     */
    public void setProduct(MstProduct product) {
        this.product = product;
    }

    /**
     * 保管棚を取得する。
     *
     * @return 保管棚
     */
    public MstLocation getLocation() {
        return this.location;
    }

    /**
     * 保管棚を設定する。
     *
     * @param location 保管棚
     */
    public void setLocation(MstLocation location) {
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
     * 末尾番号を取得する。
     *
     * @return 末尾番号
     */
    public Integer getBranchNo() {
        return this.branchNo;
    }

    /**
     * 末尾番号を設定する。
     *
     * @param branchNo 末尾番号
     */
    public void setBranchNo(Integer branchNo) {
        this.branchNo = branchNo;
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
    public MstLocation getInventoryLocation() {
        return this.inventoryLocation;
    }

    /**
     * 棚番訂正を設定する。
     *
     * @param inventoryLocation 棚番訂正
     */
    public void setInventoryLocation(MstLocation inventoryLocation) {
        this.inventoryLocation = inventoryLocation;
    }

    /**
     * 棚卸実施日時を取得する。
     *
     * @return 棚卸実施日時
     */
    public Date getInventoryDate() {
        return this.inventoryDate;
    }

    /**
     * 棚卸実施日時を設定する。
     *
     * @param inventoryDate 棚卸実施日時
     */
    public void setInventoryDate(Date inventoryDate) {
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
     * 品目を取得する。
     *
     * @return 品目
     */
    public String getJsonProductNo() {
        return this.jsonProductNo;
    }

    /**
     * 品名を取得する。
     *
     * @return 品名
     */
    public String getJsonProductName() {
        return this.jsonProductName;
    }

    /**
     * 修正フラグを取得する。
     *
     * @return 修正フラグ
     */
    public Short getJsonModifyFlag() {
        if (Objects.isNull(this.jsonModifyFlag)) {
            return 0;
        }
        return this.jsonModifyFlag;
    }

    /**
     * 削除フラグを取得する。
     *
     * @return 削除フラグ
     */
    public Short getJsonDeleteFlag() {
        if (Objects.isNull(this.jsonDeleteFlag)) {
            return 0;
        }
        return this.jsonDeleteFlag;
    }

    /**
     * 倉庫コードを取得する。
     *
     * @return 倉庫コード
     */
    //public String getJsonAreaNo() {
    //    return this.jsonAreaNo;
    //}

    /**
     * 倉庫コードを設定する。
     *
     * @param jsonAreaNo 倉庫コード
     */
    //public void setJsonAreaNo(String jsonAreaNo) {
    //    this.jsonAreaNo = jsonAreaNo;
    //}

    /**
     * 区画名を取得する。
     * 
     * @return 区画名
     */
    public String getJsonAreaName() {
        return jsonAreaName;
    }

    /**
     * 区画名を設定する。
     * 
     * @param jsonAreaName 区画名
     */
    public void setJsonAreaName(String jsonAreaName) {
        this.jsonAreaName = jsonAreaName;
    }

    /**
     * 棚番号を取得する。
     * 
     * @return 棚番号
     */
    public String getJsonLocNo() {
        return jsonLocNo;
    }

    /**
     * 棚番号を設定する。
     * 
     * @param jsonLocNo 棚番号 
     */
    public void setJsonLocNo(String jsonLocNo) {
        this.jsonLocNo = jsonLocNo;
    }

    /**
     * 要求数を取得する。
     *
     * @return 要求数
     */
    public int getRequiredNum() {
        return this.requiredNum;
    }

    /**
     * 要求数を設定する。
     *
     * @param requiredNum 要求数
     */
    public void setRequiredNum(int requiredNum) {
        this.requiredNum = requiredNum;
    }

    /**
     * 型式・仕様を取得する。
     * @return 型式・仕様
     */
    public String getSepc() {
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
     * 仕分数を取得する。
     * @return 仕分数
     */
    public Integer getSortNum() {
        return sortNum;
    }

    /**
     * 仕分数を設定する。
     * @param sortNum 仕分数
     */
    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
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
     *  不良数を設定する。
     * 
     * @param defectNum  不良数 
     */
    public void setDefectNum(Integer defectNum) {
        this.defectNum = defectNum;
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
     * 検査実施日時を設定する。
     * 
     * @param inspectedAt 検査実施日時
     */
    public void setInspectedAt(Date inspectedAt) {
        this.inspectedAt = inspectedAt;
    }

    /**
     * 材質を取得する。
     * @return 材質
     */
    public String getJosnMaterial() {
        return jsonMaterial;
    }

    /**
     * メーカーを取得する。
     * @return メーカー
     */
    public String getJsonVendor() {
        return jsonVendor;
    }

    /**
     * 型式・仕様を取得する。
     * @return 型式・仕様
     */
    public String getJsonSpec() {
        return jsonSpec;
    }

    /**
     * 備考を取得する。
     * @return 備考
     */
    public String getJosnNote() {
        return jsonNote;
    }

    /**
     * ユニット番号を取得する。
     * 
     * @return ユニット番号
     */
    public String getJsonUnitNo() {
        return jsonUnitNo;
    }

    /**
     * ユニット番号を設定する。
     * 
     * @param jsonUnitNo ユニット番号
     */
    public void setJsonUnitNo(String jsonUnitNo) {
        this.jsonUnitNo = jsonUnitNo;
    }
    
    /**
     * 製造番号が含まれているかどうかを返す。
     * 
     * @param modelName
     * @param orderNo
     * @return 
     */
    public boolean containsOrderNo(String modelName, String orderNo) {
        try {
            if (StringUtils.isEmpty(this.getOrderNo())) {
                return false;
            }

            String[] array = this.getOrderNo().split("-");
            if (array.length < 2) {
                return false;
            }

            if (!StringUtils.equals(modelName, array[0])) {
                return false;
            }

            if (StringUtils.equals(this.getOrderNo(), orderNo)) {
                return true;
            }
            
            int order = Integer.parseInt(orderNo);
            int begin = Integer.parseInt(array[array.length - 2]);
            int end = Integer.parseInt(array[array.length - 1]);
            
            return order >= begin && order <= end;

        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    /**
     * 有効在庫数を取得する。
     * 
     * @return 有効在庫数
     */
    public int getAvailableNum() {
        if (Objects.isNull(this.reserveMaterials)) {
            return this.getInStockNum();
        }
        return this.getInStockNum() - this.reserveMaterials.stream().mapToInt(o -> o.getReservedNum()).sum();
    }

    /**
     * 在庫引当情報を取得する。
     * 
     * @return 
     */
    public List<TrnReserveMaterial> getReserveMaterials() {
        if (Objects.isNull(this.reserveMaterials)) {
            this.reserveMaterials = new ArrayList<>();
        }
        return this.reserveMaterials;
    }
   
    /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (Objects.nonNull(this.materialNo) ? this.materialNo.hashCode() : 0);
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
        if (!(object instanceof TrnMaterial)) {
            return false;
        }
        TrnMaterial other = (TrnMaterial) object;
        return Objects.equals(this.materialNo, other.materialNo);
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
                .append(", requiredNum=").append(this.requiredNum)
                .append(", stockDate=").append(this.stockDate)
                .append(", property=").append(this.property)
                .append(", branchNo=").append(this.branchNo)
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
