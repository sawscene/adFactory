/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * キーボードタイプ列挙型
 * 
 * @author s-heya
 */
public enum DatetimeOptionType {
    DATETIME("key.DatetimeInputOption"),
    DATE("key.DateInputOption"),
    TIME("key.TimeInputOption");

    private final String resourceKey;

    /**
     * コンストラクタ
     * 
     * @param resourceKey リソースキー
     */
    private DatetimeOptionType(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * リソースキーを取得する。
     * 
     * @return リソースキー
     */
    public String getResourceKey() {
        return resourceKey;
    }
}
