/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.utility;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringTime;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import jp.adtekfuji.adFactory.entity.comparator.WorkKanbanStartDateComparator;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;

/**
 *
 * @author e-mori
 */
public class KanbanTimeUtils {

    /**
     * 工程カンバンを基準開始時間に沿ってオフセットする
     *
     * @param workKanbanInfoEntitys 工程カンバン情報
     * @param startDateTime 基準開始時間
     * @param dateTimeFormat 工程カンバンで使用されている日付のフォーマット
     */
    public static void batchStartOffsetTime(List<WorkKanbanInfoEntity> workKanbanInfoEntitys, Date startDateTime, String dateTimeFormat) {
        batchStartOffsetTime(workKanbanInfoEntitys, StringTime.convertDateToString(startDateTime, dateTimeFormat), dateTimeFormat);
    }

    /**
     * 工程カンバンを基準開始時間に沿ってオフセットする
     *
     * @param workKanbanInfoEntitys 工程カンバン情報
     * @param startDateTime 基準開始時間
     * @param dateTimeFormat 工程カンバンで使用されている日付のフォーマット
     */
    public static void batchStartOffsetTime(List<WorkKanbanInfoEntity> workKanbanInfoEntitys, String startDateTime, String dateTimeFormat) {
        if (workKanbanInfoEntitys.isEmpty()) {
            return;
        }

        //リストのソート
        Collections.sort(workKanbanInfoEntitys, new WorkKanbanStartDateComparator());

        Calendar startDate = new Calendar.Builder().setInstant(workKanbanInfoEntitys.get(0).getStartDatetime()).build();
        startDate.set(Calendar.MILLISECOND, 0);
        long differenceTime
                = StringTime.convertStringToDate(startDateTime, dateTimeFormat).getTime() - startDate.getTimeInMillis();
        String offsetTime = StringTime.convertMillisToStringTime(differenceTime);

        workKanbanInfoEntitys.stream().map((entity) -> {
            entity.setStartDatetime(StringTime.getFixedDate(entity.getStartDatetime(), offsetTime));
            return entity;
        }).forEach((entity) -> {
            entity.setCompDatetime(StringTime.getFixedDate(entity.getCompDatetime(), offsetTime));
        });

    }

    /**
     * 工程カンバンをスキップに沿ってオフセットする モデル一覧 TS:スキップ対象の開始時間 TC：スキップ対象の終了時間 NS:スキップ対象以降の開始時間 NC：スキップ対象以降の終了時間
     *
     * @param workKanbanInfoEntitys 工程カンバンリスト
     */
    public static void batchValidSkipWorkKanbanOffsetTimes(List<WorkKanbanInfoEntity> workKanbanInfoEntitys) {
        if (workKanbanInfoEntitys.isEmpty()
                || !isSkipFlag(workKanbanInfoEntitys)) {
            return;
        }
        Collections.sort(workKanbanInfoEntitys, new WorkKanbanStartDateComparator());
        for (int i = 0; i < workKanbanInfoEntitys.size(); i++) {
            batchValidSkipWorkKanbanOffsetTime(i, workKanbanInfoEntitys);
        }
    }

