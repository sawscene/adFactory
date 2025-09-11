/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabase.common;

import adtekfuji.property.AdProperty;
import adtekfuji.utility.PasswordEncoder;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nar-nakamura
 */
public class AdDatabaseConfig {

    private static final Logger logger = LogManager.getLogger();

    private static final String LOCALE_KEY = "locale";// 表示言語
    private static final String BACKUP_LOCAL_DIR_KEY = "backup_local_dir";// 既定のバックアップ先（ローカル）
    private static final String BACKUP_NETWORK_DIR_KEY = "backup_network_dir";// ネットワークバックアップ先
    private static final String BACKUP_DEST_TYPE_KEY = "backup_dest_type";// バックアップ先種別
    private static final String BACKUP_NETWORK_USER_KEY = "backup_network_user";// ネットワークバックアップユーザ
    private static final String BACKUP_NETWORK_PASSWORD_KEY = "backup_network_password";// ネットワークバックアップパスワード
    private final static String REPORT_CSV_FILENAME_KEY = "report_csv_filename";// 実績出力ファイル名
    private final static String REPORT_CSV_ENCODE_KEY = "report_csv_encode_type";// 実績出力エンコード
    private final static String DATABASE_BACKUP_MAX_KEY = "database_backup_max";// バックアップファイル数の上限

    // 初期値
    private final static String DEFAULT_REPORT_CSV_FILENAME = "adfactory_report.csv";// 実績出力ファイル名
    private final static String DEFAULT_REPORT_CSV_ENCODE = "Shift_JIS";// 実績出力エンコード
    private final static int DEFAULT_DATABASE_BACKUP_MAX = 30;// バックアップファイル数の上限
    private final static String DEFAULT_BACKUP_DEST_TYPE = "LOCAL";// バックアップ先種別初期値
    
    /**
     * プロパティファイルを読み込む。
     */
    public static void load() {
        try {
           AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adDatabase.properties");
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * プロパティファイルを更新する。
     */
    public static void store() {
        try {
            AdProperty.store();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 表示言語を取得する。
     *
     * @return 表示言語
     */
    public static String getLocale() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(LOCALE_KEY)) {
            properties.setProperty(LOCALE_KEY, Locale.getDefault().toLanguageTag());
        }
        return properties.getProperty(LOCALE_KEY);
    }

    /**
     * 表示言語を設定する。
     *
     * @param value 表示言語
     */
    public static void setLocale(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(LOCALE_KEY, value);
    }
    
    /**
     * ローカルバックアップ先を取得する。
     *
     * @return ローカルバックアップ先
     */
    public static String getBackupLocalDir() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(BACKUP_LOCAL_DIR_KEY)) {
            File dir = new File(System.getenv("ADFACTORY_HOME") + File.separator + "backup");
            properties.setProperty(BACKUP_LOCAL_DIR_KEY, dir.getPath());
        }
        return properties.getProperty(BACKUP_LOCAL_DIR_KEY);
    }

    /**
     * ローカルバックアップ先を設定する。
     *
     * @param value ローカルバックアップ先
     */
    public static void setBackupLocalDir(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(BACKUP_LOCAL_DIR_KEY, value);
    }


    /**
     * 既定のバックアップ先を取得する。
     *
     * @return 既定のバックアップ先
     */
    public static String getBackupDir() {
        if ("NETWORK".equalsIgnoreCase(getBackupDestType())) {
            return getBackupNetworkDir();
        }
        return getBackupLocalDir();
    }

    /**
     * 既定のバックアップ先を設定する。
     *
     * @param value 既定のバックアップ先
     */
    public static void setDBackupDir(String value) {
        setBackupLocalDir(value);
    }

