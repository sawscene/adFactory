package jp.adtekfuji.adfactoryserver.entity.workflow;

import jp.adtekfuji.adFactory.enumerate.WorkflowDateCheckErrorTypeEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "workflow_data_check")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowDataCheckEntity implements Serializable {

    @XmlElement()
    private WorkflowDateCheckErrorTypeEnum errorType;

    @XmlElement(name="message")
    private String message;

    public WorkflowDataCheckEntity()
    {
    }

    public WorkflowDataCheckEntity(WorkflowDateCheckErrorTypeEnum errorType, String message)
    {
        this.errorType = errorType;
        this.message = message;
    }

}
