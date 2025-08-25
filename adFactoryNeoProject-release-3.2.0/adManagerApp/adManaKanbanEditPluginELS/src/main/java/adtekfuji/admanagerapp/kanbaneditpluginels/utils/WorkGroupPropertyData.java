/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.utils;

import java.util.Date;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * ワークグループ用プロパティデータ
 * 
 * @author e.mori
 * @version 1.6.1
 * @since 2017.1.11.Wen
 */
public class WorkGroupPropertyData {
    
    private LongProperty qauntityProperty;
    private ObjectProperty<Date> startTimeProperty;

    private Long qauntity;
    private Date startTime;

    public WorkGroupPropertyData() {
    }

    public WorkGroupPropertyData(Long qauntity, Date startTime) {
        this.qauntity = qauntity;
        this.startTime = startTime;
    }

    public LongProperty qauntityProperty() {
        if (Objects.isNull(qauntityProperty)) {
            qauntityProperty = new SimpleLongProperty(qauntity);
        }
        return qauntityProperty;
    }

    public ObjectProperty<Date> startTimeProperty() {
        if (Objects.isNull(startTimeProperty)) {
            startTimeProperty = new SimpleObjectProperty<>(startTime);
        }
        return startTimeProperty;
    }

    public Long getQauntity() {
        if (Objects.nonNull(qauntityProperty)) {
            return qauntityProperty.get();
        }
        return qauntity;
    }

    public void setQauntity(Long qauntity) {
        if (Objects.nonNull(qauntityProperty)) {
            qauntityProperty.set(qauntity);
        } else {
            this.qauntity = qauntity;
        }
    }

    public Date getStartTime() {
        if (Objects.nonNull(startTimeProperty)) {
            return startTimeProperty.get();
        }
        return startTime;
    }

    public void setStartTime(Date startTime) {
        if (Objects.nonNull(startTimeProperty)) {
            startTimeProperty.set(startTime);
        } else {
            this.startTime = startTime;
        }
    }

    public void update() {
        this.qauntity = getQauntity();
        this.startTime = getStartTime();
    }

    @Override
    public String toString() {
        return "WorkGroupPropertyData{" + "qauntity=" + qauntity + ", startTime=" + startTime + '}';
    }
}
