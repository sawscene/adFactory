/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 工程カンバン作業中情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "trn_work_kanban_working")
@XmlRootElement
@NamedQueries({
    // 工程カンバンIDを指定して、工程カンバン作業中情報一覧を取得する。
    @NamedQuery(name = "WorkKanbanWorkingEntity.findByWorkKanbanId", query = "SELECT w FROM WorkKanbanWorkingEntity w WHERE w.workKanbanId = :workKanbanId"),
    // 工程カンバンIDを指定して、工程カンバン作業中情報の件数を取得する。
    @NamedQuery(name = "WorkKanbanWorkingEntity.countByWorkKanbanId", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanWorkingEntity w WHERE w.workKanbanId = :workKanbanId"),
    // 工程カンバンIDを指定して、応援者の人数を取得する
    @NamedQuery(name="WorkKanbanWorkingEntity.countSupporterByWorkKanbanID", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanWorkingEntity w WHERE w.workKanbanId = :workKanbanId AND w.supporterFlag = :supporterFlag"),
    // 設備IDを指定して、
    @NamedQuery(name = "WorkKanbanWorkingEntity.find", query = "SELECT w FROM WorkKanbanWorkingEntity w WHERE w.workKanbanId = :workKanbanId AND w.equipmentId = :equipmentId"),
    // 組織IDを指定して、
    @NamedQuery(name = "WorkKanbanWorkingEntity.findByOrganizationId", query = "SELECT w FROM WorkKanbanWorkingEntity w WHERE w.workKanbanId = :workKanbanId AND w.organizationId = :organizationId"),
    // 工程カンバンIDを指定して、工程カンバン作業中情報を削除する。
    @NamedQuery(name = "WorkKanbanWorkingEntity.removeByWorkKanbanId", query = "DELETE FROM WorkKanbanWorkingEntity w WHERE w.workKanbanId = :workKanbanId"),
    // 工程カンバンID一覧を指定して、工程カンバン作業中情報を削除する。
    @NamedQuery(name = "WorkKanbanWorkingEntity.removeByWorkKanbanIds", query = "DELETE FROM WorkKanbanWorkingEntity w WHERE w.workKanbanId IN :workKanbanIds"),
})
public class WorkKanbanWorkingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "work_kanban_working_id")
    private Long workKanbanWorkingId;// 工程カンバン作業中ID

    @Column(name = "work_kanban_id")
    private Long workKanbanId;// 工程カンバンID

    @Column(name = "equipment_id")
    private Long equipmentId;// 設備ID

    @Column(name = "organization_id")
    private Long organizationId;// 組織ID

    @Column(name = "supporter_flag")
    private Boolean supporterFlag; // 応援者フラグ

    /**
     * コンストラクタ
     */
    public WorkKanbanWorkingEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workKanbanId 工程カンバンID
     * @param equipmentId 設備ID
     * @param organizationId 組織ID
     */
    public WorkKanbanWorkingEntity(Long workKanbanId, Long equipmentId, Long organizationId, Boolean supporterFlag) {
        this.workKanbanId = workKanbanId;
        this.equipmentId = equipmentId;
        this.organizationId = organizationId;
        this.supporterFlag = supporterFlag;
    }

    /**
     * 工程カンバン作業中IDを取得する。
     *
     * @return 工程カンバン作業中ID
     */
    public Long getWorkKanbanWorkingId() {
        return workKanbanWorkingId;
    }

    /**
     * 工程カンバン作業中IDを設定する。
     *
     * @param workKanbanWorkingId 工程カンバン作業中ID
     */
    public void setWorkKanbanWorkingId(Long workKanbanWorkingId) {
        this.workKanbanWorkingId = workKanbanWorkingId;
    }

    /**
     * 工程カンバンIDを取得する。
     *
     * @return 工程カンバンID
     */
    public Long getWorkKanbanId() {
        return this.workKanbanId;
    }

    /**
     * 工程カンバンIDを設定する。
     *
     * @param workKanbanId 工程カンバンID
     */
    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    /**
     * 設備IDを取得する。
     *
     * @return 設備ID
     */
    public Long getEquipmentId() {
        return this.equipmentId;
    }

    /**
     * 設備IDを設定する。
     *
     * @param equipmentId 設備ID
     */
    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getOrganizationId() {
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 応援者フラグを取得する。
     * @return 応援者フラグ
     */
    public Boolean getSupporterFlag(){
        return supporterFlag;
    }

    /**
     * 応援者フラグを設定する
     * @param supportFlag 応援者フラグ
     */
    public void setSupporterFlag(Boolean supporterFlag) {
        this.supporterFlag=supporterFlag;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.workKanbanWorkingId);
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
        final WorkKanbanWorkingEntity other = (WorkKanbanWorkingEntity) obj;
        if (!Objects.equals(this.workKanbanWorkingId, other.workKanbanWorkingId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkKanbanWorkingEntity{")
                .append("workKanbanWorkingId=").append(this.workKanbanWorkingId)
                .append(", ")
                .append("workKanbanId=").append(this.workKanbanId)
                .append(", ")
                .append("equipmentId=").append(this.equipmentId)
                .append(", ")
                .append("organizationId=").append(this.organizationId)
                .append(", ")
                .append("supportFlag=").append(this.supporterFlag)
                .append("}")
                .toString();
    }
}
