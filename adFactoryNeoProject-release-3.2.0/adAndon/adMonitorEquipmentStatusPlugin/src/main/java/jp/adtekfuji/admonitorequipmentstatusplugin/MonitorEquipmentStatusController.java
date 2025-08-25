package jp.adtekfuji.admonitorequipmentstatusplugin;

import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.BreakCommand;
import jp.adtekfuji.adFactory.adinterface.command.CallingNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.MonitorEquipmentStatusInfoEntity;
import jp.adtekfuji.andon.entity.MonitorStatusEntity;
import jp.adtekfuji.andon.enumerate.MonitorStatusEnum;
import jp.adtekfuji.andon.media.MelodyPlayer;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 設備ステータス画面
 *
 * @author e.mori
 * @version 1.6.2
 * @since 2017.02.08.Wen
 */
@AndonComponent(title = "工程設備ステータスフレーム")
@FxComponent(id = "DailyEquipmentStatusMonitor", fxmlPath = "/fxml/equip_status_monitor.fxml")
public class MonitorEquipmentStatusController implements Initializable, AdAndonComponentInterface {

    /**
     * 終了コマンド
     */
    private class ExitCommand {
    }

    private final double GAP = 3.0;

    private final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade monitorFacade = new AndonLineMonitorFacade();
    private final AndonMonitorSettingFacade andonMonitorSettingFacade = new AndonMonitorSettingFacade();
    private final Map<Integer, Timeline> suspendTimerHolder = new HashMap<>();
    private final Map<Integer, Long> suspendTime = new HashMap<>();
    private final Map<Integer, Timeline> callReasonHolder = new HashMap<>();  // 2020/01/07 呼び出し理由の表示 追加
    private final Map<Integer, String> callReasons = new HashMap<>();  // 2020/01/07 呼び出し理由の表示 追加
    private final Object lock = new Object();
    private Long monitorId = 0L;
    private AndonMonitorLineProductSetting setting = null;
    private List<DisplayedStatusInfoEntity> displayStatuses = null;
    private List<MonitorStatusEntity> statusEntities = new ArrayList<>();
    private double fontSize = Double.NaN;
    private final LinkedList<Long> melodyQueue = new LinkedList<>();
    private final MelodyPlayer melodyPlayer = new MelodyPlayer();
    private String playingMelody;
    private boolean enableHelpMelody = false;
    private final Map<Integer, Timeline> equipTimelines = new HashMap<>();
    private final LinkedList<Object> queue = new LinkedList<>();
    private Thread thread;

    @FXML
    private AnchorPane anchorPane;

    private int wrapItems;

    /**
     * 初期処理
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // フォントサイズ
        if (!AdProperty.getProperties().containsKey(Constants.FONT_SIZE_SMALL)) {
            AdProperty.getProperties().setProperty(Constants.FONT_SIZE_SMALL, Constants.DEF_FONT_SIZE_SMALL);
        }
        this.fontSize = Double.parseDouble(AdProperty.getProperties().getProperty(Constants.FONT_SIZE_SMALL));

        this.wrapItems = Integer.parseInt(AdProperty.getProperties().getProperty(Constants.WRAP_ITEMS, Constants.WRAP_ITEMS_DEFAULT));

        // 画面サイズが変更された場合再描画
        this.anchorPane.getChildren().clear();
        this.anchorPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.draw();
        });

        this.anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.draw();
        });

        if (!AdProperty.getProperties().containsKey("helpMelodyFrame")) {
            AdProperty.getProperties().setProperty("helpMelodyFrame", "equipment");
        }
        this.enableHelpMelody = Objects.equals("equipment", AdProperty.getProperties().getProperty("helpMelodyFrame"));

        try {
            this.monitorId = AndonLoginFacade.getMonitorId();

            this.readSetting();

            if (!this.monitorId.equals(0L)) {
                this.displayStatuses = new DisplayedStatusInfoFacade().findAll();
                this.startupThread();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 実績受信時の画面更新処理
     *
     * @param msg 受信コマンド
     */
    @Override
    public void updateDisplay(Object msg) {
        if (!this.monitorId.equals(0L)) {
            this.send(msg);
        }
    }

