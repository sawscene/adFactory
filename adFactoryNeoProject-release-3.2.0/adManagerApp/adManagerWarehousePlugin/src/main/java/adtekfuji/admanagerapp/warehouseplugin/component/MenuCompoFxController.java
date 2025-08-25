/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.component;

import adtekfuji.admanagerapp.warehouseplugin.common.Constants;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 倉庫案内メニュー
 *
 * @author s-heya
 */
@FxComponent(id = "WarehouseMenuCompo", fxmlPath = "/fxml/warehouseplugin/warehouse_menu.fxml")
public class MenuCompoFxController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    /**
     * ロットトレース(製品)
     */
    @FXML
    private Button lotTraceKanbanButton;

    /**
     * ロットトレース(資材)
     */
    @FXML
    private Button lotTraceMaterialButton;

    /**
     * ロットトレース(作業者)
     */
    @FXML
    private Button lotTracePersonButton;

    @FXML
    private Button importButton;

    /**
     * 倉庫案内メニューを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            AdProperty.load(Constants.WAREHOUSE_PROPERTY_NAME, Constants.WAREHOUSE_PROPERTY_NAME + ".properties");
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        // ロットトレース (enableLotTrace=true の場合のみ表示)
        Boolean enableAccept = Boolean.valueOf(AdProperty.getProperties().getProperty(Constants.ENABLE_LOT_TRACE_KEY, Constants.ENABLE_LOT_TRACE_DEF));
        if (!enableAccept) {
            // ロットトレース(製品)
            this.lotTraceKanbanButton.setManaged(false);
            // ロットトレース(資材)
            this.lotTraceMaterialButton.setManaged(false);
            // ロットトレース(作業者)
            this.lotTracePersonButton.setManaged(false);
        }
    }

    /**
     * 在庫モニター画面を開く。
     * 
     * @param event 
     */
    @FXML
    public void onStock(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "StockView");
    }

    /**
     * 出庫状況照会画面を開く。
     * 
     * @param event 
     */
    @FXML
    public void onDelivery(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "DeliveryView");
    }

    /**
     * インポート画面を開く。
     * 
     * @param event 
     */
    @FXML
    public void onImport(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "ImportCompo");
    }

    /**
     * ロットトレース(製品)画面を開く。
     *
     * @param event 
     */
    @FXML
    public void onLotTraceKanban(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "LotTraceKanban");
    }

    /**
     * ロットトレース(資材)画面を開く。
     *
     * @param event 
     */
    @FXML
    public void onLotTraceMaterial(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "LotTraceMaterial");
    }

    /**
     * ロットトレース(作業者)画面を開く。
     *
     * @param event 
     */
    @FXML
    public void onLotTracePerson(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "LotTracePerson");
    }

    /**
     * 作業ログ出力画面を開く。
     * 
     * @param event 
     */
    @FXML
    public void onOperationLog(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "OperationLogView");
    }

}
