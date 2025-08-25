/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

/**
 * 生産管理ナビの設定情報
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
public class ProductionNaviPropertyConstants {

    public static final String PROPERTY_TAG = "ProductionNavi";
    public static final String PROPERTY_FILE = "adFactoryProductionNavi.properties";

    public static final String WORKFLOW_NAME = "工程順名";
    public static final String MODEL_NAME = "モデル名";
    public static final String ORDER_NUMBER = "オーダー番号";
    public static final String SERIAL_NUMBER = "シリアル";

    public static final String TITLE_SEPARATOR = ",";

    public static final String MASTER_DATA_SETTING_TITLE = new StringBuilder()
            .append(WORKFLOW_NAME)
            .append(TITLE_SEPARATOR)
            .append(MODEL_NAME)
            .append(TITLE_SEPARATOR)
            .append(ORDER_NUMBER)
            .append(TITLE_SEPARATOR)
            .append(SERIAL_NUMBER)
            .toString();

    public final static String MASTER_XLS_COL = "A,B,C,D,E,F,G,H,I,J,K,L,M,N";

    public static final String KEY_MASTER_CSV_FILE_ENCODE = "master.csv.file.encode";
    public static final String MASTER_CSV_FILE_ENCODE = "S-JIS,UTF-8,UTF-16";

    public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DEFAULT_DATETIME_FORMAT = DEFAULT_DATE_FORMAT.concat(" ").concat(DEFAULT_TIME_FORMAT);

    /**
     * 表示設定
     */
    public static final String KEY_SETTING_TITLE = "setting.title";
    public static final String INIT_SETTING_TITLE = MASTER_DATA_SETTING_TITLE;
    public static final String MASTER_SETTING_TITLE = "setting.title.master";
    public static final String KEY_SETTING_SCEDULE_CELL_SIZE = "setting.schedule.cellSize";
    public static final String KEY_SETTING_ORGANIZATION = "setting.organization";

    public static final String KEY_SETTING_HOLIDAY_COLOR_CHAR = "setting.color.holiday.character";
    public static final String INIT_SETTING_HOLIDAY_COLOR_CHAR = "#FFFFFF";
    public static final String KEY_SETTING_HOLIDAY_COLOR_BACK = "setting.color.holiday.background";
    public static final String INIT_SETTING_HOLIDAY_COLOR_BACK = "#FF0000";
    public static final String KEY_SETTING_PLANS_COLOR_CHAR = "setting.color.plans.character";
    public static final String INIT_SETTING_PLANS_COLOR_CHAR = "#FFFFFF";
    public static final String KEY_SETTING_PLANS_COLOR_BACK = "setting.color.plans.background";
    public static final String INIT_SETTING_PLANS_COLOR_BACK = "#808080";
    public static final String KEY_SETTING_NONE_COLOR_CHAR = "setting.color.none.character";
    public static final String INIT_SETTING_NONE_COLOR_CHAR = "#000000";
    public static final String KEY_SETTING_NONE_COLOR_BACK = "setting.color.none.background";
    public static final String INIT_SETTING_NONE_COLOR_BACK = "#FFFFFF";

    /**
     * 勤務表
     */
    public static final String MASTER_HOLIDAY_NAME = "roster.holiday.name.master";
    public static final String INIT_MASTER_HOLIDAY_NAME = "有休,半休,出張,教育,研修";
    public static final String SET_ROSTER_TARGET_DATE = "roster.target.date";
    public static final String SET_ROSTER_TARGET_DAY = "roster.target.day";
    public static final String SET_ROSTER_TARGET_INIT_MODE = "roster.target.init.mode";
    public static final String SET_ROSTER_TARGET_ORGANIZATION = "roster.target.orgaization";

    public static final String KEY_ROSTER_COLOR_SUNDAY_BACK = "roster.color.week.sunday.back";
    public static final String KEY_ROSTER_COLOR_SUNDAY_CHAR = "roster.color.week.sunday.char";
    public static final String KEY_ROSTER_COLOR_MONDAY_BACK = "roster.color.week.monday.back";
    public static final String KEY_ROSTER_COLOR_MONDAY_CHAR = "roster.color.week.monday.char";
    public static final String KEY_ROSTER_COLOR_TUESDAY_BACK = "roster.color.week.tuesday.back";
    public static final String KEY_ROSTER_COLOR_TUESDAY_CHAR = "roster.color.week.tuesday.char";
    public static final String KEY_ROSTER_COLOR_WEDNESDAY_BACK = "roster.color.week.wednesday.back";
    public static final String KEY_ROSTER_COLOR_WEDNESDAY_CHAR = "roster.color.week.wednesday.char";
    public static final String KEY_ROSTER_COLOR_THURSDAY_BACK = "roster.color.week.thursday.back";
    public static final String KEY_ROSTER_COLOR_THURSDAY_CHAR = "roster.color.week.thursday.char";
    public static final String KEY_ROSTER_COLOR_FRIDAY_BACK = "roster.color.week.friday.back";
    public static final String KEY_ROSTER_COLOR_FRIDAY_CHAR = "roster.color.week.friday.char";
    public static final String KEY_ROSTER_COLOR_SATURDAY_BACK = "roster.color.week.saturday.back";
    public static final String KEY_ROSTER_COLOR_SATURDAY_CHAR = "roster.color.week.saturday.char";

    /**
     * 予定表
     */
    public static final String IMPORT_PLANS_CSV_PATH = "plans.import.csv.path";// インポートパス設定
    public static final String IMPORT_PLANS_XLS_PATH = "plans.import.excel.path";// インポートパス設定
    public static final String IMPORT_PLANS_SELECT_TAB = "plans.import.select.tab";

    public static final String IMPORT_PLANS_DATE_FORMAT = "plans.import.date.format";
    public static final String IMPORT_PLANS_TIME_FORMAT = "plans.import.time.format";
    public static final String IMPORT_PLANS_DT_FORMAT = "plans.import.datetime.format";

    // 予定表のCSVのキー 
    public static final String KEY_PLANS_CSV_ENCODE = "plans.csv.encode";
    public static final String KEY_PLANS_CSV_LINE = "plans.csv.plineStart";
    public static final String KEY_PLANS_CSV_PLANS_NAME = "plans.csv.plansName";
    public static final String KEY_PLANS_CSV_START_DT = "plans.csv.startDatetime";
    public static final String KEY_PLANS_CSV_STOP_DT = "plans.csv.stopDatetime";
    public static final String KEY_PLANS_CSV_ORGANIZATION = "plans.csv.Organization";

    // 予定表のExcelのキー 
    public static final String KEY_PLANS_XLS_SHEET_NAME = "plans.excel.sheetName";
    public static final String KEY_PLANS_XLS_ORGANIZATION = "plans.excel.Organization";
    public static final String KEY_PLANS_XLS_LINE = "plans.excel.lineStart";
    public static final String KEY_PLANS_XLS_START_DT = "plans.excel.startDatetime";
    public static final String KEY_PLANS_XLS_STOP_DT = "plans.excel.stopDatetime";
    public static final String KEY_PLANS_XLS_PLANS_NAME = "plans.excel.plansName";

    // 予定表のCSVの初期値
    public static final String INIT_PLANS_CSV_ENCODE = "UTF-8";
    public static final String INIT_PLANS_CSV_LINE = "2";
    public static final String INIT_PLANS_CSV_PLANS_NAME = "1";
    public static final String INIT_PLANS_CSV_START_DT = "2";
    public static final String INIT_PLANS_CSV_STOP_DT = "3";
    public static final String INIT_PLANS_CSV_ORGANIZATION = "4";

    // 予定表のExcelの初期値
    public static final String INIT_PLANS_XLS_SHEET_NAME = "予定表";
    public static final String INIT_PLANS_XLS_LINE = "2";
    public static final String INIT_PLANS_XLS_PLANS_NAME = "A";
    public static final String INIT_PLANS_XLS_START_DT = "B";
    public static final String INIT_PLANS_XLS_STOP_DT = "C";
    public static final String INIT_PLANS_XLS_ORGANIZATION = "D";

    /**
     * 休日表
     */
    public static final String IMPORT_HOLIDAY_CSV_PATH = "holiday.import.csv.path";// インポートパス設定
    public static final String IMPORT_HOLIDAY_XLS_PATH = "holiday.import.excel.path";// インポートパス設定
    public static final String IMPORT_HOLIDAY_SELECT_TAB = "holiday.import.select.tab";

    public static final String IMPORT_HOLIDAY_DATE_FORMAT = "holiday.import.date.format";
    public static final String IMPORT_HOLIDAY_TIME_FORMAT = "holiday.import.time.format";
    public static final String IMPORT_HOLIDAYDT_FORMAT = "holiday.import.datetime.format";

    public static final String INIT_HOLIDAY_CSV_ENCODE = "UTF-8";
    public static final String INIT_HOLIDAY_CSV_LINE = "2";
    public static final String INIT_HOLIDAY_CSV_HOLIDAY = "1";
    public static final String INIT_HOLIDAY_CSV_NAME = "2";

    public static final String INIT_HOLIDAY_XLS_SHEET_NAME = "休日表";
    public static final String INIT_HOLIDAY_XLS_LINE = "2";
    public static final String INIT_HOLIDAY_XLS_HOLIDAY = "A";
    public static final String INIT_HOLIDAY_XLS_NAME = "B";

    public static final String SELECT_PROD_DEFAULT_DRIVE = "C:";
    public static final String SELECT_PROD_DEFAULT_PATH = "adFactory_IN";
    public static final String IMPORT_PROD_DATE_FORMAT = "production.import.date.format";
    public static final String SELECT_PROD_CSV_PATH = "select.production.import.csv.path";// インポートパス設定
    public static final String SELECT_PROD_XLS_PATH = "select.production.import.excel.path";// インポートパス設定
    public static final String SELECT_PROD_TAB = "select.production.import.tab";// インポートパス設定

    public static final String PROD_AUTO_IMPORT_POLLING_TIME = "production.auto.import.pollingTime"; // 自動読込ポーリング時間
    public static final String DEF_PROD_AUTO_IMPORT_POLLING_TIME = "60"; // 自動読込ポーリング時間デフォルト

    // すでにカンバンが存在するなら無視
    public static final String SELECT_PROD_IGNORE_EXISTING = "select.production.ignore.existing";

    // カンバン
    public static final String INIT_PROD_KANBAN_CSV_FILE = "kanban.csv";
    public static final String INIT_PROD_KANBAN_CSV_ENCODE = "UTF-8";
    public static final String INIT_PROD_KANBAN_CSV_LINE = "2";
    public static final String INIT_PROD_KANBAN_XLS_SHEET = "カンバン";
    public static final String INIT_PROD_KANBAN_XLS_LINE = "2";

    // カンバン情報 - カンバン階層名
    public static final String INIT_PROD_KANBAN_CSV_HIERA_NAME = "1";
    public static final String INIT_PROD_KANBAN_XLS_HIERA_NAME = "A";
    // カンバン情報 - カンバン名
    public static final String INIT_PROD_KANBAN_CSV_NAME = "2";
    public static final String INIT_PROD_KANBAN_XLS_NAME = "B";
    // カンバン情報 - 工程順名
    public static final String INIT_PROD_KANBAN_CSV_WF_NAME = "3";
    public static final String INIT_PROD_KANBAN_XLS_WF_NAME = "C";
    // カンバン情報 - 工程順版数
    public static final String INIT_PROD_KANBAN_CSV_WF_REV = "4";
    public static final String INIT_PROD_KANBAN_XLS_WF_REV = "D";
    // カンバン情報 - モデル名
    public static final String INIT_PROD_KANBAN_CSV_MODEL_NAME = "5";
    public static final String INIT_PROD_KANBAN_XLS_MODEL_NAME = "E";
    // カンバン情報 - 開始予定日時
    public static final String INIT_PROD_KANBAN_CSV_START_DT = "6";
    public static final String INIT_PROD_KANBAN_XLS_START_DT = "F";
    // カンバン情報 - ロット生産
    //public static final String INIT_PROD_KANBAN_CSV_PRODUCTION_TYPE = "7";
    //public static final String INIT_PROD_KANBAN_XLS_PRODUCTION_TYPE = "G";
    // カンバン情報 - ロット数量
    //public static final String INIT_PROD_KANBAN_CSV_LOT_NUM = "8";
    //public static final String INIT_PROD_KANBAN_XLS_LOT_NUM = "H";

    // カンバンプロパティ
    public static final String INIT_PROD_KANBANPROP_CSV_FILE = "kanban_property.csv";
    public static final String INIT_PROD_KANBANPROP_CSV_ENCODE = "UTF-8";
    public static final String INIT_PROD_KANBANPROP_CSV_LINE = "2";
    public static final String INIT_PROD_KANBANPROP_XLS_SHEET = "カンバンプロパティ";
    public static final String INIT_PROD_KANBANPROP_XLS_LINE = "2";

    // カンバンプロパティ情報 - フォーマット
    public static final String INIT_PROD_KANBANPROP_FORMAT_NO = "1";

    // カンバンプロパティ情報 - カンバン名
    public static final String INIT_PROD_KANBANPROP_CSV_NAME = "1";
    public static final String INIT_PROD_KANBANPROP_XLS_NAME = "A";
    // カンバンプロパティ情報 - プロパティ名
    public static final String INIT_PROD_KANBANPROP_CSV_PROP_NAME = "2";
    public static final String INIT_PROD_KANBANPROP_XLS_PROP_NAME = "B";
    // カンバンプロパティ情報 - 型
    public static final String INIT_PROD_KANBANPROP_CSV_PROP_TYPE = "3";
    public static final String INIT_PROD_KANBANPROP_XLS_PROP_TYPE = "C";
    // カンバンプロパティ情報 - 値
    public static final String INIT_PROD_KANBANPROP_CSV_PROP_VALUE = "4";
    public static final String INIT_PROD_KANBANPROP_XLS_PROP_VALUE = "D";

    // カンバンプロパティ情報 (フォーマット２) - ヘッダー行
    public static final String INIT_PROD_KANBANPROP_F2_CSV_HEADER_ROW = "1";
    public static final String INIT_PROD_KANBANPROP_F2_XLS_HEADER_ROW = "1";
    // カンバンプロパティ情報 (フォーマット２) - 読み込み開始行
    public static final String INIT_PROD_KANBANPROP_F2_CSV_START_ROW = "2";
    public static final String INIT_PROD_KANBANPROP_F2_XLS_START_ROW = "2";
    // カンバンプロパティ情報 (フォーマット２) - カンバン名
    public static final String INIT_PROD_KANBANPROP_F2_CSV_KANBAN_NAME = "1";
    public static final String INIT_PROD_KANBANPROP_F2_XLS_KANBAN_NAME = "A";
    // カンバンプロパティ情報 (フォーマット２) - プロパティ値
    public static final String INIT_PROD_KANBANPROP_F2_CSV_PROP_VALUE = "2";
    public static final String INIT_PROD_KANBANPROP_F2_XLS_PROP_VALUE = "B";

    // 工程カンバン
    public static final String INIT_PROD_WORKKANBAN_CSV_FILE = "work_kanban.csv";
    public static final String INIT_PROD_WORKKANBAN_CSV_ENCODE = "UTF-8";
    public static final String INIT_PROD_WORKKANBAN_CSV_LINE = "2";
    public static final String INIT_PROD_WORKKANBAN_XLS_SHEET = "工程カンバン";
    public static final String INIT_PROD_WORKKANBAN_XLS_LINE = "2";

    // 工程カンバン情報 - カンバン名
    public static final String INIT_PROD_WORKKANBAN_CSV_NAME = "1";
    public static final String INIT_PROD_WORKKANBAN_XLS_NAME = "A";
    // 工程カンバン情報 - 工程の番号
    public static final String INIT_PROD_WORKKANBAN_CSV_NUM = "2";
    public static final String INIT_PROD_WORKKANBAN_XLS_NUM = "B";
    // 工程カンバン情報 - スキップフラグ
    public static final String INIT_PROD_WORKKANBAN_CSV_SKIP_FLAG = "3";
    public static final String INIT_PROD_WORKKANBAN_XLS_SKIP_FLAG = "C";
    // 工程カンバン情報 - 開始予定日時
    public static final String INIT_PROD_WORKKANBAN_CSV_START_DT = "4";
    public static final String INIT_PROD_WORKKANBAN_XLS_START_DT = "D";
    // 工程カンバン情報 - 完了予定日時
    public static final String INIT_PROD_WORKKANBAN_CSV_COMP_DT = "5";
    public static final String INIT_PROD_WORKKANBAN_XLS_COMP_DT = "E";
    // 工程カンバン情報 - 組織識別名
    public static final String INIT_PROD_WORKKANBAN_CSV_USER = "6";
    public static final String INIT_PROD_WORKKANBAN_XLS_USER = "F";
    // 工程カンバン情報 - 設備識別名
    public static final String INIT_PROD_WORKKANBAN_CSV_EQUIPMENTS = "7";
    public static final String INIT_PROD_WORKKANBAN_XLS_EQUIPMENTS = "G";
    // 工程カンバン情報 - 工程名 2019/12/18 工程名項目の追加対応
    public static final String INIT_PROD_WORKKANBAN_CSV_WKNAME = "8";
    public static final String INIT_PROD_WORKKANBAN_XLS_WKNAME = "H";

    // 工程カンバン情報 - タクトタイム 2020/02/20 MES連携 タクトタイム追加
    public static final String INIT_PROD_WORKKANBAN_CSV_TACTTIME = "9";
    public static final String INIT_PROD_WORKKANBAN_XLS_TACTTIME = "I";
    
    // 工程カンバンプロパティ
    public static final String INIT_PROD_WORKKNBANPROP_CSV_FILE = "work_kanban_property.csv";
    public static final String INIT_PROD_WORKKNBANPROP_CSV_ENCODE = "UTF-8";
    public static final String INIT_PROD_WORKKANBANPROP_CSV_LINE = "2";
    public static final String INIT_PROD_WORKKNBANPROP_XLS_SHEET = "工程カンバンプロパティ";
    public static final String INIT_PROD_WORKKANBANPROP_XLS_LINE = "2";

    // 更新用工程カンバンプロパティ
    public static final String INIT_PROD_UPDATEWORKKNBANPROP_CSV_FILE = "update_work_kanban_property.csv";

    // 工程カンバンプロパティ情報 - フォーマット
    public static final String INIT_PROD_WORKKANBANPROP_FORMAT_NO = "1";

    // 工程カンバンプロパティ情報 - カンバン名
    public static final String INIT_PROD_WORKKNBANPROP_CSV_NAME = "1";
    public static final String INIT_PROD_WORKKNBANPROP_XLS_NAME = "A";
    // 工程カンバンプロパティ情報 - 工程の番号
    public static final String INIT_PROD_WORKKNBANPROP_CSV_NUM = "2";
    public static final String INIT_PROD_WORKKNBANPROP_XLS_NUM = "B";
    // 工程カンバンプロパティ情報 - プロパティ名
    public static final String INIT_PROD_WORKKNBANPROP_CSV_PROP_NAME = "3";
    public static final String INIT_PROD_WORKKNBANPROP_XLS_PROP_NAME = "C";
    // 工程カンバンプロパティ情報 - 型
    public static final String INIT_PROD_WORKKNBANPROP_CSV_PROP_TYPE = "4";
    public static final String INIT_PROD_WORKKNBANPROP_XLS_PROP_TYPE = "D";
    // 工程カンバンプロパティ情報 - 値
    public static final String INIT_PROD_WORKKNBANPROP_CSV_ROP_VALUE = "5";
    public static final String INIT_PROD_WORKKNBANPROP_XLS_ROP_VALUE = "E";
    // 工程カンバンプロパティ情報 - 工程名
    public static final String INIT_PROD_WORKKNBANPROP_CSV_WKNAME = "6";
    public static final String INIT_PROD_WORKKNBANPROP_XLS_WKNAME = "F";
    
    // 工程カンバンプロパティ情報 (フォーマット２) - ヘッダー行
    public static final String INIT_PROD_WORKKANBANPROP_F2_CSV_HEADER_ROW = "1";
    public static final String INIT_PROD_WORKKANBANPROP_F2_XLS_HEADER_ROW = "1";
    // 工程カンバンプロパティ情報 (フォーマット２) - 読み込み開始行
    public static final String INIT_PROD_WORKKANBANPROP_F2_CSV_START_ROW = "2";
    public static final String INIT_PROD_WORKKANBANPROP_F2_XLS_START_ROW = "2";
    // 工程カンバンプロパティ情報 (フォーマット２) - カンバン名
    public static final String INIT_PROD_WORKKANBANPROP_F2_CSV_KANBAN_NAME = "1";
    public static final String INIT_PROD_WORKKANBANPROP_F2_XLS_KANBAN_NAME = "A";
    // 工程カンバンプロパティ情報 (フォーマット２) - 工程名
    public static final String INIT_PROD_WORKKANBANPROP_F2_CSV_WKNAME = "2";
    public static final String INIT_PROD_WORKKANBANPROP_F2_XLS_WKNAME = "B";
    // 工程カンバンプロパティ情報 (フォーマット２) - 工程の番号
    public static final String INIT_PROD_WORKKANBANPROP_F2_CSV_NUM = "3";
    public static final String INIT_PROD_WORKKANBANPROP_F2_XLS_NUM = "C";
    // 工程カンバンプロパティ情報 (フォーマット２) - プロパティ
    public static final String INIT_PROD_WORKKANBANPROP_F2_CSV_PROP_VALUE = "4";
    public static final String INIT_PROD_WORKKANBANPROP_F2_XLS_PROP_VALUE = "D";

    // カンバンステータス
    public static final String INIT_PROD_KANBAN_STATUS_CSV_FILE = "kanban_status.csv";
    public static final String INIT_PROD_KANBAN_STATUS_CSV_ENCODE = "UTF-8";
    public static final String INIT_PROD_KANBAN_STATUS_CSV_LINE = "2";
    public static final String INIT_PROD_KANBAN_STATUS_XLS_SHEET = "カンバンステータス";
    public static final String INIT_PROD_KANBAN_STATUS_XLS_LINE = "2";

    // カンバンステータス情報 - カンバン名
    public static final String INIT_PROD_KANBAN_STATUS_CSV_NAME = "1";
    public static final String INIT_PROD_KANBAN_STATUS_XLS_NAME = "A";
    // カンバンステータス情報 - ステータス
    public static final String INIT_PROD_KANBAN_STATUS_CSV_STATUS_NAME = "2";
    public static final String INIT_PROD_KANBAN_STATUS_XLS_STATUS_NAME = "B";

    // 製品
    public static final String INIT_PROD_PRODUCT_CSV_FILE = "product.csv";
    public static final String INIT_PROD_PRODUCT_CSV_ENCODE = "UTF-8";
    public static final String INIT_PROD_PRODUCT_CSV_LINE = "2";
    public static final String INIT_PROD_PRODUCT_XLS_SHEET = "製品";
    public static final String INIT_PROD_PRODUCT_XLS_LINE = "2";
    // 製品情報 - ユニークID
    public static final String INIT_PROD_PRODUCT_CSV_UNIQUE_ID = "1";
    public static final String INIT_PROD_PRODUCT_XLS_UNIQUE_ID = "A";
    // 製品情報 - カンバン名
    public static final String INIT_PROD_PRODUCT_CSV_KANBAN_NAME = "2";
    public static final String INIT_PROD_PRODUCT_XLS_KANBAN_NAME = "B";

    //カンバン注入
    public static final String SELECTED_KANBAN = "selectedKanban";
    public static final String IS_DRAGGED = "isDragged";
    public static final String DRAGGED_ID = "draggedId";

    // 作業計画：カンバン階層ID
    public static final String SEARCH_WP_KANBAN_HIERARCHY_ID = "search.work.plan.kanban.hierarchy.id";
    // 作業計画：カンバン名
    public static final String SEARCH_WP_KANBA_NAME = "search.work.plan.kanban.name";
    // 作業計画：モデル名
    public static final String SEARCH_WP_MODEL_NAME = "search.work.plan.model.name";
    // 作業計画：ステータス
    public static final String SEARCH_WP_KANBAN_STATUS = "search.work.plan.kanban.status";
    // 作業計画：検索開始日
    public static final String SEARCH_WP_START_DATE = "search.work.plan.start.date";
    // 作業計画：検索終了日
    public static final String SEARCH_WP_STOP_DATE = "search.work.plan.stop.date";
    // 作業計画：工程の追従
    public static final String SELECT_WP_FOLLOWING = "select.work.plan.following";
    // 作業計画：スケールサイズ
    public static final String SELECT_WP_SCALE_SIZE = "select.work.plan.scale.size";
    // 作業計画：スケール位置
    public static final String SELECT_WP_SCALE_POSITION = "select.work.plan.scale.position";

    // 作業者管理：組織階層ID
    public static final String SEARCH_WORKER_ORGANIZATION_ID = "search.worker.organization.id";
    // 作業者管理：検索開始日
    public static final String SEARCH_WORKER_START_DATE = "search.worker.start.date";
    // 作業者管理：検索終了日
    public static final String SEARCH_WORKER_STOP_DATE = "search.worker.stop.date";
    // 作業者管理：工程の追従
    public static final String SELECT_WORKER_FOLLOWING = "select.worker.following";
    // 作業者管理：スケールサイズ
    public static final String SELECT_WORKER_SCALE_SIZE = "select.worker.scale.size";
    // 作業者管理：スケール位置
    public static final String SELECT_WORKER_SCALE_POSITION = "select.worker.scale.position";
}
