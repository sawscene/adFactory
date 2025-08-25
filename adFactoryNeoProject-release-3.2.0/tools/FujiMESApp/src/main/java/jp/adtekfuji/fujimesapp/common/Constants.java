package jp.adtekfuji.fujimesapp.common;

/**
 * 定数クラス
 *
 * @author s-heya
 */
public class Constants {
    public static final String BUNDLE_NAME = "jp.adtekfuji.fujimesapp.locale.locale";

    public static final String PROPETRY_NAME = "FujiMESApp";
    public static final String PROPERTIES_EXT = ".properties";
    
    public static final String KEY_ADFACTORY_URL = "adFactoryURL";
    public static final String DEF_ADFACTORY_URL = "https://localhost/adFactoryServer/rest";
   
    public static final String KEY_FUJIMES_URL = "FujiMESURL";
    public static final String DEF_FUJIMES_URL = "https://api.mes.fuji.co.jp/test";
   
    public static final String KEY_CONNECT_TIMEOUT = "connectTimeout";
    public static final String DEF_CONNECT_TIMEOUT = "3000";

    public static final String KEY_RETRY_MAX = "retryMax";
    public static final String DEF_RETRY_MAX = "3";

    public static final String KEY_FETCH_MAX = "fetchMax";
    public static final String DEF_FETCH_MAX = "20";
}
