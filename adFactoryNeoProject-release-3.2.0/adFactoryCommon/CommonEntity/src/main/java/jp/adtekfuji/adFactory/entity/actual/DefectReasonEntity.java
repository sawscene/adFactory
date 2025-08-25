/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.actual;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 不良内容
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "defectiveContent")
public class DefectReasonEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Long defectId;
    @XmlElement()
    private Long defectOrder;// 表示順
    @XmlElement()
    private String defectType;// 種別
    @XmlElement()
    private String defectClass;// 分類
    @XmlElement()
    private String defectValue;// 内容

    /**
     * コンストラクタ
     */
    public DefectReasonEntity() {
    }

    /**
     * 不良IDを取得する。
     *
     * @return 不良ID
     */
    public Long getDefectId() {
        return this.defectId;
    }

    /**
     * 不良IDを設定する。
     *
     * @param defectId 不良ID
     */
    public void setDefectId(Long defectId) {
        this.defectId = defectId;
    }

    /**
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public Long getDefectOrder() {
        return this.defectOrder;
    }

    /**
     * 表示順を設定する。
     *
     * @param defectOrder 表示順
     */
    public void setDefectOrder(Long defectOrder) {
        this.defectOrder = defectOrder;
    }

    /**
     * 種別を取得する。
     *
     * @return 種別
     */
    public String getDefectType() {
        return this.defectType;
    }

    /**
     * 種別を設定する。
     *
     * @param defectType 種別
     */
    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }

    /**
     * 分類を取得する。
     *
     * @return 分類
     */
    public String getDefectClass() {
        return this.defectClass;
    }

    /**
     * 分類を設定する。
     *
     * @param defectClass 分類
     */
    public void setDefectClass(String defectClass) {
        this.defectClass = defectClass;
    }

    /**
     * 内容を取得する。
     *
     * @return 内容
     */
    public String getDefectValue() {
        return this.defectValue;
    }

    /**
     * 内容を設定する。
     *
     * @param defectValue 内容
     */
    public void setDefectValue(String defectValue) {
        this.defectValue = defectValue;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.defectId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefectReasonEntity other = (DefectReasonEntity) obj;
        if (!Objects.equals(this.defectId, other.defectId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("DefectReasonEntity{")
                .append("defectId=").append(this.defectId)
                .append(", ")
                .append("defectOrder=").append(this.defectOrder)
                .append(", ")
                .append("defectType=").append(this.defectType)
                .append(", ")
                .append("defectClass=").append(this.defectClass)
                .append(", ")
                .append("defectValue=").append(this.defectValue)
                .append("}")
                .toString();
    }
}
