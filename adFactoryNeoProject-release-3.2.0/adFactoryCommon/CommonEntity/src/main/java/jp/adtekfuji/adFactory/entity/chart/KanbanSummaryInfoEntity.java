/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.chart;

import java.io.Serializable;
import java.util.Date;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバン集計データ
 *
 * @author s-heya
 */
@XmlRootElement(name = "kanbanSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class KanbanSummaryInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Long kanbanId;

    @XmlElement
    private String kanbanName;

    @XmlElement
    private Integer workTimes;

    @XmlElement
    private Date actualStartTime;

    @XmlElement
    private Date actualEndTime;

    public Long getKanbanId() {
        return this.kanbanId;
    }

    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    public String getKanbanName() {
        return this.kanbanName;
    }

    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    public Integer getWorkTimes() {
        return this.workTimes;
    }

    public void setWorkTimes(Integer workTimes) {
        this.workTimes = workTimes;
    }

    public Date getActualStartTime() {
        return this.actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public Date getActualEndTime() {
        return this.actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kanbanName != null ? kanbanName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KanbanSummaryInfoEntity)) {
            return false;
        }
        KanbanSummaryInfoEntity other = (KanbanSummaryInfoEntity) object;
        if ((this.kanbanName == null && other.kanbanName != null) || (this.kanbanName != null && !this.kanbanName.equals(other.kanbanName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KanbanSummaryInfoEntity{" + "kanbanId=" + kanbanId + ", kanbanName=" + kanbanName + ", workTimes=" + workTimes + ", actualStartTime=" + actualStartTime + ", actualEndTime=" + actualEndTime + '}';
    }
}
