/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.Exceptions;

/**
 * 機器通信サービスの例外エラーベースクラス
 *
 * @author okada
 */
public class BaseException extends Exception {

    private static final long serialVersionUID = 1L;
    
    /**
     * エラー区分
     */
    public enum ErrorKbnEnum{
        /** 致命的なエラー */
        FATAL,
        /** エラー */
        ERROR,
        /** 警告 */
        WARN,
        /** 情報 */
        INFO,
        /** デバッグ情報 */
        DEBUG,
        /** トレース情報 */
        TRACE;
    }

    // -- プロパティ --
    // エラー区分
    protected ErrorKbnEnum errorKbn = ErrorKbnEnum.ERROR;
    // 例外エラーを含むか true:含む
    protected boolean exceptionFlag = false;
    // メール送信の有無 true:送信を行う
    protected boolean sendMailFlag = false;
    // メールタイトル
    protected String sendMailTitle = "";
    // メール送信内容
    protected String sendMailMessage = "";

    
    /**
     * エラー区分の取得
     *
     * @return trueエラー区分
     */
    public ErrorKbnEnum getErrorKbn() {
        return this.errorKbn;
    }

    /**
     * 例外エラーを含むかの取得
     *
     * @return true:例外エラーを含む
     */
    public boolean getExceptionFlag() {
        return this.exceptionFlag;
    }
    
    /**
     * メール送信の有無を取得
     *
     * @return true:送信を行う
     */
    public boolean getSendMailFlag() {
        return this.sendMailFlag;
    }
    
    /**
     * メールタイトルを取得
     *
     * @return メールタイトル
     */
    public String getSendMailTitle() {
        return this.sendMailTitle;
    }

    /**
     * メール送信内容を取得
     *
     * @return メール送信内容
     */
    public String getSendMailMessage() {
        return this.sendMailMessage;
    }
    
    // -- コンストラクタ --
    /**
     * 発生した例外エラーを含む場合に使用
     * 
     * @param msg
     * @param cause 
     */
    public BaseException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * 発生した例外エラーを含む場合に使用
     * 
     * @param msg
     * @param cause
     * @param exceptionFlag
     * @param sendMailFlag
     * @param sendMailTitle
     * @param sendMailMessage 
     */
    public BaseException(String msg, Throwable cause, boolean exceptionFlag, boolean sendMailFlag, String sendMailTitle, String sendMailMessage) {
        this(msg, cause);
        this.exceptionFlag = exceptionFlag;
        this.sendMailFlag = sendMailFlag;
        this.sendMailTitle = sendMailTitle;
        this.sendMailMessage = sendMailMessage;
    }

    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(String msg, boolean exceptionFlag, boolean sendMailFlag, String sendMailTitle, String sendMailMessage) {
        this(msg);
        this.exceptionFlag = exceptionFlag;
        this.sendMailFlag = sendMailFlag;
        this.sendMailTitle = sendMailTitle;
        this.sendMailMessage = sendMailMessage;
    }

    /**
     * 発生した例外エラーを含まずメール送信の設定のみ行う場合に使用
     * 
     * @param sendMailFlag
     * @param sendMailTitle
     * @param sendMailMessage 
     */
    public BaseException(boolean sendMailFlag, String sendMailTitle, String sendMailMessage) {
        this("", false, sendMailFlag, sendMailTitle, sendMailMessage);
    }
}
