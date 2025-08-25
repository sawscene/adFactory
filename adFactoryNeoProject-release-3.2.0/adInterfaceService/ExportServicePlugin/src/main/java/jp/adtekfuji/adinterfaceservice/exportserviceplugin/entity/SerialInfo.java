/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.exportserviceplugin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * シリアル情報
 *
 * @author nar-nakamura
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class SerialInfo {

    @JsonProperty("Date")
    private String date;// ログ日時

    @JsonProperty("Process")
    private String process;// 工程

    @JsonProperty("Serial")
    private String serial;// 製造シリアル番号

    @JsonProperty("ComponentCode")
    private String componentCode;// 部品品目コード

    @JsonProperty("ComponentSerial")
    private String componentSerial;// 部品シリアル番号

    @JsonProperty("Operator")
    private String operator;// 作業員ID

    /**
     * コンストラクタ
     */
    public SerialInfo() {
    }

    /**
     * ログ日時を取得する。
     *
     * @return ログ日時
     */
    public String getDate() {
        return this.date;
    }

    /**
     * ログ日時を設定する。
     *
     * @param date ログ日時
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 工程を取得する。
     *
     * @return 工程
     */
    public String getProcess() {
        return this.process;
    }

    /**
     * 工程を設定する。
     *
     * @param process 工程
     */
    public void setProcess(String process) {
        this.process = process;
    }

    /**
     * 製造シリアル番号を取得する。
     *
     * @return 製造シリアル番号
     */
    public String getSerial() {
        return this.serial;
    }

    /**
     * 製造シリアル番号を設定する。
     *
     * @param serial 製造シリアル番号
     */
    public void setSerial(String serial) {
        this.serial = serial;
    }

    /**
     * 部品品目コードを取得する。
     *
     * @return 部品品目コード
     */
    public String getComponentCode() {
        return this.componentCode;
    }

    /**
     * 部品品目コードを設定する。
     *
     * @param componentCode 部品品目コード
     */
    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    /**
     * 部品シリアル番号を取得する。
     *
     * @return 部品シリアル番号
     */
    public String getComponentSerial() {
        return this.componentSerial;
    }

    /**
     * 部品シリアル番号を設定する。
     *
     * @param componentSerial 部品シリアル番号
     */
    public void setComponentSerial(String componentSerial) {
        this.componentSerial = componentSerial;
    }

    /**
     * 作業員IDを取得する。
     *
     * @return 作業員ID
     */
    public String getOperator() {
        return this.operator;
    }

    /**
     * 作業員IDを設定する。
     *
     * @param operator 作業員ID
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return new StringBuilder("SerialInfo{")
                .append("date=").append(this.date)
                .append(", process=").append(this.process)
                .append(", serial=").append(this.serial)
                .append(", componentCode=").append(this.componentCode)
                .append(", componentSerial=").append(this.componentSerial)
                .append(", operator=").append(this.operator)
                .append("}")
                .toString();
    }
}
