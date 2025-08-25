/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.Date;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 計画時間変更条件
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "planChangeCondition")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlanChangeCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Date startDatetime;
    @XmlElement()
    private Date interruptFromTime;
    @XmlElement()
    private Date interruptToTime;

    /**
     * コンストラクタ
     */
    public PlanChangeCondition() {
    }

    /**
     * コンストラクタ
     *
     * @param startDatetime 開始日時
     * @param interruptFromTime 中断時間の開始時刻
     * @param interruptToTime 中断時間の終了時刻
     */
    public PlanChangeCondition(Date startDatetime, Date interruptFromTime, Date interruptToTime) {
        this.startDatetime = startDatetime;
        this.interruptFromTime = interruptFromTime;
        this.interruptToTime = interruptToTime;
    }

    /**
     * 開始日時を取得する。
     *
     * @return 開始日時
     */
    public Date getStartDatetime() {
        return this.startDatetime;
    }

    /**
     * 開始日時を設定する。
     *
     * @param startDatetime 開始日時
     */
    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    /**
     * 中断時間の開始時刻を取得する。
     *
     * @return 中断時間の開始時刻
     */
    public Date getInterruptFromTime() {
        return this.interruptFromTime;
    }

    /**
     * 中断時間の開始時刻を設定する。
     *
     * @param interruptFromTime 中断時間の開始時刻
     */
    public void setInterruptFromTime(Date interruptFromTime) {
        this.interruptFromTime = interruptFromTime;
    }

    /**
     * 中断時間の終了時刻を取得する。
     *
     * @return 中断時間の終了時刻
     */
    public Date getInterruptToTime() {
        return this.interruptToTime;
    }

    /**
     * 中断時間の終了時刻を設定する。
     *
     * @param interruptToTime 中断時間の終了時刻
     */
    public void setInterruptToTime(Date interruptToTime) {
        this.interruptToTime = interruptToTime;
    }

    @Override
    public String toString() {
        return new StringBuilder("PlanChangeCondition{")
                .append("startDatetime=").append(this.startDatetime)
                .append(", interruptFromTime=").append(this.interruptFromTime)
                .append(", interruptToTime=").append(this.interruptToTime)
                .append("}")
                .toString();
    }
}
