package jp.adtekfuji.adandondailyplandeviatedtimeplugin;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.adinterface.command.TimerCommand;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.EstimatedTimeInfoEntity;
import jp.adtekfuji.andon.entity.MonitorPlanDeviatedInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.utility.DelayAction;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "当日実績進捗時間フレーム")
@FxComponent(id = "DailyPlanDeviatedTime", fxmlPath = "/fxml/daily_plan_deviated_time_compo.fxml")
public class MonitorDailyPlanDeviatedTimeCompoFxController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();

    private final AndonLineMonitorFacade lineMonitorFacade = new AndonLineMonitorFacade();
    private final AndonMonitorSettingFacade andonMonitorSettingFacade = new AndonMonitorSettingFacade();

    private final Object lock = new Object();

    private Long monitorId;
    private MonitorPlanDeviatedInfoEntity deviatedInfo = new MonitorPlanDeviatedInfoEntity().fontColor(Color.web(Color.WHITE.toString()).toString()).backColor(Color.web(Color.BLACK.toString()).toString());
    private EstimatedTimeInfoEntity estimatedTimeInfo = null;
    private double fontSize3L = Double.NaN;
    private int progressTime;
    final private DelayAction delayAction = new DelayAction();

    private AndonMonitorLineProductSetting setting = null;
    private final List<Long> targetEquipmentIds = new ArrayList();// 対象設備

    @FXML
    private HBox contentPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label timeLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // フォントサイズ
            if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_3LARGE)) {
                AdProperty.getProperties().setProperty(Constants.FONT_SIZE_3LARGE, Constants.DEF_FONT_SIZE_3LARGE);
            }
            this.fontSize3L = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_3LARGE));

            // 遅れ時間の種類
            if (!AdProperty.getProperties().containsKey("progressTime")) {
                AdProperty.getProperties().setProperty("progressTime", "0");
            }
            this.progressTime = Integer.parseInt(AdProperty.getProperties().getProperty("progressTime"));

            this.monitorId = AndonLoginFacade.getMonitorId();

            this.readSetting();

            this.contentPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                this.draw();
            });

            this.updateData(this.monitorId);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    @Override
    public void updateDisplay(Object msg) {
        if (this.progressTime == 2) {
            if (msg instanceof ResetCommand
                || msg instanceof ActualNoticeCommand
                || msg instanceof TimerCommand) {
                this.updateData(this.monitorId);
            }
            return;
        }
        
        if (msg instanceof ActualNoticeCommand) {
            // 表示対象外の実績通知の場合は無視する。
            ActualNoticeCommand command = (ActualNoticeCommand) msg;
            if (Objects.nonNull(this.setting)) {
                switch (setting.getCompCountType()) {
                    case EQUIPMENT:// 対象設備を巡回した数をカウント
                        if (!KanbanStatusEnum.COMPLETION.equals(command.getWorkKanbanStatus())
                                || !this.targetEquipmentIds.contains(command.getEquipmentId())
                                || !command.isCompletion()) {
                            logger.info("not target: compCountType={}, workKanbanStatus={}, equipmentId={}, isCompletion={}",
                                    setting.getCompCountType(), command.getWorkKanbanStatus(), command.getEquipmentId(), command.isCompletion());
                            return;
                        }
                        break;
                    case WORK:// 対象工程を巡回した数をカウント
                        if (!KanbanStatusEnum.COMPLETION.equals(command.getWorkKanbanStatus())
                                || !command.isCompletion()) {
                            logger.info("not target: compCountType={}, workKanbanStatus={}, isCompletion={}",
                                    setting.getCompCountType(), command.getWorkKanbanStatus(), command.isCompletion());
                            return;
                        }
                        break;
                    case KANBAN:// 完了したカンバン数をカウント
                    default:
                        if (!KanbanStatusEnum.COMPLETION.equals(command.getKanbanStatus())
                                || !this.targetEquipmentIds.contains(command.getEquipmentId())) {
                            logger.info("not target: compCountType={}, kanbanStatus={}, equipmentId={}", setting.getCompCountType(), command.getKanbanStatus(), command.getEquipmentId());
                            return;
                        }
                        break;
                }

                // モデル名
                if (!StringUtils.like(command.getModelName(), this.setting.getModelName())) {
                    logger.info("not target modelName:{}", command.getModelName());
                    return;
                }
            }

            if (command.getEquipmentStatus() != KanbanStatusEnum.WORKING) {
                delayAction.run(() -> {
                    this.updateData(command.getMonitorId());
                });
            }
        } else if (msg instanceof TimerCommand) {
            this.updateData(this.monitorId);
        } else if (msg instanceof ResetCommand) {
            this.readSetting();
            this.updateData(this.monitorId);
        }
    }

    @Override
    public void exitComponent() {
        logger.info("exitComponent");
        try {
            this.delayAction.cancel();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 情報を更新する。
     */
    private void updateData(Long monitorId) {
        logger.info("updateData: monitorId={}", monitorId);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (lock) {
                        if (progressTime != 2) {
                            deviatedInfo = lineMonitorFacade.getDailyDeviatedInfo(monitorId);
                        } else {
                            estimatedTimeInfo= lineMonitorFacade.getEstimatedTime(monitorId);
                        }
                    }
                    draw();
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
                this.titleLabel.setPrefWidth(this.contentPane.getWidth() * 0.3);
                this.timeLabel.setPrefWidth(this.contentPane.getWidth() * 0.7);

                final Font font = Font.font("Meiryo UI", this.fontSize3L);

                int value = 0;
                String sign = "";
                String foreColor = Color.WHITE.toString();
                String backColor = Color.BLACK.toString();
                String timeFormat = "HH:mm";

                switch (this.progressTime) {
                case 1:
                    if (Objects.nonNull(this.deviatedInfo)) {
                        timeFormat = this.deviatedInfo.getTimeFormat();
                        if (Objects.nonNull(this.deviatedInfo.getWorkResult())) {
                            value = (int) Math.abs(this.deviatedInfo.getWorkResult());

                            if (this.deviatedInfo.getWorkResult() < 0) {
                                sign = "-";
                                foreColor = Color.BLACK.toString();
                                backColor = Color.RED.toString();
                            } else {
                                sign = "+";
                            }
                        }
                    }
                    break;
                case 2:
                    if (Objects.nonNull(this.estimatedTimeInfo) && Objects.nonNull(this.estimatedTimeInfo.getEstimatedTime())) {
                        long diff = this.estimatedTimeInfo.getScheduledTime().getTime() - this.estimatedTimeInfo.getEstimatedTime().getTime();
                        value = (int) Math.abs(diff) / 1000;
                        if (diff < 0) {
                            sign = "-";
                            foreColor = Color.BLACK.toString();
                            backColor = Color.RED.toString();
                        } else {
                            sign = "+";
                        }
                    }
                    if (0 == this.estimatedTimeInfo.getRemaining()) {
                        // 作業終了と見なし、更新は行わない
                        return;
                    }
                    break;

                default:
                    if (Objects.nonNull(this.deviatedInfo)) {
                        value = (int) Math.abs(this.deviatedInfo.getPlanDeviatedTime());
                        sign = (this.deviatedInfo.getPlanDeviatedTime() < 0) ? "-" : "+";
                        backColor = this.deviatedInfo.getBackColor();
                        foreColor = this.deviatedInfo.getFontColor();
                        timeFormat = this.deviatedInfo.getTimeFormat();
                    }
                    break;
                }

                Text helper;
                double fontSize;

                helper = new Text(this.titleLabel.getText());
                helper.setFont(font);
                fontSize = (this.titleLabel.getPrefWidth() >= helper.getBoundsInLocal().getWidth()) ? this.fontSize3L : this.fontSize3L * (this.titleLabel.getPrefWidth() / helper.getBoundsInLocal().getWidth() * 0.9);

                helper.setFont(Font.font("Meiryo UI", fontSize));
                fontSize = this.contentPane.getHeight() >= helper.getBoundsInLocal().getHeight() ? fontSize : fontSize * (this.contentPane.getHeight()/helper.getBoundsInLocal().getHeight() * 0.99);
                this.titleLabel.setStyle(String.format("-fx-font-size:%fpx; -fx-text-fill:%s;", fontSize, this.toRGB(foreColor)));

                LocalTime time = LocalTime.of(value / 3600, value % 3600 / 60);
                //LocalTime time = LocalTime.of(value / 3600, value % 3600 / 60, value % 3600 % 60);
                String text = sign + time.format(DateTimeFormatter.ofPattern(timeFormat));

                this.timeLabel.setText(text);
                helper = new Text(text);
                helper.setFont(font);
                fontSize = (this.timeLabel.getPrefWidth() >= helper.getBoundsInLocal().getWidth()) ? this.fontSize3L : this.fontSize3L * (this.timeLabel.getPrefWidth() / helper.getBoundsInLocal().getWidth() * 0.9);

                helper.setFont(Font.font("Meiryo UI", fontSize));
                fontSize = this.contentPane.getHeight() >= helper.getBoundsInLocal().getHeight() ? fontSize : fontSize * (this.contentPane.getHeight()/helper.getBoundsInLocal().getHeight() * 0.99);
                this.timeLabel.setStyle(String.format("-fx-font-size:%fpx; -fx-text-fill:%s;", fontSize, this.toRGB(foreColor)));

                this.contentPane.setStyle(String.format("-fx-background-color:%s;", this.toRGB(backColor)));
            }
        });
    }

    /**
     * カラーをRGBに変換する。
     *
     * @param color
     * @return
     */
    private String toRGB(String color) {
        Color c = Color.valueOf(color);
        return String.format("#%02X%02X%02X", (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
    }

    /**
     * 進捗モニタ設定を取得する。
     */
    private void readSetting() {
        logger.info("readSetting: monitorId={}", this.monitorId);
        try {
            if (!this.monitorId.equals(0L)) {
                this.setting = (AndonMonitorLineProductSetting) this.andonMonitorSettingFacade
                        .getLineSetting(this.monitorId, AndonMonitorLineProductSetting.class);
            }

            if (Objects.nonNull(this.setting) && Objects.nonNull(this.setting.getLineId())) {
                // ラインID
                Long lineId = this.setting.getLineId();

                // 対象設備
                this.targetEquipmentIds.clear();

                List<Long> ids;
                switch (setting.getCompCountType()) {
                    case EQUIPMENT:// 対象設備を巡回した数をカウント
                        ids = MonitorTools.getWorkEquipmentIds(this.setting.getWorkEquipmentCollection());
                        break;
                    case WORK:// 対象工程を巡回した数をカウント
                        ids = new ArrayList();
                        break;
                    case KANBAN:// 完了したカンバン数をカウント
                    default:
                        ids = MonitorTools.getLineEquipmentIds(lineId);
                        break;
                }

                if (!ids.isEmpty()) {
                    this.targetEquipmentIds.addAll(ids);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
