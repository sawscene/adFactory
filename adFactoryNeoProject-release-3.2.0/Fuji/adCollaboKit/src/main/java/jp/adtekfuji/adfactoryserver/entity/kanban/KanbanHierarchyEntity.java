/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
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
@Table(name = "mst_kanban_hierarchy")
@XmlRootElement(name = "kanbanHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "KanbanHierarchyEntity.checkAddByHierarchyName", query = "SELECT COUNT(k.kanbanHierarchyId) FROM KanbanHierarchyEntity k WHERE k.hierarchyName = :hierarchyName"),
    @NamedQuery(name = "KanbanHierarchyEntity.checkUpdateByHierarchyName", query = "SELECT COUNT(k.kanbanHierarchyId) FROM KanbanHierarchyEntity k WHERE k.hierarchyName = :hierarchyName AND k.kanbanHierarchyId != :kanbanHierarchyId"),

    @NamedQuery(name = "KanbanHierarchyEntity.findAll", query = "SELECT k FROM KanbanHierarchyEntity k"),
    @NamedQuery(name = "KanbanHierarchyEntity.findByKanbanHierarchyId", query = "SELECT k FROM KanbanHierarchyEntity k WHERE k.kanbanHierarchyId = :kanbanHierarchyId"),
    @NamedQuery(name = "KanbanHierarchyEntity.findByHierarchyName", query = "SELECT k FROM KanbanHierarchyEntity k WHERE k.hierarchyName = :hierarchyName"),
    // 親階層に属する階層を問い合わせる
    @NamedQuery(name = "KanbanHierarchyEntity.findByParentId", query = "SELECT k FROM TreeKanbanHierarchyEntity p JOIN KanbanHierarchyEntity k ON p.treeKanbanHierarchyEntityPK.childId = k.kanbanHierarchyId WHERE p.treeKanbanHierarchyEntityPK.parentId = :parentId ORDER BY k.hierarchyName")})
public class KanbanHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "kanban_hierarchy_id")
    private Long kanbanHierarchyId;
    @Transient
    private Long parentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "hierarchy_name")
    private String hierarchyName;
    @Transient
    private Long childCount = 0L;
    @XmlElementWrapper(name = "kanbans")
    @XmlElement(name = "kanban")
    @Transient
    private List<KanbanEntity> kanbanCollection = null;

    // 完了カンバン自動移動フラグ
    @Column(name = "partition_flag")
    private Boolean partitionFlag = false;

    public KanbanHierarchyEntity() {
    }

    public KanbanHierarchyEntity(Long parentId, String hierarchyName) {
        this.parentId = parentId;
        this.hierarchyName = hierarchyName;
    }

    public Long getKanbanHierarchyId() {
        return kanbanHierarchyId;
    }

    public void setKanbanHierarchyId(Long kanbanHierarchyId) {
        this.kanbanHierarchyId = kanbanHierarchyId;
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

    public List<KanbanEntity> getKanbanCollection() {
        return kanbanCollection;
    }

    public void setKanbanCollection(List<KanbanEntity> kanbanCollection) {
        this.kanbanCollection = kanbanCollection;
    }

    /**
     * 完了カンバン自動移動フラグを取得する。
     *
     * @return 完了カンバン自動移動フラグ
     */
    public boolean getPartitionFlag() {
        return Objects.nonNull(this.partitionFlag) ? this.partitionFlag : Boolean.FALSE;
    }

    /**
     * 完了カンバン自動移動フラグを設定する。
     *
     * @param partitionFlag 完了カンバン自動移動フラグ
     */
    public void setPartitionFlag(boolean partitionFlag) {
        this.partitionFlag = partitionFlag;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kanbanHierarchyId != null ? kanbanHierarchyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KanbanHierarchyEntity)) {
            return false;
        }
        KanbanHierarchyEntity other = (KanbanHierarchyEntity) object;
        if ((this.kanbanHierarchyId == null && other.kanbanHierarchyId != null) || (this.kanbanHierarchyId != null && !this.kanbanHierarchyId.equals(other.kanbanHierarchyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KanbanHierarchyEntity{" + "kanbanHierarchyId=" + kanbanHierarchyId + ", hierarchyName=" + hierarchyName + ", partitionFlag=" + partitionFlag + '}';
    }
}
