/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.importformat;

import jakarta.xml.bind.annotation.*;

import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * CSV形式（ヘッダー名指定）_カンバンのフォーマット情報
 *
 * @author (AQTOR)Koga
 */
@XmlRootElement(name = "kanbanHeaderFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class KanbanHeaderFormatInfo {

    private String headerCsvFileEncode;
    private String headerCsvFileName;
    private String headerCsvHeaderRow;
    private String headerCsvStartRow;
    @XmlElementWrapper(name = "headerCsvKanbanHierarchyNames")
    @XmlElement(name = "headerCsvKanbanHierarchyName")
    private List<String> headerCsvKanbanHierarchyNames = new ArrayList<>();
    private String headerCsvHierarchyDelimiter;
    @XmlElementWrapper(name = "headerCsvKanbanNames")
    @XmlElement(name = "headerCsvKanbanName")
    private List<String> headerCsvKanbanNames = new ArrayList<>();
    private String headerCsvKanbanDelimiter;
    @XmlElementWrapper(name = "headerCsvWorkflowNames")
    @XmlElement(name = "headerCsvWorkflowName")
    private List<String> headerCsvWorkflowNames = new ArrayList<>();
    private String headerCsvWorkflowDelimiter;
    private String headerCsvWorkflowRev;
    @XmlElementWrapper(name = "headerCsvModelNames")
    @XmlElement(name = "headerCsvModelName")
    private List<String> headerCsvModelNames = new ArrayList<>();
    private String headerCsvModelDelimiter;
    @XmlElementWrapper(name = "headerCsvProductNumNames")
    @XmlElement(name = "headerCsvProductNumName")
    private List<String> headerCsvProductNumNames = new ArrayList<>();
    private String headerCsvProductDelimiter;
    private String headerCsvStartDateTime;
    private String headerCsvProductionType;
    private String headerCsvLotNum;
    private KanbanStatusEnum kanbanInitStatus;

    private String fileName;

    /**
     * コンストラクタ
     */
    public KanbanHeaderFormatInfo() {
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
     * カンバン階層を取得する。
     *
     * @return カンバン階層
     */
    public List<String> getHeaderCsvKanbanHierarchyNames() {
        return headerCsvKanbanHierarchyNames;
    }

    /**
     * カンバン階層を設定する
     * @param headerCsvKanbanHierarchyNames カンバン階層
     */
    public void setHeaderCsvKanbanHierarchyNames(List<String> headerCsvKanbanHierarchyNames) {
        this.headerCsvKanbanHierarchyNames = headerCsvKanbanHierarchyNames;
    }

    /**
     * カンバン階層（区切り文字）を取得する。
     *
     * @return 工程階層（区切り文字）
     */
    public String getHeaderCsvHierarchyDelimiter() {
        return headerCsvHierarchyDelimiter;
    }
    /**
     * カンバン階層（区切り文字）を設定する。
     *
     * @param headerCsvHierarchyDelimiter 工程階層（区切り文字）
     */
    public void setHeaderCsvHierarchyDelimiter(String headerCsvHierarchyDelimiter) {
        this.headerCsvHierarchyDelimiter = headerCsvHierarchyDelimiter;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public List<String> getHeaderCsvKanbanNames() {
        return headerCsvKanbanNames;
    }

    /**
     * カンバン名を設定する。
     * @param headerCsvKanbanNames v
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
     * @param headerCsvKanbanDelimiter カンバン名（区切り文字）
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
     * 工程順名を設定
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
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public List<String> getHeaderCsvModelNames() {
        return headerCsvModelNames;
    }

    /**
     * モデル名を設定する。
     * @param headerCsvModelNames モデル名
     */
    public void setHeaderCsvModelNames(List<String> headerCsvModelNames) {
        this.headerCsvModelNames = headerCsvModelNames;
    }

    /**
     * モデル名（区切り文字）を取得する。
     *
     * @return モデル名（区切り文字）
     */
    public String getHeaderCsvModelDelimiter() {
        return headerCsvModelDelimiter;
    }
    /**
     * モデル名（区切り文字）を設定する。
     *
     * @param headerCsvModelDelimiter モデル名（区切り文字）
     */
    public void setHeaderCsvModelDelimiter(String headerCsvModelDelimiter) {
        this.headerCsvModelDelimiter = headerCsvModelDelimiter;
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public List<String> getHeaderCsvProductNumNames() {
        return headerCsvProductNumNames;
    }

    /**
     * 製造番号を設定する
     * @param headerCsvProductNumNames 製造番号
     */
    public void setHeaderCsvProductNumNames(List<String> headerCsvProductNumNames) {
        this.headerCsvProductNumNames = headerCsvProductNumNames;
    }

    /**
     * 製造番号（区切り文字）を取得する。
     *
     * @return 製造番号
     */
    public String getHeaderCsvProductDelimiter() {
        return headerCsvProductDelimiter;
    }
    /**
     * 製造番号（区切り文字）を設定する。
     *
     * @param headerCsvProductDelimiter 製造番号（区切り文字）
     */
    public void setHeaderCsvProductDelimiter(String headerCsvProductDelimiter) {
        this.headerCsvProductDelimiter = headerCsvProductDelimiter;
    }

    /**
     * 開始予定日時を取得する。
     *
     * @return 開始予定日時
     */
    public String getHeaderCsvStartDateTime() {
        return this.headerCsvStartDateTime;
    }
    /**
     * 開始予定日時を設定する。
     *
     * @param headerCsvStartDateTime 開始予定日時
     */
    public void setHeaderCsvStartDateTime(String headerCsvStartDateTime) {
        this.headerCsvStartDateTime = headerCsvStartDateTime;
    }

    /**
     * 生産タイプを取得する。
     *
     * @return 生産タイプ
     */
    public String getHeaderCsvProductionType() {
        return headerCsvProductionType;
    }
    /**
     * 生産タイプを取得する。
     *
     * @param headerCsvProductionType　生産タイプ
     */
    public void setHeaderCsvProductionType(String headerCsvProductionType) {
        this.headerCsvProductionType = headerCsvProductionType;
    }

    /**
     * ロット数量を取得する。
     *
     * @return ロット数量
     */
    public String getHeaderCsvLotNum() {
        return headerCsvLotNum;
    }
    /**
     * ロット数量を取得する。
     *
     * @param headerCsvLotNum　ロット数量
     */
    public void setHeaderCsvLotNum(String headerCsvLotNum) {
        this.headerCsvLotNum = headerCsvLotNum;
    }

    /**
     * カンバンの初期ステータスを取得する
     * 
     * @return カンバンの初期ステータス
     */
    public KanbanStatusEnum getKanbanInitStatus() {
        return this.kanbanInitStatus;
    }
    /**
     * カンバンの初期ステータスを設定する
     * 
     * @param kanbanInitStatus カンバンの初期ステータス
     */
    public void setKanbanInitStatus(KanbanStatusEnum kanbanInitStatus) {
        this.kanbanInitStatus = kanbanInitStatus;
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
                .append(", headerCsvKanbanHierarchyNames=").append(this.headerCsvKanbanHierarchyNames)
                .append(", headerCsvHierarchyDelimiter=").append(this.headerCsvHierarchyDelimiter)
                .append(", headerCsvKanbanNames=").append(this.headerCsvKanbanNames)
                .append(", headerCsvKanbanDelimiter=").append(this.headerCsvKanbanDelimiter)
                .append(", headerCsvWorkflowNames=").append(this.headerCsvWorkflowNames)
                .append(", headerCsvWorkflowDelimiter=").append(this.headerCsvWorkflowDelimiter)
                .append(", headerCsvWorkflowRev=").append(this.headerCsvWorkflowRev)
                .append(", headerCsvModelNames=").append(this.headerCsvModelNames)
                .append(", headerCsvModelDelimiter=").append(this.headerCsvModelDelimiter)
                .append(", headerCsvProductNumNames=").append(this.headerCsvProductNumNames)
                .append(", headerCsvProductDelimiter=").append(this.headerCsvProductDelimiter)
                .append(", headerCsvStartDateTime=").append(this.headerCsvStartDateTime)
                .append(", headerCsvProductionType=").append(this.headerCsvProductionType)
                .append(", headerCsvLotNum=").append(this.headerCsvLotNum)
                .append(", kanbanInitStatus=").append(this.kanbanInitStatus)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
