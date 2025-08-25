package jp.adtekfuji.adreporter;

import adtekfuji.property.AdProperty;
import adtekfuji.utility.IniFile;
import adtekfuji.utility.PathUtils;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jp.adtekfuji.adreporter.utils.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author nar-nakamura
 */
public class MainApp extends Application {

    /**
     * 
     * @param stage
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("prism.lcdtext", "false"); // 文字が虹色になるのを防止するために設定

        URL location = getClass().getResource("/fxml/adreporter/reporter_scene.fxml");
        ResourceBundle rb = LocaleUtils.load("locale.adreporter.locale", Locale.getDefault());
        FXMLLoader loader = new FXMLLoader(location, rb);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/adreporter/Styles.css");

        ReporterFxController controller = (ReporterFxController) loader.getController();
        controller.setStage(stage);

        // アイコン
        Image icon = new Image("image/adreporter/appicon.png");
        // ウィンドウタイトル
        String title = new StringBuilder("adReporter")
                .append(" - ")
                .append(getBuildVersion())
                .toString();

        double wid = 300.0;
        double hei = 80.0;

        Rectangle2D desktopSize = Screen.getPrimary().getVisualBounds();

        stage.getIcons().add(icon);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setMinHeight(hei);
        stage.setMinWidth(wid);
        stage.setHeight(hei);
        stage.setWidth(wid);

        // ウィンドウをデスクトップの右下に配置する。
        stage.setX(desktopSize.getWidth() - wid);
        stage.setY(desktopSize.getHeight() - hei);

        stage.show();
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
        Logger logger = LogManager.getLogger();
        logger.info("start adReporter");

        String curDir = new File(System.getProperty("user.dir")).getParent();// adReporterフォルダ
        String confDir = Paths.get(curDir, "conf").toString();// adReporter/confフォルダ

        try {
            // プロパティファイルを読み込む。
            AdProperty.rebasePath(confDir);
            AdProperty.load("adReporter.properties");

        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        // ファイルロックによる多重起動防止
        File tempDir = new File(curDir, "temp");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File lockFile = new File(tempDir, "adReporter.lock");
        lockFile.deleteOnExit();
        try (FileChannel fc = FileChannel.open(lockFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE); FileLock lock = fc.tryLock()) {
            if (Objects.isNull(lock)) {
                // すでに起動している
                logger.info("Other process locked: {}", lockFile);
                throw new RuntimeException(String.format("Other process locked: %s", lockFile.getName()));
            }

            // FX起動
            launch(args);

        } catch (IOException | RuntimeException ex) {
            logger.fatal(ex, ex);
        }

        // 後始末
        try {
            // プロパティファイルを更新する。
            AdProperty.store();

        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        logger.info("stop adReporter");
    }

    /**
     * ビルドバージョンを取得する。
     *
     * @return
     */
    public String getBuildVersion() {
        String buildVersion = null;
        Logger logger = LogManager.getLogger();

        try {
            String path = PathUtils.getRootPath() + "\\version.ini";
            File verIni = new File(path);
            if (verIni.exists()) {
                IniFile iniFile = new IniFile(path);
                buildVersion = iniFile.getString("Version", "Ver", null);
            } else {
                URL url = this.getClass().getResource("/jp/adtekfuji/adreporter/MainApp.class");
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPAN);
                buildVersion = formatter.format(new Date(url.openConnection().getLastModified()));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return buildVersion;
    }
}
