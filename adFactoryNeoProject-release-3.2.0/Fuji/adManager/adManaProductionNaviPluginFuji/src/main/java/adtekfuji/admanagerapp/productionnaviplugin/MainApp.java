package adtekfuji.admanagerapp.productionnaviplugin;

import adtekfuji.clientservice.SystemResourceFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import adtekfuji.serialcommunication.SerialCommunication;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.mainapp.LocalePluginInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static javafx.application.Application.launch;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.system.SystemOptionEntity;
import jp.adtekfuji.adFactory.entity.system.SystemPropEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
public class MainApp extends Application {

    private static final Double SCREEN_MIN_WIDTH = 600.0;
    private static final Double SCREEN_MIN_HEIGHT = 400.0;

    private final Properties properties = AdProperty.getProperties();

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

        // オプションライセンスを取得する。
        this.getSystemOption();

        // キャッシュする情報を取得する
        CacheUtils.createCacheData(EquipmentInfoEntity.class, true);
        CacheUtils.createCacheData(OrganizationInfoEntity.class, true);

        //作業画面へ
        sc.trans("PuroductionNaviScene");
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("SideNaviPane", "ProductionNaviMenuCompoFuji");
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

        // NetBeansでデバッグする場合、下記のログインユーザー情報のコメントアウトを解除する。
//        LoginUserInfoEntity.getInstance().setId(1L);
//        LoginUserInfoEntity.getInstance().setLoginId("admin");
//        LoginUserInfoEntity.getInstance().setName("admin");

        //プロパティファイル読み込み.
        try {
            //言語ファイルプラグイン読み込み.
            PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
            PluginLoader.load(LocalePluginInterface.class);
            //プロパティファイル読み込み.
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adManeApp.properties");
            AdProperty.load("ForFuji", "adFactoryForFujiClient.properties");

            // サーバーからシステム設定を取得する。
            getServerSystemProps();

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

    /**
     * オプションライセンスを取得する。
     */
    private void getSystemOption() {
        SystemResourceFacade systemResourceFacade = new SystemResourceFacade();
        List<SystemOptionEntity> optionLicenses = systemResourceFacade.getLicenseOptions();

        Properties optionProps = new Properties();
        for (SystemOptionEntity optionLicence : optionLicenses) {
            optionProps.setProperty(optionLicence.getOptionName(), optionLicence.getEnable().toString());
        }

        // プラグインの使用を許可
        this.setProperties(optionProps);
    }

    /**
     * プロパティを設定する
     *
     * @param properties
     */
    public void setProperties(Properties properties) {
        if (Objects.nonNull(properties)) {
            for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
                String propertyName = (String) e.nextElement();
                String propertyValue = properties.getProperty(propertyName);
                this.properties.setProperty(propertyName, propertyValue);
            }
        }
    }

    /**
     * サーバーからシステム設定を取得して、ローカルプロパティに反映する。
     */
    public static void getServerSystemProps() {
        Logger logger = LogManager.getLogger();
        try {
            SystemResourceFacade systemResourceFacade = new SystemResourceFacade();

            List<SystemPropEntity> systemProps = systemResourceFacade.getSystemProperties();
            if (Objects.isNull(systemProps)) {
                return;
            }

            for (SystemPropEntity systemProp : systemProps) {
                AdProperty.getProperties().setProperty(systemProp.getKey(), systemProp.getValue());
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
