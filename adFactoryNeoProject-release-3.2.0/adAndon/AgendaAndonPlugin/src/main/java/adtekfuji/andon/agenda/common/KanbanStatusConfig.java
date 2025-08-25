/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.common;

import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.util.Objects;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン進捗情報の設定
 *
 * @author nar-nakamura
 */
public class KanbanStatusConfig {

    // カンバン進捗情報出力の設定
    public static final String KANBAN_STATUS_PROPERTY_TAG = "kanbanStatus";
    public static final String KANBAN_STATUS_PROPERTY_DEF = "kanbanStatus.properties";

    // 画面表示の有無
    private static final String ENABLE_VIEW_KEY = "enableView";
    private static final String ENABLE_VIEW_DEF = "true";
    // 進捗情報CSVファイル出力の有無
    private static final String ENABLE_KANBAN_STATUS_CSV_KEY = "enableKanbanStatusCsv";
    private static final String ENABLE_KANBAN_STATUS_CSV_DEF = "false";
    // カンバン進捗情報CSVファイルのパス
    private static final String KANBAN_STATUS_CSV_PATH_KEY = "kanbanStatusCsvPath";
    private static final String KANBAN_STATUS_CSV_PATH_DEF = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator + "kanbanStatus" + File.separator + "kanbanStatus.csv";
    // 付加情報1(kanban_info1)に出力するプロパティ名
    private static final String KANBAN_INFO_1_KEY = "kanbanInfo1";
    private static final String KANBAN_INFO_1_DEF = "";
    // 遅れ警告の閾値(％)
    private static final String KANBAN_WARNING_THRESHOLD_KEY = "kanbanWarningThreshold";
    private static final String KANBAN_WARNING_THRESHOLD_DEF = "6";
    // 工程進捗情報CSVファイルのパス
    private static final String WORK_STATUS_CSV_PATH_KEY = "workStatusCsvPath";
    private static final String WORK_STATUS_CSV_PATH_DEF = System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator + "kanbanStatus" + File.separator + "workStatus.csv";

    /**
     * カンバン進捗情報の設定を取得する。
     * @param fileName 
     */
    public static void load(String fileName) {
        Logger logger = LogManager.getLogger();
        try {
            AdProperty.load(KanbanStatusConfig.KANBAN_STATUS_PROPERTY_TAG, fileName);
            loadValues();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    /**
     * カンバン進捗情報の設定を保存する。
     */
    public static void store() {
        Logger logger = LogManager.getLogger();
        try {
            AdProperty.store(KanbanStatusConfig.KANBAN_STATUS_PROPERTY_TAG);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 設定を全て読み込む。(未設定の場合はデフォルト値が入る)
     */
    private static void loadValues() {
        // 画面表示の有無
        getEnableView();
        // 進捗情報CSVファイル出力の有無
        getEnableKanbanStatusCsv();
        // カンバン進捗情報CSVファイルのパス
        getKanbanStatusCsvPath();
        // 付加情報1(kanban_info1)に出力するプロパティ名
        getKanbanInfo1();
        // 遅れ警告の閾値(％)
        getKanbanWarningThreshold();
        // 工程進捗情報CSVファイルのパス
        getWorkStatusCsvPath();
    }

    /**
     * 画面表示の有無を取得する。
     *
     * @return 画面表示の有無
     */
    public static boolean getEnableView() {
        return Boolean.valueOf(getPropatyValue(ENABLE_VIEW_KEY, ENABLE_VIEW_DEF, true));
    }

    /**
     * 進捗情報CSVファイル出力の有無を取得する。
     *
     * @return 進捗情報CSVファイル出力の有無
     */
    public static boolean getEnableKanbanStatusCsv() {
        return Boolean.valueOf(getPropatyValue(ENABLE_KANBAN_STATUS_CSV_KEY, ENABLE_KANBAN_STATUS_CSV_DEF, true));
    }

    /**
     * カンバン進捗情報CSVファイルのパスを取得する。
     *
     * @return カンバン進捗情報CSVファイルのパス
     */
    public static String getKanbanStatusCsvPath() {
        return getPropatyValue(KANBAN_STATUS_CSV_PATH_KEY, KANBAN_STATUS_CSV_PATH_DEF, true);
    }

    /**
     * 付加情報1(kanban_info1)に出力するプロパティ名を取得する。
     *
     * @return 付加情報1(kanban_info1)に出力するプロパティ名
     */
    public static String getKanbanInfo1() {
        return getPropatyValue(KANBAN_INFO_1_KEY, KANBAN_INFO_1_DEF, false);
    }

    /**
     * 遅れ警告の閾値(％)を取得する。
     *
     * @return 遅れ警告の閾値(％)
     */
    public static long getKanbanWarningThreshold() {
        try {
            return Long.valueOf(getPropatyValue(KANBAN_WARNING_THRESHOLD_KEY, KANBAN_WARNING_THRESHOLD_DEF, true));
        } catch (Exception ex) {
            return Long.valueOf(KANBAN_WARNING_THRESHOLD_DEF);
        }
    }

    /**
     * 工程進捗情報CSVファイルのパスを取得する。
     *
     * @return 工程進捗情報CSVファイルのパス
     */
    public static String getWorkStatusCsvPath() {
        return getPropatyValue(WORK_STATUS_CSV_PATH_KEY, WORK_STATUS_CSV_PATH_DEF, true);
    }

    /**
     * 指定したキーのプロパティ値を取得する。
     * 存在しない場合はデフォルト値で設定する。
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return プロパティ値
     */
    private static String getPropatyValue(String key, String defaultValue, boolean isNotEmpty) {
        try {
            Properties properties = AdProperty.getProperties(KANBAN_STATUS_PROPERTY_TAG);
            if (Objects.isNull(properties)) {
                properties = AdProperty.getProperties();
            }

            if (!properties.containsKey(key)) {
                properties.setProperty(key, defaultValue);
            }

            String value = properties.getProperty(key);
            if (isNotEmpty && StringUtils.isEmpty(value)) {
                value = defaultValue;
            }
            return value;

        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
