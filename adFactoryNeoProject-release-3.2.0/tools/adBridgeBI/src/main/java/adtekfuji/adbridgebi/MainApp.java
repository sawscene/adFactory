/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi;

import adtekfuji.property.AdProperty;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * adBridgeBI メインクラス
 *
 * @author nar-nakamura
 */
public class MainApp {

    private final static String STOP_COMMAND = "stop";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Logger logger = LogManager.getLogger();
        logger.info("Starting the application.");
        try {
            // 設定ファイルを読み込む。
            AdProperty.rebasePath(new File(System.getProperty("user.dir")).getParent() + File.separator + "conf");
            AdProperty.load("adBridgeBI.properties");

            // 開始
            MainApp mainApp = new MainApp();
            mainApp.exec();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("Shutdown the application.");

            // Java VMを終了する
            System.exit(0);
        }
    }

    /**
     * コンストラクタ
     */
    public MainApp() {
        // サービス停止時の処理
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LogManager.getLogger().info("service shutdown");
            }
        });
    }

    /**
     * 処理を実行する。
     */
    public final void exec() {
        Logger logger = LogManager.getLogger();
        logger.info("service start");
        try {
            // 進捗情報出力を開始する。
            OutputProgress outputProgress = OutputProgress.getInstance();
            outputProgress.start();

            // 標準入力からの停止コマンドを待つ。
            while (true) {
                BufferedReader systemReader = new BufferedReader(new InputStreamReader(System.in));
                String line = systemReader.readLine();
                if (line.equals(STOP_COMMAND)) {
                    break;
                }
            }
            logger.info("service stop");

            // 進捗情報出力を停止する。(サービス停止時はここまで到達せずに終了する)
            outputProgress.stop();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("service stop");
        }
    }
}
