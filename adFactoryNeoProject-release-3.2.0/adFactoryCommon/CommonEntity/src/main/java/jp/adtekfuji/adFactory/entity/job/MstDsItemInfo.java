/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.job;

import adtekfuji.utility.StringUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.dsKanban.DsParts;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 品番マスタ情報
 * 
 * @author s-heya
 */
@XmlRootElement(name = "dsItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class MstDsItemInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private Long productId;
    @XmlElement()
    private Integer category;
    @XmlElement()
    private String productNo;
    @XmlElement()
    private String productName;
    @XmlElement()
    private String spec;
    @XmlElement()
    private String unit;
    @XmlElement()
    private String location1;
    @XmlElement()
    private String location2;
    @XmlElement()
    private String bom;
    @XmlElement()
    private Long workflow1;
    @XmlElement()
    private Long workflow2;
    @XmlElement()
    private Long updatePersonId;
    @XmlElement()
    private Date updateDatetime;
    @XmlElement()
    private Integer verInfo;
    
    private List<DsParts> dsPartsList;

    /**
     * コンストラクタ
     */
    public MstDsItemInfo() {
    }

    /**
     * コンストラクタ
     * 
     * @param category 区分
     */
    public MstDsItemInfo(Integer category) {
        this.category = category;
    }

    /**
     * コンストラクタ
     * 
     * @param category
     * @param productNo 
     */
    public MstDsItemInfo(int category, String productNo) {
        this.category = category;
        this.productNo = productNo;
    }

    /**
     * 品番マスタIDを取得する。
     * 
     * @return 品番マスタID
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * 品番マスタIDを設定する。
     * 
     * @param productId 品番マスタID
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * 区分を取得する。
     * 
     * @return 区分 1: 補給生産、2:検査
     */
    public int getCategory() {
        return category;
    }

    /**
     * 区分を設定する。
     * 
     * @param category 区分 
     */
    public void setCategory(int category) {
        this.category = category;
    }

    /**
     * 品番を取得する。
     * 
     * @return 品番
     */
    public String getProductNo() {
        return productNo;
    }

    /**
     * 品番を設定する。
     * 
     * @param productNo 品番
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
     * 車種・タイプを取得する。
     * 
     * @return 車種・タイプ
     */
    public String getSpec() {
        return spec;
    }

    /**
     * 車種・タイプを設定する。
     * 
     * @param spec 車種・タイプ
     */
    public void setSpec(String spec) {
        this.spec = spec;
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
     * ロケーション1を取得する。
     * 
     * @return ロケーション1
     */
    public String getLocation1() {
        return location1;
    }

    /**
     * ロケーション1を設定する。
     * 
     * @param location1 ロケーション1
     */
    public void setLocation1(String location1) {
        this.location1 = location1;
    }

    /**
     * ロケーション2を取得する。
     * 
     * @return ロケーション2
     */
    public String getLocation2() {
        return location2;
    }

    /**
     * ロケーション2を設定する。
     * 
     * @param location2 ロケーション2を設定する。
     */
    public void setLocation2(String location2) {
        this.location2 = location2;
    }

    /**
     * 構成部品を取得する。
     * 
     * @return 構成部品
     */
    public String getBom() {
        return bom;
    }

    /**
     * 構成部品を設定する。
     * 
     * @param bom 構成部品
     */
    public void setBom(String bom) {
        this.bom = bom;
        this.dsPartsList = null;
    }

    /**
     * 構成部品を取得する。
     * 
     * @return 
     */
    public List<DsParts> getDsParts() {
        if (Objects.isNull(this.dsPartsList)) {
            if (!StringUtils.isEmpty(this.bom)) {
                this.dsPartsList = JsonUtils.jsonToObjects(this.bom, DsParts[].class);
            } else {
                this.dsPartsList = new ArrayList<>();
            }
        }
        return this.dsPartsList;
    }
    
    /**
     * 工程順1を取得する。
     * 
     * @return 工程順1
     */
    public Long getWorkflow1() {
        return workflow1;
    }

    /**
     * 工程順1を設定する。
     * 
     * @param workflow1 工程順1
     */
    public void setWorkflow1(Long workflow1) {
        this.workflow1 = workflow1;
    }

    /**
     * 工程順2を取得する。
     * 
     * @return 工程順2
     */
    public Long getWorkflow2() {
        return workflow2;
    }

    /**
     * 工程順2を設定する。
     * 
     * @param workflow2 工程順2
     */
    public void setWorkflow2(Long workflow2) {
        this.workflow2 = workflow2;
    }

    /**
     * 更新者(組織ID)を取得する。
     *
     * @return 更新者(組織ID)
     */
    public Long getUpdatePersonId() {
        return updatePersonId;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDatetime() {
        return updateDatetime;
    }

    /**
     * バージョン情報を取得する。
     * 
     * @return バージョン情報
     */
    public Integer getVerInfo() {
        return verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * オブジェクトを複製する。
     * 
     * @return 品番マスタ情報
     */
    public MstDsItemInfo clone() {
        MstDsItemInfo obj = new MstDsItemInfo();

        obj.setProductId(this.getProductId());
        obj.setCategory(this.getCategory());
        obj.setProductNo(this.getProductNo());
        obj.setProductName(this.getProductName());
        obj.setSpec(this.getSpec());
        obj.setLocation1(this.getLocation1());
        obj.setLocation2(this.getLocation2());
        obj.setBom(this.getBom());
        obj.setWorkflow1(this.getWorkflow1());
        obj.setWorkflow2(this.getWorkflow2());
        obj.setVerInfo(this.getVerInfo());

        return obj;
    }
    
    public boolean equalsData(MstDsItemInfo obj) {
        if (!Objects.equals(this.getCategory(), obj.getCategory())
                || !Objects.equals(this.getProductNo(), obj.getProductNo())
                || !Objects.equals(this.getProductName(), obj.getProductName())
                || !Objects.equals(this.getSpec(), obj.getSpec())
                || !Objects.equals(this.getLocation1(), obj.getLocation1())
                || !Objects.equals(this.getLocation2(), obj.getLocation2())
                || !this.equalsData(obj.getDsParts())
                || !Objects.equals(this.getWorkflow1(), obj.getWorkflow1())
                || !Objects.equals(this.getWorkflow2(), obj.getWorkflow2())
                ) {
            return false;
        }
        return true;
    }
    
    public boolean equalsData(List<DsParts> list) {

        List<DsParts> p1 = (Objects.nonNull(list) ? list : new ArrayList<DsParts>()).stream()
                .sorted(DsParts.keyComparator)
                .collect(Collectors.toList());

        List<DsParts> p2 = (Objects.nonNull(this.dsPartsList) ? this.dsPartsList : new ArrayList<DsParts>()).stream()
                .sorted(DsParts.keyComparator)
                .collect(Collectors.toList());
        
        return p1.equals(p2);
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
     * @return true: 等しい、false: 異なる
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MstDsItemInfo)) {
            return false;
        }
        MstDsItemInfo other = (MstDsItemInfo) object;
        return Objects.equals(this.productId, other.productId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("MstDsItemInfo{")
                .append("productId=").append(this.productId)
                .append(", category=").append(this.category)
                .append(", productNo=").append(this.productNo)
                .append(", productName=").append(this.productName)
                .append(", spec=").append(this.spec)
                .append(", unit=").append(this.unit)
                .append(", location1=").append(this.location1)
                .append(", location2=").append(this.location2)
                .append(", workflow1=").append(this.workflow1)
                .append(", workflow2=").append(this.workflow2)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
    
}
