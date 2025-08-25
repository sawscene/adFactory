package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.command;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ProcessCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ActionCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.utils.Connector;

import java.util.function.Consumer;

/**
 * 終了処理
 */
public class ActionEndCommand implements ActionCommand {

    /**
     * 送信コマンド実行
     */
    @Override
    public void executeCommand()
    {
    }

    /**
     * 反映 & 削除するか?
     * @param receiveData 受信データ
     * @param consumer 送信用コンシューマ
     * @return 削除するか?
     */
    @Override
    public boolean executeCommand(Connector connector, Consumer<ProcessCommand> consumer) {
        return false;
    }
}
