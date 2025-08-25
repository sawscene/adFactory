/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.plugin;

/**
 * 機器連携プラグインインターフェース
 * 
 * @author s-heya
 */
public interface LinkPlugin {

    /**
     * 連携サービスを開始する。
     * 
     * @throws Exception 
     */
    default void start() throws Exception {};

    /**
     * 連携サービスを停止する。
     * 
     * @throws Exception 
     */
    default void stop() throws Exception {};

    /**
     * プラグイン名を取得する。
     *
     * @return プラグイン名
     */
    public String getPluginName();

    /**
     * メッセージを受信する。
     * 
     * @param message メッセージ
     * @param session セッション
     * @return 応答メッセージ
     */
    default Object onMessage(Object message, Object session) { 
        return null;
    }
}
