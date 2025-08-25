/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.component;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import jp.adtekfuji.addatabase.utils.LocaleUtils;
import jp.adtekfuji.addatabaseapp.postgres.PGContents;
import jp.adtekfuji.addatabaseapp.utils.ExecUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 定期メンテナンス計画ダイアログ
 *
 * @author s-maeda
 */
public class DatabaseRegularMainteFxController implements Initializable {

    private final static Logger LOGGER = LogManager.getLogger();

    @FXML
    private Pane progressPane;
    @FXML
    private CheckBox enabledCheck;
    @FXML
    private CheckBox autoBackupCheck;
    @FXML
    private ComboBox<String> scheduleTypeCombo;
    @FXML
    private TextField startTimeText;
    @FXML
    private Pane dateSelectPane;
    @FXML
    private VBox triggerSettings;
    @FXML
    private Button okButton;

    private Dialog dialog;

    private Boolean enabled = false;
    private Boolean autoBackup = false;
    private String scheduleType = "Daily";
    private String startTime = "";
    private String daysOfWeek = "";
    private String dayOfMonth = "";

    private final LinkedHashMap<String, String> scheduleTypes = new LinkedHashMap<>();

    private DatabaseMainteDayInfoPaneFxController regularMainteDayInfoPane;

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);

        initEnableCheckbox();
        initScheduleTypeCombo();
    }

    /**
     * 有効チェックボックスの初期化
     */
    private void initEnableCheckbox() {
        LOGGER.info("initEnableCheckbox : start");

        try {
            // 定期実行無効時は他のコントロールを無効にする
            this.enabledCheck.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (!Objects.equals(newValue, oldValue)) {
                    this.enabled = newValue;

                    this.autoBackupCheck.setDisable(!newValue);
                    this.triggerSettings.setDisable(!newValue);
                }
            });

            this.enabledCheck.setSelected(enabled);

            this.autoBackupCheck.setDisable(!enabled);
            this.triggerSettings.setDisable(!enabled);

        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info("initEnableCheckbox : end");
        }
    }

    /**
     * スケジュール実行タイプ選択コンボボックスの初期化
     */
    private void initScheduleTypeCombo() {
        LOGGER.info("initScheduleTypeCombo : start");

        try {
            this.scheduleTypes.put(LocaleUtils.getString("key.Schedule.Daily"), "Daily");
            this.scheduleTypes.put(LocaleUtils.getString("key.Schedule.Weekly"), "Weekly");
            this.scheduleTypes.put(LocaleUtils.getString("key.Schedule.Monthly"), "Monthly");
            this.scheduleTypeCombo.getItems().addAll(this.scheduleTypes.keySet());

            this.scheduleTypeCombo.getSelectionModel().select(
                    this.scheduleTypes.entrySet().stream().filter(p -> p.getValue().equals(this.scheduleType)).findFirst().get().getKey());
            setDateSelectPane(this.scheduleType);

            // タイプ選択切替時、日選択ペインの内容を切り替える
            scheduleTypeCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.equals(oldValue)) {
                    if (this.scheduleTypes.get(oldValue).equals("Weekly")) {
                        this.daysOfWeek = this.regularMainteDayInfoPane.get();
                    } else if (this.scheduleTypes.get(oldValue).equals("Monthly")) {
                        this.dayOfMonth = this.regularMainteDayInfoPane.get();
                    }
                    this.scheduleType = this.scheduleTypes.get(newValue);
                    this.dateSelectPane.getChildren().clear();
                    this.okButton.setDisable(false);
                    setDateSelectPane(this.scheduleType);
                }
            });

        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info("initScheduleTypeCombo : end");
        }
    }

    /**
     * 日選択ペインの切り替え
     *
     * @param selectedScheduleItem
     */
    private void setDateSelectPane(String selectedScheduleType) {
        LOGGER.info("setDateSelectPane : start");

        try {
            FXMLLoader loader;
            switch (selectedScheduleType) {
                case "Weekly":
                    // 「毎週」選択時
                    loader = new FXMLLoader(getClass().getResource("/fxml/addatabaseapp/database_mainte_day_of_week.fxml"), LocaleUtils.getResourceBundle());
                    loader.setRoot(this.dateSelectPane);
                    loader.load();
                    DatabaseMainteDayOfWeekFxContoroller dayOfWeekCtrl = loader.getController();
                    dayOfWeekCtrl.setDaysOfWeek(this.daysOfWeek);
                    this.regularMainteDayInfoPane = dayOfWeekCtrl;
                    // 一つも曜日を選択していない場合、OKボタンを無効にする
                    this.okButton.setDisable(!dayOfWeekCtrl.isSelctionExist());
                    dayOfWeekCtrl.getSelectExistProperty().addListener((observal, oldValue, NewValue) -> {
                        if (!NewValue.equals(oldValue)) {
                            this.okButton.setDisable(!dayOfWeekCtrl.isSelctionExist());
                        }
                    });
                    break;

                case "Monthly":
                    // 「毎月」選択時
                    loader = new FXMLLoader(getClass().getResource("/fxml/addatabaseapp/database_mainte_day_of_month.fxml"), LocaleUtils.getResourceBundle());
                    loader.setRoot(this.dateSelectPane);
                    loader.load();
                    DatabaseMainteDayOfMonthFxContoroller dayOfMonthCtrl = loader.getController();
                    dayOfMonthCtrl.setDayOfMonth(this.dayOfMonth);
                    this.regularMainteDayInfoPane = dayOfMonthCtrl;
                    break;

                default:
                    // その他(「毎日」選択時含む)
                    this.regularMainteDayInfoPane = null;
                    break;

            }

        } catch (Exception ex) {
            DatabaseRegularMainteFxController.LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info("setDateSelectPane : end");
        }
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
     * OKボタン
     *
     * @param event
     */
    @FXML
    private void onOKButton(ActionEvent event) {
        try {
            blockUI(true);

            if (this.enabledCheck.selectedProperty().get()) {
                scheduleRegularMainte();
            } else {
                setDisableSchedule();
            }
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            blockUI(false);
            this.closeDialog();
        }
    }

    /**
     * 定期実行を無効化する
     */
    private void setDisableSchedule() {
        LOGGER.info("setDisableSchedule start.");

        try {
            List<String> argList = new ArrayList<>();

            argList.add("/change");

            argList.add("/tn");
            argList.add(new StringBuilder("\"").append(PGContents.ADFACTORY_DB).append(" Regular Maintenance\"").toString());

            argList.add("/disable");

            ExecUtils.exec("schtasks", argList.toArray(new String[argList.size()]), ExecUtils.ExeProcessEnum.WAIT_FOR);

        } catch (IOException | InterruptedException ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info("setDisableSchedule end.");
        }
    }

    /**
     * 定期実行をスケジュールする
     */
    private void scheduleRegularMainte() {
        LOGGER.info("scheduleRegularMainte start.");

        try {
            List<String> argList = new ArrayList<>();

            argList.add("/create");
            argList.add("/tn");
            argList.add(new StringBuilder("\"").append(PGContents.ADFACTORY_DB).append(" Regular Maintenance\"").toString());

            argList.add("/rl");
            argList.add("highest"); // 最上位の特権で実行する

            argList.add("/ru");
            argList.add("System");  // SYSTEMユーザーで実行する

            argList.add("/st");
            argList.add(this.startTimeText.getText());

            String backupOpt = this.autoBackupCheck.selectedProperty().get() ? " -backup" : "";
            argList.add("/tr");
            argList.add("\"%ADFACTORY_HOME%\\bin\\adDatabaseApp.exe -update -maintenance" + backupOpt + "\"");

            argList.add("/sc");
            argList.add(this.scheduleType);

            if (this.scheduleType.equals("Monthly") && this.regularMainteDayInfoPane.get().equals("32")) {
                argList.add("/mo");
                argList.add("LASTDAY");

                argList.add("/m");
                argList.add("*");

            } else if (this.scheduleType.equals("Monthly") || this.scheduleType.equals("Weekly")) {
                argList.add("/d");
                argList.add(this.regularMainteDayInfoPane.get());

            }

            argList.add("/f"); // タスクを上書きする

            ExecUtils.exec("schtasks", argList.toArray(new String[argList.size()]), ExecUtils.ExeProcessEnum.WAIT_FOR);

        } catch (IOException | InterruptedException ex) {
            LOGGER.fatal(ex, ex);
        } finally {
            LOGGER.info("scheduleRegularMainte end.");
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
     * メッセージダイアログを表示する。
     *
     * @param title タイトル
     * @param message メッセージ
     * @param type ダイアログ種別
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        }
    }

    /**
     * 終了処理
     */
    private void closeDialog() {
        try {
            this.dialog.getDialogPane().getScene().getWindow().hide();
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        }
    }

    public void setCurrentSetting(Boolean enabled, Boolean autoBackup, String scheduleType, String startTime, String scheduleDays) {
        try {
            this.enabled = enabled;
            this.autoBackup = autoBackup;
            this.scheduleType = (!scheduleType.isEmpty()) ? scheduleType : "Daily";
            Date startTimeTmp = (!startTime.isEmpty()) ? new SimpleDateFormat("H:mm").parse(startTime) : new Date();
            this.startTime = new SimpleDateFormat("HH:mm").format(startTimeTmp);

            this.daysOfWeek = "";
            this.dayOfMonth = "";
            if (this.scheduleType.equals("Weekly")) {
                this.daysOfWeek = scheduleDays;
            } else if (this.scheduleType.equals("Monthly")) {
                this.dayOfMonth = scheduleDays;
            }

            this.enabledCheck.setSelected(this.enabled);
            this.autoBackupCheck.setSelected(this.autoBackup);
            this.scheduleTypeCombo.getSelectionModel().select(
                    this.scheduleTypes.entrySet().stream().filter(p -> p.getValue().equals(this.scheduleType)).findFirst().get().getKey());
            this.startTimeText.setText(this.startTime);
        } catch (Exception ex) {
            LOGGER.fatal(ex, ex);
        }
    }
}
