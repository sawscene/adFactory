/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 作業ログの検索条件
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "operationLogCondition")
public class OperationLogCondition implements Serializable {

    @XmlElement()
    private Date fromDate = null;
    @XmlElement()
    private Date toDate = null;
    @XmlElement()
    private String areaName;
    @XmlElementWrapper(name = "organizationIds")
    @XmlElement(name = "organizationId")
    private List<Long> organizationIds = null;
    @XmlElement()
    private String productNo = null;
    @XmlElement()
    private String orderNo = null;
    @XmlElement()
    private String partsNo = null;
    @XmlElement()
    private String deliveryNo = null;
    @XmlElementWrapper(name = "categories")
    @XmlElement(name = "category")
    private List<String> categories = null;

    /**
     * コンストラクタ
     */
    public OperationLogCondition() {
    }

    /**
     * 日時範囲(From)を取得する。
     *
     * @return 日時範囲(From)
     */
    public Date getFromDate() {
        return this.fromDate;
    }

    /**
     * 日時範囲(From)を設定する。
     *
     * @param fromDate 日時範囲(From)
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * 日時範囲(To)を取得する。
     *
     * @return 日時範囲(To)
     */
    public Date getToDate() {
        return this.toDate;
    }

    /**
     * 日時範囲(To)を設定する。
     *
     * @param toDate 日時範囲(To)
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
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
     * 社員番号(組織ID)を取得する。
     * 
     * @return 組織ID一覧
     */
    public List<Long> getOrganizationIds() {
        return organizationIds;
    }

    /**
     * 社員番号(組織ID)を設定する。
     * 
     * @param organizationIds 組織ID一覧
     */
    public void setOrganizationIds(List<Long> organizationIds) {
        this.organizationIds = organizationIds;
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
     * 品目を設定する。
     *
     * @param productNo 品目
     */
    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    /**
     * 払出指示番号を取得する。
     *
     * @return 払出指示番号
     */
    public String getDeliveryNo() {
        return this.deliveryNo;
    }

    /**
     * 払出指示番号を設定する。
     *
     * @param deliveryNo 払出指示番号
     */
    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
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
     * 種別を取得する。
     * 
     * @return 種別
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * 種別を設定する。
     * 
     * @param categories 種別 
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("OperationLogCondition{")
                .append("fromDate=").append(this.fromDate)
                .append(", toDate=").append(this.toDate)
                .append(", areaName=").append(this.areaName)
                .append(", deliveryNo=").append(this.deliveryNo)
                .append(", orderNo=").append(this.orderNo)
                .append(", productNo=").append(this.productNo)
                .append(", partsNo=").append(this.partsNo)
                .append("}")
                .toString();
    }
}
