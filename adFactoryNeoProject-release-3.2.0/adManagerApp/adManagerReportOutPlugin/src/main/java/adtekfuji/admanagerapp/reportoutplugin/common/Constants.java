/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.reportoutplugin.common;

/**
 * 実績出力用定数
 *
 * @author nar-nakamura
 */
public class Constants {

    // 設定ファイル用定数
    public static final String CSV_FILENAME_KEY = "report_csv_filename";
    public static final String CSV_FILENAME_DEFAULT = "adfactory_report.csv";
    public static final String CSV_FILENAME_SUMMARY_KEY = "report_csv_filename_summary";
    public static final String CSV_FILENAME_SUMMARY_DEFAULT = "adfactory_report_summary.csv";
    public static final String CSV_ENCODE_KEY = "report_csv_encode_type";
    public static final String CSV_ENCODE_DEFAULT = "Shift_JIS";
    public static final String REPORT_OUT_SEARCH_MAX_KEY = "reportOutSearchMax";
    public static final String REPORT_OUT_SEARCH_MAX_DEFAULT = "10000";
    public static final String ACTUAL_PROP_SEARCH_MAX_KEY = "actualPropSearchMax";
    public static final String ACTUAL_PROP_SEARCH_MAX_DEFAULT = "50000";

    // 専用設定ファイル用定数
    public static final String REPORT_OUT_PROPETRY_NAME = "adManagerReportOutPlugin";
    public static final String PROPERTIES_EXT = ".properties";

    public static final String SEARCH_DATE_SELECTED = "dateSelected";// 対象日の選択
    public static final String SEARCH_FROM_DATE = "fromDate";// 対象開始日
    public static final String SEARCH_TO_DATE = "toDate";// 対象終了日
    public static final String SEARCH_KANBAN_NAME_SELECTED = "kanbanNameSelected";// カンバン名の選択
    public static final String SEARCH_KANBAN_NAME = "kanbanName";// カンバン名
    
    // 設定保存ボタン
    public static final String SAVE_SETTING_INITIAL_FILENAME = "ReportOutSetting";// 初期ファイル名
    public static final String REPORT_OUT_SAVE_SETTING_FOLDER = "reportOutSaveSettingFolder";// 設定保存先フォルダ
    
    // 設定読込ボタン
    public static final String REPORT_OUT_LOAD_SETTING_FOLDER = "reportOutLoadSettingFolder";// 設定読込元フォルダ

    public static final String SEARCH_PRODUCTION_NUMBER_SELECTED = "productionNumberSelected";// 製造番号の選択
    public static final String SEARCH_PRODUCTION_NUMBER = "productionNumber";// 製造番号

    // TODO: 機種名を使用する場合、有効にする。
    public static final String SEARCH_MODEL_NAME_SELECTED = "modelNameSelected";// 機種名の選択
    public static final String SEARCH_MODEL_NAME = "modelName";// 機種名

    public static final String SEARCH_EQUIPMENT_SELECTED = "equipmentSelected";// 設備の選択
    public static final String SEARCH_EQUIPMENT_IDS = "equipmentIds";// 設備ID
    public static final String SEARCH_EQUIPMENT_NAME= "equipmentName";// 設備名
    public static final String SEARCH_ORGANIZATION_SELECTED = "organizationSelected";// 組織の選択
    public static final String SEARCH_ORGANIZATION_IDS = "organizationIds";// 組織ID
    public static final String SEARCH_ORGANIZATION_NAME = "organizationName";// 組織名
    public static final String SEARCH_WORKFLOW_SELECTED = "workflowSelected";// 工程順の選択
    public static final String SEARCH_WORKFLOW_IDS = "workflowIds";// 工程順ID
    public static final String SEARCH_WORKFLOW_NAME = "workflowName";// 工程順名・版数
    public static final String SEARCH_STATUS_SELECTED = "statusSelected";// ステータスの選択
    public static final String SEARCH_STATUS = "status";// ステータス
    public static final String SEARCH_FIXED_COLUMNS_SELECTED = "fixedColumnsSelected"; // 固定列出力の選択
    public static final String SEARCH_OUTPUT_SUMMARY = "outputSummary"; // CSV出力時にサマリーを出力する
}
