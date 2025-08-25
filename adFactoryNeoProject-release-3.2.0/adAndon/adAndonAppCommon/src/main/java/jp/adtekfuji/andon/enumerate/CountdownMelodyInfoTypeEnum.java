/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.enumerate;

/**
 * サイクルカウントダウンメロディ種別
 *
 * @author s-maeda
 */
public enum CountdownMelodyInfoTypeEnum {
    PATH_BEFORE_COUNTDOWN, // カウントダウン開始前
    PATH_COUNTDOWN_START, // カウントダウン開始
    PATH_BEFORE_END_OF_COUNTDOWN, // カウントダウン終了前
    TIME_RING_TIMING_END_OF_COUNTDOWN, // カウントダウン終了前鳴動タイミング
    PATH_WORK_DELAYED, // 作業遅延中
    PATH_BREAKTIME_START, // 休憩開始
    PATH_BEFORE_END_OF_BREAKTIME, // 休憩終了前
    TIME_RING_TIMING_END_OF_BREAKTIME; // 休憩終了前鳴動タイミング
}
