/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.common;

import java.util.Date;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import jp.adtekfuji.adfactoryforfujiserver.adinterface.AdInterfaceClientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Webアプリケーション起動処理
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 */
public class InitListener implements ServletContextListener {

    private static final Logger logger = LogManager.getLogger();
    private final AdInterfaceClientService adInterfaceService = new AdInterfaceClientService();

    /**
     * 起動処理
     *
     * @param event
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.info("WebApp Initialized:{}", new Date());
//        adInterfaceService.startService();
    }

    /**
     * 終了処理
     *
     * @param event
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.info("WebApp Destroyed:{}", new Date());
//        adInterfaceService.stopService();
    }
}
