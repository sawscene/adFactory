/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.admanagerlinetimerplugin.component;

import adtekfuji.admanagerapp.admanagerlinetimerplugin.facade.LineTimerFacade;
import adtekfuji.admanagerapp.admanagerlinetimerplugin.facade.LineTimerProperty;
import adtekfuji.admanagerapp.admanagerlinetimerplugin.facade.LineTimerViewInterface;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ReasonInfoEntity;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.utility.MonitorTools;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.validator.LocalTimeValidator;
import jp.adtekfuji.javafxcommon.validator.NumericValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author ke.yokoi
 */
@FxComponent(id = "LintTimerCompo", fxmlPath = "/fxml/compo/line_time_compo.fxml")
public class LineTimerCompoFxController implements Initializable, LineTimerViewInterface, ComponentHandler {

    private static final String START_COUNTDOWN = "startCountdown";

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final AndonMonitorSettingFacade monitorSettingFacade = new AndonMonitorSettingFacade();
    private LineTimerFacade lineTimerFacade = null;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final ObjectProperty<LocalTime> taktTimeProperty = new SimpleObjectProperty(LocalTime.of(0, 0, 0));
    private final LongProperty startCountProperty = new SimpleLongProperty(0L);
    private Timeline timer = null;

    @FXML
    private Label timerLabel;
    @FXML
    private Label lineNameLabel;
    @FXML
    private TextField taktTimeField;
    @FXML
    private RestrictedTextField startCountField;
    @FXML
    private Button selectMonitorButton;
    @FXML
    private Label statusLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Pane progressPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.lineTimerFacade = new LineTimerFacade(this);

        this.blockUI(true);

        //バリデーション設定.
        LocalTimeValidator.bindValidator(this.taktTimeField, this.taktTimeProperty, this.timeFormatter);
        NumericValidator.bindValidator(this.startCountField, this.startCountProperty).addRange(0, Long.MAX_VALUE).setMaxDegit(6);

        this.startCountProperty.set(StringUtils.parseLong(AdProperty.getProperties().getProperty(START_COUNTDOWN, "15")));

