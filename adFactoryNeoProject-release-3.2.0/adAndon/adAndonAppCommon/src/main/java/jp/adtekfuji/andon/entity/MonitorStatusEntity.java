/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.andon.enumerate.MonitorStatusEnum;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;

/**
 * 簡易進捗モニターステータス
 *
 * @author e.mori
 * @version 1.6.2
 * @since 2017.02.08.Wen
 */
public class MonitorStatusEntity {

    private Integer order;
    private String name;
    private String frontColor;
    private String backColor;
    private String melodyFilePath;
    private boolean melodyReplay;
    private boolean isCall = false;
    private MonitorStatusEnum monitorStatus;
    private Map<Long, MonitorStatusEnum> equipmentStatus;
    private LightPatternEnum lightPattern;

    public MonitorStatusEntity() {
        this.order = 0;
        this.name = "";
        this.frontColor = "#FF0000";
        this.backColor = "#FF0000";
        this.melodyFilePath = "";
        this.melodyReplay = false;
        this.monitorStatus = MonitorStatusEnum.READY;
        this.equipmentStatus = new HashMap<>();
        this.lightPattern = LightPatternEnum.LIGHTING;
    }

    public MonitorStatusEntity(String name, String frontColor, String backColor, String melodyFilePath, boolean melodyReplay, MonitorStatusEnum monitorStatus) {
        this.name = name;
        this.frontColor = frontColor;
        this.backColor = backColor;
        this.melodyFilePath = melodyFilePath;
        this.melodyReplay = melodyReplay;
        this.monitorStatus = monitorStatus;
    }

    public MonitorStatusEntity(Integer order, String name, String frontColor, String backColor, String melodyFilePath, boolean melodyReplay, MonitorStatusEnum monitorStatus, Map<Long, MonitorStatusEnum> equipmentStatus) {
        this.order = order;
        this.name = name;
        this.frontColor = frontColor;
        this.backColor = backColor;
        this.melodyFilePath = melodyFilePath;
        this.melodyReplay = melodyReplay;
        this.monitorStatus = monitorStatus;
        this.equipmentStatus = equipmentStatus;
    }

    public MonitorStatusEntity(Integer order, String name, String frontColor, String backColor, String melodyFilePath, boolean melodyReplay, MonitorStatusEnum monitorStatus, Map<Long, MonitorStatusEnum> equipmentStatus, LightPatternEnum lightPattern) {
        this.order = order;
        this.name = name;
        this.frontColor = frontColor;
        this.backColor = backColor;
        this.melodyFilePath = melodyFilePath;
        this.melodyReplay = melodyReplay;
        this.monitorStatus = monitorStatus;
        this.equipmentStatus = equipmentStatus;
        this.lightPattern = lightPattern;
    }
    
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrontColor() {
        return frontColor;
    }

