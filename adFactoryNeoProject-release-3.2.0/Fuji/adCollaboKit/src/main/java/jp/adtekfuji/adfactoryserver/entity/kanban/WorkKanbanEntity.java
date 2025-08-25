/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.kanban.WorkKanbanGroupKey;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "trn_work_kanban")
@XmlRootElement(name = "workKanban")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "WorkKanbanEntity.findByFkKanbanIdAndSeparateFlg", query = "SELECT w FROM WorkKanbanEntity w WHERE w.fkKanbanId = :fkKanbanId AND w.separateWorkFlag = :separateWorkFlag ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.countByFkKanbanIdAndSeparateFlg", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanEntity w WHERE w.fkKanbanId = :fkKanbanId AND w.separateWorkFlag = :separateWorkFlag"),
    @NamedQuery(name = "WorkKanbanEntity.findByEquipmentId", query = "SELECT w FROM WorkKanbanEntity w WHERE w.workKanbanId IN (SELECT e.conWorkkanbanEquipmentEntityPK.fkWorkkanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.conWorkkanbanEquipmentEntityPK.fkEquipmentId = :equipmentId) ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByOrganizationId", query = "SELECT w FROM WorkKanbanEntity w WHERE w.workKanbanId IN (SELECT o.conWorkkanbanOrganizationEntityPK.fkWorkkanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.conWorkkanbanOrganizationEntityPK.fkOrganizationId = :organizationId) ORDER BY w.startDatetime, w.workKanbanId"),

    @NamedQuery(name = "WorkKanbanEntity.countInterrupt", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanEntity w WHERE w.fkKanbanId = :kanbanId AND w.workStatus = :workStatus AND w.skipFlag = false"),
    @NamedQuery(name = "WorkKanbanEntity.countSuspend", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanEntity w WHERE w.fkKanbanId = :kanbanId AND w.workStatus = :workStatus AND w.skipFlag = false"),
    @NamedQuery(name = "WorkKanbanEntity.countCompletion", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanEntity w WHERE w.fkKanbanId = :kanbanId AND (w.workStatus = :workStatus OR w.skipFlag = true)"),
    @NamedQuery(name = "WorkKanbanEntity.countWorking", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanEntity w WHERE w.fkKanbanId = :kanbanId AND (w.workStatus = :workStatus1 OR w.workStatus = :workStatus2) AND w.skipFlag = false"),
    @NamedQuery(name = "WorkKanbanEntity.countByKanbanId", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanEntity w WHERE w.fkKanbanId = :kanbanId"),

    @NamedQuery(name = "WorkKanbanEntity.findByKanbanWorkflowWorkSeparate", query = "SELECT w FROM WorkKanbanEntity w WHERE w.fkKanbanId = :fkKanbanId AND w.fkWorkflowId = :fkWorkflowId AND w.fkWorkId = :fkWorkId AND w.separateWorkFlag = :separateWorkFlag"),

    @NamedQuery(name = "WorkKanbanEntity.findAll", query = "SELECT w FROM WorkKanbanEntity w ORDER BY w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByWorkKanbanId", query = "SELECT w FROM WorkKanbanEntity w WHERE w.workKanbanId = :workKanbanId ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByFkKanbanId", query = "SELECT w FROM WorkKanbanEntity w WHERE w.fkKanbanId = :fkKanbanId ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByFkWorkflowId", query = "SELECT w FROM WorkKanbanEntity w WHERE w.fkWorkflowId = :fkWorkflowId ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByFkWorkId", query = "SELECT w FROM WorkKanbanEntity w WHERE w.fkWorkId = :fkWorkId ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findBySeparateWorkFlag", query = "SELECT w FROM WorkKanbanEntity w WHERE w.separateWorkFlag = :separateWorkFlag ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findBySkipFlag", query = "SELECT w FROM WorkKanbanEntity w WHERE w.skipFlag = :skipFlag ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByStartDatetime", query = "SELECT w FROM WorkKanbanEntity w WHERE w.startDatetime = :startDatetime ORDER BY w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByCompDatetime", query = "SELECT w FROM WorkKanbanEntity w WHERE w.compDatetime = :compDatetime ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByTaktTime", query = "SELECT w FROM WorkKanbanEntity w WHERE w.taktTime = :taktTime ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByFkUpdatePersonId", query = "SELECT w FROM WorkKanbanEntity w WHERE w.fkUpdatePersonId = :fkUpdatePersonId ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByUpdateDatetime", query = "SELECT w FROM WorkKanbanEntity w WHERE w.updateDatetime = :updateDatetime ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByWorkStatus", query = "SELECT w FROM WorkKanbanEntity w WHERE w.workStatus = :workStatus ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByFkInterruptReasonId", query = "SELECT w FROM WorkKanbanEntity w WHERE w.fkInterruptReasonId = :fkInterruptReasonId ORDER BY w.startDatetime, w.workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findByFkDelayReasonId", query = "SELECT w FROM WorkKanbanEntity w WHERE w.fkDelayReasonId = :fkDelayReasonId ORDER BY w.startDatetime, w.workKanbanId"),

    // カンバンIDと工程名から工程カンバンを問い合わせ
    @NamedQuery(name = "WorkKanbanEntity.findByWorkName", query = "SELECT w FROM WorkKanbanEntity w, WorkEntity m WHERE w.fkKanbanId = :fkKanbanId AND w.fkWorkId = m.workId AND m.workName = :workName"),
    // カンバンIDと工程ID、シリアル番号から工程カンバンを問い合わせ
    @NamedQuery(name = "WorkKanbanEntity.findBySerialNumber", query = "SELECT w FROM WorkKanbanEntity w WHERE w.fkKanbanId = :kanbanId AND w.fkWorkId = :workId AND w.serialNumber = :serialNumber"),
    // 工程IDから実施フラグを更新 (ロット生産の工程順を進める時に使用)
    @NamedQuery(name = "WorkKanbanEntity.updateImplementFlagByWorkId", query ="UPDATE WorkKanbanEntity w SET w.implementFlag = true WHERE w.fkKanbanId = :kanbanId AND w.fkWorkId = :workId AND w.skipFlag = false"),
    // 工程IDとシリアル番号から実施フラグを更新 (ロット生産の工程順を進める時に使用)
    @NamedQuery(name = "WorkKanbanEntity.updateImplementFlagBySerialNumber", query ="UPDATE WorkKanbanEntity w SET w.implementFlag = true WHERE w.fkKanbanId = :kanbanId AND w.fkWorkId = :workId AND w.serialNumber = :serialNumber AND w.skipFlag = false"),
    // 生産可能な工程カンバンを問い合わせ
    @NamedQuery(name = "WorkKanbanEntity.findProduct", query = "SELECT wk FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.conWorkkanbanOrganizationEntityPK.fkWorkkanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.conWorkkanbanOrganizationEntityPK.fkOrganizationId = :organizationId) AND wk.workKanbanId IN (SELECT e.conWorkkanbanEquipmentEntityPK.fkWorkkanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.conWorkkanbanEquipmentEntityPK.fkEquipmentId = :equipmentId) ORDER BY wk.startDatetime, wk.workKanbanId"),
    // 工程別生産情報を取得する
    @NamedQuery(name = "ProductivityEntity.completionByFkWorkId", query = "SELECT NEW jp.adtekfuji.andon.entity.ProductivityEntity(a.fkWorkId, COUNT(a.fkWorkId)) FROM WorkKanbanEntity a WHERE a.actualCompTime >= :fromDate AND a.actualCompTime < :toDate AND a.fkWorkId IN :workIds AND a.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION GROUP BY a.fkWorkId"),
})
public class WorkKanbanEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "work_kanban_id")
    private Long workKanbanId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_kanban_id")
    private Long fkKanbanId;
    @Transient
    private String kanbanName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_workflow_id")
    private Long fkWorkflowId;
    @Transient
    private String workflowName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_work_id")
    private Long fkWorkId;
    @Transient
    private String workName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "separate_work_flag")
    private Boolean separateWorkFlag;
    @Basic(optional = false)
    @NotNull
    @Column(name = "implement_flag")
    private Boolean implementFlag;
    @Basic(optional = false)
    @NotNull
    @Column(name = "skip_flag")
    private Boolean skipFlag;
    @Column(name = "start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDatetime;
    @Column(name = "comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compDatetime;
    @Transient
    private String content;
    @Transient
    private ContentTypeEnum contentType;
    @Column(name = "takt_time")
    private Integer taktTime;
    @Column(name = "sum_times")
    private Integer sumTimes;
    @Column(name = "fk_update_person_id")
    private Long fkUpdatePersonId;
    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;
    @Transient
    private KanbanStatusEnum kanbanStatus;
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @NotNull
    @Column(name = "work_status")
    private KanbanStatusEnum workStatus;
    @Column(name = "fk_interrupt_reason_id")
    private Long fkInterruptReasonId;
    @Column(name = "fk_delay_reason_id")
    private Long fkDelayReasonId;
    @Column(name = "work_kanban_order")
    private Integer workKanbanOrder;
    @XmlElementWrapper(name = "workKanbanPropertys")
    @XmlElement(name = "workKanbanProperty")
    @Transient
    private List<WorkKanbanPropertyEntity> propertyCollection = null;
    @XmlElementWrapper(name = "equipments")
    @XmlElement(name = "equipment")
    @Transient
    private List<Long> equipmentCollection = null;
    @XmlElementWrapper(name = "organizations")
    @XmlElement(name = "organization")
    @Transient
    private List<Long> organizationCollection = null;
    @Column(name = "serial_number")
    private Integer serialNumber;
    @Column(name = "sync_work")
    private Boolean syncWork;
    @Column(name = "actual_start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartTime;
    @Column(name = "actual_comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualCompTime;

    public WorkKanbanEntity() {
    }

    public WorkKanbanEntity(Long fkKanbanId, Long fkWorkflowId, Long fkWorkId, String workName, Boolean separateWorkFlag, Boolean implementFlag, Boolean skipFlag, Date startDatetime, Date compDatetime, Integer taktTime, Integer sumTimes, Long fkUpdatePersonId, Date updateDatetime, KanbanStatusEnum workStatus, Long fkInterruptReasonId, Long fkDelayReasonId, Integer workKanbanIrder) {
        this.fkKanbanId = fkKanbanId;
        this.fkWorkflowId = fkWorkflowId;
        this.fkWorkId = fkWorkId;
        this.workName = workName;
        this.separateWorkFlag = separateWorkFlag;
        this.implementFlag = implementFlag;
        this.skipFlag = skipFlag;
        this.startDatetime = startDatetime;
        this.compDatetime = compDatetime;
        this.taktTime = taktTime;
        this.sumTimes = sumTimes;
        this.fkUpdatePersonId = fkUpdatePersonId;
        this.updateDatetime = updateDatetime;
        this.workStatus = workStatus;
        this.fkInterruptReasonId = fkInterruptReasonId;
        this.fkDelayReasonId = fkDelayReasonId;
        this.workKanbanOrder = workKanbanIrder;
    }

    public Long getWorkKanbanId() {
        return workKanbanId;
    }

    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    public Long getFkKanbanId() {
        return fkKanbanId;
    }

    public void setFkKanbanId(Long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

    public String getKanbanName() {
        return kanbanName;
    }

    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    public Long getFkWorkflowId() {
        return fkWorkflowId;
    }

    public void setFkWorkflowId(Long fkWorkflowId) {
        this.fkWorkflowId = fkWorkflowId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public Long getFkWorkId() {
        return fkWorkId;
    }

    public void setFkWorkId(Long fkWorkId) {
        this.fkWorkId = fkWorkId;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public Boolean getSeparateWorkFlag() {
        return separateWorkFlag;
    }

    public void setSeparateWorkFlag(Boolean separateWorkFlag) {
        this.separateWorkFlag = separateWorkFlag;
    }

    public Boolean getImplementFlag() {
        return implementFlag;
    }

    public void setImplementFlag(Boolean implementFlag) {
        this.implementFlag = implementFlag;
    }

    public Boolean getSkipFlag() {
        return skipFlag;
    }

    public void setSkipFlag(Boolean skipFlag) {
        this.skipFlag = skipFlag;
    }

    public Date getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Date getCompDatetime() {
        return compDatetime;
    }

    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ContentTypeEnum getContentType() {
        return contentType;
    }

    public void setContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
    }

    public Integer getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    public Integer getSumTimes() {
        return this.sumTimes;
    }

    public void setSumTimes(Integer sumTimes) {
        this.sumTimes = sumTimes;
    }

    public Long getFkUpdatePersonId() {
        return fkUpdatePersonId;
    }

    public void setFkUpdatePersonId(Long fkUpdatePersonId) {
        this.fkUpdatePersonId = fkUpdatePersonId;
    }

    public Date getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public KanbanStatusEnum getKanbanStatus() {
        return kanbanStatus;
    }

    public void setKanbanStatus(KanbanStatusEnum kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
    }

    public KanbanStatusEnum getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(KanbanStatusEnum workStatus) {
        this.workStatus = workStatus;
    }

    public Long getFkInterruptReasonId() {
        return fkInterruptReasonId;
    }

    public void setFkInterruptReasonId(Long fkInterruptReasonId) {
        this.fkInterruptReasonId = fkInterruptReasonId;
    }

    public Long getFkDelayReasonId() {
        return fkDelayReasonId;
    }

    public void setFkDelayReasonId(Long fkDelayReasonId) {
        this.fkDelayReasonId = fkDelayReasonId;
    }

    public Integer getWorkKanbanOrder() {
        return workKanbanOrder;
    }

    public void setWorkKanbanOrder(Integer workKanbanOrder) {
        this.workKanbanOrder = workKanbanOrder;
    }

    public List<WorkKanbanPropertyEntity> getPropertyCollection() {
        return propertyCollection;
    }

    public void setPropertyCollection(List<WorkKanbanPropertyEntity> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    public List<Long> getEquipmentCollection() {
        return equipmentCollection;
    }

    public void setEquipmentCollection(List<Long> equipmentCollection) {
        this.equipmentCollection = equipmentCollection;
    }

    public List<Long> getOrganizationCollection() {
        return organizationCollection;
    }

    public void setOrganizationCollection(List<Long> organizationCollection) {
        this.organizationCollection = organizationCollection;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Boolean isSyncWork() {
        return Objects.nonNull(this.syncWork) ? this.syncWork : Boolean.FALSE;
    }

    public void setSyncWork(Boolean syncWork) {
        this.syncWork = syncWork;
    }

    public WorkKanbanGroupKey getGroupKey() {
        return WorkKanbanGroupKey.createGroupKey(this.getStartDatetime(), this.getWorkflowName());
    }

    public Date getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public Date getActualCompTime() {
        return actualCompTime;
    }

    public void setActualCompTime(Date actualCompTime) {
        this.actualCompTime = actualCompTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workKanbanId != null ? workKanbanId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkKanbanEntity)) {
            return false;
        }
        WorkKanbanEntity other = (WorkKanbanEntity) object;
        return !((this.workKanbanId == null && other.workKanbanId != null) || (this.workKanbanId != null && !this.workKanbanId.equals(other.workKanbanId)));
    }

    @Override
    public String toString() {
        return "WorkKanbanEntity{" + "workKanbanId=" + workKanbanId + ", fkKanbanId=" + fkKanbanId + ", kanbanName=" + kanbanName + ", fkWorkflowId=" + fkWorkflowId +
                ", workflowName=" + workflowName + ", fkWorkId=" + fkWorkId + ", workName=" + workName + ", separateWorkFlag=" + separateWorkFlag + ", implementFlag=" + implementFlag +
                ", skipFlag=" + skipFlag + ", startDatetime=" + startDatetime + ", compDatetime=" + compDatetime + ", content=" + content + ", contentType=" + contentType +
                ", taktTime=" + taktTime + ", sumTimes=" + sumTimes + ", fkUpdatePersonId=" + fkUpdatePersonId + ", updateDatetime=" + updateDatetime + ", kanbanStatus=" + kanbanStatus +
                ", workStatus=" + workStatus + ", fkInterruptReasonId=" + fkInterruptReasonId + ", fkDelayReasonId=" + fkDelayReasonId + ", workKanbanOrder=" + workKanbanOrder +
                ", serialNumber=" + serialNumber + ", syncWork=" + syncWork + ", actualStartTime=" + actualStartTime + ", actualCompTime=" + actualCompTime + '}';
    }
}
