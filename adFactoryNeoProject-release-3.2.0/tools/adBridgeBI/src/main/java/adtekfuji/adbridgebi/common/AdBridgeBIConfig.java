/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.common;

import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 設定データ
 *
 * @author nar-nakamura
 */
public class AdBridgeBIConfig {

    private static final Logger logger = LogManager.getLogger();
    private final Properties properties;

    private static AdBridgeBIConfig instance = null;

    /**
     * 設定データのインスタンスを取得する。
     *
     * @return 設定データ
     */
    public static AdBridgeBIConfig getInstance() {
        if (Objects.isNull(instance)) {
            instance = new AdBridgeBIConfig();
        }
        return instance;
    }

    /**
     * コンストラクタ
     */
    private AdBridgeBIConfig() {
        properties = AdProperty.getProperties();
    }

    /**
     * 更新間隔(分)を取得する。
     *
     * @return 更新間隔(分)
     */
    public int getUpdateInterval() {
        if (!properties.containsKey(Constants.UPDATE_INTERVAL)) {
            properties.setProperty(Constants.UPDATE_INTERVAL, Constants.UPDATE_INTERVAL_DEFAULT);
            store();
        }
        return Integer.parseInt(properties.getProperty(Constants.UPDATE_INTERVAL));
    }

    /**
     * サーバーアドレスを取得する。
     *
     * @return サーバーアドレス
     */
    public String getServerAddress() {
        if (!properties.containsKey(Constants.SERVER_ADDRESS)) {
            properties.setProperty(Constants.SERVER_ADDRESS, Constants.SERVER_ADDRESS_DEFAULT);
            store();
        }
        return properties.getProperty(Constants.SERVER_ADDRESS);
    }

    /**
     * 完了予定日のプロパティ名を取得する。
     *
     * @return 完了予定日のプロパティ名
     */
    public String getPropDeadLine() {
        if (!properties.containsKey(Constants.PROP_DEAD_LINE)) {
            properties.setProperty(Constants.PROP_DEAD_LINE, Constants.PROP_DEAD_LINE_DEFAULT);
            store();
        }
        return properties.getProperty(Constants.PROP_DEAD_LINE);
    }

    /**
     * 予備日数のプロパティ名を取得する。
     *
     * @return 完了予定日のプロパティ名
     */
    public String getPropReserveDays() {
        if (!properties.containsKey(Constants.PROP_RESERVE_DAYS)) {
            properties.setProperty(Constants.PROP_RESERVE_DAYS, Constants.PROP_RESERVE_DAYS_DEFAULT);
            store();
        }
        return properties.getProperty(Constants.PROP_RESERVE_DAYS);
    }

    /**
     * グループ名のプロパティ名を取得する。
     * 
     * @return 
     */
    public String getPropGroupNo() {
        if (!properties.containsKey(Constants.PROP_GROUP_NO)) {
            properties.setProperty(Constants.PROP_GROUP_NO, Constants.PROP_GROUP_NO_DEFAULT);
            store();
        }
        return properties.getProperty(Constants.PROP_GROUP_NO);
    }

    /**
     * 追加情報1のプロパティ名を取得する。
     * 
     * @return 
     */
    public String getPropInfo1() {
        if (!properties.containsKey(Constants.PROP_INFO1)) {
            properties.setProperty(Constants.PROP_INFO1, Constants.PROP_INFO1_DEFAULT);
            store();
        }
        return properties.getProperty(Constants.PROP_INFO1);
    }

    /**
     * 追加情報2のプロパティ名を取得する。
     * 
     * @return 
     */
    public String getPropInfo2() {
        if (!properties.containsKey(Constants.PROP_INFO2)) {
            properties.setProperty(Constants.PROP_INFO2, Constants.PROP_INFO2_DEFAULT);
            store();
        }
        return properties.getProperty(Constants.PROP_INFO2);
    }

    /**
     * 休憩時間名を取得する。
     *
     * @return 休憩時間名(カンマ区切りで列挙)　※.例「休憩1,休憩2,休憩3」
     */
    public String getBreaktimeNames() {
        if (!properties.containsKey(Constants.BREAKTIME_NAMES)) {
            properties.setProperty(Constants.BREAKTIME_NAMES, Constants.BREAKTIME_NAMES_DEFAULT);
            store();
        }
        return properties.getProperty(Constants.BREAKTIME_NAMES);
    }

    /**
     * 設定ファイルを更新する。
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
