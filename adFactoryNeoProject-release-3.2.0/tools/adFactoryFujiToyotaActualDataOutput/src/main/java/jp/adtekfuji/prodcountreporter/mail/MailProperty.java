/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.prodcountreporter.mail;

/**
 * メール設定情報
 *
 * @author nar-nakamura
 */
public class MailProperty {

    // 接続情報
    private String host;
    private Integer port = 25;

    // 認証情報
    private String user = "";
    private String password = "";

    // タイムアウト情報
    private Integer connectionTimeout = 30000;
    private Integer timeout = 30000;

    // エンコード情報
    private String charset = "MS932";
    private String contentTransferEncoding = "base64";

    /**
     * 
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

    @Override
    public String toString() {
        return "MailProperty{"
                + "host=" + this.host
                + ", port=" + this.port
                + ", user=" + this.user
                + ", password=" + this.password
                + ", connectionTimeout=" + this.connectionTimeout
                + ", timeout=" + this.timeout
                + ", charset=" + this.charset
                + ", contentTransferEncoding=" + this.contentTransferEncoding
                + '}';
    }
}
