package adtekfuji.adcustommonitorapp.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import jp.adtekfuji.forfujiapp.clientnativeservice.ChangeDateMonitoringService;
import jp.adtekfuji.javafxcommon.utils.SwitchCompoSubject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * セル生産監視画面サブメニュークラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.5.Mon
 */
@FxComponent(id = "CellProductionMonitorSubMeneCompo", fxmlPath = "/fxml/adcustommonitorapp/cellProductionMonitorSubMeneCompo.fxml")
public class CellProductionMonitorSubMeneCompoFxController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final SwitchCompoSubject switchCompoSubject = SwitchCompoSubject.getInstance();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(CellProductionMonitorSubMeneCompoFxController.class.getName() + ":initialize start");
        logger.info(CellProductionMonitorSubMeneCompoFxController.class.getName() + ":initialize end");
    }

    @FXML
    public void onShowPanelProduction(ActionEvent event) {
        logger.info(CellProductionMonitorSubMeneCompoFxController.class.getName() + ":onShowPanelProduction start");
        ChangeDateMonitoringService.getInstance().stop();
        switchCompoSubject.switchCompo();
        sc.setComponent("ContentNaviPane", "CellProductionPanelMonitorCompo");
        logger.info(CellProductionMonitorSubMeneCompoFxController.class.getName() + ":onShowPanelProduction end");
    }

    @FXML
    public void onShowGraphProduction(ActionEvent event) {
        logger.info(CellProductionMonitorSubMeneCompoFxController.class.getName() + ":onShowGraphProduction start");
        ChangeDateMonitoringService.getInstance().stop();
        switchCompoSubject.switchCompo();
        sc.setComponent("ContentNaviPane", "CellProductionGraphMonitorCompo");
        logger.info(CellProductionMonitorSubMeneCompoFxController.class.getName() + ":onShowGraphProduction end");
    }

    @FXML
    public void onShowListProduction(ActionEvent event) {
        logger.info(CellProductionMonitorSubMeneCompoFxController.class.getName() + ":onShowListProduction start");
        ChangeDateMonitoringService.getInstance().stop();
        switchCompoSubject.switchCompo();
        sc.setComponent("ContentNaviPane", "CellProductionListMonitorCompo");
        logger.info(CellProductionMonitorSubMeneCompoFxController.class.getName() + ":onShowListProduction end");
    }

    @FXML
    public void onShowList2Production(ActionEvent event) {
        logger.info(CellProductionMonitorSubMeneCompoFxController.class.getName() + ":onShowList2Production start");
        ChangeDateMonitoringService.getInstance().stop();
        switchCompoSubject.switchCompo();
        sc.setComponent("ContentNaviPane", "CellProductionList2MonitorCompo");
        logger.info(CellProductionMonitorSubMeneCompoFxController.class.getName() + ":onShowList2Production end");
    }

    @FXML
    public void onSetting(ActionEvent event) {
        logger.info(CellProductionMonitorSubMeneCompoFxController.class.getName() + ":onSetting start");
        ChangeDateMonitoringService.getInstance().stop();
        switchCompoSubject.switchCompo();
        sc.setComponent("ContentNaviPane", "CellProductionMonitorSettingCompo");
        logger.info(CellProductionMonitorSubMeneCompoFxController.class.getName() + ":onSetting end");
    }
}
