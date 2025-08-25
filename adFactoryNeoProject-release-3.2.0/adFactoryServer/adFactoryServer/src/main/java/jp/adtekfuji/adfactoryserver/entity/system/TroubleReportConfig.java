/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.system;

import adtekfuji.property.AdProperty;
import adtekfuji.utility.CipherHelper;
import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
import jp.adtekfuji.adfactoryserver.common.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 障害レポート設定
 *
 * @author nar-nakamura
 */
public class TroubleReportConfig {

    private static final Logger logger = LogManager.getLogger();

    private static final String TROUBLE_REPORT_PROPERTY = "troubleReport";
    private static final String TROUBLE_REPORT_PROPERTIES = "troubleReport.properties";

    private static final String REPORT_MAIL_FROM_KEY = "reportMailFrom";// 送信者
    private static final String REPORT_MAIL_FROM_DEFAULT = "adfactory@adtek-fuji.co.jp";

    private static final String REPORT_MAIL_TO_KEY = "reportMailTo";// 宛先
    private static final String REPORT_MAIL_TO_DEFAULT = "adfactory@adtek-fuji.co.jp";

    private static final String REPORT_MAIL_SERVER_KEY = "reportMailServer";// メールサーバー
    private static final String REPORT_MAIL_SERVER_DEFAULT = "smtp.adtek-fuji.co.jp";

    private static final String REPORT_MAIL_PORT_KEY = "reportMailPort";// ポート番号
    private static final String REPORT_MAIL_PORT_DEFAULT = "587";

    private static final String REPORT_MAIL_TIMEOUT_KEY = "reportMailTimeout";// タイムアウト時間 (秒)
    private static final String REPORT_MAIL_TIMEOUT_DEFAULT = "30";

    private static final String REPORT_MAIL_AUTH_KEY = "reportMailAuth";// 認証を使用するか
    private static final String REPORT_MAIL_AUTH_DEFAULT = "true";

    private static final String REPORT_MAIL_USER_KEY = "reportMailUser";// ユーザー
    private static final String REPORT_MAIL_USER_DEFAULT = "adfactory";

    private static final String REPORT_MAIL_PASSWORD_KEY = "reportMailPassword";// パスワード
    private static final String REPORT_MAIL_PASSWORD_DEFAULT = "";

    private static final String REPORT_MAIL_TLS_KEY = "reportMailTLS";// TLSを使用するか
    private static final String REPORT_MAIL_TLS_DEFAULT = "true";

    private static final String REPORT_MAIL_SUBJECT_KEY = "reportMailSubject";// 件名
    private static final String REPORT_MAIL_SUBJECT_DEFAULT = "[Trouble Report]";

    private static final String REPORT_MAIL_CONTENT_KEY = "reportMailContent";// 本文
    private static final String REPORT_MAIL_CONTENT_DEFAULT = "adFactory Trouble Report";

    private static final String TLOG_FTP_ROOT_PATH_KEY = "tlogFtpRootPath";// TLOGファイルのFTPルートパス
    private static final String TLOG_FTP_ROOT_PATH_DEFAULT = Paths.get(System.getenv("ADFACTORY_HOME"), "3rd/apache-ftpserver-1.0.6/res/home").toString();

    private static TroubleReportConfig instance = null;
    private Properties properties = new Properties();

    /**
     * コンストラクタ
     */
    private TroubleReportConfig() {
    }

    /**
     * 設定クラスのインスタンスを取得する
     *
     * @return
     */
    public static TroubleReportConfig getInstance() {
        if (Objects.isNull(instance)) {
            instance = new TroubleReportConfig();
        }
        return instance;
    }

    /**
     * 送信者を取得する。
     *
     * @return 送信者
     */
    public String getReportMailFrom() {
        if (!properties.containsKey(REPORT_MAIL_FROM_KEY)) {
            properties.setProperty(REPORT_MAIL_FROM_KEY, REPORT_MAIL_FROM_DEFAULT);
        }
        return properties.getProperty(REPORT_MAIL_FROM_KEY);
    }

    /**
     * 送信者を設定する。
     *
     * @param value 送信者
     */
    public void setReportMailFrom(String value) {
        properties.setProperty(REPORT_MAIL_FROM_KEY, value);
    }

    /**
     * 宛先を取得する。
     *
     * @return 宛先
     */
    public String getReportMailTo() {
        if (!properties.containsKey(REPORT_MAIL_TO_KEY)) {
            properties.setProperty(REPORT_MAIL_TO_KEY, REPORT_MAIL_TO_DEFAULT);
        }
        return properties.getProperty(REPORT_MAIL_TO_KEY);
    }

