/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.helper;

import adtekfuji.adcustommonitorapp.common.ReSizeHandler;
import adtekfuji.adcustommonitorapp.controller.GraphMonitorController;
import adtekfuji.adcustommonitorapp.service.CellProductionMonitorService;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorGraphInfoEntity;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * グラフモニタ成用クラス
 *
 * @author e-mori
 * @version 1.4.3
 * @since 2016.12.16.Fri
 */
public class GraphMonitorCreator {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * グラフ生成処理
     *
     * @param graphBasePane グラフの一覧を表示する親の画面
     * @param startDate 実績情報表示開始日
     * @param endDate 実績情報表示終了日
     * @param yRange グラフのY軸のレンジ
     * @param handlers
     */
    public void createGraph(TilePane graphBasePane, Date startDate, Date endDate, double yRange, List<ReSizeHandler> handlers) {
        logger.info(GraphMonitorCreator.class.getName() + ":createGraph start");

        CellProductionMonitorService.getInstance().clearCellProductionMonitorServiceInterfaces();

        // 検索条件
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        String[] unitTemplateIds = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_LIST_UNITTEMPLATE, "0").split(",");
        UnitSearchCondition condition = new UnitSearchCondition().fromDate(startDate).toDate(endDate).unitTemplateIdCollection(new ArrayList<>());
        for (String unitTemplateId : unitTemplateIds) {
            condition.getUnittemplateIdCollection().add(Long.parseLong(unitTemplateId));
        }

        String option = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_GRAPH_TITLE, ClientPropertyConstants.DEFAULT_SELECT_TITLE);

        List<MonitorGraphInfoEntity> infos = RestAPI.getMonitorGraph(condition, option);

        for (MonitorGraphInfoEntity info : infos) {
            Platform.runLater(() -> {
                try {
                    GraphMonitorController controller = new GraphMonitorController();
                    FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/fxml/adcustommonitorapp/graphMonitor.fxml"), rb);
                    fXMLLoader.setController(controller);
                    AnchorPane root = fXMLLoader.load();
                    controller.setArgument(info);
                    graphBasePane.getChildren().add(root);
                    CellProductionMonitorService.getInstance().addCellProductionMonitorServiceInterface(controller);
                    handlers.add(controller);
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            });
        }
        logger.info(GraphMonitorCreator.class.getName() + ":createGraph end");
    }
}
