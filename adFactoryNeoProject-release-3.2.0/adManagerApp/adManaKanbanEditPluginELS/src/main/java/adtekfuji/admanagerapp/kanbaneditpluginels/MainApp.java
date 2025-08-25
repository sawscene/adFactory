package adtekfuji.admanagerapp.kanbaneditpluginels;

import adtekfuji.admanagerapp.kanbaneditpluginels.common.KanbanEditConfig;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import adtekfuji.serialcommunication.SerialCommunication;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.mainapp.LocalePluginInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static javafx.application.Application.launch;

public class MainApp extends Application {

    private static final Double SCREEN_MIN_WIDTH = 600.0;
    private static final Double SCREEN_MIN_HEIGHT = 400.0;

    @Override
    public void start(Stage stage) throws Exception {
        ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
        //画面設定.
        SceneProperties sp = new SceneProperties(stage, AdProperty.getProperties());
        sp.setAppTitle(LocaleUtils.getString("key.adManagerAppTitle"));
        sp.addCssPath("/styles/colorStyles.css");
        sp.addCssPath("/styles/designStyles.css");
        sp.addCssPath("/styles/fontStyles.css");
        sp.setMinWidth(SCREEN_MIN_WIDTH);
        sp.setMinHeight(SCREEN_MIN_HEIGHT);
        SceneContiner.createInstance(sp);
        SceneContiner sc = SceneContiner.getInstance();
        //作業画面へ
        sc.trans("KanbanEditSceneELS");
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);

//        // カンバン編集メニューを使用するか
//        if (KanbanEditConfig.getKanbanEditMenuEnabled()) {
//            sc.setComponent("SideNaviPane", "KanbanEditMenuCompoELS");// カンバン編集メニュー
//        } else {
//            sc.setComponent("ContentNaviPane", "KanbanListCompoELS_base");// カンバン編集画面 (メニューは使用しない)
//        }

        // ELS版は、adManeApp.properties の設定に関係なく KanbanCreateCompoELS を呼び出す。
        sc.setComponent("ContentNaviPane", "KanbanListCompoELS");// カンバン編集画面 (ELS)
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
        logger.info("start adManager application");

        //デバッグ用にシステムアドミン権限を設定しておく.
        LoginUserInfoEntity.getInstance().setAuthorityType(AuthorityEnum.SYSTEM_ADMIN);

        //プロパティファイル読み込み.
        try {
            //言語ファイルプラグイン読み込み.
            PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
            PluginLoader.load(LocalePluginInterface.class);
            //プロパティファイル読み込み.
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adManeApp.properties");
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        // FX起動
        launch(args);

        // 後始末
        try {
            SerialCommunication serialComm = SerialCommunication.getIncetance();
            if (Objects.nonNull(serialComm)) {
                serialComm.disconect();
            }
            AdProperty.store();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

}
