package jp.adtekfuji.adfactoryserver.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class BrowserLog {

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("eqId")
    private String eqId;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setEqId(String eqId) {
        this.eqId = eqId;
    }

    public String getEqId() {
        return eqId;
    }
};
