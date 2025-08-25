/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adappentity.enumerate;

import adtekfuji.locale.LocaleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * カンバンステータス
 *
 * @author s-heya
 */
public enum KanbanStatusEnum {

    PLANNING("key.KanbanStatusPlanning"),
    PLANNED("key.KanbanStatusPlanned"),
    WORKING("key.KanbanStatusWorking"),
    SUSPEND("key.KanbanStatusSuspend"),
    INTERRUPT("key.KanbanStatusInterrupt"),
    COMPLETION("key.KanbanStatusCompletion"),
    OTHER("key.KanbanStatusOther"),
    NONE("key.KanbanStatusNone");

    private final String resourceKey;

    /**
     * コンストラクタ
     *
     * @param resourceKey
     */
    private KanbanStatusEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return this.resourceKey;
    }

    public static String getValueText(int index) {
        String value = "";
        KanbanStatusEnum[] enumArray = KanbanStatusEnum.values();
        if (enumArray.length > index) {
            value = KanbanStatusEnum.values()[index].toString();
        }
        return value;
    }

    public static KanbanStatusEnum getEnum(String string) {
        KanbanStatusEnum[] enumArray = KanbanStatusEnum.values();
        for (KanbanStatusEnum enumStr : enumArray) {
            if (string.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }

    public static String getMessage(ResourceBundle rb, KanbanStatusEnum value) {
        KanbanStatusEnum[] enumArray = KanbanStatusEnum.values();
        for (KanbanStatusEnum enumStr : enumArray) {
            if (enumStr.equals(value)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        KanbanStatusEnum[] enumArray = KanbanStatusEnum.values();
        for (KanbanStatusEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }
}
