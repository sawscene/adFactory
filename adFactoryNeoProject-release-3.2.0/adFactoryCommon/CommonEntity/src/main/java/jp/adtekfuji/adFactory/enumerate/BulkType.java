/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * 製品単位に入力タイプ
 *
 * @author j.gomis
 */
public enum BulkType {
    BULK_TYPE_SEQUENTIAL("key.BulkType_sequential"),
    BULK_TYPE_GROUPING("key.BulkType_grouping");

    private final String resourceKey;

    /**
     * コンストラクタ
     *
     * @param resourceKey リソースキー
     */
    private BulkType(String resourceKey) {
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