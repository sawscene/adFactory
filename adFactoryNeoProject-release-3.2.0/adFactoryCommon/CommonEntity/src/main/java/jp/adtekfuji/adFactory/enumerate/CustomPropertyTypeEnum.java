/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * プロパティ型 列挙型
 *
 * @author e.mori
 */
public enum CustomPropertyTypeEnum {

    TYPE_STRING("key.propertyStringType"),
    TYPE_BOOLEAN("key.propertyBooleanType"),
    TYPE_INTEGER("key.propertyIntegerType"),
    TYPE_NUMERIC("key.propertyNumericType"),
    TYPE_DATE("key.propertyDateType"),
    TYPE_IP4_ADDRESS("key.propertyIp4AddressType"),
    TYPE_MAC_ADDRESS("key.propertyMacAddressType"),
    TYPE_PLUGIN("key.PluginName"),
    TYPE_TRACE("key.TraceInfo"),
    TYPE_DEFECT("key.DefectInfo");

    private final String resourceKey;

    private CustomPropertyTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public static String getMessage(ResourceBundle rb, CustomPropertyTypeEnum val) {
        CustomPropertyTypeEnum[] enumArray = CustomPropertyTypeEnum.values();
        for (CustomPropertyTypeEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        CustomPropertyTypeEnum[] enumArray = CustomPropertyTypeEnum.values();
        for (CustomPropertyTypeEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }

    public String getResourceKey() {
        return resourceKey;
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
    public static CustomPropertyTypeEnum toEnum(String name) {
        return Arrays.stream(CustomPropertyTypeEnum.values())
                .filter(property->property.name().equals(name))
                .findFirst()
                .orElse(null);
    }
}
