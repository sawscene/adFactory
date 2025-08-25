/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバン階層情報(親子関係)
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "tre_kanban_hierarchy")
@XmlRootElement(name = "treeKanbanHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
        // 子孫階層のIDを名前から検索
        @NamedNativeQuery(name = "TreeKanbanHierarchyEntity.findDescendantsIdByName", query = "WITH RECURSIVE hierarchy AS( SELECT tkh.parent_id AS id FROM tre_kanban_hierarchy tkh JOIN mst_kanban_hierarchy mkh ON mkh.hierarchy_name=?1 AND tkh.parent_id = mkh.kanban_hierarchy_id UNION ALL SELECT tkh2.child_id AS id FROM tre_kanban_hierarchy tkh2, hierarchy WHERE tkh2.parent_id = hierarchy.id) SELECT hierarchy.* FROM hierarchy"),
})
@NamedQueries({
    // 親階層IDを指定して、子階層の件数を取得する。
    @NamedQuery(name = "TreeKanbanHierarchyEntity.countChild", query = "SELECT COUNT(t.childId) FROM TreeKanbanHierarchyEntity t WHERE t.parentId = :parentId"),
    // 親階層IDを指定して、カンバン階層情報一覧を取得する。
    @NamedQuery(name = "TreeKanbanHierarchyEntity.findByParentId", query = "SELECT t FROM TreeKanbanHierarchyEntity t WHERE t.parentId = :parentId"),
    // 子階層IDを指定して、カンバン階層情報一覧を取得する。
//    @NamedQuery(name = "TreeKanbanHierarchyEntity.findByChildId", query = "SELECT t FROM TreeKanbanHierarchyEntity t WHERE t.childId = :childId"),
    // 子階層IDを指定して、カンバン階層情報を削除する。
    @NamedQuery(name = "TreeKanbanHierarchyEntity.removeByChildId", query = "DELETE FROM TreeKanbanHierarchyEntity t WHERE t.childId = :childId"),
    // 子階層IDを指定して、カンバン階層情報の件数を取得する。
    @NamedQuery(name = "TreeKanbanHierarchyEntity.countByChildId", query = "SELECT COUNT(t.parentId) FROM TreeKanbanHierarchyEntity t WHERE t.childId = :childId"),
    // 子階層IDを指定して、親階層IDを更新する。
    @NamedQuery(name = "TreeKanbanHierarchyEntity.updateParentId", query = "UPDATE TreeKanbanHierarchyEntity t SET t.parentId = :parentId WHERE t.childId = :childId"),
    // 子階層IDを指定して、親階層IDを取得する。
    @NamedQuery(name = "TreeKanbanHierarchyEntity.findParentId", query = "SELECT t.parentId FROM TreeKanbanHierarchyEntity t WHERE t.childId = :childId"),
})
public class TreeKanbanHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "parent_id")
    private Long parentId;// 親階層ID

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "child_id")
    private Long childId;// 子階層ID

    /**
     * コンストラクタ
     */
    public TreeKanbanHierarchyEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param parentId 親階層ID
     * @param childId 子階層ID
     */
    public TreeKanbanHierarchyEntity(long parentId, long childId) {
        this.parentId = parentId;
        this.childId = childId;
    }

    /**
     * 親階層IDを取得する。
     *
     * @return 親階層ID
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * 親階層IDを設定する。
     *
     * @param parentId 親階層ID
     */
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    /**
     * 子階層IDを取得する。
     *
     * @return 子階層ID
     */
    public long getChildId() {
        return childId;
    }

    /**
     * 子階層IDを設定する。
     *
     * @param childId 子階層ID
     */
    public void setChildId(long childId) {
        this.childId = childId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (int) (this.parentId ^ (this.parentId >>> 32));
        hash = 47 * hash + (int) (this.childId ^ (this.childId >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TreeKanbanHierarchyEntity other = (TreeKanbanHierarchyEntity) obj;
        if (this.parentId != other.parentId) {
            return false;
        }
        if (this.childId != other.childId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("TreeKanbanHierarchyEntity{")
                .append("parentId=").append(this.parentId)
                .append(", ")
                .append("childId=").append(this.childId)
                .append("}")
                .toString();
    }
}
