/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils.scheduling;

import adtekfuji.admanagerapp.productionnaviplugin.clientservice.WorkPlanRestAPI;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanTopicSearchCondition;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import org.apache.commons.lang3.time.DateUtils;

/**
 *
 * @since 2018/10/12
 * @author jmin
 */
public class WorkerDowntimeUtil {
    
    private final WorkPlanRestAPI REST_API = new WorkPlanRestAPI();
    
    private final LocalTime openTime;
    private final LocalTime closeTime;
    private final int days;
    
    private Date targetDate;
    
    private Map<Long, BreakTimeInfoEntity> breaktimesMap;
    
    public WorkerDowntimeUtil(LocalTime openTime, LocalTime closeTime, int days) {
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.days = days;
        this.breaktimesMap = new HashMap<>();
        for(BreakTimeInfoEntity breaktime : REST_API.searchBreaktimes()) {
            this.breaktimesMap.put(breaktime.getBreaktimeId(), breaktime);
        }
    }
    
    /**
     * get WorkerDowntimeData List
     *
     * @param workerIds
     * @param targetDate
     * @param days 日数
     */
    public List<WorkerDowntimeData> getData(WorkKanbanInfoEntity workKanban, Date targetDate) {
        this.targetDate = targetDate;
        
        //days分取得
        List<WorkerDowntimeData> result = new ArrayList<>();
        List<Long> workerIds = workKanban.getOrganizationCollection();
        // 作業者がいない場合
        if(workerIds.size() == 0) {
            workerIds.add(-1L);
        }
        
        List<DowntimeData> holidays = getHolidaysData();
        List<DowntimeData> notOperationTimes = getNotOperationTimesData();
        
        for(Long workerId : workerIds) {
            WorkerDowntimeData worker = new WorkerDowntimeData(workerId);
            // 少ない順から注入（速度早い）
            // Other work
            //worker.setDowntimes(getOtherWorkData(workerId, workKanban));
            
            // 予定
            //worker.addAllDowntimes(getSchedulesData(workerId));
            
            // 休日表
            worker.setDowntimes(holidays);
            //worker.addAllDowntimes(holidays);
            
            //作業時間枠
            worker.addAllDowntimes(notOperationTimes);
            
            // 休憩時間
            getBreaktimes(workerId).stream()
            .forEach(b -> 
                worker.addAllDowntimes(
                    getPeriodData(new DowntimeData(b.getStarttime(), b.getEndtime())))
            );
            
            // 注入
            result.add(worker);
        }
        return result;
    }
    
    /**
     * 作業時間枠
     */
    private List<DowntimeData> getNotOperationTimesData() {
        List<DowntimeData> result = new ArrayList<>();
        if((Objects.isNull(openTime) && Objects.isNull(closeTime))) { 
            return result;
        }
        Calendar so = Calendar.getInstance();
        //openttime
        if(Objects.nonNull(openTime)) {  
            so.setTime(targetDate);
            so = DateUtils.truncate(so, Calendar.DAY_OF_MONTH);
            Calendar eo = DateUtils.truncate(so, Calendar.DAY_OF_MONTH);
            eo.add(Calendar.HOUR, openTime.getHour());
            eo.add(Calendar.MINUTE, openTime.getMinute());
            eo.add(Calendar.SECOND, openTime.getSecond());
            eo.add(Calendar.SECOND, -1);
            
            DowntimeData openDowntimeData = new DowntimeData(so.getTime(), eo.getTime());

            result.addAll(getPeriodData(openDowntimeData));
        }

        //closetime
        if(Objects.nonNull(closeTime)) {        
            Calendar sc = DateUtils.truncate(so, Calendar.DAY_OF_MONTH);
            sc.add(Calendar.HOUR, closeTime.getHour());
            sc.add(Calendar.MINUTE, closeTime.getMinute());
            sc.add(Calendar.SECOND, closeTime.getSecond());
            Calendar ec = DateUtils.truncate(so, Calendar.DAY_OF_MONTH);
            ec.add(Calendar.DAY_OF_MONTH, 1);
            ec.add(Calendar.SECOND, -1);

            DowntimeData closeDowntimeData = new DowntimeData(sc.getTime(), ec.getTime());

            result.addAll(getPeriodData(closeDowntimeData));
        }
  
        result.sort(Comparator.comparing(item -> item.getStarttime()));
        
        return result;
    }
    
