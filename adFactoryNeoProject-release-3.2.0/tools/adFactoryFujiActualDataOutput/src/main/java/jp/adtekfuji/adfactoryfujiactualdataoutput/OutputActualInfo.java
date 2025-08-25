/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryfujiactualdataoutput;

import adtekfuji.property.AdProperty;
import adtekfuji.utility.CipherHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 実績出力設定情報
 *
 * @author ke.yokoi
 */
public class OutputActualInfo {

    // 実績出力設定
    private final String KEY_ADFCTORY_ADDRESS = "adFactoryAddress";
    private final String KEY_UPTAKE_INTERVAL = "uptakeInterval";
    private final String KEY_FROM_SEARCH_DATETIME = "fromSearchDatetime";
    private final String KEY_TO_SEARCH_DATETIME = "toSearchDatetime";
    private final String KEY_FTP_ADDRESS = "ftpAddressProperty";
    private final String KEY_FTP_PORT = "ftpPortProperty";
    private final String KEY_FTP_USER = "ftpUserProperty";
    private final String KEY_FTP_PASSWORD = "ftpPasswordProperty";
    private final String KEY_FTP_UPLOAD_PATH = "ftpUploadPath";
    //実績出力設定の初期値
    private final String DEF_ADFCTORY_ADDRESS = "http://localhost:8080";
    private final String DEF_UPTAKE_INTERVAL = "24";
    private final String DEF_FTP_ADDRESS = "127.0.0.1";
    private final String DEF_FTP_PORT = "21";
    private final String DEF_FTP_USER = "admin";
    private final String DEF_FTP_PASSWORD = "";
    private final String DEF_FTP_UPLOAD_PATH = "in/AdFactory/AD2W/";

    // エラー発生通知メールの設定
    private final String KEY_ERROR_MAIL_SERVER = "errorMailServer";// メールサーバー
    private final String KEY_ERROR_MAIL_PORT = "errorMailPort";// ポート番号
    private final String KEY_ERROR_MAIL_USER = "errorMailUser";// ユーザー
    private final String KEY_ERROR_MAIL_PASSWORD = "errorMailPassword";// パスワード
    private final String KEY_ERROR_MAIL_FROM = "errorMailFrom";// 送信者
    private final String KEY_ERROR_MAIL_TO = "errorMailTo";// 宛先
    private final String KEY_ERROR_MAIL_TIMEOUT = "errorMailTimeout";// タイムアウト時間
    // エラー発生通知メールの設定の初期値
    private final String DEF_ERROR_MAIL_SERVER = "";
    private final String DEF_ERROR_MAIL_PORT = "587";
    private final String DEF_ERROR_MAIL_USER = "";
    private final String DEF_ERROR_MAIL_PASSWORD = "";
    private final String DEF_ERROR_MAIL_FROM = "support@adtek-fuji.co.jp";
    private final String DEF_ERROR_MAIL_TO = "support@adtek-fuji.co.jp";
    private final String DEF_ERROR_MAIL_TIMEOUT = "30";

    private final String CIPHER_KEY = "BpzEsGPXnkW2SdUW";  //キーは16文字で
    private final String CIPHER_ALGORITHM = "AES";

    private Properties property;
    private final StringProperty adFactoryAddressProperty = new SimpleStringProperty();
    private final IntegerProperty uptakeIntervalProperty = new SimpleIntegerProperty();
    private final ObjectProperty<Date> fromSearchDatetimeProperty = new SimpleObjectProperty();
    private final ObjectProperty<Date> toSearchDatetimeProperty = new SimpleObjectProperty();
    private final StringProperty ftpAddressProperty = new SimpleStringProperty();
    private final IntegerProperty ftpPortProperty = new SimpleIntegerProperty();
    private final StringProperty ftpUserProperty = new SimpleStringProperty();
    private final StringProperty ftpPasswordProperty = new SimpleStringProperty();
    private final StringProperty ftpUploadPathProperty = new SimpleStringProperty();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    // エラー発生通知メールの設定
    private final StringProperty errorMailServerProperty = new SimpleStringProperty();
    private final IntegerProperty errorMailPortProperty = new SimpleIntegerProperty();
    private final StringProperty errorMailUserProperty = new SimpleStringProperty();
    private final StringProperty errorMailPasswordProperty = new SimpleStringProperty();
    private final StringProperty errorMailFromProperty = new SimpleStringProperty();
    private final StringProperty errorMailToProperty = new SimpleStringProperty();
    private final IntegerProperty errorMailTimeoutProperty = new SimpleIntegerProperty();

    /**
     * コンストラクタ
     */
    public OutputActualInfo() {
    }

