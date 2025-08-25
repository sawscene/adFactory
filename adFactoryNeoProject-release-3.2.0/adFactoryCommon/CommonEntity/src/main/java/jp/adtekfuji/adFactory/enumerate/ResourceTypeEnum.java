/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * リソース種別
 *
 * @author HN)y-harada
 */
public enum ResourceTypeEnum {

    LOCALE,     // 言語ファイル
    IMAGE,      // 画像ファイル
    SUMMARY_REPORT_CONFIG; // 設定ファイル

    /**
     * 文字列からEnum取得
     *
     * @param str 文字列
     * @return ResourceTypeEnum
     */
    public static ResourceTypeEnum getEnum(String str) {
        ResourceTypeEnum[] enumArray = ResourceTypeEnum.values();
        for (ResourceTypeEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }
}
