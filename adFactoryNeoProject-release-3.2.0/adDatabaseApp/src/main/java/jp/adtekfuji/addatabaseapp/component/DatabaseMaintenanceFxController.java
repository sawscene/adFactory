/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.component;

import adtekfuji.utility.NetworkFileUtil;
import io.vavr.control.Either;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import jp.adtekfuji.addatabase.common.AdDatabaseConfig;
import jp.adtekfuji.addatabase.utils.LocaleUtils;
import jp.adtekfuji.addatabaseapp.controller.PostgresManager;
import jp.adtekfuji.addatabaseapp.postgres.PGContents;
import jp.adtekfuji.addatabaseapp.utils.CMDContents;
import jp.adtekfuji.addatabaseapp.utils.ExecUtils;
import jp.adtekfuji.addatabaseapp.utils.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * データベースメンテナンス画面
 *
 * @author nar-nakamura
 */
public class DatabaseMaintenanceFxController implements Initializable {

    private final static Logger logger = LogManager.getLogger();

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane progressPane;
    @FXML
    private ListView databaseInfoList;
    @FXML
    private TextField backupDirField;
    @FXML
    private RadioButton localRadio;
    @FXML
    private RadioButton networkRadio;
    @FXML
    private ToggleGroup backupDestGroup;
    @FXML
    private TextField backupUserField;
    @FXML
    private PasswordField backupPasswordField;
    @FXML
    private Label backupUserLabel;
    @FXML
    private Label backupPasswordLabel;
    @FXML
    private Label lastMainteDateLabel;
    @FXML
    private Label lastBackupDateLabel;

    private final PostgresManager postgresManager = new PostgresManager();

    private final int SCHEDULE_DATA_HEADER_ROW = 1;
    private final int SCHEDULE_DATA_ROW = 2;

    private final int SCHEDATA_NEXT_RUN_COL = 2;
    private final int SCHEDATA_LAST_RUN_COL = 5;
    private final int SCHEDATA_TASK_TO_RUN_COL = 8;
    private final int SCHEDATA_SHCE_TYPE_COL = 18;
    private final int SCHEDATA_START_TIME_COL = 19;
    private final int SCHEDATA_DAYS_COL = 22;

    // 定期メンテナンス
    private String lastMaintenanceDate = "";
    private Boolean regularMainteEnabled = false;
    private Boolean autoBackupEnabled = false;
    private String regularMainteScheduleType = "";
    private String regularMainteScheduleDays = "";
    private String regularMainteStartTime = "";

