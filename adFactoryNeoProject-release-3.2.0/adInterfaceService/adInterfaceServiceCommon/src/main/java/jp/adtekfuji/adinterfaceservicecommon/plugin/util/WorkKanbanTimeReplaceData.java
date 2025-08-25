/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin.util;

import java.util.Date;
import java.util.List;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;

/**
 *
 * @author e-mori
 */
public class WorkKanbanTimeReplaceData {

    private String dateFormat;
    private Date referenceStartTime;
    private Integer workKanbanListIndex;
    private List<WorkKanbanInfoEntity> workKanbanInfoEntitys;
    private List<BreakTimeInfoEntity> breakTimeInfoEntitys;
    private WorkPlanKanbanDefaultOffsetData kanbanDefaultOffsetData;

    /**
     *
     */
    public WorkKanbanTimeReplaceData() {
    }

    /**
     *
     * @param dateFormat
     * @return
     */
    public WorkKanbanTimeReplaceData dateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    /**
     *
     * @param referenceStartTime
     * @return
     */
    public WorkKanbanTimeReplaceData referenceStartTime(Date referenceStartTime) {
        this.referenceStartTime = referenceStartTime;
        return this;
    }

    /**
     *
     * @param workKanbanListIndex
     * @return
     */
    public WorkKanbanTimeReplaceData workKanbanListIndex(Integer workKanbanListIndex) {
        this.workKanbanListIndex = workKanbanListIndex;
        return this;
    }

    /**
     *
     * @param workKanbanInfoEntitys
     * @return
     */
    public WorkKanbanTimeReplaceData workKanbanInfoEntitys(List<WorkKanbanInfoEntity> workKanbanInfoEntitys) {
        this.workKanbanInfoEntitys = workKanbanInfoEntitys;
        return this;
    }

    /**
     *
     * @param breakTimeInfoEntitys
     * @return
     */
    public WorkKanbanTimeReplaceData breakTimeInfoEntitys(List<BreakTimeInfoEntity> breakTimeInfoEntitys) {
        this.breakTimeInfoEntitys = breakTimeInfoEntitys;
        return this;
    }

    /**
     *
     * @param kanbanDefaultOffsetData
     * @return
     */
    public WorkKanbanTimeReplaceData kanbanDefaultOffsetData(WorkPlanKanbanDefaultOffsetData kanbanDefaultOffsetData) {
        this.kanbanDefaultOffsetData = kanbanDefaultOffsetData;
        return this;
    }

    /**
     *
     * @return
     */
    public String getDateFormat() {
        return this.dateFormat;
    }

    /**
     *
     * @param dateFormat
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     *
     * @return
     */
    public Date getReferenceStartTime() {
        return this.referenceStartTime;
    }

    /**
     *
     * @param referenceStartTime
     */
    public void setReferenceStartTime(Date referenceStartTime) {
        this.referenceStartTime = referenceStartTime;
    }

    /**
     *
     * @return
     */
    public Integer getWorkKanbanListIndex() {
        return this.workKanbanListIndex;
    }

    /**
     *
     * @param workKanbanListIndex
     */
    public void setWorkKanbanListIndex(Integer workKanbanListIndex) {
        this.workKanbanListIndex = workKanbanListIndex;
    }

    /**
     *
     * @return
     */
    public List<WorkKanbanInfoEntity> getWorkKanbanInfoEntitys() {
        return this.workKanbanInfoEntitys;
    }

    /**
     *
     * @param workKanbanInfoEntitys
     */
    public void setWorkKanbanInfoEntitys(List<WorkKanbanInfoEntity> workKanbanInfoEntitys) {
        this.workKanbanInfoEntitys = workKanbanInfoEntitys;
    }

    /**
     *
     * @return
     */
    public List<BreakTimeInfoEntity> getBreakTimeInfoEntitys() {
        return this.breakTimeInfoEntitys;
    }

    /**
     *
     * @param breakTimeInfoEntitys
     */
    public void setBreakTimeInfoEntitys(List<BreakTimeInfoEntity> breakTimeInfoEntitys) {
        this.breakTimeInfoEntitys = breakTimeInfoEntitys;
    }

    /**
     *
     * @return
     */
    public WorkPlanKanbanDefaultOffsetData getKanbanDefaultOffsetData() {
        return this.kanbanDefaultOffsetData;
    }

    /**
     *
     * @param kanbanDefaultOffsetData
     */
    public void setKanbanDefaultOffsetData(WorkPlanKanbanDefaultOffsetData kanbanDefaultOffsetData) {
        this.kanbanDefaultOffsetData = kanbanDefaultOffsetData;
    }
}
