/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.component;

import adtekfuji.admanagerapp.analysisplugin.common.AnalysisWorkFilterData;
import adtekfuji.admanagerapp.analysisplugin.javafx.CheckTableData;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import jp.adtekfuji.adFactory.entity.master.DelayReasonInfoEntity;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 設定画面クラス(TODO:動的生成にリファクタすること(設定が増えると後々可読性が悪くなる))
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.01.Mon
 */
@FxComponent(id = "AnalysisSubMenuCompo", fxmlPath = "/fxml/compo/analysisSubMenuCompo.fxml")
public class AnalysisSubMenuCompoController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(AnalysisSubMenuCompoController.class.getName() + ":initialize start");
        
        AnalysisSettingEventer.getInstance().setAnalysisSubMenu(this);
        
        logger.info(AnalysisSubMenuCompoController.class.getName() + ":initialize end");
    }

    /**
     * 設定ボタン押下時の処理
     *
     * @param event
     */
    @FXML
    public void onSetting(ActionEvent event) {
        logger.info(AnalysisSubMenuCompoController.class.getName() + ":onSetting start");
        try {
            AnalysisWorkFilterData analysisWorkFilterData = this.loadSetting();

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.setting.Title"), "AnalysisSettingCompo", analysisWorkFilterData);
            if (ret.equals(ButtonType.OK)) {
                // 設定書き込み処理
                this.storeSetting(analysisWorkFilterData);
                // 画面に設定項目を反映
                AnalysisSettingEventer.getInstance().updateDistribution();
            }
        } catch (NumberFormatException ex) {
            logger.fatal(ex, ex);
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.alert.loadConfig"), LocaleUtils.getString("key.alert.loadConfig.details"));
        }

        logger.info(AnalysisSubMenuCompoController.class.getName() + ":onSetting start");
    }

    /**
     * 各種設定読み込み処理後で外だし
     *
     */
    private AnalysisWorkFilterData loadSetting() {
        logger.info(AnalysisSubMenuCompoController.class.getName() + ":loadSearchSetting start");

        try {
            //設定を読み込んで検索条件保持用クラスにデータを入れる
            List<DelayReasonInfoEntity> delayReasons = RestAPI.getDelayReasons();
            String[] delays = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).getProperty(ClientPropertyConstants.KEY_DELAYREASON, "").split(",");
            ObservableList<CheckTableData> datas = FXCollections.observableArrayList();
            if (delays.length > 0) {
                for (DelayReasonInfoEntity entity : delayReasons) {
                    boolean isCheck = false;
                    for (String delay : delays) {
                        if (entity.getDelayReason().equals(delay)) {
                            isCheck = true;
                        }
                    }
                    CheckTableData data = new CheckTableData(entity.getDelayReason());
                    data.setIsSelect(isCheck);
                    datas.add(data);
                }
            }

            // ミリセコンドに変換
            return new AnalysisWorkFilterData(
                    Integer.parseInt(AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).getProperty(ClientPropertyConstants.KEY_TACTTIME_EARLIEST, "0")) / 1000,
                    Integer.parseInt(AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).getProperty(ClientPropertyConstants.KEY_TACTTIME_SLOWEST, "0")) / 1000,
                    datas, AnalysisWorkFilterData.TimeUnitEnum.getEnum(AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).getProperty(ClientPropertyConstants.KEY_TIME_UNIT, "SECOND")));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(AnalysisSubMenuCompoController.class.getName() + ":loadSearchSetting end");
        return new AnalysisWorkFilterData(0, 0, FXCollections.observableArrayList(), AnalysisWorkFilterData.TimeUnitEnum.SECOND);
    }

    /**
     * 検索設定書き込み処理
     *
     */
    private void storeSetting(AnalysisWorkFilterData analysisWorkFilterData) {
        logger.info(AnalysisSubMenuCompoController.class.getName() + ":storeSearchSetting start");

        try {
            Integer filterTacttimeE = analysisWorkFilterData.getFilterTactTimeEarliest() * 1000;
            Integer filterTacttimeS = analysisWorkFilterData.getFilterTactTimeSlowest() * 1000;
            AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.KEY_TACTTIME_EARLIEST, filterTacttimeE.toString());
            AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.KEY_TACTTIME_SLOWEST, filterTacttimeS.toString());
            StringBuilder delays = new StringBuilder();
            for (CheckTableData data : analysisWorkFilterData.getFilterDelayReason()) {
                if (data.getIsSelect()) {
                    delays.append(data.getName());
                    delays.append(",");
                }
            }
            AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.KEY_DELAYREASON, delays.toString());
            AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).setProperty(ClientPropertyConstants.KEY_TIME_UNIT, analysisWorkFilterData.getTimeUnit().name());
            AdProperty.store(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        logger.info(AnalysisSubMenuCompoController.class.getName() + ":storeSearchSetting end");
    }

}
