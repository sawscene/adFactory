/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.holiday;

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
 * 休日情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "holiday")
public class HolidayInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty holidayIdProperty;
    private StringProperty holidayNameProperty;
    private ObjectProperty<Date> holidayDateProperty;

    @XmlElement(required = true)
    private Long holidayId;
    @XmlElement()
    private String holidayName;
    @XmlElement()
    private Date holidayDate;

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    /**
     * コンストラクタ
     */
    public HolidayInfoEntity() {
    }

    /**
     * 休日IDプロパティを取得する。
     *
     * @return 休日ID
     */
    public LongProperty holidayIdProperty() {
        if (Objects.isNull(this.holidayIdProperty)) {
            this.holidayIdProperty = new SimpleLongProperty(this.holidayId);
        }
        return this.holidayIdProperty;
    }

    /**
     * 休日の名称プロパティを取得する。
     *
     * @return 休日の名称
     */
    public StringProperty holidayNameProperty() {
        if (Objects.isNull(this.holidayNameProperty)) {
            this.holidayNameProperty = new SimpleStringProperty(this.holidayName);
        }
        return this.holidayNameProperty;
    }

    /**
     * 休日の日付プロパティを取得する。
     *
     * @return 休日の日付
     */
    public ObjectProperty<Date> holidayDateProperty() {
        if (Objects.isNull(this.holidayDateProperty)) {
            this.holidayDateProperty = new SimpleObjectProperty(this.holidayDate);
        }
        return this.holidayDateProperty;
    }

    /**
     * 休日IDを取得する。
     *
     * @return 休日ID
     */
    public Long getHolidayId() {
        if (Objects.nonNull(this.holidayIdProperty)) {
            return this.holidayIdProperty.get();
        }
        return this.holidayId;
    }

    /**
     * 休日IDを設定する。
     *
     * @param holidayId 休日ID
     */
    public void setHolidayId(Long holidayId) {
        if (Objects.nonNull(this.holidayIdProperty)) {
            this.holidayIdProperty.set(holidayId);
        } else {
            this.holidayId = holidayId;
        }
    }

    /**
     * 休日の名称を取得する。
     *
     * @return 休日の名称
     */
    public String getHolidayName() {
        if (Objects.nonNull(this.holidayNameProperty)) {
            return this.holidayNameProperty.get();
        }
        return this.holidayName;
    }

    /**
     * 休日の名称を設定する。
     *
     * @param holidayName 休日の名称
     */
    public void setHolidayName(String holidayName) {
        if (Objects.nonNull(this.holidayNameProperty)) {
            this.holidayNameProperty.set(holidayName);
        } else {
            this.holidayName = holidayName;
        }
    }

    /**
     * 休日の日付を取得する。
     *
     * @return 休日の日付
     */
    public Date getHolidayDate() {
        if (Objects.nonNull(this.holidayDateProperty)) {
            return this.holidayDateProperty.get();
        }
        return this.holidayDate;
    }

    /**
     * 休日の日付を設定する。
     *
     * @param holidayDate 休日の日付
     */
    public void setHolidayDate(Date holidayDate) {
        if (Objects.nonNull(this.holidayDateProperty)) {
            this.holidayDateProperty.set(holidayDate);
        } else {
            this.holidayDate = holidayDate;
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
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.holidayId);
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
        final HolidayInfoEntity other = (HolidayInfoEntity) obj;
        if (!Objects.equals(this.holidayId, other.holidayId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("HolidayInfoEntity{")
                .append("holidayId=").append(this.holidayId)
                .append(", ")
                .append("holidayName=").append(this.holidayName)
                .append(", ")
                .append("holidayDate=").append(this.holidayDate)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
