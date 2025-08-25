/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

import java.util.Date;

/**
 * スケジュール画面表示設定
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.24.Wen
 */
public class WorkPlanScheduleShowConfig {

    // スケジュール表示の初期設定
    private WorkPlanScheduleCellSizeTypeEnum scheduleSize = WorkPlanScheduleCellSizeTypeEnum.DAILY;
    // 日付表示の横幅の表示倍率
    private double dailyWidthMagnification = 1.0;

    //　日付表示の日数
    private int baseMonthlyDate = 0;
    private Date baseStartDate = new Date();
    private Date baseEndDate = new Date();

    public WorkPlanScheduleShowConfig() {
    }

    public WorkPlanScheduleCellSizeTypeEnum getScheduleSize() {
        return scheduleSize;
    }

    public void setScheduleSize(WorkPlanScheduleCellSizeTypeEnum defaultScheduleSize) {
        this.scheduleSize = defaultScheduleSize;
    }

    public int getBaseMonthlyDate() {
        return baseMonthlyDate;
    }

    public void setBaseMonthlyDate(int baseMonthlyDate) {
        this.baseMonthlyDate = baseMonthlyDate;
    }

    public Date getBaseStartDate() {
        return baseStartDate;
    }

    public void setBaseStartDate(Date baseStartDate) {
        this.baseStartDate = baseStartDate;
    }

    public Date getBaseEndDate() {
        return baseEndDate;
    }

    public void setBaseEndDate(Date baseEndDate) {
        this.baseEndDate = baseEndDate;
    }

    public double getDailyWidthMagnification() {
        return dailyWidthMagnification;
    }

    public void setDailyWidthMagnification(double dailyWidthMagnification) {
        this.dailyWidthMagnification = dailyWidthMagnification;
    }

}
