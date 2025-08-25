/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import jp.adtekfuji.adinterfaceservice.socketcomm.SocketLinkageCtrl;

/**
 * WebSocket 用のエンドポイント定義(adProductとの通信用・テスト)
 *
 * @author ka.Makihara 2019/10/08
 */
@ServerEndpoint("/adproduct")
public class ClientWebSocketEndpoint {

    private static SocketLinkageCtrl ctrl = null;

    /**
     * 
     * @param session 
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("onOpen " + session);
        ctrl = SocketLinkageCtrl.getInstance();

        ctrl.addClientWebSocket(session);
    }

    /**
     * 
     * @param message
     * @param session 
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // テキストデータを受信した場合
        //  ※一度に受信できるサイズが getDefaultMaxTextMessageBufferSize() で65536 バイトに定義されている
        //    このサイズを超えて受信すると、例外が発生して、OnClose() がよばれてセッションはクローズする
        System.out.println("Message: " + message);
        //try {
        //    ctrl.recvExecCommand(session,message);
        //} catch (IOException ex) {
        //    Logger.getLogger(ClientWebSocketEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        //}
    }

    /**
     * 
     * @param message
     * @param session 
     */
    @OnMessage
    public void onMessageBin(byte[] message, Session session) {
        // バイナリデータを受信した場合
        //  ※一度に受信できるサイズが getDefaultMaxBinaryMessageBufferSize() で65536 バイトに定義されている
        //String str = new String(message);
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
        ctrl.deleteSocketClient(session);
    }
}
