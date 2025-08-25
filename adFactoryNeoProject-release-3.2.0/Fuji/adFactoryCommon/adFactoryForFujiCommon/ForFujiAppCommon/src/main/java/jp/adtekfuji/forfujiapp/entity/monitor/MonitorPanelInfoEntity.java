/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.monitor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 進捗モニタパネル表示用情報クラス
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.20.Thr
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "monitorPanelInfo")
public class MonitorPanelInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Long unitId;
    @XmlElement()
    private Date startDate;
    @XmlElement
    private Date endDate;
    @XmlElement()
    private String title;
    @XmlElement()
    private String workName;
    @XmlElement()
    private Long progressTimeMillisec;
    @XmlElement()
    private String workerName;
    @XmlElement()
    private String backgroundColor;
    @XmlElementWrapper(name = "kanbanIds")
    @XmlElement(name = "kanbanId")
    private List<Long> kanbanIds;

    public MonitorPanelInfoEntity() {
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public Long getProgressTimeMillisec() {
        return progressTimeMillisec;
    }

    public void setProgressTimeMillisec(Long progressTimeMillisec) {
        this.progressTimeMillisec = progressTimeMillisec;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public List<Long> getKanbanIds() {
        return kanbanIds;
    }

    public void setKanbanIds(List<Long> kanbanIds) {
        this.kanbanIds = kanbanIds;
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
        final MonitorPanelInfoEntity other = (MonitorPanelInfoEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return "MonitorPanelInfoEntity{" + "title=" + title + ", workName=" + workName + ", progressTimeMillisec=" + progressTimeMillisec + ", workerName=" + workerName + ", backgroundColor=" + backgroundColor + '}';
    }

}
