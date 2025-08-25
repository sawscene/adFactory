/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservicecommon.plugin;

import io.netty.channel.Channel;
import java.util.List;

/**
 * ソケット通信ハンドラ.
 *
 * @author ke.yokoi
 */
public interface SocketServerHandlerInterface {

    /**
     *
     */
    public default void startHandler() {
        // do nothing
    }

    /**
     *
     */
    public default void stopHandler() {
        // do nothing
    }

    /**
     *
     * @param channel
     */
    public default void connectChannel(Channel channel) {
        // do nothing
    }

    /**
     *
     * @param channel
     */
    public default void disconnectChannel(Channel channel) {
        // do nothing
    }

    /**
     * 受信するコマンドのリストを返却してください.
     *
     * @return 受信するコマンドのリスト
     */
    public List<Class> getCommandCollection();

    /**
     * 受信したコマンドを処理してください.
     *
     * @param channel
     * @param command
     */
    public void recvCommand(Channel channel, Object command);
}
