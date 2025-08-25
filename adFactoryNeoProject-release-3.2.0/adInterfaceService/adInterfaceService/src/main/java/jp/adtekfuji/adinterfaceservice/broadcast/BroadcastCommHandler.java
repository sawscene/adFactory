/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.broadcast;

import adtekfuji.property.AdProperty;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import jp.adtekfuji.adFactory.adinterface.command.*;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import jp.adtekfuji.adinterfaceservicecommon.plugin.SocketServerHandlerInterface;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import jp.adtekfuji.andon.property.WorkSetting;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ブロードキャストハンドラ
 *
 * @author ke.yokoi
 */
public class BroadcastCommHandler implements SocketServerHandlerInterface {

    private static final Logger logger = LogManager.getLogger();
    private static final String RESET_HOURS = "resetHours";
    private static final String KEEP_INTERVAL = "keepInterval";

    private final AndonMonitorSettingFacade andonFacede = new AndonMonitorSettingFacade();

    // 万が一重複しても良いように、重複排除のリストを使用する
    private final Map<Long, Channel> terminalChannels = new LinkedHashMap<>();
    private final LinkedHashSet<Channel> monitorChannels = new LinkedHashSet<>();

    // 進捗モニター設定 Map<モニターID, AndonMonitorLineProductSetting>
    private final Map<Long, AndonMonitorLineProductSetting> settingMap = new HashMap<>();

    // 工程進捗情報 Map<モニターID, Map<設備ID(or 工程ID), WorkReportCommand>>
    private final Map<Long, Map<Long, WorkReportCommand>> workReportMap = new HashMap<>();

    // トレース情報 Map<工程カンバンID, TraceCommand>
    private final Map<Long, TraceCommand> traceMap = new HashMap<>();

    // 各作業者端末の作業中の工程 Map<設備ID, 工程カンバンID>
    private final Map<Long, Long> workingMap = new HashMap<>();

    private final List<AdInterfaceServiceInterface> plugins;

    // 作業者端末の工程進捗フラグ Map<設備ID, 工程進捗フラグ>
    private final Map<Long, Boolean> workProgressMap = new HashMap<>();

    /**
     * コンストラクタ
     *
     * @param plugins
     */
    public BroadcastCommHandler(List<AdInterfaceServiceInterface> plugins) {
        this.scheduleTask();
        this.scheduleDailyTask();
        this.plugins = plugins;
    }

    /**
     *
     * @return
     */
    @Override
    public List<Class> getCommandCollection() {
        return Arrays.asList(
                ConnectNoticeCommand.class,
                ActualNoticeCommand.class,
                TraceCommand.class,
                CallingNoticeCommand.class,
                LineTimerNoticeCommand.class,
                WorkReportCommand.class,
                ResetCommand.class,
                CancelWorkCommand.class,
                RequestCommand.class,
                SummaryReportNoticeCommand.class,
                DeviceConnectionServiceCommand.class);
    }

