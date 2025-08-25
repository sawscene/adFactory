/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
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
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
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
import jp.adtekfuji.adFactory.entity.warehouse.Location;
import jp.adtekfuji.adfactoryserver.entity.MapAdapter;
import jp.adtekfuji.adfactoryserver.utility.JsonMapConverter;

/**
 * 部品マスタ
 * 
 * @author s-heya
 */
@Entity
@Table(name = "mst_product")
@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
@NamedQueries({
    @NamedQuery(name = "MstProduct.findByProductNo", query = "SELECT DISTINCT o FROM MstProduct o LEFT OUTER JOIN FETCH o.stockList LEFT OUTER JOIN FETCH o.partsList WHERE UPPER(o.productNo) = :productNo"),
    @NamedQuery(name = "MstProduct.findByFigureNo", query = "SELECT DISTINCT o FROM MstProduct o LEFT OUTER JOIN FETCH o.stockList LEFT OUTER JOIN FETCH o.partsList WHERE o.figureNo = :figureNo"),
    @NamedQuery(name = "MstProduct.findAllByProductNo", query = "SELECT DISTINCT o FROM MstProduct o LEFT OUTER JOIN FETCH o.stockList LEFT OUTER JOIN FETCH o.partsList WHERE o.productNo IN :productNo"),
    @NamedQuery(name = "MstProduct.findNnecessity", query = "SELECT DISTINCT o FROM MstProduct o LEFT OUTER JOIN FETCH o.stockList LEFT OUTER JOIN FETCH o.partsList WHERE o.updateDate < :updateDate"),
    @NamedQuery(name = "MstProduct.crearAll", query = "UPDATE MstProduct o SET o.locationList = NULL, o.importantRank = NULL"),
})
@Cacheable(false)
public class MstProduct implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "product_id")
    @JsonIgnore
    private Long productId;
    @Column(name = "figure_no")
    @JsonProperty("figNo")
    private String figureNo;
    @Basic(optional = false)
    //@NotNull
    @Column(name = "product_no")
    @JsonProperty("prodNo")
    private String productNo;
    @Size(max = 64)
    @Column(name = "product_name")
    @JsonProperty("prodName")
    private String productName;
    @Column(name = "important_rank")
    private Short importantRank;
    @Column(name = "location", length = 30000)
    @Convert(converter = JsonLocationConverter.class)
    private List<Location> locationList;
    @Column(name = "property", length = 30000)
    @Convert(converter = JsonMapConverter.class)
    @XmlJavaTypeAdapter(MapAdapter.class)
    //@XmlTransient
    @JsonIgnore
    private Map<String, String> property;
    //private String property;
    @Version
    @Column(name = "ver_info")
    @JsonIgnore
    private Integer verInfo;
    @Basic(optional = false)
    //@NotNull
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date createDate;
    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date updateDate;
    @Column(name = "classify")
    private String classify;
    @Column(name = "unit")
    @JsonProperty("unit")
    private String unit;
    
    // 親 MstProduct → 子 TrnMaterial (双方向)
    // メモリを圧迫するため、遅延ロードを行なっています
    @OneToMany(fetch = FetchType.LAZY, mappedBy="product")
    //@JoinColumn(name = "product_id")
    @XmlTransient
    @JsonIgnore
    private List<TrnMaterial> materialList;

    // 親 MstProduct → 子 MstStock (単方向)
    // N+1問題を解消するため、JPQLにてJOIN FETCHを行なっています
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    @XmlTransient
    @JsonIgnore
    private List<MstStock> stockList;
    
    // 親 MstProduct → 子 MstBom (単方向)
    // N+1問題を解消するため、JPQLにてJOIN FETCHを行なっています
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "parent_id", referencedColumnName= "product_id", insertable = false, updatable = false) 
    @XmlTransient
    private List<MstBom> partsList;

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
    @JsonProperty("area")
    private String jsonAreaName;
    @Transient
    @XmlTransient
    @JsonProperty("loc")
    private String jsonLocationNo;
    @Transient
    @XmlTransient
    @JsonProperty("rank")
    private String jsonRank;

    /**
     * コンストラクタ
     */
    public MstProduct() {
    }

    /**
     * コンストラクタ
     * 
     * @param productNo 品目
     * @param productName 品名
     * @param createDate 作成日時
     */
    public MstProduct(String productNo, String productName, Date createDate) {
        this.productNo = productNo;
        this.productName = productName;
        this.createDate = createDate;
    }

    /**
     * 図番を取得する。
     * 
     * @return 図番
     */
    public String getFigureNo() {
        return figureNo;
    }

    /**
     * 図番を設定する。
     * 
     * @param figureNo 図番 
     */
    public void setFigureNo(String figureNo) {
        this.figureNo = figureNo;
    }

    /**
     * 部品IDを取得する。
     * 
     * @return 部品ID
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * 部品IDを設定する。
     * 
     * @param productId 部品ID
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * 品目を取得する。
     * 
     * @return 品目
     */
    public String getProductNo() {
        return productNo;
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
     * 品名を取得する。
     * 
     * @return 品名
     */
    public String getProductName() {
        return productName;
    }

    /**
     * 品名を設定する。
     * 
     * @param productName 品名
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * 重要度ランクを取得する。
     * 
     * @return 重要度ランク
     */
    public Short getImportantRank() {
        return importantRank;
    }

    /**
     * 重要度ランクを設定する。
     * 
     * @param importantRank 重要度ランク
     */
    public void setImportantRank(Short importantRank) {
        this.importantRank = importantRank;
    }

    /**
     * 指定棚一覧を取得する。
     * 
     * @return 指定棚一覧
     */
    public List<Location> getLocationList() {
        if (Objects.isNull(locationList)) {
            locationList = new ArrayList<>();
        }
        return locationList;
    }

    /**
     * 指定棚一覧を設定する。
     * 
     * @param location 指定棚一覧
     */
    public void setLocationList(List<Location> location) {
        this.locationList = location;
    }

    /**
     * プロパティを取得する。
     * 
     * @return プロパティ
     */
    public Map<String, String> getProperty() {
        //return Objects.nonNull(this.property) ? JsonUtils.jsonToMap(this.property) : new HashMap<>();
        return property;
    }

    /**
     * プロパティを設定する。
     * 
     * @param property プロパティ
     */
    public void setProperty(Map<String, String> property) {
        //this.property = JsonUtils.mapToJson(property);
        this.property = property;
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
     * 管理区分を取得する。
     * 
     * @return 管理区分 
     */
    public String getClassify() {
        return classify;
    }

    /**
     * 管理区分を設定する。
     * 
     * @param classify 管理区分
     */
    public void setClassify(String classify) {
        this.classify = classify;
    }

    /**
     * 単位を取得する。
     * 
     * @return 単位
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 単位を設定する。
     * 
     * @param unit 単位
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 資材情報一覧を取得する。
     * 
     * @return 資材情報一覧
     */
    public List<TrnMaterial> getMaterialList() {
        return materialList;
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
     * 在庫一覧を取得する。
     * 
     * @return 在庫一覧
     */
    public List<MstStock> getStockList() {
        return stockList;
    }

    /**
     * 在庫一覧を設定する。
     * 
     * @param stockList 在庫一覧
     */
    public void setStockList(List<MstStock> stockList) {
        this.stockList = stockList;
    }

    /**
     * 部品構成(パーツリスト)を取得する。
     * 
     * @return 部品構成(パーツリスト)
     */
    public List<MstBom> getPartsList() {
        return partsList;
    }

    /**
     * 部品構成(パーツリスト)を設定する。
     * 
     * @param partsList 部品構成(パーツリスト)
     */
    public void setPartsList(List<MstBom> partsList) {
        this.partsList = partsList;
    }

    /**
     * メーカー名を取得する。(インポート用)
     * 
     * @return メーカー名
     */
    public String getJsonVendor() {
        return jsonVendor;
    }

    /**
     * 仕様・規格を取得する。(インポート用)
     * 
     * @return 仕様・規格
     */
    public String getJsonSpec() {
        return jsonSpec;
    }

    /**
     * 区画名を取得する。(インポート用)
     * 
     * @return 区画名
     */
    public String getJsonAreaName() {
        return jsonAreaName;
    }

    /**
     * 棚番号を取得する。(インポート用)
     * 
     * @return 棚番号
     */
    public String getJsonLocationNo() {
        return jsonLocationNo;
    }

    /**
     * 重要度ランクを取得する。(インポート用)
     * 
     * @return 重要度ランク
     */
    public String getJsonRank() {
        return jsonRank;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productId != null ? productId.hashCode() : 0);
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
        if (!(object instanceof MstProduct)) {
            return false;
        }
        MstProduct other = (MstProduct) object;
        return Objects.equals(this.productId, other.productId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("MstProduct{")
            .append("productId=").append(this.productId)
            .append(", figureNo=").append(this.figureNo)
            .append(", productNo=").append(this.productNo)
            .append(", productName=").append(this.productName)
            .append(", importantRank=").append(this.importantRank)
            .append(", location=").append(this.locationList)
            .append(", property=").append(this.property)
            .append(", unit=").append(this.unit)
            .append(", verInfo=").append(this.verInfo)
            .append("}")
            .toString();
    }
}
