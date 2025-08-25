/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.common;

/**
 * 定数
 *
 * @author s-heya
 */
public class Constants {

    public static final String LOG_FILE_NAME = "logFileName";
    public static final String CONF_FILE_NAME = "confFileName";

    public static final String FETCH_SIZE = "fetchSize";
    public static final String FETCH_SIZE_DEFAULT = "4";

    // アジェンダモニタの製品進捗の進捗表示
    public static final String AGENDA_PRODUCT_PROGRESS = "agendaProductProgress";
    public static final String DEF_AGENDA_PRODUCT_PROGRESS = "Work";
    public static final String WORK_PROGRESS = "Work";
    public static final String PRODUCTION_PROGRESS = "Production";
    // Liteモニターの進捗アラートを使用するか
    public static final String ENABLE_LITE_TAKT_TIME = "enableLiteTaktTime";
    public static final String ENABLE_LITE_TAKT_TIME_DEFAULT = "false";
}
