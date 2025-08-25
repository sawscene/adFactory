/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.master;

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
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.HierarchyTypeEnum;

/**
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "con_hierarchy")
@XmlRootElement(name = "conHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 階層IDを指定して、階層関連付け情報の件数を取得する。
    @NamedQuery(name = "ConHierarchyEntity.countChild", query = "SELECT COUNT(c.workWorkflowId) FROM ConHierarchyEntity c WHERE c.hierarchyId =:hierarchyId"),
    // 階層種別と工程・工程順IDを指定して、階層関連付け情報を取得する。
    @NamedQuery(name = "ConHierarchyEntity.findByTypeAndMstId", query = "SELECT c FROM ConHierarchyEntity c WHERE c.hierarchyType = :hierarchyType AND c.workWorkflowId = :workWorkflowId"),
    // 階層種別と工程・工程順IDを指定して、階層関連付け情報を削除する。
    @NamedQuery(name = "ConHierarchyEntity.removeByTypeAndMstId", query = "DELETE FROM ConHierarchyEntity c WHERE c.hierarchyType = :hierarchyType AND c.workWorkflowId = :workWorkflowId"),
    // 階層種別と工程・工程順IDを指定して、階層関連付け情報の件数を取得する。
    @NamedQuery(name = "ConHierarchyEntity.countByTypeAndMstId", query = "SELECT COUNT(c.hierarchyId) FROM ConHierarchyEntity c WHERE c.hierarchyType = :hierarchyType AND c.workWorkflowId = :workWorkflowId"),
    // 階層種別と工程・工程順IDを指定して、階層IDを更新する。
    @NamedQuery(name = "ConHierarchyEntity.updateHierarchyId", query = "UPDATE ConHierarchyEntity c SET c.hierarchyId = :hierarchyId WHERE c.hierarchyType = :hierarchyType AND c.workWorkflowId = :workWorkflowId"),
    // 階層種別と工程・工程順IDを指定して、階層IDを取得する。
    @NamedQuery(name = "ConHierarchyEntity.findHierarchyId", query = "SELECT c.hierarchyId FROM ConHierarchyEntity c WHERE c.hierarchyType = :hierarchyType AND c.workWorkflowId = :workWorkflowId"),
})
public class ConHierarchyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "hierarchy_id")
    private long hierarchyId;// 階層ID

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_workflow_id")
    private long workWorkflowId;// 工程・工程順ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "hierarchy_type")
    private HierarchyTypeEnum hierarchyType;// 階層種別

    /**
     * コンストラクタ
     */
    public ConHierarchyEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param hierarchyId 階層ID
     * @param workWorkflowId 工程・工程順ID
     * @param hierarchyType 階層種別
     */
    public ConHierarchyEntity(long hierarchyId, long workWorkflowId, HierarchyTypeEnum hierarchyType) {
        this.hierarchyId = hierarchyId;
        this.workWorkflowId = workWorkflowId;
        this.hierarchyType = hierarchyType;
    }

    /**
     * 階層IDを取得する。
     *
     * @return 階層ID
     */
    public long getHierarchyId() {
        return this.hierarchyId;
    }

    /**
     * 階層IDを設定する。
     *
     * @param hierarchyId 階層ID
     */
    public void setHierarchyId(long hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    /**
     * 工程・工程順IDを取得する。
     *
     * @return 工程・工程順ID
     */
    public long getWorkWorkflowId() {
        return this.workWorkflowId;
    }

    /**
     * 工程・工程順IDを設定する。
     *
     * @param workWorkflowId 工程・工程順ID
     */
    public void setWorkWorkflowId(long workWorkflowId) {
        this.workWorkflowId = workWorkflowId;
    }

    /**
     * 階層種別を取得する。
     *
     * @return 階層種別
     */
    public HierarchyTypeEnum getHierarchyType() {
        return this.hierarchyType;
    }

    /**
     * 階層種別を設定する。
     *
     * @param hierarchyType 階層種別
     */
    public void setHierarchyType(HierarchyTypeEnum hierarchyType) {
        this.hierarchyType = hierarchyType;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (int) (this.hierarchyId ^ (this.hierarchyId >>> 32));
        hash = 31 * hash + (int) (this.workWorkflowId ^ (this.workWorkflowId >>> 32));
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
        final ConHierarchyEntity other = (ConHierarchyEntity) obj;
        if (this.hierarchyId != other.hierarchyId) {
            return false;
        }
        if (this.workWorkflowId != other.workWorkflowId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ConHierarchyEntity{")
                .append("hierarchyId=").append(this.hierarchyId)
                .append(", ")
                .append("workWorkflowId=").append(this.workWorkflowId)
                .append(", ")
                .append("hierarchyType=").append(this.hierarchyType)
                .append("}")
                .toString();
    }
}