    /**
     * ネットワークバックアップ先を取得する。
     *
     * @return ネットワークバックアップ先
     */
    public static String getBackupNetworkDir() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(BACKUP_NETWORK_DIR_KEY)) {
            properties.setProperty(BACKUP_NETWORK_DIR_KEY, "");
        }
        return properties.getProperty(BACKUP_NETWORK_DIR_KEY);
    }

    /**
     * ネットワークバックアップ先を設定する。
     *
     * @param value ネットワークバックアップ先
     */
    public static void setBackupNetworkDir(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(BACKUP_NETWORK_DIR_KEY, value);
    }

    /**
     * バックアップ先の種別を取得する。
     *
     * @return バックアップ先種別
     */
    public static String getBackupDestType() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(BACKUP_DEST_TYPE_KEY)) {
            properties.setProperty(BACKUP_DEST_TYPE_KEY, DEFAULT_BACKUP_DEST_TYPE);
        }
        return properties.getProperty(BACKUP_DEST_TYPE_KEY);
    }

    /**
     * バックアップ先の種別を設定する。
     *
     * @param value バックアップ先種別
     */
    public static void setBackupDestType(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(BACKUP_DEST_TYPE_KEY, value);
    }

    /**
     * ネットワークバックアップユーザを取得する。
     *
     * @return ネットワークバックアップユーザ
     */
    public static String getBackupNetworkUser() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(BACKUP_NETWORK_USER_KEY)) {
            properties.setProperty(BACKUP_NETWORK_USER_KEY, "");
        }
        return properties.getProperty(BACKUP_NETWORK_USER_KEY);
    }

    /**
     * ネットワークバックアップユーザを設定する。
     *
     * @param value ネットワークバックアップユーザ
     */
    public static void setBackupNetworkUser(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(BACKUP_NETWORK_USER_KEY, value);
    }

    /**
     * ネットワークバックアップパスワードを取得する。
     *
     * @return ネットワークバックアップパスワード
     */
    public static String getBackupNetworkPassword() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(BACKUP_NETWORK_PASSWORD_KEY)) {
            properties.setProperty(BACKUP_NETWORK_PASSWORD_KEY, "");
        }
        try {
            String decoded = new PasswordEncoder().decodeAES(properties.getProperty(BACKUP_NETWORK_PASSWORD_KEY));
            return decoded == null ? "" : decoded;
        } catch (Exception ex) {
            return "";
        }
//        return properties.getProperty(BACKUP_NETWORK_PASSWORD_KEY);
    }
    
    /**
     * ネットワークバックアップパスワードを設定する。
     *
     * @param value ネットワークバックアップパスワード
     */
    public static void setBackupNetworkPassword(String value) {
        Properties properties = AdProperty.getProperties();
        if (value == null || value.isEmpty()) {
            properties.setProperty(BACKUP_NETWORK_PASSWORD_KEY, "");
        } else {
            properties.setProperty(BACKUP_NETWORK_PASSWORD_KEY, new PasswordEncoder().encodeAES(value));
        }
    }
    
    

    /**
     * 実績出力ファイル名を取得する。
     *
     * @return 実績出力ファイル名
     */
    public static String getReportCsvFilename() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(REPORT_CSV_FILENAME_KEY)) {
            properties.setProperty(REPORT_CSV_FILENAME_KEY, DEFAULT_REPORT_CSV_FILENAME);
        }
        return properties.getProperty(REPORT_CSV_FILENAME_KEY);
    }

    /**
     * 実績出力ファイル名を設定する。
     *
     * @param value 実績出力ファイル名
     */
    public static void setReportCsvFilename(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(REPORT_CSV_FILENAME_KEY, value);
    }

    /**
     * 実績出力のエンコードを取得する。
     *
     * @return 実績出力のエンコード
     */
    public static String getReportCsvEncode() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(REPORT_CSV_ENCODE_KEY)) {
            properties.setProperty(REPORT_CSV_ENCODE_KEY, DEFAULT_REPORT_CSV_ENCODE);
        }
        return properties.getProperty(REPORT_CSV_ENCODE_KEY);
    }

    /**
     * 実績出力のエンコードを設定する。
     *
     * @param value 実績出力のエンコード
     */
    public static void setReportCsvEncode(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(REPORT_CSV_ENCODE_KEY, value);
    }

    /**
     * バックアップファイル数の上限を取得する。
     *
     * @return バックアップファイル数の上限
     */
    public static int getDatabaseBackupMax() {
        try {
            Properties properties = AdProperty.getProperties();
            if (!properties.containsKey(DATABASE_BACKUP_MAX_KEY)) {
                properties.setProperty(DATABASE_BACKUP_MAX_KEY, String.valueOf(DEFAULT_DATABASE_BACKUP_MAX));
            }
            return Integer.parseInt(properties.getProperty(DATABASE_BACKUP_MAX_KEY));
        } catch (Exception ex) {
            return DEFAULT_DATABASE_BACKUP_MAX;
        }
    }
}
