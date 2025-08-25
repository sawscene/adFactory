package jp.adtekfuji.adandondailyplannumplugin;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.MonitorPlanNumInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.utility.DelayAction;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "当日計画実績数フレーム")
@FxComponent(id = "DailyPlanNum", fxmlPath = "/fxml/daily_plannum_compo.fxml")
public class MonitorDailyPlunNumCompoFxController implements Initializable, AdAndonComponentInterface {

    private final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private final AndonMonitorSettingFacade andonMonitorSettingFacade = new AndonMonitorSettingFacade();
    
    private final Object lock = new Object();

    private MonitorPlanNumInfoEntity planNumInfo = null;
    private Long monitorId;
    private double fontSize;
    final private DelayAction delayAction = new DelayAction();

    private AndonMonitorLineProductSetting setting = null;
    private final List<Long> targetEquipmentIds = new ArrayList();// 対象設備

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label planLabel;
    @FXML
    private Label actualLabel;
    @FXML
    private Label planNumLabel;
    @FXML
    private Label actualNumLabel;
    @FXML
    private Label planUnitLabel;
    @FXML
    private Label actualUnitLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // モニタID
        this.monitorId = AndonLoginFacade.getMonitorId();

        this.readSetting();

        // フォントサイズ
        if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_LARGE)) {
           AdProperty.getProperties().setProperty(Constants.FONT_SIZE_LARGE, Constants.DEF_FONT_SIZE_LARGE);
        }
        this.fontSize = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_LARGE));

        planNumLabel.setText("");
        actualNumLabel.setText("");
        planUnitLabel.setText("");
        actualUnitLabel.setText("");        
        anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
        readTask(AndonLoginFacade.getMonitorId());
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

            delayAction.run(() -> {
                this.readTask(command.getMonitorId());
            });
        } else if (msg instanceof ResetCommand) {
            this.readSetting();
            this.readTask(this.monitorId);
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

    private void readTask(Long monitorId) {
        logger.info("readTask: monitorId={}", monitorId);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (lock) {
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

    private void draw() {
        Platform.runLater(() -> {
            synchronized (this.lock) {
                if (Objects.isNull(this.planNumInfo)) {
                    return;
                }

                final Font font = Font.font("Meiryo UI", this.fontSize);

                if (!StringUtils.isEmpty(this.planNumInfo.getUnit())) {
                    this.planLabel.setPrefWidth(this.anchorPane.getWidth() * 0.3);
                    this.actualLabel.setPrefWidth(this.anchorPane.getWidth() * 0.3);
                    this.planNumLabel.setPrefWidth(this.anchorPane.getWidth() * 0.4);
                    this.actualNumLabel.setPrefWidth(this.anchorPane.getWidth() * 0.4);
                    this.planUnitLabel.setPrefWidth(this.anchorPane.getWidth() * 0.3);
                    this.actualUnitLabel.setPrefWidth(this.anchorPane.getWidth() * 0.3);
                } else {
                    this.planLabel.setPrefWidth(this.anchorPane.getWidth() * 0.4);
                    this.actualLabel.setPrefWidth(this.anchorPane.getWidth() * 0.4);
                    this.planNumLabel.setPrefWidth(this.anchorPane.getWidth() * 0.6);
                    this.actualNumLabel.setPrefWidth(this.anchorPane.getWidth() * 0.6);
                    this.planUnitLabel.setManaged(false);
                    this.planUnitLabel.setVisible(false);
                    this.actualUnitLabel.setManaged(false);
                    this.actualUnitLabel.setVisible(false);
                }
                     
                // フォントサイズ調整
                double height = this.anchorPane.getHeight() * 0.5;
                Function<Label, String> adjust = (label) -> {
                    Text helper = new Text(label.getText());
                    helper.setFont(font);
                    double width = label.getPrefWidth();
                    double fs = (width >= helper.getBoundsInLocal().getWidth()) ? this.fontSize : this.fontSize * (width / helper.getBoundsInLocal().getWidth() * 0.9);
                    helper.setFont(Font.font("Meiryo UI", fs));
                    fs = height >= helper.getBoundsInLocal().getHeight() ? fs : fs * (height / helper.getBoundsInLocal().getHeight() * 0.99);
                    return String.format("-fx-font-size:%fpx; -fx-text-fill:white;", fs);
                };
                
                this.planLabel.setStyle(adjust.apply(this.planLabel));
                this.actualLabel.setStyle(adjust.apply(this.actualLabel));

                NumberFormat nb = NumberFormat.getNumberInstance();
                
                this.planNumLabel.setText(nb.format(this.planNumInfo.getPlanNum()));
                this.planNumLabel.setStyle(adjust.apply(this.planNumLabel));
                
                this.actualNumLabel.setText(nb.format(this.planNumInfo.getActualNum()));
                this.actualNumLabel.setStyle(adjust.apply(this.actualNumLabel));
                
                if (!StringUtils.isEmpty(this.planNumInfo.getUnit())) {
                    this.planUnitLabel.setText(this.planNumInfo.getUnit());
                    this.actualUnitLabel.setText(this.planNumInfo.getUnit());
                    this.planUnitLabel.setStyle(adjust.apply(this.planUnitLabel));
                    this.actualUnitLabel.setStyle(adjust.apply(this.planUnitLabel));
                }
            }
        });
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
