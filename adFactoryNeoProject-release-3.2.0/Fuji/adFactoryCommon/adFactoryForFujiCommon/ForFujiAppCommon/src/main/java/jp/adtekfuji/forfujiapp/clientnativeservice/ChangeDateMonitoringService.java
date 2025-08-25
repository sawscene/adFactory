/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.clientnativeservice;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.utility.DateUtils;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 画面更新タイマークラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.8.Thr
 */
public class ChangeDateMonitoringService {
    
    private final Logger logger = LogManager.getLogger();
    private static ChangeDateMonitoringService instance = null;
    private ChangeDateMonitoringHandler handler = null;
    
    private boolean isUpdate;
    private Timer updateTimer;
    private Long updateInterval = 0L;
    
    private ChangeDateMonitoringService() {
        SceneContiner sc = SceneContiner.getInstance();
        isUpdate = false;
        
        sc.getStage().setOnCloseRequest((WindowEvent we) -> {
            if (updateTimer != null) {
                updateTimer.cancel();
            }
        });
    }
    
    public static ChangeDateMonitoringService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ChangeDateMonitoringService();
        }
        return instance;
    }
    
    public void setChangeDateMonitoringHandler(ChangeDateMonitoringHandler handler) {
        this.handler = handler;
    }
    
    /**
     * 日付更新の呼び出し
     * 
     */
    synchronized private void call() {
        if(Objects.nonNull(handler)){
            handler.changeDate();
        }
    }

    /**
     * 日付変更監視用タイマー開始
     *
     */
    public void start() {
        updateInterval = DateUtils.getEndOfDate(new Date()).getTime() - new Date().getTime();
         this.isUpdate = true;
        if (updateTimer != null) {
            logger.info("Date - " + new Date() + ": Reset updating...");
            updateTimer.cancel();
        }
        
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (isUpdate) {
                        logger.info(new Date() + ": updating...");
                        call();
                        updateInterval = DateUtils.getEndOfDate(new Date()).getTime() - new Date().getTime();
                    } else {
                        logger.info(new Date() + ": Pause update...");
                    }
                } catch (Exception ex) {
                    ex.getStackTrace();
                    logger.fatal(ex, ex);
                }
            }
        }, updateInterval);
    }
    
    /**
     * 日付変更監視用タイマー停止
     * 
     */
    public void stop(){
        this.isUpdate = false;
        this.handler = null;
    }
}
