/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
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
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adfactoryserver.utility.PgJsonbConverter;

/**
 * 工程実績情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "trn_actual_result")
@XmlRootElement(name = "actualResult")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
    @NamedNativeQuery(name = "ActualResultEntity.countByTagName",
            query = "SELECT COUNT(p.key)"
                    + " FROM trn_actual_result a"
                    + " CROSS JOIN jsonb_to_recordset(a.actual_add_info) p(key text, type text, val text, disp text)"
                    + " WHERE a.implement_datetime >= ?1 AND a.implement_datetime <= ?2 AND p.key = ANY(?3)"),
    @NamedNativeQuery(name = "ActualResultEntity.findByTagName",
            query = "SELECT a.actual_id, p.disp, w.work_name, p.key tag_name, a.implement_datetime date_time, p.val trace_value"
                    + " FROM trn_actual_result a"
                    + " CROSS JOIN jsonb_to_recordset(a.actual_add_info) p(key text, type text, val text, disp text)"
                    + " LEFT JOIN mst_work w ON w.work_id = a.work_id"
                    + " WHERE a.implement_datetime >= ?1 AND a.implement_datetime <= ?2 AND p.key = ANY(?3)",
            resultClass = TraceEntity.class),
        // 履歴を取得する
        @NamedNativeQuery(name = "ActualResultEntity.searchHistory",
                query = "SELECT * FROM trn_actual_result tar WHERE tar.remove_flag=FALSE AND equipment_id = ?3 AND tar.work_kanban_id IN (SELECT trn_work_kanban.work_kanban_id FROM trn_work_kanban WHERE workflow_id = ?1 AND work_id = ?2) ORDER BY implement_datetime DESC",
        resultClass = ActualResultEntity.class),
        // 工程順IDを指定して日にち範囲の工程実績を取得する(電子帳票)
        @NamedNativeQuery(name = "ActualResultEntity.searchHistoryByDate",
                query = "SELECT * FROM trn_actual_result tar WHERE tar.remove_flag = FALSE AND ?1 <= tar.implement_datetime AND tar.implement_datetime < ?2 AND EXISTS(SELECT * FROM mst_workflow mw WHERE mw.remove_flag = FALSE AND mw.workflow_id = tar.workflow_id AND EXISTS(SELECT * FROM mst_workflow mw2 WHERE mw.workflow_name = mw2.workflow_name AND mw2.workflow_id = ?3)) ORDER BY implement_datetime",
                resultClass = ActualResultEntity.class),
        @NamedNativeQuery(name = "ActualResultEntity.searchHistoryByLimit", query="SELECT tar.* FROM trn_actual_result tar JOIN (SELECT implement_datetime FROM trn_actual_result WHERE equipment_id = ?1 AND workflow_id = ?2 GROUP BY implement_datetime ORDER BY implement_datetime DESC LIMIT ?3) subq ON tar.implement_datetime = subq.implement_datetime WHERE tar.equipment_id = ?1 AND tar.workflow_id = ?2", resultClass = ActualResultEntity.class),
        @NamedNativeQuery(name = "ActualResultEntity.searchHistoryByPeriod", query="SELECT tar.* FROM trn_actual_result tar WHERE tar.implement_datetime BETWEEN ?3 AND ?4 AND tar.equipment_id = ?1 AND tar.workflow_id = ?2", resultClass = ActualResultEntity.class),
        @NamedNativeQuery(name = "ActualResultEntity.searchFirstById", query="WITH RECURSIVE actuals AS (SELECT tar.* FROM trn_actual_result tar WHERE tar.actual_id = ?1 UNION ALL SELECT tar2.* FROM trn_actual_result tar2, actuals WHERE actuals.pair_id = tar2.actual_id) SELECT * FROM actuals WHERE pair_id ISNULL", resultClass = ActualResultEntity.class),
        @NamedNativeQuery(name = "ActualResultEntity.searchLastById", query="WITH RECURSIVE actuals AS ( SELECT tar.actual_id FROM trn_actual_result tar WHERE tar.actual_id = ?1 UNION ALL SELECT CAST(W.val AS INT) FROM actuals JOIN trn_actual_result tar2 ON tar2.actual_id = actuals.actual_id JOIN JSONB_TO_RECORDSET(tar2.actual_add_info) AS W(key TEXT, val TEXT) ON W.key = '@forward_report_id@' AND tar2.actual_id = actuals.actual_id ) SELECT tar3.* FROM actuals JOIN trn_actual_result tar3 ON tar3.actual_id = actuals.actual_id AND tar3.remove_flag = FALSE ORDER BY tar3.implement_datetime DESC LIMIT 1", resultClass = ActualResultEntity.class),
})
@NamedQueries({
    // カンバンID一覧を指定して、工程実績情報一覧を取得する。
    @NamedQuery(name = "ActualResultEntity.findByKanbanIds", query = "SELECT a FROM ActualResultEntity a WHERE a.kanbanId IN :kanbanIds"),

    // 工程別生産情報を取得する。 (未使用)
    @NamedQuery(name = "ActualResultEntity.completionByWorkId", query = "SELECT NEW jp.adtekfuji.andon.entity.ProductivityEntity(a.workId, COUNT(a.workId)) FROM ActualResultEntity a WHERE a.implementDatetime >= :fromDate AND a.implementDatetime < :toDate AND a.workId IN :workIds AND a.actualStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION GROUP BY a.workId"),
    // 設備ID一覧・実績日時の範囲を指定して、設備毎の生産情報を取得する。
    @NamedQuery(name = "ProductivityEntity.completionByEquipmentId", query = "SELECT NEW jp.adtekfuji.andon.entity.ProductivityEntity(a.equipmentId, COUNT(a.equipmentId)) FROM ActualResultEntity a WHERE a.implementDatetime >= :fromDate AND a.implementDatetime < :toDate AND a.equipmentId IN :equipmentIds AND a.actualStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION GROUP BY a.equipmentId"),
    // 設備ID一覧・実績日時の範囲を指定して、工程毎の不具合情報を取得する。
    @NamedQuery(name = "DefectEntity.suspendByWorkId", query = "SELECT NEW jp.adtekfuji.andon.entity.DefectEntity(a.workId, COUNT(a.workId)) FROM ActualResultEntity a WHERE a.implementDatetime >= :fromDate AND a.implementDatetime < :toDate AND a.workId IN :workIds AND a.actualStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND GROUP BY a.workId"),

//    // エクスポート済フラグを更新する。(エクスポート済にする)
//    @NamedQuery(name = "ActualResultEntity.updateExportedFlagByActualId", query = "UPDATE ActualResultEntity a SET a.exportedFlag = true WHERE a.actualId = :actualId"),
//    // カンバンIDを指定して、実績ID一覧を取得する。
//    @NamedQuery(name = "ActualResultEntity.findIdByKanbanId", query = "SELECT a.actualId FROM ActualResultEntity a WHERE a.kanbanId = :kanbanId"),

    // カンバンIDを指定して、工程実績情報を削除する。
    @NamedQuery(name = "ActualResultEntity.removeByKanbanId", query = "DELETE FROM ActualResultEntity a WHERE a.kanbanId = :kanbanId"),
    // 設備ID一覧・日時範囲を指定して、カンバンID一覧を取得する。
    @NamedQuery(name = "ActualResultEntity.findKanbanByEquipmentId", query = "SELECT a.kanbanId FROM ActualResultEntity a WHERE a.implementDatetime >= :fromDate AND a.implementDatetime <= :toDate AND a.equipmentId IN :equipmentIds"),
    // 設備ID一覧・モデル名・日時範囲を指定して、カンバンID一覧を取得する。
    @NamedQuery(name = "ActualResultEntity.findKanbanByEquipmentIdAndModelName", query = "SELECT a.kanbanId FROM ActualResultEntity a JOIN KanbanEntity k ON a.kanbanId = k.kanbanId WHERE a.implementDatetime >= :fromDate AND a.implementDatetime <= :toDate AND a.equipmentId IN :equipmentIds AND k.modelName LIKE :modelName"),
    // 工程実績IDを指定して、追加情報を更新する。
    @NamedQuery(name = "ActualResultEntity.updateAddInfo", query = "UPDATE ActualResultEntity a SET a.actualAddInfo = :addInfo WHERE a.actualId = :actualId"),
    // 中断中の工程を取得する
    @NamedQuery(name = "ActualResultEntity.findSuspendingActualResult", query = "SELECT a FROM ActualResultEntity a JOIN WorkKanbanEntity b ON a.workKanbanId = b.workKanbanId JOIN KanbanEntity c ON c.kanbanId = a.kanbanId WHERE a.actualStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND AND b.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND AND a.equipmentId = :equipmentId AND a.organizationId = :organizationId AND (c.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND OR c.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING) ORDER BY a.implementDatetime DESC"),
    // 実績を取得する
    @NamedQuery(name = "ActualResultEntity.findByActualIds", query = "SELECT a FROM ActualResultEntity a WHERE a.actualId IN :actualIds ORDER BY a.implementDatetime"),
    // 工程実績IDを指定して、実績時間を更新する。
    @NamedQuery(name = "ActualResultEntity.updateTime", query = "UPDATE ActualResultEntity a SET a.implementDatetime = :implementDatetime WHERE a.actualId = :actualId"),
    // ペアIDを指定して、工程実績を取得する
    @NamedQuery(name = "ActualResultEntity.findByPairId", query = "SELECT a FROM ActualResultEntity a WHERE a.pairId = :pairId"),
})
public class ActualResultEntity implements Serializable, Cloneable  {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "actual_id")
    private Long actualId;// 工程実績ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "kanban_id")
    @XmlElement(name = "fkKanbanId")
    private Long kanbanId;// カンバンID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_kanban_id")
    @XmlElement(name = "fkWorkKanbanId")
    private Long workKanbanId;// 工程カンバンID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "implement_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date implementDatetime;// 実施日時

    @Basic(optional = false)
    //@NotNull
    @Column(name = "transaction_id")
    private Long transactionId;// トランザクションID

    @Column(name = "equipment_id")
    @XmlElement(name = "fkEquipmentId")
    private Long equipmentId;// 設備ID

    @Column(name = "organization_id")
    @XmlElement(name = "fkOrganizationId")
    private Long organizationId;// 組織ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "workflow_id")
    @XmlElement(name = "fkWorkflowId")
    private Long workflowId;// 工程順ID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_id")
    @XmlElement(name = "fkWorkId")
    private Long workId;// 工程ID

    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    //@NotNull
    @Column(name = "actual_status")
    private KanbanStatusEnum actualStatus;// 工程実績ステータス

    @Column(name = "work_time")
    private Integer workingTime;// 作業時間[ms]

    @Size(max = 256)
    @Column(name = "interrupt_reason")
    private String interruptReason;// 中断理由

    @Size(max = 256)
    @Column(name = "delay_reason")
    private String delayReason;// 遅延理由

    @Column(name = "comp_num")
    private Integer compNum;// 完成数

    @Column(name = "pair_id")
    private Long pairId;// ペアID

    @Column(name = "non_work_time")
    private Integer nonWorkTime;// 中断時間[ms]

    @Column(name = "interrupt_reason_id")
    private Long interruptReasonId;// 中断理由ID

    @Column(name = "delay_reason_id")
    private Long delayReasonId;// 遅延理由ID

    @Column(name = "kanban_name")
    private String kanbanName;// カンバン名

    @Column(name = "equipment_name")
    private String equipmentName;// 設備名

    @Column(name = "organization_name")
    private String organizationName;// 組織名

    @Column(name = "workflow_name")
    private String workflowName;// 工程順名

    @Column(name = "work_name")
    private String workName;// 工程名

    @Column(name = "actual_add_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String actualAddInfo;// 追加情報(JSON)

    @Column(name = "service_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String serviceInfo;// サービス情報(JSON)

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バージョン

    @Size(max = 256)
    @Column(name = "defect_reason")
    private String defectReason;// 不良理由

    @Column(name = "defect_num")
    private Integer defectNum;// 不良数

    @Column(name = "assist")
    private Integer assist;// 応援
    
    @Size(max = 1024)
    @Column(name = "serial_no")
    private String serialNo;// シリアル番号
    
    @Column(name = "rework_num")
    private Integer reworkNum; // 作業やり直し回数

    @Column(name = "remove_flag")
    private Boolean removeFlag;

    @Transient
    private String kanbanParentName;// カンバン階層名

    @Transient
    private String kanbanSubname;// サブカンバン名

    @Transient
    private String equipmentParentName;// 親設備の設備名

    @Transient
    private String equipmentParentIdentName;// 親設備の設備識別名

    @Transient
    private String equipmentIdentName;// 設備識別名

    @Transient
    private String organizationParentName;// 親組織の組織名

    @Transient
    private String organizationParentIdentName;// 親組織の組織識別名

    @Transient
    private String organizationIdentName;// 組織識別名

    @Transient
    private String workflowParentName;// 工程順階層名

    @Transient
    private String workflowRevision;// 工程順版数

    @Transient
    private String workParentName;// 工程階層名

    @Transient
    private Integer taktTime;// タクトタイム

    @Transient
    private Integer serialNumber;

    @Transient
    private Boolean isSeparateWork;// 追加工程フラグ
    
    @Transient
    @JsonProperty("traceabilities")
    private String traceabilities;

    @Transient
    @JsonProperty("createDatetime")
    private Date createDatetime; // 生成日時

    @Transient
    @JsonProperty("createOrganizationName")
    private String createOrganizationName;// 組織ID"

    /**
     * コンストラクタ
     */
    public ActualResultEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param kanbanId カンバンID
     * @param workKanbanId 工程カンバンID
     * @param implementDatetime 実施日時
     * @param transactionId トランザクションID
     * @param equipmentId 設備ID
     * @param organizationId 組織ID
     * @param workflowId 工程順ID
     * @param workId 工程ID
     * @param actualStatus 工程実績ステータス
     * @param interruptReason 中断理由
     * @param delayReason 遅延理由
     * @param workingTime 作業時間[ms]
     * @param pairId ペアID
     * @param nonWorkTime 中断時間[ms]
     */
    public ActualResultEntity(Long kanbanId, Long workKanbanId, Date implementDatetime, Long transactionId, Long equipmentId, Long organizationId, Long workflowId, Long workId, KanbanStatusEnum actualStatus, String interruptReason, String delayReason, Integer workingTime, Long pairId, Integer nonWorkTime, Integer reworkNum, Boolean removeFlag) {
        this.kanbanId = kanbanId;
        this.workKanbanId = workKanbanId;
        this.implementDatetime = implementDatetime;
        this.transactionId = transactionId;
        this.equipmentId = equipmentId;
        this.organizationId = organizationId;
        this.workflowId = workflowId;
        this.workId = workId;
        this.actualStatus = actualStatus;
        this.interruptReason = interruptReason;
        this.delayReason = delayReason;
        this.workingTime = workingTime;
        this.pairId = pairId;
        this.nonWorkTime = nonWorkTime;
        this.reworkNum = reworkNum;
        this.removeFlag = removeFlag;
    }

    /**
     * 工程実績IDを取得する。
     *
     * @return 工程実績ID
     */
    public Long getActualId() {
        return this.actualId;
    }

    /**
     * 工程実績IDを設定する。
     *
     * @param actualId 工程実績ID
     */
    public void setActualId(Long actualId) {
        this.actualId = actualId;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getKanbanId() {
        return this.kanbanId;
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
     * 実施日時を取得する。
     *
     * @return 実施日時
     */
    public Date getImplementDatetime() {
        return this.implementDatetime;
    }

    /**
     * 実施日時を設定する。
     *
     * @param implementDatetime 実施日時
     */
    public void setImplementDatetime(Date implementDatetime) {
        this.implementDatetime = implementDatetime;
    }

    /**
     * トランザクションIDを取得する。
     *
     * @return トランザクションID
     */
    public Long getTransactionId() {
        return this.transactionId;
    }

    /**
     * トランザクションIDを設定する。
     *
     * @param transactionId トランザクションID
     */
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
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
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public Long getWorkId() {
        return this.workId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param workId 工程ID
     */
    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    /**
     * 工程実績ステータスを取得する。
     *
     * @return 工程実績ステータス
     */
    public KanbanStatusEnum getActualStatus() {
        return this.actualStatus;
    }

    /**
     * 工程実績ステータスを設定する。
     *
     * @param actualStatus 工程実績ステータス
     */
    public void setActualStatus(KanbanStatusEnum actualStatus) {
        this.actualStatus = actualStatus;
    }

    /**
     * 作業時間[ms]を取得する。
     *
     * @return 作業時間[ms]
     */
    public Integer getWorkingTime() {
        return this.workingTime;
    }

    /**
     * 作業時間[ms]を設定する。
     *
     * @param workingTime 作業時間[ms]
     */
    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
    }

    /**
     * 中断理由を取得する。
     *
     * @return 中断理由
     */
    public String getInterruptReason() {
        return this.interruptReason;
    }

    /**
     * 中断理由を設定する。
     *
     * @param interruptReason 中断理由
     */
    public void setInterruptReason(String interruptReason) {
        this.interruptReason = interruptReason;
    }

    /**
     * 遅延理由を取得する。
     *
     * @return 遅延理由
     */
    public String getDelayReason() {
        return this.delayReason;
    }

    /**
     * 遅延理由を設定する。
     *
     * @param delayReason 遅延理由
     */
    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }

    /**
     * 完成数を取得する。
     *
     * @return 完成数
     */
    public Integer getCompNum() {
        return this.compNum;
    }

    /**
     * 完成数を設定する。
     *
     * @param compNum 完成数
     */
    public void setCompNum(Integer compNum) {
        this.compNum = compNum;
    }

    /**
     * ペアIDを取得する。
     *
     * @return ペアID
     */
    public Long getPairId() {
        return this.pairId;
    }

    /**
     * ペアIDを設定する。
     *
     * @param pairId ペアID
     */
    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    /**
     * 中断時間[ms]を取得する。
     *
     * @return 中断時間[ms]
     */
    public Integer getNonWorkTime() {
        return this.nonWorkTime;
    }

    /**
     * 中断時間[ms]を設定する。
     *
     * @param nonWorkTime 中断時間[ms]
     */
    public void setNonWorkTime(Integer nonWorkTime) {
        this.nonWorkTime = nonWorkTime;
    }

    /**
     * 中断理由IDを取得する。
     *
     * @return 中断理由ID
     */
    public Long getInterruptReasonId() {
        return this.interruptReasonId;
    }

    /**
     * 中断理由IDを設定する。
     *
     * @param interruptReasonId 中断理由ID
     */
    public void setInterruptReasonId(Long interruptReasonId) {
        this.interruptReasonId = interruptReasonId;
    }

    /**
     * 遅延理由IDを取得する。
     *
     * @return 遅延理由ID
     */
    public Long getDelayReasonId() {
        return this.delayReasonId;
    }

    /**
     * 遅延理由IDを設定する。
     *
     * @param delayReasonId 遅延理由ID
     */
    public void setDelayReasonId(Long delayReasonId) {
        this.delayReasonId = delayReasonId;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
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
     * 設備名を取得する。
     *
     * @return 設備名
     */
    public String getEquipmentName() {
        return this.equipmentName;
    }

    /**
     * 設備名を設定する。
     *
     * @param equipmentName 設備名
     */
    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    /**
     * 組織名を取得する。
     *
     * @return 組織名
     */
    public String getOrganizationName() {
        return this.organizationName;
    }

    /**
     * 組織名を設定する。
     *
     * @param organizationName 組織名
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
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

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 工程名を設定する。
     *
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getActualAddInfo() {
        return this.actualAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param actualAddInfo 追加情報(JSON)
     */
    public void setActualAddInfo(String actualAddInfo) {
        this.actualAddInfo = actualAddInfo;
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
     * 排他用バージョンを取得する。
     *
     * @return 排他用バージョン
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バージョンを設定する。
     *
     * @param verInfo 排他用バージョン
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * 不良理由を取得する。
     *
     * @return 不良理由
     */
    public String getDefectReason() {
        return this.defectReason;
    }

    /**
     * 不良理由を設定する。
     *
     * @param defectReason 不良理由
     */
    public void setDefectReason(String defectReason) {
        this.defectReason = defectReason;
    }

    /**
     * 不良数を取得する。
     *
     * @return 不良数
     */
    public Integer getDefectNum() {
        return this.defectNum;
    }

    /**
     * 不良数を設定する。
     *
     * @param defectNum 不良数
     */
    public void setDefectNum(Integer defectNum) {
        this.defectNum = defectNum;
    }

    /**
     * カンバン階層名を取得する。
     *
     * @return カンバン階層名
     */
    public String getKanbanParentName() {
        return this.kanbanParentName;
    }

    /**
     * カンバン階層名を設定する。
     *
     * @param kanbanParentName カンバン階層名
     */
    public void setKanbanParentName(String kanbanParentName) {
        this.kanbanParentName = kanbanParentName;
    }

    /**
     * サブカンバン名を取得する。
     *
     * @return サブカンバン名
     */
    public String getKanbanSubname() {
        return this.kanbanSubname;
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
     * 親設備の設備名を取得する。
     *
     * @return 親設備の設備名
     */
    public String getEquipmentParentName() {
        return this.equipmentParentName;
    }

    /**
     * 親設備の設備名を設定する。
     *
     * @param equipmentParentName 親設備の設備名
     */
    public void setEquipmentParentName(String equipmentParentName) {
        this.equipmentParentName = equipmentParentName;
    }

    /**
     * 親設備の設備識別名を取得する。
     *
     * @return 親設備の設備識別名
     */
    public String getEquipmentParentIdentName() {
        return this.equipmentParentIdentName;
    }

    /**
     * 親設備の設備識別名を設定する。
     *
     * @param equipmentParentIdentName 親設備の設備識別名
     */
    public void setEquipmentParentIdentName(String equipmentParentIdentName) {
        this.equipmentParentIdentName = equipmentParentIdentName;
    }

    /**
     * 設備識別名を取得する。
     *
     * @return 設備識別名
     */
    public String getEquipmentIdentName() {
        return this.equipmentIdentName;
    }

    /**
     * 設備識別名を設定する。
     *
     * @param equipmentIdentName 設備識別名
     */
    public void setEquipmentIdentName(String equipmentIdentName) {
        this.equipmentIdentName = equipmentIdentName;
    }

    /**
     * 親組織の組織名を取得する。
     *
     * @return 親組織の組織名
     */
    public String getOrganizationParentName() {
        return this.organizationParentName;
    }

    /**
     * 親組織の組織名を設定する。
     *
     * @param organizationParentName 親組織の組織名
     */
    public void setOrganizationParentName(String organizationParentName) {
        this.organizationParentName = organizationParentName;
    }

    /**
     * 親組織の組織識別名を取得する。
     *
     * @return 親組織の組織識別名
     */
    public String getOrganizationParentIdentName() {
        return this.organizationParentIdentName;
    }

    /**
     * 親組織の組織識別名を設定する。
     *
     * @param organizationParentIdentName 親組織の組織識別名
     */
    public void setOrganizationParentIdentName(String organizationParentIdentName) {
        this.organizationParentIdentName = organizationParentIdentName;
    }

    /**
     * 組織識別名を取得する。
     *
     * @return 組織識別名
     */
    public String getOrganizationIdentName() {
        return this.organizationIdentName;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param organizationIdentName 組織識別名
     */
    public void setOrganizationIdentName(String organizationIdentName) {
        this.organizationIdentName = organizationIdentName;
    }

    /**
     * 工程順階層名を取得する。
     *
     * @return 工程順階層名
     */
    public String getWorkflowParentName() {
        return this.workflowParentName;
    }

    /**
     * 工程順階層名を設定する。
     *
     * @param workflowParentName 工程順階層名
     */
    public void setWorkflowParentName(String workflowParentName) {
        this.workflowParentName = workflowParentName;
    }

    /**
     * 工程順版数を取得する。
     *
     * @return 工程順版数
     */
    public String getWorkflowRevision() {
        return this.workflowRevision;
    }

    /**
     * 工程順版数を設定する。
     *
     * @param workflowRevision 工程順版数
     */
    public void setWorkflowRevision(String workflowRevision) {
        this.workflowRevision = workflowRevision;
    }

    /**
     * 工程階層名を取得する。
     *
     * @return 工程階層名
     */
    public String getWorkParentName() {
        return this.workParentName;
    }

    /**
     * 工程階層名を設定する。
     *
     * @param workParentName 工程階層名
     */
    public void setWorkParentName(String workParentName) {
        this.workParentName = workParentName;
    }

    /**
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public Integer getTaktTime() {
        return this.taktTime;
    }

    /**
     * タクトタイムを設定する。
     *
     * @param taktTime タクトタイム
     */
    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    /**
     * シリアル番号を取得します。
     *
     * @return シリアル番号
     */
    public Integer getSerialNumber() {
        return this.serialNumber;
    }

    /**
     * シリアル番号を設定します。
     *
     * @param serialNumber シリアル番号
     */
    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }


    /**
     * 追加工程フラグを取得する。
     *
     * @return 追加工程フラグ (true: 追加工程, false: 工程順の工程)
     */
    public Boolean getIsSeparateWork() {
        return this.isSeparateWork;
    }

    /**
     * 追加工程フラグを設定する。
     *
     * @param isSeparateWork 追加工程フラグ (true: 追加工程, false: 工程順の工程)
     */
    public void setIsSeparateWork(Boolean isSeparateWork) {
        this.isSeparateWork = isSeparateWork;
    }

    /**
     * 応援かどうかを返す。
     * 
     * @return true: 応援, false: 応援以外
     */
    public boolean isAssist() {
        return Objects.nonNull(assist) && assist == 1;
    }

    /**
     * 応援を設定する。
     * 
     * @param assist 応援
     */
    public void setAssist(Integer assist) {
        this.assist = assist;
    }

    /**
     * 作業やり直し回数を返す。
     *
     * @return 作業やり直し回数
     */
    public Integer getReworkNum() { return this.reworkNum; }

    public void setReworkNum(Integer reworkNum) { this.reworkNum = reworkNum; }

    /**
     * 論理削除フラグ
     * @return 論理削除フラグ
     */
    public Boolean getRemoveFlag() {
        return removeFlag;
    }

    /**
     * 論理削除フラグ
     * @param removeFlag 論理削除フラグ
     */
    public void setRemoveFlag(Boolean removeFlag) {
        this.removeFlag = removeFlag;
    }

    /**
     * シリアル番号を取得する。
     * 
     * @return シリアル番号
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * シリアル番号を設定する。
     * 
     * @param serialNo シリアル番号
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * トレーサビリティデータを取得する。
     * 
     * @return トレーサビリティデータ
     */
    public String getTraceabilities() {
        return traceabilities;
    }

    /**
     * トレーサビリティデータを設定する。
     * 
     * @param traceabilities トレーサビリティデータ
     */
    public void setTraceabilities(String traceabilities) {
        this.traceabilities = traceabilities;
    }

    @Override
    public ActualResultEntity clone() throws CloneNotSupportedException {
      ActualResultEntity clone = (ActualResultEntity) super.clone();
      return clone;
    }
    
    /**
     * 生成日(reporter用)
     * @return 生成日
     */
    public Date getCreateDatetime() {
        return createDatetime;
    }

    /**
     * 生成日(reporter用)
     * @param createDate 生成日
     */
    public void setCreateDatetime(Date createDate) {
        this.createDatetime = createDate;
    }

    /**
     * 作成者
     * @param createOrganizationName 作成者
     */
    public void setCreateOrganizationName(String createOrganizationName) {
        this.createOrganizationName = createOrganizationName;
    }

    /**
     * 作成者
     * @return 作成者
     */
    public String getCreateOrganizationName() {
        return createOrganizationName;
    }

    /**
     * ハッシュ値を返す。
     * 
     * @return ハッシュ値
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (actualId != null ? actualId.hashCode() : 0);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param object オブジェクト
     * @return true:オブジェクトが一致、false:オブジェクトが不一致
     */
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

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ActualResultEntity{")
                .append("actualId=").append(this.actualId)
                .append(", kanbanId=").append(this.kanbanId)
                .append(", workKanbanId=").append(this.workKanbanId)
                .append(", implementDatetime=").append(this.implementDatetime)
                .append(", transactionId=").append(this.transactionId)
                .append(", equipmentId=").append(this.equipmentId)
                .append(", organizationId=").append(this.organizationId)
                .append(", workflowId=").append(this.workflowId)
                .append(", workId=").append(this.workId)
                .append(", actualStatus=").append(this.actualStatus)
                .append(", workingTime=").append(this.workingTime)
                .append(", interruptReason=").append(this.interruptReason)
                .append(", delayReason=").append(this.delayReason)
                .append(", compNum=").append(this.compNum)
                .append(", pairId=").append(this.pairId)
                .append(", nonWorkTime=").append(this.nonWorkTime)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", equipmentName=").append(this.equipmentName)
                .append(", organizationName=").append(this.organizationName)
                .append(", workflowName=").append(this.workflowName)
                .append(", workName=").append(this.workName)
                .append(", verInfo=").append(this.verInfo)
                .append(", defectReason=").append(this.defectReason)
                .append(", defectNum=").append(this.defectNum)
                .append(", assist=").append(this.assist)
                .append(", reworkNum=").append(this.reworkNum)
                .append(", removeFlag=").append(this.removeFlag)
                .append("}")
                .toString();
    }
}
