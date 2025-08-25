/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバン階層関連付け情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "con_kanban_hierarchy")
@XmlRootElement(name = "conKanbanHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 階層IDを指定して、カンバン階層関連付け情報の件数を取得する。
    @NamedQuery(name = "ConKanbanHierarchyEntity.countChild", query = "SELECT COUNT(c.kanbanId) FROM ConKanbanHierarchyEntity c WHERE c.kanbanHierarchyId = :kanbanHierarchyId"),
    // カンバンIDを指定して、カンバン階層関連付け情報を取得する。
    @NamedQuery(name = "ConKanbanHierarchyEntity.findByKanbanId", query = "SELECT c FROM ConKanbanHierarchyEntity c WHERE c.kanbanId = :kanbanId"),
    // カンバンIDを指定して、カンバン階層関連付け情報を削除する。
    @NamedQuery(name = "ConKanbanHierarchyEntity.removeByKanbanId", query = "DELETE FROM ConKanbanHierarchyEntity c WHERE c.kanbanId = :kanbanId"),
    // カンバンIDを指定して、カンバン階層関連付け情報の件数を取得する。
    @NamedQuery(name = "ConKanbanHierarchyEntity.countByKanbanId", query = "SELECT COUNT(c.kanbanHierarchyId) FROM ConKanbanHierarchyEntity c WHERE c.kanbanId = :kanbanId"),
    // カンバンIDを指定して、カンバン階層IDを更新する。
    @NamedQuery(name = "ConKanbanHierarchyEntity.updateHierarchyId", query = "UPDATE ConKanbanHierarchyEntity c SET c.kanbanHierarchyId = :hierarchyId WHERE c.kanbanId = :kanbanId"),
    // カンバンIDを指定して、カンバン階層IDを取得する。
    @NamedQuery(name = "ConKanbanHierarchyEntity.findHierarchyId", query = "SELECT c.kanbanHierarchyId FROM ConKanbanHierarchyEntity c WHERE c.kanbanId = :kanbanId"),

    @NamedQuery(name = "ConKanbanHierarchyEntity.findByKanbanIds", query = "SELECT c FROM ConKanbanHierarchyEntity c WHERE c.kanbanId IN :kanbanIds"),
})
public class ConKanbanHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "kanban_hierarchy_id")
    @XmlElement(name = "fkKanbanHierarchyId")
    private Long kanbanHierarchyId;// カンバン階層ID

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "kanban_id")
    @XmlElement(name = "fkKanbanId")
    private Long kanbanId;// カンバンID

    /**
     * コンストラクタ
     */
    public ConKanbanHierarchyEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param kanbanHierarchyId カンバン階層ID
     * @param kanbanId カンバンID
     */
    public ConKanbanHierarchyEntity(long kanbanHierarchyId, long kanbanId) {
        this.kanbanHierarchyId = kanbanHierarchyId;
        this.kanbanId = kanbanId;
    }

    /**
     * カンバン階層IDを取得する。
     *
     * @return カンバン階層ID
     */
    public long getKanbanHierarchyId() {
        return kanbanHierarchyId;
    }

    /**
     * カンバン階層IDを設定する。
     *
     * @param kanbanHierarchyId カンバン階層ID
     */
    public void setKanbanHierarchyId(long kanbanHierarchyId) {
        this.kanbanHierarchyId = kanbanHierarchyId;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public long getKanbanId() {
        return kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(long kanbanId) {
        this.kanbanId = kanbanId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (int) (this.kanbanHierarchyId ^ (this.kanbanHierarchyId >>> 32));
        hash = 19 * hash + (int) (this.kanbanId ^ (this.kanbanId >>> 32));
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
        final ConKanbanHierarchyEntity other = (ConKanbanHierarchyEntity) obj;
        if (this.kanbanHierarchyId != other.kanbanHierarchyId) {
            return false;
        }
        if (this.kanbanId != other.kanbanId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ConKanbanHierarchyEntity{")
                .append("kanbanHierarchyId=").append(this.kanbanHierarchyId)
                .append(", ")
                .append("kanbanId=").append(this.kanbanId)
                .append("}")
                .toString();
    }
}
