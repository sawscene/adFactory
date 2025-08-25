/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.enumerate;

/**
 * アジェンダモニター設定 時間軸設定
 *
 * @author HN)y-harada
 */
public enum TimeAxisEnum {
    
    VerticalAxis("key.VerticalAxis"),  //縦軸
    HorizonAxis("key.HorizonAxis");  //横軸

    private final String name;

    /**
     * コンストラクタ
     *
     * @return
     */
    private TimeAxisEnum(String name) {
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
    public static TimeAxisEnum getDefault() {
        return VerticalAxis;
    }
}