    /**
     * スキップ単体有効処理
     *
     * @param targetIndex
     * @param workKanbanInfoEntitys
     */
    public static void batchValidSkipWorkKanbanOffsetTime(Integer targetIndex, List<WorkKanbanInfoEntity> workKanbanInfoEntitys) {
        if (workKanbanInfoEntitys.get(targetIndex).getSkipFlag() && (workKanbanInfoEntitys.size() > targetIndex + 1)) {
            Calendar ts = new Calendar.Builder().setInstant(workKanbanInfoEntitys.get(targetIndex).getStartDatetime()).build();
            ts.set(Calendar.MILLISECOND, 0);
            Calendar tc = new Calendar.Builder().setInstant(workKanbanInfoEntitys.get(targetIndex).getCompDatetime()).build();
            tc.set(Calendar.MILLISECOND, 0);
            Calendar ns = new Calendar.Builder().setInstant(workKanbanInfoEntitys.get(targetIndex + 1).getStartDatetime()).build();
            ns.set(Calendar.MILLISECOND, 0);
            Calendar nc = new Calendar.Builder().setInstant(workKanbanInfoEntitys.get(targetIndex + 1).getCompDatetime()).build();
            nc.set(Calendar.MILLISECOND, 0);

            //TODO:並列工程は対応しない
            if (tc.getTimeInMillis() > ns.getTimeInMillis() && ts.getTimeInMillis() < nc.getTimeInMillis()) {
                return;
            }
            long differenceTime = ts.getTimeInMillis() - tc.getTimeInMillis();
            String offsetTime = StringTime.convertMillisToStringTime(differenceTime);
            for (int offsetTargetNum = targetIndex + 1; offsetTargetNum < workKanbanInfoEntitys.size(); offsetTargetNum++) {
                workKanbanInfoEntitys.get(offsetTargetNum).setStartDatetime(StringTime.getFixedDate(workKanbanInfoEntitys.get(offsetTargetNum).getStartDatetime(), offsetTime));
                workKanbanInfoEntitys.get(offsetTargetNum).setCompDatetime(StringTime.getFixedDate(workKanbanInfoEntitys.get(offsetTargetNum).getCompDatetime(), offsetTime));
            }
        }
    }

    /**
     * 工程カンバンをスキップに沿ってオフセットする モデル一覧 TS:スキップ対象の開始時間 TC：スキップ対象の終了時間 NS:スキップ対象以降の開始時間 NC：スキップ対象以降の終了時間
     *
     * @param workKanbanInfoEntitys 工程カンバンリスト
     */
    public static void batchInvalidSkipWorkKanbanOffsetTimes(List<WorkKanbanInfoEntity> workKanbanInfoEntitys) {
        if (workKanbanInfoEntitys.isEmpty()) {
            return;
        }
        for (int i = workKanbanInfoEntitys.size() - 1; i >= 0; i--) {
            if (workKanbanInfoEntitys.get(i).getSkipFlag()) {
                workKanbanInfoEntitys.get(i).setSkipFlag(Boolean.FALSE);
                batchInvalidSkipWorkKanbanOffsetTime(i, workKanbanInfoEntitys);
                workKanbanInfoEntitys.get(i).setSkipFlag(Boolean.TRUE);
            }
        }
    }

    /**
     * スキップ単体有効処理
     *
     * @param targetIndex
     * @param workKanbanInfoEntitys
     */
    public static void batchInvalidSkipWorkKanbanOffsetTime(Integer targetIndex, List<WorkKanbanInfoEntity> workKanbanInfoEntitys) {
        if (!workKanbanInfoEntitys.get(targetIndex).getSkipFlag() && workKanbanInfoEntitys.size() > targetIndex + 1) {
            Calendar ts = new Calendar.Builder().setInstant(workKanbanInfoEntitys.get(targetIndex).getStartDatetime()).build();
            ts.set(Calendar.MILLISECOND, 0);
            Calendar tc = new Calendar.Builder().setInstant(workKanbanInfoEntitys.get(targetIndex).getCompDatetime()).build();
            tc.set(Calendar.MILLISECOND, 0);

            long differenceTime = tc.getTimeInMillis() - ts.getTimeInMillis();

            String offsetTime = StringTime.convertMillisToStringTime(differenceTime);
            for (int offsetTargetNum = targetIndex + 1; offsetTargetNum < workKanbanInfoEntitys.size(); offsetTargetNum++) {
                workKanbanInfoEntitys.get(offsetTargetNum).setStartDatetime(StringTime.getFixedDate(workKanbanInfoEntitys.get(offsetTargetNum).getStartDatetime(), offsetTime));
                workKanbanInfoEntitys.get(offsetTargetNum).setCompDatetime(StringTime.getFixedDate(workKanbanInfoEntitys.get(offsetTargetNum).getCompDatetime(), offsetTime));
            }
        }
    }

