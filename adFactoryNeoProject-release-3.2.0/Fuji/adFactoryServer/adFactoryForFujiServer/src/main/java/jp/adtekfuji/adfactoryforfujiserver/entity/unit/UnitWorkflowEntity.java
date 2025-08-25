/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.entity.unit;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 工程順情報
 *
 * @author s-heya
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
    "workflow_id",
    "workflow_name"}))
public class UnitWorkflowEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "workflow_id")
    private Long workflowId;
    @Column(name = "workflow_name")
    private String workflowName;

    public Long getWorkflowId() {
        return workflowId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    @Override
    public String toString() {
        return "UnitWorkflowEntity{" + "workflowId=" + workflowId + ", workflowName=" + workflowName + '}';
    }
}
