package jp.adtekfuji.admonitorfloorplugin;

import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.utility.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.BreakCommand;
import jp.adtekfuji.adFactory.adinterface.command.CallingNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.entity.MonitorEquipmentStatusInfoEntity;
import jp.adtekfuji.andon.enumerate.MonitorStatusEnum;
import jp.adtekfuji.andon.media.MelodyPlayer;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "俯瞰フレーム")
@FxComponent(id = "Floor", fxmlPath = "/fxml/floor_compo.fxml")
public class MonitorFloorCompoFxController implements Initializable, ArgumentDelivery, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final AndonMonitorSettingFacade andonMonitorSettingFacade = new AndonMonitorSettingFacade();
    private final AndonLineMonitorFacade monitorFacade = new AndonLineMonitorFacade();
    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final DisplayedStatusInfoFacade displayedStatusInfoFacade = new DisplayedStatusInfoFacade();
    private final Object lock = new Object();
    private Long monitorId;
    private Map<StatusPatternEnum, DisplayedStatusInfoEntity> dispStatuses = new HashMap<>();
    private final Map<KanbanStatusEnum, StatusPatternEnum> kanbanStatusChanges = new HashMap<>();
    private final Map<MonitorStatusEnum, StatusPatternEnum> monitorStatusChanges = new HashMap<>();
    private final Map<Long, IconObject> icons = Collections.synchronizedMap(new HashMap<>());
    private final MelodyPlayer melodyPlayer = new MelodyPlayer();
    private StatusPatternEnum nowAllStatus = StatusPatternEnum.PLAN_NORMAL;
    private final LinkedList<IconObject> melodyQueue = new LinkedList<>();
    private IconObject nowMelodyIcon = null;

    private Timeline timeline = null;// 定期更新タイムライン

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ImageView imageBackground;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // モニタID
        this.monitorId = AndonLoginFacade.getMonitorId();

        this.imageBackground.setFitWidth(Setting.GetResolutionWidth());
        this.imageBackground.setFitHeight(Setting.GetResolutionHeight());

        // 定期更新タイムライン (休憩中の状態を取得するため、定期的にサーバーから設備ステータスを取得する)
        this.timeline = new Timeline(new KeyFrame(Duration.minutes(1), (ActionEvent event) -> {
            // 設備ステータス更新
            Platform.runLater(() -> {
                try {
                    // 定期的に取得すると、サーバーの負荷が高くなる
                    //this.updateAllEquipmentStatus();
                    this.playIcons();
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            });
        }));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();

        // 読み込みと描画.
        //this.drawBackground();
        this.readTask(this.monitorId);
    }

    /**
     * パラメータを設定する。
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
    }

    /**
     * 後処理
     */
    @Override
    public void exitComponent() {
        logger.info("exitComponent");
        try {
            this.timeline.stop();

            this.stopIcons();
            if (this.melodyPlayer.isPlaying()) {
                this.melodyPlayer.stop();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        try {
            this.icons.clear();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 表示を更新する。
     *
     * @param msg
     */
    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof ActualNoticeCommand) {
            // 実績通知イベント
            this.updateDisplayActual((ActualNoticeCommand) msg);
        } else if (msg instanceof CallingNoticeCommand) {
            // 呼出通知イベント
            Platform.runLater(() -> this.updateDisplayCalling((CallingNoticeCommand) msg));

        } else if (msg instanceof BreakCommand) {
            // 休憩イベント
            this.updateAllEquipmentStatus();
            Platform.runLater(() -> draw());
            
        } else if (msg instanceof ResetCommand) {
            // リセットイベント
            ResetCommand command = (ResetCommand) msg;
            logger.info("ResetCommand", command);
            this.readTask(this.monitorId);

        }
    }

    /**
     * ステータスを更新する。
     * 
     * @param notice 実績通知
     */
    private void updateDisplayActual(ActualNoticeCommand notice) {
        logger.info("ActualNotice", notice);
        try {
            if (!this.icons.containsKey(notice.getEquipmentId())) {
                return;
            }

            IconObject icon = this.icons.get(notice.getEquipmentId());
            if (icon.isCalling() || icon.isBreaktime()) {
                // 呼び出し・休憩の表示中は、ステータス通知で表示を更新しない。
                return;
            }

            // アニメーションを停止
            this.stopIcons();

            StatusPatternEnum pattern = this.kanbanStatusChanges.get(notice.getEquipmentStatus());
            this.updateEquipmentStatus(icon, pattern, null);

            // アニメーションを開始
            this.playIcons();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     * @param notice
     */
    private void updateDisplayCalling(CallingNoticeCommand notice) {
        logger.info("CallingNotice={}", notice);
        try {
            if (!this.icons.containsKey(notice.getEquipmentId())) {
                return;
            }

            StatusPatternEnum statusPattern = StatusPatternEnum.PLAN_NORMAL;
            IconObject icon = this.icons.get(notice.getEquipmentId());

            // アニメーションを停止
            this.stopIcons();

            if (notice.getIsCall()) {
                // 呼び出し中
                statusPattern = this.monitorStatusChanges.get(MonitorStatusEnum.CALL);
                this.updateEquipmentStatus(icon, statusPattern, notice.getReason());

            } else {
                // 呼び出し解除
                List<MonitorEquipmentStatusInfoEntity> entities = this.monitorFacade.getEquipmentStatus(this.monitorId, Arrays.asList(notice.getEquipmentId()));
                if (!entities.isEmpty()) {
                    statusPattern = this.monitorStatusChanges.get(entities.get(0).getStatus());
                }

                this.updateEquipmentStatus(icon, statusPattern, null);
            }

            // アニメーションを開始
            this.playIcons();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * データ取得タスク
     *
     * @param monitorId
     */
    private void readTask(Long monitorId) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // ステータス一覧を更新
                    updateStatusInfo();
                    // 設備情報を更新
                    createEquipmentIcon(monitorId);
                    // 設備ステータス更新
                    Platform.runLater(() -> {
                        try {
                            updateAllEquipmentStatus();
                            draw();
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                        }
                    });
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * ステータス一覧を更新
     */
    private void updateStatusInfo() {
        // ステータス一覧を取得.
        List<DisplayedStatusInfoEntity> statusList = this.displayedStatusInfoFacade.findAll();
        this.dispStatuses = statusList.stream().collect(Collectors.toMap(DisplayedStatusInfoEntity::getStatusName, c -> c));
        // カンバンステータス定義と設備ステータス定義の対応付け
        this.kanbanStatusChanges.clear();

        this.kanbanStatusChanges.put(KanbanStatusEnum.WORKING, StatusPatternEnum.WORK_NORMAL);
        this.kanbanStatusChanges.put(KanbanStatusEnum.INTERRUPT, StatusPatternEnum.INTERRUPT_NORMAL);
        this.kanbanStatusChanges.put(KanbanStatusEnum.SUSPEND, StatusPatternEnum.SUSPEND_NORMAL);
        this.kanbanStatusChanges.put(KanbanStatusEnum.COMPLETION, StatusPatternEnum.PLAN_NORMAL);

        // モニタステータス定義と設備ステータス定義の対応付け
        this.monitorStatusChanges.clear();

        this.monitorStatusChanges.put(MonitorStatusEnum.READY, StatusPatternEnum.PLAN_NORMAL);
        this.monitorStatusChanges.put(MonitorStatusEnum.WORKING, StatusPatternEnum.WORK_NORMAL);
        this.monitorStatusChanges.put(MonitorStatusEnum.SUSPEND, StatusPatternEnum.SUSPEND_NORMAL);
        this.monitorStatusChanges.put(MonitorStatusEnum.CALL, StatusPatternEnum.CALLING);
        this.monitorStatusChanges.put(MonitorStatusEnum.BREAK_TIME, StatusPatternEnum.BREAK_TIME);
    }

    /**
     * 設備情報を更新
     */
    private void createEquipmentIcon(Long monitorId) {

        try {
            // アイコンのアニメーションを停止する。
            this.stopIcons();

            if (this.melodyPlayer.isPlaying()) {
                this.melodyPlayer.stop();
            }
        } catch (Exception ex) {
        }

        this.icons.clear();

        // 設備設定を取得
        AndonMonitorLineProductSetting lineProductsetting = (AndonMonitorLineProductSetting) this.andonMonitorSettingFacade
                .getLineSetting(this.monitorId, AndonMonitorLineProductSetting.class);
        List<WorkEquipmentSetting> works = lineProductsetting.getWorkEquipmentCollection();
        for (WorkEquipmentSetting work : works) {
            // 設備IDをキーとして登録する
            Integer id = work.getOrder();
            for (Long equpId : work.getEquipmentIds()) {
                //設備情報を取得
                EquipmentInfoEntity info = this.equipmentInfoFacade.get(equpId);

                //設定ファイルに設備登録があること
                if (Setting.GetIconExist(id) && !this.icons.containsKey(equpId)) {
                    IconObject icon = new IconObject(
                            id, info.getEquipmentName(),
                            Setting.GetIconPosX(id),
                            Setting.GetIconPosY(id),
                            Setting.GetIconWidth(id),
                            Setting.GetIconHeight(id),
                            work, equpId);
                    this.icons.put(equpId, icon);
                }
            }
        }
    }

    /**
     * すべての設備状態を取得更新する
     */
    private void updateAllEquipmentStatus() {
        this.nowAllStatus = StatusPatternEnum.PLAN_NORMAL;

        List<Long> targetIds = new ArrayList<>(this.icons.keySet());
        List<MonitorEquipmentStatusInfoEntity> entities = this.monitorFacade.getEquipmentStatus(this.monitorId, targetIds);

        for (MonitorEquipmentStatusInfoEntity entity : entities) {
            if (this.icons.containsKey(entity.getEquipmentId())) {
                IconObject icon = this.icons.get(entity.getEquipmentId());
                MonitorStatusEnum status = entity.isCalled() ? MonitorStatusEnum.CALL : entity.getStatus();
                if (this.monitorStatusChanges.containsKey(status)) {
                    StatusPatternEnum st = this.monitorStatusChanges.get(entity.getStatus());
                    this.updateEquipmentStatus(icon, st, icon.getReason());
                }
            }
        }
    }

    /**
     * 設備状態を更新する
     *
     * @param status
     */
    private void updateEquipmentStatus(IconObject icon, StatusPatternEnum status, String reason) {
        logger.info("UpdateEquipmentStatus start: icon={}, status={}, reason={}", icon, status, reason);

        // アイコンのアニメーションを停止する。
        this.stopIcons();

        DisplayedStatusInfoEntity stat = this.dispStatuses.get(status);

        //ｱｲｺﾝのﾃｷｽﾄを「理由」または「設備名」に設定する
        //  (ここで表示がきりかわるわけではなく、表示はtimelineによって行われる)
        EquipmentInfoEntity info = this.equipmentInfoFacade.get(icon.getEquId());
        String eqName = info.getEquipmentName();

        if (Objects.nonNull(stat) && StatusPatternEnum.CALLING == stat.getStatusName()) {
            if (Objects.isNull(reason) || reason.isEmpty()) {
                //呼出理由が設定されていない場合は理由として""となるので理由ではなく設備名とする
                icon.setReason(eqName);
            } else {
                icon.setReason(reason);
            }

        } else {
            icon.setReason(eqName);
        }

        icon.Update(stat);
        logger.info("Status={}", stat.getStatusName());

        // 全体ステータス更新
        StatusPatternEnum all = StatusPatternEnum.PLAN_NORMAL;
        for (Map.Entry<Long, IconObject> e : this.icons.entrySet()) {
            all = StatusPatternEnum.compareStatus(all, e.getValue().getStatusPattern());
        }
        DisplayedStatusInfoEntity dispStatus = this.dispStatuses.get(all);

        //設備毎のメロディーを先入れ先出しで鳴らす.
        if (StatusPatternEnum.CALLING == status) {
            if (!this.melodyQueue.contains(icon)) {
                this.melodyQueue.offer(icon);
            }
        } else {
            this.melodyQueue.remove(icon);
            if (this.melodyQueue.isEmpty()) {
                this.nowMelodyIcon = null;
            }
        }

        if (StringUtils.isEmpty(dispStatus.getMelodyPath()) && this.melodyQueue.isEmpty()) {
            this.melodyPlayer.stop();
        } else if (this.nowMelodyIcon != this.melodyQueue.peek()) {
            this.nowMelodyIcon = this.melodyQueue.peek();
            //デフォルトとしてシステム設定のメロディ指定を採用.
            DisplayedStatusInfoEntity callStatus = this.dispStatuses.get(StatusPatternEnum.CALLING);
            String melodyPath = callStatus.getMelodyPath();
            Boolean melodyRepeat = callStatus.getMelodyRepeat();

            String path = this.nowMelodyIcon.getSetting().getCallMelodyPath();
            if (!StringUtils.isEmpty(path)) {
                //設備毎のメロディ指定を採用.
                File file = new File(path);
                if (file.exists()) {
                    melodyPath = path;
                    melodyRepeat = true;
                }
            }

            this.melodyPlayer.stop();
            this.melodyPlayer.play(melodyPath, melodyRepeat);
        } else if (this.nowAllStatus != all) {
            this.melodyPlayer.stop();
            this.melodyPlayer.play(dispStatus.getMelodyPath(), dispStatus.getMelodyRepeat());
        }
        this.nowAllStatus = all;

        logger.info("UpdateEquipmentStatus end: icon={}, status={}, reason={}", icon, status, reason);
    }

    /**
     * 描画
     */
    private void draw() {
        // アイコンのアニメーションを停止する。
        stopIcons();

        // 背景画像を描画する。
        this.drawBackground();

        // 設備アイコン
        this.icons.forEach((key, value) -> {
            if (this.anchorPane.getChildren().contains(value.getNode())) {
                this.anchorPane.getChildren().remove(value.getNode());
            }
            this.anchorPane.getChildren().add(value.getNode());
        });

        // アイコンのアニメーションを開始する。
        this.playIcons();
    }

    /**
     * 背景画像を描画する。
     */
    private void drawBackground() {
        try {
            File file = new File(Setting.GetBackImagePath());
            if (file.exists()) {
                Image image = new Image(new FileInputStream(file));
                this.imageBackground.setImage(image);
                if (!Setting.GetFitResolution()) {
                    // 背景画像を等倍サイズで表示する
                    this.imageBackground.setFitHeight(image.getHeight());
                    this.imageBackground.setFitWidth(image.getWidth());
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 全てのアイコンのアニメーションを開始する。
     */
    private void playIcons() {
        if (this.icons.isEmpty()) {
            return;
        }

        for (IconObject icon : this.icons.values()) {
            icon.playTimeline();
        }
    }

    /**
     * 全てのアイコンのアニメーションを停止する。
     */
    private void stopIcons() {
        if (this.icons.isEmpty()) {
            return;
        }

        for (IconObject icon : this.icons.values()) {
            icon.stopTimeline();
        }
    }
}
