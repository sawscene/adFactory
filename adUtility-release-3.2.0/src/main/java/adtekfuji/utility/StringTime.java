/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;

/**
 * 時間変換用クラス
 *
 * @author ta.ito
 */
public class StringTime {

    private final static String TIME_FORMAT = "%02d:%02d:%02d";

    private final static String TIME_SPLIT = ":";
    private final static String TIME_REGEX = "\\d+:\\d+:\\d+";

    private StringTime() {
    }

    /**
     * ミリ秒を文字列「時:分:秒」に変換
     *
     * @param msec
     * @return
     */
    public static String convertMillisToStringTime(long msec) {
        boolean isMinus = false;
        if (msec < 0) {
            msec = Math.abs(msec);
            isMinus = true;
        }

        long hours = TimeUnit.MILLISECONDS.toHours(msec);
        msec -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(msec);
        msec -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(msec);

        StringBuilder sb = new StringBuilder();
        if (isMinus) {
            sb.append('-');
        }

        sb.append(String.format(TIME_FORMAT, hours, minutes, seconds));
        return sb.toString();
    }
    
    /**
     * 文字列「時:分:秒」をミリ秒に変換
     *
     * @param time
     * @return
     */
    public static long convertStringTimeToMillis(String time) {
        long msec = 0;
        try {
            String[] split = time.split(TIME_SPLIT);
            msec = msec + TimeUnit.HOURS.toMillis(Long.parseLong(split[0]));
            msec = msec + TimeUnit.MINUTES.toMillis(Long.parseLong(split[1]));
            msec = msec + TimeUnit.SECONDS.toMillis(Long.parseLong(split[2]));
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            LogManager.getLogger().fatal(ex);
            msec = 0;
        }

        return msec;
    }

    /**
     * 文字列「時:分:秒」がフォーマットと一致するか判定する
     *
     * @param time
     * @return
     */
    public static Boolean validStringTime(String time) {
        return time.matches(TIME_REGEX);
    }

    public static Date getFixedTime(Date targetDate, Date fixTime) {
        Calendar target = new Calendar.Builder().setInstant(targetDate).build();
        Calendar fix = new Calendar.Builder().setInstant(fixTime).build();
        target.set(Calendar.HOUR, target.get(Calendar.HOUR) + fix.get(Calendar.HOUR));
        target.set(Calendar.MINUTE, target.get(Calendar.MINUTE) + fix.get(Calendar.MINUTE));
        target.set(Calendar.SECOND, target.get(Calendar.SECOND) + fix.get(Calendar.SECOND));

        return target.getTime();
    }

    /**
     * Date型のオブジェクトに「時:分:秒」形式の文字列を加算する
     *
     * @param targetDate
     * @param fixTime
     * @return
     */
    public static Date getFixedDate(Date targetDate, String fixTime) {
        //[0]:時間
        //[1]:分
        //[2]:秒
        boolean sub = false;
        if (fixTime.indexOf('-') == 0) {
            sub = true;
            fixTime = fixTime.substring(1);
        }

        long millis = convertStringTimeToMillis(fixTime);
        if (sub) {
            millis = millis * -1;
        }

        if (0 == millis) {
            return targetDate;
        }

        return new Date(targetDate.getTime() + millis);
    }

    /**
     * Date型のオブジェクトをString型に変換する
     *
     * @param date
     * @return
     */
    public static String convertDateToString(Date date, String format) {
        if (Objects.isNull(date)) {
            return "";
        }
        return (new SimpleDateFormat(format)).format(date);
    }

    /**
     * String型のオブジェクトをDate型に変換する
     *
     * @param date
     * @return
     */
    public static Date convertStringToDate(String date, String format) {
        try {
            return (new SimpleDateFormat(format)).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
