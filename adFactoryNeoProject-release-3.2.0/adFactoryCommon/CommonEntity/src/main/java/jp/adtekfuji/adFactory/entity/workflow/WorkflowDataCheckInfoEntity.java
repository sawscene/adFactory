package jp.adtekfuji.adFactory.entity.workflow;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.WorkflowDateCheckErrorTypeEnum;

@XmlRootElement(name = "workflow_data_check")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowDataCheckInfoEntity implements Serializable {

    @XmlElement()
    private WorkflowDateCheckErrorTypeEnum errorType;

    @XmlElement(name = "message")
    private String message;

    /**
     * コンストラクタ
     */
    public WorkflowDataCheckInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param errorType
     * @param message 
     */
    public WorkflowDataCheckInfoEntity(WorkflowDateCheckErrorTypeEnum errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }

    /**
     * 
     * @return 
     */
    public WorkflowDateCheckErrorTypeEnum getErrorType() {
        return this.errorType;
    }

    /**
     * 
     * @param errorType 
     */
    public void setErrorType(WorkflowDateCheckErrorTypeEnum errorType) {
        this.errorType = errorType;
    }

    /**
     * 
     * @return 
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * 
     * @param message 
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
