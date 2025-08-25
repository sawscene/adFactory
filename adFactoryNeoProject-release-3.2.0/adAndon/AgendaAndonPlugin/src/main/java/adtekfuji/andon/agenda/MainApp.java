package adtekfuji.andon.agenda;

import adtekfuji.andon.agenda.common.AgendaSettings;
import adtekfuji.andon.agenda.common.Constants;
import adtekfuji.andon.agenda.common.KanbanStatusConfig;
import adtekfuji.andon.agenda.model.data.ConfigData;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.ResourceInfoFacade;
import adtekfuji.clientservice.SystemResourceFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.fxscene.StageProperties;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.IniFile;
import adtekfuji.utility.PathUtils;
import adtekfuji.utility.PropertyUtils;
import adtekfuji.utility.StringUtils;
import java.io.*;
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
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.LocaleFileInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.resource.ResourceInfoEntity;
import jp.adtekfuji.adFactory.entity.system.SystemOptionEntity;
import jp.adtekfuji.adFactory.entity.system.SystemPropEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.mainapp.LocalePluginInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainApp extends Application {

    private static Logger logger;
    private static String equipmentIdName;

    private final Properties properties = AdProperty.getProperties();

    @Override
    public void start(Stage stage) throws Exception {
        try {
            ResourceBundle rb = LocaleUtils.load("locale");

            ConfigData configData = ConfigData.getInstance();
            configData.setEquipmentIdName(equipmentIdName);

            SceneProperties sp = new SceneProperties(stage, AdProperty.getProperties());

            // 設備ログインに必要な設備管理名を保存
            if (!StringUtils.isEmpty(equipmentIdName)) {
                sp.getProperties().setProperty("equipmentIdName", equipmentIdName);
            }

            sp.addCssPath("/styles/agendaStyles.css");
            sp.setAppTitle("adAgendaMonitor" + " - " + LocaleUtils.getString("key.verison") + " " + getBuildVersion());
            sp.setAppIcon(new Image("image/appicon.png"));

            // オプションライセンスを取得する。
            this.getSystemOption();

            // キャッシュする情報を取得する
            CacheUtils.createCacheData(EquipmentInfoEntity.class, true);
            CacheUtils.createCacheData(OrganizationInfoEntity.class, true);

            // アジェンダモニター設定を読み込む
            AgendaSettings.load();

            // カンバン進捗情報の設定で「進捗情報CSVファイル出力あり」の場合、進捗モニタ設定の「自動スクロール」を「ON」にする。
            if (KanbanStatusConfig.getEnableKanbanStatusCsv()) {
                AgendaSettings.getAndonSetting().getAgendaMonitorSetting().setAutoScroll(true);
            }

            AgendaSettings.buildConfigData();
            AgendaSettings.buildCurrentData();

            // アジェンダモニターに存在しない項目はpropertiesを参照
            configData.setAdFactoryServerURI(ClientServiceProperty.getServerUri());

            SceneContiner.createInstance(sp);
            SceneContiner sc = SceneContiner.getInstance();
            sc.trans("AgendaMain");

            stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (WindowEvent event) -> {
                logger.info("Closing the window...");
                StageProperties stageProperties = new StageProperties(stage, sc.getSceneProperties().getProperties());
                stageProperties.storation();
            });

            final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade(ClientServiceProperty.getServerUri());
            final ResourceInfoFacade resourceInfoFacade = new ResourceInfoFacade(ClientServiceProperty.getServerUri());

            EquipmentInfoEntity equipmentInfoEntity = equipmentInfoFacade.findName(equipmentIdName);
            List<LocaleFileInfoEntity> langIds = jp.adtekfuji.adFactory.utility.JsonUtils.jsonToObjects(equipmentInfoEntity.getLangIds(), LocaleFileInfoEntity[].class);
            // サーバよりデータ取得
            Map<String, String> localMap = langIds.stream()
                    .sorted(Comparator.comparing(ids -> ids.getLocaleType().getPriority())) // 優先順にソート
                    .map(id -> resourceInfoFacade.find(id.resource().getResourceId()))      // 変換データ取得
                    .map(ResourceInfoEntity::getResourceString)
                    .filter(str -> !StringUtils.isEmpty(str))
                    .map(str -> str.split("\r\n|\r|\n"))
                    .flatMap(Arrays::stream)
                    .map(line -> line.split("="))
                    .filter(line -> line.length == 2)
                    .collect(Collectors.toMap(l -> l[0], l -> l[1]));

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
        }catch (Exception ex) {
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

        if (args.length > 0) {
            equipmentIdName = args[0];
        }

        String kanbanStatusConfFile;

        if (StringUtils.isEmpty(equipmentIdName)) {
            System.setProperty(Constants.LOG_FILE_NAME, "adAgendaMonitor");
            System.setProperty(Constants.CONF_FILE_NAME, "adAndonApp.properties");

            // カンバン進捗情報の設定ファイル
            kanbanStatusConfFile = KanbanStatusConfig.KANBAN_STATUS_PROPERTY_DEF;
        } else {
            System.setProperty(Constants.LOG_FILE_NAME, "adAgendaMonitor_" + equipmentIdName);
            System.setProperty(Constants.CONF_FILE_NAME, "adAndonApp_" + equipmentIdName + ".properties");

            // カンバン進捗情報の設定ファイル
            kanbanStatusConfFile = addStringPath(KanbanStatusConfig.KANBAN_STATUS_PROPERTY_DEF, equipmentIdName);
        }

        logger = LogManager.getLogger();
        logger.info("Starting the application.");

        try {
            // 言語ファイルプラグイン読み込み
            PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
            PluginLoader.load(LocalePluginInterface.class);

            // プロパティファイル読み込み
            if (!StringUtils.isEmpty(equipmentIdName)) {
                String confFolder = Paths.get(System.getenv("ADFACTORY_HOME"), "conf").toString();

                Path path = Paths.get(System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + System.getProperty(Constants.CONF_FILE_NAME));
                Path defaultPath = Paths.get(System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + "adAndonApp.properties");
                if (!Files.exists(path) && Files.exists(defaultPath)) {
                    Files.copy(defaultPath, path);
                }

                Path statusConfPath = Paths.get(confFolder, kanbanStatusConfFile);
                Path statusConfDefaultPath = Paths.get(confFolder, KanbanStatusConfig.KANBAN_STATUS_PROPERTY_DEF);
                if (!Files.exists(statusConfPath) && Files.exists(statusConfDefaultPath)) {
                    Files.copy(statusConfDefaultPath, statusConfPath);
                }
            }

            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load(System.getProperty(Constants.CONF_FILE_NAME));

            // カンバン進捗情報の設定を取得する。
            KanbanStatusConfig.load(kanbanStatusConfFile);

            // サーバーからシステム設定を取得する。
            getServerSystemProps();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        launch(args);

        try {
            AdProperty.store();

            // カンバン進捗情報の設定を保存する。
            KanbanStatusConfig.store();

        } catch (IOException ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("Shutdown the application.");

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
            String path = PathUtils.getRootPath() + "\\version_agenda.ini";
            File verIni = new File(path);
            if (verIni.exists()) {
                IniFile iniFile = new IniFile(path);
                buildVersion = iniFile.getString("Version", "Ver", null);
            } else {
                URL url = this.getClass().getResource("/adtekfuji/andon/agenda/MainApp.class");
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
     * ファイル名の拡張子の前に、「_」と指定した文字列を追加する。
     * (aaa.ext → aaa_挿入する文字列.ext)
     *
     * @param path パス(ファイル名)の文字列
     * @param insertString 挿入する文字列
     * @return 挿入後のパス(ファイル名)の文字列
     */
    private static String addStringPath(String path, String insertString) {
        int dotPos = path.lastIndexOf(".");
        String path1;
        String ext;
        if (dotPos < 0) {
            path1 = path;
            ext = "";
        } else {
            path1 = path.substring(0, dotPos);
            ext = path.substring(dotPos);
        }
        return new StringBuilder(path1).append("_").append(insertString).append(ext).toString();
    }

    /**
     * オプションライセンスを取得する。
     */
    private void getSystemOption() {
        SystemResourceFacade systemResourceFacade = new SystemResourceFacade();
        List<SystemOptionEntity> optionLicenses = systemResourceFacade.getLicenseOptions();
        if (Objects.isNull(optionLicenses)) {
            return;
        }

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
        if (Objects.nonNull(properties))  {
            for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
                String propertyName = (String) e.nextElement();
                String propertyValue = properties.getProperty(propertyName);
                this.properties.setProperty(propertyName, propertyValue);
            }
        }
    }
}
