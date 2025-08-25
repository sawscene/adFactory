/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.adinterface;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;

/**
 * クライアントサービス通信インターフェース
 * 接続・切断処理が変更される場合はこのクラスを実装して使用してください。
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.8.thr
 */
public interface AdInterfaceClientListner {

    public boolean getCrypt();

    public long getRecconectDelay();

    public void connect(Bootstrap bootstrap);

    public Bootstrap configureBootstrap(Bootstrap b, EventLoopGroup g);

}
