/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.common;

import adtekfuji.property.AdProperty;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 設定データの管理クラス
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 */
public class AdFactoryForFujiServerConfig {

    private static final Logger logger = LogManager.getLogger();
    private Properties properties;

    public AdFactoryForFujiServerConfig() {
        try {
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load(ServerPropertyConstants.ADFACTORY_FOR_FUJI_PROPERTY_FILE);
            properties = AdProperty.getProperties();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * adFactory標準サーバパスの取得
     *
     * @return サーバパス
     */
    public String getAdFactoryServerAddress() {
        if (!properties.containsKey(ServerPropertyConstants.ADFACTORY_SERVICE_URI)) {
            properties.setProperty(ServerPropertyConstants.ADFACTORY_SERVICE_URI, "http\\://localhost:8080/adFactoryServer/rest");
            store();
        }
        return properties.getProperty(ServerPropertyConstants.ADFACTORY_SERVICE_URI);
    }

    /**
     * adFactoryInterFaceのサーバパスの取得
     *
     * @return サーバパス
     */
    public String getInterfaceServiceAddress() {
        if (!properties.containsKey(ServerPropertyConstants.KEY_SERVICE_PATH)) {
            properties.setProperty(ServerPropertyConstants.KEY_SERVICE_PATH, "127.0.0.1");
            store();
        }
        return properties.getProperty(ServerPropertyConstants.KEY_SERVICE_PATH);
    }

    /**
     * adFactoryInterFaceのポート情報の取得
     *
     * @return ポート番号
     */
    public int getInterfaceServicePortNum() {
        if (!properties.containsKey(ServerPropertyConstants.KEY_PORT_NUM)) {
            properties.setProperty(ServerPropertyConstants.KEY_PORT_NUM, String.valueOf(18005));
            store();
        }
        return Integer.parseInt(properties.getProperty(ServerPropertyConstants.KEY_PORT_NUM));
    }

    /**
     * adFactoryInterFaceのクリプト(復号データ)の取得
     *
     * @return クリプト
     */
    public boolean getInterfaceServiceCrypt() {
        if (!properties.containsKey(ServerPropertyConstants.KEY_CRYPT)) {
            properties.setProperty(ServerPropertyConstants.KEY_CRYPT, String.valueOf(true));
            store();
        }
        return Boolean.parseBoolean(properties.getProperty(ServerPropertyConstants.KEY_CRYPT));
    }

    /**
     * adFactoryInterFaceの再接続時間の設定値を取得
     *
     * @return 再接続時間
     */
    public int getInterfaceServiceReconnectTime() {
        if (!properties.containsKey(ServerPropertyConstants.KEY_RECONNECT_TIMER)) {
            properties.setProperty(ServerPropertyConstants.KEY_RECONNECT_TIMER, String.valueOf(5));
            store();
        }
        return Integer.parseInt(properties.getProperty(ServerPropertyConstants.KEY_RECONNECT_TIMER));
    }

    /**
     * 設定ファイルの書き込み処理
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
