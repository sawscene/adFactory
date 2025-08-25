/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.exportserviceplugin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 応答メッセージ情報
 *
 * @author nar-nakamura
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResponseMessage {
    @JsonProperty("Message")
    private String message;// メッセージ

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
        return this.message;
    }

    /**
     * メッセージを設定する。
     *
     * @param message メッセージ
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new StringBuilder("ResponseMessage{")
                .append("message=").append(this.message)
                .append("}")
                .toString();
    }
}
