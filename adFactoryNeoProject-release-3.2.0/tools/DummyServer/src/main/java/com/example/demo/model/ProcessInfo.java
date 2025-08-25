/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.model;

import java.io.Serializable;

/**
 * 着工完工情報
 *
 * @author nar-nakamura
 */
public class ProcessInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String Date;// ログ日時

    private String Process;// 工程

    private String Serial;// 製造シリアル番号

    private String Work;// 着工完工(1:着工, 2:完工)

    private String Operator;// 作業員ID

    private String ManagementSection;// 管理区分(未設定可)

    /**
     * コンストラクタ
     */
    public ProcessInfo() {
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
     * 着工完工を取得する。
     *
     * @return 着工完工(1:着工, 2:完工)
     */
    public String getWork() {
        return this.Work;
    }

    /**
     * 着工完工を設定する。
     *
     * @param work 着工完工(1:着工, 2:完工)
     */
    public void setWork(String work) {
        this.Work = work;
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

    /**
     * 管理区分を取得する。
     *
     * @return 管理区分(未設定可)
     */
    public String getManagementSection() {
        return this.ManagementSection;
    }

    /***
     * 管理区分を設定する。
     *
     * @param managementSection 管理区分(未設定可)
     */
    public void setManagementSection(String managementSection) {
        this.ManagementSection = managementSection;
    }

    @Override
    public String toString() {
        return new StringBuilder("ProcessInfo{")
                .append("Date=").append(this.Date)
                .append(", Process=").append(this.Process)
                .append(", Serial=").append(this.Serial)
                .append(", Work=").append(this.Work)
                .append(", Operator=").append(this.Operator)
                .append(", ManagementSection=").append(this.ManagementSection)
                .append("}")
                .toString();
    }
}
