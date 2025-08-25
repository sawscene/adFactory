/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.dialog;

import adtekfuji.admanagerapp.analysisplugin.common.AnalysisTabelData;
import adtekfuji.admanagerapp.analysisplugin.common.AnalysisWorkFilterData;
import adtekfuji.admanagerapp.analysisplugin.javafx.NomalDistribtionData;
import javafx.scene.control.Label;
import jp.adtekfuji.forfujiapp.utils.Staristics;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 分析画面クラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.01.Mon
 */
@FxComponent(id = "AnalysisDialog", fxmlPath = "/fxml/dialog/analysisDialog.fxml")
public class AnalysisDialog implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private NomalDistribtionData distribtionData;
    private final AnalysisWorkFilterData.TimeUnitEnum TIME_UNIT_RATE
            = AnalysisWorkFilterData.TimeUnitEnum.getEnum(AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG)
                    .getProperty(ClientPropertyConstants.KEY_TIME_UNIT, "SECOND")); // ミリ秒を分に変更
    private final String TIME_UNIT_TAG = "(" + AnalysisWorkFilterData.TimeUnitEnum.getLocale(rb, TIME_UNIT_RATE) + ")";

    @FXML
    private AnchorPane base;
    @FXML
    private LineChart<Number, Number> worktimeLineChart;
    @FXML
    private NumberAxis worktimeAxis;
    @FXML
    private NumberAxis probabilityAxis;
    @FXML
    private TableView<AnalysisTabelData> staristicsTable;
    @FXML
    private TableColumn<AnalysisTabelData, String> typeColumn;
    @FXML
    private TableColumn<AnalysisTabelData, Double> dataColumn;
    @FXML
    private TableView<ActualResultEntity> actualTable;
    @FXML
    private TableColumn<ActualResultEntity, String> kanbanNameColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> workflowNameColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> workNameColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> workingParsonColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> workEquipmentColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> delayReasonColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> implementDatetimeColumn;
    @FXML
    private TableColumn<ActualResultEntity, String> totalWorkTimeColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info(AnalysisDialog.class.getName() + ":initialize start");

        staristicsTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        actualTable.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        // ウィンドウのサイズ設定
        this.base.setPrefSize(sc.getStage().getWidth() - 128, sc.getStage().getHeight() - 128);

        // 分析結果テーブルの初期化
        this.typeColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        this.dataColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        // 作業実績テーブルの初期化
        this.kanbanNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ActualResultEntity, String> param) -> param.getValue().kanbanNameProperty());
        this.workflowNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ActualResultEntity, String> param) -> param.getValue().workflowNameProperty());
        this.workNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ActualResultEntity, String> param) -> param.getValue().workNameProperty());
        this.workingParsonColumn.setCellValueFactory((TableColumn.CellDataFeatures<ActualResultEntity, String> param) -> param.getValue().organizationNameProperty());
        this.workEquipmentColumn.setCellValueFactory((TableColumn.CellDataFeatures<ActualResultEntity, String> param) -> param.getValue().equipmentNameProperty());
        this.delayReasonColumn.setCellValueFactory((TableColumn.CellDataFeatures<ActualResultEntity, String> param) -> param.getValue().delayReasonProperty());
        this.implementDatetimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<ActualResultEntity, String> param) -> Bindings.createStringBinding(()
                -> Objects.isNull(param.getValue().getImplementDatetime()) ? "" : new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat")).format(param.getValue().getImplementDatetime())));
        this.workflowNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ActualResultEntity, String> param) -> param.getValue().workflowNameProperty());
        this.totalWorkTimeColumn.setCellValueFactory((TableColumn.CellDataFeatures<ActualResultEntity, String> param) -> Bindings.createStringBinding(()
                -> Integer.toString(param.getValue().getWorkingTime() / TIME_UNIT_RATE.getTimeUnit())));
        this.totalWorkTimeColumn.setText(this.totalWorkTimeColumn.getText() + TIME_UNIT_TAG);

        this.logger.info(AnalysisDialog.class.getName() + ":initialize end");
    }

    @Override
    public void setArgument(Object argument) {
        logger.info(AnalysisDialog.class.getName() + ":setArgument start");
        if (argument instanceof NomalDistribtionData) {
            this.distribtionData = (NomalDistribtionData) argument;
            this.createNomalDistribution();
            this.createStatistics();
            this.createActualTable();
        }
        logger.info(AnalysisDialog.class.getName() + ":setArgument end");
    }

    /**
     * 正規分布のグラフを生成する
     *
     */
    private void createNomalDistribution() {
        logger.info(AnalysisDialog.class.getName() + ":calculateNomalDistribution start");
        try {
            // Integerをdoubleに変換
            double[] dataList = new double[this.distribtionData.getDatas().size()];
            int i = 0;
            for (Long data : distribtionData.getDatas()) {
                dataList[i] = (double) distribtionData.getTakttime() - data;
                i++;
            }

            // 正規分布のデータ生成
            XYChart.Series series = new XYChart.Series();
            series.setName(this.distribtionData.getGraphTitle());
            double[] probabilities = new double[this.distribtionData.getDatas().size()];
            for (int j = 0; j < dataList.length; j++) {
                probabilities[j] = Staristics.probabilityDestiny(dataList[j], dataList);
                series.getData().add(new XYChart.Data<>(dataList[j], probabilities[j]));
            }
            this.probabilityAxis.setUpperBound(Staristics.maximum(probabilities));
            this.worktimeAxis.setUpperBound(Staristics.maximum(dataList));
            this.worktimeAxis.setLowerBound(Staristics.minimum(dataList));
            this.worktimeLineChart.getData().add(series);

            for (XYChart.Series<Number, Number> s : this.worktimeLineChart.getData()) {
                for (XYChart.Data<Number, Number> d : s.getData()) {
                    Tooltip.install(d.getNode(), new Tooltip(LocaleUtils.getString("key.WorkTime") + " : " + d.getXValue()
                            + "\n\r" + LocaleUtils.getString("key.probabilityDestiny") + " : " + d.getYValue()));
                }
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
        logger.info(AnalysisDialog.class.getName() + ":calculateNomalDistribution end");
    }

    /**
     * 分析データを表示する
     *
     */
    private void createStatistics() {
        logger.info(AnalysisDialog.class.getName() + ":createStatistics start");
        try {
            double[] dataList = new double[this.distribtionData.getDatas().size()];
            int i = 0;
            for (Long data : distribtionData.getDatas()) {
                dataList[i] = (double) distribtionData.getTakttime() - data;
                i++;
            }

            ObservableList<AnalysisTabelData> analysisTabelDatas = FXCollections.observableArrayList(new ArrayList<AnalysisTabelData>());
            analysisTabelDatas.add(new AnalysisTabelData(LocaleUtils.getString("key.ProcessName"), String.valueOf(this.distribtionData.getGraphTitle())));
            analysisTabelDatas.add(new AnalysisTabelData(LocaleUtils.getString("key.TactTime") + TIME_UNIT_TAG, Integer.toString(this.distribtionData.getTakttime())));
            analysisTabelDatas.add(new AnalysisTabelData(LocaleUtils.getString("key.actualNum"), String.valueOf(this.distribtionData.getActuals().size())));
            analysisTabelDatas.add(new AnalysisTabelData(LocaleUtils.getString("key.itemMaximum") + TIME_UNIT_TAG, String.valueOf(new Double(Staristics.maximum(dataList)))));
            analysisTabelDatas.add(new AnalysisTabelData(LocaleUtils.getString("key.itemMinimum") + TIME_UNIT_TAG, String.valueOf(new Double(Staristics.minimum(dataList)))));
            analysisTabelDatas.add(new AnalysisTabelData(LocaleUtils.getString("key.itemMedium") + TIME_UNIT_TAG, String.valueOf(new Double(Staristics.medium(dataList)))));
            analysisTabelDatas.add(new AnalysisTabelData(LocaleUtils.getString("key.itemsAverage") + TIME_UNIT_TAG, String.valueOf(new Double(Staristics.average(dataList)))));
            analysisTabelDatas.add(new AnalysisTabelData(LocaleUtils.getString("key.itemsStandardDeviation"), String.valueOf(new Double(Staristics.standardDeviation(dataList)))));
            analysisTabelDatas.add(new AnalysisTabelData(LocaleUtils.getString("key.itemsVariance"), String.valueOf(new Double(Staristics.variance(dataList)))));

            Platform.runLater(() -> {
                this.staristicsTable.setItems(analysisTabelDatas);
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(AnalysisDialog.class.getName() + ":createStatistics end");
    }

    /**
     * 作業データをテーブルに表示する
     *
     */
    private void createActualTable() {
        logger.info(AnalysisDialog.class.getName() + ":createTable start");
        try {
            // 実績プロパティカラムの表示
            ObservableList<ActualResultEntity> tableData = FXCollections.observableArrayList(this.distribtionData.getActuals());
            List<String> propertyColumnDatas = new ArrayList();
            tableData.stream().forEach((resultEntity) -> {
                resultEntity.getPropertyCollection().stream().filter((propertyEntity) -> (!propertyColumnDatas.contains(propertyEntity.getActualPropName()))).forEach((propertyEntity) -> {
                    propertyColumnDatas.add(propertyEntity.getActualPropName());
                });
            });
            if (!propertyColumnDatas.isEmpty()) {
                for (String propertyName : propertyColumnDatas) {
                    TableColumn<ActualResultEntity, String> propertyColumn = new TableColumn<>(propertyName);
                    propertyColumn.setCellValueFactory((TableColumn.CellDataFeatures<ActualResultEntity, String> param) -> param.getValue().getPropertyValue(propertyName));
                    Platform.runLater(() -> {
                        this.actualTable.getColumns().add(propertyColumn);
                    });
                }
            }

            // 実績情報の表示
            Platform.runLater(() -> {
                actualTable.setItems(tableData);
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(AnalysisDialog.class.getName() + ":createTable end");
    }
}
