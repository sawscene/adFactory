package jp.adtekfuji.adfactoryserver.entity.workflow;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement(name = "WorkflowTagExtract")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
        @NamedNativeQuery(name = "WorkflowTagExtractEntity.findTag", query = "SELECT cwfw.workflow_id, mw.work_name work_name, mw.work_rev work_rev, mws.document_title sheet_name, mws.work_section_order, (prop ->> 'tag') tag_name FROM (SELECT workflow_id, work_id FROM con_workflow_work WHERE workflow_id = ANY (?1)) cwfw JOIN mst_work mw ON cwfw.work_id = mw.work_id JOIN mst_work_section mws ON mws.work_id = mw.work_id JOIN jsonb_array_elements(mw.work_check_info) props(prop) ON (prop ->> 'page')::integer = mws.work_section_order AND (prop ->> 'tag') NOTNULL AND (prop ->> 'tag') <> ''", resultClass = WorkflowTagExtractEntity.class)})
public class WorkflowTagExtractEntity {

    @Id
    @Column(name = "workflow_id")
    private String workflowId;

    @Id
    @Column(name = "work_name")
    private String workName;

    @Id
    @Column(name = "work_rev")
    private Integer workRev;

    @Id
    @Column(name = "sheet_name")
    private String sheetName;

    @Id
    @Column(name = "tag_name")
    private String tagName;

    public WorkflowTagExtractEntity() {
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public Integer getWorkRev() {
        return workRev;
    }

    public void setWorkRev(Integer work_rev) {
        this.workRev = work_rev;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
