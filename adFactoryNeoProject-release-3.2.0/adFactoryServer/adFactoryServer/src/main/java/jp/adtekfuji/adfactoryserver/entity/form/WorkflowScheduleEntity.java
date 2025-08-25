package jp.adtekfuji.adfactoryserver.entity.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;

import jakarta.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class WorkflowScheduleEntity extends WorkflowEntity implements Serializable {

    @XmlElement()
    @JsonProperty("equipment")
    private EquipmentEntity equipment;

    @XmlElement()
    @JsonProperty("lastDatetime")
    private Date lastDatetime;

    @XmlElement()
    @JsonProperty("nextDatetime")
    private Date nextDatetime;

    @XmlElement()
    @JsonProperty("works")
    List<WorkEntity> works;

    @XmlElement()
    @JsonProperty("hasInProgressReports")
    private boolean hasInProgressReports;

    public WorkflowScheduleEntity() {
    }

    public WorkflowScheduleEntity(WorkflowEntity workflowEntity) {
        super(workflowEntity);
        this.setWorkflowId(workflowEntity.getWorkflowId());
    }

    public Date getLastDatetime() {
        return lastDatetime;
    }

    public void setLastDatetime(Date lastDatetime) {
        this.lastDatetime = lastDatetime;
    }

    public Date getNextDatetime() {
        return nextDatetime;
    }

    public void setNextDatetime(Date nextDatetime) {
        this.nextDatetime = nextDatetime;
    }

    public EquipmentEntity getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentEntity equipment) {
        this.equipment = equipment;
    }

    public List<WorkEntity> getWorks() {
        return works;
    }

    public void setWorks(List<WorkEntity> works) {
        this.works = works;
    }

    public boolean isHasInProgressReports() {
        return hasInProgressReports;
    }

    public void setHasInProgressReports(boolean hasInProgressReports) {
        this.hasInProgressReports = hasInProgressReports;
    }
}

