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
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import static java.lang.Math.max;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
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
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.andon.enumerate.AgendaDisplayPatternEnum;
import jp.adtekfuji.andon.enumerate.DisplayModeEnum;
import jp.adtekfuji.andon.utility.MonitorTools;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 計画実績ペインのコントローラー
 *
 * @author s-heya
 */
@FxComponent(id = "AgendaCompo", fxmlPath = "/fxml/compo/agenda_andon_compo.fxml")
public class AgendaCompoController implements Initializable, ArgumentDelivery, ComponentHandler, AgendaCompoInterface {

    private final Logger logger = LogManager.getLogger();

    private final String TRANSPARENT = "-fx-background-color:transparent;";
    private final String BORDER_LINE = "-fx-border-style:solid;-fx-border-width:0 0 0 1;-fx-border-color:#404040;-fx-background-color:transparent;";
    private final String pattern[] = {"HH:mm"};
    private final String pattern_HHmmss[] = {"HH:mm:ss"};
    private final FastDateFormat formatter = FastDateFormat.getInstance("HH:mm");
    private final FastDateFormat formatter_HHmmss = FastDateFormat.getInstance("HH:mm:ss");

    private final SceneContiner sc = SceneContiner.getInstance();
    private final ConfigData config = ConfigData.getInstance();
    private ResourceBundle rb;// = LocaleUtils.getBundle("locale.locale");
    private final CurrentData currentData = CurrentData.getInstance();
    private final AgendaModel model = AgendaModel.getInstance();
    private final Blinking blinking = Blinking.getInstance();

    private Map<Long, Agenda> agendas = new LinkedHashMap<>();

    private Timeline pageToggleTimeline;

    @FXML
    private AnchorPane scalePane;
    @FXML
    private AnchorPane contentPane;
    @FXML
    private AnchorPane verticalPane;
    @FXML
    private AnchorPane horizonPane;
    @FXML
    private AnchorPane blindPane;
    @FXML
    private ScrollBar scrollBarHeight;
    @FXML
    private ScrollBar scrollBarWidth;
    @FXML
    private Slider sliderZoom;
    @FXML
    private Button labelZoom;
    @FXML
    private Label timeLine;

    private double timeColumnTranslateY = 0.0;
    private double timeLineTranslateY = 0.0;
//    private double timeTranslateYPane = 0.0;
//    private double scale = 0;
    private double panePrefHeight;
    private double timeColumnPrefWidth;
    private double agendaHeaderPanePrefHeight;
    private double hightPaneZoom = 0;
    //private double widthPaneZoom = 0;
    private boolean flgZoom = false;
//    private boolean flgCheckEndTime = false;
    private int flgShowZoom = 1;
    private int flgKeyPress = 1;

    private Date startTime;
    private Date endTime;
    private Double timeColumnY; // 時間軸の位置調整距離

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.rb = rb;

            // ウィンドウをディスプレイの解像度に合わせる
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            logger.info("Screen: Width={0} Height={1}", visualBounds.getWidth(), visualBounds.getHeight());

            contentPane.setPrefHeight(visualBounds.getHeight() - 160.0);
            contentPane.setPrefWidth(visualBounds.getWidth() - 120.0);
            horizonPane.setPrefWidth(visualBounds.getWidth() - 120.0);
            verticalPane.setPrefHeight(visualBounds.getHeight() - 160.0);

            timeColumnPrefWidth = verticalPane.getPrefWidth();
            agendaHeaderPanePrefHeight = horizonPane.getPrefHeight();
            
            // 時間軸の位置調整をするY軸の移動距離の初期化
            this.timeColumnY = config.getShowActualTime() ? 0 : 25.0;
            
            // 進捗時間表示のONとOFFで横軸の幅を調整する
            horizonPane.setPrefHeight(agendaHeaderPanePrefHeight - this.timeColumnY);
            blindPane.setPrefHeight(agendaHeaderPanePrefHeight - this.timeColumnY);
            
            blinking.stop();
            //unvisible scrollbars
            scrollBarWidth.setVisible(false);
            scrollBarHeight.setVisible(false);
            sliderZoom.setVisible(false);

            this.startTime = this.config.getStartTime();
            this.endTime = this.config.getEndTime();

            if (config.isAutoScroll()) {
                this.startTime = adtekfuji.utility.DateUtils.getBeginningOfDate(this.startTime);
                this.endTime = DateUtils.addDays(this.startTime, 1);
                double rows = (this.endTime.getTime() - this.startTime.getTime()) / (this.config.getAutoScrollTime().getTime() - adtekfuji.utility.DateUtils.min().getTime());
                this.panePrefHeight = contentPane.getPrefHeight() * rows;

                this.labelZoom.setVisible(false);
                this.scrollBarHeight.setVisible(false);
            } else {
                this.panePrefHeight = this.contentPane.getPrefHeight() + this.timeColumnY;
            }

