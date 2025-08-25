/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.clientservice;

import java.util.Date;
import adtekfuji.admanagerapp.productionnaviplugin.common.agenda.WorkPlanCustomAgendaConcurrentEntity;

/**
 * 日付計算用
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.26.Wen
 */
public class DatetimeConcurrent {

    private Date start;
    private Date end;
    private WorkPlanCustomAgendaConcurrentEntity concurrent;

    public DatetimeConcurrent() {
    }

    public DatetimeConcurrent(Date start, Date end, WorkPlanCustomAgendaConcurrentEntity concurrent) {
        this.start = start;
        this.end = end;
        this.concurrent = concurrent;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public WorkPlanCustomAgendaConcurrentEntity getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(WorkPlanCustomAgendaConcurrentEntity concurrent) {
        this.concurrent = concurrent;
    }
}
