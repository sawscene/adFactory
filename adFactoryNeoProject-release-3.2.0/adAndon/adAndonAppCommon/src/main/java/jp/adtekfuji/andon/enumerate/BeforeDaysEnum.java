/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.enumerate;

import adtekfuji.locale.LocaleUtils;
import java.util.Objects;

/**
 * アジェンダモニター設定 表示開始日設定
 *
 * @author s-morita
 */
public enum BeforeDaysEnum {

    TargetDay(0),  // 対象日
    DayOne(1),  // 1日前
    DayTwo(2),  // 2日前
    DayThree(3),  // 3日前
    DayFour(4),  // 4日前
    DayFive(5),  // 5日前
    DaySix(6),  // 6日前
    DaySeven(7);  // 7日前

    private final int value;

    /**
     * コンストラクタ
     *
     * @return
     */
    private BeforeDaysEnum(int value) {
        this.value = value;
    }
    
    /**
     * 値を取得する。
     *
     * @return 名前
     */
    public int getValue() {
        return value;
    }

    /**
     * 設定のデフォルト値を取得する。
     *
     * @return デフォルト値
     */
    public static BeforeDaysEnum getDefault() {
        return TargetDay;
    }

    /**
     * 値を変換する。
     * 
     * @param object 表示開始日
     * @return 表示開始日
     */
    public static String toString(BeforeDaysEnum object) {
        if (Objects.equals(0, object.getValue())) {
            return LocaleUtils.getString("key.TargetDay");
        } else {
            return new StringBuilder()
                    .append(object.getValue())
                    .append(LocaleUtils.getString("key.CalibWarningDaysUnit"))
                    .toString();
        }
    }
}
