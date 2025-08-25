/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.chartplugin.controller;

import adtekfuji.admanagerapp.chartplugin.common.Constants;
import adtekfuji.clientservice.ChartFacade;
import adtekfuji.clientservice.WorkflowHierarchyInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import com.sun.javafx.charts.Legend;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import javax.imageio.*;
import jp.adtekfuji.adFactory.entity.chart.ProductionSummaryInfoEntity;
import jp.adtekfuji.adFactory.entity.chart.SummaryItem;
import static jp.adtekfuji.adFactory.entity.chart.SummaryTypeEnum.SUSPEND;
import jp.adtekfuji.adFactory.entity.chart.TimeLineInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import static jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.COMPLETION;
import static jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum.WORKING;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;
import jp.adtekfuji.javafxcommon.controls.TimeHMTextField;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import jp.adtekfuji.javafxcommon.treecell.WorkflowHierarchyTreeCell;
import jp.adtekfuji.javafxcommon.utils.Selection;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gillius.jfxutils.chart.AxisConstraint;
import org.gillius.jfxutils.chart.AxisConstraintStrategies;
import org.gillius.jfxutils.chart.ChartPanManager;
import org.gillius.jfxutils.chart.ChartZoomManager;
import org.gillius.jfxutils.chart.StableTicksAxis;

/**
 * 作業分析グラフ画面のコントローラー
 *
 * @author s-heya
 */
