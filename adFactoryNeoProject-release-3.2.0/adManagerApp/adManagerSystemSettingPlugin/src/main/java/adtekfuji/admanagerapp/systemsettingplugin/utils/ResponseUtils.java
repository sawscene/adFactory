/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.utils;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import jp.adtekfuji.adFactory.entity.ResponseEntity;

/**
 * サーバーレスポンス ユーティリティ
 *
 * ※. jp.adtekfuji.adFactory.entity.ResponseAnalyzer では、ダイアログ表示時に入力画面のブロックが解除されてしまうため作成。
 *
 * @author nar-nakamura
 */
public class ResponseUtils {

    private final static SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * 警告メッセージを表示する。
     *
     * @param response サーバーレスポンス
     */
    public static void showAlertDialog(ResponseEntity response) {
        if (Objects.isNull(response) || Objects.isNull(response.isSuccess())) {
            // サーバーとの通信に問題が発生しました
            StringBuilder message = new StringBuilder(LocaleUtils.getString("key.alert.communicationServer"))
                    .append("\r\n\r\n");

            if (Objects.nonNull(response) && Objects.nonNull(response.getException())) {
                message.append(LocaleUtils.getString("key.Reason"))
                        .append(": ")
                        .append(response.getException().getMessage());
            } else {
                // サーバーの状態を確認してください
                message.append(LocaleUtils.getString("key.alert.communicationServer.details3"));
            }

            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Error"), message.toString());
            return;
        }
        
        if (response.isSuccess()) {
            return;
        }

        switch (response.getErrorType()) {
            //case SERVER_FETAL:// サーバー処理エラー
            //    break;
            case NAME_OVERLAP:// 名前重複で追加/更新不可
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.AddErrNameOverLap"));
                break;
            case IDENTNAME_OVERLAP:// 識別子重複で追加/更新不可
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.AddErrIdentNameOrverLap"));
                break;
            case NOTFOUND_UPDATE:// 指定IDなしで更新不可
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.UpdateErrNotFoundUpdate"));
                break;
            case NOTFOUND_DELETE:// 指定IDなしで削除不可
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DeleteErrNotFountDelete"));
                break;
            case NOTFOUND_PARENT:// 指定親階層IDなしで追加/更新不可
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.AddErrNotFoundParent"));
                break;
            case EXIST_HIERARCHY_DELETE:// 子階層ありで削除不可
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DeleteErrExistHierarchDelete"));
                break;
            case EXIST_CHILD_DELETE:// 子要素ありで削除不可
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DeleteErrExistChildDelete"));
                break;
            case PROTCTED_DATA:// 保護された情報で削除不可
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DeleteErrProtectedData"));
                break;
            //case NOT_PERMIT_EQUIPMENT:// 設備の接続不許可
            //  showAlert(Alert.AlertType.WARNING, null, LocaleUtils.getString("key.LoginErrNotmatchPassword"));
            //  break;
            case NOT_LOGINID_ORGANIZATION:// 組織のログイン不許可（ログインID未登録）
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.organizationLogin"));
                break;
            case NOT_AUTH_ORGANIZATION:// 組織のログイン不許可（認証情報不一致）
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.LoginErrNotmatchPassword"));
                break;
            case NOT_PERMIT_ORGANIZATION:// 組織のログイン不許可（権限なし）
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.LoginErrNotPermitOrganization"));
                break;
            case THERE_START_NON_EDITABLE:// 開始している工程があるので、編集不可
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.KanbanEditThereStart"));
                break;
            case THERE_START_NON_DELETABLE:// 開始している工程があるので、削除不可
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.KanbanDeleteThereStart"));
                break;
            case UPDATE_ONLY_STATUS:// ステータスのみ更新
                break;
            case LICENSE_ERROR:// ライセンスエラー
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.licenseError"), LocaleUtils.getString("key.alert.licenseError.details"));
                break;
            case NOT_DELETE_SYSTEM_ADMIN:// SYSTEM_ADMIN削除不可
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DeleteErrProtectedData"));
                break;
            //case NOTFOUND_WORKFLOW:// 工程順が見つからない
            //case NOTFOUND_ORGANIZATION:// 組織が見つからない
            //case INVALID_ARGUMENT:// 無効な引数です
            //case THERE_WORKING_NON_START:// 作業中のため作業不可
            //case THERE_COMPLETED_NON_START:// 作業完了済みのため作業不可
            //case NOTFOUND_KANBAN:// カンバンが見つからない
            //case NOTFOUND_WORKKANBAN:// 工程カンバンが見つからない
            //    break;
            case OVER_MAX_VALUE:// 最大値をオーバーしているデータがある
                showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.overMaxValue"));
                break;
            //case FILE_NOT_EXIST:// ファイルが存在しない
            //case MAIL_AUTHENTICATION_FAILED:// メール送信で認証失敗
            //case MAIL_MESSAGING_EXCEPTION:// メール送信でメッセージ異常
            //case ALREADY_WORKING_ORGANIZATION:// 他端末で作業中の組織
            //case NOTFOUND_WORK_CATEGORY:// 作業区分が見つからない
            //case EXIST_RELATION_DELETE:// 関連付けありで削除不可
            //case UNMOVABLE_HIERARCHY:// 移動不可能な階層
            //    break;
            case DIFFERENT_VER_INFO:// 排他用バージョンが異なる
                showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Error"), LocaleUtils.getString("key.alert.differentVerInfo"));
                break;
            //case PARTS_ADD_ERROR:// 完成品情報登録エラー
            //case PARTS_DEL_ERROR:// 完成品情報削除エラー
            //case SERAL_ACTUAL_ADD_ERROR:// ロット流し生産のシリアル番号毎の実績登録エラー
            //case DEFECT_ACTUAL_ADD_ERROR:// 不良品の実績登録エラー
            //case REPLENISHMENT_KANBAN_ADD_ERROR:// 補充カンバン登録エラー
            //case DEFECT_ACTUAL_FAILED:// 不良品登録エラー
            //case ACTUAL_ADITION_ADD_ERROR:// 工程実績付加情報登録エラー
            //case LOGIN_LDAP_EXCEPTION:// LDAP認証処理で不明なエラー
            default:
                break;
        }
    }

    /**
     * 警告メッセージを表示する。
     *
     * @param type 種別
     * @param title タイトル
     * @param message メッセージ
     */
    private static void showAlert(Alert.AlertType type, String title, String message) {
        showAlert(type, title, message, null);
    }

    /**
     * 警告メッセージを表示する。
     *
     * @param type 種別
     * @param title タイトル
     * @param message メッセージ
     * @param details 詳細メッセージ
     */
    private static void showAlert(Alert.AlertType type, String title, String message, String details) {
        if (Platform.isFxApplicationThread()) {
            sc.showAlert(type, title, message, details);
        } else {
            Platform.runLater(() -> {
                sc.showAlert(type, title, message, details);
            });
        }
    }

    /**
     * 処理結果からID を抽出する。
     * 
     * @param uri
     * @return 
     */
    public static long uriToId(String uri) {
        long ret = 0;
        try {
            String[] split = uri.split("/");
            if (split.length >= 2) {
                ret = Long.parseLong(split[split.length - 1]);
            }
        } catch (Exception ex) {
        }
        return ret;
    }
}
