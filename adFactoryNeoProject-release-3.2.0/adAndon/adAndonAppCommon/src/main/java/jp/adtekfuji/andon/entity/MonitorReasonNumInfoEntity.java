/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 進捗モニタ 理由回数情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "monitorReasonNumInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitorReasonNumInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String reason;// 理由
    private Integer reasonNum;// 回数

    /**
     * コンストラクタ
     */
    public MonitorReasonNumInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param reason 理由
     * @param reasonNum 回数
     */
    public MonitorReasonNumInfoEntity(String reason, Integer reasonNum) {
        this.reason = reason;
        this.reasonNum = reasonNum;
    }

    /**
     * 理由を設定して、理由回数情報を取得する。
     *
     * @param reason 理由
     * @return 理由回数情報
     */
    public MonitorReasonNumInfoEntity reason(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * 回数を設定して、理由回数情報を取得する。
     *
     * @param reasonNum 回数
     * @return 理由回数情報
     */
    public MonitorReasonNumInfoEntity reasonNum(Integer reasonNum) {
        this.reasonNum = reasonNum;
        return this;
    }

    /**
     * 理由を取得する。
     *
     * @return 理由
     */
    public String getReason() {
        return this.reason;
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
        return this.reasonNum;
    }

    /**
     * 回数を設定する。
     *
     * @param reasonNum 回数
     */
    public void setReasonNum(Integer reasonNum) {
        this.reasonNum = reasonNum;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.reason);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MonitorReasonNumInfoEntity other = (MonitorReasonNumInfoEntity) obj;
        if (!Objects.equals(this.reason, other.reason)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorReasonNumInfoEntity{")
                .append("reason=").append(this.reason)
                .append(", ")
                .append("reasonNum=").append(this.reasonNum)
                .append("}")
                .toString();
    }
}
