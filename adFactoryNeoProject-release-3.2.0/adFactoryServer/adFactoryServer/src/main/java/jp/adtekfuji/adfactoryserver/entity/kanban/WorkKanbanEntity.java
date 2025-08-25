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
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.kanban.WorkKanbanGroupKey;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adfactoryserver.utility.PgJsonbConverter;

/**
 * 工程カンバン情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "trn_work_kanban")
@XmlRootElement(name = "workKanban")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedNativeQueries({
    @NamedNativeQuery(name = "WorkKanbanEntity.findProductByUID", query = "SELECT wkan.work_kanban_id FROM trn_work_kanban wkan " +
        "JOIN jsonb_array_elements(wkan.service_info) items(item) ON item->>'service' = 'product' " +
        "JOIN jsonb_array_elements(item::jsonb->'job') job ON job->>'uid' = ?1 AND job->>'status' != 'COMPLETION' AND (job->>'implement')::boolean = true " +
        "WHERE wkan.kanban_id = ?2 AND wkan.implement_flag = true AND wkan.separate_work_flag = false " +
        "ORDER BY wkan.work_kanban_id"),
})
@NamedQueries({
    // カンバンID・追加工程フラグを指定して、工程カンバン情報一覧を取得する。
    @NamedQuery(name = "WorkKanbanEntity.findByKanbanIdAndSeparateFlg", query = "SELECT w FROM WorkKanbanEntity w WHERE w.kanbanId = :kanbanId AND w.separateWorkFlag = :separateWorkFlag ORDER BY w.startDatetime, w.workKanbanId"),
    // カンバンID・追加工程フラグを指定して、工程カンバン情報の件数を取得する。
    @NamedQuery(name = "WorkKanbanEntity.countByKanbanIdAndSeparateFlg", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanEntity w WHERE w.kanbanId = :kanbanId AND w.separateWorkFlag = :separateWorkFlag"),

    // カンバンID・ステータスを指定して、工程カンバンの件数を取得する。
    @NamedQuery(name = "WorkKanbanEntity.countByIdAndStatus", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanEntity w WHERE w.kanbanId = :kanbanId AND w.workStatus IN :workStatuses AND w.skipFlag = false"),
    // カンバンID・ステータスを指定して、指定したステータスかスキップ指定されている工程カンバンの件数を取得する。
    @NamedQuery(name = "WorkKanbanEntity.countCompOrSkip", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanEntity w WHERE w.kanbanId = :kanbanId AND (w.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION OR w.skipFlag = true)"),

    // カンバンID・工程順ID・工程ID・追加工程フラグを指定して、工程カンバン情報を取得する。
    @NamedQuery(name = "WorkKanbanEntity.findByKanbanWorkflowWorkSeparate", query = "SELECT w FROM WorkKanbanEntity w WHERE w.kanbanId = :kanbanId AND w.workflowId = :workflowId AND w.workId = :workId AND w.separateWorkFlag = :separateWorkFlag"),

    // 工程順ID・工程ID・追加工程フラグを指定して、工程カンバン情報を取得する。
    @NamedQuery(name = "WorkKanbanEntity.findByWorkflowWorkSeparate", query = "SELECT w FROM WorkKanbanEntity w WHERE w.workflowId = :workflowId AND w.workId = :workId AND w.separateWorkFlag = :separateWorkFlag"),

    @NamedQuery(name = "WorkKanbanEntity.findByWorkKanbanId", query = "SELECT w FROM WorkKanbanEntity w WHERE w.workKanbanId = :workKanbanId ORDER BY w.startDatetime, w.workKanbanId"),
    // 工程カンバンID一覧を指定して、工程カンバン情報一覧を取得する。
    @NamedQuery(name = "WorkKanbanEntity.findByWorkkanbanIds", query = "SELECT w FROM WorkKanbanEntity w WHERE w.workKanbanId IN :workKanbanId ORDER BY w.startDatetime, w.workKanbanId"),

    // 工程カンバンID一覧を指定して、工程カンバン情報一覧を取得する。
    @NamedQuery(name = "WorkKanbanEntity.findByKanbanIds", query = "SELECT w FROM WorkKanbanEntity w WHERE w.kanbanId IN :kanbanIds"),

    // カンバンIDを指定して、工程カンバン情報一覧を取得する。
    @NamedQuery(name = "WorkKanbanEntity.findByKanbanId", query = "SELECT w FROM WorkKanbanEntity w WHERE w.kanbanId = :kanbanId ORDER BY w.startDatetime, w.workKanbanId"),

    @NamedQuery(name = "WorkKanbanEntity.findByWorkNameOnly", query = "SELECT w FROM WorkKanbanEntity w LEFT JOIN WorkEntity m ON m.workId = w.workId WHERE m.workName = :workName"),
    // カンバンID・工程名を指定して、工程カンバン情報を取得する。
    @NamedQuery(name = "WorkKanbanEntity.findByWorkName", query = "SELECT w FROM WorkKanbanEntity w LEFT JOIN WorkEntity m ON m.workId = w.workId WHERE w.kanbanId = :kanbanId AND m.workName = :workName"),
    // カンバンID・工程ID・シリアル番号を指定して、工程カンバン情報一覧を取得する。
    @NamedQuery(name = "WorkKanbanEntity.findByWorkIdAndSerialNumber", query = "SELECT w FROM WorkKanbanEntity w WHERE w.kanbanId = :kanbanId AND w.workId = :workId AND w.serialNumber = :serialNumber"),
    // カンバンID・シリアル番号を指定して、工程カンバン情報一覧を取得する。
    @NamedQuery(name = "WorkKanbanEntity.findBySerialNumber", query = "SELECT w FROM WorkKanbanEntity w WHERE w.kanbanId = :kanbanId AND w.serialNumber = :serialNumber"),

    // 工程IDから実施フラグを更新 (ロット生産の工程順を進める時に使用)
    @NamedQuery(name = "WorkKanbanEntity.updateImplementFlagByWorkId", query = "UPDATE WorkKanbanEntity w SET w.implementFlag = true WHERE w.kanbanId = :kanbanId AND w.workId = :workId"),
    // 工程IDとシリアル番号から実施フラグを更新 (ロット生産の工程順を進める時に使用)
    @NamedQuery(name = "WorkKanbanEntity.updateImplementFlagBySerialNumber", query = "UPDATE WorkKanbanEntity w SET w.implementFlag = true WHERE w.kanbanId = :kanbanId AND w.workId = :workId AND w.serialNumber = :serialNumber"),
    // 工程カンバンID一覧から実施フラグを更新
    @NamedQuery(name = "WorkKanbanEntity.updateOutputFlagByWorkKanbanIds", query = "UPDATE WorkKanbanEntity w SET w.needActualOutputFlag = :needActualOutputFlag, w.actualOutputDatetime = :actualOutputDatetime WHERE w.workKanbanId IN :workKanbanIds"),
    // 生産可能な工程カンバンを問い合わせ
//    @NamedQuery(name = "WorkKanbanEntity.findProduct", query = "SELECT wk FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workkanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId = :organizationId) AND wk.workKanbanId IN (SELECT e.workkanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId = :equipmentId) ORDER BY wk.startDatetime, wk.workKanbanId"),
    // 生産可能な工程カンバンを問い合わせ(日付付き)
//    @NamedQuery(name = "WorkKanbanEntity.findProductUntillDay", query = "SELECT wk FROM WorkKanbanEntity wk WHERE wk.implementFlag = true AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.actualStartTime < :toDate AND wk.workKanbanId IN (SELECT o.workkanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId = :organizationId) AND wk.workKanbanId IN (SELECT e.workkanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId = :equipmentId) ORDER BY wk.startDatetime, wk.workKanbanId"),

    // 工程ID一覧・実績日時の範囲を指定して、工程別生産情報を取得する。
    @NamedQuery(name = "ProductivityEntity.completionByWorkId", query = "SELECT NEW jp.adtekfuji.andon.entity.ProductivityEntity(a.workId, COUNT(a.workId)) FROM WorkKanbanEntity a WHERE a.actualCompTime >= :fromDate AND a.actualCompTime < :toDate AND a.workId IN :workIds AND a.workStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION GROUP BY a.workId"),
    // 1日の完了数を取得する。
    @NamedQuery(name = "WorkKanbanEntity.countCompletionForDay", query = "SELECT COUNT(wk.workKanbanId) FROM WorkKanbanEntity wk WHERE wk.workId = :workId AND wk.workflowId = :workflowId AND wk.actualStartTime >= :fromDate AND wk.actualCompTime < :toDate"),
    // 次の工程カンバンを取得する。
    @NamedQuery(name = "WorkKanbanEntity.findNext", query = "SELECT wk FROM WorkKanbanEntity wk WHERE wk.kanbanId = :kanbanId AND wk.workId IN :workIds AND wk.implementFlag = true AND wk.skipFlag = false AND wk.workStatus != jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION AND wk.workKanbanId IN (SELECT o.workKanbanId FROM ConWorkkanbanOrganizationEntity o WHERE o.organizationId IN :organizationIds) AND wk.workKanbanId IN (SELECT e.workKanbanId FROM ConWorkkanbanEquipmentEntity e WHERE e.equipmentId IN :equipmentIds) ORDER BY wk.startDatetime, wk.workKanbanId"),
    // 工程カンバンIDを指定して、工程カンバンの詳細情報(カンバン名・工程順名・工程名等)を取得する。
//    @NamedQuery(name = "WorkKanbanEntity.findDetails", query = "SELECT k.kanbanName, k.kanbanStatus, wf.workflowName, wf.workflowRev, w.workName, w.content, w.contentType FROM WorkKanbanEntity wk LEFT JOIN KanbanEntity k on k.kanbanId = wk.kanbanId LEFT JOIN WorkflowEntity wf on wf.workflowId = wk.workflowId LEFT JOIN WorkEntity w on w.workId = wk.workId WHERE wk.workKanbanId = :workKanbanId"),
    @NamedQuery(name = "WorkKanbanEntity.findDetails", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanDetail(k.kanbanName, k.kanbanStatus, wf.workflowName, wf.workflowRev, w.workName, w.content, w.contentType) FROM WorkKanbanEntity wk LEFT JOIN KanbanEntity k on k.kanbanId = wk.kanbanId LEFT JOIN WorkflowEntity wf on wf.workflowId = wk.workflowId LEFT JOIN WorkEntity w on w.workId = wk.workId WHERE wk.workKanbanId = :workKanbanId"),
    // 最終実績IDを更新する。
    @NamedQuery(name = "WorkKanbanEntity.updateLastActualId", query = "UPDATE WorkKanbanEntity w SET w.lastActualId = :lastActualId WHERE w.workKanbanId = :workKanbanId"),
    // 累計作業時間を更新する。
    @NamedQuery(name = "WorkKanbanEntity.updateSumTimes", query = "UPDATE WorkKanbanEntity w SET w.sumTimes = :sumTimes WHERE w.workKanbanId = :workKanbanId"),
})
public class WorkKanbanEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "work_kanban_id")
    private Long workKanbanId;// 工程カンバンID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "kanban_id")
    @XmlElement(name = "fkKanbanId")
    private Long kanbanId;// カンバンID

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

    @Basic(optional = false)
    //@NotNull
    @Column(name = "separate_work_flag")
    private Boolean separateWorkFlag;// 追加工程フラグ

    @Basic(optional = false)
    //@NotNull
    @Column(name = "implement_flag")
    private Boolean implementFlag;// 実施フラグ

    @Basic(optional = false)
    //@NotNull
    @Column(name = "skip_flag")
    private Boolean skipFlag;// スキップフラグ

    @Column(name = "start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDatetime;// 開始予定日時

    @Column(name = "comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compDatetime;// 完了予定日時

    @Column(name = "takt_time")
    private Integer taktTime;// タクトタイム[ms]

    @Column(name = "sum_times")
    private Long sumTimes;// 作業累計時間[ms]

    @Column(name = "update_person_id")
    @XmlElement(name = "fkUpdatePersonId")
    private Long updatePersonId;// 更新者(組織ID)

    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;// 更新日時

    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_status")
    private KanbanStatusEnum workStatus;// 工程ステータス

    @Column(name = "interrupt_reason_id")
    @XmlElement(name = "fkInterruptReasonId")
    private Long interruptReasonId;// 中断理由ID

    @Column(name = "delay_reason_id")
    @XmlElement(name = "fkDelayReasonId")
    private Long delayReasonId;// 遅延理由ID

    @Column(name = "work_kanban_order")
    private Integer workKanbanOrder;// 表示順

    @Column(name = "serial_number")
    private Integer serialNumber;// シリアル番号

    @Column(name = "sync_work")
    private Boolean syncWork;// 同時作業フラグ

    @Column(name = "actual_start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartTime;// 開始日時(実績)

    @Column(name = "actual_comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualCompTime;// 完了日時(実績)

    @Column(name = "actual_num1")
    private Integer actualNum1;// A品実績数

    @Column(name = "actual_num2")
    private Integer actualNum2;// B品実績数

    @Column(name = "actual_num3")
    private Integer actualNum3;// C品実績数

    @Column(name = "rework_num")
    private Integer reworkNum;// 作業やり直し回数

    @Column(name = "work_kanban_add_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String workKanbanAddInfo;// 追加情報(JSON)

    @Column(name = "service_info", length = 30000)
    @Convert(converter = PgJsonbConverter.class)
    private String serviceInfo;// サービス情報(JSON)

    @Column(name = "need_actual_output_flag")
    private Boolean needActualOutputFlag = false;// 要実績出力フラグ
    
    @Column(name = "actual_output_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualOutputDatetime;// 実績出力日時

    @Column(name = "last_actual_id")
    private Long lastActualId;// 最終実績ID

    @Transient
    private String kanbanName;// カンバン名

    @Transient
    private String workflowName;// 工程順名

    @Transient
    private String workName;// 工程名

    @Transient
    private String content;// コンテンツ

    @Transient
    private ContentTypeEnum contentType;// コンテンツ種別

    @Transient
    private KanbanStatusEnum kanbanStatus;// カンバンステータス

    @XmlElementWrapper(name = "equipments")
    @XmlElement(name = "equipment")
    @Transient
    private List<Long> equipmentCollection = null;// 設備ID一覧

    @XmlElementWrapper(name = "organizations")
    @XmlElement(name = "organization")
    @Transient
    private List<Long> organizationCollection = null;// 組織ID一覧

    /**
     * コンストラクタ
     */
    public WorkKanbanEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param kanbanId カンバンID
     * @param workflowId 工程カンバンID
     * @param workId 工程ID
     * @param workName 工程名
     * @param separateWorkFlag 追加工程フラグ
     * @param implementFlag 実施フラグ
     * @param skipFlag スキップフラグ
     * @param startDatetime 開始予定日時
     * @param compDatetime 完了予定日時
     * @param taktTime タクトタイム[ms]
     * @param sumTimes 作業累計時間[ms]
     * @param updatePersonId 更新者(組織ID)
     * @param updateDatetime 更新日時
     * @param workStatus 工程ステータス
     * @param interruptReasonId 中断理由ID
     * @param delayReasonId 遅延理由ID
     * @param workKanbanOrder 表示順
     */
    public WorkKanbanEntity(Long kanbanId, Long workflowId, Long workId, String workName, Boolean separateWorkFlag, Boolean implementFlag, Boolean skipFlag, Date startDatetime, Date compDatetime, Integer taktTime, Long sumTimes, Long updatePersonId, Date updateDatetime, KanbanStatusEnum workStatus, Long interruptReasonId, Long delayReasonId, Integer workKanbanOrder) {
        this.kanbanId = kanbanId;
        this.workflowId = workflowId;
        this.workId = workId;
        this.workName = workName;
        this.separateWorkFlag = separateWorkFlag;
        this.implementFlag = implementFlag;
        this.skipFlag = skipFlag;
        this.startDatetime = startDatetime;
        this.compDatetime = compDatetime;
        this.taktTime = taktTime;
        this.sumTimes = sumTimes;
        this.updatePersonId = updatePersonId;
        this.updateDatetime = updateDatetime;
        this.workStatus = workStatus;
        this.interruptReasonId = interruptReasonId;
        this.delayReasonId = delayReasonId;
        this.workKanbanOrder = workKanbanOrder;
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
     * 追加工程フラグを取得する。
     *
     * @return 追加工程フラグ
     */
    public Boolean getSeparateWorkFlag() {
        return this.separateWorkFlag;
    }

    /**
     * 追加工程フラグを設定する。
     *
     * @param separateWorkFlag 追加工程フラグ
     */
    public void setSeparateWorkFlag(Boolean separateWorkFlag) {
        this.separateWorkFlag = separateWorkFlag;
    }

    /**
     * 実施フラグを取得する。
     *
     * @return 実施フラグ
     */
    public Boolean getImplementFlag() {
        return this.implementFlag;
    }

    /**
     * 実施フラグを設定する。
     *
     * @param implementFlag 実施フラグ
     */
    public void setImplementFlag(Boolean implementFlag) {
        this.implementFlag = implementFlag;
    }

    /**
     * スキップフラグを取得する。
     *
     * @return スキップフラグ
     */
    public Boolean getSkipFlag() {
        return this.skipFlag;
    }

    /**
     * スキップフラグを設定する。
     *
     * @param skipFlag スキップフラグ
     */
    public void setSkipFlag(Boolean skipFlag) {
        this.skipFlag = skipFlag;
    }

    /**
     * 開始予定日時を取得する。
     *
     * @return 開始予定日時
     */
    public Date getStartDatetime() {
        return this.startDatetime;
    }

    /**
     * 開始予定日時を設定する。
     *
     * @param startDatetime 開始予定日時
     */
    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    /**
     * 完了予定日時を取得する。
     *
     * @return 完了予定日時
     */
    public Date getCompDatetime() {
        return this.compDatetime;
    }

    /**
     * 完了予定日時を設定する。
     *
     * @param compDatetime 完了予定日時
     */
    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    /**
     * タクトタイム[ms]を取得する。
     *
     * @return タクトタイム[ms]
     */
    public Integer getTaktTime() {
        return this.taktTime;
    }

    /**
     * タクトタイム[ms]を設定する。
     *
     * @param taktTime タクトタイム[ms]
     */
    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    /**
     * 作業累計時間[ms]を取得する。
     *
     * @return 作業累計時間[ms]
     */
    public Long getSumTimes() {
        return this.sumTimes;
    }

    /**
     * 作業累計時間[ms]を設定する。
     *
     * @param sumTimes 作業累計時間[ms]
     */
    public void setSumTimes(Long sumTimes) {
        this.sumTimes = sumTimes;
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
        return this.updateDatetime;
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
     * 工程ステータスを取得する。
     *
     * @return 工程ステータス
     */
    public KanbanStatusEnum getWorkStatus() {
        return this.workStatus;
    }

    /**
     * 工程ステータスを設定する。
     *
     * @param workStatus 工程ステータス
     */
    public void setWorkStatus(KanbanStatusEnum workStatus) {
        this.workStatus = workStatus;
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
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public Integer getWorkKanbanOrder() {
        return this.workKanbanOrder;
    }

    /**
     * 表示順を設定する。
     *
     * @param workKanbanOrder 表示順
     */
    public void setWorkKanbanOrder(Integer workKanbanOrder) {
        this.workKanbanOrder = workKanbanOrder;
    }

    /**
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public Integer getSerialNumber() {
        return this.serialNumber;
    }

    /**
     * シリアル番号を設定する。
     *
     * @param serialNumber シリアル番号
     */
    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * 同時作業フラグを取得する。
     *
     * @return 同時作業フラグ
     */
    public Boolean isSyncWork() {
        return Objects.nonNull(this.syncWork) ? this.syncWork : Boolean.FALSE;
    }

    /**
     * 同時作業フラグを設定する。
     *
     * @param syncWork 同時作業フラグ
     */
    public void setSyncWork(Boolean syncWork) {
        this.syncWork = syncWork;
    }

    /**
     * 開始日時(実績)を取得する。
     *
     * @return 開始日時(実績)
     */
    public Date getActualStartTime() {
        return this.actualStartTime;
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
        return this.actualCompTime;
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
     * A品実績数を取得する。
     *
     * @return A品実績数
     */
    public Integer getActualNum1() {
        return this.actualNum1;
    }

    /**
     * A品実績数を設定する。
     *
     * @param actualNum1 A品実績数
     */
    public void setActualNum1(Integer actualNum1) {
        this.actualNum1 = actualNum1;
    }

    /**
     * B品実績数を取得する。
     *
     * @return B品実績数
     */
    public Integer getActualNum2() {
        return this.actualNum2;
    }

    /**
     * B品実績数を設定する。
     *
     * @param actualNum2 B品実績数
     */
    public void setActualNum2(Integer actualNum2) {
        this.actualNum2 = actualNum2;
    }

    /**
     * C品実績数を取得する。
     *
     * @return C品実績数
     */
    public Integer getActualNum3() {
        return this.actualNum3;
    }

    /**
     * C品実績数を設定する。
     *
     * @param actualNum3 C品実績数
     */
    public void setActualNum3(Integer actualNum3) {
        this.actualNum3 = actualNum3;
    }

    /**
     * 作業やり直し回数を取得する。
     *
     * @return 作業やり直し回数
     */
    public Integer getReworkNum() {
        return this.reworkNum;
    }

    /**
     * 作業やり直し回数を設定する。
     *
     * @param reworkNum 作業やり直し回数
     */
    public void setReworkNum(Integer reworkNum) {
        this.reworkNum = reworkNum;
    }

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getWorkKanbanAddInfo() {
        return this.workKanbanAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param workKanbanAddInfo 追加情報(JSON)
     */
    public void setWorkKanbanAddInfo(String workKanbanAddInfo) {
        this.workKanbanAddInfo = workKanbanAddInfo;
    }

    /**
     * サービス情報を取得する。
     *
     * @return サービス情報
     */
    public String getServiceInfo() {
        return this.serviceInfo;
    }

    /**
     * サービス情報を設定する。
     *
     * @param serviceInfo サービス情報
     */
    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
    
    /**
     * 要実績出力フラグを取得する。
     *
     * @return 要実績出力フラグ
     */
    public Boolean getNeedActualOutputFlag() {
        if (Objects.isNull(needActualOutputFlag)) {
            needActualOutputFlag = false;
        }
        return this.needActualOutputFlag;
    }

    /**
     * 要実績出力フラグを設定する。
     *
     * @param needActualOutputFlag 要実績出力フラグ
     */
    public void setNeedActualOutputFlag(Boolean needActualOutputFlag) {
        this.needActualOutputFlag = needActualOutputFlag;
    }
    
    /**
     * 実績出力日時を取得する。
     *
     * @return 実績出力日時
     */
    public Date getActualOutputDatetime() {
        return this.actualOutputDatetime;
    }

    /**
     * 実績出力日時を設定する。
     *
     * @param actualOutputDatetime 実績出力日時
     */
    public void setActualOutputDatetime(Date actualOutputDatetime) {
        this.actualOutputDatetime = actualOutputDatetime;
    }

    /**
     * 最終実績IDを取得する。
     *
     * @return 最終実績ID
     */
    public Long getLastActualId() {
        return this.lastActualId;
    }

    /**
     * 最終実績IDを設定する。
     *
     * @param lastActualId 最終実績ID
     */
    public void setLastActualId(Long lastActualId) {
        this.lastActualId = lastActualId;
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
     * コンテンツを取得する。
     *
     * @return コンテンツ
     */
    public String getContent() {
        return this.content;
    }

    /**
     * コンテンツを設定する。
     *
     * @param content コンテンツ
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * コンテンツ種別を取得する。
     *
     * @return コンテンツ種別
     */
    public ContentTypeEnum getContentType() {
        return this.contentType;
    }

    /**
     * コンテンツ種別を設定する。
     *
     * @param contentType コンテンツ種別
     */
    public void setContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
    }

    /**
     * カンバンステータスを取得する。
     *
     * @return カンバンステータス
     */
    public KanbanStatusEnum getKanbanStatus() {
        return this.kanbanStatus;
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
     * 設備ID一覧を取得する。
     *
     * @return 設備ID一覧
     */
    public List<Long> getEquipmentCollection() {
        return this.equipmentCollection;
    }

    /**
     * 設備ID一覧を設定する。
     *
     * @param equipmentCollection 設備ID一覧
     */
    public void setEquipmentCollection(List<Long> equipmentCollection) {
        this.equipmentCollection = equipmentCollection;
    }

    /**
     * 組織ID一覧を取得する。
     *
     * @return 組織ID一覧
     */
    public List<Long> getOrganizationCollection() {
        return this.organizationCollection;
    }

    /**
     * 組織ID一覧を設定する。
     *
     * @param organizationCollection 組織ID一覧
     */
    public void setOrganizationCollection(List<Long> organizationCollection) {
        this.organizationCollection = organizationCollection;
    }

    /**
     * 工程グループキーを取得する。
     *
     * @return 工程グループキー
     */
    public WorkKanbanGroupKey getGroupKey() {
        return WorkKanbanGroupKey.createGroupKey(this.getStartDatetime(), this.getWorkflowName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.workKanbanId);
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
        final WorkKanbanEntity other = (WorkKanbanEntity) obj;
        return Objects.equals(this.workKanbanId, other.workKanbanId);
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkKanbanEntity{")
                .append("workKanbanId=").append(this.workKanbanId)
                .append(", kanbanId=").append(this.kanbanId)
                .append(", workflowId=").append(this.workflowId)
                .append(", workId=").append(this.workId)
                .append(", separateWorkFlag=").append(this.separateWorkFlag)
                .append(", implementFlag=").append(this.implementFlag)
                .append(", skipFlag=").append(this.skipFlag)
                .append(", startDatetime=").append(this.startDatetime)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", taktTime=").append(this.taktTime)
                .append(", sumTimes=").append(this.sumTimes)
                .append(", updatePersonId=").append(this.updatePersonId)
                .append(", updateDatetime=").append(this.updateDatetime)
                .append(", workStatus=").append(this.workStatus)
                .append(", interruptReasonId=").append(this.interruptReasonId)
                .append(", delayReasonId=").append(this.delayReasonId)
                .append(", workKanbanOrder=").append(this.workKanbanOrder)
                .append(", serialNumber=").append(this.serialNumber)
                .append(", syncWork=").append(this.syncWork)
                .append(", actualStartTime=").append(this.actualStartTime)
                .append(", actualCompTime=").append(this.actualCompTime)
                .append(", actualNum1=").append(this.actualNum1)
                .append(", actualNum2=").append(this.actualNum2)
                .append(", actualNum3=").append(this.actualNum3)
                .append(", reworkNum=").append(this.reworkNum)
                .append(", needActualOutputFlag=").append(this.needActualOutputFlag)
                .append(", actualOutputDatetime=").append(this.actualOutputDatetime)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", workflowName=").append(this.workflowName)
                .append(", workName=").append(this.workName)
                .append(", content=").append(this.content)
                .append(", contentType=").append(this.contentType)
                .append(", kanbanStatus=").append(this.kanbanStatus)
                .append(", lastActualId=").append(this.lastActualId)
                .append("}")
                .toString();
    }
}
