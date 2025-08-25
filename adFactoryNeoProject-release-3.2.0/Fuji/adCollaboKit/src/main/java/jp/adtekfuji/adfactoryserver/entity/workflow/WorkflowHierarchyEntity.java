/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_workflow_hierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workflowHierarchy")
@NamedQueries({
    @NamedQuery(name = "WorkflowHierarchyEntity.checkAddByHierarchyName", query = "SELECT COUNT(w.workflowHierarchyId) FROM WorkflowHierarchyEntity w WHERE w.hierarchyName = :hierarchyName"),
    @NamedQuery(name = "WorkflowHierarchyEntity.checkUpdateByHierarchyName", query = "SELECT COUNT(w.workflowHierarchyId) FROM WorkflowHierarchyEntity w WHERE w.hierarchyName = :hierarchyName AND w.workflowHierarchyId != :workflowHierarchyId"),

    @NamedQuery(name = "WorkflowHierarchyEntity.findAll", query = "SELECT w FROM WorkflowHierarchyEntity w"),
    @NamedQuery(name = "WorkflowHierarchyEntity.findByWorkflowHierarchyId", query = "SELECT w FROM WorkflowHierarchyEntity w WHERE w.workflowHierarchyId = :workflowHierarchyId"),
    @NamedQuery(name = "WorkflowHierarchyEntity.findByHierarchyName", query = "SELECT w FROM WorkflowHierarchyEntity w WHERE w.hierarchyName = :hierarchyName"),
    // 親階層に属する階層を問い合わせる
    @NamedQuery(name = "WorkflowHierarchyEntity.findByParentId", query = "SELECT w FROM TreeWorkflowHierarchyEntity pk JOIN WorkflowHierarchyEntity w ON pk.treeWorkflowHierarchyEntityPK.childId = w.workflowHierarchyId WHERE pk.treeWorkflowHierarchyEntityPK.parentId = :parentId ORDER BY w.hierarchyName")})
public class WorkflowHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "workflow_hierarchy_id")
    private Long workflowHierarchyId;
    @Transient
    private Long parentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "hierarchy_name")
    private String hierarchyName;
    @Transient
    private Long childCount = 0L;
    @XmlElementWrapper(name = "workflows")
    @XmlElement(name = "workflow")
    @Transient
    private List<WorkflowEntity> workflowCollection = null;

    public WorkflowHierarchyEntity() {
    }

    public WorkflowHierarchyEntity(Long parentId, String hierarchyName) {
        this.parentId = parentId;
        this.hierarchyName = hierarchyName;
    }

    public Long getWorkflowHierarchyId() {
        return workflowHierarchyId;
    }

    public void setWorkflowHierarchyId(Long workflowHierarchyId) {
        this.workflowHierarchyId = workflowHierarchyId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public Long getChildCount() {
        return childCount;
    }

    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

    public List<WorkflowEntity> getWorkflowCollection() {
        return workflowCollection;
    }

    public void setWorkflowCollection(List<WorkflowEntity> workflowCollection) {
        this.workflowCollection = workflowCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workflowHierarchyId != null ? workflowHierarchyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkflowHierarchyEntity)) {
            return false;
        }
        WorkflowHierarchyEntity other = (WorkflowHierarchyEntity) object;
        if ((this.workflowHierarchyId == null && other.workflowHierarchyId != null) || (this.workflowHierarchyId != null && !this.workflowHierarchyId.equals(other.workflowHierarchyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WorkflowHierarchyEntity{" + "workflowHierarchyId=" + workflowHierarchyId + ", parentId=" + parentId + ", hierarchyName=" + hierarchyName + '}';
    }

}
