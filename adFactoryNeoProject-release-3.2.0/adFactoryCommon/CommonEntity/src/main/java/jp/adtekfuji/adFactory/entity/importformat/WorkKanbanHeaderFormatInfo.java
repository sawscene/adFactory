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
@XmlRootElement(name = "workKanbanHeaderFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkKanbanHeaderFormatInfo {

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
    private String headerCsvTactTime;
    private String headerCsvTactTimeUnit;
    private String headerCsvStartDateTime;
    private String headerCsvEndDateTime;
    private String headerCsvOrganization;
    private String headerCsvEquipment;

    private String fileName;

    /**
     * コンストラクタ
     */
    public WorkKanbanHeaderFormatInfo() {
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
     * カンバン名（１フィールド目）を取得する。
     *
     * @return カンバン名
     */
    public List<String> getHeaderCsvKanbanNames() {
        return headerCsvKanbanNames;
    }

    /**
     * カンバン名を設定する
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
     * 工程名
     * @return 工程名
     */
    public List<String> getHeaderCsvWorkNames() {
        return headerCsvWorkNames;
    }

    /**
     * 工程名設定
     * @param headerCsvWorkNames 工程名
     */
    public void setHeaderCsvWorkNames(List<String> headerCsvWorkNames) {
        this.headerCsvWorkNames = headerCsvWorkNames;
    }

    /**
     * 工程名(区切り文字)
     * @return 工程名(区切り文字)
     */
    public String getHeaderCsvWorkDelimiter() {
        return headerCsvWorkDelimiter;
    }

    /**
     * 工程名(区切り文字)
     * @param headerCsvWorkDelimiter
     */
    public void setHeaderCsvWorkDelimiter(String headerCsvWorkDelimiter) {
        this.headerCsvWorkDelimiter = headerCsvWorkDelimiter;
    }

    /**
     * タクトタイム
     * @return タクトタイム
     */
    public String getHeaderCsvTactTime() {
        return headerCsvTactTime;
    }

    /**
     * タクトタイム
     * @param headerCsvTactTime タクトタイム
     */
    public void setHeaderCsvTactTime(String headerCsvTactTime) {
        this.headerCsvTactTime = headerCsvTactTime;
    }

    /**
     * タクトタイム単位
     * @return タクトタイム単位
     */
    public String getHeaderCsvTactTimeUnit() {
        return headerCsvTactTimeUnit;
    }

    /**
     * タクトタイム単位
     * @param headerCsvTactTimeUnit タクトタイム単位
     */
    public void setHeaderCsvTactTimeUnit(String headerCsvTactTimeUnit) {
        this.headerCsvTactTimeUnit = headerCsvTactTimeUnit;
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
     * 完了予定日時を取得する
     * @return 完了予定日時
     */
    public String getHeaderCsvEndDateTime() {
        return headerCsvEndDateTime;
    }

    /**
     * 完了予定日時を設定する
     * @param headerCsvEndDateTime
     */
    public void setHeaderCsvEndDateTime(String headerCsvEndDateTime) {
        this.headerCsvEndDateTime = headerCsvEndDateTime;
    }

    /**
     * 設備を取得する
     * @return 設備
     */
    public String getHeaderCsvOrganization() {
        return headerCsvOrganization;
    }

    /**
     * 設備を設定する
     * @param headerCsvOrganization 設備
     */
    public void setHeaderCsvOrganization(String headerCsvOrganization) {
        this.headerCsvOrganization = headerCsvOrganization;
    }

    /**
     * 設備を取得
     * @return 設備
     */
    public String getHeaderCsvEquipment() {
        return headerCsvEquipment;
    }

    /**
     * 設備を設定
     * @param headerCsvEquipment 設備
     */
    public void setHeaderCsvEquipment(String headerCsvEquipment) {
        this.headerCsvEquipment = headerCsvEquipment;
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
                .append(", headerCsvKanbanNames=").append(this.headerCsvKanbanNames)
                .append(", headerCsvKanbanDelimiter=").append(this.headerCsvKanbanDelimiter)
                .append(", headerCsvWorkflowNames=").append(this.headerCsvWorkflowNames)
                .append(", headerCsvWorkflowDelimiter=").append(this.headerCsvWorkflowDelimiter)
                .append(", headerCsvWorkflowRev=").append(this.headerCsvWorkflowRev)
                .append(", headerCsvWorkNames=").append(this.headerCsvWorkNames)
                .append(", headerCsvWorkDelimiter=").append(this.headerCsvWorkDelimiter)
                .append(", headerCsvTactTime=").append(this.headerCsvTactTime)
                .append(", headerCsvTactTimeUnit=").append(this.headerCsvTactTimeUnit)
                .append(", headerCsvStartDateTime=").append(this.headerCsvStartDateTime)
                .append(", headerCsvEndDateTime=").append(this.headerCsvEndDateTime)
                .append(", headerCsvOrganization=").append(this.headerCsvOrganization)
                .append(", headerCsvEquipment=").append(this.headerCsvEquipment)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
