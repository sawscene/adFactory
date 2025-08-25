/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice;

import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import jp.adtekfuji.adinterfaceservice.socketcomm.SocketCommBaseServer;
import jp.adtekfuji.adinterfaceservice.socketcomm.SocketLinkageCtrl;
import jp.adtekfuji.adinterfaceservice.websocket.CtrlWebSocketServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class MainApp {

    private static final Logger logger = LogManager.getLogger();
    private final List<AdInterfaceServiceInterface> plugins = new ArrayList<>();
    private Map<String, Boolean> options = null;
    private SocketCommBaseServer socketCommBaseServer = null;
    private final static String STOP_COMMAND = "stop";

    private SocketLinkageCtrl socketCommandCtrl = null;    //外部とadProの接続用

    private CtrlWebSocketServer ctrlWebSocketServer = null;

    /**
     *
     */
    public MainApp() {
        //
        PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
        this.plugins.clear();
        this.plugins.addAll(PluginLoader.load(AdInterfaceServiceInterface.class));
        logger.info("plugin:{}", plugins);

        //adFactory内(adProduct <-> adInterface <-> adFactoryServer)でのソケット通信用
        SocketCommBaseServer.createInstance(plugins);
        this.socketCommBaseServer = SocketCommBaseServer.getInstance();

        //外部 <-- adInterface --> adProduct とのソケット通信用
        SocketLinkageCtrl.createInstance(plugins);
        this.socketCommandCtrl = SocketLinkageCtrl.getInstance();

        CtrlWebSocketServer.createInstance(18008, "/adInterfaceService/rest");
        this.ctrlWebSocketServer = CtrlWebSocketServer.getInstance();
    }

    /**
     *
     */
    public final void exec() {
        logger.info("service start");
        try {
            this.socketCommBaseServer.startService();
            this.socketCommandCtrl.startService();
            this.ctrlWebSocketServer.startService();
            this.start();
            
            Scanner sc = new Scanner(System.in);
            while (true) {
                if (sc.hasNext()) {
                    String line = sc.nextLine();
                    if (STOP_COMMAND.equals(line)) {
                        // 標準入力からstopが来たらスレッドを終わらせる
                        break;
                    }
                }
            }
            
            logger.info("service stoed...");
            this.ctrlWebSocketServer.stopService();
            this.socketCommBaseServer.stopService();
            this.socketCommandCtrl.stopService();
            this.stop();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info("service stop");
    }

    /**
     * プラグインのサービスを開始する。
     */
    private void start() {
        try {
            logger.info("start start.");

            // Admin 権限で実行
            LoginUserInfoEntity.getInstance().setLoginId(LoginUserInfoEntity.ADMIN_LOGIN_ID);
            
            this.options = LicenseManager.getInstance().getLicenseOptions();

            List<String> services = Arrays.asList("@Warehouse", "@DBImportService", "@DBOutputService", "@ImportService", "@ExportService", "@SummaryReport", "@DeviceConnectionService");
            this.plugins.stream().forEach((imp) -> {
                try {
                    // オプションライセンスを確認する
                    String optionName = "@" + imp.getServiceName();
                    boolean isLicensed = false;

                    if (services.contains(optionName)) {
                        isLicensed = true;
                    }

                    if (!isLicensed && options.containsKey(optionName)) {
                        isLicensed = this.options.get(optionName);
                    }

                    logger.info("Licensed: {},{}", optionName, isLicensed);

                    if (isLicensed) {
                        logger.info("start:{}", imp.getClass().toString());
                        imp.startService();
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });
        } finally {
            logger.info("start end.");
        }
    }

    /**
     * プラグインのサービスを停止する。
     */
    private void stop() {
        try {
            logger.info("stop start.");

            this.plugins.stream().forEach((imp) -> {
                try {
                    // オプションライセンスを確認する
                    String optionName = "@" + imp.getServiceName();
                    boolean isLicensed = false;

                    if (this.options.containsKey(optionName)) {
                        isLicensed = this.options.get(optionName);
                    }

                    if (isLicensed) {
                        logger.info("stop:{}", imp.getClass().toString());
                        imp.stopService();
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });
        } finally {
            logger.info("stop end.");
        }
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            //プロパティファイル読み込み.
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adInterface.properties");
            //開始.
            MainApp mainApp = new MainApp();
            mainApp.exec();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }
}
