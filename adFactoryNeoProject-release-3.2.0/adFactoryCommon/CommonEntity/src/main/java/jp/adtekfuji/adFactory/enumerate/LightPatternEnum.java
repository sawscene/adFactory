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
 *
 * @author e-mori
 */
public enum LightPatternEnum {

    LIGHTING("key.LightPatternLightnig"),
    BLINK("key.LightPatternBlink");

    private final String resourceKey;

    private LightPatternEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public static String getMessage(ResourceBundle rb, AuthorityEnum val) {
        LightPatternEnum[] enumArray = LightPatternEnum.values();
        for (LightPatternEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        LightPatternEnum[] enumArray = LightPatternEnum.values();
        for (LightPatternEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }

    public static LightPatternEnum getEnum(String str) {
        LightPatternEnum[] enumArray = LightPatternEnum.values();
        for (LightPatternEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }

}
