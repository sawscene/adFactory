/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.socketcomm;

import jp.adtekfuji.adinterfaceservice.AdInterfaceConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
import java.util.List;
import java.util.Objects;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class SocketCommBaseServer extends Thread {

    private static final Logger logger = LogManager.getLogger();
    private static SocketCommBaseServer instance = null;
    private final SocketCommBaseHandler commBaseHandler;
    private final Integer portNum;
    private final Boolean crypt;

    /**
     *
     * @param plugin
     */
    private SocketCommBaseServer(List<AdInterfaceServiceInterface> plugin) {
        AdInterfaceConfig config = AdInterfaceConfig.getInstance();
        this.portNum = config.getPortNum();
        this.crypt = config.getCrypt();
        this.commBaseHandler = new SocketCommBaseHandler(plugin);
    }

    /**
     *
     * @param plugin
     */
    static public void createInstance(List<AdInterfaceServiceInterface> plugin) {
        if (Objects.isNull(instance)) {
            instance = new SocketCommBaseServer(plugin);
        }
    }

    /**
     *
     * @return
     */
    static public SocketCommBaseServer getInstance() {
        return instance;
    }

    /**
     *
     */
    public void startService() {
        logger.info("SocketCommBaseServer start...");
        super.start();
        this.commBaseHandler.startHandler();
    }

    /**
     *
     */
    public void stopService() {
        logger.info("SocketCommBaseServer stoped...");
        try {
            this.commBaseHandler.stopHandler();
            super.interrupt();
            super.join();
        } catch (InterruptedException ex) {
        }
    }

    /**
     *
     */
    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(this.portNum)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            if (crypt == true) {
                                logger.info("use ssl connect");
                                SSLEngine engine = getSSLContext().createSSLEngine();
                                engine.setUseClientMode(false);
                                pipeline.addLast("ssl", new SslHandler(engine));
                            }

                            // Application Logic Handler
                            pipeline.addLast("obj_decoder", new ObjectDecoder(ClassResolvers.weakCachingResolver(Thread.currentThread().getContextClassLoader())));
                            pipeline.addLast("obj_encoder", new ObjectEncoder());
                            pipeline.addLast("commBaseHandler", commBaseHandler);
                        }
                    });
            // Start the server.
            ChannelFuture future = bootstrap.bind().sync();
            // Wait until the server socket is closed.
            future.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            //logger.fatal(ex, ex);
        } finally {
            try {
                // Shut down all event loops to terminate all threads.
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                // Wait until all threads are terminated.
                bossGroup.terminationFuture().sync();
                workerGroup.terminationFuture().sync();
            } catch (InterruptedException ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    /**
     *
     * @return
     */
    private SSLContext getSSLContext() {
        SSLContext context = null;
        try {
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(getClass().getResourceAsStream("/key/adtekfuji.keystore"), "adtekfuji".toCharArray());
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
