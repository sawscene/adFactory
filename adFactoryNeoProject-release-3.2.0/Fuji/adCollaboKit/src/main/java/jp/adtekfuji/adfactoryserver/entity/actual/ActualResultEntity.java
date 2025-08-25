/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "trn_actual_result")
@XmlRootElement(name = "actualResult")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ActualResultEntity.findAll", query = "SELECT a FROM ActualResultEntity a"),
    @NamedQuery(name = "ActualResultEntity.findByActualId", query = "SELECT a FROM ActualResultEntity a WHERE a.actualId = :actualId"),
    @NamedQuery(name = "ActualResultEntity.findByFkKanbanId", query = "SELECT a FROM ActualResultEntity a WHERE a.fkKanbanId = :fkKanbanId"),
    @NamedQuery(name = "ActualResultEntity.findByFkWorkKanbanId", query = "SELECT a FROM ActualResultEntity a WHERE a.fkWorkKanbanId = :fkWorkKanbanId"),
    @NamedQuery(name = "ActualResultEntity.findByImplementDatetime", query = "SELECT a FROM ActualResultEntity a WHERE a.implementDatetime = :implementDatetime"),
    @NamedQuery(name = "ActualResultEntity.findByTransactionId", query = "SELECT a FROM ActualResultEntity a WHERE a.transactionId = :transactionId"),
    @NamedQuery(name = "ActualResultEntity.findByFkEquipmentId", query = "SELECT a FROM ActualResultEntity a WHERE a.fkEquipmentId = :fkEquipmentId"),
    @NamedQuery(name = "ActualResultEntity.findByFkOrganizationId", query = "SELECT a FROM ActualResultEntity a WHERE a.fkOrganizationId = :fkOrganizationId"),
    @NamedQuery(name = "ActualResultEntity.findByFkWorkflowId", query = "SELECT a FROM ActualResultEntity a WHERE a.fkWorkflowId = :fkWorkflowId"),
    @NamedQuery(name = "ActualResultEntity.findByFkWorkId", query = "SELECT a FROM ActualResultEntity a WHERE a.fkWorkId = :fkWorkId"),
    @NamedQuery(name = "ActualResultEntity.findByActualStatus", query = "SELECT a FROM ActualResultEntity a WHERE a.actualStatus = :actualStatus"),
    @NamedQuery(name = "ActualResultEntity.findByInterruptReason", query = "SELECT a FROM ActualResultEntity a WHERE a.interruptReason = :interruptReason"),
    @NamedQuery(name = "ActualResultEntity.findByDelayReason", query = "SELECT a FROM ActualResultEntity a WHERE a.delayReason = :delayReason"),
    @NamedQuery(name = "ActualResultEntity.findByFirstStatus", query = "SELECT a FROM ActualResultEntity a WHERE a.fkWorkKanbanId = :fkWorkKanbanId AND a.fkOrganizationId = :fkOrganizationId AND a.fkEquipmentId = :fkEquipmentId  AND a.actualStatus = :actualStatus ORDER BY a.implementDatetime, a.actualId"),
    @NamedQuery(name = "ActualResultEntity.findByInterruptReason", query = "SELECT a FROM ActualResultEntity a WHERE a.interruptReason = :interruptReason"),

    // 工程別生産情報を取得する
    @NamedQuery(name = "ActualResultEntity.completionByFkWorkId", query = "SELECT NEW jp.adtekfuji.andon.entity.ProductivityEntity(a.fkWorkId, COUNT(a.fkWorkId)) FROM ActualResultEntity a WHERE a.implementDatetime >= :fromDate AND a.implementDatetime < :toDate AND a.fkWorkId IN :workIds AND a.actualStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION GROUP BY a.fkWorkId"),
    // 設備別生産情報を取得する
    @NamedQuery(name = "ProductivityEntity.completionByFkEquipmentId", query = "SELECT NEW jp.adtekfuji.andon.entity.ProductivityEntity(a.fkEquipmentId, COUNT(a.fkEquipmentId)) FROM ActualResultEntity a WHERE a.implementDatetime >= :fromDate AND a.implementDatetime < :toDate AND a.fkEquipmentId IN :equipmentIds AND a.actualStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION GROUP BY a.fkEquipmentId"),
    // 工程別不具合情報を取得する
    @NamedQuery(name = "DefectEntity.suspendByFkWorkId", query = "SELECT NEW jp.adtekfuji.andon.entity.DefectEntity(a.fkWorkId, COUNT(a.fkWorkId)) FROM ActualResultEntity a WHERE a.implementDatetime >= :fromDate AND a.implementDatetime < :toDate AND a.fkWorkId IN :workIds AND a.actualStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND GROUP BY a.fkWorkId"),
    // エクスポート済フラグを更新 (エクスポート済にする)
    @NamedQuery(name = "ActualResultEntity.updateExportedFlagByActualId", query ="UPDATE ActualResultEntity a SET a.exportedFlag = true WHERE a.actualId = :actualId"),
    // カンバンIDで工程実績IDを取得する
    @NamedQuery(name = "ActualResultEntity.findIdByFkKanbanId", query = "SELECT a.actualId FROM ActualResultEntity a WHERE a.fkKanbanId = :fkKanbanId"),
    // カンバンIDでカンバンを削除する
    @NamedQuery(name = "ActualResultEntity.removeByFkKanbanId", query = "DELETE FROM ActualResultEntity a WHERE a.fkKanbanId = :fkKanbanId")})
