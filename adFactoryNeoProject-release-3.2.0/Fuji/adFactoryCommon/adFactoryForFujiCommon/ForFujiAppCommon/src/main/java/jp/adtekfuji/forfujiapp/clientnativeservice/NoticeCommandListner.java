/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.clientnativeservice;

/**
 * コマンド通知用リスナー
 * ：通知を受信したい場合はこのクラスを実装して使ってください。
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.8.thr
 */
public interface NoticeCommandListner {

    /**
     * コマンド受信時の呼び出し処理
     * 
     * @param command 
     */
    public void notice(Object command);

}
