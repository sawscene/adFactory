package jp.adtekfuji.admonitorequipmentplannumplugin;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.MonitorEquipmentPlanNumInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程計画実績数フレームのコントローラー
 *
 * @author s-heya
 * @since 2021/04/02
 */
@AndonComponent(title = "工程計画実績数フレーム")
@FxComponent(id = "DailyEquipmentPlanNumMonitor", fxmlPath = "/fxml/equip_plunnum_monitor.fxml")
public class MonitorEquipmentPlanNumController implements Initializable, AdAndonComponentInterface {

    /**
     * 終了コマンド
     */
    private class ExitCommand {
    }

    private final double GAP = 3.0;

    private final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade monitorFacade = new AndonLineMonitorFacade();
    private final AndonMonitorSettingFacade settingFacade = new AndonMonitorSettingFacade();
    private final Object lock = new Object();
    private final LinkedList<Object> queue = new LinkedList<>();

    private List<MonitorEquipmentPlanNumInfoEntity> equipPlanNums = null;
    private Long monitorId;
    private double fontSize = Double.NaN;
    private boolean showPlan;
    private AndonMonitorLineProductSetting setting;
    private int wrapItems;
    private Thread thread;
    private boolean isReady = false;
    
    @FXML
    private AnchorPane anchorPane;

    /**
     * 画面を初期化する。
     *
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // モニタID
        this.monitorId = AndonLoginFacade.getMonitorId();

        this.wrapItems = Integer.valueOf(AdProperty.getProperties().getProperty(Constants.WRAP_ITEMS, Constants.WRAP_ITEMS_DEFAULT));

        // フォントサイズ
        if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_LARGE)) {
            AdProperty.getProperties().setProperty(Constants.FONT_SIZE_LARGE, Constants.DEF_FONT_SIZE_LARGE);
        }
        this.fontSize = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_LARGE));

        // 計画表示
        if (!AdProperty.getProperties().containsKey("showPlan")) {
            AdProperty.getProperties().setProperty("showPlan", "true");
        }
        this.showPlan = Boolean.parseBoolean(AdProperty.getProperties().getProperty("showPlan"));

        // 画面サイズが変更された場合再描画
        this.anchorPane.getChildren().clear();
        this.anchorPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.draw();
        });

        anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.draw();
        });

        try {
            this.readSetting();

            if (!this.monitorId.equals(0L)) {
                this.startupThread();
            }
            
            isReady = true;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 画面を更新する。
     *
     * @param msg コマンド
     */
    @Override
    public void updateDisplay(Object msg) {
        if (!this.monitorId.equals(0L) && isReady) {
            if (msg instanceof ResetCommand) {
                ResetCommand command = (ResetCommand) msg;
                if (Objects.isNull(command.getMonitorId()) || Objects.equals(this.monitorId, command.getMonitorId())) {
                    logger.info("Reset start: MonitorId={}", command.getMonitorId());
                    isReady = false;
                } else {
                    return;
                }
            }
            this.send(msg);
        }
    }

    /**
     * コンポーネントを終了する。
     */
    @Override
    public void exitComponent() {
        logger.info("exitComponent");
        try {
            if (Objects.nonNull(this.thread)) {
                // スレッドを終了する。
                this.send(new ExitCommand());
                this.thread.join();
            }

        } catch (InterruptedException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * スレッドにコマンドを送信する。
     *
     * @param msg コマンド
     */
    private void send(Object msg) {
        synchronized (this.queue) {
            // 末尾に追加
            this.queue.add(msg);
            this.queue.notify();
        }
    }
    
    /**
     * スレッドを起動する。
     */
    private void startupThread() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    equipPlanNums = monitorFacade.getDailyEquipmentPlanInfo(monitorId);
                    if (Objects.nonNull(equipPlanNums)) {
                        Collections.sort(equipPlanNums, (a, b) -> a.getOrder().compareTo(b.getOrder()));
                        draw();
                    }

                    while (true) {
                        synchronized (queue) {
                            if (queue.isEmpty()) {
                                try {
                                    queue.wait();
                                } catch (InterruptedException ex) {
                                    logger.fatal(ex, ex);
                                }
                            }

                            try {
                                Object msg = null;
                                if (!queue.isEmpty()) {
                                    msg = queue.removeFirst();
                                }

                                if (Objects.nonNull(msg)) {
                                    if (msg instanceof ActualNoticeCommand) {
                                        // 実績通知コマンド
                                        ActualNoticeCommand command = (ActualNoticeCommand) msg;

                                        if (!KanbanStatusEnum.COMPLETION.equals(command.getWorkKanbanStatus())
                                                || !StringUtils.like(command.getModelName(), setting.getModelName())
                                                || !command.isCompletion()) {
                                            continue;
                                        }

                                        // 実績数を加算
                                        for (WorkEquipmentSetting equip : setting.getWorkEquipmentCollection()) {
                                            if (equip.getEquipmentIds().contains(command.getEquipmentId())) {
                                                MonitorEquipmentPlanNumInfoEntity equpPlanNum = equipPlanNums.get(equip.getOrder() - 1);
                                                logger.info("updateDisplay: ActualNoticeCommand equipmentId={} compNum={} actualNum={}", command.getEquipmentId(), command.getCompNum(), equpPlanNum.getActualNum());
                                                equpPlanNum.setActualNum(equpPlanNum.getActualNum() + command.getCompNum());
                                                break;
                                            }
                                        }
                
                                    } else if (msg instanceof ResetCommand) {
                                        // リセットコマンド
                                        readSetting();
                                        equipPlanNums = monitorFacade.getDailyEquipmentPlanInfo(monitorId);
                                        if (Objects.nonNull(equipPlanNums)) {
                                            Collections.sort(equipPlanNums, (a, b) -> a.getOrder().compareTo(b.getOrder()));
                                        }
 
                                        logger.info("Reset completed.");
                                        isReady = true;

                                    } else if (msg instanceof ExitCommand) {
                                        logger.info("Exit thread.");
                                        break;
                                    }

                                    draw();
                                }
                            } catch (Exception ex) {
                                logger.fatal(ex, ex);
                            }
                        }
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }

                return null;
            }
        };

