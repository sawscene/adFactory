/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin.component;

import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleCellSizeTypeEnum;
import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleConstants;
import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleSearcher;
import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleShowConfig;
import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleTypeEnum;
import adtekfuji.admanagerapp.scheduleplugin.schedule.ScheduleInjecter;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.common.DateTimeConstants;
import jp.adtekfuji.forfujiapp.common.agenda.CustomAgendaEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 生産ユニット計画予定画面
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.24.Wen
 */
@FxComponent(id = "UnitScheduleCompo", fxmlPath = "/fxml/compo/unitScheduleCompo.fxml")
public class UnitScheduleCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);

    private final List<CustomAgendaEntity> agendaEntitys = new ArrayList<>();
    private ScheduleTypeEnum schedule;
    private Boolean isUpdateNow = false;

    @FXML
    private GridPane base;
    @FXML
    private DatePicker showStartDatePicker;
    @FXML
    private DatePicker showEndDatePicker;
    @FXML
    private GridPane scheduleGrid;
    @FXML
    private HBox timePane;
    @FXML
    private ScrollPane timeScrollPane;
    @FXML
    private VBox serialPane;
    @FXML
    private ScrollPane serialScrollPane;
    @FXML
    private VBox schedulePane;
    @FXML
    private ScrollPane scheduleScrollPane;
    @FXML
    private Label timeLine;
    @FXML
    private Slider dateWidthSizeSlider;
    @FXML
    private VBox progressPane;

    /**
     * 画面の初期化処理
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(UnitScheduleCompoFxController.class.getName() + ":initialize start.");
        blockUI(false);
        //日付選択用のピッカーを今日の日付に設定
        showStartDatePicker.setValue(DateUtils.toLocalDate(DateUtils.getBeginningOfMonth(new Date())));
        showStartDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            screenUpdate();
        });
        showEndDatePicker.setValue(DateUtils.toLocalDate(DateUtils.getEndOfMonth(new Date())));
        showEndDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            screenUpdate();
        });

        // スクロールバーを拘束
        scheduleScrollPane.vvalueProperty().bindBidirectional(serialScrollPane.vvalueProperty());
        scheduleScrollPane.hvalueProperty().bindBidirectional(timeScrollPane.hvalueProperty());
        timePane.prefWidthProperty().bindBidirectional(schedulePane.prefWidthProperty());

        //画面拡大時のイベント処理
        dateWidthSizeSlider.setOnMouseReleased((MouseEvent event) -> {
            showSchedule(agendaEntitys, createScheduleShowConfig(true));
            event.consume();
        });

        logger.info(UnitScheduleCompoFxController.class.getName() + ":initialize end.");
    }

    /**
     * 描画の初期化処理
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        logger.info(UnitScheduleCompoFxController.class.getName() + ":setArgument start.");
        if (argument instanceof ScheduleTypeEnum) {
            schedule = (ScheduleTypeEnum) argument;
            screenUpdate();
        }
        logger.info(UnitScheduleCompoFxController.class.getName() + ":setArgument end.");
    }

    /**
     * 画面更新処理
     *
     */
    private void screenUpdate() {
        if (Objects.isNull(agendaEntitys) || Objects.isNull(schedule)) {
            return;
        }

        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // 年月を選択した物で取得するようにする
                    Date startDate = DateUtils.getBeginningOfDate(Date.from(showStartDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                    Date endDate = DateUtils.getEndOfDate(Date.from(showEndDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

                    agendaEntitys.clear();
                    switch (schedule) {
                        case UNIT_SCHEDULE:
                            agendaEntitys.addAll(ScheduleSearcher.getUnitSchedule(startDate, endDate, null));
                            break;
                        case ORGANIZATION_SCHEDULE:
                            agendaEntitys.addAll(ScheduleSearcher.getOrganizationSchedule(startDate, endDate, null));
                            break;
                    }
                    showSchedule(agendaEntitys, createScheduleShowConfig(false));
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * スケジュール表示
     *
     * @param agendaEntity スケジュールデータ
     */
    private void showSchedule(List<CustomAgendaEntity> agendaEntity, ScheduleShowConfig config) {
        blockUI(true);
        Platform.runLater(() -> {
            logger.info(UnitScheduleCompoFxController.class.getName() + ":screenUpdate start.");

            Date startDate = DateUtils.getBeginningOfDate(Date.from(showStartDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            Date endDate = DateUtils.getEndOfDate(Date.from(showEndDatePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

            ScheduleInjecter.loadConfig(config);
            ScheduleInjecter.injectDate(timePane, timeScrollPane);
            ScheduleInjecter.injectSerial(startDate, endDate, serialPane, schedulePane, agendaEntity, schedule);
            //　TimeLineを現在の日時まで移動
            ScheduleInjecter.setTimeLine(timeLine, startDate, endDate);
            Platform.runLater(() -> {
                ScheduleInjecter.setScheduleDateNowPoint(timeScrollPane, startDate, endDate);
            });
            blockUI(false);
            logger.info(UnitScheduleCompoFxController.class.getName() + ":screenUpdate end.");
        });
    }

    /**
     * キーイベント処理
     *
     * @param key
     */
    @FXML
    private void onKeyPressed(KeyEvent key) {
        // F5を押されたら画面更新
        if (key.getCode().equals(KeyCode.F5)) {
            if (!isUpdateNow) {
                isUpdateNow = true;
                blockUI(true);
                try {
                    screenUpdate();
                } finally {
                    Platform.runLater(() -> {
                        blockUI(false);
                        isUpdateNow = false;
                    });
                }
            }
        }
    }

    /**
     * 読み込み中断
     *
     * @param event
     */
    @FXML
    private void onCommInterrupt(ActionEvent event) {
        ScheduleSearcher.setIsInterrpt(Boolean.TRUE);
    }

    /**
     * スケジュール表示の設定情報を作成する
     *
     * @param isChangeSchduleSize サイズ変更を行う
     * @return 表示設定
     */
    private ScheduleShowConfig createScheduleShowConfig(boolean isChangeSchduleSize) {
        try {
            ScheduleShowConfig config = new ScheduleShowConfig();
            // サイズ変更の変更か初期表示か設定
            if (isChangeSchduleSize) {
                config.setScheduleSize(null);
            } else {
                config.setScheduleSize(ScheduleCellSizeTypeEnum.getEnum(properties.getProperty(ClientPropertyConstants.PROP_KEY_SCEDULE_CELL_SIZE, ScheduleCellSizeTypeEnum.MONTHLY.name())));
                switch (config.getScheduleSize()) {
                    case DAILY:
                        dateWidthSizeSlider.setValue(ScheduleConstants.DEFAULT_DATE_MAGNIFICATION_DAILY * ScheduleConstants.DEFAULT_MAGNIFICATION);
                        break;
                    case WEEKLY:
                        dateWidthSizeSlider.setValue(ScheduleConstants.DEFAULT_DATE_MAGNIFICATION_WEEKLY * ScheduleConstants.DEFAULT_MAGNIFICATION);
                        break;
                    case MONTHLY:
                        dateWidthSizeSlider.setValue(ScheduleConstants.DEFAULT_DATE_MAGNIFICATION_MONTHLY * ScheduleConstants.DEFAULT_MAGNIFICATION);
                        break;
                }
            }

            // 表示倍率を設定
            config.setDailyWidthMagnification(dateWidthSizeSlider.getValue() / ScheduleConstants.DEFAULT_MAGNIFICATION);
            // 表示する日付の範囲を設定
            config.setBaseMonthlyDate(DateUtils.differenceOfDate(
                    DateUtils.toDate(showEndDatePicker.getValue()),
                    DateUtils.toDate(showStartDatePicker.getValue()),
                    DateTimeConstants.FORMAT_DATE) + 1);
            config.setBaseStartDate(DateUtils.toDate(showStartDatePicker.getValue()));
            config.setBaseEndDate(DateUtils.toDate(showEndDatePicker.getValue()));
            return config;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ScheduleShowConfig();
        }
    }

    private void blockUI(boolean isBlock) {
        this.progressPane.setVisible(isBlock);
        this.base.setDisable(isBlock);
        this.sc.blockUI("SideNaviPane", isBlock);
    }
}