    /**
     * 宛先を設定する。
     *
     * @param value 宛先
     */
    public void setReportMailTo(String value) {
        properties.setProperty(REPORT_MAIL_TO_KEY, value);
    }

    /**
     * メールサーバーを取得する。
     *
     * @return メールサーバー
     */
    public String getReportMailServer() {
        if (!properties.containsKey(REPORT_MAIL_SERVER_KEY)) {
            properties.setProperty(REPORT_MAIL_SERVER_KEY, REPORT_MAIL_SERVER_DEFAULT);
        }
        return properties.getProperty(REPORT_MAIL_SERVER_KEY);
    }

    /**
     * メールサーバーを設定する。
     *
     * @param value メールサーバー
     */
    public void setReportMailServer(String value) {
        properties.setProperty(REPORT_MAIL_SERVER_KEY, value);
    }

    /**
     * ポート番号を取得する。
     *
     * @return ポート番号
     */
    public int getReportMailPort() {
        try {
            if (!properties.containsKey(REPORT_MAIL_PORT_KEY)) {
                properties.setProperty(REPORT_MAIL_PORT_KEY, REPORT_MAIL_PORT_DEFAULT);
            }
            return Integer.parseInt(properties.getProperty(REPORT_MAIL_PORT_KEY));
        } catch (Exception ex) {
            return Integer.parseInt(REPORT_MAIL_PORT_DEFAULT);
        }
    }

    /**
     * ポート番号を設定する。
     *
     * @param value ポート番号
     */
    public void setReportMailPort(int value) {
        properties.setProperty(REPORT_MAIL_PORT_KEY, String.valueOf(value));
    }

    /**
     * タイムアウト時間(秒)を取得する。
     *
     * @return タイムアウト時間(秒)
     */
    public int getReportMailTimeout() {
        try {
            if (!properties.containsKey(REPORT_MAIL_TIMEOUT_KEY)) {
                properties.setProperty(REPORT_MAIL_TIMEOUT_KEY, REPORT_MAIL_TIMEOUT_DEFAULT);
            }
            return Integer.parseInt(properties.getProperty(REPORT_MAIL_TIMEOUT_KEY));
        } catch (Exception ex) {
            return Integer.parseInt(REPORT_MAIL_TIMEOUT_DEFAULT);
        }
    }

    /**
     * タイムアウト時間(秒)を設定する。
     *
     * @param value タイムアウト時間(秒)
     */
    public void setReportMailTimeout(int value) {
        properties.setProperty(REPORT_MAIL_TIMEOUT_KEY, String.valueOf(value));
    }

    /**
     * 認証を使用するかを取得する。
     *
     * @return 認証を使用するか
     */
    public boolean getReportMailAuth() {
        try {
            if (!properties.containsKey(REPORT_MAIL_AUTH_KEY)) {
                properties.setProperty(REPORT_MAIL_AUTH_KEY, REPORT_MAIL_AUTH_DEFAULT);
            }
            return Boolean.parseBoolean(properties.getProperty(REPORT_MAIL_AUTH_KEY));
        } catch (Exception ex) {
            return Boolean.parseBoolean(REPORT_MAIL_AUTH_DEFAULT);
        }
    }

    /**
     * 認証を使用するかを設定する。
     *
     * @param value 認証を使用するか
     */
    public void setReportMailAuth(boolean value) {
        properties.setProperty(REPORT_MAIL_AUTH_KEY, String.valueOf(value));
    }

    /**
     * ユーザーを取得する。
     *
     * @return ユーザー
     */
    public String getReportMailUser() {
        if (!properties.containsKey(REPORT_MAIL_USER_KEY)) {
            properties.setProperty(REPORT_MAIL_USER_KEY, REPORT_MAIL_USER_DEFAULT);
        }
        return properties.getProperty(REPORT_MAIL_USER_KEY);
    }

    /**
     * ユーザーを設定する。
     *
     * @param value ユーザー
     */
    public void setReportMailUser(String value) throws Exception {
        properties.setProperty(REPORT_MAIL_USER_KEY, CipherHelper.encrypt(value, Constants.CIPHER_KEY, Constants.CIPHER_ALGORITHM));
    }

