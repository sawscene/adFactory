/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import jp.adtekfuji.adinterfaceservice.socketcomm.SocketLinkageCtrl;
import org.apache.logging.log4j.LogManager;

/**
 * WebSocket 用のエンドポイント定義(上位システムとのWebSocket接続用)
 *
 * @author ka.Makihara 2019/10/08
 */
@ServerEndpoint("/ctrl")
public class CtrlWebSocketEndpoint {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();
    private static SocketLinkageCtrl ctrl = null;

    /**
     * 
     * @param session 
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("onOpen " + session);
        ctrl = SocketLinkageCtrl.getInstance();
        ctrl.addCtrlWebSocket(session);
    }

    /**
     * 
     * @param message
     * @param session
     * @return 
     */
    @OnMessage
    public String onMessage(String message, Session session) {
        // テキストデータを受信した場合
        //  ※一度に受信できるサイズが getDefaultMaxTextMessageBufferSize() で65536 バイトに定義されている
        //    このサイズを超えて受信すると、例外が発生して、OnClose() がよばれてセッションはクローズする
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jnode = mapper.readTree(message);
            ObjectNode node = jnode.deepCopy();

            String ret = ctrl.execCommand(null, message, node) + "\n";

            return ret; //String のリターンで、送信側で受信できる

        } catch (IOException ex) {
            logger.fatal(ex);
            return ex.toString();
        } catch (Exception ex) {
            logger.fatal(ex);
            return ex.toString();
        }
    }

    /**
     * 
     * @param message
     * @param session
     * @return 
     */
    @OnMessage
    public String onMessageBin(byte[] message, Session session) {
        // バイナリデータを受信した場合
        //  ※一度に受信できるサイズが getDefaultMaxBinaryMessageBufferSize() で65536 バイトに定義されている
        try {
            String str = new String(message);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jnode = mapper.readTree(str);
            ObjectNode node = jnode.deepCopy();

            String ret = ctrl.execCommand(null, str, node) + "\n";
            return ret;

        } catch (Exception ex) {
            Logger.getLogger(CtrlWebSocketEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        }
    }

    /**
     * 
     * @param t 
     */
    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    /**
     * 
     * @param session 
     */
    @OnClose
    public void onClose(Session session) {
        System.out.println("onClose " + session);
        ctrl.deleteSocketCtrl(session);
    }
}
