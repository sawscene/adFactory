package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class InvalidReceiveData implements ReceiveData {
    private static final Logger logger = LogManager.getLogger();
    public static final int id = -1;
    @Override
    public long getId() {
        return id;
    }

    private InvalidReceiveData() {}

    static Optional<ReceiveData> Create() {
        logger.info("receive InvalidReceiveData");
        return Optional.of(new InvalidReceiveData());
    }
}
