/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adcustommonitorapp.controller;

import adtekfuji.adcustommonitorapp.service.CellProductionMonitorServiceInterface;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringTime;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorPanelInfoEntity;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import jp.adtekfuji.forfujiapp.utils.StyleInjecter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * パネルモニタークラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.8.Thr
 */
public class PanelMonitorController implements Initializable, CellProductionMonitorServiceInterface {

    private final Logger logger = LogManager.getLogger();

    @FXML
    AnchorPane panelBase;
    @FXML
    Label serialLabel;
    @FXML
    Label workLabel;
    @FXML
    Label progressTimeLabel;
    @FXML
    Label organizationLabel;
    @FXML
    Pane progressPane;

    private MonitorPanelInfoEntity info;
    private long interval;
    private long lastMillis = 0L;

    /**
     * Initializes the controller class.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.interval = Long.parseLong(AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).getProperty(ClientPropertyConstants.PROP_KEY_MASTER_INTERVAL, ClientPropertyConstants.DEFALT_MASTER_INTERVAL));
    }

    public void setArgument(MonitorPanelInfoEntity info) {
        if (Objects.nonNull(info)) {
            this.info = info;
            this.updateView();
        }
    }

    /**
     * 実績受信処理
     *
     * @param kanbanId 実績を受信したカンバンID
     */
    @Override
    public void receivedActualDataKanbanId(long kanbanId) {
        long nowTime = System.currentTimeMillis();
        if ((nowTime - this.lastMillis) <= this.interval) {
            return;
        }
        this.lastMillis = nowTime;

        if (this.checkCarryKanbanId(kanbanId)) {
            this.updateThread();
        }
    }

    /**
     * カンバンIDを保有しているか確認
     *
     * @param kanbanId 確認するカンバンID
     * @return true:持っている/false:持っていない
     */
    private boolean checkCarryKanbanId(long kanbanId) {
        if (Objects.nonNull(this.info)) {
            if (this.info.getKanbanIds().contains(kanbanId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新処理
     *
     */
    private void updateThread() {
        logger.info(PanelMonitorController.class.getName() + ":updateThread start");

        this.blockUI(true);

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    UnitSearchCondition condition = new UnitSearchCondition().unitId(info.getUnitId());
                    List<MonitorPanelInfoEntity> list = RestAPI.getMonitorPanel(condition, AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG).getProperty(ClientPropertyConstants.PROP_KEY_SELECT_PANEL_TITLE, ClientPropertyConstants.DEFAULT_SELECT_TITLE));
                    if (!list.isEmpty()) {
                        info = list.get(0);
                        updateView();
                    }
                } catch (Exception ex) {
                    blockUI(false);
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                    logger.info(PanelMonitorController.class.getName() + ":updateThread end");
                }
                return null;
            }
        };

        new Thread(task).start();
    }

    /**
     * 画面更新
     *
     */
    private void updateView() {
        Platform.runLater(() -> {
            StyleInjecter.setBackGrandColorStyle(panelBase, info.getBackgroundColor());
            StyleInjecter.setBorderColorStyle(panelBase, "white");
            StyleInjecter.setTextStyle(serialLabel, "white", 20, true);
            StyleInjecter.setTextStyle(workLabel, "white", 20, true);
            StyleInjecter.setTextStyle(progressTimeLabel, "white", 40, true);
            StyleInjecter.setTextStyle(organizationLabel, "white", 20, true);

            serialLabel.setText(info.getTitle());
            workLabel.setText(info.getWorkName());
            progressTimeLabel.setText(StringTime.convertMillisToStringTime(info.getProgressTimeMillisec()));
            organizationLabel.setText(info.getWorkerName());
        });
    }

    /**
     * 画面操作制限
     *
     * @param isBlock true;有効/false:無効
     */
    private void blockUI(boolean isBlock) {
        panelBase.setDisable(isBlock);
        progressPane.setVisible(isBlock);
    }
}
