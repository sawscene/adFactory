package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ConnectionUtils;
import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.TreeMap;

@Getter
public class ConnectionReceiveData implements ReceiveData {
    private static final Logger logger = LogManager.getLogger();
    static final public int id = 0;

    enum ConnectionReciveDataEnum {
        ReceiveData // AMR からの応答
    }


    final static TreeMap<ConnectionReciveDataEnum, Integer> sendDataMap
            = new TreeMap<ConnectionReciveDataEnum, Integer>() {{
                put(ConnectionReciveDataEnum.ReceiveData, 4); // AMR からの応答
    }};

    @Getter
    long receiveData; // プログラムポジション番号


    @Override
    public long getId() {
        return id;
    }

    private ConnectionReceiveData() {

    }

    static Optional<ReceiveData> Create(byte[] data) {
        return ConnectionUtils.decomposed(data, sendDataMap)
                .map(elements -> {
                    // 受信データから変換
                    ConnectionReceiveData ret = new ConnectionReceiveData();
                    ret.receiveData = ConnectionUtils.fromLittleEndianToLong(elements.get(ConnectionReciveDataEnum.ReceiveData));
                    logger.info("receive ConnectionReceiveData: {}", ret.receiveData);
                    return ret;
                });
    }
}
