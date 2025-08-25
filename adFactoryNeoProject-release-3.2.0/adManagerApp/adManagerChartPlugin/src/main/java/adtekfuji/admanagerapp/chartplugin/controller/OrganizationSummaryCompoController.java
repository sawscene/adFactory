/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.chartplugin.controller;

import adtekfuji.admanagerapp.chartplugin.common.Constants;
import adtekfuji.clientservice.ChartFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
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
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
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
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.imageio.ImageIO;
import jp.adtekfuji.adFactory.entity.chart.OrganizationSummaryInfoEntity;
import jp.adtekfuji.adFactory.entity.chart.ProductionSummaryInfoEntity;
import jp.adtekfuji.adFactory.entity.chart.SummaryItem;
import jp.adtekfuji.adFactory.entity.chart.SummaryTypeEnum;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import jp.adtekfuji.javafxcommon.utils.Selection;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業者の平均作業時間グラフ画面のコントローラー
 *
 * @author s-heya
 */
@FxComponent(id = "ChartOrganizationSummaryCompo", fxmlPath = "/fxml/chartplugin/OrganizationSummaryCompo.fxml")
public class OrganizationSummaryCompoController implements Initializable, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");;

    @FXML
    private SplitPane organizationSummaryPane;

    @FXML
    private DatePane datePaneController;

    @FXML
    private WorkflowPane workflowPaneController;

    @FXML
    private WorkPane workPaneController;

    @FXML
    private Button exportButton;

    @FXML
    private PropertySaveTableView<SummaryItem> summaryView;
    @FXML
    private TableColumn<SummaryItem, String> summaryNameColumn;
    @FXML
    private TableColumn<SummaryItem, String> summaryValueColumn;

    @FXML
    private FlowPane chartPane;

    private final ChartFacade chartFacade = new ChartFacade();

    /**
     * 棒グラフ用のSeriesマップ
     */
    private Map<String, XYChart.Series<String, Number>> seriesBarChartMap;
    
    /**
     * 折れ線グラフ用のSeriesマップ
     */
    private Map<String, XYChart.Series<String, Number>> seriesLineChartMap;

    //組織データ
    private Map<String, List<OrganizationSummaryInfoEntity>> grouping;

    //サマリーデータ
    private List<SummaryItem> summaryData;
    @FXML
    private Pane progressPane;

    /**
     * 作業者の平均作業時間グラフ画面の初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        summaryView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        SplitPaneUtils.loadDividerPosition(organizationSummaryPane, getClass().getSimpleName());

        // サマリー表示設定
        this.summaryView.init("ChartOrganization" + "summaryView");

        this.workflowPaneController.addListener((observable, oldValue, newValue) -> {
            this.workPaneController.create(this.workflowPaneController.getSelectedWorkflow());
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
            return new ReadOnlyObjectWrapper<String>(column.getValue().getType() == SummaryTypeEnum.AVG_WORK_TIME  || column.getValue().getType() == SummaryTypeEnum.STDDEV_WORK_TIME
                    ? DateUtils.formatTaktTime(column.getValue().getValue().longValue())
                    : column.getValue().getValue().toString());
        });


        this.workPaneController.addCheckBoxChangeListener((observable, oldValue, newValue) -> {
            drawChart();
        });

        // プロパティを読み込む
        this.loadProperties();

        //工程リスト入れ替え時の再描写
        this.workPaneController.setMoveListener(e -> {
            drawChart();
        });

        this.blockUI(false);
    }

    /**
     * チャートを再描写する
     */
    private void drawChart() {
        ObservableList<Selection<ConWorkflowWorkInfoEntity>> selections = workPaneController.getWorkSelections();
        ObservableList<Node> newCharts = FXCollections.observableArrayList();

        //すでに作成しているチャートらに対してリストの順番となるよう順番のみ入れ替える
        for (Selection<ConWorkflowWorkInfoEntity> sel : selections) {
            for (Node node : chartPane.getChildren()) {
                VBox vbox = (VBox) node;
                String labelName = ((Label) vbox.getChildren().get(0)).getText();

                if (Objects.equals(sel.getName(), labelName)) {
                    newCharts.add(vbox);
                }
            }
        }

        chartPane.getChildren().clear();
        chartPane.getChildren().addAll(newCharts);

        // 標準偏差
        if(Objects.nonNull(grouping)) {
            List<Double> orgAvgWorkTimes = selections.stream()
                    .filter(Selection::isSelected)
                    .map(Selection::getName)
                    .map(grouping::get)
                    .flatMap(Collection::stream)
                    .map(OrganizationSummaryInfoEntity::getAvgWorkTime)
                    .collect(Collectors.toList());

            final Double avg = orgAvgWorkTimes.stream()
                    .mapToDouble(l->l)
                    .average()
                    .orElse(0.0);

            summaryData.stream()
                    .filter(item -> item.getType() == SummaryTypeEnum.AVG_WORK_TIME)
                    .forEach(item -> {
                        item.setValue(avg);
                    });

            final double sigma = Math.sqrt(orgAvgWorkTimes.stream()
                    .mapToDouble(l->l)
                    .reduce(0, (result, element) -> result + (element - avg) * (element - avg)) / orgAvgWorkTimes.size());

            summaryData.stream()
                    .filter(item -> item.getType() == SummaryTypeEnum.STDDEV_WORK_TIME)
                    .forEach(item -> {
                        item.setValue(sigma);
                    });

            summaryView.refresh();

        }
    }

    /**
     * チャートデータを更新する。
     */
    private synchronized void updateChart() {
        try {
            logger.info("updateChart start.");

            // 日時取得
            Date fromDate = datePaneController.getFrom();
            Date toDate = datePaneController.getTo();

            // 工程順ID取得
            if (Objects.isNull(workflowPaneController.getSelectedWorkflow())) {
                return;
            }
            Long workflowId = workflowPaneController.getSelectedWorkflow().getWorkflowId();

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        blockUI(true);

                        // データ構築
                        ProductionSummaryInfoEntity summary = chartFacade.getProductionOrganization(workflowId, fromDate, toDate);
                        grouping = summary.getOrganization().stream().collect(Collectors.groupingBy(p -> p.getDisplayWorkName()));

                        seriesBarChartMap = new HashMap<>();
                        seriesLineChartMap = new HashMap<>();

                        grouping.entrySet().stream().forEach(o -> {
                            XYChart.Series<String, Number> seriesBarChart = new XYChart.Series<>();
                            seriesBarChart.setName(LocaleUtils.getString("key.Average"));
                            XYChart.Series<String, Number> seriesLineChart = new XYChart.Series<>();
                            seriesLineChart.setName(LocaleUtils.getString("key.deviation"));
                            o.getValue().stream().forEach((entity) -> {
                                seriesBarChart.getData().add(new XYChart.Data<>(entity.getOrganizationName(), entity.getAvgWorkTime(), entity));
                                seriesLineChart.getData().add(new XYChart.Data<>(entity.getOrganizationName(), entity.getStandardDeviation(), entity));
                            });

                            seriesBarChartMap.put(o.getKey(), seriesBarChart);
                            seriesLineChartMap.put(o.getKey(), seriesLineChart);
                        });

                        Platform.runLater(() -> {
                            summaryData = summary.getSummaryItems();
                            // サマリー表示
                            summaryView.setItems(FXCollections.observableArrayList(summaryData));

                            // 工程ごとにグラフを作成
                            chartPane.getChildren().clear();
                            seriesBarChartMap.entrySet().stream().sorted(java.util.Map.Entry.comparingByKey()).forEach(m -> {
                                // チャート作成
                                CategoryAxis barXAxis = new CategoryAxis();
                                NumberAxis barYAxis = new NumberAxis();
                                barYAxis.setLabel(LocaleUtils.getString("key.AverageWorkTime"));

                                // 平均作業時間はdoubleなので時間表示に変換する
                                barYAxis.setTickLabelFormatter(new StringConverter<Number>() {
                                    @Override
                                    public String toString(Number number) {
                                        return DateUtils.formatTaktTime((long) number.doubleValue());
                                    }

                                    @Override
                                    public Number fromString(String s) {
                                        return 0;
                                    }
                                });

                                BarChart<String, Number> barChart = new BarChart<>(barXAxis, barYAxis);
                                barChart.getStylesheets().add(getClass().getResource("/styles/BarChart.css").toExternalForm());
                                barChart.setAnimated(false);
                                barChart.getData().clear();
                                barChart.layout();
                                barChart.getData().addAll(m.getValue());
                                barChart.setUserData(true);
                                barChart.setCategoryGap(2.0);
                                
                                // チャート作成 (折れ線グラフ)
                                CategoryAxis lineXAxis = new CategoryAxis();
                                NumberAxis lineYAxis = new NumberAxis();
                                lineYAxis.setLabel(LocaleUtils.getString("key.itemsStandardDeviation"));

                                // 平均作業時間はdoubleなので時間表示に変換する
                                lineYAxis.setTickLabelFormatter(new StringConverter<Number>() {
                                    @Override
                                    public String toString(Number number) {
                                        return DateUtils.formatTaktTime((long) number.doubleValue());
                                    }

                                    @Override
                                    public Number fromString(String s) {
                                        return 0;
                                    }
                                });
                                
                                lineYAxis.setSide(Side.RIGHT);
                                LineChart<String, Number> lineChart = new LineChart<>(lineXAxis, lineYAxis);
                                lineChart.getStylesheets().add(getClass().getResource("/styles/LineChart.css").toExternalForm());
                                lineChart.setAnimated(false);
                                lineChart.setHorizontalGridLinesVisible(false);
                                lineChart.setVerticalGridLinesVisible(false);
                                lineChart.getXAxis().setOpacity(0.0);
                                lineChart.getData().clear();
                                lineChart.layout();
                                seriesLineChartMap.entrySet().stream().filter(f -> f.getKey().equals(m.getKey())).forEach(lineMap -> {
                                    lineChart.getData().addAll(lineMap.getValue());
                                });
                                lineChart.setUserData(true);

                                StackPane stackPane = new StackPane();
                                stackPane.getChildren().addAll(barChart, lineChart);
                                
                                ScrollPane scrollPane = new ScrollPane();
                                scrollPane.layout();
                                scrollPane.setContent(stackPane);

                                scrollPane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
                                    if ((boolean) barChart.getUserData()) {
                                        updateChartPositionAndSize(scrollPane, barChart, lineChart);
                                        scrollPane.setPannable((barChart.getPrefWidth() > newBounds.getWidth()) || (barChart.getPrefHeight() > newBounds.getHeight()));
                                    }
                                });

                                barChart.widthProperty().addListener((observable, oldValue, newValue) -> {
                                    double hValue = scrollPane.getHvalue() * (newValue.doubleValue() / oldValue.doubleValue());
                                    if (hValue > scrollPane.getHmin() && hValue < scrollPane.getHmax()) {
                                        scrollPane.setHvalue(hValue);
                                    }
                                });

                                barChart.heightProperty().addListener((observable, oldValue, newValue) -> {
                                    double vValue = scrollPane.getVvalue() * (newValue.doubleValue() / oldValue.doubleValue());
                                    if (vValue > scrollPane.getVmin() && vValue < scrollPane.getVmax()) {
                                        scrollPane.setVvalue(vValue);
                                    }
                                });

                                // チャート上でマウススクロール
                                barChart.setOnScroll(event -> {
                                    double zoomFactor = 1.2;
                                    barChart.setUserData(false);

                                    if (event.isShiftDown()) {
                                        // 水平方向をズーム
                                        if (event.getDeltaX() < 0) {
                                            zoomFactor = 2 - zoomFactor;
                                        }
                                        barChart.setMinWidth(barChart.getWidth() * zoomFactor);
                                        lineChart.setMinWidth(lineChart.getWidth() * zoomFactor);
                                    } else {
                                        // 垂直方向をズーム
                                        if (event.getDeltaY() < 0) {
                                            zoomFactor = 2 - zoomFactor;
                                        }
                                        barChart.setMinHeight(barChart.getHeight() * zoomFactor);
                                        lineChart.setMinHeight(lineChart.getHeight() * zoomFactor);
                                    }

                                    event.consume();
                                });

                                // チャート上でマウスクリック
                                barChart.setOnMouseClicked(event -> {
                                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                                        // チャートをパン
                                        barChart.setUserData(true);
                                        updateChartPositionAndSize(scrollPane, barChart, lineChart);
                                    }
                                });
                                
                                // 折れ線グラフのマウスイベントは棒グラフへに受け渡す
                                lineChart.addEventHandler(Event.ANY, event -> {
                                    if (event instanceof MouseEvent) {
                                        lineChart.setMouseTransparent(true);
                                    }
                                    event.consume();
                                });

                                // 工程名
                                Label label = new Label(m.getKey());

                                // 工程名＋チャート
                                VBox vbox = new VBox();
                                vbox.setAlignment(Pos.CENTER);
                                vbox.setStyle("-fx-background-color: white; -fx-border-color: gray;");
                                vbox.getChildren().addAll(label, scrollPane);

                                // 工程名のチェックとの紐づけ
                                workPaneController.getWorkSelection(m.getKey()).ifPresent(selection -> {
                                    vbox.visibleProperty().bind(selection.selectedProperty());
                                    vbox.managedProperty().bind(selection.selectedProperty());
                                });

                                chartPane.getChildren().add(vbox);

                                // マウスオーバーによるツールチップ表示 (棒グラフ)
                                for (XYChart.Series<String, Number> series : barChart.getData()) {
                                    for (XYChart.Data<String, Number> data : series.getData()) {
                                        addTooltipEvent(data);
                                    }
                                }

                                // マウスオーバーによるツールチップ表示 (折れ線グラフ)
                                for (XYChart.Series<String, Number> series : lineChart.getData()) {
                                    for (XYChart.Data<String, Number> data : series.getData()) {
                                        addTooltipEvent(data);
                                    }
                                }

                                // 表示条件に従ってチャートを再表示
                                drawChart();
                            });
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
     * グラフの表示位置とサイズを更新する。
     * 
     * @param scrollPane グラフの親ノードのスクロールペイン
     * @param barChart 棒グラフ
     * @param lineChart 折れ線グラフ
     */
    private void updateChartPositionAndSize(ScrollPane scrollPane, BarChart<String, Number> barChart, LineChart<String, Number> lineChart) {
        Platform.runLater(() -> {
            double width = barChart.getWidth();
            double height = barChart.getHeight();
            double yAxisWidth = barChart.getYAxis().getWidth();

            barChart.setLayoutX(0);
            barChart.setTranslateX(0);
            barChart.setMinWidth(width - yAxisWidth);
            barChart.setMinHeight(height);

            lineChart.setLayoutX(0);
            lineChart.setTranslateX(yAxisWidth);
            lineChart.setMinWidth(width - yAxisWidth);
            lineChart.setMinHeight(height);

            scrollPane.setMinWidth(width + yAxisWidth);
            scrollPane.setMinHeight(height);
        });
    }

    /**
     * ツールチップイベントを追加する。
     * 
     * @param seriesData Seriesデータ
     */
    private void addTooltipEvent(XYChart.Data<String, Number> seriesData) {
        OrganizationSummaryInfoEntity entity = (OrganizationSummaryInfoEntity) seriesData.getExtraValue();
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
                        sb1.append(LocaleUtils.getString("key.WorkingParson"));
                        sb1.append(crlf);
                        sb1.append(LocaleUtils.getString("key.AverageWorkTime"));
                        sb1.append(crlf);
                        sb1.append(LocaleUtils.getString("key.itemsStandardDeviation"));
                        sb1.append(crlf);

                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(entity.getDisplayWorkName());
                        sb2.append(crlf);
                        sb2.append(entity.getOrganizationName());
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

            //出力先取得
            Properties properties = AdProperty.getProperties(Constants.PROPETRY_NAME);
            String path = properties.getProperty(Constants.EXPORT_DIRECTORY);
            String pattern = properties.getProperty(Constants.EXPORT_FILENAME_PATTERN_PERSON);

            // 設定ファイルにデフォルトファイル名の項目を作成しておく(インストール時に作業分析向け設定ファイルが作成されないため)
            if (Objects.isNull(pattern)) {
                properties.setProperty(Constants.EXPORT_FILENAME_PATTERN_PERSON, "");
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
                    fileName = String.format(Constants.DEF_EXPORT_PATTERN_PERSON, now, now, now, now, now, now);
                }
                fileChooser.setInitialFileName(fileName);
            }

            if (Objects.isNull(fileChooser.getInitialFileName())) {
                Date now = new Date();
                String fileName = String.format(Constants.DEF_EXPORT_PATTERN_PERSON, now, now, now, now, now, now);
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
                            title.append(LocaleUtils.getString("key.ProcessName"));
                            title.append(separator);
                            title.append(LocaleUtils.getString("key.WorkingParson"));
                            title.append(separator);
                            title.append(LocaleUtils.getString("key.AverageWorkTime"));
                            title.append(separator);
                            title.append(LocaleUtils.getString("key.itemsStandardDeviation"));
                            title.append(separator);
                            writer.write(title.toString());
                            writer.newLine();

                            for (BarChart<String, Number> barChart : chartPane.getChildren().stream()
                                    .map(node -> {
                                        ScrollPane scrollPane = (ScrollPane) ((VBox) node).getChildren().get(1);
                                        StackPane stackPane = (StackPane) scrollPane.getContent();
                                        return (BarChart) stackPane.getChildren().get(0);
                                    })
                                    .collect(Collectors.toList())) {
                                for (XYChart.Series<String, Number> series : barChart.getData()) {
                                    for (XYChart.Data<String, Number> data : series.getData()) {
                                        OrganizationSummaryInfoEntity entity = (OrganizationSummaryInfoEntity) data.getExtraValue();

                                        StringBuilder sb = new StringBuilder();
                                        sb.append(entity.getDisplayWorkName());
                                        sb.append(separator);
                                        sb.append(entity.getOrganizationName());
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
                        }

                        blockUI(false);

                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText(LocaleUtils.getString("key.FileOutputCompleted"));
                            alert.setTitle(LocaleUtils.getString("key.Result"));
                            alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(LocaleUtils.getString("key.FileName") + ": " + file.getName())));
                            alert.showAndWait();
                        });

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);

                        blockUI(false);

                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText(LocaleUtils.getString("key.FileOutputErrorOccured"));
                            alert.setTitle(LocaleUtils.getString("key.Result"));
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

            SplitPaneUtils.saveDividerPosition(organizationSummaryPane, getClass().getSimpleName());

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
            this.workPaneController.loadProperties(properties, this.workflowPaneController.getSelectedWorkflow());

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
        this.exportButton.setDisable(Objects.isNull(seriesBarChartMap) || seriesBarChartMap.isEmpty() ? true : block);
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
            String pattern = properties.getProperty(Constants.EXPORT_FILENAME_PATTERN_PERSON);

            // 設定ファイルにデフォルトファイル名の項目を作成しておく(インストール時に作業分析向け設定ファイルが作成されないため)
            if (Objects.isNull(pattern) || pattern.isEmpty()) {
                pattern = Constants.DEF_EXPORT_PATTERN_PERSON;
                properties.setProperty(Constants.EXPORT_FILENAME_PATTERN_PERSON, pattern);
            }

            if (!StringUtils.isEmpty(pattern)) {
                Date now = new Date();
                try {
                    result = String.format(pattern, now, now, now, now, now, now);
                } catch (Exception ex) {
                    result = String.format(Constants.DEF_EXPORT_PATTERN_PERSON, now, now, now, now, now, now);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }
}
