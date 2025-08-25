/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.schedule;

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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 予定情報
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "mst_schedule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "schedule")
public class ScheduleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "schedule_id")
    private Long scheduleId;// 予定ID

    @Size(max = 256)
    @Column(name = "schedule_name")
    private String scheduleName;// 予定の名称

    @Column(name = "schedule_from_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduleFromDate;// 予定日時の先頭

    @Column(name = "schedule_to_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduleToDate;// 予定日時の末尾

    @Column(name = "organization_id")
    @XmlElement(name = "fkOrganizationId")
    private Long organizationId;// 組織ID

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * コンストラクタ
     */
    public ScheduleEntity() {
    }

    /**
     * 予定IDを取得する。
     *
     * @return 予定ID
     */
    public Long getScheduleId() {
        return scheduleId;
    }

    /**
     * 予定IDを設定する。
     *
     * @param scheduleId 予定ID
     */
    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    /**
     * 予定の名称を取得する。
     *
     * @return 予定の名称
     */
    public String getScheduleName() {
        return scheduleName;
    }

    /**
     * 予定の名称を設定する。
     *
     * @param scheduleName 予定の名称
     */
    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    /**
     * 予定日時の先頭を取得する。
     *
     * @return 予定日時の先頭
     */
    public Date getScheduleFromDate() {
        return scheduleFromDate;
    }

    /**
     * 予定日時の先頭を設定する。
     *
     * @param scheduleFromDate 予定日時の先頭
     */
    public void setScheduleFromDate(Date scheduleFromDate) {
        this.scheduleFromDate = scheduleFromDate;
    }

    /**
     * 予定日時の末尾を取得する。
     *
     * @return 予定日時の末尾
     */
    public Date getScheduleToDate() {
        return scheduleToDate;
    }

    /**
     * 予定日時の末尾を設定する。
     *
     * @param scheduleToDate 予定日時の末尾
     */
    public void setScheduleToDate(Date scheduleToDate) {
        this.scheduleToDate = scheduleToDate;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getOrganizationId() {
        return organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.scheduleId);
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
        final ScheduleEntity other = (ScheduleEntity) obj;
        if (!Objects.equals(this.scheduleId, other.scheduleId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ScheduleEntity{")
                .append("scheduleId=").append(this.scheduleId)
                .append(", ")
                .append("scheduleName=").append(this.scheduleName)
                .append(", ")
                .append("scheduleFromDate=").append(this.scheduleFromDate)
                .append(", ")
                .append("scheduleToDate=").append(this.scheduleToDate)
                .append(", ")
                .append("organizationId=").append(this.organizationId)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
