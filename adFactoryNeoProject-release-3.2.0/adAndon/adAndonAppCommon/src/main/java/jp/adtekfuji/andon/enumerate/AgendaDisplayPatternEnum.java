/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.enumerate;

/**
 * 予実表示パータン用Enum
 */
public enum AgendaDisplayPatternEnum {

    CompactDisplay("key.AgendaCompactDisplay"),     // まとめて表示
    DisplayPerKanban("key.AgendaDisplayPerKanban");  // カンバン毎に表示

    private final String name;

    /**
     * コンストラクタ
     *
     * @return
     */
    private AgendaDisplayPatternEnum(String name) {
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
    public static AgendaDisplayPatternEnum getDefault() {
        return CompactDisplay;
    }
}
