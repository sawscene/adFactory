/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.common;

/**
 * 定数
 *
 * @author nar-nakamura
 */
public class Constants {

    // 更新間隔(分)
    public static final String UPDATE_INTERVAL = "updateInterval";
    public static final String UPDATE_INTERVAL_DEFAULT = "1";
    // サーバーアドレス
    public static final String SERVER_ADDRESS = "serverAddress";
    public static final String SERVER_ADDRESS_DEFAULT = "localhost";

    // 完了予定日のプロパティ名
    public static final String PROP_DEAD_LINE = "propDeadLine";
    public static final String PROP_DEAD_LINE_DEFAULT = "完了予定日";
    // 予備日数のプロパティ名
    public static final String PROP_RESERVE_DAYS = "propReserveDays";
    public static final String PROP_RESERVE_DAYS_DEFAULT = "予備日数";
    // グループNoのプロパティ名
    public static final String PROP_GROUP_NO = "propGroupNo";
    public static final String PROP_GROUP_NO_DEFAULT = "グループNo";
    // 追加情報1のプロパティ名
    public static final String PROP_INFO1 = "propInfo1";
    public static final String PROP_INFO1_DEFAULT = "プロジェクトNo";
    // 追加情報2のプロパティ名
    public static final String PROP_INFO2 = "propInfo2";
    public static final String PROP_INFO2_DEFAULT = "ユーザー名";

    // 休憩時間名(カンマ区切りで列挙)　※.例「休憩1,休憩2,休憩3」
    public static final String BREAKTIME_NAMES = "breaktimes";
    public static final String BREAKTIME_NAMES_DEFAULT = "";
}
