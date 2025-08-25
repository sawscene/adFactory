/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.common;

/**
 * 定数
 *
 * @author e.mori
 */
public class Constants {

    // インストールフォルダの環境変数名
    public static final String ADFACTORY_HOME = "ADFACTORY_HOME";

    // オプション名
    public static final String LICENSE_WAREHOUSE = "@Warehouse";

    public static final String ORGANIZATION = "organization";
    public static final String KANBAN = "kanban";
    public static final String FOLLOW_START = "followStart";
    public static final String RESET_TIME = "resetTime";
    public static final String ENABLE_DAILY_REPORT_KEY = "enableDailyReport";// 作業日報が有効か
    public static final String ENABLE_TROUBLE_REPORT_KEY = "enableTroubleReport";// 障害レポート送信が有効か
    public static final String CSV_ENCODE_KEY = "csvEncode";// CSVファイルのエンコード
    public static final String CSV_ENCODE_DEFAULT = "MS932";
    public static final int WORKFLOW_REV_LIMIT = 999;// 工程順の版数の最大値

    // TODO ローカライズの対応
    public static final String DISABLE_SYNC_WORK = "同時作業禁止";
    public static final String YES = "YES";
    public static final String PRODUCT_QTY = "数量";
    public static final String FINAL_WORK = "最終工程";

    // トレーサビリティDB使用フラグ (品質トレーサビリティの保存先)
    public static final String TRACEABILITY_DB_ENABLED_KEY = "traceabilityDBEnabled";
    public static final String TRACEABILITY_DB_ENABLED_DEF = "false";
    
    // 休憩時間中でのカウントダウン継続設定
    public static final String WORK_OVERTIME = "workOvertime";
    
    // 半角スペース
    public static final String SPACE = " ";

    // キーのセパレーター
    public static final String KEY_SEPARATOR = "-";
    
    public static final String SUFFIX_BRANCH_NO = "-R";

    // システム移行中の資材情報を出庫するときに入力するコード
    // public static final String DUMMY_CODE = "9999";
    public static final String DUMMY_CODE = "0000";
   
    // LIKE式のパターン文字列
    public static final String LIKE_PATTERN = "%";
    
    public static final String UTF_8 = "UTF-8";
    
    // 項目名
    public static final String PRODUCT_NO = "ProductNo";
    public static final String PRODUCT_NAME = "ProductName";
    public static final String MATERIAL_NO = "MaterialNo";
    public static final String FIGURE_NO = "FigureNo";
    public static final String ORDER_NO = "OrderNo";
    public static final String MATERIAL = "Material";
    public static final String VENDOR = "Vendor";
    public static final String SPEC = "Spec";
    public static final String NOTE = "Note";
    public static final String LOT_NO = "LotNo";

    // 動作モード
    public static final String NEXAS = "NEXAS";
    
    /**
     * パスワード暗号化/複合化時の秘密鍵
     */
    public static final String CIPHER_KEY = "BpzEsGPXnkW2SdUW";  //キーは16文字で

    /**
     * パスワード暗号化/複合化時の暗号化方式
     */
    public static final String CIPHER_ALGORITHM = "AES";

    /**
     * adFactory Lite 階層名の設定キー
     */
    public final static String LITE_HIERARCHY_TOP_KEY = "LiteHierarchyTop";

    public final static String NORMAL_STYLE = "item-normal";
    public final static String IMPORTANT_STYLE = "item-important";
    public final static String INSPECTION_STYLE = "item-inspection";
}
