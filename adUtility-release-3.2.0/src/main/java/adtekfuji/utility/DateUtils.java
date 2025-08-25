/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

public class DateUtils {

    private static final String COUNTDOWN_UNSIGNED_HHMM = "%02dh %02dm";
    private static final String COUNTDOWN_UNSIGNED_MMSS = "%02dm %02ds";
    private static final String COUNTDOWN_MMSS = "%s  %02dm %02ds";
    private static final String COUNTDOWN_HHMMSS = "%s %02dh %02dm %02ds";

    public static final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";
    
    /**
     * 日時フォーマット
     */
    public static final String JSON_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";

    /**
     * LocalDateTime コンパレーター
     */
    public static final Comparator<LocalDateTime> localDateTimeComparator = (p1, p2) -> {
        if (p1.isBefore(p2)) {
            return -1;
        } else if (p1.isAfter(p2)) {
            return 1;
        } else {
            return 0;
        }
    };

    /**
     * 指定日を00:00:00に変換
     *
     * @param date
     * @return
     */
    public static Date getBeginningOfDate(Date date) {
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        return start.getTime();
    }

    /**
     * 指定日を23:59:59に変換
     *
     * @param date
     * @return Date
     */
    public static Date getEndOfDate(Date date) {
        Calendar end = Calendar.getInstance();
        end.setTime(date);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        return end.getTime();
    }

    /**
     * 指定日を00:00:00に変換
     *
     * @param date
     * @return
     */
    public static Date getBeginningOfDate(LocalDate date) {
        if (Objects.isNull(date)) return null;
        return getBeginningOfDate(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    }

    /**
     * 指定日を23:59:59に変換
     *
     * @param date
     * @return Date
     */
    public static Date getEndOfDate(LocalDate date) {
        if (Objects.isNull(date)) return null;
        return getEndOfDate(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    }

    public static int differenceOfDate(String farstDate, String lastDate) throws ParseException {
        Long date1 = DateFormat.getDateInstance().parse(farstDate).getTime();
        Long date2 = DateFormat.getDateInstance().parse(lastDate).getTime();
        Long dateTime = 1000l * 60l * 60l * 24l;
        Long differenceOfDate = (date1 - date2) / dateTime;
        return Integer.parseInt(differenceOfDate.toString());
    }

    public static int differenceOfDate(Date farstDate, Date lastDate, String dateformat) throws ParseException {
        return differenceOfDate(StringTime.convertDateToString(farstDate, dateformat), StringTime.convertDateToString(lastDate, dateformat));
    }

    /**
     * 指定日を月の月初00:00:00に変換
     *
     * @param date
     * @return
     */
    public static Date getBeginningOfMonth(Date date) {
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        int day = start.getActualMinimum(Calendar.DAY_OF_MONTH);
        start.set(Calendar.DAY_OF_MONTH, day);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        return start.getTime();
    }

    /**
     * 指定日を月の月末23:59:59に変換
     *
     * @param date
     * @return Date
     */
    public static Date getEndOfMonth(Date date) {
        Calendar end = Calendar.getInstance();
        end.setTime(date);
        int day = end.getActualMaximum(Calendar.DAY_OF_MONTH);
        end.set(Calendar.DAY_OF_MONTH, day);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        return end.getTime();
    }

    /**
     * 新日時APIをDateに変換
     *
     * @param date
     * @param time
     * @return Date
     */
    public static Date toDate(LocalDate date, LocalTime time) {
        return toDate(date.atTime(time));
    }

    /**
     * 新日時APIをDateに変換
     *
     * @param date
     * @return Date
     */
    public static Date toDate(LocalDate date) {
        return Objects.nonNull(date) ? Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    /**
     * 新日時APIをDateに変換
     *
     * @param dateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * DateをLocalDateTimeに変換
     *
     * @param dateTime
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date dateTime) {
        return LocalDateTime.ofInstant(dateTime.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 文字列をLocalDateTimeに変換
     *
     * @param value
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(String value) {
        return Optional.ofNullable(parse(value)).map(DateUtils::toLocalDateTime).orElse(null);
    }


    /**
     * DateをLocalDateに変換
     *
     * @param date
     * @return
     */
    public static LocalDate toLocalDate(Date date) {
        return Objects.nonNull(date) ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }

    /**
     * DateをLocalTimeに変換
     *
     * @param date
     * @return
     */
    public static LocalTime toLocalTime(Date date) {
        return Objects.nonNull(date) ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime() : null;
    }

    /**
     * 最小値を返す。
     *
     * @return
     */
    public static Date min() {
        Date date = null;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.parse("1970-01-01 00:00:00+00");
        } catch (Exception ex) {
        }
        return date;
    }

    /**
     * 指定した日付の月末日付を取得
     *
     * @param date
     * @return
     */
    public static Date getFarstDay(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        int last = cal1.getActualMinimum(Calendar.DATE);
        Calendar cal2 = Calendar.getInstance();
        cal2.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), last);
        return cal2.getTime();
    }

