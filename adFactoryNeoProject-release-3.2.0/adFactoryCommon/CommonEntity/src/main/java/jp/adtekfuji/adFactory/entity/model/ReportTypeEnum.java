/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * レポート種別
 *
 * @author kentarou.suzuki
 */
public enum ReportTypeEnum {
    /**
     * 経営レポート
     */
    @JsonProperty("経営")
    MANAGEMENT("経営"),
    /**
     * 生産レポート
     */
    @JsonProperty("生産")
    PRODUCTION("生産"),
    /**
     * 品質レポート
     */
    @JsonProperty("品質")
    QUALITY("品質");

    /**
     * リソースキー
     */
    private final String resourceKey;

    /**
     * コンストラクタ
     *
     * @param resourceKey リソースキー
     */
    private ReportTypeEnum(String resourceKey) {
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
