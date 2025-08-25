/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.helper;

import adtekfuji.adcustommonitorapp.controller.PanelMonitorController;
import adtekfuji.adcustommonitorapp.service.CellProductionMonitorService;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
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
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorPanelInfoEntity;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * パネルモニタ成用クラス
 *
 * @author e-mori
 * @version 1.4.3
 * @since 2016.12.16.Fri
 */
public class PanelMonitorCreater {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * パネル生成処理
     *
     * @param panelBasePane
     */
    public void createPanel(TilePane panelBasePane) {
        logger.info(PanelMonitorCreater.class.getName() + ":createPanel start");

        CellProductionMonitorService.getInstance().clearCellProductionMonitorServiceInterfaces();

        Date now = new Date();

        // 検索条件
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        String[] unitTemplateIds = properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_UNITTEMPLATE, "0").split(",");
        UnitSearchCondition condition = new UnitSearchCondition().fromDate(DateUtils.getBeginningOfDate(now)).toDate(DateUtils.getEndOfDate(now)).unitTemplateIdCollection(new ArrayList<>());
        for (String unitTemplateId : unitTemplateIds) {
            condition.getUnittemplateIdCollection().add(Long.parseLong(unitTemplateId));
        }

        List<MonitorPanelInfoEntity> list = RestAPI.getMonitorPanel(condition, properties.getProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_TITLE, ClientPropertyConstants.DEFAULT_SELECT_TITLE));

        for (MonitorPanelInfoEntity info : list) {
            Platform.runLater(() -> {
                try {
                    PanelMonitorController controller = new PanelMonitorController();
                    FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/fxml/adcustommonitorapp/panelMonitor.fxml"), rb);
                    fXMLLoader.setController(controller);
                    AnchorPane root = fXMLLoader.load();
                    controller.setArgument(info);
                    panelBasePane.getChildren().add(root);
                    // 実績受信時に更新する画面の情報を登録
                    CellProductionMonitorService.getInstance().addCellProductionMonitorServiceInterface(controller);
                } catch (IOException ex) {
                    logger.fatal(ex, ex);
                }
            });
        }

        logger.info(PanelMonitorCreater.class.getName() + ":createPanel end");
    }
}
