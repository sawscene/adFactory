package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.command;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ActionCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ProcessCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive.AMRStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class WaitStateProcessCommand implements ProcessCommand {

    final String reason;

    private static final Logger logger = LogManager.getLogger();

    public WaitStateProcessCommand(String reason) {
        this.reason = reason;
    }

    /**
     * 受信データを基に動作を決定する。また処理待ちをやめるかを返す。
     * @param receiveData 受信データ
     * @param consumer 動作コマンド
     * @return true: 削除する, false: 削除しない
     */
    @Override
    public boolean applyAndIsRemove(ReceiveData receiveData, Consumer<ActionCommand> consumer) {

        // AMRの状態を待つ
        if (!(receiveData instanceof AMRStatus)) {
            return false;
        }

        AMRStatus amrStatus = (AMRStatus) receiveData;
        if (amrStatus.getNeutralState() != 1) {
            // ニュートラル状態でない場合は終了
            logger.info("not Neutral State");
            consumer.accept(new ActionEndCommand());
            return true;
        }

        // ニュートラル状態の場合は移動を指示
        consumer.accept(new ActionSendConveyorCommand(this.reason));
        return true;
    }
}
