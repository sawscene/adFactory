/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorequipmentdeliveryplugin;

import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.CallingNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.adinterface.command.TimerCommand;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.MonitorEquipmentStatusInfoEntity;
import jp.adtekfuji.andon.entity.ProductivityEntity;
import jp.adtekfuji.andon.enumerate.MonitorStatusEnum;
import jp.adtekfuji.andon.property.MonitorSettingTP;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程実績進捗フレームのコントローラー
 *
 * @author s-heya
 */
@AndonComponent(title = "工程実績進捗フレーム")
@FxComponent(id = "EquipmentDeliveryCompo", fxmlPath = "/fxml/admonitorequipmentdeliveryplugin/EquipmentDeliveryCompo.fxml")
public class EquipmentDeliveryCompoController  implements Initializable, ArgumentDelivery, AdAndonComponentInterface {

    private final double MAX_COLUMN_WIDTH = 180.0;

    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade monitorFacade = new AndonLineMonitorFacade();
    private final List<ProdData> prodList = new LinkedList<>();
    private final Set<Long> equipmentIds = new HashSet<>();
    private Long monitorId;
    private MonitorSettingTP setting;
    private final Map<StatusPatternEnum, DisplayedStatusInfoEntity> displayedStatuses = new HashMap<>();
    private double fontSize = Double.NaN;
    private double columnWidth;
    private double columnHeight;
    private boolean updateData;
    private boolean updateRest;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private HBox hBox;

    private VBox titleColumn;

