/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.exportserviceplugin;

import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ExportService 設定
 *
 * @author nar-nakamura
 */
public class ExportServiceConfig {

    private static final Logger logger = LogManager.getLogger();

    private static ExportServiceConfig instance = null;

    private final Properties properties;

    private static final String SERVICE_NAME = "ExportService";
    private static final String PROPERTY_NAME = "exportService.properties";

    // FTPサーバー アドレス
    private static final String FTP_ADDRESS = "ftpAddress";
    private static final String DEF_FTP_ADDRESS = "127.0.0.1";

    // FTPサーバー ポート番号
    private static final String FTP_SERVER_PORT = "ftpServerPort";
    private static final String DEF_FTP_SERVER_PORT = "21";

    // FTPサーバー ログインユーザー
    private static final String FTP_LOGIN_USER = "ftpLoginUser";
    private static final String DEF_FTP_LOGIN_USER = "admin";

    // FTPサーバー ログインパスワード
    private static final String FTP_LOGIN_PASSWORD = "ftpLoginPassword";
    private static final String DEF_FTP_LOGIN_PASSWORD = "";

    // FTPサーバー アップロードフォルダ
    private static final String FTP_TO_DIR = "ftpToDir";
    private static final String DEF_FTP_TO_DIR = "/in/AdFactory/AD2W/";

    // FTPサーバー ダウンロードフォルダ
    private static final String FTP_FROM_DIR = "ftpFromDir";
    private static final String DEF_FTP_FROM_DIR = "/out/AdFactory/master/";

    // WebAPI ベースURL
    private static final String WEB_API_BASE_URL = "webApiBaseUrl";
    private static final String DEF_WEB_API_BASE_URL = "http://localhost:8081/DummyServer";

    // WebAPI 着工完工情報をPOSTするURL
    private static final String WEB_API_PROCESS = "webApiProcess";
    private static final String DEF_WEB_API_PROCESS = "/api/process";

    // WebAPI シリアル情報をPOSTするURL
    private static final String WEB_API_SERIAL = "webApiSerial";
    private static final String DEF_WEB_API_SERIAL = "/api/serial";

    // エラー出力ファイルのベース名
    private static final String ERROR_LOG_BASE_NAME = "errorLogBaseName";
    private static final String DEF_ERROR_LOG_BASE_NAME = SERVICE_NAME;

    /**
     * コンストラクタ
     */
    private ExportServiceConfig() {
        try {
            AdProperty.load(SERVICE_NAME, PROPERTY_NAME);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        this.properties = AdProperty.getProperties(SERVICE_NAME);
    }

    /**
     * 設定クラスのインスタンスを取得する。
     *
     * @return
     */
    public static ExportServiceConfig getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ExportServiceConfig();
        }
        return instance;
    }

    /**
     * FTPアドレスを取得する。
     *
     * @return FTPアドレス
     */
    public String getFtpAddress() {
        if (!properties.containsKey(FTP_ADDRESS)) {
            properties.setProperty(FTP_ADDRESS, DEF_FTP_ADDRESS);
        }
        return properties.getProperty(FTP_ADDRESS);
    }

    /**
     * FTPサーバー ポート番号を取得する。
     *
     * @return FTPサーバー ポート番号
     */
    public int getFtpServerPort() {
        if (!properties.containsKey(FTP_SERVER_PORT)) {
            properties.setProperty(FTP_SERVER_PORT, DEF_FTP_SERVER_PORT);
        }
        return Integer.parseInt(properties.getProperty(FTP_SERVER_PORT));
    }

    /**
     * FTPサーバー ログインユーザーを取得する。
     *
     * @return FTPサーバー ログインユーザー
     */
    public String getFtpLoginUser() {
        if (!properties.containsKey(FTP_LOGIN_USER)) {
            properties.setProperty(FTP_LOGIN_USER, DEF_FTP_LOGIN_USER);
        }
        return properties.getProperty(FTP_LOGIN_USER);
    }

    /**
     * FTPサーバー ログインパスワードを取得する。
     *
     * @return FTPサーバー ログインパスワード
     */
    public String getFtpLoginPassword() {
        if (!properties.containsKey(FTP_LOGIN_PASSWORD)) {
            properties.setProperty(FTP_LOGIN_PASSWORD, DEF_FTP_LOGIN_PASSWORD);
        }
        return properties.getProperty(FTP_LOGIN_PASSWORD);
    }

    /**
     * FTPサーバー アップロードフォルダを取得する。
     *
     * @return FTPサーバー アップロードフォルダ
     */
    public String getToFromDir() {
        if (!properties.containsKey(FTP_TO_DIR)) {
            properties.setProperty(FTP_TO_DIR, DEF_FTP_TO_DIR);
        }
        return properties.getProperty(FTP_TO_DIR);
    }

    /**
     * FTPサーバー ダウンロードフォルダを取得する。
     *
     * @return FTPサーバー ダウンロードフォルダ
     */
    public String getFtpFromDir() {
        if (!properties.containsKey(FTP_FROM_DIR)) {
            properties.setProperty(FTP_FROM_DIR, DEF_FTP_FROM_DIR);
        }
        return properties.getProperty(FTP_FROM_DIR);
    }

    /**
     * WebAPI ベースURLを取得する。
     *
     * @return WebAPI ベースURL
     */
    public String getWebApiBaseUrl() {
        if (!properties.containsKey(WEB_API_BASE_URL)) {
            properties.setProperty(WEB_API_BASE_URL, DEF_WEB_API_BASE_URL);
            store();
        }
        return properties.getProperty(WEB_API_BASE_URL);
    }

    /**
     * WebAPI 着工完工情報をPOSTするURLを取得する。
     *
     * @return WebAPI 着工完工情報をPOSTするURL
     */
    public String getWebApiProcess() {
        if (!properties.containsKey(WEB_API_PROCESS)) {
            properties.setProperty(WEB_API_PROCESS, DEF_WEB_API_PROCESS);
            store();
        }
        return properties.getProperty(WEB_API_PROCESS);
    }

    /**
     * WebAPI シリアル情報をPOSTするURLを取得する。
     *
     * @return WebAPI シリアル情報をPOSTするURL
     */
    public String getWebApiSerial() {
        if (!properties.containsKey(WEB_API_SERIAL)) {
            properties.setProperty(WEB_API_SERIAL, DEF_WEB_API_SERIAL);
            store();
        }
        return properties.getProperty(WEB_API_SERIAL);
    }

    /**
     * エラー出力ファイルのベース名を取得する。
     *
     * @return エラー出力ファイルのベース名
     */
    public String getErrorLogBaseName() {
        if (!properties.containsKey(ERROR_LOG_BASE_NAME)) {
            properties.setProperty(ERROR_LOG_BASE_NAME, DEF_ERROR_LOG_BASE_NAME);
            store();
        }
        return properties.getProperty(ERROR_LOG_BASE_NAME);
    }

    /**
     * 設定を保存する。
     */
    private void store() {
        try {
            AdProperty.store(SERVICE_NAME);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }
}
