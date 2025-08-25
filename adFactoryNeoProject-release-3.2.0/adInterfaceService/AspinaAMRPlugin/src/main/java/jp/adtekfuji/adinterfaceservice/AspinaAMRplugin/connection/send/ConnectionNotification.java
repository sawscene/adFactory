package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ConnectionUtils;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.SendData;

/**
 * 接続初期化通知
 */
public class ConnectionNotification extends SendData {
    // 2Dスキャナ有効 (0:2Dスキャナを起動しません(AMRの通常動作不可), 1:2Dスキャナを起動します)
    final long scanner2D;
    // 3Dスキャナ有効 (0:3Dスキャナを起動しません(AMRの通常動作不可), 1:3Dスキャナを起動します)
    final long scanner3D;
    public ConnectionNotification(int scanner2D, int scanner3D) {
        this.scanner2D = scanner2D;
        this.scanner3D = scanner3D;
    }

    @Override
    public long getId() {
        return 0;
    }

    /**
     * 送信用データ作成
     * @return 送信用データ
     */
    @Override
    protected byte[] createSendDataImpl() {
        return ConnectionUtils.concat(
                ConnectionUtils.toLittleEndian(this.scanner2D, 4),
                ConnectionUtils.toLittleEndian(this.scanner3D, 4)
        );
    }
}
