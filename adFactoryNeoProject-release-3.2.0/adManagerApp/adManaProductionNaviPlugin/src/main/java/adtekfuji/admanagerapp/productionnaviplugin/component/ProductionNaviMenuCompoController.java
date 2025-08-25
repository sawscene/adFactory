/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 生産管理ナビのメニュー
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "ProductionNaviMenuCompo", fxmlPath = "/fxml/compo/production_navi_menu.fxml")
public class ProductionNaviMenuCompoController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    private final String PROP_DISABLE_WORK_PLAN = "disable.workplan";
    private final String PROP_DISABLE_IMPORT = "disable.import";
    private final String PROP_DISABLE_WORKER = "disable.worker";
    private final String PROP_ENABLE_LINK_ASP = "enableLinkAsprova";

    @FXML
    private VBox menuPane;
    @FXML
    private Button workPlanButton;
    @FXML
    private Button importButton;
    @FXML
    private Button workerButton;
    @FXML
    private Button linkAspButton;
    @FXML
    private Button displaySettingButton;
    @FXML
    private Button rosterButton;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties propProductionNavi = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

            if (Boolean.valueOf(propProductionNavi.getProperty(PROP_DISABLE_WORK_PLAN, "false"))) {
                menuPane.getChildren().remove(workPlanButton);
            }
            if (Boolean.valueOf(propProductionNavi.getProperty(PROP_DISABLE_IMPORT, "false"))) {
                menuPane.getChildren().remove(importButton);
            }
            if (Boolean.valueOf(propProductionNavi.getProperty(PROP_DISABLE_WORKER, "false"))) {
                menuPane.getChildren().remove(workerButton);
            }
            if (!Boolean.valueOf(AdProperty.getProperties().getProperty(PROP_ENABLE_LINK_ASP, "false"))) {
                menuPane.getChildren().remove(linkAspButton);
            }

            // キャッシュする情報を取得する
            CacheUtils.createCacheData(EquipmentInfoEntity.class, true);
            CacheUtils.createCacheData(OrganizationInfoEntity.class, true);
            CacheUtils.createCacheData(HolidayInfoEntity.class, true);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {

    }

    /**
     * 作業計画
     *
     * @param event
     */
    @FXML
    public void onViewWorkPlanList(ActionEvent event) {
        logger.info(":onViewWorkPlanList start");
        sc.setComponent("ContentNaviPane", "WorkPlanChartCompo");
        logger.info(":onViewWorkPlanList end");
    }

    /**
     * 作業計画の取込
     *
     * @param event
     */
    @FXML
    public void onViewImport(ActionEvent event) {
        logger.info(":onViewImport start");
        sc.setComponent("ContentNaviPane", "WorkPlanImportCompo", "fromNaviMenu");
        logger.info(":onViewImport end");
    }

    /**
     * 作業者管理
     *
     * @param event
     */
    @FXML
    public void onViewWorker(ActionEvent event) {
        logger.info(":onViewWorker start");
        sc.setComponent("ContentNaviPane", "WorkerCompo");
        logger.info(":onViewWorker end");
    }

    /**
     * 表示設定
     *
     * @param event
     */
    @FXML
    public void onViewDisplaySetting(ActionEvent event) {
        logger.info(":onViewDisplaySetting start");
        sc.setComponent("ContentNaviPane", "DisplaySettingCompo");
        logger.info(":onViewDisplaySetting end");
    }

    /**
     * 勤務表
     *
     * @param event
     */
    @FXML
    public void onViewRoster(ActionEvent event) {
        logger.info(":initialize start");
        sc.setComponent("ContentNaviPane", "RosterCompo");
        logger.info(":initialize end");
    }
    
    /**
     * Asprova連携
     *
     * @param event
     */
    @FXML
    public void onViewLinkAsp(ActionEvent event) {
        logger.info(":onViewLinkAsp start");
        sc.setComponent("ContentNaviPane", "LinkAspCompo");
        logger.info(":onViewLinkAsp end");
    }
}
