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
public class OperationWorkTimeAndTactTimeEntity {

    @Id
    @Column(name ="actual_id")
    private Long actualId;

    @Column(name ="work_kanban_id")
    private Long workkanbanId;

    @Column(name ="model_name")
    private String modelName;

    @Column(name ="work_name")
    private String workName;

    @Column (name = "takt_time")
    Long tactTime;

    @Column (name = "assist")
    Boolean assist;

    @Column(name = "work_time")
    Long workTime;


    @Column(name ="work_id")
    Long workId;

    public Long getActualId() {
        return actualId;
    }

    public void setActualId(Long actualId) {
        this.actualId = actualId;
    }

    public Long getWorkkanbanId() {
        return workkanbanId;
    }

    public void setWorkkanbanId(Long workkanbanId) {
        this.workkanbanId = workkanbanId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public Long getTactTime() {
        return tactTime;
    }

    public void setTactTime(Long tactTime) {
        this.tactTime = tactTime;
    }

    public Boolean getAssist() {
        return assist;
    }

    public void setAssist(Boolean assist) {
        this.assist = assist;
    }

    public Long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Long workTime) {
        this.workTime = workTime;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    @Override
    public String toString() {
        return "OperationWorkTimeAndTactTimeEntity{" +
                "actualId=" + actualId +
                ", workkanbanId=" + workkanbanId +
                ", modelName='" + modelName + '\'' +
                ", workName='" + workName + '\'' +
                ", tactTime=" + tactTime +
                ", assist=" + assist +
                ", workTime=" + workTime +
                ", workId=" + workId +
                '}';
    }
}
