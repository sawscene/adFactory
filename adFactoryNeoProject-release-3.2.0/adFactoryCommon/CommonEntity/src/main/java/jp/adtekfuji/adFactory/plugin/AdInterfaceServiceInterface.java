/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.plugin;

import jp.adtekfuji.adFactory.adinterface.command.RequestCommand;

/**
 *
 * @author ke.yokoi
 */
public interface AdInterfaceServiceInterface {

    /**
     * 
     * @throws Exception 
     */
    public void startService() throws Exception;

    /**
     * 
     * @throws Exception 
     */
    public void stopService() throws Exception;

    /**
     * サービス名を取得する。
     *
     * @return
     */
    public String getServiceName();

    /**
     * 実績通知コマンドを受信した。
     *
     * @param command
     */
    default void noticeActualCommand(Object command) {}

    /**
     * コマンド(実績通知コマンド以外)を受信した。
     *
     * @param command
     */
    default void notice(Object command) {}

    /**
     * リクエストを受信した。
     * 
     * @param request リクエスト
     * @return 
     */
    default Object request(RequestCommand request) {
        return null;
    }

    /**
     * 日次処理を実行する。
     */
    default void execDaily() {}
}
