/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 送信周期
 *
 * @author kentarou.suzuki
 */
public enum SendCycleEnum {
    /**
     * 日次
     */
    @JsonProperty("日次")
    DAILY("日次"),
    /**
     * 週次
     */
    @JsonProperty("週次")
    WEEKLY("週次"),
    /**
     * 月次
     */
    @JsonProperty("月次")
    MONTHLY("月次");

    /**
     * リソースキー
     */
    private final String resourceKey;

    /**
     * コンストラクタ
     *
     * @param resourceKey リソースキー
     */
    private SendCycleEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * リソースキーを取得する
     *
     * @return リソースキー
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * 文字列と比較する
     *
     * @param value 比較対象の文字列
     * @return 指定された文字列を等しい場合はtrue、それ以外はfalse
     */
    public boolean equals(String value) {
        return this.toString().equals(value);
    }
}
