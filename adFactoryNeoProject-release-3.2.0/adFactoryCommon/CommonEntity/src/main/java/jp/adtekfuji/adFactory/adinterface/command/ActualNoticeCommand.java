/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adinterface.command;

import java.io.Serializable;
import java.util.Date;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 実績通知コマンド
 *
 * @author ke.yokoi
 */
public class ActualNoticeCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long monitorId;
    private Long andonEquipmentId = null;
    private Long kanbanId;
    private Long workKanbanId;
    private Long parentEquipmentId;
    private Long equipmentId;
    private Long parentOrganizationId;
    private Long organizationId;
    private Long workId;
    private KanbanStatusEnum workKanbanStatus;
    private KanbanStatusEnum kanbanStatus;
    private KanbanStatusEnum equipmentStatus;
    private String modelName;
    private boolean completion;
    private Integer compNum;
    private Long actualId;
    private Date dateTime;

    private WorkResult workResult;

    /**
     *
     */
    public ActualNoticeCommand() {
    }

    /**
     *
     * @param kanbanId
     * @param workKanbanId
     * @param workId
     * @param parentEquipmentId
     * @param equipmentId
     * @param parentOrganizationId
     * @param organizationId
     * @param workKanbanStatus
     * @param kanbanStatus
     * @param equipmentStatus
     * @param modelName
     * @param isCompletion
     * @param compNum
     * @param dateTime
     */
    public ActualNoticeCommand(Long kanbanId, Long workKanbanId, Long workId, Long parentEquipmentId, Long equipmentId, Long parentOrganizationId, Long organizationId,
            KanbanStatusEnum workKanbanStatus, KanbanStatusEnum kanbanStatus, KanbanStatusEnum equipmentStatus, String modelName, boolean isCompletion, int compNum, Date dateTime) {
        this.kanbanId = kanbanId;
        this.workKanbanId = workKanbanId;
        this.workId = workId;
        this.parentEquipmentId = parentEquipmentId;
        this.equipmentId = equipmentId;
        this.parentOrganizationId = parentOrganizationId;
        this.organizationId = organizationId;
        this.workKanbanStatus = workKanbanStatus;
        this.kanbanStatus = kanbanStatus;
        this.equipmentStatus = equipmentStatus;
        this.modelName = modelName;
        this.completion = isCompletion;
        this.compNum = compNum;
        this.dateTime = dateTime;
    }

    /**
     *
     * @param kanbanId
     * @param workKanbanId
     * @param workId
     * @param parentEquipmentId
     * @param equipmentId
     * @param parentOrganizationId
     * @param organizationId
     * @param workKanbanStatus
     * @param kanbanStatus
     * @param equipmentStatus
     * @param modelName
     * @param compNum
     * @param dateTime
     */
    public ActualNoticeCommand(Long kanbanId, Long workKanbanId, Long workId, Long parentEquipmentId, Long equipmentId, Long parentOrganizationId, Long organizationId,
            KanbanStatusEnum workKanbanStatus, KanbanStatusEnum kanbanStatus, KanbanStatusEnum equipmentStatus, String modelName, int compNum, Date dateTime) {
        this.kanbanId = kanbanId;
        this.workKanbanId = workKanbanId;
        this.workId = workId;
        this.parentEquipmentId = parentEquipmentId;
        this.equipmentId = equipmentId;
        this.parentOrganizationId = parentOrganizationId;
        this.organizationId = organizationId;
        this.workKanbanStatus = workKanbanStatus;
        this.kanbanStatus = kanbanStatus;
        this.equipmentStatus = equipmentStatus;
        this.modelName = modelName;
        this.compNum = compNum;
        this.dateTime = dateTime;
    }

    /**
     *
     * @param kanbanId
     * @param workKanbanId
     * @param workId
     * @param parentEquipmentId
     * @param equipmentId
     * @param parentOrganizationId
     * @param organizationId
     */
    public ActualNoticeCommand(Long kanbanId, Long workKanbanId, Long workId, Long parentEquipmentId, Long equipmentId, Long parentOrganizationId, Long organizationId) {
        this.kanbanId = kanbanId;
        this.workKanbanId = workKanbanId;
        this.workId = workId;
        this.parentEquipmentId = parentEquipmentId;
        this.equipmentId = equipmentId;
        this.parentOrganizationId = parentOrganizationId;
        this.organizationId = organizationId;
    }

    /**
     *
     * @param actualId
     * @param kanbanId
     * @param workKanbanId
     * @param workId
     * @param parentEquipmentId
     * @param equipmentId
     * @param parentOrganizationId
     * @param organizationId
     * @param workKanbanStatus
     * @param kanbanStatus
     * @param equipmentStatus
     * @param modelName
     * @param isCompletion
     * @param compNum
     * @param dateTime
     */
    public ActualNoticeCommand(Long actualId, Long kanbanId, Long workKanbanId, Long workId, Long parentEquipmentId, Long equipmentId, Long parentOrganizationId, Long organizationId,
            KanbanStatusEnum workKanbanStatus, KanbanStatusEnum kanbanStatus, KanbanStatusEnum equipmentStatus, String modelName, boolean isCompletion, int compNum, Date dateTime) {
        this.actualId = actualId;
        this.kanbanId = kanbanId;
        this.workKanbanId = workKanbanId;
        this.workId = workId;
        this.parentEquipmentId = parentEquipmentId;
        this.equipmentId = equipmentId;
        this.parentOrganizationId = parentOrganizationId;
        this.organizationId = organizationId;
        this.workKanbanStatus = workKanbanStatus;
        this.kanbanStatus = kanbanStatus;
        this.equipmentStatus = equipmentStatus;
        this.modelName = modelName;
        this.completion = isCompletion;
        this.compNum = compNum;
        this.dateTime = dateTime;
    }

    /**
     *
     * @return
     */
    public Long getMonitorId() {
        return this.monitorId;
    }

    /**
     *
     * @param monitorId
     */
    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    /**
     *
     * @return
     */
    public Long getAndonEquipmentId() {
        return this.andonEquipmentId;
    }

    /**
     *
     * @param andonEquipmentId
     */
    public void setAndonEquipmentId(Long andonEquipmentId) {
        this.andonEquipmentId = andonEquipmentId;
    }

    /**
     *
     * @param kanbanId
     * @return
     */
    public ActualNoticeCommand kanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
        return this;
    }

    /**
     *
     * @param workKanbanId
     * @return
     */
    public ActualNoticeCommand workKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
        return this;
    }

    /**
     *
     * @param parentEquipmentId
     * @return
     */
    public ActualNoticeCommand parentEquipmentId(Long parentEquipmentId) {
        this.parentEquipmentId = parentEquipmentId;
        return this;
    }

    /**
     *
     * @param equipmentId
     * @return
     */
    public ActualNoticeCommand equipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
        return this;
    }

    /**
     *
     *
     * @param parentOrganizationId
     * @return
     */
    public ActualNoticeCommand parentOrganizationId(Long parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
        return this;
    }

    /**
     *
     * @param organizationId
     * @return
     */
    public ActualNoticeCommand organizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    /**
     *
     * @param workKanbanStatus
     * @return
     */
    public ActualNoticeCommand parentOrganizationId(KanbanStatusEnum workKanbanStatus) {
        this.workKanbanStatus = workKanbanStatus;
        return this;
    }

    /**
     *
     * @param kanbanStatus
     * @return
     */
    public ActualNoticeCommand organizationId(KanbanStatusEnum kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
        return this;
    }

    /**
     *
     * @return
     */
    public Long getKanbanId() {
        return this.kanbanId;
    }

    /**
     *
     * @param kanbanId
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     *
     * @return
     */
    public Long getWorkKanbanId() {
        return this.workKanbanId;
    }

    /**
     *
     * @param workKanbanId
     */
    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    /**
     *
     * @return
     */
    public Long getParentEquipmentId() {
        return this.parentEquipmentId;
    }

    /**
     *
     * @param parentEquipmentId
     */
    public void setParentEquipmentId(Long parentEquipmentId) {
        this.parentEquipmentId = parentEquipmentId;
    }

    /**
     *
     * @return
     */
    public Long getEquipmentId() {
        return this.equipmentId;
    }

    /**
     *
     * @param equipmentId
     */
    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     *
     * @return
     */
    public Long getParentOrganizationId() {
        return this.parentOrganizationId;
    }

    /**
     *
     * @param parentOrganizationId
     */
    public void setParentOrganizationId(Long parentOrganizationId) {
        this.parentOrganizationId = parentOrganizationId;
    }

    /**
     *
     * @return
     */
    public Long getOrganizationId() {
        return this.organizationId;
    }

    /**
     *
     * @param organizationId
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     *
     * @return
     */
    public Long getWorkId() {
        return this.workId;
    }

    /**
     *
     * @return
     */
    public KanbanStatusEnum getWorkKanbanStatus() {
        return this.workKanbanStatus;
    }

    /**
     *
     * @param workKanbanStatus
     */
    public void setWorkKanbanStatus(KanbanStatusEnum workKanbanStatus) {
        this.workKanbanStatus = workKanbanStatus;
    }

    /**
     *
     * @return
     */
    public KanbanStatusEnum getKanbanStatus() {
        return this.kanbanStatus;
    }

    /**
     *
     * @param kanbanStatus
     */
    public void setKanbanStatus(KanbanStatusEnum kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
    }

    /**
     *
     * @return
     */
    public KanbanStatusEnum getEquipmentStatus() {
        return this.equipmentStatus;
    }

    /**
     *
     * @param equipmentStatus
     */
    public void setEquipmentStatus(KanbanStatusEnum equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    /**
     *
     * @return
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     *
     * @param modelName
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 実績通知が「完了」によるものか
     *
     * @return true: 「完了」が押された時の通知
     */
    public boolean isCompletion() {
        return this.completion;
    }

    /**
     *
     * @param completion
     */
    public void setCompletion(boolean completion) {
        this.completion = completion;
    }

    /**
     *
     * @param id
     */
    public void setActualId(Long id) {
        this.actualId = id;
    }

    /**
     *
     * @return
     */
    public Long getActualId() {
        return this.actualId;
    }

    /**
     * 完了数を取得する。
     *
     * @return
     */
    public Integer getCompNum() {
        return this.compNum;
    }

    /**
     * 完了数を設定する。
     *
     * @param compNum
     */
    public void setCompNum(Integer compNum) {
        this.compNum = compNum;
    }

    /**
     * 実績データを取得する。
     *
     * @return
     */
    public WorkResult getWorkResult() {
        return this.workResult;
    }

    /**
     * 実績データを設定する。
     *
     * @param workResult
     */
    public void setWorkResult(WorkResult workResult) {
        this.workResult = workResult;
    }

    /**
     * 日時を取得する。
     * 
     * @return 
     */
    public Date getDateTime() {
        return this.dateTime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ActualNoticeCommand other = (ActualNoticeCommand) obj;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ActualNoticeCommand{")
                .append("monitorId=").append(this.monitorId)
                .append(", andonEquipmentId=").append(this.andonEquipmentId)
                .append(", kanbanId=").append(this.kanbanId)
                .append(", workKanbanId=").append(this.workKanbanId)
                .append(", parentEquipmentId=").append(this.parentEquipmentId)
                .append(", equipmentId=").append(this.equipmentId)
                .append(", parentOrganizationId=").append(this.parentOrganizationId)
                .append(", organizationId=").append(this.organizationId)
                .append(", workId=").append(this.workId)
                .append(", workKanbanStatus=").append(this.workKanbanStatus)
                .append(", kanbanStatus=").append(this.kanbanStatus)
                .append(", equipmentStatus=").append(this.equipmentStatus)
                .append(", modelName=").append(this.modelName)
                .append(", compNum=").append(this.compNum)
                .append(", actualId=").append(this.actualId)
                .append("}")
                .toString();
    }
}
