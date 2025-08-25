/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adandonlinecountdownplugin.facade;

import java.util.Objects;
import jp.adtekfuji.adFactory.enumerate.LineManagedCommandEnum;
import jp.adtekfuji.adFactory.enumerate.LineManagedStateEnum;
import jp.adtekfuji.adandonlinecountdownplugin.enumerate.BreakStatus;
import jp.adtekfuji.andon.clientservice.AndonLineMonitorFacade;
import jp.adtekfuji.andon.entity.MonitorLineTimerInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ラインタイマー制御ファサードクラス
 *
 * @author s-heya
 */
public class LineTimerFacade {

    private final Logger logger = LogManager.getLogger();
    private final AndonLineMonitorFacade andonLineMonitorFacade = new AndonLineMonitorFacade();
    private final LineTimerViewInterface view;
    private MonitorLineTimerInfoEntity lineTimerInfo = null;
    private long lastMillis;

    public LineTimerFacade(LineTimerViewInterface view) {
        this.view = view;
    }

    /**
     * タイマーを更新する。
     *
     * @param monitorId
     * @param command
     */
    public synchronized void update(long monitorId, LineManagedCommandEnum command) {
        try {
            this.lineTimerInfo = this.andonLineMonitorFacade.getDailyTimerInfo(monitorId);
            logger.info("Line timer state: " + command + ", " + this.lineTimerInfo.getLineTimerState().name());

            if (Objects.nonNull(command)) {
                switch (command) {
                    case START:
                    case RESTART:
                        switch (this.lineTimerInfo.getLineTimerState()) {
                            case STARTCOUNT:
                                this.view.setStartCount();
                                break;
                            case TAKTCOUNT:
                                this.view.setTaktCount();
                                break;
                        }
                        break;
                    case PAUSE:
                        switch (this.lineTimerInfo.getLineTimerState()) {
                            case STARTCOUNT_PAUSE:
                                this.view.setStartCountPause();
                                break;
                            case TAKTCOUNT_PAUSE:
                                this.view.setTaktCountPause();
                                break;
                        }
                        break;
                    case STOP:
                        this.view.setStop();
                        break;
                    case RESET:
                        this.view.setStartWait();
                        break;
                }
            } else {
                switch (lineTimerInfo.getLineTimerState()) {
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
                        break;
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カウントダウンの時間を取得する。
     *
     * @param monitorId
     * @param status
     * @return
     */
    public long getTime(Long monitorId, BreakStatus status) {
        synchronized (this) {
            long sec = 0;

            if (this.lineTimerInfo.getLineTimerState() == LineManagedStateEnum.STARTCOUNT || this.lineTimerInfo.getLineTimerState() == LineManagedStateEnum.TAKTCOUNT) {
                long start = this.lineTimerInfo.getStartTime().getTime();
                long now = System.currentTimeMillis();

                switch (status) {
                    case BREAK:
                    case SOON_OVER:
                        now = this.lastMillis;
                        sec = this.lineTimerInfo.getLeftTimeSec() - ((now - start) / 1000);
                        if (this.lineTimerInfo.getTaktTime() < sec) {
                            return this.lineTimerInfo.getTaktTime();
                        }
                        return sec;
                    case END:
                        logger.info("Break is over.");
                        this.lineTimerInfo = this.andonLineMonitorFacade.getDailyTimerInfo(monitorId);
                        start = this.lineTimerInfo.getStartTime().getTime();
                        this.lastMillis = now;
                        break;
                    default:
                        this.lastMillis = now;
                        break;
                }

                sec = this.lineTimerInfo.getLeftTimeSec() - ((now - start) / 1000);
                if (this.lineTimerInfo.getLineTimerState() == LineManagedStateEnum.STARTCOUNT && sec < 0) {
                    sec = 0;
                }
            } else {
                sec = this.lineTimerInfo.getLeftTimeSec();
            }

            return sec;
        }
    }
}
