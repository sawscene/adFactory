/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.warehouse;

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
public class ReserveInventoryParamInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private String deliveryNo;
    @XmlElement
    private Integer itemNo;
    @XmlElement
    private String areaName;
    @XmlElementWrapper(name = "reserveMaterials")
    @XmlElement(name = "reserveMaterial")
    List<TrnReserveMaterialInfo> reserveMaterials;

    /**
     * コンストラクタ
     */
    public ReserveInventoryParamInfo() {
    }
            
    /**
     * コンストラクタ
     * 
     * @param deliveryNo
     * @param itemNo
     * @param areaName
     * @param reserveMaterials 
     */
    public ReserveInventoryParamInfo(String deliveryNo, Integer itemNo, String areaName, List<TrnReserveMaterialInfo> reserveMaterials) {
        this.deliveryNo = deliveryNo;
        this.itemNo = itemNo;
        this.areaName = areaName;
        this.reserveMaterials = reserveMaterials;
    }

    
}
