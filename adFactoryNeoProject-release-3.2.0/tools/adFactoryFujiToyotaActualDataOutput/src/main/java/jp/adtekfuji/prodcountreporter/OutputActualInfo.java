/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.prodcountreporter;

import adtekfuji.property.AdProperty;
import adtekfuji.utility.CipherHelper;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;

/**
 * @author ke.yokoi
 */
public class OutputActualInfo {


    // 実績出力設定
    private final String KEY_ADFCTORY_ADDRESS = "adFactoryAddress";
    private final String KEY_UPTAKE_INTERVAL = "uptakeInterval";
    private final String KEY_FROM_SEARCH_DATETIME = "fromSearchDatetime";
    private final String KEY_TO_SEARCH_DATETIME = "toSearchDatetime";
    private final String KEY_LAST_UPDATE_DATETIME = "lastUpdateDate";
    private final String KEY_FROM_LAST_SEARCH_ACTUAL_ID = "fromLastSearchActualId";
    private final String KEY_MAIN_WORKER = "mainWorker";

    private final String DEF_LAST_SEARCH_ID = "0";

    private final String KEY_HTTP_ADDRESS = "httpAddressProperty";

    //実績出力設定の初期値
    private final String DEF_ADFCTORY_ADDRESS = "localhost";
    private final String DEF_UPTAKE_INTERVAL = "24";
    private final String DEF_HTTP_ADDRESS = "127.0.0.1";
    private final String DEF_MAIN_WORKER = "0000";

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


    private Properties property;
    private final StringProperty adFactoryAddressProperty = new SimpleStringProperty();
    private final IntegerProperty uptakeIntervalProperty = new SimpleIntegerProperty();
    private final ObjectProperty<Date> fromSearchDatetimeProperty = new SimpleObjectProperty();
    private final ObjectProperty<Date> toSearchDatetimeProperty = new SimpleObjectProperty();
    private final ObjectProperty<Date> lastUpdateDateTimeProperty = new SimpleObjectProperty();
    private final ObjectProperty<Date> bkLastUpdateDateTimeProperty = new SimpleObjectProperty<>();
    private final StringProperty fromLastSearchActualId = new SimpleStringProperty();
    private final StringProperty httpAddressProperty = new SimpleStringProperty();
    private final StringProperty mainWorkerProperty = new SimpleStringProperty();

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    // エラー発生通知メールの設定
    private final StringProperty errorMailServerProperty = new SimpleStringProperty();
    private final IntegerProperty errorMailPortProperty = new SimpleIntegerProperty();
    private final StringProperty errorMailUserProperty = new SimpleStringProperty();
    private final StringProperty errorMailPasswordProperty = new SimpleStringProperty();
    private final StringProperty errorMailFromProperty = new SimpleStringProperty();
    private final StringProperty errorMailToProperty = new SimpleStringProperty();
    private final IntegerProperty errorMailTimeoutProperty = new SimpleIntegerProperty();

    public OutputActualInfo() {
    }

