/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
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
import javax.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;

/**
 * カンバン情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "trn_kanban")
@XmlRootElement(name = "kanban")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "KanbanEntity.checkAddOvarlapName", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE k.kanbanName = :kanbanName AND k.kanbanSubname = :kanbanSubname AND k.fkWorkflowId = :fkWorkflowId"),
    @NamedQuery(name = "KanbanEntity.checkUpdateOvarlapName", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE k.kanbanName = :kanbanName AND k.kanbanSubname = :kanbanSubname AND k.fkWorkflowId = :fkWorkflowId AND k.kanbanId != :kanbanId"),

    @NamedQuery(name = "KanbanEntity.findAll", query = "SELECT k FROM KanbanEntity k"),
    @NamedQuery(name = "KanbanEntity.findByKanbanId", query = "SELECT k FROM KanbanEntity k WHERE k.kanbanId = :kanbanId"),
    @NamedQuery(name = "KanbanEntity.findByKanbanIds", query = "SELECT k FROM KanbanEntity k WHERE k.kanbanId IN :kanbanId"),
    @NamedQuery(name = "KanbanEntity.findByKanbanName", query = "SELECT k FROM KanbanEntity k WHERE k.kanbanName = :kanbanName ORDER BY k.kanbanId"),
    @NamedQuery(name = "KanbanEntity.findByKanbanSubname", query = "SELECT k FROM KanbanEntity k WHERE k.kanbanSubname = :kanbanSubname"),
    @NamedQuery(name = "KanbanEntity.findByFkWorkflowId", query = "SELECT k FROM KanbanEntity k WHERE k.fkWorkflowId = :fkWorkflowId"),
    @NamedQuery(name = "KanbanEntity.findByStartDatetime", query = "SELECT k FROM KanbanEntity k WHERE k.startDatetime = :startDatetime"),
    @NamedQuery(name = "KanbanEntity.findByCompDatetime", query = "SELECT k FROM KanbanEntity k WHERE k.compDatetime = :compDatetime"),
    @NamedQuery(name = "KanbanEntity.findByFkUpdatePersonId", query = "SELECT k FROM KanbanEntity k WHERE k.fkUpdatePersonId = :fkUpdatePersonId"),
    @NamedQuery(name = "KanbanEntity.findByUpdateDatetime", query = "SELECT k FROM KanbanEntity k WHERE k.updateDatetime = :updateDatetime"),
    @NamedQuery(name = "KanbanEntity.findByKanbanStatus", query = "SELECT k FROM KanbanEntity k WHERE k.kanbanStatus = :kanbanStatus"),
    @NamedQuery(name = "KanbanEntity.findByFkInterruptReasonId", query = "SELECT k FROM KanbanEntity k WHERE k.fkInterruptReasonId = :fkInterruptReasonId"),
    @NamedQuery(name = "KanbanEntity.findByFkDelayReasonId", query = "SELECT k FROM KanbanEntity k WHERE k.fkDelayReasonId = :fkDelayReasonId"),
    // カンバン階層に属するカンバンを問い合わせる
    @NamedQuery(name = "KanbanEntity.findByFkKanbanHierarchyId", query = "SELECT k FROM ConKanbanHierarchyEntity c JOIN KanbanEntity k ON c.kanbanId = k.kanbanId WHERE c.kanbanHierarchyId = :fkHierarchyId ORDER BY k.kanbanName, k.kanbanSubname ASC"),
    @NamedQuery(name = "KanbanEntity.countProduct", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE (k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED OR k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING OR k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND) AND k.kanbanId IN (SELECT wk.fkKanbanId FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.conWorkkanbanOrganizationEntityPK.fkWorkkanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.conWorkkanbanOrganizationEntityPK.fkOrganizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.conWorkkanbanEquipmentEntityPK.fkWorkkanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.conWorkkanbanEquipmentEntityPK.fkEquipmentId IN :equipmentIds))"),
    @NamedQuery(name = "KanbanEntity.findProduct", query = "SELECT k FROM KanbanEntity k WHERE (k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.PLANNED OR k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING OR k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND) AND k.kanbanId IN (SELECT wk.fkKanbanId FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.conWorkkanbanOrganizationEntityPK.fkWorkkanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.conWorkkanbanOrganizationEntityPK.fkOrganizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.conWorkkanbanEquipmentEntityPK.fkWorkkanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.conWorkkanbanEquipmentEntityPK.fkEquipmentId IN :equipmentIds)) ORDER BY k.startDatetime, k.kanbanId"),
     // 指定した設備に対する完了数を取得する
    @NamedQuery(name = "KanbanEntity.completionByEquipmentId", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE k.actualCompTime >= :fromDate AND k.actualCompTime < :toDate AND k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND k.kanbanId IN (SELECT wk.fkKanbanId FROM WorkKanbanEntity wk WHERE wk.workKanbanId IN (SELECT e.conWorkkanbanEquipmentEntityPK.fkWorkkanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.conWorkkanbanEquipmentEntityPK.fkEquipmentId IN :equipmentIds))"),
})
public class KanbanEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "kanban_id")
    protected Long kanbanId;
    @Transient
    private Long parentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "kanban_name")
    private String kanbanName;
    @Size(max = 256)
    @Column(name = "kanban_subname")
    private String kanbanSubname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_workflow_id")
    private Long fkWorkflowId;
    @Transient
    private String workflowName;
    @Transient
    private Integer workflowRev;
    @Column(name = "start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDatetime;
    @Column(name = "comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compDatetime;
    @Column(name = "fk_update_person_id")
    private Long fkUpdatePersonId;
    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @NotNull
    @Column(name = "kanban_status")
    private KanbanStatusEnum kanbanStatus;
    @Column(name = "fk_interrupt_reason_id")
    private Long fkInterruptReasonId;
    @Column(name = "fk_delay_reason_id")
    private Long fkDelayReasonId;
    @XmlElementWrapper(name = "KanbanPropertys")
    @XmlElement(name = "KanbanProperty")
    @Transient
    private List<KanbanPropertyEntity> propertyCollection = null;
    @XmlElementWrapper(name = "workKanbans")
    @XmlElement(name = "workKanban")
    @Transient
    private List<WorkKanbanEntity> workKanbanCollection = null;
    @XmlElementWrapper(name = "separateworkKanbans")
    @XmlElement(name = "separateworkKanban")
    @Transient
    private List<WorkKanbanEntity> separateworkKanbanCollection = null;
    @XmlElementWrapper(name = "actualResults")
    @XmlElement(name = "actualResult")
    @Transient
    private List<ActualResultEntity> actualResultCollection = null;
    @Column(name = "actual_start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartTime;
    @Column(name = "actual_comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualCompTime;

    @XmlTransient
    @JoinColumn(name = "fk_workflow_id", referencedColumnName = "workflow_id", insertable = false, updatable = false, nullable = true)
    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private WorkflowEntity workflow;

    @Column(name = "lot_quantity")
    private Integer lotQuantity;

    @Transient
    @XmlTransient
    private long workKanbanCount;

    @Column(name = "model_name")
    private String modelName;

    /**
     * コンストラクタ
     */
    public KanbanEntity() {
    }

    /**
     * 
     * @return
     * @throws CloneNotSupportedException 
     */
    @Override
    public KanbanEntity clone() throws CloneNotSupportedException {
        try {
            return (KanbanEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
    }

    /**
     * コンストラクタ
     *
     * @param in 
     */
    public KanbanEntity(KanbanEntity in) {
        this.kanbanId = in.kanbanId;
        this.parentId = in.parentId;
        this.kanbanName = in.kanbanName;
        this.kanbanSubname = in.kanbanSubname;
        this.fkWorkflowId = in.fkWorkflowId;
        this.workflowName = in.workflowName;
        this.workflowRev = in.workflowRev;
        this.startDatetime = in.startDatetime;
        this.compDatetime = in.compDatetime;
        this.fkUpdatePersonId = in.fkUpdatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.kanbanStatus = in.kanbanStatus;
        this.fkInterruptReasonId = in.fkInterruptReasonId;
        this.fkDelayReasonId = in.fkDelayReasonId;
        propertyCollection = new ArrayList<>();
        for (KanbanPropertyEntity property : in.getPropertyCollection()) {
            this.propertyCollection.add(new KanbanPropertyEntity(property));
        }
        this.workKanbanCollection = new ArrayList<>();
        this.separateworkKanbanCollection = new ArrayList<>();
        this.modelName = in.modelName;
    }

    /**
     * コンストラクタ
     *
     * @param in
     * @param parentId
     * @param workflowName
     * @param workflowRev 
     */
    public KanbanEntity(KanbanEntity in, Long parentId, String workflowName, Integer workflowRev) {
        this.kanbanId = in.getKanbanId();
        this.kanbanName = in.getKanbanName();
        this.kanbanSubname = in.getKanbanSubname();
        this.fkWorkflowId = in.getFkWorkflowId();
        this.startDatetime = in.getStartDatetime();
        this.compDatetime = in.getCompDatetime();
        this.fkUpdatePersonId = in.getFkUpdatePersonId();
        this.updateDatetime = in.getUpdateDatetime();
        this.kanbanStatus = in.getKanbanStatus();
        this.fkInterruptReasonId = in.getFkInterruptReasonId();
        this.fkDelayReasonId = in.getFkDelayReasonId();
        this.actualStartTime = in.getActualStartTime();
        this.actualCompTime = in.getActualCompTime();
        this.lotQuantity = in.getLotQuantity();
        this.modelName = in.getModelName();

        this.parentId = parentId;
        this.workflowName = workflowName;
        this.workflowRev = workflowRev;
    }

    /**
     * コンストラクタ
     *
     * @param parentId
     * @param kanbanName
     * @param kanbanSubname
     * @param fkWorkflowId
     * @param workflowName
     * @param startDatetime
     * @param compDatetime
     * @param fkUpdatePersonId
     * @param updateDatetime
     * @param kanbanStatus
     * @param fkInterruptReasonId
     * @param fkDelayReasonId 
     */
    public KanbanEntity(Long parentId, String kanbanName, String kanbanSubname, Long fkWorkflowId, String workflowName, Date startDatetime, Date compDatetime, Long fkUpdatePersonId, Date updateDatetime, KanbanStatusEnum kanbanStatus, Long fkInterruptReasonId, Long fkDelayReasonId) {
        this.parentId = parentId;
        this.kanbanName = kanbanName;
        this.kanbanSubname = kanbanSubname;
        this.fkWorkflowId = fkWorkflowId;
        this.workflowName = workflowName;
        this.startDatetime = startDatetime;
        this.compDatetime = compDatetime;
        this.fkUpdatePersonId = fkUpdatePersonId;
        this.updateDatetime = updateDatetime;
        this.kanbanStatus = kanbanStatus;
        this.fkInterruptReasonId = fkInterruptReasonId;
        this.fkDelayReasonId = fkDelayReasonId;
    }

    /**
     * コンストラクタ
     *
     * @param kanbanName
     * @param fkWorkflowId
     * @param workflowName
     * @param fkUpdatePersonId
     * @param updateDatetime
     * @param kanbanStatus 
     */
    public KanbanEntity(String kanbanName, Long fkWorkflowId, String workflowName, Long fkUpdatePersonId, Date updateDatetime, KanbanStatusEnum kanbanStatus) {
        this.parentId = 0L;
        this.kanbanName = kanbanName;
        this.fkWorkflowId = fkWorkflowId;
        this.workflowName = workflowName;
        this.fkUpdatePersonId = fkUpdatePersonId;
        this.updateDatetime = updateDatetime;
        this.kanbanStatus = kanbanStatus;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getKanbanId() {
        return kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * カンバン階層IDを取得する。
     *
     * @return カンバン階層ID
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * カンバン階層IDを設定する。
     *
     * @param parentId カンバン階層ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    /**
     * サブカンバン名を取得する。
     *
     * @return サブカンバン名
     */
    public String getKanbanSubname() {
        return kanbanSubname;
    }

    /**
     * サブカンバン名を設定する。
     *
     * @param kanbanSubname サブカンバン名
     */
    public void setKanbanSubname(String kanbanSubname) {
        this.kanbanSubname = kanbanSubname;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getFkWorkflowId() {
        return fkWorkflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param fkWorkflowId 工程順ID
     */
    public void setFkWorkflowId(Long fkWorkflowId) {
        this.fkWorkflowId = fkWorkflowId;
    }

    /**
     * 先頭工程開始予定日時を取得する。
     *
     * @return 先頭工程開始予定日時
     */
    public Date getStartDatetime() {
        return startDatetime;
    }

    /**
     * 先頭工程開始予定日時を設定する。
     *
     * @param startDatetime 先頭工程開始予定日時
     */
    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    /**
     * 工程順情報を取得する。
     *
     * @return 工程順情報
     */
    public WorkflowEntity getWorkflow() {
        return this.workflow;
    }

    /**
     * 工程順情報を設定する。
     *
     * @param workflow 工程順情報
     */
    public void setWorkflow(WorkflowEntity workflow) {
        this.workflow = workflow;
    }

    /**
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
        return workflowName;
    }

    /**
     * 工程順名を設定する。
     *
     * @param workflowName 工程順名
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /**
     * 工程順の版数を取得する。
     *
     * @return 工程順の版数
     */
    public Integer getWorkflowRev() {
        return workflowRev;
    }

    /**
     * 工程順の版数を設定する。
     *
     * @param workflowRev 工程順の版数
     */
    public void setWorkflowRev(Integer workflowRev) {
        this.workflowRev = workflowRev;
    }

    /**
     * 最終工程完了予定日時を取得する。
     *
     * @return 最終工程完了予定日時
     */
    public Date getCompDatetime() {
        return compDatetime;
    }

    /**
     * 最終工程完了予定日時を設定する。
     *
     * @param compDatetime 最終工程完了予定日時
     */
    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    /**
     * 更新者の組織IDを取得する。
     *
     * @return 更新者の組織ID
     */
    public Long getFkUpdatePersonId() {
        return fkUpdatePersonId;
    }

    /**
     * 更新者の組織IDを設定する。
     *
     * @param fkUpdatePersonId 更新者の組織ID
     */
    public void setFkUpdatePersonId(Long fkUpdatePersonId) {
        this.fkUpdatePersonId = fkUpdatePersonId;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDatetime() {
        return updateDatetime;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDatetime 更新日時
     */
    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    /**
     * カンバンステータスを取得する。
     *
     * @return カンバンステータス
     */
    public KanbanStatusEnum getKanbanStatus() {
        return kanbanStatus;
    }

    /**
     * カンバンステータスを設定する。
     *
     * @param kanbanStatus カンバンステータス
     */
    public void setKanbanStatus(KanbanStatusEnum kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
    }

    /**
     * 中断理由IDを取得する。
     *
     * @return 中断理由ID
     */
    public Long getFkInterruptReasonId() {
        return fkInterruptReasonId;
    }

    /**
     * 中断理由IDを設定する。
     *
     * @param fkInterruptReasonId 中断理由ID
     */
    public void setFkInterruptReasonId(Long fkInterruptReasonId) {
        this.fkInterruptReasonId = fkInterruptReasonId;
    }

    /**
     * 遅延理由IDを取得する。
     *
     * @return 遅延理由ID
     */
    public Long getFkDelayReasonId() {
        return fkDelayReasonId;
    }

    /**
     * 遅延理由IDを設定する。
     *
     * @param fkDelayReasonId 遅延理由ID
     */
    public void setFkDelayReasonId(Long fkDelayReasonId) {
        this.fkDelayReasonId = fkDelayReasonId;
    }

    /**
     * カンバンプロパティ情報一覧を取得する。
     *
     * @return カンバンプロパティ情報一覧
     */
    public List<KanbanPropertyEntity> getPropertyCollection() {
        return propertyCollection;
    }

    /**
     * カンバンプロパティ情報一覧を設定する。
     *
     * @param propertyCollection カンバンプロパティ情報一覧
     */
    public void setPropertyCollection(List<KanbanPropertyEntity> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    /**
     * 工程カンバン情報一覧を取得する。
     *
     * @return 工程カンバン情報一覧
     */
    public List<WorkKanbanEntity> getWorkKanbanCollection() {
        return workKanbanCollection;
    }

    /**
     * 工程カンバン情報一覧を設定する。
     *
     * @param workKanbanCollection 工程カンバン情報一覧
     */
    public void setWorkKanbanCollection(List<WorkKanbanEntity> workKanbanCollection) {
        this.workKanbanCollection = workKanbanCollection;
    }

    /**
     * 追加工程カンバン情報一覧を取得する。
     *
     * @return 追加工程カンバン情報一覧
     */
    public List<WorkKanbanEntity> getSeparateworkKanbanCollection() {
        return separateworkKanbanCollection;
    }

    /**
     * 追加工程カンバン情報一覧を設定する。
     *
     * @param separateworkKanbanCollection 追加工程カンバン情報一覧
     */
    public void setSeparateworkKanbanCollection(List<WorkKanbanEntity> separateworkKanbanCollection) {
        this.separateworkKanbanCollection = separateworkKanbanCollection;
    }

    /**
     * 工程実績情報一覧を取得する。
     *
     * @return 工程実績情報一覧
     */
    public List<ActualResultEntity> getActualResultCollection() {
        return this.actualResultCollection;
    }

    /**
     * 工程実績情報一覧を設定する。
     *
     * @param actualResultCollection 工程実績情報一覧
     */
    public void setActualResultCollection(List<ActualResultEntity> actualResultCollection) {
        this.actualResultCollection = actualResultCollection;
    }

    /**
     * ロット数量を取得する。
     *
     * @return ロット数量
     */
    public Integer getLotQuantity() {
        return lotQuantity;
    }

    /**
     * ロット数量を設定する。
     *
     * @param lotQuantity ロット数量
     */
    public void setLotQuantity(Integer lotQuantity) {
        this.lotQuantity = lotQuantity;
    }

    /**
     * 開始日時(実績)を取得する。
     *
     * @return 開始日時(実績)
     */
    public Date getActualStartTime() {
        return actualStartTime;
    }

    /**
     * 開始日時(実績)を設定する。
     *
     * @param actualStartTime 開始日時(実績)
     */
    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    /**
     * 完了日時(実績)を取得する。
     *
     * @return 完了日時(実績)
     */
    public Date getActualCompTime() {
        return actualCompTime;
    }

    /**
     * 完了日時(実績)を設定する。
     *
     * @param actualCompTime 完了日時(実績)
     */
    public void setActualCompTime(Date actualCompTime) {
        this.actualCompTime = actualCompTime;
    }

    /**
     * 工程カンバン数を取得する。
     *
     * @return 工程カンバン数
     */
    public long getWorkKanbanCount() {
        if (Objects.nonNull(this.workKanbanCollection)) {
            return this.workKanbanCollection.size();
        }
        return this.workKanbanCount;
    }

    /**
     * 追加工程カンバン数を取得する。
     *
     * @return 追加工程カンバン数
     */
    public long getSeparateWorkKanbanCount() {
        if (Objects.nonNull(this.separateworkKanbanCollection)) {
            return this.separateworkKanbanCollection.size();
        }
        return 0L;
    }

    /**
     * 工程カンバン数を設定する。
     *
     * @param workKanbanCount 工程カンバン数
     */
    public void setWorkKanbanCount(long workKanbanCount) {
        this.workKanbanCount = workKanbanCount;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kanbanId != null ? kanbanId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KanbanEntity)) {
            return false;
        }
        KanbanEntity other = (KanbanEntity) object;
        return !((this.kanbanId == null && other.kanbanId != null) || (this.kanbanId != null && !this.kanbanId.equals(other.kanbanId)));
    }

    @Override
    public String toString() {
        return "KanbanEntity{"
                + "kanbanId=" + this.kanbanId
                + ", parentId=" + this.parentId
                + ", kanbanName=" + this.kanbanName
                + ", kanbanSubname=" + this.kanbanSubname
                + ", fkWorkflowId=" + this.fkWorkflowId
                + ", workflowName=" + this.workflowName
                + ", workflowRev=" + this.workflowRev
                + ", startDatetime=" + this.startDatetime
                + ", compDatetime=" + this.compDatetime
                + ", fkUpdatePersonId=" + this.fkUpdatePersonId
                + ", updateDatetime=" + this.updateDatetime
                + ", kanbanStatus=" + this.kanbanStatus
                + ", fkInterruptReasonId=" + this.fkInterruptReasonId
                + ", fkDelayReasonId=" + this.fkDelayReasonId
                + ", actualStartTime=" + this.actualStartTime
                + ", actualCompTime=" + this.actualCompTime
                + ", workflow=" + this.workflow
                + ", lotQuantity=" + this.lotQuantity
                + ", workKanbanCount=" + this.workKanbanCount
                + ", modelName=" + this.modelName
                + '}';
    }
}
