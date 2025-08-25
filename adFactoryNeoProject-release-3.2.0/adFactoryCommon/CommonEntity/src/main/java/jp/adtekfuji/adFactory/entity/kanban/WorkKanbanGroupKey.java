/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 工程グループキー
 *
 * @author s-heya
 */
@XmlRootElement(name = "groupKey")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkKanbanGroupKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Date startTime;
    @XmlElement
    private String workflowName;

    public static WorkKanbanGroupKey createGroupKey(Date startDate, String workflowName) {
        WorkKanbanGroupKey groupKey = new WorkKanbanGroupKey();
        groupKey.setStartTime(startDate);
        groupKey.setWorkflowName(workflowName);
        return groupKey;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getWorkflowName() {
        return this.workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.startTime);
        hash = 97 * hash + Objects.hashCode(this.workflowName);
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
        final WorkKanbanGroupKey other = (WorkKanbanGroupKey) obj;
        if (!Objects.equals(this.workflowName, other.workflowName)) {
            return false;
        }
        return Objects.equals(this.startTime, other.startTime);
    }

    @Override
    public String toString() {
        return "WorkKanbanGroupKey{" + "startTime=" + startTime + ", workflowName=" + workflowName + '}';
    }
}
