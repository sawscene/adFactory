/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 理由時間情報
 *
 * @author kentarou.suzuki
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "reasonTimeInfoEntity")
public class ReasonTimeInfoEntity {

    /**
     * 理由
     */
    @XmlElement()
    private String reason;

    /**
     * ロス時間
     */
    @XmlElement()
    Double loseTime;

    /**
     * 理由を取得する。
     *
     * @return 理由
     */
    public String getReason() {
        return reason;
    }

    /**
     * 理由を設定する。
     *
     * @param reason 理由
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * ロス時間を取得する。
     *
     * @return ロス時間
     */
    public Double getLoseTime() {
        return loseTime;
    }

    /**
     * ロス時間を設定する。
     *
     * @param loseTime ロス時間
     */
    public void setLoseTime(Double loseTime) {
        this.loseTime = loseTime;
    }

    /**
     * 文字列表現を取得する。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "ReasonTimeInfoEntity{" +
                "reason=" + reason +
                ", loseTime=" + loseTime +
                '}';
    }
}
