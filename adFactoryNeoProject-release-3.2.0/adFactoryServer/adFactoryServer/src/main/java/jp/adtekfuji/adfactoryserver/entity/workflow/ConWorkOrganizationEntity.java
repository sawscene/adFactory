/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

import java.io.Serializable;
import java.util.Objects;
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
import jp.adtekfuji.adFactory.enumerate.WorkKbnEnum;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "con_work_organization")
@XmlRootElement(name = "conWorkOrganization")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 工程区分・工程順ID・工程IDを指定して、工程・組織関連付け情報の設備ID一覧を取得する。
    @NamedQuery(name = "ConWorkOrganizationEntity.findOrganizationId", query = "SELECT c.organizationId FROM ConWorkOrganizationEntity c WHERE c.workKbn = :workKbn AND c.workflowId = :workflowId AND c.workId = :workId ORDER BY c.organizationId"),
    // 工程順IDを指定して、工程・組織関連付け情報を削除する。
    @NamedQuery(name = "ConWorkOrganizationEntity.removeByWorkflowId", query = "DELETE FROM ConWorkOrganizationEntity c WHERE c.workflowId = :workflowId"),

    // 組織IDを指定して、工程・組織関連付け情報を削除する。
    @NamedQuery(name = "ConWorkOrganizationEntity.removeByOrganizationId", query = "DELETE FROM ConWorkOrganizationEntity c WHERE c.organizationId = :organizationId"),
    // 組織IDを指定して、工程・組織関連付け情報の件数を取得する。
    @NamedQuery(name = "ConWorkOrganizationEntity.countByOrganizationId", query = "SELECT COUNT(c.workId) FROM ConWorkOrganizationEntity c WHERE c.organizationId = :organizationId"),

    // 工程順ID・工程IDと新しい工程IDを指定して、工程・組織関連付け情報の工程IDを更新する。
    @NamedQuery(name = "ConWorkOrganizationEntity.updateWorkId", query = "UPDATE ConWorkOrganizationEntity c SET c.workId = :newWorkId WHERE c.workflowId = :workflowId AND c.workId = :oldWorkId"),
})
public class ConWorkOrganizationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_kbn")
    private WorkKbnEnum workKbn;// 工程区分

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "workflow_id")
    private long workflowId;// 工程順ID

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_id")
    private long workId;// 工程ID

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "organization_id")
    private long organizationId;// 組織ID

    /**
     * コンストラクタ
     */
    public ConWorkOrganizationEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workKbn 工程区分
     * @param workflowId 工程順ID
     * @param workId 工程ID
     * @param organizationId 組織ID
     */
    public ConWorkOrganizationEntity(WorkKbnEnum workKbn, long workflowId, long workId, long organizationId) {
        this.workKbn = workKbn;
        this.workflowId = workflowId;
        this.workId = workId;
        this.organizationId = organizationId;
    }

    /**
     * 工程区分を取得する。
     *
     * @return 工程区分
     */
    public WorkKbnEnum getWorkKbn() {
        return this.workKbn;
    }

    /**
     * 工程区分を設定する。
     *
     * @param workKbn 工程区分
     */
    public void setWorkKbn(WorkKbnEnum workKbn) {
        this.workKbn = workKbn;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public long getWorkflowId() {
        return this.workflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(long workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public long getWorkId() {
        return this.workId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param workId 工程ID
     */
    public void setWorkId(long workId) {
        this.workId = workId;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public long getOrganizationId() {
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.workKbn);
        hash = 71 * hash + (int) (this.workflowId ^ (this.workflowId >>> 32));
        hash = 71 * hash + (int) (this.workId ^ (this.workId >>> 32));
        hash = 71 * hash + (int) (this.organizationId ^ (this.organizationId >>> 32));
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
        final ConWorkOrganizationEntity other = (ConWorkOrganizationEntity) obj;
        if (this.workKbn != other.workKbn) {
            return false;
        }
        if (this.workflowId != other.workflowId) {
            return false;
        }
        if (this.workId != other.workId) {
            return false;
        }
        if (this.organizationId != other.organizationId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ConWorkOrganizationEntity{")
                .append("workKbn=").append(this.workKbn)
                .append(", workflowId=").append(this.workflowId)
                .append(", organizationId=").append(this.organizationId)
                .append("}")
                .toString();
    }
}
