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
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 工程カンバン・組織関連付け情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "con_workkanban_organization")
@XmlRootElement(name = "conWorkkanbanOrganization")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 工程カンバンIDを指定して、関連付けられた組織ID一覧を取得する。
    @NamedQuery(name = "ConWorkkanbanOrganizationEntity.findOrganizationIdByWorkKanbanId", query = "SELECT c.organizationId FROM ConWorkkanbanOrganizationEntity c WHERE c.workKanbanId = :workKanbanId GROUP BY c.organizationId"),
    // 工程カンバンIDを指定して、工程カンバン・組織関連付け情報を削除する。
    @NamedQuery(name = "ConWorkkanbanOrganizationEntity.removeByWorkKanbanId", query = "DELETE FROM ConWorkkanbanOrganizationEntity c WHERE c.workKanbanId = :workKanbanId"),
    // 工程カンバンID一覧を指定して、関連付けられた組織ID一覧を取得する。
    @NamedQuery(name = "ConWorkkanbanOrganizationEntity.findOrganizationId", query = "SELECT c.organizationId FROM ConWorkkanbanOrganizationEntity c WHERE c.workKanbanId IN :workKanbanIds GROUP BY c.organizationId"),
    // 組織IDを指定して、工程カンバン・組織関連付け情報の件数を取得する。
    @NamedQuery(name = "ConWorkkanbanOrganizationEntity.countByOrganizationId", query = "SELECT COUNT(c.workKanbanId) FROM ConWorkkanbanOrganizationEntity c WHERE c.organizationId = :organizationId"),
})
public class ConWorkkanbanOrganizationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "workkanban_id")
    private long workKanbanId;// 工程カンバンID

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "organization_id")
    private long organizationId;// 組織ID

    /**
     * コンストラクタ
     */
    public ConWorkkanbanOrganizationEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workKanbanId 工程カンバンID
     * @param organizationId 組織ID
     */
    public ConWorkkanbanOrganizationEntity(long workKanbanId, long organizationId) {
        this.workKanbanId = workKanbanId;
        this.organizationId = organizationId;
    }

    /**
     * 工程カンバンIDを取得する。
     *
     * @return 工程カンバンID
     */
    public long getWorkKanbanId() {
        return this.workKanbanId;
    }

    /**
     * 工程カンバンIDを設定する。
     *
     * @param workKanbanId 工程カンバンID
     */
    public void setWorkKanbanId(long workKanbanId) {
        this.workKanbanId = workKanbanId;
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
        int hash = 3;
        hash = 97 * hash + (int) (this.workKanbanId ^ (this.workKanbanId >>> 32));
        hash = 97 * hash + (int) (this.organizationId ^ (this.organizationId >>> 32));
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
        final ConWorkkanbanOrganizationEntity other = (ConWorkkanbanOrganizationEntity) obj;
        if (this.workKanbanId != other.workKanbanId) {
            return false;
        }
        if (this.organizationId != other.organizationId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ConWorkkanbanOrganizationEntity{")
                .append("workKanbanId=").append(this.workKanbanId)
                .append(", ")
                .append("organizationId=").append(this.organizationId)
                .append("}")
                .toString();
    }
}
