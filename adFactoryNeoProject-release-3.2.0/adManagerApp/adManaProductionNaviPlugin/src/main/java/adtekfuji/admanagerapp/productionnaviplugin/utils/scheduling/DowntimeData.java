/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils.scheduling;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.time.DateUtils;

/**
 * @since 2018/10/12
 * @author jmin
 */
public class DowntimeData {
    private Date starttime;
    private Date endtime;

    public DowntimeData() {
    }

    public DowntimeData( Date daytime) {
        
        Calendar s = Calendar.getInstance();
        s.setTime(daytime);
        s = DateUtils.truncate(s, Calendar.DAY_OF_MONTH);
        Calendar e = DateUtils.truncate(s, Calendar.DAY_OF_MONTH);
        e.add(Calendar.DAY_OF_MONTH, 1);
        e.add(Calendar.SECOND, -1);
        this.starttime= s.getTime();
        this.endtime = e.getTime();       
    }
    
    public DowntimeData(Date starttime, Date endtime) {
        this.starttime = starttime;
        if(endtime.getSeconds() == 0) {
            this.endtime = org.apache.commons.lang3.time.DateUtils.addSeconds(endtime, -1);
        } else {
            this.endtime = endtime;
        }
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.starttime);
        hash = 47 * hash + Objects.hashCode(this.endtime);
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
        final DowntimeData other = (DowntimeData) obj;
        if (!Objects.equals(this.starttime, other.starttime)) {
            return false;
        }
        if (!Objects.equals(this.endtime, other.endtime)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DowntimeData{" + "starttime=" + starttime + ", endtime=" + endtime + '}';
    }

}
