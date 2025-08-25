/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.dsKanban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバン作成条件 (デンソー高棚様向け)
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dsKanbanCreateCondition")
@JsonIgnoreProperties(ignoreUnknown=true)
public class DsKanbanCreateCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Integer category;
    @XmlElement()
    private String productNo;
    @XmlElement()
    private Integer quantity;
    @XmlElement()
    private String packageCode;
    @XmlElement()
    private String serial;
    @XmlElement()
    private String property;
    @XmlElement()
    private String qrCode;
    
    /**
     * コンストラクタ
     */
    public DsKanbanCreateCondition() {
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanCreateCondition{")
                .append("category=").append(this.category)
                .append("productNo=").append(this.productNo)
                .append("quantity=").append(this.quantity)
                .append("packageCode=").append(this.packageCode)
                .append("serial=").append(this.serial)
                .append("}")
                .toString();
    }
}
