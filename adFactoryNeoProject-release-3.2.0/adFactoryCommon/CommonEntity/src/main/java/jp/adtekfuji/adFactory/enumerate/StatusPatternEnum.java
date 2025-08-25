/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 *
 * @author ke.yokoi
 */
public enum StatusPatternEnum {

    PLAN_NORMAL("key.StatusPatternPlanNormal"),
    PLAN_DELAYSTART("key.StatusPatternPlanDelayStart"),
    WORK_NORMAL("key.StatusPatternWorkNormal"),
    WORK_DELAYSTART("key.StatusPatternWorkDelayStart"),
    WORK_DELAYCOMP("key.StatusPatternWorkDerayComp"),
    SUSPEND_NORMAL("key.StatusPatternSuspendNormal"),
    INTERRUPT_NORMAL("key.StatusPatternInterruptNormal"),
    COMP_NORMAL("key.StatusPatternCompNormal"),
    COMP_DELAYCOMP("key.StatusPatternCompDelayComp"),
    BREAK_TIME("key.StatusPatternBreakTime"),
    CALLING("key.StatusPatternCalling"),
    DEFECT("key.KanbanStatusDefect");

    private final String resourceKey;

    private StatusPatternEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public static String getValueText(int idx) {
        String value = "";

        // 列挙型を中身の並び順に取得する
        StatusPatternEnum[] enumArray = StatusPatternEnum.values();
        // 引数の数値が並び順のMAX数より大きいか判定
        if (enumArray.length > idx) {
            value = StatusPatternEnum.values()[idx].toString();
        }

        return value;
    }

    public static StatusPatternEnum getEnum(String str) {
        StatusPatternEnum[] enumArray = StatusPatternEnum.values();
        for (StatusPatternEnum enumStr : enumArray) {
            if (str.equals(enumStr.toString())) {
                return enumStr;
            }
        }
        return null;
    }

    public static String getMessage(ResourceBundle rb, StatusPatternEnum val) {
        StatusPatternEnum[] enumArray = StatusPatternEnum.values();
        for (StatusPatternEnum enumStr : enumArray) {
            if (enumStr.equals(val)) {
                return LocaleUtils.getString(enumStr.resourceKey);
            }
        }
        return "";
    }

    public static List<String> getMessages(ResourceBundle rb) {
        List<String> messages = new ArrayList<>();
        StatusPatternEnum[] enumArray = StatusPatternEnum.values();
        for (StatusPatternEnum enumStr : enumArray) {
            messages.add(LocaleUtils.getString(enumStr.resourceKey));
        }
        return messages;
    }

    /**
     * ステータスパターン取得.
     *
     * @param status
     * @param planStart
     * @param planEnd
     * @param actualStart
     * @param actualEnd
     * @param now
     * @return
     */
    public static StatusPatternEnum getStatusPattern(KanbanStatusEnum status, Date planStart, Date planEnd, Date actualStart, Date actualEnd, Date now) {
        switch (status) {
            case PLANNING:
            case PLANNED:
                if (Objects.nonNull(planStart) && planStart.before(now)) {
                    return StatusPatternEnum.PLAN_DELAYSTART;
                }
                break;
            case WORKING:
                if (Objects.nonNull(planEnd) && planEnd.before(now)) {
                    return StatusPatternEnum.WORK_DELAYCOMP;
                } else if (Objects.nonNull(planStart) && Objects.nonNull(actualStart) && planStart.before(actualStart)) {
                    return StatusPatternEnum.WORK_DELAYSTART;
                } else {
                    return StatusPatternEnum.WORK_NORMAL;
                }
            case SUSPEND:
                return StatusPatternEnum.SUSPEND_NORMAL;
            case INTERRUPT:
                return StatusPatternEnum.INTERRUPT_NORMAL;
            case COMPLETION:
                if (Objects.nonNull(planEnd) && Objects.nonNull(actualEnd) && actualEnd.after(planEnd)) {
                    return StatusPatternEnum.COMP_DELAYCOMP;
                } else {
                    return StatusPatternEnum.COMP_NORMAL;
                }
            default:
                break;
        }
        return StatusPatternEnum.PLAN_NORMAL;
    }

    /**
     * ステータスパターン取得.
     *
     * @param kanbanStatus
     * @param workKanbanStatus
     * @param planStart
     * @param planEnd
     * @param actualStart
     * @param actualEnd
     * @param now
     * @return
     */
    public static StatusPatternEnum getStatusPattern(KanbanStatusEnum kanbanStatus, KanbanStatusEnum workKanbanStatus, Date planStart, Date planEnd, Date actualStart, Date actualEnd, Date now) {
        if(kanbanStatus == KanbanStatusEnum.INTERRUPT || kanbanStatus == KanbanStatusEnum.SUSPEND || kanbanStatus == KanbanStatusEnum.COMPLETION) {
            workKanbanStatus = kanbanStatus;
        }
        return getStatusPattern(workKanbanStatus, planStart, planEnd, actualStart, actualEnd, now);
    }

