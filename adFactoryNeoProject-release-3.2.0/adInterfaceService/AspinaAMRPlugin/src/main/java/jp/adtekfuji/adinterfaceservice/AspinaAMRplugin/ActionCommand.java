package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.utils.Connector;

import java.io.IOException;
import java.util.function.Consumer;

public interface ActionCommand {

    /**
     * 送信コマンド実行
     */
    void executeCommand();

    /**
     * 送信コマンド実行
     * @param connector コネクタ
     * @return 接続しつづけるか?
     * @throws IOException 例外
     */
    boolean executeCommand(Connector connector, Consumer<ProcessCommand> consumer) throws IOException;
}
