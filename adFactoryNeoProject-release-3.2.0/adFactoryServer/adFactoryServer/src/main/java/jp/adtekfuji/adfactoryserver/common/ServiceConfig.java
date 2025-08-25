/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.common;

import adtekfuji.property.AdProperty;
import adtekfuji.utility.CipherHelper;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * adFactoryServer 設定
 *
 * @author nar-nakamura
 */
public class ServiceConfig {

    private static final Logger logger = LogManager.getLogger();

    // adInterface IPアドレス
    private static final String SERVICE_ADDRESS_KEY = "adInterfaceServiceAddress";
    private static final String SERVICE_ADDRESS_DEF = "127.0.0.1";

    // adInterface 通信ポート
    private static final String SERVICE_PORT_NUM_KEY = "adInterfaceServicePortNum";
    private static final int SERVICE_PORT_NUM_DEF = 18005;

    // adInterface 再接続時間
    private static final String SERVICE_RECONNECT_TIME_KEY = "adInterfaceServiceReConnectTime";
    private static final int SERVICE_RECONNECT_TIME_DEF = 5;

    // 
    private static final String SERVICE_CRYPT_KEY = "adFactoryCrypt";
    private static final boolean SERVICE_CRYPT_DEF = true;

    // 作業日報の中断時間表示 (true:表示する, false:表示しない)
    private static final String WORK_REPORT_SUSPEND_ENABLED_KEY = "workReportSuspendEnabled";
    private static final boolean WORK_REPORT_SUSPEND_ENABLED_DEF = false;

    // 倉庫案内 現品票(支給品)のQRコード 正規表現文字列 (オペレーションNo:E1)
    private static final String WH_PATTERN_E1 = "wh_patternE1";
    private static final String WH_PATTERN_E1_DEF = "^.{47}.{30}\\d{6}GS\\d{7}\\d{7}.*";
    
    // 倉庫案内 製造指示書のQRコード 正規表現文字列 (オペレーションNo:E5)
    private static final String WH_PATTERN_E5 = "wh_patternE5";
    private static final String WH_PATTERN_E5_DEF = "^.+\\s.+\\s.+\\s.+\\s\\d+$";    
    
    // 倉庫案内 現品票のQRコード 正規表現文字列 (オペレーションNo:E6)
    private static final String WH_PATTERN_E6 = "wh_patternE6";
    private static final String WH_PATTERN_E6_DEF = "^.+\\s.+\\s.+\\s.+\\s\\d+\\s.+$";    

    // 倉庫案内 現品票(購入品)のQRコード 正規表現文字列 (オペレーションNo:E3)
    private static final String WH_PATTERN_E3 = "wh_patternE3";
    private static final String WH_PATTERN_E3_DEF = "^.+\\s.+\\s.+\\s\\d+$";

    // 倉庫案内 払出指示書のQRコード 正規表現文字列 (オペレーションNo:L2)
    private static final String WH_PATTERN_L2 = "wh_patternL2";
    private static final String WH_PATTERN_L2_DEF = "^.+\\s.+\\s.+\\s.+\\s\\d+$";

    // 倉庫案内 現品票(納品書) GSオーダー開始桁数
    private static final String WH_GS_ORDER_POS = "wh_gsOrderPos";
    private static final Integer WH_GS_ORDER_POS_DEF = 84;
    
    // 倉庫案内 GSオーダー桁数
    private static final String WH_GS_ORDER_LENGTH = "wh_gsOrderLength";
    private static final Integer WH_GS_ORDER_LENGTH_DEF = 9;

    // 倉庫案内 ラベル印刷ベースURL(URLスキーム)
    private static final String WH_PRINT_URL_SCHEME = "wh_printUrlScheme";
    private static final String WH_PRINT_URL_SCHEME_DEF = "http";

    // 倉庫案内 ラベル印刷ベースURL(FQDN)
    private static final String WH_PRINT_URL_FQDN = "wh_printUrlFqdn";
    private static final String WH_PRINT_URL_FQDN_DEF = "localhost:8080";

    // 倉庫案内 ラベル印刷ベースURL(Baseパス)
    private static final String WH_PRINT_URL_BASE = "wh_printUrlBase";
    private static final String WH_PRINT_URL_BASE_DEF = "";

