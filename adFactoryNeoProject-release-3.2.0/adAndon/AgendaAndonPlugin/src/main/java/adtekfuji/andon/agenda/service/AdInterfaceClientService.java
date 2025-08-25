/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.service;

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
import java.util.Properties;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class AdInterfaceClientService extends Thread implements AdInterfaceClientListner {

    private static final Logger logger = LogManager.getLogger();

    private static final String KEY_SERVER_PATH = "adInterfaceServiceAddress";
    private static final String KEY_PORT_NUM = "adInterfaceServicePortNum";
    private static final String KEY_CRYPT = "adFactoryCrypt";
    private static final String KEY_RECONNECT_TIMER = "adInterfaceServiceReConnectTime";

    private final String servicePath;
    private final Integer portNum;
    private final Boolean isCrypt;
    private final Long reconnectTimer;
    private final AdInterfaceClientHandler clientHandler;
    private final EventLoopGroup eventLoop;

    public AdInterfaceClientService() {
        Properties properties = AdProperty.getProperties();
        this.servicePath = getProperty(properties, KEY_SERVER_PATH, "127.0.0.1");
        this.portNum = Integer.parseInt(getProperty(properties, KEY_PORT_NUM, String.valueOf(18005)));
        this.isCrypt = Boolean.parseBoolean(getProperty(properties, KEY_CRYPT, String.valueOf(true)));
        this.reconnectTimer = Long.parseLong(getProperty(properties, KEY_RECONNECT_TIMER, String.valueOf(5L)));
        this.clientHandler = new AdInterfaceClientHandler(this);
        this.eventLoop = new NioEventLoopGroup();
    }

    private String getProperty(Properties properties, String key, String def) {
        if (!properties.containsKey(key)) {
            properties.setProperty(key, def);
        }
        return properties.getProperty(key);
    }

    public AdInterfaceClientHandler getHandler() {
        return clientHandler;
    }

    public void startService() {
        super.start();
    }

    public void stopService() throws InterruptedException {
        logger.info("stopService start.");
        this.eventLoop.shutdownGracefully();
        this.clientHandler.closeAllChannel();
        //super.interrupt();
        //super.join();
        logger.info("stopService end.");
    }

    @Override
    public boolean getCrypt() {
        return isCrypt;
    }

    @Override
    public long getRecconectDelay() {
        return reconnectTimer;
    }

    @Override
    public void run() {
        Bootstrap bootstrap = new Bootstrap();
        try {
            ChannelFuture future = configureBootstrap(bootstrap, this.eventLoop).connect().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            logger.info(ex);
            this.eventLoop.shutdownGracefully();
        }
    }

    @Override
    public void connect(Bootstrap b) {
        b.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.cause() != null) {
                    logger.info("Failed to connect: " + future.cause());
                }
            }
        });
    }

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
    private SSLContext getSSLContext() {
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
            
            //KeyStore keystore = KeyStore.getInstance("JKS");
            //ClassLoader cl = Thread.currentThread().getContextClassLoader();
            //
            //try (InputStream is = cl.getResourceAsStream("key/client.keystore"))
            //{
            //    keystore.load(is, "adtekfuji".toCharArray());
            //}
            //
            //KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            //kmf.init(keystore, "adtekfuji".toCharArray());
            //context = SSLContext.getInstance("TLS");
            //
            //TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            //tmf.init(keystore);
            //context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (KeyStoreException | FileNotFoundException ex) {
            logger.error(ex, ex);
        } catch (IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException ex) {
            logger.error(ex, ex);
        }
        return context;
    }

}
