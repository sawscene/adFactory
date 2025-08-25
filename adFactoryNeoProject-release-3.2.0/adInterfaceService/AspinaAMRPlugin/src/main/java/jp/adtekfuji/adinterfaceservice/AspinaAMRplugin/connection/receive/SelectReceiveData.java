package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class SelectReceiveData {
    private static final Logger logger = LogManager.getLogger();

    /**
     * IDを基に受信データを解析する。
     * @param id 受信データID
     * @param data 受信 byte データ
     * @return 変換したデータ送信データ
     */
    static public Optional<ReceiveData> decodeReceiveData(long id, byte[] data) {
        if (id == 48) {
            return Optional.empty();
        }
        logger.info("****** <---- AMR (ID: {})", id);
        switch((int) id) {
            case ConnectionReceiveData.id:
                return ConnectionReceiveData.Create(data);
            case ReceiveConnectCheck.id:
                return ReceiveConnectCheck.Create(data);
            case AMRStatus.id:
                return AMRStatus.Create(data);
            case ProgramProcessState.id:
                return ProgramProcessState.Create(data);
            case ReceiveMessage.id:
                return ReceiveMessage.Create(data);
            case BatteryInfo.id:
                return BatteryInfo.Create(data);
            case InvalidReceiveData.id:
            default:
                return InvalidReceiveData.Create();
        }
    }

}
