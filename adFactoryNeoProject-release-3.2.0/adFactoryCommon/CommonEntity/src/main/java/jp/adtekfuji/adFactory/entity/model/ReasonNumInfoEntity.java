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
 * 理由回数情報
 *
 * @author kentarou.suzuki
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "reasonNumInfoEntity")
public class ReasonNumInfoEntity {

    /**
     * 理由
     */
    @XmlElement()
    private String reason;

    /**
     * 回数
     */
    @XmlElement()
    Integer reasonNum;

    public ReasonNumInfoEntity() {
    }

    public ReasonNumInfoEntity(String reason, Integer reasonNum) {
        this.reason = reason;
        this.reasonNum = reasonNum;
    }

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
     * 回数を取得する。
     *
     * @return 回数
     */
    public Integer getReasonNum() {
        return reasonNum;
    }

    /**
     * 回数を設定する。
     *
     * @param reasonNum 回数
     */
    public void setReasonNum(Integer reasonNum) {
        this.reasonNum = reasonNum;
    }

    /**
     * 文字列表現を取得する。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "ReasonNumInfoEntity{" +
                "reason=" + reason +
                ", reasonNum=" + reasonNum +
                '}';
    }
}
