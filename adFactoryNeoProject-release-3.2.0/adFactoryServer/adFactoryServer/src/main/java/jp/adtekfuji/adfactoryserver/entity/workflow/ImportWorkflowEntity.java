/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkHierarchyEntity;

/**
 *
 * @author i.chugin
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "importWorkflow")
public class ImportWorkflowEntity {

    /** 工程順 */
    private WorkflowEntity workflow;

    /** 工程順階層情報 */
    private WorkflowHierarchyEntity workflowHierarchy;

    /** 工程 */
    @XmlElementRef(name = "work", type = WorkEntity.class)
    private List<WorkEntity> works = new ArrayList<>();

    /** 工程階層情報 */
    @XmlElementRef(name = "workHierarchy", type = WorkHierarchyEntity.class)
    private List<WorkHierarchyEntity> workHierarchies = new ArrayList<>();

    /** 作業パラメータ */
    @XmlElementRef(name = "workParameters", type = WorkParametersEntity.class)
    private List<WorkParametersEntity> workParameters = new ArrayList<>();


    public WorkflowEntity getWorkflow() {
        return workflow;
    }

    public void setWorkflow(WorkflowEntity workflow) {
        this.workflow = workflow;
    }

    public WorkflowHierarchyEntity getWorkflowHierarchy() {
        return workflowHierarchy;
    }

    public void setWorkflowHierarchy(WorkflowHierarchyEntity workflowHierarchy) {
        this.workflowHierarchy = workflowHierarchy;
    }

    public List<WorkEntity> getWorks() {
        return works;
    }

    public void setWorks(List<WorkEntity> works) {
        this.works = works;
    }

    public List<WorkHierarchyEntity> getWorkHierarchies() {
        return workHierarchies;
    }

    public void setWorkHierarchies(List<WorkHierarchyEntity> workHierarchies) {
        this.workHierarchies = workHierarchies;
    }

    public List<WorkParametersEntity> getWorkParameters() {
        return workParameters;
    }

    public void setWorkParameters(List<WorkParametersEntity> workParameters) {
        this.workParameters = workParameters;
    }
}
