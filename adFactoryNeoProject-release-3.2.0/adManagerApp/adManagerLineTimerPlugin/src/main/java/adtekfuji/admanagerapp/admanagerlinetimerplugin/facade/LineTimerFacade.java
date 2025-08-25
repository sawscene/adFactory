/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.admanagerlinetimerplugin.facade;

import adtekfuji.admanagerapp.admanagerlinetimerplugin.service.AdInterfaceClientService;
import adtekfuji.admanagerapp.admanagerlinetimerplugin.service.NoticeCommandListner;
import adtekfuji.utility.DateUtils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import jp.adtekfuji.adFactory.adinterface.command.LineTimerNoticeCommand;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ReasonInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LineManagedCommandEnum;
import jp.adtekfuji.adFactory.enumerate.LineManagedStateEnum;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.entity.LineTimerControlRequest;
import jp.adtekfuji.andon.entity.MonitorLineTimerInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class LineTimerFacade implements NoticeCommandListner {

    private final Logger logger = LogManager.getLogger();
    private final AndonMonitorSettingFacade andonMonitorSettingFacade = new AndonMonitorSettingFacade();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private final LineTimerViewInterface view;
    private final List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList<>();

    private Long monitorId;
    private AdInterfaceClientService adInterfaceClientService;
    private MonitorLineTimerInfoEntity lineTimerInfo = null;
    private AndonMonitorLineProductSetting setting;
    private long lastMillis;
    private boolean duringBreaks = false;
    private TimerTask timerTask;

    public LineTimerFacade(LineTimerViewInterface view) {
        this.view = view;
    }

    /**
     * ラインタイマーがスタート状態か判別する
     *
     * @return true:開始中(中断可能)　false:非開始中(再開可能)
     */
    public boolean isStarting() {
        return Objects.nonNull(this.lineTimerInfo)
                ? this.lineTimerInfo.getLineTimerState() == LineManagedStateEnum.STARTCOUNT || this.lineTimerInfo.getLineTimerState() == LineManagedStateEnum.TAKTCOUNT
                : false;
    }

    /**
     * コマンド受信
     *
     * @param command
     */
    @Override
    public synchronized void notice(Object command) {
        logger.info("notice: {}", command);
        if (command instanceof LineTimerNoticeCommand) {
            LineTimerNoticeCommand notice = (LineTimerNoticeCommand) command;
            if (notice.getMonitorId().equals(this.monitorId)) {
                switch (notice.getCommand()) {
                    case START:
                    case PAUSE:
                    case RESTART:
                    case STOP:
                    case RESET:
                        this.updateLineTimerInfo();
                        break;
                }
            }
        }
    }

    /**
     *
     *
     * @param monitor
     */
    public void selectMonitor(EquipmentInfoEntity monitor) {
        synchronized (this) {
            try {
                // 進捗モニター設定を取得
                this.monitorId = monitor.getEquipmentId();
                this.setting = (AndonMonitorLineProductSetting) this.andonMonitorSettingFacade
                        .getLineSetting(this.monitorId, AndonMonitorLineProductSetting.class);
                //this.lineId = this.setting.getLineId();
                this.view.updateInfo(monitor, setting);

                logger.info("Selected monitor: {}, {}", monitor.getEquipmentName(), this.setting.getTitle());

                this.duringBreaks = false;

                // 休憩時間、終業時間を更新
                this.breaktimeCollection.clear();
                if (Objects.nonNull(setting.getBreaktimes())) {
                    this.breaktimeCollection.addAll(setting.getBreaktimes());
                }

                // 作業時間外は強制的に一時停止
                // LocalDate now = LocalDate.now();
                //if (Objects.nonNull(this.setting.getStartWorkTime()) && Objects.nonNull(this.setting.getEndWorkTime())) {
                //    this.breaktimeCollection.add(new BreakTimeInfoEntity(null, DateUtils.toDate(now, this.setting.getEndWorkTime()), DateUtils.toDate(now.plusDays(1), this.setting.getStartWorkTime())));
                //}

                logger.info("Break time: {}", this.breaktimeCollection);
                this.updateLineTimerInfo();

                if (Objects.nonNull(adInterfaceClientService)) {
                    this.adInterfaceClientService.stopService();
                }

                if (this.setting.isAutoCountdown()) {
                    this.adInterfaceClientService = new AdInterfaceClientService();
                    this.adInterfaceClientService.getHandler().setNoticeListner(this);
                    this.adInterfaceClientService.startService();
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    /**
     *
     */
    private void updateLineTimerInfo() {
        synchronized (this) {
            this.lineTimerInfo = this.andonLineMonitorFacade.getDailyTimerInfo(this.monitorId);
            logger.info("LineTimer: {}", lineTimerInfo);

            switch (this.lineTimerInfo.getLineTimerState()) {
                case START_WAIT:
                    this.view.setStartWait();
                    break;
                case STARTCOUNT:
                    this.view.setStartCount();
                    break;
                case STARTCOUNT_PAUSE:
                    this.view.setStartCountPause();
                    break;
                case TAKTCOUNT:
                    this.view.setTaktCount();
                    break;
                case TAKTCOUNT_PAUSE:
                    this.view.setTaktCountPause();
                    break;
                case STOP:
                    this.view.setStop();
                    if (Objects.nonNull(this.timerTask)) {
                        this.timerTask.cancel();
                    }
                    break;
            }
        }
    }

    /**
     *
     *
     * @param property
     */
    public void onStartAction(LineTimerProperty property) {
        synchronized (this) {
            LineTimerControlRequest request = new LineTimerControlRequest(this.monitorId, null, new Date(), property.getStartCountTime(), property.getTaktTime());

            switch (lineTimerInfo.getLineTimerState()) {
                case START_WAIT:
                    request.setCommand(LineManagedCommandEnum.START);
                    break;
                case STOP:
                    request.setCommand(LineManagedCommandEnum.RESET);
                    break;
                default:
                    request.setCommand(LineManagedCommandEnum.STOP);
                    break;
            }

            this.andonLineMonitorFacade.putDailyTimerControlRequest(request);
            this.updateLineTimerInfo();
        }
    }

    /**
     * 中断理由をつけて一時停止する
     *
     * @param property
     * @param reason
     */
    public void onPauseAction(LineTimerProperty property, ReasonInfoEntity reason) {
        synchronized (this) {
            LineTimerControlRequest request = new LineTimerControlRequest(this.monitorId, null, new Date(), property.getStartCountTime(), property.getTaktTime());

            switch (lineTimerInfo.getLineTimerState()) {
                case STARTCOUNT:
                    request.setCommand(LineManagedCommandEnum.PAUSE);
                    request.setMessage(reason.getReason());
                    break;
                case STARTCOUNT_PAUSE:
                    request.setCommand(LineManagedCommandEnum.RESTART);
                    break;
                case TAKTCOUNT:
                    request.setCommand(LineManagedCommandEnum.PAUSE);
                    request.setMessage(reason.getReason());
                    break;
                case TAKTCOUNT_PAUSE:
                    request.setCommand(LineManagedCommandEnum.RESTART);
                    break;
            }

            this.andonLineMonitorFacade.putDailyTimerControlRequest(request);
            this.updateLineTimerInfo();
        }
    }

    /**
     * 自動カウントダウンが有効かどうかを返す。
     *
     * @return
     */
    public boolean isAutoCountdown() {
        return this.setting.isAutoCountdown();
    }

    /**
     * 休憩中かどうかを返す。
     *
     * @param now
     * @return
     */
    public boolean isBreak(Date now) {
        return BreaktimeUtil.isBreaktime(this.breaktimeCollection, now);
    }

    /**
     * カウントダウンの時間を取得する。
     *
     * @param now
     * @param isBreak
     * @return
     */
    public synchronized long getTimeForAuto(long now, boolean isBreak) {
        long sec = 0;

        // 自動カウントダウン
        if (this.lineTimerInfo.getLineTimerState() == LineManagedStateEnum.STARTCOUNT
                || this.lineTimerInfo.getLineTimerState() == LineManagedStateEnum.TAKTCOUNT) {
            long start = this.lineTimerInfo.getStartTime().getTime();

            if (isBreak) {
                this.duringBreaks = true;
                sec = this.lineTimerInfo.getLeftTimeSec() - ((this.lastMillis - start) / 1000);
                if (this.lineTimerInfo.getTaktTime() < sec) {
                    return this.lineTimerInfo.getTaktTime();
                }
                return sec;
            }

            if (this.duringBreaks) {
                logger.info("Break is over.");
                this.duringBreaks = false;
                this.lineTimerInfo = this.andonLineMonitorFacade.getDailyTimerInfo(monitorId);
                logger.info("LineTimer: {}", lineTimerInfo);
                start = this.lineTimerInfo.getStartTime().getTime();
            }

            this.lastMillis = now;

            sec = this.lineTimerInfo.getLeftTimeSec() - ((now - start) / 1000);
            if (this.lineTimerInfo.getLineTimerState() == LineManagedStateEnum.STARTCOUNT && sec < 0) {
                logger.info("Start taktcountdown.");
                LineTimerControlRequest request = new LineTimerControlRequest(this.monitorId, LineManagedCommandEnum.START, new Date(now), this.lineTimerInfo.getLeftTimeSec(), this.lineTimerInfo.getTaktTime());
                this.andonLineMonitorFacade.putDailyTimerControlRequest(request);
                this.updateLineTimerInfo();
                now = System.currentTimeMillis();
                return this.getTimeForAuto(now, isBreak(new Date(now)));
            }
        } else {
            sec = this.lineTimerInfo.getLeftTimeSec();
        }

        return sec;
    }

    /**
     * カウントダウンの時間を取得する。
     *
     * @param now
     * @param isBreak
     * @return
     */
    public synchronized long getTime(LocalDateTime now, boolean isBreak) {
        long sec = 0;

        // 手動カウントダウン
        LocalDateTime start = LocalDateTime.ofInstant(lineTimerInfo.getStartTime().toInstant(), ZoneOffset.systemDefault());

        if (this.lineTimerInfo.getLineTimerState() == LineManagedStateEnum.STARTCOUNT || lineTimerInfo.getLineTimerState() == LineManagedStateEnum.TAKTCOUNT) {
            sec = this.lineTimerInfo.getLeftTimeSec() - Duration.between(start, now).getSeconds();
        } else {
            sec = this.lineTimerInfo.getLeftTimeSec();
        }

        // スタートカウントダウンがゼロになったら、タクトカウントダウンに切り替える
        if (this.lineTimerInfo.getLineTimerState() == LineManagedStateEnum.STARTCOUNT && sec < 0) {
            logger.info("Start taktcountdown.");
            LineTimerControlRequest request = new LineTimerControlRequest(this.monitorId, LineManagedCommandEnum.START, DateUtils.toDate(now), this.lineTimerInfo.getLeftTimeSec(), this.lineTimerInfo.getTaktTime());
            this.andonLineMonitorFacade.putDailyTimerControlRequest(request);
            this.updateLineTimerInfo();
            return this.getTime(now, isBreak);
        }

        // 休憩時間、終業終了時間の場合、カウントダウンを一時停止.
        if ((this.lineTimerInfo.getLineTimerState() == LineManagedStateEnum.STARTCOUNT || this.lineTimerInfo.getLineTimerState() == LineManagedStateEnum.TAKTCOUNT) && isBreak) {

            // カウントダウン一時停止
            logger.info("Countdown pause.");
            LineTimerControlRequest request = new LineTimerControlRequest(this.monitorId, LineManagedCommandEnum.PAUSE, DateUtils.toDate(now), this.lineTimerInfo.getLeftTimeSec(), this.lineTimerInfo.getTaktTime());
            this.andonLineMonitorFacade.putDailyTimerControlRequest(request);
            this.updateLineTimerInfo();

            return this.getTime(now, isBreak);
        }

        if (isBreak && !this.duringBreaks) {
            this.duringBreaks = true;
            // カウントダウン再開用タイマーを作成
            Date restart = BreaktimeUtil.getEndOfBreaktime(breaktimeCollection, DateUtils.toDate(now));
            this.createRestartTimer(restart);
        }

        return sec;
    }

    /**
     * リソースを破棄する。
     */
    public void destroy() {
        try {
            if (Objects.nonNull(adInterfaceClientService)) {
                this.adInterfaceClientService.stopService();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     *
     * @param restart
     */
    private void createRestartTimer(Date restart) {
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!setting.isAutoCountdown()) {
                    logger.info("Countdown restart.");
                    duringBreaks = false;
                    LineTimerControlRequest request = new LineTimerControlRequest(monitorId, LineManagedCommandEnum.RESTART, new Date(), lineTimerInfo.getLeftTimeSec(), lineTimerInfo.getTaktTime());
                    andonLineMonitorFacade.putDailyTimerControlRequest(request);
                }
                updateLineTimerInfo();
            }
        };
        new Timer(true).schedule(this.timerTask, restart);
    }
}
