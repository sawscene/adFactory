package jp.adtekfuji.adsetuptool.scene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import jp.adtekfuji.adsetuptool.MainApp;
import jp.adtekfuji.adsetuptool.utils.LocaleUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class adSetupToolFXController implements Initializable {

    private final Logger logger = LogManager.getLogger();

    private static final String PROPERTIES_EXT = ".properties";// 設定ファイルの拡張子
    private static final String INI_EXT = ".ini";// 設定ファイルの拡張子
    private static final String PROPERTIES_CHARSET = "MS932";// 設定ファイルのキャラセット (MS932: Shift-JIS)

    // 設定ファイル名 (PROPERTIES_EXTを追加した文字列が実際のファイル名)
    private static final String MANAGER_PROP = "adManeApp";// adManager
    private static final String MONITOR_PREFIX = "adAndonApp";// adMonitor設定ファイル名の接頭辞
    private static final List<String> MONITOR_PROPS = new ArrayList<>();// adMonitor
    private static final String FUJI_CLIENT_PROP = "adFactoryForFujiClient";
    private static final String CELL_PRODUCTION_MONITOR_PROP = "adCellProductionMonitor";
    private static final String INTERFACE_PROP = "adInterface"; // adInterfaceService

    private static final String COMMON_REST_SERVICE_URI = "adManagerServiceURI";// adFactoryServerのREST URI
    private static final String COMMON_REST_SERVICE_URI_2 = "adFactoryServiceURI";// adFactoryServerのREST URI (adCellProductionMonitorで使用)
    private static final String CUSTOM_REST_SERVICE_URI = "adFactoryForFujiServiceURI";// adFactoryForFujiServerのREST URI
    private static final String INTERFACE_SERVICE_ADDRESS = "adInterfaceServiceAddress";// adInterfaceのアドレス

    private static final String DEFAULT_SERVER = "https://localhost";
    private static final String COMMON_REST_SERVER = "/adFactoryServer/rest";
    private static final String CUSTOM_REST_SERVER = "/adFactoryForFujiServer/rest";

    private final HashMap<String, Properties> propertiesMap = new HashMap<>();// 設定ファイル群
    private boolean isCustom = false;// カスタム (true: あり, false: なし)

    private final List<TextField> editTextFields = new ArrayList();// 編集チェック対象リスト

    // 予実モニタ 設定
    private static final String AGENDA_MON_INI_PREFIX = "agendaAndonPlugin";    // 設定ファイル名接頭辞
    private static final List<String> AGENDA_MON_INI_FILES = new ArrayList<>(); // 設定ファイル名
    private static final String SYSTEM_ADFACTORY_SERVER_URI = "system.adFactoryServerURI";// adFactoryServerのREST URIの「section.key」
    private final HashMap<String, Configuration> agendaConfigFilesMap = new HashMap<>();

    // 設定ファイルのフォルダパス
    private String confPath;

    @FXML
    private GridPane operationPane;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab commonTab;
    @FXML
    private TextField commonServerField;
    @FXML
    private Tab customTab;
    @FXML
    private TextField customServerField;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Pane progressPane;

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);

        List<String> appArgs = MainApp.getAppArgs();// 起動引数

        String homePath = System.getenv("ADFACTORY_HOME");
        if (StringUtils.isEmpty(homePath)) {
            File curDir = new File(System.getProperty("user.dir"));
            homePath = curDir.getParent();
        }
        logger.info(String.format("ADFACTORY_HOME: %s", homePath));

        this.confPath = homePath + File.separator + "conf";

        // adManager
        this.loadPropertiesFile(MANAGER_PROP);

        try {
            // adMonitor
            MONITOR_PROPS.clear();
            Files.find(Paths.get(confPath), 1,
                    (path, attr) -> Files.isRegularFile(path)
                    && path.getFileName().toString().startsWith(MONITOR_PREFIX)
                    && path.getFileName().toString().endsWith(PROPERTIES_EXT))
                    .forEach(a -> {
                        String name = a.getFileName().toString();
                        name = name.substring(0, name.lastIndexOf("."));
                        MONITOR_PROPS.add(name);
                        this.loadPropertiesFile(name);
                    });
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        try {
            // adAgendaMonitor
            AGENDA_MON_INI_FILES.clear();
            Files.find(Paths.get(confPath), 1,
                    (path, attr) -> Files.isRegularFile(path)
                    && path.getFileName().toString().startsWith(AGENDA_MON_INI_PREFIX)
                    && path.getFileName().toString().endsWith(INI_EXT))
                    .forEach(a -> {
                        String name = a.getFileName().toString();
                        name = name.substring(0, name.lastIndexOf("."));
                        AGENDA_MON_INI_FILES.add(name);
                        this.loadIniFile(name);
                    });
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        // adInterfaceService
        this.loadPropertiesFile(INTERFACE_PROP);

        // adFactoryForFujiClient
        if (this.loadPropertiesFile(FUJI_CLIENT_PROP)) {
            this.isCustom = true;
        }
        // adCellProductionMonitor
        if (this.loadPropertiesFile(CELL_PRODUCTION_MONITOR_PROP)) {
            this.isCustom = true;
        }

        // 全般タブ
        this.dispCommonTab();
        // カスタムタブ
        if (this.isCustom) {
            this.dispCustomTab();

            // 起動引数で "-custom" が指定された場合、カスタムタブを選択状態にする。
            if (appArgs.contains("-custom")) {
                this.tabPane.getSelectionModel().select(this.customTab);
            }
        } else {
            this.tabPane.getTabs().remove(this.customTab);
        }
    }

    /**
     * OK ボタン
     *
     * @param event
     */
    @FXML
    private void okButtonAction(ActionEvent event) {
        blockUI(true);
        Task task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return savePropertiesFiles();
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                blockUI(false);
                Boolean isOk = this.getValue();
                if (isOk) {
                    closeWindow();
                } else {
                    // 設定の保存に失敗
                    showAlert(LocaleUtils.getString("key.WindowTitle"), LocaleUtils.getString("key.alert.FailedToSaveSetting"), Alert.AlertType.ERROR, ButtonType.OK);
                }
            }

            @Override
            protected void failed() {
                super.failed();
                blockUI(false);
            }
        };
        new Thread(task).start();
    }

    /**
     * キャンセル ボタン
     *
     * @param event
     */
    @FXML
    private void cancelButtonAction(ActionEvent event) {
        // 変更がある場合は破棄してもよいか警告ダイアログで確認する。
        if (isEdited()) {
            ButtonType alertResult = showAlert(LocaleUtils.getString("key.WindowTitle"), LocaleUtils.getString("key.alert.DiscardChanges"),
                    Alert.AlertType.CONFIRMATION, ButtonType.YES, ButtonType.NO);
            if (!ButtonType.YES.equals(alertResult)) {
                // YES以外は何もせずに戻る。
                return;
            }
        }

        this.closeWindow();
    }

    /**
     * 指定した設定ファイルを読み込む。
     *
     * @param fileName
     */
    private boolean loadPropertiesFile(String fileName) {
        boolean result = false;
        try {
            File file = new File(confPath, fileName + PROPERTIES_EXT);
            if (!file.exists()) {
                return result;
            }

            Properties properties = new Properties();
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file.getPath()), PROPERTIES_CHARSET)) {
                properties.load(reader);
            }

            propertiesMap.put(fileName, properties);

            result = true;
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 設定ファイルを保存する。
     *
     * @return
     */
    private boolean savePropertiesFiles() {
        boolean result = true;
        try {
            // Common Server
            String commonServerPath = commonServerField.getText();
            String commonServer = getServerPathToAddress(commonServerPath);
            String commonRestServiceURI = commonServerPath + COMMON_REST_SERVER;

            // adManager
            Properties managerProp = propertiesMap.get(MANAGER_PROP);
            if (Objects.nonNull(managerProp)) {
                managerProp.setProperty(COMMON_REST_SERVICE_URI, commonRestServiceURI);
                managerProp.setProperty(INTERFACE_SERVICE_ADDRESS, commonServer);
                if (!savePropertiesFile(MANAGER_PROP, managerProp)) {
                    // 保存失敗
                    result = false;
                }
            }

            // adMonitor
            for (String prop : MONITOR_PROPS) {
                Properties monitorProp = propertiesMap.get(prop);
                if (Objects.nonNull(monitorProp)) {
                    monitorProp.setProperty(COMMON_REST_SERVICE_URI, commonRestServiceURI);
                    monitorProp.setProperty(INTERFACE_SERVICE_ADDRESS, commonServer);
                    if (!savePropertiesFile(prop, monitorProp)) {
                        // 保存失敗
                        result = false;
                    }
                }
            }

            // adAgendaMonitor
            for (String iniFile : AGENDA_MON_INI_FILES) {
                Configuration agendaConfig = agendaConfigFilesMap.get(iniFile);
                if (Objects.nonNull(agendaConfig)) {
                    agendaConfig.setProperty(SYSTEM_ADFACTORY_SERVER_URI, commonRestServiceURI);
                    if (!this.saveIniFile(iniFile, agendaConfig)) {
                        // 保存失敗
                        result = false;
                    }
                }
            }

            // adInterfaceService
            Properties interfaceProp = propertiesMap.get(INTERFACE_PROP);
            if (Objects.nonNull(interfaceProp)) {
                // adFactory APIのアドレスを設定する
                interfaceProp.setProperty(COMMON_REST_SERVICE_URI, commonRestServiceURI);
                if (!savePropertiesFile(INTERFACE_PROP, interfaceProp)) {
                    // 保存失敗
                    result = false;
                }
            }

            if (this.isCustom) {
                // Custom server
                String customServerPath = customServerField.getText();
                String customRestServiceURI = customServerPath + CUSTOM_REST_SERVER;

                // adFactoryForFujiClient
                Properties fujiClientProp = propertiesMap.get(FUJI_CLIENT_PROP);
                if (Objects.nonNull(fujiClientProp)) {
                    fujiClientProp.setProperty(COMMON_REST_SERVICE_URI, commonRestServiceURI);
                    fujiClientProp.setProperty(CUSTOM_REST_SERVICE_URI, customRestServiceURI);
                    if (!savePropertiesFile(FUJI_CLIENT_PROP, fujiClientProp)) {
                        // 保存失敗
                        result = false;
                    }
                }

                // adCellProductionMonitor
                Properties cellProductionMonitorProp = propertiesMap.get(CELL_PRODUCTION_MONITOR_PROP);
                if (Objects.nonNull(cellProductionMonitorProp)) {
                    cellProductionMonitorProp.setProperty(COMMON_REST_SERVICE_URI, commonRestServiceURI);
                    cellProductionMonitorProp.setProperty(COMMON_REST_SERVICE_URI_2, commonRestServiceURI);
                    cellProductionMonitorProp.setProperty(INTERFACE_SERVICE_ADDRESS, commonServer);
                    cellProductionMonitorProp.setProperty(CUSTOM_REST_SERVICE_URI, customRestServiceURI);
                    if (!savePropertiesFile(CELL_PRODUCTION_MONITOR_PROP, cellProductionMonitorProp)) {
                        // 保存失敗
                        result = false;
                    }
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            result = false;
        }
        return result;
    }

    /**
     * 指定した設定をファイルに保存する。
     *
     * @param fileName
     * @param properties
     * @return
     */
    private boolean savePropertiesFile(String fileName, Properties properties) {
        boolean result = false;
        try {
            File file = new File(confPath, fileName + PROPERTIES_EXT);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file.getPath()), PROPERTIES_CHARSET)) {
                properties.store(writer, null);
            }

            result = true;
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    private String getProperty(String fileName, String key) {
        Properties prop = propertiesMap.get(fileName);
        if (Objects.isNull(prop)) {
            return null;
        }
        return prop.getProperty(key);
    }

    /**
     * INIファイルを読み込む。
     *
     * @param fileName ファイル名
     * @return 読み込んだ設定
     */
    private boolean loadIniFile(String fileName) {
        boolean result = false;
        try {
            File file = new File(confPath, fileName + INI_EXT);
            if (file.exists()) {
                FileInputStream input = new FileInputStream(file);
                InputStreamReader stream = new InputStreamReader(input, PROPERTIES_CHARSET);

                INIConfiguration iniConfig = new INIConfiguration();
                iniConfig.read(new BufferedReader(stream));

                agendaConfigFilesMap.put(fileName, iniConfig);
                
                result = true;
            }
        } catch (ConfigurationException | IOException ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 設定をINIファイルに書き込む。
     *
     * @param fileName ファイル名
     * @param config 設定
     * @return 結果 (true:成功, false:失敗)
     */
    private boolean saveIniFile(String fileName, Configuration config) {
        boolean result = false;
        try {
            File file = new File(confPath, fileName + INI_EXT);

            FileOutputStream output = new FileOutputStream(file);
            OutputStreamWriter stream = new OutputStreamWriter(output, PROPERTIES_CHARSET);

            INIConfiguration iniConfig = new INIConfiguration();
            iniConfig.append(config);
            iniConfig.write(new BufferedWriter(stream));

            result = true;
        } catch (ConfigurationException | IOException ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 全般タブ
     */
    private void dispCommonTab() {
        String serviceURI = null;
        if (propertiesMap.containsKey(MANAGER_PROP)) {
            // adManagerの設定を使用する。
            serviceURI = getProperty(MANAGER_PROP, COMMON_REST_SERVICE_URI);
        }

        if (Objects.isNull(serviceURI) || serviceURI.isEmpty()) {
            for (String prop : MONITOR_PROPS) {
                if (propertiesMap.containsKey(prop)) {
                    // adMonitorの設定を使用する。
                    serviceURI = getProperty(prop, COMMON_REST_SERVICE_URI);
                    break;
                }
            }
        }

        if ((Objects.isNull(serviceURI) || serviceURI.isEmpty()) && propertiesMap.containsKey(CELL_PRODUCTION_MONITOR_PROP)) {
            // adCellProductionMonitorの設定を使用する。
            serviceURI = getProperty(CELL_PRODUCTION_MONITOR_PROP, COMMON_REST_SERVICE_URI);
        }

        if ((Objects.isNull(serviceURI) || serviceURI.isEmpty())) {
            for (String iniFile : AGENDA_MON_INI_FILES) {
                Configuration agendaConfig = agendaConfigFilesMap.get(iniFile);
                if (Objects.nonNull(agendaConfig)) {
                    // adAgendaMonitorの設定を使用する。
                    serviceURI = agendaConfig.getString(SYSTEM_ADFACTORY_SERVER_URI);
                    break;
                }
            }
        }

        if (Objects.isNull(serviceURI) || serviceURI.isEmpty()) {
            this.commonServerField.setText(DEFAULT_SERVER);
        } else {
            int pos = serviceURI.lastIndexOf(COMMON_REST_SERVER);
            String serverPath = serviceURI.substring(0, pos);

            this.addEditedTextFields(this.commonServerField, serverPath);
        }
    }

    /**
     * カスタムタブ
     */
    private void dispCustomTab() {
        String serviceURI = null;
        if (propertiesMap.containsKey(FUJI_CLIENT_PROP)) {
            // FujiClientServiceの設定を使用する。
            serviceURI = getProperty(FUJI_CLIENT_PROP, CUSTOM_REST_SERVICE_URI);
        } else if (propertiesMap.containsKey(CELL_PRODUCTION_MONITOR_PROP)) {
            // adCellProductionMonitorの設定を使用する。
            serviceURI = getProperty(CELL_PRODUCTION_MONITOR_PROP, CUSTOM_REST_SERVICE_URI);
        }

        if (Objects.isNull(serviceURI) || serviceURI.isEmpty()) {
            this.customServerField.setText(DEFAULT_SERVER);
        } else {
            int pos = serviceURI.lastIndexOf(CUSTOM_REST_SERVER);
            String serverPath = serviceURI.substring(0, pos);

            this.addEditedTextFields(this.customServerField, serverPath);
        }
    }

    /**
     * TextFieldにオリジナル値をセットして、編集チェック対象に追加する。
     *
     * @param item
     * @param text
     */
    private void addEditedTextFields(TextField item, String text) {
        item.setText(text);
        item.setUserData(text);
        this.editTextFields.add(item);
    }

    /**
     * 編集した項目があるかどうかチェックする。
     *
     * @return (true: あり, false: なし)
     */
    private boolean isEdited() {
        for (TextField item : this.editTextFields) {
            if (!item.getText().equals(item.getUserData())) {
                return true;
            }
        }
        return false;
    }

    /**
     * サーバーパスからアドレス部の文字列を取得する。
     *
     * @param serverPath ("https://アドレス", "http://アドレス:8080" 等)
     * @return アドレス部の文字列 (書式が異なる等で取得できない場合は"localhost")
     */
    private String getServerPathToAddress(String serverPath) {
        String result = "localhost";
        Pattern pattern = Pattern.compile("^https?://([^:/]*)");
        Matcher matcher = pattern.matcher(serverPath);
        if (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    /**
     * メッセージダイアログを表示する。
     *
     * @param title タイトル
     * @param message メッセージ
     * @param type ダイアログ種別
     * @return
     */
    private ButtonType showAlert(String title, String message, Alert.AlertType type, ButtonType... buttons) {
        ButtonType result = ButtonType.CANCEL;
        try {
            Alert alert = new Alert(type, "", buttons);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            Optional<ButtonType> opt = alert.showAndWait();
            if (opt.isPresent()) {
                result = opt.get();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * ウィンドウを閉じる
     */
    private void closeWindow() {
        try {
            this.getWindow().hide();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ウィンドウを取得する。
     *
     * @return
     */
    private Window getWindow() {
        return this.operationPane.getScene().getWindow();
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        operationPane.setDisable(flg);
        progressPane.setVisible(flg);
    }
}
