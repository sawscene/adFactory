package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.command;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ProcessCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ActionCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive.ReceiveConnectCheck;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.function.Consumer;

/**
 * キープアライブ
 */
public class KeepAliveProcessCommand implements ProcessCommand {

    private static final Logger logger = LogManager.getLogger();

    // 最大リトライ数
    private final static int RetryMaxCount = 3;
    // 最大送信待機時間
    private final static int ReceiveWaitTime = 3000;
    // タイムアウト時間
    private final static int SendWaitTime = 5000;

    // 送信時間
    @Getter
    private long sendTime = 0;
    // 受信時間
    @Getter
    private long receiveTime = 0;
    // 繰返し数
    @Getter
    private int retryCount = 0;

    public KeepAliveProcessCommand() {
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * 送信待ち残り時間
     * @return 送信待ち残り時間
     */
    private int getSendRemainingTime() {
        return (int)(SendWaitTime - (new Date().getTime() - this.sendTime));
    }

    /**
     * 受信待ち残り時間
     * @return 受信待ち残り時間
     */
    private int getReceiveRemainingTime() {
        return (int)(ReceiveWaitTime - (new Date().getTime() - this.receiveTime));
    }

    /**
     * 生存確認コマンド送信
     */
    public void sendCheckCommand(Consumer<ActionCommand> consumer)
    {
        this.receiveTime = 0;
        this.sendTime = 0;
        consumer.accept(new ActionConnectCheckCommand(this));
    }

    /**
     * 反映 & 削除するか?
     * @param receiveData 受信データ
     * @param consumer 送信用コンシューマ
     * @return 削除するか?
     */
    @Override
    public boolean applyAndIsRemove(ReceiveData receiveData, Consumer<ActionCommand> consumer) {
        if (receiveData instanceof ReceiveConnectCheck) {
            this.retryCount = 0;
            this.receiveTime = new Date().getTime();
        }

        if (this.receiveTime != 0) {
            // 受け取っている場合
            int remainingTime = getReceiveRemainingTime();
            if (remainingTime > 0) {
                return false;
            }
            sendCheckCommand(consumer);
            return false;
        }

        if (this.sendTime != 0) {
            if (this.retryCount > RetryMaxCount ) {
                // リトライ回数が上限に達している。
                logger.info("Connection Error!!!");
                consumer.accept(new ActionEndCommand());
                return true;
            }

            // 送信済みの場合
            int remainingTime = getSendRemainingTime();
            if (remainingTime > 0) {
                return false;
            }
            logger.info("Retry: {}", this.retryCount);
            sendCheckCommand(consumer);
            return false;
        }

        sendCheckCommand(consumer);
        return false;
    }
}
