package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ke.yokoi
 */
public enum AuthorityEnum {

    SYSTEM_ADMIN("key.authoritySystemAdmin"),
    ADMINISTRATOR("key.authorityAdmin"),
    WORKER("key.authorityWoker");

    private final String resourceKey;

    private AuthorityEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public static String getMessage(ResourceBundle rb, AuthorityEnum val) {
        AuthorityEnum[] enumArray = AuthorityEnum.values();
        for (AuthorityEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        AuthorityEnum[] enumArray = AuthorityEnum.values();
        for (AuthorityEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }

    public static AuthorityEnum getEnum(String str) {
        AuthorityEnum[] enumArray = AuthorityEnum.values();
        for (AuthorityEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }

}
