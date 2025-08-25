/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import java.util.Date;
import java.util.List;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 *
 * @author ke.yokoi
 */
public class WorkKanbanTimeData {

    private final KanbanStatusEnum status;
    private final Date planStartDatetime;
    private final Date planEndDatetime;
    private final Date actualStartDatetime;
    private final Date actualEndDatetime;
    private final List<BreakTimeInfoEntity> breaktimes;
    private final Long actualWorktime;

    public WorkKanbanTimeData(KanbanStatusEnum status, Date planStartDatetime, Date planEndDatetime, Date actualStartDatetime, Date actualEndDatetime, List<BreakTimeInfoEntity> breaktimes, Long actualWorktime) {
        this.status = status;
        this.planStartDatetime = planStartDatetime;
        this.planEndDatetime = planEndDatetime;
        this.actualStartDatetime = actualStartDatetime;
        this.actualEndDatetime = actualEndDatetime;
        this.breaktimes = breaktimes;
        this.actualWorktime = actualWorktime;
    }

    public KanbanStatusEnum getStatus() {
        return status;
    }

    public Date getPlanStartDatetime() {
        return planStartDatetime;
    }

    public Date getPlanEndDatetime() {
        return planEndDatetime;
    }

    public Date getActualStartDatetime() {
        return actualStartDatetime;
    }

    public Date getActualEndDatetime() {
        return actualEndDatetime;
    }

    public List<BreakTimeInfoEntity> getBreaktimes() {
        return breaktimes;
    }

    public Long getActualWorktime() {
        return actualWorktime;
    }

}
