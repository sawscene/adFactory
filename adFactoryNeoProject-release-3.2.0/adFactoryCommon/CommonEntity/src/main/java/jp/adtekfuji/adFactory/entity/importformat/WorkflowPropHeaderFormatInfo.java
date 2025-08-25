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
 * CSV形式（ヘッダー名指定）_工程順プロパティのフォーマット情報
 *
 * @author (AQTOR)Koga
 */
@XmlRootElement(name = "workflowPropHeaderFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowPropHeaderFormatInfo {

    private String headerCsvFileEncode;
    private String headerCsvFileName;
    private String headerCsvHeaderRow;
    private String headerCsvStartRow;
    @XmlElementWrapper(name = "headerCsvWorkflowNames")
    @XmlElement(name = "headerCsvWorkflowName")
    private List<String> headerCsvWorkflowNames = new ArrayList<>();
    private String headerCsvWorkflowDelimiter;

    @XmlElementWrapper(name = "headerCsvPropValues")
    @XmlElement(name = "headerCsvPropValue")
    private List<PropHeaderFormatInfo> headerCsvPropValues = new ArrayList();

    private String fileName;

    /**
     * コンストラクタ
     */
    public WorkflowPropHeaderFormatInfo() {
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
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public List<String> getHeaderCsvWorkflowNames() {
        return headerCsvWorkflowNames;
    }

    /**
     * 工程順名を設定する。
     * @param headerCsvWorkflowNames 工程順名
     */
    public void setHeaderCsvWorkflowNames(List<String> headerCsvWorkflowNames) {
        this.headerCsvWorkflowNames = headerCsvWorkflowNames;
    }

    /**
     * 工程順名（区切り文字）を取得する。
     *
     * @return 工程順名（区切り文字）
     */
    public String getHeaderCsvWorkflowDelimiter() {
        return headerCsvWorkflowDelimiter;
    }
    /**
     * 工程順名（区切り文字）を設定する。
     *
     * @param headerCsvWorkflowDelimiter 工程順名（区切り文字）
     */
    public void setHeaderCsvWorkflowDelimiter(String headerCsvWorkflowDelimiter) {
        this.headerCsvWorkflowDelimiter = headerCsvWorkflowDelimiter;
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
                .append(", headerCsvWorkflowNames=").append(this.headerCsvWorkflowNames)
                .append(", headerCsvWorkflowDelimiter=").append(this.headerCsvWorkflowDelimiter)
                .append(", headerCsvPropValues=").append(this.headerCsvPropValues)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
