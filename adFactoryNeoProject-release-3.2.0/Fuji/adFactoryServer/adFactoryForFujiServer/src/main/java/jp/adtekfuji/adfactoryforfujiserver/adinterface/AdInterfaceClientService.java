/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.adinterface;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslHandler;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * サーバー用ネイティブサービス
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 */
public class AdInterfaceClientService extends Thread implements AdInterfaceClientListner {

    private final AdInterfaceClientHandler clientHandler;
    private final AdFactoryForFujiServerConfig config = new AdFactoryForFujiServerConfig();
    private final String servicePath;
    private final Integer portNum;
    private final Boolean isCrypt;
    private final int reconnectTimer;
    private static final Logger logger = LogManager.getLogger();

    public AdInterfaceClientService() {
        servicePath = config.getInterfaceServiceAddress();
        portNum = config.getInterfaceServicePortNum();
        isCrypt = config.getInterfaceServiceCrypt();
        reconnectTimer = config.getInterfaceServiceReconnectTime();
        clientHandler = new AdInterfaceClientHandler(this);
    }

    /**
     * ハンドリング用クラスを取得する
     *
     * @return
     */
    public AdInterfaceClientHandler getHandler() {
        return clientHandler;
    }

    /**
     * クライアントサービスを起動する。同時に親への通信の確立を開始する。
     *
     */
    public void startService() {
        start();
    }

    /**
     * 確立されている親との通信を終了する。
     *
     */
    public void stopService() {
        clientHandler.closeAllChannel();
        super.interrupt();
    }

    /**
     * 暗号化の有効無効を返す
     *
     */
    @Override
    public void run() {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup g = new NioEventLoopGroup();
        try {
            ChannelFuture future = configureBootstrap(bootstrap, g).connect().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            logger.info(ex);
            g.shutdownGracefully();
        }
    }

    /**
     * 再接続時間を返す
     *
     * @return
     */
    @Override
    public boolean getCrypt() {
        return isCrypt;
    }

    /**
     * サービス実行処理
     *
     * @return 
     */
    @Override
    public long getRecconectDelay() {
        return reconnectTimer;
    }

    /**
     * チャンネル接続処理
     *
     * @param b
     */
    @Override
    public void connect(Bootstrap b) {
        b.connect().addListener((ChannelFutureListener) (ChannelFuture future) -> {
            if (future.cause() != null) {
                logger.info("Failed to connect: " + future.cause());
            }
        });
    }

    /**
     * チャンネル接続設定の作成
     *
     * @param b 起動設定
     * @param g イベント領域
     * @return
     */
    @Override
    public Bootstrap configureBootstrap(Bootstrap b, EventLoopGroup g) {
        b
                .group(g)
                .channel(NioSocketChannel.class)
                .remoteAddress(servicePath, portNum)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        if (isCrypt == true) {
                            SSLEngine engine = getSSLContext().createSSLEngine();
                            engine.setUseClientMode(true);
                            pipeline.addLast("ssl", new SslHandler(engine));
                        }
                        pipeline.addLast("obj_decoder", new ObjectDecoder(ClassResolvers.weakCachingResolver(Thread.currentThread().getContextClassLoader())));
                        pipeline.addLast("obj_encoder", new ObjectEncoder());
                        pipeline.addLast("AdInterfaceClientHandler", clientHandler);
                    }
                });

        return b;
    }

    /**
     * SSLContext作成
     *
     * @return
     */
    static private SSLContext getSSLContext() {
        SSLContext context = null;
        try {
            KeyStore keystore = KeyStore.getInstance("JKS");
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            keystore.load(cl.getResourceAsStream("key/client.keystore"), "adtekfuji".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, "adtekfuji".toCharArray());
            context = SSLContext.getInstance("TLS");

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keystore);
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (KeyStoreException | FileNotFoundException ex) {
            logger.error(ex, ex);
        } catch (IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException ex) {
            logger.error(ex, ex);
        }
        return context;
    }

}
