/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryasprovaactualdataoutput;

import adtekfuji.property.AdProperty;
import java.io.IOException;
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
 * 実績出力設定
 * 
 * @author koga
 */
public class OutputActualInfo {

    // 実績出力設定
    private final String KEY_ADFCTORY_ADDRESS = "adFactoryAddress";             // ADFACTORYサーバーアドレス
    private final String KEY_FROM_SEARCH_DATETIME = "fromSearchDatetime";       // 検索範囲日時（FROM）
    private final String KEY_TO_SEARCH_DATETIME = "toSearchDatetime";           // 検索範囲日時（TO）
    private final String KEY_LAST_SEARCHED_DATETIME = "lastSearchedDatetime";   // 前回実行日時
    private final String KEY_WORK_LAST_SEARCHED_DATETIME = "workLastSearchedDatetime";  // 前回実行日時（工程の作業コード出力用）
    private final String KEY_START_WORK_NAME ="startWorkName";                  // 開始工程名
    private final String KEY_END_WORK_NAME ="endWorkName";                      // 終了工程名
    private final String KEY_LAST_SEARCHED_ACTUAL_ID = "lastSearchedActualId";  // 前回検索実績ID
    private final String KEY_WORK_LAST_SEARCHED_ACTUAL_ID = "workLastSearchedActualId"; // 前回検索実績ID（工程の作業コード出力用）
    private final String KEY_FOLDER_PATH = "folderPath";                        // フォルダーパス
    private final String KEY_ERROR_MAIL_SERVER = "errorMailServer";             // メールサーバー
    private final String KEY_ERROR_MAIL_PORT = "errorMailPort";                 // メールポート番号
    private final String KEY_ERROR_MAIL_USER = "errorMailUser";                 // メールユーザー
    private final String KEY_ERROR_MAIL_PASSWORD = "errorMailPassword";         // メールパスワード
    private final String KEY_ERROR_MAIL_TIMEOUT = "errorMailTimeout";           // タイムアウト時間
    private final String KEY_ERROR_MAIL_FROM = "errorMailFrom";                 // メール送信者
    private final String KEY_ERROR_MAIL_TO = "errorMailTo";                     // メール宛先

    // 初期値
    private final String DEF_ADFCTORY_ADDRESS = "localhost";                    // ADFACTORYサーバーアドレス
    private final String DEF_START_WORK_NAME ="前段取";                          // 開始工程名
    private final String DEF_END_WORK_NAME ="後段取";                            // 終了工程名
    private final String DEF_LAST_SEARCHED_ACTUAL_ID = "0";                     // 前回検索実績ID
    private final String DEF_FOLDER_PATH = "";                                  // フォルダーパス
    private final String DEF_ERROR_MAIL_SERVER = "";                            // メールサーバー
    private final String DEF_ERROR_MAIL_PORT = "587";                           // メールポート番号
    private final String DEF_ERROR_MAIL_USER = "";                              // メールユーザー
    private final String DEF_ERROR_MAIL_PASSWORD = "";                          // メールパスワード
    private final String DEF_ERROR_MAIL_TIMEOUT = "30";                         // タイムアウト時間
    private final String DEF_ERROR_MAIL_FROM = "support@adtek-fuji.co.jp";      // メール送信者
    private final String DEF_ERROR_MAIL_TO = "support@adtek-fuji.co.jp";        // メール宛先

    // プロパティ設定
    private Properties property;
    private final StringProperty adFactoryAddressProperty = new SimpleStringProperty();
    private final ObjectProperty<Date> fromSearchDatetimeProperty = new SimpleObjectProperty();
    private final ObjectProperty<Date> toSearchDatetimeProperty = new SimpleObjectProperty();
    private final ObjectProperty<Date> lastSearchedDatetimeProperty = new SimpleObjectProperty();
    private final ObjectProperty<Date> bkLastSearchedDatetimeProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> workLastSearchedDatetimeProperty = new SimpleObjectProperty();
    private final ObjectProperty<Date> bkWorkLastSearchedDatetimeProperty = new SimpleObjectProperty<>();
    private final StringProperty startWorkNameProperty = new SimpleStringProperty();
    private final StringProperty endWorkNameProperty = new SimpleStringProperty();
    private final StringProperty lastSearchedActualIdProperty = new SimpleStringProperty();
    private final StringProperty workLastSearchedActualIdProperty = new SimpleStringProperty();
    private final StringProperty folderPathProperty = new SimpleStringProperty();
    private final StringProperty errorMailServerProperty = new SimpleStringProperty();
    private final IntegerProperty errorMailPortProperty = new SimpleIntegerProperty();
    private final StringProperty errorMailUserProperty = new SimpleStringProperty();
    private final StringProperty errorMailPasswordProperty = new SimpleStringProperty();
    private final IntegerProperty errorMailTimeoutProperty = new SimpleIntegerProperty();
    private final StringProperty errorMailFromProperty = new SimpleStringProperty();
    private final StringProperty errorMailToProperty = new SimpleStringProperty();

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * コンストラクタ
     */
    public OutputActualInfo() {
    }

