/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice;

import jp.adtekfuji.adinterfaceservice.warehouseplugin.WarehouseServicePlugin;
import adtekfuji.property.AdProperty;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class MainApp {

    private static final Logger logger = LogManager.getLogger();
    private WarehouseServicePlugin plugin = null;

    public MainApp() {
        try {
            plugin = new WarehouseServicePlugin();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    public final void exec() {
        logger.info("service start");
        try {
            start();
            while (true) {
                //標準入力からstopが来たらスレッドを終わらせる.
                BufferedReader systemReader = new BufferedReader(new InputStreamReader(System.in));
                String line = systemReader.readLine();
                if (line.equals("stop")) {
                    break;
                }
            }
            logger.info("service stoed...");
            stop();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info("service stop");
    }

    private void start() throws Exception {
        plugin.startService();
    }

    private void stop() throws Exception {
        plugin.stopService();
    }

    public static void main(String[] args) {
        try {
            // 設定ファイルの読み込み
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adInterface.properties");

            // 開始
            MainApp mainApp = new MainApp();
            mainApp.exec();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

}
