/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.enumerate;

import adtekfuji.locale.LocaleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * モニター種別
 *
 * @author ke.yokoi
 */
public enum AndonMonitorTypeEnum {

    LINE_PRODUCT("key.andonMonitorLineType"),       // ラインモニター
    AGENDA("key.agendaMonitorLineType"),            // アジェンダモニター
    LITE_MONITOR("key.LiteMonitorType");            // Liteモニター

    private final String resourceKey;

    private AndonMonitorTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public static String getValueText(int idx) {
        String value = "";
        // 列挙型を中身の並び順に取得する
        AndonMonitorTypeEnum[] enumArray = AndonMonitorTypeEnum.values();
        // 引数の数値が並び順のMAX数より大きいか判定
        if (enumArray.length > idx) {
            value = AndonMonitorTypeEnum.values()[idx].toString();
        }
        return value;
    }

    public static AndonMonitorTypeEnum getEnum(String str) {
        AndonMonitorTypeEnum[] enumArray = AndonMonitorTypeEnum.values();
        for (AndonMonitorTypeEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }

    public static String getMessage(ResourceBundle rb, AndonMonitorTypeEnum val) {
        AndonMonitorTypeEnum[] enumArray = AndonMonitorTypeEnum.values();
        for (AndonMonitorTypeEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        AndonMonitorTypeEnum[] enumArray = AndonMonitorTypeEnum.values();
        for (AndonMonitorTypeEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }

}
