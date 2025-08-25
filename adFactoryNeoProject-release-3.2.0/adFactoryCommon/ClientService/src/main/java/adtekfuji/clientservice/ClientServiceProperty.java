/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.property.AdProperty;
import java.util.Properties;

/**
 *
 * @author ke.yokoi
 */
public class ClientServiceProperty {

    private final static String ADMANAGER_SERVICE_KEY = "adManagerServiceURI";
    private final static String ADMANAGER_SERVICE_DEFAULT = "https://localhost/adFactoryServer/rest";
    private static final String SEARCH_MAX_KEY = "search_max";
    private static final String REST_RANGE_NUM_KEY = "rest_range_num";
    private static final String PAGE_LOAD_NUM_KEY = "page_load_num";
    // 接続タイムアウト時間
    public static final String CONNECT_TIMEOUT = "connectTimeout";
    // 電子マニュアル表示サイズ
    public static final String PDOC_HEIGHT = "pdocHeight";
    public static final String PDOC_WIDTH = "pdocWidth";

    public static final String PDOC_ZOOM_RATIO = "pdocZoomRatio";

    private ClientServiceProperty() {
    }

    public static String getServerUri() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(ADMANAGER_SERVICE_KEY)) {
            properties.setProperty(ADMANAGER_SERVICE_KEY, ADMANAGER_SERVICE_DEFAULT);
        }
        return properties.getProperty(ADMANAGER_SERVICE_KEY);
    }

    public static long getSearchMax() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(SEARCH_MAX_KEY)) {
            properties.setProperty(SEARCH_MAX_KEY, String.valueOf(100));
        }
        return Long.parseLong(properties.getProperty(SEARCH_MAX_KEY));
    }

    public static long getRestRangeNum() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(REST_RANGE_NUM_KEY)) {
            properties.setProperty(REST_RANGE_NUM_KEY, String.valueOf(200));
        }
        return Long.parseLong(properties.getProperty(REST_RANGE_NUM_KEY));
    }

    public static long getPageLoadNum() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(PAGE_LOAD_NUM_KEY)) {
            properties.setProperty(PAGE_LOAD_NUM_KEY, String.valueOf(20));
        }
        return Long.parseLong(properties.getProperty(PAGE_LOAD_NUM_KEY));
    }

    /**
     * ライセンスが供与されているかどうかを返す
     *
     * @param optionName
     * @return
     */
    public static boolean isLicensed(String optionName) {
        try {
            Properties properties = AdProperty.getProperties();
            if (properties.containsKey(optionName)) {
                return Boolean.parseBoolean(properties.getProperty(optionName));
            }
        }
        catch (Exception ex) {
        }
        return false;
    }

    /**
     * 接続タイムアウト時間を取得する。
     *
     * @return
     */
    public static int getConnectTimeout() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(CONNECT_TIMEOUT)) {
            properties.setProperty(CONNECT_TIMEOUT, "3000");
        }
        return Integer.parseInt(properties.getProperty(CONNECT_TIMEOUT));
    }
}
