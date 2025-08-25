package jp.adtekfuji.adandonlinecountdownplugin;

import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
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
import javafx.util.Duration;
import jp.adtekfuji.adFactory.adinterface.command.LineTimerNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LineManagedCommandEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.adandonlinecountdownplugin.entities.CountdownMelodyPlayInfo;
import jp.adtekfuji.adandonlinecountdownplugin.enumerate.BreakStatus;
import jp.adtekfuji.adandonlinecountdownplugin.facade.LineTimerFacade;
import jp.adtekfuji.adandonlinecountdownplugin.facade.LineTimerViewInterface;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.AndonLoginFacade;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.CountdownMelodyInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.utility.MonitorTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AndonComponent(title = "ラインカウントダウンフレーム")
@FxComponent(id = "LineCountDown", fxmlPath = "/fxml/line_countdown_compo.fxml")
public class MonitorCountDownCompoFxController implements Initializable, AdAndonComponentInterface, LineTimerViewInterface {

    private enum MelodyType {
        BEFORE_COUNTDOWN,
        COUNTDOWN_START,
        BEFORE_END_OF_COUNT,
        WORK_DELAYED,
        BREAKTIME_START,
        BEFORE_END_OF_BREAK,
        NONE;
    }

    private enum CountStatus {
        STOP,
        PAUSE,
        PRE_COUNT,
        COUNT,
        SOON_OVER,
        OVER;
    }

    private static final Logger logger = LogManager.getLogger();
    private static final String COLOR_DELAY = "Red";
    private static final String COLOR_NORMAL = "Yellow";

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final AndonMonitorSettingFacade andonMonitorSettingFacade = new AndonMonitorSettingFacade();
    private final LineTimerFacade lineTimerFacade = new LineTimerFacade(this);
    private final MelodyPlayer melodyPlayer = new MelodyPlayer();
    private final Map<MelodyType, CountdownMelodyPlayInfo> melodyList = new LinkedHashMap<>();
    private final Object lock = new Object();
    private final LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>(10);

    private Timeline viewTimer = null;
    private long monitorId;
    private AndonMonitorLineProductSetting setting;
    private MelodyType playingNow = MelodyType.NONE;
    private int ringTimingEndOfCount = 60;
    private int ringTimingEndOfBreak = 60;
    private List<BreakTimeInfoEntity> breaktimes;
    private List<DisplayedStatusInfoEntity> displayStatuses;
    private CountStatus countStatus = CountStatus.STOP;
    private BreakStatus breakStatus = BreakStatus.NOT;
    private long countdownSec = 0L;
    private static boolean active = false;
    private String message;
    private Double fontSize;
    private String fontColor;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label counterLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.displayStatuses = new DisplayedStatusInfoFacade().findAll();

        LocalTime localTime = LocalTime.ofSecondOfDay(0);
        String text = localTime.format(this.timeFormatter);
        this.fontSize = MonitorTools.getFontSize(text, this.anchorPane.getWidth(), this.anchorPane.getHeight(), Double.parseDouble(Constants.DEF_FONT_COUNTDOWN));

        this.updateCounter(0L);

