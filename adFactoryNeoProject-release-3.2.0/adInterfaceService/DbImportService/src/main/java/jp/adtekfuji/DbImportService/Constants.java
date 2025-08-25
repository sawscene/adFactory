/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.DbImportService;

/**
 * 自動読み込み定数
 *
 * @author fu-kato, ka.makihara
 */
public class Constants {

    public static final String ENABLE_IMPORT = "enableImport";
    public static final String POLLING_TIME = "pollingTime";

    //for Oracle DB
    public static final String DB_HOST_NAME = "DB_HOST_NAME";
    public static final String DB_PORT      = "DB_PORT";
    public static final String DB_USER_NAME = "DB_USER_NAME";
    public static final String DB_USER_PASS = "DB_USER_PASS";
    public static final String DB_SID       = "DB_SID";
    public static final String DB_KANBAN_TABLE= "DB_KANBAN_TABLE";

    //for Oracle DB TABLE Item
    public static final String DB_ITEM_SEIBAN           = "DB_ITEM_SEIBAN";           // 製番
    public static final String DB_ITEM_GOKI             = "DB_ITEM_MODEL_GOKI";       // 号機
    public static final String DB_ITEM_KANBAN_NAME      = "DB_ITEM_KANBAN_NAME";      // カンバン名称
    public static final String DB_ITEM_VIEW_NAME        = "DB_ITEM_VIEW_NAME";        // 表示名称
    public static final String DB_ITEM_LINE             = "DB_ITEM_LINE";             // 製造ライン
    public static final String DB_ITEM_SEIBAN_KBN       = "DB_ITEM_SEIBAN_KBN";       // 製番区分
    public static final String DB_ITEM_KISYU_ID         = "DB_ITEM_KISYU_ID";         // 機種ID
    public static final String DB_ITEM_KEIKAKU_BI       = "DB_ITEM_KEIKAKU_BI";       // カンバンの作業計画日時
    public static final String DB_ITEM_KANBAN_STS       = "DB_ITEM_STATUS";           // カンバン取り込みステータス
    public static final String DB_ITEM_STS_DETAILS      = "DB_ITEM_ERR_DETAIL";       // カンバン取り込みエラー詳細
    public static final String DB_ITEM_SOCKET_STS       = "DB_ITEM_SOCKET_STATUS";    // ソケット送信ステータス
    public static final String DB_ITEM_KAKO_MOTO_SEIBAN = "DB_ITEM_KAKO_MOTO_SEIBAN"; // 仕込み加工元製番
    public static final String DB_ITEM_DATA_TOROKU_BI   = "DB_ITEM_DATA_TOROKU_BI";   // データ登録日
    public static final String DB_ITEM_DATA_KOSHIN_BI   = "DB_ITEM_DATA_KOUSHIN_BI";  // データ更新日

    public static final String COL_SOSHIKI_SKB_NAME = "SOSHIKI_SKB_NAME";
    public static final String COL_SOSHIKI_NAME = "SOSHIKI_NAME"; 
    public static final String COL_DEPT_NAME = "DEPT_NAME";
}
