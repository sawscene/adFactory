package jp.adtekfuji.DbImportService;

import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import jp.adtekfuji.mainapp.LocalePluginInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainApp {

    private static final Logger logger = LogManager.getLogger();
    private DbImportServicePlugin plugin = null;

    public MainApp() {
        try {
            plugin = new DbImportServicePlugin();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    public final void exec() {
        logger.info("import service started");
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
            logger.info("import service stopped...");
            stop();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info("import service stop");
    }

    private void start() throws Exception {
        plugin.startService();
    }

    private void stop() throws Exception {
        plugin.stopService();
    }

    public static void main(String[] args) {
        try {
            //言語ファイルプラグイン読み込み.
            PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
            PluginLoader.load(LocalePluginInterface.class);

            //プロパティ読み込み.
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adInterface.properties");

            // adManagerと同じサーバーに登録するためadManager設定読み込み
            AdProperty.load("adManeApp.properties");

            //開始.
            MainApp mainApp = new MainApp();
            mainApp.exec();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

}
