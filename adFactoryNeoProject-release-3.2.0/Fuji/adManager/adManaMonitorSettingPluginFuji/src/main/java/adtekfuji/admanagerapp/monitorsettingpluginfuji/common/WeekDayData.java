/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingpluginfuji.common;

import java.time.DayOfWeek;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author e-mori
 */
public class WeekDayData {

    private ObjectProperty<DayOfWeek> weekDayProperty;
    private DayOfWeek weekDay;

    public WeekDayData() {
    }

    public WeekDayData(DayOfWeek weekDay) {
        this.weekDay = weekDay;
    }

    public ObjectProperty<DayOfWeek> weekDayProperty() {
        if (Objects.isNull(weekDayProperty)) {
            weekDayProperty = new SimpleObjectProperty<>(weekDay);
        }
        return weekDayProperty;
    }

    public DayOfWeek getWeekDay() {
        if (Objects.nonNull(weekDayProperty)) {
            return weekDayProperty.get();
        }
        return weekDay;
    }

    public void setWeekDay(DayOfWeek weekDay) {
        if (Objects.nonNull(weekDayProperty)) {
            weekDayProperty.set(weekDay);
        } else {
            this.weekDay = weekDay;
        }
    }
}
