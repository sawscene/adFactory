/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * シリアル番号情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "serialNoInfo")
public class SerialNoInfo implements Serializable {

    @XmlElement()
    private String productName;// 品名

    @XmlElement()
    private String serialNo;// シリアル番号

    /**
     * コンストラクタ
     */
    public SerialNoInfo() {
    }

    /**
     * 品名を取得する。
     *
     * @return 品名
     */
    public String getProductName() {
        return this.productName;
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
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public String getSerialNo() {
        return this.serialNo;
    }

    /**
     * シリアル番号を設定する。
     *
     * @param serialNo シリアル番号
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.productName);
        hash = 37 * hash + Objects.hashCode(this.serialNo);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SerialNoInfo other = (SerialNoInfo) obj;
        if (!Objects.equals(this.productName, other.productName)) {
            return false;
        }
        if (!Objects.equals(this.serialNo, other.serialNo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("SerialNoInfo{")
                .append("productName=").append(this.productName)
                .append(", ")
                .append("serialNo=").append(this.serialNo)
                .append("}")
                .toString();
    }
}
