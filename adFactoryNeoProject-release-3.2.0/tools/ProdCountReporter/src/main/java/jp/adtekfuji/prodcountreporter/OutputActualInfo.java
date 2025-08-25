/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.prodcountreporter;

import adtekfuji.property.AdProperty;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Objects;

import javafx.beans.property.*;
import org.apache.logging.log4j.LogManager;

/**
 * @author y.yamashita
 */
public class OutputActualInfo {

    // 実績出力設定
    private final String KEY_ADFCTORY_ADDRESS = "adFactoryAddress";
    private final String KEY_FROM_SEARCH_DATETIME = "fromSearchDatetime";
    private final String KEY_TO_SEARCH_DATETIME = "toSearchDatetime";
    private final String KEY_LAST_UPDATE_DATETIME = "lastUpdateDate";
    private final String KEY_LAST_SEARCHED_ACTUAL_ID = "lastSearchedActualid";
    private final String KEY_AUTH_USER = "authUser";
    private final String KEY_AUTH_PASSWORD = "authPassword";
    private final String KEY_MAX_RETRY = "maxRetry";
    private final String KEY_NUM_INTERVAL = "numInterval";

    private final String DEF_LAST_SEARCH_ID = "0";
    private final String DEF_AUTH_USER = "";
    private final String DEF_AUTH_PASSWORD = "";
    private final String DEF_MAX_RETRY = "3";
    private final String DEF_NUM_INTERVAL = "3";

    private final String KEY_HTTP_UPLOAD_PATH = "httpUploadPath";

    //実績出力設定の初期値
    private final String DEF_ADFCTORY_ADDRESS = "localhost";
    private final String DEF_HTTP_ADDRESS = "";

    // エラー発生通知メールの設定
    private final String KEY_ERROR_MAIL_SERVER = "errorMailServer";// メールサーバー
    private final String KEY_ERROR_MAIL_PORT = "errorMailPort";// ポート番号
    private final String KEY_ERROR_MAIL_USER = "errorMailUser";// ユーザー
    private final String KEY_ERROR_MAIL_PASSWORD = "errorMailPassword";// パスワード
    private final String KEY_ERROR_MAIL_FROM = "errorMailFrom";// 送信者
    private final String KEY_ERROR_MAIL_TO = "errorMailTo";// 宛先
    private final String KEY_ERROR_MAIL_TIMEOUT = "errorMailTimeout";// タイムアウト時間
    private final String KEY_ERROR_MAIL_START_TLS_ENABLE = "startTLSEnable"; // STARTTLSコマンドによる暗号化するか？
    // エラー発生通知メールの設定の初期値
    private final String DEF_ERROR_MAIL_SERVER = "";
    private final String DEF_ERROR_MAIL_PORT = "587";
    private final String DEF_ERROR_MAIL_USER = "";
    private final String DEF_ERROR_MAIL_PASSWORD = "";
    private final String DEF_ERROR_MAIL_FROM = "support@adtek-fuji.co.jp";
    private final String DEF_ERROR_MAIL_TO = "support@adtek-fuji.co.jp";
    private final String DEF_ERROR_MAIL_TIMEOUT = "30";

