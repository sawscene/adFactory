/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitoryielddiffplugin;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.adinterface.command.TimerCommand;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.ProductivityEntity;
import jp.adtekfuji.andon.property.MonitorSettingTP;
import jp.adtekfuji.andon.property.WorkSetting;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 出来高差異フレームのコントローラー
 *
 * @author s-heya
 */
@AndonComponent(title = "出来高差異フレーム")
@FxComponent(id = "YieldDiffCompo", fxmlPath = "/fxml/admonitoryielddiffplugin/YieldDiffCompo.fxml")
public class YieldDiffCompoController implements Initializable, AdAndonComponentInterface {

    enum Status {
        NORMAL,
        NOT_SET,
        NOT_FOUND_WORK
    }
    
    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade monitorFacade = new AndonLineMonitorFacade();

    private Long monitorId;
    private MonitorSettingTP setting;
    private WorkSetting workSetting;
    private Status status = Status.NOT_SET;
    private long taktTime = 0L;
    private long actualNum = 0L;
    private double fontSize = Double.NaN;

    @FXML
    private HBox contentPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label valueLabel;

    /**
     * 出来高差異フレーム"を初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // フォントサイズ
            if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_LARGE)) {
                AdProperty.getProperties().setProperty(Constants.FONT_SIZE_LARGE, Constants.DEF_FONT_SIZE_LARGE);
            }
            this.fontSize = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_LARGE));

            this.monitorId = AndonLoginFacade.getMonitorId();

            this.contentPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                this.draw();
            });

            this.readSetting();
            this.refresh();

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
        if (this.status != Status.NORMAL) {
            return;
        }

        if (msg instanceof ActualNoticeCommand) {
            // 表示対象外の実績通知の場合は無視する。
            ActualNoticeCommand command = (ActualNoticeCommand) msg;
            if (command.getWorkKanbanStatus() != KanbanStatusEnum.COMPLETION) {
                return;
            }

            // モデル名
            if (!StringUtils.like(command.getModelName(), this.setting.getModelName())) {
                return;
            }

            if (Objects.nonNull(this.workSetting) && this.workSetting.getWorkIds().contains(command.getWorkId())) {
                this.actualNum += 1;
            }

            this.updateData();
        } else if (msg instanceof TimerCommand) {
            this.updateData();
        } else if (msg instanceof ResetCommand) {
            this.readSetting();
            this.refresh();
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
     * フレームを更新する。
     */
    private void refresh() {
        if (this.status != Status.NORMAL) {
            this.updateData();
            return;
        }
        
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    List<ProductivityEntity> productivities = monitorFacade.getDailyWorkProductivity(new ArrayList<>(workSetting.getWorkIds()));

                    for (ProductivityEntity productivity : productivities) {
                        if (workSetting.getWorkIds().contains(productivity.getId())) {
                            actualNum = actualNum + productivity.getProdCount();
                        }
                    }

                    updateData();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 情報を更新する。
     */
    private void updateData() {
        
        switch (this.status) {
            case NORMAL:
                // 現在までの作業時間
                LocalDate today = LocalDate.now();
                Date startWorkTime = Date.from(workSetting.getStartWorkTime().atDate(today).atZone(ZoneId.systemDefault()).toInstant());
                long currentTime = BreaktimeUtil.getDiffTime(setting.getBreaktimes(), startWorkTime, new Date());

                // 現在の目標数
                long goal = Math.min(currentTime / taktTime, workSetting.getPlanNum());
                goal = (0 < goal) ? goal : 0;

                // 差異
                long diff = actualNum - goal;
                Platform.runLater(() -> this.valueLabel.setText(String.valueOf(diff)));
                break;

            case NOT_FOUND_WORK:
            case NOT_SET:
            default:
                Platform.runLater(() -> this.valueLabel.setText("未設定"));
                break;
        }

        this.draw();
    }
    
    /**
     * フレームを再描画する。
     */
    private void draw() {
        Platform.runLater(() -> {
            this.titleLabel.setPrefWidth(this.contentPane.getWidth() * 0.4);
            this.valueLabel.setPrefWidth(this.contentPane.getWidth() * 0.6);

            double size = MonitorTools.getFontSize(this.titleLabel.getText(), this.titleLabel.getPrefWidth(), this.contentPane.getHeight(), this.fontSize);
            this.titleLabel.setStyle(String.format("-fx-text-fill: White; -fx-font-size: %fpx;", size));

            size = MonitorTools.getFontSize(this.valueLabel.getText(), this.valueLabel.getPrefWidth(), this.contentPane.getHeight(), this.fontSize);
            this.valueLabel.setStyle(String.format("-fx-text-fill: White; -fx-font-size: %fpx;", size));
        });
    }

    /**
     * 設定を読み込む。
     */
    private void readSetting() {
        this.status = Status.NOT_SET;

        if (0 != this.monitorId) {
            AndonMonitorSettingFacade monitorSettingFacade = new AndonMonitorSettingFacade();
            this.setting = (MonitorSettingTP) monitorSettingFacade.getLineSetting(this.monitorId, MonitorSettingTP.class);

            if (Objects.isNull(this.setting.getYieldDiff())) {
                return;
            }
            
            Optional<WorkSetting> optional = this.setting.getGroupWorkCollection()
                    .stream()
                    .filter(o -> this.setting.getYieldDiff().equals(o.getOrder())).findFirst();
            
            if (!optional.isPresent()) {
                this.status = Status.NOT_FOUND_WORK;
                return;
            }
            
            this.workSetting = optional.get();

            LocalDate today = LocalDate.now();
            ZoneId zoneId = ZoneId.systemDefault();

            Date startWorkTime = Date.from(workSetting.getStartWorkTime().atDate(today).atZone(zoneId).toInstant());
            Date endWorkTime = Date.from(workSetting.getEndWorkTime().atDate(today).atZone(zoneId).toInstant());
            List<BreakTimeInfoEntity> breaktimes = BreaktimeUtil.getAppropriateBreaktimes(this.setting.getBreaktimes(), startWorkTime, endWorkTime);

            // 1日の作業時間
            long breakTime = 0;
            for (BreakTimeInfoEntity breaktime : breaktimes) {
                LocalTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(breaktime.getStarttime().getTime()), zoneId).toLocalTime();
                LocalTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(breaktime.getEndtime().getTime()), zoneId).toLocalTime();
                breakTime += ChronoUnit.MILLIS.between(start, end);
            }
            
            Long workTime = ChronoUnit.MILLIS.between(workSetting.getStartWorkTime(), workSetting.getEndWorkTime()) - breakTime;
            this.taktTime = 0 < this.workSetting.getPlanNum() ? workTime / this.workSetting.getPlanNum() : 0;

            this.status = Status.NORMAL;
        }
    }
}
