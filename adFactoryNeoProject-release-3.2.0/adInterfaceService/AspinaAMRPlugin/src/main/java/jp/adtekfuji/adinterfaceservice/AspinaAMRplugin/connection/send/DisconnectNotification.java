package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.SendData;

public class DisconnectNotification extends SendData {
    public DisconnectNotification()
    {}

    @Override
    public long getId() {
        return 1;
    }

    /**
     * 送信用データ作成
     * @return 送信用データ
     */
    @Override
    protected byte[] createSendDataImpl() {
        return null;
    }
}
