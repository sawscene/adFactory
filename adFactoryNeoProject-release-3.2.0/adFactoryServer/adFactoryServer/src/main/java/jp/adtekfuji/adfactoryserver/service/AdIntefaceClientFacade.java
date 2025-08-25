/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import jp.adtekfuji.adFactory.adinterface.command.*;
import jp.adtekfuji.adfactoryserver.adinterface.AdInterfaceClientHandler;
import jp.adtekfuji.adfactoryserver.adinterface.AdInterfaceClientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
@Singleton
@Startup
public class AdIntefaceClientFacade extends Thread {

    private final Logger logger = LogManager.getLogger();
    private final AdInterfaceClientService adInterfaceService = new AdInterfaceClientService();
    private final AdInterfaceClientHandler adInterfaceHandler;
    private static final int MAX_SEND_QUEUE = 100;
    private final LinkedBlockingQueue<Object> sendQueue = new LinkedBlockingQueue<>(MAX_SEND_QUEUE);
    private Boolean threaded = false;

    /**
     *
     */
    public AdIntefaceClientFacade() {
        this.adInterfaceHandler = this.adInterfaceService.getHandler();
    }

    /**
     *
     */
    @PostConstruct
    public void setUp() {
        logger.info("startService...");
        this.threaded = true;
        this.adInterfaceService.startService();
        this.start();
        // 開始処理
        DeviceConnectionServiceCommand command = new DeviceConnectionServiceCommand(DeviceConnectionServiceCommand.COMMAND.START_SERVER);
        noticeDeviceConnectionService(command);

        SummaryReportNoticeCommand summaryReportNoticeCommand = new SummaryReportNoticeCommand(SummaryReportNoticeCommand.COMMAND.START_SERVER);
        noticeSummaryReport(summaryReportNoticeCommand);
        logger.info("startService!");
    }

    /**
     *
     */
    @PreDestroy
    public void tearDown() {
        try {
            logger.info("stopService...");
            this.adInterfaceService.stopService();
            this.threaded = false;
            synchronized (this.sendQueue) {
                this.sendQueue.notify();
            }
            this.interrupt();
            this.join();
            logger.info("stopService!");
        } catch (InterruptedException ex) {
        }
    }

    /**
     * 実績通知.
     *
     * @param command
     */
    public void noticeActual(ActualNoticeCommand command) {
        try {
            synchronized (this.sendQueue) {
                this.sendQueue.put(command);
            }
        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 呼出/呼出解除.
     *
     * @param command
     */
    public void noticeCalling(CallingNoticeCommand command) {
        try {
            synchronized (this.sendQueue) {
                this.sendQueue.put(command);
            }
        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ラインタイマー通知.
     *
     * @param command
     */
    public void noticeLineTimer(LineTimerNoticeCommand command) {
        try {
            synchronized (this.sendQueue) {
                this.sendQueue.put(command);
            }
        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * サマリーレポート通知
     *
     * @param command サマリーレポート通知
     */
    public void noticeSummaryReport(SummaryReportNoticeCommand command) {
        try {
            synchronized (this.sendQueue) {
                this.sendQueue.put(command);
            }
        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }

    public void noticeDeviceConnectionService(DeviceConnectionServiceCommand command) {
        try {
            synchronized (this.sendQueue) {
                this.sendQueue.put(command);
            }
        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     *
     * @param command
     */
    public void notice(Object command) {
        try {
            synchronized (this.sendQueue) {
                this.sendQueue.put(command);
            }
        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     */
    @Override
    public void run() {

        while(this.threaded && !this.adInterfaceHandler.isActive()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                logger.fatal(ex, ex);
            }
        }

        while (this.threaded) {
            try {
                Object sendCommand = this.sendQueue.take();
                if (Objects.nonNull(sendCommand)) {
                    this.adInterfaceHandler.sendCommand(sendCommand);
                }
            } catch (InterruptedException ex) {
                logger.fatal(ex, ex);
            }
        }
    }
}
