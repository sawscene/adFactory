/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.controller;

import adtekfuji.adcustommonitorapp.helper.PanelMonitorCreater;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.forfujiapp.clientnativeservice.ChangeDateMonitoringHandler;
import jp.adtekfuji.forfujiapp.clientnativeservice.ChangeDateMonitoringService;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * セル生産監視画面パネル表示クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.5.Mon
 */
@FxComponent(id = "CellProductionPanelMonitorCompo", fxmlPath = "/fxml/adcustommonitorapp/cellProductionPanelMonitorCompo.fxml")
public class CellProductionPanelMonitorCompoFxController implements Initializable, ChangeDateMonitoringHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);

    @FXML
    ScrollPane scrollPane;
    @FXML
    VBox tileAnchor;
    @FXML
    TilePane panelBasePane;
    @FXML
    Pane progressPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(CellProductionPanelMonitorCompoFxController.class.getName() + ":initialize start");

        blockUI(false);
        createChangeMonitorSizeEvent();
        createPanelMonitor();
        ChangeDateMonitoringService service = ChangeDateMonitoringService.getInstance();
        service.setChangeDateMonitoringHandler(this);
        service.start();

        logger.info(CellProductionPanelMonitorCompoFxController.class.getName() + ":initialize end");
    }

    /**
     * 画面サイズ変更時のイベント設定
     *
     */
    private void createChangeMonitorSizeEvent() {
        tileAnchor.prefWidthProperty().bind(scrollPane.widthProperty());
        double columnSiz = Double.parseDouble(properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_COLUMN_NUM, ClientPropertyConstants.DEFAULT_SELECT_COLUMN_NUM));
        double rowSiz = Double.parseDouble(properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_ROW_NUM, ClientPropertyConstants.DEFAULT_SELECT_ROW_NUM));
        // 初期画面サイズ設定
        panelBasePane.setPrefTileWidth((double) (scrollPane.getPrefWidth() / columnSiz) - 1.0);
        panelBasePane.setPrefTileHeight((double) (scrollPane.getPrefHeight() / rowSiz) - 1.0);

        // 画面サイズ変更時のイベント設定
        scrollPane.widthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> {
            Platform.runLater(() -> {
                // タイルの折り返しの幅のサイズと同値にすると指定した表示数よりも1少なく表示されるため
                // 1.0サイズを小さくする
                double width = (double) (newSceneWidth.doubleValue() / columnSiz) - 1.0;
                panelBasePane.setPrefTileWidth(width);
            });
        });
        scrollPane.heightProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) -> {
            Platform.runLater(() -> {
                // タイルの折り返しの幅のサイズと同値にすると指定した表示数よりも1少なく表示されるため
                // 1.0サイズを小さくする
                double hight = (double) (newSceneHeight.doubleValue() / rowSiz) - 1.0;
                panelBasePane.setPrefTileHeight(hight);
            });
        });
    }

    /**
     * モニター画面生成処理
     *
     */
    private void createPanelMonitor() {
        Platform.runLater(() -> {
            panelBasePane.getChildren().clear();
        });
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                new PanelMonitorCreater().createPanel(panelBasePane);
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
        createPanelMonitor();
    }
}
