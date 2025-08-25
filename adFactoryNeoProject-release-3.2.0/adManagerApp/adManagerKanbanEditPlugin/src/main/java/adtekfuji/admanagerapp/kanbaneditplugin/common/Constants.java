/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.common;

import javafx.geometry.Insets;
import javafx.scene.layout.CornerRadii;

/**
 * カンバン用定数
 *
 * @author e.mori
 * @version 1.6.1
 * @since 2017.1.19.Wen
 */
public class Constants {

    // カンバン生成設定
    public final static String OPENING_DATE_TIME = "opening_date_time";
    public final static String CLOSING_DATE_TIME = "closing_date_time";
    public final static String LOT_QUANTITY = "one_Piece_Flow";
    public final static Integer DEFAULT_LOT_QUANTITY = 1;

    // 専用設定ファイル用定数
    public static final String PROPERTY_NAME = "adManagerKanbanEditPlugin";
    public static final String PROPERTIES_EXT = ".properties";
    public static final String PROP_TRACEABILITY_DB_ENABLED = "traceabilityDBEnabled";
    public static final String TRACEABILITY_DB_ENABLED_DEF = "false";

    // カンバン編集画面
    public final static Long SEPARATE_WORKFLOW_ID = 0l;
    public final static String DEFAULT_OFFSETTIME = "00:00:00";
    public static final String SEARCH_KANBAN_NAME_SELECTED = "kanbanNameSelected";// カンバン名の選択
    public static final String SEARCH_KANBAN_NAME = "kanbanName";// カンバン名
    public static final String SEARCH_PRODUCT_NUM_SELECTED = "productNumSelected";// 製造番号の選択
    public static final String SEARCH_PRODUCT_NUM = "productNum";// 製造番号
    public static final String SEARCH_MODEL_NAME_SELECTED = "modelNameSelected";// 機種名の選択
    public static final String SEARCH_MODEL_NAME = "modelName";// 機種名
    public static final String SEARCH_WORKFLOW_NAME_SELECTED = "workflowNameSelected";// 工程順名の選択
    public static final String SEARCH_WORKFLOW_NAME = "workflowName";// 工程順名
    public static final String SEARCH_WORK_NAME_SELECTED = "workNameSelected";// 工程名の選択
    public static final String SEARCH_WORK_NAME = "workName";// 工程名
    public static final String SEARCH_STATUS_SELECTED = "statusSelected";// ステータスの選択
    public static final String SEARCH_STATUS = "status";// ステータス

    public static final String SEARCH_LOT_SELECTED = "lotSelected"; // ロット生産 チェックボックス
    public static final String SEARCH_LOT_WORK_FLOW = "lotWorkFlow"; // ロット生産 工程順
    
    /**
     * カンバンラベルセルのパディング
     */
    public static final Insets KANBAN_LABEL_CELL_PADDING = new Insets(4.0);
    
    /**
     * セル内のカンバンラベルパディング
     */
    public static final Insets KANBAN_LABEL_PADDING_IN_CELL = new Insets(4.0);
            
    /**
     * セル内のカンバンラベルボーダー幅
     */
    public static final double KANBAN_LABEL_BORDER_WIDTH_IN_CELL = 1.0;
    
    /**
     * セル内のカンバンラベル間隔（並行方向）
     */
    public static final double KANBAN_LABELS_HGAP_IN_CELL = 4.0;
    
    /**
     * セル内のカンバンラベル間隔（垂直方向）
     */
    public static final double KANBAN_LABELS_VGAP_IN_CELL = 4.0;
    
    /**
     * セル内の最大カンバンラベル行数
     */
    public static final int MAX_NUMBER_OF_KANBAN_LABEL_ROWS_IN_CELL = 2;
    
    /**
     * カンバンラベルセルの高さ
     */
    public static final double KANBAN_LABELS_CELL_HEIGHT = 64.0;
    
    /**
     * セル内のカンバンラベル角丸値
     */
    public static final CornerRadii KANBAN_LABEL_CORNER_RADIUS_IN_CELL = new CornerRadii(16.0);
    
    /**
     * セル内のカンバンラベル省略文字列
     */
    public static final String KANBAN_LABELS_ELLIPSIS_IN_CELL = "…";
    
    /**
     * セル内のカンバンラベル省略文字フォントスタイル
     */
    public static final String KANBAN_LABELS_ELLIPSIS_FONT_STYLE_IN_CELL = "-fx-font-weight: bold;";
    
    /**
     * ツールチップ内のカンバンラベル区切り文字列
     */
    public static final String KANBAN_LABELS_DELIMITER_IN_TOOLTIP = ", ";
    
    /**
     * カンバンラベルメニュー名最小幅
     */
    public static final double KANBAN_LABEL_MENU_NAME_MIN_WIDTH = 100.0;

    // 帳票出力
    public static final String REPORT_FOLDER = "reportFolder"; // 出力先
    public static final String REPORT_LEAVE_TAGS = "reportLeaveTags"; // 置換されなかったタグを残す
    public static final String REPORT_AS_PDF_TAGS = "exportAsPdfTags";

    // まとめて帳票出力
    public static final String COLLECTIVE_REPORT_FOLDER = "collectiveReportFolder"; // 出力先
    public static final String COLLECTIVE_REPORT_LEAVE_TAGS = "collectiveRreportLeaveTags"; // 置換されなかったタグを残す
    public static final String EXPORT_AS_PDF_TAGS = "exportAsPdfTags"; // pdfにて出力する

    /**
     * 拡張タグを使用するか(true: 使用する/false: 使用しない)
     */
    public static final String USE_EXTENSION_TAG = "useExtensionTag";

    /**
     * 拡張タグを使用するか(true: 使用する/false: 使用しない)の初期値
     */
    public static final String USE_EXTENSION_TAG_DEFAULT = "false";
    
    // 品質データ
    public static final String TRACEABILITY_DOWNLOAD_DIRECTORY = "traceabilityDownloadDirectory"; // ダウンロード先
    
    // 承認
    public static final String APPROVE_NUM = "approveNum";
    public static final String APPROVE_NUM_DEF = "2";

    // 部品トレースを使用するか(true:使用する, false:使用しない)
    public static final String ENABLE_PARTS_TRACE = "enablePartsTrace";
    public static final String ENABLE_PARTS_TRACE_DEF = "false";

    // QRコードを使用するか(true:使用する, false:使用しない)
    public static final String USE_QRCODE = "useQRCode";
    public static final String USE_QRCODE_DEF = "false";

    /**
     * タクトタイムの最大値(ミリ秒)
     */
    public static final int TAKT_TIME_MAX_MILLIS = 1731599000;
    
    /**
     * パーツID編集のプロパティ名
     */
    public static final String PROP_EDITING_PARTS_ID = "editingPartsID";
    
    /**
     * パーツID編集のデフォルト値
     */
    public static final String EDITING_PARTS_ID_DEF = "false";
}
