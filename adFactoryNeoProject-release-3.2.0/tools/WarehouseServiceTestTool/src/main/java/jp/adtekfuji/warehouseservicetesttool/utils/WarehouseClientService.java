/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.warehouseservicetesttool.utils;

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
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
public class WarehouseClientService extends Thread {

    private final Logger logger = LogManager.getLogger();

    private final WarehouseClientHandler clientHandler;
 
    private String remoteAddress = "127.0.0.1";
    private static final Integer portNumber = 8500;

    public WarehouseClientService(String remoteAddress , String serialNumber, String ipAddress) {
        this.remoteAddress = remoteAddress;
        this.clientHandler = new WarehouseClientHandler(serialNumber, ipAddress);
    }

    public WarehouseClientHandler getClient() {
        return this.clientHandler;
    }

    public void startService() {
        logger.info("start WarehouseClientService");
        this.start();
    }

    public void stopService() {
        logger.info("stop WarehouseClientService");
        try {
            this.clientHandler.closeAllChannel();
            super.interrupt();
            super.join();
        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }

    @Override
    public void run() {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup g = new NioEventLoopGroup();
        try {
            ChannelFuture future = configureBootstrap(bootstrap, g).connect().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            logger.info(ex);
        } finally {
            g.shutdownGracefully();
        }
    }

    private Bootstrap configureBootstrap(Bootstrap bootstrap, EventLoopGroup g) {
        bootstrap
                .group(g)
                .channel(NioSocketChannel.class)
                .remoteAddress(remoteAddress, portNumber)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("decoder", new StringDecoder());
                        pipeline.addLast("encoder", new StringEncoder());
                        pipeline.addLast("WarehouseClientHandler", clientHandler);
                    }
                });
        
        return bootstrap;
    }

    private void connect(Bootstrap b) {
        b.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.cause() != null) {
                    logger.info("Failed to connect: " + future.cause());
                }
            }
        });
    }
}
