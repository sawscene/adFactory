package jp.adtekfuji.adfactoryserver.entity.operation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "call")
@JsonIgnoreProperties(ignoreUnknown=true)
public class CallOperationEntity implements Serializable {
    @XmlElement(required = false)
    @JsonProperty("call")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCall; // 呼出 true/ 呼出キャンセル false

    @XmlElement(required = false)
    @JsonProperty("reason")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reason; // 呼出理由

    @XmlElement(required = false)
    @JsonProperty("work_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long workId; // 工程ID

    @XmlElement(required = false)
    @JsonProperty("work_kanban_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long workKanbanId; // 工程ID

    @XmlElement(required = false)
    @JsonProperty("pair_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long pairId; // ペアID

    public CallOperationEntity() {
    }

    public CallOperationEntity(Boolean isCall, String reason, Long workId, Long workKanbanId) {
        this.isCall = isCall;
        this.reason = reason;
        this.workId = workId;
        this.workKanbanId = workKanbanId;
    }

    /**
     * 呼出?
     * @return 呼出?
     */
    public Boolean getCall() {
        return isCall;
    }

    /**
     * 呼出?設定
     * @param call　呼出?
     */
    public void setCall(Boolean call) {
        isCall = call;
    }

    /**
     * 呼出理由取得
     * @return 呼出理由
     */
    public String getReason() {
        return reason;
    }

    /**
     * 呼出理由を設定
     * @param reason 呼出理由
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 工程ID取得
     * @return 工程ID
     */
    public Long getWorkId() {
        return workId;
    }

    /**
     * 工程ID設定
     * @param workId 工程ID
     */
    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    /**
     * 工程カンバンID取得
     * @return 工程カンバンID
     */
    public Long getWorkKanbanId() {
        return workKanbanId;
    }

    /**
     * 工程カンバンID設定
     * @param workKanbanId 工程カンバンID
     */
    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    /**
     * ペアID取得 (呼出取り消し時のみ)
     * @return ペアID
     */
    public Long getPairId() {
        return pairId;
    }

    /**
     * ペアID設定 (呼出取り消し時のみ)
     * @param pairId ペアID
     */
    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    @Override
    public String toString() {
        return "CallOperationEntity{" +
                "isCall=" + isCall +
                ", reason='" + reason + '\'' +
                ", workId=" + workId +
                ", workKanbanId=" + workKanbanId +
                '}';
    }
}
