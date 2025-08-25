/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.chart;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * タイムライン情報エンティティクラス
 *
 * @author s-heya
 */
@XmlRootElement(name = "timeLine")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimeLineInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Long actualId;
    @XmlElement
    private Long fkKanbanId;
    @XmlElement
    private Long fkWorkflowId;
    @XmlElement
    private Long fkWorkKanbanId;
    @XmlElement
    private Long fkWorkId;
    @XmlElement
    private Long fkOrganizationId;
    @XmlElement
    private Long fkEquipmentId;
    @XmlElement
    private String kanbanName;
    //@XmlElement
    //private String workflowName;
    @XmlElement
    private String workName;
    @XmlElement
    private String organizationName;
    @XmlElement
    private String equipmentName;
    @XmlElement
    private Date implementDatetime;
    @XmlElement
    private KanbanStatusEnum actualStatus;
    @XmlElement
    private Integer workTime;
    @XmlElement
    private String interruptReason;
    @XmlElement
    private String delayReason;
    @XmlElement
    private String modelName;
    @XmlElement
    private Integer workRev;

    /**
     * コンストラクタ
     */
    public TimeLineInfoEntity() {
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
    public Long getFkKanbanId() {
        return this.fkKanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param fkKanbanId カンバンID
     */
    public void setFkKanbanId(Long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getFkWorkflowId() {
        return this.fkWorkflowId;
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
     * 工程カンバンIDを取得する。
     *
     * @return 工程カンバンID
     */
    public Long getFkWorkKanbanId() {
        return this.fkWorkKanbanId;
    }

    /**
     * 工程カンバンIDを設定する。
     *
     * @param fkWorkKanbanId 工程カンバンID
     */
    public void setFkWorkKanbanId(Long fkWorkKanbanId) {
        this.fkWorkKanbanId = fkWorkKanbanId;
    }

    /**
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public Long getFkWorkId() {
        return this.fkWorkId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param fkWorkId 工程ID
     */
    public void setFkWorkId(Long fkWorkId) {
        this.fkWorkId = fkWorkId;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getFkOrganizationId() {
        return this.fkOrganizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param fkOrganizationId 組織ID
     */
    public void setFkOrganizationId(Long fkOrganizationId) {
        this.fkOrganizationId = fkOrganizationId;
    }

    /**
     * 設備IDを取得する。
     *
     * @return 設備ID
     */
    public Long getFkEquipmentId() {
        return this.fkEquipmentId;
    }

    /**
     * 設備IDを設定する。
     *
     * @param fkEquipmentId 設備ID
     */
    public void setFkEquipmentId(Long fkEquipmentId) {
        this.fkEquipmentId = fkEquipmentId;
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

    /**
     * 表示名を取得する。
     *
     * @return 表示名(工程名 : 版数)
     */
    public String getDisplayWorkName() {
        StringBuilder name = new StringBuilder(this.workName);
        if (Objects.nonNull(this.workRev)) {
            name.append(" : ").append(this.workRev);
        }
        return name.toString();
    }

    @Override
    public String toString() {
        return new StringBuilder("TimeLineInfoEntity{")
                .append("actualId=").append(this.actualId)
                .append(", fkKanbanId=").append(this.fkKanbanId)
                .append(", fkWorkflowId=").append(this.fkWorkflowId)
                .append(", fkWorkKanbanId=").append(this.fkWorkKanbanId)
                .append(", fkWorkId=").append(this.fkWorkId)
                .append(", fkOrganizationId=").append(this.fkOrganizationId)
                .append(", fkEquipmentId=").append(this.fkEquipmentId)
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
