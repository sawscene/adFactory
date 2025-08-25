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
 * 工程カンバンプロパティのフォーマット情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "workKanbanPropFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkKanbanPropFormatInfo {

    private String csvFileEncode;
    private String csvFileName;
    private String xlsSheetName;

    private String selectedFormat;

    // フォーマットA
    private String csvStartRow;
    private String xlsStartRow;
    private String csvKanbanName;
    private String xlsKanbanName;
    private String csvWorkNum;
    private String xlsWorkNum;
    private String csvPropName;
    private String xlsPropName;
    private String csvPropType;
    private String xlsPropType;
    private String csvPropValue;
    private String xlsPropValue;
    private String csvWorkName;
    private String xlsWorkName;

    // フォーマットB
    private String f2CsvHeaderRow;
    private String f2XlsHeaderRow;
    private String f2CsvStartRow;
    private String f2XlsStartRow;
    private String f2CsvKanbanName;
    private String f2XlsKanbanName;
    private String f2CsvWorkName;
    private String f2XlsWorkName;
    private String f2CsvWorkNo;
    private String f2XlsWorkNo;

    // プロパティを組み合わせて読み込む
    private Boolean f2IsCheckUnionProp;
    private String f2UnionPropNewName;
    private String f2UnionPropLeftName;
    private String f2UnionPropRightName;

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
    public WorkKanbanPropFormatInfo() {
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
     * フォーマットA:CSV:工程の番号を取得する。
     *
     * @return フォーマットA:CSV:工程の番号
     */
    public String getCsvWorkNum() {
        return this.csvWorkNum;
    }

    /**
     * フォーマットA:CSV:工程の番号を設定する。
     *
     * @param csvWorkNum フォーマットA:CSV:工程の番号
     */
    public void setCsvWorkNum(String csvWorkNum) {
        this.csvWorkNum = csvWorkNum;
    }

    /**
     * フォーマットA:Excel:工程の番号を取得する。
     *
     * @return フォーマットA:Excel:工程の番号
     */
    public String getXlsWorkNum() {
        return this.xlsWorkNum;
    }

    /**
     * フォーマットA:Excel:工程の番号を設定する。
     *
     * @param xlsWorkNum フォーマットA:Excel:工程の番号
     */
    public void setXlsWorkNum(String xlsWorkNum) {
        this.xlsWorkNum = xlsWorkNum;
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
     * フォーマットA:CSV:工程名を取得する。
     *
     * @return フォーマットA:CSV:工程名
     */
    public String getCsvWorkName() {
        return this.csvWorkName;
    }

    /**
     * フォーマットA:CSV:工程名を設定する。
     *
     * @param csvWorkName フォーマットA:CSV:工程名
     */
    public void setCsvWorkName(String csvWorkName) {
        this.csvWorkName = csvWorkName;
    }

    /**
     * フォーマットA:Excel:工程名を取得する。
     *
     * @return フォーマットA:Excel:工程名
     */
    public String getXlsWorkName() {
        return this.xlsWorkName;
    }

    /**
     * フォーマットA:Excel:工程名を設定する。
     *
     * @param xlsWorkName フォーマットA:Excel:工程名
     */
    public void setXlsWorkName(String xlsWorkName) {
        this.xlsWorkName = xlsWorkName;
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
     * フォーマットB:CSV:工程名を取得する。
     *
     * @return フォーマットB:CSV:工程名
     */
    public String getF2CsvWorkName() {
        return this.f2CsvWorkName;
    }

    /**
     * フォーマットB:CSV:工程名を設定する。
     *
     * @param f2CsvWorkName フォーマットB:CSV:工程名
     */
    public void setF2CsvWorkName(String f2CsvWorkName) {
        this.f2CsvWorkName = f2CsvWorkName;
    }

    /**
     * フォーマットB:Excel:工程名を取得する。
     *
     * @return フォーマットB:Excel:工程名
     */
    public String getF2XlsWorkName() {
        return this.f2XlsWorkName;
    }

    /**
     * フォーマットB:Excel:工程名を設定する。
     *
     * @param f2XlsWorkName フォーマットB:Excel:工程名
     */
    public void setF2XlsWorkName(String f2XlsWorkName) {
        this.f2XlsWorkName = f2XlsWorkName;
    }

    /**
     * フォーマットB:CSV:工程の番号を取得する。
     *
     * @return フォーマットB:CSV:工程の番号
     */
    public String getF2CsvWorkNo() {
        return this.f2CsvWorkNo;
    }

    /**
     * フォーマットB:CSV:工程の番号を設定する。
     *
     * @param f2CsvWorkNo フォーマットB:CSV:工程の番号
     */
    public void setF2CsvWorkNo(String f2CsvWorkNo) {
        this.f2CsvWorkNo = f2CsvWorkNo;
    }

    /**
     * フォーマットB:Excel:工程の番号を取得する。
     *
     * @return フォーマットB:Excel:工程の番号
     */
    public String getF2XlsWorkNo() {
        return this.f2XlsWorkNo;
    }

    /**
     * フォーマットB:Excel:工程の番号を設定する。
     *
     * @param f2XlsWorkNo フォーマットB:Excel:工程の番号
     */
    public void setF2XlsWorkNo(String f2XlsWorkNo) {
        this.f2XlsWorkNo = f2XlsWorkNo;
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
     * フォーマットB:プロパティを組み合わせて読み込む？を取得する。
     *
     * @return フォーマットB:プロパティを組み合わせて読み込む？
     */
    public Boolean getF2IsCheckUnionProp() {
        return this.f2IsCheckUnionProp;
    }

    /**
     * フォーマットB:プロパティを組み合わせて読み込む？を設定する。
     *
     * @param f2IsCheckUnionProp フォーマットB:プロパティを組み合わせて読み込む？
     */
    public void setF2IsCheckUnionProp(Boolean f2IsCheckUnionProp) {
        this.f2IsCheckUnionProp = f2IsCheckUnionProp;
    }

    /**
     * フォーマットB:新しいプロパティ名を取得する。
     *
     * @return フォーマットB:新しいプロパティ名
     */
    public String getF2UnionPropNewName() {
        return this.f2UnionPropNewName;
    }

    /**
     * フォーマットB:新しいプロパティ名を設定する。
     *
     * @param f2UnionPropNewName フォーマットB:新しいプロパティ名
     */
    public void setF2UnionPropNewName(String f2UnionPropNewName) {
        this.f2UnionPropNewName = f2UnionPropNewName;
    }

    /**
     * フォーマットB:組み合わせるプロパティ名(左)を取得する。
     *
     * @return フォーマットB:組み合わせるプロパティ名(左)
     */
    public String getF2UnionPropLeftName() {
        return this.f2UnionPropLeftName;
    }

    /**
     * フォーマットB:組み合わせるプロパティ名(左)を設定する。
     *
     * @param f2UnionPropLeftName フォーマットB:組み合わせるプロパティ名(左)
     */
    public void setF2UnionPropLeftName(String f2UnionPropLeftName) {
        this.f2UnionPropLeftName = f2UnionPropLeftName;
    }

    /**
     * フォーマットB:組み合わせるプロパティ名(右)を取得する。
     *
     * @return フォーマットB:組み合わせるプロパティ名(右)
     */
    public String getF2UnionPropRightName() {
        return this.f2UnionPropRightName;
    }

    /**
     * フォーマットB:組み合わせるプロパティ名(右)を設定する。
     *
     * @param f2UnionPropRightName フォーマットB:組み合わせるプロパティ名(右)
     */
    public void setF2UnionPropRightName(String f2UnionPropRightName) {
        this.f2UnionPropRightName = f2UnionPropRightName;
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
        return new StringBuilder("WorkKanbanPropFormatInfo{")
                .append("csvFileEncode=").append(this.csvFileEncode)
                .append(", csvFileName=").append(this.csvFileName)
                .append(", xlsSheetName=").append(this.xlsSheetName)
                .append(", selectedFormat=").append(this.selectedFormat)
                .append(", csvStartRow=").append(this.csvStartRow)
                .append(", xlsStartRow=").append(this.xlsStartRow)
                .append(", csvKanbanName=").append(this.csvKanbanName)
                .append(", xlsKanbanName=").append(this.xlsKanbanName)
                .append(", csvWorkNum=").append(this.csvWorkNum)
                .append(", xlsWorkNum=").append(this.xlsWorkNum)
                .append(", csvPropName=").append(this.csvPropName)
                .append(", xlsPropName=").append(this.xlsPropName)
                .append(", csvPropType=").append(this.csvPropType)
                .append(", xlsPropType=").append(this.xlsPropType)
                .append(", csvPropValue=").append(this.csvPropValue)
                .append(", xlsPropValue=").append(this.xlsPropValue)
                .append(", csvWorkName=").append(this.csvWorkName)
                .append(", xlsWorkName=").append(this.xlsWorkName)
                .append(", f2CsvHeaderRow=").append(this.f2CsvHeaderRow)
                .append(", f2XlsHeaderRow=").append(this.f2XlsHeaderRow)
                .append(", f2CsvStartRow=").append(this.f2CsvStartRow)
                .append(", f2XlsStartRow=").append(this.f2XlsStartRow)
                .append(", f2CsvKanbanName=").append(this.f2CsvKanbanName)
                .append(", f2XlsKanbanName=").append(this.f2XlsKanbanName)
                .append(", f2CsvWorkName=").append(this.f2CsvWorkName)
                .append(", f2XlsWorkName=").append(this.f2XlsWorkName)
                .append(", f2CsvWorkNo=").append(this.f2CsvWorkNo)
                .append(", f2XlsWorkNo=").append(this.f2XlsWorkNo)
                .append(", f2IsCheckUnionProp=").append(this.f2IsCheckUnionProp)
                .append(", f2UnionPropNewName=").append(this.f2UnionPropNewName)
                .append(", f2UnionPropLeftName=").append(this.f2UnionPropLeftName)
                .append(", f2UnionPropRightName=").append(this.f2UnionPropRightName)
                .append(", f2CsvPropValues=").append(this.f2CsvPropValues)
                .append(", f2XlsPropValues=").append(this.f2XlsPropValues)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
