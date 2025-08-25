/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

import adtekfuji.utility.StringUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 部品マスタ
 * 
 * @author s-heya
 */
@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.FIELD)
public class MstProductInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String MATERIAL = "Material";
    public static final String VENDOR = "Vendor";
    public static final String SPEC = "Spec";

    @XmlElement
    private String figureNo;
    @XmlElement
    private Long productId;
    @XmlElement
    private String productNo;
    @XmlElement
    private String productName;
    @XmlElement
    private Short importantRank;
    @XmlElement
    private List<Location> locationList;
    @XmlElement
    private String property;
    @XmlElement
    private Integer verInfo;
    @XmlElement
    private Date createDate;
    @XmlElement
    private Date updateDate;
    @XmlElement
    private String classify;
    @XmlElement
    private String unit;

    /**
     * コンストラクタ
     */
    public MstProductInfo() {
    }

    /**
     * コンストラクタ
     * 
     * @param productNo 品目
     * @param productName 品名
     * @param createDate 作成日時
     */
    public MstProductInfo(String productNo, String productName, Date createDate) {
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
     * 棚番号を取得する。
     * 
     * @param areaName 区画名
     * @return 棚番号
     */
    public String getLocationNo(String areaName) {
        if (Objects.nonNull(locationList)) {
            Optional<Location> location = this.locationList.stream().filter(o -> StringUtils.equals(areaName, o.getAreaName())).findFirst();
            return location.isPresent() ? location.get().getLocationNo() : "";
        }
        return "";
    }

    /**
     * プロパティーを取得する。
     *
     * @return
     */
    public Map<String, String> getProperty() {
        if (!StringUtils.isEmpty(property)) {
            return JsonUtils.jsonToMap(property);
        }
        return new HashMap<>();
    }

    /**
     * プロパティを設定する。
     * 
     * @param map プロパティ
     */
    public void setProperty(Map<String, String> map) {
        this.property = JsonUtils.mapToJson(map);
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
     * 資材情報一覧を取得する。
     * 
     * @return 資材情報一覧
     */
    //public List<TrnMaterialInfo> getMaterialList() {
    //    return materialList;
    //}

    /**
     * 資材情報一覧を設定する。
     * 
     * @param materialList 資材情報一覧
     */
    //public void setMaterialList(List<TrnMaterialInfo> materialList) {
    //    this.materialList = materialList;
    //}

    /**
     * 在庫一覧を取得する。
     * 
     * @return 在庫一覧
     */
    //public List<MstStock> getStockList() {
    //    return stockList;
    //}

    /**
     * 在庫一覧を設定する。
     * 
     * @param stockList 在庫一覧
     */
    //public void setStockList(List<MstStock> stockList) {
    //    this.stockList = stockList;
    //}

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
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
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
        if (!(object instanceof MstProductInfo)) {
            return false;
        }
        MstProductInfo other = (MstProductInfo) object;
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
            .append(", productNo=").append(this.productNo)
            .append(", productName=").append(this.productName)
            .append(", importantRank=").append(this.importantRank)
            .append(", location=").append(this.locationList)
            .append(", property=").append(this.property)
            .append(", verInfo=").append(this.verInfo)
            .append(", classify=").append(this.classify)
            .append("}")
            .toString();
    }
}
