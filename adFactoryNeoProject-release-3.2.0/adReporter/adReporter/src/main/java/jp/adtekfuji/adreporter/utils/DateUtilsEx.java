/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adreporter.utils;

import java.math.BigInteger;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 日付時間ユーティリティクラス
 *
 * @author s-heya
 */
public class DateUtilsEx {

    private final static String COUNTDOWN_MMSS = "%s%02dm %02ds";
    private final static String COUNTDOWN_HHMMSS = "%s%02dh %02dm %02ds";
    private final static String COUNTDOWN_MMSS_2 = "%s%02d:%02d";
    private final static String COUNTDOWN_HHMMSS_2 = "%s%02d:%02d:%02d";
    private final static String pattern_tz = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern();
    private final static String pattern = DateFormatUtils.ISO_DATETIME_FORMAT.getPattern() + "'Z'";
    private final static FastDateFormat formatter = FastDateFormat.getInstance(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern());

    /**
     * 現在時刻を取得する。
     *
     * @return
     */
    public static Date now() {
        return new Date();
    }

    /**
     * ISO8601形式("yyyy-MM-dd'T'HH:mm:ssXXX")の文字列をDate型に変換する。
     *
     * @param value
     * @return
     */
    public static Date toDate(String value) {
        Date date = null;
        try {
            date = DateUtils.parseDate(value, pattern_tz);
        } catch (ParseException ex1) {
            try {
                date = DateUtils.parseDateStrictly(value, pattern);
                date = DateUtils.addMilliseconds(date, getTimeZoneOffset());
            } catch (ParseException ex2) {
                try {
                    // ミリ秒が入っていることがある
                    date = DateUtils.parseDate(value, "yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
                } catch (ParseException ex3) {
                    Logger logger = LogManager.getLogger();
                    logger.fatal(ex3);
                    date = new Date(0);
                }
            }
        }
        return date;
    }

    /**
     * Date型からオフセット付きの日付時間形式の文字列を返す。
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        if (Objects.isNull(date)) {
            return "";
        }
        return formatter.format(date);
    }

    /**
     * タイムゾーンのオフセット時間を取得する
     *
     * @return
     */
    public static int getTimeZoneOffset() {
        TimeZone tz = TimeZone.getDefault();
        return (int) tz.getRawOffset();
    }

    /**
     * オフセット付きの日付時間形式("yyyy-MM-dd'T'HH:mm:ssXXX")の文字列をCalendar型に変換する。
     *
     * @param value
     * @return
     * @throws Exception
     */
    public static Calendar toCalendar(String value) throws Exception {
        Date date = DateUtilsEx.toDate(value);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * オフセット付きの日付時間形式("yyyy-MM-dd'T'HH:mm:ssXXX")の文字列をCalendar型に変換する。
     *
     * @param value
     * @return
     */
    public static Calendar toDay(String value) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(DateUtilsEx.toDate(value));
            Calendar now = Calendar.getInstance();
            cal.set(Calendar.YEAR, now.get(Calendar.YEAR));
            cal.set(Calendar.MONTH, now.get(Calendar.MONTH));
            cal.add(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) - 1);
        } catch (Exception ex) {
            Logger logger = LogManager.getLogger();
            logger.fatal(ex);
        }
        return cal;
    }

    /**
     * 時刻の差分を取得する。
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static Date toDiffTime(Date startTime, Date endTime) {
        long diffTime = endTime.getTime() - startTime.getTime();
        return new Date(diffTime);
    }

    private static double doubleValue;
    private static int hour;
    private static int min;
    private static int sec;
    private static String symbol;

    /**
     * カウントダウン時間を表示文字列に整形する。 小数点以下は切り捨て
     *
     * @param time
     * @return
     */
    public static String formatCountdown(long time) {
        if (time > 0) {
            symbol = " ";
            doubleValue = time / 1000;
        } else {
            symbol = "-";
            doubleValue = -(time / 1000) + 0.99999999999999D;
        }
        hour = (int) (doubleValue / 3600);
        min = (int) (doubleValue % 3600 / 60);
        sec = (int) (doubleValue % 3600 % 60);
        return (hour == 0) ? String.format(COUNTDOWN_MMSS, symbol, min, sec) : String.format(COUNTDOWN_HHMMSS, symbol, hour, min, sec);
    }

    /**
     * タクトタイムを表示文字列に整形する。
     *
     * @param time
     * @return
     */
    public static String formatTaktTime(long time) {
        if (time >= 0) {
            symbol = " ";
            doubleValue = time / 1000;
        } else {
            symbol = "-";
            doubleValue = -(time / 1000);
        }
        hour = (int) (doubleValue / 3600);
        min = (int) (doubleValue % 3600 / 60);
        sec = (int) (doubleValue % 3600 % 60);
        return (hour == 0) ? String.format(COUNTDOWN_MMSS, symbol, min, sec) : String.format(COUNTDOWN_HHMMSS, symbol, hour, min, sec);
    }

    private static long longValue;

    /**
     * タクトタイムを表示文字列「-HH:mm:ss」に整形する。
     *
     * @param time
     * @return
     */
    public static String formatTaktTime2(long time) {
        if (time >= 0) {
            symbol = " ";
            longValue = time / 1000;
        } else {
            symbol = "-";
            longValue = -(time / 1000);
        }
        hour = (int) (longValue / 3600);
        min = (int) (longValue % 3600 / 60);
        sec = (int) (longValue % 3600 % 60);
        return (hour == 0) ? String.format(COUNTDOWN_MMSS_2, symbol, min, sec) : String.format(COUNTDOWN_HHMMSS_2, symbol, hour, min, sec);
    }

    /**
     * OffsetTime型をDate型に変換する。
     *
     * @param offsetTime
     * @return
     */
    public static Date toDate(OffsetTime offsetTime) {
        if (Objects.isNull(offsetTime)) {
            return null;
        }
        Instant instant = offsetTime.atDate(LocalDate.now()).atZoneSameInstant(ZoneId.systemDefault()).toInstant();
        return toDate(instant);
    }

    /**
     * InstantをDate型に変換する。
     *
     * @param instant
     * @return
     */
    private static Date toDate(Instant instant) {
        BigInteger milis = BigInteger.valueOf(instant.getEpochSecond()).multiply(BigInteger.valueOf(1000));
        milis = milis.add(BigInteger.valueOf(instant.getNano()).divide(BigInteger.valueOf(1_000_000)));
        return new Date(milis.longValue());
    }

    /**
     * 時間を 00:00:00 に変換する。
     *
     * @param date
     * @return
     */
    public static Date toBeginning(Date date) {
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        return start.getTime();
    }

    /**
     * 時間を 23:59:59 に変換する。
     *
     * @param date
     * @return
     */
    public static Date toEnd(Date date) {
        Calendar end = Calendar.getInstance();
        end.setTime(date);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        return end.getTime();
    }
}
