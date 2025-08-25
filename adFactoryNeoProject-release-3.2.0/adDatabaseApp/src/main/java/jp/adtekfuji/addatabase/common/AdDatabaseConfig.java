/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabase.common;

import adtekfuji.property.AdProperty;
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
    private static final String BACKUP_DIR_KEY = "backup_dir";// 既定のバックアップ先
    private final static String REPORT_CSV_FILENAME_KEY = "report_csv_filename";// 実績出力ファイル名
    private final static String REPORT_CSV_ENCODE_KEY = "report_csv_encode_type";// 実績出力エンコード
    private final static String DATABASE_BACKUP_MAX_KEY = "database_backup_max";// バックアップファイル数の上限

    // 初期値
    private final static String DEFAULT_REPORT_CSV_FILENAME = "adfactory_report.csv";// 実績出力ファイル名
    private final static String DEFAULT_REPORT_CSV_ENCODE = "Shift_JIS";// 実績出力エンコード
    private final static int DEFAULT_DATABASE_BACKUP_MAX = 30;// バックアップファイル数の上限

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
     * 既定のバックアップ先を取得する。
     *
     * @return 既定のバックアップ先
     */
    public static String getBackupDir() {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(BACKUP_DIR_KEY)) {
            File dir = new File(System.getenv("ADFACTORY_HOME") + File.separator + "backup");
            properties.setProperty(BACKUP_DIR_KEY, dir.getPath());
        }
        return properties.getProperty(BACKUP_DIR_KEY);
    }

    /**
     * 既定のバックアップ先を設定する。
     *
     * @param value 既定のバックアップ先
     */
    public static void setDBackupDir(String value) {
        Properties properties = AdProperty.getProperties();
        properties.setProperty(BACKUP_DIR_KEY, value);
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
