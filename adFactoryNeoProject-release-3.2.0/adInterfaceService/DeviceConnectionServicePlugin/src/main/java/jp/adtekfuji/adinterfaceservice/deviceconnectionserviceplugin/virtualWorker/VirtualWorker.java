package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.virtualWorker;

import java.util.LinkedList;

import java.util.Objects;
import java.util.Optional;

import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.entity.DeviceConnectionEntity;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.mail.MailSender;
import jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.utility.MessageUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 仮想作業者(装置状態管理)
 * 
 * @author okada
 */
public class VirtualWorker implements Runnable
{
    static protected final Logger logger = LogManager.getLogger(); // ログ出力用クラス
    static protected final Optional<MailSender> mailSender = MailSender.getInstance();

    /** 現在の接続状態 */
    protected IConnectStatus connectStatus = null;

    /** 受信した実績通知コマンドを蓄積するキュー */
    private final LinkedList<Command> recvQueue = new LinkedList<>();

    /** サービス実行状態 <pre>true：実行中、false：停止中</pre> */
    private Boolean execution;

    /** デバイス接続方法情報 */
    protected DeviceConnectionEntity deviceConnectionInfo;

    
    // ▼▼▼ 実績通知コマンド関連のロジック ▼▼▼
    /**
     * コマンド
     */
    private interface Command{
        void apply(VirtualWorker worker);
    }

    /**
     * スレッド開始コマンド
     * */
    private static class StartVirtualWorkerCommand implements  Command {
        @Override
        public void apply(VirtualWorker worker) {
            logger.info(logMsg("VirtualWorker thread start", worker.deviceConnectionInfo));
        }
    }

    /**
     * 接続状態更新コマンド
     */
    private static class UpdateConnectStatusCommand implements Command {
        @Override
        public void apply(VirtualWorker worker) {
            worker.update();
        }
    }

    /**
     * 処理実行コマンド
     */
    private static class DoStatusCommand implements Command{
        IWorkStatusCommand command;
        DoStatusCommand(IWorkStatusCommand command) {
            this.command = command;
        }
        @Override
        public void apply(VirtualWorker worker) {
            worker.doStatusCommand(command);
        }
    }

    /**
     * 監視処理終了コマンド
     */
    private static class ProcessEndCommand implements Command {
        @Override
        public void apply(VirtualWorker worker) {
            worker.connectStatus = worker.connectStatus.logout();
            worker.execution = false;
        }
    }

    /**
     * テストコマンド
     */
    private static class TestCommand implements Command {
        @Override
        public void apply(VirtualWorker worker) {
            worker.confirmationLog();
        }
    }
    // ▲▲▲ 実績通知コマンド関連のロジック ▲▲▲


    /**
     * コンストラクタ
     */
    protected VirtualWorker() {
        this.execution = true;

        // 開始コマンド設定
        StartVirtualWorkerCommand cmd = new StartVirtualWorkerCommand();
        synchronized (this.recvQueue) {
            this.recvQueue.add(cmd);
            this.recvQueue.notify();
        }
    }

    /**
     * 更新
     */
    private void update()
    {
        IConnectStatus nextStatus = this.connectStatus.updateConnectState();
        if (Objects.nonNull(nextStatus)) {
            this.connectStatus = nextStatus;
        }
    }

    /**
     * コマンド実行
     * @param command コマンド
     */
    private void doStatusCommand(IWorkStatusCommand command)
    {
        this.connectStatus.doStatusCommand(command);
    }

