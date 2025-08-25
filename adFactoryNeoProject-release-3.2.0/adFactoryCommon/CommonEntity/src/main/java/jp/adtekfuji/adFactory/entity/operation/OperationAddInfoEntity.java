package jp.adtekfuji.adFactory.entity.operation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown=true)
public class OperationAddInfoEntity implements Serializable {

    @XmlTransient
    @JsonProperty("call")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CallOperationEntity callOperationEntity;

    @XmlTransient
    @JsonProperty("indirectWork")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private IndirectWorkOperationEntity indirectWorkOperationEntity;

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

    public IndirectWorkOperationEntity getIndirectWorkOperationEntity() {
        return indirectWorkOperationEntity;
    }

    public void setIndirectWorkOperationEntity(IndirectWorkOperationEntity indirectWorkOperationEntity) {
        this.indirectWorkOperationEntity = indirectWorkOperationEntity;
    }
}
