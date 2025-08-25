/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.websocket;

import java.util.Objects;
import jp.adtekfuji.adinterfaceservice.websocket.form.output.FormOutputEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

/**
 * WebSocket サーバー 接続は ws://host名:port番号/(path)/ctrl
 * "ctrl"はエンドポイント(CtrlWebSocketEndpoint)で定義
 *
 * @author ka.Makihara 2019/10/09
 */
public class CtrlWebSocketServer extends Thread {

    static private final Logger logger = LogManager.getLogger();

    private static Server server = null;
    // ポート番号
    private final int port;
    // パス
    private final String path;

    private static CtrlWebSocketServer instance = null;

    /**
     * インスタンス作成
     * @param port ポート番号
     * @param path パス
     */
    static public void createInstance(int port, String path) {
        if(Objects.isNull(CtrlWebSocketServer.instance)) {
            CtrlWebSocketServer.instance = new CtrlWebSocketServer(port, path);
        }
    }

    /**
     * インスタンス取得
     * @return インスタンス
     */
    static public CtrlWebSocketServer getInstance() {
        return CtrlWebSocketServer.instance;
    }

    /**
     *
     * @param port ポート
     * @param path パス
     */
    public CtrlWebSocketServer(int port, String path) {
        this.port = port;
        this.path = path;
    }

    /**
     * *
     * サービス開始
     */
    public void startService()  {
        super.start();
    }

    /**
     * サービス停止
     */
    public void stopService() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    /**
     * WebSocket実行タスク
     */
    @Override
    public void run() {
        try {
            server = new Server();

            ServerConnector connector = new ServerConnector(server);

            connector.setPort(this.port);
//            connector.setHost("127.0.0.1");

            server.addConnector(connector);

            //ServletContextHandler 生成
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

            context.setContextPath(this.path);
            server.setHandler(context);

            //ServerContainer生成
            ServerContainer wsContainer = WebSocketServerContainerInitializer.initialize(context);
            wsContainer.addEndpoint(FormOutputEndpoint.class); // 実績出力用 End Point

            //タイムアウト無しに設定
            //  ※設定なしでは、デフォルトで60秒でタイムアウトして、セッションがクローズされる
            wsContainer.setDefaultMaxSessionIdleTimeout(0);

            //一度に受け取れるバイナリメッセージサイズ(default::65536)
            int bb = wsContainer.getDefaultMaxBinaryMessageBufferSize();
            wsContainer.setDefaultMaxBinaryMessageBufferSize(bb * 10);

            //一度に受け取れるテキストメッセージサイズ(default::65536)
            bb = wsContainer.getDefaultMaxTextMessageBufferSize();
            wsContainer.setDefaultMaxBinaryMessageBufferSize(bb * 10);
            // とりあえず、10倍にしておく

            //server起動
            server.start();
            server.join();

            System.out.println("Server Join");
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            if (server != null) {
                try {
                    server.stop();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
    }

}
