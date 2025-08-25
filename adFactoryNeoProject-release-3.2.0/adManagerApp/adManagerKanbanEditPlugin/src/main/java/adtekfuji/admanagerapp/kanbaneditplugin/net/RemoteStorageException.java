/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.net;

/**
 * RemoteStorage 例外クラス
 *
 * @author s-heya
 * adtekfuji.admanagerapp.workfloweditplugin.netからコピー y-harada
 */
public class RemoteStorageException extends Exception {
    private final int responseCode;

    /**
     * コンストラクタ
     *
     * @param responseCode レスポンスコード
     * @param responseMessage レスポンスメッセージ
     */
    public RemoteStorageException(int responseCode, String responseMessage) {
        super(responseMessage);
        this.responseCode = responseCode;
    }

    /**
     * HTTP状態コードを取得する。
     *
     * @return レスポンスコード
     */
    public int getResponseCode() {
        return this.responseCode;
    }
}