    /**
     * 設定を読み込む。
     *
     * @throws Exception 
     */
    public void load() throws Exception {
        property = AdProperty.getProperties();
        adFactoryAddressProperty.set(property.getProperty(KEY_ADFCTORY_ADDRESS, DEF_ADFCTORY_ADDRESS));
        uptakeIntervalProperty.set(Integer.parseInt(property.getProperty(KEY_UPTAKE_INTERVAL, DEF_UPTAKE_INTERVAL)));
        fromSearchDatetimeProperty.set(sdf.parse(property.getProperty(KEY_FROM_SEARCH_DATETIME, sdf.format(new Date()))));
        toSearchDatetimeProperty.set(sdf.parse(property.getProperty(KEY_TO_SEARCH_DATETIME, sdf.format(new Date()))));
        ftpAddressProperty.set(property.getProperty(KEY_FTP_ADDRESS, DEF_FTP_ADDRESS));
        ftpPortProperty.set(Integer.parseInt(property.getProperty(KEY_FTP_PORT, DEF_FTP_PORT)));
        ftpUserProperty.set(property.getProperty(KEY_FTP_USER, DEF_FTP_USER));
        String password = property.getProperty(KEY_FTP_PASSWORD, DEF_FTP_PASSWORD);
        if (!password.isEmpty()) {
            ftpPasswordProperty.set(CipherHelper.decrypt(password, CIPHER_KEY, CIPHER_ALGORITHM));
        }
        ftpUploadPathProperty.set(property.getProperty(KEY_FTP_UPLOAD_PATH, DEF_FTP_UPLOAD_PATH));

        // エラー発生通知メールの設定
        errorMailServerProperty.set(property.getProperty(KEY_ERROR_MAIL_SERVER, DEF_ERROR_MAIL_SERVER));
        errorMailPortProperty.set(Integer.parseInt(property.getProperty(KEY_ERROR_MAIL_PORT, DEF_ERROR_MAIL_PORT)));
        errorMailUserProperty.set(property.getProperty(KEY_ERROR_MAIL_USER, DEF_ERROR_MAIL_USER));
        errorMailPasswordProperty.set(property.getProperty(KEY_ERROR_MAIL_PASSWORD, DEF_ERROR_MAIL_PASSWORD));
        errorMailFromProperty.set(property.getProperty(KEY_ERROR_MAIL_FROM, DEF_ERROR_MAIL_FROM));
        errorMailToProperty.set(property.getProperty(KEY_ERROR_MAIL_TO, DEF_ERROR_MAIL_TO));
        errorMailTimeoutProperty.set(Integer.parseInt(property.getProperty(KEY_ERROR_MAIL_TIMEOUT, DEF_ERROR_MAIL_TIMEOUT)));
    }

    /**
     * 設定を保存する。
     *
     * @throws Exception 
     */
    public void save() throws Exception {
        property.setProperty(KEY_ADFCTORY_ADDRESS, adFactoryAddressProperty.get());
        property.setProperty(KEY_UPTAKE_INTERVAL, String.valueOf(uptakeIntervalProperty.get()));
        property.setProperty(KEY_FROM_SEARCH_DATETIME, sdf.format(fromSearchDatetimeProperty.get()));
        property.setProperty(KEY_TO_SEARCH_DATETIME, sdf.format(toSearchDatetimeProperty.get()));
        property.setProperty(KEY_FTP_ADDRESS, ftpAddressProperty.get());
        property.setProperty(KEY_FTP_PORT, String.valueOf(ftpPortProperty.get()));
        property.setProperty(KEY_FTP_USER, ftpUserProperty.get());
        String password = ftpPasswordProperty.get();
        if (Objects.isNull(password)) {
            password = "";
        }
        property.setProperty(KEY_FTP_PASSWORD, CipherHelper.encrypt(password, CIPHER_KEY, CIPHER_ALGORITHM));
        property.setProperty(KEY_FTP_UPLOAD_PATH, ftpUploadPathProperty.get());

        // エラー発生通知メールの設定
        property.setProperty(KEY_ERROR_MAIL_SERVER, errorMailServerProperty.get());
        property.setProperty(KEY_ERROR_MAIL_PORT, String.valueOf(errorMailPortProperty.get()));
        property.setProperty(KEY_ERROR_MAIL_USER, errorMailUserProperty.get());
        property.setProperty(KEY_ERROR_MAIL_PASSWORD, errorMailPasswordProperty.get());
        property.setProperty(KEY_ERROR_MAIL_FROM, errorMailFromProperty.get());
        property.setProperty(KEY_ERROR_MAIL_TO, errorMailToProperty.get());
        property.setProperty(KEY_ERROR_MAIL_TIMEOUT, String.valueOf(errorMailTimeoutProperty.get()));

        AdProperty.store();
    }

