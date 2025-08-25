/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * コンテンツ種別
 *
 * @author ke.yokoi
 */
public enum ContentTypeEnum {

    STRING("key.ContentTypeString"),
    HTML("key.ContentTypeHTML"),
    PDF("key.ContentTypePDF");

    private final String resourceKey;

    private ContentTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public static String getValueText(int idx) {
        String value = "";
        // 列挙型を中身の並び順に取得する
        ContentTypeEnum[] enumArray = ContentTypeEnum.values();
        // 引数の数値が並び順のMAX数より大きいか判定
        if (enumArray.length > idx) {
            value = ContentTypeEnum.values()[idx].toString();
        }
        return value;
    }

    public static ContentTypeEnum getEnum(String str) {
        ContentTypeEnum[] enumArray = ContentTypeEnum.values();
        for (ContentTypeEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }

    public static String getMessage(ResourceBundle rb, ContentTypeEnum val) {
        ContentTypeEnum[] enumArray = ContentTypeEnum.values();
        for (ContentTypeEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        ContentTypeEnum[] enumArray = ContentTypeEnum.values();
        for (ContentTypeEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }
}
