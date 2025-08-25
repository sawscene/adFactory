/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andonapp.comm;

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
    private long monitorId = 0;

    public AdInterfaceClientHandler(AdInterfaceClientListner reconnectListner) {
        this.reconnectListner = reconnectListner;
        this.isCrypt = reconnectListner.getCrypt();
    }

    public void setNoticeListner(NoticeCommandListner noticeListner) {
        this.noticeListner = noticeListner;
    }
    
    /**
     * モニターIDを設定する。
     * 
     * @param monitorId 
     */
    public void setMonitorId(long monitorId) {
        this.monitorId = monitorId;
    }

    @Override
    public synchronized void channelActive(ChannelHandlerContext chc) throws Exception {
        logger.info("connect: {},{},{}", this.monitorId, chc.channel().hashCode(), chc.channel());

        if (this.monitorId == 0) {
            return;
        }
        
        if (isCrypt) {
            chc.pipeline().get(SslHandler.class).handshakeFuture().addListener((Future<Channel> future) -> {
                logger.info("Your session is protected by " + chc.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() + " cipher suite");

                this.channel = chc.channel();
                this.sendCommand(new ConnectNoticeCommand(EquipmentTypeEnum.MONITOR, this.monitorId));
                if (this.isReady) {
                    logger.info("Recovered from communication problem.");
                    this.noticeListner.notice(new ResetCommand());
                }
                this.isReady = true;
            });
        } else {
            logger.info("Your session is not protected by SSL");
            
            this.channel = chc.channel();
            this.sendCommand(new ConnectNoticeCommand(EquipmentTypeEnum.MONITOR, this.monitorId));
            if (this.isReady) {
                logger.info("Recovered from communication problem.");
                this.noticeListner.notice(new ResetCommand());
            }
            this.isReady = true;
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
