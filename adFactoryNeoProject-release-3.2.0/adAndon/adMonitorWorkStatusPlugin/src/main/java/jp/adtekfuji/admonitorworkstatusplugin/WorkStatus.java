/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorworkstatusplugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.adinterface.command.WorkReportCommand;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;

/**
 * 工程進捗
 *
 * @author s-heya
 */
public class WorkStatus {
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty progress = new SimpleIntegerProperty();
    private final IntegerProperty todayProgress = new SimpleIntegerProperty();
    private final List<Long> ids;
    private final Integer id;
    private DisplayedStatusInfoEntity equipmentStatus;
    private Long workStatusId = 0L;
    private Long todayStatusId = 0L;
    private final Map<Long, WorkReportCommand> workReports = new HashMap<>();
    private boolean called;

    public WorkStatus(Integer id, String name, List<Long> ids) {
        this.id = id;
        this.name.set(name);
        this.ids = ids;
    }

    public StringProperty nameProperty() {
        return this.name;
    }

	public IntegerProperty progressProperty() {
        return this.progress;
    }

    public IntegerProperty todayProgressProperty() {
        return this.todayProgress;
    }

    public Integer getId() {
        return this.id;
    }

    public DisplayedStatusInfoEntity getEquipmentStatus() {
        return this.equipmentStatus;
    }

    public Long getWorkStatusId() {
        return this.workStatusId;
    }

    public Long getTodayStatusId() {
        return this.todayStatusId;
    }

    /**
     * 工程進捗を更新する。
     * 
     * @param id
     * @param workReport
     * @param displaySetting 
     */
    public void update(long id, WorkReportCommand workReport, Map<Long, DisplayedStatusInfoEntity> displaySetting) {
        if (this.ids.contains(id)) {
            this.workReports.put(id, workReport);

            int workProgress = Integer.MAX_VALUE;
            int dailyProgress = Integer.MAX_VALUE;

            for (WorkReportCommand value : this.workReports.values()) {
                if (workProgress > value.getWorkProgress()) {
                    workProgress = value.getWorkProgress();
                    this.workStatusId = value.getWorkStatusId();
                }
                if (dailyProgress > value.getDailyProgress()) {
                    dailyProgress = value.getDailyProgress();
                    this.todayStatusId = value.getTodayStatusId();
                }

                //if (displaySetting.containsKey(value.getEquipmentStatusId())) {
                //    DisplayedStatusInfoEntity displayedStatus = displaySetting.get(value.getEquipmentStatusId());
                //    StatusPatternEnum status = StatusPatternEnum.compareStatus(displayedStatus.getStatusName(), this.equipmentStatus.getStatusName());
                //    this.equipmentStatus = (status == displayedStatus.getStatusName()) ? displayedStatus : this.equipmentStatus;
                //}
            }

            if (displaySetting.containsKey(workReport.getEquipmentStatusId())) {
                this.equipmentStatus = displaySetting.get(workReport.getEquipmentStatusId());
            } else {
                Optional<DisplayedStatusInfoEntity> optional = displaySetting.values().stream().filter(o -> Objects.equals(o.getStatusName(), StatusPatternEnum.PLAN_NORMAL)).findFirst();
                this.equipmentStatus = optional.isPresent() ? optional.get() : null;
            }

            this.progress.set(workProgress);
            this.todayProgress.set(dailyProgress);
        }
    }

    public boolean contains(long id) {
        return this.ids.contains(id);
    }

    /**
     * 呼出中かどうかを返す。
     *
     * @return
     */
    public boolean isCalled() {
        return this.called;
    }

    /**
     * 呼出中の状態を設定する。
     *
     * @param called
     */
    public void setCalled(boolean called) {
        this.called = called;

        String value = this.name.get();
        this.name.set(null);
        this.name.set(value);
    }
}
