/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.ledger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
/**
 * 帳票出力ターゲット
 *
 * @author yu.nara
 */
public class LedgerTargetEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonProperty("name")
    private String name = ""; // 名称
    @JsonProperty("hierarchy_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long workflowHierarchyId; // 工程階層ID
    @JsonProperty("workflow_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long workflowId; // 工程順ID
    @JsonProperty("work_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long workId; // 工程ID

    public LedgerTargetEntity() {
    }

    public LedgerTargetEntity(String name, Long workflowHierarchyId, Long workflowId, Long workId) {
        this.name = name;
        this.workflowHierarchyId = workflowHierarchyId;
        this.workflowId = workflowId;
        this.workId = workId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getWorkflowHierarchyId() {
        return workflowHierarchyId;
    }

    public void setWorkflowHierarchyId(Long workflowHierarchyId) {
        this.workflowHierarchyId = workflowHierarchyId;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }


}
