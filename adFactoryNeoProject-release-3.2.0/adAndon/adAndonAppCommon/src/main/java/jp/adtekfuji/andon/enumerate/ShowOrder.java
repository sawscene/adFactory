/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.enumerate;

/**
 * アジェンダモニター設定カンバン表示順設定
 *
 * @author fu-kato
 */
public enum ShowOrder {
    PlanTimeOrder("key.PlanTimeOrder"),
    SelectOrder("key.SelectOrder");

    private final String name;

    private ShowOrder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * 設定のデフォルト値を取得する。
     *
     * @return
     */
    public static ShowOrder getDefault() {
        return PlanTimeOrder;
    }
}
