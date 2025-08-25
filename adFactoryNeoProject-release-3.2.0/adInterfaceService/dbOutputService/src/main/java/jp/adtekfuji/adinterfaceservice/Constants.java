/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice;

/**
 * DB実績出力
 *  プロパティの項目名
 *
 * @author ka.makihara
 */
public class Constants {

    // properties ファイルには
    //    DB_HOST_NAME=*****
    //     変数名=プロパティ値
    // という形式で記述される
    //   例えば BD_HOST_NAME=localhost と設定された場合
    //   プログラム上では DB_HOST_NAME という変数に "localhost" が設定されるようになる

    //for Oracle DB
    public static final String DB_HOST_NAME = "DB_HOST_NAME";
    public static final String DB_PORT      = "DB_PORT";
    public static final String DB_USER_NAME = "DB_USER_NAME";
    public static final String DB_USER_PASS = "DB_USER_PASS";
    public static final String DB_SID       = "DB_SID";
    public static final String DB_ACTUAL_TABLE = "DB_ACTUAL_TABLE";
    public static final String DB_DETAIL_TABLE = "DB_DETAIL_TABLE";
    public static final String DB_OUTPUT_ENABLE = "enabledbOutput";

    //for Oracle DB TABLE Item
    public static final String DB_ITEM_LINE              = "DB_ITEM_LINE";            //組立てライン
    public static final String DB_ITEM_KANBAN_NAME       = "DB_ITEM_KANBAN_NAME";     //カンバン名称
    public static final String DB_ITEM_KOUTEI_JUN_NAME   = "DB_ITEM_KOUTEI_JUN_NAME"; //工程順名
    public static final String DB_ITEM_KOUTEI_NAME       = "DB_ITEM_KOUTEI_NAME";     //工程名
    public static final String DB_ITEM_SOSHIKI_NAME      = "DB_ITEM_SOSHIKI_NAME";    //組織名
    public static final String DB_ITEM_SOSHIKI_SKB_NAME  = "DB_ITEM_SOSHIKI_SKB_NAME";//組織識別名
    public static final String DB_ITEM_SETSUBI_NAME      = "DB_ITEM_SETSUBI_NAME";    //設備名
    public static final String DB_ITEM_SETSUBI_MNG_NAME  = "DB_ITEM_SETSUBI_MNG_NAME";//設備管理名
    public static final String DB_ITEM_HISTORY_STS       = "DB_ITEM_HISTORY_STS";     //ステータス
    public static final String DB_ITEM_CYUDAN_REASON     = "DB_ITEM_CYUDAN_REASON";   //中断理由
    public static final String DB_ITEM_CHIEN_REASON      = "DB_ITEM_CHIEN_REASON";    //遅延理由
    public static final String DB_ITEM_JISSHI_BI         = "DB_ITEM_JISSHI_BI";       //実施日
    public static final String DB_ITEM_TACT_TIME         = "DB_ITEM_TACT_TIME";       //タクトタイム
    public static final String DB_ITEM_WORK_TIME         = "DB_ITEM_WORK_TIME";       //作業時間
    public static final String DB_ITEM_DATA_TOROKU_BI    = "DB_ITEM_DATA_TOROKU_BI";  //データ登録日
    public static final String DB_ITEM_DATA_KOSHIN_BI    = "DB_ITEM_DATA_KOSHIN_BI";  //データ更新日
    public static final String DB_ITEM_ACTUAL_ID         = "DB_ITEM_ACTUAL_ID";

    // カラム名
    public static final String COL_ACTUAL_ID = "ACTUAL_ID";
    public static final String COL_SUB_ID = "SUB_ID";
    public static final String COL_TRACE_NAME = "TRACE_NAME";
    public static final String COL_TRACE_TAG = "TRACE_TAG";
    public static final String COL_TRACE_VALUE = "TRACE_VALUE";
    public static final String COL_DATA_TOROKU_BI = "DATA_TOROKU_BI";
}
