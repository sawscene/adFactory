/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;

import java.util.ResourceBundle;

/**
 * プロパティ種類
 *
 * @author s-heya
 */
public enum PropertyEnum {

    WORK_PROGRESS("key.WorkProgress");

    private final String resourceKey;

    /**
     * コンストラクタ
     *
     * @param resourceKey
     */
    private PropertyEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * 表示名を取得する。
     *
     * @param rb
     * @return
     */
    public String getDisplayName(ResourceBundle rb) {
        return LocaleUtils.getString(this.resourceKey);
    }

    /**
     * 名前からオブジェクトに変換する
     *
     * @param name
     * @return
     */
    public static PropertyEnum toEnum(String name) {
        PropertyEnum[] array = PropertyEnum.values();
        for (PropertyEnum property : array) {
            if (property.name().equals(name)) {
                return property;
            }
        }
        return null;
    }

}
