/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.utility;

/**
 * ツール
 *
 * @author s-heya
 */
public class MonitorTools {
    private static final String COUNTDOWN_MMSS = "%s%02d:%02d";
    private static final String COUNTDOWN_HHMMSS = "%s%02d:%02d:%02d";

    /**
     * タクトタイムを表示文字列に整形する。
     *
     * @param time
     * @return
     */
    public static String formatTaktTime(long time) {
        double doubleValue;
        int hour;
        int min;
        int sec;
        String symbol;

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
        return String.format(COUNTDOWN_HHMMSS, symbol, hour, min, sec);
    }
}
