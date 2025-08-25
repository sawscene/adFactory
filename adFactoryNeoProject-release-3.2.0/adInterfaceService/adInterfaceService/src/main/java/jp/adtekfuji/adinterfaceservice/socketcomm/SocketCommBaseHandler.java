/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tooｔls | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.socketcomm;

import adtekfuji.plugin.PluginLoader;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.ArrayList;
import java.util.List;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import jp.adtekfuji.adinterfaceservice.broadcast.BroadcastCommHandler;
import jp.adtekfuji.adinterfaceservicecommon.plugin.SocketServerHandlerInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
@ChannelHandler.Sharable
public class SocketCommBaseHandler extends SimpleChannelInboundHandler {

    private final List<SocketCommContiner> socketCommContiner = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger();

    /**
     *
     * @param plugin
     */
    public SocketCommBaseHandler(List<AdInterfaceServiceInterface> plugin) {
        //標準で持つ.
        this.socketCommContiner.add(new SocketCommContiner(new BroadcastCommHandler(plugin)));
        //プラグインで追加.
        List<SocketServerHandlerInterface> handlers = PluginLoader.load(SocketServerHandlerInterface.class);
        for (SocketServerHandlerInterface handler : handlers) {
            this.socketCommContiner.add(new SocketCommContiner(handler));
        }
    }

    /**
     *
     */
    public synchronized void startHandler() {
        for (SocketCommContiner continer : this.socketCommContiner) {
            logger.info("start:{}", continer);
            continer.startHandler();
        }
    }

    /**
     *
     */
    public synchronized void stopHandler() {
        for (SocketCommContiner continer : this.socketCommContiner) {
            logger.info("stop:{}", continer);
            continer.stopHandler();
        }
    }

    /**
     *
     * @param chc
     * @throws Exception
     */
    @Override
    public synchronized void channelActive(ChannelHandlerContext chc) throws Exception {
        logger.info("connect:{},{}", chc.channel().hashCode(), chc.channel());
        for (SocketCommContiner continer : this.socketCommContiner) {
            continer.connectChannel(chc.channel());
        }
    }

    /**
     *
     * @param chc
     * @throws Exception
     */
    @Override
    public synchronized void channelInactive(ChannelHandlerContext chc) throws Exception {
        logger.info("unconnect:{},{}", chc.channel().hashCode(), chc.channel());
        for (SocketCommContiner continer : this.socketCommContiner) {
            continer.disconnectChannel(chc.channel());
        }
    }

    /**
     * メッセージ受信処理
     * 
     * @param chc
     * @param command
     * @throws Exception
     */
    @Override
    protected synchronized void messageReceived(ChannelHandlerContext chc, Object command) throws Exception {
        logger.info("messageReceived: {},{},{}", chc.channel().hashCode(), chc.channel(), command);
        for (SocketCommContiner continer : this.socketCommContiner) {
            continer.acceptRecv(chc.channel(), command);
        }
    }

    /**
     *
     * @param chc
     * @param cause
     */
    @Override
    public synchronized void exceptionCaught(ChannelHandlerContext chc, Throwable cause) {
        logger.info("exceptionCaught:{},{},{}", chc.channel().hashCode(), chc.channel(), cause);
        for (SocketCommContiner continer : this.socketCommContiner) {
            continer.disconnectChannel(chc.channel());
        }
        chc.close();
    }
}
