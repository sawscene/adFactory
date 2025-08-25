/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.utils;

import java.util.Date;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.commons.lang.time.DateUtils;

/**
 * 時間オフセット用エンティティ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.17.Tsu
 */
public class KanbanDefaultOffsetData {

    private ObjectProperty<Date> startOffsetTimeProperty;
    private BooleanProperty checkOffsetWorkingHoursProperty;
    private ObjectProperty<Date> openingTimeProperty;
    private ObjectProperty<Date> closingTimeProperty;

    private Date startOffsetTime;
    private Boolean checkOffsetWorkingHours;
    private Date openingTime;
    private Date closingTime;

    public KanbanDefaultOffsetData() {
    }

    public KanbanDefaultOffsetData(Date startOffsetTime, Boolean checkOffsetWorkingHours, Date openingTime, Date closingTime) {
        this.startOffsetTime = startOffsetTime;
        this.checkOffsetWorkingHours = checkOffsetWorkingHours;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }
    
    public ObjectProperty<Date> startOffsetTimeProperty() {
        if (Objects.isNull(startOffsetTimeProperty)) {
            startOffsetTimeProperty = new SimpleObjectProperty<>(startOffsetTime);
        }
        return startOffsetTimeProperty;
    }

    public BooleanProperty checkOffsetWorkingHoursProperty() {
        if (Objects.isNull(checkOffsetWorkingHoursProperty)) {
            checkOffsetWorkingHoursProperty = new SimpleBooleanProperty(checkOffsetWorkingHours);
        }
        return checkOffsetWorkingHoursProperty;
    }

    public ObjectProperty<Date> openingTimeProperty() {
        if (Objects.isNull(openingTimeProperty)) {
            openingTimeProperty = new SimpleObjectProperty<>(openingTime);
        }
        return openingTimeProperty;
    }

    public ObjectProperty<Date> closingTimeProperty() {
        if (Objects.isNull(closingTimeProperty)) {
            closingTimeProperty = new SimpleObjectProperty<>(closingTime);
        }
        return closingTimeProperty;
    }

    public Date getStartOffsetTime() {
        if (Objects.nonNull(startOffsetTimeProperty)) {
            return DateUtils.setMilliseconds(startOffsetTimeProperty.get(), 0);
        }
        return Objects.nonNull(startOffsetTime)? DateUtils.setMilliseconds(startOffsetTime, 0) : null;
    }

    public void setStartOffsetTime(Date startOffsetTime) {
        if (Objects.nonNull(startOffsetTimeProperty)) {
            startOffsetTimeProperty.set(startOffsetTime);
        } else {
            this.startOffsetTime = startOffsetTime;
        }
    }

    public Boolean getCheckOffsetWorkingHours() {
        if (Objects.nonNull(checkOffsetWorkingHoursProperty)) {
            return checkOffsetWorkingHoursProperty.get();
        }
        return checkOffsetWorkingHours;
    }

    public void setCheckOffsetWorkingHours(Boolean checkOffsetWorkingHours) {
        if (Objects.nonNull(checkOffsetWorkingHoursProperty)) {
            checkOffsetWorkingHoursProperty.set(checkOffsetWorkingHours);
        } else {
            this.checkOffsetWorkingHours = checkOffsetWorkingHours;
        }
    }

    public Date getOpeningTime() {
        if (Objects.nonNull(openingTimeProperty)) {
            return openingTimeProperty.get();
        }
        return openingTime;
    }

    public void setOpeningTime(Date openingTime) {
        if (Objects.nonNull(openingTimeProperty)) {
            openingTimeProperty.set(openingTime);
        } else {
            this.openingTime = openingTime;
        }
    }

    public Date getClosingTime() {
        if (Objects.nonNull(closingTimeProperty)) {
            return closingTimeProperty.get();
        }
        return closingTime;
    }

    public void setClosingTime(Date closingTime) {
        if (Objects.nonNull(closingTimeProperty)) {
            closingTimeProperty.set(closingTime);
        } else {
            this.closingTime = closingTime;
        }
    }
}
