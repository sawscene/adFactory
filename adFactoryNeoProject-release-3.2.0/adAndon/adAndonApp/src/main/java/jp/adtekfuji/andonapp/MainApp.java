package jp.adtekfuji.andonapp;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.ResourceInfoFacade;
import adtekfuji.clientservice.SystemResourceFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.IniFile;
import adtekfuji.utility.PathUtils;
import adtekfuji.utility.PropertyUtils;
import adtekfuji.utility.StringUtils;
import java.io.*;
import java.net.Inet4Address;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.LocaleFileInfoEntity;
import jp.adtekfuji.adFactory.entity.resource.ResourceInfoEntity;
import jp.adtekfuji.adFactory.entity.system.SystemPropEntity;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.utility.AndonMonitorLineProductSettingFileAccessor;
import jp.adtekfuji.andonapp.comm.LocalConfig;
import jp.adtekfuji.andonapp.common.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

public class MainApp extends Application {

    private static Logger logger;
    private static String equipmentIdName = "";
    private static Stage stage = null;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        // 言語リソースの読み込み
        ResourceBundle rb = LocaleUtils.load("locale");

        final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade(ClientServiceProperty.getServerUri());
        final ResourceInfoFacade resourceInfoFacade = new ResourceInfoFacade(ClientServiceProperty.getServerUri());

        EquipmentInfoEntity equipmentInfoEntity = equipmentInfoFacade.findName(equipmentIdName);
        List<LocaleFileInfoEntity> langIds = jp.adtekfuji.adFactory.utility.JsonUtils.jsonToObjects(equipmentInfoEntity.getLangIds(), LocaleFileInfoEntity[].class);
        // サーバよりデータ取得
        Map<String, String> localMap = langIds.stream()
                .sorted(Comparator.comparing(ids->ids.getLocaleType().getPriority())) // 優先順にソート
                .map(id->resourceInfoFacade.find(id.resource().getResourceId()))      // 変換データ取得
                .map(ResourceInfoEntity::getResourceString)
                .filter(str->!StringUtils.isEmpty(str))
                .map(str->str.split("\r\n|\r|\n"))
                .flatMap(Arrays::stream)
                .map(line->line.split("="))
                .filter(line->line.length==2)
                .collect(Collectors.toMap(l->l[0], l->l[1]));

        if (!localMap.isEmpty()) {
            // デフォルト設定取込
            Map<String, String> rbMap = Collections.list(rb.getKeys())
                    .stream()
                    .collect(Collectors.toMap(Function.identity(), LocaleUtils::getString, (a, b) -> b, TreeMap::new));

            rbMap.putAll(localMap);

            // 個人用言語ファイルを作成
            File file = LocaleUtils.getLocaleFile("locale");
            if (Objects.nonNull(file)) {
                try (BufferedOutputStream outBuffer = new BufferedOutputStream(new FileOutputStream(file));
                     BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outBuffer, "8859_1"))) {
                    for (Map.Entry<String, String> entry : rbMap.entrySet()) {
                        String val = PropertyUtils.convertToUnicodeEscape(entry.getValue());
                        bw.write(entry.getKey() + "=" + val);
                        bw.newLine();
                    }
                    bw.flush();
                    // 言語リソースの再読み込み
                    LocaleUtils.load("locale");
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            }

