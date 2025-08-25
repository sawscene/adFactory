/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.admanagerlinetimerplugin.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;

/**
 *
 * @author ke.yokoi
 */
public interface AdInterfaceClientListner {

    public boolean getCrypt();

    public long getRecconectDelay();

    public void connect(Bootstrap bootstrap);

    public Bootstrap configureBootstrap(Bootstrap b, EventLoopGroup g);

}
