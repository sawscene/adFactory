package adtekfuji.adcustommonitorapp;

import adtekfuji.adcustommonitorapp.service.CellProductionMonitorService;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import jp.adtekfuji.forfujiapp.clientnativeservice.ChangeDateMonitoringService;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.javafxcommon.utils.SwitchCompoSubject;
import jp.adtekfuji.mainapp.LocalePluginInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainApp extends Application {

    private static final Double SCREEN_MIN_WIDTH = 600.0;
    private static final Double SCREEN_MIN_HEIGHT = 400.0;

    @Override
    public void start(Stage stage) throws Exception {
        //画面設定.
        SceneProperties sp = new SceneProperties(stage, AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG));
        sp.setAppTitle("adCellProductionMonitor");
        sp.addCssPath("/styles/colorStyles.css");
        sp.addCssPath("/styles/designStyles.css");
        sp.addCssPath("/styles/fontStyles.css");
        sp.setMinWidth(SCREEN_MIN_WIDTH);
        sp.setMinHeight(SCREEN_MIN_HEIGHT);
        SceneContiner.createInstance(sp);
        SceneContiner sc = SceneContiner.getInstance();
        //作業画面へ
        sc.trans("Scene");
        sc.setComponent("SideNaviPane", "CellProductionMonitorSubMeneCompo");
    }

    @Override
    public void stop() throws Exception {
        SwitchCompoSubject.getInstance().switchCompo();
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
        logger.info("start adCellProductionMonitor application");
        CellProductionMonitorService service = null;
        //プロパティファイル読み込み.
        try {
            //言語ファイルプラグイン読み込み.
            PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
            PluginLoader.load(LocalePluginInterface.class);
            //プロパティファイル読み込み.
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG, "adCellProductionMonitor.properties");
            AdProperty.load("adCellProductionMonitor.properties");
            // 通信開始
            service = CellProductionMonitorService.getInstance();
            service.startService();
            //FX起動.
            launch(args);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        } finally {
            try {
                //後始末.
                if (Objects.nonNull(service)) {
                    service.stopService();
                    ChangeDateMonitoringService.getInstance().stop();
                }
                if (!AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).containsKey(ClientPropertyConstants.ADFACTORY_SERVICE_URI)) {
                    AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.ADFACTORY_SERVICE_URI, "http://localhost:8080/adFactoryServer/rest");
                }
                if (!AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).containsKey(ClientPropertyConstants.ADFACTORY_FOR_FUJI_SERVICE_URI)) {
                    AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.ADFACTORY_FOR_FUJI_SERVICE_URI, "http://localhost:8080/adFactoryForFujiServer/rest");
                }
                AdProperty.store(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
            } catch (InterruptedException | IOException ex) {
                logger.fatal(ex, ex);
            }

            // Java VMを終了する
            System.exit(0);
        }
    }

}
