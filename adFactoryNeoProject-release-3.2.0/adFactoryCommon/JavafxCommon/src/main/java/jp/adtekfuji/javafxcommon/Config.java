/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon;

import jp.adtekfuji.adFactory.enumerate.ProductionTypeEnum;

/**
 * 設定項目クラス
 *
 * @author s-heya
 */
public class Config {

    // バーコードリーダーの使用
    public final static String USE_BARCODE = "useBarcode";

    // カンバン作成画面のデフォルト工程順
    public final static String DEFAULT_WORKFLOW = "defaultWorkflow";
    public final static String DEFAULT_WORKFLOW_REV = "defaultWorkflowRev";
    public final static String DEFAULT_LITE_WORKFLOW = "defaultLiteWorkflow";
    public final static String DEFAULT_LITE_WORKFLOW_REV = "defaultLiteWorkflowRev";

    // カンバン作成画面のコンポーネント名
    public final static String COMPO_CREATE_KANBAN = "compoCreateKanban";

    // 並列工程の作業時間をシフトする
    public final static String SHIFT_TIME = "shiftTime";

    // ロット生産機能の使用有無のデフォルト値
    public final static String LOT_PRODUCTION_DEFAULT = "lotProductionDefault";

    // 帳票ファイルをサーバーに送るか否かを判断する
    public final static String SEND_EXCEL_FILE = "sendExcelFile";
    // 2019/12/05 作業完了以外の帳票出力対応 帳票ファイルを開くか否かを判断する
    public final static String OPEN_EXCEL_FILE = "openExcelFile";

    // 生産タイプ
    public final static String PRODUCTION_TYPE_KEY = "productionType";
    public final static ProductionTypeEnum PRODUCTION_TYPE_DEFAULT = ProductionTypeEnum.ONE_PIECE;
    
    // 区画名
    public final static String WAREHOUSE_AREA_NAME_DEFAULT = "warehouseAreaNameDefault";
    
    // ラベル印刷要否確認
    public final static String WAREHOUSE_PRINT_CHECK_DEFAULT = "warehousePrintCheckDefault";

    // Lite階層プロパティキー
    public final static String LITE_HIERARCHY_TOP_KEY = "LiteHierarchyTop";
}