        this.thread = new Thread(task);
        this.thread.start();
    }

    /**
     * 進捗モニタ設定を取得する。
     */
    private void readSetting() {
        logger.info("readSetting: monitorId={}", this.monitorId);
        try {
            if (!this.monitorId.equals(0L)) {
                this.setting = (AndonMonitorLineProductSetting) this.settingFacade.getLineSetting(this.monitorId, AndonMonitorLineProductSetting.class);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 画面を描画する。
     */
    private void draw() {
        Platform.runLater(() -> {
            synchronized (lock) {
                // 描画処理
                if (Objects.isNull(this.equipPlanNums)) {
                    return;
                }

                this.anchorPane.getChildren().clear();

                final int clomuns = this.equipPlanNums.size() >= wrapItems ? wrapItems : this.equipPlanNums.size() % wrapItems;
                final int rows = (this.equipPlanNums.size() - 1) / wrapItems + 1;
                final double width = Math.floor((this.anchorPane.getWidth() - (GAP * (clomuns + 1))) / clomuns); // AnchorPaneの幅から、間のvcap分とpadding左右分を引く
                final double height = Math.floor((this.anchorPane.getHeight() - (GAP * (1 + rows))) / rows); // AnchorPaneの高さから、間のhgap分とpadding上下分を引く

                final TilePane pane = new TilePane();
                pane.setHgap(GAP);
                pane.setVgap(GAP);
                pane.setPrefColumns(clomuns);

                for (MonitorEquipmentPlanNumInfoEntity equipPlanNum : this.equipPlanNums) {
                    if (this.showPlan) {
                        final GridPane childPane = new GridPane();
                        childPane.setPrefSize(width, height);

                        final Separator sep = this.createSeparator(width);
                        childPane.add(this.createLabel(String.valueOf(equipPlanNum.getActualNum()), width, (height - sep.getPrefHeight()) / 2, "black"), 0, 0);
                        childPane.add(sep, 0, 1);
                        childPane.add(this.createLabel(String.valueOf(equipPlanNum.getPlanNum()), width, (height - sep.getPrefHeight()) / 2, "black"), 0, 2);

                        pane.getChildren().add(childPane);
                    } else {
                        pane.getChildren().add(this.createLabel(String.valueOf(equipPlanNum.getActualNum()), width, height, "#303030"));
                    }
                }

                this.anchorPane.getChildren().add(pane);
                AnchorPane.setTopAnchor(pane, GAP);
                AnchorPane.setBottomAnchor(pane, GAP);
                AnchorPane.setLeftAnchor(pane, GAP);
                AnchorPane.setRightAnchor(pane, GAP);
            }
        });
    }

    /**
     * 計画の数値を生成
     *
     * @param text 計画数
     * @param width 横幅
     * @param height 高さ
     * @param color 設定された色
     * @return
     */
    private Label createLabel(String text, double width, double height, String color) {
        Label label = new Label(text);
        label.setPrefSize(width, height);
        label.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        label.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        label.setAlignment(Pos.CENTER);
        double size = MonitorTools.getFontSize(text, width, height, this.fontSize);
        label.setStyle(String.format("-fx-font-size:%fpx; -fx-text-fill:white; -fx-background-color:%s;", size, color));
        return label;
    }

    /**
     * 計画の分割部分を生成
     *
     * @param width 横幅
     * @return
     */
    private Separator createSeparator(double width) {
        Separator separator = new Separator();
        separator.setPrefSize(width, 2);
        separator.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        separator.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        return separator;
    }
}
