/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andonapp;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.net.HttpClientException;
import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.BreakCommand;
import jp.adtekfuji.adFactory.adinterface.command.KeepCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResetCommand;
import jp.adtekfuji.adFactory.adinterface.command.ResultUpdateCommand;
import jp.adtekfuji.adFactory.adinterface.command.TimerCommand;
import jp.adtekfuji.adFactory.adinterface.command.UpdateCommand;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.andon.clientservice.AndonMonitorSettingFacade;
import jp.adtekfuji.andon.common.AndonCampus;
import jp.adtekfuji.andon.common.AndonComponent;
import jp.adtekfuji.andon.common.Constants;
import jp.adtekfuji.andon.entity.PluginInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import jp.adtekfuji.andonapp.comm.AdInterfaceClientService;
import jp.adtekfuji.andonapp.comm.EquipmentInfoFacade;
import jp.adtekfuji.andonapp.comm.NoticeCommandListner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class AndonPluginContainer implements NoticeCommandListner {

    private static final Logger logger = LogManager.getLogger();
    private static AndonPluginContainer instance = null;
    private final AndonCampus andonCampus = new AndonCampus();
    private final AdInterfaceClientService adInterfaceClientService = new AdInterfaceClientService();
    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final AndonMonitorSettingFacade andonMonitorSettingFacade = new AndonMonitorSettingFacade();
    private final List<PluginInfoEntity> components = new ArrayList<>();
    private final Object lock = new Object();
   
    private Long monitorId = null;
    private AndonMonitorLineProductSetting setting = null;
    // 最終通知時間
    private long lastTime;
    // 更新間隔
    private long interval;
    // 遅延更新時間
    private long delay;
    private boolean update;
    // 実績更新間隔
    private long resultInterval;
    private long lastResultUpdate;
    // 休憩中
    private boolean isBreakTime = false;

    // 定期更新処理
    private final Timeline timeline = new Timeline();

    private AndonPluginContainer() {
        //Pluginを読み込む.
        PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
        List<AdAndonComponentInterface> plugins = new ArrayList<>();
        plugins.addAll(PluginLoader.load(AdAndonComponentInterface.class));
        logger.info("plugin:{}", plugins);
        for (AdAndonComponentInterface plugin : plugins) {
            AndonComponent c = plugin.getClass().getAnnotation(AndonComponent.class);
            FxComponent f = plugin.getClass().getAnnotation(FxComponent.class);

            // プラグイン名
            String pluginName = plugin.getClass().getPackage().getName();
            int pos = pluginName.lastIndexOf('.');
            if (pos > 0) {
                pluginName = pluginName.substring(pos + 1);
            }

            if (Objects.nonNull(c) && Objects.nonNull(f)) {
                PluginInfoEntity pluginInfo = new PluginInfoEntity();
                pluginInfo.setDispName(c.title());
                pluginInfo.setComponentName(f.id());
                pluginInfo.setPluginName(pluginName);
                components.add(pluginInfo);
            } else {
                logger.fatal("!!!not define:{}", plugin);
            }
        }
        //リスナー登録.
        adInterfaceClientService.getHandler().setNoticeListner(this);
    }

    public static AndonPluginContainer getInstance() {
        if (Objects.isNull(instance)) {
            instance = new AndonPluginContainer();
        }
        return instance;
    }

    /**
     * 進捗モニタ設定を更新する
     *
     */
    public void updateAndonSetting() {
        logger.info("AndonPluginContainer::updateAndonSetting start; monitorId = {}", monitorId);
        if (Objects.nonNull(monitorId)) {
            this.setting = (AndonMonitorLineProductSetting) andonMonitorSettingFacade
                    .getLineSetting(monitorId, AndonMonitorLineProductSetting.class);
        }
    }

    /**
     * 設備ログイン
     * @throws java.lang.Exception
     * @throws adtekfuji.net.HttpClientException
     */
    public void andonLogin() throws Exception, HttpClientException {
        EquipmentLoginResult result = equipmentInfoFacade.login();
        logger.info("EquipmentLoginResult:{}", result);
        if (result.getErrorType() != ServerErrorTypeEnum.SUCCESS) {
            throw new HttpClientException(result.getErrorType().getCode(), "");
        }
        this.monitorId = result.getEquipmentId();
        this.startService();
    }

    /**
     * 各フレームを表示する。
     *
     * @param rootPane
     * @param width
     * @param height
     */
    public void visibleMonitor(Node rootPane, double width, double height) {
        andonCampus.loadComponents((Pane) rootPane, components, setting);
        //andonCampus.transforms(rootPane, width, height);

        // 遅延時間(秒)
        if (!AdProperty.getProperties().containsKey(Constants.DELAY_TIME)) {
            AdProperty.getProperties().setProperty(Constants.DELAY_TIME, String.valueOf(Constants.DEF_DELAY_TIME));
        }
        this.delay = Long.parseLong(AdProperty.getProperties().getProperty(Constants.DELAY_TIME));

        // 更新時間(秒)
        if (!AdProperty.getProperties().containsKey(Constants.UPDATE_INTERVAL)) {
            AdProperty.getProperties().setProperty(Constants.UPDATE_INTERVAL, String.valueOf(Constants.DEF_UPDATE_INTERVAL));
        }
        this.interval = Long.parseLong(AdProperty.getProperties().getProperty(Constants.UPDATE_INTERVAL));
        
        if (!AdProperty.getProperties().containsKey(Constants.RESULT_INTERVAL)) {
            AdProperty.getProperties().setProperty(Constants.RESULT_INTERVAL, String.valueOf(Constants.DEF_RESULT_INTERVAL));
        }
        this.resultInterval = Long.parseLong(AdProperty.getProperties().getProperty(Constants.RESULT_INTERVAL));
        
        if (0 < delay && Objects.nonNull(this.setting)) {
            if (this.timeline.getStatus() == Animation.Status.RUNNING) {
                this.timeline.stop();
            }

            this.lastTime = System.currentTimeMillis();
            this.lastResultUpdate = System.currentTimeMillis();
            
            this.timeline.setCycleCount(Timeline.INDEFINITE);
            this.timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(3), (ActionEvent event) -> {
                try {
                    synchronized (lock) {
                        if ((System.currentTimeMillis() - this.lastResultUpdate) >= this.resultInterval) {
                            this.timerUpdate(new ResultUpdateCommand());
                        }

                        long elapsedTime = System.currentTimeMillis() - this.lastTime;
                        if (elapsedTime >= this.interval) {
                            this.update(new TimerCommand());
                        }

                        if (this.update && elapsedTime >= this.delay) {
                            this.update = false;
                            this.update(new UpdateCommand());
                        }

                        if (Objects.nonNull(setting.getBreaktimes()) && BreaktimeUtil.isBreaktime(setting.getBreaktimes(), new Date())) {
                            // 休憩中
                            if (!this.isBreakTime) {
                                isBreakTime = true;
                                logger.info("Break time start." );
                                this.update(new BreakCommand());
                            }
                        } else {
                            // 稼働中
                            if (this.isBreakTime) {
                                isBreakTime = false;
                                logger.info("Break time end." );
                                this.update(new BreakCommand());
                            }
                        }
                    }

                } catch (Exception ex) {
                    logger.fatal(ex);
                }
            }));

            this.timeline.play();
        }
    }

    /**
     * 各フレームにコマンドを通知する。
     *
     * @param command コマンド
     */
    @Override
    public void notice(Object command) {
        synchronized (lock) {
            logger.info("notice:{}", command);
           
            if (command instanceof ActualNoticeCommand) {
                ActualNoticeCommand actualNoticeCommand = (ActualNoticeCommand) command;
                actualNoticeCommand.setMonitorId(this.monitorId);
                this.update(actualNoticeCommand);
                this.update = true;

            } else if (command instanceof ResetCommand) {
                ResetCommand reserCommand = (ResetCommand) command;
                if (Objects.isNull(reserCommand.getMonitorId()) || Objects.equals(this.monitorId, reserCommand.getMonitorId())) {
                    logger.info("Reset start: MonitorId={}", reserCommand.getMonitorId());
                    this.updateAndonSetting();
                    this.update(command);
                }

            } else if (!(command instanceof KeepCommand)){
                this.update(command);
            }
        }
    }

    /**
     * モニターを更新する。
     *
     * @param command コマンド
     */
    public synchronized void update(Object command) {
        try {
            logger.info("updateDisplay: {}", command);

            SceneContiner sc = SceneContiner.getInstance();
            for (Map.Entry<String, Object> e : sc.getFxComponentObjects().entrySet()) {
                if (e.getValue() instanceof AdAndonComponentInterface) {
                    AdAndonComponentInterface component = (AdAndonComponentInterface) e.getValue();
                    component.updateDisplay(command);
                }
            }

        } finally {
            this.lastTime = System.currentTimeMillis();
        }
    }
    
    /**
     * モニターを定期更新する。
     *
     * @param command コマンド
     */
    public synchronized void timerUpdate(Object command) {
        try {
            logger.info("timerUpdate start: {}", command);

            SceneContiner sc = SceneContiner.getInstance();
            for (Map.Entry<String, Object> e : sc.getFxComponentObjects().entrySet()) {
                if (e.getValue() instanceof AdAndonComponentInterface) {
                    AdAndonComponentInterface component = (AdAndonComponentInterface) e.getValue();
                    component.updateDisplay(command);
                }
            }

        } finally {
            this.lastResultUpdate = System.currentTimeMillis();
            logger.info("timerUpdate end.");
        }
    }
    
    private void startService() {
        logger.info("startService");
        this.adInterfaceClientService.getHandler().setMonitorId(this.monitorId);
        this.adInterfaceClientService.startService();
    }

    public void stopService() throws InterruptedException {
        logger.info("stopService start.");

        if (Objects.nonNull(this.timeline)) {
            this.timeline.stop();
        }

        adInterfaceClientService.stopService();
        SceneContiner sc = SceneContiner.getInstance();
        for (Map.Entry<String, Object> e : sc.getFxComponentObjects().entrySet()) {
            if (e.getValue() instanceof AdAndonComponentInterface) {
                AdAndonComponentInterface component = (AdAndonComponentInterface) e.getValue();
                component.exitComponent();
            }
        }

        logger.info("stopService end.");
    }
}
