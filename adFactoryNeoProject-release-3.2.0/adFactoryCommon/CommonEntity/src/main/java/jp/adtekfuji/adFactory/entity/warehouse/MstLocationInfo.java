/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

import adtekfuji.utility.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * 棚マスタ
 * 
 * @author s-heya
 */
@XmlRootElement(name = "location")
@XmlAccessorType(XmlAccessType.FIELD)
public class MstLocationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Long locationId;
    @XmlElement
    @JsonProperty("area")
    private String areaName;
    @XmlElement
    @JsonProperty("loc")
    private String locationNo;
    @XmlTransient
    @JsonProperty("newArea")
    private String jsonNewAreaName;
    @XmlTransient
    @JsonProperty("newLoc")
    private String jsonNewlocationNo;
    @XmlElement
    @JsonProperty("order")
    private Integer guideOrder;
    @XmlElement
    @JsonProperty("locSpec")
    private String locationSpec;
    @XmlElement
    @JsonProperty("type")
    private Integer locationType;
    @XmlElement
    @JsonIgnore
    private Integer verInfo;
    @XmlElement
    @JsonIgnore
    private Date createDate;
    @XmlElement
    @JsonIgnore
    private Date updateDate;
    //@JsonIgnore
    //private List<TrnMaterial> materialList;
    //@JsonIgnore
    //private List<MstStock> stockList;

    /**
     * コンストラクタ
     */
    public MstLocationInfo() {
    }

    /**
     * コンストラクタ
     * 
     * @param areaName 区画名
     * @param locationNo 棚番号
     */
    public MstLocationInfo(String areaName, String locationNo) {
        this.areaName = areaName;
        this.locationNo = locationNo;
    }

    /**
     * 棚IDを取得する。
     * 
     * @return 棚ID
     */
    public Long getLocationId() {
        return locationId;
    }

    /**
     * 区画名を取得する。
     * 
     * @return 区画名
     */
    public String getAreaName() {
        return areaName;
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
     * 新区画名を取得する。
     * 
     * @return 新区画名
     */
    public String getJsonNewAreaName() {
        return jsonNewAreaName;
    }

    /**
     * 新区画名を設定する。
     * 
     * @param jsonNewAreaName 新区画名
     */
    public void setJsonNewAreaName(String jsonNewAreaName) {
        this.jsonNewAreaName = jsonNewAreaName;
    }

    /**
     * 新棚番号を取得する。
     * 
     * @return 新棚番号
     */
    public String getJsonNewlocationNo() {
        return jsonNewlocationNo;
    }

    /**
     * 新棚番号を設定する。
     * 
     * @param jsonNewlocationNo 新棚番号
     */
    public void setJsonNewlocationNo(String jsonNewlocationNo) {
        this.jsonNewlocationNo = jsonNewlocationNo;
    }

    /**
     * 案内順を取得する。
     * 
     * @return 案内順
     */
    public Integer getGuideOrder() {
        return guideOrder;
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
        if (StringUtils.isEmpty(locationSpec)) {
            return new String[0];
        }
        return locationSpec.split(",");
    }

    /**
     * 指定部品を取得する。
     * 
     * @return 
     */
    public String getLocationSpec() {
        return locationSpec;
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
     * 棚種類を取得する。
     * 
     * @return 棚種類
     */
    public Integer getLocationType() {
        return locationType;
    }

    /**
     * 棚種類を設定する。
     * 
     * @param locationType 棚種類
     */
    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
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
     * 更新日時を取得する。
     * 
     * @return 更新日時
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * 資材情報一覧を取得する。
     * 
     * @return 資材情報一覧
     */
    //public List<TrnMaterial> getMaterialList() {
    //    return materialList;
    //}

    /**
     * 在庫マスタ一覧を取得する。
     * 
     * @return 在庫マスタ一覧
     */
    //public List<MstStock> getStockList() {
    //    return stockList;
    //}

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (locationId != null ? locationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MstLocationInfo)) {
            return false;
        }
        MstLocationInfo other = (MstLocationInfo) object;
        return Objects.equals(this.locationId, other.locationId);
    }

    @Override
    public String toString() {
        return new StringBuilder("MstLocationInfo{")
            .append("locationId=").append(this.locationId)
            .append(", areaName=").append(this.areaName)
            .append(", locationNo=").append(this.locationNo)
            .append(", jsonNewAreaName=").append(this.jsonNewAreaName)
            .append(", jsonNewlocationNo=").append(this.jsonNewlocationNo)
            .append(", guideOrder=").append(this.guideOrder)
            .append(", locationSpec=").append(this.locationSpec)
            .append(", locationType=").append(this.locationType)
            .append(", verInfo=").append(this.verInfo)
            .append("}")
            .toString();
    }
}
