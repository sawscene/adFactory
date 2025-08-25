package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.send;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ConnectionUtils;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.SendData;


/**
 * プログラム搬送開始
 */
public class SendConveyProgram extends SendData {
    final int programNo;
    final long targetProgram;
    public SendConveyProgram(int programNo, long targetProgram)
    {
        this.programNo = programNo;
        this.targetProgram = targetProgram;
    }

    @Override
    public long getId() {
        return 44;
    }

    /**
     * 送信用データ作成
     * @return 送信用データ
     */
    @Override
    protected byte[] createSendDataImpl()
    {
        return ConnectionUtils.concat(
                ConnectionUtils.toLittleEndian(programNo, 4),
                ConnectionUtils.toLittleEndian(targetProgram, 4)
        );
    }
}