    /**
     * 優先度が高いステータスパターンの判定.
     *
     * @param nowStatus
     * @param status
     * @return
     */
    public static StatusPatternEnum compareStatus(StatusPatternEnum nowStatus, StatusPatternEnum status) {
        if (nowStatus == StatusPatternEnum.CALLING || status == StatusPatternEnum.CALLING) {
            return StatusPatternEnum.CALLING;
        }
        if (nowStatus == StatusPatternEnum.BREAK_TIME || status == StatusPatternEnum.BREAK_TIME) {
            return StatusPatternEnum.BREAK_TIME;
        }

        StatusPatternEnum ret = nowStatus;
        switch (nowStatus) {
            case PLAN_NORMAL:
                if (status == StatusPatternEnum.PLAN_DELAYSTART || status == StatusPatternEnum.WORK_NORMAL
                        || status == StatusPatternEnum.WORK_DELAYSTART || status == StatusPatternEnum.WORK_DELAYCOMP
                        || status == StatusPatternEnum.SUSPEND_NORMAL || status == StatusPatternEnum.INTERRUPT_NORMAL) {
                    ret = status;
                }
                break;
            case PLAN_DELAYSTART:
                if (status == StatusPatternEnum.WORK_DELAYSTART || status == StatusPatternEnum.WORK_DELAYCOMP
                        || status == StatusPatternEnum.SUSPEND_NORMAL || status == StatusPatternEnum.INTERRUPT_NORMAL) {
                    ret = status;
                }
                break;
            case WORK_NORMAL:
                if (status == StatusPatternEnum.WORK_DELAYSTART || status == StatusPatternEnum.WORK_DELAYCOMP
                        || status == StatusPatternEnum.SUSPEND_NORMAL || status == StatusPatternEnum.INTERRUPT_NORMAL
                        || status == StatusPatternEnum.PLAN_DELAYSTART) {
                    ret = status;
                }
                break;
            case WORK_DELAYSTART:
                if (status == StatusPatternEnum.PLAN_DELAYSTART || status == StatusPatternEnum.WORK_DELAYCOMP
                        || status == StatusPatternEnum.SUSPEND_NORMAL || status == StatusPatternEnum.INTERRUPT_NORMAL) {
                    ret = status;
                }
                break;
            case WORK_DELAYCOMP:
                if (status == StatusPatternEnum.PLAN_DELAYSTART
                        || status == StatusPatternEnum.SUSPEND_NORMAL || status == StatusPatternEnum.INTERRUPT_NORMAL) {
                    ret = status;
                }
                break;
            case SUSPEND_NORMAL:
                break;
            case INTERRUPT_NORMAL:
                // if (status == StatusPatternEnum.SUSPEND_NORMAL) {
                //    ret = status;
                // }
                break;
            case COMP_NORMAL:
                ret = status;
                break;
            case COMP_DELAYCOMP:
                if (status == StatusPatternEnum.PLAN_DELAYSTART
                        || status == StatusPatternEnum.WORK_DELAYSTART || status == StatusPatternEnum.WORK_DELAYCOMP
                        || status == StatusPatternEnum.SUSPEND_NORMAL || status == StatusPatternEnum.INTERRUPT_NORMAL
                        || status == StatusPatternEnum.PLAN_NORMAL || status == StatusPatternEnum.WORK_NORMAL
                        || status == StatusPatternEnum.COMP_NORMAL) {
                    ret = status;
                }
                break;
            default:
                break;
        }
        return ret;
    }

    public static KanbanStatusEnum toKanbanStatus(StatusPatternEnum statusPattern) {
        switch(statusPattern) {
            case DEFECT:
                return KanbanStatusEnum.DEFECT;
            case COMP_NORMAL:
            case COMP_DELAYCOMP:
                return KanbanStatusEnum.COMPLETION;
            case INTERRUPT_NORMAL:
                return KanbanStatusEnum.INTERRUPT;
            case WORK_DELAYSTART:
            case WORK_NORMAL:
            case WORK_DELAYCOMP:
                return KanbanStatusEnum.WORKING;
            case PLAN_NORMAL:
            case PLAN_DELAYSTART:
                return KanbanStatusEnum.PLANNED;
            case SUSPEND_NORMAL:
                return KanbanStatusEnum.SUSPEND;
            case CALLING :
            case BREAK_TIME:
            default:
                return KanbanStatusEnum.OTHER;
        }
    }

    /**
     * StatusPatternEnum を返す。
     * 
     * @param kanbanStatus カンバンステータス
     * @return StatusPatternEnum
     */
    public static StatusPatternEnum toStatusPattern(KanbanStatusEnum kanbanStatus) {
        if (Objects.nonNull(kanbanStatus)) {
            switch (kanbanStatus) {
                case PLANNING:
                case PLANNED:
                    return PLAN_NORMAL;
                case WORKING:
                    return WORK_NORMAL;
                case SUSPEND:
                    return SUSPEND_NORMAL;
                case INTERRUPT:
                    return INTERRUPT_NORMAL;
                case COMPLETION:
                    return COMP_NORMAL;
                case OTHER:
                    return WORK_NORMAL;
                case DEFECT:
                    return SUSPEND_NORMAL;
                default:
                    break;
            }
        }
        return null;
    }

}