    /**
     *
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

            if (this.melodyPlayer.isPlaying()) {
                this.melodyPlayer.stop();
            }
            for (Map.Entry<Integer, Timeline> entry : this.suspendTimerHolder.entrySet()) {
                entry.getValue().stop();
            }
            for (Map.Entry<Integer, Timeline> entry : this.callReasonHolder.entrySet()) {
                entry.getValue().stop();
            }
            //点滅用タイムライン消去
            this.equipTimelines.entrySet().stream()
                    .map(s -> s.getValue())
                    .forEach(t -> {
                        t.stop();
                        t.getKeyFrames().clear();
                    });
            this.equipTimelines.clear();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ステータス更新スレッドにコマンドを送信する。
     *
     * @param msg
     */
    private void send(Object msg) {
        synchronized (this.queue) {
            // 末尾に追加
            this.queue.add(msg);
            this.queue.notify();
        }
    }

    /**
     * ステータス更新スレッドを起動する。
     */
    private void startupThread() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // 初期化
                    statusEntities = createStatus(setting.getWorkEquipmentCollection());

                    // 描画
                    draw();

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
                                        updateStatus((ActualNoticeCommand) msg);

                                    } else if (msg instanceof CallingNoticeCommand) {
                                        callStatus((CallingNoticeCommand) msg);

                                    } else if (msg instanceof ResetCommand) {
                                        readSetting();
                                        statusEntities = createStatus(setting.getWorkEquipmentCollection());

                                    } else if (msg instanceof BreakCommand) {
                                        statusEntities = createStatus(setting.getWorkEquipmentCollection());

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
     * 描画処理
     *
     */
    private void draw() {
        Platform.runLater(() -> {
            synchronized (this.lock) {
                // 描画処理
                if (Objects.isNull(this.statusEntities)) {
                    return;
                }

                this.anchorPane.getChildren().clear();

                //点滅用タイムライン消去
                this.equipTimelines.entrySet().stream()
                        .map(s -> s.getValue())
                        .forEach(t -> {
                            t.stop();
                            t.getKeyFrames().clear();
                        });
                this.equipTimelines.clear();

                final int columns = this.statusEntities.size() >= wrapItems ? wrapItems : this.statusEntities.size() % this.wrapItems;
                final int rows = (this.statusEntities.size() - 1) / wrapItems + 1;
                final double width = Math.floor((this.anchorPane.getWidth() - (GAP * (columns + 1))) / columns); // AnchorPaneの幅から、間のvcap分とpadding左右分を引く
                final double height = Math.floor((this.anchorPane.getHeight() - (GAP * (1 + rows))) / rows); // AnchorPaneの高さから、間のhgap分とpadding上下分を引く

                TilePane pane = new TilePane();
                pane.setHgap(GAP);
                pane.setVgap(GAP);
                pane.setPrefColumns(columns);

                for (MonitorStatusEntity status : this.statusEntities) {
                    String text = status.getName();
                    Label label = new Label(text);
                    if (MonitorStatusEnum.SUSPEND.equals(status.getMonitorStatus())) {
                        // 中断開始時間が存在しない場合は現在時間を設定する。
                        if (!this.suspendTime.containsKey(status.getOrder())) {
                            this.suspendTime.put(status.getOrder(), System.currentTimeMillis());
                        }
                        // 中断時間表示用タイムラインを開始または再開する。
                        if (!this.suspendTimerHolder.containsKey(status.getOrder())) {
                            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(30), (ActionEvent event) -> this.draw()));
                            timeline.setCycleCount(Timeline.INDEFINITE);
                            timeline.play();
                            this.suspendTimerHolder.put(status.getOrder(), timeline);
                        } else {
                            this.suspendTimerHolder.get(status.getOrder()).play();
                        }
                        // 中断時間表示用タイムラインが開始してからの経過時間を表示する。
                        label.setText(DateUtils.formatCountup(System.currentTimeMillis() - this.suspendTime.get(status.getOrder())));
                    } else {
                        switch (status.getMonitorStatus()) {
                            case BREAK_TIME:
                                // 休憩中はカウントダウンを一時停止する。
                                if (this.suspendTimerHolder.containsKey(status.getOrder())) {
                                    this.suspendTimerHolder.get(status.getOrder()).pause();
                                }
                                break;
                            case CALL:
                                // 呼出中はカウントダウンを継続する。
                                break;
                            default:
                                // 中断時間表示用タイムラインをが存在する場合は消去する。
                                if (this.suspendTimerHolder.containsKey(status.getOrder())) {
                                    this.suspendTimerHolder.get(status.getOrder()).stop();
                                    this.suspendTimerHolder.remove(status.getOrder());
                                }
                                // 中断開始時間が存在する場合は消去する。
                                if (this.suspendTime.containsKey(status.getOrder())) {
                                    this.suspendTime.remove(status.getOrder());
                                }
                        }
                    }
                    // 2020/01/07 呼び出し理由の表示対応 呼び出し理由表示用タイムライン
                    if (MonitorStatusEnum.CALL.equals(status.getMonitorStatus())) {
                        if (this.callReasonHolder.containsKey(status.getOrder())) {
                            this.callReasonHolder.get(status.getOrder()).pause();
                        }
                    } else {
                        // 存在する場合は呼び出し理由表示用タイムラインを消去する。
                        if (this.callReasonHolder.containsKey(status.getOrder())) {
                            this.callReasonHolder.get(status.getOrder()).stop();
                            this.callReasonHolder.remove(status.getOrder());
                        }
                        // 存在する場合は呼び出し理由を消去する。
                        if (this.callReasons.containsKey(status.getOrder())) {
                            this.callReasons.remove(status.getOrder());
                        }
                    }

                    double size = MonitorTools.getFontSize(label.getText(), width, height, this.fontSize);
                    final String frontColor = status.getFrontColor();
                    final String backColor = status.getBackColor();

                    label.setPrefSize(width, height);
                    label.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                    label.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
                    label.setAlignment(Pos.CENTER);

                    Timeline timeline;
                    switch (status.getMonitorStatus()) {
                        case CALL:
                            // 2020/01/07 呼び出し理由の表示対応 呼び出しの場合は呼び出し理由と表示名を交互に表示
                            timeline = new Timeline();
                            timeline.setCycleCount(Timeline.INDEFINITE);
                            timeline.setAutoReverse(false);

                            String reason;
                            if (this.callReasons.containsKey(status.getOrder())) {
                                //呼び出し理由を保持しているなら呼び出し理由を取得
                                reason = this.callReasons.get(status.getOrder());
                                if (StringUtils.isEmpty(reason)) {
                                    reason = text;
                                }
                            } else {
                                //保持していない場合は表示名を取得
                                reason = text;
                            }
                            final double reasonSize = MonitorTools.getFontSize(reason, width, height, this.fontSize);
                            final long blinkTime = 1000L;

                            Color cFrontColor = Color.web(frontColor);
                            Color cBackColor = Color.web(backColor);

                            if (LightPatternEnum.BLINK.equals(status.getLightPattern())) {

                                // 点滅
                                StackPane stack = new StackPane();
                                stack.prefWidthProperty().set(width);
                                stack.prefHeightProperty().set(height);

                                // 背景アニメーション用
                                Rectangle backRect = new Rectangle();
                                backRect.widthProperty().bind(stack.widthProperty());
                                backRect.heightProperty().bind(stack.heightProperty());

                                stack.getChildren().addAll(backRect, label);
                                pane.getChildren().add(stack);

                                timeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO,
                                        new KeyValue(backRect.fillProperty(), cBackColor),
                                        new KeyValue(label.styleProperty(), String.format("-fx-font-size:%fpx;", reasonSize)),
                                        new KeyValue(label.textFillProperty(), cFrontColor),
                                        new KeyValue(label.textProperty(), reason)
                                ),
                                        new KeyFrame(new Duration(blinkTime),
                                                new KeyValue(backRect.fillProperty(), Color.BLACK),
                                                new KeyValue(label.styleProperty(), String.format("-fx-font-size:%fpx;", size)),
                                                new KeyValue(label.textFillProperty(), Color.WHITE),
                                                new KeyValue(label.textProperty(), text)
                                        ),
                                        new KeyFrame(new Duration(blinkTime * 2),
                                                new KeyValue(backRect.fillProperty(), Color.BLACK),
                                                new KeyValue(label.styleProperty(), String.format("-fx-font-size:%fpx;", reasonSize)),
                                                new KeyValue(label.textFillProperty(), Color.WHITE),
                                                new KeyValue(label.textProperty(), reason)
                                        )
                                );

                                if (!this.callReasonHolder.containsKey(status.getOrder())) {
                                    timeline.playFrom(Duration.ZERO);
                                } else {
                                    timeline.playFrom(this.callReasonHolder.get(status.getOrder()).getCurrentTime());
                                }
                                this.callReasonHolder.put(status.getOrder(), timeline);

                            } else {
                                // 点灯
                                pane.getChildren().add(label);

                                if (!StringUtils.isEmpty(reason)) {
                                    timeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO,
                                            new KeyValue(label.styleProperty(), String.format("-fx-font-size:%fpx; -fx-background-color:%s;", reasonSize, backColor)),
                                            new KeyValue(label.textFillProperty(), cFrontColor),
                                            new KeyValue(label.textProperty(), reason)
                                    ),
                                            new KeyFrame(new Duration(blinkTime),
                                                    new KeyValue(label.styleProperty(), String.format("-fx-font-size:%fpx; -fx-background-color:%s;", size, backColor)),
                                                    new KeyValue(label.textFillProperty(), cFrontColor),
                                                    new KeyValue(label.textProperty(), text)
                                            ),
                                            new KeyFrame(new Duration(blinkTime * 2),
                                                    new KeyValue(label.styleProperty(), String.format("-fx-font-size:%fpx; -fx-background-color:%s;", reasonSize, backColor)),
                                                    new KeyValue(label.textFillProperty(), cFrontColor),
                                                    new KeyValue(label.textProperty(), reason)
                                            )
                                    );

                                    if (!this.callReasonHolder.containsKey(status.getOrder())) {
                                        timeline.playFrom(Duration.ZERO);
                                    } else {
                                        timeline.playFrom(this.callReasonHolder.get(status.getOrder()).getCurrentTime());
                                    }
                                    this.callReasonHolder.put(status.getOrder(), timeline);

                                } else {
                                    label.setStyle(String.format("-fx-font-size:%fpx; -fx-text-fill:%s; -fx-background-color:%s;", size, frontColor, backColor));
                                }
                            }
                            break;

                        // 呼び出し以外の場合
                        default:
                            if (status.getLightPattern() == LightPatternEnum.BLINK) {
                                timeline = new Timeline();
                                timeline.setCycleCount(Timeline.INDEFINITE);
                                timeline.setAutoReverse(true);
                                timeline.getKeyFrames().addAll(
                                        new KeyFrame(Duration.ZERO, (e) -> {
                                            label.setStyle(String.format("-fx-font-size:%fpx; -fx-text-fill:%s; -fx-background-color:%s;", size, frontColor, backColor));
                                        }),
                                        new KeyFrame(Duration.millis(300), (e) -> {
                                            label.setStyle(String.format("-fx-font-size:%fpx; -fx-text-fill:%s; -fx-background-color:%s;", size, frontColor, "black"));
                                        })
                                );
                                timeline.play();
                                this.equipTimelines.put(status.getOrder(), timeline);
                            } else {
                                label.setStyle(String.format("-fx-font-size:%fpx; -fx-text-fill:%s; -fx-background-color:%s;", size, frontColor, backColor));
                            }
                            pane.getChildren().add(label);
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
     * 文字の長さを取得
     *
     * @param text
     * @return
     */
    private int getLength(String text) {
        try {
            return text.getBytes("Shift_JIS").length;
        } catch (UnsupportedEncodingException ex) {
        }
        return text.length();
    }

    /**
     * 設定生成処理
     *
     */
    private List<MonitorStatusEntity> createStatus(List<WorkEquipmentSetting> equipmentSettings) {
        if (Objects.isNull(equipmentSettings)) {
            return new ArrayList<>();
        }
        
        List<MonitorStatusEntity> statuses = new ArrayList<>();

        synchronized (this.lock) {
            try {
                Set<Long> equipmentIds = new HashSet<>();
                for (WorkEquipmentSetting equipmentSetting : equipmentSettings) {
                    equipmentIds.addAll(equipmentSetting.getEquipmentIds());
                }

                // 設備ステータスを取得
                List<MonitorEquipmentStatusInfoEntity> entities = this.monitorFacade.getEquipmentStatus(this.monitorId, new ArrayList<>(equipmentIds));

                for (WorkEquipmentSetting equipmentSetting : equipmentSettings) {
                    Map<Long, MonitorStatusEnum> equipmentStatuses = new HashMap<>();

                    for (Long equipmentId : equipmentSetting.getEquipmentIds()) {
                        Optional<MonitorEquipmentStatusInfoEntity> optional = entities.stream().filter(o -> Objects.equals(o.getEquipmentId(), equipmentId)).findFirst();
                        if (optional.isPresent()) {
                            equipmentStatuses.put(equipmentId, optional.get().getStatus());
                        } else {
                            equipmentStatuses.put(equipmentId, MonitorStatusEnum.READY);
                        }
                    }

                    statuses.add(MonitorStatusEntity.getMonitorStatusEntity(equipmentSetting, this.displayStatuses, equipmentStatuses));
                }

                Collections.sort(statuses, (MonitorStatusEntity a, MonitorStatusEntity b) -> a.getOrder().compareTo(b.getOrder()));
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }

        return statuses;
    }

    /**
     * 設定更新処理
     *
     */
    private void updateStatus(ActualNoticeCommand cmd) {
        if (Objects.isNull(this.setting)) {
            if (this.readSetting()) {
                statusEntities = createStatus(setting.getWorkEquipmentCollection());
            } else {
                return;
            }
        }
        
        if (!StringUtils.like(cmd.getModelName(), this.setting.getModelName())) {
            return;
        }
       
        for (WorkEquipmentSetting equipmentSetting : this.setting.getWorkEquipmentCollection()) {
            for (Long equipmentId : equipmentSetting.getEquipmentIds()) {
                if (Objects.equals(cmd.getEquipmentId(), equipmentId)) {
                    for (MonitorStatusEntity monitorStatus : this.statusEntities) {
                        if (monitorStatus.getOrder().equals(equipmentSetting.getOrder())) {
                            monitorStatus.updateStatus(cmd.getWorkKanbanStatus(), cmd.getEquipmentId(), this.displayStatuses, false);
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * 呼び出し処理
     *
     * @param cmd 呼出通知コマンド
     */
    private void callStatus(CallingNoticeCommand cmd) {
        WorkEquipmentSetting equipment = null;
        for (WorkEquipmentSetting equipmentSetting : this.setting.getWorkEquipmentCollection()) {
            for (Long equipId : equipmentSetting.getEquipmentIds()) {
                if (cmd.getEquipmentId().equals(equipId)) {
                    equipment = equipmentSetting;
                    break;
                }
            }
        }
        
        if (Objects.isNull(equipment)) {
            logger.info("Equipment is not subject to display: " + cmd.equipmentId(monitorId));
            return;
        }

        if (cmd.getIsCall()) {
            for (MonitorStatusEntity monitorStatus : this.statusEntities) {
                if (Objects.equals(monitorStatus.getOrder(), equipment.getOrder())) {
                    monitorStatus.updateStatus(null, cmd.getEquipmentId(), this.displayStatuses, true);
                    // 2020/01/08 呼び出し理由表示対応 呼び出し理由を取得
                    this.callReasons.put(monitorStatus.getOrder(), cmd.getReason());

                    //呼び出しメロディを先勝ちで鳴らすためキューにいれる
                    if (!this.melodyQueue.contains(cmd.getEquipmentId())) {
                        this.melodyQueue.offer(cmd.getEquipmentId());
                    }
                }
            }
        } else {
            List<MonitorStatusEntity> list = this.createStatus(Arrays.asList(equipment));
            if (!list.isEmpty()) {
                MonitorStatusEntity monitorStatus = list.get(0);
                this.statusEntities.set(monitorStatus.getOrder() - 1, monitorStatus);
            }

            this.melodyQueue.remove(cmd.getEquipmentId());
            if (this.melodyQueue.isEmpty()) {
                this.playingMelody = "";
            }
        }

        //呼び出しメロディを鳴らす
        if (this.enableHelpMelody) {
            if (this.melodyQueue.isEmpty()) {
                this.melodyPlayer.stop();
            } else {
                //デフォルトとしてシステム設定のメロディを使う
                DisplayedStatusInfoEntity status = getDisplayStatus(StatusPatternEnum.CALLING);
                String defaultMelodyPath = status.getMelodyPath();
                Boolean isRepeat = status.getMelodyRepeat();

                String melodyPath = this.setting.getWorkEquipmentCollection().stream()
                        .filter(entity -> entity.getEquipmentIds().stream().anyMatch(id -> id.equals(this.melodyQueue.peek())))
                        .map(entity -> entity.getCallMelodyPath())
                        .findFirst().orElse(defaultMelodyPath);

                if (StringUtils.isEmpty(melodyPath)) {
                    melodyPath = defaultMelodyPath;
                }

                //同じメロディなら引き続き鳴らす
                if (!Objects.equals(melodyPath, this.playingMelody)) {
                    this.melodyPlayer.stop();
                    this.melodyPlayer.play(melodyPath, isRepeat);

                    this.playingMelody = melodyPath;
                }
            }
        }
    }

    /**
     * ディスプレイ設定取得処理
     *
     * @param status
     * @param displays
     * @return
     */
    private DisplayedStatusInfoEntity getDisplayStatus(StatusPatternEnum status) {
        for (DisplayedStatusInfoEntity display : this.displayStatuses) {
            if (display.getStatusName().equals(status)) {
                return display;
            }
        }
        return null;
    }

    /**
     * 進捗モニタ設定を取得する。
     * 
     * @return true:取得成功, false:取得失敗
     */
    private boolean readSetting() {
        try {
            logger.info("readSetting start: monitorId={}", this.monitorId);

            if (!this.monitorId.equals(0L)) {
                this.setting = (AndonMonitorLineProductSetting) this.andonMonitorSettingFacade
                        .getLineSetting(this.monitorId, AndonMonitorLineProductSetting.class);
            }

            return true;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;

        } finally {
            logger.info("readSetting end.");
        }
    }
}
