/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

import java.io.Serializable;
import java.util.ArrayList;
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
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;

/**
 * 工程順情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_workflow")
@XmlRootElement(name = "workflow")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "WorkflowEntity.countKanbanAssociation", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE k.fkWorkflowId = :fkWorkflowId"),

    // 指定した工程順名の最大の版数を取得 ※.削除済も対象
    @NamedQuery(name = "WorkflowEntity.findLatestRev", query = "SELECT MAX(w.workflowRev) FROM WorkflowEntity w WHERE w.workflowName = :workflowName"),
    // 指定した工程順名の最大の版数を取得 ※.削除済は対象外
    @NamedQuery(name = "WorkflowEntity.findLatestRevNotRemove", query = "SELECT MAX(w.workflowRev) FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.removeFlag = false"),

    // 工程順名の重複確認 (追加時) ※.削除済も対象
    @NamedQuery(name = "WorkflowEntity.checkAddByWorkflowName", query = "SELECT COUNT(w.workflowId) FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.workflowRev = :workflowRev"),
    // 工程順名の重複確認 (更新時) ※.削除済も対象
    @NamedQuery(name = "WorkflowEntity.checkUpdateByWorkflowName", query = "SELECT COUNT(w.workflowId) FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.workflowRev = :workflowRev AND w.workflowId != :workflowId"),

    @NamedQuery(name = "WorkflowEntity.findAll", query = "SELECT w FROM WorkflowEntity w WHERE w.removeFlag = false"),
    @NamedQuery(name = "WorkflowEntity.findByWorkflowId", query = "SELECT w FROM WorkflowEntity w WHERE w.workflowId = :workflowId AND w.removeFlag = false"),
    @NamedQuery(name = "WorkflowEntity.findByWorkflowName", query = "SELECT w FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.workflowRev = :workflowRev AND w.removeFlag = false"),
    @NamedQuery(name = "WorkflowEntity.findByWorkflowRevision", query = "SELECT w FROM WorkflowEntity w WHERE w.workflowRevision = :workflowRevision AND w.removeFlag = false"),
    @NamedQuery(name = "WorkflowEntity.findByWorkflowDiaglam", query = "SELECT w FROM WorkflowEntity w WHERE w.workflowDiaglam = :workflowDiaglam AND w.removeFlag = false"),
    @NamedQuery(name = "WorkflowEntity.findByFkUpdatePersonId", query = "SELECT w FROM WorkflowEntity w WHERE w.fkUpdatePersonId = :fkUpdatePersonId AND w.removeFlag = false"),
    @NamedQuery(name = "WorkflowEntity.findByUpdateDatetime", query = "SELECT w FROM WorkflowEntity w WHERE w.updateDatetime = :updateDatetime AND w.removeFlag = false"),
    @NamedQuery(name = "WorkflowEntity.findByLedgerPath", query = "SELECT w FROM WorkflowEntity w WHERE w.ledgerPath = :ledgerPath AND w.removeFlag = false"),
    @NamedQuery(name = "WorkflowEntity.findByRemoveFlag", query = "SELECT w FROM WorkflowEntity w WHERE w.removeFlag = :removeFlag"),
    // 工程順階層に属する工程順を問い合わせる
    @NamedQuery(name = "WorkflowEntity.findByFkWorkflowHierarchyId", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity(w.workflowId, w.workflowName, w.workflowRev, w.workflowRevision, w.workflowDiaglam, w.fkUpdatePersonId, w.updateDatetime, w.ledgerPath, w.removeFlag, w.workflowNumber, r.latestRev, w.modelName) FROM WorkflowEntity w, ConWorkflowHierarchyEntity c, (SELECT wf.workflowName, MAX(wf.workflowRev) latestRev FROM WorkflowEntity wf GROUP BY wf.workflowName) r WHERE c.conWorkflowHierarchyEntityPK.fkWorkflowHierarchyId = :fkHierarchyId AND c.conWorkflowHierarchyEntityPK.fkWorkflowId = w.workflowId AND r.workflowName = w.workflowName ORDER BY w.workflowName, w.workflowRev, w.workflowId"),
    // 工程順IDで工程順名を取得する ※.削除済も対象
    @NamedQuery(name = "WorkflowEntity.findWorkflowName", query = "SELECT w.workflowName FROM WorkflowEntity w WHERE w.workflowId = :workflowId")})
public class WorkflowEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "workflow_id")
    private Long workflowId;
    @Transient
    private Long parentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "workflow_name")
    private String workflowName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "workflow_rev")
    private Integer workflowRev;
    @Size(max = 256)
    @Column(name = "workflow_revision")
    private String workflowRevision;
    @Size(max = 2147483647)
    @Column(name = "workflow_diaglam", length = 32672)
    private String workflowDiaglam;
    @Column(name = "fk_update_person_id")
    private Long fkUpdatePersonId;
    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;
    @Size(max = 2147483647)
    @Column(name = "ledger_path")
    private String ledgerPath;
    @Column(name = "remove_flag")
    private Boolean removeFlag = false;
    @Size(max = 64)
    @Column(name = "workflow_number")
    private String workflowNumber;
    @XmlElementWrapper(name = "kanbanPropertyTemplates")
    @XmlElement(name = "kanbanPropertyTemplate")
    @Transient
    private List<KanbanPropertyTemplateEntity> kanbanPropertyTemplateCollection = null;
    @XmlElementWrapper(name = "conWorkflowWorks")
    @XmlElement(name = "conWorkflowWork")
    @Transient
    private List<ConWorkflowWorkEntity> conWorkflowWorkCollection = null;
    @XmlElementWrapper(name = "conWorkflowSeparateworks")
    @XmlElement(name = "conWorkflowSeparatework")
    @Transient
    private List<ConWorkflowSeparateworkEntity> conWorkflowSeparateworkCollection = null;
    @Transient
    private Integer latestRev;

    @Column(name = "model_name")
    private String modelName;
    @Column(name = "open_time")
    @Temporal(TemporalType.TIME)
    private Date openTime;
    @Column(name = "close_time")
    @Temporal(TemporalType.TIME)
    private Date closeTime;
    @Column(name = "schedule_policy")
    @Enumerated(EnumType.ORDINAL)
    private SchedulePolicyEnum schedulePolicy;

    /**
     * コンストラクタ
     */
    public WorkflowEntity() {
        this.schedulePolicy = SchedulePolicyEnum.PriorityParallel;
    }

    /**
     * コンストラクタ
     *
     * @param in 
     */
    public WorkflowEntity(WorkflowEntity in) {
        this.parentId = in.parentId;
        this.workflowName = in.workflowName;
        this.workflowRev = in.workflowRev;
        this.workflowRevision = in.workflowRevision;
        this.workflowDiaglam = in.workflowDiaglam;
        this.fkUpdatePersonId = in.fkUpdatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.ledgerPath = in.ledgerPath;
        this.removeFlag = in.removeFlag;
        this.workflowNumber = in.workflowNumber;
        this.kanbanPropertyTemplateCollection = new ArrayList<>();
        for (KanbanPropertyTemplateEntity property : in.getKanbanPropertyTemplateCollection()) {
            this.kanbanPropertyTemplateCollection.add(new KanbanPropertyTemplateEntity(property));
        }
        this.conWorkflowWorkCollection = new ArrayList<>();
        for (ConWorkflowWorkEntity connect : in.getConWorkflowWorkCollection()) {
            this.conWorkflowWorkCollection.add(new ConWorkflowWorkEntity(connect));
        }
        this.conWorkflowSeparateworkCollection = new ArrayList<>();
        for (ConWorkflowSeparateworkEntity connect : in.getConWorkflowSeparateworkCollection()) {
            this.conWorkflowSeparateworkCollection.add(new ConWorkflowSeparateworkEntity(connect));
        }
        this.latestRev = in.latestRev;
        this.modelName = in.modelName;
        this.openTime = in.openTime;
        this.closeTime = in.closeTime;
        this.schedulePolicy = in.schedulePolicy;
    }

    /**
     * コンストラクタ
     * ※.NamedQuery (WorkflowEntity.findByFkWorkflowHierarchyId) で使用。
     *
     * @param workflowId
     * @param workflowName
     * @param workflowRev
     * @param workflowRevision
     * @param workflowDiaglam
     * @param fkUpdatePersonId
     * @param updateDatetime
     * @param ledgerPath
     * @param removeFlag
     * @param workflowNumber
     * @param latestRev
     * @param modelName 
     */
    public WorkflowEntity(Long workflowId, String workflowName, Integer workflowRev, String workflowRevision, String workflowDiaglam, Long fkUpdatePersonId, Date updateDatetime, String ledgerPath, Boolean removeFlag, String workflowNumber, Integer latestRev, String modelName) {
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.workflowRev = workflowRev;
        this.workflowRevision = workflowRevision;
        this.workflowDiaglam = workflowDiaglam;
        this.fkUpdatePersonId = fkUpdatePersonId;
        this.updateDatetime = updateDatetime;
        this.ledgerPath = ledgerPath;
        this.removeFlag = removeFlag;
        this.workflowNumber = workflowNumber;
        this.latestRev = latestRev;
        this.modelName = modelName;
        this.schedulePolicy = SchedulePolicyEnum.PriorityParallel;
    }

    /**
     * コンストラクタ
     *
     * @param parentId
     * @param workflowName
     * @param workflowRevision
     * @param workflowDiaglam
     * @param fkUpdatePersonId
     * @param updateDatetime
     * @param ledgerPath 
     */
    public WorkflowEntity(Long parentId, String workflowName, String workflowRevision, String workflowDiaglam, Long fkUpdatePersonId, Date updateDatetime, String ledgerPath) {
        this.parentId = parentId;
        this.workflowName = workflowName;
        this.workflowRev = 1;
        this.workflowRevision = workflowRevision;
        this.workflowDiaglam = workflowDiaglam;
        this.fkUpdatePersonId = fkUpdatePersonId;
        this.updateDatetime = updateDatetime;
        this.ledgerPath = ledgerPath;
        this.schedulePolicy = SchedulePolicyEnum.PriorityParallel;
        this.removeFlag = false;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getWorkflowId() {
        return workflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * 工程順階層IDを取得する。
     *
     * @return 工程順階層ID
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * 工程順階層IDを設定する。
     *
     * @param parentId 工程順階層ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
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
     * 版数を取得する。
     *
     * @return 版数
     */
    public Integer getWorkflowRev() {
        return workflowRev;
    }

    /**
     * 版数を設定する。
     *
     * @param workflowRev 版数
     */
    public void setWorkflowRev(Integer workflowRev) {
        this.workflowRev = workflowRev;
    }

    // TODO: 削除予定
    public String getWorkflowRevision() {
        return workflowRevision;
    }

    // TODO: 削除予定
    public void setWorkflowRevision(String workflowRevision) {
        this.workflowRevision = workflowRevision;
    }

    /**
     * 工程順ダイアグラムを取得する。
     *
     * @return 工程順ダイアグラム
     */
    public String getWorkflowDiaglam() {
        return workflowDiaglam;
    }

    /**
     * 工程順ダイアグラムを設定する。
     *
     * @param workflowDiaglam 工程順ダイアグラム
     */
    public void setWorkflowDiaglam(String workflowDiaglam) {
        this.workflowDiaglam = workflowDiaglam;
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
     * 帳票テンプレートパスを取得する。
     *
     * @return 帳票テンプレートパス
     */
    public String getLedgerPath() {
        return ledgerPath;
    }

    /**
     * 帳票テンプレートパスを設定する。
     *
     * @param ledgerPath 帳票テンプレートパス
     */
    public void setLedgerPath(String ledgerPath) {
        this.ledgerPath = ledgerPath;
    }

    /**
     * 削除フラグを取得する。
     *
     * @return 削除フラグ (true: 削除済, false: 有効な工程順)
     */
    public Boolean getRemoveFlag() {
        return removeFlag;
    }

    /**
     * 削除フラグを設定する。
     *
     * @param removeFlag 削除フラグ (true: 削除済, false: 有効な工程順)
     */
    public void setRemoveFlag(Boolean removeFlag) {
        this.removeFlag = removeFlag;
    }

    /**
     * 作業番号を取得する。
     *
     * @return 作業番号
     */
    public String getWorkflowNumber() {
        return this.workflowNumber;
    }

    /**
     * 作業番号を設定する。
     *
     * @param workflowNumber 作業番号
     */
    public void setWorkflowNumber(String workflowNumber) {
        this.workflowNumber = workflowNumber;
    }

    /**
     * カンバンプロパティテンプレート情報一覧を取得する。
     *
     * @return カンバンプロパティテンプレート情報一覧
     */
    public List<KanbanPropertyTemplateEntity> getKanbanPropertyTemplateCollection() {
        return kanbanPropertyTemplateCollection;
    }

    /**
     * カンバンプロパティテンプレート情報一覧を設定する。
     *
     * @param kanbanPropertyTemplateCollection カンバンプロパティテンプレート情報一覧
     */
    public void setKanbanPropertyTemplateCollection(List<KanbanPropertyTemplateEntity> kanbanPropertyTemplateCollection) {
        this.kanbanPropertyTemplateCollection = kanbanPropertyTemplateCollection;
    }

    /**
     * 工程の関連付け情報一覧を取得する。
     *
     * @return 工程の関連付け情報一覧
     */
    public List<ConWorkflowWorkEntity> getConWorkflowWorkCollection() {
        return conWorkflowWorkCollection;
    }

    /**
     * 工程の関連付け情報一覧を設定する。
     *
     * @param conWorkflowWorkCollection 工程の関連付け情報一覧
     */
    public void setConWorkflowWorkCollection(List<ConWorkflowWorkEntity> conWorkflowWorkCollection) {
        this.conWorkflowWorkCollection = conWorkflowWorkCollection;
    }

    /**
     * 追加工程の関連付け情報一覧を取得する。
     *
     * @return 追加工程の関連付け情報一覧
     */
    public List<ConWorkflowSeparateworkEntity> getConWorkflowSeparateworkCollection() {
        return conWorkflowSeparateworkCollection;
    }

    /**
     * 追加工程の関連付け情報一覧を設定する。
     *
     * @param conWorkflowSeparateworkCollection 追加工程の関連付け情報一覧
     */
    public void setConWorkflowSeparateworkCollection(List<ConWorkflowSeparateworkEntity> conWorkflowSeparateworkCollection) {
        this.conWorkflowSeparateworkCollection = conWorkflowSeparateworkCollection;
    }

    /**
     * 最新版数を取得する。
     *
     * @return 最新版数
     */
    public Integer getLatestRev() {
        return latestRev;
    }

    /**
     * 最新版数を設定する。
     *
     * @param latestRev 最新版数
     */
    public void setLatestRev(Integer latestRev) {
        this.latestRev = latestRev;
    }

    /**
     * モデル名を取得する。
     *
     * @return
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 始業時間を取得する。
     *
     * @return
     */
    public Date getOpenTime() {
        return openTime;
    }

    /**
     * 始業時間を設定する。
     *
     * @param openTime
     */
    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    /**
     * 終業時間を取得する。
     *
     * @return
     */
    public Date getCloseTime() {
        return closeTime;
    }

    /**
     * 終業時間を設定する。
     *
     * @param closeTime
     */
    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    /**
     * 作業順序を取得する。
     *
     * @return
     */
    public SchedulePolicyEnum getSchedulePolicy() {
        return schedulePolicy;
    }

    /**
     * 作業順序を設定する。
     *
     * @param schedulePolicy
     */
    public void setSchedulePolicy(SchedulePolicyEnum schedulePolicy) {
        this.schedulePolicy = schedulePolicy;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workflowId != null ? workflowId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkflowEntity)) {
            return false;
        }
        WorkflowEntity other = (WorkflowEntity) object;
        if ((this.workflowId == null && other.workflowId != null) || (this.workflowId != null && !this.workflowId.equals(other.workflowId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WorkflowEntity{"
                + "workflowId=" + workflowId
                + ", parentId=" + parentId
                + ", workflowName=" + workflowName
                + ", workflowRev=" + workflowRev
                + ", workflowRevision=" + workflowRevision
                + ", workflowDiaglam=" + workflowDiaglam
                + ", fkUpdatePersonId=" + fkUpdatePersonId
                + ", updateDatetime=" + updateDatetime
                + ", ledgerPath=" + ledgerPath
                + ", removeFlag=" + removeFlag
                + ", workflowNumber=" + workflowNumber
                + ", latestRev=" + latestRev
                + ", modelName=" + modelName
                + ", openTime=" + openTime
                + ", closeTime=" + closeTime
                + ", schedulePolicy="
                + schedulePolicy
                + '}';
    }
}
