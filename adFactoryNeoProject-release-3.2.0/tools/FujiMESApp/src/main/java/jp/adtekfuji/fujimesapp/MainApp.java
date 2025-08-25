package jp.adtekfuji.fujimesapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * メインクラス
 *
 * @author s-heya
 */
public class MainApp extends Application {

    private static final Logger logger = LogManager.getLogger();
    public static final String PACKAGE_NAME = "FujiMESApp";
    
    /**
     * アプリケーション起動
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        // マニフェストからバージョンを取得
        Class<?> clazz = this.getClass();
        String rc = clazz.getResource(clazz.getSimpleName() + ".class").toExternalForm();
        String jar = rc.substring(0, rc.lastIndexOf(clazz.getPackage().getName().replace('.', '/')));
        URL url = new URL(jar + "META-INF/MANIFEST.MF");

        String version;
        try (InputStream is = url.openStream()) {
            Manifest mf = new Manifest(is);
            Attributes attr = mf.getMainAttributes();
            version = attr.getValue("AppVersion");
        }

        logger.info("Starting the application: {}", version);

        FujiMESClient client = null;
        
        try {
            int length = this.getParameters().toString().length();
            if (8000 < length) {
                // 起動パラメータの文字数が長すぎます。起動パラメータは8000文字以内で指定してください
                logger.error("Boot parameter too long: {}", length);
                System.out.println("Boot parameter too long.");
                System.exit(2);
            }  
            
            client = new FujiMESClient();
            client.postInspectionData();
 
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            File lockFile = new File(System.getProperty("user.dir") + File.separator + PACKAGE_NAME + ".lock");
        
            try (FileChannel fc = FileChannel.open(lockFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE); FileLock lock = fc.tryLock()) {
                if (Objects.isNull(lock)) {
                    // 多重起動防止
                    logger.error("Task already running: {}", lockFile);
                    System.exit(1);
                }

                lockFile.deleteOnExit();
                launch(args);
            }
        } catch (IOException | RuntimeException ex) {
            LogManager.getLogger().fatal(ex, ex);
        } finally {
            logger.info("Shutdown the application.");
            // Java VMを終了する
            System.exit(0);
        }
    }

}
