/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.chartplugin.controller;

import adtekfuji.admanagerapp.chartplugin.common.Constants;
import adtekfuji.admanagerapp.chartplugin.controls.BarChartWithMarkers;
import adtekfuji.clientservice.ChartFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringTime;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.imageio.ImageIO;
import jp.adtekfuji.adFactory.entity.chart.ProductionSummaryInfoEntity;
import jp.adtekfuji.adFactory.entity.chart.SummaryItem;
import jp.adtekfuji.adFactory.entity.chart.SummaryTypeEnum;
import jp.adtekfuji.adFactory.entity.chart.WorkSummaryInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.javafxcommon.Locale;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.controls.TimeTextField;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程の平均作業時間グラフ画面のコントローラー
 *
 * @author s-heya
 */
@FxComponent(id = "ChartWorkSummaryCompo", fxmlPath = "/fxml/chartplugin/WorkSummaryCompo.fxml")
public class WorkSummaryCompoController implements Initializable, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private XYChart.Data<String, Number> marker;
    private boolean pannable = true;
    private final Tooltip tooltip = new Tooltip();

    @FXML
    private SplitPane workSummaryPane;
    @FXML
    private DatePane datePaneController;
    @FXML
    private WorkflowPane workflowPaneController;
    @FXML
    private WorkPane workPaneController;
    @FXML
    private ScrollPane chartPane;
    @FXML
    private TimeTextField tactTimeField;
    @FXML
    private ToggleButton taktTimeButton;
    @FXML
    private Button exportButton;
    @FXML
    private PropertySaveTableView<SummaryItem> summaryView;
    @FXML
    private TableColumn<SummaryItem, String> summaryNameColumn;
    @FXML
    private TableColumn<SummaryItem, String> summaryValueColumn;

    /**
     * 棒グラフと折れ線グラフを重ねるStackPane
     */
    @FXML
    private StackPane stackPane;

    @FXML
    private BarChartWithMarkers<String, Number> barChart;

    /**
     * 棒グラフのX軸
     */
    @FXML
    private CategoryAxis barXAxis;
    
    /**
     * 棒グラフのY軸
     */
    @FXML
    private NumberAxis barYAxis;

    /**
     * 折れ線グラフ
     */
    @FXML
    private LineChart<String, Number> lineChart;
    
    /**
     * 折れ線グラフのY軸
     */
    @FXML
    private NumberAxis lineYAxis;

    private final ChartFacade chartFacade = new ChartFacade();

    /**
     * 棒グラフのチャートデータ
     */
    private XYChart.Series<String, Number> barChartDataAll = new XYChart.Series<>();

    /**
     * 折れ線グラフのチャートデータ
     */
    private XYChart.Series<String, Number> lineChartDataAll = new XYChart.Series<>();

    //サマリーデータ
    private List<SummaryItem> summaryData;

    @FXML
    private Pane progressPane;

    /**
     * 工程の平均作業時間グラフ画面の初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        summaryView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        SplitPaneUtils.loadDividerPosition(workSummaryPane, getClass().getSimpleName());

        // サマリー表示設定
        this.summaryView.init("ChartWork" + "summaryView");

        this.workflowPaneController.addListener((observable, oldValue, newValue) -> {
            this.workPaneController.create(workflowPaneController.getSelectedWorkflow());
        });

        // サマリー表示設定
        this.summaryNameColumn.setCellValueFactory(column -> {
            SummaryItem item = (SummaryItem) column.getValue();
            String name = item.getType().getDisplayName(rb);
            switch (item.getType()) {
                case SUSPEND:
                case DELAY:
                    name += "(" + item.getName() + ")";
                    break;
                default:
                    break;
            }
            return new ReadOnlyObjectWrapper<String>(name);
        });

        this.summaryValueColumn.setCellValueFactory(column -> {
            return new ReadOnlyObjectWrapper<String>((column.getValue().getType() == SummaryTypeEnum.AVG_WORK_TIME || column.getValue().getType() == SummaryTypeEnum.STDDEV_WORK_TIME)
                    ? DateUtils.formatTaktTime(column.getValue().getValue().longValue()) : column.getValue().getValue().toString());
        });

        // プロパティを読み込む
        this.loadProperties();

        // 工程のチェックボックス変更時チャートに表示される項目を変更する
        this.workPaneController.addCheckBoxChangeListener((observable, oldValue, newValue) -> {
            this.drawChart();
        });

        // 工程移動時にもチャートを再描写する
        this.workPaneController.setMoveListener(event -> this.drawChart());

        this.chartPane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            if (this.pannable) {
                this.chartPane.setPannable((this.barChart.getPrefWidth() > newBounds.getWidth()) || (this.barChart.getPrefHeight() > newBounds.getHeight()));
                updateChartPositionAndSize();
            }
        });

        this.barChart.widthProperty().addListener((observable, oldValue, newValue) -> {
            double hValue = this.chartPane.getHvalue() * (newValue.doubleValue() / oldValue.doubleValue());
            if (hValue > this.chartPane.getHmin() && hValue < this.chartPane.getHmax()) {
                this.chartPane.setHvalue(hValue);
            }
        });

        this.barChart.heightProperty().addListener((observable, oldValue, newValue) -> {
            double vValue = this.chartPane.getVvalue() * (newValue.doubleValue() / oldValue.doubleValue());
            if (vValue > this.chartPane.getVmin() && vValue < this.chartPane.getVmax()) {
                this.chartPane.setVvalue(vValue);
            }
        });

        // チャート上でマウススクロール
        this.barChart.setOnScroll(event -> {
            double zoomFactor = 1.2;
            this.pannable = false;

            if (event.isShiftDown()) {
                // 水平方向をズーム
                if (event.getDeltaX() < 0) {
                    zoomFactor = 2 - zoomFactor;
                }
                this.barChart.setMinWidth(this.barChart.getWidth() * zoomFactor);
                this.lineChart.setMinWidth(this.lineChart.getWidth() * zoomFactor);
            } else {
                // 垂直方向をズーム
                if (event.getDeltaY() < 0) {
                    zoomFactor = 2 - zoomFactor;
                }
                this.barChart.setMinHeight(this.barChart.getHeight() * zoomFactor);
                this.lineChart.setMinHeight(this.lineChart.getHeight() * zoomFactor);
            }

            event.consume();
        });

        // チャート上でマウスクリック
        this.barChart.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                // チャートをパン
                this.pannable = true;
                updateChartPositionAndSize();
            }
        });

        // 折れ線グラフのマウスイベントは棒グラフへに受け渡す
        this.lineChart.addEventHandler(Event.ANY, event -> {
            if (event instanceof MouseEvent) {
                lineChart.setMouseTransparent(true);
            }
            event.consume();
        });

        this.taktTimeButton.setSelected(true);
        
        // 棒グラフと折れ線グラフに対してCSSを対応
        this.barChart.getStylesheets().add(getClass().getResource("/styles/BarChart.css").toExternalForm());
        this.lineChart.getStylesheets().add(getClass().getResource("/styles/LineChart.css").toExternalForm());
        
        this.tactTimeField.setMaxMillis(Constants.TAKT_TIME_MAX_MILLIS);
        this.tactTimeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                return;
            }

            String text = this.tactTimeField.getText();
            if (Objects.nonNull(text) && Objects.nonNull(Constants.TAKT_TIME_MAX_MILLIS)
                    && (!StringTime.validStringTime(text) || StringTime.convertStringTimeToMillis(text) > Constants.TAKT_TIME_MAX_MILLIS)) {
                // 最大値(20日)より大きい場合
                final String minStringTime = "00:00:00";
                final String maxStringTime = StringTime.convertMillisToStringTime(Constants.TAKT_TIME_MAX_MILLIS);
                this.tactTimeField.setText(maxStringTime);

                // ヒントの表示
                tooltip.setText(String.format(LocaleUtils.getString(Locale.RANGE_THREE_DIGIT_HOUR), minStringTime, maxStringTime));
                tooltip.setStyle("-fx-font-size: 10pt; -fx-text-fill: black; -fx-background-color: #F8F0D7; -fx-background-radius: 0 0 0 0;");
                Bounds bounds = this.tactTimeField.localToScreen(this.tactTimeField.getBoundsInLocal());
                tooltip.show(this.tactTimeField, bounds.getMinX(), bounds.getMaxY() + 2.0);

                this.tactTimeField.requestFocus();
            } else {
                tooltip.setText("");
                tooltip.hide();
            }
        });


        this.blockUI(false);
    }

    /**
     * 対象工程のチェックが入っているものを対象に上から順番にチャートを描写する
     */
    private void drawChart() {
        // 工程選択リストからチェックが入っているものを取得
        Map<Integer, ConWorkflowWorkInfoEntity> map = this.workPaneController.getSelectedWork();

        // チャートのXの全項目のうちチェックが入っているもののみ抽出 (棒グラフ用)
        ObservableList<XYChart.Data<String, Number>> barFiltered = this.barChartDataAll.getData().filtered(new Predicate<XYChart.Data<String, Number>>() {
            @Override
            public boolean test(XYChart.Data<String, Number> data) {
                return map.entrySet().stream()
                        .anyMatch(o -> StringUtils.equals(createDisplayWorkName(o.getValue().getWorkName(), o.getValue().getWorkRev()), data.getXValue()));
            }
        });

        // チェックが入っている工程のみ、工程選択リストの順番でシリーズに格納 (棒グラフ用)
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName(LocaleUtils.getString("key.Average"));
        map.entrySet().stream().forEach(o -> {
            for (XYChart.Data<String, Number> data : barFiltered) {
                if (StringUtils.equals(createDisplayWorkName(o.getValue().getWorkName(), o.getValue().getWorkRev()), data.getXValue())) {
                    barSeries.getData().add(data);
                    break;
                }
            }
        });

        this.barChart.getData().clear();
        this.barChart.layout();
        this.barChart.getData().addAll(barSeries);
        
        // チャートのXの全項目のうちチェックが入っているもののみ抽出 (折れ線グラフ用)
        ObservableList<XYChart.Data<String, Number>> lineFiltered = this.lineChartDataAll.getData().filtered(new Predicate<XYChart.Data<String, Number>>() {
            @Override
            public boolean test(XYChart.Data<String, Number> data) {
                return map.entrySet().stream()
                        .anyMatch(o -> StringUtils.equals(createDisplayWorkName(o.getValue().getWorkName(), o.getValue().getWorkRev()), data.getXValue()));
            }
        });

        // チェックが入っている工程のみ、工程選択リストの順番でシリーズに格納 (折れ線グラフ用)
        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        lineSeries.setName(LocaleUtils.getString("key.deviation"));
        map.entrySet().stream().forEach(o -> {
            for (XYChart.Data<String, Number> data : lineFiltered) {
                if (StringUtils.equals(createDisplayWorkName(o.getValue().getWorkName(), o.getValue().getWorkRev()), data.getXValue())) {
                    lineSeries.getData().add(data);
                    break;
                }
            }
        });

        this.lineChart.getData().clear();
        this.lineChart.layout();
        this.lineChart.getData().addAll(lineSeries);
        
        stackPane.getChildren().clear();
        stackPane.getChildren().addAll(this.barChart, this.lineChart);
        updateChartPositionAndSize();

        //サマリー表示 平均を再計算する
        if (Objects.nonNull(summaryData)) {
            double avg = barFiltered.stream()
                    .mapToDouble(data -> data.getYValue().doubleValue())
                    .average()
                    .orElse(0.0);

            summaryData.stream()
                    .filter(item -> item.getType() == SummaryTypeEnum.AVG_WORK_TIME)
                    .forEach(item -> {
                        item.setValue(avg);
                    });

            // 標準偏差
            final double sigma = Math.sqrt(barFiltered.stream()
                    .mapToDouble(data -> data.getYValue().doubleValue())
                    .reduce(0, (result, element)-> result + (element-avg)*(element-avg))/barFiltered.size());
            summaryData.stream()
                    .filter(item -> item.getType() == SummaryTypeEnum.STDDEV_WORK_TIME)
                    .forEach(item -> {
                        item.setValue(sigma);
                    });
            summaryView.refresh();
        }
    }
    
    /**
     * グラフの表示位置とサイズを更新する。
     */
    private void updateChartPositionAndSize() {
        Platform.runLater(() -> {
            Bounds bounds = chartPane.getViewportBounds();
        
            double width = bounds.getWidth();
            double height = bounds.getHeight();
            double yAxisWidth = barChart.getYAxis().getWidth();

            barChart.setLayoutX(0);
            barChart.setTranslateX(0);
            barChart.setMinWidth(width - yAxisWidth);
            barChart.setMinHeight(height);

            lineChart.setLayoutX(0);
            lineChart.setTranslateX(yAxisWidth);
            lineChart.setMinWidth(width - yAxisWidth);
            lineChart.setMinHeight(height);

            chartPane.setMinWidth(width + yAxisWidth);
            chartPane.setMinHeight(height);
        });
    }

    /**
     * チャートデータを更新する。
     */
    private synchronized void updateChart() {
        try {
            logger.info("updateChart start.");

            // タクトタイム
            if (Objects.nonNull(this.marker)) {
                this.barChart.removeMarker(this.marker);
            }

            this.marker = new XYChart.Data<>(LocaleUtils.getString("key.TactTime"), StringTime.convertStringTimeToMillis(this.tactTimeField.getText()));
            this.barChart.addMarker(this.marker, LocaleUtils.getString("key.TactTime"));
            this.barChart.bindMarkerVisible(this.taktTimeButton.selectedProperty());

            // 開始終了日時
            Date fromDate = this.datePaneController.getFrom();
            Date toDate = this.datePaneController.getTo();

            // 工程順ID
            if (Objects.isNull(this.workflowPaneController.getSelectedWorkflow())) {
                return;
            }
            Long workflowId = this.workflowPaneController.getSelectedWorkflow().getWorkflowId();

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        blockUI(true);

                        // データ構築
                        ProductionSummaryInfoEntity summary = chartFacade.getProductionWork(workflowId, fromDate, toDate);

                        barChartDataAll = new XYChart.Series<>();
                        lineChartDataAll = new XYChart.Series<>();
                        summary.getWork().stream().forEach(entity -> {
                            barChartDataAll.getData().add(new XYChart.Data<>(entity.getDisplayWorkName(), entity.getAvgWorkTime(), entity));
                            lineChartDataAll.getData().add(new XYChart.Data<>(entity.getDisplayWorkName(), entity.getStandardDeviation(), entity));
                        });

                        Platform.runLater(() -> {
                            // サマリー表示
                            summaryData = summary.getSummaryItems();
                            summaryView.setItems(FXCollections.observableArrayList(summaryData));

                            // 平均作業時間はdoubleなので時間表示に変換する
                            barYAxis.setTickLabelFormatter(new StringConverter<Number>() {
                                @Override
                                public String toString(Number number) {
                                    return DateUtils.format(Constants.FORMAT_HHMMSS, (long) number.doubleValue());
                                }

                                @Override
                                public Number fromString(String s) {
                                    return 0;
                                }
                            });

                            barChart.getData().clear();
                            barChart.getData().addAll(barChartDataAll);

                            // マウスオーバーによるツールチップ表示 (棒グラフ)
                            for (XYChart.Series<String, Number> series : barChart.getData()) {
                                for (XYChart.Data<String, Number> data : series.getData()) {
                                    addTooltipEvent(data);
                                }
                            }

                            // 平均作業時間はdoubleなので時間表示に変換する
                            lineYAxis.setTickLabelFormatter(new StringConverter<Number>() {
                                @Override
                                public String toString(Number number) {
                                    return DateUtils.format(Constants.FORMAT_HHMMSS, (long) number.doubleValue());
                                }

                                @Override
                                public Number fromString(String s) {
                                    return 0;
                                }
                            });
                            
                            lineChart.getData().clear();
                            lineChart.getData().addAll(lineChartDataAll);

                            // マウスオーバーによるツールチップ表示 (折れ線グラフ)
                            for (XYChart.Series<String, Number> series : lineChart.getData()) {
                                for (XYChart.Data<String, Number> data : series.getData()) {
                                    addTooltipEvent(data);
                                }
                            }

                            // 表示条件に従いチャートを再構築
                            drawChart();
                        });
                    } finally {
                        blockUI(false);
                    }
                    return null;
                }
            };

            new Thread(task).start();

        } finally {
            logger.info("updateChart end.");
        }
    }

    /**
     * ツールチップイベントを追加する。
     * 
     * @param seriesData Seriesデータ
     */
    private void addTooltipEvent(XYChart.Data<String, Number> seriesData) {
        WorkSummaryInfoEntity entity = (WorkSummaryInfoEntity) seriesData.getExtraValue();
        if (Objects.nonNull(entity)) {
            try {
                Tooltip toolTip = TooltipBuilder.build(seriesData.getNode());

                seriesData.getNode().setOnMouseEntered(event -> {
                    seriesData.getNode().getStyleClass().add("onHover");
                    if (Objects.isNull(toolTip.getGraphic())) {
                        String crlf = System.getProperty("line.separator");

                        HBox hBox = new HBox();
                        Label label1 = new Label();
                        label1.setStyle("-fx-background-color:transparent; -fx-padding: 0 8 0 8; -fx-border-color: white transparent white white;");
                        Label label2 = new Label();
                        label2.setStyle("-fx-background-color:transparent; -fx-padding: 0 8 0 8; -fx-border-color: white;");

                        StringBuilder sb1 = new StringBuilder();
                        sb1.append(LocaleUtils.getString("key.Process"));
                        sb1.append(crlf);
                        sb1.append(LocaleUtils.getString("key.AverageWorkTime"));
                        sb1.append(crlf);
                        sb1.append(LocaleUtils.getString("key.itemsStandardDeviation"));
                        sb1.append(crlf);

                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(entity.getDisplayWorkName());
                        sb2.append(crlf);
                        sb2.append(DateUtils.formatTaktTime(entity.getAvgWorkTime().longValue()));
                        sb2.append(crlf);
                        sb2.append(DateUtils.formatTaktTime(entity.getStandardDeviation().longValue()));
                        sb2.append(crlf);

                        label1.setText(sb1.toString());
                        label2.setText(sb2.toString());
                        hBox.getChildren().addAll(label1, label2);
                        toolTip.setGraphic(hBox);
                    }
                });

                seriesData.getNode().setOnMouseExited(event -> seriesData.getNode().getStyleClass().remove("onHover"));
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    /**
     * 更新
     *
     * @param event
     */
    @FXML
    public void onUpdate(ActionEvent event) {
        try {
            logger.info("onUpdate start.");

            this.updateChart();
        } finally {
            logger.info("onUpdate end.");
        }
    }

    /**
     * CSV出力
     *
     * @param event
     */
    @FXML
    public void onExport(ActionEvent event) {
        try {
            logger.info("onExport start.");

            // 出力先取得
            Properties properties = AdProperty.getProperties(Constants.PROPETRY_NAME);
            String path = properties.getProperty(Constants.EXPORT_DIRECTORY);
            String pattern = properties.getProperty(Constants.EXPORT_FILENAME_PATTERN_WORK);

            // 設定ファイルにデフォルトファイル名の項目を作成しておく(インストール時に作業分析向け設定ファイルが作成されないため)
            if (Objects.isNull(pattern)) {
                properties.setProperty(Constants.EXPORT_FILENAME_PATTERN_WORK, "");
            }

            FileChooser fileChooser = new FileChooser();

            if (!StringUtils.isEmpty(path)) {
                File dir = new File(path);
                if (dir.exists()) {
                    fileChooser.setInitialDirectory(dir);
                }
            }

            if (Objects.isNull(fileChooser.getInitialDirectory())) {
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home"), "Desktop"));
            }

            if (!StringUtils.isEmpty(pattern)) {
                Date now = new Date();
                String fileName;
                try {
                    fileName = String.format(pattern, now, now, now, now, now, now);
                } catch (Exception ex) {
                    fileName = String.format(Constants.DEF_EXPORT_PATTERN_WORK, now, now, now, now, now, now);
                }
                fileChooser.setInitialFileName(fileName);
            }

            if (Objects.isNull(fileChooser.getInitialFileName())) {
                Date now = new Date();
                String fileName = String.format(Constants.DEF_EXPORT_PATTERN_WORK, now, now, now, now, now, now);
                fileChooser.setInitialFileName(fileName);
            }

            fileChooser.setTitle(LocaleUtils.getString("key.OutReportTitle"));
            FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("TSV files (*.tsv)", "*.tsv");
            fileChooser.getExtensionFilters().addAll(extFilter1, extFilter2);

            File file = fileChooser.showSaveDialog(sc.getWindow());
            if (Objects.isNull(file)) {
                return;
            }

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        blockUI(true);
                        properties.setProperty(Constants.EXPORT_DIRECTORY, file.getParent());

                        String charset = AdProperty.getProperties().getProperty(Constants.EXPORT_CHARSET).toUpperCase();
                        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS").contains(charset)) {
                            charset = "MS932";
                        }

                        final Character separator = (FilenameUtils.getExtension(file.getPath()).equals("tsv")) ? '\t' : ',';
                        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset))) {

                            StringBuilder title = new StringBuilder();
                            title.append(LocaleUtils.getString("key.Process"));
                            title.append(separator);
                            title.append(LocaleUtils.getString("key.AverageWorkTime"));
                            title.append(separator);
                            title.append(LocaleUtils.getString("key.itemsStandardDeviation"));
                            title.append(separator);
                            writer.write(title.toString());
                            writer.newLine();

                            for (XYChart.Series<String, Number> series : barChart.getData()) {
                                for (XYChart.Data<String, Number> data : series.getData()) {
                                    WorkSummaryInfoEntity entity = (WorkSummaryInfoEntity) data.getExtraValue();

                                    StringBuilder sb = new StringBuilder();
                                    sb.append(entity.getDisplayWorkName());
                                    sb.append(separator);
                                    sb.append(entity.getAvgWorkTime());
                                    sb.append(separator);
                                    sb.append(entity.getStandardDeviation());
                                    sb.append(separator);
                                    writer.write(sb.toString());
                                    writer.newLine();
                                }
                            }
                        }

                        blockUI(false);

                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(LocaleUtils.getString("key.Result"));
                            alert.setHeaderText(LocaleUtils.getString("key.FileOutputCompleted"));
                            alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(LocaleUtils.getString("key.FileName") + ": " + file.getName())));
                            alert.showAndWait();
                        });

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);

                        blockUI(false);

                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(LocaleUtils.getString("key.Result"));
                            alert.setHeaderText(LocaleUtils.getString("key.FileOutputErrorOccured"));
                            alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(LocaleUtils.getString("key.ErrorDetail") + ": " + ex.getLocalizedMessage())));
                            alert.showAndWait();
                        });
                    }
                    return null;
                }
            };

            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onExport end.");
        }
    }

    /**
     * コンポーネントが破棄される前に呼び出される。
     *
     * @return
     */
    @Override
    public boolean destoryComponent() {
        try {
            logger.info("destoryComponent start.");

            SplitPaneUtils.saveDividerPosition(workSummaryPane, getClass().getSimpleName());

            this.seveProperties();

            return true;
        } finally {
            logger.info("destoryComponent end.");
        }
    }

    /**
     * プロパティを読み込む。
     */
    private void loadProperties() {
        try {
            logger.info("loadProperties start.");

            AdProperty.load(Constants.PROPETRY_NAME, Constants.PROPETRY_NAME + ".properties");
            Properties properties = AdProperty.getProperties(Constants.PROPETRY_NAME);

            logger.info("Properties: " + properties.toString());

            this.datePaneController.loadProperties(properties);
            this.workflowPaneController.loadProperties(properties);
            this.workPaneController.loadProperties(properties, workflowPaneController.getSelectedWorkflow());

            String value = properties.getProperty(Constants.TIMELINE_TAKT_TIME);
            if (!StringUtils.isEmpty(value)) {
                this.tactTimeField.setText(value);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("loadProperties end.");
        }
    }

    /**
     * プロパティを保存する。
     */
    private void seveProperties() {
        try {
            logger.info("seveProperties start.");

            Properties properties = AdProperty.getProperties(Constants.PROPETRY_NAME);

            this.datePaneController.saveProperties(properties);
            this.workPaneController.saveProperties(properties);
            this.workflowPaneController.saveProperties(properties);

            properties.setProperty(Constants.TIMELINE_TAKT_TIME, this.tactTimeField.getText());

            AdProperty.store(Constants.PROPETRY_NAME);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("seveProperties end.");
        }
    }

    /**
     * 操作をロックする
     *
     * @param block
     */
    private void blockUI(Boolean block) {
        this.exportButton.setDisable(this.barChartDataAll.getData().isEmpty() ? true : block);
        sc.blockUI("ContentNaviPane", block);
        this.progressPane.setVisible(block);
    }

    /**
     * 画像ファイル出力ボタンのアクション
     *
     * @param event
     */
    @FXML
    public void onOutputImageFile(ActionEvent event) {
        logger.info("onOutputImageFile");
        try {
            // UIブロックの前にイメージを取得する。
            BufferedImage image = getChartImage();
            if (Objects.isNull(image)) {
                return;
            }

            blockUI(true);

            Node node = (Node) event.getSource();

            File desktopDir = new File(System.getProperty("user.home"), "Desktop");

            // 出力ファイルのベース名を取得する。
            String fileName = FilenameUtils.getBaseName(this.getExportFileName());

            // ファイル保存ダイアログを表示する。
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(desktopDir);
            chooser.setInitialFileName(fileName);
            chooser.setTitle(LocaleUtils.getString("key.OutputImageFile"));

            FileChooser.ExtensionFilter ext1 = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
            FileChooser.ExtensionFilter ext2 = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
            FileChooser.ExtensionFilter ext3 = new FileChooser.ExtensionFilter("BMP files (*.bmp)", "*.bmp");
            FileChooser.ExtensionFilter ext4 = new FileChooser.ExtensionFilter("GIF files (*.gif)", "*.gif");
            chooser.getExtensionFilters().addAll(ext1, ext2, ext3, ext4);

            File file = chooser.showSaveDialog(node.getScene().getWindow());
            if (Objects.isNull(file)) {
                return;
            }

            // 画像ファイルを出力する。
            if (this.outputImageFile(file, image)) {
                sc.showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.OutputImageFile"), LocaleUtils.getString("key.FileOutputCompleted"));
            } else {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.OutputImageFile"), LocaleUtils.getString("key.FileOutputErrorOccured"));
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }


    /**
     * チャート画像を取得する。
     *
     * @return チャート画像
     */
    private BufferedImage getChartImage() {
        BufferedImage chartImage = null;
        try {
            WritableImage wi = chartPane.snapshot(new SnapshotParameters(), null);
            BufferedImage bi = SwingFXUtils.fromFXImage(wi, null);

            chartImage = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);

            Graphics grap = chartImage.getGraphics();
            grap.drawImage(bi, 0, 0, null);
            grap.dispose();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return chartImage;
    }

    /**
     * 画像ファイルを出力する。
     *
     * @param file ファイルパス
     * @param image 出力する画像
     * @return 結果 (true:成功, false:失敗)
     */
    private boolean outputImageFile(File file, BufferedImage image) {
        boolean result = false;
        try {
            String fmt =  FilenameUtils.getExtension(file.getPath());

            result = ImageIO.write(image, fmt, file);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 出力ファイル名を取得する。
     *
     * @return
     */
    private String getExportFileName() {
        String result = null;
        try {
            Properties properties = AdProperty.getProperties(Constants.PROPETRY_NAME);
            String pattern = properties.getProperty(Constants.EXPORT_FILENAME_PATTERN_WORK);

            // 設定ファイルにデフォルトファイル名の項目を作成しておく(インストール時に作業分析向け設定ファイルが作成されないため)
            if (Objects.isNull(pattern) || pattern.isEmpty()) {
                pattern = Constants.DEF_EXPORT_PATTERN_WORK;
                properties.setProperty(Constants.EXPORT_FILENAME_PATTERN_WORK, pattern);
            }

            if (!StringUtils.isEmpty(pattern)) {
                Date now = new Date();
                try {
                    result = String.format(pattern, now, now, now, now, now, now);
                } catch (Exception ex) {
                    result = String.format(Constants.DEF_EXPORT_PATTERN_WORK, now, now, now, now, now, now);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }

    /**
     * 工程の表示名を作成する。
     *
     * @param workName 工程名
     * @param workRev 版数
     * @return 表示名(工程名 : 版数)
     */
    private String createDisplayWorkName(String workName, Integer workRev) {
        StringBuilder name = new StringBuilder(workName);
        if (Objects.nonNull(workRev)) {
            name.append(" : ").append(workRev);
        }
        return name.toString();
    }
}
