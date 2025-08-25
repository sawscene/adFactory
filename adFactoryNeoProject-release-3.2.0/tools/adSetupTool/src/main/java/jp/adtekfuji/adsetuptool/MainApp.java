package jp.adtekfuji.adsetuptool;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jp.adtekfuji.adsetuptool.utils.IniFile;
import jp.adtekfuji.adsetuptool.utils.LocaleUtils;
import jp.adtekfuji.adsetuptool.utils.PathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainApp extends Application {

    private static List<String> appArgs;// 起動引数

    /**
     * 起動引数を取得する。
     *
     * @return 起動引数
     */
    public static List<String> getAppArgs() {
        return appArgs;
    }

    /**
     * 
     * @param stage
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        URL location = getClass().getResource("/fxml/adsetuptool/adsetup_tool.fxml");
        ResourceBundle rb = LocaleUtils.load("locale.adsetuptool.locale", Locale.getDefault());
        FXMLLoader loader = new FXMLLoader(location, rb);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/adsetuptool/Styles.css");

        stage.setTitle(rb.getString("key.WindowTitle") + " - " + rb.getString("key.verison") + " " + getBuildVersion());
        stage.setScene(scene);
        stage.setMinHeight(300.0);
        stage.setMinWidth(400.0);
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
        logger.info("start adSetupTool");

        appArgs = Arrays.asList(args);

        launch(args);

        logger.info("stop adSetupTool");
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
                URL url = this.getClass().getResource("/jp/adtekfuji/adsetuptool/MainApp.class");
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPAN);
                buildVersion = formatter.format(new Date(url.openConnection().getLastModified()));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return buildVersion;
    }
}
