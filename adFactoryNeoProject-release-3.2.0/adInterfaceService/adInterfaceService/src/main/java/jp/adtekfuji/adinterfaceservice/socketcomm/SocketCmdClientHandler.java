/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.socketcomm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * adProductのソケット通信ハンドラ
 *
 * @author ka.makihara
 */
@ChannelHandler.Sharable
public class SocketCmdClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     *
     */
    class ClientThread extends Thread {

        private final ChannelHandlerContext ctx;
        private final LinkedBlockingQueue<TCommand> sendQueue;
        private final LinkedBlockingQueue<String> recvQueue;
        private Boolean threaded = true;
        private String equipmentName;

        /**
         * コンストラクタ
         *
         * @param ctx
         */
        public ClientThread(ChannelHandlerContext ctx) {
            this.ctx = ctx;
            this.sendQueue = new LinkedBlockingQueue<>();
            this.recvQueue = new LinkedBlockingQueue<>();
        }

        /**
         * 送受信処理
         */
        @Override
        public void run() {
            try {
                Thread.sleep(1000L);

                this.sendQueue.clear();
                this.recvQueue.clear();

                // 設備名を取得する
                this.ctx.writeAndFlush(Unpooled.copiedBuffer("{\"NO\":65535,\"CMD\":\"get\",\"PARAM\":[\"termname\"]}", CharsetUtil.UTF_8));

                synchronized (recvQueue) {
                    String res;
                    try {
                        res = this.recvQueue.poll(30L, TimeUnit.SECONDS);
                        if (Objects.isNull(res)) {
                            // 応答が無い場合は切断する
                            logger.info("No response.");
                            ctx.close();
                            return;
                        }

                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode node = mapper.readTree(res);
                        if (node.get("ERROR").asInt() == 0) {
                            this.equipmentName = node.get("termname").asText();
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }

            while (this.threaded) {
                try {
                    TCommand cmd = this.sendQueue.poll(10L, TimeUnit.SECONDS);
                    if (Objects.isNull(cmd)) {
                        continue;
                    }

                    // 受信キューをクリア
                    this.recvQueue.clear();

                    // adProductにコマンドを送信する
                    this.ctx.writeAndFlush(Unpooled.copiedBuffer(cmd.getNode().toString(), CharsetUtil.UTF_8));
                    logger.info("SEND: " + cmd.getNode().toString());

                    String res;
                    synchronized (recvQueue) {
                        try {
                            res = this.recvQueue.poll(30L, TimeUnit.SECONDS);
                            if (Objects.isNull(res)) {
                                break;
                            }

                        } catch (InterruptedException ex) {
                            logger.fatal(ex, ex);
                            res = TCommand.createResponse(cmd.getNode(), -99, ex.toString());
                        }
                    }

                    if (StringUtils.isEmpty(res)) {
                        res = TCommand.createResponse(cmd.getNode(), -99, "Timeout");
                    } else if (SOCKET_ERROR.equals(res)) {
                        res = TCommand.createResponse(cmd.getNode(), -99, "通信エラー");

                        // 通信エラーの場合、スレッド終了
                        this.threaded = false;
                    }

                    // 外部システムに応答を返す
                    if (Objects.nonNull(cmd.getChannel())) {
                        cmd.getChannel().writeAndFlush(Unpooled.copiedBuffer(res + "\n", CharsetUtil.UTF_8));
                    }
                    logger.info("RETURN: " + res);

                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                    this.threaded = false;
                }
            }

            logger.info("Terminate thread: " + this.equipmentName);
        }

        /**
         * 設備名を取得する。
         *
         * @return
         */
        public String getEquipmentName() {
            return this.equipmentName;
        }

        /**
         * スレッドを終了する。
         *
         */
        public void terminate() {
            this.threaded = false;
            this.recv(null);
        }

        /**
         * 要求コマンドを送信する。
         *
         * @param cmd
         */
        public void send(TCommand node) {
            try {
                synchronized (this.sendQueue) {
                    this.sendQueue.offer(node);
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }

        /**
         * 応答コマンドを受信する。
         *
         * @param cmd
         */
        public void recv(String cmd) {
            try {
                this.recvQueue.offer(cmd);
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    private static final Logger logger = LogManager.getLogger();
    private final Map<ChannelHandlerContext, ClientThread> threadMap = new HashMap<>();
    private final String SOCKET_ERROR = "SOCKET_ERROR";

    /**
     * adProductとの接続が確立された。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public synchronized void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Connected of adProduct: " + ctx.toString());

        if (!this.threadMap.containsKey(ctx)) {
            ClientThread thread = new ClientThread(ctx);
            this.threadMap.put(ctx, thread);
            thread.start();
        }

        SocketLinkageCtrl.getInstance().addClient(ctx);
    }

    /**
     * adProductが切断された。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public synchronized void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Close of adProduct: " + ctx.toString());

        if (this.threadMap.containsKey(ctx)) {
            ClientThread thread = this.threadMap.get(ctx);
            thread.send(new TCommand(null, null));
            thread.recv(SOCKET_ERROR);
            this.threadMap.remove(ctx);
        }

        ctx.close();

        SocketLinkageCtrl.getInstance().deleteSocketClient(ctx);
    }

    /**
     * メッセージ受信処理
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        try {
            String cmd = msg.readBytes(msg.readableBytes()).toString(Charset.forName("UTF-8"));

            logger.info("RES: " + cmd);

            if (this.threadMap.containsKey(ctx)) {
                ClientThread thread = this.threadMap.get(ctx);
                thread.recv(cmd);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            if (this.threadMap.containsKey(ctx)) {
                ClientThread thread = this.threadMap.get(ctx);
                thread.send(new TCommand(null, null));
                thread.recv(SOCKET_ERROR);
            }
        }
    }

    /**
     * 例外が発生した。
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn(cause, cause);

        if (this.threadMap.containsKey(ctx)) {
            ClientThread thread = this.threadMap.get(ctx);
            thread.send(new TCommand(null, null));
            thread.recv(SOCKET_ERROR);
            this.threadMap.remove(ctx);
        }

        ctx.close();

        SocketLinkageCtrl.getInstance().deleteSocketClient(ctx);
    }

    /**
     * コマンドを送信する。
     *
     * @param ctx
     * @param cmd
     * @return
     */
    public boolean send(ChannelHandlerContext ctx, TCommand cmd) {
        if (this.threadMap.containsKey(ctx)) {
            this.threadMap.get(ctx).send(cmd);
            return true;
        }
        return false;
    }

    /**
     * 指定したadProductにコマンドを送信する。
     *
     * @param equipmentName
     * @param cmd
     * @return
     */
    public boolean send(String equipmentName, TCommand cmd) {
        for (Map.Entry<ChannelHandlerContext, ClientThread> entry : this.threadMap.entrySet()) {
            if (StringUtils.equalsIgnoreCase(equipmentName, entry.getValue().getEquipmentName())) {
                entry.getValue().send(cmd);
                return true;
            }
        }
        return false;
    }

    /**
     * 全てのadProductにコマンドを送信する。
     *
     * @param cmd
     */
    public void sendAll(TCommand cmd) {
        for (Map.Entry<ChannelHandlerContext, ClientThread> entry : this.threadMap.entrySet()) {
            entry.getValue().send(cmd);
        }
    }
}
