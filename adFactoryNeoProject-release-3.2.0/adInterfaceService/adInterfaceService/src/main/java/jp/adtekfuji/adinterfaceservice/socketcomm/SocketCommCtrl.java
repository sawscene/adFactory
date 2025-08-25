/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.socketcomm;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ka.makihara
 */
public class SocketCommCtrl extends Thread {

    private static final Logger logger = LogManager.getLogger();
    private final Integer portNum;
    private final SimpleChannelInboundHandler<ByteBuf> handler;

    /**
     *
     * @param port
     * @param hdl
     */
    public SocketCommCtrl(Integer port, SimpleChannelInboundHandler<ByteBuf> hdl) {
        this.portNum = port;
        this.handler = hdl;
    }

    /**
     *
     */
    public void startService() {
        logger.info("SocketCommCtrl start...");
        super.start();
    }

    /**
     *
     */
    public void stopService() {
        logger.info("SocketCommCtrl stoped...");
        try {
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

                            // Application Logic Handler
                            //pipeline.addLast("commCtrlHandler", new SocketCmdCtrlHandler() );
                            pipeline.addLast("commCtrlHandler", handler);
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
    public SimpleChannelInboundHandler<ByteBuf> getHandler() {
        return this.handler;
    }
}