    /**
     * コマンド受信処理
     *
     * @param channel
     * @param command
     */
    @Override
    public void recvCommand(Channel channel, Object command) {
        if (command instanceof ConnectNoticeCommand) {
            // 接続通知を受信したら、接続先を保持
            ConnectNoticeCommand connect = (ConnectNoticeCommand) command;
            switch (connect.getEquipmentType()) {
                case TERMINAL:
                    // 作業者端末チャンネルマップに情報を追加する。
                    logger.info("Add terminal: {} {}", connect.getEuipmenteId(), channel);
                    this.terminalChannels.put(connect.getEuipmenteId(), channel);

                    logger.info("workProgressMap add: equipmentId={}, workProgressFlag={}", connect.getEuipmenteId(), connect.isWorkProgressFlag());
                    this.workProgressMap.put(connect.getEuipmenteId(), connect.isWorkProgressFlag());
                    break;

                case MONITOR:
                    // モニタ端末チャンネルマップに情報を追加する。
                    logger.info("Add monitor: {}", channel);
                    this.monitorChannels.add(channel);

                    // データを初期化
                    this.initializeMonitor(connect.getEuipmenteId(), false);

                    // 蓄積した工程進捗コマンドを返送
                    if (this.workReportMap.containsKey(connect.getEuipmenteId())) {
                        Map<Long, WorkReportCommand> workReports = this.workReportMap.get(connect.getEuipmenteId());
                        for (Entry<Long, WorkReportCommand> entry : workReports.entrySet()) {
                            if (Objects.nonNull(entry.getValue())) {
                                channel.writeAndFlush(entry.getValue());
                            }
                        }
                    }
                    break;
            }
            return;
        }

        // 作業キャンセルコマンド
        if (command instanceof CancelWorkCommand) {
            for (Channel terminalChannel : this.terminalChannels.values()) {
                terminalChannel.writeAndFlush(command);
            }
            return;
        }

        // 進捗モニターにコマンドをブロードキャスト
        for (Channel motitorChannel : this.monitorChannels) {
            motitorChannel.writeAndFlush(command);
        }

        if (command instanceof ActualNoticeCommand) {
            // 実績通知コマンド
            ActualNoticeCommand actualCommand = (ActualNoticeCommand) command;

            switch (actualCommand.getEquipmentStatus()) {
                case WORKING:
                    // 作業者端末の作業中工程カンバンマップに情報を追加する。
                    logger.info("workingMap add: equipmentId={}, workKanbanId={}", actualCommand.getEquipmentId(), actualCommand.getWorkKanbanId());
                    workingMap.put(actualCommand.getEquipmentId(), actualCommand.getWorkKanbanId());

                    // 工程進捗がONの作業者端末で、工程カンバンのトレース情報が存在する場合、蓄積したトレースコマンドを返送する。
                    if (this.workProgressMap.containsKey(actualCommand.getEquipmentId())
                            && this.workProgressMap.get(actualCommand.getEquipmentId())
                            && this.traceMap.containsKey(actualCommand.getWorkKanbanId())
                            && this.terminalChannels.containsKey(actualCommand.getEquipmentId())) {
                        // トレースコマンドを送信する。
                        Channel terminalChannel = this.terminalChannels.get(actualCommand.getEquipmentId());
                        TraceCommand traceCommand = this.traceMap.get(actualCommand.getWorkKanbanId());
                        logger.info("Send: {} {}", traceCommand, terminalChannel);
                        terminalChannel.writeAndFlush(traceCommand);
                    }
                    break;

                case COMPLETION:
                    // 送信元の作業者端末が工程進捗ONの場合、同じ工程カンバンを作業している工程進捗ONの作業者端末に完了コマンドを送信する。
                    if (this.workProgressMap.containsKey(actualCommand.getEquipmentId())
                            && this.workProgressMap.get(actualCommand.getEquipmentId())) {
                        for (Entry<Long, Long> entry : this.workingMap.entrySet()) {
                            if (this.workProgressMap.containsKey(entry.getKey())
                                    && this.workProgressMap.get(entry.getKey())
                                    && Objects.equals(entry.getValue(), actualCommand.getWorkKanbanId())
                                    && !Objects.equals(entry.getKey(), actualCommand.getEquipmentId())
                                    && this.terminalChannels.containsKey(entry.getKey())) {
                                // 完了コマンドを送信する。
                                Channel terminalChannel = this.terminalChannels.get(entry.getKey());
                                WorkCommand workCommand = new WorkCommand("complete", null, actualCommand.getWorkKanbanId(), actualCommand.getDateTime());
                                logger.info("Send: {} {}", workCommand, terminalChannel);
                                terminalChannel.writeAndFlush(workCommand);
                            }
                        }
                    }

                    // 工程カンバン完了で、トレース情報マップに該当工程カンバンの情報が存在する場合、マップから情報を削除する。
                    if (KanbanStatusEnum.COMPLETION.equals(actualCommand.getWorkKanbanStatus())
                            && this.traceMap.containsKey(actualCommand.getWorkKanbanId())) {
                        logger.info("Remove trace log: " + actualCommand.getWorkKanbanId());
                        this.traceMap.remove(actualCommand.getWorkKanbanId());
                    }

                case SUSPEND:
                    // 作業者端末の作業中工程カンバンマップから情報を削除する。
                    if (this.workingMap.containsKey(actualCommand.getEquipmentId())) {
                        this.workingMap.remove(actualCommand.getEquipmentId());
                    }

                    // 他に該当工程カンバンを作業中の作業者端末がない場合、トレース情報マップから情報を削除する。
                    if (!this.workingMap.values().contains(actualCommand.getWorkKanbanId())
                            && this.traceMap.containsKey(actualCommand.getWorkKanbanId())) {
                        logger.info("Remove trace log: " + actualCommand.getWorkKanbanId());
                        this.traceMap.remove(actualCommand.getWorkKanbanId());
                    }
                    break;
            }

            // 実績通知コマンド
            this.plugins.stream().forEach(plugin -> {
                plugin.noticeActualCommand(command);
            });

        } else if (command instanceof TraceCommand) {
            // トレースコマンド
            TraceCommand traceCommand = (TraceCommand) command;

            // 送信元の作業者端末が工程進捗OFFの場合は無視する。
            if (!this.workProgressMap.containsKey(traceCommand.getEquipmentId())
                    || !this.workProgressMap.get(traceCommand.getEquipmentId())) {
                return;
            }

            if (traceCommand.isStart()) {
                if (this.traceMap.containsKey(traceCommand.getWorkKanbanId())) {
                    // 開始時にトレーサビリティデータが保存済の場合は無視する
                    return;
                }
            } else {
                // 同じ工程カンバンを作業している工程進捗ONの作業者端末にトレースコマンドを送信する。
                for (Entry<Long, Long> entry : this.workingMap.entrySet()) {
                    if (this.workProgressMap.containsKey(entry.getKey())
                            && this.workProgressMap.get(entry.getKey())
                            && Objects.equals(entry.getValue(), traceCommand.getWorkKanbanId())
                            && !Objects.equals(entry.getKey(), traceCommand.getEquipmentId())
                            && this.terminalChannels.containsKey(entry.getKey())) {
                        logger.info("send trace: equipmentId={}, command={}", entry.getKey(), command);
                        
                        // トレースコマンドを送信
                        Channel terminalChannel = this.terminalChannels.get(entry.getKey());
                        terminalChannel.writeAndFlush(command);
                    }
                }
            }

            TraceCommand value;
            if (this.traceMap.containsKey(traceCommand.getWorkKanbanId())) {
                value = this.traceMap.get(traceCommand.getWorkKanbanId());
            } else {
                value = new TraceCommand(traceCommand.getWorkKanbanId(), 0L, null, new Properties(), new Properties(), new Date(), false);
                this.traceMap.put(traceCommand.getWorkKanbanId(), value);
            }

            value.getValues().putAll(traceCommand.getValues());
            value.getAuthor().putAll(traceCommand.getAuthor());

        } else if (command instanceof LineTimerNoticeCommand) {
            for (Channel terminalChannel : this.terminalChannels.values()) {
                terminalChannel.writeAndFlush(command);
            }

        } else if (command instanceof WorkReportCommand) {
            synchronized (BroadcastCommHandler.this) {
                WorkReportCommand workReport = (WorkReportCommand) command;

                for (Entry<Long, Map<Long, WorkReportCommand>> entry : this.workReportMap.entrySet()) {
                    AndonMonitorLineProductSetting setting = this.settingMap.get(entry.getKey());

                    if (!StringUtils.isEmpty(setting.getModelName())
                            && !StringUtils.equals(setting.getModelName(), workReport.getModelName())) {
                        // モデル名が異なる
                        continue;
                    }

                    if (setting.isReportByWork()) {
                        // 工程の場合
                        if (entry.getValue().containsKey(workReport.getWorkId())) {
                            entry.getValue().put(workReport.getWorkId(), workReport);
                        }
                    } else {
                        // 設備の場合
                        if (entry.getValue().containsKey(workReport.getEquipmentId())) {
                            entry.getValue().put(workReport.getEquipmentId(), workReport);
                        }
                    }
                }
            }

        } else if (command instanceof ResetCommand) {
            // リセットコマンド
            List<Long> monitorIds = new ArrayList<>(this.settingMap.keySet());
            for (Long monitorId : monitorIds) {
                this.initializeMonitor(monitorId, true);
            }

        } else if (command instanceof RequestCommand) {
            // リクエストコマンド
            RequestCommand request = (RequestCommand) command;
            this.plugins.stream().forEach(plugin -> {
                Object responce = plugin.request(request);
                if (Objects.nonNull(responce)) {
                    channel.writeAndFlush(responce);
                }
            });
        } else if (command instanceof SummaryReportNoticeCommand) {
            SummaryReportNoticeCommand summaryReportNoticeCommand = (SummaryReportNoticeCommand) command;
            this.plugins.forEach(plugin -> {
                plugin.notice(command);
            });
        } else if (command instanceof  DeviceConnectionServiceCommand) {
            this.plugins.forEach(plugin -> {
                plugin.notice(command);
            });
        } else if (command instanceof CallingNoticeCommand) {
            this.plugins.forEach(plugin -> plugin.notice(command));
        }
    }

