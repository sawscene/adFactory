/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.importformat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 工程カンバンのフォーマット情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "workKanbanFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkKanbanFormatInfo {

    private String csvFileEncode;
    private String csvFileName;
    private String xlsSheetName;

    private String csvStartRow;
    private String xlsStartRow;
    private String csvKanbanName;
    private String xlsKanbanName;
    private String csvWorkNum;
    private String xlsWorkNum;
    private String csvSkipFlag;
    private String xlsSkipFlag;
    private String csvStartDateTime;
    private String xlsStartDateTime;
    private String csvCompDateTime;
    private String xlsCompDateTime;
    private String csvOrganizationIdentName;
    private String xlsOrganizationIdentName;
    private String csvEquipmentIdentName;
    private String xlsEquipmentIdentName;
    private String csvWorkName;
    private String xlsWorkName;
    private String csvTactTime;
    private String xlsTactTime;

    private String fileName;

    /**
     * コンストラクタ
     */
    public WorkKanbanFormatInfo() {
    }

    /**
     * CSV:エンコードを取得する。
     *
     * @return CSV:エンコード
     */
    public String getCsvFileEncode() {
        return this.csvFileEncode;
    }

    /**
     * CSV:エンコードを設定する。
     *
     * @param csvFileEncode CSV:エンコード
     */
    public void setCsvFileEncode(String csvFileEncode) {
        this.csvFileEncode = csvFileEncode;
    }

    /**
     * CSV:ファイル名を取得する。
     *
     * @return CSV:ファイル名
     */
    public String getCsvFileName() {
        return this.csvFileName;
    }

    /**
     * CSV:ファイル名を設定する。
     *
     * @param csvFileName CSV:ファイル名
     */
    public void setCsvFileName(String csvFileName) {
        this.csvFileName = csvFileName;
    }

    /**
     * Excel:シート名を取得する。
     *
     * @return Excel:シート名
     */
    public String getXlsSheetName() {
        return this.xlsSheetName;
    }

    /**
     * Excel:シート名を設定する。
     *
     * @param xlsSheetName Excel:シート名
     */
    public void setXlsSheetName(String xlsSheetName) {
        this.xlsSheetName = xlsSheetName;
    }

    /**
     * CSV:読み込み開始行を取得する。
     *
     * @return CSV:読み込み開始行
     */
    public String getCsvStartRow() {
        return this.csvStartRow;
    }

    /**
     * CSV:読み込み開始行を設定する。
     *
     * @param csvStartRow CSV:読み込み開始行
     */
    public void setCsvStartRow(String csvStartRow) {
        this.csvStartRow = csvStartRow;
    }

    /**
     * Excel:読み込み開始行を取得する。
     *
     * @return Excel:読み込み開始行
     */
    public String getXlsStartRow() {
        return this.xlsStartRow;
    }

    /**
     * Excel:読み込み開始行を設定する。
     *
     * @param xlsStartRow Excel:読み込み開始行
     */
    public void setXlsStartRow(String xlsStartRow) {
        this.xlsStartRow = xlsStartRow;
    }

    /**
     * CSV:カンバン名を取得する。
     *
     * @return CSV:カンバン名
     */
    public String getCsvKanbanName() {
        return this.csvKanbanName;
    }

    /**
     * CSV:カンバン名を設定する。
     *
     * @param csvKanbanName CSV:カンバン名
     */
    public void setCsvKanbanName(String csvKanbanName) {
        this.csvKanbanName = csvKanbanName;
    }

    /**
     * Excel:カンバン名を取得する。
     *
     * @return Excel:カンバン名
     */
    public String getXlsKanbanName() {
        return this.xlsKanbanName;
    }

    /**
     * Excel:カンバン名を設定する。
     *
     * @param xlsKanbanName Excel:カンバン名
     */
    public void setXlsKanbanName(String xlsKanbanName) {
        this.xlsKanbanName = xlsKanbanName;
    }

    /**
     * CSV:工程の番号を取得する。
     *
     * @return CSV:工程の番号
     */
    public String getCsvWorkNum() {
        return this.csvWorkNum;
    }

    /**
     * CSV:工程の番号を設定する。
     *
     * @param csvWorkNum CSV:工程の番号
     */
    public void setCsvWorkNum(String csvWorkNum) {
        this.csvWorkNum = csvWorkNum;
    }

    /**
     * Excel:工程の番号を取得する。
     *
     * @return Excel:工程の番号
     */
    public String getXlsWorkNum() {
        return this.xlsWorkNum;
    }

    /**
     * Excel:工程の番号を設定する。
     *
     * @param xlsWorkNum Excel:工程の番号
     */
    public void setXlsWorkNum(String xlsWorkNum) {
        this.xlsWorkNum = xlsWorkNum;
    }

    /**
     * CSV:スキップを取得する。
     *
     * @return CSV:スキップ
     */
    public String getCsvSkipFlag() {
        return this.csvSkipFlag;
    }

    /**
     * CSV:スキップを設定する。
     *
     * @param csvSkipFlag CSV:スキップ
     */
    public void setCsvSkipFlag(String csvSkipFlag) {
        this.csvSkipFlag = csvSkipFlag;
    }

    /**
     * Excel:スキップを取得する。
     *
     * @return Excel:スキップ
     */
    public String getXlsSkipFlag() {
        return this.xlsSkipFlag;
    }

    /**
     * Excel:スキップを設定する。
     *
     * @param xlsSkipFlag Excel:スキップ
     */
    public void setXlsSkipFlag(String xlsSkipFlag) {
        this.xlsSkipFlag = xlsSkipFlag;
    }

    /**
     * CSV:開始予定日時を取得する。
     *
     * @return CSV:開始予定日時
     */
    public String getCsvStartDateTime() {
        return this.csvStartDateTime;
    }

    /**
     * CSV:開始予定日時を設定する。
     *
     * @param csvStartDateTime CSV:開始予定日時
     */
    public void setCsvStartDateTime(String csvStartDateTime) {
        this.csvStartDateTime = csvStartDateTime;
    }

    /**
     * Excel:開始予定日時を取得する。
     *
     * @return Excel:開始予定日時
     */
    public String getXlsStartDateTime() {
        return this.xlsStartDateTime;
    }

    /**
     * Excel:開始予定日時を設定する。
     *
     * @param xlsStartDateTime Excel:開始予定日時
     */
    public void setXlsStartDateTime(String xlsStartDateTime) {
        this.xlsStartDateTime = xlsStartDateTime;
    }

    /**
     * CSV:完了予定日時を取得する。
     *
     * @return CSV:完了予定日時
     */
    public String getCsvCompDateTime() {
        return this.csvCompDateTime;
    }

    /**
     * CSV:完了予定日時を設定する。
     *
     * @param csvCompDateTime CSV:完了予定日時
     */
    public void setCsvCompDateTime(String csvCompDateTime) {
        this.csvCompDateTime = csvCompDateTime;
    }

    /**
     * Excel:完了予定日時を取得する。
     *
     * @return Excel:完了予定日時
     */
    public String getXlsCompDateTime() {
        return this.xlsCompDateTime;
    }

    /**
     * Excel:完了予定日時を設定する。
     *
     * @param xlsCompDateTime Excel:完了予定日時
     */
    public void setXlsCompDateTime(String xlsCompDateTime) {
        this.xlsCompDateTime = xlsCompDateTime;
    }

    /**
     * CSV:組織識別名を取得する。
     *
     * @return CSV:組織識別名
     */
    public String getCsvOrganizationIdentName() {
        return this.csvOrganizationIdentName;
    }

    /**
     * CSV:組織識別名を設定する。
     *
     * @param csvOrganizationIdentName CSV:組織識別名
     */
    public void setCsvOrganizationIdentName(String csvOrganizationIdentName) {
        this.csvOrganizationIdentName = csvOrganizationIdentName;
    }

    /**
     * Excel:組織識別名を取得する。
     *
     * @return Excel:組織識別名
     */
    public String getXlsOrganizationIdentName() {
        return this.xlsOrganizationIdentName;
    }

    /**
     * Excel:組織識別名を設定する。
     *
     * @param xlsOrganizationIdentName Excel:組織識別名
     */
    public void setXlsOrganizationIdentName(String xlsOrganizationIdentName) {
        this.xlsOrganizationIdentName = xlsOrganizationIdentName;
    }

    /**
     * CSV:設備識別名を取得する。
     *
     * @return CSV:設備識別名
     */
    public String getCsvEquipmentIdentName() {
        return this.csvEquipmentIdentName;
    }

    /**
     * CSV:設備識別名を設定する。
     *
     * @param csvEquipmentIdentName CSV:設備識別名
     */
    public void setCsvEquipmentIdentName(String csvEquipmentIdentName) {
        this.csvEquipmentIdentName = csvEquipmentIdentName;
    }

    /**
     * Excel:設備識別名を取得する。
     *
     * @return Excel:設備識別名
     */
    public String getXlsEquipmentIdentName() {
        return this.xlsEquipmentIdentName;
    }

    /**
     * Excel:設備識別名を設定する。
     *
     * @param xlsEquipmentIdentName Excel:設備識別名
     */
    public void setXlsEquipmentIdentName(String xlsEquipmentIdentName) {
        this.xlsEquipmentIdentName = xlsEquipmentIdentName;
    }

    /**
     * CSV:工程名を取得する。
     *
     * @return CSV:工程名
     */
    public String getCsvWorkName() {
        return this.csvWorkName;
    }

    /**
     * CSV:工程名を設定する。
     *
     * @param csvWorkName CSV:工程名
     */
    public void setCsvWorkName(String csvWorkName) {
        this.csvWorkName = csvWorkName;
    }

    /**
     * Excel:工程名を取得する。
     *
     * @return Excel:工程名
     */
    public String getXlsWorkName() {
        return this.xlsWorkName;
    }

    /**
     * Excel:工程名を設定する。
     *
     * @param xlsWorkName Excel:工程名
     */
    public void setXlsWorkName(String xlsWorkName) {
        this.xlsWorkName = xlsWorkName;
    }

    /**
     * CSV:タクトタイムを取得する。
     *
     * @return CSV:タクトタイム
     */
    public String getCsvTactTime() {
        return this.csvTactTime;
    }

    /**
     * CSV:タクトタイムを設定する。
     *
     * @param csvTactTime CSV:タクトタイム
     */
    public void setCsvTactTime(String csvTactTime) {
        this.csvTactTime = csvTactTime;
    }

    /**
     * Excel:タクトタイムを取得する。
     *
     * @return Excel:タクトタイム
     */
    public String getXlsTactTime() {
        return this.xlsTactTime;
    }

    /**
     * Excel:タクトタイムを設定する。
     *
     * @param xlsTactTime Excel:タクトタイム
     */
    public void setXlsTactTime(String xlsTactTime) {
        this.xlsTactTime = xlsTactTime;
    }

    /**
     * ファイル名を取得する。
     *
     * @return ファイル名
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * ファイル名を設定する。
     *
     * @param fileName ファイル名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkKanbanFormatInfo{")
                .append("csvFileEncode=").append(this.csvFileEncode)
                .append(", csvFileName=").append(this.csvFileName)
                .append(", xlsSheetName=").append(this.xlsSheetName)
                .append(", csvStartRow=").append(this.csvStartRow)
                .append(", xlsStartRow=").append(this.xlsStartRow)
                .append(", csvKanbanName=").append(this.csvKanbanName)
                .append(", xlsKanbanName=").append(this.xlsKanbanName)
                .append(", csvWorkNum=").append(this.csvWorkNum)
                .append(", xlsWorkNum=").append(this.xlsWorkNum)
                .append(", csvSkipFlag=").append(this.csvSkipFlag)
                .append(", xlsSkipFlag=").append(this.xlsSkipFlag)
                .append(", csvStartDateTime=").append(this.csvStartDateTime)
                .append(", xlsStartDateTime=").append(this.xlsStartDateTime)
                .append(", csvCompDateTime=").append(this.csvCompDateTime)
                .append(", xlsCompDateTime=").append(this.xlsCompDateTime)
                .append(", csvOrganizationIdentName=").append(this.csvOrganizationIdentName)
                .append(", xlsOrganizationIdentName=").append(this.xlsOrganizationIdentName)
                .append(", csvEquipmentIdentName=").append(this.csvEquipmentIdentName)
                .append(", xlsEquipmentIdentName=").append(this.xlsEquipmentIdentName)
                .append(", csvWorkName=").append(this.csvWorkName)
                .append(", xlsWorkName=").append(this.xlsWorkName)
                .append(", csvTactTime=").append(this.csvTactTime)
                .append(", xlsTactTime=").append(this.xlsTactTime)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