    private Properties property;
    private final StringProperty adFactoryAddressProperty = new SimpleStringProperty();
    private final ObjectProperty<Date> fromSearchDatetimeProperty = new SimpleObjectProperty();
    private final ObjectProperty<Date> toSearchDatetimeProperty = new SimpleObjectProperty();
    private final ObjectProperty<Date> lastUpdateDateTimeProperty = new SimpleObjectProperty();
    private final ObjectProperty<Date> bkLastUpdateDateTimeProperty = new SimpleObjectProperty<>();
    private final StringProperty fromLastSearchActualId = new SimpleStringProperty();
    private final StringProperty httpUploadPathProperty = new SimpleStringProperty();
    private final StringProperty authUserProperty = new SimpleStringProperty();
    private final StringProperty authPasswordProperty = new SimpleStringProperty();
    private final StringProperty maxRetryProperty = new SimpleStringProperty();
    private final StringProperty numIntervalProperty = new SimpleStringProperty();

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    // エラー発生通知メールの設定
    private final StringProperty errorMailServerProperty = new SimpleStringProperty();
    private final IntegerProperty errorMailPortProperty = new SimpleIntegerProperty();
    private final StringProperty errorMailUserProperty = new SimpleStringProperty();
    private final StringProperty errorMailPasswordProperty = new SimpleStringProperty();
    private final StringProperty errorMailFromProperty = new SimpleStringProperty();
    private final StringProperty errorMailToProperty = new SimpleStringProperty();
    private final IntegerProperty errorMailTimeoutProperty = new SimpleIntegerProperty();
    private final BooleanProperty errorMailStartTLSEnableProperty = new SimpleBooleanProperty();

    public OutputActualInfo() {
    }

    public void load() throws Exception {
        property = AdProperty.getProperties();
        adFactoryAddressProperty.set(property.getProperty(KEY_ADFCTORY_ADDRESS, DEF_ADFCTORY_ADDRESS));
        fromSearchDatetimeProperty.set((Objects.isNull(property.getProperty(KEY_FROM_SEARCH_DATETIME)) || "".equals(property.getProperty(KEY_FROM_SEARCH_DATETIME))) ? null : sdf.parse(property.getProperty(KEY_FROM_SEARCH_DATETIME)));
        toSearchDatetimeProperty.set((Objects.isNull(property.getProperty(KEY_TO_SEARCH_DATETIME)) || "".equals(property.getProperty(KEY_TO_SEARCH_DATETIME))) ? null : sdf.parse(property.getProperty(KEY_TO_SEARCH_DATETIME)));
        lastUpdateDateTimeProperty.set((Objects.isNull(property.getProperty(KEY_LAST_UPDATE_DATETIME)) || "".equals(property.getProperty(KEY_LAST_UPDATE_DATETIME))) ? null : sdf.parse(property.getProperty(KEY_LAST_UPDATE_DATETIME)));
        bkLastUpdateDateTimeProperty.set(lastUpdateDateTimeProperty.get());
        fromLastSearchActualId.set(property.getProperty(KEY_LAST_SEARCHED_ACTUAL_ID, DEF_LAST_SEARCH_ID));
        httpUploadPathProperty.set(property.getProperty(KEY_HTTP_UPLOAD_PATH, DEF_HTTP_ADDRESS));
        authUserProperty.set(property.getProperty(KEY_AUTH_USER, DEF_AUTH_USER));
        authPasswordProperty.set(property.getProperty(KEY_AUTH_PASSWORD, DEF_AUTH_PASSWORD));
        maxRetryProperty.set(property.getProperty(KEY_MAX_RETRY, DEF_MAX_RETRY));
        numIntervalProperty.set(property.getProperty(KEY_NUM_INTERVAL, DEF_NUM_INTERVAL));

        // エラー発生通知メールの設定
        errorMailServerProperty.set(property.getProperty(KEY_ERROR_MAIL_SERVER, DEF_ERROR_MAIL_SERVER));
        errorMailPortProperty.set(Integer.parseInt(property.getProperty(KEY_ERROR_MAIL_PORT, DEF_ERROR_MAIL_PORT)));
        errorMailUserProperty.set(property.getProperty(KEY_ERROR_MAIL_USER, DEF_ERROR_MAIL_USER));
        errorMailPasswordProperty.set(property.getProperty(KEY_ERROR_MAIL_PASSWORD, DEF_ERROR_MAIL_PASSWORD));
        errorMailFromProperty.set(property.getProperty(KEY_ERROR_MAIL_FROM, DEF_ERROR_MAIL_FROM));
        errorMailToProperty.set(property.getProperty(KEY_ERROR_MAIL_TO, DEF_ERROR_MAIL_TO));
        errorMailTimeoutProperty.set(Integer.parseInt(property.getProperty(KEY_ERROR_MAIL_TIMEOUT, DEF_ERROR_MAIL_TIMEOUT)));
        errorMailStartTLSEnableProperty.set(Boolean.parseBoolean(property.getProperty(KEY_ERROR_MAIL_START_TLS_ENABLE, "false")));
    }

