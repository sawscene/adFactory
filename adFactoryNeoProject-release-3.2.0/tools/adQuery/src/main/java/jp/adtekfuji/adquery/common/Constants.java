package jp.adtekfuji.adquery.common;

/**
 * 定数クラス
 *
 * @author s-heya
 */
public class Constants {
    //public static final Double SCREEN_MIN_WIDTH = 1024.0;
    //public static final Double SCREEN_MIN_HEIGHT = 700.0;
    //public static final String BUNDLE_NAME = "jp.adtekfuji.adquery.locale.locale";

    // 設定ファイル用定数
    public static final String QUERY_PROPETRY_NAME = "adQuery";
    public static final String PROPERTIES_EXT = ".properties";
    
    public static final String CSV_FILENAME_KEY = "report_csv_filename";
    public static final String CSV_FILENAME_DEFAULT = "adfactory_report.csv";
    public static final String CSV_FILENAME_SUMMARY_KEY = "report_csv_filename_summary";
    public static final String CSV_FILENAME_SUMMARY_DEFAULT = "adfactory_report_summary.csv";
    public static final String CSV_ENCODE_KEY = "report_csv_encode_type";
    public static final String CSV_ENCODE_DEFAULT = "Shift_JIS";
    
    public static final String REPORT_OUT_SEARCH_MAX_KEY = "reportOutSearchMax";
    public static final String REPORT_OUT_SEARCH_MAX_DEFAULT = "200";
    
    public static final String REPORT_OUT_ACTUAL_MAX_KEY = "actualReportOutMax";
    public static final String REPORT_OUT_ACTUAL_MAX_DEFAULT = "1000";
    
    public static final String ENABLE_MANAGER = "enableManager";
    public static final String ENABLE_MONITOR = "enableMonitor";
    public static final String ENABLE_WORKER = "enableWorker";
    public static final String ENABLE_AGENDA_MONITOR = "enableAgendaMonitor";

    public static final String LOCALE_ID = "locale";
    public static final String SERVER = "server";
    public static final String REST_CONNECT_TIMEOUT = "restConnectTimeout";
    public static final String REST_MAX_RETRY = "restMaxRetry";
    public static final String REST_BASEPATH = "/adFactoryServer/rest";
    public static final String REST_SOFTWAREUPDATE = "/system/softwareupdate";

    public static final String DEF_SERVER = "https://localhost:8080";
    public static final String DEF_LOCALE_ID = "ja_JP";
    public static final String DEF_REST_CONNECT_TIMEOUT = "3000";
    public static final String DEF_REST_MAX_RETRY = "3";
}
