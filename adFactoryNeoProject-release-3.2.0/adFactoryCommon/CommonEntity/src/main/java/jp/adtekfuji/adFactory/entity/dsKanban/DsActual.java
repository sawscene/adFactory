/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.dsKanban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author s-heya
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DsActual implements Serializable {

    @JsonProperty("key")
    private String key;

    @JsonProperty("pn")
    private String personName;

    @JsonProperty("st")
    private String startDateTime;

    @JsonProperty("ct")
    private String compDateTime;

    @JsonProperty("wt")
    private Long workTime;

    /**
     * コンストラクタ
     */
    public DsActual() {
    }

    /**
     * コンストラクタ
     * 
     * @param key 
     */
    public DsActual(String key) {
        this.key = key;
    }

    /**
     * コンストラクタ
     * 
     * @param map 
     */
    public DsActual(Map<String, Object> map) {
        this.key = (String) map.get("key");
        this.personName = (String) map.get("pn");
        this.startDateTime = (String) map.get("st");
        this.compDateTime = (String) map.get("ct");
        this.workTime = Objects.nonNull(map.get("wt")) ? ((Integer) map.get("wt")).longValue() : null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getCompDateTime() {
        return compDateTime;
    }

    public void setCompDateTime(String compDateTime) {
        this.compDateTime = compDateTime;
    }

    public Long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Long workTime) {
        this.workTime = workTime;
    }
    
    
    
}
