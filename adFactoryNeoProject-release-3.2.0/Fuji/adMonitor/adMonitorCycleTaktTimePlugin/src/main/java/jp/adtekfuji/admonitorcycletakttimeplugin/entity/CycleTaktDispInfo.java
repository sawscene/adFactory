/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorcycletakttimeplugin.entity;

import jp.adtekfuji.admonitorcycletakttimeplugin.enumerate.CycleTaktDispTypeEnum;
import java.time.LocalTime;

/**
 * サイクルタクトタイム表示情報
 *
 * @author nar-nakamura
 */
public class CycleTaktDispInfo {
    private LocalTime cycleStartTime;
    private LocalTime cycleEndTime;
    private Long taktTime;
    private Long remainingTime;
    private String productionNum;
    private String taktTimeMin;
    private String taktTimeSec;
    private CycleTaktDispTypeEnum cycleType;

    /**
     * サイクルタクトタイム表示情報
     */
    public CycleTaktDispInfo() {
    }

    /**
     * サイクル開始時刻を取得する。
     *
     * @return 
     */
    public LocalTime getCycleStartTime() {
        return this.cycleStartTime;
    }

    /**
     * サイクル開始時刻を設定する。
     *
     * @param value 
     */
    public void setCycleStartTime(LocalTime value) {
        this.cycleStartTime = value;
    }

    /**
     * サイクル終了時刻を取得する。
     *
     * @return 
     */
    public LocalTime getCycleEndTime() {
        return this.cycleEndTime;
    }

    /**
     * サイクル終了時刻を設定取得する。
     *
     * @param value 
     */
    public void setCycleEndTime(LocalTime value) {
        this.cycleEndTime = value;
    }

    /**
     * タクトタイム(秒)を取得する。
     *
     * @return 
     */
    public Long getTaktTime() {
        return this.taktTime;
    }

    /**
     * タクトタイム(秒)を設定する。
     *
     * @param value 
     */
    public void setTaktTime(Long value) {
        this.taktTime = value;
    }

    /**
     * タクトタイムの残り時間(秒)を取得する。
     *
     * @return 
     */
    public Long getRemainingTime() {
        return this.remainingTime;
    }

    /**
     * タクトタイムの残り時間(秒)を設定する。
     *
     * @param value 
     */
    public void setRemainingTime(Long value) {
        this.remainingTime = value;
    }

    /**
     * 生産数を取得する。
     *
     * @return 
     */
    public String getProductionNum() {
        return this.productionNum;
    };

    /**
     * 生産数を設定する。
     *
     * @param value 
     */
    public void setProductionNum(String value) {
        this.productionNum = value;
    };

    /**
     * タクトの分部分(MM)を取得する。
     *
     * @return 
     */
    public String getTaktTimeMin() {
        return this.taktTimeMin;
    };

    /**
     * タクトの分部分(MM)を設定する。
     *
     * @param value 
     */
    public void setTaktTimeMin(String value) {
        this.taktTimeMin = value;
    };

    /**
     * タクトの秒部分(ss)を取得する。
     *
     * @return 
     */
    public String getTaktTimeSec() {
        return this.taktTimeSec;
    };

    /**
     * タクトの秒部分(ss)を設定する。
     *
     * @param value 
     */
    public void setTaktTimeSec(String value) {
        this.taktTimeSec = value;
    };

    /**
     * サイクルタクトタイム表示種別を取得する。
     *
     * @return 
     */
    public CycleTaktDispTypeEnum getCycleType() {
        return this.cycleType;
    };

    /**
     * サイクルタクトタイム表示種別を設定する。
     *
     * @param value 
     */
    public void setCycleType(CycleTaktDispTypeEnum value) {
        this.cycleType = value;
    };
}
