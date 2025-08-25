package jp.adtekfuji.adfactoryserver.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement(name = "OperationProductTimeAndTactTimeEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationProductTimeAndTactTimeEntity {
    @Id
    @Column(name ="actual_id")
    private Long actualId;

    @Column(name ="model_name")
    private String modelName;

    @Column(name ="kanban_id")
    private Long kanbanId;

    @Column(name ="takt_time")
    private Long tactTime;

    @Column(name ="work_kanban_id")
    private Long workKanbanId;

    @Column(name ="work_time")
    private Long workTime;

    public Long getActualId() {
        return actualId;
    }

    public void setActualId(Long actualId) {
        this.actualId = actualId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Long getKanbanId() {
        return kanbanId;
    }

    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    public Long getTactTime() {
        return tactTime;
    }

    public void setTactTime(Long tactTime) {
        this.tactTime = tactTime;
    }

    public Long getWorkKanbanId() {
        return workKanbanId;
    }

    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    public Long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Long workTime) {
        this.workTime = workTime;
    }

    @Override
    public String toString() {
        return "OperationProductTimeAndTactTimeEntity{" +
                "actualId=" + actualId +
                ", modelName='" + modelName + '\'' +
                ", kanbanId=" + kanbanId +
                ", tactTime=" + tactTime +
                ", workKanbanId=" + workKanbanId +
                ", workTime=" + workTime +
                '}';
    }
}
