/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.utility;

import adtekfuji.net.HttpClientException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.xml.bind.MarshalException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.SSLException;

/**
 * エラーユーティリティクラス
 *
 * @author s-heya
 */
public class ErrorUtils {
    private static ResourceBundle rb = null;

    /**
     * エラーメッセージと対処方法を取得する
     *
     * @param ex
     * @return
     */
    public static String[] getMessage(Exception ex) {
        if (ex instanceof ExecutionException) {
            return ErrorUtils.getMessage((Exception)ex.getCause());
        }
        else if (ex instanceof HttpClientException) {
            return ErrorUtils.getMessage((HttpClientException)ex);
        }
        //else if (ex instanceof ClientHandlerException) {
        //    ClientHandlerException clientHandlerException = (ClientHandlerException)ex;
        //    return ErrorUtils.getMessage((Exception)clientHandlerException.getCause());
        //}
        else if (ex instanceof WebApplicationException) {
            WebApplicationException webApplicationException = (WebApplicationException)ex;
            return ErrorUtils.getMessage((Exception)webApplicationException.getCause());
        }
        else if (ex instanceof MarshalException) {
            MarshalException marshalException = (MarshalException)ex;
            Throwable throwable = marshalException.getLinkedException();
            if (Objects.nonNull(throwable)) {
                return ErrorUtils.getMessage((Exception)throwable);
            }
        }
        else if (ex instanceof UnknownHostException || ex instanceof SocketTimeoutException) {
            return new String[]{ ErrorUtils.getLocalizedMessage("key.alert.unknownServer"), ErrorUtils.getLocalizedMessage("key.alert.unknownServer.details")};
        }
        else if (ex instanceof ConnectException) {
            return new String[]{ ErrorUtils.getLocalizedMessage("key.alert.connectServer"), ErrorUtils.getLocalizedMessage("key.alert.connectServer.details")};
        }
        else if (ex instanceof SSLException) {
            return new String[]{ ErrorUtils.getLocalizedMessage("key.alert.accessDenied"), ErrorUtils.getLocalizedMessage("key.alert.accessDenied.details")};
        }
        else if (ex instanceof NoSuchAlgorithmException) {
            return new String[]{ ErrorUtils.getLocalizedMessage("key.alert.connectServer"), ErrorUtils.getLocalizedMessage("key.alert.connectServer.details")};
        }

        return new String[]{ ex.getMessage(), "" };
    }

    /**
     * エラーメッセージと対処方法を取得する
     *
     * @param ex
     * @return
     */
    private static String[] getMessage(HttpClientException ex) {
        String message = "";
        String details = "";
        switch (ex.getResponseCode()) {
            case 0x00: //成功
                break;
            case 0x01: // SERVER_FETAL サーバー処理エラー
                break;
            case 0x02: // NAME_OVERLAP 名前重複で追加/更新不可
                break;
            case 0x03: // IDENTNAME_OVERLAP 識別子重複で追加/更新不可
                break;
            case 0x04: // NOTFOUND_UPDATE 指定IDなしで更新不可
                break;
            case 0x05: // NOTFOUND_DELETE 指定IDなしで削除不可
                break;
            case 0x06: // NOTFOUND_PARENT 指定親階層IDなしで追加/更新不可
                break;
            case 0x07: // EXIST_HIERARCHY_DELETE 子階層ありで削除不可
                break;
            case 0x08: // EXIST_CHILD_DELETE 子要素ありで削除不可
                break;
            case 0x09: // PROTCTED_DATA 保護された情報で削除不可
                break;
            case 0x0A: // NOT_PERMIT_EQUIPMENT 設備の接続不許可
                message = ErrorUtils.getLocalizedMessage("key.alert.equipmentLogin");
                details = ErrorUtils.getLocalizedMessage("key.alert.equipmentLogin.details");
                break;
            case 0x0B: // NOT_LOGINID_ORGANIZATION 組織のログイン不許可（ログインID未登録）
                message = ErrorUtils.getLocalizedMessage("key.alert.organizationLogin");
                details = ErrorUtils.getLocalizedMessage("key.alert.organizationLogin.details");
                break;
            case 0x0C: // NOT_AUTH_ORGANIZATION 組織のログイン不許可（認証情報不一致）
                break;
            case 0x0D: // NOT_PERMIT_ORGANIZATION 組織のログイン不許可（権限なし）
                break;
            case 0x0E: // THERE_START_NON_EDITABLE 開始している工程があるので、編集不可
                break;
            case 0x0F: // THERE_START_NON_DELETABLE 開始している工程があるので、削除不可
                break;
            case 0x10: // UPDATE_ONLY_STATUS ステータスのみ更新
                break;
            case 0x11: // LICENSE_ERROR ライセンスエラー
                message = ErrorUtils.getLocalizedMessage("key.alert.licenseError");
                details = ErrorUtils.getLocalizedMessage("key.alert.licenseError.details");
                break;
            case 0x012: // NOT_DELETE_SYSTEM_ADMIN SYSTEM_ADMIN削除不可
                break;
            default:
                break;
        }

        return new String[] { message, details };
    }

    /**
     * ローカライズされたメッセージを取得する
     *
     * @param key
     * @return
     */
    private static String getLocalizedMessage(String key) {
        if (rb == null) {
            try {
                try {
                    // ロケールプラグインを読み込む
                    rb = ResourceBundle.getBundle("adtekfuji.utility.locale.locale", Locale.getDefault());
                    //rb = ResourceBundle.getBundle("locale.locale", locale);
                }
                catch (MissingResourceException ex) {
                    // ロケールプラグインが存在しない場合、パッケージ内のリソースを読み込む
                    rb = ResourceBundle.getBundle("adtekfuji.utility.locale.locale", Locale.getDefault());
                }
            }
            catch (Exception ex) {
                rb = ResourceBundle.getBundle("adtekfuji.utility.locale.locale", Locale.getDefault());
            }
        }
        if (!rb.containsKey(key)) {
            return key;
        }
        return rb.getString(key);
    }
}
