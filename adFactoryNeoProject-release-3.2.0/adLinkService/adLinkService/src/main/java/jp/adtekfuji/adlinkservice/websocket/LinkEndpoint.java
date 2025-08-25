/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import jp.adtekfuji.adlinkservice.PluginContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * サーバーエンドポイント
 * 
 * @author s-heya
 */
@ServerEndpoint("/link")
public class LinkEndpoint {

    static private final Logger logger = LogManager.getLogger();
    
    /**
     * クライアントが接続した。
     * 
     * @param session セッション
     */
    @OnOpen
    public void onOpen(Session session) {
        logger.info("onOpen: session=" + session);
        PluginContainer.getInstance().onOpen(session);
    }

    /**
     * クライアントからメッセージを受信した。
     * 
     * @param message メッセージ
     * @param session セッション
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("onMessage: message=" + message + " session=" + session);
        PluginContainer.getInstance().onCommand(message, session);
    }

    /**
     * エラーが発生した。
     * 
     * @param t Throwable
     */
    @OnError
    public void onError(Throwable t) {
        logger.fatal("onError: ", t);
    }

    /**
     * クライアントが切断した。
     * 
     * @param session セッション
     */
    @OnClose
    public void onClose(Session session) {
        logger.info("onClose: session=" + session);
        PluginContainer.getInstance().onClose(session);
    }
}
