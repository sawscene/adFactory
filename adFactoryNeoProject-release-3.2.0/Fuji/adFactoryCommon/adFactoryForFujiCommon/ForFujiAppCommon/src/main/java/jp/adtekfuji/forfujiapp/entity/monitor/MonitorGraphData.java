/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.monitor;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 進捗モニタグラフ座標データクラス
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.20.Thr
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "monitorGraphData")
public class MonitorGraphData implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Long workNumIndex;
    @XmlElement()
    private Long workkanbanId;
    @XmlElement()
    private String workName;
    @XmlElement()
    private Long taktTime;
    @XmlElement
    private Long workingTime;
    @XmlElement()
    private Long processTime;
    @XmlElement()
    private Date workStartDatetime;
    @XmlElement()
    private Date workEndDatetime;
    @XmlElement()
    private Date actualStartDatetime;
    @XmlElement()
    private Date actualEndDatetime;
    @XmlTransient
    private String displayValue;

    public MonitorGraphData() {
    }

    public MonitorGraphData(Long workNumIndex, Long processTime) {
        this.workNumIndex = workNumIndex;
        this.processTime = processTime;
    }

    public MonitorGraphData(Long workNumIndex, String workName, Long taktTime, Long workingTime, Long processTime, Date workStartDatetime, Date workEndDatetime, Date actualStartDatetime, Date actualEndDatetime) {
        this.workNumIndex = workNumIndex;
        this.workName = workName;
        this.taktTime = taktTime;
        this.workingTime = workingTime;
        this.processTime = processTime;
        this.workStartDatetime = workStartDatetime;
        this.workEndDatetime = workEndDatetime;
        this.actualStartDatetime = actualStartDatetime;
        this.actualEndDatetime = actualEndDatetime;
    }

    public Long getWorkNumIndex() {
        return workNumIndex;
    }

    public void setWorkNumIndex(Long workNumIndex) {
        this.workNumIndex = workNumIndex;
    }

    public Long getWorkkanbanId() {
        return workkanbanId;
    }

    public void setWorkkanbanId(Long workkanbanId) {
        this.workkanbanId = workkanbanId;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public Long getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(Long taktTime) {
        this.taktTime = taktTime;
    }

    public Long getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Long workingTime) {
        this.workingTime = workingTime;
    }

    public Long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Long processTime) {
        this.processTime = processTime;
    }

    public Date getWorkStartDatetime() {
        return workStartDatetime;
    }

    public void setWorkStartDatetime(Date workStartDatetime) {
        this.workStartDatetime = workStartDatetime;
    }

    public Date getWorkEndDatetime() {
        return workEndDatetime;
    }

    public void setWorkEndDatetime(Date workEndDatetime) {
        this.workEndDatetime = workEndDatetime;
    }

    public Date getActualStartDatetime() {
        return actualStartDatetime;
    }

    public void setActualStartDatetime(Date actualStartDatetime) {
        this.actualStartDatetime = actualStartDatetime;
    }

    public Date getActualEndDatetime() {
        return actualEndDatetime;
    }

    public void setActualEndDatetime(Date actualEndDatetime) {
        this.actualEndDatetime = actualEndDatetime;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
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
        final MonitorGraphData other = (MonitorGraphData) obj;
        return true;
    }

    @Override
    public String toString() {
        return "MonitorGraphData{" + "workNumIndex=" + workNumIndex + ", processTime=" + processTime + '}';
    }
}
