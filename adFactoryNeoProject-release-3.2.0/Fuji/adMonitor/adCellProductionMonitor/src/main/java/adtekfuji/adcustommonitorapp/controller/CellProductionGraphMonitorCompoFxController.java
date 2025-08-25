package adtekfuji.adcustommonitorapp.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import adtekfuji.adcustommonitorapp.helper.GraphMonitorCreator;
import adtekfuji.adcustommonitorapp.common.ReSizeHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.forfujiapp.clientnativeservice.ChangeDateMonitoringHandler;
import jp.adtekfuji.forfujiapp.clientnativeservice.ChangeDateMonitoringService;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * セル生産監視画面グラフ表示クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.5.Mon
 */
@FxComponent(id = "CellProductionGraphMonitorCompo", fxmlPath = "/fxml/adcustommonitorapp/cellProductionGraphMonitorCompo.fxml")
public class CellProductionGraphMonitorCompoFxController implements Initializable, ChangeDateMonitoringHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);

    @FXML
    DatePicker startDatePicker;
    @FXML
    DatePicker endDatePicker;
    @FXML
    ScrollPane scrollPane;
    @FXML
    VBox tileAnchor;
    @FXML
    TilePane graphBasePane;
    @FXML
    Slider yRangeSlider;
    @FXML
    Pane progressPane;

    List<ReSizeHandler> handlers = new ArrayList<>();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(CellProductionGraphMonitorCompoFxController.class.getName() + ":initialize start");

        blockUI(false);
        createChangeMonitorSizeEvent();
        createGraphMonitor();
        ChangeDateMonitoringService service = ChangeDateMonitoringService.getInstance();
        service.setChangeDateMonitoringHandler(this);
        service.start();

        logger.info(CellProductionGraphMonitorCompoFxController.class.getName() + ":initialize end");
    }

    /**
     * 画面サイズ変更時のイベント設定
     *
     */
    private void createChangeMonitorSizeEvent() {
        //日付選択用のピッカーを今日の日付に設定
        startDatePicker.setValue(DateUtils.toLocalDate(DateUtils.getBeginningOfDate(new Date())));
        startDatePicker.valueProperty().addListener((ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) -> {
            createGraphMonitor();
        });
        endDatePicker.setValue(DateUtils.toLocalDate(DateUtils.getEndOfDate(new Date())));
        tileAnchor.prefWidthProperty().bind(scrollPane.widthProperty());

        double columnSiz = Double.parseDouble(properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_GRAPH_COLUMN_NUM, ClientPropertyConstants.DEFAULT_SELECT_COLUMN_NUM));
        double rowSiz = Double.parseDouble(properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_GRAPH_ROW_NUM, ClientPropertyConstants.DEFAULT_SELECT_ROW_NUM));
        // 初期画面サイズ設定
        graphBasePane.setPrefTileWidth((double) (scrollPane.getPrefWidth() / columnSiz) - 1.0);
        graphBasePane.setPrefTileHeight((double) (scrollPane.getPrefHeight() / rowSiz) - 1.0);

        // 画面サイズ変更時のイベント設定
        scrollPane.widthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> {
            Platform.runLater(() -> {
                // タイルの折り返しの幅のサイズと同値にすると指定した表示数よりも1少なく表示されるため
                // 1.0サイズを小さくする
                double width = (double) (newSceneWidth.doubleValue() / columnSiz) - 1.0;
                graphBasePane.setPrefTileWidth(width);
            });
        });
        scrollPane.heightProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) -> {
            Platform.runLater(() -> {
                // タイルの折り返しの幅のサイズと同値にすると指定した表示数よりも1少なく表示されるため
                // 1.0サイズを小さくする
                double hight = (double) (newSceneHeight.doubleValue() / rowSiz) - 1.0;
                graphBasePane.setPrefTileHeight(hight);
            });
        });
        // グラフのレンジ調整
        yRangeSlider.setOnMouseReleased((MouseEvent event) -> {
            for (ReSizeHandler handler : handlers) {
                handler.resize(0d, yRangeSlider.getValue());
            }
        });
    }

    /**
     * モニター画面生成処理
     *
     */
    private void createGraphMonitor() {
        Platform.runLater(() -> {
            graphBasePane.getChildren().clear();
        });
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                new GraphMonitorCreator().createGraph(graphBasePane,
                        DateUtils.getBeginningOfDate(DateUtils.toDate(startDatePicker.getValue())),
                        DateUtils.getEndOfDate(DateUtils.toDate(endDatePicker.getValue())),
                        yRangeSlider.getValue(), handlers);
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 画面の利用制限
     *
     * @param isBlock true;有効/false:無効
     */
    private void blockUI(boolean isBlock) {
        sc.blockUI(isBlock);
        progressPane.setVisible(isBlock);
    }

    /**
     * 日付が変わった場合の画面更新処理
     *
     */
    @Override
    public void changeDate() {
        blockUI(false);
        createGraphMonitor();
    }
}
