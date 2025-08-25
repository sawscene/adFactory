/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adfactoryserver.entity.master.BreaktimeEntity;
import org.joda.time.Interval;

/**
 * 休憩時間ユーティリティクラス
 *
 * @author s-heya
 */
public class BreakTimeUtils {

    /**
     * 作業内の休憩時間を取得する。
     *
     * @param BreakTimes
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static List<BreaktimeEntity> getBreakInWork(List<BreaktimeEntity> BreakTimes, Date startDateTime, Date endDateTime) {
        LocalDate startDate = LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneOffset.systemDefault()).toLocalDate();
        LocalDate endDate = LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneOffset.systemDefault()).toLocalDate();
        List<BreaktimeEntity> breaktimes = new ArrayList<>();
        long diffDate = ChronoUnit.DAYS.between(startDate, endDate);
        for (int loop = 0; loop <= diffDate; loop++) {
            for (BreaktimeEntity breaktimeEntity : BreakTimes) {
                LocalDate date = startDate.plusDays(loop);
                Calendar s = Calendar.getInstance();
                Calendar e = Calendar.getInstance();
                Calendar breaktimeStartTime = new Calendar.Builder().setInstant(breaktimeEntity.getStarttime()).build();
                Calendar breaktimeEndTime = new Calendar.Builder().setInstant(breaktimeEntity.getEndtime()).build();

                //休憩時間の日付が異なっていた場合休憩時間の日付を上書きしない
                if (breaktimeStartTime.get(Calendar.YEAR) == breaktimeEndTime.get(Calendar.YEAR)
                        && breaktimeStartTime.get(Calendar.MONDAY) == breaktimeEndTime.get(Calendar.MONDAY)
                        && breaktimeStartTime.get(Calendar.DATE) == breaktimeEndTime.get(Calendar.DATE)) {
                    s.setTime(breaktimeEntity.getStarttime());
                    s.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                    e.setTime(breaktimeEntity.getEndtime());
                    e.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                } else {
                    s.setTime(breaktimeEntity.getStarttime());
//                    s.set(breaktimeStartTime.get(Calendar.YEAR), breaktimeStartTime.get(Calendar.MONDAY), breaktimeStartTime.get(Calendar.DATE));
                    s.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                    e.setTime(breaktimeEntity.getEndtime());
//                    e.set(breaktimeEndTime.get(Calendar.YEAR), breaktimeEndTime.get(Calendar.MONDAY), breaktimeEndTime.get(Calendar.DATE));
                    e.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth() + 1);
                }
                if (breaktimes.isEmpty()) {
                    breaktimes.add(new BreaktimeEntity("", s.getTime(), e.getTime()));
                    continue;
                }
                //休憩時間の重なりを取り除く.
                boolean overLabFlag = false;
                List<BreaktimeEntity> overLabList = new ArrayList<>();
                for (BreaktimeEntity breaktime : breaktimes) {
                    if ((s.getTime().equals(breaktime.getStarttime()) || s.getTime().before(breaktime.getStarttime()))
                            && (e.getTime().equals(breaktime.getEndtime()) || e.getTime().after(breaktime.getEndtime()))) {
                        //既存の休憩情報が新しい休憩情報に時間が完全に重複している場合後で削除
                        overLabList.add(breaktime);
                        continue;
                    } else if ((breaktime.getStarttime().equals(s.getTime()) || breaktime.getStarttime().before(s.getTime()))
                            && (breaktime.getEndtime().equals(e.getTime()) || breaktime.getEndtime().after(e.getTime()))) {
                        //新しく追加される休憩情報が既存の休憩情報に時間が完全に重複している場合
                        overLabFlag = true;
                        break;
                    }
                    if (s.getTime().after(breaktime.getStarttime()) && s.getTime().before(breaktime.getEndtime())) {
                        s.setTime(breaktime.getEndtime());
                    }
                    if (e.getTime().after(breaktime.getStarttime()) && e.getTime().before(breaktime.getEndtime())) {
                        e.setTime(breaktime.getStarttime());
                    }
                }
                if (!overLabFlag) {
                    breaktimes.add(new BreaktimeEntity("", s.getTime(), e.getTime()));
                }
                for (BreaktimeEntity overLapData : overLabList) {
                    breaktimes.remove(overLapData);
                }
            }
        }
        breaktimes.sort(Comparator.comparing(item -> item.getStarttime()));

        return breaktimes;
    }

    /**
     * 作業内の休憩時間を取得する。
     *
     * @param BreakTimes
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static List<BreaktimeEntity> getBreakInWork2(List<BreakTimeInfoEntity> BreakTimes, Date startDateTime, Date endDateTime) {
        LocalDate startDate = LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneOffset.systemDefault()).toLocalDate();
        LocalDate endDate = LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneOffset.systemDefault()).toLocalDate();
        List<BreaktimeEntity> breaktimes = new ArrayList<>();
        long diffDate = ChronoUnit.DAYS.between(startDate, endDate);
        for (int loop = 0; loop <= diffDate; loop++) {
            for (BreakTimeInfoEntity breaktimeEntity : BreakTimes) {
                LocalDate date = startDate.plusDays(loop);
                Calendar s = Calendar.getInstance();
                Calendar e = Calendar.getInstance();
                Calendar breaktimeStartTime = new Calendar.Builder().setInstant(breaktimeEntity.getStarttime()).build();
                Calendar breaktimeEndTime = new Calendar.Builder().setInstant(breaktimeEntity.getEndtime()).build();

                //休憩時間の日付が異なっていた場合休憩時間の日付を上書きしない
                if (breaktimeStartTime.get(Calendar.YEAR) == breaktimeEndTime.get(Calendar.YEAR)
                        && breaktimeStartTime.get(Calendar.MONDAY) == breaktimeEndTime.get(Calendar.MONDAY)
                        && breaktimeStartTime.get(Calendar.DATE) == breaktimeEndTime.get(Calendar.DATE)) {
                    s.setTime(breaktimeEntity.getStarttime());
                    s.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                    e.setTime(breaktimeEntity.getEndtime());
                    e.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                } else {
                    s.setTime(breaktimeEntity.getStarttime());
//                    s.set(breaktimeStartTime.get(Calendar.YEAR), breaktimeStartTime.get(Calendar.MONDAY), breaktimeStartTime.get(Calendar.DATE));
                    s.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
                    e.setTime(breaktimeEntity.getEndtime());
//                    e.set(breaktimeEndTime.get(Calendar.YEAR), breaktimeEndTime.get(Calendar.MONDAY), breaktimeEndTime.get(Calendar.DATE));
                    e.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth() + 1);
                }
                if (breaktimes.isEmpty()) {
                    breaktimes.add(new BreaktimeEntity("", s.getTime(), e.getTime()));
                    continue;
                }
                //休憩時間の重なりを取り除く.
                boolean overLabFlag = false;
                List<BreaktimeEntity> overLabList = new ArrayList<>();
                for (BreaktimeEntity breaktime : breaktimes) {
                    if ((s.getTime().equals(breaktime.getStarttime()) || s.getTime().before(breaktime.getStarttime()))
                            && (e.getTime().equals(breaktime.getEndtime()) || e.getTime().after(breaktime.getEndtime()))) {
                        //既存の休憩情報が新しい休憩情報に時間が完全に重複している場合後で削除
                        overLabList.add(breaktime);
                        continue;
                    } else if ((breaktime.getStarttime().equals(s.getTime()) || breaktime.getStarttime().before(s.getTime()))
                            && (breaktime.getEndtime().equals(e.getTime()) || breaktime.getEndtime().after(e.getTime()))) {
                        //新しく追加される休憩情報が既存の休憩情報に時間が完全に重複している場合
                        overLabFlag = true;
                        break;
                    }
                    if (s.getTime().after(breaktime.getStarttime()) && s.getTime().before(breaktime.getEndtime())) {
                        s.setTime(breaktime.getEndtime());
                    }
                    if (e.getTime().after(breaktime.getStarttime()) && e.getTime().before(breaktime.getEndtime())) {
                        e.setTime(breaktime.getStarttime());
                    }
                }
                if (!overLabFlag) {
                    breaktimes.add(new BreaktimeEntity("", s.getTime(), e.getTime()));
                }
                for (BreaktimeEntity overLapData : overLabList) {
                    breaktimes.remove(overLapData);
                }
            }
        }
        breaktimes.sort(Comparator.comparing(item -> item.getStarttime()));

        return breaktimes;
    }

    /**
     * 休憩を含めた終了時間を取得する。
     *
     * @param breakTimes
     * @param startTime
     * @param endTime
     * @return
     */
    public static Date getEndTimeWithBreak(List<BreaktimeEntity> breakTimes, Date startTime, Date endTime) {
        long start = startTime.getTime();
        long end = endTime.getTime();

        for (BreaktimeEntity breakTime : breakTimes) {
            long startBreak = breakTime.getStarttime().getTime();
            long endBreak = breakTime.getEndtime().getTime();

            long diff = 0;
            if ((start > startBreak && start < endBreak) && (end < endBreak && end > startBreak)) {
                // ④ (工程開始 > 休憩開始 and 工程開始 < 休憩終了) and (工程終了 < 休憩終了 and 工程終了 > 休憩開始)
                // 休憩時間を一部加算する
                diff = endBreak - start;
            } else if (start <= startBreak && end >= endBreak) {
                // ① 工程開始 <= 休憩開始 and 工程終了 >= 休憩終了
                // 休憩時間を全部加算する
                diff = endBreak - startBreak;
            } else if (end < endBreak && end > startBreak) {
                // ② 工程終了 < 休憩終了 and 工程終了 > 休憩開始
                // 休憩時間を全部加算する
                diff = endBreak - startBreak;
            } else if (start > startBreak && start < endBreak) {
                // ③ 工程開始 > 休憩開始 and 工程開始 < 休憩終了
                // 休憩時間を一部加算する
                diff = endBreak - start;
            }  else if (end < startBreak ) {
                break;
            }

            end += diff;
        }

        return new Date(end);
    }

    /**
     * 時間帯から休憩時間を取得する。
     *
     * @param breakTimes
     * @param startTime
     * @param endTime
     * @return
     */
    public static long getBreakTime(List<BreaktimeEntity> breakTimes, Date startTime, Date endTime) {
        Interval interval = new Interval(startTime.getTime(), endTime.getTime());
        Interval breakInterval;
        long time = 0;

        for (BreaktimeEntity breakTime : breakTimes) {
            breakInterval = new Interval(breakTime.getStarttime().getTime(), breakTime.getEndtime().getTime());
            if (interval.overlaps(breakInterval)) {
                Interval overlap = interval.overlap(breakInterval);
                time += overlap.getEndMillis() - overlap.getStartMillis();
            }
        }

        return time;
    }
}
