/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.enumerate;

/**
 * アジェンダモニター設定 時間軸スケール設定
 *
 * @author HN)y-harada
 */
public enum TimeScaleEnum {
    
    Time("key.Time"),        // 時間
    HalfDay("key.HalfDay"),  // 半日
    Day("key.1Day"),         // 1日
    Week("key.1Week"),       // 1週間
    Month("key.1Month");     // 1ヵ月
    
    private final String name;

    /**
     * コンストラクタ
     *
     * @return
     */
    private TimeScaleEnum(String name) {
        this.name = name;
    }
    
    /**
     * 名前を取得する。
     *
     * @return 名前
     */
    public String getName() {
        return name;
    }

    /**
     * 設定のデフォルト値を取得する。
     *
     * @return デフォルト値
     */
    public static TimeScaleEnum getDefault() {
        return Time;
    }
}
