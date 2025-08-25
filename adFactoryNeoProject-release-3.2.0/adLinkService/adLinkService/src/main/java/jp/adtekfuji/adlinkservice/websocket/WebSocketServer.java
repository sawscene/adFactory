/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.websocket;

import java.util.Objects;
import javax.websocket.server.ServerContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

/**
 * WebSocket サーバー
 * 
 * クライアントは ws://<ip>:18080/adfactory で接続
 *
 * @author s-heya
 */
public class WebSocketServer extends Thread {

    static private final Logger logger = LogManager.getLogger();

    private static Server server = null;
    private final int port;
    private final String path;
    private static WebSocketServer instance = null;

    /**
     * インスタンスを生成する。
     * 
     * @param port ポート番号
     * @param path パス
     */
    public static WebSocketServer createInstance(int port, String path) {
        if(Objects.isNull(instance)) {
            instance = new WebSocketServer(port, path);
        }
        return instance;
    }

    /**
     * インスタンスを取得する。
     * 
     * @return インスタンス
     */
    public static WebSocketServer getInstance() {
        return instance;
    }

    /**
     * コンストラクタ
     * 
     * @param port サーバーのポート番号
     * @param path URLパス
     */
    public WebSocketServer(int port, String path) {
        this.port = port;
        this.path = path;
    }

    /**
     * サーバーを起動する。
     */
    public void startServer()  {
        super.start();
    }

    /**
     * サーバーを終了する。
     */
    public void stopServer() {
        if (Objects.nonNull(server)) {
            try {
                server.stop();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    /**
     * サーバースレッド 
     */
    @Override
    public void run() {
        try {
            this.server = new Server();

            // ServerConnector
            ServerConnector connector = new ServerConnector(this.server);
            connector.setPort(this.port);
            server.addConnector(connector);

            // ServletContextHandler
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

            context.setContextPath(this.path);
            server.setHandler(context);

            // ServerContainer
            ServerContainer serverContainer = WebSocketServerContainerInitializer.initialize(context);
            serverContainer.addEndpoint(LinkEndpoint.class);

            // タイムアウト無制限 (初期値: 60秒)
            serverContainer.setDefaultMaxSessionIdleTimeout(0);

            // バイナリメッセージサイズ (初期値: 65536)
            // int size = serverContainer.getDefaultMaxBinaryMessageBufferSize();
            // serverContainer.setDefaultMaxBinaryMessageBufferSize(size * 10);

            // テキストメッセージサイズ (初期値: 65536)
            // size = serverContainer.getDefaultMaxTextMessageBufferSize();
            // serverContainer.setDefaultMaxBinaryMessageBufferSize(size * 10);

            logger.info("WebSocketServer join.");

            server.start();
            server.join();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            if (Objects.nonNull(server)) {
                try {
                    server.stop();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
    }

}