    // -- パブリックメソッド --
    /**
     * スレッドのメイン処理
     */
    @Override
    public void run() {
        logger.info(logMsg("VirtualWorker run start", this.deviceConnectionInfo));
        try {
            while (this.execution) {

                try {
                    Command cmd = null;
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
                        cmd = recvQueue.removeFirst();
                    }

                        logger.debug(logMsg(String.format("CMD: %s Start", !Objects.isNull(cmd) ? cmd.getClass().getSimpleName() : ""), this.deviceConnectionInfo));
                        cmd.apply(this);
                        logger.debug(logMsg(String.format("CMD: %s End", !Objects.isNull(cmd) ? cmd.getClass().getSimpleName() : ""), this.deviceConnectionInfo));

                } catch (Exception ex) {
                    logger.fatal(logMsg("VirtualWorker run Exception", this.deviceConnectionInfo));
                    logger.fatal(ex, ex);
                }
            }
        } finally {
            logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!! VirtualWorker Task End");
            remove();
        }
    }

    /**
     * テストイベント
     */
    public void testlog(){
        logger.info(logMsg("VirtualWorker testlog", this.deviceConnectionInfo));
        TestCommand cmd = new TestCommand();
        synchronized (recvQueue) {
            recvQueue.add(cmd);
            recvQueue.notify();
        }
    }

    /**
     * 監視処理を終了させる
     */
    public void endProcess(){
        logger.info(logMsg("VirtualWorker End", this.deviceConnectionInfo));
        ProcessEndCommand cmd = new ProcessEndCommand();
        synchronized (this.recvQueue) {
            this.recvQueue.add(cmd);
            this.recvQueue.notify();
        }
    }

    /**
     * コマンド送信
     * @param command コマンド送信
     */
    protected void sendCommand(IWorkStatusCommand command) {
        logger.info(logMsg("VirtualWorker SendCommand", this.deviceConnectionInfo));
        DoStatusCommand cmd = new DoStatusCommand(command);
        synchronized (this.recvQueue) {
            this.recvQueue.add(cmd);
            this.recvQueue.notify();
        }
    }

    /**
     * ログイン処理(スレッド生成側からのログイン指示時に利用)
     */
    public void loginProcess(){
        logger.info(logMsg("login VirtualWorker.", this.deviceConnectionInfo));
        UpdateConnectStatusCommand cmd = new UpdateConnectStatusCommand();
        synchronized (this.recvQueue) {
            this.recvQueue.add(cmd);
            this.recvQueue.notify();
        }
    }

    /**
     * 組織(仮想作業者)更新処理(スレッド生成側からのログイン指示時に利用)
     */
    public void updateOrganizationProcess(){
        logger.info(logMsg("updateOrganizationProcess VirtualWorker.", this.deviceConnectionInfo));
        UpdateConnectStatusCommand cmd = new UpdateConnectStatusCommand();
        synchronized (this.recvQueue) {
            this.recvQueue.add(cmd);
            this.recvQueue.notify();
        }
    }

    /**
     * 設置(装置)更新処理(スレッド生成側からのログイン指示時に利用)
     * 
     */
    public void updateEquipmentProcess(){
        logger.info(logMsg("updateEquipmentProcess VirtualWorker.", this.deviceConnectionInfo));
        UpdateConnectStatusCommand cmd = new UpdateConnectStatusCommand();
        synchronized (this.recvQueue) {
            this.recvQueue.add(cmd);
            this.recvQueue.notify();
        }
    }
    
    // -- プライベートメソッド --
    /**
     * ThreadLocalを削除
     */
    private void remove(){
        try {
        } catch (Exception e) {
            logger.error(e, e);
        }
    }
        
    /**
     * ログ出力時の共通情報の付与
     * 
     * @param msg ログ内容
     * @return 共通情報付与後のログ内容
     */
    static private String logMsg(String msg, DeviceConnectionEntity deviceConnectionInfo){
        return String.format("%s %s",
                msg,
                MessageUtility.getLoginToInformation(deviceConnectionInfo.getEquipmentIdentify(),
                                                     deviceConnectionInfo.getOrganizationIdentify()));
    }

    /**
     * 確認用のログ出力
     * @return 本クラスの設定値
     */
    private void confirmationLog(){
        
        String name = !Objects.isNull(this.connectStatus) ? this.connectStatus.getClass().getSimpleName() : "";
        
        String msg = "[\"VirtualWorker\",{" + 
                "\"" + "execution" + "\":\"" + this.execution + "\"," + 
                "\"" + "equipmentIdentify" + "\":\"" + this.deviceConnectionInfo.getEquipmentIdentify() + "\"," + 
                "\"" + "organizationIdentify" + "\":\"" + this.deviceConnectionInfo.getOrganizationIdentify() + "\"," + 
                "\"" + "connectStatus" + "\":\"" + name + "\"," + 
                "\"" + "workStatus" + "\":\"" + this.connectStatus.getWorkStatus() + "\"" + 
                "}]";

        logger.debug(msg);
    }
     
}
