/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.agenda;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 計画実績情報(VIEW)
 *
 * @author s-heya
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "kanbanTopic")
public class AbstractTopicEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "kanban_id")
    private Long kanbanId;// カンバンID

    @Id
    @Column(name = "work_kanban_id")
    private Long workKanbanId;// 工程カンバンID

    @Id
    @Column(name = "organization_id")
    private Long organizationId;// 組織ID

    @Column(name = "kanban_name")
    private String kanbanName;// カンバン名

    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    //@NotNull
    @Column(name = "kanban_status")
    private KanbanStatusEnum kanbanStatus;// カンバンステータス

    @Column(name = "workflow_name")
    private String workflowName;// 工程順名

    @Column(name = "model_name")
    private String modelName;// モデル名

    @Column(name = "work_name")
    private String workName;// 工程名

    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_kanban_status")
    private KanbanStatusEnum workKanbanStatus;// 工程ステータス

    @Column(name = "equipment_name")
    private String equipmentName;// 設備名

    @Column(name = "organization_name")
    private String organizationName;// 組織名

    @Column(name = "plan_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date planStartTime;// 開始予定日時

    @Column(name = "plan_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date planEndTime;// 完了予定日時

    @Column(name = "actual_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartTime;// 開始日時(実績)

    @Column(name = "actual_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualEndTime;// 完了日時(実績)

    @Column(name = "font_color")
    private String fontColor;// 工程の文字色

    @Column(name = "back_color")
    private String backColor;// 工程の背景色

    @Column(name = "sum_times")
    private Long sumTimes;// 作業累計時間

    @Column(name = "takt_time")
    private Integer taktTime;// タクトタイム

    @Column(name = "work_id")
    private Long workId;// 工程ID

    @Transient
    private Long parentId;// 親ID

    @Column(name = "workflow_rev")
    private Integer workflowRev;// 工程順の版数

    @Column(name = "kanban_plan_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date kanbanPlanStartTime;// カンバンの開始予定日時

    @Column(name = "kanban_plan_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date kanbanPlanEndTime;// カンバンの完了予定日時

    @Column(name = "kanban_actual_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date kanbanActualStartTime;// カンバンの最初の開始実績日時

    @Column(name = "kanban_actual_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date kanbanActualEndTime;// カンバンの最後の完了実績日時

    @Column(name = "work_kanban_order")
    private Integer workKanbanOrder;// 工程カンバンの表示順

    @Column(name = "separate_work_flag")
    private Boolean separateWorkFlag;// 追加工程

    @Column(name = "worker_name")
    private String workerName;// 作業者名

    /**
     * コンストラクタ
     */
    public AbstractTopicEntity() {
    }

    /**
     * コンストラクタ
     * 
     * @param organizationId 組織ID
     * @param parentId 親組織ID
     * @param other 計画実績情報
     */
    public AbstractTopicEntity(Long organizationId, Long parentId, AbstractTopicEntity other) {
        this.kanbanId = other.kanbanId;
        this.workKanbanId = other.workKanbanId;
        this.organizationId = organizationId;
        this.kanbanName = other.kanbanName;
        this.kanbanStatus = other.kanbanStatus;
        this.workflowName = other.workflowName;
        this.modelName = other.modelName;
        this.workName = other.workName;
        this.workKanbanStatus = other.workKanbanStatus;
        this.equipmentName = other.equipmentName;
        this.organizationName = other.organizationName;
        this.planStartTime = other.planStartTime;
        this.planEndTime = other.planEndTime;
        this.actualStartTime = other.actualStartTime;
        this.actualEndTime = other.actualEndTime;
        this.fontColor = other.fontColor;
        this.backColor = other.backColor;
        this.sumTimes = other.sumTimes;
        this.taktTime = other.taktTime;
        this.workId = other.workId;
        this.parentId = parentId;
        this.workflowRev = other.workflowRev;
        this.kanbanPlanStartTime = other.kanbanPlanStartTime;
        this.kanbanPlanEndTime = other.kanbanPlanEndTime;
        this.kanbanActualStartTime = other.kanbanActualStartTime;
        this.kanbanActualEndTime = other.kanbanActualEndTime;
        this.workKanbanOrder = other.workKanbanOrder;
        this.separateWorkFlag = other.separateWorkFlag;
        this.workerName = other.workerName;
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
     * 工程ステータスを取得する。
     *
     * @return 工程カンバンステータス
     */
    public KanbanStatusEnum getWorkKanbanStatus() {
        return this.workKanbanStatus;
    }

    /**
     * 工程ステータスを設定する。
     *
     * @param workKanbanStatus 工程カンバンステータス
     */
    public void setWorkKanbanStatus(KanbanStatusEnum workKanbanStatus) {
        this.workKanbanStatus = workKanbanStatus;
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
     * 開始予定日時を取得する。
     *
     * @return 開始予定日時
     */
    public Date getPlanStartTime() {
        return this.planStartTime;
    }

    /**
     * 開始予定日時を設定する。
     *
     * @param planStartTime 開始予定日時
     */
    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    /**
     * 完了予定日時を取得する。
     *
     * @return 完了予定日時
     */
    public Date getPlanEndTime() {
        return this.planEndTime;
    }

    /**
     * 完了予定日時を設定する。
     *
     * @param planEndTime 完了予定日時
     */
    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
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
    public Date getActualEndTime() {
        return this.actualEndTime;
    }

    /**
     * 完了日時(実績)を設定する。
     *
     * @param actualEndTime 完了日時(実績)
     */
    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    /**
     * 工程の文字色を取得する。
     *
     * @return 工程の文字色
     */
    public String getFontColor() {
        return this.fontColor;
    }

    /**
     * 工程の文字色を設定する。
     *
     * @param fontColor 工程の文字色
     */
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * 工程の背景色を取得する。
     *
     * @return 工程の背景色
     */
    public String getBackColor() {
        return this.backColor;
    }

    /**
     * 工程の背景色を設定する。
     *
     * @param backColor 工程の背景色
     */
    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    /**
     * 作業累計時間を取得する。
     *
     * @return 作業累計時間
     */
    public Long getSumTimes() {
        return this.sumTimes;
    }

    /**
     * 作業累計時間を設定する。
     *
     * @param sumTimes 作業累計時間
     */
    public void setSumTimes(Long sumTimes) {
        this.sumTimes = sumTimes;
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
     * 親IDを取得する。
     *
     * @return 親ID
     */
    public Long getParentId() {
        return this.parentId;
    }

    /**
     * 親IDを設定する。
     *
     * @param parentId 親ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 工程順の版数を取得する。
     *
     * @return 工程順の版数
     */
    public Integer getWorkflowRev() {
        return this.workflowRev;
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
     * カンバンの開始予定日時を取得する。
     *
     * @return カンバンの開始予定日時
     */
    public Date getKanbanPlanStartTime() {
        return this.kanbanPlanStartTime;
    }

    /**
     * カンバンの開始予定日時を設定する。
     *
     * @param kanbanPlanStartTime カンバンの開始予定日時
     */
    public void setKanbanPlanStartTime(Date kanbanPlanStartTime) {
        this.kanbanPlanStartTime = kanbanPlanStartTime;
    }

    /**
     * カンバンの完了予定日時を取得する。
     *
     * @return カンバンの完了予定日時
     */
    public Date getKanbanPlanEndTime() {
        return this.kanbanPlanEndTime;
    }

    /**
     * カンバンの完了予定日時を設定する。
     *
     * @param kanbanPlanEndTime カンバンの完了予定日時
     */
    public void setKanbanPlanEndTime(Date kanbanPlanEndTime) {
        this.kanbanPlanEndTime = kanbanPlanEndTime;
    }

    /**
     * カンバンの開始実績日時を取得する。
     *
     * @return カンバンの開始実績日時
     */
    public Date getKanbanActualStartTime() {
        return this.kanbanActualStartTime;
    }

    /**
     * カンバンの開始実績日時を設定する。
     *
     * @param kanbanActualStartTime カンバンの開始実績日時
     */
    public void setKanbanActualStartTime(Date kanbanActualStartTime) {
        this.kanbanActualStartTime = kanbanActualStartTime;
    }

    /**
     * カンバンの完了実績日時を取得する。
     *
     * @return カンバンの完了実績日時
     */
    public Date getKanbanActualEndTime() {
        return this.kanbanActualEndTime;
    }

    /**
     * カンバンの完了実績日時を設定する。
     *
     * @param kanbanActualEndTime カンバンの完了実績日時
     */
    public void setKanbanActualEndTime(Date kanbanActualEndTime) {
        this.kanbanActualEndTime = kanbanActualEndTime;
    }

    /**
     * 工程カンバンの表示順を取得する。
     *
     * @return 工程カンバンの表示順
     */
    public Integer getWorkKanbanOrder() {
        return this.workKanbanOrder;
    }

    /**
     * 工程カンバンの表示順を設定する。
     *
     * @param workKanbanOrder 工程カンバンの表示順
     */
    public void setWorkKanbanOrder(Integer workKanbanOrder) {
        this.workKanbanOrder = workKanbanOrder;
    }

    /**
     * 追加工程かどうかを返す。
     * 
     * @return true:追加工程、false:通常工程
     */
    public Boolean isSeparateWorkFlag() {
        return this.separateWorkFlag;
    }

    /**
     * 作業者名を取得する。
     *
     * @return 作業者名
     */
    public String getWorkerName() {
        return this.workerName;
    }

    /**
     * 作業者名を設定する。
     *
     * @param workerName 作業者名
     */
    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.kanbanId);
        hash = 59 * hash + Objects.hashCode(this.workKanbanId);
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
        final AbstractTopicEntity other = (AbstractTopicEntity) obj;
        if (!Objects.equals(this.kanbanId, other.kanbanId)) {
            return false;
        }
        return Objects.equals(this.workKanbanId, other.workKanbanId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("AbstractTopicEntity{")
                .append("kanbanId=").append(this.kanbanId)
                .append(", workKanbanId=").append(this.workKanbanId)
                .append(", organizationId=").append(this.organizationId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", kanbanStatus=").append(this.kanbanStatus)
                .append(", workflowName=").append(this.workflowName)
                .append(", modelName=").append(this.modelName)
                .append(", workName=").append(this.workName)
                .append(", workKanbanStatus=").append(this.workKanbanStatus)
                .append(", equipmentName=").append(this.equipmentName)
                .append(", organizationName=").append(this.organizationName)
                .append(", planStartTime=").append(this.planStartTime)
                .append(", planEndTime=").append(this.planEndTime)
                .append(", actualStartTime=").append(this.actualStartTime)
                .append(", actualEndTime=").append(this.actualEndTime)
                .append(", fontColor=").append(this.fontColor)
                .append(", backColor=").append(this.backColor)
                .append(", sumTimes=").append(this.sumTimes)
                .append(", taktTime=").append(this.taktTime)
                .append(", workId=").append(this.workId)
                .append(", parentId=").append(this.parentId)
                .append(", workflowRev=").append(this.workflowRev)
                .append(", kanbanPlanStartTime=").append(this.kanbanPlanStartTime)
                .append(", kanbanPlanEndTime=").append(this.kanbanPlanEndTime)
                .append(", kanbanActualStartTime=").append(this.kanbanActualStartTime)
                .append(", kanbanActualEndTime=").append(this.kanbanActualEndTime)
                .append(", workKanbanOrder=").append(this.workKanbanOrder)
                .append(", separateWorkFlag=").append(this.separateWorkFlag)
                .append(", workerName=").append(this.workerName)
                .append("}")
                .toString();
    }
}
