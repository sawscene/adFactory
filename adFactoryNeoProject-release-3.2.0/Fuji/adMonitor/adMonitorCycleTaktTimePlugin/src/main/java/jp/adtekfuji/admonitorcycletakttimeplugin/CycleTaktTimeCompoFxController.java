package jp.adtekfuji.admonitorcycletakttimeplugin;

import jp.adtekfuji.admonitorcycletakttimeplugin.entity.CycleTaktDispInfo;
import jp.adtekfuji.admonitorcycletakttimeplugin.entity.CycleMelodyPlayInfo;
import jp.adtekfuji.admonitorcycletakttimeplugin.enumerate.CycleTaktDispTypeEnum;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateUtils;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.entity.MonitorReasonNumInfoEntity;
import jp.adtekfuji.andon.property.MonitorSettingFuji;
import jp.adtekfuji.forfujiapp.entity.monitor.CycleMelodyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.monitor.CycleTaktInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * サイクルタクトタイムフレームのコントローラー
 *
 * @author nar-nakamura
 */
@AndonComponent(title = "サイクルタクトタイムフレーム")
@FxComponent(id = "CycleTaktTimeCompo", fxmlPath = "/fxml/admonitorcycletakttimeplugin/cycle_takt_time_compo.fxml")
public class CycleTaktTimeCompoFxController implements Initializable, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();
    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern(LocaleUtils.getString("key.ForFuji.CycleTaktTime.DateFormat"));
    private static final List<KanbanStatusEnum> lineoutStatusList = Arrays.asList(KanbanStatusEnum.INTERRUPT, KanbanStatusEnum.SUSPEND);// ラインアウト対象のステータス

    private final MelodyPlayer melodyPlayer = new MelodyPlayer();

    private final AndonLineMonitorFacade monitorFacade = new AndonLineMonitorFacade();
    private final Object lock = new Object();
    private Long monitorId;
    private Timeline dispTimer = null;
    private Timeline melodyTimer = null;
    private MonitorSettingFuji setting;

    private LocalDateTime dispDateTime = null;
    private LocalDateTime melodyDateTime = null;

    private final List<CycleTaktDispInfo> cycleTaktList = new ArrayList<>();
    private CycleTaktDispInfo cycleTaktNow = null;
    private int nextTaktId;

    private final List<CycleMelodyPlayInfo> cycleMelodyList = new ArrayList<>();
    private int nextMelodyId;

    private static final String LUNCH_TIME_NAME = "昼休憩";

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane mainPane;
    @FXML
    private Label currentDateLabel;// 現在の日付
    @FXML
    private Label productionNumLabel;// 生産台数
    @FXML
    private Pane countdownBackgroundPane;// 残り時間の背景
    @FXML
    private Pane countdownPane;// 残り時間
    @FXML
    private Label countdownMinLabel;// 残り時間(mm)
    @FXML
    private Label countdownSecLabel;// 残り時間(ss)
    @FXML
    private Pane breaktimePane;// 休憩時間
    @FXML
    private Pane refreshTimePane;// リフレッシュタイム
    @FXML
    private Pane lunchTimePane;// 昼休憩
    @FXML
    private Label breaktimeMinLabel;// 休憩時間の残り(mm)
    @FXML
    private Label breaktimeSecLabel;// 休憩時間の残り(ss)
    @FXML
    private Label planNumLabel;// 計画台数
    @FXML
    private Label taktMinLabel;// タクト(mm)
    @FXML
    private Label taktSecLabel;// タクト(ss)
    @FXML
    private Label lineoutNumLabel;// ラインアウト数

    /**
     * 初期処理
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("readMonitorSetting:{}", monitorId);
        datetimeFormatter = DateTimeFormatter.ofPattern(LocaleUtils.getString("key.ForFuji.CycleTaktTime.DateFormat"));
        try {
            readSetting();

            // 画面サイズが変更されたら、mainPaneの表示倍率を更新する。
            anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                changeSizeTask();
            });
            anchorPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                changeSizeTask();
            });

            // 定期的に表示を更新する。
            dispTimer = new Timeline(new KeyFrame(Duration.millis(100), (ActionEvent event) -> {
                draw();
            }));
            dispTimer.setCycleCount(Timeline.INDEFINITE);

            // メロディ鳴動
            melodyTimer = new Timeline(new KeyFrame(Duration.millis(100), (ActionEvent event) -> {
                playMelody();
            }));
            melodyTimer.setCycleCount(Timeline.INDEFINITE);

            readTask(this.monitorId);

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
        if (msg instanceof ActualNoticeCommand) {
            // ラインアウト対象となる実績通知を受信した場合、ラインアウト数の表示更新を行なう。
            ActualNoticeCommand command = (ActualNoticeCommand) msg;
            if (lineoutStatusList.contains(command.getWorkKanbanStatus())) {
                readTask(command.getMonitorId());
            }
        } else if (msg instanceof ResetCommand) {
            // リセット通知を受信した場合
            dispTimer.pause();
            melodyTimer.pause();
            readSetting();
            draw();
            playMelody();
            readTask(this.monitorId);
        }
    }

    /**
     * 
     */
    @Override
    public void exitComponent() {
        logger.info("exitComponent");
        Platform.runLater(() -> {
            try {
                if (Objects.nonNull(melodyPlayer)) {
                    melodyPlayer.stop();
                }
                if (Objects.nonNull(melodyTimer)) {
                    melodyTimer.stop();
                }
                if (Objects.nonNull(dispTimer)) {
                    dispTimer.stop();
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });
    }

    /**
     * 進捗モニタ設定を取得して、表示を初期化する。
     */
    private void readSetting() {
        try {
            LocalTime nowTime = LocalTime.now();// 現在の時刻

            // モニタIDを取得する。
            this.monitorId = AndonLoginFacade.getMonitorId();

            if (0 != this.monitorId) {
                // 進捗モニタ設定(Fuji)を取得する。
                AndonMonitorSettingFacade monitorSettingFacade = new AndonMonitorSettingFacade();
                this.setting = (MonitorSettingFuji) monitorSettingFacade.getLineSetting(this.monitorId, MonitorSettingFuji.class);

                // サイクルメロディ (サイクルタクトタイムフレーム用)
                String melodyWorkStart30sec = "";
                String melodyCycleStart = "";
                String melodyCycle60sec = "";
                String melodyCycle30sec = "";
                String melodyCycle5sec = "";
                String melodyLunchStart = "";
                String melodyLunch30sec = "";
                String melodyRefreshStart = "";
                String melodyRefresh30sec = "";
                String melodyWorkEnd = "";
                for (CycleMelodyInfoEntity cycleMelody : this.setting.getCycleMelodyCollection()) {
                    switch (cycleMelody.getMelodyType()) {
                        case WORK_START_30SEC:
                            melodyWorkStart30sec = cycleMelody.getMelodyPath();
                            break;
                        case CYCLE_START:
                            melodyCycleStart = cycleMelody.getMelodyPath();
                            break;
                        case CYCLE_60SEC:
                            melodyCycle60sec = cycleMelody.getMelodyPath();
                            break;
                        case CYCLE_30SEC:
                            melodyCycle30sec = cycleMelody.getMelodyPath();
                            break;
                        case CYCLE_5SEC:
                            melodyCycle5sec = cycleMelody.getMelodyPath();
                            break;
                        case LUNCH_TIME_START:
                            melodyLunchStart = cycleMelody.getMelodyPath();
                            break;
                        case LUNCH_TIME_30SEC:
                            melodyLunch30sec = cycleMelody.getMelodyPath();
                            break;
                        case REFRESH_TIME_START:
                            melodyRefreshStart = cycleMelody.getMelodyPath();
                            break;
                        case REFRESH_TIME_30SEC:
                            melodyRefresh30sec = cycleMelody.getMelodyPath();
                            break;
                        case WORK_END:
                            melodyWorkEnd = cycleMelody.getMelodyPath();
                            break;
                    }
                }

                cycleMelodyList.clear();

                // 作業開始時刻 30秒前のメロディ
                LocalTime workStart30sec = this.setting.getStartWorkTime().minusSeconds(30L);// 作業開始 30秒前
                if (!melodyWorkStart30sec.isEmpty()) {
                    if (workStart30sec.isAfter(LocalTime.MIN)) {
                        this.cycleMelodyList.add(new CycleMelodyPlayInfo(workStart30sec, melodyWorkStart30sec));
                    }
                }

                // 休憩時間
                //      ※休憩終了時刻が作業開始時刻より後の休憩のみ
                List<BreakTimeInfoEntity> breaktimes = this.setting.getBreaktimes()
                        .stream().filter(p -> DateUtils.toLocalDateTime(p.getEndtime()).toLocalTime().isAfter(this.setting.getStartWorkTime()))
                        .collect(Collectors.toList());

                // 休憩時間の途中で作業開始時刻になる場合、作業開始時刻より前の休憩は無視する。(作業開始時刻から休憩開始にする)
                List<BreakTimeInfoEntity> bts = breaktimes
                        .stream().filter(p -> DateUtils.toLocalDateTime(p.getStarttime()).toLocalTime().isBefore(this.setting.getStartWorkTime())
                                && DateUtils.toLocalDateTime(p.getEndtime()).toLocalTime().isAfter(this.setting.getStartWorkTime()))
                        .collect(Collectors.toList());
                for (BreakTimeInfoEntity bt : bts) {
                    bt.setStarttime(DateUtils.toDate(DateUtils.toLocalDate(DateUtils.min()), this.setting.getStartWorkTime()));
                }

                // 休憩時間を開始時間順にソートする。
                breaktimes.sort(Comparator.comparing(p -> p.getStarttime()));

                // 休憩時間のメロディ
                for (BreakTimeInfoEntity breaktime : breaktimes) {
                    LocalTime breaktimeStart = DateUtils.toLocalDateTime(breaktime.getStarttime()).toLocalTime();
                    LocalTime breaktime30sec = DateUtils.toLocalDateTime(breaktime.getEndtime()).toLocalTime().minusSeconds(30L);

                    // 休憩 開始
                    String breaktimeMelody;
                    if (breaktime.getBreaktimeName().startsWith(LUNCH_TIME_NAME)) {
                        breaktimeMelody = melodyLunchStart;
                    } else {
                        breaktimeMelody = melodyRefreshStart;
                    }

                    if (!breaktimeMelody.isEmpty()) {
                        this.cycleMelodyList.add(new CycleMelodyPlayInfo(breaktimeStart, breaktimeMelody));
                    }

                    // 休憩 終了30秒前
                    String breaktime30secMelody;
                    if (breaktime30sec.isAfter(breaktimeStart)) {
                        if (breaktime.getBreaktimeName().startsWith(LUNCH_TIME_NAME)) {
                            breaktime30secMelody = melodyLunch30sec;
                        } else {
                            breaktime30secMelody = melodyRefresh30sec;
                        }

                        if (!breaktime30secMelody.isEmpty()) {
                            this.cycleMelodyList.add(new CycleMelodyPlayInfo(breaktime30sec, breaktime30secMelody));
                        }
                    }
                }

                // サイクルタクトタイム (サイクルタクトタイムフレーム用)
                List<CycleTaktInfoEntity> cycleTakts = this.setting.getCycleTaktCollection();
                cycleTakts.sort(Comparator.comparing(p -> p.getCycleNo()));

                Platform.runLater(() -> {
                    if (Objects.nonNull(cycleTakts) && !cycleTakts.isEmpty()) {
                        this.planNumLabel.setText(String.valueOf(cycleTakts.size()));// 計画台数
                    } else {
                        this.planNumLabel.setText("--");// 計画台数
                    }
                });

                this.cycleTaktList.clear();
                int productionNum = 0;
                LocalTime nextStartTime = LocalTime.from(this.setting.getStartWorkTime());// 作業開始時刻

                // 作業開始前
                if (!LocalTime.MIN.equals(this.setting.getStartWorkTime())) {
                    List<CycleTaktDispInfo> infos = new ArrayList<>();

                    // 0時 - 作業開始30秒前
                    infos.add(this.createOutOfWorkDispInfo(LocalTime.MIN, workStart30sec, 0L, 0));

                    // 作業開始30秒前 - 作業開始
                    infos.add(this.createOutOfWorkDispInfo(workStart30sec, nextStartTime, 30L, 0));

                    this.cycleTaktList.addAll(infos);
                }

                // 作業
                for (CycleTaktInfoEntity cycleTakt : cycleTakts) {
                    productionNum++;

                    // タクトタイムが「00:00:00」の場合は生産数のカウントだけ進めて表示情報は設定しない。
                    if (Objects.isNull(cycleTakt.getTaktTime()) || cycleTakt.getTaktTime().equals(DateUtils.min())) {
                        continue;
                    }

                    List<CycleTaktDispInfo> infos = new ArrayList<>();

                    LocalTime taktDt = DateUtils.toLocalDateTime(cycleTakt.getTaktTime()).toLocalTime();

                    int taktMin = (taktDt.getHour() * 60) + taktDt.getMinute();
                    int taktSec = taktDt.getSecond();
                    long taktTime = (taktMin * 60) + taktSec;

                    LocalTime endTime = nextStartTime.plusSeconds(taktTime);// タクトの終了時間

                    CycleTaktDispInfo info = new CycleTaktDispInfo();
                    info.setCycleStartTime(LocalTime.from(nextStartTime));
                    info.setCycleEndTime(LocalTime.from(endTime));
                    info.setTaktTime(taktTime);
                    info.setRemainingTime(taktTime);

                    info.setProductionNum(String.valueOf(productionNum));
                    info.setTaktTimeMin(String.format("%02d", taktMin));
                    info.setTaktTimeSec(String.format("%02d", taktSec));
                    info.setCycleType(CycleTaktDispTypeEnum.TaktTime);

                    infos.add(info);

                    for (BreakTimeInfoEntity breaktime : breaktimes) {
                        LocalTime breaktimeStart = DateUtils.toLocalDateTime(breaktime.getStarttime()).toLocalTime();
                        LocalTime breaktimeEnd = DateUtils.toLocalDateTime(breaktime.getEndtime()).toLocalTime();

                        for (int i = 0; i < infos.size(); i++) {
                            CycleTaktDispInfo targetInfo = infos.get(i);
                            CycleTaktDispInfo afterInfo = null;

                            if (!targetInfo.getCycleType().equals(CycleTaktDispTypeEnum.TaktTime)) {
                                continue;
                            }

                            if (breaktimeEnd.isBefore(targetInfo.getCycleStartTime())
                                    || breaktimeStart.equals(targetInfo.getCycleEndTime()) || breaktimeStart.isAfter(targetInfo.getCycleEndTime())) {
                                continue;
                            }

                            long offset;
                            if (breaktimeStart.equals(targetInfo.getCycleStartTime())) {
                                // 作業を休憩時間分ずらす。
                                offset = breaktimeStart.until(breaktimeEnd, ChronoUnit.SECONDS);

                                targetInfo.setCycleStartTime(targetInfo.getCycleStartTime().plusSeconds(offset));
                                targetInfo.setCycleEndTime(targetInfo.getCycleEndTime().plusSeconds(offset));
                            } else {
                                // 作業を休憩時間の前後に分割する。
                                long elapsedTime = targetInfo.getCycleStartTime().until(breaktimeStart, ChronoUnit.SECONDS);// 作業開始から休憩開始までの経過時間
                                long remainingTime = targetInfo.getRemainingTime() - elapsedTime;// 休憩後の作業の残り時間
                                offset = remainingTime;

                                afterInfo = new CycleTaktDispInfo();
                                afterInfo.setCycleStartTime(LocalTime.from(breaktimeEnd));
                                afterInfo.setCycleEndTime(breaktimeEnd.plusSeconds(remainingTime));
                                afterInfo.setTaktTime(targetInfo.getTaktTime());
                                afterInfo.setRemainingTime(remainingTime);

                                afterInfo.setProductionNum(targetInfo.getProductionNum());
                                afterInfo.setTaktTimeMin(targetInfo.getTaktTimeMin());
                                afterInfo.setTaktTimeSec(targetInfo.getTaktTimeSec());
                                afterInfo.setCycleType(CycleTaktDispTypeEnum.TaktTime);

                                targetInfo.setCycleEndTime(LocalTime.from(breaktimeStart));
                            }

                            // 後の作業を休憩時間分ずらす。
                            for (int j = i + 1; j < infos.size(); j++) {
                                CycleTaktDispInfo otherInfo = infos.get(j);
                                otherInfo.setCycleStartTime(otherInfo.getCycleStartTime().plusSeconds(offset));
                                otherInfo.setCycleEndTime(otherInfo.getCycleEndTime().plusSeconds(offset));
                            }

                            // 休憩時間
                            CycleTaktDispInfo breaktimeInfo = createBreaktimeDispInfo(breaktimeStart, breaktimeEnd, breaktime.getBreaktimeName(), targetInfo);

                            if (Objects.nonNull(afterInfo)) {
                                i++;
                                infos.add(i, breaktimeInfo);
                                i++;
                                infos.add(i, afterInfo);
                            } else {
                                infos.add(i, breaktimeInfo);
                                i++;
                            }
                            break;
                        }
                    }

                    nextStartTime = LocalTime.from(infos.get(infos.size() - 1).getCycleEndTime());

                    this.cycleTaktList.addAll(infos);

                    // 作業開始のメロディ
                    if (!melodyCycleStart.isEmpty()) {
                        this.cycleMelodyList.add(new CycleMelodyPlayInfo(LocalTime.from(info.getCycleStartTime()), melodyCycleStart));
                    }

                    boolean isSet60sec = melodyCycle60sec.isEmpty();
                    boolean isSet30sec = melodyCycle30sec.isEmpty();
                    boolean isSet5sec = melodyCycle5sec.isEmpty();
                    for (int j = infos.size() - 1; j >= 0; j--) {
                        CycleTaktDispInfo dispInfo = infos.get(j);
                        if (!CycleTaktDispTypeEnum.TaktTime.equals(dispInfo.getCycleType())) {
                            continue;
                        }

                        LocalTime cycleEndTime = dispInfo.getCycleStartTime().plusSeconds(dispInfo.getRemainingTime());

                        // 作業終了1分前
                        if (!isSet60sec && dispInfo.getRemainingTime() > 60) {
                            this.cycleMelodyList.add(new CycleMelodyPlayInfo(cycleEndTime.minusSeconds(60), melodyCycle60sec));
                            isSet60sec = true;
                        }

                        // 作業終了30秒前
                        if (!isSet30sec && dispInfo.getRemainingTime() > 30) {
                            this.cycleMelodyList.add(new CycleMelodyPlayInfo(cycleEndTime.minusSeconds(30), melodyCycle30sec));
                            isSet30sec = true;
                        }

                        // 作業終了5秒前
                        if (!isSet5sec && dispInfo.getRemainingTime() > 5) {
                            this.cycleMelodyList.add(new CycleMelodyPlayInfo(cycleEndTime.minusSeconds(5), melodyCycle5sec));
                            isSet5sec = true;
                        }

                        if (isSet60sec && isSet30sec && isSet5sec) {
                            break;
                        }
                    }
                }

                // 作業終了後
                if (!LocalTime.MAX.equals(nextStartTime)) {
                    List<CycleTaktDispInfo> infos = new ArrayList<>();

                    infos.add(this.createOutOfWorkDispInfo(nextStartTime, LocalTime.MAX, 0L, productionNum));

                    for (BreakTimeInfoEntity breaktime : breaktimes) {
                        LocalTime breaktimeStart = DateUtils.toLocalDateTime(breaktime.getStarttime()).toLocalTime();
                        LocalTime breaktimeEnd = DateUtils.toLocalDateTime(breaktime.getEndtime()).toLocalTime();

                        for (int i = 0; i < infos.size(); i++) {
                            CycleTaktDispInfo targetInfo = infos.get(i);
                            CycleTaktDispInfo afterInfo = null;

                            if (!targetInfo.getCycleType().equals(CycleTaktDispTypeEnum.TaktTime)) {
                                continue;
                            }

                            if (breaktimeEnd.isBefore(targetInfo.getCycleStartTime())
                                    || breaktimeStart.equals(targetInfo.getCycleEndTime()) || breaktimeStart.isAfter(targetInfo.getCycleEndTime())) {
                                continue;
                            }

                            if (breaktimeStart.equals(targetInfo.getCycleStartTime())) {
                                if (breaktimeEnd.isAfter(targetInfo.getCycleEndTime())) {
                                    // 作業開始を休憩時間終了時間に変更する。
                                    targetInfo.setCycleStartTime(breaktimeEnd);
                                } else {
                                    // 作業を削除する。
                                    infos.remove(i);
                                    i--;
                                }
                            } else {
                                // 作業を休憩時間の前後に分割する。(時間はずらさない)
                                afterInfo = createOutOfWorkDispInfo(breaktimeEnd, targetInfo.getCycleEndTime(), 0L, productionNum);

                                targetInfo.setCycleEndTime(LocalTime.from(breaktimeStart));
                            }

                            // 休憩時間
                            CycleTaktDispInfo breaktimeInfo = createBreaktimeDispInfo(breaktimeStart, breaktimeEnd, breaktime.getBreaktimeName(), targetInfo);

                            if (Objects.nonNull(afterInfo)) {
                                i++;
                                infos.add(i, breaktimeInfo);
                                i++;
                                infos.add(i, afterInfo);
                            } else {
                                infos.add(i, breaktimeInfo);
                                i++;
                            }
                            break;
                        }
                    }

                    this.cycleTaktList.addAll(infos);

                    // 最後のサイクルの終了.
                    if (!melodyWorkEnd.isEmpty())
                    {
                        this.cycleMelodyList.add(new CycleMelodyPlayInfo(nextStartTime, melodyWorkEnd));
                    }
                }
                
                this.cycleTaktList.sort(Comparator.comparing(p -> p.getCycleStartTime()));
                this.cycleMelodyList.sort(Comparator.comparing(p -> p.getPlayTime()));

                // 表示更新タスクで、次の表示対象とするサイクルタクト
                this.setNextTaktId(nowTime);

                // メロディ鳴動タスクで、次に鳴らすメロディ
                this.setNextMelodyId(nowTime);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 作業時間外の表示情報を作成する。
     *
     * @param startTime 開始時刻
     * @param endTime 終了時刻
     * @param remainingTime 残り時間 (sec)
     * @param productionNum 生産台数
     * @return 
     */
    private CycleTaktDispInfo createOutOfWorkDispInfo(LocalTime startTime, LocalTime endTime, Long remainingTime, int productionNum) {
        CycleTaktDispInfo info = new CycleTaktDispInfo();
        info.setCycleStartTime(LocalTime.from(startTime));
        info.setCycleEndTime(LocalTime.from(endTime));
        info.setTaktTime(0L);
        info.setRemainingTime(remainingTime);
        info.setProductionNum(String.valueOf(productionNum));
        info.setTaktTimeMin("--");
        info.setTaktTimeSec("--");
        info.setCycleType(CycleTaktDispTypeEnum.TaktTime);
        return info;
    }

    /**
     * 休憩時間の表示情報を作成する。
     *
     * @param breaktimeStart 休憩開始時刻
     * @param breaktimeEnd 休憩終了時刻
     * @param breaktimeName 休憩名
     * @param targetInfo 
     * @return 
     */
    private CycleTaktDispInfo createBreaktimeDispInfo(LocalTime breaktimeStart, LocalTime breaktimeEnd, String breaktimeName, CycleTaktDispInfo targetInfo) {
        long breakTimeSec = breaktimeStart.until(breaktimeEnd, ChronoUnit.SECONDS);
        CycleTaktDispInfo info = new CycleTaktDispInfo();
        info.setCycleStartTime(LocalTime.from(breaktimeStart));
        info.setCycleEndTime(LocalTime.from(breaktimeEnd));
        info.setTaktTime(targetInfo.getTaktTime());
        info.setRemainingTime(breakTimeSec);
        info.setProductionNum(targetInfo.getProductionNum());
        info.setTaktTimeMin(targetInfo.getTaktTimeMin());
        info.setTaktTimeSec(targetInfo.getTaktTimeSec());
        if (breaktimeName.startsWith(LUNCH_TIME_NAME)) {
            info.setCycleType(CycleTaktDispTypeEnum.LunchTime);
        } else {
            info.setCycleType(CycleTaktDispTypeEnum.RefreshTime);
        }
        return info;
    }

    /**
     * 表示更新タスクで、次の表示対象とするサイクルタクトを設定する。
     *
     * @param nowTime 現在時刻
     */
    private void setNextTaktId(LocalTime nowTime) {
        try {
            // 表示更新タスクで、次の表示対象とするサイクルタクト
            Optional<CycleTaktDispInfo> optTakt = this.cycleTaktList.stream().filter(p -> nowTime.equals(p.getCycleStartTime()) || nowTime.isAfter(p.getCycleStartTime()))
                    .max(Comparator.comparing(p -> p.getCycleStartTime()));
            if (optTakt.isPresent()) {
                this.nextTaktId = this.cycleTaktList.indexOf(optTakt.get());
            } else {
                this.nextTaktId = this.cycleTaktList.size();
            }

//            logger.info("*** cycleTaktList");
//            for (int i = 0; i < this.cycleTaktList.size(); i++) {
//                CycleTaktDispInfo info = this.cycleTaktList.get(i);
//                logger.info("***** index={}, cycleStartTime={}, cycleEndTime={}, taktTime={}, remainingTime={}, productionNum={}, taktTimeMin={}, taktTimeSec={}, cycleType={}",
//                        i, info.getCycleStartTime(), info.getCycleEndTime(), info.getTaktTime(), info.getRemainingTime(),
//                        info.getProductionNum(), info.getTaktTimeMin(), info.getTaktTimeSec(), info.getCycleType());
//            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * メロディ鳴動タスクで、次に鳴らすメロディを設定する。
     *
     * @param nowTime 現在時刻
     */
    private void setNextMelodyId(LocalTime nowTime) {
        try {
            Optional<CycleMelodyPlayInfo> optMelody = this.cycleMelodyList.stream().filter(p -> nowTime.isBefore(p.getPlayTime()))
                    .min(Comparator.comparing(p -> p.getPlayTime()));
            if (optMelody.isPresent()) {
                this.nextMelodyId = this.cycleMelodyList.indexOf(optMelody.get());
            } else {
                this.nextMelodyId = this.cycleMelodyList.size();
            }

//            logger.info("*** cycleMelodyList");
//            for (int i = 0; i < this.cycleMelodyList.size(); i++) {
//                CycleMelodyPlayInfo info = this.cycleMelodyList.get(i);
//                logger.info("***** index={}, playTime={}, playFile={}", i, info.getPlayTime(), info.getPlayFile());
//            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 進捗情報読み込み(サーバーから)
     * 
     * @param monitorId 自身の設備ID
     */
    private void readTask(Long monitorId) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    synchronized (lock) {
                        drawLineout();
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
                draw();
                playMelody();
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * anchorPaneのサイズにあわせて、mainPaneの表示倍率を更新する。
     */
    private void changeSizeTask() {
        Platform.runLater(() -> {
            synchronized (lock) {
                // アスペクト比を維持したままmainPaneをスケーリングするための倍率
                double scale;
                double scaleX = anchorPane.getWidth() / mainPane.getWidth();
                double scaleY = anchorPane.getHeight() / mainPane.getHeight();
                if (scaleX < scaleY) {
                    scale = scaleX;
                } else {
                    scale = scaleY;
                }

                // スケーリング後のmainPaneのイメージサイズ
                double wid = (mainPane.getWidth() * scale);
                double hei = (mainPane.getHeight() * scale);

                // スケーリングされたmainPaneのイメージが、anchorpaneの中央に表示されるオフセット
                double offsetX = ((anchorPane.getWidth() - wid) / 2.0) - ((mainPane.getWidth() - wid) / 2.0);
                double offsetY = ((anchorPane.getHeight() - hei) / 2.0) - ((mainPane.getHeight() - hei) / 2.0);

                mainPane.setScaleX(scale);
                mainPane.setScaleY(scale);
                mainPane.setLayoutX(offsetX);
                mainPane.setLayoutY(offsetY);
             }
        });
    }

    /**
     * 表示を更新する。
     */
    private void draw() {
        Platform.runLater(() -> {
            dispTimer.pause();
            synchronized (lock) {
                LocalDateTime nowDateTime = LocalDateTime.now();

                this.currentDateLabel.setText(datetimeFormatter.format(nowDateTime));// 現在の日付

                if (Objects.nonNull(this.dispDateTime)) {
                    if (this.dispDateTime.isAfter(nowDateTime)) {
                        // 日時が戻ったら次の表示対象とするサイクルタクトのインデックスを再設定する。
                        this.setNextTaktId(nowDateTime.toLocalTime());
                    } else if (!this.dispDateTime.toLocalDate().equals(nowDateTime.toLocalDate())) {
                        // 日付が変わったら表示リストのインデックスを先頭に戻す。
                        this.nextTaktId = 0;
                    }
                }
                this.dispDateTime = nowDateTime;

                LocalTime nowTime = nowDateTime.toLocalTime();
                if (Objects.isNull(this.cycleTaktNow)
                        || (Objects.nonNull(this.cycleTaktNow) && this.nextTaktId < this.cycleTaktList.size())) {

                    CycleTaktDispInfo dispInfo = null;
                    for (int i = this.nextTaktId; i < this.cycleTaktList.size(); i++) {
                        CycleTaktDispInfo info = this.cycleTaktList.get(i);
                        if (nowTime.isBefore(info.getCycleStartTime())) {
                            break;
                        }
                        dispInfo = info;
                        this.nextTaktId++;
                    }

                    if (Objects.nonNull(dispInfo)) {
                        // サイクル更新
                        this.productionNumLabel.setText(dispInfo.getProductionNum());// 生産台数

                        this.taktMinLabel.setText(dispInfo.getTaktTimeMin());// タクト(mm)
                        this.taktSecLabel.setText(dispInfo.getTaktTimeSec());// タクト(ss)

                        if (Objects.nonNull(dispInfo)) {
                            this.countdownBackgroundPane.getStyleClass().clear();
                            switch (dispInfo.getCycleType()) {
                                case TaktTime:
                                    this.countdownBackgroundPane.getStyleClass().add("CycleTakt-background-lightGray");
                                    this.countdownPane.setVisible(true);
                                    this.breaktimePane.setVisible(false);
                                    this.refreshTimePane.setVisible(false);
                                    this.lunchTimePane.setVisible(false);
                                    break;
                                case RefreshTime:
                                    this.countdownBackgroundPane.getStyleClass().add("CycleTakt-background-refreshTime");
                                    this.countdownPane.setVisible(false);
                                    this.breaktimePane.setVisible(true);
                                    this.refreshTimePane.setVisible(true);
                                    this.lunchTimePane.setVisible(false);
                                    break;
                                case LunchTime:
                                    this.countdownBackgroundPane.getStyleClass().add("CycleTakt-background-lunchTime");
                                    this.countdownPane.setVisible(false);
                                    this.breaktimePane.setVisible(true);
                                    this.refreshTimePane.setVisible(false);
                                    this.lunchTimePane.setVisible(true);
                                    break;
                            }
                        }

                        this.cycleTaktNow = dispInfo;
                    }

                    String countdownMin;
                    String countdownSec;
                    if (this.cycleTaktNow.getRemainingTime() > 0) {
                        long elapsedTime = this.cycleTaktNow.getCycleStartTime().until(nowTime, ChronoUnit.SECONDS);// 開始からの経過時間
                        long countdown = this.cycleTaktNow.getRemainingTime() - elapsedTime;

                        countdownMin = String.format("%02d", countdown / 60);
                        countdownSec = String.format("%02d", countdown % 60);
                    } else {
                        countdownMin = "--";
                        countdownSec = "--";
                    }

                    if (CycleTaktDispTypeEnum.TaktTime.equals(this.cycleTaktNow.getCycleType())) {
                        this.countdownMinLabel.setText(countdownMin);
                        this.countdownSecLabel.setText(countdownSec);
                    } else {
                        this.breaktimeMinLabel.setText(countdownMin);
                        this.breaktimeSecLabel.setText(countdownSec);
                    }
                } else {
                    this.taktMinLabel.setText("--");// タクト(mm)
                    this.taktSecLabel.setText("--");// タクト(ss)

                    this.countdownPane.setVisible(true);
                    this.countdownMinLabel.setText("--");
                    this.countdownSecLabel.setText("--");

                    this.breaktimePane.setVisible(false);
                    this.refreshTimePane.setVisible(false);
                    this.lunchTimePane.setVisible(false);
                    this.breaktimeMinLabel.setText("--");
                    this.breaktimeSecLabel.setText("--");
                }

                dispTimer.play();
             }
        });
    }

    /**
     * ラインアウト数を取得して表示を更新する。
     */
    private void drawLineout() {
        List<MonitorReasonNumInfoEntity> reasonNumInfos = monitorFacade.getDailyInterruptReasonInfo(monitorId);

        Platform.runLater(() -> {
            if (Objects.nonNull(reasonNumInfos)) {
                int lineoutNum = 0;
                for (MonitorReasonNumInfoEntity info : reasonNumInfos) {
                    lineoutNum += info.getReasonNum();
                }
                this.lineoutNumLabel.setText(String.valueOf(lineoutNum));
            } else {
                this.lineoutNumLabel.setText("-");
            }
        });
    }

    /**
     * メロディファイルを再生する。
     */
    private void playMelody() {
        Platform.runLater(() -> {
            melodyTimer.pause();
            try {
                LocalDateTime nowDateTime = LocalDateTime.now();

                if (Objects.nonNull(this.melodyDateTime)) {
                    if (this.melodyDateTime.isAfter(nowDateTime)) {
                        // 日時が戻ったら次に鳴らすメロディのインデックスを再設定する。
                        this.setNextMelodyId(nowDateTime.toLocalTime());
                    } else if (!this.melodyDateTime.toLocalDate().equals(nowDateTime.toLocalDate())) {
                        // 日付が変わったらメロディリストのインデックスを先頭に戻す。
                        this.nextMelodyId = 0;
                    }
                }
                this.melodyDateTime = nowDateTime;

                LocalTime nowTime = nowDateTime.toLocalTime();
                if (this.nextMelodyId < this.cycleMelodyList.size()) {
                    CycleMelodyPlayInfo playInfo = null;
                    for (int i = this.nextMelodyId; i < this.cycleMelodyList.size(); i++) {
                        CycleMelodyPlayInfo info = this.cycleMelodyList.get(i);
                        if (nowTime.isBefore(info.getPlayTime())) {
                            break;
                        }

                        if (nowTime.isBefore(info.getPlayTime().plusSeconds(1))) {
                            playInfo = info;
                        }
                        this.nextMelodyId++;
                    }

                    if (Objects.nonNull(playInfo)) {
                        melodyPlayer.play(playInfo.getPlayFile(), false);
                    }
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            } finally {
                melodyTimer.play();
            }
        });
    }
}
