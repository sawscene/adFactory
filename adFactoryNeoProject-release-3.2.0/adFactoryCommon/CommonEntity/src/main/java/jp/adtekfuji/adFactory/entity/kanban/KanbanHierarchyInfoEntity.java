/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバン階層情報
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "kanbanHierarchy")
public class KanbanHierarchyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty kanbanHierarchyIddProperty;
    private LongProperty parentIdProperty;
    private StringProperty hierarchyNameProperty;
    private BooleanProperty partitionFlagProperty;

    @XmlElement(required = true)
    private Long kanbanHierarchyId;
    @XmlElement()
    private Long parentId;
    @XmlElement()
    private String hierarchyName;
    @XmlElement()
    private Long childCount;
    @XmlElementWrapper(name = "kanbans")
    @XmlElement(name = "kanban")
    private List<KanbanInfoEntity> kanbanCollection;

    // 完了カンバン自動移動フラグ
    private Boolean partitionFlag = false;

    public KanbanHierarchyInfoEntity() {
    }

    public KanbanHierarchyInfoEntity(Long kanbanHierarchyId, String hierarchyName) {
        this.kanbanHierarchyId = kanbanHierarchyId;
        this.hierarchyName = hierarchyName;
    }

    public LongProperty kanbanHierarchyIdProperty() {
        if (Objects.isNull(kanbanHierarchyIddProperty)) {
            kanbanHierarchyIddProperty = new SimpleLongProperty(kanbanHierarchyId);
        }
        return kanbanHierarchyIddProperty;
    }

    public LongProperty parentIdProperty() {
        if (Objects.isNull(parentIdProperty)) {
            parentIdProperty = new SimpleLongProperty(parentId);
        }
        return parentIdProperty;
    }

    public StringProperty hierarchyNameProperty() {
        if (Objects.isNull(hierarchyNameProperty)) {
            hierarchyNameProperty = new SimpleStringProperty(hierarchyName);
        }
        return hierarchyNameProperty;
    }

    /**
     * 完了カンバン自動移動フラグプロパティを取得する。
     *
     * @return 完了カンバン自動移動フラグ
     */
    public BooleanProperty partitionFlagProperty() {
        if (Objects.isNull(partitionFlagProperty)) {
            partitionFlagProperty = new SimpleBooleanProperty(partitionFlag);
        }
        return partitionFlagProperty;
    }

    public Long getKanbanHierarchyId() {
        if (Objects.nonNull(kanbanHierarchyIddProperty)) {
            return kanbanHierarchyIddProperty.get();
        }
        return kanbanHierarchyId;
    }

    public void setKanbanHierarchyId(Long kanbanHierarchyId) {
        if (Objects.nonNull(kanbanHierarchyIddProperty)) {
            kanbanHierarchyIddProperty.set(kanbanHierarchyId);
        } else {
            this.kanbanHierarchyId = kanbanHierarchyId;
        }
    }

    public Long getParentId() {
        if (Objects.nonNull(parentIdProperty)) {
            return parentIdProperty.get();
        }
        return parentId;
    }

    public void setParentId(Long parentId) {
        if (Objects.nonNull(parentIdProperty)) {
            parentIdProperty.set(parentId);
        } else {
            this.parentId = parentId;
        }
    }

    public String getHierarchyName() {
        if (Objects.nonNull(hierarchyNameProperty)) {
            return hierarchyNameProperty.get();
        }
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        if (Objects.nonNull(hierarchyNameProperty)) {
            hierarchyNameProperty.set(hierarchyName);
        } else {
            this.hierarchyName = hierarchyName;
        }
    }

    public Long getChildCount() {
        return childCount;
    }

    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

    public List<KanbanInfoEntity> getKanbanCollection() {
        return kanbanCollection;
    }

    public void setKanbanCollection(List<KanbanInfoEntity> kanbanCollection) {
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
        hash = 83 * hash + Objects.hashCode(this.hierarchyName);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KanbanHierarchyInfoEntity)) {
            return false;
        }
        KanbanHierarchyInfoEntity other = (KanbanHierarchyInfoEntity) object;
        if ((this.kanbanHierarchyId == null && other.kanbanHierarchyId != null) || (this.kanbanHierarchyId != null && !this.kanbanHierarchyId.equals(other.kanbanHierarchyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KanbanHierarchyInfoEntity{" + "kanbanHierarchyId=" + kanbanHierarchyId + ", hierarchyName=" + hierarchyName + ", partitionFlag=" + partitionFlag + '}';
    }
}
