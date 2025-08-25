/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.workflow;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;
import jp.adtekfuji.adFactory.enumerate.WorkKbnEnum;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalEntity;
import jp.adtekfuji.adfactoryserver.utility.PgJsonbConverter;

/**
 * 工程順情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_workflow")
@XmlRootElement(name = "workflow")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
    // 工程IDを指定して、最新版数の工程順のうち、対象工程を使用している工程順一覧を取得する。
    @NamedNativeQuery(name = "WorkflowEntity.findByWorkId",
            query = "SELECT w.* FROM mst_workflow w WHERE w.workflow_id IN (SELECT c.workflow_id FROM con_workflow_work c WHERE c.work_id = ?1) AND EXISTS (SELECT b.* FROM (SELECT a.workflow_name, MAX(a.workflow_rev) workflow_rev FROM mst_workflow a GROUP BY a.workflow_name) b WHERE b.workflow_name = w.workflow_name AND b.workflow_rev = w.workflow_rev)",
            resultClass = WorkflowEntity.class),

    @NamedNativeQuery(name = "WorkflowEntity.findWorkflowList", query = "WITH RECURSIVE hierarchy AS (WITH RECURSIVE org AS (SELECT CAST(?1 AS BIGINT) as organization_id UNION ALL SELECT mo1.parent_organization_id FROM mst_organization mo1 JOIN org ON org.organization_id = mo1.organization_id) SELECT CAST(?2 AS BIGINT) as hierarchy_id UNION ALL SELECT mh1.hierarchy_id FROM mst_hierarchy mh1, hierarchy WHERE mh1.parent_hierarchy_id = hierarchy.hierarchy_id AND mh1.hierarchy_type = 1 AND (?3 = 1 OR NOT EXISTS(SELECT * FROM trn_access_hierarchy tah WHERE mh1.hierarchy_id = tah.hierarchy_id AND NOT EXISTS(SELECT * FROM trn_access_hierarchy tah2 WHERE tah.hierarchy_id = tah2.hierarchy_id AND tah2.organization_id IN (SELECT organization_id FROM org))))), workflow_hierarcy AS (SELECT mh.hierarchy_id FROM mst_hierarchy mh WHERE mh.hierarchy_name = 'adFactoryLite' AND hierarchy_type = 1 UNION ALL SELECT mh2.hierarchy_id FROM mst_hierarchy mh2, workflow_hierarcy WHERE workflow_hierarcy.hierarchy_id = mh2.parent_hierarchy_id) SELECT mw.* FROM hierarchy hi JOIN con_hierarchy ch ON ch.hierarchy_id = hi.hierarchy_id JOIN mst_workflow mw ON ch.work_workflow_id = mw.workflow_id AND mw.remove_flag = FALSE WHERE NOT EXISTS (SELECT * FROM con_hierarchy ch JOIN workflow_hierarcy ON workflow_hierarcy.hierarchy_id = ch.hierarchy_id WHERE ch.work_workflow_id = mw.workflow_id)", resultClass = WorkflowEntity.class),
    @NamedNativeQuery(name = "WorkflowEntity.findLatestWorkflowList", query = "WITH RECURSIVE hierarchy AS (WITH RECURSIVE org AS (SELECT CAST(?1 AS BIGINT) as organization_id UNION ALL SELECT mo1.parent_organization_id FROM mst_organization mo1 JOIN org ON org.organization_id = mo1.organization_id) SELECT CAST(?2 AS BIGINT) as hierarchy_id UNION ALL SELECT mh1.hierarchy_id FROM mst_hierarchy mh1, hierarchy WHERE mh1.parent_hierarchy_id = hierarchy.hierarchy_id AND mh1.hierarchy_type = 1 AND (?3 = 1 OR NOT EXISTS(SELECT * FROM trn_access_hierarchy tah WHERE mh1.hierarchy_id = tah.hierarchy_id AND NOT EXISTS(SELECT * FROM trn_access_hierarchy tah2 WHERE tah.hierarchy_id = tah2.hierarchy_id AND tah2.organization_id IN (SELECT organization_id FROM org))))), workflow_hierarcy AS (SELECT mh.hierarchy_id FROM mst_hierarchy mh WHERE mh.hierarchy_name = 'adFactoryLite' AND hierarchy_type = 1 UNION ALL SELECT mh2.hierarchy_id FROM mst_hierarchy mh2, workflow_hierarcy WHERE workflow_hierarcy.hierarchy_id = mh2.parent_hierarchy_id) SELECT mw.* FROM hierarchy hi JOIN con_hierarchy ch ON ch.hierarchy_id = hi.hierarchy_id JOIN mst_workflow mw ON ch.work_workflow_id = mw.workflow_id WHERE EXISTS (SELECT * FROM (SELECT DISTINCT(FIRST_VALUE(mw.workflow_id) OVER (PARTITION BY mw.workflow_name ORDER BY mw.workflow_rev DESC)) id FROM mst_workflow mw WHERE mw.remove_flag = FALSE) a WHERE a.id = mw.workflow_id) AND NOT EXISTS (SELECT * FROM con_hierarchy ch JOIN workflow_hierarcy ON workflow_hierarcy.hierarchy_id = ch.hierarchy_id WHERE ch.work_workflow_id = mw.workflow_id) AND mw.remove_flag = FALSE ", resultClass = WorkflowEntity.class),
    @NamedNativeQuery(name = "WorkflowEntity.findReporterWorkflow", query = "WITH RECURSIVE a AS (SELECT mh.hierarchy_id FROM mst_hierarchy mh WHERE mh.hierarchy_name = 'adFactoryLite' AND hierarchy_type = 1 UNION ALL SELECT mh2.hierarchy_id FROM mst_hierarchy mh2, a WHERE a.hierarchy_id = mh2.parent_hierarchy_id) SELECT * FROM mst_workflow mw WHERE NOT EXISTS (SELECT * FROM con_hierarchy ch JOIN a ON a.hierarchy_id = ch.hierarchy_id WHERE ch.work_workflow_id = mw.workflow_id) AND mw.remove_flag=FALSE", resultClass = WorkflowEntity.class),
})
@NamedQueries({
    // 工程順IDを指定して、カンバン情報の件数を取得する。
    @NamedQuery(name = "WorkflowEntity.countKanbanAssociation", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE k.workflowId = :workflowId"),
    // 未完了のカンバン情報の件数を取得する。
    @NamedQuery(name = "WorkflowEntity.countIncompleteKanban", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE k.workflowId = :workflowId AND NOT (k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION OR k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.INTERRUPT)"),

    // 指定した工程順名の最大の版数を取得 ※.削除済・未承認も対象
    @NamedQuery(name = "WorkflowEntity.findLatestRev", query = "SELECT MAX(w.workflowRev) FROM WorkflowEntity w WHERE w.workflowName = :workflowName"),
    // 指定した工程順名の最大の版数を取得 ※.削除済は対象外、未承認は対象
    @NamedQuery(name = "WorkflowEntity.findLatestRevNotRemove", query = "SELECT MAX(w.workflowRev) FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.removeFlag = false"),
    // 指定した工程順名の最大の版数を取得 ※.削除済も対象、未承認は対象外
    @NamedQuery(name = "WorkflowEntity.findLatestRevApprove", query = "SELECT MAX(w.workflowRev) FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE"),
    // 指定した工程順名の最大の版数を取得 ※.削除済・未承認は対象外
    @NamedQuery(name = "WorkflowEntity.findLatestRevApproveNotRemove", query = "SELECT MAX(w.workflowRev) FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.removeFlag = false AND w.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE"),

    // 工程名を指定して、最終承認済の最新版数の工程IDを取得する。※.削除済は対象外
    @NamedQuery(name = "WorkflowEntity.findWorkIdLatestRevByName", query = "SELECT w.workflowId FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.workflowRev = (SELECT MAX(w2.workflowRev) FROM WorkflowEntity w2 WHERE w2.removeFlag = false AND w2.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE AND w2.workflowName = :workflowName)"),
    // 工程名を指定して、最終承認済の最新版数の工程を取得する。※.削除済は対象外
    @NamedQuery(name = "WorkflowEntity.findLatestRevByName", query = "SELECT w FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.workflowRev = (SELECT MAX(w2.workflowRev) FROM WorkflowEntity w2 WHERE w2.removeFlag = false AND w2.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE AND w2.workflowName = :workflowName)"),

    @NamedQuery(name = "WorkflowEntity.findByNameList", query = "SELECT w FROM WorkflowEntity w WHERE w.workflowName IN :workflowName"),

    // 工程順名の重複確認 (追加時) ※.削除済も対象
    @NamedQuery(name = "WorkflowEntity.checkAddByWorkflowName", query = "SELECT COUNT(w.workflowId) FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.workflowRev = :workflowRev"),
    // 工程順名の重複確認 (更新時) ※.削除済も対象
    @NamedQuery(name = "WorkflowEntity.checkUpdateByWorkflowName", query = "SELECT COUNT(w.workflowId) FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.workflowRev = :workflowRev AND w.workflowId != :workflowId"),

    // 工程順名・版数を指定して、工程順情報を取得する。(削除済は対象外)
    @NamedQuery(name = "WorkflowEntity.findByWorkflowName", query = "SELECT w FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.workflowRev = :workflowRev AND w.removeFlag = false"),

    // 階層IDを指定して、階層に属する工程順情報一覧を取得する。
    @NamedQuery(name = "WorkflowEntity.findByHierarchyId", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity(w, r.latestRev) FROM WorkflowEntity w, ConHierarchyEntity c, (SELECT wf.workflowName, MAX(CASE WHEN wf.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE THEN wf.workflowRev ELSE 0 END) latestRev FROM WorkflowEntity wf WHERE wf.removeFlag = false GROUP BY wf.workflowName) r WHERE c.hierarchyId = :hierarchyId AND c.workWorkflowId = w.workflowId AND r.workflowName = w.workflowName ORDER BY w.workflowName, w.workflowRev, w.workflowId"),
    // 階層IDを指定して、階層に属する工程順情報一覧を取得する。(承認済のみ)
    @NamedQuery(name = "WorkflowEntity.findByHierarchyIdApprove", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity(w, r.latestRev) FROM WorkflowEntity w, ConHierarchyEntity c, (SELECT wf.workflowName, MAX(wf.workflowRev) latestRev FROM WorkflowEntity wf WHERE wf.removeFlag = false AND wf.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE GROUP BY wf.workflowName) r WHERE c.hierarchyId = :hierarchyId AND c.workWorkflowId = w.workflowId AND r.workflowName = w.workflowName AND w.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE ORDER BY w.workflowName, w.workflowRev, w.workflowId"),

    // 工程順IDで工程順名を取得する ※.削除済も対象
    @NamedQuery(name = "WorkflowEntity.findWorkflowName", query = "SELECT w.workflowName FROM WorkflowEntity w WHERE w.workflowId = :workflowId"),
    // 工程順名と版数で工程順IDを取得する。
    @NamedQuery(name = "WorkflowEntity.findIdByWorkflowName", query = "SELECT w.workflowId FROM WorkflowEntity w WHERE w.workflowName = :workflowName AND w.workflowRev = :workflowRev AND w.removeFlag = false"),

    // 申請IDを指定して、工程情報を取得する。
    @NamedQuery(name = "WorkflowEntity.findByApprovalId", query = "SELECT w FROM WorkflowEntity w WHERE w.approvalId = :approvalId"),

    @NamedQuery(name = "WorkflowEntity.findByIds", query = "SELECT w FROM WorkflowEntity w WHERE w.workflowId IN :workflowIds"),
    // 工程順名を指定して作業中の工程カンバン数を取得
    @NamedQuery(name ="WorkflowEntity.countWorkingWorkKanbanByWorkflowName", query = "SELECT count(wkwe.workKanbanId) FROM WorkKanbanEntity wk JOIN WorkflowEntity wf ON wk.workflowId = wf.workflowId JOIN WorkKanbanWorkingEntity wkwe ON wk.workKanbanId = wkwe.workKanbanId  WHERE wf.workflowName IN :workflowName AND wk.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING"),
    // 工程階層から工程情報を取得する
    @NamedQuery(name = "WorkflowEntity.findLatestOnlyByHierarchyIdAndName", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity(w, c.hierarchyId, c.hierarchyId) FROM WorkflowEntity w, ConHierarchyEntity c WHERE c.hierarchyId IN :hierarchyIds AND c.workWorkflowId = w.workflowId AND LOWER(w.workflowName) LIKE :workflowName AND w.workflowRev = (SELECT MAX(w2.workflowRev) FROM WorkflowEntity w2 WHERE w2.removeFlag = false AND w2.workflowName = w.workflowName) ORDER BY w.workflowName, w.workflowId"),
    @NamedQuery(name = "WorkflowEntity.findByHierarchyIdAndName", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity(w, c.hierarchyId, c.hierarchyId) FROM WorkflowEntity w, ConHierarchyEntity c WHERE c.hierarchyId IN :hierarchyIds AND c.workWorkflowId = w.workflowId AND LOWER(w.workflowName) LIKE :workflowName ORDER BY w.workflowName, w.workflowId"),
    // 工程を使用した最新版の工程順を取得する
    @NamedQuery(name = "WorkflowEntity.findLatestByWorkId", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity(w, r.latestRev) FROM WorkflowEntity w, ConWorkflowWorkEntity c, (SELECT wf.workflowName, MAX(CASE WHEN wf.approvalState = jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum.FINAL_APPROVE THEN wf.workflowRev ELSE 0 END) latestRev FROM WorkflowEntity wf WHERE wf.removeFlag = false GROUP BY wf.workflowName) r WHERE c.workId IN :workIds AND c.workflowId = w.workflowId AND r.workflowName = w.workflowName ORDER BY w.workflowName, w.workflowRev, w.workflowId"),
})
public class WorkflowEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "workflow_id")
    private Long workflowId;// 工程順ID

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "workflow_name")
    private String workflowName;// 工程順名

    @Size(max = 256)
    @Column(name = "workflow_revision")
    private String workflowRevision;// 版名

    @Size(max = 2147483647)
    @Column(name = "workflow_diaglam", length = 32672)
    private String workflowDiaglam;// ワークフロー図

    @XmlElement(name = "fkUpdatePersonId")
    @Column(name = "update_person_id")
    private Long updatePersonId;// 更新者(組織ID)

    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;// 更新日時

    @Size(max = 2147483647)
    @Column(name = "ledger_path")
    private String ledgerPath;// 帳票テンプレートパス

    @Column(name = "remove_flag")
    private Boolean removeFlag = false;// 論理削除フラグ

    @Size(max = 64)
    @Column(name = "workflow_number")
    private String workflowNumber;// 作業番号

    @Basic(optional = false)
    //@NotNull
    @Column(name = "workflow_rev")
    private Integer workflowRev;// 版数

    @Column(name = "model_name")
    private String modelName;// モデル名

    @Column(name = "open_time")
    @Temporal(TemporalType.TIME)
    private Date openTime;// 始業時間

    @Column(name = "close_time")
    @Temporal(TemporalType.TIME)
    private Date closeTime;// 就業時間

    @Column(name = "schedule_policy")
    @Enumerated(EnumType.ORDINAL)
    private SchedulePolicyEnum schedulePolicy;// 作業順序

    @Column(name = "workflow_add_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String workflowAddInfo;// 追加情報(JSON)

    @Column(name = "service_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String serviceInfo;// サービス情報(JSON)

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * 申請ID
     */
    @Column(name = "approval_id")
    private Long approvalId;

    /**
     * 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    @Column(name = "approval_state")
    private ApprovalStatusEnum approvalState;

    @Transient
    private Long parentId;// 工程順階層ID

    @Transient
    private Integer latestRev;// 最新版数

    @XmlElementWrapper(name = "conWorkflowWorks")
    @XmlElement(name = "conWorkflowWork")
    @Transient
    private List<ConWorkflowWorkEntity> conWorkflowWorkCollection = null;// 工程順・工程関連付け情報一覧(通常工程)

    @XmlElementWrapper(name = "conWorkflowSeparateworks")
    @XmlElement(name = "conWorkflowSeparatework")
    @Transient
    private List<ConWorkflowWorkEntity> conWorkflowSeparateworkCollection = null;// 工程順・工程関連付け情報一覧(追加工程)

    /**
     * 申請情報
     */
    @Transient
    private ApprovalEntity approval;

    /**
     * コンストラクタ
     */
    public WorkflowEntity() {
        this.schedulePolicy = SchedulePolicyEnum.PriorityParallel;
    }

    /**
     * コンストラクタ
     *
     * @param in 工程順情報
     */
    public WorkflowEntity(WorkflowEntity in) {
        this.workflowName = in.workflowName;
        this.workflowRevision = in.workflowRevision;
        this.workflowDiaglam = in.workflowDiaglam;
        this.updatePersonId = in.updatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.ledgerPath = in.ledgerPath;
        this.removeFlag = in.removeFlag;
        this.workflowNumber = in.workflowNumber;
        this.workflowRev = in.workflowRev;
        this.modelName = in.modelName;
        this.openTime = in.openTime;
        this.closeTime = in.closeTime;
        this.schedulePolicy = in.schedulePolicy;

        // 追加情報
        this.workflowAddInfo = in.workflowAddInfo;
        // サービス情報
        this.serviceInfo = in.serviceInfo;

        this.parentId = in.parentId;
        this.latestRev = in.latestRev;

        this.conWorkflowWorkCollection = new ArrayList<>();
        if (Objects.nonNull(in.getConWorkflowWorkCollection())) {
            for (ConWorkflowWorkEntity connect : in.getConWorkflowWorkCollection()) {
                this.conWorkflowWorkCollection.add(new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, connect));
            }
        }

        this.conWorkflowSeparateworkCollection = new ArrayList<>();
        if (Objects.nonNull(in.getConWorkflowSeparateworkCollection())) {
            for (ConWorkflowWorkEntity connect : in.getConWorkflowSeparateworkCollection()) {
                this.conWorkflowSeparateworkCollection.add(new ConWorkflowWorkEntity(WorkKbnEnum.ADDITIONAL_WORK, connect));
            }
        }
    }

    /**
     * コンストラクタ
     * ※.NamedQuery (WorkflowEntity.findByFkWorkflowHierarchyId) で使用。
     *
     * @param in 工程順情報
     * @param latestRev 最新版数
     */
    public WorkflowEntity(WorkflowEntity in, Integer latestRev) {
        this.workflowId = in.workflowId;
        this.workflowName = in.workflowName;
        this.workflowRev = in.workflowRev;
        this.workflowRevision = in.workflowRevision;
        this.workflowDiaglam = in.workflowDiaglam;
        this.updatePersonId = in.updatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.ledgerPath = in.ledgerPath;
        this.removeFlag = in.removeFlag;
        this.workflowNumber = in.workflowNumber;
        this.modelName = in.modelName;
        this.openTime = in.openTime;
        this.closeTime = in.closeTime;
        this.schedulePolicy = in.schedulePolicy;
        this.workflowAddInfo = in.workflowAddInfo;
        this.serviceInfo = in.serviceInfo;
        this.verInfo = in.verInfo;
        this.approvalId = in.approvalId;
        this.approvalState = in.approvalState;

        this.latestRev = latestRev;
    }
    
    /**
     * コンストラクタ
     * 
     * @param in 工程順情報
     * @param parentId 階層ID
     * @param p1 ダミー
     */
    public WorkflowEntity(WorkflowEntity in, Long parentId, Long p1) {
        this.workflowId = in.workflowId;
        this.workflowName = in.workflowName;
        this.workflowRev = in.workflowRev;
        this.workflowRevision = in.workflowRevision;
        this.workflowDiaglam = in.workflowDiaglam;
        this.updatePersonId = in.updatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.ledgerPath = in.ledgerPath;
        this.removeFlag = in.removeFlag;
        this.workflowNumber = in.workflowNumber;
        this.modelName = in.modelName;
        this.openTime = in.openTime;
        this.closeTime = in.closeTime;
        this.schedulePolicy = in.schedulePolicy;
        this.workflowAddInfo = in.workflowAddInfo;
        this.serviceInfo = in.serviceInfo;
        this.verInfo = in.verInfo;
        this.approvalId = in.approvalId;
        this.approvalState = in.approvalState;
        this.parentId = parentId;
    }

    /**
     * コンストラクタ
     *
     * @param parentId 親階層ID
     * @param workflowName 工程順名
     * @param workflowRevision 版名
     * @param workflowDiaglam ワークフロー図
     * @param updatePersonId 更新者(組織ID)
     * @param updateDatetime 更新日時
     * @param ledgerPath 帳票テンプレートパス
     */
    public WorkflowEntity(Long parentId, String workflowName, String workflowRevision, String workflowDiaglam, Long updatePersonId, Date updateDatetime, String ledgerPath) {
        this.parentId = parentId;
        this.workflowName = workflowName;
        this.workflowRev = 1;
        this.workflowRevision = workflowRevision;
        this.workflowDiaglam = workflowDiaglam;
        this.updatePersonId = updatePersonId;
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
        return this.workflowId;
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
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
        return this.workflowName;
    }

    /**
     * 工程順名を設定する。
     *
     * @param workflowName 工程順名
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    // TODO: 削除予定
    /**
     * 版名を取得する。
     *
     * @return 版名
     */
    public String getWorkflowRevision() {
        return this.workflowRevision;
    }

    // TODO: 削除予定
    /**
     * 版名を設定する。
     *
     * @param workflowRevision 版名
     */
    public void setWorkflowRevision(String workflowRevision) {
        this.workflowRevision = workflowRevision;
    }

    /**
     * 工程順ダイアグラムを取得する。
     *
     * @return 工程順ダイアグラム
     */
    public String getWorkflowDiaglam() {
        return this.workflowDiaglam;
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
     * 更新者(組織ID)を取得する。
     *
     * @return 更新者(組織ID)
     */
    public Long getUpdatePersonId() {
        return this.updatePersonId;
    }

    /**
     * 更新者(組織ID)を設定する。
     *
     * @param updatePersonId 更新者(組織ID)
     */
    public void setUpdatePersonId(Long updatePersonId) {
        this.updatePersonId = updatePersonId;
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
     * 論理削除フラグを取得する。
     *
     * @return 論理削除フラグ (true: 削除済, false: 有効な工程順)
     */
    public Boolean getRemoveFlag() {
        return removeFlag;
    }

    /**
     * 論理削除フラグを設定する。
     *
     * @param removeFlag 論理削除フラグ (true: 削除済, false: 有効な工程順)
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
     * 版数を取得する。
     *
     * @return 版数
     */
    public Integer getWorkflowRev() {
        return this.workflowRev;
    }

    /**
     * 版数を設定する。
     *
     * @param workflowRev 版数
     */
    public void setWorkflowRev(Integer workflowRev) {
        this.workflowRev = workflowRev;
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

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getWorkflowAddInfo() {
        return this.workflowAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param workflowAddInfo 追加情報(JSON)
     */
    public void setWorkflowAddInfo(String workflowAddInfo) {
        this.workflowAddInfo = workflowAddInfo;
    }

    /**
     * サービス情報(JSON)を取得する。
     *
     * @return サービス情報(JSON)
     */
    public String getServiceInfo() {
        return this.serviceInfo;
    }

    /**
     * サービス情報(JSON)を設定する。
     *
     * @param serviceInfo サービス情報(JSON)
     */
    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * 申請IDを取得する。
     *
     * @return 申請ID
     */
    public Long getApprovalId() {
        return this.approvalId;
    }

    /**
     * 申請IDを設定する。
     *
     * @param approvalId 申請ID
     */
    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    /**
     * 承認状態を取得する。
     *
     * @return 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    public ApprovalStatusEnum getApprovalState() {
        return this.approvalState;
    }

    /**
     * 承認状態を設定する。
     *
     * @param approvalState 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    public void setApprovalState(ApprovalStatusEnum approvalState) {
        this.approvalState = approvalState;
    }

    /**
     * 工程順階層IDを取得する。
     *
     * @return 工程順階層ID
     */
    public Long getParentId() {
        return this.parentId;
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
     * 工程順・工程関連付け情報一覧(通常工程)を取得する。
     *
     * @return 工程順・工程関連付け情報一覧(通常工程)
     */
    public List<ConWorkflowWorkEntity> getConWorkflowWorkCollection() {
        return conWorkflowWorkCollection;
    }

    /**
     * 工程順・工程関連付け情報一覧(通常工程)を設定する。
     *
     * @param conWorkflowWorkCollection 工程順・工程関連付け情報一覧(通常工程)
     */
    public void setConWorkflowWorkCollection(List<ConWorkflowWorkEntity> conWorkflowWorkCollection) {
        this.conWorkflowWorkCollection = conWorkflowWorkCollection;
    }

    /**
     * 工程順・工程関連付け情報一覧(追加工程)を取得する。
     *
     * @return 工程順・工程関連付け情報一覧(追加工程)
     */
//    public List<ConWorkflowSeparateworkEntity> getConWorkflowSeparateworkCollection() {
    public List<ConWorkflowWorkEntity> getConWorkflowSeparateworkCollection() {
        return this.conWorkflowSeparateworkCollection;
    }

    /**
     * 工程順・工程関連付け情報一覧(追加工程)を設定する。
     *
     * @param conWorkflowSeparateworkCollection 工程順・工程関連付け情報一覧(追加工程)
     */
    public void setConWorkflowSeparateworkCollection(List<ConWorkflowWorkEntity> conWorkflowSeparateworkCollection) {
        this.conWorkflowSeparateworkCollection = conWorkflowSeparateworkCollection;
    }

    /**
     * 申請情報を取得する。
     *
     * @return 申請情報
     */
    public ApprovalEntity getApproval() {
        return this.approval;
    }

    /**
     * 申請情報を設定する。
     *
     * @param approval 申請情報
     */
    public void setApproval(ApprovalEntity approval) {
        this.approval = approval;
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
        return new StringBuilder("WorkflowEntity{")
                .append("workflowId=").append(this.workflowId)
                .append(", workflowName=").append(this.workflowName)
                .append(", workflowRev=").append(this.workflowRev)
                .append(", workflowRevision=").append(this.workflowRevision)
                .append(", workflowDiaglam=").append(this.workflowDiaglam)
                .append(", updatePersonId=").append(this.updatePersonId)
                .append(", updateDatetime=").append(this.updateDatetime)
                .append(", ledgerPath=").append(this.ledgerPath)
                .append(", removeFlag=").append(this.removeFlag)
                .append(", workflowNumber=").append(this.workflowNumber)
                .append(", modelName=").append(this.modelName)
                .append(", openTime=").append(this.openTime)
                .append(", closeTime=").append(this.closeTime)
                .append(", schedulePolicy=").append(this.schedulePolicy)
                .append(", approvalId=").append(this.approvalId)
                .append(", approvalState=").append(this.approvalState)
                .append(", parentId=").append(this.parentId)
                .append(", latestRev=").append(this.latestRev)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