    /**
     * 休日
     */
    private List<DowntimeData> getHolidaysData() {
        List<DowntimeData> result = new ArrayList<>();
        REST_API.searchHolidays(DateUtils.addDays(targetDate, -1), DateUtils.addDays(targetDate, days))
                .stream().forEach(h -> result.add(new DowntimeData(h.getHolidayDate())));
        return result;
    }
    
    /**
     * 予定
     */
    private List<DowntimeData> getSchedulesData(Long workerId) {
        List<DowntimeData> result = new ArrayList<>();
        List<Long> workerIds = new ArrayList<>();
        workerIds.add(workerId);
        REST_API.searchSchedules(workerIds, targetDate, DateUtils.addDays(targetDate, days))
                .stream().forEach(s -> result.add(
                    new DowntimeData(s.getScheduleFromDate(), s.getScheduleToDate())
                ));
        return result;
    }
    
    /**
     * 他の作業
     */
    private List<DowntimeData> getOtherWorkData(Long workerId, WorkKanbanInfoEntity workKanban) {
        List<DowntimeData> result = new ArrayList<>();
        Long kanbanId = workKanban.getFkKanbanId();
        int taktTime = workKanban.getTaktTime();
        KanbanTopicSearchCondition condition = new KanbanTopicSearchCondition(KanbanTopicSearchCondition.ContentType.DAYS_ORGANIZATION);
        List<Long> workerIds = new ArrayList<>();
        workerIds.add(workerId);
        condition.setPrimaryKeys(workerIds);
        condition.setFromDate(targetDate);
        condition.setToDate(DateUtils.addDays(targetDate, days));
        REST_API.searchKanbanTopic(condition).stream()
        .filter(w -> (w.getTaktTime() <= taktTime) && (!w.getKanbanId().equals(kanbanId)))
        .forEach(w -> result.add(
                new DowntimeData(w.getPlanStartTime(), w.getPlanEndTime())
            )
        );
        return result;
    }
    
    /**
     * 期間データ
     */
    private List<DowntimeData> getPeriodData(DowntimeData data) {
        List<DowntimeData> result = new ArrayList<>();
        
        Date starttime = data.getStarttime();
        int startHour = starttime.getHours();
        int startMin = starttime.getMinutes();
        int startSec = starttime.getSeconds();
        
        Date endtime = data.getEndtime();
        int endHour = endtime.getHours();
        int endMin = endtime.getMinutes();
        int endSec = endtime.getSeconds();
        
        for(int i=0; i<days; i++) {
            Date today = DateUtils.addDays(targetDate, i);
            
            Calendar s = Calendar.getInstance();
            s.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH));
            s.add(Calendar.HOUR, startHour);
            s.add(Calendar.MINUTE, startMin);
            s.add(Calendar.SECOND, startSec);
            
            Calendar e = Calendar.getInstance();
            e.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH));
            e.add(Calendar.HOUR, endHour);
            e.add(Calendar.MINUTE, endMin);
            e.add(Calendar.SECOND, endSec);
            
            result.add(new DowntimeData(s.getTime(), e.getTime()));
        }
        return result;
    }
    
    /**
     * 休憩
     *
     * @param workerId 作業者ID
     */
    private List<BreakTimeInfoEntity> getBreaktimes(Long workerId) {
        List<Long> breaktimeIds = new ArrayList<>();
        OrganizationInfoEntity organization = REST_API.searchOrganization(workerId);
        if (Objects.nonNull(organization.getBreakTimeInfoCollection())) {
            for (long breakId : organization.getBreakTimeInfoCollection()) {
                if (!breaktimeIds.contains(breakId)) {
                    breaktimeIds.add(breakId);
                }
            }
        }
        List<BreakTimeInfoEntity> breakTimes = new ArrayList<>();
        for (Long breaktimeId : breaktimeIds) {
            breakTimes.add(this.breaktimesMap.get(breaktimeId));
        }
        return breakTimes;
    }
}
