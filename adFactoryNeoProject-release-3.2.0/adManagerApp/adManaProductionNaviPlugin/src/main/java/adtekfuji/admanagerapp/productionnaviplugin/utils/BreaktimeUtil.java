/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils;

import adtekfuji.utility.DateUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;

/**
 *
 * @author w1092tst
 */
public class BreaktimeUtil {
    
    private BreaktimeUtil() {
    }

    /**
     * 休憩時間内かどうか.
     *
     * @param breaktimeCollection
     * @param checkTime
     * @return
     */
    public static boolean isBreaktime(List<BreakTimeInfoEntity> breaktimeCollection, Date checkTime) {
        return Objects.nonNull(getEndOfBreaktime(breaktimeCollection, checkTime));
    }

    /**
     * 休憩時間の終わりを取得する
     *
     * @param breaktimeCollection
     * @param checkTime
     * @return
     */
    public static Date getEndOfBreaktime(List<BreakTimeInfoEntity> breaktimeCollection, Date checkTime) {
        //休憩時間を現在日付に展開する.
        LocalDate date = DateUtils.toLocalDateTime(checkTime).toLocalDate();
        List<BreakTimeInfoEntity> breaktimes = getAppropriateBreaktimes(breaktimeCollection,
                DateUtils.getBeginningOfDate(date.minusDays(1)), DateUtils.getEndOfDate(date.plusDays(1)), 0);
        //探索.
        for (BreakTimeInfoEntity breaktime : breaktimes) {
            if (checkTime.equals(breaktime.getStarttime()) || checkTime.equals(breaktime.getEndtime())
                    || ((checkTime.after(breaktime.getStarttime()) && checkTime.before(breaktime.getEndtime())))) {
                return breaktime.getEndtime();
            }
        }
        return null;
    }

