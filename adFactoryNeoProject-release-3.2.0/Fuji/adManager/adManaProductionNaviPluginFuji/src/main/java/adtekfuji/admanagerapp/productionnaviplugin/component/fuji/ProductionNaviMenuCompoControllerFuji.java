/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component.fuji;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import java.net.URL;
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
 * 生産管理のメニュー
 *
 * @author nar-nakamura
 */
@FxComponent(id = "ProductionNaviMenuCompoFuji", fxmlPath = "/fxml/admanagerapp/productionnaviplugin/fuji/production_navi_menu_fuji.fxml")
public class ProductionNaviMenuCompoControllerFuji implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    @FXML
    private VBox menuPane;
    @FXML
    private Button importButton;
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

            // キャッシュする情報を取得する。
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
     * 生産計画の読み込み
     *
     * @param event
     */
    @FXML
    public void onViewImport(ActionEvent event) {
        logger.info(":onViewImport start");
        sc.setComponent("ContentNaviPane", "WorkPlanImportCompoFuji", "fromNaviMenu");
        logger.info(":onViewImport end");
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
}
