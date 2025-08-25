
package com.example.demo.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "OrderNO")
    private String orderNo;
    @XmlElement(name = "OrderSN")
    private String orderSN;
    @XmlElement(name = "OperationName")
    private String operationName;
    @XmlElement(name = "Qty")
    private Integer qty;
    @XmlElement(name = "CompletionDate")
    private String completionDate;

    @Override
    public String toString() {
        return "Report [completionDate=" + completionDate + ", operationName=" + operationName + ", orderNo=" + orderNo
                + ", orderSN=" + orderSN + ", qty=" + qty + "]";
    }
    
}