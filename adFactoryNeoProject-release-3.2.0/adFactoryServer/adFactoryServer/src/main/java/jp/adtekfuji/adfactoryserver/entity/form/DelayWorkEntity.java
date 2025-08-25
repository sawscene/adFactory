package jp.adtekfuji.adfactoryserver.entity.form;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class DelayWorkEntity implements Serializable {
    @JsonProperty("workId")
    Long workId; // 工程ID

    @JsonProperty("nextImplementDatetime")
    Date nextImplementDatetime; // 次回実施日時

    public DelayWorkEntity() {
    }

    public DelayWorkEntity(Long workId, Date nextImplementDatetime) {
        this.workId = workId;
        this.nextImplementDatetime = nextImplementDatetime;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Date getNextImplementDatetime() {
        return nextImplementDatetime;
    }

    public void setNextImplementDatetime(Date nextImplementDatetime) {
        this.nextImplementDatetime = nextImplementDatetime;
    }
}