public class ActualResultEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "actual_id")
    private Long actualId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_kanban_id")
    private Long fkKanbanId;
    @Transient
    private String kanbanParentName;
    @Transient
    private String kanbanName;
    @Transient
    private String kanbanSubname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_work_kanban_id")
    private Long fkWorkKanbanId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "implement_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date implementDatetime;
    @Column(name = "work_time")
    private Integer workingTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "transaction_id")
    private Long transactionId;
    @Column(name = "fk_equipment_id")
    private Long fkEquipmentId;
    @Transient
    private String equipmentParentName;
    @Transient
    private String equipmentParentIdentName;
    @Transient
    private String equipmentName;
    @Transient
    private String equipmentIdentName;
    @Column(name = "fk_organization_id")
    private Long fkOrganizationId;
    @Transient
    private String organizationParentName;
    @Transient
    private String organizationParentIdentName;
    @Transient
    private String organizationName;
    @Transient
    private String organizationIdentName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_workflow_id")
    private Long fkWorkflowId;
    @Transient
    private String workflowParentName;
    @Transient
    private String workflowName;
    @Transient
    private String workflowRevision;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_work_id")
    private Long fkWorkId;
    @Transient
    private String workParentName;
    @Transient
    private String workName;
    @Transient
    private Integer taktTime;
    @Transient
    private Boolean isSeparateWork;
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @NotNull
    @Column(name = "actual_status")
    private KanbanStatusEnum actualStatus;
    @Size(max = 256)
    @Column(name = "interrupt_reason")
    private String interruptReason;
    @Size(max = 256)
    @Column(name = "delay_reason")
    private String delayReason;
    @Column(name = "exported_flag")
    private Boolean exportedFlag;
    @XmlElementWrapper(name = "actualPropertys")
    @XmlElement(name = "actualProperty")
    @Transient
    private List<ActualPropertyEntity> propertyCollection = null;

    public ActualResultEntity() {
    }

    public ActualResultEntity(Long fkKanbanId, Long fkWorkKanbanId, Date implementDatetime, Long transactionId, Long fkEquipmentId, Long fkOrganizationId, Long fkWorkflowId, Long fkWorkId, KanbanStatusEnum actualStatus, String interruptReason, String delayReason, Integer workingTime) {
        this.fkKanbanId = fkKanbanId;
        this.fkWorkKanbanId = fkWorkKanbanId;
        this.implementDatetime = implementDatetime;
        this.transactionId = transactionId;
        this.fkEquipmentId = fkEquipmentId;
        this.fkOrganizationId = fkOrganizationId;
        this.fkWorkflowId = fkWorkflowId;
        this.fkWorkId = fkWorkId;
        this.actualStatus = actualStatus;
        this.interruptReason = interruptReason;
        this.delayReason = delayReason;
        this.workingTime = workingTime;
        this.exportedFlag = null;
    }

    public Long getActualId() {
        return actualId;
    }

    public void setActualId(Long actualId) {
        this.actualId = actualId;
    }

    public Long getFkKanbanId() {
        return fkKanbanId;
    }

    public void setFkKanbanId(Long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

    public Long getFkWorkKanbanId() {
        return fkWorkKanbanId;
    }

    public void setFkWorkKanbanId(Long fkWorkKanbanId) {
        this.fkWorkKanbanId = fkWorkKanbanId;
    }

    public Date getImplementDatetime() {
        return implementDatetime;
    }

    public void setImplementDatetime(Date implementDatetime) {
        this.implementDatetime = implementDatetime;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getFkEquipmentId() {
        return fkEquipmentId;
    }

    public void setFkEquipmentId(Long fkEquipmentId) {
        this.fkEquipmentId = fkEquipmentId;
    }

    public Long getFkOrganizationId() {
        return fkOrganizationId;
    }

    public void setFkOrganizationId(Long fkOrganizationId) {
        this.fkOrganizationId = fkOrganizationId;
    }

    public Long getFkWorkflowId() {
        return fkWorkflowId;
    }

    public void setFkWorkflowId(Long fkWorkflowId) {
        this.fkWorkflowId = fkWorkflowId;
    }

    public Long getFkWorkId() {
        return fkWorkId;
    }

    public void setFkWorkId(Long fkWorkId) {
        this.fkWorkId = fkWorkId;
    }

    public KanbanStatusEnum getActualStatus() {
        return actualStatus;
    }

    public void setActualStatus(KanbanStatusEnum actualStatus) {
        this.actualStatus = actualStatus;
    }

    public String getInterruptReason() {
        return interruptReason;
    }

    public void setInterruptReason(String interruptReason) {
        this.interruptReason = interruptReason;
    }

    public String getDelayReason() {
        return delayReason;
    }

    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }

    public List<ActualPropertyEntity> getPropertyCollection() {
        return propertyCollection;
    }

    public void setPropertyCollection(List<ActualPropertyEntity> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    public String getKanbanParentName() {
        return kanbanParentName;
    }

    public void setKanbanParentName(String kanbanParentName) {
        this.kanbanParentName = kanbanParentName;
    }

    public String getKanbanName() {
        return kanbanName;
    }

    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    public String getKanbanSubname() {
        return kanbanSubname;
    }

    public void setKanbanSubname(String kanbanSubname) {
        this.kanbanSubname = kanbanSubname;
    }

    public Integer getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
    }

    public String getEquipmentParentName() {
        return equipmentParentName;
    }

    public void setEquipmentParentName(String equipmentParentName) {
        this.equipmentParentName = equipmentParentName;
    }

    public String getEquipmentParentIdentName() {
        return equipmentParentIdentName;
    }

    public void setEquipmentParentIdentName(String equipmentParentIdentName) {
        this.equipmentParentIdentName = equipmentParentIdentName;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentIdentName() {
        return equipmentIdentName;
    }

    public void setEquipmentIdentName(String equipmentIdentName) {
        this.equipmentIdentName = equipmentIdentName;
    }

    public String getOrganizationParentName() {
        return organizationParentName;
    }

    public void setOrganizationParentName(String organizationParentName) {
        this.organizationParentName = organizationParentName;
    }

    public String getOrganizationParentIdentName() {
        return organizationParentIdentName;
    }

    public void setOrganizationParentIdentName(String organizationParentIdentName) {
        this.organizationParentIdentName = organizationParentIdentName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationIdentName() {
        return organizationIdentName;
    }

    public void setOrganizationIdentName(String organizationIdentName) {
        this.organizationIdentName = organizationIdentName;
    }

    public String getWorkflowParentName() {
        return workflowParentName;
    }

    public void setWorkflowParentName(String workflowParentName) {
        this.workflowParentName = workflowParentName;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getWorkflowRevision() {
        return workflowRevision;
    }

    public void setWorkflowRevision(String workflowRevision) {
        this.workflowRevision = workflowRevision;
    }

    public String getWorkParentName() {
        return workParentName;
    }

    public void setWorkParentName(String workParentName) {
        this.workParentName = workParentName;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public Integer getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    public Boolean getIsSeparateWork() {
        return isSeparateWork;
    }

    public void setIsSeparateWork(Boolean isSeparateWork) {
        this.isSeparateWork = isSeparateWork;
    }

    /**
     * エクスポート済フラグを取得する。
     *
     * @return (true: エクスポート済み, false または null: エクスポート未実施)
     */
    public Boolean getExportedFlag() {
        return this.exportedFlag;
    }

    /**
     * エクスポート済フラグを設定する。
     *
     * @param exportedFlag (true: エクスポート済み, false または null: エクスポート未実施)
     */
    public void setExportedFlag(Boolean exportedFlag) {
        this.exportedFlag = exportedFlag;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (actualId != null ? actualId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ActualResultEntity)) {
            return false;
        }
        ActualResultEntity other = (ActualResultEntity) object;
        if ((this.actualId == null && other.actualId != null) || (this.actualId != null && !this.actualId.equals(other.actualId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ActualResultEntity{" + ", fkKanbanId=" + fkKanbanId + ", fkWorkKanbanId=" + fkWorkKanbanId + ", implementDatetime=" + implementDatetime + ", transactionId=" + transactionId + ", fkEquipmentId=" + fkEquipmentId + ", fkOrganizationId=" + fkOrganizationId + ", fkWorkflowId=" + fkWorkflowId + ", fkWorkId=" + fkWorkId + ", actualStatus=" + actualStatus + ", workingTime=" + workingTime + ", interruptReason=" + interruptReason + ", delayReason=" + delayReason + ", exportedFlag=" + exportedFlag + '}';
    }

}