    /**
     * adFactory のサーバーアドレスを取得する。
     *
     * @return adFactory のサーバーアドレス
     */
    public StringProperty adFactoryAddressProperty() {
        return this.adFactoryAddressProperty;
    }

    /**
     * adFactory のサーバーアドレスを取得する。
     *
     * @return adFactory のサーバーアドレス
     */
    public String getAdFactoryAddress() {
        return this.adFactoryAddressProperty.get();
    }

    /**
     * adFactory のサーバーアドレスを設定する。
     *
     * @param adFactoryServerAddress adFactory のサーバーアドレス
     */
    public void setAdFactoryServerAddress(String adFactoryServerAddress) {
        this.adFactoryAddressProperty.set(adFactoryServerAddress);
    }

    /**
     * データ取り込み間隔を取得する。
     *
     * @return データ取り込み間隔
     */
    public IntegerProperty uptakeIntervalProperty() {
        return this.uptakeIntervalProperty;
    }

    /**
     * データ取り込み間隔を取得する。
     *
     * @return データ取り込み間隔
     */
    public Integer getUptakeInterval() {
        return this.uptakeIntervalProperty.get();
    }

    /**
     * データ取り込み間隔を設定する。
     *
     * @param uptakeInterval データ取り込み間隔
     */
    public void setUptakeInterval(Integer uptakeInterval) {
        this.uptakeIntervalProperty.set(uptakeInterval);
    }

    /**
     * 日時範囲の先頭を取得する。
     *
     * @return 日時範囲の先頭
     */
    public ObjectProperty<Date> fromSearchDatetimeProperty() {
        return this.fromSearchDatetimeProperty;
    }

    /**
     * 日時範囲の先頭を取得する。
     *
     * @return 日時範囲の先頭
     */
    public Date getFromSearchDatetime() {
        return this.fromSearchDatetimeProperty.get();
    }

    /**
     * 日時範囲の先頭を設定する。
     *
     * @param fromSearchDatetime 日時範囲の先頭
     */
    public void setFromSearchDatetime(Date fromSearchDatetime) {
        this.fromSearchDatetimeProperty.set(fromSearchDatetime);
    }

    /**
     * 日時範囲の末尾を取得する。
     *
     * @return 日時範囲の末尾
     */
    public ObjectProperty<Date> toSearchDatetimeProperty() {
        return this.toSearchDatetimeProperty;
    }

    /**
     * 日時範囲の末尾を取得する。
     *
     * @return 日時範囲の末尾
     */
    public Date getToSearchDatetime() {
        return this.toSearchDatetimeProperty.get();
    }

    /**
     * 日時範囲の末尾を設定する。
     *
     * @param toSearchDatetime 日時範囲の末尾
     */
    public void setToSearchDatetime(Date toSearchDatetime) {
        this.toSearchDatetimeProperty.set(toSearchDatetime);
    }

    /**
     * FTPサーバーアドレスを取得する。
     *
     * @return FTPサーバーアドレス
     */
    public StringProperty ftpAddressProperty() {
        return this.ftpAddressProperty;
    }

    /**
     * FTPサーバーアドレスを取得する。
     *
     * @return FTPサーバーアドレス
     */
    public String getFtpAddress() {
        return this.ftpAddressProperty.get();
    }

    /**
     * FTPサーバーアドレスを設定する。
     *
     * @param ftpAddress FTPサーバーアドレス
     */
    public void setFtpAddress(String ftpAddress) {
        this.ftpAddressProperty.set(ftpAddress);
    }

    /**
     * FTPポート番号を取得する。
     *
     * @return FTPポート番号
     */
    public IntegerProperty ftpPortProperty() {
        return this.ftpPortProperty;
    }

    /**
     * FTPポート番号を取得する。
     *
     * @return FTPポート番号
     */
    public Integer getFtpPort() {
        return this.ftpPortProperty.get();
    }

    /**
     * FTPポート番号を設定する。
     *
     * @param ftpPort FTPポート番号
     */
    public void setFtpPort(Integer ftpPort) {
        this.ftpPortProperty.set(ftpPort);
    }

    /**
     * FTPユーザー名を取得する。
     *
     * @return FTPユーザー名
     */
    public StringProperty ftpUserProperty() {
        return this.ftpUserProperty;
    }

    /**
     * FTPユーザー名を取得する。
     *
     * @return FTPユーザー名
     */
    public String getFtpUser() {
        return this.ftpUserProperty.get();
    }

    /**
     * FTPユーザー名を設定する。
     *
     * @param ftpUser FTPユーザー名
     */
    public void setFtpUser(String ftpUser) {
        this.ftpUserProperty.set(ftpUser);
    }