    /**
     * 工程カンバンの中にスキップがあるか否か確認する
     *
     * @param workKanbanInfoEntitys 工程カンバンリスト
     * @return true:有 false:無
     */
    private static boolean isSkipFlag(List<WorkKanbanInfoEntity> workKanbanInfoEntitys) {
        return workKanbanInfoEntitys.stream().anyMatch((entity) -> (entity.getSkipFlag()));
    }

    /**
     * 工程カンバンがすべてスキップであるか否か確認する
     *
     * @param workKanbanInfoEntitys 工程カンバンリスト
     * @return true:すべてスキップ false:すべてではない
     */
    private static boolean isAllSkipFlag(List<WorkKanbanInfoEntity> workKanbanInfoEntitys) {
        return workKanbanInfoEntitys.stream().noneMatch((entity) -> (!entity.getSkipFlag()));
    }

    /**
     * 作業期間中の就業外時間リスト作成
     *
     * @param startDateTime 作業開始日
     * @param endDateTime 作業終了日
     * @param opningTime 始業時間
     * @param closingTime 就業時間
     * @param dateformat
     * @return 就業外時間リスト作成
     * @throws java.text.ParseException 日付のフォーマットに失敗
     */
    public static List<BreakTimeInfoEntity> getEmploymentOutTime(Date startDateTime, Date endDateTime, Date opningTime, Date closingTime, String dateformat) throws ParseException {
        //作業実施の日数を取得
        int differenceOfDate = -DateUtils.differenceOfDate(DateUtils.getBeginningOfDate(startDateTime), DateUtils.getEndOfDate(endDateTime), dateformat);

        Calendar works = new Calendar.Builder().setInstant(startDateTime).build();
        Calendar s = new Calendar.Builder().setInstant(opningTime).build();
        s.set(works.get(Calendar.YEAR), works.get(Calendar.MONTH), works.get(Calendar.DATE));
        Calendar e = new Calendar.Builder().setInstant(closingTime).build();
        e.set(works.get(Calendar.YEAR), works.get(Calendar.MONTH), works.get(Calendar.DATE) - 1);

        //就業外時間を作成
        List<BreakTimeInfoEntity> breakTimeInfoEntitys = new ArrayList<>();
        for (int i = 0; i <= differenceOfDate; i++) {
            //日数をオフセット
            e.set(Calendar.DATE, e.get(Calendar.DATE) + 1);
            s.set(Calendar.DATE, s.get(Calendar.DATE) + 1);

            breakTimeInfoEntitys.add(new BreakTimeInfoEntity(null, e.getTime(), s.getTime()));
        }

        return breakTimeInfoEntitys;
    }

    /**
     * 休憩時間を工程カンバンの作業時間に埋め込む。
     *
     * @param workKanbans 基準時間再設定対象のカンバン
     * @param breakTimes 休憩時間リスト
     */
    public static void batchValidBreakTimeWorkKanbanOffsetTimes(List<WorkKanbanInfoEntity> workKanbans, List<BreakTimeInfoEntity> breakTimes) {
        for (int ii = 0; workKanbans.size() > ii; ii++) {
            if (workKanbans.get(ii).getSkipFlag()) {
                continue;
            }
            batchValidBreakTimeWorkKanbanOffsetTimes(ii, workKanbans, breakTimes);
        }
    }

    /**
     * 計画時間に休憩を入れる。
     *
     * @param index
     * @param workKanbans
     * @param breakTimes
     */
    public static void batchValidBreakTimeWorkKanbanOffsetTimes(int index, List<WorkKanbanInfoEntity> workKanbans, List<BreakTimeInfoEntity> breakTimes) {
        WorkKanbanInfoEntity workKanban = workKanbans.get(index);

        List<BreakTimeInfoEntity> targetBreaks = BreaktimeUtil.getAppropriateBreaktimes(breakTimes, workKanban.getStartDatetime(), workKanban.getCompDatetime());
        Date compTime = BreaktimeUtil.getEndTimeWithBreak(targetBreaks, workKanban.getStartDatetime(), workKanban.getCompDatetime());
        long diffTime = compTime.getTime() - workKanban.getCompDatetime().getTime();

        workKanban.setCompDatetime(compTime);

        // 開始時間を設定する
        for (int ii = index + 1; ii < workKanbans.size(); ii++) {
            workKanban = workKanbans.get(ii);
            workKanban.setStartDatetime(new Date(workKanban.getStartDatetime().getTime() + diffTime));
            workKanban.setCompDatetime(new Date(workKanban.getCompDatetime().getTime() + diffTime));
        }
    }