        this.anchorPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.updateCounter(this.countdownSec);
            if (CountStatus.PAUSE == this.countStatus && !StringUtils.isEmpty(this.message)) {
                return;
            }
            this.fontSize = MonitorTools.getFontSize(this.counterLabel.getText(), this.anchorPane.getWidth(), this.anchorPane.getHeight(), Double.parseDouble(Constants.DEF_FONT_COUNTDOWN));
            String style = String.format("-fx-font-size:%dpx; -fx-text-fill: %s;", this.fontSize.longValue(), this.fontColor);
            this.counterLabel.setStyle(style);
            logger.info("Countdown font: " + this.fontSize);
        });

        this.anchorPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.updateCounter(this.countdownSec);
            if (CountStatus.PAUSE == this.countStatus && !StringUtils.isEmpty(this.message)) {
                return;
            }
            this.fontSize = MonitorTools.getFontSize(this.counterLabel.getText(), this.anchorPane.getWidth(), this.anchorPane.getHeight(), Double.parseDouble(Constants.DEF_FONT_COUNTDOWN));
            String style = String.format("-fx-font-size:%dpx; -fx-text-fill: %s;", this.fontSize.longValue(), this.fontColor);
            this.counterLabel.setStyle(style);
            logger.info("Countdown font: " + this.fontSize);
        });

        // タイマー
        this.viewTimer = new Timeline(new KeyFrame(Duration.millis(1000), (ActionEvent event) -> {
            synchronized (this.lock) {
                try {
                    this.updateCountStatus();
                    this.updateBreakStatus();
                    this.playMelody();
                    this.updateCounter(this.lineTimerFacade.getTime(this.monitorId, this.breakStatus));
                } catch (Exception ex) {
                    logger.fatal(ex);
                }
            }
        }));

        this.viewTimer.setCycleCount(Timeline.INDEFINITE);
        this.viewTimer.stop();

        this.createTask();
        this.queue.add(new ResetCommand());
    }

    @Override
    public void updateDisplay(Object msg) {
        if (msg instanceof LineTimerNoticeCommand) {
            LineTimerNoticeCommand command = (LineTimerNoticeCommand) msg;
            
            if (command.getMonitorId().equals(this.monitorId)) {
                if (!adtekfuji.utility.StringUtils.like(command.getModelName(), this.setting.getModelName())) {
                    return;
                }

                this.queue.add(command);
            }
        } else if (msg instanceof ResetCommand) {
            this.queue.add(msg);
        }
    }

    @Override
    public void exitComponent() {
        logger.info("exitComponent");
        try {
            active = false;

            if (Objects.nonNull(this.melodyPlayer)) {
                this.melodyPlayer.stop();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        try {
            if (Objects.nonNull(this.viewTimer)) {
                this.viewTimer.stop();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * バックグランドスレッドを生成する。
     */
    private void createTask() {
        active = true;

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (active) {
                    try {
                        Object command = queue.poll(100, TimeUnit.MILLISECONDS);
                        if (Objects.nonNull(command)) {
                            update(command);
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * ステータスを更新する。
     *
     * @param command
     */
    private void update(Object command) {
        logger.info("update: " + command);

        LineManagedCommandEnum lineCommand = null;

        if (command instanceof LineTimerNoticeCommand) {
            LineTimerNoticeCommand notice = (LineTimerNoticeCommand) command;
            lineCommand = notice.getCommand();
            this.message = notice.getMessage();
        } else if (command instanceof ResetCommand) {
            this.monitorId = AndonLoginFacade.getMonitorId();
            this.setting = (AndonMonitorLineProductSetting) this.andonMonitorSettingFacade.getLineSetting(this.monitorId, AndonMonitorLineProductSetting.class);
            this.breaktimes = this.setting.getBreaktimes();
            this.initMelody();
        }

        synchronized (this.lock) {
            this.lineTimerFacade.update(this.monitorId, lineCommand);
            this.updateCountStatus();
            this.updateBreakStatus();
            this.playMelody();
            Platform.runLater(() -> this.updateCounter(this.lineTimerFacade.getTime(this.monitorId, this.breakStatus)));
        }
    }

    /**
     * カウンターを更新する。
     *
     * @param time
     */
    private void updateCounter(long time) {

        if (CountStatus.PAUSE == this.countStatus && !StringUtils.isEmpty(this.message)) {
            DisplayedStatusInfoEntity displayedStatus = this.getDisplayStatus(StatusPatternEnum.SUSPEND_NORMAL);
            Double size = MonitorTools.getFontSize(message, this.counterLabel.getWidth(), this.counterLabel.getHeight(), Double.parseDouble(Constants.DEF_FONT_SIZE_3LARGE));
            String style = String.format("-fx-font-size:%dpx; -fx-text-fill:%s; -fx-background-color:%s;", size.longValue(), displayedStatus.getFontColor(), displayedStatus.getBackColor());

            this.counterLabel.setText(this.message);
            this.counterLabel.setStyle(style);
            return;
        }

        this.countdownSec = time;
        LocalTime localTime = LocalTime.ofSecondOfDay(Math.abs(time));

        String befor = this.fontColor;
        String sign = "";
        if (time < 0) {
            sign = "-";
            this.fontColor = COLOR_DELAY;
        } else {
            this.fontColor = COLOR_NORMAL;
        }
        
        String text = sign + localTime.format(this.timeFormatter);
        this.counterLabel.setText(text);

        if (!StringUtils.equals(befor, this.fontColor)) {
            this.fontSize = MonitorTools.getFontSize(text, this.anchorPane.getWidth(), this.anchorPane.getHeight(), Double.parseDouble(Constants.DEF_FONT_COUNTDOWN));
        }

        String style = String.format("-fx-font-size:%dpx; -fx-text-fill: %s;", this.fontSize.longValue(), this.fontColor);
        this.counterLabel.setStyle(style);
    }

    @Override
    public void setStartWait() {
        this.viewTimer.stop();
        this.countStatus = CountStatus.STOP;
        this.breakStatus = BreakStatus.NOT;
    }

    @Override
    public void setStartCount() {
        this.countStatus = CountStatus.PRE_COUNT;
        this.viewTimer.play();
    }

    @Override
    public void setStartCountPause() {
        this.countStatus = CountStatus.PAUSE;
    }

    @Override
    public void setTaktCount() {
        this.countStatus = CountStatus.COUNT;
        this.viewTimer.play();
    }

    @Override
    public void setTaktCountPause() {
        this.countStatus = CountStatus.PAUSE;
    }

    @Override
    public void setStop() {

        this.viewTimer.stop();
        this.melodyPlayer.stop();
        this.countStatus = CountStatus.STOP;
        this.breakStatus = BreakStatus.NOT;
        this.playingNow = MelodyType.NONE;
    }

    /**
     * メロディファイルを再生する。
     */
    private synchronized void playMelody() {
        if (this.melodyList.isEmpty()) {
            return;
        }
    
        MelodyType type = MelodyType.NONE;

        if (this.countStatus.equals(CountStatus.STOP)) {
            return;
        }
        
        switch (this.breakStatus) {
            case BREAK:
                type = MelodyType.BREAKTIME_START;
                break;
            case SOON_OVER:
                type = MelodyType.BEFORE_END_OF_BREAK;
                break;
            default:
                switch (this.countStatus) {
                    case PRE_COUNT:
                        Long currentCount = this.lineTimerFacade.getTime(this.monitorId, this.breakStatus);
                        type = (currentCount > 0) ? MelodyType.BEFORE_COUNTDOWN : MelodyType.COUNTDOWN_START;
                        break;
                    case COUNT:
                        type = MelodyType.COUNTDOWN_START;
                        break;
                    case SOON_OVER:
                        type = MelodyType.BEFORE_END_OF_COUNT;
                        break;
                    case OVER:
                        type = MelodyType.WORK_DELAYED;
                        break;
                    default:
                        break;
                }
                break;
        }

        if (!type.equals(this.playingNow)) {
            logger.info("playMelody: {}, {}", this.playingNow, type);

            this.melodyPlayer.stop();
            if (!type.equals(MelodyType.NONE)) {
                this.melodyPlayer.play(this.melodyList.get(type));
            }
            this.playingNow = type;
        }
    }

    /**
     * メロディ設定の読み込み
     */
    private void initMelody() {

        this.melodyList.clear();

        for (CountdownMelodyInfoEntity countDownMelody : this.setting.getCountdownMelodyInfoCollection()) {
            switch (countDownMelody.getMelodyInfoType()) {
                case PATH_BEFORE_COUNTDOWN:
                    this.melodyList.put(MelodyType.BEFORE_COUNTDOWN, new CountdownMelodyPlayInfo(countDownMelody.getMelodyInfoBody(), true));
                    break;
                case PATH_COUNTDOWN_START:
                    this.melodyList.put(MelodyType.COUNTDOWN_START, new CountdownMelodyPlayInfo(countDownMelody.getMelodyInfoBody(), false));
                    break;
                case PATH_BEFORE_END_OF_COUNTDOWN:
                    this.melodyList.put(MelodyType.BEFORE_END_OF_COUNT, new CountdownMelodyPlayInfo(countDownMelody.getMelodyInfoBody(), true));
                    break;
                case PATH_WORK_DELAYED:
                    this.melodyList.put(MelodyType.WORK_DELAYED, new CountdownMelodyPlayInfo(countDownMelody.getMelodyInfoBody(), true));
                    break;
                case PATH_BREAKTIME_START:
                    this.melodyList.put(MelodyType.BREAKTIME_START, new CountdownMelodyPlayInfo(countDownMelody.getMelodyInfoBody(), false));
                    break;
                case PATH_BEFORE_END_OF_BREAKTIME:
                    this.melodyList.put(MelodyType.BEFORE_END_OF_BREAK, new CountdownMelodyPlayInfo(countDownMelody.getMelodyInfoBody(), true));
                    break;
                case TIME_RING_TIMING_END_OF_COUNTDOWN:
                    this.ringTimingEndOfCount = Integer.valueOf(countDownMelody.getMelodyInfoBody());
                    break;
                case TIME_RING_TIMING_END_OF_BREAKTIME:
                    this.ringTimingEndOfBreak = Integer.valueOf(countDownMelody.getMelodyInfoBody());
                    break;
            }
        }
    }

    /**
     * カウントダウン状態を更新する。
     */
    private void updateCountStatus() {
        switch (this.countStatus) {
            case COUNT:
            case SOON_OVER:
            case OVER:
                Long currentCount = this.lineTimerFacade.getTime(this.monitorId, this.breakStatus);
                if (this.ringTimingEndOfCount < currentCount) {
                    this.countStatus = CountStatus.COUNT;
                } else if (0 <= currentCount) {
                    this.countStatus = CountStatus.SOON_OVER;
                } else {
                    this.countStatus = CountStatus.OVER;
                }
                break;
        }
    }

    /**
     * 休憩状態を更新する。
     */
    private void updateBreakStatus() {
        BreakStatus status = BreakStatus.NOT;
        LocalTime now = LocalTime.now();

        for (BreakTimeInfoEntity breaktime : this.breaktimes) {
            LocalTime start = LocalDateTime.ofInstant(breaktime.getStarttime().toInstant(), ZoneId.systemDefault()).toLocalTime();
            LocalTime end = LocalDateTime.ofInstant(breaktime.getEndtime().toInstant(), ZoneId.systemDefault()).toLocalTime();
            if (now.isAfter(start) && now.isBefore(end)) {
                if (this.ringTimingEndOfBreak <= ChronoUnit.SECONDS.between(now, end)) {
                    status = BreakStatus.BREAK;
                } else {
                    status = BreakStatus.SOON_OVER;
                }
                break;
            }
        }

        if (status.equals(BreakStatus.NOT) && this.breakStatus.equals(BreakStatus.SOON_OVER)) {
            this.breakStatus = BreakStatus.END;
        } else {
            this.breakStatus = status;
        }
    }

    /**
     * ディスプレイステータス情報を取得する。
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
}
