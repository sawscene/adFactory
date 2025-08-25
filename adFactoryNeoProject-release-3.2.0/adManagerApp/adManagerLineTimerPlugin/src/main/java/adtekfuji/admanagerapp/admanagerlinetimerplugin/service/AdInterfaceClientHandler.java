/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.admanagerlinetimerplugin.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import jp.adtekfuji.adFactory.adinterface.command.ConnectNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
@ChannelHandler.Sharable
public class AdInterfaceClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger();
    private final AdInterfaceClientListner reconnectListner;
    private NoticeCommandListner noticeListner = null;
    private final Boolean isCrypt;
    private Channel channel = null;
    private boolean isReady;

    public AdInterfaceClientHandler(AdInterfaceClientListner reconnectListner) {
        this.reconnectListner = reconnectListner;
        this.isCrypt = reconnectListner.getCrypt();
    }

    public void setNoticeListner(NoticeCommandListner noticeListner) {
        this.noticeListner = noticeListner;
    }

    @Override
    public synchronized void channelActive(ChannelHandlerContext chc) throws Exception {
        logger.info("connect:{},{}", chc.channel().hashCode(), chc.channel());

        if (isCrypt == true) {
            chc.pipeline().get(SslHandler.class).handshakeFuture().addListener(new GenericFutureListener<Future<Channel>>() {
                        @Override
                        public void operationComplete(Future<Channel> future) throws Exception {
                            logger.info("Your session is protected by " + chc.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() + " cipher suite");
                            channel = chc.channel();
                            sendCommand(new ConnectNoticeCommand(EquipmentTypeEnum.MONITOR));
                            if (isReady) {
                                logger.info("Recovered from communication problem.");
                                noticeListner.notice(new ResetCommand());
                            }
                            isReady = true;
                        }
                    });
        } else {
            logger.info("Your session is not protected by SSL");
            channel = chc.channel();
            sendCommand(new ConnectNoticeCommand(EquipmentTypeEnum.MONITOR));
            if (isReady) {
                logger.info("Recovered from communication problem.");
                noticeListner.notice(new ResetCommand());
            }
            isReady = true;
        }
    }

    public synchronized boolean sendCommand(Object command) {
        logger.info("sendCommand:{}", command);
        if (Objects.nonNull(channel)) {
            channel.writeAndFlush(command);
            return true;
        }
        logger.fatal("not connected server!!!");
        return false;
    }

    @Override
    public synchronized void channelInactive(ChannelHandlerContext chc) throws Exception {
        logger.info("unconnect:{},{}", chc.channel().hashCode(), chc.channel());
        channel = null;
        //super.channelInactive(chc);
    }

    @Override
    public synchronized void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.fatal("exceptionCaught:{} {}", ctx, cause);
        ctx.close();
    }

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
     * コマンド受信
     *
     * @param chc
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext chc, Object msg) {
        logger.info("channelRead:{},{},{}", chc.channel().hashCode(), chc.channel(), msg.toString());
        noticeListner.notice(msg);
    }

    /**
     * Channelをクローズ
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
