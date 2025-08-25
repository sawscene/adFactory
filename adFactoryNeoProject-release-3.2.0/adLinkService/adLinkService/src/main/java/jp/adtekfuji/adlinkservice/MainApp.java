package jp.adtekfuji.adlinkservice;

import adtekfuji.property.AdProperty;
import adtekfuji.utility.IniFile;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jp.adtekfuji.adlinkservice.websocket.WebSocketServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * adLinkService アプリケーション
 * 
 * @author s-heya
 */
public class MainApp extends Application {

    static private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public MainApp() {
        PluginContainer.createInstance();
    }
    
    /**
     * JavaFX アプリケーション・スレッドが開始された。
     * 
     * @param stage ステージ
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {

        // マニフェストからバージョンを取得
        //Class<?> clazz = this.getClass();
        //String rc = clazz.getResource(clazz.getSimpleName()).toExternalForm();
        //String jar = rc.substring(0, rc.lastIndexOf(clazz.getPackage().getName().replace('.', '/')));
        //URL url = new URL(jar + "META-INF/MANIFEST.MF");
        //
        //String version = "?";
        //try (InputStream is = url.openStream()) {
        //    Manifest mf = new Manifest(is);
        //    Attributes attr = mf.getMainAttributes();
        //    version = attr.getValue("AppVersion");
        //} catch (Exception ex) {
        //}

        String title = "adLinkService Version: " + getBuildVersion();
        logger.info(title);
                
        WebSocketServer.createInstance(18080, "/adfactory").startServer();
        
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * JavaFX アプリケーション・スレッドが終了した。
     * 
     * @throws Exception 
     */
    @Override
    public void stop() throws Exception {
        WebSocketServer.getInstance().stopServer();

        logger.info("adLinkService stop.");
    }
        
    /**
     * アプリケーションのエントリ・ポイント。
     * 
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //プロパティファイル読み込み.
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adLinkServiceApp.properties");

            File lockFile = new File(System.getenv("ADFACTORY_HOME") + File.separator + "bin" + File.separator + "adLinkService.lock");

            try (FileChannel fc = FileChannel.open(lockFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE); FileLock lock = fc.tryLock()) {
                if (Objects.isNull(lock)) {
                    logger.info("Other process locked: {0}", lockFile);
                    // 多重起動防止
                    throw new RuntimeException(MessageFormat.format("Other process locked: {0}", lockFile));
                }
                lockFile.deleteOnExit();

                if (args.length == 1 && "-silent".equals(args[0])) {
                    logger.info("start adLinkService by silent mode.");
                    WebSocketServer.createInstance(18080, "/adfactory").startServer();
                    synchronized (args) {
                        try {
                            args.wait();
                        } catch (InterruptedException e) {
                            logger.fatal(e);
                        }
                    }
                } else {
                    logger.info("start adLinkService by debug mode.");
                    launch(args);
                }
            }
        } catch (IOException | RuntimeException ex) {
            logger.fatal(ex, ex);
        } finally {
            // Java VMを終了する
            System.exit(0);
        }
    }
 
    /**
     * version.ini からビルドバージョンを取得する。
     *
     * @return ビルドバージョン
     */
    public String getBuildVersion() {
        String buildVersion = null;

        try {
            String path = System.getenv("ADFACTORY_HOME") + "\\version_link.ini";
            File verIni = new File(path);
            if (verIni.exists()) {
                IniFile iniFile = new IniFile(path);
                buildVersion = iniFile.getString("Version", "Ver", null);
            } else {
                URL url = this.getClass().getResource("/jp/adtekfuji/adlinkservice/MainApp.class");
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPAN);
                buildVersion = formatter.format(new Date(url.openConnection().getLastModified()));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return buildVersion;
    }
}
