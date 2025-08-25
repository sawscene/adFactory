/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;

/**
 * REST APIのレスポンス(結果情報付き)
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "resultResponse")
public class ResultResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private final Boolean isSuccess;// 成功したか

    @XmlElement()
    private ServerErrorTypeEnum errorType;// エラー種別

    @XmlElement()
    private String result;// 結果情報

    /**
     * コンストラクタ
     */
    public ResultResponse() {
        this.isSuccess = null;
    }

    /**
     * コンストラクタ
     *
     * @param isSuccess 成功したか
     */
    private ResultResponse(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * エラー種別に成功を設定して、レスポンスを取得する。
     * @return 成功したか
     */
    public static ResultResponse success() {
        return new ResultResponse(true).errorType(ServerErrorTypeEnum.SUCCESS);
    }

    /**
     * エラー種別を設定して、レスポンスを取得する。
     *
     * @param errorType　エラー種別
     * @return 
     */
    public static ResultResponse failed(ServerErrorTypeEnum errorType) {
        return new ResultResponse(false).errorType(errorType);
    }

    /**
     * エラー種別を設定して、レスポンスを取得する。
     *
     * @param errorType エラー種別
     * @return レスポンス
     */
    public ResultResponse errorType(ServerErrorTypeEnum errorType) {
        this.errorType = errorType;
        return this;
    }

    /**
     * 結果情報を設定して、レスポンスを取得する。
     *
     * @param result　結果情報
     * @return レスポンス
     */
    public ResultResponse result(String result) {
        this.result = result;
        return this;
    }

    /**
     * 成功したかを取得する。
     *
     * @return 成功したか (true:成功, false:失敗)
     */
    public Boolean isSuccess() {
        return this.isSuccess;
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
     * 結果情報を取得する。
     *
     * @return 結果情報
     */
    public String getResult() {
        return this.result;
    }

    @Override
    public String toString() {
        return new StringBuilder("ResultResponse{")
                .append("isSuccess=").append(this.isSuccess)
                .append(", errorType=").append(this.errorType)
                .append(", result=").append(this.result)
                .append("}")
                .toString();
    }
}
