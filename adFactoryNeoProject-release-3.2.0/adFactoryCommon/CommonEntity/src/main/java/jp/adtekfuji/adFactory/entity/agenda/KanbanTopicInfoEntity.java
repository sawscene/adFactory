/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.agenda;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * カンバン予実情報
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "kanbanTopic")
public class KanbanTopicInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Long kanbanId;// カンバンID
    @XmlElement()
    private Long workKanbanId;// 工程カンバンID
    @XmlElement()
    private Long organizationId;// 組織ID
    @XmlElement()
    private String kanbanName;// カンバン名
    @XmlElement()
    private KanbanStatusEnum kanbanStatus;// カンバンステータス
    @XmlElement()
    private String workflowName;// 工程順名
    @XmlElement()
    private String modelName;// モデル名
    @XmlElement()
    private String workName;// 工程名
    @XmlElement()
    private KanbanStatusEnum workKanbanStatus;// 工程カンバンステータス
    @XmlElement()
    private String equipmentName;// 設備名
    @XmlElement()
    private String organizationName;// 組織名
    @XmlElement()
    private Date planStartTime;// カンバンの開始予定日時
    @XmlElement()
    private Date planEndTime;// カンバンの完了予定日時
    @XmlElement()
    private Date actualStartTime;// 工程カンバンの最初の実績日時
    @XmlElement()
    private Date actualEndTime;// 工程カンバンの最後の実績日時
    @XmlElement()
    private String fontColor;// 工程の文字色
    @XmlElement()
    private String backColor;// 工程の背景色
    @XmlElement()
    private Long sumTimes;// 作業累計時間
    @XmlElement()
    private Integer taktTime;// タクトタイム
    @XmlElement()
    private Long workId;// 工程ID

    private Long parentId;// 親ID

    @XmlElement()
    private Integer workflowRev;// 工程順の版数
    @XmlElement()
    private Date kanbanPlanStartTime;// 開始予定日時
    @XmlElement()
    private Date kanbanPlanEndTime;// 完了予定日時
    @XmlElement()
    private Date kanbanActualStartTime;// 最初の開始実績日時
    @XmlElement()
    private Date kanbanActualEndTime;// 最後の完了実績日時
    @XmlElement()
    private Integer workKanbanOrder;// 工程カンバンの表示順
    @XmlElement()
    private Boolean separateWorkFlag;// 
    @XmlElement()
    private String workerName;// 作業者名
    @XmlElement()
    private Boolean skipFlag;// スキップフラグ

    /**
     * コンストラクタ
     */
    public KanbanTopicInfoEntity() {
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
     * カンバンの開始予定日時を取得する。
     *
     * @return カンバンの開始予定日時
     */
    public Date getPlanStartTime() {
        return this.planStartTime;
    }

    /**
     * カンバンの開始予定日時を設定する。
     *
     * @param planStartTime カンバンの開始予定日時
     */
    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    /**
     * カンバンの完了予定日時を取得する。
     *
     * @return カンバンの完了予定日時
     */
    public Date getPlanEndTime() {
        return this.planEndTime;
    }

    /**
     * カンバンの完了予定日時を設定する。
     *
     * @param planEndTime カンバンの完了予定日時
     */
    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    /**
     * 工程カンバンの最初の実績日時を取得する。
     *
     * @return 工程カンバンの最初の実績日時
     */
    public Date getActualStartTime() {
        return this.actualStartTime;
    }

    /**
     * 工程カンバンの最初の実績日時を設定する。
     *
     * @param actualStartTime 工程カンバンの最初の実績日時
     */
    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    /**
     * 工程カンバンの最後の実績日時を取得する。
     *
     * @return 工程カンバンの最後の実績日時
     */
    public Date getActualEndTime() {
        return this.actualEndTime;
    }

    /**
     * 工程カンバンの最後の実績日時を設定する。
     *
     * @param actualEndTime 工程カンバンの最後の実績日時
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
     * カンバン開始予定日時を取得する。
     *
     * @return カンバン開始予定日時
     */
    public Date getKanbanPlanStartTime() {
        return this.kanbanPlanStartTime;
    }

    /**
     * カンバン開始予定日時を設定する。
     *
     * @param kanbanPlanStartTime カンバン開始予定日時
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.kanbanId);
        hash = 59 * hash + Objects.hashCode(this.workKanbanId);
        hash = 59 * hash + Objects.hashCode(this.organizationId);
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
        final KanbanTopicInfoEntity other = (KanbanTopicInfoEntity) obj;
        if (!Objects.equals(this.kanbanId, other.kanbanId)) {
            return false;
        }
        if (!Objects.equals(this.workKanbanId, other.workKanbanId)) {
            return false;
        }
        return Objects.equals(this.organizationId, other.organizationId);
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanTopicInfoEntity{")
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
                .append(", skipFlag=").append(this.skipFlag)
                .append("}")
                .toString();
    }
}
