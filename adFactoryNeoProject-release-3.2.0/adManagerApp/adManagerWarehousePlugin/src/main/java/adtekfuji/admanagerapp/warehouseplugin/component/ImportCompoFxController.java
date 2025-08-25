/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.component;

import adtekfuji.admanagerapp.warehouseplugin.socket.WarehouseClientHandler;
import adtekfuji.admanagerapp.warehouseplugin.socket.WarehouseClientService;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * インポート画面
 *
 * @author nar-nakamura
 */
@FxComponent(id = "ImportCompo", fxmlPath = "/fxml/warehouseplugin/import_compo.fxml")
public class ImportCompoFxController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final WarehouseClientHandler clientHandler = WarehouseClientService.getInstance().getClient();

    private static final String GET_STATUS = "GET_STATUS";
    private static final String AUTO_SYNC = "AUTO_SYNC";
    private static final String START_IMPORT = "START_IMPORT";
    private static final String STOP_SYNC = "STOP_SYNC";
    private static final String RESPONCE_STOP = "SYNC_STOP";
    private static final String RESPONCE_RUNNING = "SYNC_RUNNING";

    private static final String IMPORT_PATH = "warehouse_importPath";
    private static final String AUTO_IMPORT = "warehouse_autoImport";
    private static final String INTERVAL = "warehouse_interval";

    @FXML
    private TextField importFolderField;
    @FXML
    private Label messageLabel;
    @FXML
    private Button importButton;
    @FXML
    private Pane progressPane;

    /**
     * インポート画面を初期化する。
     * 
     * @param url 
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Properties properties = AdProperty.getProperties();
        String importPath = properties.getProperty(IMPORT_PATH, "C:\\adFactory_IN");

        this.importFolderField.setText(importPath);
        this.blockUI(false);
    }

    /**
     * 画面操作を禁止する。
     * 
     * @param block 
     */
    private void blockUI(boolean block) {
        Platform.runLater(() -> {
            sc.blockUI("ContentNaviPane", block);
            progressPane.setVisible(block);
        });
    }

    /**
     * リクエストコマンドを送信する。
     * 
     * @param command コマンド
     * @param message メッセージ
     * @return 処理結果
     */
    private void sendCommand(String command, String message) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String responce = clientHandler.send(command, message);

                Platform.runLater(() -> {
                    String text;
                    if (!StringUtils.isEmpty(responce)) {
                        Date date = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        text = rb.getString("last_time") + " " + format.format(date);
                    } else {
                        text = rb.getString("last_time") + " " + rb.getString("key.import.failed");
                    }
                    messageLabel.setText(text);
                    importButton.setDisable(false);
                });
        
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void onSelectFolderAction(ActionEvent event) {
        blockUI(true);
        DirectoryChooser dc = new DirectoryChooser();
        File fol = new File(importFolderField.getText());
        if (fol.exists() && fol.isDirectory()) {
            dc.setInitialDirectory(fol);
        }
        File selectedFile = dc.showDialog(sc.getStage().getScene().getWindow());
        if(selectedFile != null) {
            importFolderField.setText(selectedFile.getPath());
        }
        blockUI(false);
    }

    @FXML
    private void onImportAction(ActionEvent event) {
        try {
            blockUI(true);
            
            String importPath = this.importFolderField.getText();
            File file = new File(importPath);
            if (!file.exists() || !file.isDirectory()) {
                return;
            }
            
            Properties properties = AdProperty.getProperties();
            boolean autoImport = Boolean.valueOf(properties.getProperty(AUTO_IMPORT, "true"));
            int interval = Integer.valueOf(properties.getProperty(INTERVAL, "1"));
            
            StringBuilder sb = new StringBuilder();
            sb.append(importPath);
            sb.append(",");
            sb.append(autoImport);
            sb.append(",");
            sb.append(interval);

            properties.setProperty(IMPORT_PATH, importPath);

            // インポート
            importButton.setDisable(true);
            sendCommand(START_IMPORT, sb.toString());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }
}
