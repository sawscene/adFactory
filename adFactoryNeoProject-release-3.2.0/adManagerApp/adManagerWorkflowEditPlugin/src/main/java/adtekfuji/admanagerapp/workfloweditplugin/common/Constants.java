/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.common;

/**
 * 定数クラス
 *
 * @author fu-kato
 */
public class Constants {

    public static final String IMG_TAG = "<img src=\"%s\">";
    public static final String A_TAG = "<a href=\"%s\">%s</a>";
    public static final String URL_OPEN_KEY = "rvZE/xl8isESD7AkAFlvVA==";

    // 工程順のワークフローに、設備を表示するか。
    public static final String VISIBLE_EQUIPMENT_KEY = "workflowVisibleEquipment";
    public static final String VISIBLE_EQUIPMENT_DEFAULT = "true";
    // 工程順のワークフローに、組織を表示するか。
    public static final String VISIBLE_ORGANIZATION_KEY = "workflowVisibleOrganization";
    public static final String VISIBLE_ORGANIZATION_DEFAULT = "true";
    // 工程順のワークフローに、スケールのスライダーを表示するか。
    public static final String VISIBLE_WORKFLOW_SCALE_KEY = "workflowVisibleScale";
    public static final String VISIBLE_WORKFLOW_SCALE_DEFAULT = "false";
    // 工程順のワークフローの、スケールのスライダー値。
    public static final String WORKFLOW_SCALE_KEY = "workflowScale";
    public static final String WORKFLOW_SCALE_DEFAULT = "1.0";
    // バーコード照合プラグインを使用するか
    public static final String ENABLE_CHECK_BARCODE = "enableCheckBarcode";
    public static final String ENABLE_CHECK_BARCODE_DEFAULT = "false";
    // Lite工程設定で標準作業時間を使用するか
    public static final String ENABLE_LITE_TAKT_TIME = "enableLiteTaktTime";
    public static final String ENABLE_LITE_TAKT_TIME_DEFAULT = "false";
    // 製造番号登録プラグインを使用するか
    public static final String ENABLE_INPUT_PRODUCT_NUM = "enableInputProductNum";
    public static final String ENABLE_INPUT_PRODUCT_NUM_DEFAULT = "false";

    /**
     * サーバのアドレス
     */
    public static final String SERVER_ADDRESS_KEY = "serverAddress";

    /**
     * サーバのアドレスのデフォルト値
     */
    public static final String SERVER_ADDRESS_DEFAULT = "http://localhost:8080";

    /**
     * 承認WebページのURL
     */
    public static final String APPROVAL_WEB_URL_KEY = "approvalWebURL";

    /**
     * 承認WebページのURL(デフォルト値)
     */
    public static final String APPROVAL_WEB_URL_DEFAULT = "/adFactoryServer/approval?id=%s&organization=%s";

    /**
     * タクトタイムの最大値(ミリ秒)
     */
    public static final int TAKT_TIME_MAX_MILLIS = 1731599000;
}
