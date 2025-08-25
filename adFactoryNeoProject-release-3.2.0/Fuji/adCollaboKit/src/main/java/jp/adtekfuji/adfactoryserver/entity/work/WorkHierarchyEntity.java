/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.work;

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
@Table(name = "mst_work_hierarchy")
@XmlRootElement(name = "workHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "WorkHierarchyEntity.checkAddByHierarchyName", query = "SELECT COUNT(w.workHierarchyId) FROM WorkHierarchyEntity w WHERE w.hierarchyName = :hierarchyName"),
    @NamedQuery(name = "WorkHierarchyEntity.checkUpdateByHierarchyName", query = "SELECT COUNT(w.workHierarchyId) FROM WorkHierarchyEntity w WHERE w.hierarchyName = :hierarchyName AND w.workHierarchyId != :workHierarchyId"),

    @NamedQuery(name = "WorkHierarchyEntity.findAll", query = "SELECT w FROM WorkHierarchyEntity w"),
    @NamedQuery(name = "WorkHierarchyEntity.findByWorkHierarchyId", query = "SELECT w FROM WorkHierarchyEntity w WHERE w.workHierarchyId = :workHierarchyId"),
    @NamedQuery(name = "WorkHierarchyEntity.findByHierarchyName", query = "SELECT w FROM WorkHierarchyEntity w WHERE w.hierarchyName = :hierarchyName"),
    // 親階層に属する階層を問い合わせる
    @NamedQuery(name = "WorkHierarchyEntity.findByParentId", query = "SELECT w FROM TreeWorkHierarchyEntity pk JOIN WorkHierarchyEntity w ON pk.treeWorkHierarchyEntityPK.childId = w.workHierarchyId WHERE pk.treeWorkHierarchyEntityPK.parentId = :parentId ORDER BY w.hierarchyName")})
public class WorkHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "work_hierarchy_id")
    private Long workHierarchyId;
    @Transient
    private Long parentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "hierarchy_name")
    private String hierarchyName;
    @Transient
    private Long childCount = 0L;
    @XmlElementWrapper(name = "works")
    @XmlElement(name = "work")
    @Transient
    private List<WorkEntity> workCollection = null;

    public WorkHierarchyEntity() {
    }

    public WorkHierarchyEntity(Long parentId, String hierarchyName) {
        this.parentId = parentId;
        this.hierarchyName = hierarchyName;
    }

    public Long getWorkHierarchyId() {
        return workHierarchyId;
    }

    public void setWorkHierarchyId(Long workHierarchyId) {
        this.workHierarchyId = workHierarchyId;
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

    public List<WorkEntity> getWorkCollection() {
        return workCollection;
    }

    public void setWorkCollection(List<WorkEntity> workCollection) {
        this.workCollection = workCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workHierarchyId != null ? workHierarchyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkHierarchyEntity)) {
            return false;
        }
        WorkHierarchyEntity other = (WorkHierarchyEntity) object;
        if ((this.workHierarchyId == null && other.workHierarchyId != null) || (this.workHierarchyId != null && !this.workHierarchyId.equals(other.workHierarchyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WorkHierarchyEntity{" + "workHierarchyId=" + workHierarchyId + ", hierarchyName=" + hierarchyName + '}';
    }

}
