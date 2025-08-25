package jp.adtekfuji.adinterfaceservice.AspinaAMRplugin.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 送信データ
 */
public abstract class SendData {
    private static final Logger logger = LogManager.getLogger();

    public abstract long getId();

    /**
     * 送信用データ作成
     * @return 送信要データ
     */
    protected abstract byte[] createSendDataImpl();

    /**
     * 送信用データ作成
     * @return 送信用データ
     */
    public byte[] crateSendData() {
        logger.info("****** ----> AMR (ID: {})", getId());
        return ConnectionUtils.createSendData(getId(), createSendDataImpl());
    }

}
