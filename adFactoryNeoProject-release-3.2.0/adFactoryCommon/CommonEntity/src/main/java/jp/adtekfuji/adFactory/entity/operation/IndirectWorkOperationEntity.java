package jp.adtekfuji.adFactory.entity.operation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "indirect")
@JsonIgnoreProperties(ignoreUnknown=true)
public class IndirectWorkOperationEntity implements Serializable {


    @XmlElement(required = false)
    @JsonProperty("doIndirect")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean doIndirect; // 開始 true/ 完了 false

    @XmlElement(required = false)
    @JsonProperty("indirectWorkId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long indirectWorkId; // 間接作業ID

    @XmlElement(required = false)
    @JsonProperty("pairId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long pairId; // ペアID

    @XmlElement(required = false)
    @JsonProperty("productionNum")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String productionNum; // 製造番号

    @XmlElement(required = false)
    @JsonProperty("reason")
    private String reason; // 理由

    public IndirectWorkOperationEntity() {
    }

    public IndirectWorkOperationEntity(Boolean doIndirect, Long indirectWorkId, Long pairId, String productionNum) {
        this.doIndirect = doIndirect;
        this.indirectWorkId = indirectWorkId;
        this.pairId = pairId;
        this.productionNum = productionNum;
    }

    public Boolean getDoIndirect() {
        return doIndirect;
    }

    public void setDoIndirect(Boolean doIndirect) {
        this.doIndirect = doIndirect;
    }

    public Long getIndirectWorkId() {
        return indirectWorkId;
    }

    public void setIndirectWorkId(Long indirectWorkId) {
        this.indirectWorkId = indirectWorkId;
    }

    public Long getPairId() {
        return pairId;
    }

    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    public String getProductionNum() {
        return productionNum;
    }

    public void setProductionNum(String productionNum) {
        this.productionNum = productionNum;
    }

    /**
     * 理由を取得する
     * 
     * @return 理由
     */
    public String getReason() {
        return reason;
    }

    
}
