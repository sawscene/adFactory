/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.common;

import java.time.LocalDateTime;
import java.time.LocalTime;
import jp.adtekfuji.andon.enumerate.ContentTypeEnum;
import jp.adtekfuji.andon.enumerate.DisplayModeEnum;

/**
 * 進捗モニター設定定数
 *
 * @author fu-kato
 */
public class Constants {
    
    // 設定ファイル
    public static final String AUTO_ENDTIME = "calcEndTime";

    // 時間軸スケールの「1週間」と「1カ月」を表示するか？
    public static final String ENABLE_LONG_TIME = "enableLongTime";
    public static final String ENABLE_LONG_TIME_DEFAULT = "false";

    // 時間軸設定の表示/非表示
    public static final String VISIBLE_TIME_AXIS = "visibleTimeAxis";
    public static final String VISIBLE_TIME_AXIS_DEFAULT = "true";

    // 入力制限用正規表現(簡易版)
    public static final String REGEX_INTEGER = "\\d+";
    public static final String REGEX_DOUBLE = "(\\d+)|(\\.\\d+)|(\\d+\\.)|(\\d+\\.\\d+)";

    // アジェンダモニター設定 初期値
    // 条件表示
    public static final LocalDateTime AGENDA_DATE = LocalDateTime.now();
    public static final LocalTime AGENDA_START_TIME = LocalTime.of(8, 0);
    public static final LocalTime AGENDA_END_TIME = LocalTime.of(19, 0);
    public static final DisplayModeEnum AGENDA_MODE = DisplayModeEnum.LINE;
    public static final long AGENDA_UPDATE_INTERVEL = 1;

    // 画面設定
    public static final int AGENDA_DISPLAY_NUMBER = 1;
    public static final int AGENDA_COLUMN_COUNT = 5;
    public static final boolean AGENDA_ONLY_PLANED = false;
    public static final ContentTypeEnum AGENDA_CONTENT = ContentTypeEnum.WORKFLOW_NAME;
    public static final int AGENDA_TIME_UNIT = 30;
    public static final boolean AGENDA_TOGGL_EPAGES = true;
    public static final int AGENDA_TOGGLE_TIME = 10;
    public static final boolean AGENDA_AUTO_SCROLL = false;
    public static final LocalTime AGENDA_SCROLL_UNIT = LocalTime.of(3, 0);

    // フォントサイズ
    public static final double AGENDA_TITLE_SIZE = 17.0;
    public static final double AGENDA_COLUMN_SIZE = 17.0;
    public static final double AGENDA_SUB_COLUMN_SIZE = 20.0;
    public static final double AGENDA_ITEM_SIZE = 15.0;
    public static final double AGENDA_ZOOMBAR_SIZE = 17.0;

    // 更新間隔の設定範囲
    public static final long AGENDA_MIN_INTERVAL = 1;
    public static final long AGENDA_MAX_INTERVAL = 60;
    public static final long AGENDA_MIN_INTERVAL_WEEKLY = 10;
    public static final long AGENDA_MAX_INTERVAL_WEEKLY = 60;
}
