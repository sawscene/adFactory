/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.model;

import java.io.Serializable;

/**
 * シリアル情報
 *
 * @author nar-nakamura
 */
public class SerialInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String Date;// ログ日時

    private String Process;// 工程

    private String Serial;// 製造シリアル番号

    private String ComponentCode;// 部品品目コード

    private String ComponentSerial;// 部品シリアル番号

    private String Operator;// 作業員ID

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
        return this.Date;
    }

    /**
     * ログ日時を設定する。
     *
     * @param date ログ日時
     */
    public void setDate(String date) {
        this.Date = date;
    }

    /**
     * 工程を取得する。
     *
     * @return 工程
     */
    public String getProcess() {
        return this.Process;
    }

    /**
     * 工程を設定する。
     *
     * @param process 工程
     */
    public void setProcess(String process) {
        this.Process = process;
    }

    /**
     * 製造シリアル番号を取得する。
     *
     * @return 製造シリアル番号
     */
    public String getSerial() {
        return this.Serial;
    }

    /**
     * 製造シリアル番号を設定する。
     *
     * @param serial 製造シリアル番号
     */
    public void setSerial(String serial) {
        this.Serial = serial;
    }

    /**
     * 部品品目コードを取得する。
     *
     * @return 部品品目コード
     */
    public String getComponentCode() {
        return this.ComponentCode;
    }

    /**
     * 部品品目コードを設定する。
     *
     * @param componentCode 部品品目コード
     */
    public void setComponentCode(String componentCode) {
        this.ComponentCode = componentCode;
    }

    /**
     * 部品シリアル番号を取得する。
     *
     * @return 部品シリアル番号
     */
    public String getComponentSerial() {
        return this.ComponentSerial;
    }

    /**
     * 部品シリアル番号を設定する。
     *
     * @param componentSerial 部品シリアル番号
     */
    public void setComponentSerial(String componentSerial) {
        this.ComponentSerial = componentSerial;
    }

    /**
     * 作業員IDを取得する。
     *
     * @return 作業員ID
     */
    public String getOperator() {
        return this.Operator;
    }

    /**
     * 作業員IDを設定する。
     *
     * @param operator 作業員ID
     */
    public void setOperator(String operator) {
        this.Operator = operator;
    }

    @Override
    public String toString() {
        return new StringBuilder("SerialInfo{")
                .append("Date=").append(this.Date)
                .append(", Process=").append(this.Process)
                .append(", Serial=").append(this.Serial)
                .append(", ComponentCode=").append(this.ComponentCode)
                .append(", ComponentSerial=").append(this.ComponentSerial)
                .append(", Operator=").append(this.Operator)
                .append("}")
                .toString();
    }
}