            currentData.setFromDate(currentData.getKeepTargetDay());
            //Set scroll bar change event
            setScrollBarListener();
            //Show time column
            showTimeColumn();
            //show list kanban agenda
            draw();
            //Time line
            drawSystemTimeLine();

            model.setController(this);

            // 表示倍率を設定
            SceneContiner sc = SceneContiner.getInstance();
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
        double ratio = newWidth / oldWidth;
        this.scalePane.getTransforms().clear();
        this.scalePane.getTransforms().add(new Scale(ratio, ratio, 0, 0));
    }
    
    @Override
    public void setArgument(Object argument) {
    }

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
     * Handle scrollbar event
     *
     */
    private void setScrollBarListener() {

        int diplayedSideNum = config.getDiplayedSideNum();

        this.setScrollRange(diplayedSideNum);

        sliderZoom.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            flgZoom = true;
            double hightPane = contentPane.getPrefHeight() + this.timeColumnY;
            //double widthPane = contentPane.getPrefWidth();
            hightPaneZoom = hightPane * (new_val.doubleValue() / 100.0);
            
            // 幅はズーム機能を適用しない
            //widthPaneZoom = widthPane * (value / 100);
            //widthPaneZoom = widthPane;
            //scrollBarWidth.setVisible(true);
            scrollBarHeight.setVisible(true);
            setScrollRange(diplayedSideNum);
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
                if (config.isFullScreen() && hightPaneZoom <= contentPane.getPrefHeight()) {
                    scrollBarHeight.setVisible(false);
                }
                //if (topicMap.size() <= diplayedSideNum) {
                //    if (widthPaneZoom <= contentPane.getPrefWidth()) {
                //        scrollBarWidth.setVisible(false);
                //    }
                //}
                contentPane.setTranslateY(0);
                verticalPane.setTranslateY(0);
                timeLine.setTranslateY(0);
                contentPane.setTranslateX(0);
                horizonPane.setTranslateX(0);
                scrollBarHeight.setValue(0);
                scrollBarWidth.setValue(0);
                
                contentPane.getChildren().clear();
                horizonPane.getChildren().clear();
                draw();
                showTimeColumn();
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
                    double hightPane = contentPane.getPrefHeight();
                    //double widthPane = contentPane.getPrefWidth();
                    hightPaneZoom = hightPane * (value / 100);

                    // 幅はズーム機能を適用しない
                    //widthPaneZoom = widthPane * (value / 100);
                    //widthPaneZoom = widthPane;
                    //scrollBarWidth.setVisible(true);
                    scrollBarHeight.setVisible(true);
                    Platform.runLater(() -> {
                        if (config.isFullScreen() && hightPaneZoom <= contentPane.getPrefHeight()) {
                            scrollBarHeight.setVisible(false);
                        }
                        //if (topicMap.size() <= diplayedSideNum) {
                        //    if (widthPaneZoom <= contentPane.getPrefWidth()) {
                        //        scrollBarWidth.setVisible(false);
                        //    }
                        //}
                        contentPane.setTranslateY(0);
                        verticalPane.setTranslateY(0);
                        timeLine.setTranslateY(0);
                        contentPane.setTranslateX(0);
                        horizonPane.setTranslateX(0);
                        scrollBarHeight.setValue(0);
                        //scrollBarWidth.setValue(0);

                        contentPane.getChildren().clear();
                        draw();
                        showTimeColumn();
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
                labelZoom.setStyle("-fx-background-image: url('image/btnZoomON.png'); -fx-padding: 0; -fx-spacing: 0; -fx-background-repeat: no-repeat; -fx-background-position: center center; -fx-border-width: 0;-fx-border-color: black;");
                flgShowZoom = 2;
                
            } else if (flgShowZoom == 2) {
                sliderZoom.setVisible(false);
                labelZoom.setStyle("-fx-background-image: url('image/btnZoomOFF.PNG'); -fx-padding: 0; -fx-spacing: 0; -fx-background-repeat: no-repeat; -fx-background-position: center center; -fx-border-width: 0;-fx-border-color: black;");
                flgShowZoom = 1;
            }
            
            if (config.isFullScreen()) {
                if (hightPaneZoom <= contentPane.getPrefHeight()) {
                    scrollBarHeight.setVisible(false);
                }
            }
            //if (topicMap.size() <= diplayedSideNum) {
            //    if (widthPaneZoom <= contentPane.getPrefWidth()) {
            //        scrollBarWidth.setVisible(false);
            //    }
            //}
        });

        buildTooltip(sliderZoom, "Zoom");

        sc.getStage().getScene().widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (config.isAutoScroll()) {
                scrollBarWidth.setVisible(false);
            } else if (!config.isFullScreen()) {
                scrollBarWidth.setVisible(true);
            } else if (this.agendas.size() <= diplayedSideNum) {
                scrollBarWidth.setVisible(false);
            } else {
                scrollBarWidth.setVisible(true);
            }
        });

        sc.getStage().getScene().heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (config.isAutoScroll()) {
                scrollBarHeight.setVisible(false);
            } else if (!config.isFullScreen()) {
                scrollBarHeight.setVisible(true);
            } else {
                scrollBarHeight.setVisible(true);
                if (hightPaneZoom <= contentPane.getPrefHeight()) {
                    scrollBarHeight.setVisible(false);
                }
            }
        });

        scrollBarHeight.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            timeColumnTranslateY = newValue.doubleValue() * -1;
            contentPane.setTranslateY(timeColumnTranslateY);
            verticalPane.setTranslateY(timeColumnTranslateY);
            timeLine.setTranslateY(timeLineTranslateY + timeColumnTranslateY - timeColumnY);
        });

        scrollBarWidth.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            contentPane.setTranslateX(-newValue.doubleValue());
            horizonPane.setTranslateX(-newValue.doubleValue());
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

        scrollBarWidth.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.setScrollRange(diplayedSideNum);
        });

        scrollBarHeight.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.setScrollRange(diplayedSideNum);
        });

        contentPane.setOnScroll(event -> {
            if (model.isUpdate()) {
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

    /**
     * 実績の列を描写する
     *
     * @param partial 表示する実績の集合
     */
    private void createPageColumns(List<Entry<Long, Agenda>> partial) {
        try {
            logger.info("createPageColumns start.");

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

            contentPane.getChildren().clear();
            horizonPane.getChildren().clear();

            double workTime = (endTime.getTime() - startTime.getTime()) / 60000.0;

            // 実績列が設定のカラム数より大きいなら設定を優先する
            double paneWidth = this.agendas.size() > config.getDiplayedSideNum()
                    ? contentPane.getPrefWidth() / config.getDiplayedSideNum()
                    : contentPane.getPrefWidth() / this.agendas.size();

            double columnWidth = config.isOnlyPlaned() ? paneWidth : paneWidth / 2;

            // グリッドを描画
            this.drawGrid(contentPane, paneWidth * this.agendas.size());

            int countPane = 0;

            for (Entry<Long, Agenda> entry : partial) {
                Agenda agenda = entry.getValue();

                String title1 = agenda.getTitle1();
                String title2 = agenda.getTitle2();
                String fontColor = agenda.getFontColor();
                String backgraundColor = agenda.getBackColor();
                String textPlan = LocaleUtils.getString("key.PlanTitle");
                String textActual = LocaleUtils.getString("key.ActualTitle");

                double left = paneWidth * countPane;

                // 行ペイン
                AnchorPane rowPane = new AnchorPane();
                // 進捗時間表示のONとOFFで行のスタート位置を調整する
                AnchorPane.setTopAnchor(rowPane, 0.0 - this.timeColumnY);
                // 自動スクロールがONの場合は時間軸が上下均等に広がるようにスタート位置を調整する距離を半分にする
                if (config.isAutoScroll()) {
                    AnchorPane.setTopAnchor(rowPane, 0.0 - (this.timeColumnY / 2));
                }
                AnchorPane.setBottomAnchor(rowPane, 0.0);
                AnchorPane.setLeftAnchor(rowPane, left);
                rowPane.setPrefWidth(paneWidth);
                rowPane.setStyle(TRANSPARENT);

                // 行ヘッダー
                AnchorPane headerPane = new AnchorPane();
                AnchorPane.setTopAnchor(headerPane, 0.0);
                AnchorPane.setBottomAnchor(headerPane, 0.0);
                AnchorPane.setLeftAnchor(headerPane, left);
                headerPane.setPrefWidth(paneWidth);
                headerPane.setStyle(TRANSPARENT);

                // カンバン名(作業者名)ラベル
                Label lableTitle1 = new Label();
                lableTitle1.setPrefHeight(50.0);
                AnchorPane.setLeftAnchor(lableTitle1, 0.0);
                AnchorPane.setRightAnchor(lableTitle1, 0.0);
                AnchorPane.setTopAnchor(lableTitle1, 0.0);
                lableTitle1.setStyle("-fx-font-size:" + config.getTitleFontSize() + ";" + "-fx-background-color: " + backgraundColor + ";" + "-fx-text-fill: " + fontColor + ";" + "-fx-border-width: 1;-fx-border-color: black;");
                lableTitle1.setAlignment(Pos.CENTER);
                lableTitle1.setText(title1 + "\n" + title2);

                // 予定ラベル
                Label lablePlan = new Label();
                lablePlan.setPrefHeight(50.0 - this.timeColumnY);
                lablePlan.setMinHeight(50.0 - this.timeColumnY);
                lablePlan.setStyle("-fx-background-color: white;" + "-fx-font-size:" + config.getColumnFontSize() + ";" + "-fx-border-width: 1;-fx-border-color: black;");
                lablePlan.setAlignment(Pos.CENTER);
                lablePlan.setTextAlignment(TextAlignment.CENTER);
                AnchorPane.setLeftAnchor(lablePlan, 0.0);
                AnchorPane.setTopAnchor(lablePlan, 50.0);
                lablePlan.setText(textPlan);
                
                Label lableActual = new Label();
                if (!config.isOnlyPlaned()) {
                    AnchorPane.setRightAnchor(lablePlan, columnWidth);

                    // 実績ラベル
                    lableActual.setPrefHeight(50.0 - this.timeColumnY);
                    lableActual.setMinHeight(50.0 - this.timeColumnY);
                    lableActual.setStyle("-fx-background-color: white" + ";" + "-fx-text-fill: black;" + "-fx-font-size:" + config.getColumnFontSize() + ";" + "-fx-border-width: 1;-fx-border-color: black;");
                    lableActual.setAlignment(Pos.CENTER);
                    lableActual.setTextAlignment(TextAlignment.CENTER);
                    AnchorPane.setRightAnchor(lableActual, 0.0);
                    AnchorPane.setTopAnchor(lableActual, 50.0);
                    lableActual.setText(textActual);
                    // 進捗時間表示がONの場合、実績ラベルの二行目に進捗時間を表示する
                    if (config.getShowActualTime()) {
                        lableActual.setText(textActual + "\n" + MonitorTools.formatTaktTime(agenda.getDelayTimeMillisec()));
                    }
                    
                    AnchorPane.setLeftAnchor(lableActual, columnWidth);
                } else {
                    lablePlan.setPrefWidth(columnWidth);
                }

                AnchorPane pane = new AnchorPane();
                pane.setStyle("-fx-background-color:transparent;-fx-border-style:solid;-fx-border-width:0 1 1 0;-fx-border-color:white;");
                AnchorPane.setTopAnchor(pane, 0.0);
                AnchorPane.setBottomAnchor(pane, 0.0);
                AnchorPane.setLeftAnchor(pane, 0.0);
                AnchorPane.setRightAnchor(pane, 0.0);

                // 計画
                AnchorPane planPane = new AnchorPane();
                planPane.setStyle(TRANSPARENT);
                AnchorPane.setLeftAnchor(planPane, 0.0);
                AnchorPane.setTopAnchor(planPane, 0.0);
                AnchorPane.setBottomAnchor(planPane, 0.0);
                planPane.setPrefWidth(columnWidth);


                List<AgendaTopic> planTopics = agenda.getPlans().stream()
                        .map(AgendaPlan::getTopics)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

                    List<AgendaGroup> planGroups = AgendaGroup.groupBy(planTopics);
                    double planWidth = columnWidth / planGroups.size();

                    int planColumn = 0;
                    for (AgendaGroup group : planGroups) {


                        for (AgendaTopic topic : group.getTopics()) {

                            String text1 = topic.getTitle1();
                            String text2 = Objects.nonNull(topic.getTitle2()) ? topic.getTitle2() : "";
                            String text3 = topic.getTitle3();
                            String fontColor1 = topic.getFontColor();
                            String backColor = topic.getBackColor();
                            String planStart = formatter.format(topic.getPlanStartTime());
                            String planEnd = formatter.format(topic.getPlanEndTime());

                            Date pranStartTime;
                            if (topic.getPlanStartTime().after(this.currentData.getFromDate())) {
                                pranStartTime = DateUtils.parseDate(formatter_HHmmss.format(topic.getPlanStartTime()), pattern_HHmmss);
                            } else {
                                pranStartTime = startTime;
                            }

                            Date planEndTime;
                            if (topic.getPlanEndTime().before(this.currentData.getToDate())) {
                                planEndTime = DateUtils.parseDate(formatter_HHmmss.format(topic.getPlanEndTime()), pattern_HHmmss);
                            } else {
                                planEndTime = endTime;
                            }

                            if (planEndTime.compareTo(startTime) > 0 || pranStartTime.compareTo(endTime) > 0) {
                                double cycleTime = (planEndTime.getTime() - pranStartTime.getTime()) / 60000.0;
                                double prefHeight = (panePrefHeight / workTime) * cycleTime;
                                double translateY = (panePrefHeight / workTime) * ((pranStartTime.getTime() - startTime.getTime()) / 60000.0);

                                Label label = new Label();
                                label.setText(text1 + "\n" + text2 + "\n" + text3);
                                label.setStyle("-fx-font-size:" + config.getItemFontSize() + ";" + "-fx-text-fill: " + fontColor1 + ";" + "-fx-background-color:" + backColor + ";" + "-fx-border-width: 0.5; -fx-border-color: white;");
                                label.setAlignment(Pos.CENTER);
                                label.setTextAlignment(TextAlignment.CENTER);
                                label.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                                label.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

                                if (topic.isBlink()) {
                                    blinking.play(label);
                                }

                                this.buildTooltip(label, text1 + "\n" + text2 + "\n" + text3 + "\n" + planStart + " - " + planEnd);

                                AnchorPane.setTopAnchor(label, translateY);
                                if (AgendaDisplayPatternEnum.DisplayPerKanban.equals(this.config.getAgendaDisplayPattern())
                                        || (!DisplayModeEnum.LINE.equals(this.config.getMode()) && !DisplayModeEnum.KANBAN.equals(this.config.getMode()) && !DisplayModeEnum.WORKER.equals(this.config.getMode()))
                                        && Objects.nonNull(topic.getRow())) {
                                    double prefWidth = columnWidth / agenda.getRowCount();
                                    label.setPrefSize(prefWidth, prefHeight);
                                    AnchorPane.setLeftAnchor(label, prefWidth * topic.getRow());
                                } else {
                                    label.setPrefSize(planWidth, prefHeight);
                                    AnchorPane.setLeftAnchor(label, planWidth * planColumn);
                                }

                                planPane.getChildren().addAll(label);
                            }
                        }
                        ++planColumn;
                    }


                // 実績
                AnchorPane actualPane = new AnchorPane();
                if (!config.isOnlyPlaned()) {
                    actualPane.setStyle(BORDER_LINE);
                    AnchorPane.setLeftAnchor(actualPane, columnWidth);
                    AnchorPane.setTopAnchor(actualPane, 0.0);
                    AnchorPane.setBottomAnchor(actualPane, 0.0);
                    actualPane.setPrefWidth(columnWidth);


                    List<AgendaTopic> actualTopics = agenda.getActuals()
                            .stream()
                            .map(AgendaGroup::getTopics)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());


                    List<AgendaActualGroup> actualGroups = AgendaActualGroup.groupBy(actualTopics);
                    double actualWidth = columnWidth / actualGroups.size();

                    int actualColumn = 0;
                    for (AgendaActualGroup group : actualGroups) {
                        for (AgendaTopic topic : group.getTopics()) {

                            String text1 = topic.getTitle1();
                            String text2 = Objects.nonNull(topic.getTitle2()) ? topic.getTitle2() : "";
                            String text3 = topic.getTitle3();
                            String fontColor1 = topic.getFontColor();
                            String backColor = topic.getBackColor();

                            String actualStart = formatter.format(topic.getActualStartTime());
                            String actualEnd = formatter.format(topic.getActualEndTime());

                            Date actualStartTime;
                            if (topic.getActualStartTime().after(this.currentData.getFromDate())) {
                                actualStartTime = DateUtils.parseDate(formatter_HHmmss.format(topic.getActualStartTime()), pattern_HHmmss);
                            } else {
                                actualStartTime = startTime;
                            }

                            Date actualEndTime;
                            if (topic.getActualEndTime().before(this.currentData.getToDate())) {
                                actualEndTime = DateUtils.parseDate(formatter_HHmmss.format(topic.getActualEndTime()), pattern_HHmmss);
                            } else {
                                actualEndTime = endTime;
                            }

                            if (actualEndTime.compareTo(startTime) > 0 || actualStartTime.compareTo(endTime) > 0) {
                                double cycleTime = (actualEndTime.getTime() - actualStartTime.getTime()) / 60000.0;
                                double prefHeight = max((panePrefHeight / workTime) * cycleTime, 1.0);
                                double translateY = (panePrefHeight / workTime) * ((actualStartTime.getTime() - startTime.getTime()) / 60000.0);

                                Label label = new Label();
                                label.setText(text1 + "\n" + text2 + "\n" + text3);
                                label.setStyle("-fx-font-size:" + config.getItemFontSize() + ";" + "-fx-text-fill: " + fontColor1 + ";" + "-fx-background-color:" + backColor + ";" + "-fx-border-width: 0.5; -fx-border-color: white;");
                                label.setAlignment(Pos.CENTER);
                                label.setTextAlignment(TextAlignment.CENTER);
                                label.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                                label.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                                AnchorPane.setTopAnchor(label, translateY);

                                // ラベルのサイズとX方向の位置を設定
                                if (AgendaDisplayPatternEnum.DisplayPerKanban.equals(this.config.getAgendaDisplayPattern())
                                        || (!DisplayModeEnum.WORKER.equals(this.config.getMode()) && !DisplayModeEnum.LINE.equals(this.config.getMode()) && !DisplayModeEnum.KANBAN.equals(this.config.getMode()))
                                        && Objects.nonNull(topic.getRow())) {
                                    double prefWidth = columnWidth / agenda.getRowCount();
                                    label.setPrefSize(prefWidth, prefHeight);
                                    AnchorPane.setLeftAnchor(label, prefWidth * topic.getRow());
                                } else {
                                    //double prefWidth = columnWidth / actual.getTopics().size();
                                    label.setPrefSize(actualWidth, prefHeight);
                                    AnchorPane.setLeftAnchor(label, actualWidth * actualColumn);
                                }

                                if (topic.isBlink()) {
                                    blinking.play(label);
                                }

                                this.buildTooltip(label, text1 + "\n" + text2 + "\n" + text3 + "\n" + actualStart + " - " + actualEnd);

                                actualPane.getChildren().addAll(label);
                            }
                        }
                        ++actualColumn;
                    }

                } else {
                    actualPane.setVisible(false);
                }

                pane.getChildren().addAll(planPane, actualPane);
                rowPane.getChildren().addAll(pane);
                headerPane.getChildren().addAll(lableTitle1, lableActual, lablePlan);

                contentPane.getChildren().addAll(rowPane);
                horizonPane.getChildren().addAll(headerPane);

                countPane++;
            }

            // 休憩時間を描画
            this.drawBreakTime(contentPane, paneWidth * partial.size());

        } catch (Exception ex) {
            logger.fatal(ex, ex);

        } finally {
            logger.info("createPageColumns end.");
        }
    }

    /**
     * コンテンツを描画する。
     *
     */
    private void draw() {
        try {
            if (this.agendas.size() <= 0) {
                return;
            }

            if (flgZoom == true && hightPaneZoom > 0) {
                panePrefHeight = hightPaneZoom;
            }

            if (!config.isTogglePages() && this.agendas.size() > config.getDiplayedSideNum()) {
                scrollBarWidth.setVisible(true);
            }

            // 表示する項目をカラム数で分割
            IntStream indices = this.agendas.size() % this.config.getDiplayedSideNum() == 0
                    ? IntStream.range(0, this.agendas.size() / this.config.getDiplayedSideNum())
                    : IntStream.rangeClosed(0, this.agendas.size() / this.config.getDiplayedSideNum());

            List<List<Entry<Long, Agenda>>> separated = indices
                    .mapToObj(i -> this.agendas.entrySet().stream()
                            .limit(i * config.getDiplayedSideNum() + config.getDiplayedSideNum())
                            .skip(i * config.getDiplayedSideNum())
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());

            if (config.isTogglePages() && separated.size() > 1) {
                // ページ切り替え
                List<KeyFrame> keyframes = IntStream.range(0, separated.size())
                        .boxed()
                        .map(i -> new KeyFrame(Duration.seconds(i * config.getToggleTime()), (event) -> {
                            createPageColumns(separated.get(i));
                        }))
                        .collect(Collectors.toList());

                // ループの最後と先頭が同じ時間になってしまうため最後の要素を再度追加
                keyframes.add(new KeyFrame(Duration.seconds(separated.size() * config.getToggleTime()), event -> {
                    createPageColumns(separated.get(separated.size() - 1));
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
                if (Objects.nonNull(pageToggleTimeline)) {
                    pageToggleTimeline.stop();
                    pageToggleTimeline.getKeyFrames().clear();
                }
                createPageColumns(separated.stream().flatMap(elm -> elm.stream()).collect(Collectors.toList()));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * Show time column
     *
     */
    private void showTimeColumn() {
        try {
            double fontSize = config.getItemFontSize();

            if (flgZoom == true && hightPaneZoom > 0) {
                panePrefHeight = hightPaneZoom;
            }

            verticalPane.getChildren().clear();

            Date date = new Date(this.startTime.getTime());
            if (endTime.compareTo(date) > 0) {
                double cycleTimeLineStartEnd = endTime.getTime() - date.getTime();
                double countLineTime = (cycleTimeLineStartEnd / 1000.0) / 60.0 / config.getTimeUnit();
                double timeCellHeight = (panePrefHeight) / (countLineTime);

                for (int i = 0; i < countLineTime; i++) {
                    Label label = new Label();
                    label.setText(formatter.format(date));
                    label.setPrefHeight(timeCellHeight);
                    label.setPrefWidth(timeColumnPrefWidth);
                    label.setAlignment(Pos.CENTER);
                    label.setTranslateY(0.0);
                    label.setStyle("-fx-background-color: white;-fx-border-width: 1;-fx-border-color: black;" + "-fx-font-size:" + fontSize);
                    AnchorPane.setTopAnchor(label, timeCellHeight * i - timeColumnY);
                    // 自動スクロールがONの場合は時間軸が上下均等に広がるようにスタート位置を調整する距離を半分にする
                    if (config.isAutoScroll()) {
                        AnchorPane.setTopAnchor(label, timeCellHeight * i - (timeColumnY / 2));
                    }
                    verticalPane.getChildren().add(label);

                    date.setTime(date.getTime() + config.getTimeUnit() * 60000L);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * グリッドを描画する。
     *
     * @param pane
     * @param width
     */
    private void drawGrid(AnchorPane pane, double width) {
        try {
            if (flgZoom && this.hightPaneZoom > 0) {
                this.panePrefHeight = this.hightPaneZoom;
            }

            Date date = new Date(this.startTime.getTime());
            if (this.endTime.compareTo(date) > 0) {
                double time = endTime.getTime() - date.getTime();
                double count = time / 60000 / this.config.getTimeUnit();
                double y = this.panePrefHeight / count;

                for (int i = 0; i <= count; i++) {
                    Line line = new Line(0, y * i - timeColumnY, width, y * i - timeColumnY);
                    // 自動スクロールがONの場合は時間軸が上下均等に広がるようにスタート位置を調整する距離を半分にする
                    if (config.isAutoScroll()) {
                        line = new Line(0, y * i - (timeColumnY / 2), width, y * i - (timeColumnY / 2));
                    }
                    line.setStroke(Color.LIGHTGRAY);
                    line.setStrokeWidth(0.2);
                    pane.getChildren().add(line);
                    date.setTime(date.getTime() + this.config.getTimeUnit() * 60000L);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 休憩時間を描画する。
     *
     * @param pane
     * @param width
     */
    private void drawBreakTime(AnchorPane pane, double width) {
        try {
            if (Double.isNaN(width)) {
                return;
            }

            double workTime = (endTime.getTime() - startTime.getTime()) / 60000.0;

            for (BreakTime breakTime : this.config.getBreakTimes()) {
                BreakTimeInfoEntity entity = model.getBreakTime(breakTime.getId());
                if (Objects.isNull(entity)) {
                    continue;
                }

                String startBreak = formatter_HHmmss.format(entity.getStarttime());
                String endBreak = formatter_HHmmss.format(entity.getEndtime());

                Date startBreakTime = DateUtils.parseDate(startBreak, pattern_HHmmss);
                Date endBreakTime = DateUtils.parseDate(endBreak, pattern_HHmmss);

                double cycleBreakTime = (endBreakTime.getTime() - startBreakTime.getTime()) / 60000.0;
                double translate = (startBreakTime.getTime() - startTime.getTime()) / 60000.0;
                double prefHeight = (this.panePrefHeight / workTime) * cycleBreakTime;
                double translateY = (this.panePrefHeight / workTime) * translate;

                StringBuilder style = new StringBuilder();
                style.append("-fx-font-size:");
                style.append(this.config.getItemFontSize());
                style.append("; -fx-text-fill: White;");
                style.append("-fx-background-color: rgba(128, 128, 128, 0.5);");
                style.append("-fx-border-width: 0.5;");
                style.append("-fx-border-color: Black;");

                Label label = new Label();
                label.setText(entity.getBreaktimeName());
                label.setStyle(style.toString());
                label.setAlignment(Pos.CENTER);
                label.setPrefSize(width, prefHeight);
                label.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                label.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                label.setPickOnBounds(false);
                AnchorPane.setTopAnchor(label, translateY - this.timeColumnY);
                // 自動スクロールがONの場合は時間軸が上下均等に広がるようにスタート位置を調整する距離を半分にする
                if (config.isAutoScroll()) {
                    AnchorPane.setTopAnchor(label, translateY - (this.timeColumnY / 2));
                }
                AnchorPane.setLeftAnchor(label, 0.0);

                buildTooltip(label, entity.getBreaktimeName() + " \n" + startBreak + " - " + endBreak);

                pane.getChildren().add(label);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * Only show time line when selected date is today
     *
     */
    private void drawSystemTimeLine() {
        try {
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
        }
    }

    /**
     * Show and run system time line
     *
     */
    private void systemTimeLine() throws Exception {
        logger.info("systemTimeLine start.");

        Date nowTime = DateUtils.parseDate(formatter.format(new Date()), pattern);
        timeLineTranslateY = getTimeLineTranslateY(nowTime);

        final boolean showTimeLine = nowTime.getTime() >= startTime.getTime() && nowTime.getTime() <= endTime.getTime();
        logger.info("systemTimeLine:{}, {}, {}; isAutoScroll = {}", startTime, endTime, nowTime, config.isAutoScroll());

        timeLine.setVisible(showTimeLine);

        //Handle auto scroll and time line
        if (config.isAutoScroll() == true) {
            double y = (timeLineTranslateY - ((contentPane.getPrefHeight() / 2) + horizonPane.getHeight())) * -1;
            scrollBarHeight.setVisible(false);
            contentPane.setTranslateY(y);
            verticalPane.setTranslateY(y);
            // 自動スクロールがONの場合は時間軸が上下均等に広がるようにスタート位置を調整する距離を半分にする
            timeLine.setTranslateY((contentPane.getPrefHeight() / 2) + horizonPane.getHeight() - (this.timeColumnY / 2));
        } else {
            timeLine.setTranslateY(timeColumnTranslateY + timeLineTranslateY - this.timeColumnY);
        }

        logger.info("systemTimeLine end.");
    }

    /**
     * Get system time line translate_y
     *
     * @return timeLineTranslateY
     */
    private double getTimeLineTranslateY(Date now) {
        logger.info("getTimeLineTranslateY start. now={}, flgZoom={}, heightPaneZoom={}", now, flgZoom, hightPaneZoom);

        if (flgZoom == true && hightPaneZoom > 0) {
            panePrefHeight = hightPaneZoom;
        }

        double scale = panePrefHeight / (double) (this.endTime.getTime() - this.startTime.getTime());
        return scale * (now.getTime() - this.startTime.getTime()) + agendaHeaderPanePrefHeight;
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
            horizonPane.getChildren().clear();

            agendas = currentData.getAgendas();
            currentData.setFromDate(currentData.getKeepTargetDay());            
            drawSystemTimeLine();
            draw();

            if (!config.isFullScreen() && !config.isAutoScroll()) {
                scrollBarWidth.setVisible(true);
                scrollBarHeight.setVisible(true);
            }

            setScrollRange(config.getDiplayedSideNum());

        } catch (Exception ex) {
            logger.fatal(ex, ex);

        } finally {
            logger.info("updateDisplay end.");
        }
    }

    /**
     * スクロール範囲を設定する。
     *
     * @param diplayedSideNum
     */
    private void setScrollRange(int diplayedSideNum) {
        double max = 0;


        if (config.isTogglePages() || this.agendas.size() <= diplayedSideNum) {
            max = this.contentPane.getPrefWidth() - (this.scrollBarWidth.getWidth() - 100);
        } else {
            max = (this.contentPane.getPrefWidth() / diplayedSideNum) * this.agendas.size() - (this.scrollBarWidth.getWidth() - 100);
        }

        this.scrollBarWidth.setMin(0.0);
        this.scrollBarWidth.setMax(Math.max(0.0, max));

        if (this.flgZoom) {
            max = this.hightPaneZoom - (this.scrollBarHeight.getHeight() - 100);
        } else {
            max = this.panePrefHeight - (this.scrollBarHeight.getHeight() - 100);
        }

        this.scrollBarHeight.setMin(0.0);
        this.scrollBarHeight.setMax(Math.max(0.0, max));
    }
    
    /**
     * ツールチップを構築する。
     *
     * @param control
     * @param text
     */
    private void buildTooltip(Control control, String text) {
        try {
            Tooltip toolTip = TooltipBuilder.build(control);
            toolTip.setText(text);
        } catch (Exception ex) {
        }
    }
}
