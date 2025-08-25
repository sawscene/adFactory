/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.enumerate.LineManagedStateEnum;

/**
 * 進捗モニタ カウントダウン情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "monitorLineTimerInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitorLineTimerInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LineManagedStateEnum lineTimerState = LineManagedStateEnum.START_WAIT;// ライン制御ステータス
    private Date startTime;// 開始時間
    private Long leftTimeSec = 0L;// 残り時間[sec]
    private Long taktTime = 0L;// タクトタイム

    @XmlTransient
    private final Map<Long, Integer> delivered = new HashMap<>();// 完了数一覧

    /**
     * サイクル数
     */
    @XmlTransient
    private int cycle = 0;

    /**
     * 完了時間
     */
    @XmlTransient
    private long compTime;

    /**
     * コンストラクタ
     */
    public MonitorLineTimerInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param lineTimer カウントダウン情報
     */
    public MonitorLineTimerInfoEntity(MonitorLineTimerInfoEntity lineTimer) {
        this.lineTimerState = lineTimer.getLineTimerState();
        this.startTime = lineTimer.getStartTime();
        this.leftTimeSec = lineTimer.getLeftTimeSec();
        this.taktTime = lineTimer.getTaktTime();
    }

    /**
     * 開始時間を設定して、カウントダウン情報を取得する。
     *
     * @param startTime 開始時間
     * @return カウントダウン情報
     */
    public MonitorLineTimerInfoEntity startTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * 残り時間[sec]を設定して、カウントダウン情報を取得する。
     *
     * @param leftTime 残り時間[sec]
     * @return カウントダウン情報
     */
    public MonitorLineTimerInfoEntity leftTime(Long leftTime) {
        this.leftTimeSec = leftTime;
        return this;
    }

    /**
     * タクトタイムを設定して、カウントダウン情報を取得する。
     *
     * @param taktTime タクトタイム
     * @return カウントダウン情報
     */
    public MonitorLineTimerInfoEntity taktTime(Long taktTime) {
        this.taktTime = taktTime;
        return this;
    }

    /**
     * ライン制御ステータスを取得する。
     *
     * @return ライン制御ステータス
     */
    public LineManagedStateEnum getLineTimerState() {
        return this.lineTimerState;
    }

    /**
     * ライン制御ステータスを設定する。
     *
     * @param lineTimerState ライン制御ステータス
     */
    public void setLineTimerState(LineManagedStateEnum lineTimerState) {
        this.lineTimerState = lineTimerState;
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
     * 残り時間[sec]を取得する。
     *
     * @return 残り時間[sec]
     */
    public Long getLeftTimeSec() {
        return this.leftTimeSec;
    }

    /**
     * 残り時間[sec]を設定する。
     *
     * @param leftTimeSec 残り時間[sec]
     */
    public void setLeftTimeSec(Long leftTimeSec) {
        this.leftTimeSec = leftTimeSec;
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
     * 完了数一覧を取得する。
     *
     * @return 完了数一覧
     */
    public Map<Long, Integer> delivered() {
        return this.delivered;
    }

    /**
     * サイクル数を取得する。
     *
     * @return サイクル数
     */
    public int getCycle() {
        return this.cycle;
    }

    /**
     * サイクル数を設定する。
     *
     * @param cycle サイクル数
     */
    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    /**
     * 完了時間を取得する。
     *
     * @return 完了時間
     */
    public long getCompTime() {
        return this.compTime;
    }

    /**
     * 完了時間を設定する。
     *
     * @param compTime 完了時間
     */
    public void setCompTime(long compTime) {
        this.compTime = compTime;
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
        final MonitorLineTimerInfoEntity other = (MonitorLineTimerInfoEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorLineTimerInfoEntity{")
                .append("lineTimerState=").append(this.lineTimerState)
                .append(", ")
                .append("startTime=").append(this.startTime)
                .append(", ")
                .append("leftTimeSec=").append(this.leftTimeSec)
                .append(", ")
                .append("taktTime=").append(this.taktTime)
                .append(", ")
                .append("delivered=").append(this.delivered)
                .append(", ")
                .append("cycle=").append(this.cycle)
                .append(", ")
                .append("compTime=").append(this.compTime)
                .append("}")
                .toString();
    }
}
