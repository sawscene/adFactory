/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.socketcomm;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.util.Objects;
import javax.websocket.Session;

/**
 * ソケット接続情報と端末名を纏める
 *
 * @author ka.makihara
 */
public class LinkageData {

    private ChannelHandlerContext channel;
    private Session session;
    private String channelName;

    /**
     *
     */
    LinkageData() {
    }

    /**
     *
     * @param ch
     */
    LinkageData(ChannelHandlerContext ch) {
        this.channel = ch;
        this.channelName = "";
        this.session = null;
    }

    /**
     *
     * @param s
     */
    LinkageData(Session s) {
        this.channel = null;
        this.channelName = "";
        this.session = s;
    }

    /**
     *
     * @param ch
     * @param name
     */
    LinkageData(ChannelHandlerContext ch, String name) {
        this.channel = ch;
        this.channelName = name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.channelName = name;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return this.channelName;
    }

    /**
     *
     * @return
     */
    public ChannelHandlerContext getChannel() {
        return this.channel;
    }

    /**
     *
     * @param cmdStr
     * @throws IOException
     */
    public void sendText(String cmdStr) throws IOException {
        if (Objects.nonNull(this.channel)) {
            this.channel.writeAndFlush(Unpooled.copiedBuffer(cmdStr, CharsetUtil.UTF_8));
        } else if (Objects.nonNull(this.session)) {
            this.session.getBasicRemote().sendText(cmdStr);
        }
    }

    /**
     *
     * @param s
     */
    public void setSession(Session s) {
        this.session = s;
    }

    /**
     *
     * @return
     */
    public Session getSession() {
        return this.session;
    }
}
