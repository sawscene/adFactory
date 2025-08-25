/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.summaryreportplugin.builder;

import java.util.HashMap;
import java.util.Map;

/**
 * HTMLタグの属性名と属性値を保持するマップ
 *
 * @author shizuka.hirano
 * @param <V>
 */
public class HtmlTagAttributeMap<V> {

    /**
     * HTMLタグの属性格納マップ
     */
    private final Map<HtmlTagAttributeEnum, V> attributeMap = new HashMap<>();

    /**
     * HTMLタグの属性を追加します。
     *
     * @param key 属性名
     * @param value 属性値
     */
    public void put(HtmlTagAttributeEnum key, V value) {
        attributeMap.put(key, value);
    }

    /**
     * 文字列表現を取得します。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        attributeMap.keySet().forEach((key) -> {
            sb.append(key).append("\"").append(attributeMap.get(key)).append("\" ");
        });
        return sb.toString();
    }
}
