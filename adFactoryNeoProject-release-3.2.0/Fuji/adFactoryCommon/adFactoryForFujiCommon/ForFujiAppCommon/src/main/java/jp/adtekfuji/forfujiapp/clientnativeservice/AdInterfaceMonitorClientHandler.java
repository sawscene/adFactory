/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.clientnativeservice;

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
import jp.adtekfuji.adFactory.adinterface.command.ConnectNoticeCommand;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * モニター用クライアントサービスハンドリング処理
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.8.thr
 */
@ChannelHandler.Sharable
public class AdInterfaceMonitorClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger();
    private final AdInterfaceClientListner reconnectListner;
    private NoticeCommandListner noticeListner = null;
    private final Boolean isCrypt;
    private Channel channel = null;

    public AdInterfaceMonitorClientHandler(AdInterfaceClientListner reconnectListner) {
        this.reconnectListner = reconnectListner;
        this.isCrypt = reconnectListner.getCrypt();
    }

    /**
     * 受信時に呼び出す先のリスナーを設定
     * 
     * @param noticeListner 
     */
    public void setNoticeListner(NoticeCommandListner noticeListner) {
        this.noticeListner = noticeListner;
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
                sendCommand(new ConnectNoticeCommand(EquipmentTypeEnum.MONITOR));
            });
        } else {
            logger.info("Your session is not protected by SSL");
            channel = chc.channel();
            sendCommand(new ConnectNoticeCommand(EquipmentTypeEnum.MONITOR));
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
     * @throws Exception 
     */
    @Override
    public synchronized void channelInactive(ChannelHandlerContext chc) throws Exception {
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
        loop.schedule(new Runnable() {
            @Override
            public void run() {
                logger.info("Reconnecting to server ");
                reconnectListner.connect(reconnectListner.configureBootstrap(new Bootstrap(), loop));
            }
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
        noticeListner.notice(msg);
    }

    /**
     * 全てのチャンネルをクローズ
     * 
     */
    public void closeAllChannel() {
        logger.info("closeAllChannel start.");
        if (Objects.nonNull(channel)) {
            channel.close();
            logger.info("closed all channels.");
        }
        logger.info("closeAllChannel end.");
    }
}
