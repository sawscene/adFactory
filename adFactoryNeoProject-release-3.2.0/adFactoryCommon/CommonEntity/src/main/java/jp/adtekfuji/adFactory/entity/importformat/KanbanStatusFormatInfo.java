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
 * カンバンステータスのフォーマット情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "kanbanStatusFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class KanbanStatusFormatInfo {

    private String csvFileEncode;
    private String csvFileName;
    private String xlsSheetName;

    private String csvStartRow;
    private String xlsStartRow;
    private String csvKanbanName;
    private String xlsKanbanName;
    private String csvKanbanStatus;
    private String xlsKanbanStatus;

    /**
     * コンストラクタ
     */
    public KanbanStatusFormatInfo() {
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
     * CSV:ステータスを取得する。
     *
     * @return CSV:ステータス
     */
    public String getCsvKanbanStatus() {
        return this.csvKanbanStatus;
    }

    /**
     * CSV:ステータスを設定する。
     *
     * @param csvKanbanStatus CSV:ステータス
     */
    public void setCsvKanbanStatus(String csvKanbanStatus) {
        this.csvKanbanStatus = csvKanbanStatus;
    }

    /**
     * Excel:ステータスを取得する。
     *
     * @return Excel:ステータス
     */
    public String getXlsKanbanStatus() {
        return this.xlsKanbanStatus;
    }

    /**
     * Excel:ステータスを設定する。
     *
     * @param xlsKanbanStatus Excel:ステータス
     */
    public void setXlsKanbanStatus(String xlsKanbanStatus) {
        this.xlsKanbanStatus = xlsKanbanStatus;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanStatusFormatInfo{")
                .append("csvFileEncode=").append(this.csvFileEncode)
                .append(", csvFileName=").append(this.csvFileName)
                .append(", xlsSheetName=").append(this.xlsSheetName)
                .append(", csvStartRow=").append(this.csvStartRow)
                .append(", xlsStartRow=").append(this.xlsStartRow)
                .append(", csvKanbanName=").append(this.csvKanbanName)
                .append(", xlsKanbanName=").append(this.xlsKanbanName)
                .append(", csvKanbanStatus=").append(this.csvKanbanStatus)
                .append(", xlsKanbanStatus=").append(this.xlsKanbanStatus)
                .append("}")
                .toString();
    }
}
