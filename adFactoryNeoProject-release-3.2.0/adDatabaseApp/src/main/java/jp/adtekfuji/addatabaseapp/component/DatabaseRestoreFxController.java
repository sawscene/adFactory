/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.component;

import adtekfuji.utility.DateUtils;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import jakarta.xml.bind.annotation.XmlElement;
import jp.adtekfuji.addatabase.utils.LocaleUtils;
import jp.adtekfuji.addatabaseapp.controller.PostgresManager;
import jp.adtekfuji.addatabaseapp.postgres.PGContents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * リストアダイアログ
 *
 * @author s-maeda
 */
public class DatabaseRestoreFxController implements Initializable {

    private final static Logger logger = LogManager.getLogger();

    @FXML
    private Pane progressPane;
    @FXML
    private TableView<BackupHistoryInfo> tableView;
    @FXML
    private TableColumn<BackupHistoryInfo, Number> numberColumn;
    @FXML
    private TableColumn<BackupHistoryInfo, String> datetimeColumn;
    @FXML
    private Button RestoreButton;
    @FXML
    private Label RestoreLabel;

    private Dialog dialog;
    private String backupDir;

    private final ObservableList<BackupHistoryInfo> rows = FXCollections.observableArrayList();

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);

        tableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        this.numberColumn.setCellValueFactory((CellDataFeatures<BackupHistoryInfo, Number> param) -> param.getValue().numberProperty());
        this.datetimeColumn.setCellValueFactory((CellDataFeatures<BackupHistoryInfo, String> param) -> param.getValue().datetimeProperty());

        this.rows.clear();
        this.tableView.setItems(this.rows);

        StringBuilder sb = new StringBuilder();
        sb.append(LocaleUtils.getString("key.Restore.RestoreData"));
        sb.append("\r\r");
        sb.append(LocaleUtils.getString("key.Restore.Attention"));
        this.RestoreLabel.setText(sb.toString());

        this.RestoreButton.setDisable(true);
        this.tableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() < 0) {
                this.RestoreButton.setDisable(true);
            } else {
                this.RestoreButton.setDisable(false);
            }
        });
    }

    /**
     *
     * @param backupDir
     */
    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
        this.dispBackupHistory();
    }

    /**
     *
     * @param dialog
     */
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.closeDialog();
        });
    }

    /**
     * UIをロックする。
     *
     * @param flg (true:ロック, false:ロック解除)
     */
    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            this.dialog.getDialogPane().setDisable(flg);
            this.progressPane.setVisible(flg);
        });
    }

    /**
     * 実行ボタン
     *
     * @param event
     */
    @FXML
    private void onRestoreButton(ActionEvent event) {
        Boolean isCancel = true;
        try {
            blockUI(true);

            final String alertCaption = LocaleUtils.getString("key.Restore");
            String alertMessage;
            AlertType alertType;

            // リストアにあたっての注意表示
            alertMessage = LocaleUtils.getString("key.Restore.Attention");
            alertType = AlertType.CONFIRMATION;
            if (ButtonType.CANCEL.equals(showAlert(alertCaption, alertMessage, alertType))) {
                return;
            }

            // リストアの最終確認
            String backupedDate = tableView.getSelectionModel().getSelectedItem().datetimeProperty().get();
            alertMessage = LocaleUtils.getString("key.Restore.FinalConfirm") + "\r" + backupedDate;
            alertType = AlertType.CONFIRMATION;
            if (ButtonType.CANCEL.equals(showAlert(alertCaption, alertMessage, alertType))) {
                return;
            }

            // バックアップファイル
            String filePath = tableView.getSelectionModel().getSelectedItem().getFilePath();

            isCancel = false;

            Task task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    Boolean result = false;
                    try {
                        PostgresManager postgresManager = new PostgresManager();
                        Boolean isRestore = postgresManager.restoreData(filePath);
                        if (isRestore) {
                            result = postgresManager.updateTable();
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                    return result;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // 処理結果
                        Boolean isRestored = this.getValue();

                        Alert.AlertType alertType;
                        String message;
                        if (isRestored) {
                            alertType = Alert.AlertType.INFORMATION;
                            message = LocaleUtils.getString("key.Restore.RestoreSuccess");
                        } else {
                            alertType = Alert.AlertType.ERROR;
                            message = LocaleUtils.getString("key.Restore.RestoreFailure");
                        }
                        showAlert(alertCaption, message, alertType);
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    showAlert(alertCaption, LocaleUtils.getString("key.Restore.RestoreFailure"), Alert.AlertType.ERROR);
                    blockUI(false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }
    }

    /**
     * キャンセルボタン
     *
     * @param event
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
        this.closeDialog();
    }

    /**
     * バックアップ履歴を表示する。
     */
    private void dispBackupHistory() {
        try {
            this.rows.clear();

            File fol = new File(this.backupDir);
            if (Objects.isNull(fol.listFiles())) {
                return;
            }

            // ファイル名で対象を絞り込む。
            List<File> files = Arrays.asList(fol.listFiles()).stream().filter(p -> p.isFile()
                    && p.length() > 0
                    && p.getName().matches(PGContents.DB_BACKUP_FILE_FORMAT)
            ).collect(Collectors.toList());
            if (files.isEmpty()) {
                return;
            }

            // ファイル名順にソートする。
            files.sort(Comparator.comparing(p -> p.getName()));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

            long number = 0;
            for (File file : files) {
                String datetime = null;
                try {
                    Date dt = DateUtils.toDate(LocalDateTime.parse(file.getName()
                            .substring(PGContents.ADFACTORY_DB.length() + 1, PGContents.ADFACTORY_DB.length() + 16), formatter));
                    datetime = datetimeFormatter.format(DateUtils.toLocalDateTime(dt));
                } catch (Exception ex) {
                }

                if (Objects.nonNull(datetime)) {
                    number++;
                    this.rows.add(new BackupHistoryInfo(number, datetime, file.getPath()));
                }
            }

            if (this.rows.size() > 0) {
                BackupHistoryInfo lastRow = this.rows.get(this.rows.size() - 1);
                this.tableView.sort();
                this.tableView.scrollTo(lastRow);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
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
     * 終了処理
     */
    private void closeDialog() {
        try {
            this.dialog.getDialogPane().getScene().getWindow().hide();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * バックアップ履歴情報
     */
    private class BackupHistoryInfo {

        @XmlElement()
        private Long number;
        @XmlElement()
        private String datetime;

        private LongProperty numberProperty;
        private StringProperty datetimeProperty;

        private String filePath;

        public BackupHistoryInfo() {
        }

        public BackupHistoryInfo(Long number, String datetime, String filePath) {
            this.number = number;
            this.datetime = datetime;
            this.filePath = filePath;
        }

        public LongProperty numberProperty() {
            if (Objects.isNull(this.numberProperty)) {
                this.numberProperty = new SimpleLongProperty(number);
            }
            return this.numberProperty;
        }

        public StringProperty datetimeProperty() {
            if (Objects.isNull(this.datetimeProperty)) {
                this.datetimeProperty = new SimpleStringProperty(datetime);
            }
            return this.datetimeProperty;
        }

        public String getFilePath() {
            return this.filePath;
        }
    };
}
