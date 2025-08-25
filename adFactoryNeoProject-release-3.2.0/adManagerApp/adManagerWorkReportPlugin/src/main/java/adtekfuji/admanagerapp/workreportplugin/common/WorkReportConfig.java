/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.common;

import adtekfuji.property.AdProperty;
import java.io.File;
import java.util.Properties;

/**
 * 作業日報設定
 *
 * @author nar-nakamura
 */
public class WorkReportConfig {

    private static final String MY_DOCUMENT = System.getProperty("user.home") + File.separator + "Documents";// マイドキュメント
    private static final String BASE_FILENAME = "WorkReport";

    private static final String WORK_REPORT_OUTPUT_PATH_KEY = "workReportOutputPath";// 作業日報出力フォルダパス
    private static final String WORK_REPORT_BASE_FILENAME_KEY = "workReportBaseFilename";// 作業日報ベースファイル名

    public static final String WORK_REPORT_START_DATE = "workReportStartDate";
    public static final String WORK_REPORT_END_DATE = "workReportEndDate";

    private static final String WORK_REPORT_ROW_COLOR_DIRECT_WORK_KEY = "workReportRowColorDirectWork";// 直接作業の行の背景色
    private static final String WORK_REPORT_ROW_COLOR_DIRECT_WORK_DEFAULT = "#fffacd";// 直接作業の行の背景色 (#fffacd: lemonchiffon)

    private static final String WORK_REPORT_ROW_COLOR_NON_WORK_KEY = "workReportRowColorNonWork";// 中断時間の行の背景色
    private static final String WORK_REPORT_ROW_COLOR_NON_WORK_DEFAULT = "#ffffff";// 中断時間の行の背景色 (#ffffff: white)

    private static final String COLOR_TRANSPARENT = "transparent";// 透明色

    private static final String WORK_REPORT_TOOL_REFERENCE_TIME_KEY = "workReportToolReferenceTime";// 作業日報ツールの基準時間(H:mm)
    private static final String WORK_REPORT_TOOL_REFERENCE_TIME_DEFAULT = "0:00";

    private static final String WORK_REPORT_DIRECT_WORK_EDITABLE_KEY = "workReportDirectWorkEditable";// 直接工数の編集可否
    private static final String WORK_REPORT_DIRECT_WORK_EDITABLE_DEFAULT = "false";

    private static final String WORK_REPORT_WORK_NUM_VISIBLE_KEY = "workReportWorkNumVisible";// 作業数と製番を表示するか
    private static final String WORK_REPORT_WORK_NUM_VISIBLE_DEFAULT = "false";

    private static final String WORK_REPORT_TYPE_KEY = "workReportType";// 作業日報リストの表示形式
    private static final String WORK_REPORT_TYPE_DEFAULT = "order";

    public static final String WORK_REPORT_TYPE_KANBAN = "kanban";// 作業日報リストの表示形式(カンバン単位で表示)
    public static final String WORK_REPORT_TYPE_PRODUCTION = "production";// 作業日報リストの表示形式(製造番号単位で表示)

    public static final String WORK_REPORT_CSV_TYPE_KEY = "workReportCsvType";
    private static final String WORK_REPORT_CSV_TYPE_DEFAULT = "defalt";

