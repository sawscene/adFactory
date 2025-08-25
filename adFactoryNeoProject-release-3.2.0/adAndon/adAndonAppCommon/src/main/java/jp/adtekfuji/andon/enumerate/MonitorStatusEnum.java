/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.enumerate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import adtekfuji.locale.LocaleUtils;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 簡易進捗モニターステータス定数
 *
 * @author e.mori
 * @version 1.6.2
 * @since 2017.02.08.Wen
 */
public enum MonitorStatusEnum {

    READY("keyMonitorStatusReady"),
    WORKING("key.MonitorStatusWorking"),
    SUSPEND("key.MonitorStatusSuspend"),
    CALL(""),
    BREAK_TIME("");

    private final String resourceKey;

    private MonitorStatusEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    /**
     * 指定した文字列のEnumを取得
     *
     * @param str Enum名
     * @return Enum
     */
    public static MonitorStatusEnum getEnum(String str) {
        MonitorStatusEnum[] enumArray = MonitorStatusEnum.values();
        for (MonitorStatusEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }

    /**
     * 指定したモニターの表示文字を取得
     *
     * @param rb ロケールのリソースデータ
     * @param val モニターのEnum
     * @return 指定したモニターの表示文字
     */
    public static String getMessage(ResourceBundle rb, MonitorStatusEnum val) {
        MonitorStatusEnum[] enumArray = MonitorStatusEnum.values();
        for (MonitorStatusEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    /**
     * モニターの表示文字を全て取得
     *
     * @param rb ロケールのリソースデータ
     * @return モニターの全ての表示文字
     */
    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        MonitorStatusEnum[] enumArray = MonitorStatusEnum.values();
        for (MonitorStatusEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }

    /**
     * カンバンステータスからモニター用ステータスに変換する
     *
     * @param kanbanStatus カンバンステータス
     * @return
     */
    public static MonitorStatusEnum valueOf(KanbanStatusEnum kanbanStatus) {
        MonitorStatusEnum monitorStatus = null;

        if (Objects.isNull(kanbanStatus)) {
            return monitorStatus;
        }

        switch (kanbanStatus) {
            case WORKING:
                monitorStatus = MonitorStatusEnum.WORKING;
                break;
            case INTERRUPT:
            case SUSPEND:
                monitorStatus = MonitorStatusEnum.SUSPEND;
                break;
            case PLANNING:
            case PLANNED:
            case COMPLETION:
            case OTHER:
            default:
                monitorStatus = MonitorStatusEnum.READY;
                break;
        }

        return monitorStatus;
    }

    public static void sort(List<MonitorStatusEnum> enums){
        enums.sort((MonitorStatusEnum o1, MonitorStatusEnum o2) -> {
            if(o1.equals(o2)){
                return 0;
            }
            MonitorStatusEnum ret = comparator(o1, o2);
            if(ret.equals(o1)){
                return -1;
            }else{
                return 1;
            }
        });
    }

    /**
     * 並び替え処理(状態遷移は進捗モニター簡易表示対応.vsdを参照)
     *
     * @param now
     * @param next
     * @return
     */
    public static MonitorStatusEnum comparator(MonitorStatusEnum now, MonitorStatusEnum next) {
        if(Objects.isNull(now) || Objects.isNull(next)){
            return READY;
        }
        MonitorStatusEnum ret = now;
        switch (next) {
            case READY:
                break;
            case WORKING:
                if (now.equals(READY)) {
                    ret = next;
                }
                break;
            case SUSPEND:
                if (now.equals(READY) || now.equals(WORKING)) {
                    ret = next;
                }
                break;
            case BREAK_TIME:
                if (now.equals(READY) || now.equals(WORKING) || now.equals(SUSPEND)) {
                    ret = next;
                }
                break;
            case CALL:
                if (now.equals(READY) || now.equals(WORKING) || now.equals(SUSPEND) || now.equals(BREAK_TIME)) {
                    ret = next;
                }
                break;
        }
        return ret;
    }
}
