package jp.adtekfuji.adandondailyplandeviatednumplugin;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;
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
import jp.adtekfuji.andon.entity.MonitorPlanDeviatedInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.utility.DelayAction;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "当日実績進捗数フレーム")
@FxComponent(id = "DailyPlanDeviatedNum", fxmlPath = "/fxml/daily_plan_deviated_compo.fxml")
public class MonitorDailyPlanDeviatedNumCompoFxController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();

    private final AndonLineMonitorFacade lineMonitorFacade = new AndonLineMonitorFacade();
    private final AndonMonitorSettingFacade andonMonitorSettingFacade = new AndonMonitorSettingFacade();

    private final Object lock = new Object();

    private Long monitorId;
    private MonitorPlanDeviatedInfoEntity deviatedInfo = new MonitorPlanDeviatedInfoEntity().fontColor(Color.web(Color.WHITE.toString()).toString()).backColor(Color.web(Color.BLACK.toString()).toString());
    private double fontSize3L = Double.NaN;
    final private DelayAction delayAction = new DelayAction();

    private AndonMonitorLineProductSetting setting = null;
    private final List<Long> targetEquipmentIds = new ArrayList();// 対象設備

    @FXML
    private HBox contentPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label numLabel;
    @FXML
    private Label unitLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // フォントサイズ
            if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_3LARGE)) {
                AdProperty.getProperties().setProperty(Constants.FONT_SIZE_3LARGE, Constants.DEF_FONT_SIZE_3LARGE);
            }
            this.fontSize3L = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_3LARGE));

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
                        deviatedInfo = lineMonitorFacade.getDailyDeviatedInfo(monitorId);
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
                if (Objects.isNull(this.deviatedInfo)) {
                    return;
                }

                this.titleLabel.setPrefWidth(this.contentPane.getWidth() * 0.3);
                this.numLabel.setPrefWidth(this.contentPane.getWidth() * 0.4);
                this.unitLabel.setPrefWidth(this.contentPane.getWidth() * 0.3);

                final Font font = Font.font("Meiryo UI", this.fontSize3L);

                Text helper;
                double width;
                double fontSize;
                
                //フォントサイズ調整
                Function<Label, String> adjust = (label) -> {
                    Text he = new Text(label.getText());
                    he.setFont(font);
                    double w = label.getPrefWidth();
                    double fs = (w >= he.getBoundsInLocal().getWidth()) ? this.fontSize3L : this.fontSize3L * (w / he.getBoundsInLocal().getWidth() * 0.9);
                    he.setFont(Font.font("Meiryo UI", fs));
                    fs = this.contentPane.getHeight() >= he.getBoundsInLocal().getHeight() ? fs : fs * (this.contentPane.getHeight() / he.getBoundsInLocal().getHeight()  * 0.99);
                    return String.format("-fx-font-size:%fpx; -fx-text-fill:%s;", fs, this.toRGB(this.deviatedInfo.getFontColor()));
                };

                helper = new Text(this.titleLabel.getText());
                helper.setFont(font);
                width = this.titleLabel.getPrefWidth();
                fontSize = (width >= helper.getBoundsInLocal().getWidth()) ? this.fontSize3L : this.fontSize3L * (width / helper.getBoundsInLocal().getWidth() * 0.9);

                helper.setFont(Font.font("Meiryo UI", fontSize));
                fontSize = this.contentPane.getHeight() >= helper.getBoundsInLocal().getHeight() ? fontSize : fontSize * (this.contentPane.getHeight() / helper.getBoundsInLocal().getHeight() * 0.99);
                this.titleLabel.setStyle(String.format("-fx-font-size:%fpx; -fx-text-fill:%s;", fontSize, this.toRGB(this.deviatedInfo.getFontColor())));

                String sign = (this.deviatedInfo.getPlanDeviatedNum() < 0) ? "" : "+";
                String text = sign + NumberFormat.getNumberInstance().format(this.deviatedInfo.getPlanDeviatedNum());

                this.numLabel.setText(text);
                helper = new Text(text);
                helper.setFont(font);
                width = this.numLabel.getPrefWidth();
                fontSize = (width >= helper.getBoundsInLocal().getWidth()) ? this.fontSize3L : this.fontSize3L * (width / helper.getBoundsInLocal().getWidth() * 0.9);

                helper.setFont(Font.font("Meiryo UI", fontSize));
                fontSize = this.contentPane.getHeight() >= helper.getBoundsInLocal().getHeight() ? fontSize : fontSize * (this.contentPane.getHeight() / helper.getBoundsInLocal().getHeight() * 0.99);
                this.numLabel.setStyle(String.format("-fx-font-size:%fpx; -fx-text-fill:%s;", fontSize, this.toRGB(this.deviatedInfo.getFontColor())));

                this.unitLabel.setText(this.deviatedInfo.getUnit());
                this.unitLabel.setStyle(adjust.apply(this.unitLabel));                

                this.contentPane.setStyle(String.format("-fx-background-color:%s;", this.toRGB(this.deviatedInfo.getBackColor())));
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