    /**
     * クライアント切断処理
     *
     * @param channel
     */
    @Override
    public void disconnectChannel(Channel channel) {
        // 作業者端末チャンネルマップから、該当チャンネルを検索する。
        Optional<Entry<Long, Channel>> optTerminalChannel = this.terminalChannels.entrySet().stream()
                .filter(p -> Objects.equals(p.getValue(), channel))
                .findFirst();

        // 該当チャンネルが存在したら、作業者端末に関連するマップから情報を削除する。
        if (optTerminalChannel.isPresent()) {
            long equipmentId = optTerminalChannel.get().getKey();

            logger.info("Remove terminal: {}", equipmentId);
            this.terminalChannels.remove(equipmentId);

            if (this.workingMap.containsKey(equipmentId)) {
                logger.info("workingMap remove: equipmentId={}", equipmentId);
                this.workingMap.remove(equipmentId);
            }

            if (this.workProgressMap.containsKey(equipmentId)) {
                logger.info("workProgressMap remove: equipmentId={}", equipmentId);
                this.workProgressMap.remove(equipmentId);
            }
        }

        // モニタ端末チャンネルマップに該当チャンネルが存在したら、マップから情報を削除する。
        if (monitorChannels.contains(channel)) {
            logger.info("Remove monitor: {}", channel);
            this.monitorChannels.remove(channel);
        }
    }

