/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.utility;

import java.util.Date;
import java.util.Objects;

/**
 * 日時計算処理
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.21.Fri
 */
public class DateUtils {

    /**
     * 日時の差分の時間を算出する
     *
     * @param dateTo 終了日時
     * @param dateFrom 開始日時
     * @return 時間の差分(ms)
     */
    public static long differenceOfDateTimeMillsec(Date dateTo, Date dateFrom){
        if (Objects.isNull(dateTo) || Objects.isNull(dateFrom)) {
            return 0;
        }
        long dateTimeTo = dateTo.getTime();
        long dateTimeFrom = dateFrom.getTime();
        return dateTimeTo - dateTimeFrom;
    }

}
