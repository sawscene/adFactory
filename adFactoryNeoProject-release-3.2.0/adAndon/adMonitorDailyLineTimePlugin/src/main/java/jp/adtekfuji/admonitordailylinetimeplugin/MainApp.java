package jp.adtekfuji.admonitordailylinetimeplugin;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.mainapp.LocalePluginInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainApp extends Application {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void start(Stage stage) throws Exception {
        //画面設定.
        LocaleUtils.load("locale");
        SceneProperties sp = new SceneProperties(stage, AdProperty.getProperties());
        sp.setAppTitle(LocaleUtils.getString("key.AndonAppTitle"));
        sp.addCssPath("/styles/andonStyles.css");
        SceneContiner.createInstance(sp);
        SceneContiner sc = SceneContiner.getInstance();
        sc.trans("DailyLineTimeMain");
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as
     * fallback in case the application can not be launched through deployment artifacts, e.g., in
     * IDEs with limited FX support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        logger.info("START adAndonApp");
        try {
            //言語ファイルプラグイン読み込み.
            PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
            PluginLoader.load(LocalePluginInterface.class);
            //プロパティファイル読み込み.
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adAndonApp.properties");

            launch(args);

            // 後始末を行なう。
            SceneContiner.getInstance().getFxComponentObjects().entrySet().forEach(e -> {
                if (e.getValue() instanceof AdAndonComponentInterface) {
                    AdAndonComponentInterface component = (AdAndonComponentInterface) e.getValue();
                    component.exitComponent();
                }
            });

            AdProperty.store();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        logger.info("END adAndonApp");
    }

}
