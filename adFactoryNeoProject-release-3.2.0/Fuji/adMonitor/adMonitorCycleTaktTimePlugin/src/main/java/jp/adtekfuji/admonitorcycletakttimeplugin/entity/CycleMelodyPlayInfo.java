package jp.adtekfuji.admonitorcycletakttimeplugin.entity;

import java.time.LocalTime;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * サイクルメロディ鳴動情報
 *
 * @author nar-nakamura
 */
public class CycleMelodyPlayInfo {
    private LocalTime playTime;
    private String playFile;

    /**
     * サイクルメロディ鳴動情報
     */
    public CycleMelodyPlayInfo() {
    }

    /**
     * サイクルメロディ鳴動情報
     *
     * @param playTime メロディ再生時刻
     * @param playFile メロディファイルパス
     */
    public CycleMelodyPlayInfo(LocalTime playTime, String playFile) {
        this.playTime = playTime;
        this.playFile = playFile;
    }

    /**
     * メロディ再生時刻を取得する。
     *
     * @return メロディ再生時刻
     */
    public LocalTime getPlayTime() {
        return this.playTime;
    }

    /**
     * メロディ再生時刻を設定する。
     *
     * @param value メロディ再生時刻
     */
    public void setPlayTime(LocalTime value) {
        this.playTime = value;
    }

    /**
     * メロディファイルパスを取得する。
     *
     * @return メロディファイルパス
     */
    public String getPlayFile() {
        return this.playFile;
    };

    /**
     * メロディファイルパスを設定する。
     *
     * @param value メロディファイルパス
     */
    public void setPlayFile(String value) {
        this.playFile = value;
    };
}
