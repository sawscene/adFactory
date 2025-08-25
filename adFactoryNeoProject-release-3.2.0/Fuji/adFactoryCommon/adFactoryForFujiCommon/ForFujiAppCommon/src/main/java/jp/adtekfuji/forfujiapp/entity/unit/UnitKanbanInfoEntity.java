/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.unit;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * カンバンエンティティ
 *
 * @author s-heya
 */
@XmlRootElement(name = "kanban")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class UnitKanbanInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    protected Long kanbanId;
    @XmlElement
    private String kanbanName;
    @XmlElement
    private Long fkWorkflowId;
    @XmlElement
    private String workflowName;
    @XmlElement
    private Date startDatetime;
    @XmlElement
    private Date compDatetime;
    @XmlElement
    private KanbanStatusEnum kanbanStatus;
    //@Column(name = "fk_interrupt_reason_id")
    //private Long fkInterruptReasonId;
    //@Column(name = "fk_delay_reason_id")
    //private Long fkDelayReasonId;
    //@Transient
    //private List<KanbanPropertyEntity> propertyCollection = null;
    //@Transient
    //private List<WorkKanbanEntity> workKanbanCollection = null;
    //@Transient
    //private List<WorkKanbanEntity> separateworkKanbanCollection = null;
    //@Transient
    //private List<ActualResultEntity> actualResultCollection = null;
    @XmlElement
    private Date actualStartTime;
    @XmlElement
    private Date actualCompTime;

    public Long getKanbanId() {
        return kanbanId;
    }

    public String getKanbanName() {
        return kanbanName;
    }

    public Long getFkWorkflowId() {
        return fkWorkflowId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public Date getStartDatetime() {
        return startDatetime;
    }

    public Date getCompDatetime() {
        return compDatetime;
    }

    public KanbanStatusEnum getKanbanStatus() {
        return kanbanStatus;
    }

    public Date getActualStartTime() {
        return actualStartTime;
    }

    public Date getActualCompTime() {
        return actualCompTime;
    }

    @Override
    public String toString() {
        return "UnitKanbanInfoEntity{" + "kanbanId=" + kanbanId + ", kanbanName=" + kanbanName + ", fkWorkflowId=" + fkWorkflowId + ", workflowName=" + workflowName +
                ", startDatetime=" + startDatetime + ", compDatetime=" + compDatetime + ", kanbanStatus=" + kanbanStatus + ", actualStartTime=" + actualStartTime +
                ", actualCompTime=" + actualCompTime + '}';
    }
}
