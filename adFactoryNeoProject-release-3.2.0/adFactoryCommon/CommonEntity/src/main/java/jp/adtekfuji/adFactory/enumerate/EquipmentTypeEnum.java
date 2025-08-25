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
 * 設備種別
 *
 * @author ke.yokoi
 */
public enum EquipmentTypeEnum {

    TERMINAL("key.equipmentTerminalType"),          // 作業者端末
    MONITOR("key.equipmentMonitorType"),            // モニター端末
    MANUFACTURE("key.equipmentManufactureType"),    // 製造設備
    MEASURE("key.equipmentMeasureType"),            // 測定機器
    LITE("liteTerminal"),
    REPORTER("reporterTerminal");

    private final String resourceKey;

    private EquipmentTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public static String getValueText(int idx) {
        String value = "";
        // 列挙型を中身の並び順に取得する
        EquipmentTypeEnum[] enumArray = EquipmentTypeEnum.values();
        // 引数の数値が並び順のMAX数より大きいか判定
        if (enumArray.length > idx) {
            value = EquipmentTypeEnum.values()[idx].toString();
        }
        return value;
    }

    public static EquipmentTypeEnum getEnum(String str) {
        EquipmentTypeEnum[] enumArray = EquipmentTypeEnum.values();
        for (EquipmentTypeEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }

    public static String getMessage(ResourceBundle rb, EquipmentTypeEnum val) {
        EquipmentTypeEnum[] enumArray = EquipmentTypeEnum.values();
        for (EquipmentTypeEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        EquipmentTypeEnum[] enumArray = EquipmentTypeEnum.values();
        for (EquipmentTypeEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }
}
