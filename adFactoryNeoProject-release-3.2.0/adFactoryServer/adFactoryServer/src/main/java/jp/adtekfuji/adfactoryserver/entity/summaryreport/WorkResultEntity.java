package jp.adtekfuji.adfactoryserver.entity.summaryreport;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public
class WorkResultEntity implements Serializable {
    @Id
    @Column(name="actual_id")
    public Long actualId;
    @Column(name = "item")
    public String item;
    @Column(name = "kanban_id")
    public Long kanbanId;
    @Column(name = "workflow_id")
    public Long workflowId;
    @Column(name = "workflow_name")
    public String workflowName;
    @Column(name = "work_kanban_id")
    public Long workKanbanId;
    @Column(name = "work_id")
    public Long workId;
    @Column(name = "work_name")
    public String workName;
    @Column(name = "takt_time")
    public Long tactTime;
    @Column(name = "st")
    public Date startTime;
    @Column(name = "et")
    public Date endTime;
    @Column(name = "ei")
    public Long equipmentId;
    @Column(name = "equipment_identify")
    public String equipmentIdentify;
    @Column(name = "oi")
    public Long organizationId;
    @Column(name = "organization_identify")
    public String organizationIdentify;
    @Column(name = "organization_name")
    public String organizationName;
    @Column(name = "ir")
    public String interruptReason;
    @Column(name = "dr")
    public String delayReason;

    public long workTime = 0;
    public long workNum = 0;

    public WorkResultEntity copy()
    {
        WorkResultEntity copy = new WorkResultEntity();
        copy.actualId = this.actualId;
        copy.item = this.item;
        copy.kanbanId = this.kanbanId;
        copy.workflowId = this.workflowId;
        copy.workflowName = this.workflowName;
        copy.workKanbanId = this.workKanbanId;
        copy.workId = this.workId;
        copy.workName = this.workName;
        copy.tactTime = this.tactTime;
        copy.startTime = this.startTime;
        copy.endTime = this.endTime;
        copy.equipmentId = this.equipmentId;
        copy.equipmentIdentify = this.equipmentIdentify;
        copy.organizationId = this.organizationId;
        copy.organizationIdentify = this.organizationIdentify;
        copy.organizationName = this.organizationName;
        copy.interruptReason = this.interruptReason;
        copy.delayReason = this.delayReason;
        copy.workTime = this.workTime;
        copy.workNum = this.workNum;
        return copy;
    }

}
