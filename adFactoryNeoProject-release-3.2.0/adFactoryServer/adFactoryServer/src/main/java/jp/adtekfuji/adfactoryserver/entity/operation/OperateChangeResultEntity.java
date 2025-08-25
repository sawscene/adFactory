/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.operation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;

/**
 * 実績修正の操作実績
 * 
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "changeResult")
@JsonIgnoreProperties(ignoreUnknown=true)
public class OperateChangeResultEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @XmlElement(required = false)
    @JsonProperty("oldResult")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final ActualResultEntity oldResult;
    
    @XmlElement(required = false)
    @JsonProperty("newResult")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final ActualResultEntity newResult;
    
    /**
     * コンストラクタ
     * 
     * @param oldResult
     * @param newResult 
     */
    public OperateChangeResultEntity(ActualResultEntity oldResult, ActualResultEntity newResult) {
        this.oldResult = oldResult;
        this.newResult = newResult;
    }
    
}