    // 倉庫案内 表示アイテム情報
    private static final String WH_MATERIAL_ITEMS = "wh_materialItems";
    private static final String WH_MATERIAL_ITEMS_DEF = "{\"ProductNo\":\"品目\",\"ProductName\":\"品名\"}";

    // 倉庫案内 加工品を入庫対象とするかどうか (入庫画面に入庫数フィールドを表示)
    private static final String WH_ENABLE_SEMI_PRODUCTS = "wh_enableSemiProducts";
    private static final String WH_ENABLE_SEMI_PRODUCTS_DEF = "false";
    
    // 倉庫案内 現品ラベルのレイアウト番号
    private static final String WH_PRINT_FORMAT = "wh_printFormat";
    private static final String WH_PRINT_FORMAT_DEF = "1";

    // 倉庫案内 棚卸ラベルのレイアウト番号
    private static final String WH_INVENTORY_LABEL_FORMAT = "wh_inventoryLabelFormat";
    private static final String WH_INVENTORY_LABEL_FORMAT_DEF = "2";

    // 受入画面にて納品書を照合するかどうか (受入画面に納品書フォームを表示)
    private static final String WH_ENABLE_STATEMENT = "wh_enableStatement";
    private static final String WH_ENABLE_STATEMENT_DEF = "false";

    // 資材情報の保持月数(-1:期限なし, 0～:月数)
    private static final String WH_MATERIAL_DELETE_MONTH = "wh_materialDeleteMonth";
    private static final int WH_MATERIAL_DELETE_MONTH_DEF = -1;

    // LDAP認証先URL
    private static final String LDAP_PROVIDER_URL_KEY = "ldapProviderURL";
    private static final String LDAP_PROVIDER_URL_KEY_DEF = "";
    
    // LDAP認証ドメイン名
    private static final String LDAP_DOMAIN_KEY = "ldapDomain";
    private static final String LDAP_DOMAIN_KEY_DEF = "";

    // 部品情報インポートで使用する共有ディレクトリのパス
    private static final String IMPORT_PARTS_KEY = "importParts";
    private static final String IMPORT_PARTS_DEF = "C:\\adFactory_IN";

    // ポーリング間隔(分)
    private static final String POLLING_INTERVAL_KEY = "pollingInterval";
    private static final long POLLING_INTERVAL_DEF = 3;

    /**
     * メールサーバのホスト名 又は、IPアドレスの設定値取得キー
     */
    private static final String SMTP_SERVER_KEY = "smtpServer";

    /**
     * メールサーバのホスト名 又は、IPアドレスの設定値のデフォルト値
     */
    private static final String SMTP_SERVER_DEF = "localhost";

    /**
     * ポート番号の設定値取得キー
     */
    private static final String SMTP_PORT_KEY = "smtpPort";

    /**
     * ポート番号の設定値のデフォルト値
     */
    private static final int SMTP_PORT_DEF = 587;

    /**
     * ユーザ名の設定値取得キー
     */
    private static final String SMTP_USER_KEY = "smtpUser";

    /**
     * ユーザ名の設定値のデフォルト値
     */
    private static final String SMTP_USER_DEF = "adtek";

    /**
     * パスワードの設定値取得キー
     */
    private static final String SMTP_PASSWORD_KEY = "smtpPassword";

    /**
     * パスワードの設定値のデフォルト値
     */
    private static final String SMTP_PASSWORD_DEF = "adtek";

    private static ServiceConfig instance = null;

    private Properties properties;

    private AdProperty adProperty;

