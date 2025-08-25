/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.component;

import adtekfuji.andon.agenda.common.AgendaCompoInterface;
import adtekfuji.andon.agenda.common.Blinking;
import adtekfuji.andon.agenda.common.KanbanStatusConfig;
import adtekfuji.andon.agenda.model.AgendaModel;
import adtekfuji.andon.agenda.model.data.*;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;

import static java.lang.Math.log;
import static java.lang.Math.max;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.andon.enumerate.AgendaDisplayPatternEnum;
import jp.adtekfuji.andon.enumerate.DisplayModeEnum;
import jp.adtekfuji.andon.enumerate.PlanActualShowTypeEnum;
import jp.adtekfuji.andon.enumerate.TimeScaleEnum;
import jp.adtekfuji.andon.utility.MonitorTools;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 計画実績ペインのコントローラー(横軸に時間軸、縦軸にカンバン・作業者)
 *
 * @author HN)y-harada
 */
@FxComponent(id = "AgendaHorizonCompo", fxmlPath = "/fxml/compo/agenda_andon_horizon_compo.fxml")
public class AgendaHorizonCompoController implements Initializable, ComponentHandler, AgendaCompoInterface {

    private final Logger logger = LogManager.getLogger();

    private final String RED = "red";
    private final String BLACK = "black";
    private final String TRANSPARENT = "-fx-background-color:transparent;";
    private final String DARKGRAY_ALPHA50 = "-fx-background-color:rgba(120,120,120,0.5)";
    //private final String BORDER_LINE = "-fx-border-style:solid;-fx-border-width:1 0 0 0;-fx-border-color:#505050;-fx-background-color:rgba(255,255,255,0.05);";
    private final String BORDER_LINE = "-fx-border-style:solid;-fx-border-width:1 0 0 0;-fx-border-color:#505050;-fx-background-color:transparent;";
    private final String pattern_HHmmss[] = {"HH:mm:ss"};
    private final FastDateFormat formatter = FastDateFormat.getInstance("HH:mm");
    private final FastDateFormat formatter_HHmmss = FastDateFormat.getInstance("HH:mm:ss");
    private final FastDateFormat formatter_HH = FastDateFormat.getInstance("HH");
    private final FastDateFormat formatter_yyyyMMdd  = FastDateFormat.getInstance("yyyy/M/d");
    private final FastDateFormat formatter_yyyyMMddEEE = FastDateFormat.getInstance("yyyy/M/d(EEE)");
    private final FastDateFormat formatter_MMdd  = FastDateFormat.getInstance("M/d");
    private final FastDateFormat formatter_yyyyMM  = FastDateFormat.getInstance("yyyy/M");
    private final FastDateFormat formatter_MMddHHmm = FastDateFormat.getInstance("M/dd HH:mm");
    
    private final double MILLIS_PER_MINUTE = 60000.0;
    
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ConfigData config = ConfigData.getInstance();
    private ResourceBundle rb; //= LocaleUtils.getBundle("locale.locale");
    private final CurrentData currentData = CurrentData.getInstance();
    private final AgendaModel model = AgendaModel.getInstance();
    private final Blinking blinking = Blinking.getInstance();
    private Map<Long, Agenda> agendas = new LinkedHashMap<>();
    private Timeline pageToggleTimeline;
    private double timeColumnTranslateX = 0.0;
    private double timeLineTranslateX = 0.0;
    private double panePrefWidth;
    private double timeColumnPrefHeight;
    private double verticalPanePrefWidth;
    private double widthPaneZoom = 0;
    private boolean flgZoom = false;
    private double zoomRate = 1.0;
    private boolean flgColumnZoom = false;
    private double columnZoomRate = 1.0;
    private double windowZoomRate = 1.0;
    private int flgShowZoom = 1;
    private int flgKeyPress = 1;
    private Date startTime;
    private Date endTime;
    private long timeDiff; // UTCからの時差
    private PlanActualShowTypeEnum planActualType;
    private AnchorPane gridPane;
    private final List<Line> gridLines = new ArrayList<>();
    private Date targetDate;
    private double timeColumnX; // 時間軸の位置調整距離
    
    @FXML
    private AnchorPane scalePane;
    @FXML
    private AnchorPane contentPane;
    @FXML
    private AnchorPane horizonPane;
    @FXML
    private AnchorPane verticalPane;
    @FXML
    private AnchorPane blindPane;
    @FXML
    private ScrollBar scrollBarWidth;
    @FXML
    private ScrollBar scrollBarHeight;
    @FXML
    private Slider sliderZoom;
    @FXML
    private Button labelZoom;
    @FXML
    private Label timeLine;
    
    /**
     * 予実表示画面(横時間軸)を初期化する。
     *
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.rb = rb;

            // ウィンドウをディスプレイの解像度に合わせる
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            logger.info("Screen: Height={0} Width={1}", visualBounds.getHeight(), visualBounds.getWidth());

            contentPane.setPrefWidth(visualBounds.getWidth() - 320.0);
            contentPane.setPrefHeight(visualBounds.getHeight() - 110.0);
            verticalPane.setPrefHeight(visualBounds.getHeight() - 110.0);
            horizonPane.setPrefWidth(visualBounds.getWidth() - 320.0);

            timeColumnPrefHeight = horizonPane.getPrefHeight();
            verticalPanePrefWidth = verticalPane.getPrefWidth();
            
            // 時間軸の位置調整をするX軸の移動距離の初期化
            this.timeColumnX = this.getTimeColumnX();

            // 進捗時間表示のONとOFFで縦軸の幅を調整する
            verticalPane.setPrefWidth(verticalPanePrefWidth - this.timeColumnX);
            blindPane.setPrefWidth(verticalPanePrefWidth - this.timeColumnX);

            blinking.stop();
            //unvisible scrollbars
            scrollBarHeight.setVisible(false);
            scrollBarWidth.setVisible(false);
            sliderZoom.setVisible(false);

            this.startTime = this.config.getStartTime();
            this.endTime = this.config.getEndTime();
            
            Calendar cTime = Calendar.getInstance();
            cTime.clear();
            this.timeDiff = cTime.getTimeInMillis();

            this.planActualType = Objects.isNull(this.config.getHorizonPlanActualShowType()) ? PlanActualShowTypeEnum.getDefault() : this.config.getHorizonPlanActualShowType();
            this.gridPane = new AnchorPane();

            this.contentPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                for (Line line : this.gridLines) {
                    line.setEndY(newValue.doubleValue());
                }
            });

            if (config.isAutoScroll()) {
                // 日跨ぎの為, 前日,当日,翌日の3日間のデータをセットしする。
                // 日にち表示の際に1日オフセットをかける。(時刻のヘッダに関しては辻妻があっている為、オフセットはかけていない)
                this.startTime = adtekfuji.utility.DateUtils.getBeginningOfDate(this.startTime);
                this.endTime = DateUtils.addDays(this.startTime, 3);
                double rows = (this.endTime.getTime() - this.startTime.getTime()) / (this.config.getHorizonAutoScrollTime().getTime() - adtekfuji.utility.DateUtils.min().getTime());
                this.flgZoom = true;
                this.zoomRate = rows;

                this.labelZoom.setVisible(false);
                this.scrollBarWidth.setVisible(false);
            } else {
                this.panePrefWidth = this.contentPane.getPrefWidth() + this.timeColumnX;
            }
            
            // 時間軸を表示する
            this.showTimeColumn();
            // ズームバーとスクロールバーを初期化する
            this.initializeControls();
            // コンテンツを描画する
            this.draw();
            // タイムラインを描画する
            this.drawSystemTimeLine();

            model.setController(this);

            // 表示倍率を設定
            Scene scene = sc.getStage().getScene();       
            Rectangle2D bounds = Screen.getPrimary().getBounds();

            this.transforms(bounds.getWidth(), bounds.getHeight(), scene.getWidth(), scene.getHeight());
            
            scene.widthProperty().addListener((observable, oldValue, newValue) -> {
                this.transforms(bounds.getWidth(), bounds.getHeight(), scene.getWidth(), scene.getHeight());
            });

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 表示倍率を設定する。
     *
     * @param oldWidth
     * @param oldHeight
     * @param newWidth
     * @param newHeight
     */
    private void transforms(double oldWidth, double oldHeight, double newWidth, double newHeight) {
        // ウィンドウをディスプレイの解像度に合わせる
//        windowZoomRate = (contentPane.getPrefWidth() - (oldWidth - newWidth)) / contentPane.getPrefWidth();
//
//        widthPaneZoom = contentPane.getPrefWidth() * zoomRate * columnZoomRate * windowZoomRate;
//        Platform.runLater(() -> {
//            if (config.isFullScreen() && widthPaneZoom <= contentPane.getPrefWidth()) {
//                scrollBarWidth.setVisible(false);
//            }
//
//            contentPane.setTranslateX(0);
//            horizonPane.setTranslateX(0);
//            timeLine.setTranslateX(0);
//            contentPane.setTranslateY(0);
//            verticalPane.setTranslateY(0);
//            scrollBarWidth.setValue(0);
//
//            contentPane.getChildren().clear();
//            verticalPane.getChildren().clear();
//
//            showTimeColumn();
//            draw();
//            drawSystemTimeLine();
//        });
        double ratio = newWidth / oldWidth;
        this.scalePane.getTransforms().clear();
        this.scalePane.getTransforms().add(new Scale(ratio, ratio, 0, 0));
    }
    
    /**
     * コンポーネントが破棄される時に呼び出される。
     * 
     * @return true:破棄可能、false:破棄不可能
     */
    @Override
    public boolean destoryComponent() {
        logger.info("destroyComponent start.");
        if (Objects.nonNull(pageToggleTimeline)) {
            pageToggleTimeline.stop();
            pageToggleTimeline.getKeyFrames().clear();
        }
        return true;
    }