    public void load() throws Exception {
        property = AdProperty.getProperties();
        adFactoryAddressProperty.set(property.getProperty(KEY_ADFCTORY_ADDRESS, DEF_ADFCTORY_ADDRESS));
        uptakeIntervalProperty.set(Integer.parseInt(property.getProperty(KEY_UPTAKE_INTERVAL, DEF_UPTAKE_INTERVAL)));
        fromSearchDatetimeProperty.set(sdf.parse(property.getProperty(KEY_FROM_SEARCH_DATETIME, sdf.format(new Date()))));
        toSearchDatetimeProperty.set(sdf.parse(property.getProperty(KEY_TO_SEARCH_DATETIME, sdf.format(new Date()))));
        lastUpdateDateTimeProperty.set(sdf.parse(property.getProperty(KEY_LAST_UPDATE_DATETIME, sdf.format(new Date()))));
        bkLastUpdateDateTimeProperty.set(lastUpdateDateTimeProperty.get());
        fromLastSearchActualId.set(property.getProperty(KEY_FROM_LAST_SEARCH_ACTUAL_ID, DEF_LAST_SEARCH_ID));
        httpAddressProperty.set(property.getProperty(KEY_HTTP_ADDRESS, DEF_HTTP_ADDRESS));
        mainWorkerProperty.set(property.getProperty(KEY_MAIN_WORKER, DEF_MAIN_WORKER));

        // エラー発生通知メールの設定
        errorMailServerProperty.set(property.getProperty(KEY_ERROR_MAIL_SERVER, DEF_ERROR_MAIL_SERVER));
        errorMailPortProperty.set(Integer.parseInt(property.getProperty(KEY_ERROR_MAIL_PORT, DEF_ERROR_MAIL_PORT)));
        errorMailUserProperty.set(property.getProperty(KEY_ERROR_MAIL_USER, DEF_ERROR_MAIL_USER));
        errorMailPasswordProperty.set(property.getProperty(KEY_ERROR_MAIL_PASSWORD, DEF_ERROR_MAIL_PASSWORD));
        errorMailFromProperty.set(property.getProperty(KEY_ERROR_MAIL_FROM, DEF_ERROR_MAIL_FROM));
        errorMailToProperty.set(property.getProperty(KEY_ERROR_MAIL_TO, DEF_ERROR_MAIL_TO));
        errorMailTimeoutProperty.set(Integer.parseInt(property.getProperty(KEY_ERROR_MAIL_TIMEOUT, DEF_ERROR_MAIL_TIMEOUT)));
    }

    public boolean save() {
        try {
            property.setProperty(KEY_ADFCTORY_ADDRESS, adFactoryAddressProperty.get());
            property.setProperty(KEY_UPTAKE_INTERVAL, String.valueOf(uptakeIntervalProperty.get()));
            property.setProperty(KEY_FROM_SEARCH_DATETIME, sdf.format(fromSearchDatetimeProperty.get()));
            property.setProperty(KEY_TO_SEARCH_DATETIME, sdf.format(toSearchDatetimeProperty.get()));
            property.setProperty(KEY_LAST_UPDATE_DATETIME, sdf.format(lastUpdateDateTimeProperty.get()));
            property.setProperty(KEY_FROM_LAST_SEARCH_ACTUAL_ID, fromLastSearchActualId.get());
            property.setProperty(KEY_HTTP_ADDRESS, httpAddressProperty.get());
            property.setProperty(KEY_MAIN_WORKER, mainWorkerProperty.get());

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
        }catch (Exception ex) {
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

    public IntegerProperty uptakeIntervalProperty() {
        return uptakeIntervalProperty;
    }

    public Integer getUptakeInterval() {
        return uptakeIntervalProperty.get();
    }

    public void setUptakeInterval(Integer uptakeInterval) {
        this.uptakeIntervalProperty.set(uptakeInterval);
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
        return this.bkLastUpdateDateTimeProperty.get().equals(this.lastUpdateTimeProperty().get());
    }

    public StringProperty httpAddressProperty() {
        return httpAddressProperty;
    }

    public String getHttpAddress() {
        return httpAddressProperty.get();
    }

    public void setHttpAddress(String ftpAddress) {
        this.httpAddressProperty.set(ftpAddress);
    }


    public StringProperty errorMailServerProperty() { return this.errorMailServerProperty; }

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

    public IntegerProperty errorMailPortProperty() { return this.errorMailPortProperty; }

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

    StringProperty errorMailToProperty() { return this.errorMailToProperty; }

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
     * 作業者を取得
     * @return 作業者
     */
    public String getMainWorker() { return mainWorkerProperty.get(); }

    /**
     * 作業者を設定
     * @param mainWorker　作業者
     */
    public void setMainWorker(String mainWorker) { this.mainWorkerProperty.set(mainWorker);}

}
