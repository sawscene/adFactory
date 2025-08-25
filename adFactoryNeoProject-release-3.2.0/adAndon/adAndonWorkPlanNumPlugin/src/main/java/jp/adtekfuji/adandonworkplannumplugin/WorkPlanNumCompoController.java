/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adandonworkplannumplugin;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.MonitorWorkPlanNumInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程別計画実績数フレームコントローラー
 *
 * @author fu-kato
 */
@AndonComponent(title = "工程別計画実績数フレーム")
@FxComponent(id = "WorkPlanNum", fxmlPath = "/fxml/WorkPlanNumCompo.fxml")
public class WorkPlanNumCompoController implements Initializable, AdAndonComponentInterface {

      private final double GAP = 3.0;

      private final Logger logger = LogManager.getLogger();
    private final AndonMonitorSettingFacade andonMonitorSettingFacade = new AndonMonitorSettingFacade();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private final Object lock = new Object();

    private Long monitorId;
    private double fontSize;
    private boolean isShowPlan;
    private AndonMonitorLineProductSetting setting;

    List<MonitorWorkPlanNumInfoEntity> workActualPlans;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private VBox vbox;

    private int wrapItems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        this.isShowPlan = Boolean.parseBoolean(AdProperty.getProperties().getProperty("showPlan"));

        // 画面サイズが変更された場合再描画
        anchorPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw(workActualPlans);
        });
        anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw(workActualPlans);
        });

        this.updateSetting();
        this.readTask(true);
    }

    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof ActualNoticeCommand) {
            ActualNoticeCommand command = (ActualNoticeCommand) msg;

            if (!KanbanStatusEnum.COMPLETION.equals(command.getWorkKanbanStatus())
                    || !StringUtils.like(command.getModelName(), setting.getModelName())
                    || !command.isCompletion()) {
                return;
            }

            // 実績通知の場合は内部でカウント
            this.readTask(command.isCompletion(), new Task<Void>() {
                @Override
                protected Void call() throws Exception {

                    setting.getWorkCollection().stream()
                            .filter(work -> work.getWorkIds().contains(command.getWorkId()) || work.getWorkIds().isEmpty()) // 「割り当てなし」はすべての工程が対象
                            .map(work -> workActualPlans.get(work.getOrder() - 1))
                            .forEach(actualPlan -> {
                                actualPlan.setActualNum(actualPlan.getActualNum() + command.getCompNum());
                            });

                    draw(workActualPlans);

                    return null;
                }
            });
        } else if (msg instanceof ResetCommand) {
            updateSetting();
            readTask(true);
        }
    }

    /**
     * 実績の更新タスクを実施
     *
     * @param isCompletion
     * @param task
     */
    private void readTask(boolean isCompletion, Task task) {
        if (!isCompletion) {
            return;
        }

        new Thread(task).start();
    }

    /**
     * サーバーから実績を取得して更新
     *
     * @param isCompletion
     */
    private void readTask(boolean isCompletion) {
        readTask(isCompletion, new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (lock) {
                        workActualPlans = andonLineMonitorFacade.getWorkPlanNum(monitorId);

                        draw(workActualPlans);
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                return null;
            }
        });
    }

    @Override
    public void exitComponent() {
        logger.info("exitComponent");
    }

    /**
     * 進捗モニタ設定を更新する
     */
    private void updateSetting() {
        logger.info("updateSetting: monitorId={}", this.monitorId);
        try {
            if (!this.monitorId.equals(0L)) {
                this.setting = (AndonMonitorLineProductSetting) this.andonMonitorSettingFacade
                        .getLineSetting(this.monitorId, AndonMonitorLineProductSetting.class);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    private void draw(List<MonitorWorkPlanNumInfoEntity> works) {

        if (Objects.isNull(works) || works.isEmpty()) {
            return;
        }

        Platform.runLater(() -> {
            synchronized (lock) {
                // 描画処理
                vbox.getChildren().clear();
                vbox.setFillWidth(false);
                vbox.setAlignment(Pos.TOP_LEFT);

                final int columns = works.size() >= wrapItems ? wrapItems : works.size() % wrapItems;
                final int rows = (works.size() - 1) / wrapItems + 1;
                final double width = Math.floor((anchorPane.getWidth() - (GAP * (columns + 1))) / columns); // AnchorPaneの幅から、間のvcap分とpadding左右分を引く
                final double height = Math.floor((anchorPane.getHeight() - (GAP * rows * (2 + (isShowPlan ? 2 : 1)))) / rows); // AnchorPaneの高さから、間のhgap分とpadding上下分を引く

                List<GridPane> panes = IntStream.rangeClosed(0, works.size() / columns)
                        .mapToObj(i -> {
                            GridPane gridPane = new GridPane();
                            gridPane.setHgap(3);
                            gridPane.setVgap(3);
                            gridPane.setPadding(new Insets(3.0));
                            gridPane.setStyle("-fx-background-color: white");
                            gridPane.setSnapToPixel(false);

                            works.stream()
                                    .limit(i * columns + columns)
                                    .skip(i * columns)
                                    .forEach(workSetting -> {
                                        final int col = (workSetting.getOrder() - 1) % columns;
                                        if (isShowPlan) {
                                            gridPane.add(createLabel(workSetting.getTitle(), width, height / 3, "black"), col, 0);
                                            gridPane.add(createLabel(workSetting.getActualNum().toString(), width, height / 3, "black"), col, 1);
                                            gridPane.add(createLabel(workSetting.getPlanNum().toString(), width, height / 3, "black"), col, 2);
                                        } else {
                                            gridPane.add(createLabel(workSetting.getTitle(), width, height / 2, "#303030"), col, 0);
                                            gridPane.add(createLabel(workSetting.getActualNum().toString(), width, height / 2, "#303030"), col, 1);
                                        }
                                    });
                            return gridPane;
                        })
                        .collect(toList());

                vbox.getChildren().addAll(panes);
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
