package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.command;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ProcessCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ActionCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.utils.Connector;

import java.io.IOException;
import java.util.function.Consumer;

public class ActionExceptionCommand implements ActionCommand {

    final IOException exception;
    public ActionExceptionCommand(IOException exception) {
        this.exception = exception;
    }

    /**
     * 送信コマンド実行
     */
    @Override
    public void executeCommand() {

    }

    /**
     * 送信コマンド実行
     * @param connector コネクタ
     * @return 接続しつづけるか?
     * @throws IOException 例外
     */
    @Override
    public boolean executeCommand(Connector connector, Consumer<ProcessCommand> consumer) throws IOException {
        throw exception;
    }
}
