/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.monitor;

/**
 * サイクルメロディ種別
 *
 * @author nar-nakamura
 */
public enum CycleMelodyTypeEnum {
    WORK_START_30SEC,   // 作業開始時刻 30秒前
    CYCLE_START,        // サイクル 開始
    CYCLE_60SEC,        // サイクル 終了1分前
    CYCLE_30SEC,        // サイクル 終了30秒前
    CYCLE_5SEC,         // サイクル 終了5秒前
    LUNCH_TIME_START,   // 昼休憩 開始
    LUNCH_TIME_30SEC,   // 昼休憩 終了30秒前
    REFRESH_TIME_START, // リフレッシュタイム 開始
    REFRESH_TIME_30SEC, // リフレッシュタイム 終了30秒前
    WORK_END; // 作業終了
}
