/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.plugin;

import jp.adtekfuji.adlinkservice.command.DevRequest;
import jp.adtekfuji.adlinkservice.command.DevResponse;

/**
 * adLinkServceプラグイン インターフェース
 * 
 * @author s-heya
 */
public interface adLinkServiceInterface {

    /**
     * プラグイン名を取得する。
     * 
     * @return プラグイン名 
     */
    String getPluginName();
   
    /**
     * クライアントが接続した。
     * 
     * @param value セッション
     */
    void onOpen(Object value);

    /**
     * コマンドを受信した。
     * 
     * @param command コマンド
     * @param value セッション
     * @return 応答コマンド
     */
    DevResponse onRequest(DevRequest command, Object value);

    /**
     * クライアントが切断した。
     * 
     * @param value セッション
     */
    void onClose(Object value);
}