    // 定期バックアップ
    private String lastBackupDate = "";
    private Boolean regularBackupEnabled = false;
    private String regularBackupScheduleType = "";
    private String regularBackupScheduleDays = "";
    private String regularBackupStartTime = "";


    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);

        this.backupUserField.setText(AdDatabaseConfig.getBackupNetworkUser());
        this.backupPasswordField.setText(AdDatabaseConfig.getBackupNetworkPassword());

        String destType = AdDatabaseConfig.getBackupDestType();
        if ("NETWORK".equalsIgnoreCase(destType)) {
            this.networkRadio.setSelected(true);
            this.backupDirField.setText(AdDatabaseConfig.getBackupNetworkDir());
        } else {
            this.localRadio.setSelected(true);
            this.backupDirField.setText(AdDatabaseConfig.getBackupLocalDir());
        }

        this.backupUserField.managedProperty().bind(this.backupUserField.visibleProperty());
        this.backupPasswordField.managedProperty().bind(this.backupPasswordField.visibleProperty());
        this.backupUserLabel.managedProperty().bind(this.backupUserLabel.visibleProperty());
        this.backupPasswordLabel.managedProperty().bind(this.backupPasswordLabel.visibleProperty());

        this.backupUserField.textProperty().addListener((o, ov, nv) -> AdDatabaseConfig.setBackupNetworkUser(nv));
        this.backupPasswordField.textProperty().addListener((o, ov, nv) -> AdDatabaseConfig.setBackupNetworkPassword(nv));

        this.backupDestGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (this.localRadio.isSelected()) {
                AdDatabaseConfig.setBackupDestType("LOCAL");
                this.backupDirField.setText(AdDatabaseConfig.getBackupLocalDir());
            } else {
                AdDatabaseConfig.setBackupDestType("NETWORK");
                this.backupDirField.setText(AdDatabaseConfig.getBackupNetworkDir());
            }
            toggleNetworkFields();
        });

        toggleNetworkFields();
        
        this.loadScheduleSetting();
        if (!this.lastMaintenanceDate.isEmpty()) {
            this.lastMainteDateLabel.setText(this.lastMaintenanceDate);
        } else {
            this.lastMainteDateLabel.setText(LocaleUtils.getString("key.Schedule.NoHistory"));
        }

        this.loadBackupScheduleSetting();
        if (!this.lastBackupDate.isEmpty()) {
            this.lastBackupDateLabel.setText(this.lastBackupDate);
        } else {
            this.lastBackupDateLabel.setText(LocaleUtils.getString("key.Schedule.NoHistory"));
        }

        this.dispDatabaseInfo();
    }
    
    private void toggleNetworkFields() {
        boolean network = this.networkRadio.isSelected();
        this.backupUserLabel.setVisible(network);
        this.backupUserField.setVisible(network);
        this.backupPasswordLabel.setVisible(network);
        this.backupPasswordField.setVisible(network);
    }

    /**
     * UIをロックする。
     *
     * @param flg (true:ロック, false:ロック解除)
     */
    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            this.anchorPane.setDisable(flg);
            this.progressPane.setVisible(flg);
        });
    }

    /**
     * ウィンドウを取得する。
     *
     * @return
     */
    private Window getWindow() {
        return this.anchorPane.getScene().getWindow();
    }

    /**
     * 閉じるボタン
     *
     * @param event
     */
    @FXML
    private void onCloseButton(ActionEvent event) {
        this.getWindow().hide();
    }

    /**
     * 既定のフォルダ変更ボタン
     *
     * @param event
     */
    @FXML
    private void onBackupDirButton(ActionEvent event) {
        blockUI(true);
        try {
            // フォルダ選択ダイアログ
//            DirectoryChooser dc = new DirectoryChooser();
//            File fol = new File(this.backupDirField.getText());
//            if (fol.exists() && fol.isDirectory()) {
//                dc.setInitialDirectory(fol);
//            }
            String selectedPath = null;
            if (this.localRadio.isSelected()) {
                // フォルダ選択ダイアログ
                DirectoryChooser dc = new DirectoryChooser();
                File fol = new File(this.backupDirField.getText());
                if (fol.exists() && fol.isDirectory()) {
                    dc.setInitialDirectory(fol);
                }
                File selectedFile = dc.showDialog(this.getWindow());
                if (selectedFile != null) {
                    selectedPath = selectedFile.getPath();
                }
            } else {
                TextInputDialog dlg = new TextInputDialog(this.backupDirField.getText());
                dlg.setTitle(LocaleUtils.getString("key.NetworkPath"));
                dlg.setHeaderText(null);
                dlg.setContentText(LocaleUtils.getString("key.NetworkPath"));
                selectedPath = dlg.showAndWait().orElse(null);
            }
//            File selectedFile = dc.showDialog(this.getWindow());
//            if (selectedFile != null) {
//                this.backupDirField.setText(selectedFile.getPath());
//                AdDatabaseConfig.setDBackupDir(selectedFile.getPath());
//            }
            if (selectedPath != null) {
                this.backupDirField.setText(selectedPath);
                if (this.localRadio.isSelected()) {
                    AdDatabaseConfig.setBackupLocalDir(selectedPath);
                    AdDatabaseConfig.setBackupDestType("LOCAL");
                } else {
                    AdDatabaseConfig.setBackupNetworkDir(selectedPath);
                    AdDatabaseConfig.setBackupDestType("NETWORK");
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.dispDatabaseInfo();
            blockUI(false);
        }
    }

    /**
     * バックアップ先がネットワークフォルダの場合は接続を確認し、接続できたパスを返却する。
     * 接続に失敗した場合はメッセージを表示してUIを解除する。
     *
     * @param dir チェックするディレクトリ
     * @param title エラーダイアログのタイトル
     * @return 接続後のディレクトリ。エラー時はnull
     */
    private String prepareBackupDir(String dir, String title) {
        if ("NETWORK".equals(AdDatabaseConfig.getBackupDestType())) {
            Either<String, File> result = NetworkFileUtil.connect(dir,
                    AdDatabaseConfig.getBackupNetworkUser(),
                    AdDatabaseConfig.getBackupNetworkPassword());
            if (result.isLeft()) {
                showAlert(title, result.getLeft(), Alert.AlertType.ERROR);
                blockUI(false);
                return null;
            }
            dir = result.get().getPath();
        }
        return dir;
    }

    /**
     * バックアップボタン
     *
     * @param event
     */
    @FXML
    private void onBackupButton(ActionEvent event) {
        blockUI(true);
        try {
            // 既定のバックアップ先
            String dir = this.backupDirField.getText();
            if (Objects.isNull(dir) || dir.isEmpty()) {
                return;
            }

            dir = prepareBackupDir(this.backupDirField.getText(), LocaleUtils.getString("key.Backup"));
            if (dir == null) {
                return;
            }

            // バックアップダイアログ
            Dialog dlg = new Dialog();
            dlg.initModality(Modality.WINDOW_MODAL);
            dlg.initOwner(this.getWindow());
            dlg.initStyle(StageStyle.UTILITY);
            dlg.setResizable(false);
            dlg.setTitle(LocaleUtils.getString("key.Backup"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addatabaseapp/database_backup_dialog.fxml"), LocaleUtils.getResourceBundle());
            Pane fxml = (Pane) loader.load();

            DatabaseBackupFxController controller = loader.getController();
            controller.setBackupDir(dir);
            controller.setDialog(dlg);

            dlg.getDialogPane().setContent(fxml);
            dlg.showAndWait();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.dispDatabaseInfo();
            blockUI(false);
        }
    }

    /**
     * リストアボタン
     *
     * @param event
     */
    @FXML
    private void onRestoreButton(ActionEvent event) {
        blockUI(true);
        try {
            // 既定のバックアップ先
            String dir = prepareBackupDir(this.backupDirField.getText(), LocaleUtils.getString("key.Backup"));
            if (Objects.isNull(dir) || dir.isEmpty()) {
                return;
            }

            // リストアダイアログ
            Dialog dlg = new Dialog();
            dlg.initModality(Modality.WINDOW_MODAL);
            dlg.initOwner(this.getWindow());
            dlg.initStyle(StageStyle.UTILITY);
            dlg.setResizable(false);
            dlg.setTitle(LocaleUtils.getString("key.Restore"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addatabaseapp/database_restore_dialog.fxml"), LocaleUtils.getResourceBundle());
            Pane fxml = (Pane) loader.load();

            DatabaseRestoreFxController controller = loader.getController();
            controller.setBackupDir(dir);
            controller.setDialog(dlg);

            dlg.getDialogPane().setContent(fxml);
            dlg.showAndWait();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.dispDatabaseInfo();
            blockUI(false);
        }
    }

    /**
     * 実績出力・削除ボタン
     *
     * @param event
     */
    @FXML
    private void onDeleteActualButton(ActionEvent event) {
        blockUI(true);
        try {
            // 既定のバックアップ先
            String dir = prepareBackupDir(this.backupDirField.getText(), LocaleUtils.getString("key.Backup"));
            if (Objects.isNull(dir) || dir.isEmpty()) {
                return;
            }

            // 実績出力・削除ダイアログ
            Dialog dlg = new Dialog();
            dlg.initModality(Modality.WINDOW_MODAL);
            dlg.initOwner(this.getWindow());
            dlg.initStyle(StageStyle.UTILITY);
            dlg.setResizable(false);
            dlg.setTitle(LocaleUtils.getString("key.DeleteActual"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addatabaseapp/delete_actual_dialog.fxml"), LocaleUtils.getResourceBundle());
            Pane fxml = (Pane) loader.load();

            DeleteActualFxController controller = loader.getController();
            controller.setBackupDir(dir);
            controller.setDialog(dlg);

            dlg.getDialogPane().setContent(fxml);
            dlg.showAndWait();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.dispDatabaseInfo();
            blockUI(false);
        }
    }

    /**
     * メンテナンスボタン
     *
     * @param event
     */
    @FXML
    private void onMaintenanceButton(ActionEvent event) {
        Boolean isCancel = true;
        try {
            blockUI(true);

            final String alertCaption = LocaleUtils.getString("key.Maintenance");

            // メンテナンスの開始可否を問い合わせる
            String alertMessage = LocaleUtils.getString("key.Maintenance.Confirmation");
            AlertType alertType = AlertType.CONFIRMATION;
            if (ButtonType.CANCEL.equals(showAlert(alertCaption, alertMessage, alertType))) {
                return;
            }

            // DBバージョンが古い場合、更新可否を問い合わせる
            if (this.postgresManager.isDBVerOld()) {
                alertMessage = LocaleUtils.getString("key.Maintenance.DBUpdateInquiry");
                if (ButtonType.CANCEL.equals(showAlert(alertCaption, alertMessage, alertType))) {
                    return;
                }
            }

            isCancel = false;

            Task task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    try {
                        if (!postgresManager.updateTable()) {
                            return false;
                        }
                        return postgresManager.executeMaintenance();

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // 処理結果
                        Boolean isSuccessed = this.getValue();

                        AlertType alertType;
                        String alertMessage;

                        if (isSuccessed) {
                            alertType = Alert.AlertType.INFORMATION;
                            alertMessage = LocaleUtils.getString("key.Maintenance.MaintenanceComplete");
                        } else {
                            alertType = Alert.AlertType.ERROR;
                            alertMessage = LocaleUtils.getString("key.Maintenance.MaintenanceError");
                        }
                        showAlert(alertCaption, alertMessage, alertType);
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        dispDatabaseInfo();
                        blockUI(false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    showAlert(alertCaption, LocaleUtils.getString("key.Maintenance.MaintenanceError"), Alert.AlertType.ERROR);
                    dispDatabaseInfo();
                    blockUI(false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            if (isCancel) {
                dispDatabaseInfo();
                blockUI(false);
            }
        }
    }

    /**
     * データベース情報を表示する。
     */
    private void dispDatabaseInfo() {
        try {
            // データベース情報を取得する。
            Map<String, String> dbInfos = this.postgresManager.getDBInfo();

            String version = dbInfos.getOrDefault(PGContents.KEY_DB_INFO_VERSION, "-");// データベース情報
            String tableSize = dbInfos.getOrDefault(PGContents.KEY_DB_INFO_TABLE_SIZE, "-");// バージョン
            String deadTuple = dbInfos.getOrDefault(PGContents.KEY_DB_INFO_DEAD_TUPLE, "-");// デッドタプル

            // データベース情報
            this.databaseInfoList.getItems().clear();
            // バージョン
            this.databaseInfoList.getItems().add(String.format("%s: %s", LocaleUtils.getString("key.DatabaseInfo.Version"), version));
            // テーブルサイズ
            this.databaseInfoList.getItems().add(String.format("%s: %s", LocaleUtils.getString("key.DatabaseInfo.TableSize"), tableSize));
            // デッドタプル
            this.databaseInfoList.getItems().add(String.format("%s: %s", LocaleUtils.getString("key.DatabaseInfo.DeadTuple"), deadTuple));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.databaseInfoList.refresh();
        }
    }

    /**
     * メッセージダイアログを表示する。
     *
     * @param title タイトル
     * @param message メッセージ
     * @param type ダイアログ種別
     * @return 押下したボタンの種類
     */
    private ButtonType showAlert(String title, String message, Alert.AlertType type) {
        ButtonType result = null;
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            result = alert.showAndWait().get();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * メンテナンス定期実行計画ボタン
     *
     * @param event
     */
    @FXML
    private void onScheduleButton(ActionEvent event) {
        blockUI(true);
        try {
            // メンテナンス定期実行ダイアログ
            Dialog dlg = new Dialog();
            dlg.initModality(Modality.WINDOW_MODAL);
            dlg.initOwner(this.getWindow());
            dlg.initStyle(StageStyle.UTILITY);
            dlg.setResizable(false);
            dlg.setTitle(LocaleUtils.getString("key.Schedule.DialogCaption"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addatabaseapp/database_regular_mainte_dialog.fxml"), LocaleUtils.getResourceBundle());
            Pane fxml = (Pane) loader.load();

            DatabaseRegularMainteFxController controller = loader.getController();
            controller.setCurrentSetting(this.regularMainteEnabled, this.autoBackupEnabled,
                    this.regularMainteScheduleType, this.regularMainteStartTime, this.regularMainteScheduleDays);
            controller.setDialog(dlg);

            dlg.getDialogPane().setContent(fxml);
            dlg.showAndWait();

            loadScheduleSetting();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 定期バックアップ実行計画ボタン
     *
     * @param event
     */
    @FXML
    private void onBackupScheduleButton(ActionEvent event) {
        blockUI(true);
        try {
            // メンテナンス定期実行ダイアログ
            Dialog dlg = new Dialog();
            dlg.initModality(Modality.WINDOW_MODAL);
            dlg.initOwner(this.getWindow());
            dlg.initStyle(StageStyle.UTILITY);
            dlg.setResizable(false);
            dlg.setTitle(LocaleUtils.getString("key.Schedule.PlanBackupDialogCaption"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addatabaseapp/database_regular_backup_dialog.fxml"), LocaleUtils.getResourceBundle());
            Pane fxml = (Pane) loader.load();

            DatabaseRegularBackupFxController controller = loader.getController();
            controller.setCurrentSetting(this.regularBackupEnabled,
                    this.regularBackupScheduleType, this.regularBackupStartTime, this.regularBackupScheduleDays);
            controller.setDialog(dlg);

            dlg.getDialogPane().setContent(fxml);
            dlg.showAndWait();

            loadBackupScheduleSetting();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }


    /**
     * 現在のスケジュール設定を読み込む
     */
    private void loadScheduleSetting() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd H:mm:ss");

        logger.info("loadCurrentSetting start.");

        File bat = new File("get_schedule_setting.bat");
        try {
            List<String> contents = new ArrayList<>();
            //実行処理追加
            contents.add("@chcp 437");
            contents.add(new StringBuilder("@schtasks /query /tn \"").append(PGContents.ADFACTORY_DB).append(" Regular Maintenance\" /fo csv /v").toString());
            contents.add(CMDContents.ARG_EXIT);

            FileUtils.delete(bat);
            FileUtils.create(bat, contents);

            List<String> results = (List<String>) ExecUtils.exec(bat.getPath(), new String[]{}, ExecUtils.ExeProcessEnum.WAIT_FOR);
            if (!results.get(SCHEDULE_DATA_HEADER_ROW).isEmpty()) {
                String[] currentSetting = results.get(SCHEDULE_DATA_ROW).replace("\",\"", "\n").replace("\"", "").split("\n");

                try {
                    new SimpleDateFormat("yyyy/MM/dd H:mm:ss").parse(currentSetting[SCHEDATA_NEXT_RUN_COL]);
                    this.regularMainteEnabled = true;
                } catch (ParseException ex) {
                    this.regularMainteEnabled = false;
                }

                try {
                    dateFormat.parse(currentSetting[SCHEDATA_LAST_RUN_COL]);
                    this.lastMaintenanceDate = currentSetting[SCHEDATA_LAST_RUN_COL];
                } catch (ParseException ex) {
                    this.lastMaintenanceDate = "";
                }

                this.autoBackupEnabled = currentSetting[SCHEDATA_TASK_TO_RUN_COL].contains("-backup");

                this.regularMainteScheduleType = currentSetting[SCHEDATA_SHCE_TYPE_COL].trim();
                this.regularMainteScheduleDays = currentSetting[SCHEDATA_DAYS_COL];
                this.regularMainteStartTime = currentSetting[SCHEDATA_START_TIME_COL].substring(0, currentSetting[SCHEDATA_START_TIME_COL].length() - 3);
            }

        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(bat);
            logger.info("loadCurrentSetting end.");
        }
    }


    /**
     * 現在のスケジュール設定を読み込む
     */
    private void loadBackupScheduleSetting() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd H:mm:ss");

        logger.info("loadCurrentBackupSetting start.");

        File bat = new File("get_schedule_setting.bat");
        try {
            List<String> contents = new ArrayList<>();
            //実行処理追加
            contents.add("@chcp 437");
            contents.add("@schtasks /query /tn " + DatabaseRegularBackupFxController.ScheduleName + " /fo csv /v");
            contents.add(CMDContents.ARG_EXIT);

            FileUtils.delete(bat);
            FileUtils.create(bat, contents);

            List<String> results = (List<String>) ExecUtils.exec(bat.getPath(), new String[]{}, ExecUtils.ExeProcessEnum.WAIT_FOR);
            if (!results.get(SCHEDULE_DATA_HEADER_ROW).isEmpty()) {
                String[] currentSetting = results.get(SCHEDULE_DATA_ROW).replace("\",\"", "\n").replace("\"", "").split("\n");

                try {
                    new SimpleDateFormat("yyyy/MM/dd H:mm:ss").parse(currentSetting[SCHEDATA_NEXT_RUN_COL]);
                    this.regularBackupEnabled = true;
                } catch (ParseException ex) {
                    this.regularBackupEnabled = false;
                }

                try {
                    dateFormat.parse(currentSetting[SCHEDATA_LAST_RUN_COL]);
                    this.lastBackupDate = currentSetting[SCHEDATA_LAST_RUN_COL];
                } catch (ParseException ex) {
                    this.lastBackupDate = "";
                }

                this.regularBackupScheduleType = currentSetting[SCHEDATA_SHCE_TYPE_COL].trim();
                this.regularBackupScheduleDays = currentSetting[SCHEDATA_DAYS_COL];
                this.regularBackupStartTime = currentSetting[SCHEDATA_START_TIME_COL].substring(0, currentSetting[SCHEDATA_START_TIME_COL].length() - 3);
            }

        } catch (IOException | InterruptedException ex) {
            logger.fatal(ex, ex);
        } finally {
            FileUtils.delete(bat);
            logger.info("loadCurrentSetting end.");
        }
    }

}
