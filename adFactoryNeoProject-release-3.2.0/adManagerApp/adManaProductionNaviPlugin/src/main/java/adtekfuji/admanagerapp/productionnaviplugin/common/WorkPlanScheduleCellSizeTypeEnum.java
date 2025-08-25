/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 表示する予定のセルサイズEnum
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
public enum WorkPlanScheduleCellSizeTypeEnum {

    DAILY("key.Day"),
    WEEKLY("key.Week"),
    MONTHLY("key.Month");

    private final String resourceKey;

    private WorkPlanScheduleCellSizeTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public static String getValueText(int idx) {
        String value = "";

        // 列挙型を中身の並び順に取得する
        WorkPlanScheduleCellSizeTypeEnum[] enumArray = WorkPlanScheduleCellSizeTypeEnum.values();
        // 引数の数値が並び順のMAX数より大きいか判定
        if (enumArray.length > idx) {
            value = WorkPlanScheduleCellSizeTypeEnum.values()[idx].toString();
        }

        return value;
    }

    public static WorkPlanScheduleCellSizeTypeEnum getEnum(String str) {
        WorkPlanScheduleCellSizeTypeEnum[] enumArray = WorkPlanScheduleCellSizeTypeEnum.values();
        for (WorkPlanScheduleCellSizeTypeEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }

    public static String getMessage(ResourceBundle rb, WorkPlanScheduleCellSizeTypeEnum val) {
        WorkPlanScheduleCellSizeTypeEnum[] enumArray = WorkPlanScheduleCellSizeTypeEnum.values();
        for (WorkPlanScheduleCellSizeTypeEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return rb.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        WorkPlanScheduleCellSizeTypeEnum[] enumArray = WorkPlanScheduleCellSizeTypeEnum.values();
        for (WorkPlanScheduleCellSizeTypeEnum enumStr : enumArray) {
            messages.add(rb.getString(enumStr.resourceKey));
        }
        return messages;
    }
}
