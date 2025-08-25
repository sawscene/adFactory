package jp.adtekfuji.adFactory.entity.form;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ledger_condition")
public class FormCondition {

    @JsonProperty("workflow_ids")
    private List<Long> workflowIds;

    public FormCondition() {
    }

    public List<Long> getWorkflowIds() {
        return workflowIds;
    }

    public void setWorkflowIds(List<Long> workflowIds) {
        this.workflowIds = workflowIds;
    }

    @Override
    public String toString() {
        return "FormCondition{" +
                "workflowIds=" + workflowIds +
                '}';
    }
}