    /**
     * 実績出力設定読み込み
     * 
     * @throws java.lang.Exception
     */
    public void load() throws Exception {
        property = AdProperty.getProperties();
        // ADFACTORYサーバーアドレス
        adFactoryAddressProperty.set(property.getProperty(KEY_ADFCTORY_ADDRESS, DEF_ADFCTORY_ADDRESS));
        // 検索範囲日時（FROM）
        fromSearchDatetimeProperty.set(sdf.parse(property.getProperty(KEY_FROM_SEARCH_DATETIME, sdf.format(new Date()))));
        // 検索範囲日時（TO）
        toSearchDatetimeProperty.set(sdf.parse(property.getProperty(KEY_TO_SEARCH_DATETIME, sdf.format(new Date()))));
        // 前回実行日時
        lastSearchedDatetimeProperty.set(sdf.parse(property.getProperty(KEY_LAST_SEARCHED_DATETIME, sdf.format(new Date()))));
        bkLastSearchedDatetimeProperty.set(lastSearchedDatetimeProperty.get());
        // 前回実行日時（工程の作業コード出力用）
        workLastSearchedDatetimeProperty.set(sdf.parse(property.getProperty(KEY_WORK_LAST_SEARCHED_DATETIME, sdf.format(new Date()))));
        bkWorkLastSearchedDatetimeProperty.set(workLastSearchedDatetimeProperty.get());
        // 開始工程名
        startWorkNameProperty.set(property.getProperty(KEY_START_WORK_NAME, DEF_START_WORK_NAME));
        // 終了工程名
        endWorkNameProperty.set(property.getProperty(KEY_END_WORK_NAME, DEF_END_WORK_NAME));
        // 前回検索実績ID
        lastSearchedActualIdProperty.set(property.getProperty(KEY_LAST_SEARCHED_ACTUAL_ID, DEF_LAST_SEARCHED_ACTUAL_ID));
        // 前回検索実績ID（工程の作業コード出力用）
        workLastSearchedActualIdProperty.set(property.getProperty(KEY_WORK_LAST_SEARCHED_ACTUAL_ID, DEF_LAST_SEARCHED_ACTUAL_ID));
        // フォルダーパス
        folderPathProperty.set(property.getProperty(KEY_FOLDER_PATH, DEF_FOLDER_PATH));
        // メールサーバー
        errorMailServerProperty.set(property.getProperty(KEY_ERROR_MAIL_SERVER, DEF_ERROR_MAIL_SERVER));
        // メールポート番号
        errorMailPortProperty.set(Integer.parseInt(property.getProperty(KEY_ERROR_MAIL_PORT, DEF_ERROR_MAIL_PORT)));
        // メールユーザー
        errorMailUserProperty.set(property.getProperty(KEY_ERROR_MAIL_USER, DEF_ERROR_MAIL_USER));
        // メールパスワード
        errorMailPasswordProperty.set(property.getProperty(KEY_ERROR_MAIL_PASSWORD, DEF_ERROR_MAIL_PASSWORD));
        // タイムアウト時間
        errorMailTimeoutProperty.set(Integer.parseInt(property.getProperty(KEY_ERROR_MAIL_TIMEOUT, DEF_ERROR_MAIL_TIMEOUT)));
        // メール送信者
        errorMailFromProperty.set(property.getProperty(KEY_ERROR_MAIL_FROM, DEF_ERROR_MAIL_FROM));
        // メール宛先
        errorMailToProperty.set(property.getProperty(KEY_ERROR_MAIL_TO, DEF_ERROR_MAIL_TO));
    }

