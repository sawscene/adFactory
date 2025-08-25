/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.holiday;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 休日情報
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "mst_holiday")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "holiday")
public class HolidayEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "holiday_id")
    private Long holidayId;// 休日ID

    @Size(max = 256)
    @Column(name = "holiday_name")
    private String holidayName;// 休日の名称

    @Column(name = "holiday_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date holidayDate;// 休日の日付

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * コンストラクタ
     */
    public HolidayEntity() {
    }

    /**
     * 休日IDを取得する。
     *
     * @return 休日ID
     */
    public Long getHolidayId() {
        return this.holidayId;
    }

    /**
     * 休日IDを設定する。
     *
     * @param holidayId 休日ID
     */
    public void setHolidayId(Long holidayId) {
        this.holidayId = holidayId;
    }

    /**
     * 休日の名称を取得する。
     *
     * @return 休日の名称
     */
    public String getHolidayName() {
        return this.holidayName;
    }

    /**
     * 休日の名称を設定する。
     *
     * @param holidayName 休日の名称
     */
    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    /**
     * 休日の日付を取得する。
     *
     * @return 休日の日付
     */
    public Date getHolidayDate() {
        return this.holidayDate;
    }

    /**
     * 休日の日付を設定する。
     *
     * @param holidayDate 休日の日付
     */
    public void setHolidayDate(Date holidayDate) {
        this.holidayDate = holidayDate;
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
        hash = 79 * hash + Objects.hashCode(this.holidayId);
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
        final HolidayEntity other = (HolidayEntity) obj;
        if (!Objects.equals(this.holidayId, other.holidayId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("HolidayEntity{")
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
