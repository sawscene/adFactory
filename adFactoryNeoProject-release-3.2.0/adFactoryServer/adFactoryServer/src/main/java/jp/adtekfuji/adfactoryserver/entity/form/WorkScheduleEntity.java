package jp.adtekfuji.adfactoryserver.entity.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;

import jakarta.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.Date;

public class WorkScheduleEntity extends WorkEntity implements Serializable {

    @XmlElement()
    @JsonProperty("implementDatetime")
    private Date implementDatetime;

    @XmlElement()
    @JsonProperty("lastDatetime")
    private Date lastDatetime;

    @XmlElement()
    @JsonProperty("nextDatetime")
    private Date nextDatetime;

    public WorkScheduleEntity() {
    }

    public WorkScheduleEntity(WorkEntity workEntity) {
        super(workEntity, workEntity.getLatestRev());
    }

    public Date getImplementDatetime() {
        return implementDatetime;
    }

    public void setImplementDatetime(Date implementDatetime) {
        this.implementDatetime = implementDatetime;
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
}

