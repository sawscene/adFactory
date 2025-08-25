package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.SendData;

public class SendStop extends SendData {
    @Override
    public long getId() {
        return 45;
    }

    /**
     * 送信用データ作成
     *
     * @return 送信要データ
     */
    @Override
    protected byte[] createSendDataImpl() {
        return null;
    }
}
