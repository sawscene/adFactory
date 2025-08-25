package jp.adtekfuji.admonitorlinestatusplugin;

import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.BreakCommand;
import jp.adtekfuji.adFactory.adinterface.command.CallingNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.enumerate.ActualResultDailyEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.entity.MonitorEquipmentStatusInfoEntity;
import jp.adtekfuji.andon.entity.MonitorStatusEntity;
import jp.adtekfuji.andon.enumerate.MonitorStatusEnum;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ラインステータス画面
 *
 * @author e.mori
 * @version 1.6.2
 * @since 2017.02.08.Wen
 */
@AndonComponent(title = "ライン全体ステータスフレーム")
@FxComponent(id = "DailyLineStatusMonitor", fxmlPath = "/fxml/line_status_monitor.fxml")
public class MonitorLineStatusController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();

    private final ActualResultInfoFacade actualResultFacade = new ActualResultInfoFacade();
    private final AndonMonitorSettingFacade andonMonitorSettingFacade = new AndonMonitorSettingFacade();
    private final AndonLineMonitorFacade monitorFacade = new AndonLineMonitorFacade();

    private MonitorStatusEntity lineStatus = null;
    private final MelodyPlayer melodyPlayer = new MelodyPlayer();
    private Long monitorId = 0L;
    private final Object lock = new Object();
    private AndonMonitorLineProductSetting setting = null;
    private List<DisplayedStatusInfoEntity> displayStatuses = null;
    private Timeline timeline = null;
    private final LinkedList<Long> melodyQueue = new LinkedList<>();
    private String playingMelody;
    private boolean enableHelpMelody = false;
    private final Map<MonitorStatusEnum, Integer> statusPriority = new HashMap<>();
    private Timeline blinkTimeline = new Timeline();
    private final Set<Long> targetEquipmentIds = new HashSet<>(); // 対象設備

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label statusLabel;

    /**
     * 初期処理
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //ステータス優先順位設定(小さいほうが強い)
        this.statusPriority.put(MonitorStatusEnum.CALL, 1);
        this.statusPriority.put(MonitorStatusEnum.BREAK_TIME, 2);
        this.statusPriority.put(MonitorStatusEnum.WORKING, 3);
        this.statusPriority.put(MonitorStatusEnum.SUSPEND, 4);
        this.statusPriority.put(MonitorStatusEnum.READY, 5);

        // 画面サイズが変更された場合再描画
        this.statusLabel.setText("");
        this.timeline = new Timeline(new KeyFrame(Duration.minutes(1), (ActionEvent event) -> {
            draw();
        }));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();
        this.anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
        if (!AdProperty.getProperties().containsKey("helpMelodyFrame")) {
            AdProperty.getProperties().setProperty("helpMelodyFrame", "equipment");
        }
        this.enableHelpMelody = Objects.equals("line", AdProperty.getProperties().getProperty("helpMelodyFrame"));

        try {
            // モニタID
            this.monitorId = AndonLoginFacade.getMonitorId();

            this.readSetting();

            if (!this.monitorId.equals(0L)) {
                this.displayStatuses = new DisplayedStatusInfoFacade().findAll();
                this.readTask(new ActualNoticeCommand(), true);
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
        try {
            if (msg instanceof ActualNoticeCommand) {
                // 実績通知 (表示対象外の実績通知の場合は無視する)
                ActualNoticeCommand command = (ActualNoticeCommand) msg;
                if (Objects.nonNull(this.setting)) {
                    // 設備
                    if (!this.targetEquipmentIds.contains(command.getEquipmentId())) {
                        logger.info("ActualNoticeCommand: not target equipmentId:{}", command.getEquipmentId());
                        return;
                    }
                    // モデル名
                    if (!StringUtils.like(command.getModelName(), this.setting.getModelName())) {
                        logger.info("ActualNoticeCommand: not target modelName:{}", command.getModelName());
                        return;
                    }
                }

                this.readTask(msg, false);

            } else if (msg instanceof CallingNoticeCommand command) {
                // 呼び出し
                if (Objects.nonNull(this.setting) && !this.targetEquipmentIds.contains(command.getEquipmentId())) {
                    // 対象設備以外の通知は無視する。
                    logger.info("CallingNoticeCommand: not target equipmentId:{}", command.getEquipmentId());
                    return;
                }

                this.readTask(msg, false);

            } else if (msg instanceof BreakCommand) {
                // 休憩イベント
                this.readTask(msg, false);

            } else if (msg instanceof ResetCommand) {
                // リセット
                this.readSetting();
                this.readTask(msg, false);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    @Override
    public void exitComponent() {
        logger.info("exitComponent");
        try {
            melodyPlayer.stop();
            timeline.stop();
            blinkTimeline.stop();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 進捗情報読み込み(サーバーから)
     *
     * @param monitorId 自身の設備ID
     */
    private void readTask(Object msg, boolean isCreate) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (lock) {
                        if (msg instanceof ActualNoticeCommand) {
                            ActualNoticeCommand cmd = (ActualNoticeCommand) msg;
                            if (isCreate) {
                                lineStatus = createStatus();
                            } else {
                                updateStatus(cmd);
                            }
                        } else if (msg instanceof CallingNoticeCommand) {
                            CallingNoticeCommand cmd = (CallingNoticeCommand) msg;

                            if (cmd.getIsCall()) {
                                //呼び出しメロディを先勝ちで鳴らすためキューにいれる
                                if (!melodyQueue.contains(cmd.getEquipmentId())) {
                                    melodyQueue.offer(cmd.getEquipmentId());
                                }
                            } else {
                                melodyQueue.remove(cmd.getEquipmentId());
                                if (melodyQueue.isEmpty()) {
                                    playingMelody = "";
                                }
                            }

                            // メロディキューが空ではない間はステータス表示を呼び出し中にする
                            if (melodyQueue.isEmpty()) {
                                lineStatus.setIsCall(false);
                            } else {
                                lineStatus.setIsCall(true);
                            }

                            updateDisplayStatus(MonitorStatusEnum.CALL);

                        } else {
                            lineStatus = createStatus();
                        }
                    }

                    //キューにヘルプ中の設備が存在する間は呼び出し続ける
                    if (enableHelpMelody) {
                        if (melodyQueue.isEmpty()) {
                            melodyPlayer.stop();
                        } else {
                            //デフォルトとしてシステム設定のメロディを使う
                            DisplayedStatusInfoEntity status = getDisplayStatus(StatusPatternEnum.CALLING);
                            String defaultMelodyPath = status.getMelodyPath();
                            Boolean isRepeat = status.getMelodyRepeat();

                            String melodyPath = setting.getWorkEquipmentCollection().stream()
                                    .filter(entity -> entity.getEquipmentIds().stream().anyMatch(id -> id.equals(melodyQueue.peek())))
                                    .map(entity -> entity.getCallMelodyPath())
                                    .findFirst().orElse(defaultMelodyPath);

                            if (StringUtils.isEmpty(melodyPath)) {
                                melodyPath = defaultMelodyPath;
                            }

                            //同じメロディなら引き続き鳴らす
                            if (!Objects.equals(melodyPath, playingMelody)) {
                                melodyPlayer.stop();
                                melodyPlayer.play(melodyPath, isRepeat);

                                playingMelody = melodyPath;
                            }
                        }
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
     * 描画処理
     *
     */
    private void draw() {
        Platform.runLater(() -> {
            synchronized (lock) {
                // 描画処理
                if (Objects.isNull(lineStatus)) {
                    return;
                }
                boolean isBreakTime = false;
                for (BreakTimeInfoEntity breaktime : setting.getBreaktimes()) {
                    Calendar breakStart = Calendar.getInstance();
                    breakStart.setTime(breaktime.getStarttime());
                    breakStart.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE));

                    Calendar breakEnd = Calendar.getInstance();
                    breakEnd.setTime(breaktime.getEndtime());
                    breakEnd.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE));

                    Date now = new Date();

                    if (now.after(breakStart.getTime()) && now.before(breakEnd.getTime())) {
                        isBreakTime = true;
                    }
                }

                //点滅用タイムライン消去
                this.blinkTimeline.stop();
                this.blinkTimeline.getKeyFrames().clear();

                String text;
                Double size;
                String frontColor;
                String backColor;
                if (lineStatus.isIsCall()) {
                    DisplayedStatusInfoEntity call = getDisplayStatus(StatusPatternEnum.CALLING);
                    text = call.getNotationName();
                    size = Math.min(statusLabel.getWidth() / text.length(), statusLabel.getHeight() * 0.8);
                    frontColor = call.getFontColor();
                    backColor = call.getBackColor();
                    lineStatus.setLightPattern(call.getLightPattern());
                } else if (isBreakTime) {
                    DisplayedStatusInfoEntity breaktime = getDisplayStatus(StatusPatternEnum.BREAK_TIME);
                    text = breaktime.getNotationName();
                    size = Math.min(statusLabel.getWidth() / text.length(), statusLabel.getHeight() * 0.8);
                    frontColor = breaktime.getFontColor();
                    backColor = breaktime.getBackColor();
                    lineStatus.setLightPattern(breaktime.getLightPattern());

                } else {
                    text = lineStatus.getName();
                    size = Math.min(statusLabel.getWidth() / text.length(), statusLabel.getHeight() * 0.8);
                    frontColor = lineStatus.getFrontColor();
                    backColor = lineStatus.getBackColor();
                }

                //点滅用タイムライン構築
                if (lineStatus.getLightPattern() == LightPatternEnum.BLINK) {
                    blinkTimeline = new Timeline();
                    blinkTimeline.setCycleCount(Timeline.INDEFINITE);
                    blinkTimeline.setAutoReverse(true);
                    blinkTimeline.getKeyFrames().addAll(
                            new KeyFrame(Duration.ZERO, (e) -> {
                                statusLabel.setStyle(String.format("-fx-font-size:%fpx; -fx-text-fill:%s; -fx-background-color:%s;", size, frontColor, backColor));
                            }),
                            new KeyFrame(Duration.millis(300), (e) -> {
                                statusLabel.setStyle(String.format("-fx-font-size:%fpx; -fx-text-fill:%s; -fx-background-color:%s;", size, frontColor, "black"));
                            })
                    );
                    blinkTimeline.play();
                } else {
                    statusLabel.setStyle(String.format("-fx-font-size:%dpx; -fx-text-fill:%s; -fx-background-color:%s;", size.longValue(), frontColor, backColor));
                }

                statusLabel.setText(text);
            }
        });
    }

    @FXML
    private void onSpeakerMouseCliked(MouseEvent event) {
        melodyPlayer.switchMute();
    }

    @FXML
    private void onSpeakerTouchPressed(TouchEvent event) {
        melodyPlayer.switchMute();
    }

    /**
     * ラインステータス情報を生成する。
     *
     */
    private MonitorStatusEntity createStatus() {
        MonitorStatusEntity statuses;
        List<Long> equipmentIds = new ArrayList<>(targetEquipmentIds);
        
        // 対象設備選択で設定した設備について、それぞれ今日の最新の実績を取得する。
        List<ActualResultEntity> actualResults = actualResultFacade.findLastActualResulList(new ActualSearchCondition()
                .modelName(setting.getModelName())
                .equipmentList(equipmentIds)
                .resultDailyEnum(ActualResultDailyEnum.DAILY));

        if (!actualResults.isEmpty()) {
            // 優先度の高い実績のステータスをセットする。
            actualResults.sort(Comparator.comparing(item -> item.getActualStatus()));
            statuses = MonitorStatusEntity.getMonitorStatusEntity(actualResults.get(0).getActualStatus(), setting, displayStatuses, new HashMap<>());
        } else {
            statuses = MonitorStatusEntity.getMonitorStatusEntity(KanbanStatusEnum.PLANNED, setting, displayStatuses, new HashMap<>());
        }

        // 呼出状態
        List<MonitorEquipmentStatusInfoEntity> equipStatuses = this.monitorFacade.getEquipmentStatus(this.monitorId, equipmentIds);
        statuses.setIsCall(equipStatuses.stream().filter(o -> o.isCalled()).findFirst().isPresent());
        
        // 各設備のステータス
        Map<Long, MonitorStatusEnum> equipments = actualResults.stream()
                .collect(Collectors.toMap(ActualResultEntity::getFkEquipmentId, o -> MonitorStatusEnum.valueOf(o.getActualStatus())));
        equipmentIds.stream().forEach(equipmentId -> { 
            if (!equipments.containsKey(equipmentId)) {
                equipments.put(equipmentId, MonitorStatusEnum.READY);
            }
        });
        statuses.setEquipmentStatus(equipments);
        
        return statuses;
    }

    /**
     * 設定更新処理
     *
     */
    private void updateStatus(ActualNoticeCommand cmd) {
        MonitorStatusEnum notice = MonitorStatusEnum.valueOf(cmd.getWorkKanbanStatus());
        lineStatus.getEquipmentStatus().put(cmd.getEquipmentId(), notice);

        updateDisplayStatus(notice);
    }

    /**
     * 優先度に沿った表示になるよう設定する
     *
     */
    private void updateDisplayStatus(MonitorStatusEnum notice) {
        // 現在の呼び出し状態を保持する
        boolean isCalling = lineStatus.isIsCall() == true;

        // 優先度の高いものから見て行って最初に出現したものを現在のステータスにする
        MonitorStatusEnum nextMonitorStutas = this.statusPriority.entrySet().stream()
                .sorted(Entry.comparingByValue())
                .map(e -> e.getKey())
                .filter(s -> lineStatus.getEquipmentStatus().values().contains(s))
                .findFirst()
                .orElse(MonitorStatusEnum.READY);

        if (notice.equals(MonitorStatusEnum.READY)) {
            boolean isUpdate = true;
            for (Map.Entry<Long, MonitorStatusEnum> entry : lineStatus.getEquipmentStatus().entrySet()) {
                if (!entry.getValue().equals(MonitorStatusEnum.READY)) {
                    isUpdate = false;
                }
            }
            if (isUpdate) {
                lineStatus = MonitorStatusEntity.getMonitorStatusEntity(nextMonitorStutas, setting, displayStatuses, lineStatus.getEquipmentStatus());
            }
        } else {
            lineStatus = MonitorStatusEntity.getMonitorStatusEntity(nextMonitorStutas, setting, displayStatuses, lineStatus.getEquipmentStatus());
        }

        lineStatus.setIsCall(isCalling);
    }

    /**
     * ディスプレイ設定取得処理
     *
     * @param status
     * @param displays
     * @return
     */
    private DisplayedStatusInfoEntity getDisplayStatus(StatusPatternEnum status) {
        for (DisplayedStatusInfoEntity display : displayStatuses) {
            if (display.getStatusName().equals(status)) {
                return display;
            }
        }
        return null;
    }

    /**
     * 進捗モニタ設定を取得する。
     */
    private void readSetting() {
        // 画面更新中に進捗モニタ設定が取得されないようにする
        synchronized (this.lock) {
            try {
                logger.info("readSetting start: monitorId={}", this.monitorId);

                if (!this.monitorId.equals(0L)) {
                    this.setting = (AndonMonitorLineProductSetting) this.andonMonitorSettingFacade
                            .getLineSetting(this.monitorId, AndonMonitorLineProductSetting.class);
                }

                // ステータスは、対象ラインに設定した設備とその子設備ではなく、対象設備選択で設定した設備を対象とする。
                if (Objects.nonNull(this.setting) && Objects.nonNull(this.setting.getWorkEquipmentCollection())) {
                    // 対象設備
                    this.targetEquipmentIds.clear();

                    // 対象設備選択で設定した設備
                    for (WorkEquipmentSetting equipmentSetting : this.setting.getWorkEquipmentCollection()) {
                        if (Objects.isNull(equipmentSetting.getEquipmentIds()) || equipmentSetting.getEquipmentIds().isEmpty()) {
                            continue;
                        }
                        this.targetEquipmentIds.addAll(equipmentSetting.getEquipmentIds());
                    }
                }

            } catch (Exception ex) {
                logger.fatal(ex, ex);

            } finally {
                logger.info("readSetting end.");
            }
        }
    }
}
