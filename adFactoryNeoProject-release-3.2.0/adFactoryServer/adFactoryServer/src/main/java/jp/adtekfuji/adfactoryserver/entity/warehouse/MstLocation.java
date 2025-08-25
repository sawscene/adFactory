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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import org.apache.commons.lang3.StringUtils;

/**
 * 棚マスタ
 *
 * @author s-heya
 */
@Entity
@Table(name = "mst_location")
@XmlRootElement(name = "location")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
@NamedQueries({
    @NamedQuery(name = "MstLocation.find", query = "SELECT o FROM MstLocation o WHERE o.areaName = :areaName AND o.locationNo = :locationNo"),
    @NamedQuery(name = "MstLocation.getAreaNames", query = "SELECT o.areaName FROM MstLocation o GROUP BY o.areaName ORDER BY o.areaName"),
    @NamedQuery(name = "MstLocation.findNnecessity", query = "SELECT o FROM MstLocation o WHERE o.updateDate < :updateDate"),
    // 区画名を指定して、棚卸中の棚番号を取得する。(存在する場合、その区画は棚卸中)
    @NamedQuery(name = "MstLocation.getInventoryLocationIds", query = "SELECT o.locationId FROM MstLocation o WHERE o.areaName = :areaName AND o.inventoryFlag = TRUE"),
    // すべての棚の棚卸実施フラグを更新する。
    @NamedQuery(name = "MstLocation.updateAllInventoryFlag", query = "UPDATE MstLocation o SET o.inventoryFlag = :inventoryFlag"),
    // 区画名を指定して、棚卸実施フラグを更新する。
    @NamedQuery(name = "MstLocation.updateAreaInventoryFlag", query = "UPDATE MstLocation o SET o.inventoryFlag = :inventoryFlag WHERE o.areaName = :areaName"),
})
@Cacheable(false)
public class MstLocation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 棚ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "location_id")
    @JsonIgnore
    private Long locationId;

    /**
     * 区画名
     */
    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 32)
    @Column(name = "area_name")
    @JsonProperty("area")
    private String areaName;

    /**
     * 棚番号
     */
    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 32)
    @Column(name = "location_no")
    @JsonProperty("loc")
    private String locationNo;

    /**
     * 案内順
     */
    @Column(name = "guide_order")
    @JsonProperty("order")
    private Integer guideOrder;

    /**
     * 指定部品
     */
    @Size(max = 256)
    @Column(name = "location_spec")
    @JsonProperty("locSpec")
    private String locationSpec;

    /**
     * 排他用バージョン
     */
    @Version
    @Column(name = "ver_info")
    @JsonIgnore
    private Integer verInfo;

    /**
     * 作成日時
     */
    @Basic(optional = false)
    //@NotNull
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date createDate;

    /**
     * 更新日時
     */
    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date updateDate;

    /**
     * 棚卸実施
     */
    @Column(name = "inventory_flag")
    @JsonIgnore
    private Boolean inventoryFlag;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    @XmlTransient
    @JsonIgnore
    private List<TrnMaterial> materialList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location", cascade = CascadeType.DETACH)
    @XmlTransient
    @JsonIgnore
    private List<MstStock> stockList;

    @Column(name = "location_type")
    @JsonProperty("type")
    private Integer locationType;

    @Transient
    @XmlTransient
    @JsonProperty("newArea")
    private String jsonNewAreaName;

    @Transient
    @XmlTransient
    @JsonProperty("newLoc")
    private String jsonNewlocationNo;

    /**
     * コンストラクタ
     */
    public MstLocation() {
    }

    /**
     * コンストラクタ
     *
     * @param areaName 区画名
     * @param locationNo 棚番号
     */
    public MstLocation(String areaName, String locationNo) {
        this.areaName = areaName;
        this.locationNo = locationNo;
    }

    /**
     * 棚IDを取得する。
     *
     * @return 棚ID
     */
    public Long getLocationId() {
        return this.locationId;
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
     * @param locationNo
     */
    public void setLocationNo(String locationNo) {
        this.locationNo = locationNo;
    }

    /**
     * 案内順を取得する。
     *
     * @return 案内順
     */
    public Integer getGuideOrder() {
        return this.guideOrder;
    }

    /**
     * 案内順を設定する。
     *
     * @param guideOrder 案内順
     */
    public void setGuideOrder(Integer guideOrder) {
        this.guideOrder = guideOrder;
    }

    /**
     * 指定部品を取得する。
     *
     * @return
     */
    public String[] getLocationSpecArray() {
        if (StringUtils.isEmpty(this.locationSpec)) {
            return new String[0];
        }
        return this.locationSpec.split(",");
    }

    /**
     * 指定部品を取得する。
     *
     * @return
     */
    public String getLocationSpec() {
        return this.locationSpec;
    }

    /**
     * 指定部品を設定する。
     *
     * @param locationSpec
     */
    public void setLocationSpec(String locationSpec) {
        this.locationSpec = locationSpec;
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
     * 資材情報一覧を取得する。
     *
     * @return 資材情報一覧
     */
    public List<TrnMaterial> getMaterialList() {
        return this.materialList;
    }

    /**
     * 在庫マスタ一覧を取得する。
     *
     * @return 在庫マスタ一覧
     */
    public List<MstStock> getStockList() {
        return this.stockList;
    }

    /**
     * 新区画名を取得する。
     *
     * @return 新区画名
     */
    public String getJsonNewAreaName() {
        return this.jsonNewAreaName;
    }

    /**
     * 新棚番号を取得する。
     *
     * @return 新棚番号
     */
    public String getJsonNewlocationNo() {
        return this.jsonNewlocationNo;
    }

    /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.locationId != null ? this.locationId.hashCode() : 0);
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
        if (!(object instanceof MstLocation)) {
            return false;
        }
        MstLocation other = (MstLocation) object;
        return Objects.equals(this.locationId, other.locationId);
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("MstLocation{")
                .append("locationId=").append(this.locationId)
                .append(", areaName=").append(this.areaName)
                .append(", locationNo=").append(this.locationNo)
                .append(", guideOrder=").append(this.guideOrder)
                .append(", locationSpec=").append(this.locationSpec)
                .append(", locationType=").append(this.locationType)
                .append(", verInfo=").append(this.verInfo)
                .append(", inventoryFlag=").append(this.inventoryFlag)
                .append("}")
                .toString();
    }
}