    /**
     * 実績出力設定保存
     * 
     * @return true:成功、false:失敗
     */
    public boolean save() {
        try {
            // ADFACTORYサーバーアドレス
            property.setProperty(KEY_ADFCTORY_ADDRESS, adFactoryAddressProperty.get());
            // 検索範囲日時（FROM）
            property.setProperty(KEY_FROM_SEARCH_DATETIME, sdf.format(fromSearchDatetimeProperty.get()));
            // 検索範囲日時（TO）
            property.setProperty(KEY_TO_SEARCH_DATETIME, sdf.format(toSearchDatetimeProperty.get()));
            // 前回実行日時
            property.setProperty(KEY_LAST_SEARCHED_DATETIME, sdf.format(lastSearchedDatetimeProperty.get()));
            // 前回実行日時（工程の作業コード出力用）
            property.setProperty(KEY_WORK_LAST_SEARCHED_DATETIME, sdf.format(workLastSearchedDatetimeProperty.get()));
            // 開始工程名
            property.setProperty(KEY_START_WORK_NAME, startWorkNameProperty.get());
            // 終了工程名
            property.setProperty(KEY_END_WORK_NAME, endWorkNameProperty.get());
            // 前回検索実績ID
            property.setProperty(KEY_LAST_SEARCHED_ACTUAL_ID, lastSearchedActualIdProperty.get());
            // 前回検索実績ID（工程の作業コード出力用）
            property.setProperty(KEY_WORK_LAST_SEARCHED_ACTUAL_ID, workLastSearchedActualIdProperty.get());
            // フォルダーパス
            property.setProperty(KEY_FOLDER_PATH, folderPathProperty.get());
            // メールサーバー
            property.setProperty(KEY_ERROR_MAIL_SERVER, errorMailServerProperty.get());
            // メールポート番号
            property.setProperty(KEY_ERROR_MAIL_PORT, String.valueOf(errorMailPortProperty.get()));
            // メールユーザー
            property.setProperty(KEY_ERROR_MAIL_USER, errorMailUserProperty.get());
            // メールパスワード
            property.setProperty(KEY_ERROR_MAIL_PASSWORD, errorMailPasswordProperty.get());
            // タイムアウト時間
            property.setProperty(KEY_ERROR_MAIL_TIMEOUT, String.valueOf(errorMailTimeoutProperty.get()));
            // メール送信者
            property.setProperty(KEY_ERROR_MAIL_FROM, errorMailFromProperty.get());
            // メール宛先
            property.setProperty(KEY_ERROR_MAIL_TO, errorMailToProperty.get());

            AdProperty.store();
            return true;
        }catch (IOException ex) {
            LogManager.getLogger().fatal(ex, ex);
            return false;
        }
    }

    /**
     * ADFACTORYサーバーアドレスプロパティを取得する。
     *
     * @return ADFACTORYサーバーアドレスプロパティ
     */
    public StringProperty adFactoryAddressProperty() {
        return adFactoryAddressProperty;
    }

    /**
     * ADFACTORYサーバーアドレスを取得する。
     *
     * @return ADFACTORYサーバーアドレス
     */
    public String getAdFactoryAddress() {
        return this.adFactoryAddressProperty.get();
    }

    /**
     * ADFACTORYサーバーアドレスを設定する。
     *
     * @param adFactoryAddress ADFACTORYサーバーアドレス
     */
    public void setAdFactoryAddress(String adFactoryAddress) {
        this.adFactoryAddressProperty.set(adFactoryAddress);
    }

    /**
     * 検索範囲日時（FROM）プロパティを取得する。
     *
     * @return 検索範囲日時（FROM）プロパティ
     */
    public ObjectProperty<Date> fromSearchDatetimeProperty() {
        return fromSearchDatetimeProperty;
    }

    /**
     * 検索範囲日時（FROM）を取得する。
     *
     * @return 検索範囲日時（FROM）
     */
    public Date getFromSearchDatetime() {
        return fromSearchDatetimeProperty.get();
    }

    /**
     * 検索範囲日時（FROM）を設定する。
     *
     * @param fromSearchDatetime 検索範囲日時（FROM）
     */
    public void setFromSearchDatetime(Date fromSearchDatetime) {
        this.fromSearchDatetimeProperty.set(fromSearchDatetime);
    }

    /**
     * 検索範囲日時（TO）プロパティを取得する。
     *
     * @return 検索範囲日時（TO）プロパティ
     */
    public ObjectProperty<Date> toSearchDatetimeProperty() {
        return toSearchDatetimeProperty;
    }

    /**
     * 検索範囲日時（TO）を取得する。
     *
     * @return 検索範囲日時（TO）
     */
    public Date getToSearchDatetime() {
        return toSearchDatetimeProperty.get();
    }

