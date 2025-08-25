/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.common;

import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.util.Objects;
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
public class AdFactoryForFujiClientAppConfig {

    private static final Logger logger = LogManager.getLogger();

    /**
     * adFactory標準サーバパスの取得
     *
     * @return サーバパス
     */
    public String getAdFactoryForFujiServerAddress() {
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        if (!properties.containsKey(ClientPropertyConstants.ADFACTORY_FOR_FUJI_SERVICE_URI)) {
            properties.setProperty(ClientPropertyConstants.ADFACTORY_FOR_FUJI_SERVICE_URI, "http://localhost:8080/adFactoryForFujiServer/rest");
            store();
        }
        return properties.getProperty(ClientPropertyConstants.ADFACTORY_FOR_FUJI_SERVICE_URI);
    }

    /**
     * adFactoryInterFaceのサーバパスの取得
     *
     * @return サーバパス
     */
    public String getInterfaceServiceAddress() {
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        if (!properties.containsKey(ClientPropertyConstants.KEY_SERVICE_PATH)) {
            properties.setProperty(ClientPropertyConstants.KEY_SERVICE_PATH, "127.0.0.1");
            store();
        }
        return properties.getProperty(ClientPropertyConstants.KEY_SERVICE_PATH);
    }

    /**
     * adFactoryInterFaceのポート情報の取得
     *
     * @return ポート番号
     */
    public int getInterfaceServicePortNum() {
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        if (!properties.containsKey(ClientPropertyConstants.KEY_PORT_NUM)) {
            properties.setProperty(ClientPropertyConstants.KEY_PORT_NUM, String.valueOf(18005));
            store();
        }
        return Integer.parseInt(properties.getProperty(ClientPropertyConstants.KEY_PORT_NUM));
    }

    /**
     * adFactoryInterFaceのクリプト(復号データ)の取得
     *
     * @return クリプト
     */
    public boolean getInterfaceServiceCrypt() {
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        if (!properties.containsKey(ClientPropertyConstants.KEY_CRYPT)) {
            properties.setProperty(ClientPropertyConstants.KEY_CRYPT, String.valueOf(true));
            store();
        }
        return Boolean.parseBoolean(properties.getProperty(ClientPropertyConstants.KEY_CRYPT));
    }

    /**
     * adFactoryInterFaceの再接続時間の設定値を取得
     *
     * @return 再接続時間
     */
    public int getInterfaceServiceReconnectTime() {
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        if (!properties.containsKey(ClientPropertyConstants.KEY_RECONNECT_TIMER)) {
            properties = AdProperty.getProperties();
            properties.put(ClientPropertyConstants.KEY_RECONNECT_TIMER, String.valueOf(5));
            store();
        }
        return Integer.parseInt(properties.getProperty(ClientPropertyConstants.KEY_RECONNECT_TIMER));
    }

    /**
     * 最後に開いたユニットテンプレート階層IDを読み込み
     *
     * @return ID
     */
    public Long getSelectedUnitTemplateHierarchy() {
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        if (!properties.containsKey(ClientPropertyConstants.KEY_SELECT_UNIT_TEMPLATE_HIERARCHY)) {
            properties.setProperty(ClientPropertyConstants.KEY_SELECT_UNIT_TEMPLATE_HIERARCHY, String.valueOf(EntityConstants.ROOT_ID));
            store();
        }
        return Long.parseLong(properties.getProperty(ClientPropertyConstants.KEY_SELECT_UNIT_TEMPLATE_HIERARCHY));
    }

    /**
     * 最後に開いたユニットテンプレート階層IDを書き込み
     *
     * @param hierarchyId 階層ID
     */
    public void setSelectedUnitTemplateHierarchy(Long hierarchyId) {
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        if (Objects.nonNull(hierarchyId)) {
            properties.setProperty(ClientPropertyConstants.KEY_SELECT_UNIT_TEMPLATE_HIERARCHY, String.valueOf(hierarchyId));
            store();
        } else {
            properties.setProperty(ClientPropertyConstants.KEY_SELECT_UNIT_TEMPLATE_HIERARCHY, String.valueOf(EntityConstants.ROOT_ID));
            store();
        }
    }

    /**
     * 最後に開いたユニット階層IDを読み込み
     *
     * @return ID
     */
    public Long getSelectedUnitHierarchy() {
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        if (!properties.containsKey(ClientPropertyConstants.KEY_SELECT_UNIT_HIERARCHY)) {
            properties.setProperty(ClientPropertyConstants.KEY_SELECT_UNIT_HIERARCHY, String.valueOf(EntityConstants.ROOT_ID));
            store();
        }
        return Long.parseLong(properties.getProperty(ClientPropertyConstants.KEY_SELECT_UNIT_HIERARCHY));
    }

    /**
     * 最後に開いたユニット階層IDを書き込み
     *
     * @param hierarchyId 階層ID
     */
    public void setSelectedUnitHierarchy(Long hierarchyId) {
        Properties properties = AdProperty.getProperties(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        if (Objects.nonNull(hierarchyId)) {
            properties.setProperty(ClientPropertyConstants.KEY_SELECT_UNIT_HIERARCHY, String.valueOf(hierarchyId));
            store();
        } else {
            properties.setProperty(ClientPropertyConstants.KEY_SELECT_UNIT_HIERARCHY, String.valueOf(EntityConstants.ROOT_ID));
            store();
        }
    }

    /**
     * 設定ファイルの書き込み処理
     *
     */
    private void store() {
        try {
            AdProperty.store(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }
}
