/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unit;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * カンバンエンティティ
 *
 * @author s-heya
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
  "kanban_id",
  "kanban_name",
  "fk_workflow_id",
  "workflow_name",
  "start_datetime",
  "comp_datetime",
  "kanban_status",
  "actual_start_datetime",
  "actual_comp_datetime"}))
@XmlRootElement(name = "kanban")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnitKanbanEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "kanban_id")
    protected Long kanbanId;
    @Column(name = "kanban_name")
    private String kanbanName;
    @Column(name = "fk_workflow_id")
    private Long fkWorkflowId;
    @Column(name = "workflow_name")
    private String workflowName;
    @Column(name = "start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDatetime;
    @Column(name = "comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compDatetime;
    @Enumerated(EnumType.STRING)
    @Column(name = "kanban_status")
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
    @Column(name = "actual_start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartTime;
    @Column(name = "actual_comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
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
        return "UnitKanbanEntity{" + "kanbanId=" + kanbanId + ", kanbanName=" + kanbanName + ", fkWorkflowId=" + fkWorkflowId + ", workflowName=" + workflowName +
                ", startDatetime=" + startDatetime + ", compDatetime=" + compDatetime + ", kanbanStatus=" + kanbanStatus + ", actualStartTime=" + actualStartTime +
                ", actualCompTime=" + actualCompTime + '}';
    }
}
