/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin;


import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import jp.adtekfuji.adFactory.adinterface.command.CallingNoticeCommand;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.command.*;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ConnectionUtils;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.SendData;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive.SelectReceiveData;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send.ConnectionNotification;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send.DisconnectNotification;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.utils.Connector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;


/**
 * AMR用処理
 */
public class AspinaAMR extends Thread implements AdInterfaceServiceInterface {

    /**
     * サービス名
     */
    private final String SERVICE_NAME = "AspinaAMR";

    /**
     * ログ出力用クラス
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * 動作開始コマンド
     */
    class StartActionCommand implements ActionCommand
    {
        final String reason;

        StartActionCommand(String reason) {
            this.reason = reason;
        }
        @Override
        public void executeCommand() {
            final LinkedList<ProcessCommand> processCommandQueue = new LinkedList<>();
            // キープアライブ
            processCommandQueue.add(new KeepAliveProcessCommand());
            // 接続許可待ち
            processCommandQueue.add(new WaitConnectionPermission());
            // 状態確認待ち
            processCommandQueue.add(new WaitStateProcessCommand(this.reason));
            try {
//                String ip = "localhost";
//                int port = 1234;
                String ip = "192.168.178.168";
                int port = 3210;
                // 接続
                Connector.connect(ip, port,
                        // 送信処理
                        connector -> {
                            // 初期化処理
                            SendData initSendData = new ConnectionNotification(1, 1);
                            connector.send(initSendData.crateSendData());
                            try {
                                while (execution) {
                                    if (!getNextActionCommand()
                                            .executeCommand(connector, processCommand -> {
                                        synchronized (processCommandQueue) {
                                            processCommandQueue.add(processCommand);
                                        }
                                    })) {
                                        return;
                                    }
                                }
                            } finally {
                                // 切断処理
                                SendData disconectSendData = new DisconnectNotification();
                                Objects.requireNonNull(connector).send(disconectSendData.crateSendData());
                            }
                        },
                        // 受信処理
                        receiveData -> {
                                Tuple<byte[], List<ReceiveData>> rest = ConnectionUtils.parseReceiveData(receiveData, SelectReceiveData::decodeReceiveData);
                                 rest.getRight()
                                        .forEach(data -> {
                                            synchronized (processCommandQueue) {
                                                processCommandQueue
                                                        .removeIf(process -> process.applyAndIsRemove(data, AspinaAMR.this::sendActionCommand));
                                            }
                                        });
                                 return rest.getLeft();
                        },
                        // 受信時の異常処理
                        exception -> sendActionCommand(new ActionExceptionCommand(exception)));

            } catch (Exception ex) {
                logger.fatal(ex);
            }
            logger.info("end StartActionCommand");
        }

        /**
         * 送信コマンド実行
         *
         * @param connector コネクタ
         * @param consumer データ送信
         * @return 接続しつづけるか?
         */
        @Override
        public boolean executeCommand(Connector connector, Consumer<ProcessCommand> consumer) {
            return true;
        }
    }


    /**
     * 受信した実績通知コマンドを蓄積するキュー
     */
    private final LinkedList<ActionCommand> recvQueue = new LinkedList<>();

    /**
     * サービス実行状態
     * true：実行中、false：停止中
     */
    private boolean execution = false;

    /**
     * コンストラクタ
     */
    public AspinaAMR() throws Exception {
        startService();
    }

    /**
     * コマンド取得
     * @return コマンド
     */
    private ActionCommand getNextActionCommand() {
        synchronized (recvQueue) {
            if (recvQueue.isEmpty()) {
                try {
                    logger.info("wait action command");
                    recvQueue.wait();
                } catch (InterruptedException ex) {
                    logger.fatal(ex, ex);
                }
                if (recvQueue.isEmpty()) {
                    return new ActionEmptyCommand();
                }
            }
            return recvQueue.removeFirst();
        }
    }

    /**
     * コマンド送信
     * @param actionCommand 送信コマンド
     */
    private void sendActionCommand(ActionCommand actionCommand) {
        synchronized (recvQueue) {
            recvQueue.add(actionCommand);
            recvQueue.notify();
        }
    }

    /**
     * このプラグインによって実行されるアクション
     */
    @Override
    public void run() {
        while (execution) {
            getNextActionCommand().executeCommand();
        }
        logger.info("end task");
    }

    /**
     * サービスを開始する。
     *
     */
    @Override
    public void startService() throws Exception {
        logger.info("Start AspinaAMR start.");

        if (!this.execution) {
            this.execution = true;
            super.start();
        }
    }

    /**
     * サービスを停止する。
     *
     */
    @Override
    public void stopService() throws Exception {
        logger.info("Stop SummaryReport start.");
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
        logger.info("Notice Aspina AMR start.");
        if (command instanceof CallingNoticeCommand) {
            CallingNoticeCommand cmd = (CallingNoticeCommand) command;
            if (StringUtils.isEmpty(cmd.getReason())
                    || !cmd.getReason().contains("AMR")) {
                return;
            }

            // 移動開始
            sendActionCommand(new StartActionCommand(cmd.getReason()));
        }
    }

    /**
     * サービス名を取得する。
     *
     * @return サービス名
     */
    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

}
