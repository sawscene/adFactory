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
@XmlRootElement(name = "workKnanbaPropHeaderFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkKanbanPropHeaderFormatInfo {

    private String headerCsvFileEncode;
    private String headerCsvFileName;
    private String headerCsvHeaderRow;
    private String headerCsvStartRow;
    @XmlElementWrapper(name = "headerCsvKanbanNames")
    @XmlElement(name = "headerCsvKanbanName")
    private List<String> headerCsvKanbanNames = new ArrayList<>();
    private String headerCsvKanbanDelimiter;
    @XmlElementWrapper(name = "headerCsvWorkflowNames")
    @XmlElement(name = "headerCsvWorkflowName")
    private List<String> headerCsvWorkflowNames = new ArrayList<>();
    private String headerCsvWorkflowDelimiter;
    private String headerCsvWorkflowRev;
    @XmlElementWrapper(name = "headerCsvWorkNames")
    @XmlElement(name = "headerCsvWorkName")
    private List<String> headerCsvWorkNames = new ArrayList<>();
    private String headerCsvWorkDelimiter;

    @XmlElementWrapper(name = "headerCsvPropValues")
    @XmlElement(name = "headerCsvPropValue")
    private List<PropHeaderFormatInfo> headerCsvPropValues = new ArrayList();

    private String fileName;

    /**
     * コンストラクタ
     */
    public WorkKanbanPropHeaderFormatInfo() {
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
     * カンバン名を取得する。
     *
     * @return 工程順名
     */
    public List<String> getHeaderCsvKanbanNames() {
        return headerCsvKanbanNames;
    }

    /**
     * カンバン名を設定
     * @param headerCsvKanbanNames カンバン名
     */
    public void setHeaderCsvKanbanNames(List<String> headerCsvKanbanNames) {
        this.headerCsvKanbanNames = headerCsvKanbanNames;
    }

    /**
     * カンバン名（区切り文字）を取得する。
     *
     * @return カンバン名（区切り文字）
     */
    public String getHeaderCsvKanbanDelimiter() {
        return headerCsvKanbanDelimiter;
    }
    /**
     * カンバン名（区切り文字）を設定する。
     *
     * @param headerCsvWorkflowDelimiter カンバン名（区切り文字）
     */
    public void setHeaderCsvKanbanDelimiter(String headerCsvKanbanDelimiter) {
        this.headerCsvKanbanDelimiter = headerCsvKanbanDelimiter;
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
     * 工程順名を設定する
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
     * 工程順リビジョンを取得する。
     *
     * @return 工程順リビジョン
     */
    public String getHeaderCsvWorkflowRev() {
        return headerCsvWorkflowRev;
    }
    /**
     * 工程順リビジョンを設定する。
     *
     * @param headerCsvWorkflowRev 工程順リビジョン
     */
    public void setHeaderCsvWorkflowRev(String headerCsvWorkflowRev) {
        this.headerCsvWorkflowRev = headerCsvWorkflowRev;
    }
    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public List<String> getHeaderCsvWorkNames() {
        return headerCsvWorkNames;
    }

    /**
     * 工程名を設定する
     * @param headerCsvWorkNames 工程名
     */
    public void setHeaderCsvWorkNames(List<String> headerCsvWorkNames) {
        this.headerCsvWorkNames = headerCsvWorkNames;
    }

    /**
     * 工程名（区切り文字）を取得する。
     *
     * @return カンバン名（区切り文字）
     */
    public String getHeaderCsvWorkDelimiter() {
        return headerCsvWorkDelimiter;
    }

    /**
     * 工程名（区切り文字）を設定する。
     *
     * @param headerCsvWorkflowDelimiter 工程名（区切り文字）
     */
    public void setHeaderCsvWorkDelimiter(String headerCsvWorkDelimiter) {
        this.headerCsvWorkDelimiter = headerCsvWorkDelimiter;
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
        return new StringBuilder("WorkKanbanHeaderFormatInfo{")
                .append("headerCsvFileEncode=").append(this.headerCsvFileEncode)
                .append(", headerCsvFileName=").append(this.headerCsvFileName)
                .append(", headerCsvHeaderRow=").append(this.headerCsvHeaderRow)
                .append(", headerCsvStartRow=").append(this.headerCsvStartRow)
                .append(", headerCsvKanbanNames=").append(this.headerCsvKanbanNames)
                .append(", headerCsvKanbanDelimiter=").append(this.headerCsvKanbanDelimiter)
                .append(", headerCsvWorkflowNames=").append(this.headerCsvWorkflowNames)
                .append(", headerCsvWorkflowDelimiter=").append(this.headerCsvWorkDelimiter)
                .append(", headerCsvWorkflowRev=").append(this.headerCsvWorkflowRev)
                .append(", headerCsvWorkNames=").append(this.headerCsvWorkNames)
                .append(", headerCsvWorkDelimiter=").append(this.headerCsvWorkDelimiter)
                .append(", headerCsvPropValues=").append(this.headerCsvPropValues)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
