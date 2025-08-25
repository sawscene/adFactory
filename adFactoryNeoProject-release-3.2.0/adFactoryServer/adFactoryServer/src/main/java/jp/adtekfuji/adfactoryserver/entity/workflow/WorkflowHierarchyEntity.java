/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

import java.util.List;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.HierarchyTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.HierarchyEntity;

/**
 * 工程順階層情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "workflowHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowHierarchyEntity extends HierarchyEntity {

    @XmlElementWrapper(name = "workflows")
    @XmlElement(name = "workflow")
    @Transient
    private List<WorkflowEntity> workflowCollection = null;// 工程順情報一覧

    /**
     * コンストラクタ
     */
    public WorkflowHierarchyEntity() {
        super.setHierarchyType(HierarchyTypeEnum.WORKFLOW);
    }

    /**
     * コンストラクタ
     *
     * @param parentId 親階層ID
     * @param hierarchyName 階層名
     */
    public WorkflowHierarchyEntity(Long parentId, String hierarchyName) {
        super.setHierarchyType(HierarchyTypeEnum.WORKFLOW);
        super.setParentHierarchyId(parentId);
        super.setHierarchyName(hierarchyName);
    }

    /**
     * 階層マスタに変換する。
     *
     * @return 階層マスタ
     * @throws Exception 
     */
    public HierarchyEntity upcast() throws Exception {
        HierarchyEntity hierarchy = new HierarchyEntity();
        hierarchy.setHierarchyId(this.getHierarchyId());
        hierarchy.setHierarchyType(this.getHierarchyType());
        hierarchy.setHierarchyName(this.getHierarchyName());
        hierarchy.setParentHierarchyId(this.getParentHierarchyId());
        hierarchy.setVerInfo(this.getVerInfo());
        hierarchy.setChildCount(this.getChildCount());
        return hierarchy;
    }

    /**
     * 工程順階層IDを取得する。
     *
     * @return 工程順階層ID
     */
    @XmlElement(name="workflowHierarchyId")
    public Long getWorkflowHierarchyId() {
        return super.getHierarchyId();
    }

    /**
     * 工程順階層IDを設定する。
     *
     * @param workflowHierarchyId 工程順階層ID
     */
    public void setWorkflowHierarchyId(Long workflowHierarchyId) {
        super.setHierarchyId(workflowHierarchyId);
    }

    /**
     * 工程順情報一覧を取得する。
     *
     * @return 工程順情報一覧
     */
    public List<WorkflowEntity> getWorkflowCollection() {
        return this.workflowCollection;
    }

    /**
     * 工程順情報一覧を設定する。
     *
     * @param workflowCollection 工程順情報一覧
     */
    public void setWorkflowCollection(List<WorkflowEntity> workflowCollection) {
        this.workflowCollection = workflowCollection;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkflowHierarchyEntity{")
                .append("workflowHierarchyId=").append(this.getWorkflowHierarchyId())
                .append(", ")
                .append("hierarchyName=").append(this.getHierarchyName())
                .append(", ")
                .append("parentHierarchyId=").append(this.getParentHierarchyId())
                .append(", ")
                .append("verInfo=").append(this.getVerInfo())
                .append("}")
                .toString();
    }
}
