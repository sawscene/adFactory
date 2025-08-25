/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.model.data;

import java.util.Date;

/**
 * 期間
 * 
 * @author s-heya
 */
public class DurationModel {

    private Date startDate = new Date(Long.MAX_VALUE);
    private Date endDate = new Date(Long.MIN_VALUE);

    /**
     * コンストラクタ
     */
    public DurationModel() {
        this.startDate = new Date(Long.MAX_VALUE);
        this.endDate = new Date(Long.MIN_VALUE);
    }
    
    /**
     * コンストラクタ
     * 
     * @param startDate
     * @param endDate 
     */
    public DurationModel(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 開始日時を取得する。
     * 
     * @return 開始日時
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * 終了日時を取得する。
     * 
     * @return 終了日時
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * 期間を足し算する。
     * 
     * @param duration 
     */
    public void plus(DurationModel duration) {
        this.plus(duration.getStartDate(), duration.getEndDate());
    }

    /**
     * 期間を足し算する。
     * 
     * @param startDate
     * @param endDate 
     */
    public void plus(Date startDate, Date endDate) {
        this.startDate = this.startDate.before(startDate) ? this.startDate : startDate;
        this.endDate = this.endDate.after(endDate) ? this.endDate : endDate;
    }
    
    /**
     * 時間を返す。
     * 
     * @return 
     */
    public long between() {
        return this.endDate.getTime() - this.startDate.getTime();
    }
}
