/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.exportserviceplugin;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * オーダー完了報告通知情報
 *
 * @author nouzawa
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OMCompletionOrder")
public class OrderActualOutputEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @XmlElement(name = "OrderNO")
    private String orderNo;            // 製造オーダー番号
    @XmlElement(name = "OrderSN")
    private String orderSN;            // シリアル
    @XmlElement(name = "Qty") 
    private String qty;                 // 完了数
    @XmlElement(name = "CompletionDate")
    private String completionDate;     // 完了日

    /**
     * デフォルト・コンストラクタ
     */
    public OrderActualOutputEntity() {
    }

    /**
     * コンストラクタ
     * 
     * @param orderNo
     * @param orderSN
     * @param qty
     * @param completionDate 
     */
    public OrderActualOutputEntity(String orderNo, String orderSN, String qty, String completionDate) {
        this.orderNo = orderNo;
        this.orderSN = orderSN;
        this.qty = qty;
        this.completionDate = completionDate;
    }
}