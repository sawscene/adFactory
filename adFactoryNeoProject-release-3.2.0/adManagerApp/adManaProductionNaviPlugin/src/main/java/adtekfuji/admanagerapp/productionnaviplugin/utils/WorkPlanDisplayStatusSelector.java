/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import org.apache.commons.lang3.time.DateUtils;

/**
 * カンバンステータスに合わせた色選択用クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.14.Wen
 */
public class WorkPlanDisplayStatusSelector {

    private final List<DisplayedStatusInfoEntity> displayStatuses;
//    private final BreaktimeInfoFacade breaktimeInfoFacade = new BreaktimeInfoFacade();

    public WorkPlanDisplayStatusSelector(List<DisplayedStatusInfoEntity> displayStatuses) {
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
    public DisplayedStatusInfoEntity getPlanDisplayStatus(KanbanStatusEnum statusEnum, Date planStart, Date planEnd, Date actStart, Date actEnd
            , List<BreakTimeInfoEntity> listBreak, List<OrganizationInfoEntity> organizationDatas) {
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
                Optional<BreakTimeInfoEntity> data;
                Date nowDate = new Date();
                nowDate = DateUtils.setYears(nowDate, 1970);
                nowDate = DateUtils.setMonths(nowDate, 1);
                nowDate = DateUtils.setDays(nowDate, 1);
                Date targetStartDate = new Date();
                Date targetStopDate = new Date();
                
                for(OrganizationInfoEntity organizationData : organizationDatas){
                    for(Long _break : organizationData.getBreakTimeInfoCollection() ){
                        data = listBreak.stream().filter(e -> e.getBreaktimeId().equals(_break)).findFirst();
                        if(data.isPresent()){
                            targetStartDate = DateUtils.setYears(data.get().getStarttime(), 1970);
                            targetStartDate = DateUtils.setMonths(targetStartDate, 1);
                            targetStartDate = DateUtils.setDays(targetStartDate, 1);
                            targetStopDate = DateUtils.setYears(data.get().getEndtime(), 1970);
                            targetStopDate = DateUtils.setMonths(targetStopDate, 1);
                            targetStopDate = DateUtils.setDays(targetStopDate, 1);
                            
                            if( targetStartDate.after(nowDate) && targetStopDate.before(nowDate)){
                                entity = getDisplayStatus(StatusPatternEnum.BREAK_TIME);
                            }
                        }
                    }
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
     * 指定されたパラメータのステータスを取得
     *
     * @param statusEnum カンバンステータス
     * @param planStart 計画開始時間
     * @param planEnd 計画終了時間
     * @param actStart 実績開始時間
     * @param actEnd 実績終了時間
     * @return 現在の状態
     */
    public DisplayedStatusInfoEntity getActualDisplayStatus(KanbanStatusEnum statusEnum, Date planStart, Date planEnd, Date actStart, Date actEnd
            , List<BreakTimeInfoEntity> listBreak, List<OrganizationInfoEntity> organizationDatas) {
        DisplayedStatusInfoEntity entity = new DisplayedStatusInfoEntity();
        switch (statusEnum) {
            // 中止
            // その他
            case PLANNED:       // 計画済み
            case PLANNING:      // 計画中
                entity = getDisplayStatus(StatusPatternEnum.PLAN_NORMAL);
                if (new Date().after(planStart)) {
                    entity = getDisplayStatus(StatusPatternEnum.PLAN_DELAYSTART);
                }
                break;
            case WORKING:       // 作業中
                entity = getDisplayStatus(StatusPatternEnum.WORK_NORMAL);
                if (new Date().before(planStart)) {
                    entity = getDisplayStatus(StatusPatternEnum.WORK_DELAYSTART);
                }
                Optional<BreakTimeInfoEntity> data;
                Date nowDate = new Date();
                nowDate = DateUtils.setYears(nowDate, 1970);
                nowDate = DateUtils.setMonths(nowDate, 1);
                nowDate = DateUtils.setDays(nowDate, 1);
                Date targetStartDate = new Date();
                Date targetStopDate = new Date();
                
                for(OrganizationInfoEntity organizationData : organizationDatas){
                    for(Long _break : organizationData.getBreakTimeInfoCollection() ){
                        data = listBreak.stream().filter(e -> e.getBreaktimeId().equals(_break)).findFirst();
                        if(data.isPresent()){
                            targetStartDate = DateUtils.setYears(data.get().getStarttime(), 1970);
                            targetStartDate = DateUtils.setMonths(targetStartDate, 1);
                            targetStartDate = DateUtils.setDays(targetStartDate, 1);
                            targetStopDate = DateUtils.setYears(data.get().getEndtime(), 1970);
                            targetStopDate = DateUtils.setMonths(targetStopDate, 1);
                            targetStopDate = DateUtils.setDays(targetStopDate, 1);
                            
                            if( targetStartDate.after(nowDate) && targetStopDate.before(nowDate)){
                                entity = getDisplayStatus(StatusPatternEnum.BREAK_TIME);
                            }
                        }
                    }
                }
                
                if (new Date().after(planEnd)) {
                    entity = getDisplayStatus(StatusPatternEnum.WORK_DELAYCOMP);
                }
                break;
            case INTERRUPT:     // 一時中断
                entity = getDisplayStatus(StatusPatternEnum.INTERRUPT_NORMAL);
                break;
            case SUSPEND:       // 中止
                entity = getDisplayStatus(StatusPatternEnum.SUSPEND_NORMAL);
                break;
            case COMPLETION:    // 完了
                entity = getDisplayStatus(StatusPatternEnum.COMP_NORMAL);
                if (Objects.nonNull(actEnd) && planEnd.before(actEnd)) {
                    entity = getDisplayStatus(StatusPatternEnum.COMP_DELAYCOMP);
                }
                break;
            default:
//                entity = getDisplayStatus(workKanban.getWorkStatus());
                break;
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