    /**
     * FTPパスワードを取得する。
     *
     * @return FTPパスワード
     */
    public StringProperty ftpPasswordProperty() {
        return this.ftpPasswordProperty;
    }

    /**
     * FTPパスワードを取得する。
     *
     * @return FTPパスワード
     */
    public String getFtpPassword() {
        return this.ftpPasswordProperty.get();
    }

    /**
     * FTPパスワードを設定する。
     *
     * @param ftpPassword FTPパスワード
     */
    public void setFtpPassword(String ftpPassword) {
        this.ftpPasswordProperty.set(ftpPassword);
    }

    /**
     * FTPアップロードパスを取得する。
     *
     * @return FTPアップロードパス
     */
    public StringProperty ftpUploadPathProperty() {
        return this.ftpUploadPathProperty;
    }

    /**
     * FTPアップロードパスを取得する。
     *
     * @return FTPアップロードパス
     */
    public String getFtpUploadPath() {
        return this.ftpUploadPathProperty.get();
    }

    /**
     * FTPアップロードパスを設定する。
     *
     * @param ftpUploadPath FTPアップロードパス
     */
    public void setFtpUploadPath(String ftpUploadPath) {
        this.ftpUploadPathProperty.set(ftpUploadPath);
    }

    /**
     * エラー発生通知のメールサーバーアドレスを取得する。
     *
     * @return エラー発生通知のメールサーバーアドレス
     */
    public String getErrorMailServer() {
        return this.errorMailServerProperty.get();
    }

    /**
     * エラー発生通知のメールサーバーアドレスを設定する。
     *
     * @param errorMailServer エラー発生通知のメールサーバーアドレス
     */
    public void setErrorMailServer(String errorMailServer) {
        this.errorMailServerProperty.set(errorMailServer);
    }

    /**
     * エラー発生通知のメールポート番号を取得する。
     *
     * @return エラー発生通知のメール送信のポート番号
     */
    public Integer getErrorMailPort() {
        return this.errorMailPortProperty.get();
    }

    /**
     * エラー発生通知のメールポート番号を設定する。
     *
     * @param errorMailPort エラー発生通知のポート番号
     */
    public void setErrorMailPort(Integer errorMailPort) {
        this.errorMailPortProperty.set(errorMailPort);
    }

    /**
     * エラー発生通知のメールユーザー名を取得する。
     *
     * @return エラー発生通知のメールユーザー名
     */
    public String getErrorMailUser() {
        return this.errorMailUserProperty.get();
    }

    /**
     * エラー発生通知のメールユーザー名を設定する。
     *
     * @param errorMailUser エラー発生通知のメールユーザー名
     */
    public void setErrorMailUser(String errorMailUser) {
        this.errorMailUserProperty.set(errorMailUser);
    }

    /**
     * エラー発生通知のメールパスワードを取得する。
     *
     * @return エラー発生通知のメールパスワード
     */
    public String getErrorMailPassword() {
        return this.errorMailPasswordProperty.get();
    }

    /**
     * エラー発生通知のメールパスワードを設定する。
     *
     * @param errorMailPassword エラー発生通知のメールパスワード
     */
    public void setErrorMailPassword(String errorMailPassword) {
        this.errorMailPasswordProperty.set(errorMailPassword);
    }

    /**
     * エラー発生通知の送信者を取得する。
     *
     * @return エラー発生通知の送信者
     */
    public String getErrorMailFrom() {
        return this.errorMailFromProperty.get();
    }

    /**
     * エラー発生通知の送信者を設定する。
     *
     * @param errorMailFrom エラー発生通知の送信者
     */
    public void setErrorMailFrom(String errorMailFrom) {
        this.errorMailFromProperty.set(errorMailFrom);
    }

    /**
     * エラー発生通知の宛先を取得する。
     *
     * @return エラー発生通知の宛先
     */
    public String getErrorMailTo() {
        return this.errorMailToProperty.get();
    }

    /**
     * エラー発生通知の宛先を設定する。
     *
     * @param errorMailTo エラー発生通知の宛先
     */
    public void setErrorMailTo(String errorMailTo) {
        this.errorMailToProperty.set(errorMailTo);
    }

    /**
     * エラー発生通知のタイムアウト時間を取得する。
     *
     * @return エラー発生通知のタイムアウト時間
     */
    public Integer getErrorMailTimeout() {
        return this.errorMailTimeoutProperty.get();
    }

    /**
     * エラー発生通知のタイムアウト時間を設定する。
     *
     * @param errorMailTimeout エラー発生通知のタイムアウト時間
     */
    public void setErrorMailTimeout(Integer errorMailTimeout) {
        this.errorMailTimeoutProperty.set(errorMailTimeout);
    }
}
