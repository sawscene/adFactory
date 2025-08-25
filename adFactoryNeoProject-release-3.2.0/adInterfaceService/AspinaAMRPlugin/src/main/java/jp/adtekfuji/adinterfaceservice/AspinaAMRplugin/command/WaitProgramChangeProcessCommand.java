package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.command;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ProcessCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ActionCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive.ProgramProcessState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

public class WaitProgramChangeProcessCommand implements ProcessCommand {
    private static final Logger logger = LogManager.getLogger();
    long startTime;

    final Long waitProgramNumber;

    private boolean isTimeout() {
        return 10000 < new Date().getTime() - startTime;
    }

    public WaitProgramChangeProcessCommand(Long waitProgramNumber) {
        this.waitProgramNumber = waitProgramNumber;
        this.startTime = new Date().getTime();
    }

    /**
     * 反映 & 削除するか?
     * @param receiveData 受信データ
     * @param consumer 送信用コンシューマ
     * @return 削除するか?
     */
    @Override
    public boolean applyAndIsRemove(ReceiveData receiveData, Consumer<ActionCommand> consumer) {
        if (!(receiveData instanceof ProgramProcessState)) {
            if (isTimeout()) {
                logger.info("timeout");
                // タイムアウトの場合は終了する
                consumer.accept(new ActionEndCommand());
                return true;
            }
            return false;
        }

        ProgramProcessState programProcessState = (ProgramProcessState) receiveData;
        if (programProcessState.getState() == 1
                && (Objects.isNull(waitProgramNumber) || programProcessState.getTargetProgram() == waitProgramNumber)) {
            // プログラム番号が実施されていない
            return false;
        }

        logger.info("Sent End Command");
        consumer.accept(new ActionEndCommand());
        return true;
    }
}
