/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.adinterface.command;

import java.io.Serializable;

/**
 * リクエストコマンド
 * 
 * @author s-heya
 */
public class RequestCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String message;

    /**
     * コンストラクタ
     * 
     * @param message メッセージ
     */
    public RequestCommand(String message) {
        this.message = message;
    }
    
    /**
     * メッセージを取得する。
     * 
     * @return メッセージ
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "RequestCommand{" + "message=" + message + '}';
    }
}
