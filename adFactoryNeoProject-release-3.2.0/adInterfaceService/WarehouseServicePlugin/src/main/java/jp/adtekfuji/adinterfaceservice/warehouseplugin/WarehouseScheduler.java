/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.warehouseplugin;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * スケジューラー
 * 
 * @author 14-0282
 */
public class WarehouseScheduler {

    private static final Logger logger = LogManager.getLogger();
    private static WarehouseScheduler instance = null;
    private final WarehouseConfig config;
    private final Timer timer = new Timer();
    private ImportTask importTask = null;
    private ExportTask exportTask = null;
    private String importPath = null;
    private Boolean autoSync = null;
    private String intervalMin = null;
    
    private boolean isServerHost;
 
    /**
     * コンストラクタ
     */
    private WarehouseScheduler() {
        this.config = WarehouseConfig.getInstance();
    }

    /**
     * インスタンスを取得する。
     * 
     * @return インスタンス
     */
    public static WarehouseScheduler getInstance() {
        if (Objects.isNull(instance)) {
            instance = new WarehouseScheduler();
        }
        return instance;
    }

    /**
     * サービスを開始する。
     */
    public void startService() {
        logger.info("startService.");

        this.loadConfig();
        logger.info("Warehouse Config: autoSync={}, period={}", this.autoSync, this.intervalMin);
        
        this.isServerHost = this.isServerHost();
       
        if (this.autoSync) {
            this.startSchedule(60000L);
        }
    }

    /**
     * タスクを実行する。
     * 
     * @param date タスク開始日時
     * @param path インポートフォルダーパス
     * @param autoSync 自動同期
     * @param intervalMin ポーリング間隔(分)
     */
    public void runTask(Date date, String path, Boolean autoSync, String intervalMin) {
        logger.info("runTask: {},{},{},{}", date, path, autoSync, intervalMin);

        this.stopSchedule();
        this.storeConfig(path, autoSync, intervalMin);

        this.startSchedule(1000L);
    }

    /**
     * サービスを停止する。
     */
    public void stopService() {
        logger.info("stopService...");
        instance.stopSchedule();
    }

    /**
     * 設定を読み込む。
     */
    private void loadConfig() {
        this.importPath = this.config.getImportPath();
        this.autoSync = this.config.getAutoSync();
        this.intervalMin = this.config.getSyncIntervalMin();
    }

    /**
     * 設定を保存する。
     * 
     * @param importPath インポートフォルダーパス
     * @param autoSync 自動同期
     * @param intervalMin ポーリング間隔(分)
     */
    private void storeConfig(String importPath, Boolean autoSync, String intervalMin) {
        logger.info("storeConfig: importPath={}, autoSync={}, intervalMin={}", importPath, autoSync, intervalMin);
        
        if (Objects.nonNull(intervalMin)) {
            if (!this.intervalMin.equals(intervalMin)) {
                this.intervalMin = intervalMin;
                this.config.setSyncIntervalMin(intervalMin);
            }

            this.autoSync = autoSync;
            this.config.setAutoSync(autoSync);

            if (!this.importPath.equals(importPath)) {
                this.importPath = importPath;
                this.config.setImportPath(importPath);
            }

            try {
                AdProperty.store(WarehouseConfig.WAREHOUSE_PROPERTY);
            } catch (IOException ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    /**
     * スケジュールを開始する。
     * 
     * @param delay タスク実行までの遅延時間(ミリ秒)
     */
    private void startSchedule(long delay) {
        logger.info("startSchedule: autoSync={}, delay={}, period={}", this.autoSync, delay, this.intervalMin);

        try {
            this.importTask = new ImportTask(this);

            if (this.autoSync) {
                // 自動同期の場合、実行間隔を設定する
                long period = Long.parseLong(this.intervalMin) * 60000;
                if (period <= 0){
                    logger.error("Invalid syncIntervalMin.");
                    period = 60000L;
                }

                this.timer.schedule(this.importTask, delay, period);

                if (this.isServerHost) {
                    this.exportTask = new ExportTask();
                    this.timer.schedule(this.exportTask, delay, period);
                }

            } else {
                // 手動同期の場合
                this.timer.schedule(this.importTask, delay);

                if (this.isServerHost) {
                    this.exportTask = new ExportTask();
                    this.timer.schedule(this.exportTask, delay);
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * スケジュールを停止する。
     */
    private void stopSchedule() {
        logger.info("stopSchedule.");

        try {
            if (Objects.nonNull(this.importTask)) {
                this.importTask.cancel();
            }

            if (Objects.nonNull(this.exportTask)) {
                this.exportTask.cancel();
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
   
    /**
     * サーバーで実行中かどうかを返す。
     * 
     * @return true: 実行中、false: 非実行
     */
    private boolean isServerHost() {
        try {
            URL url = new URL(ClientServiceProperty.getServerUri());
            logger.info("ServerAddress: " + url);
            if (StringUtils.equals(url.getHost(), "localhost")
                    || StringUtils.equals(url.getHost(), "127.0.0.1")) {
                return true;
            }
            
            InetAddress inet = InetAddress.getLocalHost();
            String hostName = inet.getHostName();
            logger.info("HostName: " + hostName);
            if (StringUtils.equals(url.getHost(), hostName)) {
                return true;
            }
            
            InetAddress[] addrs = InetAddress.getAllByName(hostName);
            for (int i = 0; i < addrs.length; i++) {
                logger.info("InetAddress: " + addrs[i].getHostAddress());
                if (StringUtils.equals(url.getHost(), addrs[i].getHostAddress())) {
                    return true;
                }
            }
                    
        } catch (MalformedURLException | UnknownHostException ex) {
            logger.fatal(ex ,ex);
        }
        return false;
    }
}
