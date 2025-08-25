/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.utils;

import java.util.Date;
import java.util.List;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;

/**
 * 工程カンバン時間再計算用データクラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.17.Tsu
 */
public class WorkKanbanTimeReplaceData {

    private String dateFormat;
    private Date referenceStartTime;
    private Integer workKanbanListIndex;
    private List<WorkKanbanInfoEntity> workKanbanInfoEntitys;
    private List<BreakTimeInfoEntity> breakTimeInfoEntitys;
    private KanbanDefaultOffsetData kanbanDefaultOffsetData;

    public WorkKanbanTimeReplaceData() {
    }

    public WorkKanbanTimeReplaceData dateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public WorkKanbanTimeReplaceData referenceStartTime(Date referenceStartTime) {
        this.referenceStartTime = referenceStartTime;
        return this;
    }

    public WorkKanbanTimeReplaceData workKanbanListIndex(Integer workKanbanListIndex) {
        this.workKanbanListIndex = workKanbanListIndex;
        return this;
    }

    public WorkKanbanTimeReplaceData workKanbanInfoEntitys(List<WorkKanbanInfoEntity> workKanbanInfoEntitys) {
        this.workKanbanInfoEntitys = workKanbanInfoEntitys;
        return this;
    }

    public WorkKanbanTimeReplaceData breakTimeInfoEntitys(List<BreakTimeInfoEntity> breakTimeInfoEntitys) {
        this.breakTimeInfoEntitys = breakTimeInfoEntitys;
        return this;
    }

    public WorkKanbanTimeReplaceData kanbanDefaultOffsetData(KanbanDefaultOffsetData kanbanDefaultOffsetData) {
        this.kanbanDefaultOffsetData = kanbanDefaultOffsetData;
        return this;
    }
    
    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    public Date getReferenceStartTime() {
        return referenceStartTime;
    }

    public void setReferenceStartTime(Date referenceStartTime) {
        this.referenceStartTime = referenceStartTime;
    }

    public Integer getWorkKanbanListIndex() {
        return workKanbanListIndex;
    }

    public void setWorkKanbanListIndex(Integer workKanbanListIndex) {
        this.workKanbanListIndex = workKanbanListIndex;
    }

    public List<WorkKanbanInfoEntity> getWorkKanbanInfoEntitys() {
        return workKanbanInfoEntitys;
    }

    public void setWorkKanbanInfoEntitys(List<WorkKanbanInfoEntity> workKanbanInfoEntitys) {
        this.workKanbanInfoEntitys = workKanbanInfoEntitys;
    }

    public List<BreakTimeInfoEntity> getBreakTimeInfoEntitys() {
        return breakTimeInfoEntitys;
    }

    public void setBreakTimeInfoEntitys(List<BreakTimeInfoEntity> breakTimeInfoEntitys) {
        this.breakTimeInfoEntitys = breakTimeInfoEntitys;
    }

    public KanbanDefaultOffsetData getKanbanDefaultOffsetData() {
        return kanbanDefaultOffsetData;
    }

    public void setKanbanDefaultOffsetData(KanbanDefaultOffsetData kanbanDefaultOffsetData) {
        this.kanbanDefaultOffsetData = kanbanDefaultOffsetData;
    }
}