    /**
     * タスクをスケジュールする。
     */
    private void scheduleTask() {
        Timer timer = new Timer();

        long keepInterval = 60000L;
        String value = AdProperty.getProperties().getProperty(KEEP_INTERVAL);
        if (!StringUtils.isEmpty(value)) {
            keepInterval = Long.parseLong(value);
        } else {
            AdProperty.getProperties().setProperty(KEEP_INTERVAL, "60000");
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (BroadcastCommHandler.this) {
                    try {
                        KeepCommand command = new KeepCommand();
                        for (Channel channel : monitorChannels) {
                            channel.writeAndFlush(command);
                        }
                        for (Channel channel : terminalChannels.values()) {
                            channel.writeAndFlush(command);
                        }
                    } finally {
                    }
                }
            }
        }, keepInterval, keepInterval);
    }

    /**
     * 日次タスクをスケジュールする。
     */
    private void scheduleDailyTask() {
        Timer timer = new Timer();

        int resetHours = 0;
        String value = AdProperty.getProperties().getProperty(RESET_HOURS);
        if (!StringUtils.isEmpty(value)) {
            resetHours = Integer.parseInt(value);
        } else {
            AdProperty.getProperties().setProperty(RESET_HOURS, "0");
        }

        Date date = new Date();
        date = DateUtils.setHours(date, resetHours);
        date = DateUtils.setMinutes(date, 41);
        date = DateUtils.setSeconds(date, 0);
        date = DateUtils.setMilliseconds(date, 0);

        try {
            Thread.sleep(100L);
            if (date.before(new Date())) {
                date = DateUtils.addDays(date, 1);
            }
        } catch (Exception ex) {
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (BroadcastCommHandler.this) {
                    try {
                        logger.info("Reset the status.");
                        List<Long> monitorIds = new ArrayList<>(settingMap.keySet());
                        for (Long monitorId : monitorIds) {
                            initializeMonitor(monitorId, true);
                        }

                        traceMap.clear();

                        // 日次処理を実行
                        plugins.stream().forEach(plugin ->{
                             plugin.execDaily();
                        });
                    } finally {
                        scheduleTask();
                    }
                }
            }
        }, date);

        logger.info("Schedule the task: {}", date);
    }

    /**
     * 進捗モニターの関連データを初期化する。
     *
     * @param monitorId
     * @param reload
     */
    private boolean initializeMonitor(Long monitorId, boolean reload) {

        AndonMonitorLineProductSetting setting = (AndonMonitorLineProductSetting) this.andonFacede.getLineSetting(
                monitorId, AndonMonitorLineProductSetting.class);

        if (Objects.isNull(setting)) {
            if (this.settingMap.containsKey(monitorId)) {
                this.settingMap.remove(monitorId);
            }
            if (this.workReportMap.containsKey(monitorId)) {
                this.workReportMap.remove(monitorId);
            }
            return false;
        }

        if (!reload && this.settingMap.containsKey(monitorId)) {
            return true;
        }

        this.settingMap.put(monitorId, setting);

        Map<Long, WorkReportCommand> workReports = new HashMap<>();
        if (setting.isReportByWork()) {
            for (WorkSetting workSetting : setting.getWorkCollection()) {
                if (!workSetting.getWorkIds().isEmpty()) {
                    workReports.put(workSetting.getWorkIds().get(0), null);
                }
            }
        } else {
            for (WorkEquipmentSetting equipmentSetting : setting.getWorkEquipmentCollection()) {
                if (!equipmentSetting.getEquipmentIds().isEmpty()) {
                    workReports.put(equipmentSetting.getEquipmentIds().get(0), null);
                }
            }
        }

        this.workReportMap.put(monitorId, workReports);

        return true;
    }

//    public LinkedHashSet<Channel> getTerminalChannels() {
//        return this.terminalChannels;
//    }
}
