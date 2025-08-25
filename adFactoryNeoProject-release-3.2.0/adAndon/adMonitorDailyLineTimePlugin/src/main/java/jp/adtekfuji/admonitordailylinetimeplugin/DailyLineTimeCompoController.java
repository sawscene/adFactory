package jp.adtekfuji.admonitordailylinetimeplugin;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.MonitorPlanNumInfoEntity;
import jp.adtekfuji.andon.property.MonitorSettingELS;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "当日実績進捗時間フレーム2")
@FxComponent(id = "DailyLineTime", fxmlPath = "/fxml/admonitordailylinetimeplugin/DailyLineTimeCompo.fxml")
public class DailyLineTimeCompoController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private Long monitorId;
    private MonitorSettingELS setting;
    private MonitorPlanNumInfoEntity planNumInfo;
    private double fontSize3L = Double.NaN;

    @FXML
    private HBox contentPane;
    @FXML
    private Label deviatedLabel;
    @FXML
    private Label deviatedNumLabel;

    /**
     * 当日実績進捗時間フレームを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // フォントサイズ
        if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_3LARGE)) {
            AdProperty.getProperties().setProperty(Constants.FONT_SIZE_3LARGE, Constants.DEF_FONT_SIZE_3LARGE);
        }
        this.fontSize3L = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_3LARGE));

        // モニターID
        this.monitorId = AndonLoginFacade.getMonitorId();
        if (0 != this.monitorId) {
            this.setting = (MonitorSettingELS) new AndonMonitorSettingFacade().getLineSetting(monitorId, MonitorSettingELS.class);
        }

        this.contentPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.draw();
        });
        
        this.refresh();
    }

   /**
     * 表示を更新する。
     *
     * @param msg
     */
    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof ActualNoticeCommand) {
            ActualNoticeCommand command = (ActualNoticeCommand) msg;

            // TODO: 進捗モニタ設定(ELS)が「当日実績数のカウント方法」に対応したら、
            //      各カウント方法毎にチェック対象を変更する対応を行なうこと。

            if (command.getKanbanStatus() == KanbanStatusEnum.COMPLETION) {
                this.refresh();
            }
        } else if (msg instanceof TimerCommand) {
            this.draw();
        } else if (msg instanceof ResetCommand) {
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
     * データを更新する。
     */
    private void refresh() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (DailyLineTimeCompoController.this) {
                        planNumInfo = andonLineMonitorFacade.getDailyPlanInfo(monitorId);
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                draw();
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * データを表示する。
     */
    private void draw() {
        Platform.runLater(() -> {
            synchronized (DailyLineTimeCompoController.this) {
                if (Objects.isNull(this.planNumInfo)) {
                    return;
                }

                Color fontColor = Color.WHITE;
                Color backColor = Color.BLACK;

                // 現在までの作業時間
                Instant instant = this.setting.getStartWorkTime().atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
                long currentTime = BreaktimeUtil.getDiffTime(this.setting.getBreaktimes(), Date.from(instant), new Date());

                if (0 < this.planNumInfo.getLineTakt()) {
                    long currentGoal = Math.min(currentTime / this.planNumInfo.getLineTakt(), this.setting.getDailyPlanNum());
                    currentGoal = (0 < currentGoal) ? currentGoal : 0;
                    long diff = this.planNumInfo.getActualNum() - currentGoal;

                    if (0 < currentGoal) {
                        if (diff <= -(this.setting.getDailyLineWarnThreshold())) {
                            fontColor = this.setting.getWarningFontColor();
                            backColor = this.setting.getWarningBackColor();
                        } else if (diff <= -(this.setting.getDailyLineAttenThreshold())) {
                            fontColor = this.setting.getCautionFontColor();
                            backColor = this.setting.getCautionBackColor();
                        }
                    }

                    this.deviatedNumLabel.setText(MonitorTools.formatTaktTime(diff * this.planNumInfo.getLineTakt()));

                    logger.info("DailyLineTime: {}, {}", this.planNumInfo.getLineTakt(), currentGoal);
                } else {
                    this.deviatedNumLabel.setText(MonitorTools.formatTaktTime(0));
                }

                this.deviatedLabel.setPrefWidth(this.contentPane.getWidth() * 0.3);
                this.deviatedNumLabel.setPrefWidth(this.contentPane.getWidth() * 0.7);

                String hex = String.format("#%02X%02X%02X", (int) (backColor.getRed() * 255), (int) (backColor.getGreen() * 255), (int) (backColor.getBlue() * 255));
                this.contentPane.setStyle(String.format("-fx-background-color:%s;", hex));

                double px;
                px = MonitorTools.getFontSize(this.deviatedLabel.getText(), this.deviatedLabel.getPrefWidth(), this.contentPane.getHeight(), this.fontSize3L);
                this.deviatedLabel.setStyle(String.format("-fx-font-size: %fpx;", px));
                this.deviatedLabel.setTextFill(fontColor);

                px = MonitorTools.getFontSize(this.deviatedNumLabel.getText(), this.deviatedNumLabel.getPrefWidth(), this.contentPane.getHeight(), this.fontSize3L);
                this.deviatedNumLabel.setStyle(String.format("-fx-font-size: %fpx;", px));
                this.deviatedNumLabel.setTextFill(fontColor);
            }
        });
    }

    /**
     * フォントサイズを取得する。
     *
     * @param width
     * @param text
     * @param font
     * @return
     */
    private double getFontSize(double width, String text, Font font) {
        //double pixels = TextUtils.computeTextWidth(Font.font("Meiryo UI", this.fontSize), text, Double.MAX_VALUE);
        Text helper = new Text(text);
        helper.setFont(font);
        return width >= helper.getBoundsInLocal().getWidth() ? this.fontSize3L : this.fontSize3L * (width / helper.getBoundsInLocal().getWidth() * 0.9);
    }
}
