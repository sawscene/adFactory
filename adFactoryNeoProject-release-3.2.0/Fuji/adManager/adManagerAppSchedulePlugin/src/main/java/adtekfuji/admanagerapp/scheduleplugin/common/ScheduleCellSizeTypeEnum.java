/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin.common;

import adtekfuji.locale.LocaleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 表示する予定のセルサイズEnum
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.24.Mon
 */
public enum ScheduleCellSizeTypeEnum {

    DAILY("key.Day"),
    WEEKLY("key.Week"),
    MONTHLY("key.Month");

    private final String resourceKey;

    private ScheduleCellSizeTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public static String getValueText(int idx) {
        String value = "";

        // 列挙型を中身の並び順に取得する
        ScheduleCellSizeTypeEnum[] enumArray = ScheduleCellSizeTypeEnum.values();
        // 引数の数値が並び順のMAX数より大きいか判定
        if (enumArray.length > idx) {
            value = ScheduleCellSizeTypeEnum.values()[idx].toString();
        }

        return value;
    }

    public static ScheduleCellSizeTypeEnum getEnum(String str) {
        ScheduleCellSizeTypeEnum[] enumArray = ScheduleCellSizeTypeEnum.values();
        for (ScheduleCellSizeTypeEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }

    public static String getMessage(ResourceBundle rb, ScheduleCellSizeTypeEnum val) {
        ScheduleCellSizeTypeEnum[] enumArray = ScheduleCellSizeTypeEnum.values();
        for (ScheduleCellSizeTypeEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        ScheduleCellSizeTypeEnum[] enumArray = ScheduleCellSizeTypeEnum.values();
        for (ScheduleCellSizeTypeEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }
}
