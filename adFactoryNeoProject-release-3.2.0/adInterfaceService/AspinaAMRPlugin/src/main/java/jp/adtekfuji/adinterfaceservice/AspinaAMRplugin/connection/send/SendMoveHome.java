package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send;


import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ConnectionUtils;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.SendData;

public class SendMoveHome extends SendData {

    long recession = 0; // 後退
    @Override
    public long getId() {
        return 48;
    }

    /**
     * 送信用データ作成
     *
     * @return 送信要データ
     */
    @Override
    protected byte[] createSendDataImpl() {
        return ConnectionUtils.concat(
                ConnectionUtils.toLittleEndian(recession, 4)
        );
    }
}