    /**
     * 休憩単体減算処理
     *
     * @param workKanbans 基準時間再設定対象のカンバン
     * @param breakTimes 休憩時間リスト
     */
    public static void batchInvalidBreakTimeWorkKanbanOffsetTimes(List<WorkKanbanInfoEntity> workKanbans, List<BreakTimeInfoEntity> breakTimes) {
        for (int ii = workKanbans.size() - 1; ii >= 0; ii--) {
            if (workKanbans.get(ii).getSkipFlag()) {
                continue;
            }
            batchInvalidBreakTimeWorkKanbanOffsetTimes(ii, workKanbans, breakTimes);
        }
    }

    /**
     * 計画時間から休憩を取り除く。
     *
     * @param index
     * @param workKanbans
     * @param breakTimes
     */
    public static void batchInvalidBreakTimeWorkKanbanOffsetTimes(int index, List<WorkKanbanInfoEntity> workKanbans, List<BreakTimeInfoEntity> breakTimes) {
        WorkKanbanInfoEntity workKanban = workKanbans.get(index);

        List<BreakTimeInfoEntity> targetBreaks = BreaktimeUtil.getAppropriateBreaktimes(breakTimes, workKanban.getStartDatetime(), workKanban.getCompDatetime());
        long compTime = workKanban.getStartDatetime().getTime() + BreaktimeUtil.getDiffTime(targetBreaks, workKanban.getStartDatetime(), workKanban.getCompDatetime());
        long diffTime = workKanban.getCompDatetime().getTime() - compTime;

        workKanban.setCompDatetime(new Date(compTime));

        // 開始時間を設定する
        for (int ii = index + 1; ii < workKanbans.size(); ii++) {
            workKanban = workKanbans.get(ii);
            workKanban.setStartDatetime(new Date(workKanban.getStartDatetime().getTime() - diffTime));
            workKanban.setCompDatetime(new Date(workKanban.getCompDatetime().getTime() - diffTime));
        }
    }

    public boolean isOverlapWorkkanbanBreaktime(WorkKanbanInfoEntity entity, List<BreakTimeInfoEntity> breakTimeInfoEntitys) {
        List<BreakTimeInfoEntity> breaktimes = BreaktimeUtil.getAppropriateBreaktimes(breakTimeInfoEntitys, entity.getStartDatetime(), entity.getCompDatetime());
        for (BreakTimeInfoEntity breaktime : breaktimes) {
            if (((entity.getStartDatetime().equals(breaktime.getStarttime()) || entity.getStartDatetime().before(breaktime.getStarttime()))
                    && (entity.getCompDatetime().equals(breaktime.getEndtime()) || entity.getCompDatetime().after(breaktime.getEndtime())))
                    || (entity.getCompDatetime().before(breaktime.getEndtime()) && entity.getCompDatetime().after(breaktime.getStarttime()))) {
                //休憩情報が工程カンバン作業時間と完全に重複している時、休憩時間全部作業時間に加算
                //又は休憩開始時間が工程カンバン作業完了時間と重複している時、休憩時間全部作業時間に加算
                return true;
            } else if (entity.getStartDatetime().after(breaktime.getStarttime()) && entity.getStartDatetime().before(breaktime.getEndtime())) {
                //既存の休憩時間が工程カンバンの作業時間が完全に重複している場合休憩時間全部作業時間に加算
                return true;
            }
        }
        return false;
    }
}
