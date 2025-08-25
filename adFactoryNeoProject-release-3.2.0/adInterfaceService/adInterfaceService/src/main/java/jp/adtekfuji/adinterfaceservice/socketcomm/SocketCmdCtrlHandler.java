/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.socketcomm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

/**
 * 外部システムのソケット通信ハンドラ
 *
 * @author ka.makihara
 */
@ChannelHandler.Sharable
public class SocketCmdCtrlHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private SocketLinkageCtrl socketLinkageCtrl;
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    /**
     * ソケット接続がリクエストされた
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public synchronized void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Connected of external system: " + ctx.toString());

        if (this.socketLinkageCtrl == null) {
            this.socketLinkageCtrl = SocketLinkageCtrl.getInstance();
        }
        this.socketLinkageCtrl.addCtrl(ctx);
    }

    /**
     * クライアントから切断された
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public synchronized void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Close of external system: " + ctx.toString());

        ctx.close();
        this.socketLinkageCtrl.deleteSocketCtrl(ctx);
    }

    /**
     * メッセージ受信処理
     *
     * @param ctx
     * @param msg
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) {
        String cmd = msg.readBytes(msg.readableBytes()).toString(Charset.forName("UTF-8"));
        String ret;

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jnode = mapper.readTree(cmd);
            ObjectNode node = jnode.deepCopy();
            ret = this.socketLinkageCtrl.execCommand(ctx.channel(), cmd, node);

        } catch (IOException ex) {
            logger.fatal(ex);
            StringBuilder sb = new StringBuilder();
            sb.append("{\"NO\":-99,\"ERROR\":-1,\"DETEIL\":");
            sb.append(ex.toString()).append("\"}");
            ret = sb.toString();

        } catch (Exception ex) {
            logger.fatal(ex);
            StringBuilder sb = new StringBuilder();
            sb.append("{\"NO\":-99,\"ERROR\":-1,\"DETEIL\":");
            sb.append(ex.toString()).append("\"}");
            ret = sb.toString();
        }

        if (!StringUtils.isEmpty(ret)) {
            logger.info("RETURN: " + ret);
            ctx.writeAndFlush(Unpooled.copiedBuffer(ret + "\n", CharsetUtil.UTF_8));
        }
    }

    /**
     * 通信エラーが発生
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn(cause, cause);
        ctx.close();
    }
}