    /**
     * ズームバーとスクロールバーを初期化する。
     */
    private void initializeControls() {

        this.setScrollRange();

        sliderZoom.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            flgZoom = true;
            double widthPane = contentPane.getPrefWidth();
            zoomRate = new_val.doubleValue() / 100.0;
            widthPaneZoom = widthPane * zoomRate * columnZoomRate * windowZoomRate;
            scrollBarWidth.setVisible(true);
            setScrollRange();
            buildTooltip(sliderZoom, "Zoom: " + new_val.intValue() + "%");
        });
        sliderZoom.setOnMousePressed(event -> {
            logger.info("sliderZoom setOnMousePressed; setUpdate(false)");
            model.setUpdate(false);
        });
        sliderZoom.setOnMouseReleased((MouseEvent event) -> {
            logger.info("sliderZoom setOnMouseReleased; setUpdate(true)");
            model.setUpdate(true);
            Platform.runLater(() -> {
                if (widthPaneZoom <= contentPane.getPrefWidth()) {
                    scrollBarWidth.setVisible(false);
                }

                contentPane.setTranslateX(0);
                horizonPane.setTranslateX(0);
                timeLine.setTranslateX(0);
                contentPane.setTranslateY(0);
                verticalPane.setTranslateY(0);
                scrollBarWidth.setValue(0);
                scrollBarHeight.setValue(0);
                
                contentPane.getChildren().clear();
                verticalPane.getChildren().clear();
                
                showTimeColumn();
                draw();

                drawSystemTimeLine();
            });
            event.consume();
        });

        sliderZoom.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            final KeyCodeCombination prevNodeKeyCombination = new KeyCodeCombination(KeyCode.UP);
            final KeyCodeCombination nextNodeKeyCombination = new KeyCodeCombination(KeyCode.DOWN);

            @Override
            public void handle(KeyEvent event) {
                double value = sliderZoom.getValue();
                if (flgKeyPress == 2) {
                    flgKeyPress = 1;
                } else {
                    flgKeyPress = 2;
                }
                if (flgShowZoom == 2 && flgKeyPress == 1) {
                    if (prevNodeKeyCombination.match(event)) {
                        if (value < 500) {
                            if (value > 475) {
                                value = 500;
                            } else {
                                value = value + 25;
                            }
                        }
                    } else if (nextNodeKeyCombination.match(event)) {
                        if (value > 100) {
                            if (value < 125) {
                                value = 100;
                            } else {
                                value = value - 25;
                            }
                        }
                    }
                    flgZoom = true;
                    sliderZoom.setValue(value);
                    double widthPane = contentPane.getPrefWidth();
                    zoomRate = value / 100.0;
                    widthPaneZoom = widthPane * zoomRate * columnZoomRate * windowZoomRate;

                    scrollBarWidth.setVisible(true);
                    Platform.runLater(() -> {
                        if (config.isFullScreen() && widthPaneZoom <= contentPane.getPrefWidth()) {
                            scrollBarWidth.setVisible(false);
                        }

                        contentPane.setTranslateX(0);
                        horizonPane.setTranslateX(0);
                        timeLine.setTranslateX(0);
                        contentPane.setTranslateY(0);
                        verticalPane.setTranslateY(0);
                        scrollBarWidth.setValue(0);

                        contentPane.getChildren().clear();
                        verticalPane.getChildren().clear();
                        
                        showTimeColumn();
                        draw();
                        drawSystemTimeLine();
                    });
                }
                event.consume();
            }
        });

        labelZoom.setOnAction((ActionEvent e) -> {
            if (flgShowZoom == 1) {
                sliderZoom.setShowTickMarks(true);
                sliderZoom.setShowTickLabels(true);
                sliderZoom.setMajorTickUnit(200f);
                sliderZoom.setMinorTickCount(3);
                sliderZoom.setBlockIncrement(400f);
                sliderZoom.setSnapToTicks(false);
                sliderZoom.setStyle("-fx-text-background-color: white; -fx-background-color: rgba(0, 0, 0, 0.4); -fx-font-size:" + config.getZoomFontSize() + ";");
                sliderZoom.setVisible(true);
                sliderZoom.setMin(100);
                sliderZoom.setMax(500);
                labelZoom.setStyle("-fx-background-image: url('image/btnZoomON.png'); -fx-padding: 0; -fx-spacing: 0; -fx-background-repeat: no-repeat; -fx-background-position: center center; -fx-border-height: 0;-fx-border-color: black;");
                flgShowZoom = 2;
                
            } else if (flgShowZoom == 2) {
                sliderZoom.setVisible(false);
                labelZoom.setStyle("-fx-background-image: url('image/btnZoomOFF.PNG'); -fx-padding: 0; -fx-spacing: 0; -fx-background-repeat: no-repeat; -fx-background-position: center center; -fx-border-height: 0;-fx-border-color: black;");
                flgShowZoom = 1;
            }
            
            if (widthPaneZoom <= contentPane.getPrefWidth()) {
                scrollBarWidth.setVisible(false);
            }
        });

        buildTooltip(sliderZoom, "Zoom");
        
        sc.getStage().getScene().heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (config.isAutoScroll()) {
                scrollBarHeight.setVisible(false);
            } else {
                int topicNum = 0;
                for (Agenda agenda : this.agendas.values()) {
                    topicNum += getPlanNum(agenda) + getActualNum(agenda);
                }

                scrollBarHeight.setVisible(!(this.config.getHorizonRowHight() * topicNum < this.contentPane.getHeight()));
            }
        });



        sc.getStage().getScene().widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (config.isAutoScroll()) {
                scrollBarWidth.setVisible(false);
            } else {
                scrollBarWidth.setVisible(true);
                if (widthPaneZoom <= contentPane.getPrefWidth()) {
                    scrollBarWidth.setVisible(false);
                }
            }
        });

        scrollBarWidth.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            timeColumnTranslateX = newValue.doubleValue() * -1;
            contentPane.setTranslateX(timeColumnTranslateX);
            horizonPane.setTranslateX(timeColumnTranslateX);
            timeLine.setTranslateX(timeLineTranslateX + verticalPanePrefWidth + timeColumnTranslateX - this.timeColumnX);
        });

        scrollBarHeight.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            contentPane.setTranslateY(-newValue.doubleValue());
            verticalPane.setTranslateY(-newValue.doubleValue());
        });

        scrollBarWidth.setOnMousePressed(event -> {
            logger.info("scrollBarWidth setOnMousePressed; setUpdate(false)");
            model.setUpdate(false);
            event.consume();
        });
        scrollBarWidth.setOnMouseReleased((MouseEvent event) -> {
            logger.info("scrollBarWidth setOnMouseReleased; setUpdate(true)");
            model.setUpdate(true);
            event.consume();
        });
        scrollBarHeight.setOnMousePressed(event -> {
            logger.info("scrollBarHeight setOnMousePressed; setUpdate(false)");
            model.setUpdate(false);
            event.consume();
        });
        scrollBarHeight.setOnMouseReleased((MouseEvent event) -> {
            logger.info("scrollBarHeight setOnMouseReleased; setUpdate(true)");
            model.setUpdate(true);
            event.consume();
        });

        scrollBarHeight.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.setScrollRange();
        });

        scrollBarWidth.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.setScrollRange();
        });

        contentPane.setOnScroll(event->{
            if ( model.isUpdate()) {
                model.setUpdate(false);
                double vVal = scrollBarHeight.getValue() - event.getDeltaY();
                vVal = Math.max(vVal, scrollBarHeight.getMin());
                vVal = Math.min(vVal, scrollBarHeight.getMax());
                scrollBarHeight.setValue(vVal);

                double hVal = scrollBarWidth.getValue() - event.getDeltaX();
                hVal = Math.max(hVal, scrollBarWidth.getMin());
                hVal = Math.min(hVal, scrollBarWidth.getMax());
                scrollBarWidth.setValue(hVal);
                model.setUpdate(true);
            }
            event.consume();
        });


    }

    Function<Integer, String> oddRowColorChange = (rowCount)->rowCount%2==0 ? TRANSPARENT : DARKGRAY_ALPHA50;
    Function<Integer, String> rowColorNoChange  = (rowCount)->"-fx-background-color:rgba(120,120,120,0.5);";
    //Function<Integer, String> flatColorChange   = (rowCount)->"-fx-background-color:rgba(120,120,120,0.5);";

    Supplier<String> flatPanel = ()->"";
    Supplier<String> edgePanel = ()->" -fx-border-height: 0.5; -fx-border-color: white;";

    /**
     * 予実を描画する
     *
     * @param partial 表示する実績の集合
     */
    private void drawRows(List<Entry<Long, Agenda>> partial) {
        try {
            logger.info("drawRows start.");
            
            // 画面表示なしの場合、描画更新は行なわない。
            if (!KanbanStatusConfig.getEnableView()) {
                return;
            }

            // AgendaModelで描画停止状態の時は描画処理を行なわない。
            if (!model.isUpdate()) {
                return;
            }

            if (partial.size() <= 0) {
                return;
            }

            Function<Integer, String> rowColorSelector = this.config.getChangeEvenColor()
                    ? oddRowColorChange  // 偶数行・奇数行で色を変える
                    : rowColorNoChange;  // 偶数行・奇数行で色を変えない

            Supplier<String> panelType = this.config.isFlatPanel()
                    ? flatPanel   // フラットパネル
                    : edgePanel;  // エッジのあるパネル

            contentPane.getChildren().clear();
            verticalPane.getChildren().clear();
            contentPane.getChildren().add(gridPane);

            double totalDisplayMinutes;
            double workTime = (endTime.getTime() - startTime.getTime()) / MILLIS_PER_MINUTE;
            switch (config.getHorizonTimeScale()) {
                case Time:
                default:
                    totalDisplayMinutes = workTime;
                    break;
                case Day:
                case HalfDay:
                    totalDisplayMinutes = workTime * config.getHorizonShowDays();
                    break;
                case Month:
                case Week:
                    totalDisplayMinutes = (this.currentData.getToDate().getTime() - this.currentData.getFromDate().getTime()) / MILLIS_PER_MINUTE;
                    break;
            }

            double rowHeight = this.config.getHorizonRowHight();
            double planHeight = this.config.isShowProgress() ? rowHeight * 0.5 : rowHeight;
            double fromTop = 0;
            int rowCount = 0;

            for (Entry<Long, Agenda> entry : partial) {
                
                rowCount++;
                
                Agenda agenda = entry.getValue();
                int planNum = this.getPlanNum(agenda);
                int actualNum = this.getActualNum(agenda);

                String title1 = agenda.getTitle1();
                String title2 = agenda.getTitle2();
                String fontColor = agenda.getFontColor();
                String backgraundColor = agenda.getBackColor();
                String textPlan = LocaleUtils.getString("key.PlanTitle");
                String textActual = LocaleUtils.getString("key.ActualTitle");

                // 行ペイン
                AnchorPane rowPane = new AnchorPane();
                // 進捗時間表示のONとOFFで行のスタート位置を調整する
                AnchorPane.setLeftAnchor(rowPane, 0.0 - this.timeColumnX);
                // 自動スクロールがONの場合は時間軸が左右均等に広がるようにスタート位置を調整する距離を半分にする
                if (config.isAutoScroll()) {
                    AnchorPane.setLeftAnchor(rowPane, 0.0 - (this.timeColumnX / 2));
                }
                AnchorPane.setRightAnchor(rowPane, 0.0);
                AnchorPane.setTopAnchor(rowPane, fromTop);
                rowPane.setPrefHeight(planNum * planHeight + actualNum * rowHeight);
                rowPane.setStyle(rowColorSelector.apply(rowCount));

                // 行ヘッダー
                AnchorPane headerPane = new AnchorPane();
                AnchorPane.setLeftAnchor(headerPane, 0.0);
                AnchorPane.setRightAnchor(headerPane, 0.0);
                AnchorPane.setTopAnchor(headerPane, fromTop);
                headerPane.setPrefHeight(planNum * planHeight + actualNum * rowHeight);
                headerPane.setStyle(TRANSPARENT);

                // カンバン名(作業者名)ラベル
                Label lableTitle1 = new Label();
                lableTitle1.setPrefWidth(this.verticalPanePrefWidth * 0.6);
                AnchorPane.setTopAnchor(lableTitle1, 0.0);
                AnchorPane.setBottomAnchor(lableTitle1, 0.0);
                AnchorPane.setLeftAnchor(lableTitle1, 0.0);
                lableTitle1.setStyle("-fx-font-size:" + config.getTitleFontSize() + "; -fx-background-color: " + backgraundColor + "; -fx-text-fill: " + fontColor + "; -fx-border-width: 0 2 0 0; -fx-border-color:black;");
                lableTitle1.setAlignment(Pos.CENTER);
                lableTitle1.setTextAlignment(TextAlignment.CENTER);
                lableTitle1.setWrapText(true);
                lableTitle1.setPadding(new Insets(4));
                lableTitle1.setText(title1 + "\n" + title2);

                // 予定ラベル
                Label lablePlan = new Label();
                // 実績ラベル
                Label lableActual = new Label();
                switch (this.planActualType) {
                    case PlanOnly:
                        // 予定のみの場合、予実エリアを表示しない
                        lablePlan.setVisible(false);
                        break;
                    case ActualOnly:
                        // 実績のみ
                        // 進捗時間表示がOFFの場合は予実エリアを表示しない
                        if (!config.getHorizonShowActualTime()) {
                            lableActual.setVisible(false);
                            break;
                        }
                        // 進捗時間表示がONの場合、実績エリアの二行目に進捗時間を表示する
                        lableActual.setVisible(true);
                        lableActual.setPrefWidth(this.verticalPanePrefWidth * 0.4);
                        lableActual.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size:" + config.getColumnFontSize() + ";");
                        lableActual.setAlignment(Pos.CENTER);
                        lableActual.setTextAlignment(TextAlignment.CENTER);
                        AnchorPane.setBottomAnchor(lableActual, 0.0);
                        AnchorPane.setLeftAnchor(lableActual, this.verticalPanePrefWidth * 0.6);                        
                        lableActual.setText(textActual + "\n" + MonitorTools.formatTaktTime(agenda.getDelayTimeMillisec()));
                        lableActual.setPrefHeight(actualNum * rowHeight);
                        break;
                    case PlanAndActual:
                    default:
                        // 予定と実績(デフォルト)
                        lablePlan.setVisible(true);
                        lablePlan.setPrefWidth(this.verticalPanePrefWidth * 0.1);
                        lablePlan.setStyle("-fx-background-color: white; -fx-font-size:" + config.getColumnFontSize() + ";");
                        lablePlan.setAlignment(Pos.CENTER);
                        lablePlan.setTextAlignment(TextAlignment.CENTER);
                        AnchorPane.setTopAnchor(lablePlan, 0.0);
                        AnchorPane.setLeftAnchor(lablePlan, this.verticalPanePrefWidth * 0.6);
                        lablePlan.setText("P");
                        AnchorPane.setBottomAnchor(lablePlan, actualNum * rowHeight);

                        lableActual.setVisible(true);
                        lableActual.setPrefWidth(this.verticalPanePrefWidth * 0.1);
                        lableActual.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size:" + config.getColumnFontSize() + "; -fx-border-width: 2 0 0 0;-fx-border-color:black;");
                        lableActual.setAlignment(Pos.CENTER);
                        lableActual.setTextAlignment(TextAlignment.CENTER);
                        AnchorPane.setBottomAnchor(lableActual, 0.0);
                        AnchorPane.setLeftAnchor(lableActual, this.verticalPanePrefWidth * 0.6);
                        lableActual.setText("A");
                        // 進捗時間表示がONの場合、実績エリアと予定エリアの横幅を広げて実績エリアの二行目に進捗時間を表示する                        
                        if (config.getHorizonShowActualTime()) {
                            lablePlan.setPrefWidth(this.verticalPanePrefWidth * 0.4);
                            lablePlan.setText(textPlan);
                            lableActual.setPrefWidth(this.verticalPanePrefWidth * 0.4);
                            lableActual.setText(textActual + "\n" + MonitorTools.formatTaktTime(agenda.getDelayTimeMillisec()));
                        }
                        AnchorPane.setTopAnchor(lableActual, planNum * planHeight);
                        break;
                }

                //AnchorPane pane = new AnchorPane();
                //pane.setStyle("-fx-background-color:transparent;");
                //AnchorPane.setLeftAnchor(pane, 0.0);
                //AnchorPane.setRightAnchor(pane, 0.0);
                //AnchorPane.setTopAnchor(pane, 0.0);
                //AnchorPane.setBottomAnchor(pane, 0.0);
                
                // 計画
                AnchorPane planPane;
                if (config.isShowPlan()) {
                    List<AgendaTopic> topics = agenda.getPlans()
                            .stream()
                            .map(AgendaPlan::getTopics)
                            .map(Map::values)
                            .flatMap(Collection::stream)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());

                    planPane = this.drawProduction(topics, planNum, planHeight, totalDisplayMinutes, panelType, false);
                    AnchorPane.setTopAnchor(planPane, 0.0);

                } else {
                    planPane = new AnchorPane();
                    planPane.setVisible(false);
                    planPane.setPrefSize(0, 0);
                }
                
                AnchorPane actualPane;
                if (config.isShowProgress()) {
                    // 製品進捗
                    List<AgendaTopic> topics = agenda.getProgress()
                        .stream()
                        .map(AgendaPlan::getTopics)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
                    
                    lableTitle1.setPrefWidth(this.verticalPanePrefWidth);
                    lablePlan.setVisible(false);
                    actualPane = this.drawProduction(topics, 1, rowHeight, totalDisplayMinutes, panelType, true);
                    actualPane.setStyle(BORDER_LINE);
                    AnchorPane.setBottomAnchor(actualPane, 0.0);

                } else if (config.isShowActual()) {
                    // 実績
                    actualPane = new AnchorPane();
                    actualPane.setStyle(BORDER_LINE);
                    AnchorPane.setLeftAnchor(actualPane, 0.0);
                    AnchorPane.setRightAnchor(actualPane, 0.0);
                    AnchorPane.setTopAnchor(actualPane, config.isShowPlan() ? planHeight * planNum : 0.0);
                    actualPane.setPrefHeight(rowHeight * actualNum);

                    List<AgendaTopic> topics = agenda.getActuals()
                            .stream()
                            .map(AgendaGroup::getTopics)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());

                    int rows = 0;
                    for (AgendaActualGroup group : AgendaActualGroup.groupBy(topics)) {
                        for (AgendaTopic topic : group.getTopics()) {

                            double cycleMinutes = this.calculateCycleMinutes(topic.getActualStartTime(), topic.getActualEndTime());
                            double transrateMinutes = this.calculateTransrateMinutes(topic.getActualStartTime());

                            if (cycleMinutes < 0 || transrateMinutes < 0 || totalDisplayMinutes < transrateMinutes) {
                                continue;
                            }

                            double prefHeight = rowHeight;
                            double prefWidth = max((panePrefWidth / totalDisplayMinutes) * cycleMinutes, 1.0);
                            double translateX = (panePrefWidth / totalDisplayMinutes) * transrateMinutes;

                            String text1 = topic.getTitle1();
                            String text2 = Objects.nonNull(topic.getTitle2()) ? topic.getTitle2() : "";
                            String text3 = topic.getTitle3();
                            String fontColor1 = topic.getFontColor();
                            String backColor = changeRgba(topic.getBackColor(), 0.7);
                            String actualStart = formatter_MMddHHmm.format(topic.getActualStartTime());
                            String actualEnd = formatter_MMddHHmm.format(topic.getActualEndTime());

                            Label label = new Label();
                            label.setText(text1 + "\n" + text2 + "\n" + text3);
                            label.setStyle("-fx-font-size:" + config.getItemFontSize() + "; -fx-text-fill: " + fontColor1 + "; -fx-background-color:" + backColor + ";" + panelType.get());
                            label.setAlignment(Pos.CENTER);
                            label.setTextAlignment(TextAlignment.CENTER);
                            label.setPrefSize(prefWidth, prefHeight);
                            label.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                            label.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                            AnchorPane.setLeftAnchor(label, translateX);

                            if ((AgendaDisplayPatternEnum.DisplayPerKanban.equals(this.config.getHorizonAgendaDisplayPattern())
                                    || (!DisplayModeEnum.WORKER.equals(this.config.getMode())) && !DisplayModeEnum.KANBAN.equals(this.config.getMode()) && !DisplayModeEnum.LINE.equals(this.config.getMode()))
                                    && Objects.nonNull(topic.getRow())) {
                                AnchorPane.setTopAnchor(label, prefHeight * topic.getRow());
                            } else {
                                AnchorPane.setTopAnchor(label, prefHeight * rows);
                            }

                            if (topic.isBlink()) {
                                blinking.play(label);
                            }

                            this.buildTooltip(label, text1 + "\n" + text2 + "\n" + text3 + "\n" + actualStart + " - " + actualEnd);

                            actualPane.getChildren().addAll(label);
                        }
                        ++rows;
                    }

                } else {
                    actualPane = new AnchorPane();
                    actualPane.setVisible(false);
                }

                rowPane.getChildren().addAll(planPane, actualPane);
                headerPane.getChildren().addAll(lableTitle1, lableActual, lablePlan);

                contentPane.getChildren().addAll(rowPane);
                verticalPane.getChildren().addAll(headerPane);

                fromTop += planNum * planHeight + actualNum * rowHeight + 2;
                
            }

            // 休憩時間を描画
            this.drawBreakTime(contentPane, fromTop);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("drawRows end.");
        }
    }
    
    /**
     * 製品進捗を描画する。
     * 
     * @param topics
     * @param count
     * @param height
     * @param min
     * @param panelStyle
     * @param isActual
     * @return 
     */
    private AnchorPane drawProduction(List<AgendaTopic> topics, int count, double height, double min, Supplier<String> panelStyle, boolean isActual) {
        AnchorPane pane = new AnchorPane();
        pane.setStyle(TRANSPARENT);
        AnchorPane.setLeftAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);
        pane.setPrefHeight(count * height);

        boolean planned = true;
        int rows = 0;

        for (AgendaGroup group : AgendaGroup.groupBy(topics)) {
            for (AgendaTopic topic : group.getTopics()) {
                planned = planned && Objects.isNull(topic.getActualStartTime());

                double cycleMinutes = this.calculateCycleMinutes(topic.getPlanStartTime(), topic.getPlanEndTime());
                double transrateMinutes = this.calculateTransrateMinutes(topic.getPlanStartTime());

                if (cycleMinutes < 0 || transrateMinutes < 0 || min < transrateMinutes) {
                    continue;
                }

                double prefHeight = height;
                double prefWidth = (panePrefWidth / min) * cycleMinutes;
                double translateX = (panePrefWidth / min) * transrateMinutes;

                String text1 = topic.getTitle1();
                String text2 = Objects.nonNull(topic.getTitle2()) ? topic.getTitle2() : "";
                String text3 = topic.getTitle3();
                String fontColor1 = topic.getFontColor();
                String backColor = topic.getBackColor();
                String backColor2 = topic.getBackColor2();
                String planStart = formatter_MMddHHmm.format(topic.getPlanStartTime());
                String planEnd = formatter_MMddHHmm.format(topic.getPlanEndTime());

                // ラベル
                Label textLabel = new Label ();
                textLabel.setText(text1 + "\n" + text2 + "\n" + text3);
                textLabel.setStyle("-fx-font-size:" + config.getItemFontSize() + "; -fx-text-fill: " + fontColor1 + "; -fx-background-color:#00000000;");
                textLabel.setAlignment(Pos.CENTER);
                textLabel.setTextAlignment(TextAlignment.CENTER);
                textLabel.setPrefSize(prefWidth, prefHeight);
                textLabel.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                textLabel.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

                // 進捗表示背景ラベル
                Label label = new Label ();
                label.setStyle("-fx-background-color:" + backColor + ";" + panelStyle.get());
                label.setAlignment(Pos.CENTER);
                label.setPrefSize(prefWidth, prefHeight);
                label.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                label.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

                // 進捗表示ラベル
                Label label2 = new Label ();
                if (Objects.nonNull(topic.getProgress())) {
                    label2.setStyle("-fx-background-color:" + backColor2 + ";");
                    label2.setAlignment(Pos.CENTER);
                    label2.setPrefSize(prefWidth * topic.getProgress(), prefHeight);
                    if (topic.getProgress() == 1) {
                        label2.setPrefSize(prefWidth, prefHeight);
                    } else {
                        // 現在時刻までバーを表示
                        Date nowTime = new Date();
                        timeLineTranslateX = getTimeLineTranslateX(nowTime);
                        label2.setPrefSize(timeLineTranslateX - translateX, prefHeight);
                    }
                    label2.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                    label2.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                }

                // 点滅表示
                if (topic.isBlink()) {
                    blinking.play(label2);
                    blinking.play(label);
                    blinking.play(textLabel);
                }

                this.buildTooltip(textLabel, text1 + "\n" + text2 + "\n" + text3 + "\n" + planStart + " - " + planEnd);

                AnchorPane.setLeftAnchor(label2, translateX);
                AnchorPane.setLeftAnchor(label, translateX);
                AnchorPane.setLeftAnchor(textLabel, translateX);
                if ((AgendaDisplayPatternEnum.DisplayPerKanban.equals(this.config.getHorizonAgendaDisplayPattern())
                        || (!DisplayModeEnum.WORKER.equals(this.config.getMode())) && !DisplayModeEnum.LINE.equals(this.config.getMode()) && !DisplayModeEnum.KANBAN.equals(this.config.getMode()))
                        && Objects.nonNull(topic.getRow())) {
                    AnchorPane.setTopAnchor(label2, prefHeight * topic.getRow());
                    AnchorPane.setTopAnchor(label, prefHeight * topic.getRow());
                    AnchorPane.setTopAnchor(textLabel, prefHeight * topic.getRow());
                } else {
                    AnchorPane.setTopAnchor(label2, prefHeight * rows);
                    AnchorPane.setTopAnchor(label, prefHeight * rows);
                    AnchorPane.setTopAnchor(textLabel, prefHeight * rows);
                }

                pane.getChildren().addAll(label);
                if (Objects.nonNull(topic.getProgress())) { pane.getChildren().addAll(label2); }
                pane.getChildren().addAll(textLabel);

                logger.info("{} {} {}", topic.getKanbanId(), text1, text3);
            }
            ++rows;
        }

        if (isActual && planned) {
            pane.setVisible(false);
        }

        return pane;
    }

    /**
     * コンテンツを描画する。
     */
    private void draw() {
        try {
            logger.info("draw start.");
            
            if (Objects.isNull(this.agendas) || this.agendas.size() <= 0) {
                return;
            }

            if (widthPaneZoom > 1) {
                panePrefWidth = widthPaneZoom;
            }
            
            double cellHeight = this.config.isShowProgress() ? this.config.getHorizonRowHight() * 0.75 :  this.config.getHorizonRowHight();
            double limitCellNum = this.contentPane.getHeight() / cellHeight; // 上限縦軸カラム数
            
            // アジェンダを画面サイズに合わせて分割
            List<List<Entry<Long, Agenda>>> separated = new ArrayList<>();
            List<Entry<Long, Agenda>> list = new ArrayList<>();
            int totalNum = 0;
            for (Entry<Long, Agenda> agendaEntry : this.agendas.entrySet()) {
                Agenda agenda = agendaEntry.getValue();

                int planNum = this.getPlanNum(agenda);
                int actualNum = this.getActualNum(agenda);

                if (list.isEmpty() || totalNum + planNum + actualNum <= limitCellNum) {
                    totalNum += planNum + actualNum;
                    list.add(agendaEntry);
                } else {
                    separated.add(list);
                    totalNum = planNum + actualNum;
                    list = new ArrayList<>();
                    list.add(agendaEntry);
                }
            }

            if (!list.isEmpty()) {
                separated.add(list);
            }

            if (config.isTogglePages() && separated.size() > 1) {
                // ページ切り替え
                List<KeyFrame> keyframes = IntStream.range(0, separated.size())
                        .boxed()
                        .map(i -> new KeyFrame(Duration.seconds(i * config.getHorizonToggleTime()), (event) -> {
                            drawRows(separated.get(i));
                        }))
                        .collect(Collectors.toList());

                // ループの最後と先頭が同じ時間になってしまうため最後の要素を再度追加
                keyframes.add(new KeyFrame(Duration.seconds(separated.size() * config.getHorizonToggleTime()), event -> {
                    drawRows(separated.get(separated.size() - 1));
                }));

                if (Objects.nonNull(pageToggleTimeline)) {
                    pageToggleTimeline.stop();
                    pageToggleTimeline.getKeyFrames().clear();
                }

                pageToggleTimeline = new Timeline();
                pageToggleTimeline.getKeyFrames().addAll(keyframes);
                pageToggleTimeline.setCycleCount(Timeline.INDEFINITE);
                pageToggleTimeline.play();

            } else {
                if (separated.size() >= 2) {
                    scrollBarHeight.setVisible(true);
                }
                
                if (Objects.nonNull(pageToggleTimeline)) {
                    pageToggleTimeline.stop();
                    pageToggleTimeline.getKeyFrames().clear();
                }
                drawRows(separated.stream().flatMap(elm -> elm.stream()).collect(Collectors.toList()));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("draw end.");
        }
    }

    // バーの背景を変更する
    private final Function<Boolean, String> backColorSelectorOfChangeBarColor = (isNowTime)-> isNowTime?this.config.getNowBarColor():"white";

    // バーの背景を変更しない
    private final Function<Boolean, String> backColorSelectorOfNonChangeBarColor = (isNowTime)-> "white";

    /**
     * 時間軸を表示する。
     */
    private void showTimeColumn() {
        try {
            logger.info("showTimeColumn start.");
            
            double fontSize = config.getItemFontSize();

            horizonPane.getChildren().clear();
            gridPane.getChildren().clear();

            // 現行のカラー指定がある場合はカラーを設定する
            final String nowBackColor = config.getNowBarColor();
            Function<Boolean, String> backColorSelector = Objects.nonNull(nowBackColor)
                    ? backColorSelectorOfChangeBarColor
                    : backColorSelectorOfNonChangeBarColor;
            
            Date date;
            Calendar calendar = Calendar.getInstance();
            TimeScaleEnum timeScale = config.getHorizonTimeScale();

            switch (timeScale) {
                case Time:
                default:
                    date = new Date(this.startTime.getTime());
                    if (!(endTime.compareTo(date) > 0)) {
                        break;
                    }
                    
                    //自動スクロールがONの場合は時間軸が左右均等に広がるようにスタート位置を調整する距離を半分にする
                    double controlTimeColumnX = this.timeColumnX;
                    if (config.isAutoScroll()) {
                        controlTimeColumnX = this.timeColumnX / 2;
                    }
                    
                    if (config.getHorizonTimeUnit() % 60 != 0) {
                        double cycleTimeLineStartEnd = endTime.getTime() - startTime.getTime();
                        double countLineTime = cycleTimeLineStartEnd / MILLIS_PER_MINUTE / config.getHorizonTimeUnit();
                        flgColumnZoom = false;
                        columnZoomRate = 1.0;
                        widthPaneZoom = this.contentPane.getPrefWidth() * this.zoomRate * this.columnZoomRate * windowZoomRate + (this.zoomRate * this.timeColumnX);
                        double timeCellWidth = widthPaneZoom / (countLineTime);

                        final long now = (new Date()).getTime();
                        long fromTime = this.currentData.getFromDate().getTime() + date.getTime() - DateUtils.truncate(date, Calendar.DAY_OF_MONTH).getTime();
                        long toTime = 0;
                        for (int i = 0; i < countLineTime; i++) {
                            Label label = new Label();
                            label.setText(formatter.format(date));
                            label.setPrefWidth(timeCellWidth);
                            label.setPrefHeight(timeColumnPrefHeight);
                            label.setAlignment(Pos.CENTER);
                            label.setTranslateX(0.0);
                            date.setTime(date.getTime() + config.getHorizonTimeUnit() * 60000L);

                            toTime = fromTime + config.getHorizonTimeUnit() * 60000L;
                            label.setStyle("-fx-background-color: " + backColorSelector.apply(fromTime <= now && now < toTime) + "; -fx-border-height: 1; -fx-border-color: black; -fx-font-size:" + fontSize);

                            AnchorPane.setLeftAnchor(label, timeCellWidth * i - controlTimeColumnX);
                            horizonPane.getChildren().add(label);

                            // グリッドを描画
                            drawGrid(gridPane, this.contentPane.getHeight(), timeCellWidth * (i) - controlTimeColumnX);
                            fromTime = toTime;
                        }
                        drawGrid(gridPane, this.contentPane.getHeight(), timeCellWidth * (countLineTime) - controlTimeColumnX);
                    } else {
                        // 時間単位60×N分の場合
                        double cycleTimeLineStartEnd = endTime.getTime() - date.getTime();
                        double countLineTime = cycleTimeLineStartEnd / MILLIS_PER_MINUTE / config.getHorizonTimeUnit();
                        flgColumnZoom = false;
                        columnZoomRate = 1.0;
                        widthPaneZoom = this.contentPane.getPrefWidth() * this.zoomRate * this.columnZoomRate * windowZoomRate + (this.zoomRate * this.timeColumnX);
                        double timeCellWidth = widthPaneZoom / (countLineTime);
                        FastDateFormat format = formatter_HH;

                        calendar.setTime(date);
                        double current = calendar.getTimeInMillis() / MILLIS_PER_MINUTE;
                        calendar.add(Calendar.HOUR, config.getHorizonTimeUnit() / 60);
                        calendar.set(Calendar.MINUTE, 0);
                        double next = calendar.getTimeInMillis() / MILLIS_PER_MINUTE;
                        // 開始端から次のN時0分までの分数
                        double startMiniutes = next - current;
                        double startWidth = timeCellWidth * startMiniutes / config.getHorizonTimeUnit();
                        countLineTime = countLineTime - startMiniutes / config.getHorizonTimeUnit();

                        double totalWidth = 0;

                        final long now = (new Date()).getTime();
                        long fromTime = this.currentData.getFromDate().getTime() + date.getTime() - DateUtils.truncate(date, Calendar.DAY_OF_MONTH).getTime();
                        long toTime = 0;
                        for (int i = 0; i < countLineTime + 1; i++) {

                            double width = i==0 ? startWidth : timeCellWidth;

                            Label label = new Label();
                            label.setText(format.format(date));
                            label.setPrefWidth(width);
                            label.setPrefHeight(timeColumnPrefHeight);
                            label.setAlignment(Pos.CENTER);
                            label.setTranslateX(0.0);

                            AnchorPane.setLeftAnchor(label, totalWidth - controlTimeColumnX);
                            horizonPane.getChildren().add(label);

                            if (i == 0) {
                                date.setTime(date.getTime() + (long)startMiniutes * 60000L);
                                toTime = fromTime + (long)startMiniutes * 60000L;
                            } else {
                                date.setTime(date.getTime() + config.getHorizonTimeUnit() * 60000L);
                                toTime = fromTime + config.getHorizonTimeUnit() * 60000L;
                            }
                            label.setStyle("-fx-background-color: " + backColorSelector.apply(fromTime <= now && now < toTime) + "; -fx-border-height: 1; -fx-border-color: black; -fx-font-size:" + fontSize);
                            // グリッドを描画
                            drawGrid(gridPane, this.contentPane.getHeight(), totalWidth - controlTimeColumnX);
                            totalWidth += width;
                            fromTime = toTime;
                        }
                        drawGrid(gridPane, this.contentPane.getHeight(), totalWidth - controlTimeColumnX);
                    }
                    break;
                case Day: {
                        date = new Date(this.currentData.getFromDate().getTime());
                        flgColumnZoom = this.config.getHorizonShowDays() > this.config.getHorizonColumnNum();
                        columnZoomRate = flgColumnZoom ? 1.0 * this.config.getHorizonShowDays() / this.config.getHorizonColumnNum() : 1.0;
                    	widthPaneZoom = this.contentPane.getPrefWidth() * this.zoomRate * this.columnZoomRate * windowZoomRate + this.timeColumnX;
                        double dayCellWidth = widthPaneZoom / this.config.getHorizonShowDays();

                        // 日付がきつきつに表示される場合は、フォーマットを「M/d」にする
                        Text helper = new Text("9999/99/99");
                        helper.setFont(new Font(fontSize));
                        FastDateFormat fomatter = formatter_yyyyMMddEEE;
                        if ((dayCellWidth / 2) < helper.getBoundsInLocal().getWidth()) {
                            fomatter = formatter_MMdd;
                        }

                        final Date now = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
                        for (int i = 0; i < this.config.getHorizonShowDays(); i++) {
                            // 休日スキップEEE
                            while (!config.getHorizonShowHoliday() && model.getHolidays().contains(date)) {
                                // 1日進める
                                date = DateUtils.addDays(date, 1);
                            }

                            Label label = new Label();
                            label.setText(fomatter.format(date));
                            label.setPrefWidth(dayCellWidth);
                            label.setPrefHeight(timeColumnPrefHeight);
                            label.setAlignment(Pos.CENTER);
                            label.setTranslateX(0.0);
                            label.setStyle("-fx-background-color: " + backColorSelector.apply(date.equals(now)) + ";-fx-border-height: 1;-fx-border-color: black;-fx-font-size:" + fontSize);
                            AnchorPane.setLeftAnchor(label, dayCellWidth * i - this.timeColumnX);
                            horizonPane.getChildren().add(label);

                            // グリッドを描画
                            drawGrid(gridPane, this.contentPane.getHeight(), dayCellWidth * (i) - this.timeColumnX);
                            // 1日進める
                            date = DateUtils.addDays(date, 1);
                        }
                        drawGrid(gridPane, this.contentPane.getHeight(), dayCellWidth * (this.config.getHorizonShowDays()) - this.timeColumnX);
                    }
                    break;
                case HalfDay: {
                        date = new Date(this.currentData.getFromDate().getTime());
                        flgColumnZoom = this.config.getHorizonShowDays() > this.config.getHorizonColumnNum();
                        columnZoomRate = flgColumnZoom ? 1.0 * this.config.getHorizonShowDays() / this.config.getHorizonColumnNum() : 1.0;
                    	widthPaneZoom = this.contentPane.getPrefWidth() * this.zoomRate * this.columnZoomRate * windowZoomRate + this.timeColumnX;

                        double halfDayCellWidth = widthPaneZoom / this.config.getHorizonShowDays();
                        double am = (43200000L - (startTime.getTime() - timeDiff)) / MILLIS_PER_MINUTE;  // 43200000 = 12 * 60 * 60 * 1000
                        double pm = ((endTime.getTime() - timeDiff) - 43200000L) / MILLIS_PER_MINUTE;
                        am = am > 0 ? am : 0;
                        pm = pm > 0 ? pm : 0;

                        final boolean isAm = (new Date().getTime() - DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime()) < 43200000L;
                        final Date now = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
                        for (int i = 0; i < this.config.getHorizonShowDays(); i++) {
                            // 休日スキップ
                            while (!config.getHorizonShowHoliday() && model.getHolidays().contains(date)) {
                                // 1日進める
                                date = DateUtils.addDays(date, 1);
                            }
                            Label dayLabel = new Label();
                            dayLabel.setText(formatter_yyyyMMddEEE.format(date));
                            dayLabel.setPrefWidth(halfDayCellWidth);
                            dayLabel.setPrefHeight(timeColumnPrefHeight / 2);
                            dayLabel.setAlignment(Pos.CENTER);
                            dayLabel.setTranslateX(0.0);
                            dayLabel.setStyle("-fx-background-color: " + backColorSelector.apply(false/*date.equals(now)*/) + ";-fx-border-height: 1;-fx-border-color: black;" + "-fx-font-size:" + fontSize);
                            AnchorPane.setLeftAnchor(dayLabel, halfDayCellWidth * i - this.timeColumnX);
                            AnchorPane.setTopAnchor(dayLabel, 0.0);
                            horizonPane.getChildren().add(dayLabel);

                            Label amLabel = new Label();
                            amLabel.setText(LocaleUtils.getString("key.AnteMeridian"));
                            amLabel.setPrefWidth(halfDayCellWidth * (am / (am + pm)));
                            amLabel.setPrefHeight(timeColumnPrefHeight / 2);
                            amLabel.setAlignment(Pos.CENTER);
                            amLabel.setTranslateX(0.0);
                            amLabel.setStyle("-fx-background-color: " + backColorSelector.apply(date.equals(now) && isAm) + ";-fx-border-height: 1;-fx-border-color: black;" + "-fx-font-size:" + fontSize);
                            AnchorPane.setLeftAnchor(amLabel, halfDayCellWidth * i - this.timeColumnX);
                            AnchorPane.setTopAnchor(amLabel, dayLabel.getPrefHeight());
                            horizonPane.getChildren().add(amLabel);

                            Label pmLabel = new Label();
                            pmLabel.setText(LocaleUtils.getString("key.PostMeridian"));
                            pmLabel.setPrefWidth(halfDayCellWidth * (pm / (am + pm)));
                            pmLabel.setPrefHeight(timeColumnPrefHeight / 2);
                            pmLabel.setAlignment(Pos.CENTER);
                            pmLabel.setTranslateX(0.0);
                            pmLabel.setStyle("-fx-background-color: " + backColorSelector.apply(date.equals(now) && !isAm) + ";-fx-border-height: 1;-fx-border-color: black;" + "-fx-font-size:" + fontSize);
                            AnchorPane.setLeftAnchor(pmLabel, halfDayCellWidth * i + amLabel.getPrefWidth() - this.timeColumnX);
                            AnchorPane.setTopAnchor(pmLabel, dayLabel.getPrefHeight());
                            horizonPane.getChildren().add(pmLabel);

                            // グリッドを描画
                            drawGrid(gridPane, this.contentPane.getHeight(), halfDayCellWidth * (i) - this.timeColumnX);
                            // 1日進める
                            date = DateUtils.addDays(date, 1);
                        }
                        drawGrid(gridPane, this.contentPane.getHeight(), halfDayCellWidth * (this.config.getHorizonShowDays()) - this.timeColumnX);
                    }
                    break;
                case Month: {
                        calendar.setTime(this.currentData.getFromDate());
                        flgColumnZoom = this.config.getHorizonShowMonths() > this.config.getHorizonColumnNum();
                        columnZoomRate = flgColumnZoom ? 1.0 * this.config.getHorizonShowMonths() / this.config.getHorizonColumnNum() : 1.0;
                    	widthPaneZoom = this.contentPane.getPrefWidth() * this.zoomRate * this.columnZoomRate * windowZoomRate + this.timeColumnX;

                        double totalWidth = 0;

                        // 総描画分数
                        double total = (this.currentData.getToDate().getTime() - this.currentData.getFromDate().getTime()) / MILLIS_PER_MINUTE;
                        final double now = new Date().getTime() / MILLIS_PER_MINUTE;
                        for (int i = 0; i < this.config.getHorizonShowMonths() + 1; i++) {

                            String text = formatter_yyyyMM.format(calendar);
                            double currentMonth = calendar.getTimeInMillis() / MILLIS_PER_MINUTE;
                            // 翌月の日付
                            if (i != this.config.getHorizonShowMonths()) {
                                calendar.set(Calendar.DATE, 1);
                                calendar.add(Calendar.MONTH, 1);
                            } else {
                                calendar.setTime(this.currentData.getToDate());
                            }
                            double nextMonth = calendar.getTimeInMillis() / MILLIS_PER_MINUTE;
                            // 今月～翌月間の分数
                            double miniutes = nextMonth - currentMonth;

                            double width = (widthPaneZoom / total) * miniutes;

                            Label label = new Label();
                            label.setText(text);
                            label.setPrefWidth(width);
                            label.setPrefHeight(timeColumnPrefHeight);
                            label.setAlignment(Pos.CENTER);
                            label.setTranslateX(0.0);
                            label.setStyle("-fx-background-color: " + backColorSelector.apply(currentMonth <=now && now < nextMonth) + ";-fx-border-height: 1;-fx-border-color: black;" + "-fx-font-size:" + fontSize);
                            AnchorPane.setLeftAnchor(label, totalWidth - this.timeColumnX);
                            horizonPane.getChildren().add(label);

                            // グリッドを描画
                            drawGrid(gridPane, this.contentPane.getHeight(), totalWidth - this.timeColumnX);
                            totalWidth += width;

                        }
                        drawGrid(gridPane, this.contentPane.getHeight(), totalWidth - this.timeColumnX);
                    }
                    break;
                case Week: {
                        calendar.setTime(this.currentData.getFromDate());
                        Calendar toCalendar = Calendar.getInstance();
                        toCalendar.setTime(this.currentData.getToDate());

                        int preYear = calendar.get(Calendar.YEAR) != toCalendar.get(Calendar.YEAR) ? calendar.getActualMaximum(Calendar.DAY_OF_YEAR) : 0;
                        double count = (toCalendar.get(Calendar.DAY_OF_YEAR) + preYear - calendar.get(Calendar.DAY_OF_YEAR)) / 7.0;

                        // 総描画分数
                        double totalWeekMinutes = (this.currentData.getToDate().getTime() - this.currentData.getFromDate().getTime()) / MILLIS_PER_MINUTE;

                        flgColumnZoom = count > this.config.getHorizonColumnNum();
                        columnZoomRate = flgColumnZoom ? totalWeekMinutes / (10080L * this.config.getHorizonColumnNum()) : 1.0;
                    	widthPaneZoom = this.contentPane.getPrefWidth() * this.zoomRate * this.columnZoomRate * windowZoomRate + this.timeColumnX;

                        Calendar nowCalendar = Calendar.getInstance();
                        //nowCalendar.setTime(new Date());
                        if (calendar.get(Calendar.YEAR) == toCalendar.get(Calendar.YEAR)) {
                            Label label = new Label();
                            label.setText(String.valueOf(calendar.get(Calendar.YEAR)));
                            label.setPrefWidth(widthPaneZoom);
                            label.setPrefHeight(timeColumnPrefHeight / 2);
                            label.setAlignment(Pos.CENTER);
                            label.setTranslateX(0.0);
                            label.setStyle("-fx-background-color: " + backColorSelector.apply(false/*nowCalendar.get(Calendar.YEAR)==calendar.get(Calendar.YEAR)*/) + ";-fx-border-height: 1;-fx-border-color: black;" + "-fx-font-size:" + fontSize);
                            AnchorPane.setLeftAnchor(label, 0.0 - this.timeColumnX);
                            horizonPane.getChildren().add(label);
                        } else {

                            String year = String.valueOf(calendar.get(Calendar.YEAR));
                            String nextYear = String.valueOf(toCalendar.get(Calendar.YEAR));
                            //boolean yearIsNow = nowCalendar.get(Calendar.YEAR)==calendar.get(Calendar.YEAR);
                            //boolean nextYearIsNow = nowCalendar.get(Calendar.YEAR)==toCalendar.get(Calendar.YEAR);

                            toCalendar.set(Calendar.DATE, 1);
                            toCalendar.set(Calendar.MONTH, 0); // 1月
                            toCalendar.add(Calendar.DATE, -1);

                            double width = widthPaneZoom / totalWeekMinutes * ((toCalendar.getTimeInMillis() - calendar.getTimeInMillis()) / MILLIS_PER_MINUTE);

                            Label label = new Label();
                            label.setText(year);
                            label.setPrefWidth(width);
                            label.setPrefHeight(timeColumnPrefHeight / 2);
                            label.setAlignment(Pos.CENTER);
                            label.setTranslateX(0.0);
                            label.setStyle("-fx-background-color: " + backColorSelector.apply(false/*yearIsNow*/) + ";-fx-border-height: 1;-fx-border-color: black;" + "-fx-font-size:" + fontSize);
                            AnchorPane.setLeftAnchor(label, 0.0 - this.timeColumnX);
                            horizonPane.getChildren().add(label);

                            Label nextYearLabel = new Label();
                            nextYearLabel.setText(nextYear);
                            nextYearLabel.setPrefWidth(widthPaneZoom - width);
                            nextYearLabel.setPrefHeight(timeColumnPrefHeight / 2);
                            nextYearLabel.setAlignment(Pos.CENTER);
                            nextYearLabel.setTranslateX(0.0);
                            nextYearLabel.setStyle("-fx-background-color: " + backColorSelector.apply(false/*nextYearIsNow*/) + ";-fx-border-height: 1;-fx-border-color: black;" + "-fx-font-size:" + fontSize);
                            AnchorPane.setLeftAnchor(nextYearLabel, label.getPrefWidth() - this.timeColumnX);
                            horizonPane.getChildren().add(nextYearLabel);
                        }

                        double totalWeekWidth = 0;

                        double nowWeek = nowCalendar.getTimeInMillis() / MILLIS_PER_MINUTE;
                        for (int i = 0; i < count; i++) {

                            int day = calendar.get(Calendar.WEEK_OF_YEAR);
                            double currentWeek = calendar.getTimeInMillis() / MILLIS_PER_MINUTE;
                            // 翌週の日付
                            if (i + 1 < count) {
                                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                                calendar.set(Calendar.DAY_OF_WEEK, 1);
                            } else {
                                calendar.setTime(this.currentData.getToDate());
                            }
                            double nextWeek = calendar.getTimeInMillis() / MILLIS_PER_MINUTE;
                            // 今週～翌週の分数
                            double miniutes = nextWeek - currentWeek;

                            double width = (widthPaneZoom / totalWeekMinutes) * miniutes;

                            Label label = new Label();
                            label.setText(String.valueOf(day));
                            label.setPrefWidth(width);
                            label.setPrefHeight(timeColumnPrefHeight / 2);
                            label.setAlignment(Pos.CENTER);
                            label.setTranslateX(0.0);
                            label.setStyle("-fx-background-color: " + backColorSelector.apply(currentWeek <= nowWeek && nowWeek < nextWeek) + ";-fx-border-height: 1;-fx-border-color: black;" + "-fx-font-size:" + fontSize);
                            AnchorPane.setLeftAnchor(label, totalWeekWidth - this.timeColumnX);
                            AnchorPane.setTopAnchor(label, timeColumnPrefHeight / 2);
                            horizonPane.getChildren().add(label);


                            // グリッドを描画
                            drawGrid(gridPane, this.contentPane.getHeight(), totalWeekWidth - this.timeColumnX);
                            totalWeekWidth += width;

                        }
                        drawGrid(gridPane, this.contentPane.getHeight(), totalWeekWidth - this.timeColumnX);
                    }
                    break;
            }
 
            if (!this.config.isAutoScroll() && flgColumnZoom && contentPane.getPrefWidth() < widthPaneZoom) {
                scrollBarWidth.setVisible(true);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
             logger.info("showTimeColumn end.");
        }
    }

    
    /**
     * グリッドを描画する。
     *
     * @param pane 描画対象
     * @param height 高さ
     */
    private void drawGrid(AnchorPane pane, double height, double x) {
        try {

            Line line = new Line(x, 0, x, height);
            line.setStroke(Color.LIGHTGRAY);
            line.setStrokeWidth(0.2);
            pane.getChildren().add(line);
            this.gridLines.add(line);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 休憩時間を描画する。
     *
     * @param pane　描画対象
     * @param height 高さ
     */
    private void drawBreakTime(AnchorPane pane, double height) {
        try {
            if(!this.config.isTimeScaleTime()){
                return;
            }
            if (Double.isNaN(height)) {
                return;
            }

            double workTime = (endTime.getTime() - startTime.getTime()) / MILLIS_PER_MINUTE;
            long oneDay = 24 * 60 * 60 * 1000;

            for (int n = 0; n<workTime/((double)oneDay /MILLIS_PER_MINUTE); ++n) {
                for (BreakTime breakTime : this.config.getBreakTimes()) {
                    BreakTimeInfoEntity entity = model.getBreakTime(breakTime.getId());
                    if (Objects.isNull(entity)) {
                        continue;
                    }

                    String startBreak = formatter_HHmmss.format(entity.getStarttime());
                    String endBreak = formatter_HHmmss.format(entity.getEndtime());

                    Date startBreakTime = DateUtils.parseDate(startBreak, pattern_HHmmss);
                    Date endBreakTime = DateUtils.parseDate(endBreak, pattern_HHmmss);

                    double cycleBreakTime = (endBreakTime.getTime() - startBreakTime.getTime()) / MILLIS_PER_MINUTE;
                    double translate = (startBreakTime.getTime() - startTime.getTime() + n*oneDay) / MILLIS_PER_MINUTE;
                    double prefWidth = (this.panePrefWidth / workTime) * cycleBreakTime;
                    double TranslateX = (this.panePrefWidth / workTime) * translate;

                    StringBuilder style = new StringBuilder();
                    style.append("-fx-font-size:");
                    style.append(this.config.getItemFontSize());
                    style.append("; -fx-text-fill: White;");
                    style.append("-fx-background-color: rgba(128, 128, 128, 0.5);");
                    style.append("-fx-border-height: 0.5;");
                    style.append("-fx-border-color: Black;");

                    Label label = new Label();
                    label.setText(entity.getBreaktimeName());
                    label.setStyle(style.toString());
                    label.setAlignment(Pos.CENTER);
                    label.setPrefSize(prefWidth, height);
                    label.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                    label.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                    label.setPickOnBounds(false);
                    AnchorPane.setLeftAnchor(label, TranslateX - this.timeColumnX);
                    // 自動スクロールがONの場合は時間軸が左右均等に広がるようにスタート位置を調整する距離を半分にする
                    if (config.isAutoScroll()) {
                        AnchorPane.setLeftAnchor(label, TranslateX - (this.timeColumnX / 2));
                    }
                    AnchorPane.setTopAnchor(label, 0.0);
                    AnchorPane.setBottomAnchor(label, 0.0);

                    buildTooltip(label, entity.getBreaktimeName() + " \n" + startBreak + " - " + endBreak);

                    pane.getChildren().add(label);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * タイムラインを描画する。
     */
    private void drawSystemTimeLine() {
        try {
            logger.info("drawSystemTimeLine start.");

            
            if (currentData.getFromDate() != null
                    && System.currentTimeMillis() > currentData.getFromDate().getTime()
                    && System.currentTimeMillis() < currentData.getToDate().getTime()) {
                systemTimeLine();
                model.setUpdate(true);
            } else if (model.getSystemTimer() != null) {
                timeLine.setVisible(false);
                //model.systemTimerCancel();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("drawSystemTimeLine end.");
        }
    }

    /**
     * タイムラインを表示する。
     */
    private void systemTimeLine() {
        logger.info("systemTimeLine start.");

        Date nowTime = new Date();
        timeLineTranslateX = getTimeLineTranslateX(nowTime);

        final boolean showTimeLine = config.getShowTimeLine() && timeLineTranslateX > 0
                        && timeLineTranslateX <= panePrefWidth;

        logger.info("systemTimeLine:{}, {}, {}; isAutoScroll = {}", startTime, endTime, nowTime, config.isAutoScroll());
        timeLine.setVisible(showTimeLine);

        //Handle auto scroll and time line
        if (config.isAutoScroll()) {
            // オートスクロール時のバーの位置
            double x = (timeLineTranslateX - (contentPane.getPrefWidth() * this.windowZoomRate / 2)) * - 1 ;
            scrollBarWidth.setVisible(false);
            contentPane.setTranslateX(x);
            horizonPane.setTranslateX(x);
            timeLine.setVisible(true);
            //自動スクロールがONの場合は時間軸が左右均等に広がるようにスタート位置を調整する距離を半分にする
            timeLine.setTranslateX((contentPane.getPrefWidth() * this.windowZoomRate / 2) + this.verticalPanePrefWidth - (this.timeColumnX / 2));
        } else {
            timeLine.setTranslateX(this.verticalPanePrefWidth + timeColumnTranslateX + timeLineTranslateX - this.timeColumnX);
        }

        logger.info("systemTimeLine end.");
    }

    /**
     * Get system time line translate_y
     *
     * @return timeLineTranslateX 時間を表す縦線のX軸方向の移動距離
     */
    private double getTimeLineTranslateX(Date now) {
        logger.info("getTimeLineTranslateX start. now={}, flgZoom={}, flgColumnZoom={}, widthPaneZoom={}", now, flgZoom, flgColumnZoom, widthPaneZoom);

        if ((flgZoom || flgColumnZoom) && widthPaneZoom > 0) {
            panePrefWidth = widthPaneZoom;
        }
        
        double transrateMinutes = this.calculateTransrateMinutes(now);
        if (transrateMinutes < 0) {
            return 0;
        }

        double totalDisplayMinutes;
        double workMinutes = (endTime.getTime() - startTime.getTime()) / MILLIS_PER_MINUTE;
        switch (config.getHorizonTimeScale()) {
            case Time:
            default:
                totalDisplayMinutes = workMinutes;
                break;
            case Day:
            case HalfDay:
                totalDisplayMinutes = workMinutes * config.getHorizonShowDays();
                break;
            case Month:
            case Week:
                totalDisplayMinutes = (this.currentData.getToDate().getTime() - this.currentData.getFromDate().getTime()) / MILLIS_PER_MINUTE;
                break;
        }
        return (panePrefWidth / totalDisplayMinutes) * transrateMinutes;
    }

    /**
     * Update display by update interval time
     *
     */
    @Override
    public void updateDisplay() {
        logger.info("updateDisplay start.");

        try {
            blinking.stop();

            contentPane.getChildren().clear();
            verticalPane.getChildren().clear();
            gridLines.clear();

            agendas = currentData.getAgendas();
            drawSystemTimeLine();
            showTimeColumn();
            draw();


            setScrollRange();

        } catch (Exception ex) {
            logger.fatal(ex, ex);

        } finally {
            logger.info("updateDisplay end.");
        }
    }

    /**
     * スクロール範囲を設定する。
     *
     */
    private void setScrollRange() {
        logger.info("setScrollRange start.");
        double max = 0;

        if (Objects.isNull(this.agendas)) {
            this.scrollBarHeight.setMin(0.0);
            this.scrollBarHeight.setMax(0.0);
            return;
        }

        int topicNum = 0;
        for (Agenda agenda : this.agendas.values()) {
            topicNum += this.getPlanNum(agenda) + this.getActualNum(agenda);
        }

        if (config.isTogglePages()) {
            max = this.contentPane.getPrefHeight() - (this.scrollBarHeight.getHeight() - 50);
        } else {
            max = this.config.getHorizonRowHight() * topicNum - (this.scrollBarHeight.getHeight() - 50);
        }
        this.scrollBarHeight.setMin(0.0);
        this.scrollBarHeight.setMax(Math.max(0.0, max));

        if (this.flgZoom || this.flgColumnZoom) {
            max = this.widthPaneZoom - (this.scrollBarWidth.getWidth() - 300);
        } else {
            max = this.panePrefWidth - (this.scrollBarWidth.getWidth() - 300);
        }
        this.scrollBarWidth.setMin(0.0);
        this.scrollBarWidth.setMax(Math.max(0.0, max));
        logger.info("setScrollRange end.");
    }
    
    /**
     * 進捗時間非表示時に時間軸の描画のスタート位置を調整するX軸の移動距離を取得する
     * @return timecolumnX 時間軸の描画のスタート位置を調整するX軸の移動距離
     */
    private double getTimeColumnX() {
        // 進捗時間表示時は時間軸の描画のスタート位置を移動しない
        if (config.getHorizonShowActualTime()) {
            return 0.0;
        }
        // 製品進捗表示時は時間軸の描画のスタート位置を移動しない
        if (config.isShowProgress()) {
            return 0.0;
        }
        
        // 予実表示が「予定と実績」以外の場合、予実エリアを表示しない
        double timeColumnX = this.verticalPanePrefWidth * 0.4;
        // 予実表示が「予定と実績」の場合、予実エリアの横幅を縮める
        if (PlanActualShowTypeEnum.PlanAndActual.equals(config.getHorizonPlanActualShowType())) {
            timeColumnX = this.verticalPanePrefWidth * 0.3;
        }
        
        return timeColumnX;
    }

    /**
     * ツールチップを構築する。
     *
     * @param control ツールチップを追加する対象
     * @param text ツールチップに表示するテキスト
     */
    private void buildTooltip(Control control, String text) {
        try {
            Tooltip toolTip = TooltipBuilder.build(control);
            toolTip.setText(text);
        } catch (Exception ex) {
        }
    }
    
    /**
     * 描画対象の分数を計算する
     * 
     * @param startDate 対象の開始日時
     * @param endDate 対象の終了日時
     * @return CycleMinutes 描画時間(分)
     */
    private double calculateCycleMinutes(Date startDate, Date endDate) {
        
        long st = startTime.getTime() - timeDiff; // 0:00から勤務開始時間まで
        long ed = endTime.getTime() - timeDiff; // 0:00から勤務終了時間まで
        long targetSt = startDate.getTime(); // 計算対象の開始時刻
        long targetEd = endDate.getTime(); // 計算対象の終了時刻
        long oneDay = 86400000L;

        switch (config.getHorizonTimeScale()) {
            case Month:
            case Week:
                if (targetSt < currentData.getFromDate().getTime()) {
                    targetSt = currentData.getFromDate().getTime();
                }
                if (currentData.getToDate().getTime()< targetEd) {
                    targetEd = currentData.getToDate().getTime();
                }
                return ((targetEd - targetSt) / MILLIS_PER_MINUTE > 0) ? (targetEd - targetSt) / MILLIS_PER_MINUTE : 0;
            default:
                if (this.config.isAutoScroll()) {
                    targetSt = Math.max(targetSt, currentData.getFromDate().getTime() + st - oneDay);
                    targetEd = Math.min(targetEd, currentData.getToDate().getTime() + ed - 2*oneDay + 1000L);
                    return ((targetEd - targetSt) > 0) ? (targetEd - targetSt) / MILLIS_PER_MINUTE : 0;
                }

                if (targetSt < currentData.getFromDate().getTime() + st) {
                    targetSt = currentData.getFromDate().getTime() + st;
                }
                if (currentData.getToDate().getTime() - (oneDay - ed) + MILLIS_PER_MINUTE < targetEd) {
                    targetEd = currentData.getToDate().getTime() - (oneDay - ed) + 60000L;
                }
                break;
        }

        long calcDay = currentData.getFromDate().getTime(); // 計算日の0:00

        long nonWorkTime = oneDay - (ed - st);
        long invisibleTime = 0;

        List<Long> holidays = model.getHolidays().stream().map(h -> h.getTime()).collect(Collectors.toList());
        
        while (calcDay < currentData.getToDate().getTime()) {
            
            // 休日判定
            if (!config.isShowHoliday() && holidays.contains(calcDay)) {
                if (calcDay - oneDay + ed <= targetSt && targetSt < calcDay + oneDay) {
                    // 休日中に開始した場合、次の日の開始時刻に修正
                    targetSt = calcDay + oneDay + st;
                }
                if (targetEd <= calcDay + oneDay + st) {
                    // 休日中に終了した場合、次の日の開始時刻に修正
                    targetEd = calcDay + oneDay + st;
                }
                if (targetSt <= calcDay - oneDay + ed && calcDay + oneDay + st <= targetEd ) {
                    // 丸々作業中なら
                    invisibleTime += oneDay;
                }
                // 計算対象日を翌日に進める
                calcDay = calcDay + oneDay;
                continue;
            }
            
            if (calcDay - oneDay + ed <= targetSt && targetSt < calcDay + st) {
                // 前日の終了時刻 ～ 計算日の開始時刻の間に計算対象の開始時刻がある場合
                targetSt = calcDay + st;
            }

            if (targetSt <= calcDay + ed) {
                if (calcDay + oneDay + st <= targetEd) {
                    invisibleTime += nonWorkTime;
                } else if (calcDay + ed <= targetEd) {
                    // 計算日の終了時刻 ～ 翌日の開始時刻の間に計算対象の終了時刻がある場合
                    targetEd = calcDay + ed;
                    break;
                } else {
                    break;
                }
            }
            // 計算対象日を翌日に進める
            calcDay = calcDay + oneDay;
        }

        double cycleTime = (targetEd - targetSt - invisibleTime) / MILLIS_PER_MINUTE;

        return (cycleTime > 0) ? cycleTime : 0;
    }
    
    /**
     * 対象が開始時刻から何分離れているか計算する
     *
     * @param targetDate 計算対象の日時
     * @return 対象が開始時刻から何分離れているか
     */
    private double calculateTransrateMinutes(Date targetDate) {

        long st = startTime.getTime() - timeDiff; // 0:00から勤務開始時間まで
        long ed = endTime.getTime() - timeDiff; // 0:00から勤務終了時間まで
        long target = targetDate.getTime();
        long oneDay = 86400000L;

        if (this.config.isAutoScroll()){
            // 日跨ぎの為、開始日時を1日分シフトする。
            st -= oneDay;
            ed -= oneDay;
        }

        switch (config.getHorizonTimeScale()) {
            case Month:
            case Week:
                if (target <= currentData.getFromDate().getTime()) {
                    target = currentData.getFromDate().getTime();
                } else if (currentData.getToDate().getTime() < target) {
                    target = currentData.getToDate().getTime();
                }
                return (target - currentData.getFromDate().getTime()) / MILLIS_PER_MINUTE;
            default:
                if (target <= currentData.getFromDate().getTime() + st) {
                    target = currentData.getFromDate().getTime() + st;
                } else if (currentData.getToDate().getTime() - (oneDay - ed) + MILLIS_PER_MINUTE <= target) {
                    target = currentData.getToDate().getTime() - (oneDay - ed) + 60000L;
                }
                break;
        }

        long calcDay = currentData.getFromDate().getTime(); // 計算日の0:00
        long nonWorkTime = oneDay - (ed - st);
        long invisibleTime = 0;
        List<Long> holidays = model.getHolidays().stream().map(h -> h.getTime()).collect(Collectors.toList());

        while (calcDay < currentData.getToDate().getTime()) {

            // 休日判定
            if (!config.isShowHoliday() && holidays.contains(calcDay)) {
                if (calcDay - oneDay + ed <= target && target <= calcDay + oneDay) {
                    // 休日中に対象がある場合、次の日の開始時刻に修正
                    target = calcDay + oneDay + st;
                }

                // 非表示分数に休日を加算
                invisibleTime += oneDay;

                // 計算対象日を翌日に進める
                calcDay = calcDay + oneDay;
                continue;
            }
            
            if (calcDay - oneDay + ed <= target && target < calcDay + st) {
                // 前日の終了時刻 ～ 計算日の開始時刻の間に計算対象がある場合
                target = calcDay + st;
            }
            
            if (target < calcDay + ed) {
                break;
            } else {
                invisibleTime += nonWorkTime ;
            }
            
            // 計算対象日を翌日に進める
            calcDay = calcDay + oneDay;
        }

        return (target - (currentData.getFromDate().getTime() + st) - invisibleTime) / MILLIS_PER_MINUTE;
    }
    
    /**
     * 計画トピック最大数を取得する
     *
     * @param agenda 対象アジェンダ
     * @return 最大サイズ
     */
    private int getPlanNum(Agenda agenda) {
        
        if (!this.config.isShowPlan()) {
            return 0;
        }

        if (AgendaDisplayPatternEnum.DisplayPerKanban.equals(this.config.getHorizonAgendaDisplayPattern())
            || (!DisplayModeEnum.WORKER.equals(this.config.getMode())) && !DisplayModeEnum.KANBAN.equals(this.config.getMode()) && !DisplayModeEnum.LINE.equals(this.config.getMode())) {
            if (agenda.getRowCount() > 0) {
                return agenda.getRowCount();
            }
        }


        List<AgendaTopic> topics = agenda.getPlans().stream()
                .map(AgendaPlan::getTopics)
                .map(Map::values)
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        int num = AgendaGroup.groupBy(topics).size();
        return Math.max(num, 1);

    }

      /**
     * 実績トピック最大数を取得する
     *
     * @param agenda 対象アジェンダ
     * @return 最大サイズ
     */
    private int getActualNum(Agenda agenda) {
        
        if (!this.config.isShowActual()) {
            return 0;
        }

        if(AgendaDisplayPatternEnum.DisplayPerKanban.equals(this.config.getHorizonAgendaDisplayPattern())
        || (!DisplayModeEnum.WORKER.equals(this.config.getMode())) && !DisplayModeEnum.KANBAN.equals(this.config.getMode()) && !DisplayModeEnum.LINE.equals(this.config.getMode())) {
            if (agenda.getRowCount() > 0) {
                return agenda.getRowCount();
            }
        }

        List<AgendaTopic> topics = agenda.getActuals().stream()
                .map(AgendaGroup::getTopics)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        int num = AgendaActualGroup.groupBy(topics).size();
        return Math.max(num, 1);
    }

    /**
     * 16進数の色相文字列をRGBA文字列に変換
     *
     * @param targetStr　16進数の色相文字列 (例)#FFFFFF
     * @param alpha　透過度
     * @return RGBA文字列
     */
    private String changeRgba(String targetStr, double alpha){
        String result;
        int r;
        int g;
        int b;
        if (targetStr.length() == 7) {
            r = hex2int(targetStr.substring(1,3));
            g = hex2int(targetStr.substring(3,5));
            b = hex2int(targetStr.substring(5,7));
        } else {
            return targetStr;
        }
        try {
            result = String.format("rgba(%d, %d, %d, %f)", r, g, b, alpha);
        } catch (Exception ex) {
            result = targetStr;
        }
        
        return result;
    }
    
    /**
     * 16進数の文字列を10進数に変換
     *
     * @param targetStr　16進数の文字列
     * @return 10進数
     */
    private int hex2int(String targetStr){
        int v;
        try {
            v = Integer.parseInt(targetStr, 16);
        } catch (NumberFormatException e) {
            v = 0;
        }
        return v;
    }
}
