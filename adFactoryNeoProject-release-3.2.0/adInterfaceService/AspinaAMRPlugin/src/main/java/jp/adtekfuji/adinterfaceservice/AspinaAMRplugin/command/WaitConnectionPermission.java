package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.command;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ProcessCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ActionCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive.ConnectionReceiveData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.function.Consumer;

public class WaitConnectionPermission implements ProcessCommand {
    private static final Logger logger = LogManager.getLogger();

    /**
     * 開始時間
     */
    final long startTime;

    public WaitConnectionPermission() {
        this.startTime = new Date().getTime();
    }

    /**
     * タイムアウト
     * @return ture: タイムアウト, false: 未タイムアウト
     */
    private boolean isTimeout() {
        return 100000 < new Date().getTime() - startTime;
    }

    /**
     * 反映 & 削除するか?
     * @param receiveData 受信データ
     * @param consumer 送信用コンシューマ
     * @return 削除するか?
     */
    @Override
    public boolean applyAndIsRemove(ReceiveData receiveData, Consumer<ActionCommand> consumer) {
        if (isTimeout()) {
            logger.info("WaitConnectionPermission Timeout");
            consumer.accept(new ActionEndCommand());
            return true;
        }

        if (!(receiveData instanceof ConnectionReceiveData)) {
            return false;
        }

        ConnectionReceiveData connectionReceiveData = (ConnectionReceiveData) receiveData;
        if (connectionReceiveData.getReceiveData() != 0) {
            // 接続不可だったので終了
            logger.info("WaitConnectionPermission not Permission:{}", connectionReceiveData.getReceiveData());
            consumer.accept(new ActionEndCommand());
        }

        // 接続成功
        logger.info("Connection Permission");
        return true;
    }
}
