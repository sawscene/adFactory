/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.importformat;

import adtekfuji.utility.Tuple;
import javafx.beans.property.SimpleStringProperty;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * CSV形式（ヘッダー名指定）_工程プロパティのフォーマット情報
 *
 * @author (AQTOR)Koga
 */
@XmlRootElement(name = "workPropHeaderFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkPropHeaderFormatInfo {

    private String headerCsvFileEncode;
    private String headerCsvFileName;
    private String headerCsvHeaderRow;
    private String headerCsvStartRow;
    @XmlElementWrapper(name = "headerCsvProcessNames")
    @XmlElement(name = "headerCsvProcessName")
    private List<String> headerCsvProcessNames = new ArrayList<>();

    private String headerCsvProcessDelimiter;

    @XmlElementWrapper(name = "headerCsvPropValues")
    @XmlElement(name = "headerCsvPropValue")
    private List<PropHeaderFormatInfo> headerCsvPropValues = new ArrayList<>();


    private String fileName;

    /**
     * コンストラクタ
     */
    public WorkPropHeaderFormatInfo() {
    }

    /**
     * エンコードを取得する。
     *
     * @return CSV:エンコード
     */
    public String getHeaderCsvFileEncode() {
        return headerCsvFileEncode;
    }
    /**
     * エンコードを設定する。
     *
     * @param headerCsvFileEncode CSV:エンコード
     */
    public void setHeaderCsvFileEncode(String headerCsvFileEncode) {
        this.headerCsvFileEncode = headerCsvFileEncode;
    }

    /**
     * ファイル名を取得する。
     *
     * @return ファイル名
     */
    public String getHeaderCsvFileName() {
        return headerCsvFileName;
    }
    /**
     * ファイル名を設定する。
     *
     * @param headerCsvFileName ファイル名
     */
    public void setHeaderCsvFileName(String headerCsvFileName) {
        this.headerCsvFileName = headerCsvFileName;
    }

    /**
     * ヘッダー行を取得する。
     *
     * @return ヘッダー行
     */
    public String getHeaderCsvHeaderRow() {
        return headerCsvHeaderRow;
    }
    /**
     * ヘッダー行を設定する。
     *
     * @param headerCsvHeaderRow ヘッダー行
     */
    public void setHeaderCsvHeaderRow(String headerCsvHeaderRow) {
        this.headerCsvHeaderRow = headerCsvHeaderRow;
    }

    /**
     * 読み込み開始行を取得する。
     *
     * @return 読み込み開始行
     */
    public String getHeaderCsvStartRow() {
        return headerCsvStartRow;
    }
    /**
     * 読み込み開始行を設定する。
     *
     * @param headerCsvStartRow 読み込み開始行
     */
    public void setHeaderCsvStartRow(String headerCsvStartRow) {
        this.headerCsvStartRow = headerCsvStartRow;
    }

    /**
     * 工程名を取得する
     * @return 工程名
     */
    public List<String> getHeaderCsvProcessNames() {
        return this.headerCsvProcessNames;
    }

    /**
     * 工程名を設定する
     * @param headerCsvProcessNames 工程名
     */
    public void setHeaderCsvProcessNames(List<String> headerCsvProcessNames) {
        this.headerCsvProcessNames = headerCsvProcessNames;
    }

    /**
     * 工程名（区切り文字）を取得する。
     *
     * @return 工程名（区切り文字）
     */
    public String getHeaderCsvProcessDelimiter() {
        return headerCsvProcessDelimiter;
    }
    /**
     * 工程名（区切り文字）を設定する。
     *
     * @param headerCsvProcessDelimiter 工程名（区切り文字）
     */
    public void setHeaderCsvProcessDelimiter(String headerCsvProcessDelimiter) {
        this.headerCsvProcessDelimiter = headerCsvProcessDelimiter;
    }

    /**
     * プロパティを取得する。
     *
     * @return プロパティ
     */
    public List<PropHeaderFormatInfo> getHeaderCsvPropValues() {
        return headerCsvPropValues;
    }
    /**
     * プロパティを設定する。
     *
     * @param headerCsvPropValues プロパティ
     */
    public void setHeaderCsvPropValues(List<PropHeaderFormatInfo> headerCsvPropValues) {
        this.headerCsvPropValues = headerCsvPropValues;
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
        return new StringBuilder("KanbanHeaderFormatInfo{")
                .append("headerCsvFileEncode=").append(this.headerCsvFileEncode)
                .append(", headerCsvFileName=").append(this.headerCsvFileName)
                .append(", headerCsvHeaderRow=").append(this.headerCsvHeaderRow)
                .append(", headerCsvStartRow=").append(this.headerCsvStartRow)
                .append(", headerCsvProcessNames=").append(this.headerCsvProcessNames)
                .append(", headerCsvProcessDelimiter=").append(this.headerCsvProcessDelimiter)
                .append(", headerCsvPropValues=").append(this.headerCsvPropValues)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
