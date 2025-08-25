/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.actual;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 作業履歴情報(VIEW)
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workRecord")
public class WorkRecordInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long actualId;// 実績ID
    private Long kanbanId;// カンバンID
    private Long workKanbanId;// 工程カンバンID
    private Long workId;// 工程ID
    private Long organizationId;// 組織ID
    private String kanbanName;// カンバン名
    private KanbanStatusEnum kanbanStatus;// カンバンステータス
    private String workflowName;// 工程順名
    private String modelName;// モデル名
    private String workName;// 工程名
    private KanbanStatusEnum workKanbanStatus;// 工程カンバンステータス
    private String organizationName;// 組織名
    private Date planStartTime;// 工程カンバンの開始予定日時
    private Date planEndTime;// 工程カンバンの完了予定日時
    private Date workStartTime;// 工程カンバンの開始日時
    private Date workEndTime;// 工程カンバンの完了日時
    private Date actualStartTime;// 開始実績の日時
    private Date actualEndTime;// 完了実績の日時
    private String fontColor;// 工程の文字色
    private String backColor;// 工程の背景色
    private Long sumTimes;// 作業累計時間
    private Integer taktTime;// タクトタイム
    private Long equipmentId;// 設備ID
    private Integer workKanbanOrder;// 表示順
    private Boolean separateWorkFlag;// 追加工程
    private Integer assist;// 応援
    private KanbanStatusEnum actualStatus; // 実績ステータス
    private Boolean isIndirectData = false; // 間接作業データかどうか
    private String reason; // 理由

    /**
     * コンストラクタ
     */
    public WorkRecordInfoEntity() {
    }

    /**
     * 実績IDを取得する。
     *
     * @return 実績ID
     */
    public Long getActualId() {
        return this.actualId;
    }

    /**
     * 実績IDを設定する。
     *
     * @param actualId 実績ID
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
     * 工程カンバンステータスを取得する。
     *
     * @return 工程カンバンステータス
     */
    public KanbanStatusEnum getWorkKanbanStatus() {
        return this.workKanbanStatus;
    }

    /**
     * 工程カンバンステータスを設定する。
     *
     * @param workKanbanStatus 工程カンバンステータス
     */
    public void setWorkKanbanStatus(KanbanStatusEnum workKanbanStatus) {
        this.workKanbanStatus = workKanbanStatus;
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
     * 工程カンバンの開始予定日時を取得する。
     *
     * @return 工程カンバンの開始予定日時
     */
    public Date getPlanStartTime() {
        return this.planStartTime;
    }

    /**
     * 工程カンバンの開始予定日時を設定する。
     *
     * @param planStartTime 工程カンバンの開始予定日時
     */
    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    /**
     * 工程カンバンの完了予定日時を取得する。
     *
     * @return 工程カンバンの完了予定日時
     */
    public Date getPlanEndTime() {
        return this.planEndTime;
    }

    /**
     * 工程カンバンの完了予定日時を設定する。
     *
     * @param planEndTime 工程カンバンの完了予定日時
     */
    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    /**
     * 工程カンバンの開始日時を取得する。
     *
     * @return 工程カンバンの開始日時
     */
    public Date getWorkStartTime() {
        return this.workStartTime;
    }

    /**
     * 工程カンバンの開始日時を設定する。
     *
     * @param workStartTime 工程カンバンの開始日時
     */
    public void setWorkStartTime(Date workStartTime) {
        this.workStartTime = workStartTime;
    }

    /**
     * 工程カンバンの完了日時を取得する。
     *
     * @return 工程カンバンの完了日時
     */
    public Date getWorkEndTime() {
        return this.workEndTime;
    }

    /**
     * 工程カンバンの完了日時を設定する。
     *
     * @param workEndTime 工程カンバンの完了日時
     */
    public void setWorkEndTime(Date workEndTime) {
        this.workEndTime = workEndTime;
    }

    /**
     * 開始実績の日時を取得する。
     *
     * @return 開始実績の日時
     */
    public Date getActualStartTime() {
        return this.actualStartTime;
    }

    /**
     * 開始実績の日時を設定する。
     *
     * @param actualStartTime 開始実績の日時
     */
    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    /**
     * 完了実績の日時を取得する。
     *
     * @return 完了実績の日時
     */
    public Date getActualEndTime() {
        return this.actualEndTime;
    }

    /**
     * 完了実績の日時を設定する。
     *
     * @param actualEndTime 完了実績の日時
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
        return fontColor;
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
        return backColor;
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
     * 追加工程かどうかを返す。
     * 
     * @return true:追加工程、false:通常工程
     */
    public Boolean isSeparateWorkFlag() {
        if (Objects.isNull(separateWorkFlag)) {
            return false;
        }
        return separateWorkFlag;
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
     * 実績ステータスを取得する。
     * 
     * @return 実績ステータス 
     */
    public KanbanStatusEnum getActualStatus() {
        return actualStatus;
    }

    /**
     * 実績ステータスを設定する。
     * 
     * @param actualStatus 実績ステータス
     */
    public void setActualStatus(KanbanStatusEnum actualStatus) {
        this.actualStatus = actualStatus;
    }
    
    /**
     * 間接作業データかどうかを取得する。
     * 
     * @return true: 間接作業データ、false: 直接作業データ(カンバン)
     */
    public Boolean getIsIndirectData() {
        return isIndirectData;
    }

    /**
     * 間接作業データかどうかを設定する。
     * 
     * @param isIndirectData true: 間接作業データ、false: 直接作業データ(カンバン)
     */
    public void setIsIndirectData(Boolean isIndirectData) {
        this.isIndirectData = isIndirectData;
    }

    /**
     * 理由を取得する。
     * 
     * @return 理由
     */
    public String getReason() {
        return reason;
    }

    /**
     * 理由を設定する。
     * 
     * @param reason 理由
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.actualId);
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
        final WorkRecordInfoEntity other = (WorkRecordInfoEntity) obj;
        return Objects.equals(this.actualId, other.actualId);
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkRecordInfoEntity{")
                .append("actualId=").append(this.actualId)
                .append(", kanbanId=").append(this.kanbanId)
                .append(", workKanbanId=").append(this.workKanbanId)
                .append(", workId=").append(this.workId)
                .append(", organizationId=").append(this.organizationId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", kanbanStatus=").append(this.kanbanStatus)
                .append(", workflowName=").append(this.workflowName)
                .append(", modelName=").append(this.modelName)
                .append(", workName=").append(this.workName)
                .append(", workKanbanStatus=").append(this.workKanbanStatus)
                .append(", organizationName=").append(this.organizationName)
                .append(", planStartTime=").append(this.planStartTime)
                .append(", planEndTime=").append(this.planEndTime)
                .append(", workStartTime=").append(this.workStartTime)
                .append(", workEndTime=").append(this.workEndTime)
                .append(", actualStartTime=").append(this.actualStartTime)
                .append(", actualEndTime=").append(this.actualEndTime)
                .append(", fontColor=").append(this.fontColor)
                .append(", backColor=").append(this.backColor)
                .append(", sumTimes=").append(this.sumTimes)
                .append(", taktTime=").append(this.taktTime)
                .append(", equipmentId=").append(this.equipmentId)
                .append(", workKanbanOrder=").append(this.workKanbanOrder)
                .append(", separateWorkFlag=").append(this.separateWorkFlag)
                .append(", assist=").append(this.assist)
                .append(", isIndirectData=").append(this.isIndirectData)
                .append("}")
                .toString();
    }
}
