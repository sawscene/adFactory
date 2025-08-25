/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import java.util.Date;
import java.util.List;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;

/**
 *
 * @author ke.yokoi
 */
public class CalcWorkKanbanDelayTime {

    private CalcWorkKanbanDelayTime() {
    }

    /**
     * 遅れ、前倒し時間の計算.
     *
     * @param data
     * @param now
     * @param siftTime
     * @return millisec
     */
    public static long calcDelayTime(WorkKanbanTimeData data, Date now, long siftTime) {
        //long delayTime = 0L;        
        //予定時間を過ぎた分だけ遅れにする(予定時間より大きくならない、0より小さくならない).
        long delayTime = BreaktimeUtil.getDiffTime(data.getBreaktimes(), data.getPlanStartDatetime(), new Date(now.getTime() + siftTime));
        if (delayTime > BreaktimeUtil.getDiffTime(data.getBreaktimes(), data.getPlanStartDatetime(), data.getPlanEndDatetime())) {
            delayTime = BreaktimeUtil.getDiffTime(data.getBreaktimes(), data.getPlanStartDatetime(), data.getPlanEndDatetime());
        }
        if (delayTime < 0) {
            delayTime = 0;
        }

        //作業した分だけ進みにする(予定時間より大きくならない、0より小さくならない).
        long proceedTime = 0L;
        switch (data.getStatus()) {
            case PLANNED:
            case SUSPEND:
            case INTERRUPT:
                //if (Objects.nonNull(data.getPlanStartDatetime()) && Objects.nonNull(data.getPlanEndDatetime())) {
                //    if (now.after(data.getPlanEndDatetime())) {
                //        delayTime = delayTime - BreaktimeUtil.getDiffTime(data.getBreaktimes(), data.getPlanStartDatetime(), data.getPlanEndDatetime());
                //    } else if (now.after(data.getPlanStartDatetime())) {
                //        delayTime = delayTime - BreaktimeUtil.getDiffTime(data.getBreaktimes(), data.getPlanStartDatetime(), now);
                //    }
                //}
                proceedTime = data.getActualWorktime();
                break;
            case WORKING:
                //if (Objects.nonNull(data.getPlanStartDatetime()) && Objects.nonNull(data.getPlanEndDatetime())) {
                //    if (now.after(data.getPlanEndDatetime())) {
                //        delayTime = delayTime - BreaktimeUtil.getDiffTime(data.getBreaktimes(), data.getPlanStartDatetime(), data.getPlanEndDatetime());
                //    }
                //}
                proceedTime = data.getActualWorktime() + BreaktimeUtil.getDiffTime(data.getBreaktimes(), data.getActualStartDatetime(), now);
                break;
            case COMPLETION:
                //if (Objects.nonNull(data.getActualStartDatetime()) && Objects.nonNull(data.getActualEndDatetime())) {
                //    long plan = BreaktimeUtil.getDiffTime(data.getBreaktimes(), data.getPlanStartDatetime(), data.getPlanEndDatetime());
                //    long actual = BreaktimeUtil.getDiffTime(data.getBreaktimes(), data.getActualStartDatetime(), data.getActualEndDatetime());
                //    if (actual < plan) {
                //        delayTime = delayTime + (plan - actual);
                //    }
                //}
                proceedTime = BreaktimeUtil.getDiffTime(data.getBreaktimes(), data.getPlanStartDatetime(), data.getPlanEndDatetime());
                break;
            default:
                break;
        }
        if (proceedTime > BreaktimeUtil.getDiffTime(data.getBreaktimes(), data.getPlanStartDatetime(), data.getPlanEndDatetime())) {
            proceedTime = BreaktimeUtil.getDiffTime(data.getBreaktimes(), data.getPlanStartDatetime(), data.getPlanEndDatetime());
        }
        if (proceedTime < 0) {
            proceedTime = 0;
        }
        return proceedTime - delayTime;
    }

    /**
     * 遅れ、前倒し時間の計算.
     *
     * @param datas
     * @param now
     * @param siftTime
     * @return millisec
     */
    public static long calcDelayTimes(List<WorkKanbanTimeData> datas, Date now, long siftTime) {
        long delayTime = 0L;
        Date lastPlanEndTime = new Date(0L);

        //各工程の進捗を足す.
        for (WorkKanbanTimeData data : datas) {
            //工程カンバン計画開始前
            KanbanStatusEnum status = data.getStatus();
            if (!(data.getPlanStartDatetime().after(now) && (status == KanbanStatusEnum.PLANNING || status == KanbanStatusEnum.PLANNED))) {
                System.out.println(calcDelayTime(data, now, siftTime));
                delayTime += calcDelayTime(data, now, siftTime);
            }
            if (data.getPlanEndDatetime().after(lastPlanEndTime)) {
                lastPlanEndTime = data.getPlanEndDatetime();
            }
        }

        //現在時刻が最後の予定完了時刻を過ぎていたら、その分遅れになる.
        if (now.getTime() + siftTime > lastPlanEndTime.getTime()) {
            delayTime = delayTime - (now.getTime() + siftTime - lastPlanEndTime.getTime());
        }

        return delayTime + siftTime;
    }

}
