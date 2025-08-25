/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.utils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;

/**
 * カンバンステータスに合わせた色選択用クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.14.Wen
 */
public class DisplayStatusSelector {

    private final List<DisplayedStatusInfoEntity> displayStatuses;

    public DisplayStatusSelector(List<DisplayedStatusInfoEntity> displayStatuses) {
        this.displayStatuses = displayStatuses;
    }

    /**
     * 指定されたパラメータのステータスを取得
     *
     * @param statusEnum カンバンステータス
     * @param planStart 計画開始時間
     * @param planEnd 計画終了時間
     * @param actStart 実績開始時間
     * @param actEnd 実績終了時間
     * @return 現在の状態
     */
    public DisplayedStatusInfoEntity getKanbanDisplayStatus(KanbanStatusEnum statusEnum, Date planStart, Date planEnd, Date actStart, Date actEnd) {
        DisplayedStatusInfoEntity entity = new DisplayedStatusInfoEntity();
        switch (statusEnum) {
            case PLANNED:
            case PLANNING:
                entity = getDisplayStatus(StatusPatternEnum.PLAN_NORMAL);
                if (new Date().after(planStart)) {
                    entity = getDisplayStatus(StatusPatternEnum.PLAN_DELAYSTART);
                }
                break;
            case WORKING:
                entity = getDisplayStatus(StatusPatternEnum.WORK_NORMAL);
                if (new Date().before(planStart)) {
                    entity = getDisplayStatus(StatusPatternEnum.WORK_DELAYSTART);
                }
                if (new Date().after(planEnd)) {
                    entity = getDisplayStatus(StatusPatternEnum.WORK_DELAYCOMP);
                }
                break;
            case INTERRUPT:
                entity = getDisplayStatus(StatusPatternEnum.INTERRUPT_NORMAL);
                break;
            case SUSPEND:
                entity = getDisplayStatus(StatusPatternEnum.SUSPEND_NORMAL);
                break;
            case COMPLETION:
                entity = getDisplayStatus(StatusPatternEnum.COMP_NORMAL);
                if (Objects.nonNull(actEnd) && planEnd.before(actEnd)) {
                    entity = getDisplayStatus(StatusPatternEnum.COMP_DELAYCOMP);
                }
                break;
            default:
        }

        return entity;
    }

    /**
     * 指定されたステータスを取得
     *
     * @param pattern ステータスパターン
     * @return 表示ステータス
     */
    private DisplayedStatusInfoEntity getDisplayStatus(StatusPatternEnum pattern) {
        DisplayedStatusInfoEntity entity = new DisplayedStatusInfoEntity();
        for (DisplayedStatusInfoEntity displayStatus : displayStatuses) {
            if (Objects.nonNull(displayStatus.getStatusName()) && displayStatus.getStatusName().equals(pattern)) {
                entity = displayStatus;
            }
        }
        return entity;
    }

}
