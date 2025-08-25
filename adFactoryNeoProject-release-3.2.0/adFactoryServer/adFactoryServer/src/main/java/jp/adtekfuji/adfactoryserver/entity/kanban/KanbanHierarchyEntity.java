/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバン階層マスタ情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_kanban_hierarchy")
@XmlRootElement(name = "kanbanHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
    // 再帰クエリ(with-recursive)を使用して、子階層IDの一覧を取得する
    @NamedNativeQuery(name = "KanbanHierarchyEntity.findTreeChild",
            query = "WITH RECURSIVE child (DEPTH, child_id, parent_id, organization_id) AS (\n" +
                    "    SELECT 0,\n" +
                    "      parent.child_id,\n" +
                    "      parent.parent_id,\n" +
                    "      access.organization_id::INTEGER\n" +
                    "    FROM tre_kanban_hierarchy parent\n" +
                    "      LEFT JOIN trn_access_hierarchy access ON access.type_id = 4 AND access.hierarchy_id = parent.child_id\n" +
                    "    WHERE parent_id = ?1 AND (access.organization_id IS NULL OR access.organization_id = ANY (?2))\n" +
                    "  UNION ALL\n" +
                    "    SELECT\n" +
                    "      child.DEPTH + 1,\n" +
                    "      grand.child_id,\n" +
                    "      grand.parent_id,\n" +
                    "      access.organization_id::INTEGER\n" +
                    "    FROM child, tre_kanban_hierarchy grand\n" +
                    "      LEFT JOIN trn_access_hierarchy access ON access.type_id = 4 AND access.hierarchy_id = grand.child_id\n" +
                    "    WHERE grand.parent_id = child.child_id AND (access.organization_id IS NULL OR access.organization_id = ANY (?2)))\n" +
                    "SELECT child_id FROM child\n" +
                    "ORDER BY DEPTH;"),
})
@NamedQueries({
    // 階層名を指定して、件数を取得する。(追加時の階層名重複チェック)
    @NamedQuery(name = "KanbanHierarchyEntity.checkAddByHierarchyName", query = "SELECT COUNT(k.kanbanHierarchyId) FROM KanbanHierarchyEntity k WHERE k.hierarchyName = :hierarchyName"),
    // 階層名・階層IDを指定して、件数を取得する。(更新時の階層名重複チェック)
    @NamedQuery(name = "KanbanHierarchyEntity.checkUpdateByHierarchyName", query = "SELECT COUNT(k.kanbanHierarchyId) FROM KanbanHierarchyEntity k WHERE k.hierarchyName = :hierarchyName AND k.kanbanHierarchyId != :kanbanHierarchyId"),
    // カンバン階層IDから情報を取得する
    @NamedQuery(name = "KanbanHierarchyEntity.findByHierarchyId", query = "SELECT k FROM KanbanHierarchyEntity k WHERE k.kanbanHierarchyId IN :hierarchId"),

    // 階層名を指定して、階層情報を取得する。
    @NamedQuery(name = "KanbanHierarchyEntity.findByHierarchyName", query = "SELECT k FROM KanbanHierarchyEntity k WHERE k.hierarchyName = :hierarchyName"),
    // 階層IDを指定して、子階層のカンバン階層マスタ情報一覧を取得する。
    @NamedQuery(name = "KanbanHierarchyEntity.findByParentId", query = "SELECT k FROM TreeKanbanHierarchyEntity p JOIN KanbanHierarchyEntity k ON p.childId = k.kanbanHierarchyId WHERE p.parentId = :parentId ORDER BY k.hierarchyName"),

    // 階層IDを指定して、子階層の階層情報の件数を取得する。
    @NamedQuery(name = "KanbanHierarchyEntity.countChild", query = "SELECT COUNT(DISTINCT(k)) FROM KanbanHierarchyEntity k LEFT JOIN TreeKanbanHierarchyEntity t ON t.childId = k.kanbanHierarchyId WHERE t.parentId = :hierarchyId"),
    // 階層IDを指定して、子階層の階層情報一覧を取得する。
    @NamedQuery(name = "KanbanHierarchyEntity.findChild", query = "SELECT DISTINCT(k) FROM KanbanHierarchyEntity k LEFT JOIN TreeKanbanHierarchyEntity t ON t.childId = k.kanbanHierarchyId WHERE t.parentId = :hierarchyId ORDER BY k.hierarchyName, k.kanbanHierarchyId"),
    // 階層ID・ユーザーID(組織ID)一覧を指定して、アクセス可能な子階層の階層情報の件数を取得する。
    @NamedQuery(name = "KanbanHierarchyEntity.countChildByUserId", query = "SELECT COUNT(DISTINCT(k)) FROM KanbanHierarchyEntity k LEFT JOIN TreeKanbanHierarchyEntity t ON t.childId = k.kanbanHierarchyId LEFT JOIN AccessHierarchyEntity a ON a.typeId = jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum.KanbanHierarchy AND a.hierarchyId = k.kanbanHierarchyId WHERE t.parentId = :hierarchyId AND (a.organizationId IS NULL OR a.organizationId IN :ancestors)"),
    // 階層ID・ユーザーID(組織ID)一覧を指定して、アクセス可能な子階層の階層情報一覧を取得する。
    @NamedQuery(name = "KanbanHierarchyEntity.findChildByUserId", query = "SELECT DISTINCT(k) FROM KanbanHierarchyEntity k LEFT JOIN TreeKanbanHierarchyEntity t ON t.childId = k.kanbanHierarchyId LEFT JOIN AccessHierarchyEntity a ON a.typeId = jp.adtekfuji.adFactory.enumerate.AccessHierarchyTypeEnum.KanbanHierarchy AND a.hierarchyId = k.kanbanHierarchyId WHERE t.parentId = :hierarchyId AND (a.organizationId IS NULL OR a.organizationId IN :ancestors) ORDER BY k.hierarchyName, k.kanbanHierarchyId"),
})
public class KanbanHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "kanban_hierarchy_id")
    private Long kanbanHierarchyId;// カンバン階層ID

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "hierarchy_name")
    private String hierarchyName;// 階層名

    @Column(name = "partition_flag")
    private Boolean partitionFlag = false;// 完了カンバン自動移動フラグ

    @Transient
    private Long parentId;// 親階層ID

    @Transient
    private Long childCount = 0L;// 子階層数

    @XmlElementWrapper(name = "kanbans")
    @XmlElement(name = "kanban")
    @Transient
    private List<KanbanEntity> kanbanCollection = null;// カンバン情報一覧

    /**
     * コンストラクタ
     */
    public KanbanHierarchyEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param parentId 親階層ID
     * @param hierarchyName 階層名
     */
    public KanbanHierarchyEntity(Long parentId, String hierarchyName) {
        this.parentId = parentId;
        this.hierarchyName = hierarchyName;
    }

    /**
     * カンバン階層IDを取得する。
     *
     * @return カンバン階層ID
     */
    public Long getKanbanHierarchyId() {
        return this.kanbanHierarchyId;
    }

    /**
     * カンバン階層IDを設定する。
     *
     * @param kanbanHierarchyId カンバン階層ID
     */
    public void setKanbanHierarchyId(Long kanbanHierarchyId) {
        this.kanbanHierarchyId = kanbanHierarchyId;
    }

    /**
     * 階層名を取得する。
     *
     * @return 階層名
     */
    public String getHierarchyName() {
        return this.hierarchyName;
    }

    /**
     * 階層名を設定する。
     *
     * @param hierarchyName 階層名
     */
    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
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

    /**
     * 親階層IDを取得する。
     *
     * @return 親階層ID
     */
    public Long getParentId() {
        return this.parentId;
    }

    /**
     * 親階層IDを設定する。
     *
     * @param parentId 親階層ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 子階層数を取得する。
     *
     * @return 子階層数
     */
    public Long getChildCount() {
        return this.childCount;
    }

    /**
     * 子階層数を設定する。
     *
     * @param childCount 子階層数
     */
    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

    /**
     * カンバン情報一覧を取得する。
     *
     * @return カンバン情報一覧
     */
    public List<KanbanEntity> getKanbanCollection() {
        return this.kanbanCollection;
    }

    /**
     * カンバン情報一覧を設定する。
     *
     * @param kanbanCollection カンバン情報一覧
     */
    public void setKanbanCollection(List<KanbanEntity> kanbanCollection) {
        this.kanbanCollection = kanbanCollection;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.kanbanHierarchyId);
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
        final KanbanHierarchyEntity other = (KanbanHierarchyEntity) obj;
        if (!Objects.equals(this.kanbanHierarchyId, other.kanbanHierarchyId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanHierarchyEntity{")
                .append("kanbanHierarchyId=").append(this.kanbanHierarchyId)
                .append(", ")
                .append("hierarchyName=").append(this.hierarchyName)
                .append(", ")
                .append("partitionFlag=").append(this.partitionFlag)
                .append("}")
                .toString();
    }
}
