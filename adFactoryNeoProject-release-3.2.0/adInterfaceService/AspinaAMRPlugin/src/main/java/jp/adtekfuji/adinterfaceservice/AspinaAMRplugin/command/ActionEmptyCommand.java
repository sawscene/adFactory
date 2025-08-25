package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.command;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ProcessCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.ActionCommand;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.utils.Connector;

import java.util.function.Consumer;

/**
 * 空の処理
 */
public class ActionEmptyCommand implements ActionCommand {
    @Override
    public void executeCommand()
    {
    }

    public boolean executeCommand(Connector connector, Consumer<ProcessCommand> consumer) {
        return true;
    }
}
