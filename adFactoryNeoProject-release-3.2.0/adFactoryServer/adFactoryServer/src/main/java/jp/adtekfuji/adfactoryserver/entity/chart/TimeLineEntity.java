/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.chart;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * タイムライン情報(VIEW)
 *
 * @author s-heya
 */
@Entity
@Table(name = "view_actual_result")
@NamedQueries({
    // タイムライン情報を取得する (作業分析グラフで使用)
    @NamedQuery(name = "TimeLineEntity.findForTimeline", query = "SELECT t FROM TimeLineEntity t WHERE t.implementDatetime >= :fromDate AND t.implementDatetime < :toDate AND t.workflowId = :workflowId AND t.workId IN :workIds ORDER BY t.kanbanId, t.implementDatetime"),
    // 対象期間中の生産数
    @NamedQuery(name = "TimeLineEntity.countProduction", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE k.actualStartTime >= :fromDate AND k.actualCompTime <= :toDate AND k.workflowId = :workflowId AND k.kanbanStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION"),
    // 対象期間中の仕掛数
    @NamedQuery(name = "TimeLineEntity.countInProcess", query = "SELECT COUNT(k.kanbanId) FROM KanbanEntity k WHERE ((k.actualStartTime >= :fromDate AND (k.actualCompTime <= :toDate OR k.actualStartTime <= :toDate)) OR (k.actualCompTime <= :toDate AND k.actualCompTime >= :fromDate)) AND k.workflowId = :workflowId"),
    // 対象期間中の中断数
    @NamedQuery(name = "TimeLineEntity.summarySuspend", query = "SELECT NEW jp.adtekfuji.adFactory.entity.chart.SummaryItem('SUSPEND', a.interruptReason, COUNT(a.interruptReason)) FROM ActualResultEntity a WHERE a.implementDatetime >= :fromDate AND a.implementDatetime < :toDate AND a.actualStatus = jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.SUSPEND AND a.interruptReason IS NOT NULL AND a.workflowId = :workflowId AND a.workId IN :workIds GROUP BY a.interruptReason"),
    // 対象期間中の遅延数
    @NamedQuery(name = "TimeLineEntity.summaryDelay", query = "SELECT NEW jp.adtekfuji.adFactory.entity.chart.SummaryItem('DELAY', a.delayReason, COUNT(a.delayReason)) FROM ActualResultEntity a WHERE a.implementDatetime >= :fromDate AND a.implementDatetime < :toDate AND a.delayReason IS NOT NULL AND a.workflowId = :workflowId AND a.workId IN :workIds GROUP BY a.delayReason"),
    // 設備ID一覧・モデル名・日時範囲を指定して、タイムライン情報を取得する。
    @NamedQuery(name = "TimeLineEntity.findByEquipmentIds", query = "SELECT t FROM TimeLineEntity t WHERE t.implementDatetime >= :fromDate AND t.implementDatetime <= :toDate AND t.equipmentId IN :equipmentIds AND t.modelName LIKE :modelName ORDER BY t.implementDatetime, t.actualId"),
})
@XmlRootElement(name = "timeLine")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeLineEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "actual_id")
    private BigInteger actualId;// 実績ID

    @Column(name = "fk_kanban_id")
    @XmlElement(name = "fkKanbanId")
    private BigInteger kanbanId;// カンバンID

    @Column(name = "fk_workflow_id")
    @XmlElement(name = "fkWorkflowId")
    private BigInteger workflowId;// 工程順ID

    @Column(name = "fk_work_kanban_id")
    @XmlElement(name = "fkWorkKanbanId")
    private BigInteger workKanbanId;// 工程カンバンID

    @Column(name = "fk_work_id")
    @XmlElement(name = "fkWorkId")
    private BigInteger workId;// 工程ID

    @Column(name = "fk_organization_id")
    @XmlElement(name = "fkOrganizationId")
    private BigInteger organizationId;// 組織ID

    @Column(name = "fk_equipment_id")
    @XmlElement(name = "fkEquipmentId")
    private BigInteger equipmentId;// 設備ID

    @Size(max = 256)
    @Column(name = "kanban_name")
    private String kanbanName;// カンバン名

    //@Size(max = 256)
    //@Column(name = "workflow_name")
    //private String workflowName;

    @Size(max = 256)
    @Column(name = "work_name")
    private String workName;// 工程名

    @Size(max = 256)
    @Column(name = "organization_name")
    private String organizationName;// 組織名

    @Size(max = 256)
    @Column(name = "equipment_name")
    private String equipmentName;// 設備名

    @Column(name = "implement_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date implementDatetime;// 実施日時

    @Size(max = 256)
    @Column(name = "actual_status")
    private String actualStatus;// 工程実績ステータス

    @Column(name = "work_time")
    private Integer workTime;// 作業時間[ms]

    @Size(max = 256)
    @Column(name = "interrupt_reason")
    private String interruptReason;// 中断理由

    @Size(max = 256)
    @Column(name = "delay_reason")
    private String delayReason;// 遅延理由

    @Size(max = 256)
    @Column(name = "model_name")
    private String modelName;// モデル名

    @Column(name = "work_rev")
    private Integer workRev; // 工程の版数

    /**
     * コンストラクタ
     */
    public TimeLineEntity() {
    }

    /**
     * 実績IDを取得する。
     *
     * @return 実績ID
     */
    public BigInteger getActualId() {
        return this.actualId;
    }

    /**
     * 実績IDを設定する。
     *
     * @param actualId 実績ID
     */
    public void setActualId(BigInteger actualId) {
        this.actualId = actualId;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public BigInteger getKanbanId() {
        return this.kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(BigInteger kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public BigInteger getWorkflowId() {
        return this.workflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(BigInteger workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * 工程カンバンIDを取得する。
     *
     * @return 工程カンバンID
     */
    public BigInteger getWorkKanbanId() {
        return this.workKanbanId;
    }

    /**
     * 工程カンバンIDを設定する。
     *
     * @param workKanbanId 工程カンバンID
     */
    public void setWorkKanbanId(BigInteger workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    /**
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public BigInteger getWorkId() {
        return this.workId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param workId 工程ID
     */
    public void setWorkId(BigInteger workId) {
        this.workId = workId;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public BigInteger getOrganizationId() {
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(BigInteger organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 設備IDを取得する。
     *
     * @return 設備ID
     */
    public BigInteger getEquipmentId() {
        return this.equipmentId;
    }

    /**
     * 設備IDを設定する。
     *
     * @param equipmentId 設備ID
     */
    public void setEquipmentId(BigInteger equipmentId) {
        this.equipmentId = equipmentId;
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
     * 工程実績ステータスを取得する。
     *
     * @return 工程実績ステータス
     */
    public String getActualStatus() {
        return this.actualStatus;
    }

    /**
     * 工程実績ステータスを設定する。
     *
     * @param actualStatus 工程実績ステータス
     */
    public void setActualStatus(String actualStatus) {
        this.actualStatus = actualStatus;
    }

    /**
     * 作業時間[ms]を取得する。
     *
     * @return 作業時間[ms]
     */
    public Integer getWorkTime() {
        return this.workTime;
    }

    /**
     * 作業時間[ms]を設定する。
     *
     * @param workTime 作業時間[ms]
     */
    public void setWorkTime(Integer workTime) {
        this.workTime = workTime;
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

    /**
     * 工程の版数を取得する。
     *
     * @return 工程の版数
     */
    public Integer getWorkRev() {
        return this.workRev;
    }

    /**
     * 工程の版数を設定する。
     *
     * @param workRev 工程の版数
     */
    public void setWorkRev(Integer workRev) {
        this.workRev = workRev;
    }

    @Override
    public String toString() {
        return new StringBuilder("TimeLineEntity{")
                .append(", actualId=").append(this.actualId)
                .append(", kanbanId=").append(this.kanbanId)
                .append(", workflowId=").append(this.workflowId)
                .append(", workKanbanId=").append(this.workKanbanId)
                .append(", workId=").append(this.workId)
                .append(", organizationId=").append(this.organizationId)
                .append(", equipmentId=").append(this.equipmentId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", workName=").append(this.workName)
                .append(", organizationName=").append(this.organizationName)
                .append(", equipmentName=").append(this.equipmentName)
                .append(", implementDatetime=").append(this.implementDatetime)
                .append(", actualStatus=").append(this.actualStatus)
                .append(", workTime=").append(this.workTime)
                .append(", interruptReason=").append(this.interruptReason)
                .append(", delayReason=").append(this.delayReason)
                .append(", modelName=").append(this.modelName)
                .append(", workRev=").append(this.workRev)
                .append("}")
                .toString();
    }
}
