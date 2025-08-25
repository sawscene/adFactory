/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.model;

//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 応答メッセージ情報
 *
 * @author nar-nakamura
 */
public class ResponseMessage {

    private static final long serialVersionUID = 1L;

    private String Message;// メッセージ

    /**
     * コンストラクタ
     */
    public ResponseMessage() {
    }

    /**
     * メッセージを取得する。
     *
     * @return メッセージ
     */
    public String getMessage() {
        return this.Message;
    }

    /**
     * メッセージを設定する。
     *
     * @param message メッセージ
     */
    public void setMessage(String message) {
        this.Message = message;
    }

    @Override
    public String toString() {
        return new StringBuilder("ResponseMessage{")
                .append("Message=").append(this.Message)
                .append("}")
                .toString();
    }
}
