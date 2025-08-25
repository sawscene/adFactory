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
 * 払出指示情報
 * 
 * @author s-heya
 */
@XmlRootElement(name = "deliveryParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeliveryParam implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @XmlElementWrapper(name = "deliveries")
    @XmlElement(name = "delivery")
    private List<TrnDeliveryInfo> deliveries;

    /**
     * コンストラクタ
     * 
     * @param deliveries 払出指示一覧
     */
    public DeliveryParam(List<TrnDeliveryInfo> deliveries) {
        this.deliveries = deliveries;
    }

    /**
     * 払出指示一覧を取得する。
     * 
     * @return 払出指示一覧
     */
    public List<TrnDeliveryInfo> getDeliveries() {
        return deliveries;
    }
    
}