    /**
     * パスワードを取得する。
     *
     * @return パスワード
     */
    public String getReportMailPassword() {
        try {
            if (!properties.containsKey(REPORT_MAIL_PASSWORD_KEY)) {
                properties.setProperty(REPORT_MAIL_PASSWORD_KEY, REPORT_MAIL_PASSWORD_DEFAULT);
            }
            return CipherHelper.decrypt(properties.getProperty(REPORT_MAIL_PASSWORD_KEY), Constants.CIPHER_KEY, Constants.CIPHER_ALGORITHM);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * パスワードを設定する。
     *
     * @param value パスワード
     */
    public void setReportMailPassword(String value) throws Exception {
        properties.setProperty(REPORT_MAIL_PASSWORD_KEY, CipherHelper.encrypt(value, Constants.CIPHER_KEY, Constants.CIPHER_ALGORITHM));
    }

    /**
     * TLSを使用するかを取得する。
     *
     * @return TLSを使用するか
     */
    public boolean getReportMailTLS() {
        try {
            if (!properties.containsKey(REPORT_MAIL_TLS_KEY)) {
                properties.setProperty(REPORT_MAIL_TLS_KEY, REPORT_MAIL_TLS_DEFAULT);
            }
            return Boolean.parseBoolean(properties.getProperty(REPORT_MAIL_TLS_KEY));
        } catch (Exception ex) {
            return Boolean.parseBoolean(REPORT_MAIL_TLS_DEFAULT);
        }
    }

    /**
     * TLSを使用するかを設定する。
     *
     * @param value TLSを使用するか
     */
    public void setReportMailTLS(boolean value) {
        properties.setProperty(REPORT_MAIL_TLS_KEY, String.valueOf(value));
    }

    /**
     * 件名を取得する。
     *
     * @return 件名
     */
    public String getReportMailSubject() {
        if (!properties.containsKey(REPORT_MAIL_SUBJECT_KEY)) {
            properties.setProperty(REPORT_MAIL_SUBJECT_KEY, REPORT_MAIL_SUBJECT_DEFAULT);
        }
        return properties.getProperty(REPORT_MAIL_SUBJECT_KEY);
    }

    /**
     * 件名を設定する。
     *
     * @param value 件名
     */
    public void setReportMailSubject(String value) {
        properties.setProperty(REPORT_MAIL_SUBJECT_KEY, value);
    }

    /**
     * 本文を取得する。
     *
     * @return 本文
     */
    public String getReportMailContent() {
        if (!properties.containsKey(REPORT_MAIL_CONTENT_KEY)) {
            properties.setProperty(REPORT_MAIL_CONTENT_KEY, REPORT_MAIL_CONTENT_DEFAULT);
        }
        return properties.getProperty(REPORT_MAIL_CONTENT_KEY);
    }

    /**
     * 本文を設定する。
     *
     * @param value 本文
     */
    public void setReportMailContent(String value) {
        properties.setProperty(REPORT_MAIL_CONTENT_KEY, value);
    }

    /**
     * TLOGファイルのFTPルートパスを取得する。
     *
     * @return TLOGファイルのFTPルートパス
     */
    public String getTlogFtpRootPath() {
        if (!properties.containsKey(TLOG_FTP_ROOT_PATH_KEY)) {
            properties.setProperty(TLOG_FTP_ROOT_PATH_KEY, TLOG_FTP_ROOT_PATH_DEFAULT);
        }
        return properties.getProperty(TLOG_FTP_ROOT_PATH_KEY);
    }

    /**
     * TLOGファイルのFTPルートパスを設定する。
     *
     * @param value TLOGファイルのFTPルートパス
     */
    public void setTlogFtpRootPath(String value) {
        properties.setProperty(TLOG_FTP_ROOT_PATH_KEY, value);
    }

    /**
     * 設定を読み込む。
     */
    public void load() {
        logger.info("load");
        try {
            String confPath = Paths.get(System.getenv("ADFACTORY_HOME"), "conf").toString();
            String filePath = Paths.get(confPath, TROUBLE_REPORT_PROPERTIES).toString();//System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + TROUBLE_REPORT_PROPERTIES;
            File file = new File(filePath);
            boolean isExist = file.exists();

            properties.clear();

            AdProperty.rebasePath(confPath);
            AdProperty.load(TROUBLE_REPORT_PROPERTY, TROUBLE_REPORT_PROPERTIES);
            properties = AdProperty.getProperties(TROUBLE_REPORT_PROPERTY);
            if (!isExist) {
                save();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 設定を書き込む。
     */
    public void save() {
        logger.info("save");
        try {
            this.getReportMailFrom();
            this.getReportMailTo();
            this.getReportMailServer();
            this.getReportMailPort();
            this.getReportMailTimeout();
            this.getReportMailAuth();
            this.getReportMailUser();
            this.getReportMailPassword();
            this.getReportMailTLS();
            this.getReportMailSubject();
            this.getReportMailContent();
            this.getTlogFtpRootPath();

            AdProperty.store(TROUBLE_REPORT_PROPERTY);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