@FxComponent(id = "ChartTimeLineCompo", fxmlPath = "/fxml/chartplugin/TimeLineCompo.fxml")
public class TimeLineCompoController implements Initializable, ComponentHandler, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private TreeItem<WorkflowHierarchyInfoEntity> rootItem = new TreeItem<>(new WorkflowHierarchyInfoEntity(0L, LocaleUtils.getString("key.OrderProcessesHierarch")));
    private final WorkflowHierarchyInfoFacade workflowHierarchyFacade = new WorkflowHierarchyInfoFacade();
    private final WorkflowInfoFacade workflowFacade = new WorkflowInfoFacade();
    private final ChartFacade chartFacade = new ChartFacade();
    private WorkflowInfoEntity selectedWorkflow;

    // 定期更新処理
    private final Timeline timeline = new Timeline();

    // チャートデータ
    private ObservableList<XYChart.Series<Number, Number>> seriesList = FXCollections.observableArrayList();
    private TimeLineTickFormatter formatter = null;
    private final List<ConWorkflowWorkInfoEntity> xData = new ArrayList<>();

    @FXML
    private SplitPane timeLinePane;

    @FXML
    private DatePane datePaneController;

    @FXML
    private TimeHMTextField fromTimeField;

    @FXML
    private TimeHMTextField toTimeField;

    @FXML
    private TextField intervalField;

    @FXML
    private TextField workflowField;

    @FXML
    private TreeView<WorkflowHierarchyInfoEntity> workflowView;

    @FXML
    private TableView<Selection<ConWorkflowWorkInfoEntity>> workView;
    @FXML
    private TableColumn<Selection<ConWorkflowWorkInfoEntity>, Boolean> selectedColumn;
    @FXML
    private TableColumn<Selection<ConWorkflowWorkInfoEntity>, Integer> indexColumn;
    @FXML
    private TableColumn<Selection<ConWorkflowWorkInfoEntity>, String> workNameColumn;

    @FXML
    private Button updateButton;
    @FXML
    private Button exportButton;
    @FXML
    private Button fullScreenButton;

    @FXML
    private ToggleButton autoUpdateButton;
    @FXML
    private ToggleButton seriesButton;
    @FXML
    private ToggleButton legendButton;
    @FXML
    private ToggleButton taktButton;
    @FXML
    private PropertySaveTableView<SummaryItem> summaryView;
    @FXML
    private TableColumn<SummaryItem, String> summaryNameColumn;
    @FXML
    private TableColumn<SummaryItem, Number> summaryValueColumn;
    @FXML
    private LineChart<Number, Number> chart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private StableTicksAxis yAxis;
    @FXML
    private StackPane chartPane;
    @FXML
    private Rectangle selectRect;

    @FXML
    private Pane progressPane;

    MainSceneController mainSceneController;

    private final PlanChart planChart = new PlanChart();

    /**
     * 与えられた引数を記録する
     *
     * @param
     */
    @Override
    public void setArgument(Object argument) {
        this.mainSceneController = (MainSceneController) argument;
    }

    /**
     * 対象工程リスト
     */
    ObservableList<Selection<ConWorkflowWorkInfoEntity>> observableList = FXCollections.observableArrayList(new Callback<Selection<ConWorkflowWorkInfoEntity>, Observable[]>() {
        @Override
        public Observable[] call(Selection<ConWorkflowWorkInfoEntity> param) {
            return new Observable[]{param.selectedProperty()};
        }
    });

    /**
     * 対象工程順ツリーのリスナー
     */
    private final ChangeListener<Boolean> changeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        expand(treeItem);
                        return null;
                    }
                };
                new Thread(task).start();
            }
        }
    };

    /**
     * カスタムセルファクトリを返す。
     *
     * @return
     */
    private Callback<TableColumn<Selection<ConWorkflowWorkInfoEntity>, Integer>, TableCell<Selection<ConWorkflowWorkInfoEntity>, Integer>> getCustomCellFactory() {
        return new Callback<TableColumn<Selection<ConWorkflowWorkInfoEntity>, Integer>, TableCell<Selection<ConWorkflowWorkInfoEntity>, Integer>>() {
            @Override
            public TableCell<Selection<ConWorkflowWorkInfoEntity>, Integer> call(TableColumn<Selection<ConWorkflowWorkInfoEntity>, Integer> param) {
                TableCell<Selection<ConWorkflowWorkInfoEntity>, Integer> cell = new TableCell<Selection<ConWorkflowWorkInfoEntity>, Integer>() {
                    @Override
                    public void updateItem(final Integer id, boolean empty) {
                        if (Objects.nonNull(id) && id != -1) {
                            this.setText(String.valueOf(id));
                        } else {
                            this.setText("");
                        }
                    }
                };
                return cell;
            }
        };
    }

    /**
     * 作業分析グラフ画面の初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert this.datePaneController.getFrom() != null : "fx:id=\"fromDatePicker\" was not injected.";
        assert this.datePaneController.getTo() != null : "fx:id=\"toDatePicker\" was not injected.";
        assert this.fromTimeField != null : "fx:id=\"fromTimeField\" was not injected.";
        assert this.toTimeField != null : "fx:id=\"toTimeField\" was not injected.";
        assert this.intervalField != null : "fx:id=\"intervalField\" was not injected.";
        assert this.workflowField != null : "fx:id=\"workflowField\" was not injected.";
        assert this.workflowView != null : "fx:id=\"workflowView\" was not injected.";
        assert this.workView != null : "fx:id=\"workView\" was not injected.";
        assert this.updateButton != null : "fx:id=\"updateButton\" was not injected.";
        assert this.exportButton != null : "fx:id=\"exportButton\" was not injected.";
        assert this.legendButton != null : "fx:id=\"legendButton\" was not injected.";
        assert this.autoUpdateButton != null : "fx:id=\"autoUpdateButton\" was not injected.";

        workView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));
        summaryView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        rootItem = new TreeItem<>(new WorkflowHierarchyInfoEntity(0L, LocaleUtils.getString("key.OrderProcessesHierarch")));

        SplitPaneUtils.loadDividerPosition(timeLinePane, getClass().getSimpleName());

        // サマリー表示設定
        this.summaryView.init("ChartTimeLine" + "summaryView");

        //計画チャート
        this.planChart.enableProperty().bindBidirectional(this.taktButton.selectedProperty());

        // 対象工程順
        this.workflowView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<WorkflowHierarchyInfoEntity>> observable, TreeItem<WorkflowHierarchyInfoEntity> oldValue, TreeItem<WorkflowHierarchyInfoEntity> newValue) -> {
            if (Objects.nonNull(newValue) && newValue.getValue().getWorkflowHierarchyId() != 0L) {
                WorkflowHierarchyInfoEntity workflowHierarchy = newValue.getValue();

                if (Objects.isNull(workflowHierarchy.getParentId())) {
                    WorkflowInfoEntity entity = workflowFacade.find(workflowHierarchy.getWorkflowHierarchyId());
                    if (Objects.nonNull(entity.getWorkflowId())) {
                        this.selectedWorkflow = entity;
                        this.createWorkView(selectedWorkflow);
                    } else {
                        this.selectedWorkflow = null;
                    }
                    this.workflowField.setText(workflowHierarchy.getHierarchyName());
                }

            }
        });

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                createWorkflowTree();
                return null;
            }
        };
        new Thread(task).start();

        // 対象工程
        CheckBox checkBox = new CheckBox();
        checkBox.setOnAction((ActionEvent event) -> {
            if (checkBox.isSelected()) {
                for (Selection<ConWorkflowWorkInfoEntity> row : this.workView.getItems()) {
                    row.setSelected(Boolean.TRUE);
                }
            } else {
                for (Selection<ConWorkflowWorkInfoEntity> row : this.workView.getItems()) {
                    row.setSelected(Boolean.FALSE);
                }
            }
        });

        this.observableList.addListener(new ListChangeListener<Selection<ConWorkflowWorkInfoEntity>>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Selection<ConWorkflowWorkInfoEntity>> param) {
                while (param.next()) {
                    if (!param.wasRemoved()) {
                        numbering();
                    }
                }
            }
        });

        this.selectedColumn.setGraphic(checkBox);
        this.selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        this.selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Integer param) {
                return workView.getItems().get(param).selectedProperty();
            }
        }));
        this.selectedColumn.setEditable(true);
        this.workNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        //this.indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(this.workView.getItems().indexOf(column.getValue()) + 1));
        this.indexColumn.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        this.indexColumn.setCellFactory(this.getCustomCellFactory());
        this.workView.setEditable(true);

        // サマリー
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
        this.summaryValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        // 自動更新
        this.autoUpdateButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            logger.info("Change of auto update: {},{}", newValue, this.intervalField.getText());

            if (newValue) {
                long value = 0L;
                try {
                    String valueText = this.intervalField.getText();
                    value = Long.parseLong(valueText.matches("\\d+") ? valueText : "0");
                } catch (Exception ex) {
                    logger.warn(ex, ex);
                    return;
                }

                if (value <= 0) {
                    return;
                }

                this.timeline.setCycleCount(Timeline.INDEFINITE);
                this.timeline.getKeyFrames().clear();
                this.timeline.getKeyFrames().add(new KeyFrame(Duration.minutes(value), (ActionEvent event) -> {
                    logger.info("Auto update.");
                    this.updateChart();
                }));

                this.timeline.play();
            } else if (this.timeline.getStatus() == Animation.Status.RUNNING) {
                this.timeline.stop();
            }
            this.intervalField.setDisable(newValue);
        });

        // 計画表示
        this.taktButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            planChart.eachSeries(series -> series.getNode().setVisible(newValue));
        });

        // 実績表示
        this.seriesButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            for (Node node : this.chart.getChildrenUnmodifiable()) {
                if (node instanceof Legend) {
                    for (Legend.LegendItem item : ((Legend) node).getItems()) {
                        for (XYChart.Series<Number, Number> series : chart.getData()) {
                            if (series.getName().equals(item.getText())) {
                                series.getNode().setVisible(newValue);
                                if (series.getNode().isVisible()) {
                                    item.getSymbol().setStyle("-fx-opacity:1.0");
                                } else {
                                    item.getSymbol().setStyle("-fx-opacity:0.2");
                                }
                                for (XYChart.Data<Number, Number> data : series.getData()) {
                                    if (Objects.nonNull(data.getNode())) {
                                        data.getNode().setVisible(newValue);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        });

        // 凡例
        this.legendButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.chart.setLegendVisible(newValue);
        });

        // タイムラインチャート
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        //format.setTimeZone(TimeZone.getTimeZone("GMT"));
        format.setTimeZone(TimeZone.getDefault());
        this.formatter = new TimeLineTickFormatter(format);
        this.yAxis.setAxisTickFormatter(this.formatter);

        // 工程名のツールチップを表示
        Tooltip tooltip = new Tooltip();
        tooltip.setUserData(0);

        xAxis.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Integer index = xAxis.getValueForDisplay(mouseEvent.getX()).intValue();
                if (index > 0 && index <= xData.size()) {
                    if (tooltip.getUserData() != index) {
                        ConWorkflowWorkInfoEntity row = xData.get(index - 1);

                        String displayWorkName = createDisplayWorkName(row.getWorkName(), row.getWorkRev());

                        tooltip.setText(displayWorkName);
                        tooltip.setUserData(index);

                        Bounds bounds = chartPane.localToScreen(xAxis.getBoundsInLocal());
                        double position = xAxis.getDisplayPosition(index);

                        tooltip.show(sc.getWindow(), bounds.getMinX() + xAxis.getLayoutX() + position, bounds.getMinY() - 16.0);
                    }
                } else if (tooltip.isShowing()) {
                    tooltip.setUserData(0);
                    tooltip.hide();
                }
            }
        });

        xAxis.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (tooltip.isShowing()) {
                    tooltip.setUserData(0);
                    tooltip.hide();
                }
            }
        });

        // チャートのパン
        ChartPanManager panner = new ChartPanManager(this.chart);
        panner.setMouseFilter(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY || (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.isShortcutDown())) {
                    //let it through
                } else {
                    mouseEvent.consume();
                }
            }
        });
        panner.setAxisConstraintStrategy(AxisConstraintStrategies.getFixed(AxisConstraint.Vertical));
        panner.start();

        // チャートのズーム
        ChartZoomManager zoomManager = new ChartZoomManager(this.chartPane, this.selectRect, this.chart);
        zoomManager.setAxisConstraintStrategy(AxisConstraintStrategies.getFixed(AxisConstraint.Vertical));
        zoomManager.setMouseWheelAxisConstraintStrategy(AxisConstraintStrategies.getFixed(AxisConstraint.Vertical));
        zoomManager.setZoomAnimated(false);
        zoomManager.start();

        // ダブルクリックでY軸を再調整
        this.chart.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY) {
                    yAxis.setAutoRanging(true);
                }
            }
        });

        xAxis.setLowerBound(0);
        xAxis.setUpperBound(0);

        // プロパティを読み込む
        this.loadProperties();

        this.blockUI(false);
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
            
            SplitPaneUtils.saveDividerPosition(timeLinePane, getClass().getSimpleName());
            
            this.seveProperties();

            return true;
        } finally {
            logger.info("destoryComponent end.");
        }
    }

    /**
     * 上へ移動
     *
     * @param event
     */
    @FXML
    public void onUp(ActionEvent event) {
        try {
            logger.info("onUp start.");

            if (this.workView.getItems().isEmpty()) {
                return;
            }

            int index = this.workView.getSelectionModel().getSelectedIndex();
            if ((index - 1) >= 0) {
                Selection<ConWorkflowWorkInfoEntity> element = this.workView.getItems().get(index);
                this.workView.getItems().remove(index);
                this.workView.getItems().add(index - 1, element);
                this.workView.getSelectionModel().select(element);
            }

        } finally {
            logger.info("onUp end.");
        }
    }

    /**
     * 下へ移動
     *
     * @param event
     */
    @FXML
    public void onDown(ActionEvent event) {
        try {
            logger.info("onDown start.");

            if (this.workView.getItems().isEmpty()) {
                return;
            }

            int index = this.workView.getSelectionModel().getSelectedIndex();
            if (index >= 0 && (index + 1) < this.workView.getItems().size()) {
                Selection<ConWorkflowWorkInfoEntity> element = this.workView.getItems().get(index);
                this.workView.getItems().remove(index);
                this.workView.getItems().add(index + 1, element);
                this.workView.getSelectionModel().select(element);
            }

        } finally {
            logger.info("onDown end.");
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
     * 自動更新タイムラインをクリアする
     */
    public void clearTimeline() {
        this.timeline.stop();
        this.timeline.getKeyFrames().clear();
    }

    /**
     * 番号を割り当てる。
     */
    private void numbering() {
        int id = 1;
        for (Selection<ConWorkflowWorkInfoEntity> row : this.observableList) {
            if (row.isSelected()) {
                row.setId(id++);
            } else {
                row.setId(-1);
            }
        }
    }

    /**
     * フルスクリーン化あるいは解除を行う
     *
     * @param event
     */
    @FXML
    public void onFullScreen(ActionEvent event) {
        try {
            logger.info("onFullScreen start.");

            //フルスクリーン切り替え時にボタンの表示とメニューの表示を切り替える
            sc.getStage().fullScreenProperty().addListener((observable, oldValue, newValue) -> {
                this.mainSceneController.hideMenu(newValue);
                this.fullScreenButton.setText(newValue
                        ? LocaleUtils.getString("key.FullScreenExit")
                        : LocaleUtils.getString("key.FullScreen"));
            });

            //フルスクリーンボタンの表示でフルスクリーンにするか解除するか判断する
            this.mainSceneController.setFullScreen(Objects.equals(LocaleUtils.getString("key.FullScreen"), this.fullScreenButton.getText()));

        } finally {
            logger.info("onFullScreen end.");
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

            Properties properties = AdProperty.getProperties(Constants.PROPETRY_NAME);
            String path = properties.getProperty(Constants.EXPORT_DIRECTORY);
            String pattern = properties.getProperty(Constants.EXPORT_FILENAME_PATTERN_TIMELINE);

            // 設定ファイルにデフォルトファイル名の項目を作成しておく(インストール時に作業分析向け設定ファイルが作成されないため)
            if (Objects.isNull(pattern)) {
                properties.setProperty(Constants.EXPORT_FILENAME_PATTERN_TIMELINE, "");
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
                    fileName = String.format(Constants.DEF_EXPORT_PATTERN_TIMELINE, now, now, now, now, now, now);
                }
                fileChooser.setInitialFileName(fileName);
            }

            if (Objects.isNull(fileChooser.getInitialFileName())) {
                Date now = new Date();
                String fileName = String.format(Constants.DEF_EXPORT_PATTERN_TIMELINE, now, now, now, now, now, now);
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
                            title.append(LocaleUtils.getString("key.Equipment"));
                            title.append(separator);
                            title.append(LocaleUtils.getString("key.workers"));
                            title.append(separator);
                            title.append(LocaleUtils.getString("key.Status"));
                            title.append(separator);
                            title.append(LocaleUtils.getString("key.ImplementTime"));
                            title.append(separator);
                            title.append(LocaleUtils.getString("key.suspendedReasons"));
                            title.append(separator);
                            title.append(LocaleUtils.getString("key.delayReasons"));
                            writer.write(title.toString());
                            writer.newLine();

                            for (XYChart.Series<Number, Number> series : chart.getData()) {
                                for (XYChart.Data<Number, Number> data : series.getData()) {

                                    if (!(data.getExtraValue() instanceof TimeLineInfoEntity)) {
                                        continue;
                                    }

                                    TimeLineInfoEntity entity = (TimeLineInfoEntity) data.getExtraValue();

                                    StringBuilder sb = new StringBuilder();
                                    sb.append(entity.getKanbanName());
                                    sb.append(separator);
                                    sb.append(entity.getDisplayWorkName());
                                    sb.append(separator);
                                    sb.append(entity.getEquipmentName());
                                    sb.append(separator);
                                    sb.append(entity.getOrganizationName());
                                    sb.append(separator);
                                    sb.append(entity.getActualStatus());
                                    sb.append(separator);
                                    sb.append(DateUtils.format(entity.getImplementDatetime()));
                                    sb.append(separator);
                                    sb.append(formatValue(entity.getInterruptReason()));
                                    sb.append(separator);
                                    sb.append(formatValue(entity.getDelayReason()));

                                    writer.write(sb.toString());
                                    writer.newLine();
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
     *
     *
     * @param value
     * @return
     */
    private String formatValue(String value) {
        if (Objects.isNull(value)) {
            return "";
        }
        return value;
    }

    /**
     * 凡例を表示非表示
     *
     * @param event
     */
    public void onVisibleLegend(ActionEvent event) {
        try {
            logger.info("onLegendVisible start.");

            this.chart.setLegendVisible(this.legendButton.isSelected());
        } finally {
            logger.info("onLegendVisible end.");
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

            String value;

            // 対象日
            this.datePaneController.loadProperties(properties);

            // 対象時間
            value = properties.getProperty(Constants.TIMELINE_FROM_TIME);
            if (!StringUtils.isEmpty(value)) {
                this.fromTimeField.setText(value);
            }

            value = properties.getProperty(Constants.TIMELINE_TO_TIME);
            if (!StringUtils.isEmpty(value)) {
                this.toTimeField.setText(value);
            }

            // 更新間隔
            value = properties.getProperty(Constants.TIMELINE_INTERVAL);
            if (!StringUtils.isEmpty(value)) {
                this.intervalField.setText(value);
            }

            // 対象工程順
            value = properties.getProperty(Constants.TIMELINE_WORKFLOW_ID);
            value = Objects.isNull(value) || value.equals("null") ? "" : value;
            if (!StringUtils.isEmpty(value) && !StringUtils.equals(value, "0")) {
                WorkflowInfoEntity entity = this.workflowFacade.find(Long.parseLong(value));
                if (Objects.isNull(entity.getWorkflowId())) {
                    return;
                }
                this.selectedWorkflow = entity;
                this.workflowField.setText(selectedWorkflow.getWorkflowName() + " : " + selectedWorkflow.getWorkflowRev().toString());
            } else {
                return;
            }

            // 対象工程
            Set<Long> selectedWorkIds = new HashSet<>();
            value = properties.getProperty(Constants.TIMELINE_WORK_ID);
            if (!StringUtils.isEmpty(value)) {
                String[] values = value.split(",");
                for (String str : values) {
                    selectedWorkIds.add(Long.parseLong(str));
                }
            }

            value = properties.getProperty(Constants.TIMELINE_WORK_LIST);
            if (!StringUtils.isEmpty(value)) {
                LinkedList<Selection<ConWorkflowWorkInfoEntity>> list = new LinkedList<>();
                Map<Long, ConWorkflowWorkInfoEntity> workflowWorks = this.selectedWorkflow.getConWorkflowWorkInfoCollection().stream().collect(Collectors.toMap(ConWorkflowWorkInfoEntity::getFkWorkId, d -> d));

                String[] values = value.split(",");
                int id = 1;
                for (String str : values) {
                    Long workId = Long.parseLong(str);
                    if (workflowWorks.containsKey(workId)) {
                        ConWorkflowWorkInfoEntity entity = workflowWorks.get(workId);
                        boolean select = selectedWorkIds.contains(workId);

                        String displayWorkName = this.createDisplayWorkName(entity.getWorkName(), entity.getWorkRev());

                        list.add(new Selection<>(select, displayWorkName, entity, select ? id++ : -1));
                    }
                }

                this.observableList.addAll(list);
                this.workView.setItems(this.observableList);
            } else {
                this.createWorkView(this.selectedWorkflow);
            }

        } catch (IOException | NumberFormatException ex) {
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

            Long workflowId = null;
            if (Objects.nonNull(this.selectedWorkflow)) {
                workflowId = this.selectedWorkflow.getWorkflowId();
            }

            List<String> workIds = new ArrayList<>();
            List<String> selectedWorkIds = new ArrayList<>();
            for (Selection<ConWorkflowWorkInfoEntity> row : this.workView.getItems()) {
                workIds.add(String.valueOf(row.getValue().getFkWorkId()));
                if (row.isSelected()) {
                    selectedWorkIds.add(String.valueOf(row.getValue().getFkWorkId()));
                }
            }

            this.datePaneController.saveProperties(properties);

            properties.setProperty(Constants.TIMELINE_FROM_TIME, this.fromTimeField.getText());
            properties.setProperty(Constants.TIMELINE_TO_TIME, this.toTimeField.getText());
            properties.setProperty(Constants.TIMELINE_INTERVAL, this.intervalField.getText());
            properties.setProperty(Constants.TIMELINE_WORKFLOW_ID, String.valueOf(workflowId));
            properties.setProperty(Constants.TIMELINE_WORK_LIST, String.join(",", workIds));
            properties.setProperty(Constants.TIMELINE_WORK_ID, String.join(",", selectedWorkIds));

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
        this.updateButton.setDisable(block);
        this.exportButton.setDisable(this.seriesList.isEmpty() ? true : block);
        sc.blockUI("ContentNaviPane", block);
        this.progressPane.setVisible(block);
    }

    /**
     * ツリーの親階層生成
     *
     */
    private synchronized void createWorkflowTree() {
        try {
            logger.info("createWorkflowTree start.");

            long count = workflowHierarchyFacade.getTopHierarchyCount();
            this.rootItem.getChildren().clear();
            this.rootItem.getValue().setChildCount(count);

            for (long from = 0; from < count; from += Constants.REST_RANGE) {
                List<WorkflowHierarchyInfoEntity> entities = workflowHierarchyFacade.getTopHierarchyRange(from, from + Constants.REST_RANGE - 1, true);

                entities.stream().forEach((entity) -> {
                    TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0 || !entity.getWorkflowInfoCollection().isEmpty()) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    this.rootItem.getChildren().add(item);
                });
            }

            Platform.runLater(() -> {
                this.workflowView.rootProperty().setValue(rootItem);
                this.workflowView.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell());
            });

            this.rootItem.setExpanded(true);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("createWorkflowTree end.");
        }
    }

    /**
     * 工程順階層を展開する。
     *
     * @param parentItem 対象階層
     */
    private synchronized void expand(TreeItem<WorkflowHierarchyInfoEntity> parentItem) {
        try {
            parentItem.getChildren().clear();

            WorkflowHierarchyInfoEntity workflowHierarchy = parentItem.getValue();

            long count = workflowHierarchy.getChildCount();
            for (long from = 0; from < count; from += Constants.REST_RANGE) {
                List<WorkflowHierarchyInfoEntity> entities = workflowHierarchyFacade.getAffilationHierarchyRange(workflowHierarchy.getWorkflowHierarchyId(), from, from + Constants.REST_RANGE - 1, true);
                entities.stream().forEach((entity) -> {
                    TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0 || !entity.getWorkflowInfoCollection().isEmpty()) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    parentItem.getChildren().add(item);
                });
            }

            for (WorkflowInfoEntity workflow : workflowHierarchy.getWorkflowInfoCollection()) {
                WorkflowHierarchyInfoEntity entity = new WorkflowHierarchyInfoEntity();
                entity.setWorkflowHierarchyId(workflow.getWorkflowId());
                String rev = Objects.isNull(workflow.getWorkflowRev()) ? "" : " : " + workflow.getWorkflowRev().toString();
                entity.setHierarchyName(workflow.getWorkflowName() + rev);
                TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(entity);
                parentItem.getChildren().add(item);
            }

            Platform.runLater(() -> this.workflowView.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell()));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 対象工程リストの生成
     *
     * @param workInfoEntitys
     */
    private void createWorkView(WorkflowInfoEntity workflow) {
        try {
            logger.info("createWorkList start.");

            int id = 1;
            LinkedList<Selection<ConWorkflowWorkInfoEntity>> list = new LinkedList<>();
            for (ConWorkflowWorkInfoEntity entity : workflow.getConWorkflowWorkInfoCollection()) {
                String displayWorkName = this.createDisplayWorkName(entity.getWorkName(), entity.getWorkRev());

                list.add(new Selection<>(true, displayWorkName, entity, id++));
            }

            Collections.sort(list, new Comparator<Selection<ConWorkflowWorkInfoEntity>>() {
                @Override
                public int compare(Selection<ConWorkflowWorkInfoEntity> left, Selection<ConWorkflowWorkInfoEntity> right) {
                    try {
                        long leftOrder = left.getValue().getStandardStartTime().getTime();
                        long rightOrder = right.getValue().getStandardStartTime().getTime();
                        if (leftOrder > rightOrder) {
                            return 1;
                        } else if (leftOrder == rightOrder) {
                            return 0;
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex);
                    }
                    return -1;
                }
            });

            this.observableList.clear();
            this.observableList.addAll(list);
            this.workView.setItems(this.observableList);
        } finally {
            logger.info("createWorkList end.");
        }
    }

    /**
     * チャートデータを更新する。
     */
    private synchronized void updateChart() {
        try {
            logger.info("updateChart start.");

            // 対象開始日時
            Date fromDate = this.datePaneController.getFrom(this.fromTimeField.getHours(), this.fromTimeField.getMinutes());

            // 対象終了日時
            Date toDate = this.datePaneController.getTo(this.toTimeField.getHours(), this.toTimeField.getMinutes());

            // 工程順ID
            if (Objects.isNull(this.selectedWorkflow)) {
                return;
            }
            Long workflowId = this.selectedWorkflow.getWorkflowId();

            // 対象工程ID
            this.xData.clear();
            Map<Long, Integer> works = new HashMap<>();
            for (Selection<ConWorkflowWorkInfoEntity> row : this.workView.getItems()) {
                if (row.isSelected()) {
                    if (!works.containsKey(row.getValue().getFkWorkId())) {
                        this.xData.add(row.getValue());
                        works.put(row.getValue().getFkWorkId(), this.xData.size());
                    }
                }
            }

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        blockUI(true);

                        ProductionSummaryInfoEntity summary = chartFacade.getProductionSummary(workflowId, new ArrayList<Long>(works.keySet()), fromDate, toDate);
                        Map<Long, List<TimeLineInfoEntity>> grouping = summary.getTimeLine().stream().collect(Collectors.groupingBy(TimeLineInfoEntity::getFkKanbanId));
                        seriesList = FXCollections.observableArrayList();

                        summaryView.setItems(FXCollections.observableArrayList(summary.getSummaryItems()));

                        grouping.entrySet().stream()
                                .sorted(java.util.Map.Entry.comparingByKey())
                                .forEach(o -> {
                                    XYChart.Series<Number, Number> series = new XYChart.Series<>();
                                    for (TimeLineInfoEntity entity : o.getValue()) {
                                        XYChart.Data data = new XYChart.Data(works.get(entity.getFkWorkId()), -(entity.getImplementDatetime().getTime() - fromDate.getTime()), entity);
                                        series.setName(entity.getKanbanName());
                                        series.getData().add(data);
                                    }
                                    seriesList.add(series);
                                });

                        //カンバンから画計を作成
                        List<Long> kanbanIds = new ArrayList(grouping.keySet());

                        planChart.update(kanbanIds, fromDate.getTime(), works);
                        seriesList.addAll(planChart.getSeries());

                        Platform.runLater(() -> {
                            xAxis.setLowerBound(0);
                            xAxis.setUpperBound(works.size() + 1);
                            formatter.setDelta(fromDate.getTime());
                            chart.setData(seriesList);

                            //計画の線の表示設定
                            planChart.setSymbolVisible(false);
                            planChart.eachNode(node -> {
                                node.setVisible(taktButton.isSelected());
                                node.lookup(".chart-series-line").setStyle("-fx-stroke: rgba(30, 20, 20, 0.5); -fx-stroke-dash-array: 5 5 5 5;");
                            });

                            //凡例から計画を削除
                            chart.getChildrenUnmodifiable().stream()
                                    .filter(node -> node instanceof Legend)
                                    .findFirst()
                                    .map(node -> ((Legend) node))
                                    .ifPresent(legend -> {
                                        legend.getItems().removeIf(item -> item.getText().startsWith(planChart.PLAN_CHART_NAME));
                                    });

                            //計画チャートにツールチップ作成
                            planChart.createTooltip();

                            //チャートのツールチップ作成
                            for (XYChart.Series<Number, Number> series : chart.getData()) {
                                List<String> styleClasses = series.getNode().getStyleClass();
                                for (XYChart.Data<Number, Number> data : series.getData()) {

                                    if (!(data.getExtraValue() instanceof TimeLineInfoEntity)) {
                                        continue;
                                    }

                                    TimeLineInfoEntity entity = (TimeLineInfoEntity) data.getExtraValue();
                                    if (Objects.nonNull(entity)) {

                                        switch (entity.getActualStatus()) {
                                            case WORKING:
                                                break;
                                            case SUSPEND:
                                                data.getNode().getStyleClass().add("triangle");
                                                break;
                                            case COMPLETION:
                                                // 累計作業時間
                                                //int total = 0;
                                                //for (XYChart.Data<Number, Number> o : series.getData()) {
                                                //    TimeLineInfoEntity t = (TimeLineInfoEntity) o.getExtraValue();
                                                //    if (entity.getFkWorkId().equals(t.getFkWorkId())) {
                                                //        total += t.getWorkTime();
                                                //    }
                                                //}
                                                data.getNode().getStyleClass().add("square");
                                                break;
                                        }

                                        try {
                                            Tooltip toolTip = TooltipBuilder.build(data.getNode());

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
                                                sb1.append(LocaleUtils.getString("key.Equipment"));
                                                sb1.append(crlf);
                                                sb1.append(LocaleUtils.getString("key.workers"));
                                                sb1.append(crlf);

                                                StringBuilder sb2 = new StringBuilder();
                                                sb2.append(entity.getKanbanName());
                                                sb2.append(crlf);
                                                sb2.append(entity.getDisplayWorkName());
                                                sb2.append(crlf);
                                                sb2.append(entity.getEquipmentName());
                                                sb2.append(crlf);
                                                sb2.append(entity.getOrganizationName());
                                                sb2.append(crlf);

                                                switch (entity.getActualStatus()) {
                                                    case WORKING:
                                                        sb1.append(LocaleUtils.getString("key.StartTime"));
                                                        sb2.append(DateUtils.format(entity.getImplementDatetime()));
                                                        break;
                                                    case SUSPEND:
                                                        sb1.append(LocaleUtils.getString("key.SuspendTime"));
                                                        sb1.append(crlf);
                                                        sb1.append(LocaleUtils.getString("key.suspendedReasons"));
                                                        sb1.append(crlf);
                                                        sb1.append(LocaleUtils.getString("key.WorkTime"));

                                                        sb2.append(DateUtils.format(entity.getImplementDatetime()));
                                                        sb2.append(crlf);
                                                        sb2.append(StringUtils.isEmpty(entity.getInterruptReason()) ? "--" : entity.getInterruptReason());
                                                        sb2.append(crlf);
                                                        sb2.append(DateUtils.format(Constants.FORMAT_HHMMSS, entity.getWorkTime()));
                                                        break;
                                                    case COMPLETION:
                                                        sb1.append(LocaleUtils.getString("key.WorkCompleteTime"));
                                                        sb1.append(crlf);
                                                        sb1.append(LocaleUtils.getString("key.delayReasons"));
                                                        sb1.append(crlf);
                                                        sb1.append(LocaleUtils.getString("key.WorkTime"));

                                                        sb2.append(DateUtils.format(entity.getImplementDatetime()));
                                                        sb2.append(crlf);
                                                        sb2.append(StringUtils.isEmpty(entity.getDelayReason()) ? "--" : entity.getDelayReason());
                                                        sb2.append(crlf);
                                                        sb2.append(DateUtils.format(Constants.FORMAT_HHMMSS, entity.getWorkTime()));
                                                        break;
                                                }

                                                label1.setText(sb1.toString());
                                                label2.setText(sb2.toString());
                                                hBox.getChildren().addAll(label1, label2);
                                                toolTip.setGraphic(hBox);
                                            }

                                            data.getNode().setOnMouseEntered(event -> data.getNode().getStyleClass().add("onHover"));
                                            data.getNode().setOnMouseExited(event -> data.getNode().getStyleClass().remove("onHover"));
                                        } catch (Exception ex) {
                                            logger.fatal(ex, ex);
                                        }
                                    }
                                }
                            }

                            // 凡例のカンバン名をクリックでSeriesの表示非表示を切り替える
                            for (Node node : chart.getChildrenUnmodifiable()) {
                                if (!(node instanceof Legend)) {
                                    continue;
                                }

                                for (Legend.LegendItem item : ((Legend) node).getItems()) {
                                    item.getSymbol().setCursor(Cursor.HAND);

                                    item.getSymbol().setOnMouseClicked(event -> {

                                        Optional<XYChart.Series<Number, Number>> actualSeries = chart.getData().stream()
                                                .filter(series -> series.getName().equals(item.getText()))
                                                .findAny();

                                        Optional<XYChart.Series<Number, Number>> planSeries = planChart.getSeries().stream()
                                                .filter(series -> series.getName().equals(PlanChart.createPrefixed(item.getText())))
                                                .findAny();

                                        if (event.getButton() == MouseButton.PRIMARY) {
                                            actualSeries.ifPresent(series -> {
                                                series.getNode().setVisible(!series.getNode().isVisible());

                                                if (series.getNode().isVisible()) {
                                                    item.getSymbol().setStyle("-fx-opacity:1.0");
                                                } else {
                                                    item.getSymbol().setStyle("-fx-opacity:0.2");
                                                }

                                                for (XYChart.Data<Number, Number> data : series.getData()) {
                                                    if (Objects.nonNull(data.getNode())) {
                                                        data.getNode().setVisible(series.getNode().isVisible());
                                                    }
                                                }
                                            });
                                            planSeries.ifPresent(series -> {
                                                series.getNode().setVisible(actualSeries.map(actual -> actual.getNode().isVisible()).orElse(false));
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
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
            String pattern = properties.getProperty(Constants.EXPORT_FILENAME_PATTERN_TIMELINE);

            // 設定ファイルにデフォルトファイル名の項目を作成しておく(インストール時に作業分析向け設定ファイルが作成されないため)
            if (Objects.isNull(pattern) || pattern.isEmpty()) {
                pattern = Constants.DEF_EXPORT_PATTERN_TIMELINE;
                properties.setProperty(Constants.EXPORT_FILENAME_PATTERN_TIMELINE, pattern);
            }

            if (!StringUtils.isEmpty(pattern)) {
                Date now = new Date();
                try {
                    result = String.format(pattern, now, now, now, now, now, now);
                } catch (Exception ex) {
                    result = String.format(Constants.DEF_EXPORT_PATTERN_TIMELINE, now, now, now, now, now, now);
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
