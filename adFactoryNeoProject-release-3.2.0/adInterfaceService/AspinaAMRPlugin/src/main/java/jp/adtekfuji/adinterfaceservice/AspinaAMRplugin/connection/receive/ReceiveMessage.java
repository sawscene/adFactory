package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.receive;

import jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection.ReceiveData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ReceiveMessage implements ReceiveData {
    private static final Logger logger = LogManager.getLogger();
    static final public int id = 37;

    String message;

    @Override
    public long getId() {
        return id;
    }

    private ReceiveMessage() {

    }

    static Optional<ReceiveData> Create(byte[] data) {

        ReceiveMessage receiveMessage = new ReceiveMessage();
        receiveMessage.message = new String(data);
        logger.info("receive ReceiveMessage: {}", receiveMessage.message);
        return Optional.of(receiveMessage);
    }
}