    /**
     * 検索範囲日時（TO）を設定する。
     *
     * @param toSearchDatetime 検索範囲日時（TO）
     */
    public void setToSearchDatetimeProperty(Date toSearchDatetime) {
        this.toSearchDatetimeProperty.set(toSearchDatetime);
    }

    /**
     * 前回実行日時プロパティを取得する。
     *
     * @return 前回実行日時プロパティ
     */
    public ObjectProperty<Date> lastSearchedDatetimeProperty() {
        return this.lastSearchedDatetimeProperty;
    }

    /**
     * 前回実行日時を取得する。
     *
     * @return 前回実行日時
     */
    public Date getLastSearchedDatetime() {
        return this.lastSearchedDatetimeProperty.get();
    }

    /**
     * 前回実行日時を設定する。
     *
     * @param lastSearchedDatetime 前回実行日時
     */
    public void setLastSearchedDatetime(Date lastSearchedDatetime) {
        this.lastSearchedDatetimeProperty.set(lastSearchedDatetime);
    }

    /**
     * 前回実行日時の更新を判定する。
     *
     * @return true:更新しない、false:更新する
     */
    public boolean isNotChangedSearchedDatetime() {
        return this.bkLastSearchedDatetimeProperty.get().equals(this.lastSearchedDatetimeProperty().get());
    }

    /**
     * 前回実行日時（工程の作業コード出力用）プロパティを取得する。
     *
     * @return 前回実行日時（工程の作業コード出力用）プロパティ
     */
    public ObjectProperty<Date> workLastSearchedDatetimeProperty() {
        return this.workLastSearchedDatetimeProperty;
    }

    /**
     * 前回実行日時（工程の作業コード出力用）を取得する。
     *
     * @return 前回実行日時（工程の作業コード出力用）
     */
    public Date getWorkLastSearchedDatetime() {
        return this.workLastSearchedDatetimeProperty.get();
    }

    /**
     * 前回実行日時（工程の作業コード出力用）を設定する。
     *
     * @param workLastSearchedDatetime 前回実行日時（工程の作業コード出力用）
     */
    public void setWorkLastSearchedDatetime(Date workLastSearchedDatetime) {
        this.workLastSearchedDatetimeProperty.set(workLastSearchedDatetime);
    }

    /**
     * 前回実行日時（工程の作業コード出力用）の更新を判定する。
     *
     * @return true:更新しない、false:更新する
     */
    public boolean isNotChangedWorkSearchedDatetime() {
        return this.bkWorkLastSearchedDatetimeProperty.get().equals(this.workLastSearchedDatetimeProperty().get());
    }

    /**
     * 開始工程名プロパティを取得する。
     *
     * @return 開始工程名プロパティ
     */
    public StringProperty startWorkNameProperty() {
        return startWorkNameProperty;
    }

    /**
     * 開始工程名を取得する。
     *
     * @return 開始工程名
     */
    public String getStartWorkName() {
        return this.startWorkNameProperty.get();
    }

    /**
     * 開始工程名を設定する。
     *
     * @param startWorkName 開始工程名
     */
    public void setStartWorkName(String startWorkName) {
        this.startWorkNameProperty.set(startWorkName);
    }

    /**
     * 終了工程名プロパティを取得する。
     *
     * @return 終了工程名プロパティ
     */
    public StringProperty endWorkNameProperty() {
        return endWorkNameProperty;
    }

    /**
     * 終了工程名を取得する。
     *
     * @return 終了工程名
     */
    public String getEndWorkName() {
        return this.endWorkNameProperty.get();
    }

    /**
     * 終了工程名を設定する。
     *
     * @param endWorkName 終了工程名
     */
    public void setEndWorkName(String endWorkName) {
        this.endWorkNameProperty.set(endWorkName);
    }

    /**
     * 前回検索実績IDを取得する。
     *
     * @return 前回検索実績ID
     */
    public String getLastSearchedActualId() {
        return this.lastSearchedActualIdProperty.get();
    }

    /**
     * 前回検索実績IDを設定する。
     *
     * @param lastSearchedActualId 前回検索実績ID
     */
    public void setLastSearchedActualId(String lastSearchedActualId) {
        this.lastSearchedActualIdProperty.set(lastSearchedActualId);
    }

    /**
     * 前回検索実績ID（工程の作業コード出力用）を取得する。
     *
     * @return 前回検索実績ID（工程の作業コード出力用）
     */
    public String getWorkLastSearchedActualId() {
        return this.workLastSearchedActualIdProperty.get();
    }

