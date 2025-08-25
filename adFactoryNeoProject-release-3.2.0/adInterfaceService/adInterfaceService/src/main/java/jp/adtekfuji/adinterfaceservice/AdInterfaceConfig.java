package jp.adtekfuji.adinterfaceservice;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class AdInterfaceConfig {

    private static AdInterfaceConfig instance = null;
    private static final Logger logger = LogManager.getLogger();
    private static final String KEY_SERVER_ADDRESS = "adManagerServiceURI";
    private static final String KEY_FTP_PORT = "ftpPortNumber";
    private static final String KEY_PORT_NUM = "serverPortNum";
    private static final String KEY_CRYPT = "adFactoryCrypt";
    private static final String KEY_CTRL_PORT = "CTRL_PORT";    //上位層との接続ポート
    private static final String KEY_TERM_PORT = "TERM_PORT";    //adInterface <-> adProduct の接続ポート
    private static final String KEY_WEBSOCK_PORT = "WEBSOCK_PORT";
    private final Properties properties;

    /**
     *
     */
    private AdInterfaceConfig() {
        this.properties = AdProperty.getProperties();
    }

    /**
     *
     * @return
     */
    public static AdInterfaceConfig getInstance() {
        if (Objects.isNull(instance)) {
            instance = new AdInterfaceConfig();
        }
        return instance;
    }

    /**
     *
     * @return
     */
    public String getServerAddress() {
        if (!this.properties.containsKey(KEY_SERVER_ADDRESS)) {
            this.properties.setProperty(KEY_SERVER_ADDRESS, "https://localhost/adFactoryServer/rest");
            this.store();
        }
        return this.properties.getProperty(KEY_SERVER_ADDRESS);
    }

    /**
     *
     * @return
     */
    public int getFtpPort() {
        if (!this.properties.containsKey(KEY_FTP_PORT)) {
            this.properties.setProperty(KEY_FTP_PORT, String.valueOf(21));
            this.store();
        }
        return Integer.parseInt(this.properties.getProperty(KEY_FTP_PORT));
    }

    /**
     *
     * @return
     */
    public int getPortNum() {
        if (!this.properties.containsKey(KEY_PORT_NUM)) {
            this.properties.setProperty(KEY_PORT_NUM, String.valueOf(18005));
            this.store();
        }
        return Integer.parseInt(this.properties.getProperty(KEY_PORT_NUM));
    }

    /**
     *
     * @return
     */
    public boolean getCrypt() {
        if (!this.properties.containsKey(KEY_CRYPT)) {
            this.properties.setProperty(KEY_CRYPT, String.valueOf(true));
            this.store();
        }
        return Boolean.parseBoolean(this.properties.getProperty(KEY_CRYPT));
    }

    /**
     *
     * @return
     */
    public int getCtrlPort() {
        if (!this.properties.containsKey(KEY_CTRL_PORT)) {
            this.properties.setProperty(KEY_CTRL_PORT, String.valueOf(18006));
            this.store();
        }
        return Integer.parseInt(this.properties.getProperty(KEY_CTRL_PORT));
    }

    /**
     *
     * @return
     */
    public int getTermPort() {
        if (!this.properties.containsKey(KEY_TERM_PORT)) {
            this.properties.setProperty(KEY_TERM_PORT, String.valueOf(18007));
            this.store();
        }
        return Integer.parseInt(this.properties.getProperty(KEY_TERM_PORT));
    }

    /**
     *
     * @return
     */
    public int getWebSockPort() {
        if (!this.properties.containsKey(KEY_WEBSOCK_PORT)) {
            this.properties.setProperty(KEY_WEBSOCK_PORT, String.valueOf(18008));
            this.store();
        }
        return Integer.parseInt(this.properties.getProperty(KEY_WEBSOCK_PORT));
    }

    /**
     *
     */
    private void store() {
        try {
            AdProperty.store();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }
}
