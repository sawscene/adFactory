/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.enumerate;

/**
 * 予実モニター　表示対象タイプ
 *
 * @author fu-kato
 */
public enum DisplayModeEnum {
    /** ライン */
    LINE("key.Line"),
    /** カンバン */
    KANBAN("key.Kanban"),
    /** 作業者 */
    WORKER("key.WorkingParson"),
    /** 製品進捗 */
    PRODUCT_PROGRESS("key.ProductProgress"),
    /** 払出状況 */
    PAYOUT_STATUS("key.PayoutStatus");

    private final String name;

    private DisplayModeEnum (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }
}
