/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.utility;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jp.adtekfuji.adFactory.adinterface.command.LineTimerNoticeCommand;
import jp.adtekfuji.adFactory.enumerate.LineManagedCommandEnum;
import jp.adtekfuji.adFactory.enumerate.LineManagedStateEnum;
import jp.adtekfuji.adfactoryserver.entity.master.BreaktimeEntity;
import jp.adtekfuji.adfactoryserver.model.LineManager;
import jp.adtekfuji.adfactoryserver.service.AdIntefaceClientFacade;
import jp.adtekfuji.andon.entity.LineTimerControlRequest;
import jp.adtekfuji.andon.entity.MonitorLineTaktInfoEntity;
import jp.adtekfuji.andon.entity.MonitorLineTimerInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
@Singleton
public class AggregateLineInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final LineManager lineManager = LineManager.getInstance();

    @EJB
    private AdIntefaceClientFacade adIntefaceClientFacade;

    /**
     * コンストラクタ
     */
    public AggregateLineInfoFacade() {
    }

    /**
     * 指定ラインの日単位のタクトタイム情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @return タクトタイム情報
     */
    @Lock(LockType.READ)
    public MonitorLineTaktInfoEntity getDailyTakttimeInfo(Long monitorId) {
        AndonMonitorLineProductSetting setting = this.lineManager.getLineSetting(monitorId);
        if (Objects.isNull(setting)) {
            return new MonitorLineTaktInfoEntity();
        }
        return new MonitorLineTaktInfoEntity().taktTime((long) setting.getLineTakt().toSecondOfDay());
    }

    /**
     * 指定ラインの日単位のカウントダウン情報を取得する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param now 日時
     * @return カウントダウン情報
     */
    @Lock(LockType.READ)
    public MonitorLineTimerInfoEntity getDailyTimerInfo(Long monitorId, Date now) {
        MonitorLineTimerInfoEntity entity = this.lineManager.getLineTimer(monitorId);
        if (Objects.isNull(entity)) {
            entity = new MonitorLineTimerInfoEntity();
        }

        // スタートカウント時間を超えていたら状態を進めておく。
        if (entity.getLineTimerState() == LineManagedStateEnum.STARTCOUNT && (entity.getStartTime().getTime() / 1000 + entity.getLeftTimeSec()) <= now.getTime() / 1000) {
            entity.setLineTimerState(LineManagedStateEnum.TAKTCOUNT);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(entity.getStartTime());
            calendar.add(Calendar.SECOND, entity.getLeftTimeSec().intValue());
            entity.setStartTime(calendar.getTime());
            entity.setLeftTimeSec(entity.getTaktTime());
        }

        if (entity.getLineTimerState() == LineManagedStateEnum.TAKTCOUNT && Objects.nonNull(monitorId)) {
            AndonMonitorLineProductSetting setting = this.lineManager.getLineSetting(monitorId);

            if (Objects.nonNull(setting) && setting.isAutoCountdown()) {
                List<BreaktimeEntity> breakTimes = BreakTimeUtils.getBreakInWork2(setting.getBreaktimes(), entity.getStartTime(), now);
                Date endTime = BreakTimeUtils.getEndTimeWithBreak(breakTimes, entity.getStartTime(), now);

                long time = endTime.getTime() - now.getTime();
                if (time > 0) {
                    entity = new MonitorLineTimerInfoEntity(entity);
                    entity.setLeftTimeSec(entity.getLeftTimeSec() + (time / 1000));
                }
            }
        }

        return entity;
    }

    /**
     * 指定ラインの日単位のカウントダウンを制御する。
     *
     * @param monitorId 進捗モニタの設備ID
     * @param request リクエスト
     * @param now 日時
     * @param setting 進捗モニタ設定
     */
    //@Lock(LockType.READ)
    public void postDailyTimerControlRequest(Long monitorId, LineTimerControlRequest request, Date now, AndonMonitorLineProductSetting setting) {
        MonitorLineTimerInfoEntity entity = this.lineManager.getLineTimer(monitorId);
        if (Objects.isNull(entity)) {
            entity = new MonitorLineTimerInfoEntity().startTime(request.getStartTime()).leftTime(request.getStartCountTime()).taktTime(request.getTaktTime());
        }

        if (request.getCommand() == LineManagedCommandEnum.SETUP) {
            entity.setCycle(0);
            entity.delivered().clear();
            if (Objects.nonNull(setting)) {
                for (WorkEquipmentSetting workEquipment : setting.getWorkEquipmentCollection()) {
                    if (!workEquipment.getEquipmentIds().isEmpty()) {
                        entity.delivered().put(workEquipment.getEquipmentIds().get(0), 0);
                    }
                }
            }
            this.lineManager.setLineTimer(monitorId, entity);
            return;
        }

        // スタートカウント時間を超えていたら状態を進めておく。
        request.setStartTime(now);
        if (entity.getLineTimerState() == LineManagedStateEnum.STARTCOUNT && (request.getStartTime().getTime() / 1000 + request.getStartCountTime()) <= now.getTime() / 1000) {
            entity.setLineTimerState(LineManagedStateEnum.TAKTCOUNT);
            entity.setStartTime(request.getStartTime());
            entity.setLeftTimeSec(request.getTaktTime());
        }

        // 状態遷移する。
        this.stateTransit(entity, request, now);
        this.lineManager.setLineTimer(monitorId, entity);

        LineTimerNoticeCommand command = new LineTimerNoticeCommand(request.getCommand(), request.getMonitorId(), setting.getLineId(), setting.getModelName());
        if (request.getCommand() == LineManagedCommandEnum.PAUSE) {
            command.setMessage(request.getMessage());
        }

        // ブロードキャスト通知
        this.adIntefaceClientFacade.noticeLineTimer(command);
    }

    /**
     * カウントダウン情報の状態を遷移する。
     *
     * @param entity カウントダウン情報
     * @param request リクエスト
     * @param now 日時
     */
    private void stateTransit(MonitorLineTimerInfoEntity entity, LineTimerControlRequest request, Date now) {
        LineManagedCommandEnum command = request.getCommand();
        LineManagedStateEnum preState = entity.getLineTimerState();

        AndonMonitorLineProductSetting setting = this.lineManager.getLineSetting(request.getMonitorId());
        boolean isAutoCountdown = Objects.nonNull(setting) ? setting.isAutoCountdown() : false;

        switch (entity.getLineTimerState()) {
            case START_WAIT:
                switch (command) {
                    case START:
                        entity.setLineTimerState(LineManagedStateEnum.STARTCOUNT);
                        entity.setStartTime(request.getStartTime());
                        entity.setLeftTimeSec(request.getStartCountTime());
                        entity.setTaktTime(request.getTaktTime());
                        break;
                    case PAUSE:
                        break;
                    case RESTART:
                        break;
                    case STOP:
                        break;
                    case RESET:
                        break;
                    default:
                        break;
                }
                break;
            case STARTCOUNT:
                switch (command) {
                    case START:
                        break;
                    case PAUSE:
                        if (isAutoCountdown) {
                            entity.setLineTimerState(LineManagedStateEnum.STARTCOUNT_PAUSE);
                            List<BreaktimeEntity> breakInWork = BreakTimeUtils.getBreakInWork2(setting.getBreaktimes(), entity.getStartTime(), now);
                            long breakTime = BreakTimeUtils.getBreakTime(breakInWork, entity.getStartTime(), now) / 1000;
                            long leftTime = entity.getLeftTimeSec() - (now.getTime() - entity.getStartTime().getTime()) / 1000;
                            entity.setStartTime(now);
                            entity.setLeftTimeSec(leftTime + breakTime);
                        } else {
                            entity.setLineTimerState(LineManagedStateEnum.STARTCOUNT_PAUSE);
                            entity.setLeftTimeSec(entity.getLeftTimeSec() - (now.getTime() - entity.getStartTime().getTime()) / 1000);
                        }
                        break;
                    case RESTART:
                        break;
                    case STOP:
                        if (isAutoCountdown) {
                            entity.setLineTimerState(LineManagedStateEnum.STOP);
                            List<BreaktimeEntity> breakInWork = BreakTimeUtils.getBreakInWork2(setting.getBreaktimes(), entity.getStartTime(), now);
                            long breakTime = BreakTimeUtils.getBreakTime(breakInWork, entity.getStartTime(), now) / 1000;
                            long leftTime = entity.getLeftTimeSec() - (now.getTime() - entity.getStartTime().getTime()) / 1000;
                            entity.setStartTime(now);
                            entity.setLeftTimeSec(leftTime + breakTime);
                        } else {
                            entity.setLineTimerState(LineManagedStateEnum.STOP);
                            entity.setLeftTimeSec(entity.getLeftTimeSec() - (now.getTime() - entity.getStartTime().getTime()) / 1000);
                        }
                        break;
                    case RESET:
                        break;
                    default:
                        break;
                }
                break;
            case STARTCOUNT_PAUSE:
                switch (command) {
                    case START:
                        break;
                    case PAUSE:
                        break;
                    case RESTART:
                        entity.setLineTimerState(LineManagedStateEnum.STARTCOUNT);
                        entity.setStartTime(request.getStartTime());
                        break;
                    case STOP:
                        entity.setLineTimerState(LineManagedStateEnum.STOP);
                        break;
                    case RESET:
                        break;
                    default:
                        break;
                }
                break;
            case TAKTCOUNT:
                switch (command) {
                    case START:
                        break;
                    case PAUSE:
                        if (isAutoCountdown) {
                            entity.setLineTimerState(LineManagedStateEnum.TAKTCOUNT_PAUSE);
                            List<BreaktimeEntity> breakInWork = BreakTimeUtils.getBreakInWork2(setting.getBreaktimes(), entity.getStartTime(), now);
                            long breakTime = BreakTimeUtils.getBreakTime(breakInWork, entity.getStartTime(), now) / 1000;
                            long leftTime = entity.getLeftTimeSec() - (now.getTime() - entity.getStartTime().getTime()) / 1000;
                            entity.setStartTime(now);
                            entity.setLeftTimeSec(leftTime + breakTime);
                        } else {
                            entity.setLineTimerState(LineManagedStateEnum.TAKTCOUNT_PAUSE);
                            entity.setLeftTimeSec(entity.getLeftTimeSec() - (now.getTime() - entity.getStartTime().getTime()) / 1000);
                        }
                        break;
                    case RESTART:
                        break;
                    case STOP:
                        if (isAutoCountdown) {
                            entity.setLineTimerState(LineManagedStateEnum.STOP);
                            List<BreaktimeEntity> breakInWork = BreakTimeUtils.getBreakInWork2(setting.getBreaktimes(), entity.getStartTime(), now);
                            long breakTime = BreakTimeUtils.getBreakTime(breakInWork, entity.getStartTime(), now) / 1000;
                            long leftTime = entity.getLeftTimeSec() - (now.getTime() - entity.getStartTime().getTime()) / 1000;
                            entity.setStartTime(now);
                            entity.setLeftTimeSec(leftTime + breakTime);
                        } else {
                            entity.setLineTimerState(LineManagedStateEnum.STOP);
                            entity.setLeftTimeSec(entity.getLeftTimeSec() - (now.getTime() - entity.getStartTime().getTime()) / 1000);
                        }
                        break;
                    case RESET:
                        break;
                    default:
                        break;
                }
                break;
            case TAKTCOUNT_PAUSE:
                switch (command) {
                    case START:
                        break;
                    case PAUSE:
                        break;
                    case RESTART:
                        entity.setLineTimerState(LineManagedStateEnum.TAKTCOUNT);
                        entity.setStartTime(request.getStartTime());
                        break;
                    case STOP:
                        entity.setLineTimerState(LineManagedStateEnum.STOP);
                        break;
                    case RESET:
                        break;
                    default:
                        break;
                }
                break;
            case STOP:
                switch (command) {
                    case START:
                        break;
                    case PAUSE:
                        break;
                    case RESTART:
                        break;
                    case STOP:
                        break;
                    case RESET:
                        entity.setLineTimerState(LineManagedStateEnum.START_WAIT);
                        entity.setStartTime(null);
                        entity.setLeftTimeSec(0L);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        logger.info("line count data: line:{}, state:{}->{}, startTime:{}. leftTime:{}", request.getMonitorId(), preState, entity.getLineTimerState(), entity.getStartTime(), entity.getLeftTimeSec());
    }

    public void setAdIntefaceClientFacade(AdIntefaceClientFacade adIntefaceClientFacade) {
        this.adIntefaceClientFacade = adIntefaceClientFacade;
    }
}
