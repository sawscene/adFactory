/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adandonlinecountdownplugin.enumerate;

/**
 * 休憩状態の列挙子
 *
 * @author s-heya
 */
public enum BreakStatus {
    NOT,
    BREAK,          // 休憩中
    SOON_OVER,      // 休憩終了前
    END;            // 休憩終了
}
