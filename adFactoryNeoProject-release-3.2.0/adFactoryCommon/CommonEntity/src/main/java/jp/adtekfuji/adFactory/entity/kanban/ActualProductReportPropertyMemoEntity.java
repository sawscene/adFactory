/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
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
@XmlRootElement(name = "actualProductReportMemoProperty")
public class ActualProductReportPropertyMemoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    @JsonProperty("date")
    private String date;

    @XmlElement
    @JsonProperty("workerName")
    private String workerName;

    @XmlElement
    @JsonProperty("organizationId")
    private Long organizationId;


    @XmlElement
    @JsonProperty("memoType")
    private String memoType;

    @XmlElement
    @JsonProperty("data")
    private String data;

    public ActualProductReportPropertyMemoEntity() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getMemoType() {
        return memoType;
    }

    public void setMemoType(String memoType) {
        this.memoType = memoType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String toString() {
        return "ActualProductReportPropertyMemoEntity{" +
                "date='" + date + '\'' +
                ", workerName='" + workerName + '\'' +
                ", organizationId=" + organizationId +
                ", memoType='" + memoType + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