    /**
     * 指定した日付の月末日付を取得
     *
     * @param date
     * @return
     */
    public static Date getLastDay(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        int last = cal1.getActualMaximum(Calendar.DATE);
        Calendar cal2 = Calendar.getInstance();
        cal2.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), last);
        return cal2.getTime();
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
    public static String formatCountup(long time) {
        doubleValue = time / 1000D;
        hour = (int) (doubleValue / 3600D);
        min = (int) (doubleValue % 3600D / 60D);
        sec = (int) (doubleValue % 3600D % 60D);
        return String.format(COUNTDOWN_UNSIGNED_HHMM, hour, min);
    }

    /**
     * カウントダウン時間を表示文字列に整形する。 小数点以下は切り捨て
     *
     * @param time
     * @return
     */
    public static String formatCountdown(long time) {
        if (time > 0) {
            symbol = " ";
            doubleValue = time / 1000D;
        } else {
            symbol = "-";
            doubleValue = -(time / 1000D) + 0.99999999999999D;
        }
        hour = (int) (doubleValue / 3600D);
        min = (int) (doubleValue % 3600D / 60D);
        sec = (int) (doubleValue % 3600D % 60D);
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
            doubleValue = time / 1000D;
        } else {
            symbol = "-";
            doubleValue = -(time / 1000D);
        }
        hour = (int) (doubleValue / 3600D);
        min = (int) (doubleValue % 3600D / 60D);
        sec = (int) (doubleValue % 3600D % 60D);
        return (hour == 0) ? String.format(COUNTDOWN_MMSS, symbol, min, sec) : String.format(COUNTDOWN_HHMMSS, symbol, hour, min, sec);
    }

    /**
     * 時間を表示文字列に整形する。
     *
     * @param format
     * @param time
     * @return
     */
    public static String format(String format, long time) {
        String _symbol = "";
        double _doubleValue;

        if (time >= 0) {
            _doubleValue = time / 1000D;
        } else {
            _symbol = "-";
            _doubleValue = -(time / 1000D);
        }
        int _hour = (int) (_doubleValue / 3600D);
        int _min = (int) (_doubleValue % 3600D / 60D);
        int _sec = (int) (_doubleValue % 3600D % 60D);
        return String.format(format, _symbol, _hour, _min, _sec);
    }

    /**
     * Date型からオフセット付きの日時形式の文字列を返す。
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }
    
    /**
     * Date型からオフセット付きの日時形式の文字列を返す。
     * 
     * @param date
     * @param pattern
     * @return 
     */
    public static String format(Date date, String pattern) {
        if (Objects.isNull(date)) {
            return "";
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * LocalDateTime型からオフセット付きの日時形式の文字列を返す。
     * @param date
     * @return
     */
    public static String format(LocalDateTime date) {
        if (Objects.isNull(date)) {
            return "";
        }
        return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }
    
      /**
     * 日時形式の文字列をDate型に変換する。
     * 
     * @param value 文字列
     * @return Date
     */
    public static Date parse(String value) {
        return parse(value, DATE_PATTERN);
    }
    
    /**
     * 日時形式の文字列をDate型に変換する。
     * 
     * @param value
     * @param pattern
     * @return 
     */
    public static Date parse(String value, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(value);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 日時形式の文字列をDate型に変換する。
     * 
     * @param value 文字列
     * @return Date
     */
    public static Date parseJson(String value) {
        try {
            return new SimpleDateFormat(JSON_DATETIME_FORMAT).parse(value);
        } catch (ParseException ex) {
        }
        return null;
    }

    /**
     * Dateのタイムゾーンを変換する。
     * 
     * @param date
     * @param timeZoneId
     * @return 
     * @throws Exception
     */
    public static Date convertTimeZone(Date date, String timeZoneId) throws Exception {
        SimpleDateFormat src = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        src.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        String dateStr = src.format(date);
        SimpleDateFormat dest = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return dest.parse(dateStr);
    }
}
