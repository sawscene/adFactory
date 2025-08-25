/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.worktimereporter.mail;

/**
 * メール結果
 *
 * @author nar-nakamura
 */
public enum MailResultEnum {
    SUCCESS,// 成功
    FAILED,// 失敗
    AUTHENTICATION_FAILED,// 認証失敗
    MESSAGING_EXCEPTION;// メッセージ異常
}