    public void setFrontColor(String frontColor) {
        this.frontColor = frontColor;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public String getMelodyFilePath() {
        return melodyFilePath;
    }

    public void setMelodyFilePath(String melodyFilePath) {
        this.melodyFilePath = melodyFilePath;
    }

    public boolean isMelodyReplay() {
        return melodyReplay;
    }

    public void setMelodyReplay(boolean melodyReplay) {
        this.melodyReplay = melodyReplay;
    }

    public boolean isIsCall() {
        return isCall;
    }

    public void setIsCall(boolean isCall) {
        this.isCall = isCall;
    }

    public MonitorStatusEnum getMonitorStatus() {
        return monitorStatus;
    }

    public void setMonitorStatus(MonitorStatusEnum monitorStatus) {
        this.monitorStatus = monitorStatus;
    }

    public Map<Long, MonitorStatusEnum> getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(Map<Long, MonitorStatusEnum> equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public LightPatternEnum getLightPattern() {
        return lightPattern;
    }

    public void setLightPattern(LightPatternEnum lightPattern) {
        this.lightPattern = lightPattern;
    }
    

    /**
     * モニターステータス生成処理
     *
     * @param setting モニター設定
     * @param displays モニター表示設定
     * @param equipmentStatuses
     * @return
     */
    public static MonitorStatusEntity getMonitorStatusEntity(WorkEquipmentSetting setting, List<DisplayedStatusInfoEntity> displays, Map<Long, MonitorStatusEnum> equipmentStatuses) {

        MonitorStatusEnum monitorStatus = MonitorStatusEnum.READY;
        if (Objects.nonNull(equipmentStatuses.values()) && !equipmentStatuses.values().isEmpty()) {
            List<MonitorStatusEnum> enums = new ArrayList<>(equipmentStatuses.values());
            MonitorStatusEnum.sort(enums);
            monitorStatus = enums.get(0);
        }

        DisplayedStatusInfoEntity display;
        switch (monitorStatus) {
            case READY:
                display = getDisplayStatus(StatusPatternEnum.COMP_NORMAL, displays);
                if (Objects.nonNull(display)) {
                    return new MonitorStatusEntity(setting.getOrder(), setting.getTitle(), display.getFontColor(), display.getBackColor(),
                            display.getMelodyPath(), display.getMelodyRepeat(), monitorStatus, equipmentStatuses, display.getLightPattern());
                }
                break;
            case WORKING:
                display = getDisplayStatus(StatusPatternEnum.WORK_NORMAL, displays);
                if (Objects.nonNull(display)) {
                    return new MonitorStatusEntity(setting.getOrder(), setting.getTitle(), display.getFontColor(), display.getBackColor(),
                            display.getMelodyPath(), display.getMelodyRepeat(), monitorStatus, equipmentStatuses, display.getLightPattern());
                }
                break;
            case SUSPEND:
                display = getDisplayStatus(StatusPatternEnum.SUSPEND_NORMAL, displays);
                if (Objects.nonNull(display)) {
                    return new MonitorStatusEntity(setting.getOrder(), setting.getTitle(), display.getFontColor(), display.getBackColor(),
                            display.getMelodyPath(), display.getMelodyRepeat(), monitorStatus, equipmentStatuses, display.getLightPattern());
                }
                break;
            case CALL:
                display = getDisplayStatus(StatusPatternEnum.CALLING, displays);
                if (Objects.nonNull(display)) {
                    return new MonitorStatusEntity(setting.getOrder(), setting.getTitle(), display.getFontColor(), display.getBackColor(),
                            display.getMelodyPath(), display.getMelodyRepeat(), monitorStatus, equipmentStatuses, display.getLightPattern());
                }
                break;
            case BREAK_TIME:
                display = getDisplayStatus(StatusPatternEnum.BREAK_TIME, displays);
                if (Objects.nonNull(display)) {
                    return new MonitorStatusEntity(setting.getOrder(), setting.getTitle(), display.getFontColor(), display.getBackColor(),
                            display.getMelodyPath(), display.getMelodyRepeat(), monitorStatus, equipmentStatuses, display.getLightPattern());
                }
                break;
        }
        return new MonitorStatusEntity(setting.getOrder(), "*", "#FFFFFF", "#FF0000", "", false, MonitorStatusEnum.READY, equipmentStatuses);
    }

    /**
     * モニターステータス生成処理
     *
     * @param kanbanStatus 実績のステータス
     * @param equipId 実績を上げた設備ID
     * @param displays 進捗モニターステータス設定
     * @param isCalling 呼び出しがあるかどうか
     */
    public void updateStatus(KanbanStatusEnum kanbanStatus, Long equipId, List<DisplayedStatusInfoEntity> displays, boolean isCalling) {
        MonitorStatusEnum status;

        if (Objects.isNull(kanbanStatus)) {
            if (isCalling) {
                // 呼出
                status = MonitorStatusEnum.comparator(monitorStatus, MonitorStatusEnum.CALL);
            } else {
                // 呼出解除
                status = MonitorStatusEnum.comparator(monitorStatus, MonitorStatusEnum.READY);
            }
        } else {
            if (MonitorStatusEnum.CALL == this.monitorStatus || MonitorStatusEnum.BREAK_TIME == this.monitorStatus) {
                return;
            }
            status = MonitorStatusEnum.valueOf(kanbanStatus);
        }

        // 設備のステータスを更新
        if (equipmentStatus.containsKey(equipId)) {
            equipmentStatus.put(equipId, status);
        }

        // 現在と実績で上がったステータスを比較して表示状態を確定する
        boolean isUpdate = true;
        for (Map.Entry<Long, MonitorStatusEnum> entry : getEquipmentStatus().entrySet()) {
            if (Objects.equals(entry.getValue(), monitorStatus)) {
                status = MonitorStatusEnum.comparator(monitorStatus, status);
                isUpdate = false;
                break;
            }
        }

        if (isUpdate) {
            List<MonitorStatusEnum> enums = new ArrayList<>(getEquipmentStatus().values());
            MonitorStatusEnum.sort(enums);
            status = enums.get(0);
        }

        DisplayedStatusInfoEntity display;
        switch (status) {
            case READY:
                display = getDisplayStatus(StatusPatternEnum.COMP_NORMAL, displays);
                break;
            case WORKING:
                display = getDisplayStatus(StatusPatternEnum.WORK_NORMAL, displays);
                break;
            case SUSPEND:
                display = getDisplayStatus(StatusPatternEnum.SUSPEND_NORMAL, displays);
                break;
            case CALL:
                display = getDisplayStatus(StatusPatternEnum.CALLING, displays);
                break;
            case BREAK_TIME:
                display = getDisplayStatus(StatusPatternEnum.BREAK_TIME, displays);
                break;
            default:
                display = getDisplayStatus(StatusPatternEnum.PLAN_NORMAL, displays);
        }

        if (Objects.nonNull(display)) {
            this.frontColor = display.getFontColor();
            this.backColor = display.getBackColor();
            this.melodyFilePath = display.getMelodyPath();
            this.melodyReplay = display.getMelodyRepeat();
            this.monitorStatus = status;
            this.lightPattern = display.getLightPattern();
        }
    }

    /**
     * カンバンステータスからのモニターステータス生成処理
     *
     * @param statusEnum カンバンステータス
     * @param setting
     * @param displays
     * @param equipStatus
     * @return
     */
    public static MonitorStatusEntity getMonitorStatusEntity(KanbanStatusEnum statusEnum, AndonMonitorLineProductSetting setting, List<DisplayedStatusInfoEntity> displays, Map<Long, MonitorStatusEnum> equipStatus) {
        MonitorStatusEnum monitorStatus = MonitorStatusEnum.valueOf(statusEnum);
        if (Objects.isNull(monitorStatus)) {
            return null;
        }
        return getMonitorStatusEntity(monitorStatus, setting, displays, equipStatus);
    }
    
    /**
     * モニターステータス生成処理
     * 
     * @param monitorStatus モニターステータス
     * @param setting
     * @param displays
     * @param equipStatus
     * @return 
     */
    public static MonitorStatusEntity getMonitorStatusEntity(MonitorStatusEnum monitorStatus, AndonMonitorLineProductSetting setting, List<DisplayedStatusInfoEntity> displays, Map<Long, MonitorStatusEnum> equipStatus) {
        if (Objects.isNull(monitorStatus)) {
            return null;
        }
        DisplayedStatusInfoEntity display;
        switch (monitorStatus) {
            case READY:
                display = getDisplayStatus(StatusPatternEnum.COMP_NORMAL, displays);
                if (Objects.nonNull(display)) {
                    return new MonitorStatusEntity(null, display.getNotationName(), display.getFontColor(), display.getBackColor(),
                            display.getMelodyPath(), display.getMelodyRepeat(), MonitorStatusEnum.READY, equipStatus, display.getLightPattern());
                }
                break;
            case WORKING:
                display = getDisplayStatus(StatusPatternEnum.WORK_NORMAL, displays);
                if (Objects.nonNull(display)) {
                    return new MonitorStatusEntity(null, display.getNotationName(), display.getFontColor(), display.getBackColor(),
                            display.getMelodyPath(), display.getMelodyRepeat(), MonitorStatusEnum.WORKING, equipStatus, display.getLightPattern());
                }
                break;
            case SUSPEND:
                display = getDisplayStatus(StatusPatternEnum.SUSPEND_NORMAL, displays);
                if (Objects.nonNull(display)) {
                    return new MonitorStatusEntity(null, display.getNotationName(), display.getFontColor(), display.getBackColor(),
                            display.getMelodyPath(), display.getMelodyRepeat(), MonitorStatusEnum.SUSPEND, equipStatus, display.getLightPattern());
                }
                break;
        }
        return new MonitorStatusEntity(null, "*", "#FFFFFF", "#FF0000", "", false, MonitorStatusEnum.READY, equipStatus);
    }

    /**
     * ディスプレイ設定取得処理
     *
     * @param status
     * @param displays
     * @return
     */
    private static DisplayedStatusInfoEntity getDisplayStatus(StatusPatternEnum status, List<DisplayedStatusInfoEntity> displays) {
        for (DisplayedStatusInfoEntity display : displays) {
            if (display.getStatusName().equals(status)) {
                return display;
            }
        }
        return null;
    }
}
