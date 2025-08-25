/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.enumerate;

/**
 * アジェンダモニター設定 予実表示設定
 *
 * @author HN)y-harada
 */
public enum PlanActualShowTypeEnum {
    
    PlanAndActual("key.PlanAndActual"),  // 予定と実績
    PlanOnly("key.PlanOnly"),            // 予定のみ
    ActualOnly("key.ActualOnly");        // 実績のみ
    
    private final String name;

    /**
     * コンストラクタ
     *
     * @return
     */
    private PlanActualShowTypeEnum(String name) {
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
    public static PlanActualShowTypeEnum getDefault() {
        return PlanAndActual;
    }
}
