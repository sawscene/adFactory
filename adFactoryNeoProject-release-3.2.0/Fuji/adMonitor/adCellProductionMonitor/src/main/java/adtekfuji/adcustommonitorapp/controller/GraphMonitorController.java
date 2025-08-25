/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.controller;

import adtekfuji.adcustommonitorapp.common.ReSizeHandler;
import adtekfuji.adcustommonitorapp.service.CellProductionMonitorServiceInterface;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import jp.adtekfuji.andon.utility.MonitorTools;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorGraphData;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorGraphInfoEntity;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * グラフモニタークラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.8.Thr
 */
public class GraphMonitorController implements Initializable, CellProductionMonitorServiceInterface, ReSizeHandler {

    private final Logger logger = LogManager.getLogger();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    @FXML
    AnchorPane graphBase;
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    Pane progressPane;

    private MonitorGraphInfoEntity info;
    private long rangeY = 600L;
    private long interval;
    private long lastMillis = 0L;

    /**
     * Initializes the controller class.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.interval = Long.parseLong(AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).getProperty(ClientPropertyConstants.PROP_KEY_MASTER_INTERVAL, ClientPropertyConstants.DEFALT_MASTER_INTERVAL));
    }

    public void setArgument(MonitorGraphInfoEntity info) {
        if (Objects.nonNull(info)) {
            this.info = info;
            this.updateThread();
        }
    }

    /**
     * 実績受信処理
     *
     * @param kanbanId 実績を受信したカンバンID
     */
    @Override
    public void receivedActualDataKanbanId(long kanbanId) {
        long nowTime = System.currentTimeMillis();
        if ((nowTime - this.lastMillis) <= this.interval) {
            return;
        }
        this.lastMillis = nowTime;

        if (this.checkCarryKanbanId(kanbanId)) {
            this.updateThread();
        }
    }

    /**
     * カンバンIDを保有しているか確認
     *
     * @param kanbanId 確認するカンバンID
     * @return true:持っている/false:持っていない
     */
    private boolean checkCarryKanbanId(long kanbanId) {
        if (Objects.nonNull(this.info.getKanbanIds())) {
            if (this.info.getKanbanIds().contains(kanbanId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新処理
     *
     */
    private void updateThread() {
        logger.info(GraphMonitorController.class.getName() + ":updateThread start");
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    UnitSearchCondition condition = new UnitSearchCondition().unitId(info.getUnitId());
                    List<MonitorGraphInfoEntity> list = RestAPI.getMonitorGraph(condition, AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).getProperty(ClientPropertyConstants.PROP_KEY_SELECT_GRAPH_TITLE, ClientPropertyConstants.DEFAULT_SELECT_TITLE));
                    if (!list.isEmpty()) {
                        info = list.get(0);
                        updateView();
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }
                logger.info(GraphMonitorController.class.getName() + ":updateThread end");
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 画面更新
     *
     */
    private void updateView() {
        logger.info(GraphMonitorController.class.getName() + ":updateView start");
        try {
            Platform.runLater(() -> {
                settingGraph();
                plotGraph();
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(GraphMonitorController.class.getName() + ":updateView end");
    }

    /**
     * グラフの設定
     *
     */
    private void settingGraph() {
        this.xAxis.setLowerBound(0);
        this.xAxis.setUpperBound(this.info.getGraphData().size());
        this.xAxis.setTickUnit(5);
        this.xAxis.setMinorTickCount(1);
        this.yAxis.setLowerBound(-rangeY);
        this.yAxis.setUpperBound(rangeY);
        this.yAxis.setTickUnit(rangeY / 10);
        this.yAxis.setMinorTickCount(1);
        lineChart.setTitle(this.info.getTitle());
        lineChart.getData().clear();
    }

    /**
     * データをプロットする
     *
     */
    private void plotGraph() {
        // 計画開始時間と実績開始時間との差
        XYChart.Series series1 = new XYChart.Series();
        series1.setName(LocaleUtils.getString("key.StartScheduledTime"));

        // 標準タクトタイムと作業時間との差
        XYChart.Series series2 = new XYChart.Series();
        series2.setName(LocaleUtils.getString("key.TactTime"));

        for (MonitorGraphData data : this.info.getGraphData()) {
            long diffTaktTime = data.getTaktTime() - data.getWorkingTime();
            data.setDisplayValue(series1.getName() + " " + MonitorTools.formatTaktTime(data.getProcessTime()) + "\n" + series2.getName() + " " + MonitorTools.formatTaktTime(diffTaktTime));

            // 計画開始時間と実績開始時間との差
            long diffStartTime = data.getProcessTime() / 1000;
            if (-rangeY > diffStartTime) {
                diffStartTime = -rangeY;
            } else if (rangeY < diffStartTime) {
                diffStartTime = rangeY;
            }
            series1.getData().add(new XYChart.Data<>(data.getWorkNumIndex(), diffStartTime, data));

            // 標準タクトタイムと作業時間との差
            diffTaktTime = diffTaktTime / 1000;
            if (-rangeY > diffTaktTime) {
                diffTaktTime = -rangeY;
            } else if (rangeY < diffTaktTime) {
                diffTaktTime = rangeY;
            }
            series2.getData().add(new XYChart.Data<>(data.getWorkNumIndex(), diffTaktTime, data));
        }

        this.lineChart.getData().add(series1);
        this.lineChart.getData().add(series2);

        for (XYChart.Series<Number, Number> s : lineChart.getData()) {
            for (XYChart.Data<Number, Number> d : s.getData()) {
                MonitorGraphData data = (MonitorGraphData) d.getExtraValue();
                if (Objects.nonNull(data)) {
                    Tooltip toolTip = TooltipBuilder.build();
                    toolTip.setText(data.getWorkName() + "\n" + data.getDisplayValue());
                    Tooltip.install(d.getNode(), toolTip);
                }
            }
        }
    }

    /**
     * 画面操作制限
     *
     * @param isBlock true;有効/false:無効
     */
    private void blockUI(boolean isBlock) {
        graphBase.setDisable(isBlock);
        progressPane.setVisible(isBlock);
    }

    @Override
    public void resize(double xSize, double ySize) {
        Platform.runLater(() -> {
            rangeY = (long) ySize;
            settingGraph();
            plotGraph();
        });
    }
}
