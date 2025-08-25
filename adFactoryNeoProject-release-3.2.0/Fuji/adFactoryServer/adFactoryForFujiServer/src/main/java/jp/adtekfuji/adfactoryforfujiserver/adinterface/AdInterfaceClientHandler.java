/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.adinterface;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.ejb.EJB;
import jp.adtekfuji.adfactoryforfujiserver.service.UnitEntityFacadeREST;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * サーバー用ネイティブサービス受信時のハンドル処理
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 */
@ChannelHandler.Sharable
public class AdInterfaceClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger();
    private final AdInterfaceClientListner reconnectListner;
    private final Boolean isCrypt;
    private Channel channel = null;

    @EJB
    private UnitEntityFacadeREST unitEntityFacadeREST;

    public AdInterfaceClientHandler(AdInterfaceClientListner reconnectListner) {
        this.reconnectListner = reconnectListner;
        this.isCrypt = reconnectListner.getCrypt();
    }

    /**
     * 指定されたチャンネルを有効にする
     *
     * @param chc セッション
     * @throws Exception
     */
    @Override
    public synchronized void channelActive(ChannelHandlerContext chc) throws Exception {
        logger.info("connect:{},{}", chc.channel().hashCode(), chc.channel());

        if (isCrypt == true) {
            chc.pipeline().get(SslHandler.class).handshakeFuture().addListener((Future<Channel> future) -> {
                logger.info("Your session is protected by " + chc.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() + " cipher suite");
                channel = chc.channel();
            });
        } else {
            logger.info("Your session is not protected by SSL");
            channel = chc.channel();
        }
    }

    /**
     * コマンドをほかのサーバー・クライアントに送信する。
     *
     * @param command
     * @return
     */
    public synchronized boolean sendCommand(Object command) {
        logger.info("sendCommand:{}", command);
        if (Objects.nonNull(channel)) {
            channel.writeAndFlush(command);
            return true;
        }
        logger.fatal("not connected server!!!");
        return false;
    }

    /**
     * 指定されたチャンネルを無効にする
     *
     * @param chc チャンネル
     */
    @Override
    public synchronized void channelInactive(ChannelHandlerContext chc) {
        logger.info("unconnect:{},{}", chc.channel().hashCode(), chc.channel());
        channel = null;
    }

    /**
     * 通信中に発生したエラーを捕まえて通知する。その後チャンネルを無効する。
     *
     * @param ctx
     * @param cause
     */
    @Override
    public synchronized void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.fatal("exceptionCaught:{} {}", ctx, cause);
        logger.fatal(ctx, cause);
        ctx.close();
    }

    /**
     * 未登録の指定されたチャンネルを登録する
     *
     * @param ctx チャンネル
     * @throws Exception
     */
    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        logger.info("channel unregistered");
        final EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(() -> {
            logger.info("Reconnecting to server ");
            reconnectListner.connect(reconnectListner.configureBootstrap(new Bootstrap(), loop));
        }, reconnectListner.getRecconectDelay(), TimeUnit.SECONDS);
    }

    /**
     * 指定されたチャンネルからコマンドを受信する
     *
     * @param chc チャンネル
     * @param msg メッセージ
     */
    @Override
    public void channelRead(ChannelHandlerContext chc, Object msg) {
        logger.info("channelRead:{},{},{}", chc.channel().hashCode(), chc.channel(), msg.toString());
        
        

    }

    /**
     * 全てのチャンネルをクローズ
     *
     */
    public void closeAllChannel() {
        logger.info("close all channels");
        if (Objects.nonNull(channel)) {
            channel.eventLoop().shutdownGracefully();
            channel = null;
        }
    }

}
