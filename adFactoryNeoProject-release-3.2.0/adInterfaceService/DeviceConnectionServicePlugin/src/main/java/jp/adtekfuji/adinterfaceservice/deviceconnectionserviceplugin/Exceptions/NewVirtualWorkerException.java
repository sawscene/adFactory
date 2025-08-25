/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.deviceconnectionserviceplugin.Exceptions;

/**
 * 装置状態管理のインスタンスを作成時のエラー
 * 
 * @author okada
 */
public class NewVirtualWorkerException extends BaseException {
    
    public NewVirtualWorkerException(boolean sendMailFlag, String sendMailTitle, String sendMailMessage) {
        super(sendMailFlag, sendMailTitle, sendMailMessage);
    }

    public NewVirtualWorkerException(String msg, Throwable cause, boolean exceptionFlag, boolean sendMailFlag, String sendMailTitle, String sendMailMessage) {
        super(msg, cause, exceptionFlag, sendMailFlag, sendMailTitle, sendMailMessage);
    }
    
}
