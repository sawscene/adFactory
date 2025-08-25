package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ReceiveConnectCheck  implements ReceiveData {
    private static final Logger logger = LogManager.getLogger();
    static final public int id = 65;

    @Override
    public long getId() {
        return id;
    }

    private ReceiveConnectCheck() {

    }

    static Optional<ReceiveData> Create(byte[] data) {
        logger.info("receive ReceiveConnectCheck");
        return Optional.of(new ReceiveConnectCheck());
    }
}
