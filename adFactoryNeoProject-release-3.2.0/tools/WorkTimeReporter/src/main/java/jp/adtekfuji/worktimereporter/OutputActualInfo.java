/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.worktimereporter;

import adtekfuji.property.AdProperty;
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
    private final String KEY_FOLDER_PATH = "folderPath";
    private final String KEY_USER_NAME = "userName";
    private final String KEY_PASSWORD = "password";
    private final String KEY_READ_FILE_NAME_ADDRESS = "readFileNameAddress";

    //実績出力設定の初期値
    private final String DEF_ADFCTORY_ADDRESS = "http://localhost:8080";
    private final String DEF_UPTAKE_INTERVAL = "24";
    private final String DEF_FOLDER_ADDRESS = "";
    private final String DEF_USER_NAME = "";
    private final String DEF_PASSWORD = "";
    private final String DEF_READ_FILE_NAME_ADDRESS = "";

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
    private final StringProperty folderAddressProperty = new SimpleStringProperty();
    private final StringProperty userNameProperty = new SimpleStringProperty();
    private final StringProperty passwordProperty = new SimpleStringProperty();
    private final StringProperty readFileNameAddressProperty = new SimpleStringProperty();

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
        fromSearchDatetimeProperty.set(Objects.isNull(property.getProperty(KEY_FROM_SEARCH_DATETIME)) || "".equals(property.getProperty(KEY_FROM_SEARCH_DATETIME)) ? null : sdf.parse(property.getProperty(KEY_FROM_SEARCH_DATETIME)));
        toSearchDatetimeProperty.set(Objects.isNull(property.getProperty(KEY_TO_SEARCH_DATETIME)) || "".equals(property.getProperty(KEY_TO_SEARCH_DATETIME)) ? null : sdf.parse(property.getProperty(KEY_TO_SEARCH_DATETIME)));
        folderAddressProperty.set(property.getProperty(KEY_FOLDER_PATH, DEF_FOLDER_ADDRESS));
        userNameProperty.set(property.getProperty(KEY_USER_NAME, DEF_USER_NAME));
        passwordProperty.set(property.getProperty(KEY_PASSWORD, DEF_PASSWORD));
        readFileNameAddressProperty.set(property.getProperty(KEY_READ_FILE_NAME_ADDRESS, DEF_READ_FILE_NAME_ADDRESS));

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
        property.setProperty(KEY_FROM_SEARCH_DATETIME, Objects.isNull(fromSearchDatetimeProperty.get()) ? sdf.format(new Date()) : sdf.format(fromSearchDatetimeProperty.get()));
        property.setProperty(KEY_TO_SEARCH_DATETIME, Objects.isNull(toSearchDatetimeProperty.get()) ? sdf.format(new Date()) : sdf.format(toSearchDatetimeProperty.get()));
        property.setProperty(KEY_FOLDER_PATH, folderAddressProperty.get());
        property.setProperty(KEY_USER_NAME, userNameProperty.get());
        property.setProperty(KEY_PASSWORD, passwordProperty.get());
        property.setProperty(KEY_READ_FILE_NAME_ADDRESS, readFileNameAddressProperty.get());

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
     * 共有フォルダーのアドレスを取得する
     *
     * @return 共有フォルダーアドレス
     */
    public StringProperty getFolderAddressProperty() {
        return this.folderAddressProperty;
    }

    /**
     * 共有フォルダーのアドレスを取得する
     *
     * @return 共有フォルダーアドレス
     */
    public String getFolderAddress() {
        return this.folderAddressProperty.get();
    }

    /**
     * ユーザー名プロパティを取得します。
     *
     * @return ユーザー名を表すStringPropertyオブジェクト
     */
    public StringProperty getUserNameProperty() {
        return this.userNameProperty;
    }

    /**
     * ユーザー名を取得します。
     *
     * @return ユーザー名を表す文字列
     */
    public String getUserName() {
        return this.userNameProperty.get();
    }

    /**
     * passwordPropertyのプロパティを取得します。
     *
     * @return passwordPropertyを表すStringProperty
     */
    public StringProperty getPasswordProperty() {
        return this.passwordProperty;
    }

    /**
     * パスワードを取得します。
     *
     * @return 現在のパスワード
     */
    public String getPassword() {
        return this.passwordProperty.get();
    }


        /**
     * 読み込み対象ファイル名一覧のファイルのアドレスを取得する。
     *
     * @return 読み込み対象ファイル名一覧のファイルのアドレス
     */
    public StringProperty readFileNameAddressProperty() {
        return this.readFileNameAddressProperty;
    }

    /**
     * 読み込み対象ファイル名一覧のファイルのアドレスを取得する。
     *
     * @return 読み込み対象ファイル名一覧のファイルのアドレス
     */
    public String getReadFileNameAddress() {
        return this.readFileNameAddressProperty.get();
    }

    /**
     * 読み込み対象ファイル名一覧のファイルのアドレスを設定する。
     *
     * @param readFileNameAddress 読み込み対象ファイル名一覧のファイルのアドレス
     */
    public void setReadFileNameAddress(String readFileNameAddress) {
        this.readFileNameAddressProperty.set(readFileNameAddress);
    }

    /**
     * エラー発生通知のメールサーバーアドレスを取得する。
     *
     * @return エラー発生通知のメールサーバーアドレス
     */
    public StringProperty getErrorMailServerProperty() {
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
     * エラー発生通知のメールポート番号を取得する。
     *
     * @return エラー発生通知のメール送信のポート番号
     */
    public IntegerProperty getErrorMailPortProperty() {
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
     * エラー発生通知の宛先を取得する。
     *
     * @return エラー発生通知の宛先
     */
    public StringProperty getErrorMailToProperty() {
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
}
