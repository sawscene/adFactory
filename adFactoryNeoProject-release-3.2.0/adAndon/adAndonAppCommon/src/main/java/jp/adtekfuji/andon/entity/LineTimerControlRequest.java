/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.LineManagedCommandEnum;

/**
 * ラインタイマー制御要求情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "lineTimerControlRequest")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class LineTimerControlRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long monitorId;// 進捗モニタの設備ID
    private LineManagedCommandEnum command;// ライン制御コマンド
    private Date startTime;// 開始時間
    private Long startCountTime;// 開始カウント時間
    private Long taktTime;// タクトタイム
    private String message;// メッセージ

    /**
     * コンストラクタ
     */
    public LineTimerControlRequest() {
    }

    /**
     * コンストラクタ
     *
     * @param monitorId 進捗モニタの設備ID
     * @param command ライン制御コマンド
     * @param startTime 開始時間
     * @param startCountTime 開始カウント時間
     * @param taktTime タクトタイム
     */
    public LineTimerControlRequest(Long monitorId, LineManagedCommandEnum command, Date startTime, Long startCountTime, Long taktTime) {
        this.monitorId = monitorId;
        this.command = command;
        this.startTime = startTime;
        this.startCountTime = startCountTime;
        this.taktTime = taktTime;
    }

    /**
     * 進捗モニタの設備IDを取得する。
     *
     * @return 進捗モニタの設備ID
     */
    public Long getMonitorId() {
        return this.monitorId;
    }

    /**
     * 進捗モニタの設備IDを設定する。
     *
     * @param monitorId 進捗モニタの設備ID
     */
    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    /**
     * ライン制御コマンドを取得する。
     *
     * @return ライン制御コマンド
     */
    public LineManagedCommandEnum getCommand() {
        return this.command;
    }

    /**
     * ライン制御コマンドを設定する。
     *
     * @param command ライン制御コマンド
     */
    public void setCommand(LineManagedCommandEnum command) {
        this.command = command;
    }

    /**
     * 開始時間を取得する。
     *
     * @return 開始時間
     */
    public Date getStartTime() {
        return this.startTime;
    }

    /**
     * 開始時間を設定する。
     *
     * @param startTime 開始時間
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 開始カウント時間を取得する。
     *
     * @return 開始カウント時間
     */
    public Long getStartCountTime() {
        return this.startCountTime;
    }

    /**
     * 開始カウント時間を設定する。
     *
     * @param startCountTime 開始カウント時間
     */
    public void setStartCountTime(Long startCountTime) {
        this.startCountTime = startCountTime;
    }

    /**
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public Long getTaktTime() {
        return this.taktTime;
    }

    /**
     * タクトタイムを設定する。
     *
     * @param taktTime タクトタイム
     */
    public void setTaktTime(Long taktTime) {
        this.taktTime = taktTime;
    }

    /**
     * メッセージを取得する。
     *
     * @return メッセージ
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * メッセージを設定する。
     *
     * @param message メッセージ
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.monitorId);
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
        final LineTimerControlRequest other = (LineTimerControlRequest) obj;
        if (!Objects.equals(this.monitorId, other.monitorId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("LineTimerControlRequest{")
                .append("monitorId=").append(this.monitorId)
                .append(", ")
                .append("command=").append(this.command)
                .append(", ")
                .append("startTime=").append(this.startTime)
                .append(", ")
                .append("startCountTime=").append(this.startCountTime)
                .append(", ")
                .append("taktTime=").append(this.taktTime)
                .append(", ")
                .append("message=").append(this.message)
                .append("}")
                .toString();
    }
}
