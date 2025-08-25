/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils.scheduling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @since 2018/10/12
 * @author jmin
 */
public class WorkerDowntimeData {
    
    private Long workerId;
    private List<DowntimeData> downtimes = new ArrayList<>();

    public WorkerDowntimeData(Long workerId) {
        this.workerId = workerId;
    }
    
    /**
     * ダウンタイム可否判断。
     *
     * @param _downtimes
     * @param nowTime
     */
    public DowntimeData isDowntime(List<DowntimeData> _downtimes, Date nowTime) {
        if(Objects.isNull(_downtimes)) _downtimes = this.downtimes;
        for (DowntimeData _downtime : _downtimes) {
            if (nowTime.equals(_downtime.getStarttime()) || nowTime.equals(_downtime.getEndtime())
                    || ((nowTime.after(_downtime.getStarttime()) && nowTime.before(_downtime.getEndtime())))) {
                return _downtime;
            }
        }
        return null;
    }
    
    /*
    * downtimes 追加
    */
    public WorkerDowntimeData addAllDowntimes(List<DowntimeData> _concurrents) {
        List<DowntimeData> result = new ArrayList<>();
        
        for(DowntimeData _concurrent : _concurrents) {
            DowntimeData inputData = new DowntimeData(
                                            getComparedStartOfDowntime(_concurrent.getStarttime())
                                            , getComparedEndOfDowntime(_concurrent.getEndtime())
                                        );
            
            if(Objects.isNull(isDowntime(result, inputData.getStarttime()))) {
                result.add(inputData);
            }
        }
        
        for(DowntimeData check : downtimes) {
            if(Objects.isNull(isDowntime(result, check.getStarttime()))) {
                result.add(check);
            }
        }
        
        result.sort(Comparator.comparing(item -> item.getStarttime()));
        
        this.downtimes = result;
        
        return this;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public List<DowntimeData> getDowntimes() {
        return downtimes;
    }

    /*
    * downtimes 初期化
    */
    public void setDowntimes(List<DowntimeData> downtimes) {
        this.downtimes.clear();
        this.downtimes.addAll(downtimes);
    }
    
    /**
     *
     * @param startTime
     * @return
     */
    private Date getComparedStartOfDowntime(Date startTime) {
        DowntimeData downtime = isDowntime(null, startTime);
        if(Objects.nonNull(downtime)) return downtime.getStarttime();
        return startTime;
    }
    
    /**
     *
     * @param endTime
     * @return
     */
    private Date getComparedEndOfDowntime(Date endTime) {
        DowntimeData downtime = isDowntime(null, endTime);
        if(Objects.nonNull(downtime)) return downtime.getEndtime();
        return endTime;
    }

    @Override
    public String toString() {
        Logger logger = LogManager.getLogger();
        logger.debug(">> workerDowntimeData ***********start>>>>> workerId :"+workerId);
        for(DowntimeData d : downtimes) {
            logger.debug(d.toString());
        }
        logger.debug("<< workerDowntimeData *************end<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return "WorkerDowntimeData{" + "workerId=" + workerId + ", downtimes=" + downtimes + '}';
    }
}
