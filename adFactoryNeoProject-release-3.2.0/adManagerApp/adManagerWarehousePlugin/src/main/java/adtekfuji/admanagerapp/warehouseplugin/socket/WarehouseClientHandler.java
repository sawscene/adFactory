/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import jp.adtekfuji.adFactory.adinterface.command.RequestCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
@ChannelHandler.Sharable
public class WarehouseClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LogManager.getLogger();
    private static final String BHT_STX = "\u0002";
    private static final String BHT_ETX = "\u0003";
    private static final String BHT_MANAGER = "MANAGER   ";
    private static final String COMMA = ",";
    private static final int TIME_OUT = 10 * 1000;

    private Channel channel = null;
    private String responsce;
    private Boolean stopFlg = false;
    private final Boolean received = false;

    public WarehouseClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext chc) throws Exception {
        logger.info("connect:{},{}", chc.channel().hashCode(), chc.channel());
        channel = chc.channel();

    }

    @Override
    public void channelInactive(ChannelHandlerContext chc) throws Exception {
        logger.info("unconnect:{},{}", chc.channel().hashCode(), chc.channel());
        channel = null;
    }

    /**
     * メッセージ受信
     * 
     * @param chc
     * @param message
     * @throws Exception 
     */
    @Override
    protected void messageReceived(ChannelHandlerContext chc, String message) throws Exception {
        logger.info("messageReceived: {},{},{}", chc.channel().hashCode(), chc.channel(), message);
        synchronized (received) {
            responsce = message;
            received.notify();
        }
    }

    /**
     * リクエストコマンドを送信する。
     * 
     * @param command コマンド
     * @param message メッセージ
     * @return 処理結果
     * @throws java.lang.Exception
     */
    public String send(String command, String message) throws Exception {
        logger.info("send:{},{}", command, message);

        if (Objects.isNull(this.channel)) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(BHT_STX);
        sb.append(BHT_MANAGER);
        sb.append(COMMA);
        sb.append(command);
        if (Objects.nonNull(message)) {
            sb.append(COMMA);
            sb.append(message);
        }
        sb.append(BHT_ETX);

        RequestCommand request = new RequestCommand(sb.toString());

        synchronized (received) {
            this.responsce = null;
            this.channel.writeAndFlush(request);

            try {
                this.received.wait(TIME_OUT);
            } catch (InterruptedException ex) {
                logger.fatal(ex, ex);
            }
        }

        return responsce;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("channel exceptionCaught: {}", cause);
        ctx.close();
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        logger.info("channel unregistered");
        if (!stopFlg) {
            final EventLoop loop = ctx.channel().eventLoop();
            loop.schedule(new Runnable() {
                @Override
                public void run() {
                    logger.info("Reconnecting to server");
                    WarehouseClientService.connect(WarehouseClientService.configureBootstrap(new Bootstrap(), loop));
                }
            }, WarehouseClientService.reconnectDelay, TimeUnit.SECONDS);
        }
    }

    /**
     * 全てチャネルを閉じる
     */
    public void closeAllChannel() {
        logger.info("close all channels");
        stopFlg = true;
        if (Objects.nonNull(channel)) {
            channel.eventLoop().shutdownGracefully();
            channel = null;
        }
    }

}
