/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.importformat;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV形式（ヘッダー名指定）_工程のフォーマット情報
 *
 * @author (AQTOR)Koga
 */
@XmlRootElement(name = "workHeaderFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkHeaderFormatInfo {

    private String headerCsvFileEncode;
    private String headerCsvFileName;
    private String headerCsvHeaderRow;
    private String headerCsvStartRow;
    @XmlElementWrapper(name = "headerCsvHierarchyNames")
    @XmlElement(name = "headerCsvHierarchyName")
    private List<String> headerCsvProcessHierarchyNames = new ArrayList<>();
    private String headerCsvHierarchyDelimiter;
    @XmlElementWrapper(name = "headerCsvProcessNames")
    @XmlElement(name = "headerCsvProcessName")
    private List<String> headerCsvProcessNames = new ArrayList<>();
    private String headerCsvProcessDelimiter;
    private String headerCsvTactTime;
    private String headerCsvTactTimeUnit;
    private String headerCsvWorkContent1;
    private String headerCsvWorkContent2;
    private String headerCsvWorkContent3;

    private String fileName;

    /**
     * コンストラクタ
     */
    public WorkHeaderFormatInfo() {
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
     * 工程階層）を取得する。
     * @return 工程階層
     */
    public List<String> getHeaderCsvProcessHierarchyNames() {
        return headerCsvProcessHierarchyNames;
    }

    /**
     * 工程階層を取得する
     * @param headerCsvProcessHierarchyNames
     */
    public void setHeaderCsvProcessHierarchyNames(List<String> headerCsvProcessHierarchyNames) {
        this.headerCsvProcessHierarchyNames = headerCsvProcessHierarchyNames;
    }

    /**
     * 工程階層（区切り文字）を取得する。
     *
     * @return 工程階層（区切り文字）
     */
    public String getHeaderCsvHierarchyDelimiter() {
        return headerCsvHierarchyDelimiter;
    }
    /**
     * 工程階層（区切り文字）を設定する。
     *
     * @param headerCsvHierarchyDelimiter 工程階層（区切り文字）
     */
    public void setHeaderCsvHierarchyDelimiter(String headerCsvHierarchyDelimiter) {
        this.headerCsvHierarchyDelimiter = headerCsvHierarchyDelimiter;
    }

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public List<String> getHeaderCsvProcessNames() {
        return headerCsvProcessNames;
    }

    /**
     * 工程名を取得する
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
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public String getHeaderCsvTactTime() {
        return headerCsvTactTime;
    }
    /**
     * タクトタイムを設定する。
     *
     * @param headerCsvTactTime タクトタイム
     */
    public void setHeaderCsvTactTime(String headerCsvTactTime) {
        this.headerCsvTactTime = headerCsvTactTime;
    }

    /**
     * 単位を取得する。
     *
     * @return 単位
     */
    public String getHeaderCsvTactTimeUnit() {
        return headerCsvTactTimeUnit;
    }
    /**
     * 単位を設定する。
     *
     * @param headerCsvTactTimeUnit 単位
     */
    public void setHeaderCsvTactTimeUnit(String headerCsvTactTimeUnit) {
        this.headerCsvTactTimeUnit = headerCsvTactTimeUnit;
    }

    /**
     * 作業内容（１フィールド目）を取得する。
     *
     * @return 作業内容（１フィールド目）
     */
    public String getHeaderCsvWorkContent1() {
        return headerCsvWorkContent1;
    }
    /**
     * 作業内容（１フィールド目）を設定する。
     *
     * @param headerCsvWorkContent1 作業内容（１フィールド目）
     */
    public void setHeaderCsvWorkContent1(String headerCsvWorkContent1) {
        this.headerCsvWorkContent1 = headerCsvWorkContent1;
    }

    /**
     * 作業内容（２フィールド目）を取得する。
     *
     * @return 作業内容（２フィールド目）
     */
    public String getHeaderCsvWorkContent2() {
        return headerCsvWorkContent2;
    }
    /**
     * 作業内容（２フィールド目）を設定する。
     *
     * @param headerCsvWorkContent2 作業内容（２フィールド目）
     */
    public void setHeaderCsvWorkContent2(String headerCsvWorkContent2) {
        this.headerCsvWorkContent2 = headerCsvWorkContent2;
    }

    /**
     * 作業内容（３フィールド目）を取得する。
     *
     * @return 作業内容（３フィールド目）
     */
    public String getHeaderCsvWorkContent3() {
        return headerCsvWorkContent3;
    }
    /**
     * 作業内容（３フィールド目）を設定する。
     *
     * @param headerCsvWorkContent3 作業内容（３フィールド目）
     */
    public void setHeaderCsvWorkContent3(String headerCsvWorkContent3) {
        this.headerCsvWorkContent3 = headerCsvWorkContent3;
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
                .append(", headerCsvProcessHierarchyNames=").append(this.headerCsvProcessHierarchyNames)
                .append(", headerCsvHierarchyDelimiter=").append(this.headerCsvHierarchyDelimiter)
                .append(", headerCsvProcessNames=").append(this.headerCsvProcessNames)
                .append(", headerCsvProcessDelimiter=").append(this.headerCsvProcessDelimiter)
                .append(", headerCsvTactTime=").append(this.headerCsvTactTime)
                .append(", headerCsvTactTimeUnit=").append(this.headerCsvTactTimeUnit)
                .append(", headerCsvWorkContent1=").append(this.headerCsvWorkContent1)
                .append(", headerCsvWorkContent2=").append(this.headerCsvWorkContent2)
                .append(", headerCsvWorkContent3=").append(this.headerCsvWorkContent3)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
