/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.mail;



/**
 * メール設定情報
 *
 * @author shizuka.hirano
 */
public class MailProperty {

    // 接続情報
    /**
     * 送信メール(SMTP)サーバ
     */
    private String host;

    /**
     * メール送信で使用するポート番号
     */
    private Integer port = 25;

    // 認証情報
    /**
     * メール認証
     */
    private boolean isEnableAuth = false;

    /**
     * メール送信で使用するメールアカウントのユーザー名
     */
    private String user = "";

    /**
     * メール送信で使用するメールアカウントのパスワード
     */
    private String password = "";

    /**
     * メールTLS認証
     */
    private boolean isEnableTLS = false;

    // タイムアウト情報
    /**
     * 接続タイムアウト時間
     */
    private Integer connectionTimeout = 30000;

    /**
     * 送信タイムアウト時間
     */
    private Integer timeout = 30000;

    // エンコード情報
    /**
     * メール送信で使用する文字コード
     */
    private String charset = "MS932";

    /**
     * メールヘッダ
     */
    private String contentTransferEncoding = "base64";
    
    /**
     * メール送信者
     */
    private String mailFrom = "";

    /**
     * コンストラクタ
     */
    public MailProperty() {
    }

    /**
     * サーバーアドレスを取得する。
     *
     * @return メールサーバーアドレス
     */
    public String getHost() {
        return this.host;
    }

    /**
     * サーバーアドレスを設定する。
     *
     * @param host メールサーバーアドレス
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * ポート番号を取得する。
     *
     * @return メール送信のポート番号
     */
    public Integer getPort() {
        return this.port;
    }

    /**
     * ポート番号を設定する。
     *
     * @param port メール送信のポート番号
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 認証を使用するかを取得する。
     *
     * @return 認証を使用するか
     */
    public boolean getIsEnableAuth() {
        return this.isEnableAuth;
    }

    /**
     * 認証を使用するかを設定する。
     *
     * @param isEnableAuth 認証を使用するか
     */
    public void setIsEnableAuth(boolean isEnableAuth) {
        this.isEnableAuth = isEnableAuth;
    }

    /**
     * ユーザー名を取得する。
     *
     * @return ユーザー名
     */
    public String getUser() {
        return this.user;
    }

    /**
     * ユーザー名を設定する。
     *
     * @param user ユーザー名
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * パスワードを取得する。
     *
     * @return パスワード
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * パスワードを設定する。
     *
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * TLSを使用するかを取得する。
     *
     * @return TLSを使用するか
     */
    public boolean getIsEnableTLS() {
        return this.isEnableTLS;
    }

    /**
     * TLSを使用するかを設定する。
     *
     * @param isEnableTLS TLSを使用するか
     */
    public void setIsEnableTLS(boolean isEnableTLS) {
        this.isEnableTLS = isEnableTLS;
    }

    /**
     * 接続タイムアウト時間を取得する。
     *
     * @return 接続タイムアウト時間 (ms)
     */
    public Integer getConnectionTimeout() {
        return this.connectionTimeout;
    }

    /**
     * 接続タイムアウト時間を設定する。
     *
     * @param connectionTimeout 接続タイムアウト時間 (ms)
     */
    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * タイムアウト時間を取得する。
     *
     * @return タイムアウト時間 (ms)
     */
    public Integer getTimeout() {
        return this.timeout;
    }

    /**
     * タイムアウト時間を設定する。
     *
     * @param timeout タイムアウト時間 (ms)
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * 件名と本文の文字コード設定を取得する。
     *
     * @return 文字コード
     */
    public String getCharset() {
        return this.charset;
    }

    /**
     * 件名と本文の文字コード設定を設定する。
     *
     * @param charset 文字コード
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * ヘッダーのContent-Transfer-Encodingを取得する。
     *
     * @return Content-Transfer-Encoding
     */
    public String getContentTransferEncoding() {
        return this.contentTransferEncoding;
    }

    /**
     * ヘッダーのContent-Transfer-Encodingを設定する。
     *
     * @param contentTransferEncoding Content-Transfer-Encoding
     */
    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }

    /**
     * メール送信者を取得する。
     *
     * @return メール送信者
     */
    public String getMailFrom() {
        return this.mailFrom;
    }

    /**
     * メール送信者を設定する。
     *
     * @param mailFrom メール送信者
     */
    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    /**
     * MailPropertyを文字列に変更
     * 
     * @return 文字列
     */
    @Override
    public String toString() {
        return "MailProperty{"
                + "host=" + this.host
                + ", port=" + this.port
                + ", isEnableAuth=" + this.isEnableAuth
                + ", user=" + this.user
//                + ", password=" + this.password// 生のパスワードなので出力しない。
                + ", isEnableTLS=" + this.isEnableTLS
                + ", connectionTimeout=" + this.connectionTimeout
                + ", timeout=" + this.timeout
                + ", charset=" + this.charset
                + ", contentTransferEncoding=" + this.contentTransferEncoding
                + ", mailFrom=" + this.mailFrom
                + '}';
    }
}
