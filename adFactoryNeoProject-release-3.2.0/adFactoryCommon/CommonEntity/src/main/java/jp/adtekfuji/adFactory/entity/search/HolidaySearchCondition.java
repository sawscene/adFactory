/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import java.io.Serializable;
import java.util.Date;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 休日情報検索条件
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "holidaySearchCondition")
public class HolidaySearchCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Date fromDate = null;
    @XmlElement()
    private Date toDate = null;

    /**
     * コンストラクタ
     */
    public HolidaySearchCondition() {
    }

    /**
     * 日付範囲の先頭を設定する。
     *
     * @param fromDate 日付範囲の先頭
     * @return 休日情報検索条件
     */
    public HolidaySearchCondition fromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    /**
     * 日付範囲の末尾を設定する。
     *
     * @param toDate 日付範囲の末尾
     * @return 休日情報検索条件
     */
    public HolidaySearchCondition toDate(Date toDate) {
        this.toDate = toDate;
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

    @Override
    public String toString() {
        return new StringBuilder("HolidaySearchCondition{")
                .append("fromDate=").append(this.fromDate)
                .append(", ")
                .append("toDate=").append(this.toDate)
                .append("}")
                .toString();
    }
}
