package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;

import java.util.function.Consumer;

public interface ProcessCommand{

    /**
     * 受信データを基に動作を決定する。また処理待ちをやめるかを返す。
     * @param receiveData 受信データ
     * @param consumer 動作コマンド
     * @return true: 削除する, false: 削除しない
     */
    boolean applyAndIsRemove(ReceiveData receiveData, Consumer<ActionCommand> consumer);
}
