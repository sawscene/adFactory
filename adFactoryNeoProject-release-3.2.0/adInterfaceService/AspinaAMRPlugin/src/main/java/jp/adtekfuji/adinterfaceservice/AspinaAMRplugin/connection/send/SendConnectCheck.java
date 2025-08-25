package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.SendData;

public class SendConnectCheck  extends SendData {
    @Override
    public long getId() {
        return 80;
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
