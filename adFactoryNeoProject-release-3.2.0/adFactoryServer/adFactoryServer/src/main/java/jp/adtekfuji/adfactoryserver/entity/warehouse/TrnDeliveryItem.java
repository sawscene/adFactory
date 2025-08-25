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
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
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
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adfactoryserver.entity.MapAdapter;
import jp.adtekfuji.adfactoryserver.utility.JsonMapConverter;

/**
 * 出庫指示アイテム情報
 *
 * @author s-heya
 */
@Entity
@Table(name = "trn_delivery_item")
@XmlRootElement(name = "deliveryItem")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
@NamedQueries({
    @NamedQuery(name = "TrnDeliveryItem.find", query = "SELECT o FROM TrnDeliveryItem o WHERE o.pk.deliveryNo = :deliveryNo AND o.pk.itemNo = :itemNo"),
    @NamedQuery(name = "TrnDeliveryItem.findAllByDeliveryNo", query = "SELECT o FROM TrnDeliveryItem o WHERE o.pk.deliveryNo = :deliveryNo ORDER BY o.pk.itemNo"),
    @NamedQuery(name = "TrnDeliveryItem.findDeliveryOrder", query = "SELECT DISTINCT o FROM TrnDeliveryItem o WHERE o.pk.deliveryNo = :deliveryNo AND ((o.requiredNum - o.deliveryNum) > 0) ORDER BY o.pk.itemNo"),
    @NamedQuery(name = "TrnDeliveryItem.findOutOfStock", query = "SELECT o FROM TrnDeliveryItem o WHERE o.product.productId = :productId AND (o.arrange = 0 OR o.arrange = 1) AND o.reserve IN (0, 1, 2) ORDER BY o.pk.deliveryNo, o.pk.itemNo"),
    @NamedQuery(name = "TrnDeliveryItem.updateReserve", query = "UPDATE TrnDeliveryItem o SET o.reserve = 0 WHERE o.pk.deliveryNo IN :deliveryNo"),
})
@Cacheable(false)
public class TrnDeliveryItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    @JsonUnwrapped
    protected TrnDeliveryItemPK pk;

    @Size(max = 32)
    @Column(name = "material_no")
    private String materialNo;

    @Size(max = 32)
    @Column(name = "order_no")
    @JsonProperty("orderNo")
    private String orderNo;

    //@Basic(optional = false)
    //@NotNull
    //@Column(name = "product_id")
    //private long productId;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "required_num")
    @JsonProperty("reqNum")
    private Integer requiredNum;

    @Column(name = "delivery_num")
    private Integer deliveryNum;

    @Column(name = "property", length = 30000)
    @Convert(converter = JsonMapConverter.class)
    @XmlJavaTypeAdapter(MapAdapter.class)
    @JsonIgnore
    private Map<String, String> property;

    @Version
    @Column(name = "ver_info")
    private Integer verInfo;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "product_id")
    private MstProduct product;

    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("due")
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date dueDate;

    @Column(name = "serial_no")
    @JsonProperty("serialNo")
    private String serialNo;

    @Column(name = "unit_no")
    private String unitNo;

    @Column(name = "location_no")
    @JsonProperty("locationNo")
    private String locationNo;

    @Column(name = "usage_num")
    @JsonProperty("usageNum")
    private Integer usageNum;

    @Column(name = "arrange")
    private Integer arrange;

    @Column(name = "arrange_no")
    private String arrangeNo;

    @Column(name = "reserve")
    private Integer reserve;

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
    @JsonProperty("vendor")
    private String jsonVendor;

    @Transient
    @XmlTransient
    @JsonProperty("spec")
    private String jsonSpec;

    @Transient
    @XmlTransient
    @JsonProperty("unit")
    private String jsonUnit;

    @Transient
    @XmlTransient
    @JsonProperty("mod")
    private Short jsonModifyFlag;

    @Transient
    @XmlTransient
    @JsonProperty("del")
    private Short jsonDeleteFlag;

    @Transient
    @XmlTransient
    @JsonProperty("modelName")
    private String jsonModelName;
    
    @Transient
    @XmlTransient
    @JsonProperty("deliveryRule")
    private Integer jsonDeliveryRule;
    
    @Transient
    @XmlTransient
    private List<TrnMaterial> materialList;

    @Transient
    private Integer inStockNum;

    @Transient
    private Integer guideOrder;

    // 親 TrnDeliveryItem → 子 TrnReserveMaterial (双方向)
    // 親 TrnDeliveryItem を削除すると、子 TrnReserveMaterial も削除されます
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumns ({
        @JoinColumn(name = "delivery_no", referencedColumnName = "delivery_no"),
        @JoinColumn(name = "item_no", referencedColumnName = "item_no")
    })
    private List<TrnReserveMaterial> reserveMaterials;
    
    @Column(name = "withdraw_num")
    private Integer withdrawNum; // 在庫払出数

    /**
     * コンストラクタ
     */
    public TrnDeliveryItem() {
    }

    /**
     * コンストラクタ
     *
     * @param deliveryNo 出庫番号
     * @param itemNo 明細番号
     * @param createDate 作成日時
     */
    public TrnDeliveryItem(String deliveryNo, int itemNo, Date createDate) {
        this.pk = new TrnDeliveryItemPK(deliveryNo, itemNo);
        this.requiredNum = 0;
        this.deliveryNum = 0;
        this.createDate = createDate;
    }

    /**
     * 複合プライマリキーを取得する。
     *
     * @return 複合プライマリキー
     */
    public TrnDeliveryItemPK getPK() {
        return pk;
    }

    /**
     * 複合プライマリキーを設定する。
     *
     * @param trnDeliveryItemPK 複合プライマリキー
     */
    public void setPK(TrnDeliveryItemPK trnDeliveryItemPK) {
        this.pk = trnDeliveryItemPK;
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

    //public long getProductId() {
    //    return productId;
    //}
    //
    //public void setProductId(long productId) {
    //    this.productId = productId;
    //}

    /**
     * 要求数を取得する。
     *
     * @return 要求数
     */
    public Integer getRequiredNum() {
        return requiredNum;
    }

    /**
     * 要求数を設定する。
     *
     * @param requiredNum 要求数
     */
    public void setRequiredNum(Integer requiredNum) {
        this.requiredNum = requiredNum;
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
     * 出庫数を設定する。
     *
     * @param deliveryNum 出庫数
     */
    public void setDeliveryNum(Integer deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    /**
     * プロパティーを取得する。
     *
     * @return
     */
    public Map<String, String> getProperty() {
        return property;
    }

    /**
     * プロパティーを設定する。
     *
     * @param property
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
        return verInfo;
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
        return createDate;
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
        return updateDate;
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
        return product;
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
     * 品目を取得する。
     *
     * @return 品目
     */
    public String getJsonProductNo() {
        return jsonProductNo;
    }

    /**
     * 品名を取得する。
     *
     * @return 品名
     */
    public String getJsonProductName() {
        return jsonProductName;
    }

    /**
     * メーカーを取得する。
     * 
     * @return 
     */
    public String getJsonVendor() {
        return jsonVendor;
    }

    /**
     * 仕様・規格を取得する。
     * 
     * @return 
     */
    public String getJsonSpec() {
        return jsonSpec;
    }

    /**
     * 単位を取得する。
     * 
     * @return 
     */
    public String getJsonUnit() {
        return jsonUnit;
    }

    /**
     * 納期を取得する。
     *
     * @return 納期
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * 納期を設定する。
     *
     * @param dueDate 納期
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * シリアル番号を設定する。
     *
     * @param serialNo シリアル番号
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * 棚番号を取得する。
     * 
     * @return 棚番号
     */
    public String getLocationNo() {
        return locationNo;
    }

    /**
     * 棚番号を設定する。
     * 
     * @param locationNo 
     */
    public void setLocationNo(String locationNo) {
        this.locationNo = locationNo;
    }

    /**
     * 使用数を取得する。
     * 
     * @return 使用数
     */
    public Integer getUsageNum() {
        return usageNum;
    }

    /**
     * 使用数を設定する。
     * 
     * @param usageNum 使用数
     */
    public void setUsageNum(Integer usageNum) {
        this.usageNum = usageNum;
    }

    /**
     * 手配区分を取得する。
     * 
     * @return 手配区分
     */
    public Integer getArrange() {
        if (Objects.isNull(this.arrange)) {
            return 0;
        }
        return this.arrange;
    }

    /**
     * 手配区分を設定する。
     * 
     * @param arrange 手配区分
     */
    public void setArrange(Integer arrange) {
        this.arrange = arrange;
    }

    /**
     * 先行手配番号を取得する。
     * 
     * @return 先行手配番号
     */
    public String getArrangeNo() {
        return arrangeNo;
    }

    /**
     * 先行手配番号を設定する。
     * 
     * @param arrangeNo 先行手配番号
     */
    public void setArrangeNo(String arrangeNo) {
        this.arrangeNo = arrangeNo;
    }

    /**
     * 在庫引当状況を取得する。
     * 
     * @return 在庫引当状況
     */
    public Integer getReserve() {
        if (Objects.isNull(this.reserve)) {
            return 0;
        }
        return reserve;
    }

    /**
     * 在庫引当状況を設定する。
     * 
     * @param reserve 在庫引当状況
     */
    public void setReserve(Integer reserve) {
        this.reserve = reserve;
    }

    /**
     * 修正フラグを取得する。
     *
     * @return 修正フラグ
     */
    public Short getJsonModifyFlag() {
        if (Objects.isNull(jsonModifyFlag)) {
            return 0;
        }
        return jsonModifyFlag;
    }

    /**
     * 削除フラグを取得する。
     *
     * @return 削除フラグ
     */
    public Short getJsonDeleteFlag() {
        if (Objects.isNull(jsonDeleteFlag)) {
            return 0;
        }
        return jsonDeleteFlag;
    }

     /**
     * 機種名を取得する。
     * 
     * @return 機種名
     */
    public String getJsonModelName() {
        return jsonModelName;
    }

     /**
     * 出庫ルールを取得する。
     * 
     * @return 出庫ルール
     */
    public Integer getJsonDeliveryRule() {
        if (Objects.isNull(jsonDeliveryRule)) {
            return 1;
        }
        return jsonDeliveryRule;
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
     * 資材情報一覧を取得する。
     *
     * @return 資材情報一覧
     */
    public List<TrnMaterial> getMaterialList() {
        if (Objects.isNull(materialList)) {
            materialList = new LinkedList<>();
        }
        return materialList;
    }

    /**
     * 在庫数を取得する。
     *
     * @return 在庫数
     */
    public Integer getInStockNum() {
        return inStockNum;
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
     * 資材情報一覧を設定する。
     *
     * @param materialList 資材情報一覧
     */
    public void setMaterialList(List<TrnMaterial> materialList) {
        this.materialList = materialList;
    }

    /**
     * 案内表示用の棚番号を取得する。
     *
     * @return 案内表示用の棚番号
     */
    public String getGuideLocationNo() {
        if (!this.reserveMaterials.isEmpty()) {
            return this.reserveMaterials.get(0).getMaterial().getLocation().getLocationNo();
        }
        if (Objects.nonNull(this.materialList) && !this.materialList.isEmpty()) {
            return this.materialList.get(0).getLocation().getLocationNo();
        }
        return this.locationNo;
    }

    /**
     * 案内順を取得する。
     *
     * @return
     */
    public Integer getGuideOrder() {
        if (!this.reserveMaterials.isEmpty()) {
            return this.reserveMaterials.get(0).getMaterial().getLocation().getGuideOrder();
        }
        return guideOrder;
    }

    /**
     * 案内順を設定する。
     *
     * @param guideOrder
     */
    public void setGuideOrder(Integer guideOrder) {
        this.guideOrder = guideOrder;
    }

    /**
     * 在庫引当情報を取得する。
     * 
     * @return 在庫引当情報
     */
    public List<TrnReserveMaterial> getReserveMaterials() {
        return reserveMaterials;
    }

    /**
     * 在庫引当情報を設定する。
     * 
     * @param reserveMaterials 在庫引当情報
     */
    public void setReserveMaterials(List<TrnReserveMaterial> reserveMaterials) {
        this.reserveMaterials = reserveMaterials;
    }

    /**
     * 在庫払出数を取得する。
     * 
     * @return 在庫払出数
     */
    public Integer getWithdrawNum() {
        return withdrawNum;
    }

    /**
     * 在庫払出数を設定する。
     * 
     * @param withdrawNum 在庫払出数
     */
    public void setWithdrawNum(Integer withdrawNum) {
        this.withdrawNum = withdrawNum;
    }

    /**
     * 在庫引当数を取得する。
     * 
     * @return 在庫引当数
     */
    public int getReservedNum() {
        if (Objects.nonNull(this.reserveMaterials) && !this.reserveMaterials.isEmpty()) {
            int reservedNum = this.reserveMaterials.stream()
                .mapToInt(o -> o.getReservedNum())
                .sum();
            return reservedNum;
        }
        return 0;
    }

    /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pk != null ? pk.hashCode() : 0);
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
        if (!(object instanceof TrnDeliveryItem)) {
            return false;
        }
        TrnDeliveryItem other = (TrnDeliveryItem) object;
        return Objects.equals(this.pk, other.pk);
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("TrnDeliveryItem{")
                .append("trnDeliveryItemPK=").append(this.pk)
                .append(", unitNo=").append(this.unitNo)
                .append(", guideOrder=").append(this.guideOrder)
                .append(", materialNo=").append(this.materialNo)
                .append(", orderNo=").append(this.orderNo)
                .append(", product=").append(this.product)
                .append(", requiredNum=").append(this.requiredNum)
                .append(", dueDate=").append(this.dueDate)
                .append(", deliveryNum=").append(this.deliveryNum)
                .append(", property=").append(this.property)
                .append(", arrange=").append(this.arrange)
                .append(", reserve=").append(this.reserve)
                .append(", withdrawNum=").append(this.withdrawNum)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
