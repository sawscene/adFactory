/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * アラインメントタイプ
 * 
 * @author s-ohtani
 */
public enum AlignmentType {
    ALIGNMENT_LEFT("key.AlignmentLeft"),
    ALIGNMENT_CENTER("key.AlignmentCenter"),
    ALIGNMENT_RIGHT("key.AlignmentRight");

    private final String resourceKey;

    /**
     * コンストラクタ
     * 
     * @param resourceKey リソースキー
     */
    private AlignmentType(String resourceKey) {
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
