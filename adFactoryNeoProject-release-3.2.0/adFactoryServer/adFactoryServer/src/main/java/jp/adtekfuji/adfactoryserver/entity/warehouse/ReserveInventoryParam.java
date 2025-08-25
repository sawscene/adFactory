/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.warehouse;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 在庫引当情報
 * 
 * @author s-heya
 */
@XmlRootElement(name = "reserveInventoryParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReserveInventoryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private String deliveryNo;
    @XmlElement
    private Integer itemNo;
    @XmlElement
    private String areaName;
    @XmlElementWrapper(name = "reserveMaterials")
    @XmlElement(name = "reserveMaterial")
    List<TrnReserveMaterial> reserveMaterials;

    /**
     * 出庫番号を取得する。
     * 
     * @return 出庫番号
     */
    public String getDeliveryNo() {
        return deliveryNo;
    }

    /**
     * 明細番号を取得する。
     * 
     * @return 明細番号
     */
    public int getItemNo() {
        return itemNo;
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
     * 在庫引当を取得する。
     * 
     * @return 
     */
    public List<TrnReserveMaterial> getReserveMaterials() {
        return reserveMaterials;
    }
    
}
