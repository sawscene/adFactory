/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorestimatedtimeplugin;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.LineTimerNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.adinterface.command.TimerCommand;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LineManagedCommandEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.EstimatedTimeInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業終了予想時間フレームのコントローラー
 *
 * @author s-heya
 */
@AndonComponent(title = "作業終了予想時間フレーム")
@FxComponent(id = "EstimatedTimeCompo", fxmlPath = "/fxml/admonitorestimatedtimeplugin/EstimatedTimeCompo.fxml")
public class EstimatedTimeCompoController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade monitorFacade = new AndonLineMonitorFacade();
    private final FastDateFormat formatter = FastDateFormat.getInstance("HH:mm");

    private Long monitorId;
    private AndonMonitorLineProductSetting setting;
    private EstimatedTimeInfoEntity estimatedTimeInfo = null;
    private double fontSize3L = Double.NaN;
    private final Object lock = new Object();

    @FXML
    private HBox contentPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label timeLabel;

    /**
     * 作業終了予想時間フレームを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // フォントサイズ
            if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_3LARGE)) {
                AdProperty.getProperties().setProperty(Constants.FONT_SIZE_3LARGE, Constants.DEF_FONT_SIZE_3LARGE);
            }
            this.fontSize3L = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_3LARGE));

            this.monitorId = AndonLoginFacade.getMonitorId();

            if (0 != this.monitorId) {
                final AndonMonitorSettingFacade monitorSettingFacade = new AndonMonitorSettingFacade();
                this.setting = (AndonMonitorLineProductSetting) monitorSettingFacade.getLineSetting(this.monitorId, AndonMonitorLineProductSetting.class);
            }

            this.contentPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                this.draw();
            });

            this.updateData();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 表示を更新する。
     *
     * @param msg
     */
    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof ResetCommand) {
            final AndonMonitorSettingFacade monitorSettingFacade = new AndonMonitorSettingFacade();
            this.setting = (AndonMonitorLineProductSetting) monitorSettingFacade.getLineSetting(this.monitorId, AndonMonitorLineProductSetting.class);
            this.updateData();

        } else if (msg instanceof ActualNoticeCommand ||  msg instanceof TimerCommand) {
            synchronized (lock) {
                if (Objects.nonNull(this.estimatedTimeInfo)) {
                    Platform.runLater(() -> {
                        if (0 < this.estimatedTimeInfo.getRemaining()) {
                            Date now = new Date();
                            Date endTime = new Date(now.getTime() + (setting.getLineTakt().toSecondOfDay() * this.estimatedTimeInfo.getRemaining() * 1000L));
                            List<BreakTimeInfoEntity> breakTimes = BreaktimeUtil.getAppropriateBreaktimes(setting.getBreaktimes(), now, endTime);
                            Date estimatedTime = BreaktimeUtil.getEndTimeWithBreak(breakTimes, now, endTime);
                            timeLabel.setText(formatter.format(estimatedTime));
                        } else {
                            timeLabel.setText("作業終了");
                        }
                    });
                }
            }

        } else if (msg instanceof LineTimerNoticeCommand) {
            //LineTimerNoticeCommand lineTimer = (LineTimerNoticeCommand) msg;
            //if (lineTimer.getCommand() == LineManagedCommandEnum.STOP) {
                this.updateData();
            //}
        }
    }

    /**
     * フレームを終了する。
     */
    @Override
    public void exitComponent() {
        logger.info("exitComponent");
    }

    /**
     * 情報を更新する。
     */
    private void updateData() {
         Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (lock) {
                        if (0 == monitorId) {
                            return null;
                        }

                        estimatedTimeInfo= monitorFacade.getEstimatedTime(monitorId);
                        if (Objects.nonNull(estimatedTimeInfo)) {
                            Platform.runLater(() -> {
                                if (0 < estimatedTimeInfo.getRemaining()) {
                                    timeLabel.setText(formatter.format(estimatedTimeInfo.getEstimatedTime()));
                                } else {
                                    timeLabel.setText("作業終了");
                                }
                                draw();
                            });
                        }
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * フレームを描画する。
     */
    private void draw() {
        Platform.runLater(() -> {
            synchronized (this.lock) {
                this.titleLabel.setPrefWidth(this.contentPane.getWidth() * 0.6);
                this.timeLabel.setPrefWidth(this.contentPane.getWidth() * 0.4);

                Text helper;
                double width;
                double fontSize;

                helper = new Text(this.titleLabel.getText());
                helper.setFont(Font.font("Meiryo UI", this.fontSize3L));
                width = this.titleLabel.getPrefWidth();
                fontSize = (width >= helper.getBoundsInLocal().getWidth()) ? this.fontSize3L : this.fontSize3L * (width / helper.getBoundsInLocal().getWidth() * 0.95);

                helper.setFont(Font.font("Meiryo UI", fontSize));
                fontSize = this.contentPane.getHeight() >= helper.getBoundsInLocal().getHeight() ? fontSize : this.contentPane.getHeight() * 0.99;
                this.titleLabel.setStyle(String.format("-fx-text-fill: White; -fx-font-size: %fpx;", fontSize));

                helper = new Text(this.timeLabel.getText());
                helper.setFont(Font.font("Meiryo UI", this.fontSize3L));
                width = this.timeLabel.getPrefWidth();
                fontSize = (width >= helper.getBoundsInLocal().getWidth()) ? this.fontSize3L : this.fontSize3L * (width / helper.getBoundsInLocal().getWidth() * 0.95);

                helper.setFont(Font.font("Meiryo UI", fontSize));
                fontSize = this.contentPane.getHeight() >= helper.getBoundsInLocal().getHeight() ? fontSize : this.contentPane.getHeight() * 0.99;
                this.timeLabel.setStyle(String.format("-fx-text-fill: White; -fx-font-size: %fpx;", fontSize));
            }
        });
    }
}
