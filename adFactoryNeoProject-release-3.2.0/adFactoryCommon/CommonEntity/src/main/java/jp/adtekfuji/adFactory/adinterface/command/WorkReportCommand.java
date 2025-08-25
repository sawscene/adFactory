/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adinterface.command;

import java.io.Serializable;
import java.util.Objects;

/**
 * 作業報告コマンド
 *
 * @author s-heya
 */
public class WorkReportCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long kanbanId;
    private Long workKanbanId;
    private Long workId;
    private Long parentEquipmentId;
    private Long equipmentId;
    private Long parentOrganizationId;
    private Long organizationId;
    private Long equipmentStatusId;
    private Long workStatusId;
    private Long todayStatusId;
    private Integer workProgress;
    private Integer dailyProgress;
    private String modelName;

    public WorkReportCommand() {
    }

    public WorkReportCommand(Long kanbanId, Long workKanbanId, Long workId, Long parentEquipmentId, Long equipmentId, Long parentOrganizationId, Long organizationId, Long equipmentStatusId, Long workStatusId, Long todayStatusId, Integer workProgress, Integer dailyProgress) {
        this.kanbanId = kanbanId;
        this.workKanbanId = workKanbanId;
        this.workId = workId;
        this.parentEquipmentId = parentEquipmentId;
        this.equipmentId = equipmentId;
        this.parentOrganizationId = parentOrganizationId;
        this.organizationId = organizationId;
        this.equipmentStatusId = equipmentStatusId;
        this.workStatusId = workStatusId;
        this.todayStatusId = todayStatusId;
        this.workProgress = workProgress;
        this.dailyProgress = dailyProgress;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getKanbanId() {
        return kanbanId;
    }

    public Long getWorkKanbanId() {
        return workKanbanId;
    }

    public Long getWorkId() {
        return workId;
    }

    public Long getParentEquipmentId() {
        return parentEquipmentId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public Long getParentOrganizationId() {
        return parentOrganizationId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public Long getEquipmentStatusId() {
        return equipmentStatusId;
    }

    public Long getWorkStatusId() {
        return workStatusId;
    }

    public Long getTodayStatusId() {
        return todayStatusId;
    }

    public Integer getWorkProgress() {
        return workProgress;
    }

    public Integer getDailyProgress() {
        return dailyProgress;
    }

    /**
     * モデル名を取得する。
     * 
     * @return 
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * モデル名を設定する。
     * 
     * @param modelName 
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.workKanbanId);
        hash = 71 * hash + Objects.hashCode(this.equipmentId);
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
        final WorkReportCommand other = (WorkReportCommand) obj;
        if (!Objects.equals(this.workKanbanId, other.workKanbanId)) {
            return false;
        }
        return Objects.equals(this.equipmentId, other.equipmentId);
    }

    @Override
    public String toString() {
        return "WorkReportCommand{" + "kanbanId=" + kanbanId + ", workKanbanId=" + workKanbanId + ", workId=" + workId + ", parentEquipmentId=" + parentEquipmentId + ", equipmentId=" + equipmentId + ", parentOrganizationId=" + parentOrganizationId + ", organizationId=" + organizationId + ", equipmentStatusId=" + equipmentStatusId + ", workStatusId=" + workStatusId + ", todayStatusId=" + todayStatusId + ", workProgress=" + workProgress + ", dailyProgress=" + dailyProgress + '}';
    }
}