    /**
     * 作業日報の出力パスを取得する。
     *
     * @return 作業日報の出力パス
     */
    public static String getWorkReportOutputPath() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(WORK_REPORT_OUTPUT_PATH_KEY)) {
                properties.setProperty(WORK_REPORT_OUTPUT_PATH_KEY, MY_DOCUMENT);
            }
            return properties.getProperty(WORK_REPORT_OUTPUT_PATH_KEY);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 作業日報の出力パスを設定する。
     *
     * @param value 作業日報の出力パス
     */
    public static void setWorkReportOutputPath(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(WORK_REPORT_OUTPUT_PATH_KEY, value);
    }

    /**
     * 作業日報ベースファイル名を取得する。
     *
     * @return 作業日報ベースファイル名
     */
    public static String getWorkReportBaseFilename() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(WORK_REPORT_BASE_FILENAME_KEY)) {
                properties.setProperty(WORK_REPORT_BASE_FILENAME_KEY, BASE_FILENAME);
            }
            return properties.getProperty(WORK_REPORT_BASE_FILENAME_KEY);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * 作業日報ベースファイル名を設定する。
     *
     * @param value 作業日報ベースファイル名
     */
    public static void setWorkReportBaseFilename(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(WORK_REPORT_BASE_FILENAME_KEY, value);
    }

    /**
     * 直接作業の行の背景色を取得する。
     *
     * @return 直接作業の行の背景色
     */
    public static String getWorkReportRowColorDirectWork() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(WORK_REPORT_ROW_COLOR_DIRECT_WORK_KEY)) {
                properties.setProperty(WORK_REPORT_ROW_COLOR_DIRECT_WORK_KEY, WORK_REPORT_ROW_COLOR_DIRECT_WORK_DEFAULT);
            } else if (properties.getProperty(WORK_REPORT_ROW_COLOR_DIRECT_WORK_KEY).isEmpty()) {
                return COLOR_TRANSPARENT;
            }
            return properties.getProperty(WORK_REPORT_ROW_COLOR_DIRECT_WORK_KEY);
        } catch (Exception ex) {
            return COLOR_TRANSPARENT;
        }
    }

    /**
     * 中断時間の行の背景色を取得する。
     *
     * @return 中断時間の行の背景色
     */
    public static String getWorkReportRowColorNonWork() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(WORK_REPORT_ROW_COLOR_NON_WORK_KEY)) {
                properties.setProperty(WORK_REPORT_ROW_COLOR_NON_WORK_KEY, WORK_REPORT_ROW_COLOR_NON_WORK_DEFAULT);
            } else if (properties.getProperty(WORK_REPORT_ROW_COLOR_NON_WORK_KEY).isEmpty()) {
                return COLOR_TRANSPARENT;
            }
            return properties.getProperty(WORK_REPORT_ROW_COLOR_NON_WORK_KEY);
        } catch (Exception ex) {
            return COLOR_TRANSPARENT;
        }
    }

    /**
     * 作業日報ツールの基準時間(H:mm)を取得する。
     *
     * @return 作業日報ツールの基準時間(H:mm)
     */
    public static String getWorkReportToolReferenceTime() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(WORK_REPORT_TOOL_REFERENCE_TIME_KEY)) {
                properties.setProperty(WORK_REPORT_TOOL_REFERENCE_TIME_KEY, WORK_REPORT_TOOL_REFERENCE_TIME_DEFAULT);
            }
            return properties.getProperty(WORK_REPORT_TOOL_REFERENCE_TIME_KEY);
        } catch (Exception ex) {
            return WORK_REPORT_TOOL_REFERENCE_TIME_DEFAULT;
        }
    }

    /**
     * 作業日報ツールの基準時間(H:mm)を設定する。
     *
     * @param value 作業日報ツールの基準時間(H:mm)
     */
    public static void setWorkReportToolReferenceTime(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(WORK_REPORT_TOOL_REFERENCE_TIME_KEY, value);
    }
    
    /**
     * 作業日報リストの表示形式を取得する。
     *
     * @return order: 注文番号単位で表示する、kanban: カンバン単位で表示する、production: 製造番号単位で表示する
     */
    public static String getWorkReportType() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(WORK_REPORT_TYPE_KEY)) {
                properties.setProperty(WORK_REPORT_TYPE_KEY, WORK_REPORT_TYPE_DEFAULT);
            }
            return properties.getProperty(WORK_REPORT_TYPE_KEY);
        } catch (Exception ex) {
            return WORK_REPORT_TYPE_DEFAULT;
        }
    }
    
    /**
     * 直接工数の編集可否を取得する。
     *
     * @return true: 編集可、false: 編集不可
     */
    public static boolean getWorkReportDirectWorkEditable() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(WORK_REPORT_DIRECT_WORK_EDITABLE_KEY)) {
                properties.setProperty(WORK_REPORT_DIRECT_WORK_EDITABLE_KEY, WORK_REPORT_DIRECT_WORK_EDITABLE_DEFAULT);
            }
            return Boolean.valueOf(properties.getProperty(WORK_REPORT_DIRECT_WORK_EDITABLE_KEY));
        } catch (Exception ex) {
            return Boolean.valueOf(WORK_REPORT_DIRECT_WORK_EDITABLE_DEFAULT);
        }
    }
    
    public static boolean getWorkReportWorkNumVisible() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(WORK_REPORT_WORK_NUM_VISIBLE_KEY)) {
                properties.setProperty(WORK_REPORT_WORK_NUM_VISIBLE_KEY, WORK_REPORT_WORK_NUM_VISIBLE_DEFAULT);
            }
            return Boolean.valueOf(properties.getProperty(WORK_REPORT_WORK_NUM_VISIBLE_KEY));
        } catch (Exception ex) {
            return Boolean.valueOf(WORK_REPORT_WORK_NUM_VISIBLE_DEFAULT);
        }
    }

    /**
     * 工数の表示性を取得する。
     *
     * @return true: 表示する、false: 表示しない
     */
    public static boolean getWorkReportWorkTimeVisible() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey("workReportWorkTimeVisible")) {
                properties.setProperty("workReportWorkTimeVisible", "true");
            }
            return Boolean.valueOf(properties.getProperty("workReportWorkTimeVisible"));
        } catch (Exception ex) {
            return Boolean.valueOf("true");
        }
    }

    /**
     * 1台あたりの工数の表示性を取得する。
     *
     * @return true: 表示する、false: 表示しない
     */
    public static boolean getWorkReportUnitTimeVisible() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey("workReportUnitTimeVisible")) {
                properties.setProperty("workReportUnitTimeVisible", "false");
            }
            return Boolean.valueOf(properties.getProperty("workReportUnitTimeVisible"));
        } catch (Exception ex) {
            return Boolean.valueOf("false");
        }
    }
  
    /**
     * 実績数の表示性を取得する。
     *
     * @return true: 表示する、false: 表示しない
     */
    public static boolean getWorkReportActualNumVisible() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey("workReportActualNumVisible")) {
                properties.setProperty("workReportActualNumVisible", "false");
            }
            return Boolean.valueOf(properties.getProperty("workReportActualNumVisible"));
        } catch (Exception ex) {
            return Boolean.valueOf("false");
        }
    }
  
    /**
     * 日報データ出力形式を取得する。
     * 
     * @return 日報データ出力形式
     */
    public static String getWorkReportCsvType() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(WORK_REPORT_CSV_TYPE_KEY)) {
                properties.setProperty(WORK_REPORT_CSV_TYPE_KEY, WORK_REPORT_CSV_TYPE_DEFAULT);
            }
            return properties.getProperty(WORK_REPORT_CSV_TYPE_KEY);
        } catch (Exception ex) {
            return WORK_REPORT_CSV_TYPE_DEFAULT;
        }
    }
}