            file.delete();
        }


        // 画面設定
        SceneProperties sp = new SceneProperties(stage, AdProperty.getProperties());
        String equipmentInfo = null;
        if (!equipmentIdName.isEmpty()) {
            equipmentInfo = equipmentIdName;
            sp.getProperties().setProperty("equipmentIdName", equipmentIdName);
        } else {
            equipmentInfo = Inet4Address.getLocalHost().getHostAddress();
        }

        sp.setAppTitle(LocaleUtils.getString("key.AndonAppTitle") + "(" + equipmentInfo + ")"
                + " - " + LocaleUtils.getString("key.verison") + " " + getBuildVersion());
        sp.setAppIcon(new Image("image/appicon.png"));
        sp.addCssPath("/styles/andonStyles.css");
        sp.setMinWidth(400.0);
        sp.setMinHeight(300.0);
        if (Strings.isBlank(sp.getProperties().getProperty(Constants.STAGE_WIDTH))) {
            sp.getProperties().setProperty(Constants.STAGE_WIDTH, "400.0");
        }
        if (Strings.isBlank(sp.getProperties().getProperty(Constants.STAGE_HEIGHT))) {
            sp.getProperties().setProperty(Constants.STAGE_HEIGHT, "300.0");
        }

        //表示するモニターを設定
        List<Screen> screens = Screen.getScreens();

        String targetMonitorString = AdProperty.getProperties().getProperty(Constants.TARGET_MONITOR); 
        int targetMonitor = 1;
        if (Objects.nonNull(targetMonitorString)) {
            try {
                // プロパティファイルのディスプレイ番号を設定
                targetMonitor = Long.valueOf(targetMonitorString).intValue();
            } catch (Exception ex) {
                logger.error(ex, ex);
            }
        } else {
            // モニター設定を読み込む
            AndonMonitorLineProductSetting monitorSetting = getAndonMonitorLineProductSetting();
            if (Objects.nonNull(monitorSetting) && monitorSetting.getTargetMonitor() > 0) {
                // xmlファイルの設定のディスプレイ番号を設定 xmlファイルがない場合は新規作成して参照するためsetProperty不要
                targetMonitor = monitorSetting.getTargetMonitor();
            }
        }

        //存在しないモニタの場合プライマリの設定を使う
        if (targetMonitor < 1 || screens.size() < targetMonitor) {
            targetMonitor = 1;
        }
        --targetMonitor;
        Rectangle2D visualBounds = screens.get(targetMonitor).getBounds();
        if (LocalConfig.isFullScreen()) {
            // フルスクリーン有効の場合、指定モニタの左上に表示する。
            sp.getProperties().setProperty(Constants.STAGE_X, String.valueOf(visualBounds.getMinX()));
            sp.getProperties().setProperty(Constants.STAGE_Y, String.valueOf(visualBounds.getMinY()));
        } else {
            // フルスクリーン無効で、表示位置の指定がない場合は指定モニタの左上に表示する。
            if (Strings.isBlank(sp.getProperties().getProperty(Constants.STAGE_X))) {
                sp.getProperties().setProperty(Constants.STAGE_X, String.valueOf(visualBounds.getMinX()));
            }
            if (Strings.isBlank(sp.getProperties().getProperty(Constants.STAGE_Y))) {
                sp.getProperties().setProperty(Constants.STAGE_Y, String.valueOf(visualBounds.getMinY()));
            }
        }

        if (!LocalConfig.isShowFrame()) {
            stage.initStyle(StageStyle.UNDECORATED);
        }

        // 画面表示
        SceneContiner.createInstance(sp);
        SceneContiner sc = SceneContiner.getInstance();
        sc.trans("Main");
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            equipmentIdName = args[0];
        }

        if (StringUtils.isEmpty(equipmentIdName)) {
            System.setProperty(Constants.LOG_FILE_NAME, "adAndonApp");
        } else {
            System.setProperty(Constants.LOG_FILE_NAME, "adAndonApp_" + equipmentIdName);
        }

        logger = LogManager.getLogger();
        logger.info("Starting the application.");

        AndonPluginContainer andonPluginContainer = null;
        try {
            // 言語ファイルプラグイン読み込み.
            // PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
            // PluginLoader.load(LocalePluginInterface.class);

            // プロパティファイル読み込み.
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            if (!equipmentIdName.isEmpty()) {
                String monitorProp = "adAndonApp_" + equipmentIdName + ".properties";
                Path monitorPropPath = Paths.get(System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + monitorProp);
                Path defaultPropPath = Paths.get(System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + "adAndonApp.properties");
                if (!Files.exists(monitorPropPath) && Files.exists(defaultPropPath)) {
                    Files.copy(defaultPropPath, monitorPropPath);
                }
                AdProperty.load(monitorProp);
            } else {
                AdProperty.load("adAndonApp.properties");
            }

            // サーバーからシステム設定を取得する。
            getServerSystemProps();

            andonPluginContainer = AndonPluginContainer.getInstance();
            
            //FX起動.
            launch(args);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("Shutdown the application.");

            try {
                //後始末.
                if (Objects.nonNull(andonPluginContainer)) {
                    andonPluginContainer.stopService();
                }
                AdProperty.store();
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }

            // Java VMを終了する
            System.exit(0);
        }
    }

    /**
     * ビルドバージョンを取得する。
     *
     * @return
     */
    public String getBuildVersion() {
        String buildVersion = null;

        try {
            String path = PathUtils.getRootPath() + "\\version.ini";
            File verIni = new File(path);
            if (verIni.exists()) {
                IniFile iniFile = new IniFile(path);
                buildVersion = iniFile.getString("Version", "Ver", null);
            } else {
                URL url = this.getClass().getResource("/jp/adtekfuji/andonapp/MainApp.class");
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
    
    /**
     * 設定を読み込む。
     *
     * @return 進捗モニター設定画面の設定
     */
    public static AndonMonitorLineProductSetting getAndonMonitorLineProductSetting() {
        logger.info("getAndonMonitorLineProductSetting start");
            AndonMonitorLineProductSetting setting = new AndonMonitorLineProductSetting();
        try {
            Long monitorId = AndonLoginFacade.getMonitorId();
            if (monitorId != 0) {
                // サーバーから
                AndonMonitorSettingFacade facade = new AndonMonitorSettingFacade();
                setting = (AndonMonitorLineProductSetting) facade
                        .getLineSetting(monitorId, AndonMonitorLineProductSetting.class);
            }

            if (Objects.isNull(setting)) {
                // ローカルから
                AndonMonitorLineProductSettingFileAccessor accessor = new AndonMonitorLineProductSettingFileAccessor();
                String filePath = accessor.getFilePath();
                File file = new File(filePath);
                if (!file.exists()) {
                    accessor.save(AndonMonitorLineProductSetting.create());
                }
                setting = accessor.load();
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info("getAndonMonitorLineProductSetting end");
        return setting;
    }

    /**
     * Stage を取得する。
     * 
     * @return Stage
     */
    public static Stage getStage() {
        return stage;
    }

}
