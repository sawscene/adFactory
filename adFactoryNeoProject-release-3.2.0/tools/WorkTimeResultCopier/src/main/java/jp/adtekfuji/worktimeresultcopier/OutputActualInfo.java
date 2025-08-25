/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.worktimeresultcopier;

import adtekfuji.property.AdProperty;

import java.util.Properties;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 実績出力設定情報
 *
 * @author ke.yokoi
 */
public class OutputActualInfo {
    
    // 基幹システム設定
    private final String KEY_IN_FOLDER = "inFolder";
    private final String KEY_OUT_FOLDER = "outFolder";
    private final String KEY_USER = "UserProperty";
    private final String KEY_PASSWORD = "PassWordProperty";
    private final String KEY_READ_FILE_NAME_ADDRESS = "readFileNameAddress";
    
    // 基幹システム設定の初期値
    private final String DEF_IN_FOLDER = "";
    private final String DEF_OUT_FOLDER = "";
    private final String DEF_USER = "";
    private final String DEF_PASSWORD = "";    
    private final String DEF_READ_FILE_NAME_ADDRESS = "";
    
    
    // 共有フォルダ設定
    private final String KEY_SHARE_FOLDER = "shareFolder";
    // 共有フォルダ設定の初期値
    private final String DEF_SHARE_FOLDER = "";
    
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
    private final String DEF_ERROR_MAIL_TO = "";
    private final String DEF_ERROR_MAIL_TIMEOUT = "30";

    private Properties property;
    private final StringProperty inFolderProperty = new SimpleStringProperty();
    private final StringProperty outFolderProperty = new SimpleStringProperty();
    private final StringProperty userProperty = new SimpleStringProperty();
    private final StringProperty passwordProperty = new SimpleStringProperty();
    private final StringProperty readFileNameAddressProperty = new SimpleStringProperty();
    
    private final StringProperty shareFolderProperty = new SimpleStringProperty();
    
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
        inFolderProperty.set(property.getProperty(KEY_IN_FOLDER, DEF_IN_FOLDER));
        outFolderProperty.set(property.getProperty(KEY_OUT_FOLDER, DEF_OUT_FOLDER));
        userProperty.set(property.getProperty(KEY_USER, DEF_USER));
        passwordProperty.set(property.getProperty(KEY_PASSWORD, DEF_PASSWORD));
        readFileNameAddressProperty.set(property.getProperty(KEY_READ_FILE_NAME_ADDRESS, DEF_READ_FILE_NAME_ADDRESS));
        
        shareFolderProperty.set(property.getProperty(KEY_SHARE_FOLDER, DEF_SHARE_FOLDER));
        
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
        property.setProperty(KEY_IN_FOLDER, inFolderProperty.get());
        property.setProperty(KEY_OUT_FOLDER, outFolderProperty.get());
        property.setProperty(KEY_USER, userProperty.get());
        property.setProperty(KEY_PASSWORD, passwordProperty.get());
        property.setProperty(KEY_READ_FILE_NAME_ADDRESS, readFileNameAddressProperty.get());
        
        property.setProperty(KEY_SHARE_FOLDER, shareFolderProperty.get());
        
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
     * inFolder のアドレスを取得する。
     *
     * @return inFolder のアドレス
     */
    public StringProperty inFolderProperty() {
        return this.inFolderProperty;
    }

    /**
     * inFolder のアドレスを取得する。
     *
     * @return inFolder のアドレス
     */
    public String getInFolder() {
        return this.inFolderProperty.get();
    }

    /**
     * inFolder のアドレスを設定する。
     *
     * @param inFolder inFolder のアドレス
     */
    public void setInFolder(String inFolder) {
        this.inFolderProperty.set(inFolder);
    }
        
    /**
     * outFolder のアドレスを取得する。
     *
     * @return outFolder のアドレス
     */
    public StringProperty outFolderProperty() {
        return this.outFolderProperty;
    }

    /**
     * outFolder のアドレスを取得する。
     *
     * @return outFolder のアドレス
     */
    public String getOutFolder() {
        return this.outFolderProperty.get();
    }

    /**
     * outFolder のアドレスを設定する。
     *
     * @param outFolder outFolder のアドレス
     */
    public void setOutFolder(String outFolder) {
        this.outFolderProperty.set(outFolder);
    }
    
    /**
     * ユーザー名を取得する。
     *
     * @return ユーザー名
     */
    public StringProperty userProperty() {
        return this.userProperty;
    }

    /**
     * ユーザー名を取得する。
     *
     * @return ユーザー名
     */
    public String getUser() {
        return this.userProperty.get();
    }

    /**
     * ユーザー名を設定する。
     *
     * @param user ユーザー名
     */
    public void setUser(String user) {
        this.userProperty.set(user);
    }
    
    /**
     * パスワードを取得する。
     *
     * @return パスワード
     */
    public StringProperty passwordProperty() {
        return this.passwordProperty;
    }

    /**
     * パスワードを取得する。
     *
     * @return パスワード
     */
    public String getPassword() {
        return this.passwordProperty.get();
    }

    /**
     * パスワードを設定する。
     *
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.passwordProperty.set(password);
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
     * 共有フォルダのアドレスを取得する。
     *
     * @return 共有フォルダのアドレス
     */
    public StringProperty shareFolderProperty() {
        return this.shareFolderProperty;
    }

    /**
     * 共有フォルダのアドレスを取得する。
     *
     * @return 共有フォルダのアドレス
     */
    public String getShareFolder() {
        return this.shareFolderProperty.get();
    }

    /**
     * 共有フォルダのアドレスを設定する。
     *
     * @param shareFolder 共有フォルダのアドレス
     */
    public void setShareFolder(String shareFolder) {
        this.shareFolderProperty.set(shareFolder);
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
