/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.chart;

import adtekfuji.locale.LocaleUtils;

import java.util.ResourceBundle;

/**
 * 統計種類
 *
 * @author s-heya
 */
public enum SummaryTypeEnum {

    PRODUCT("key.kanbanNum"), // カンバン数
    INPROCESS("key.InProcessVolume"), // 仕掛数
    SUSPEND("key.suspend"), // 中断数
    DELAY("key.Delay"), // 遅延数
    AVG_WORK_TIME("key.AverageWorkTime"), // 平均作業時間
    OTHER("key.Other"), // その他
    PRODUCTION_VOLUME("key.ProductionVolume"), // 生産台数
    WORK_TIME_PER_UNIT("key.WorkTimePerUnit"), // 1台あたりの作業時間
    /**
     * 作業時間の標準偏差
     */
    STDDEV_WORK_TIME("key.SDEV");

    private final String resourceKey;

    /**
     * コンストラクタ
     *
     * @param resourceKey リソースキー
     */
    private SummaryTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * リソースキーを取得する。
     *
     * @return リソースキー
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * 表示名を取得する。
     *
     * @param rb
     * @return 表示名
     */
    public String getDisplayName(ResourceBundle rb) {
        if (!rb.containsKey(this.resourceKey)) {
            return this.name();
        }
        return LocaleUtils.getString(this.resourceKey);
    }
}