    /**
     * 休憩時間の抜いた差分の時間を計算する.
     *
     * @param breaktimeCollection 休憩時間
     * @param startDateTime 工程の開始日時
     * @param endDateTime 工程の終了日時
     * @return millsec
     */
    public static long getDiffTime(List<BreakTimeInfoEntity> breaktimeCollection, Date startDateTime, Date endDateTime) {
        if (Objects.isNull(startDateTime) || Objects.isNull(endDateTime)) {
            return 0;
        }
        if (Objects.isNull(breaktimeCollection)) {
            return ChronoUnit.MILLIS.between(startDateTime.toInstant(), endDateTime.toInstant());
        }

        //休憩時間を現在日付に展開する。2、3日とか稼働しているのも考慮.
        List<BreakTimeInfoEntity> breaktimes = getAppropriateBreaktimes(breaktimeCollection, startDateTime, endDateTime, 0);

        //休憩時間をさっぴいていく.
        LocalDateTime startTime = LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneOffset.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneOffset.systemDefault());
        long diffTime = ChronoUnit.MILLIS.between(startTime, endTime);
        for (BreakTimeInfoEntity breaktime : breaktimes) {
            LocalDateTime startBreakTime = LocalDateTime.ofInstant(breaktime.getStarttime().toInstant(), ZoneOffset.systemDefault());
            LocalDateTime endBreakTime = LocalDateTime.ofInstant(breaktime.getEndtime().toInstant(), ZoneOffset.systemDefault());

            if (startTime.isBefore(startBreakTime) && endTime.isAfter(endBreakTime)) {
                diffTime -= ChronoUnit.MILLIS.between(startBreakTime, endBreakTime);
            } else if (startTime.isBefore(startBreakTime) && endTime.isBefore(startBreakTime)) {
                //not calc
            } else if (startTime.isBefore(startBreakTime) && endTime.isAfter(startBreakTime)) {
                diffTime -= ChronoUnit.MILLIS.between(startBreakTime, endTime);
            } else if ((startTime.isAfter(startBreakTime) || startTime.equals(startBreakTime)) && (endTime.isBefore(endBreakTime) || (endTime.equals(endBreakTime)))) {
                diffTime -= ChronoUnit.MILLIS.between(startTime, endTime);
            } else if ((startTime.isAfter(startBreakTime) || startTime.equals(startBreakTime)) && startTime.isBefore(endBreakTime) && endTime.isAfter(endBreakTime)) {
                diffTime -= ChronoUnit.MILLIS.between(startTime, endBreakTime);
            } else if (startTime.isAfter(endBreakTime) && endTime.isAfter(endBreakTime)) {
                //not calc
            }
        }
        return diffTime;
    }

    /**
     * 指定期間の休憩時間を取得する。
     *
     * @param breaktimeCollection
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static List<BreakTimeInfoEntity> getAppropriateBreaktimes(List<BreakTimeInfoEntity> breaktimeCollection, Date startDateTime, Date endDateTime) {
        return BreaktimeUtil.getAppropriateBreaktimes(breaktimeCollection, startDateTime, endDateTime, 0);
    }

    /**
     * 指定期間の休憩時間を取得する。
     *
     * @param breaktimeCollection
     * @param startDateTime
     * @param endDateTime
     * @param days 期間前日からの休憩時間を取得する場合、-1を指定する。
     * @return
     */
    public static List<BreakTimeInfoEntity> getAppropriateBreaktimes(List<BreakTimeInfoEntity> breaktimeCollection, Date startDateTime, Date endDateTime, int days) {
        LocalDate startDate = LocalDateTime.ofInstant(startDateTime.toInstant(), ZoneOffset.systemDefault()).toLocalDate();
        LocalDate endDate = LocalDateTime.ofInstant(endDateTime.toInstant(), ZoneOffset.systemDefault()).toLocalDate();
        List<BreakTimeInfoEntity> breaktimes = new ArrayList<>();
        long diffDate = ChronoUnit.DAYS.between(startDate, endDate);
        for (int loop = days; loop <= diffDate; loop++) {
            for (BreakTimeInfoEntity breaktimeEntity : breaktimeCollection) {
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
                    breaktimes.add(new BreakTimeInfoEntity("", s.getTime(), e.getTime()));
                    continue;
                }
                //休憩時間の重なりを取り除く.
                boolean overLabFlag = false;
                List<BreakTimeInfoEntity> overLabList = new ArrayList<>();
                for (BreakTimeInfoEntity breaktime : breaktimes) {
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
                    breaktimes.add(new BreakTimeInfoEntity("", s.getTime(), e.getTime()));
                }
                for (BreakTimeInfoEntity overLapData : overLabList) {
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
    public static Date getEndTimeWithBreak(List<BreakTimeInfoEntity> breakTimes, Date startTime, Date endTime) {
        long start = startTime.getTime();
        long end = endTime.getTime();

        for (BreakTimeInfoEntity breakTime : breakTimes) {
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
     * 。
     *
     * @param breaktimeCollection1
     * @param breaktimeCollection2
     * @return
     */
    public static List<BreakTimeInfoEntity> addBreaktimesToBreaktimes(List<BreakTimeInfoEntity> breaktimeCollection1, List<BreakTimeInfoEntity> breaktimeCollection2) {
        List<BreakTimeInfoEntity> result = new ArrayList<>();
        
        for(BreakTimeInfoEntity entity1 : breaktimeCollection1) {
            result.add(new BreakTimeInfoEntity(""
                    , getComparedStartOfBreaktime(breaktimeCollection2, entity1.getStarttime())
                    , getComparedEndOfBreaktime(breaktimeCollection2, entity1.getEndtime())));
        }
        
        for(BreakTimeInfoEntity check : breaktimeCollection2) {
            if(!isBreaktime(result, check.getStarttime())) {
                result.add(check);
            }
        }
        
        result.sort(Comparator.comparing(item -> item.getStarttime()));

        return result;
    }
    
    /**
     * 休憩時間の終わりを取得する
     *
     * @param breaktimeCollection
     * @param startTime
     * @return
     */
    public static Date getComparedStartOfBreaktime(List<BreakTimeInfoEntity> breaktimes, Date startTime) {
        //探索.
        for (BreakTimeInfoEntity breaktime : breaktimes) {
            if (startTime.equals(breaktime.getStarttime()) || startTime.equals(breaktime.getEndtime())
                    || ((startTime.after(breaktime.getStarttime()) && startTime.before(breaktime.getEndtime())))) {
                return breaktime.getStarttime();
            }
        }
        return startTime;
    }
    
    /**
     * 休憩時間の終わりを取得する
     *
     * @param breaktimeCollection
     * @param endTime
     * @return
     */
    public static Date getComparedEndOfBreaktime(List<BreakTimeInfoEntity> breaktimes, Date endTime) {
        //探索.
        for (BreakTimeInfoEntity breaktime : breaktimes) {
            if (endTime.equals(breaktime.getStarttime()) || endTime.equals(breaktime.getEndtime())
                    || ((endTime.after(breaktime.getStarttime()) && endTime.before(breaktime.getEndtime())))) {
                return breaktime.getEndtime();
            }
        }
        return endTime;
    }
}
