package jp.adtekfuji.adfactoryserver.entity.operation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
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
    @JsonProperty("suspendActualResultId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Long> suspendActualResultId; // 中断中の実績

    @XmlElement(required = false)
    @JsonProperty("reason")
    @JsonInclude(JsonInclude.Include.NON_NULL)
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

    /**
     * 間接作業IDを取得
     * @return 間接作業ID
     */
    public Long getIndirectWorkId() {
        return indirectWorkId;
    }

    /**
     * 間接作業IDを設定
     * @param indirectWorkId 間接作業ID
     */
    public void setIndirectWorkId(Long indirectWorkId) {
        this.indirectWorkId = indirectWorkId;
    }

    /**
     * ペアIDを取得
     * @return ペアID
     */
    public Long getPairId() {
        return pairId;
    }

    /**
     * ペアIDを設定
     * @param pairId ペアID
     */
    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    /**
     * 製造番号を取得
     * @return 製造番号
     */
    public String getProductionNum() {
        return productionNum;
    }

    /**
     * 製造番号設定
     * @param productionNum 製造番号
     */
    public void setProductionNum(String productionNum) {
        this.productionNum = productionNum;
    }

    /**
     * 中断中の工程カンバンIDを取得
     * @return 工程カンバンID
     */
    public List<Long> getSuspendActualResultId() {
        return suspendActualResultId;
    }

    /**
     * 中断中の工程カンバンIDを設定
     * @param suspendActualResultId 中断中の実績ID
     */
    public void setSuspendActualResultId(List<Long> suspendActualResultId) {
        this.suspendActualResultId = suspendActualResultId;
    }

    /**
     * 理由を取得する。
     * 
     * @return 理由 
     */
    public String getReason() {
        return reason;
    }

    /**
     * 理由を設定する。
     * 
     * @param reason 理由
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

}
