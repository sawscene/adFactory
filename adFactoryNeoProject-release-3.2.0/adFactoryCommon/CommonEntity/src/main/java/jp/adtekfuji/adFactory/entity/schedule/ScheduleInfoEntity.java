/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.schedule;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 予定情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "schedule")
public class ScheduleInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty scheduleIdProperty;
    private StringProperty scheduleNameProperty;
    private ObjectProperty<Date> scheduleFromDateProperty;
    private ObjectProperty<Date> scheduleToDateProperty;
    private LongProperty fkOrganizationIdProperty;

    @XmlElement(required = true)
    private Long scheduleId;
    @XmlElement()
    private String scheduleName;
    @XmlElement()
    private Date scheduleFromDate;
    @XmlElement()
    private Date scheduleToDate;
    @XmlElement()
    private Long fkOrganizationId;

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    /**
     * コンストラクタ
     */
    public ScheduleInfoEntity() {
    }

    /**
     * 予定IDプロパティを取得する。
     *
     * @return 予定ID
     */
    public LongProperty scheduleIdProperty() {
        if (Objects.isNull(this.scheduleIdProperty)) {
            this.scheduleIdProperty = new SimpleLongProperty(this.scheduleId);
        }
        return this.scheduleIdProperty;
    }

    /**
     * 予定の名称プロパティを取得する。
     *
     * @return 予定の名称
     */
    public StringProperty scheduleNameProperty() {
        if (Objects.isNull(this.scheduleNameProperty)) {
            this.scheduleNameProperty = new SimpleStringProperty(this.scheduleName);
        }
        return this.scheduleNameProperty;
    }

    /**
     * 予定日時の先頭プロパティを取得する。
     *
     * @return 予定日時の先頭
     */
    public ObjectProperty<Date> scheduleFromDateProperty() {
        if (Objects.isNull(this.scheduleFromDateProperty)) {
            this.scheduleFromDateProperty = new SimpleObjectProperty(this.scheduleFromDate);
        }
        return this.scheduleFromDateProperty;
    }

    /**
     * 予定日時の末尾プロパティを取得する。
     *
     * @return 予定日時の末尾
     */
    public ObjectProperty<Date> scheduleToDateProperty() {
        if (Objects.isNull(this.scheduleToDateProperty)) {
            this.scheduleToDateProperty = new SimpleObjectProperty(this.scheduleToDate);
        }
        return this.scheduleToDateProperty;
    }

    /**
     * 組織IDプロパティを取得する。
     *
     * @return 組織ID
     */
    public LongProperty fkOrganizationIdProperty() {
        if (Objects.isNull(this.fkOrganizationIdProperty)) {
            this.fkOrganizationIdProperty = new SimpleLongProperty(this.fkOrganizationId);
        }
        return this.fkOrganizationIdProperty;
    }

    /**
     * 予定IDを取得する。
     *
     * @return 予定ID
     */
    public Long getScheduleId() {
        if (Objects.nonNull(this.scheduleIdProperty)) {
            return this.scheduleIdProperty.get();
        }
        return this.scheduleId;
    }

    /**
     * 予定IDを設定する。
     *
     * @param scheduleId 予定ID
     */
    public void setScheduleId(Long scheduleId) {
        if (Objects.nonNull(this.scheduleIdProperty)) {
            this.scheduleIdProperty.set(scheduleId);
        } else {
            this.scheduleId = scheduleId;
        }
    }

    /**
     * 予定の名称を取得する。
     *
     * @return 予定の名称
     */
    public String getScheduleName() {
        if (Objects.nonNull(this.scheduleNameProperty)) {
            return this.scheduleNameProperty.get();
        }
        return this.scheduleName;
    }

    /**
     * 予定の名称を設定する。
     *
     * @param scheduleName 予定の名称
     */
    public void setScheduleName(String scheduleName) {
        if (Objects.nonNull(this.scheduleNameProperty)) {
            this.scheduleNameProperty.set(scheduleName);
        } else {
            this.scheduleName = scheduleName;
        }
    }

    /**
     * 予定日時の先頭を取得する。
     *
     * @return 予定日時の先頭
     */
    public Date getScheduleFromDate() {
        if (Objects.nonNull(this.scheduleFromDateProperty)) {
            return this.scheduleFromDateProperty.get();
        }
        return this.scheduleFromDate;
    }

    /**
     * 予定日時の先頭を設定する。
     *
     * @param scheduleFromDate 予定日時の先頭
     */
    public void setScheduleFromDate(Date scheduleFromDate) {
        if (Objects.nonNull(this.scheduleFromDateProperty)) {
            this.scheduleFromDateProperty.set(scheduleFromDate);
        } else {
            this.scheduleFromDate = scheduleFromDate;
        }
    }

    /**
     * 予定日時の末尾を取得する。
     *
     * @return 予定日時の末尾
     */
    public Date getScheduleToDate() {
        if (Objects.nonNull(this.scheduleToDateProperty)) {
            return this.scheduleToDateProperty.get();
        }
        return this.scheduleToDate;
    }

    /**
     * 予定日時の末尾を設定する。
     *
     * @param scheduleToDate 予定日時の末尾
     */
    public void setScheduleToDate(Date scheduleToDate) {
        if (Objects.nonNull(this.scheduleToDateProperty)) {
            this.scheduleToDateProperty.set(scheduleToDate);
        } else {
            this.scheduleToDate = scheduleToDate;
        }
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getFkOrganizationId() {
        if (Objects.nonNull(this.fkOrganizationIdProperty)) {
            return this.fkOrganizationIdProperty.get();
        }
        return this.fkOrganizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param fkOrganizationId 組織ID
     */
    public void setFkOrganizationId(Long fkOrganizationId) {
        if (Objects.nonNull(this.fkOrganizationIdProperty)) {
            this.fkOrganizationIdProperty.set(fkOrganizationId);
        } else {
            this.fkOrganizationId = fkOrganizationId;
        }
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.scheduleId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ScheduleInfoEntity other = (ScheduleInfoEntity) obj;
        if (!Objects.equals(this.scheduleId, other.scheduleId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ScheduleInfoEntity{")
                .append("scheduleId=").append(this.scheduleId)
                .append(", ")
                .append("scheduleName=").append(this.scheduleName)
                .append(", ")
                .append("scheduleFromDate=").append(this.scheduleFromDate)
                .append(", ")
                .append("scheduleToDate=").append(this.scheduleToDate)
                .append(", ")
                .append("fkOrganizationId=").append(this.fkOrganizationId)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