   /**
     * 工程実績進捗フレームを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.rootPane.getStylesheets().addAll(getClass().getResource("/styles/grid-style.css").toExternalForm());
            this.hBox.getStyleClass().add("grid");

            // フォントサイズ
            if (!AdProperty.getProperties().containsKey("fontSizeEquipmentDelivery")) {
                AdProperty.getProperties().setProperty("fontSizeEquipmentDelivery", Constants.DEF_FONT_SIZE_MIDDLE);
            }
            this.fontSize = Double.parseDouble(AdProperty.getProperties().getProperty("fontSizeEquipmentDelivery"));

            if (!AdProperty.getProperties().containsKey("updateRest")) {
                AdProperty.getProperties().setProperty("updateRest", "false");
            }
            this.updateRest = Boolean.parseBoolean(AdProperty.getProperties().getProperty("updateRest"));

            // モニタID
            this.monitorId = AndonLoginFacade.getMonitorId();

            // 進捗モニタ設定情報
            if (0 != this.monitorId) {
                AndonMonitorSettingFacade monitorSettingFacade = new AndonMonitorSettingFacade();
                this.setting = (MonitorSettingTP) monitorSettingFacade.getLineSetting(monitorId, MonitorSettingTP.class);

                // 表示ステータスを取得
                List<DisplayedStatusInfoEntity> entities = new DisplayedStatusInfoFacade().findAll();
                for (DisplayedStatusInfoEntity entity : entities) {
                    this.displayedStatuses.put(entity.getStatusName(), entity);
                }

                LocalDate today = LocalDate.now();
                ZoneId zoneId = ZoneId.systemDefault();

                for (WorkEquipmentSetting workEquipmentSetting : setting.getWorkActualCollection()) {
                    Date startWorkTime = Date.from(workEquipmentSetting.getStartWorkTime().atDate(today).atZone(zoneId).toInstant());
                    Date endWorkTime = Date.from(workEquipmentSetting.getEndWorkTime().atDate(today).atZone(zoneId).toInstant());
                    List<BreakTimeInfoEntity> breaktimes = BreaktimeUtil.getAppropriateBreaktimes(this.setting.getBreaktimes(), startWorkTime, endWorkTime);

                    // 1日の作業時間
                    long breakTime = 0;
                    for (BreakTimeInfoEntity breaktime : breaktimes) {
                        LocalTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(breaktime.getStarttime().getTime()), zoneId).toLocalTime();
                        LocalTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(breaktime.getEndtime().getTime()), zoneId).toLocalTime();
                        breakTime += ChronoUnit.MILLIS.between(start, end);
                    }
                    Long workTime = ChronoUnit.MILLIS.between(workEquipmentSetting.getStartWorkTime(), workEquipmentSetting.getEndWorkTime()) - breakTime;

                    this.prodList.add(new ProdData(workEquipmentSetting, workTime));
                    this.equipmentIds.addAll(workEquipmentSetting.getEquipmentIds());
                }

                this.prodList.sort((a, b)-> a.getSetting().getOrder() - b.getSetting().getOrder());

                this.updateData = true;
                this.refresh();
            }

            // 分類
            Label label1 = new Label(LocaleUtils.getString("key.Class"));
            label1.getStyleClass().add("title-cell");
            label1.setStyle(String.format("-fx-font-size: %fpx;", this.fontSize));

            // 実績
            Label label2 = new Label(LocaleUtils.getString("key.MonitorDailyActualNum"));
            label2.getStyleClass().add("title-cell");
            label2.setStyle(String.format("-fx-font-size: %fpx;", this.fontSize));

            // 目標
            Label label3 = new Label(LocaleUtils.getString("key.Target"));
            label3.getStyleClass().add("title-cell");
            label3.setStyle(String.format("-fx-font-size: %fpx;", this.fontSize));

            // 工程
            Label label4 = new Label(LocaleUtils.getString("key.Process"));
            label4.getStyleClass().add("title-cell");
            label4.setStyle(String.format("-fx-font-size: %fpx;", this.fontSize));

            this.titleColumn = new VBox();
            this.titleColumn.getChildren().addAll(label1, label2, label3, label4);

            this.hBox.getChildren().add(this.titleColumn);

            this.titleColumn.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                this.columnWidth = Math.min((this.rootPane.getWidth() - this.titleColumn.getWidth()) / this.prodList.size() - 1.5, MAX_COLUMN_WIDTH);
                this.columnHeight = this.hBox.getHeight() / 4 - 1.5;

                if (this.hBox.getHeight() < this.titleColumn.getHeight()) {
                    this.fontSize = Math.min(this.fontSize, this.columnHeight * 0.7);
                    label1.setStyle(String.format("-fx-font-size: %fpx;", this.fontSize));
                    label2.setStyle(String.format("-fx-font-size: %fpx;", this.fontSize));
                    label3.setStyle(String.format("-fx-font-size: %fpx;", this.fontSize));
                    label4.setStyle(String.format("-fx-font-size: %fpx;", this.fontSize));
                }

                label1.setMinHeight(this.columnHeight);
                label2.setMinHeight(this.columnHeight);
                label3.setMinHeight(this.columnHeight);
                label4.setMinHeight(this.columnHeight);

                this.refresh();
            });

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * パラメータを設定する。
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
    }

   /**
     * 表示を更新する。
     *
     * @param msg
     */
    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof ActualNoticeCommand) {
            // 実績通知
            ActualNoticeCommand command = (ActualNoticeCommand) msg;

            if (!StringUtils.like(command.getModelName(), this.setting.getModelName())) {
                return;
            }
        
            DisplayedStatusInfoEntity status = null;
            switch (command.getEquipmentStatus()) {
                case WORKING:
                    status = this.displayedStatuses.get(StatusPatternEnum.WORK_NORMAL);
                    break;
                case INTERRUPT:
                    status = this.displayedStatuses.get(StatusPatternEnum.INTERRUPT_NORMAL);
                    break;
                case SUSPEND:
                    status = this.displayedStatuses.get(StatusPatternEnum.SUSPEND_NORMAL);
                    break;
                case COMPLETION:
                    status = this.displayedStatuses.get(StatusPatternEnum.PLAN_NORMAL);
                    for (ProdData data : this.prodList) {
                        if (data.getSetting().getEquipmentIds().contains(command.getEquipmentId())) {
                            data.setProdCount(data.getProdCount() + 1);
                        }
                    }
                    break;
            }

            if (Objects.nonNull(status)) {
                for (ProdData data : this.prodList) {
                    if (data.getSetting().getEquipmentIds().contains(command.getEquipmentId())) {

                        Optional<MonitorEquipmentStatusInfoEntity> optional = data.getStatuses().stream()
                                .filter(o -> Objects.equals(o.getEquipmentId(), command.getEquipmentId())).findFirst();

                        if (optional.isPresent()) {
                            MonitorEquipmentStatusInfoEntity equipmentStatus = optional.get();
                            equipmentStatus.status(status.getStatusName());
                            equipmentStatus.setFontColor(status.getFontColor());
                            equipmentStatus.setBackColor(status.getBackColor());
                        } else {
                            data.getStatuses().add(new MonitorEquipmentStatusInfoEntity()
                                .status(status.getStatusName()).equipmentId(command.getEquipmentId())
                                .fontColor(status.getFontColor()).backColor(status.getBackColor()));
                        }
                    }
                }

                this.refresh();
            }

        } else if (msg instanceof TimerCommand) {
            // 定期更新
            this.refresh();

        } else if (msg instanceof CallingNoticeCommand) {
            // 呼出通知
            CallingNoticeCommand command = (CallingNoticeCommand) msg;
            for (ProdData data : this.prodList) {
                if (data.getSetting().getEquipmentIds().contains(command.getEquipmentId())) {
                    data.setCalled(command.getIsCall());
                }
            }

            if (!command.getIsCall()) {
                for (ProdData data : this.prodList) {
                    if (data.getSetting().getEquipmentIds().contains(command.getEquipmentId())) {

                        Optional<MonitorEquipmentStatusInfoEntity> optional = data.getStatuses().stream()
                                .filter(o -> Objects.equals(o.getEquipmentId(), command.getEquipmentId()) && MonitorStatusEnum.CALL == o.getStatus()).findFirst();

                        if (optional.isPresent()) {
                            // 設備ステータスを再取得
                            this.updateData = true;
                            break;
                        }
                    }
                }
            }

            this.refresh();
        } else if (msg instanceof ResetCommand) {
            // リカバリー
            this.updateData = true;
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
        if (!this.updateData && !this.updateRest) {
            Platform.runLater(() -> draw());
            return;
        }
        this.updateData = false;

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (EquipmentDeliveryCompoController.this) {
                        List<Long> targetIds = new ArrayList<>(equipmentIds);

                        List<ProductivityEntity> productivities = monitorFacade.getDailyEquipmentProductivity(targetIds);

                        for (ProdData data : prodList) {
                            data.setProdCount(0);
                            data.getStatuses().clear();
                        }

                        for (ProductivityEntity productivity : productivities) {
                            for (ProdData data : prodList) {
                                if (data.getSetting().getEquipmentIds().contains(productivity.getId())) {
                                    data.setProdCount(data.getProdCount() + productivity.getProdCount().intValue());
                                }
                            }
                        }

                        List<MonitorEquipmentStatusInfoEntity> statuses = monitorFacade.getEquipmentStatus(monitorId, targetIds);

                        for (MonitorEquipmentStatusInfoEntity status : statuses) {
                            for (ProdData data : prodList) {
                                if (data.getSetting().getEquipmentIds().contains(status.getEquipmentId())) {
                                    data.getStatuses().add(status);
                                }
                            }
                        }

                        Platform.runLater(() -> draw());
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
     * データを表示する。
     */
    private synchronized void draw() {
        this.hBox.getChildren().clear();
        this.hBox.getChildren().add(this.titleColumn);

        LocalDate today = LocalDate.now();
        Date now = new Date();

        Font font = Font.font("Meiryo UI", this.fontSize);
        String text;
        double px;
        double padding = this.prodList.size() <= 11 ? 0 : Double.parseDouble(AdProperty.getProperties().getProperty("padding", "8"));

        for (ProdData data : this.prodList) {
            Color fontColor = Color.WHITE;
            Color backColor = Color.BLACK;
            long currentGoal = 0;
            WorkEquipmentSetting workEquipmentSetting = data.getSetting();

            if (0 < data.getTaktTime()) {
                Date startWorkTime = Date.from(workEquipmentSetting.getStartWorkTime().atDate(today).atZone(ZoneId.systemDefault()).toInstant());
                long currentTime = BreaktimeUtil.getDiffTime(this.setting.getBreaktimes(), startWorkTime, now);

                currentGoal = Math.min(currentTime / data.getTaktTime(), workEquipmentSetting.getPlanNum());
                currentGoal = (0 < currentGoal) ? currentGoal : 0;

                if (0 < currentGoal) {
                    long diff = data.getProdCount() - currentGoal;
                    if (diff <= -(setting.getWorkWarnThreshold())) {
                        fontColor = setting.getWarningFontColor();
                        backColor = setting.getWarningBackColor();
                    } else if (diff <= -(setting.getWorkAttenThreshold())) {
                        fontColor = setting.getCautionFontColor();
                        backColor = setting.getCautionBackColor();
                    }
                }
            }

            String hex = String.format("#%02X%02X%02X", (int) (backColor.getRed() * 255), (int) (backColor.getGreen() * 255), (int) (backColor.getBlue() * 255));

            // 分類
            px = this.getFontSize(workEquipmentSetting.getCategoryName(), this.columnWidth - padding, this.columnHeight, font);
            Label label1 = new Label(workEquipmentSetting.getCategoryName());
            label1.getStyleClass().add("title-cell");
            label1.setStyle(String.format("-fx-font-size: %fpx;", px));
            label1.setPrefWidth(this.columnWidth);
            label1.setMinHeight(this.columnHeight);

            // 実績
            text = String.valueOf(data.getProdCount());
            px = this.getFontSize(text, this.columnWidth - padding, this.columnHeight, font);
            Label label2 = new Label(text);
            label2.getStyleClass().add("cell");
            label2.setStyle(String.format("-fx-font-size: %fpx; -fx-background-color: white, %s;", px, hex));
            label2.setPrefWidth(this.columnWidth);
            label2.setMinHeight(this.columnHeight);
            label2.setTextFill(fontColor);

            // 目標
            text = String.valueOf(currentGoal);
            px = this.getFontSize(text, this.columnWidth - padding, this.columnHeight, font);
            Label label3 = new Label(text);
            label3.getStyleClass().add("cell");
            label3.setStyle(String.format("-fx-font-size: %fpx;  -fx-background-color: white, black;", px));
            label3.setPrefWidth(this.columnWidth);
            label3.setMinHeight(this.columnHeight);
            label3.setTextFill(Color.WHITE);

            // 設備ステータス
            if (data.isCalled()) {
                DisplayedStatusInfoEntity displayedStatus = this.displayedStatuses.get(StatusPatternEnum.CALLING);
                fontColor = Color.web(displayedStatus.getFontColor());
                hex = displayedStatus.getBackColor();
            } else if (Objects.nonNull(setting.getBreaktimes()) && BreaktimeUtil.isBreaktime(setting.getBreaktimes(), now)) {
                DisplayedStatusInfoEntity displayedStatus = this.displayedStatuses.get(StatusPatternEnum.BREAK_TIME);
                fontColor = Color.web(displayedStatus.getFontColor());
                hex = displayedStatus.getBackColor();
                // 休憩が終わったら、設備ステータスを再取得
                this.updateData = true;
            } else {
                MonitorEquipmentStatusInfoEntity equipmentStatus = data.getDisplayStatus();
                fontColor = Color.web(equipmentStatus.getFontColor());
                hex = equipmentStatus.getBackColor();
            }

            // 工程
            px = this.getFontSize(workEquipmentSetting.getTitle(), this.columnWidth - padding, this.columnHeight, font);
            Label label4 = new Label(workEquipmentSetting.getTitle());
            label4.getStyleClass().add("cell");
            label4.setStyle(String.format("-fx-font-size: %fpx;  -fx-background-color: white, %s;", px, hex));
            label4.setPrefWidth(this.columnWidth);
            label4.setMinHeight(this.columnHeight);
            label4.setTextFill(fontColor);

            VBox vBox = new VBox();
            vBox.getChildren().addAll(label1, label2, label3, label4);

            this.hBox.getChildren().add(vBox);
        }
    }

    /**
     * フォントサイズを取得する。
     *
     * @param text
     * @param width
     * @param height
     * @param font
     * @return
     */
    private double getFontSize(String text, double width, double height, Font font) {
        Text helper = new Text(text);
        helper.setFont(font);
        double size = width >= helper.getBoundsInLocal().getWidth() ? this.fontSize : this.fontSize * (width / helper.getBoundsInLocal().getWidth() * 0.9);
        helper.setFont(Font.font("Meiryo UI", size));
        return height >= helper.getBoundsInLocal().getHeight() ? size : size * (height / helper.getBoundsInLocal().getHeight() * 0.9);
    }
}