    public boolean save() {
        try {
            property.setProperty(KEY_ADFCTORY_ADDRESS, adFactoryAddressProperty.get());
            property.setProperty(KEY_FROM_SEARCH_DATETIME, Objects.isNull(fromSearchDatetimeProperty.get()) ? sdf.format(new Date()) : sdf.format(fromSearchDatetimeProperty.get()));
            property.setProperty(KEY_TO_SEARCH_DATETIME, Objects.isNull(toSearchDatetimeProperty.get()) ? sdf.format(new Date()) : sdf.format(toSearchDatetimeProperty.get()));
            property.setProperty(KEY_LAST_UPDATE_DATETIME, Objects.isNull(lastUpdateDateTimeProperty.get()) ? sdf.format(new Date()) : sdf.format(lastUpdateDateTimeProperty.get()));
            property.setProperty(KEY_LAST_SEARCHED_ACTUAL_ID, fromLastSearchActualId.get());
            property.setProperty(KEY_HTTP_UPLOAD_PATH, httpUploadPathProperty.get());
            property.setProperty(KEY_AUTH_USER, authUserProperty.get());
            property.setProperty(KEY_AUTH_PASSWORD, authPasswordProperty.get());
            property.setProperty(KEY_MAX_RETRY, maxRetryProperty.get());
            property.setProperty(KEY_NUM_INTERVAL, numIntervalProperty.get());

            // エラー発生通知メールの設定
            property.setProperty(KEY_ERROR_MAIL_SERVER, errorMailServerProperty.get());
            property.setProperty(KEY_ERROR_MAIL_PORT, String.valueOf(errorMailPortProperty.get()));
            property.setProperty(KEY_ERROR_MAIL_USER, errorMailUserProperty.get());
            property.setProperty(KEY_ERROR_MAIL_PASSWORD, errorMailPasswordProperty.get());
            property.setProperty(KEY_ERROR_MAIL_FROM, errorMailFromProperty.get());
            property.setProperty(KEY_ERROR_MAIL_TO, errorMailToProperty.get());
            property.setProperty(KEY_ERROR_MAIL_TIMEOUT, String.valueOf(errorMailTimeoutProperty.get()));

            AdProperty.store();
            return true;
        } catch (Exception ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
        return false;
    }

    public StringProperty adFactoryAddressProperty() {
        return adFactoryAddressProperty;
    }

    public String getAdFactoryAddress() {
        return adFactoryAddressProperty.get();
    }

    public void setAdFactoryServerAddress(String adFactoryServerAddress) {
        this.adFactoryAddressProperty.set(adFactoryServerAddress);
    }

    public ObjectProperty<Date> fromSearchDatetimeProperty() {
        return fromSearchDatetimeProperty;
    }

    public Date getFromSearchDatetime() {
        return fromSearchDatetimeProperty.get();
    }

    public void setFromSearchDatetime(Date fromSearchDatetime) {
        this.fromSearchDatetimeProperty.set(fromSearchDatetime);
    }

    public ObjectProperty<Date> toSearchDatetimeProperty() {
        return toSearchDatetimeProperty;
    }

    public Date getToSearchDatetime() {
        return toSearchDatetimeProperty.get();
    }

    public void setToSearchDatetimeProperty(Date toSearchDatetime) {
        this.toSearchDatetimeProperty.set(toSearchDatetime);
    }

    public ObjectProperty<Date> lastUpdateTimeProperty() {
        return this.lastUpdateDateTimeProperty;
    }

    public Date getLastUpdateDateTime() {
        return this.lastUpdateDateTimeProperty.get();
    }

    public void setLastUpdateDateTime(Date lastUpdateDateTime) {
        this.lastUpdateDateTimeProperty.set(lastUpdateDateTime);
    }

    public String getFromLastSearchActualId() {
        return this.fromLastSearchActualId.get();
    }

    public void setFromLastSearchActualId(String fromLastSearchActualId) {
        this.fromLastSearchActualId.set(fromLastSearchActualId);
    }

    public boolean isNotChangedLastUpdateTime() {
        return Objects.equals(this.bkLastUpdateDateTimeProperty.get(), this.lastUpdateTimeProperty().get());
    }

    public StringProperty httpAddressProperty() {
        return httpUploadPathProperty;
    }

    public String getHttpAddress() {
        return httpUploadPathProperty.get();
    }

    public void setHttpAddress(String ftpAddress) {
        this.httpUploadPathProperty.set(ftpAddress);
    }

    /**
     * 認証ユーザ名プロパティを取得する
     *
     * @return 認証ユーザ名プロパティ
     */
    public StringProperty authUserProperty() {
        return this.authUserProperty;
    }

    /**
     * 認証ユーザ名を取得する
     *
     * @return 認証ユーザ名
     */
    public String getAuthUser() {
        return this.authUserProperty.get();
    }

    /**
     * 認証パスワードプロパティを取得する
     *
     * @return 認証パスワードプロパティ
     */
    public StringProperty authPasswordProperty() {
        return this.authPasswordProperty;
    }

    /**
     * 認証パスワードを取得する
     *
     * @return 認証パスワード
     */
    public String getAuthPassword() {
        return this.authPasswordProperty.get();
    }

    /**
     * リトライ回数プロパティを取得する
     *
     * @return リトライ回数プロパティ
     */
    public StringProperty maxRetryProperty() {
        return this.maxRetryProperty;
    }

    /**
     * リトライ回数を取得する
     *
     * @return リトライ回数
     */
    public String getMaxRetry() {
        return this.maxRetryProperty.get();
    }

    /**
     * インターバルプロパティを取得する
     *
     * @return リトライ回数プロパティ
     */
    public StringProperty numIntervalProperty() {
        return this.numIntervalProperty;
    }

    /**
     * インターバルを取得する
     *
     * @return インターバル
     */
    public String getNumInterval() {
        return this.numIntervalProperty.get();
    }

    /**
     * エラー発生通知のメールサーバプロパティを取得する
     *
     * @return エラー発生通知のメールサーバーアドレスプロパティ
     */
    public StringProperty errorMailServerProperty() {
        return this.errorMailServerProperty;
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
     * エラー発生通知のメールポート番号プロパティを取得する
     *
     * @return エラー発生通知のメールポート番号プロパティ
     */
    public IntegerProperty errorMailPortProperty() {
        return this.errorMailPortProperty;
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
     * エラー発生通知の宛先プロパティを取得する
     *
     * @return エラー発生通知の宛先プロパティ
     */
    StringProperty errorMailToProperty() {
        return this.errorMailToProperty;
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

    /**
     * エラー発生通知のメール送信時にStartTLSを有効化するかどうかを取得します。
     *
     * @return StartTLSが有効化されている場合はtrue、無効化されている場合はfalse
     */
    public Boolean getErrorMailStartTLSEnable() {
        return this.errorMailStartTLSEnableProperty.get();
    }

    /**
     * エラー発生通知のメール送信時にStartTLSを有効化するかどうかを設定します。
     *
     * @param startTLSEnable StartTLSを有効化する場合はtrue、無効化する場合はfalse
     */
    public void setErrorMailStartTLSEnable(Boolean startTLSEnable) {
        this.errorMailStartTLSEnableProperty.set(startTLSEnable);
    }
}
