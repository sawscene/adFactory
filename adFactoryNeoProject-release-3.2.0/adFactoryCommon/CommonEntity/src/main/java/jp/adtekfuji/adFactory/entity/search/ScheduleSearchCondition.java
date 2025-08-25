/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 予定情報検索条件
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "scheduleSearchCondition")
public class ScheduleSearchCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Date fromDate = null;
    @XmlElement()
    private Date toDate = null;
    @XmlElementWrapper(name = "organizationIds")
    @XmlElement(name = "organizationId")
    private List<Long> organizationIdCollection = null;

    /**
     * コンストラクタ
     */
    public ScheduleSearchCondition() {
    }

    /**
     * 日付範囲の先頭を設定する。
     *
     * @param fromDate 日付範囲の先頭
     * @return 休日情報検索条件
     */
    public ScheduleSearchCondition fromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    /**
     * 日付範囲の末尾を設定する。
     *
     * @param toDate 日付範囲の末尾
     * @return 休日情報検索条件
     */
    public ScheduleSearchCondition toDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    /**
     * 組織ID一覧を設定する。
     *
     * @param organizationIdList 組織ID一覧
     * @return 休日情報検索条件
     */
    public ScheduleSearchCondition organizationIdList(List<Long> organizationIdList) {
        this.organizationIdCollection = organizationIdList;
        return this;
    }

    /**
     * 日付範囲の先頭を取得する。
     *
     * @return 日付範囲の先頭
     */
    public Date getFromDate() {
        return this.fromDate;
    }

    /**
     * 日付範囲の先頭を設定する。
     *
     * @param fromDate 日付範囲の先頭
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * 日付範囲の末尾を取得する。
     *
     * @return 日付範囲の末尾
     */
    public Date getToDate() {
        return this.toDate;
    }

    /**
     * 日付範囲の末尾を設定する。
     * @param toDate 日付範囲の末尾
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * 組織ID一覧を取得する。
     *
     * @return 組織ID一覧
     */
    public List<Long> getOrganizationIdCollection() {
        return organizationIdCollection;
    }

    /**
     * 組織ID一覧を設定する。
     *
     * @param organizationIdCollection 組織ID一覧
     */
    public void setOrganizationIdCollection(List<Long> organizationIdCollection) {
        this.organizationIdCollection = organizationIdCollection;
    }

    @Override
    public String toString() {
        return new StringBuilder("ScheduleSearchCondition{")
                .append("fromDate=").append(this.fromDate)
                .append(", ")
                .append("toDate=").append(this.toDate)
                .append(", ")
                .append("organizationIdCollection=").append(this.organizationIdCollection)
                .append("}")
                .toString();
    }
}
