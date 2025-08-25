/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 生産実績通知
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "actualProductReportProperty")
@JsonIgnoreProperties(ignoreUnknown=true)
public class ActualProductReportPropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    @JsonProperty("key")
    private String propertyName;

    @XmlElement
    @JsonProperty("type")
    private String propertyType;

    @XmlElement
    @JsonProperty("val")
    private String propertyValue;

    @XmlElement
    @JsonProperty("disp")
    private Integer propertyOrder;

    @XmlElement
    @JsonProperty("accessoryId")
    private Long workPropertyId;

    @XmlElement
    @JsonProperty("memo")
    private List<ActualProductReportPropertyMemoEntity> memo;

    /**
     *
     */
    public ActualProductReportPropertyEntity() {
    }

    /**
     *
     * @param propertyName
     * @param propertyType
     * @param propertyValue
     * @param propertyOrder
     * @param workPropertyId
     */
    public ActualProductReportPropertyEntity(String propertyName, String propertyType, String propertyValue, Integer propertyOrder, Long workPropertyId) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.propertyValue = propertyValue;
        this.propertyOrder = propertyOrder;
        this.workPropertyId = workPropertyId;
    }

    /**
     *
     * @return
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     *
     * @param propertyName
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     *
     * @return
     */
    public String getPropertyType() {
        return this.propertyType;
    }

    /**
     *
     * @param propertyType
     */
    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    /**
     *
     * @return
     */
    public String getPropertyValue() {
        return this.propertyValue;
    }

    /**
     *
     * @param propertyValue
     */
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     *
     * @return
     */
    public Integer getPropertyOrder() {
        return this.propertyOrder;
    }

    /**
     *
     * @param propertyOrder
     */
    public void setPropertyOrder(Integer propertyOrder) {
        this.propertyOrder = propertyOrder;
    }

    /**
     *
     * @return
     */
    public Long getWorkPropertyId() {
        return this.workPropertyId;
    }

    /**
     *
     * @param workPropertyId
     */
    public void setWorkPropertyId(Long workPropertyId) {
        this.workPropertyId = workPropertyId;
    }

    /**
     * メモのGetter
     * @return
     */
    public List<ActualProductReportPropertyMemoEntity> getMemo() {
        return memo;
    }

    public void setMemo(List<ActualProductReportPropertyMemoEntity> memo) {
        this.memo = memo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final ActualProductReportPropertyEntity other = (ActualProductReportPropertyEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ActualProductReportPropertyEntity{")
                .append("propertyName=").append(this.propertyName)
                .append(", propertyType=").append(this.propertyType)
                .append(", propertyValue=").append(this.propertyValue)
                .append(", propertyOrder=").append(this.propertyOrder)
                .append(", workPropertyId=").append(this.workPropertyId)
                .append(", memo=").append(this.memo)
                .append("}")
                .toString();
    }
}
