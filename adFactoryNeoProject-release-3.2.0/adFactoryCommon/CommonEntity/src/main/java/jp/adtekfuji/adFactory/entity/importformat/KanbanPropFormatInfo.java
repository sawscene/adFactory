/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.importformat;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバンプロパティのフォーマット情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "kanbanPropFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class KanbanPropFormatInfo {

    private String csvFileEncode;
    private String csvFileName;
    private String xlsSheetName;

    private String selectedFormat;

    // フォーマット１
    private String csvStartRow;
    private String xlsStartRow;
    private String csvKanbanName;
    private String xlsKanbanName;
    private String csvWorkflowName;
    private String xlsWorkflowName;
    private String csvPropName;
    private String xlsPropName;
    private String csvPropType;
    private String xlsPropType;
    private String csvPropValue;
    private String xlsPropValue;

    // フォーマット２
    private String f2CsvHeaderRow;
    private String f2XlsHeaderRow;
    private String f2CsvStartRow;
    private String f2XlsStartRow;
    private String f2CsvKanbanName;
    private String f2XlsKanbanName;
    private String f2CsvWorkflowName;
    private String f2XlsWorkflowName;

    @XmlElementWrapper(name = "f2CsvPropValues")
    @XmlElement(name = "csvPropValue")
    private List<String> f2CsvPropValues = new ArrayList();

    @XmlElementWrapper(name = "f2XlsPropValues")
    @XmlElement(name = "xlsPropValue")
    private List<String> f2XlsPropValues = new ArrayList();

    private String fileName;

    /**
     * コンストラクタ
     */
    public KanbanPropFormatInfo() {
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
     * 選択フォーマットを取得する。
     *
     * @return 選択フォーマット
     */
    public String getSelectedFormat() {
        return this.selectedFormat;
    }

    /**
     * 選択フォーマットを設定する。
     *
     * @param selectedFormat 選択フォーマット
     */
    public void setSelectedFormat(String selectedFormat) {
        this.selectedFormat = selectedFormat;
    }

    /**
     * フォーマットA:CSV:読み込み開始行を取得する。
     *
     * @return フォーマットA:CSV:読み込み開始行
     */
    public String getCsvStartRow() {
        return this.csvStartRow;
    }

    /**
     * フォーマットA:CSV:読み込み開始行を設定する。
     *
     * @param csvStartRow フォーマットA:CSV:読み込み開始行
     */
    public void setCsvStartRow(String csvStartRow) {
        this.csvStartRow = csvStartRow;
    }

    /**
     * フォーマットA:Excel:読み込み開始行を取得する。
     *
     * @return フォーマットA:Excel:読み込み開始行
     */
    public String getXlsStartRow() {
        return this.xlsStartRow;
    }

    /**
     * フォーマットA:Excel:読み込み開始行を設定する。
     *
     * @param xlsStartRow フォーマットA:Excel:読み込み開始行
     */
    public void setXlsStartRow(String xlsStartRow) {
        this.xlsStartRow = xlsStartRow;
    }

    /**
     * フォーマットA:CSV:カンバン名を取得する。
     *
     * @return フォーマットA:CSV:カンバン名
     */
    public String getCsvKanbanName() {
        return this.csvKanbanName;
    }

    /**
     * フォーマットA:CSV:カンバン名を設定する。
     *
     * @param csvKanbanName フォーマットA:CSV:カンバン名
     */
    public void setCsvKanbanName(String csvKanbanName) {
        this.csvKanbanName = csvKanbanName;
    }

    /**
     * フォーマットA:Excel:カンバン名を取得する。
     *
     * @return フォーマットA:Excel:カンバン名
     */
    public String getXlsKanbanName() {
        return this.xlsKanbanName;
    }

    /**
     * フォーマットA:Excel:カンバン名を設定する。
     *
     * @param xlsKanbanName フォーマットA:Excel:カンバン名
     */
    public void setXlsKanbanName(String xlsKanbanName) {
        this.xlsKanbanName = xlsKanbanName;
    }

    /**
     * フォーマットA:CSV:工程順名を取得する。
     *
     * @return フォーマットA:CSV:工程順名
     */
    public String getCsvWorkflowName() {
        return this.csvWorkflowName;
    }

    /**
     * フォーマットA:CSV:工程順名を設定する。
     *
     * @param csvWorkflowName フォーマットA:CSV:工程順名
     */
    public void setCsvWorkflowName(String csvWorkflowName) {
        this.csvWorkflowName = csvWorkflowName;
    }

    /**
     * フォーマットA:Excel:工程順名を取得する。
     *
     * @return フォーマットA:Excel:工程順名
     */
    public String getXlsWorkflowName() {
        return this.xlsWorkflowName;
    }

    /**
     * フォーマットA:Excel:工程順名を設定する。
     *
     * @param xlsWorkflowName フォーマットA:Excel:工程順名
     */
    public void setXlsWorkflowName(String xlsWorkflowName) {
        this.xlsWorkflowName = xlsWorkflowName;
    }

    /**
     * フォーマットA:CSV:プロパティ名を取得する。
     *
     * @return フォーマットA:CSV:プロパティ名
     */
    public String getCsvPropName() {
        return this.csvPropName;
    }

    /**
     * フォーマットA:CSV:プロパティ名を設定する。
     *
     * @param csvPropName フォーマットA:CSV:プロパティ名
     */
    public void setCsvPropName(String csvPropName) {
        this.csvPropName = csvPropName;
    }

    /**
     * フォーマットA:Excel:プロパティ名を取得する。
     *
     * @return フォーマットA:Excel:プロパティ名
     */
    public String getXlsPropName() {
        return this.xlsPropName;
    }

    /**
     * フォーマットA:Excel:プロパティ名を設定する。
     *
     * @param xlsPropName フォーマットA:Excel:プロパティ名
     */
    public void setXlsPropName(String xlsPropName) {
        this.xlsPropName = xlsPropName;
    }

    /**
     * フォーマットA:CSV:プロパティ型を取得する。
     *
     * @return フォーマットA:CSV:プロパティ型
     */
    public String getCsvPropType() {
        return this.csvPropType;
    }

    /**
     * フォーマットA:CSV:プロパティ型を設定する。
     *
     * @param csvPropType フォーマットA:CSV:プロパティ型
     */
    public void setCsvPropType(String csvPropType) {
        this.csvPropType = csvPropType;
    }

    /**
     * フォーマットA:Excel:プロパティ型を取得する。
     *
     * @return フォーマットA:Excel:プロパティ型
     */
    public String getXlsPropType() {
        return this.xlsPropType;
    }

    /**
     * フォーマットA:Excel:プロパティ型を設定する。
     *
     * @param xlsPropType フォーマットA:Excel:プロパティ型
     */
    public void setXlsPropType(String xlsPropType) {
        this.xlsPropType = xlsPropType;
    }

    /**
     * フォーマットA:CSV:プロパティ値を取得する。
     *
     * @return フォーマットA:CSV:プロパティ値
     */
    public String getCsvPropValue() {
        return this.csvPropValue;
    }

    /**
     * フォーマットA:CSV:プロパティ値を設定する。
     *
     * @param csvPropValue フォーマットA:CSV:プロパティ値
     */
    public void setCsvPropValue(String csvPropValue) {
        this.csvPropValue = csvPropValue;
    }

    /**
     * フォーマットA:Excel:プロパティ値を取得する。
     *
     * @return フォーマットA:Excel:プロパティ値
     */
    public String getXlsPropValue() {
        return this.xlsPropValue;
    }

    /**
     * フォーマットA:Excel:プロパティ値を設定する。
     *
     * @param xlsPropValue フォーマットA:Excel:プロパティ値
     */
    public void setXlsPropValue(String xlsPropValue) {
        this.xlsPropValue = xlsPropValue;
    }

    /**
     * フォーマットB:CSV:ヘッダー行を取得する。
     *
     * @return フォーマットB:CSV:ヘッダー行
     */
    public String getF2CsvHeaderRow() {
        return this.f2CsvHeaderRow;
    }

    /**
     * フォーマットB:CSV:ヘッダー行を設定する。
     *
     * @param f2CsvHeaderRow フォーマットB:CSV:ヘッダー行
     */
    public void setF2CsvHeaderRow(String f2CsvHeaderRow) {
        this.f2CsvHeaderRow = f2CsvHeaderRow;
    }

    /**
     * フォーマットB:Excel:ヘッダー行を取得する。
     *
     * @return フォーマットB:Excel:ヘッダー行
     */
    public String getF2XlsHeaderRow() {
        return this.f2XlsHeaderRow;
    }

    /**
     * フォーマットB:Excel:ヘッダー行を設定する。
     *
     * @param f2XlsHeaderRow フォーマットB:Excel:ヘッダー行
     */
    public void setF2XlsHeaderRow(String f2XlsHeaderRow) {
        this.f2XlsHeaderRow = f2XlsHeaderRow;
    }

    /**
     * フォーマットB:CSV:読み込み開始行を取得する。
     *
     * @return フォーマットB:CSV:読み込み開始行
     */
    public String getF2CsvStartRow() {
        return this.f2CsvStartRow;
    }

    /**
     * フォーマットB:CSV:読み込み開始行を設定する。
     *
     * @param f2CsvStartRow フォーマットB:CSV:読み込み開始行
     */
    public void setF2CsvStartRow(String f2CsvStartRow) {
        this.f2CsvStartRow = f2CsvStartRow;
    }

    /**
     * フォーマットB:Excel:読み込み開始行を取得する。
     *
     * @return フォーマットB:Excel:読み込み開始行
     */
    public String getF2XlsStartRow() {
        return this.f2XlsStartRow;
    }

    /**
     * フォーマットB:Excel:読み込み開始行を設定する。
     *
     * @param f2XlsStartRow フォーマットB:Excel:読み込み開始行
     */
    public void setF2XlsStartRow(String f2XlsStartRow) {
        this.f2XlsStartRow = f2XlsStartRow;
    }

    /**
     * フォーマットB:CSV:カンバン名を取得する。
     *
     * @return フォーマットB:CSV:カンバン名
     */
    public String getF2CsvKanbanName() {
        return this.f2CsvKanbanName;
    }

    /**
     * フォーマットB:CSV:カンバン名を設定する。
     *
     * @param f2CsvKanbanName フォーマットB:CSV:カンバン名
     */
    public void setF2CsvKanbanName(String f2CsvKanbanName) {
        this.f2CsvKanbanName = f2CsvKanbanName;
    }

    /**
     * フォーマットB:Excel:カンバン名を取得する。
     *
     * @return フォーマットB:Excel:カンバン名
     */
    public String getF2XlsKanbanName() {
        return this.f2XlsKanbanName;
    }

    /**
     * フォーマットB:Excel:カンバン名を設定する。
     *
     * @param f2XlsKanbanName フォーマットB:Excel:カンバン名
     */
    public void setF2XlsKanbanName(String f2XlsKanbanName) {
        this.f2XlsKanbanName = f2XlsKanbanName;
    }

    /**
     * フォーマットB:CSV:工程順名を取得する。
     *
     * @return フォーマットB:CSV:工程順名
     */
    public String getF2CsvWorkflowName() {
        return this.f2CsvWorkflowName;
    }

    /**
     * フォーマットB:CSV:工程順名を設定する。
     *
     * @param f2CsvWorkflowName フォーマットB:CSV:工程順名
     */
    public void setF2CsvWorkflowName(String f2CsvWorkflowName) {
        this.f2CsvWorkflowName = f2CsvWorkflowName;
    }

    /**
     * フォーマットB:Excel:工程順名を取得する。
     *
     * @return フォーマットB:Excel:工程順名
     */
    public String getF2XlsWorkflowName() {
        return this.f2XlsWorkflowName;
    }

    /**
     * フォーマットB:Excel:工程順名を設定する。
     *
     * @param f2XlsWorkflowName フォーマットB:Excel:工程順名
     */
    public void setF2XlsWorkflowName(String f2XlsWorkflowName) {
        this.f2XlsWorkflowName = f2XlsWorkflowName;
    }

    /**
     * フォーマットB:CSV:プロパティを取得する。
     *
     * @return フォーマットB:CSV:プロパティ
     */
    public List<String> getF2CsvPropValues() {
        return this.f2CsvPropValues;
    }

    /**
     * フォーマットB:CSV:プロパティを設定する。
     *
     * @param f2CsvPropValues フォーマットB:CSV:プロパティ
     */
    public void setF2CsvPropValues(List<String> f2CsvPropValues) {
        this.f2CsvPropValues = f2CsvPropValues;
    }

    /**
     * フォーマットB:Excel:プロパティを取得する。
     *
     * @return フォーマットB:Excel:プロパティ
     */
    public List<String> getF2XlsPropValues() {
        return this.f2XlsPropValues;
    }

    /**
     * フォーマットB:Excel:プロパティを設定する。
     *
     * @param f2XlsPropValues フォーマットB:Excel:プロパティ
     */
    public void setF2XlsPropValues(List<String> f2XlsPropValues) {
        this.f2XlsPropValues = f2XlsPropValues;
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
        return new StringBuilder("KanbanPropFormatInfo{")
                .append("csvFileEncode=").append(this.csvFileEncode)
                .append(", csvFileName=").append(this.csvFileName)
                .append(", xlsSheetName=").append(this.xlsSheetName)
                .append(", selectedFormat=").append(this.selectedFormat)
                .append(", csvStartRow=").append(this.csvStartRow)
                .append(", xlsStartRow=").append(this.xlsStartRow)
                .append(", csvKanbanName=").append(this.csvKanbanName)
                .append(", xlsKanbanName=").append(this.xlsKanbanName)
                .append(", csvWorkflowName=").append(this.csvWorkflowName)
                .append(", xlsWorkflowName=").append(this.xlsWorkflowName)
                .append(", csvPropName=").append(this.csvPropName)
                .append(", xlsPropName=").append(this.xlsPropName)
                .append(", csvPropType=").append(this.csvPropType)
                .append(", xlsPropType=").append(this.xlsPropType)
                .append(", csvPropValue=").append(this.csvPropValue)
                .append(", xlsPropValue=").append(this.xlsPropValue)
                .append(", f2CsvHeaderRow=").append(this.f2CsvHeaderRow)
                .append(", f2XlsHeaderRow=").append(this.f2XlsHeaderRow)
                .append(", f2CsvStartRow=").append(this.f2CsvStartRow)
                .append(", f2XlsStartRow=").append(this.f2XlsStartRow)
                .append(", f2CsvKanbanName=").append(this.f2CsvKanbanName)
                .append(", f2XlsKanbanName=").append(this.f2XlsKanbanName)
                .append(", f2CsvWorkflowName=").append(this.f2CsvWorkflowName)
                .append(", f2XlsWorkflowName=").append(this.f2XlsWorkflowName)
                .append(", f2CsvPropValues=").append(this.f2CsvPropValues)
                .append(", f2XlsPropValues=").append(this.f2XlsPropValues)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
