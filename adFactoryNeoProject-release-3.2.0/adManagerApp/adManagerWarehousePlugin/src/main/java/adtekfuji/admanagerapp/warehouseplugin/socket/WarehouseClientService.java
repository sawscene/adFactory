/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.socket;

import adtekfuji.property.AdProperty;
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
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.Properties;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 倉庫案内サービスのクライアント
 * 
 * @author s-heya
 */
public class WarehouseClientService extends Thread {

    private static final String SERVICE_HOST_KEY = "WarehouseServiceHost";
    private static final String SERVICE_PORT_KEY = "WarehouseServicePort";
    private static final String SERVICE_DELAY_KEY = "reconnectDelay";

    private static final Logger logger = LogManager.getLogger();
    private static WarehouseClientService instance = null;
    private static final WarehouseClientHandler handler = new WarehouseClientHandler();
    public static Integer reconnectDelay = null;
    private static String host = null;
    private static Integer port = null;

    private EventLoopGroup workGroup = null;

    /**
     * コンストラクタ
     */
    private WarehouseClientService() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(SERVICE_HOST_KEY)) {
            properties.setProperty(SERVICE_HOST_KEY, "127.0.0.1");
        }
        if (!properties.containsKey(SERVICE_PORT_KEY)) {
            properties.setProperty(SERVICE_PORT_KEY, "18005");
        }
        if (!properties.containsKey(SERVICE_DELAY_KEY)) {
            properties.setProperty(SERVICE_DELAY_KEY, "10");
        }
        host = properties.getProperty(SERVICE_HOST_KEY);
        port = Integer.parseInt(properties.getProperty(SERVICE_PORT_KEY));
        reconnectDelay = Integer.parseInt(properties.getProperty(SERVICE_DELAY_KEY));
    }

    /**
     * インスタンスを生成する。
     */
    static public void createInstance() {
        if (Objects.isNull(instance)) {
            instance = new WarehouseClientService();
        }
    }

    /**
     * インスタンスを取得する。
     * 
     * @return インスタンス
     */
    static public WarehouseClientService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new WarehouseClientService();
            instance.startService();
        }
        return instance;
    }

    public WarehouseClientHandler getClient() {
        return handler;
    }

    /**
     * サービスを開始する。
     */
    public void startService() {
        logger.info("Srart WarehouseClientService.");
        start();
    } 

    /**
     * サービスを停止する。
     */
    public void stopService() {
        logger.info("Stop WarehouseClientService.");
        try {
            handler.closeAllChannel();
            workGroup.shutdownGracefully();
            super.interrupt();
            super.join();
        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * スレッドを実行する。
     */
    @Override
    public void run() {
        Bootstrap bootstrap = new Bootstrap();
        workGroup = new NioEventLoopGroup();
        try {
            ChannelFuture future = configureBootstrap(bootstrap, workGroup).connect().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            logger.info(ex);
            workGroup.shutdownGracefully();
        }
    }

    /**
     * Bootstrapを初期化する。
     * 
     * @param bootstrap Bootstrap
     * @param group EventLoopGroup
     * @return Bootstrap
     */
    public static Bootstrap configureBootstrap(Bootstrap bootstrap, EventLoopGroup group) {
        bootstrap
            .group(group)
            .channel(NioSocketChannel.class)
            .remoteAddress(host, port)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    //if (isCrypt()) {
                        SSLEngine engine = getSSLContext().createSSLEngine();
                        engine.setUseClientMode(true);
                        pipeline.addLast("ssl", new SslHandler(engine));
                    //}
                    pipeline.addLast("obj_decoder", new ObjectDecoder(ClassResolvers.weakCachingResolver(Thread.currentThread().getContextClassLoader())));
                    pipeline.addLast("obj_encoder", new ObjectEncoder());
                    pipeline.addLast("WarehouseClientHandler", handler);
                }
            });

        return bootstrap;
    }

    /**
     * 倉庫案内サービスに接続する。
     * 
     * @param bootstrap Bootstrap
     */
    public static void connect(Bootstrap bootstrap) {
        bootstrap.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.cause() != null) {
                    logger.info("Failed to connect: " + future.cause());
                }
            }
        });
    }

    /**
     * SSLContextを取得する。
     *
     * @return SSLContext
     */
    private static SSLContext getSSLContext() {
        SSLContext context = null;
        try {
            TrustManager[] certs = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }
                }
            };
            
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            keystore.load(cl.getResourceAsStream("/key/newcert.p12"), "adtekfuji".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, "adtekfuji".toCharArray());
            context = SSLContext.getInstance("TLS");

            context.init(kmf.getKeyManagers(), certs, new SecureRandom());
 
        } catch (KeyStoreException | FileNotFoundException ex) {
            logger.error(ex, ex);
        } catch (IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException ex) {
            logger.error(ex, ex);
        }
        return context;
    }
    
}
