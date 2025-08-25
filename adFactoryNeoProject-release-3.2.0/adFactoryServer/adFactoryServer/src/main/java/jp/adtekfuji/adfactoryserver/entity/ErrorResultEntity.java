/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity;

import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;

/**
 * 処理結果とエラー種別を返すためのエンティティ
 *
 * @author nar-nakamura
 */
public class ErrorResultEntity {

    private ServerErrorTypeEnum errorType;// エラー種別
    private Object value;// 値

    /**
     * コンストラクタ
     */
    public ErrorResultEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param errorType エラー種別
     * @param value 値
     */
    public ErrorResultEntity(ServerErrorTypeEnum errorType, Object value) {
        this.errorType = errorType;
        this.value = value;
    }

    /**
     * エラー種別を取得する。
     *
     * @return エラー種別
     */
    public ServerErrorTypeEnum getErrorType() {
        return this.errorType;
    }

    /**
     * エラー種別を設定する。
     *
     * @param errorType エラー種別
     */
    public void setErrorType(ServerErrorTypeEnum errorType) {
        this.errorType = errorType;
    }

    /**
     * 値を取得する。
     *
     * @return 値
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * 値を設定する。
     *
     * @param value 値
     */
    public void setValue(Object value) {
        this.value = value;
    }
}
