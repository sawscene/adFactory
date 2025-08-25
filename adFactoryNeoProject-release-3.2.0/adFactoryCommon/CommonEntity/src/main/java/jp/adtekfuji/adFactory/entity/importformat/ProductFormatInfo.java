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
 * 製品のフォーマット情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "productFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductFormatInfo {

    private String csvFileEncode;
    private String csvFileName;
    private String xlsSheetName;

    private String csvStartRow;
    private String xlsStartRow;
    private String csvUniqueID;
    private String xlsUniqueID;
    private String csvKanbanName;
    private String xlsKanbanName;

    /**
     * コンストラクタ
     */
    public ProductFormatInfo() {
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
     * CSV:製品シリアルを取得する。
     *
     * @return CSV:製品シリアル
     */
    public String getCsvUniqueID() {
        return this.csvUniqueID;
    }

    /**
     * CSV:製品シリアルを設定する。
     *
     * @param csvUniqueID CSV:製品シリアル
     */
    public void setCsvUniqueID(String csvUniqueID) {
        this.csvUniqueID = csvUniqueID;
    }

    /**
     * Excel:製品シリアルを取得する。
     *
     * @return Excel:製品シリアル
     */
    public String getXlsUniqueID() {
        return this.xlsUniqueID;
    }

    /**
     * Excel:製品シリアルを設定する。
     *
     * @param xlsUniqueID Excel:製品シリアル
     */
    public void setXlsUniqueID(String xlsUniqueID) {
        this.xlsUniqueID = xlsUniqueID;
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

    @Override
    public String toString() {
        return new StringBuilder("KanbanStatusFormatInfo{")
                .append("csvFileEncode=").append(this.csvFileEncode)
                .append(", csvFileName=").append(this.csvFileName)
                .append(", xlsSheetName=").append(this.xlsSheetName)
                .append(", csvStartRow=").append(this.csvStartRow)
                .append(", xlsStartRow=").append(this.xlsStartRow)
                .append(", csvUniqueID=").append(this.csvUniqueID)
                .append(", xlsUniqueID=").append(this.xlsUniqueID)
                .append(", csvKanbanName=").append(this.csvKanbanName)
                .append(", xlsKanbanName=").append(this.xlsKanbanName)
                .append("}")
                .toString();
    }
}
