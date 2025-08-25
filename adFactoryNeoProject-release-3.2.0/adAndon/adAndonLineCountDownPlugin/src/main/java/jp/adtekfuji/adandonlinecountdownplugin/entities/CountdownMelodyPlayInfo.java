package jp.adtekfuji.adandonlinecountdownplugin.entities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * カウントダウンメロディ鳴動情報
 *
 * @author s-maeda
 */
public class CountdownMelodyPlayInfo {

    private String playFile;
    private Boolean repeat;

    /**
     * カウントダウンメロディ鳴動情報
     */
    public CountdownMelodyPlayInfo() {
    }

    /**
     * カウントダウンメロディ鳴動情報
     *
     * @param playFile メロディファイルパス
     * @param repeat 繰り返し有無
     */
    public CountdownMelodyPlayInfo(String playFile, Boolean repeat) {
        this.playFile = playFile;
        this.repeat = repeat;
    }

    /**
     * メロディファイルパスを取得する。
     *
     * @return メロディファイルパス
     */
    public String getPlayFile() {
        return this.playFile;
    }

    /**
     * メロディファイルパスを設定する。
     *
     * @param value メロディファイルパス
     */
    public void setPlayFile(String value) {
        this.playFile = value;
    }

    /**
     * 繰り返し有無を取得する
     *
     * @return
     */
    public Boolean isRepeat() {
        return repeat;
    }

    /**
     * 繰り返し有無を設定する
     *
     * @param repeat 繰り返し有無
     */
    public void setRepeat(Boolean repeat) {
        this.repeat = repeat;
    }

}
