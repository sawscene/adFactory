/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.property.fuji;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FUJI インポートフォーマット設定
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "importFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportFormatInfo {

    private OrderFormatInfo orderFormatInfo;// 計画情報のフォーマット情報
    private BomFormatInfo bomFormatInfo;// BOM情報のフォーマット情報

    /**
     * コンストラクタ
     */
    public ImportFormatInfo() {
    }

    /**
     * 計画情報のフォーマット情報を取得する。
     *
     * @return 計画情報のフォーマット情報
     */
    public OrderFormatInfo getOrderFormatInfo() {
        return this.orderFormatInfo;
    }

    /**
     * 計画情報のフォーマット情報を設定する。
     *
     * @param orderFormatInfo 計画情報のフォーマット情報
     */
    public void setOrderFormatInfo(OrderFormatInfo orderFormatInfo) {
        this.orderFormatInfo = orderFormatInfo;
    }

    /**
     * BOM情報のフォーマット情報を取得する。
     *
     * @return BOM情報のフォーマット情報
     */
    public BomFormatInfo getBomFormatInfo() {
        return this.bomFormatInfo;
    }

    /**
     * BOM情報のフォーマット情報を設定する。
     *
     * @param bomFormatInfo BOM情報のフォーマット情報
     */
    public void setBomFormatInfo(BomFormatInfo bomFormatInfo) {
        this.bomFormatInfo = bomFormatInfo;
    }

    @Override
    public String toString() {
        return new StringBuilder("ImportFormatInfo{")
                .append("orderFormatInfo=").append(this.orderFormatInfo)
                .append(", bomFormatInfo=").append(this.bomFormatInfo)
                .append("}")
                .toString();
    }
}