        this.startCountField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                AdProperty.getProperties().setProperty(START_COUNTDOWN, startCountField.getText());
            }
        });

        // タイマー
        this.timer = new Timeline(new KeyFrame(Duration.millis(1000), (ActionEvent event) -> {
            this.update();
        }));

        this.timer.setCycleCount(Timeline.INDEFINITE);
        this.timer.stop();

        this.statusLabel.setText("");
        this.blockUI(false);
    }

    private void blockUI(boolean block) {
        Platform.runLater(() -> {
            sc.blockUI("ContentNaviPane", block);
            progressPane.setVisible(block);
        });
    }

    /**
     * 全体を更新する。
     */
    private synchronized void update() {
        boolean isBreak = false;

        if (this.lineTimerFacade.isAutoCountdown()) {
            long now = System.currentTimeMillis();
            isBreak = this.lineTimerFacade.isBreak(new Date(now));
            this.updateCounter(this.lineTimerFacade.getTimeForAuto(now, isBreak));

        } else {
            LocalDateTime now = LocalDateTime.now();
            isBreak = this.lineTimerFacade.isBreak(DateUtils.toDate(now));
            this.updateCounter(this.lineTimerFacade.getTime(now, isBreak));
        }

        if (!isBreak) {
            this.statusLabel.setText("");
            this.pauseButton.setDisable(false);
        } else {
            this.statusLabel.setText(LocaleUtils.getString("key.duringBreaks"));
            this.pauseButton.setDisable(true);
        }
    }

    /**
     * カウンターを更新する。
     *
     * @param time
     */
    private void updateCounter(long time) {
        if (time < 0) {
            this.timerLabel.setStyle("-fx-text-fill: red");
        } else {
            this.timerLabel.setStyle("-fx-text-fill: black");
        }
        this.timerLabel.setText(MonitorTools.formatTaktTime(time * 1000L));
    }

    @FXML
    private void onSelectMonitor(ActionEvent event) {
        ObjectProperty<EquipmentInfoEntity> equipmentProperty = new SimpleObjectProperty<>();
        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.SelectAndonMonitor"), "SelectMonitorCompo", equipmentProperty);
        if (ret == ButtonType.OK && Objects.nonNull(equipmentProperty.get())) {
            EquipmentInfoEntity monitor = equipmentProperty.get();

            // タクトタイムを設定
            AndonMonitorLineProductSetting setting = (AndonMonitorLineProductSetting) this.monitorSettingFacade
                        .getLineSetting(monitor.getEquipmentId(), AndonMonitorLineProductSetting.class);
            this.taktTimeProperty.set(setting.getLineTakt());

            this.selectMonitor(monitor);
        }
    }

    private void selectMonitor(EquipmentInfoEntity monitor) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    lineTimerFacade.selectMonitor(monitor);
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void onStartAction(ActionEvent event) {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    LineTimerProperty property = new LineTimerProperty(startCountProperty.get(), (long) taktTimeProperty.get().toSecondOfDay());
                    lineTimerFacade.onStartAction(property);
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    private void onPauseAction(ActionEvent event) {
        // スタート中なら中断理由選択ダイアログを表示
        ObjectProperty<ReasonInfoEntity> reason = new SimpleObjectProperty<>();
        if (lineTimerFacade.isStarting()) {
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.TimerControlPause"), "InterruptionReasonCompo", reason);
            if (ret != ButtonType.OK) {
                return;
            }
        }

        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    LineTimerProperty property = new LineTimerProperty(startCountProperty.get(), (long) taktTimeProperty.get().toSecondOfDay());
                    lineTimerFacade.onPauseAction(property, reason.getValue());
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @Override
    public void updateInfo(EquipmentInfoEntity monitor, AndonMonitorLineProductSetting setting) {
        Platform.runLater(() -> {
            lineNameLabel.setText(monitor.getEquipmentName() + "[" + setting.getTitle() + "]");
        });
    }

    @Override
    public void setStartWait() {
        Platform.runLater(() -> {
            //taktTimeField.setDisable(false);
            this.startCountField.setDisable(false);
            this.selectMonitorButton.setDisable(false);
            this.startButton.setText(LocaleUtils.getString("key.TimerControlStart"));
            this.startButton.setDisable(false);
            this.pauseButton.setText(LocaleUtils.getString("key.TimerControlPause"));
            this.pauseButton.setDisable(true);
            this.timer.stop();
            this.updateCounter(0L);
        });
    }

    /**
     * カウントダウン開始
     */
    @Override
    public void setStartCount() {
        Platform.runLater(() -> {
            this.taktTimeField.setDisable(true);
            this.startCountField.setDisable(true);
            this.selectMonitorButton.setDisable(true);
            this.startButton.setText(LocaleUtils.getString("key.TimerControlStop"));
            this.startButton.setDisable(false);
            this.pauseButton.setText(LocaleUtils.getString("key.TimerControlPause"));
            this.pauseButton.setDisable(false);
            this.timer.play();
            this.update();
        });
    }

    /**
     * カウントダウン一時停止
     */
    @Override
    public void setStartCountPause() {
        Platform.runLater(() -> {
            this.taktTimeField.setDisable(true);
            this.startCountField.setDisable(true);
            this.selectMonitorButton.setDisable(true);
            this.startButton.setText(LocaleUtils.getString("key.TimerControlStop"));
            this.startButton.setDisable(false);
            this.pauseButton.setText(LocaleUtils.getString("key.TimerControlResume"));
            this.pauseButton.setDisable(false);
            if (!this.lineTimerFacade.isAutoCountdown()) {
                this.timer.pause();
            }
            this.update();
        });
    }

    /**
     * タクトタイムのカウントダウン開始
     */
    @Override
    public void setTaktCount() {
        Platform.runLater(() -> {
            this.taktTimeField.setDisable(true);
            this.startCountField.setDisable(true);
            this.selectMonitorButton.setDisable(true);
            this.startButton.setText(LocaleUtils.getString("key.TimerControlStop"));
            this.startButton.setDisable(false);
            this.pauseButton.setText(LocaleUtils.getString("key.TimerControlPause"));
            this.pauseButton.setDisable(false);
            this.timer.play();
            this.update();
        });
    }

    /**
     * タクトタイムのカウントダウン一時停止
     */
    @Override
    public void setTaktCountPause() {
        Platform.runLater(() -> {
            this.taktTimeField.setDisable(true);
            this.startCountField.setDisable(true);
            this.selectMonitorButton.setDisable(true);
            this.startButton.setText(LocaleUtils.getString("key.TimerControlStop"));
            this.startButton.setDisable(false);
            this.pauseButton.setText(LocaleUtils.getString("key.TimerControlResume"));
            this.pauseButton.setDisable(false);
            if (!this.lineTimerFacade.isAutoCountdown()) {
                this.timer.pause();
            }
            this.update();
        });
    }

    @Override
    public void setStop() {
        Platform.runLater(() -> {
            this.taktTimeField.setDisable(true);
            this.startCountField.setDisable(true);
            this.selectMonitorButton.setDisable(true);
            this.startButton.setText(LocaleUtils.getString("key.TimerControlReset"));
            this.startButton.setDisable(false);
            this.pauseButton.setText(LocaleUtils.getString("key.TimerControlPause"));
            this.pauseButton.setDisable(true);
            if (!this.lineTimerFacade.isAutoCountdown()) {
                this.timer.stop();
            }
            this.statusLabel.setText("");
        });
    }

    /**
     * コンポーネントを破棄する。
     *
     * @return
     */
    @Override
    public boolean destoryComponent() {
        try {
            logger.info("destoryComponent start.");
            this.lineTimerFacade.destroy();
            this.timer.stop();
        } catch (Exception ex) {
        } finally {
            logger.info("destoryComponent end.");
        }
        return true;
    }
}