    /**
     * 前回検索実績ID（工程の作業コード出力用）を設定する。
     *
     * @param workLastSearchedActualId 前回検索実績ID（工程の作業コード出力用）
     */
    public void setWorkLastSearchedActualId(String workLastSearchedActualId) {
        this.workLastSearchedActualIdProperty.set(workLastSearchedActualId);
    }

    /**
     * フォルダーパスプロパティを取得する。
     *
     * @return フォルダーパスプロパティ
     */
    public StringProperty folderPathProperty() {
        return folderPathProperty;
    }

    /**
     * フォルダーパスを取得する。
     *
     * @return フォルダーパス
     */
    public String getFolderPath() {
        return folderPathProperty.get();
    }

    /**
     * フォルダーパスを設定する。
     *
     * @param folderPath フォルダーパス
     */
    public void setFolderPath(String folderPath) {
        this.folderPathProperty.set(folderPath);
    }

    /**
     * メールサーバープロパティを取得する。
     *
     * @return メールサーバープロパティ
     */
    public StringProperty errorMailServerProperty() {
        return this.errorMailServerProperty;
    }

    /**
     * メールサーバーを取得する。
     *
     * @return メールサーバー
     */
    public String getErrorMailServer() {
        return this.errorMailServerProperty.get();
    }

    /**
     * メールサーバーを設定する。
     *
     * @param errorMailServer メールサーバー
     */
    public void setErrorMailServer(String errorMailServer) {
        this.errorMailServerProperty.set(errorMailServer);
    }

    /**
     * メールポート番号プロパティを取得する。
     *
     * @return メールポート番号プロパティ
     */
    public IntegerProperty errorMailPortProperty() {
        return this.errorMailPortProperty;
    }

    /**
     * メールポート番号を取得する。
     *
     * @return メールポート番号
     */
    public Integer getErrorMailPort() {
        return this.errorMailPortProperty.get();
    }

    /**
     * メールポート番号を設定する。
     *
     * @param errorMailPort メールポート番号
     */
    public void setErrorMailPort(Integer errorMailPort) {
        this.errorMailPortProperty.set(errorMailPort);
    }

    /**
     * メールユーザーを取得する。
     *
     * @return メールユーザー
     */
    public String getErrorMailUser() {
        return this.errorMailUserProperty.get();
    }

    /**
     * メールユーザーを設定する。
     *
     * @param errorMailUser メールユーザー
     */
    public void setErrorMailUser(String errorMailUser) {
        this.errorMailUserProperty.set(errorMailUser);
    }

    /**
     * メールパスワードを取得する。
     *
     * @return メールパスワード
     */
    public String getErrorMailPassword() {
        return this.errorMailPasswordProperty.get();
    }

    /**
     * メールパスワードを設定する。
     *
     * @param errorMailPassword メールパスワード
     */
    public void setErrorMailPassword(String errorMailPassword) {
        this.errorMailPasswordProperty.set(errorMailPassword);
    }

    /**
     * タイムアウト時間を取得する。
     *
     * @return タイムアウト時間
     */
    public Integer getErrorMailTimeout() {
        return this.errorMailTimeoutProperty.get();
    }

    /**
     * タイムアウト時間を設定する。
     *
     * @param errorMailTimeout タイムアウト時間
     */
    public void setErrorMailTimeout(Integer errorMailTimeout) {
        this.errorMailTimeoutProperty.set(errorMailTimeout);
    }

    /**
     * メール送信者を取得する。
     *
     * @return メール送信者
     */
    public String getErrorMailFrom() {
        return this.errorMailFromProperty.get();
    }

    /**
     * メール送信者を設定する。
     *
     * @param errorMailFrom メール送信者
     */
    public void setErrorMailFrom(String errorMailFrom) {
        this.errorMailFromProperty.set(errorMailFrom);
    }

    /**
     * メール宛先プロパティを取得する。
     *
     * @return メール宛先プロパティ
     */
    StringProperty errorMailToProperty() {
        return this.errorMailToProperty;
    }

    /**
     * メール宛先を取得する。
     *
     * @return メール宛先
     */
    public String getErrorMailTo() {
        return this.errorMailToProperty.get();
    }

    /**
     * メール宛先を設定する。
     *
     * @param errorMailTo メール宛先
     */
    public void setErrorMailTo(String errorMailTo) {
        this.errorMailToProperty.set(errorMailTo);
    }
}
