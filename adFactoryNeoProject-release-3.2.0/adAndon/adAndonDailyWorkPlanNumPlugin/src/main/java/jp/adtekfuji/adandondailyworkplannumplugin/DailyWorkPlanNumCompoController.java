/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adandondailyworkplannumplugin;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.entity.MonitorWorkPlanNumInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 当日工程計画実績数フレームコントローラー
 *
 * @author s-heya
 */
@AndonComponent(title = "当日工程計画実績数フレーム")
@FxComponent(id = "DailyWorkPlanNum", fxmlPath = "/fxml/DailyWorkPlanNumCompo.fxml")
public class DailyWorkPlanNumCompoController implements Initializable, ArgumentDelivery, AdAndonComponentInterface {
    private static final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private final Object lock = new Object();
    private String frameName;
    private MonitorWorkPlanNumInfoEntity data;
    private Long monitorId;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label titleLabel;
    @FXML
    private Label planLabel;
    @FXML
    private Label planNumLabel;
    @FXML
    private Label actualLabel;
    @FXML
    private Label actualNumLabel;

    /**
     * 当日工程計画実績数フレームを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // モニタID
        this.monitorId = AndonLoginFacade.getMonitorId();
    }

    /**
     * 引数を設定する。
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof String) {
            this.frameName = (String) argument;
        }

        this.updateData(AndonLoginFacade.getMonitorId(), true);

        rootPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });

        rootPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
    }

    /**
     * 表示を更新する。
     *
     * @param msg
     */
    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof ActualNoticeCommand) {
            ActualNoticeCommand command = (ActualNoticeCommand) msg;
            if (Objects.nonNull(this.data)) {
                if (!this.data.getWorkIds().contains(command.getWorkId())) {
                    return;
                }
            }
            this.updateData(command.getMonitorId(), command.isCompletion());
        } else if (msg instanceof ResetCommand) {
            this.updateData(this.monitorId, true);
        }
    }

    @Override
    public void exitComponent() {
        logger.info("exitComponent");
    }

    /**
     * コンテンツを更新する。
     *
     * @param monitorId
     */
    private void updateData(final Long monitorId, boolean isUpdate) {
        if (!isUpdate) {
            return;
        }

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (lock) {
                        data = andonLineMonitorFacade.getDailyMonitorWorkPlanNum(monitorId, frameName);
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                draw();
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 描画
     */
    private void draw() {
        Platform.runLater(() -> {
            synchronized (lock) {
                if (Objects.isNull(this.data)) {
                    return;
                }

                double width = this.rootPane.getWidth() / 2;
                double height = this.rootPane.getHeight() * 0.35;
                String planNum = "";
                String actualNum = "";
                NumberFormat nb = NumberFormat.getNumberInstance();

                // タイトル
                String title = this.data.getTitle();
                this.titleLabel.setText(title);

                // 計画数
                if (Objects.nonNull(this.data.getPlanNum())) {
                    planNum = nb.format(this.data.getPlanNum());
                    this.planNumLabel.setText(planNum);
                }

                // 実績数
                if (Objects.nonNull(this.data.getActualNum())) {
                    actualNum = nb.format(this.data.getActualNum());
                    this.actualNumLabel.setText(actualNum);
                }

                double sizePlanNum = Math.min(width / planNum.length(), height * 0.8);
                double sizeActualNum = Math.min(width / actualNum.length(), height * 0.8);

                String style = String.format("-fx-font-size: %dpx; -fx-text-fill: white;", Math.round(Math.min(sizePlanNum, sizeActualNum)));
                this.titleLabel.setStyle(style);
                this.planLabel.setStyle(style);
                this.planNumLabel.setStyle(style);
                this.actualLabel.setStyle(style);
                this.actualNumLabel.setStyle(style);
            }
        });
    }
}
