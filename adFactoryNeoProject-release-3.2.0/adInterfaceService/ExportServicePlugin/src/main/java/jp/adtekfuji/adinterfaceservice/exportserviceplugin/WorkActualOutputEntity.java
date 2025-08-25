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
 * 工程完了報告通知情報
 *
 * @author nouzawa
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OMCompletionProcess")
public class WorkActualOutputEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @XmlElement(name = "OrderNO")
    private String orderNo;            // 製造オーダー番号
    @XmlElement(name = "OrderSN")
    private String orderSN;            // シリアル
    @XmlElement(name = "OperationName")
    private String operationName;      // 手順
    @XmlElement(name = "Qty") 
    private String qty;                 // 完了数
    @XmlElement(name = "CompletionDate")
    private String completionDate;     // 完了日

    /**
     * デフォルト・コンストラクタ
     */
    public WorkActualOutputEntity() {
    }

    /**
     * コンストラクタ
     * 
     * @param orderNo
     * @param orderSN
     * @param operationName
     * @param qty
     * @param completionDate 
     */
    public WorkActualOutputEntity(String orderNo, String orderSN, String operationName, String qty, String completionDate) {
        this.orderNo = orderNo;
        this.orderSN = orderSN;
        this.operationName = operationName;
        this.qty = qty;
        this.completionDate = completionDate;
    }

}