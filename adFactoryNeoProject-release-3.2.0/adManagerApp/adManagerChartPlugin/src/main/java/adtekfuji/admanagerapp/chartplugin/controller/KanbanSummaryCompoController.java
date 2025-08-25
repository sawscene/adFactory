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
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.imageio.ImageIO;
import jp.adtekfuji.adFactory.entity.chart.*;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバンの総作業時間グラフ画面のコントローラー
 *
 * @author s-heya
 */
@FxComponent(id = "ChartKanbanSummaryCompo", fxmlPath = "/fxml/chartplugin/KanbanSummaryCompo.fxml")
public class KanbanSummaryCompoController implements Initializable, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private boolean pannable = true;

    @FXML
    private SplitPane kanbanSummaryPane;

    @FXML
    private DatePane datePaneController;

    @FXML
    private WorkflowPane workflowPaneController;

    @FXML
    private Button exportButton;

    @FXML
    private PropertySaveTableView<SummaryItem> summaryView;
    @FXML
    private TableColumn<SummaryItem, String> summaryNameColumn;
    @FXML
    private TableColumn<SummaryItem, String> summaryValueColumn;

    @FXML
    private ScrollPane chartPane;

    /**
     * 積み上げ棒グラフ
     */
    @FXML
    private StackedBarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private final ChartFacade chartFacade = new ChartFacade();

    // チャートデータ
    private List<XYChart.Series<String, Number>> chartDataAll = new ArrayList<>();

    @FXML
    private Pane progressPane;

    /**
     * カンバンの総作業時間グラフ画面の初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        summaryView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        SplitPaneUtils.loadDividerPosition(kanbanSummaryPane, getClass().getSimpleName());

        // サマリー表示設定
        this.summaryView.init("ChartKanban" + "summaryView");

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
            return new ReadOnlyObjectWrapper<>(name);
        });

        List<SummaryTypeEnum> timeSummaryTypes = Arrays.asList(
                SummaryTypeEnum.AVG_WORK_TIME,
                SummaryTypeEnum.WORK_TIME_PER_UNIT
        );
        this.summaryValueColumn.setCellValueFactory(column -> {
            return new ReadOnlyObjectWrapper<>(timeSummaryTypes.contains(column.getValue().getType())
                    ? DateUtils.formatTaktTime(column.getValue().getValue().longValue())
                    : column.getValue().getValue().toString());
        });

        // プロパティを読み込む
        this.loadProperties();

        this.chartPane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            if (pannable) {
                barChart.setMinSize(Math.max(barChart.getPrefWidth(), newBounds.getWidth()), Math.max(barChart.getPrefHeight(), newBounds.getHeight()));
                chartPane.setPannable((barChart.getPrefWidth() > newBounds.getWidth()) || (barChart.getPrefHeight() > newBounds.getHeight()));
            }
        });

        // チャート上でマウススクロール
        this.barChart.setOnScroll(event -> {
            double zoomFactor = 1.2;
            pannable = false;
            if (event.isShiftDown()) {
                // 水平方向をズーム
                if (event.getDeltaX() < 0) {
                    zoomFactor = 2 - zoomFactor;
                }
                barChart.setMinWidth(barChart.getWidth() * zoomFactor);
            } else {
                // 垂直方向をズーム
                if (event.getDeltaY() < 0) {
                    zoomFactor = 2 - zoomFactor;
                }
                barChart.setMinHeight(barChart.getHeight() * zoomFactor);
            }
            event.consume();
        });

        // チャート上でマウスクリック
        this.barChart.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                // チャートをパン
                pannable = true;
                Bounds bounds = this.chartPane.getViewportBounds();
                barChart.setMinSize(Math.max(barChart.getPrefWidth(), bounds.getWidth()), Math.max(barChart.getPrefHeight(), bounds.getHeight()));
            }
        });

        this.blockUI(false);
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

                        //データ構築
                        ProductionSummaryInfoEntity summary = chartFacade.getProductionKanban(workflowId, fromDate, toDate);

                        // 工程毎にSeriesを生成して一致する工程のカンバンデータを追加する
                        // 重複を排除した工程名の一覧を取得
                        List<String> displayWorkNames = summary.getWorkKanban().stream()
                                .map(s -> s.getDisplayWorkName())
                                .distinct()
                                .sorted()
                                .collect(Collectors.toList());
                        
                        // 工程毎にSeriesを生成して一致する工程のカンバンデータを追加する
                        chartDataAll = new ArrayList<>();
                        for (String displayWorkName: displayWorkNames) {
                            XYChart.Series<String, Number> series = new XYChart.Series<>();
                            series.setName(displayWorkName);
                            
                            summary.getWorkKanban().stream()
                                    .filter(e -> e.getDisplayWorkName().equals(displayWorkName))
                                    .forEach(kanban -> {
                                        series.getData().add(new XYChart.Data<>(kanban.getKanbanName(), kanban.getWorkTimes(), kanban));
                                    });
                            chartDataAll.add(series);
                        }

                        Platform.runLater(() -> {
                            //サマリー表示
                            summaryView.setItems(FXCollections.observableArrayList(summary.getSummaryItems()));

                            //平均作業時間はdoubleなので時間表示に変換する
                            yAxis.setTickLabelFormatter(new StringConverter<Number>() {
                                @Override
                                public String toString(Number number) {
                                    return DateUtils.formatTaktTime((long) number.doubleValue());
                                }

                                @Override
                                public Number fromString(String s) {
                                    return 0;
                                }
                            });

                            barChart.setLegendSide(Side.RIGHT);
                            barChart.getData().clear();
                            barChart.getData().addAll(chartDataAll);

                            //マウスオーバーによるツールチップ表示
                            for (XYChart.Series<String, Number> series : barChart.getData()) {

                                for (XYChart.Data<String, Number> data : series.getData()) {

                                    WorkKanbanSummaryInfoEntity entity = (WorkKanbanSummaryInfoEntity) data.getExtraValue();
                                    if (Objects.nonNull(entity)) {
                                        
                                        try {
                                            Tooltip toolTip = TooltipBuilder.build(data.getNode());

                                            data.getNode().setOnMouseEntered(event -> {
                                                data.getNode().getStyleClass().add("onHover");
                                                if (Objects.isNull(toolTip.getGraphic())) {
                                                    String crlf = System.getProperty("line.separator");

                                                    HBox hBox = new HBox();
                                                    Label label1 = new Label();
                                                    label1.setStyle("-fx-background-color:transparent; -fx-padding: 0 8 0 8; -fx-border-color: white transparent white white;");
                                                    Label label2 = new Label();
                                                    label2.setStyle("-fx-background-color:transparent; -fx-padding: 0 8 0 8; -fx-border-color: white;");

                                                    StringBuilder sb1 = new StringBuilder();
                                                    sb1.append(LocaleUtils.getString("key.Kanban"));
                                                    sb1.append(crlf);
                                                    sb1.append(LocaleUtils.getString("key.Process"));
                                                    sb1.append(crlf);
                                                    sb1.append(LocaleUtils.getString("key.WorkTimes"));
                                                    sb1.append(crlf);

                                                    StringBuilder sb2 = new StringBuilder();
                                                    sb2.append(entity.getKanbanName());
                                                    sb2.append(crlf);
                                                    sb2.append(entity.getDisplayWorkName());
                                                    sb2.append(crlf);
                                                    sb2.append(DateUtils.formatTaktTime(entity.getWorkTimes()));
                                                    sb2.append(crlf);

                                                    label1.setText(sb1.toString());
                                                    label2.setText(sb2.toString());
                                                    hBox.getChildren().addAll(label1, label2);
                                                    toolTip.setGraphic(hBox);
                                                }
                                            });

                                            data.getNode().setOnMouseExited(event -> data.getNode().getStyleClass().remove("onHover"));
                                        } catch (Exception ex) {
                                            logger.fatal(ex, ex);
                                        }
                                    }
                                }
                            }
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
            String pattern = properties.getProperty(Constants.EXPORT_FILENAME_PATTERN_KANBAN);

            // 設定ファイルにデフォルトファイル名の項目を作成しておく(インストール時に作業分析向け設定ファイルが作成されないため)
            if (Objects.isNull(pattern)) {
                properties.setProperty(Constants.EXPORT_FILENAME_PATTERN_KANBAN, "");
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
                    fileName = String.format(Constants.DEF_EXPORT_PATTERN_KANBAN, now, now, now, now, now, now);
                }
                fileChooser.setInitialFileName(fileName);
            }

            if (Objects.isNull(fileChooser.getInitialFileName())) {
                Date now = new Date();
                String fileName = String.format(Constants.DEF_EXPORT_PATTERN_KANBAN, now, now, now, now, now, now);
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
                            title.append(LocaleUtils.getString("key.Kanban"));
                            title.append(separator);
                            title.append(LocaleUtils.getString("key.Process"));
                            title.append(separator);
                            title.append(LocaleUtils.getString("key.WorkTimes"));
                            title.append(separator);
                            writer.write(title.toString());
                            writer.newLine();

                            for (XYChart.Series<String, Number> series : barChart.getData()) {
                                for (XYChart.Data<String, Number> data : series.getData()) {
                                    WorkKanbanSummaryInfoEntity entity = (WorkKanbanSummaryInfoEntity) data.getExtraValue();

                                    StringBuilder sb = new StringBuilder();
                                    sb.append(entity.getKanbanName());
                                    sb.append(separator);
                                    sb.append(entity.getDisplayWorkName());
                                    sb.append(separator);
                                    sb.append(entity.getWorkTimes());
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

            SplitPaneUtils.saveDividerPosition(kanbanSummaryPane, getClass().getSimpleName());

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

            datePaneController.loadProperties(properties);
            workflowPaneController.loadProperties(properties);

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

            datePaneController.saveProperties(properties);
            workflowPaneController.saveProperties(properties);

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
        this.exportButton.setDisable(this.chartDataAll.isEmpty() ? true : block);
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
            String pattern = properties.getProperty(Constants.EXPORT_FILENAME_PATTERN_KANBAN);

            // 設定ファイルにデフォルトファイル名の項目を作成しておく(インストール時に作業分析向け設定ファイルが作成されないため)
            if (Objects.isNull(pattern) || pattern.isEmpty()) {
                pattern = Constants.DEF_EXPORT_PATTERN_KANBAN;
                properties.setProperty(Constants.EXPORT_FILENAME_PATTERN_KANBAN, pattern);
            }

            if (!StringUtils.isEmpty(pattern)) {
                Date now = new Date();
                try {
                    result = String.format(pattern, now, now, now, now, now, now);
                } catch (Exception ex) {
                    result = String.format(Constants.DEF_EXPORT_PATTERN_KANBAN, now, now, now, now, now, now);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return result;
    }
}
