/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;

import java.util.ResourceBundle;

/**
 * ロケール種別
 *
 * @author HN)y-harada
 */
public enum LocaleTypeEnum {

    ADMANAGER("key.AdministratorTerminal", "adManager", 0),   // 管理者端末
    ADPRODUCT("key.WorkerTerminal", "adProduct", 1),          // 作業者端末
    ADMONITOR("key.AdministratorTerminal","adManager",2),    // モニタ端末
    CUSTUM("key.custom","custom", 3);                      // カスタム




    private final String localeFileType;
    private final long priority;
    private final String resourceKey;

    /**
     * コンストラクタ
     *
     * @param resourceKey リソースキー
     */
    private LocaleTypeEnum(String resourceKey, String localeFileType, long priority) {
        this.resourceKey = resourceKey;
        this.localeFileType = localeFileType;
        this.priority = priority;
    }

    /**
     * リソースキーを取得する
     *
     * @return リソースキー
     */
    public String getResourceKey() {
        return resourceKey;
    }

    /**
     * ロケールファイルタイムを取得
     * @return ロケールファイルタイプ
     */
    public String getLocaleFileType() {
        return localeFileType;
    }

    /**
     * 文字列からロケール種別を取得する
     *
     * @param str 文字列
     * @return ロケール種別
     */
    public static LocaleTypeEnum getEnum(String str) {
        LocaleTypeEnum[] enumArray = LocaleTypeEnum.values();
        for (LocaleTypeEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }

    /**
     * ロケール種別から文字列を取得する
     *
     * @param rb リソースバンドル
     * @param val ロケール種別
     * @return 文字列
     */
    public static String getMessage(ResourceBundle rb, LocaleTypeEnum val) {
        LocaleTypeEnum[] enumArray = LocaleTypeEnum.values();
        for (LocaleTypeEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    public long getPriority() {
        return priority;
    }
}
