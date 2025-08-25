/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import jp.adtekfuji.adFactory.adinterface.command.DeviceConnectionServiceCommand;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import jp.adtekfuji.adFactory.utility.JsonUtils;

import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.target.InstanceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 機器通信サービス
 *
 * @author okada
 */
public class DeviceConnectionService extends Thread implements AdInterfaceServiceInterface {

    private final static String BASE_PATH = System.getenv("ADFACTORY_HOME") + File.separator + "conf";
    private final static String DEVICE_SETTING_FILE_PATH = BASE_PATH +  File.separator + "DeviceConnectionService.json";

    /**
     * サービス名
     */
    private final String SERVICE_NAME = "DeviceConnectionService";

    /**
     * ログ出力用クラス
     */
    static private final Logger logger = LogManager.getLogger();

    /**
     * サービス実行状態
     * <pre>
     * true：実行中、false：停止中
     * </pre>
     */
    private boolean execution = false;

    /**
     * 受信した実績通知コマンドを蓄積するキュー
     */
    private final LinkedList<DeviceConnectionServiceCommand> recvQueue = new LinkedList<>();

    /**
     * コンストラクタ
     */
    public DeviceConnectionService() {
    }

    /**
     * 設定ファイルから情報を取得
     * 
     * @return 設定情報
     */
    static Optional<List<Map<String, String>>> loadConfigFile()
    {

        if (Files.notExists(Paths.get(DEVICE_SETTING_FILE_PATH))) {
            logger.fatal("Error Not found setting file");
            return Optional.empty();
        }


        try ( java.util.stream.Stream<String> item = Files.lines(Paths.get(DEVICE_SETTING_FILE_PATH), StandardCharsets.UTF_8)){
            final String jsonStr = item.collect(Collectors.joining(System.getProperty("line.separator")));
            return Optional.ofNullable(JsonUtils.jsonToMaps((jsonStr)));
        } catch (Exception e) {
            logger.fatal(e,e);
            return Optional.empty();
        }
    }

    /**
     * このプラグインによって実行されるアクション
     */
    @Override
    public void run() {
        logger.debug("DeviceConnectionService run start");

        List<Map<String, String>> deviceConnectionEntities = null;
        InstanceManager instanceManager = new InstanceManager();
        
        try {
            while (execution) {
                synchronized (recvQueue) {
                    if (recvQueue.isEmpty()) {
                        try {
                            recvQueue.wait();
                        } catch (InterruptedException ex) {
                            logger.fatal(ex, ex);
                        }
                        if (recvQueue.isEmpty()) {
                            continue;
                        }
                    }
                    DeviceConnectionServiceCommand cmd = recvQueue.removeFirst();

                    // デバイスが無い場合は再度初動作から実施
                    if (Objects.isNull(deviceConnectionEntities)) {
                        deviceConnectionEntities = loadConfigFile().orElse(new ArrayList<>());
                    }

                    logger.info("CMD: {} Start", cmd.getCommand());
                    try {
                        // 何かしらの処理
                        switch (cmd.getCommand()) {
                            case START_SERVICE:         // サービス起動イベント
                                logger.debug("event [START_SERVICE]");
                                // 設定ファイルから情報を取得
                                // サービス起動処理
                                instanceManager.startService(deviceConnectionEntities);
                                break;
                            case START_SERVER:          // サーバー起動イベント
                                logger.debug("event [START_SERVER]");
                                /*
                                サーバー起動イベントでは組織(仮想作業者)更新と設置(装置)更新の２つの処理を行う。
                                ただ、ロジックではどちらの更新処理を行っても両方処理するので１つのみ記述
                                */
                                instanceManager.updateOrganization();
                                break;
                            case UPDATE_ORGANIZATION:   // 組織(仮想作業者)更新イベント
                                logger.debug("event [UPDATE_ORGANIZATION]");
                                instanceManager.updateOrganization();
                                break;
                            case UPDATE_EQUIPMENT:      // 設置(装置)更新イベント
                                logger.debug("event [UPDATE_EQUIPMENT]");
                                instanceManager.updateEquipment();
                                break;
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                    logger.info("CMD: {} End", cmd.getCommand());
                }
            }
            
            // サービス停止
            instanceManager.endService();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        
        logger.debug("DeviceConnectionService run End");
    }

    /**
     * サービスを開始する。
     *
     * @throws Exception
     */
    @Override
    public void startService() throws Exception {
        if (!this.execution) {
            this.execution = true;
            super.start();
            DeviceConnectionServiceCommand cmd = new DeviceConnectionServiceCommand(DeviceConnectionServiceCommand.COMMAND.START_SERVICE);
            synchronized (recvQueue) {
                recvQueue.add(cmd);
                recvQueue.notify();
            }
        }
    }

    /**
     * サービスを停止する。
     *
     * @throws Exception
     */
    @Override
    public void stopService() throws Exception {
        logger.info("Stop DeviceConnectionService start.");
        this.execution = false;
        synchronized (this.recvQueue) {
            this.recvQueue.notify();
        }
        super.join();
    }

    /**
     * 通知コマンドを受信した。
     *
     * @param command 通知コマンド
     */
    @Override
    public void notice(Object command) {
        logger.info("Notice DeviceConnectionServiceCommand.");
        if (command instanceof DeviceConnectionServiceCommand) {
            DeviceConnectionServiceCommand cmd = (DeviceConnectionServiceCommand) command;
            synchronized (recvQueue) {
                recvQueue.add(cmd);
                recvQueue.notify();
            }
        }
    }

    /**
     * サービス名を取得する。
     *
     * @return
     */
    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}
