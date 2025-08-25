/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin.util;

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
public class WorkPlanWorkGroupPropertyData {

    private LongProperty qauntityProperty;
    private ObjectProperty<Date> startTimeProperty;

    private Long qauntity;
    private Date startTime;

    /**
     *
     */
    public WorkPlanWorkGroupPropertyData() {
    }

    /**
     *
     * @param qauntity
     * @param startTime
     */
    public WorkPlanWorkGroupPropertyData(Long qauntity, Date startTime) {
        this.qauntity = qauntity;
        this.startTime = startTime;
    }

    /**
     *
     * @return
     */
    public LongProperty qauntityProperty() {
        if (Objects.isNull(this.qauntityProperty)) {
            this.qauntityProperty = new SimpleLongProperty(this.qauntity);
        }
        return this.qauntityProperty;
    }

    /**
     *
     * @return
     */
    public ObjectProperty<Date> startTimeProperty() {
        if (Objects.isNull(this.startTimeProperty)) {
            this.startTimeProperty = new SimpleObjectProperty<>(this.startTime);
        }
        return this.startTimeProperty;
    }

    /**
     *
     * @return
     */
    public Long getQauntity() {
        if (Objects.nonNull(this.qauntityProperty)) {
            return this.qauntityProperty.get();
        }
        return this.qauntity;
    }

    /**
     *
     * @param qauntity
     */
    public void setQauntity(Long qauntity) {
        if (Objects.nonNull(this.qauntityProperty)) {
            this.qauntityProperty.set(qauntity);
        } else {
            this.qauntity = qauntity;
        }
    }

    /**
     *
     * @return
     */
    public Date getStartTime() {
        if (Objects.nonNull(this.startTimeProperty)) {
            return this.startTimeProperty.get();
        }
        return this.startTime;
    }

    /**
     *
     * @param startTime
     */
    public void setStartTime(Date startTime) {
        if (Objects.nonNull(this.startTimeProperty)) {
            this.startTimeProperty.set(startTime);
        } else {
            this.startTime = startTime;
        }
    }

    /**
     *
     */
    public void update() {
        this.qauntity = getQauntity();
        this.startTime = getStartTime();
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkGroupPropertyData{")
                .append("qauntity=").append(this.qauntity)
                .append(", startTime=").append(this.startTime)
                .append("}")
                .toString();
    }
}
