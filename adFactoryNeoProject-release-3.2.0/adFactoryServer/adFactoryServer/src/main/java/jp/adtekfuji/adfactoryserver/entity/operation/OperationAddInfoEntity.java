package jp.adtekfuji.adfactoryserver.entity.operation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "operationAddInfo")
@JsonIgnoreProperties(ignoreUnknown=true)
public class OperationAddInfoEntity implements Serializable {

    @XmlTransient
    @JsonProperty("call")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CallOperationEntity callOperationEntity;

    @XmlTransient
    @JsonProperty("indirectWork")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private IndirectWorkOperationEntity indirectWork;
    
    @XmlTransient
    @JsonProperty("changeResult")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OperateChangeResultEntity changeResult;
    
    /**
     * コンストラクタ
     */
    public OperationAddInfoEntity() {
    }

    /**
     * 呼出エンティティ取得
     * @return　呼出エンティティ
     */
    public CallOperationEntity getCallOperationEntity() {
        return callOperationEntity;
    }

    /**
     * 呼出エンティティセット
     * @param callOperationEntity 呼出エンティティ
     */
    public void setCallOperationEntity(CallOperationEntity callOperationEntity) {
        this.callOperationEntity = callOperationEntity;
    }

    public IndirectWorkOperationEntity getIndirectWork() {
        return indirectWork;
    }

    public void setIndirectWork(IndirectWorkOperationEntity indirectWorkOperationEntity) {
        this.indirectWork = indirectWorkOperationEntity;
    }

    /**
     * 実績修正の操作実績を取得する
     * 
     * @return 実績修正の操作実績 
     */
    public OperateChangeResultEntity getChangeResult() {
        return changeResult;
    }

    /**
     * 実績修正の操作実績を設定する
     * 
     * @param changeResult 実績修正の操作実績
     */
    public void setChangeResult(OperateChangeResultEntity changeResult) {
        this.changeResult = changeResult;
    }

    
}
