/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.Exceptions;

/**
 * 設定ファイルから情報を取得時のエラー
 * 
 * @author okada
 */
public class GetEviceConnectionInfoException extends BaseException {
    
    public GetEviceConnectionInfoException(boolean sendMailFlag, String sendMailTitle, String sendMailMessage) {
        super(sendMailFlag, sendMailTitle, sendMailMessage);
    }

    public GetEviceConnectionInfoException(String msg, Throwable cause, boolean exceptionFlag, boolean sendMailFlag, String sendMailTitle, String sendMailMessage) {
        super(msg, cause, exceptionFlag, sendMailFlag, sendMailTitle, sendMailMessage);
    }
    
}
