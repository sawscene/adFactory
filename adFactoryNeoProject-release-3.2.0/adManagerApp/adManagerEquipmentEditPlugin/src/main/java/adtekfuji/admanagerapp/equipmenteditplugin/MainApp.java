package adtekfuji.admanagerapp.equipmenteditplugin;

import adtekfuji.clientservice.SystemResourceFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.system.SystemOptionEntity;
import jp.adtekfuji.adFactory.entity.system.SystemPropEntity;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.mainapp.LocalePluginInterface;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MainApp extends Application {

    private static final Double SCREEN_MIN_WIDTH = 600.0;
    private static final Double SCREEN_MIN_HEIGHT = 400.0;

    private final Properties properties = AdProperty.getProperties();

    @Override
    public void start(Stage stage) throws Exception {
        LocaleUtils.load("locale");

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
        sc.trans("EquipmentEditScene");
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("ContentNaviPane", "EquipmentEditCompo");
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

            // サーバーからシステム設定を取得する。
            getServerSystemProps();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        //FX起動.
        launch(args);

        //後始末.
        try {
            AdProperty.store();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * オプションライセンスを取得する。
     */
    private void getSystemOption() {
        Logger logger = LogManager.getLogger();
        try {
            SystemResourceFacade systemResourceFacade = new SystemResourceFacade();

            List<SystemOptionEntity> optionLicenses = systemResourceFacade.getLicenseOptions();

            Properties optionProps = new Properties();
            for (SystemOptionEntity optionLicence : optionLicenses) {
                optionProps.setProperty(optionLicence.getOptionName(), optionLicence.getEnable().toString());
            }

            // プラグインの使用を許可
            this.setProperties(optionProps);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
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
