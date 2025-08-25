/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.exportserviceplugin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 着工完工情報
 *
 * @author nar-nakamura
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ProcessInfo {

    @JsonProperty("Date")
    private String date;// ログ日時

    @JsonProperty("Process")
    private String process;// 工程

    @JsonProperty("Serial")
    private String serial;// 製造シリアル番号

    @JsonProperty("Work")
    private String work;// 着工完工(1:着工, 2:完工)

    @JsonProperty("Operator")
    private String operator;// 作業員ID

    @JsonProperty("ManagementSection")
    private String managementSection;// 管理区分(未設定可)

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
     * 着工完工を取得する。
     *
     * @return 着工完工(1:着工, 2:完工)
     */
    public String getWork() {
        return this.work;
    }

    /**
     * 着工完工を設定する。
     *
     * @param work 着工完工(1:着工, 2:完工)
     */
    public void setWork(String work) {
        this.work = work;
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

    /**
     * 管理区分を取得する。
     *
     * @return 管理区分(未設定可)
     */
    public String getManagementSection() {
        return this.managementSection;
    }

    /***
     * 管理区分を設定する。
     *
     * @param managementSection 管理区分(未設定可)
     */
    public void setManagementSection(String managementSection) {
        this.managementSection = managementSection;
    }

    @Override
    public String toString() {
        return new StringBuilder("ProcessInfo{")
                .append("date=").append(this.date)
                .append(", process=").append(this.process)
                .append(", serial=").append(this.serial)
                .append(", work=").append(this.work)
                .append(", operator=").append(this.operator)
                .append(", managementSection=").append(this.managementSection)
                .append("}")
                .toString();
    }
}
