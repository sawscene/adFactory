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
 * CSV形式（ヘッダー名指定）_工程順のフォーマット情報
 *
 * @author (AQTOR)Koga
 */
@XmlRootElement(name = "workflowHeaderFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowHeaderFormatInfo {

    public enum PROCESS_TYPE{
        PARALLEL("key.Parallel"),
        SERIAL("key.Series");

        public String name;

        PROCESS_TYPE(String name) {
            this.name = name;
        }
    }


    private String headerCsvFileEncode;
    private String headerCsvFileName;
    private String headerCsvHeaderRow;
    private String headerCsvStartRow;
    @XmlElementWrapper(name = "headerCsvHierarchyNames")
    @XmlElement(name = "headerCsvHierarchyName")
    private List<String> headerCsvWorkflowHierarchyNames = new ArrayList<>();
    private String headerCsvHierarchyDelimiter;
    @XmlElementWrapper(name = "headerCsvWorkflowNames")
    @XmlElement(name = "headerCsvWorkflowName")
    private List<String> headerCsvWorkflowNames = new ArrayList<>();
    private String headerCsvWorkflowDelimiter;
    @XmlElementWrapper(name = "headerCsvModelNames")
    @XmlElement(name = "headerCsvModelName")
    private List<String> headerCsvModelNames = new ArrayList<>();
    private String headerCsvModelDelimiter;
    @XmlElementWrapper(name = "headerCsvProcessNames")
    @XmlElement(name = "headerCsvProcessName")
    private List<String> headerCsvProcessNames = new ArrayList<>();
    private String headerCsvProcessNameDelimiter;
    private String headerCsvOrganization;
    private String headerCsvEquipment;
    private String headerCsvProcOrder;
    private PROCESS_TYPE headerCsvProcCon;
    private Boolean isReschedule;

    private String fileName;

    /**
     * コンストラクタ
     */
    public WorkflowHeaderFormatInfo() {
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
     * 工程順階層を取得する。
     *
     * @return 工程順階層
     */
    public List<String> getHeaderCsvWorkflowHierarchyNames() {
        return headerCsvWorkflowHierarchyNames;
    }

    /**
     * 工程順階層を設定する。
     * @param headerCsvWorkflowHierarchyNames 工程順階層
     */
    public void setHeaderCsvWorkflowHierarchyNames(List<String> headerCsvWorkflowHierarchyNames) {
        this.headerCsvWorkflowHierarchyNames = headerCsvWorkflowHierarchyNames;
    }

    /**
     * 工程順階層（区切り文字）を取得する。
     *
     * @return 工程順階層（区切り文字）
     */
    public String getHeaderCsvHierarchyDelimiter() {
        return headerCsvHierarchyDelimiter;
    }
    /**
     * 工程順階層（区切り文字）を設定する。
     *
     * @param headerCsvHierarchyDelimiter 工程階層（区切り文字）
     */
    public void setHeaderCsvHierarchyDelimiter(String headerCsvHierarchyDelimiter) {
        this.headerCsvHierarchyDelimiter = headerCsvHierarchyDelimiter;
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
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public List<String> getHeaderCsvProcessNames() {
        return headerCsvProcessNames;
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
    public String getHeaderCsvProcessNameDelimiter() {
        return headerCsvProcessNameDelimiter;
    }
    /**
     * 工程名（区切り文字）を設定する。
     *
     * @param headerCsvProcessNameDelimiter 工程名（区切り文字）
     */
    public void setHeaderCsvProcessNameDelimiter(String headerCsvProcessNameDelimiter) {
        this.headerCsvProcessNameDelimiter = headerCsvProcessNameDelimiter;
    }

    /**
     * 組織を取得する。
     *
     * @return 組織
     */
    public String getHeaderCsvOrganization() {
        return headerCsvOrganization;
    }
    /**
     * 組織を設定する。
     *
     * @param headerCsvOrganization 組織
     */
    public void setHeaderCsvOrganization(String headerCsvOrganization) {
        this.headerCsvOrganization = headerCsvOrganization;
    }

    /**
     * 設備を取得する。
     *
     * @return 設備
     */
    public String getHeaderCsvEquipment() {
        return headerCsvEquipment;
    }
    /**
     * 設備を設定する。
     *
     * @param headerCsvEquipment 設備
     */
    public void setHeaderCsvEquipment(String headerCsvEquipment) {
        this.headerCsvEquipment = headerCsvEquipment;
    }

    /**
     * 工程の並び順を取得する。
     *
     * @return 工程の並び順
     */
    public String getHeaderCsvProcOrder() {
        return headerCsvProcOrder;
    }
    /**
     * 工程の並び順を設定する。
     *
     * @param headerCsvProcOrder 工程の並び順
     */
    public void setHeaderCsvProcOrder(String headerCsvProcOrder) {
        this.headerCsvProcOrder = headerCsvProcOrder;
    }

    /**
     * 工程接続を取得する。
     *
     * @return 工程接続
     */
    public PROCESS_TYPE getHeaderCsvProcCon() {
        return headerCsvProcCon;
    }
    /**
     * 工程接続を設定する。
     *
     * @param headerCsvProcCon 工程接続
     */
    public void setHeaderCsvProcCon(PROCESS_TYPE headerCsvProcCon) {
        this.headerCsvProcCon = headerCsvProcCon;
    }

    /**
     * リスケジュールするか?
     * 
     * @return リスケジュールするか?
     */
    public Boolean getIsReschedule() {
        return this.isReschedule;
    }
    /**
     * リスケジュールするか?
     * 
     * @param isReschedule リスケジュールするか?
     */
    public void setIsReschedule(Boolean isReschedule) {
        this.isReschedule = isReschedule;
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
                .append(", headerCsvWorkflowHierarchyNames=").append(this.headerCsvWorkflowHierarchyNames)
                .append(", headerCsvHierarchyDelimiter=").append(this.headerCsvHierarchyDelimiter)
                .append(", headerCsvWorkflowNames=").append(this.headerCsvWorkflowNames)
                .append(", headerCsvWorkflowDelimiter=").append(this.headerCsvWorkflowDelimiter)
                .append(", headerCsvModelNames=").append(this.headerCsvModelNames)
                .append(", headerCsvModelDelimiter=").append(this.headerCsvModelDelimiter)
                .append(", headerCsvProcessNames=").append(this.headerCsvProcessNames)
                .append(", headerCsvProcessNameDelimiter=").append(this.headerCsvProcessNameDelimiter)
                .append(", headerCsvOrganization=").append(this.headerCsvOrganization)
                .append(", headerCsvEquipment=").append(this.headerCsvEquipment)
                .append(", headerCsvProcOrder=").append(this.headerCsvProcOrder)
                .append(", headerCsvProcCon=").append(this.headerCsvProcCon)
                .append(", isReschedule=").append(this.isReschedule)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
