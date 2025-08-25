/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.adinterface;

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
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import jp.adtekfuji.adfactoryserver.common.ServiceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class AdInterfaceClientService extends Thread implements AdInterfaceClientListner {

    private final AdInterfaceClientHandler clientHandler;
    private final ServiceConfig config = ServiceConfig.getInstance();
    private final String servicePath;
    private final Integer portNum;
    private final Boolean isCrypt;
    private final int reconnectTimer;
    private static final Logger logger = LogManager.getLogger();

    private EventLoopGroup workGroup = null;

    public AdInterfaceClientService() {
        servicePath = config.getServiceAddress();
        portNum = config.getServicePortNum();
        isCrypt = config.getServiceCrypt();
        reconnectTimer = config.getServiceReconnectTime();
        clientHandler = new AdInterfaceClientHandler(this);
    }

    public AdInterfaceClientHandler getHandler() {
        return clientHandler;
    }

    public void startService() {
        start();
    }

    public void stopService() {
        clientHandler.closeAllChannel();
        workGroup.shutdownGracefully();
        super.interrupt();
    }

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

    @Override
    public boolean getCrypt() {
        return isCrypt;
    }

    @Override
    public long getRecconectDelay() {
        return reconnectTimer;
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
    static private SSLContext getSSLContext() {
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
            //keystore.load(cl.getResourceAsStream("key/client.keystore"), "adtekfuji".toCharArray());
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
