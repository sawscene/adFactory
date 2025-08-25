package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.command;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ProcessCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ActionCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.SendData;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send.SendConnectCheck;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.utils.Connector;

import java.io.IOException;
import java.util.Date;
import java.util.function.Consumer;

/**
 * キープアライブコマンド
 */
public class ActionConnectCheckCommand implements ActionCommand {

    final KeepAliveProcessCommand keepAliveProcessCommand;

    public ActionConnectCheckCommand(KeepAliveProcessCommand keepAliveProcessCommand) {
        this.keepAliveProcessCommand = keepAliveProcessCommand;
    }

    /**
     * 送信コマンド実行
     */
    @Override
    public void executeCommand()
    {
    }

    /**
     * 送信コマンド実行
     *
     * @param connector コネクタ
     * @return 接続しつづけるか?
     * @throws IOException 例外
     */
    @Override
    public boolean executeCommand(Connector connector, Consumer<ProcessCommand> consumer) throws IOException {
        if (keepAliveProcessCommand.getReceiveTime() != 0
        || keepAliveProcessCommand.getSendTime() != 0) {
            return true;
        }

        SendData sendData = new SendConnectCheck();
        connector.send(sendData.crateSendData());
        keepAliveProcessCommand.setSendTime(new Date().getTime());
        keepAliveProcessCommand.setRetryCount(keepAliveProcessCommand.getRetryCount()+1);
        return true;
    }
}