    /**
     * インスタンスを取得する。
     *
     * @return
     */
    public static ServiceConfig getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ServiceConfig();
        }
        return instance;
    }

    /**
     * コンストラクタ
     */
    public ServiceConfig() {
        try {
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adFactoryService.properties");
            properties = AdProperty.getProperties();

        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * adInterface IPアドレスを取得する。
     *
     * @return adInterface IPアドレス
     */
    public String getServiceAddress() {
        try {
            if (!properties.containsKey(SERVICE_ADDRESS_KEY)) {
                properties.setProperty(SERVICE_ADDRESS_KEY, String.valueOf(SERVICE_ADDRESS_DEF));
                store();
            } else if (properties.getProperty(SERVICE_ADDRESS_KEY).isEmpty()) {
                return SERVICE_ADDRESS_DEF;
            }
            return properties.getProperty(SERVICE_ADDRESS_KEY);
        } catch (Exception ex) {
            return SERVICE_ADDRESS_KEY;
        }
    }

    /**
     * adInterface 通信ポートを取得する。
     *
     * @return adInterface 通信ポート
     */
    public int getServicePortNum() {
        try {
            if (!properties.containsKey(SERVICE_PORT_NUM_KEY)) {
                properties.setProperty(SERVICE_PORT_NUM_KEY, String.valueOf(SERVICE_PORT_NUM_DEF));
                store();
            } else if (properties.getProperty(SERVICE_PORT_NUM_KEY).isEmpty()) {
                return SERVICE_PORT_NUM_DEF;
            }
            return Integer.parseInt(properties.getProperty(SERVICE_PORT_NUM_KEY));
        } catch (Exception ex) {
            return SERVICE_PORT_NUM_DEF;
        }
    }

    /**
     * adInterface 再接続時間を取得する。
     *
     * @return adInterface の再接続時間
     */
    public int getServiceReconnectTime() {
        try {
            if (!properties.containsKey(SERVICE_RECONNECT_TIME_KEY)) {
                properties.setProperty(SERVICE_RECONNECT_TIME_KEY, String.valueOf(SERVICE_RECONNECT_TIME_DEF));
                store();
            } else if (properties.getProperty(SERVICE_RECONNECT_TIME_KEY).isEmpty()) {
                return SERVICE_PORT_NUM_DEF;
            }
            return Integer.parseInt(properties.getProperty(SERVICE_RECONNECT_TIME_KEY));
        } catch (Exception ex) {
            return SERVICE_RECONNECT_TIME_DEF;
        }
    }

    /**
     * adInterface 通信にSSLを使用するか取得する。
     *
     * @return adInterface 通信にSSLを使用するか (true:使用する, false:使用しない)
     */
    public boolean getServiceCrypt() {
        try {
            if (!properties.containsKey(SERVICE_CRYPT_KEY)) {
                properties.setProperty(SERVICE_CRYPT_KEY, String.valueOf(SERVICE_CRYPT_DEF));
                store();
            } else if (properties.getProperty(SERVICE_CRYPT_KEY).isEmpty()) {
                return SERVICE_CRYPT_DEF;
            }
            return Boolean.parseBoolean(properties.getProperty(SERVICE_CRYPT_KEY));
        } catch (Exception ex) {
            return SERVICE_CRYPT_DEF;
        }
    }

    /**
     * 作業日報に中断時間を表示するか取得する。
     *
     * @return 作業日報に中断時間表示するか (true:表示する, false:表示しない)
     */
    public boolean getWorkReportSuspendEnabled() {
        try {
            if (!properties.containsKey(WORK_REPORT_SUSPEND_ENABLED_KEY)) {
                properties.setProperty(WORK_REPORT_SUSPEND_ENABLED_KEY, String.valueOf(WORK_REPORT_SUSPEND_ENABLED_DEF));
                store();
            } else if (properties.getProperty(WORK_REPORT_SUSPEND_ENABLED_KEY).isEmpty()) {
                return WORK_REPORT_SUSPEND_ENABLED_DEF;
            }
            return Boolean.parseBoolean(properties.getProperty(WORK_REPORT_SUSPEND_ENABLED_KEY));
        } catch (Exception ex) {
            return WORK_REPORT_SUSPEND_ENABLED_DEF;
        }
    }

    /**
     * 現品票(支給品)のQRコード 正規表現文字列を取得する。(オペレーションNo:E1)
     *
     * @return 正規表現文字列
     */
    public String getPatternE1() {
        try {
            if (!properties.containsKey(WH_PATTERN_E1)) {
                properties.setProperty(WH_PATTERN_E1, String.valueOf(WH_PATTERN_E1_DEF));
                //store();
            } else if (properties.getProperty(WH_PATTERN_E1).isEmpty()) {
                return WH_PATTERN_E1_DEF;
            }
            return properties.getProperty(WH_PATTERN_E1);
        } catch (Exception ex) {
            return WH_PATTERN_E1_DEF;
        }
    }
    
    /**
     * 現品票(購入品)のQRコード 正規表現文字列を取得する。(オペレーションNo:E3)
     * 
     * @return 正規表現文字列
     */
    public String getPatternE3() {
        try {
            if (!properties.containsKey(WH_PATTERN_E3)) {
                properties.setProperty(WH_PATTERN_E3, String.valueOf(WH_PATTERN_E3_DEF));
            } else if (StringUtils.isEmpty(properties.getProperty(WH_PATTERN_E3))) {
                return WH_PATTERN_E3_DEF;
            }
            return properties.getProperty(WH_PATTERN_E3);
        } catch (Exception ex) {
            return WH_PATTERN_E3_DEF;
        }
    }
    
    /**
     * 製造指示書のQRコード 正規表現文字列を取得する。(オペレーションNo:E5)
     *
     * @return 正規表現文字列
     */
    public String getPatternE5() {
        try {
            if (!properties.containsKey(WH_PATTERN_E5)) {
                properties.setProperty(WH_PATTERN_E5, String.valueOf(WH_PATTERN_E5_DEF));
                //store();
            } else if (properties.getProperty(WH_PATTERN_E5).isEmpty()) {
                return WH_PATTERN_E5_DEF;
            }
            return properties.getProperty(WH_PATTERN_E5);
        } catch (Exception ex) {
            return WH_PATTERN_E5_DEF;
        }
    }
    
    /**
     * 現品票のQRコード 正規表現文字列を取得する。(オペレーションNo:E6)
     * 
     * @return 正規表現文字列
     */
    public String getPatternE6() {
        try {
            if (!properties.containsKey(WH_PATTERN_E6)) {
                properties.setProperty(WH_PATTERN_E6, String.valueOf(WH_PATTERN_E6_DEF));
            } else if (properties.getProperty(WH_PATTERN_E6).isEmpty()) {
                return WH_PATTERN_E6_DEF;
            }
            return properties.getProperty(WH_PATTERN_E6);
        } catch (Exception ex) {
            return WH_PATTERN_E6_DEF;
        }
    }

    /**
     * 払出指示書のQRコード 正規表現文字列を取得する。(オペレーションNo:L2)
     * 
     * @return 正規表現文字列
     */
    public String getPatternL2() {
        try {
            if (!properties.containsKey(WH_PATTERN_L2)) {
                properties.setProperty(WH_PATTERN_L2, String.valueOf(WH_PATTERN_L2_DEF));
            } else if (StringUtils.isEmpty(properties.getProperty(WH_PATTERN_L2))) {
                return WH_PATTERN_L2_DEF;
            }
            return properties.getProperty(WH_PATTERN_L2);
        } catch (Exception ex) {
            return WH_PATTERN_L2_DEF;
        }
    }
    
    /**
     * GSオーダー番号の開始位置を取得する。
     *
     * @return GSオーダー番号の開始位置
     */
    public int getGsOrderPos() {
        try {
            if (!properties.containsKey(WH_GS_ORDER_POS)) {
                properties.setProperty(WH_GS_ORDER_POS, String.valueOf(WH_GS_ORDER_POS_DEF));
                store();
            } else if (properties.getProperty(WH_GS_ORDER_POS).isEmpty()) {
                return WH_GS_ORDER_POS_DEF;
            }
            return Integer.parseInt(properties.getProperty(WH_GS_ORDER_POS));
        } catch (Exception ex) {
            return WH_GS_ORDER_POS_DEF;
        }
    }
    
    /**
     * GSオーダー番号の桁数を取得する。
     *
     * @return GSオーダー番号の桁数
     */
    public int getGsOrderLength() {
        try {
            if (!properties.containsKey(WH_GS_ORDER_LENGTH)) {
                properties.setProperty(WH_GS_ORDER_LENGTH, String.valueOf(WH_GS_ORDER_LENGTH_DEF));
                store();
            } else if (properties.getProperty(WH_GS_ORDER_LENGTH).isEmpty()) {
                return WH_GS_ORDER_LENGTH_DEF;
            }
            return Integer.parseInt(properties.getProperty(WH_GS_ORDER_LENGTH));
        } catch (Exception ex) {
            return WH_GS_ORDER_LENGTH_DEF;
        }
    }

    /**
     * ラベル印刷ベースURL(URLスキーム)を取得する。
     *
     * @return
     */
    public String getPrintUrlScheme() {
        try {
            if (!properties.containsKey(WH_PRINT_URL_SCHEME)) {
                properties.setProperty(WH_PRINT_URL_SCHEME, String.valueOf(WH_PRINT_URL_SCHEME_DEF));
                store();
            } else if (properties.getProperty(WH_PRINT_URL_SCHEME).isEmpty()) {
                return WH_PRINT_URL_SCHEME_DEF;
            }
            return properties.getProperty(WH_PRINT_URL_SCHEME);
        } catch (Exception ex) {
            return WH_PRINT_URL_SCHEME_DEF;
        }
    }
    
    /**
     * ラベル印刷ベースURL(FQDN)を取得する。
     *
     * @return
     */
    public String getPrintUrlFqdn() {
        try {
            if (!properties.containsKey(WH_PRINT_URL_FQDN)) {
                properties.setProperty(WH_PRINT_URL_FQDN, String.valueOf(WH_PRINT_URL_FQDN_DEF));
                store();
            } else if (properties.getProperty(WH_PRINT_URL_FQDN).isEmpty()) {
                return WH_PRINT_URL_FQDN_DEF;
            }
            return properties.getProperty(WH_PRINT_URL_FQDN);
        } catch (Exception ex) {
            return WH_PRINT_URL_FQDN_DEF;
        }
    }
  
     /**
     * ラベル印刷ベースURL(Baseパス)を取得する。
     *
     * @return
     */
    public String getPrintUrlBasePath() {
        try {
            if (!properties.containsKey(WH_PRINT_URL_BASE)) {
                properties.setProperty(WH_PRINT_URL_BASE, String.valueOf(WH_PRINT_URL_BASE_DEF));
                store();
            } else if (properties.getProperty(WH_PRINT_URL_BASE).isEmpty()) {
                return WH_PRINT_URL_BASE_DEF;
            }
            return properties.getProperty(WH_PRINT_URL_BASE);
        } catch (Exception ex) {
            return WH_PRINT_URL_BASE_DEF;
        }
    }

  /**
     * 資材情報表示項目名情報を取得する。
     *
     * @return
     */
    public String getMaterialItems() {
        try {
            if (!properties.containsKey(WH_MATERIAL_ITEMS)) {
                properties.setProperty(WH_MATERIAL_ITEMS, WH_MATERIAL_ITEMS_DEF);
                store();
            } else if (properties.getProperty(WH_MATERIAL_ITEMS).isEmpty()) {
                return WH_MATERIAL_ITEMS_DEF;
            }
            return properties.getProperty(WH_MATERIAL_ITEMS);
        } catch (Exception ex) {
            return WH_MATERIAL_ITEMS_DEF;
        }
    }

    /**
     * 加工品を入庫対象とするかどうかを返す。
     * 
     * @return true: 加工品を入庫対象とする、false: 加工品を入庫対象としない
     */
    public boolean isEnableSemiProducts() {
            try {
            if (!properties.containsKey(WH_ENABLE_SEMI_PRODUCTS)) {
                properties.setProperty(WH_ENABLE_SEMI_PRODUCTS, WH_ENABLE_SEMI_PRODUCTS_DEF);
                store();
            } else if (properties.getProperty(WH_ENABLE_SEMI_PRODUCTS).isEmpty()) {
                return Boolean.valueOf(WH_ENABLE_SEMI_PRODUCTS_DEF);
            }
            return Boolean.valueOf(properties.getProperty(WH_ENABLE_SEMI_PRODUCTS));
        } catch (Exception ex) {
            return Boolean.valueOf(WH_ENABLE_SEMI_PRODUCTS_DEF);
        }
    }

    /**
     * 納品書を照合するかどうかを返す。
     * 
     * @return true: 納品書を照合する、false: 納品書を照合しない
     */
    public boolean isEnableStatement() {
            try {
            if (!properties.containsKey(WH_ENABLE_STATEMENT)) {
                properties.setProperty(WH_ENABLE_STATEMENT, WH_ENABLE_STATEMENT_DEF);
                store();
            } else if (properties.getProperty(WH_ENABLE_STATEMENT).isEmpty()) {
                return Boolean.valueOf(WH_ENABLE_STATEMENT_DEF);
            }
            return Boolean.valueOf(properties.getProperty(WH_ENABLE_STATEMENT));
        } catch (Exception ex) {
            return Boolean.valueOf(WH_ENABLE_STATEMENT_DEF);
        }
    }

    /**
     * 資材情報の保持月数を取得する。
     *
     * @return 資材情報の保持月数(-1:期限なし, 0～:月数)
     */
    public int getMaterialDeleteMonth() {
        try {
            if (!properties.containsKey(WH_MATERIAL_DELETE_MONTH)) {
                properties.setProperty(WH_MATERIAL_DELETE_MONTH, String.valueOf(WH_MATERIAL_DELETE_MONTH_DEF));
                store();
            } else if (properties.getProperty(WH_MATERIAL_DELETE_MONTH).isEmpty()) {
                return WH_MATERIAL_DELETE_MONTH_DEF;
            }
            return Integer.parseInt(properties.getProperty(WH_MATERIAL_DELETE_MONTH));
        } catch (Exception ex) {
            return WH_MATERIAL_DELETE_MONTH_DEF;
        }
    }

    /**
     * LDAP接続先URLを取得する。
     *
     * @return 
     */
    public String getLdapProviderURL() {
        try {
            if (!properties.containsKey(LDAP_PROVIDER_URL_KEY)) {
                properties.setProperty(LDAP_PROVIDER_URL_KEY, LDAP_PROVIDER_URL_KEY_DEF);
                store();
            } else if (properties.getProperty(LDAP_PROVIDER_URL_KEY).isEmpty()) {
                return LDAP_PROVIDER_URL_KEY_DEF;
            }
            return properties.getProperty(LDAP_PROVIDER_URL_KEY);
        } catch (Exception ex) {
            return LDAP_PROVIDER_URL_KEY_DEF;
        }
    }
    
    /**
     * LDAP認証ドメイン名を取得する。
     *
     * @return 
     */
    public String getLdapDomain() {
        try {
            if (!properties.containsKey(LDAP_DOMAIN_KEY)) {
                properties.setProperty(LDAP_DOMAIN_KEY, LDAP_DOMAIN_KEY_DEF);
                store();
            } else if (properties.getProperty(LDAP_DOMAIN_KEY).isEmpty()) {
                return LDAP_DOMAIN_KEY_DEF;
            }
            return properties.getProperty(LDAP_DOMAIN_KEY);
        } catch (Exception ex) {
            return LDAP_DOMAIN_KEY_DEF;
        }
    }

    /**
     * 部品情報インポートで使用する共有ディレクトリのパスを取得する。
     *
     * @return 部品情報インポートで使用する共有ディレクトリのパス
     */
    public String getImportParts() {
        try {
            if (!properties.containsKey(IMPORT_PARTS_KEY)) {
                properties.setProperty(IMPORT_PARTS_KEY, IMPORT_PARTS_DEF);
                store();
            } else if (properties.getProperty(IMPORT_PARTS_KEY).isEmpty()) {
                return IMPORT_PARTS_DEF;
            }
            return properties.getProperty(IMPORT_PARTS_KEY);
        } catch (Exception ex) {
            return IMPORT_PARTS_DEF;
        }
    }

    /**
     * ポーリング間隔を取得する。
     *
     * @return ポーリング間隔(分)
     */
    public long getPollingInterval() {
        try {
            if (!properties.containsKey(POLLING_INTERVAL_KEY)) {
                properties.setProperty(POLLING_INTERVAL_KEY, String.valueOf(POLLING_INTERVAL_DEF));
                store();
            } else if (properties.getProperty(POLLING_INTERVAL_KEY).isEmpty()) {
                return POLLING_INTERVAL_DEF;
            }
            return Long.valueOf(properties.getProperty(POLLING_INTERVAL_KEY));
        } catch (Exception ex) {
            return POLLING_INTERVAL_DEF;
        }
    }

    /**
     * メールサーバのホスト名 又は、IPアドレスを取得する。
     *
     * @return メールサーバのホスト名 又は、IPアドレス
     */
    public String getSmtpServer() {
        try {
            if (!properties.containsKey(SMTP_SERVER_KEY)) {
                properties.setProperty(SMTP_SERVER_KEY, String.valueOf(SMTP_SERVER_DEF));
                store();
            } else if (properties.getProperty(SMTP_SERVER_KEY).isEmpty()) {
                return SMTP_SERVER_DEF;
            }
            return properties.getProperty(SMTP_SERVER_KEY);
        } catch (Exception ex) {
            return SMTP_SERVER_DEF;
        }
    }

    /**
     * ポート番号を取得する。
     *
     * @return ポート番号
     */
    public int getSmtpPort() {
        try {
            if (!properties.containsKey(SMTP_PORT_KEY)) {
                properties.setProperty(SMTP_PORT_KEY, String.valueOf(SMTP_PORT_DEF));
                store();
            } else if (properties.getProperty(SMTP_PORT_KEY).isEmpty()) {
                return SMTP_PORT_DEF;
            }
            return Integer.valueOf(properties.getProperty(SMTP_PORT_KEY));
        } catch (Exception ex) {
            return SMTP_PORT_DEF;
        }
    }

    /**
     * ユーザ名を取得する。
     *
     * @return ユーザ名
     */
    public String getSmtpUser() {
        try {
            if (!properties.containsKey(SMTP_USER_KEY)) {
                properties.setProperty(SMTP_USER_KEY, String.valueOf(SMTP_USER_DEF));
                store();
            } else if (properties.getProperty(SMTP_USER_KEY).isEmpty()) {
                return SMTP_USER_DEF;
            }
            return properties.getProperty(SMTP_USER_KEY);
        } catch (Exception ex) {
            return SMTP_USER_DEF;
        }
    }

    /**
     * パスワードを取得する。
     *
     * @return パスワード
     */
    public String getSmtpPassword() {
        try {
            if (!properties.containsKey(SMTP_PASSWORD_KEY)) {
                properties.setProperty(SMTP_PASSWORD_KEY, String.valueOf(SMTP_PASSWORD_DEF));
                store();
            } else if (properties.getProperty(SMTP_PASSWORD_KEY).isEmpty()) {
                return SMTP_PASSWORD_DEF;
            }
            return CipherHelper.decrypt(properties.getProperty(SMTP_PASSWORD_KEY), Constants.CIPHER_KEY, Constants.CIPHER_ALGORITHM);
        } catch (Exception ex) {
            return SMTP_PASSWORD_DEF;
        }
    }

    /**
     * パスワードを設定する。
     *
     * @param value パスワード
     * @throws java.lang.Exception
     */
    public void setSmtpPassword(String value) throws Exception {
        properties.setProperty(SMTP_PASSWORD_KEY, CipherHelper.encrypt(value, Constants.CIPHER_KEY, Constants.CIPHER_ALGORITHM));
    }

    /**
     * 現品ラベルのレイアウト番号を取得する。
     * 
     * @return 現品ラベルのレイアウト番号
     */
    public String getPrintFormat() {
        try {
            if (!properties.containsKey(WH_PRINT_FORMAT)) {
                properties.setProperty(WH_PRINT_FORMAT, WH_PRINT_FORMAT_DEF);
                store();
            } else if (properties.getProperty(WH_PRINT_FORMAT).isEmpty()) {
                return WH_PRINT_FORMAT_DEF;
            }
            return properties.getProperty(WH_PRINT_FORMAT);
        } catch (Exception ex) {
            return WH_PRINT_FORMAT_DEF;
        } 
    }
    
    /**
     * 棚卸ラベルのレイアウト番号を取得する。
     * 
     * @return 現品ラベルのレイアウト番号
     */
    public String getInventoryLabelFormat() {
        try {
            if (!properties.containsKey(WH_INVENTORY_LABEL_FORMAT)) {
                properties.setProperty(WH_INVENTORY_LABEL_FORMAT, WH_INVENTORY_LABEL_FORMAT_DEF);
                store();
            } else if (properties.getProperty(WH_INVENTORY_LABEL_FORMAT).isEmpty()) {
                return WH_INVENTORY_LABEL_FORMAT_DEF;
            }
            return properties.getProperty(WH_INVENTORY_LABEL_FORMAT);
        } catch (Exception ex) {
            return WH_INVENTORY_LABEL_FORMAT_DEF;
        }        
    }
    
    /**
     * 出庫画面にて払出指示書と現品ラベルのバーコードを入力するかどうかを返す。
     * 
     * @return 
     */
    public boolean isInputBarcode() {
        try {
            if (!properties.containsKey("wh_inputBarcode")) {
                properties.setProperty("wh_inputBarcode", String.valueOf(false));
                store();
            }
            return Boolean.valueOf(properties.getProperty("wh_inputBarcode"));
        } catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * 倉庫案内の動作モードを取得する。
     * 
     * @return 動作モード
     */
    public String getWarehouseMode() {
        return properties.getProperty("wh_mode", "standard");
    }
    
    
    /**
     * 設定を保存する。
     */
    private void store() {
        try {
            AdProperty.store();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

}
