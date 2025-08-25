/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.io.Serializable;
import java.util.Date;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 進捗モニタ 作業終了予想時間情報
 *
 * @author s-heya
 */
@XmlRootElement(name = "estimatedTimeInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class EstimatedTimeInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 作業終了予定時間
     */
    private Date scheduledTime;
    
    /**
     * 作業終了予想時間
     */
    private Date estimatedTime;

    /**
     * 残りサイクル数
     */
    private Integer remaining;

    /**
     * コンストラクタ
     */
    public EstimatedTimeInfoEntity() {
    }

    /**
     * 作業終了予定時間を取得する。
     * 
     * @return 作業終了予定時間
     */
    public Date getScheduledTime() {
        return scheduledTime;
    }

    /**
     * 作業終了予定時間を設定する。
     * 
     * @param scheduledTime 作業終了予定時間
     */
    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    /**
     * 作業終了予想時間を取得する。
     *
     * @return 作業終了予想時間
     */
    public Date getEstimatedTime() {
        return this.estimatedTime;
    }

    /**
     * 作業終了予想時間を設定する。
     *
     * @param estimatedTime 作業終了予想時間
     */
    public void setEstimatedTime(Date estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    /**
     * 残りサイクル数を取得する。
     *
     * @return 残りサイクル数
     */
    public Integer getRemaining() {
        return this.remaining;
    }

    /**
     * 残りサイクル数を設定する。
     *
     * @param remaining 残りサイクル数
     */
    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final MonitorPlanNumInfoEntity other = (MonitorPlanNumInfoEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("EstimatedTimeInfoEntity{")
                .append("estimatedTime=").append(this.estimatedTime)
                .append(", ")
                .append("remaining=").append(this.remaining)
                .append("}")
                .toString();
    }
}
