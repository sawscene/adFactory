package adtekfuji.admanagerapp;

import adtekfuji.clientservice.SystemResourceFacade;
import adtekfuji.clientservice.common.Paths;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.serialcommunication.SerialCommunication;
import adtekfuji.utility.IniFile;
import adtekfuji.utility.PathUtils;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.system.SystemPropEntity;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainApp extends Application {

    private static final Double SCREEN_MIN_WIDTH = 1024.0;
    private static final Double SCREEN_MIN_HEIGHT = 768.0;

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("prism.lcdtext", "false");

        if (!Boolean.parseBoolean(AdProperty.getProperties().getProperty("rememberUserId", "false"))) {
            LocaleUtils.clearLocaleFile("locale");
        }

        // 言語リソースの読み込み
        LocaleUtils.load("locale");

        // プラグインの読み込み
        MainMenuContainer.createInstance();

        // 画面設定
        SceneProperties sp = new SceneProperties(stage, AdProperty.getProperties());
        sp.setAppTitle(LocaleUtils.getString("key.adManagerAppTitle") + " - " + LocaleUtils.getString("key.verison") + " " + getBuildVersion());
        sp.setAppIcon(new Image("image/appicon.png"));
        sp.addCssPath("/styles/colorStyles.css");
        sp.addCssPath("/styles/designStyles.css");
        sp.addCssPath("/styles/fontStyles.css");
        sp.setMinWidth(SCREEN_MIN_WIDTH);
        sp.setMinHeight(SCREEN_MIN_HEIGHT);

        String widthName = sp.getStage().getClass().getCanonicalName() + "." + sp.getStage().widthProperty().getName();
        String heightName = sp.getStage().getClass().getCanonicalName() + "." + sp.getStage().heightProperty().getName();
        if (StringUtils.isEmpty(sp.getProperties().getProperty(widthName))
                || StringUtils.isEmpty(sp.getProperties().getProperty(heightName))) {
            sp.getProperties().setProperty(widthName, String.valueOf(SCREEN_MIN_WIDTH));
            sp.getProperties().setProperty(heightName, String.valueOf(SCREEN_MIN_HEIGHT));
        }

        // ログイン画面へ
        SceneContiner.createInstance(sp);
        SceneContiner sc = SceneContiner.getInstance();
        sc.trans("LoginScene");
    }

    public static void main(String[] args) {
        Logger logger = LogManager.getLogger();
        logger.info("Starting the application.");

        try {
            // 言語リソースプラグイン読み込み
            //PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
            //PluginLoader.load(LocalePluginInterface.class);
 
            // プロパティファイル読み込み
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adManeApp.properties");

            // サーバーからシステム設定を取得する
            getServerSystemProps();

        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        //FX起動.
        launch(args);

        //後始末.
        try {
            MainMenuContainer.getInstance().pluginDestructor();
            SerialCommunication sc = SerialCommunication.getIncetance();
            if (Objects.nonNull(sc)) {
                sc.disconect();
            }

            AdProperty.store();

            cleanCacheData();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("Shutdown the application.");

            // Java VMを終了する
            System.exit(0);
        }
    }

    /**
     * キャッシュデータを掃除する。
     */
    private static void cleanCacheData() {
        Logger logger = LogManager.getLogger();

        try {
            int term = Integer.parseInt(AdProperty.getProperties().getProperty("cacheTerm", "60"));
            Date due = DateUtils.addDays(new Date(), -term);
            //Pattern p = Pattern.compile("^[0-9]*$");

            File[] files = new File(Paths.CLIENT_CACHE_PDOC).listFiles();
            if (files == null) {
                return;
            }

            for (File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }
                //Matcher m = p.matcher(file.getName());
                //if (!m.matches()) {
                //    continue;
                //}
                if (file.lastModified() < due.getTime()) {
                    logger.info("Delete cache data: " + file.getPath());
                    recursiveDeleteFile(file);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ファイルを削除する。
     * ディレクトリの場合は再帰処理を行い、削除する。
     *
     * @param file
     * @throws Exception
     */
    private static void recursiveDeleteFile(final File file) throws Exception {
        // 存在しない場合は処理終了
        if (!file.exists()) {
            return;
        }
        // 対象がディレクトリの場合は再帰処理
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                recursiveDeleteFile(child);
            }
        }
        // 対象がファイルもしくは配下が空のディレクトリの場合は削除する
        file.delete();
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
                URL url = this.getClass().getResource("/adtekfuji/admanagerapp/MainApp.class");
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPAN);
                buildVersion = formatter.format(new Date(url.openConnection().getLastModified()));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return buildVersion;
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
