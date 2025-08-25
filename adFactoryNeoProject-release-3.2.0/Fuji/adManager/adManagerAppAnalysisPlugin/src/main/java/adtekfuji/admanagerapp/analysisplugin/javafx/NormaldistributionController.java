/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.javafx;

import jp.adtekfuji.forfujiapp.utils.Staristics;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 正規分布グラフクラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.07.29.Fri
 */
public class NormaldistributionController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private NomalDistribtionData distribtionData;

    @FXML
    private LineChart<Number, Number> normaldistributionChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private PieChart outRangeChart;
    @FXML
    private Label totalNumLabel;
    @FXML
    private Label inRangeNumLabel;
    @FXML
    private Label outRangeNumLabel;

    private final EventHandler<MouseEvent> clickEvent = (MouseEvent mouseEvent) -> {
        logger.info(NormaldistributionController.class.getName() + ":clickEvent start");

        sc.showComponentDialog(this.distribtionData.getGraphTitle(), "AnalysisDialog", this.distribtionData);

        logger.info(NormaldistributionController.class.getName() + ":clickEvent end");
    };

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.xAxis.setLowerBound(0.0);
        this.xAxis.setUpperBound(100.0);
        this.xAxis.setTickUnit(10.0);
        this.yAxis.setLowerBound(0.0);
        this.yAxis.setUpperBound(1.0);
        this.yAxis.setTickUnit(0.1);

        this.normaldistributionChart.addEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
    }

    public void setArgument(NomalDistribtionData distribtionData) {
        this.distribtionData = distribtionData;
        this.calculateNomalDistribution();
        this.calculateOutOfRange();
    }

    /**
     * 正規分布生成処理
     *
     */
    private void calculateNomalDistribution() {
        logger.info(NormaldistributionController.class.getName() + ":calculateNomalDistribution start");
        try {
            // グラフタイトル設定
            this.normaldistributionChart.setTitle(this.distribtionData.getGraphTitle());
            // Integerをdoubleに変換
            double[] dataList = new double[this.distribtionData.getDatas().size()];
            int i = 0;
            for (Long data : this.distribtionData.getDatas()) {
                dataList[i] = (double) this.distribtionData.getTakttime() - data;
                i++;
            }

            // 正規分布のデータ生成
            XYChart.Series series = new XYChart.Series();
            series.setName(this.distribtionData.getGraphTitle());
            for (int j = 0; j < dataList.length; j++) {
                series.getData().add(new XYChart.Data<>(Staristics.deviationValue(dataList[j], dataList),
                        Staristics.probabilityDestiny(dataList[j], dataList)));
            }

            this.normaldistributionChart.getData().add(series);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(NormaldistributionController.class.getName() + ":calculateNomalDistribution end");
    }

    /**
     * 範囲外データ生成処理
     *
     */
    private void calculateOutOfRange() {
        logger.info(NormaldistributionController.class.getName() + ":calculateOutOfRange start");
        try {
            ObservableList<PieChart.Data> pieChartData
                    = FXCollections.observableArrayList(
                            new PieChart.Data(LocaleUtils.getString("key.inARange"), this.distribtionData.getActuals().size()),
                            new PieChart.Data(LocaleUtils.getString("key.outOfRange"), this.distribtionData.getOutOfRangeNum()));
            this.outRangeChart.setData(pieChartData);
            this.outRangeChart.setTitle(LocaleUtils.getString("key.dataRate"));
            this.outRangeChart.setStartAngle(90);
            this.totalNumLabel.setText(String.valueOf(this.distribtionData.getDatas().size() + this.distribtionData.getOutOfRangeNum()));
            this.inRangeNumLabel.setText(String.valueOf(this.distribtionData.getDatas().size()));
            this.outRangeNumLabel.setText(String.valueOf(this.distribtionData.getOutOfRangeNum()));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(NormaldistributionController.class.getName() + ":calculateOutOfRange end");
    }
}
