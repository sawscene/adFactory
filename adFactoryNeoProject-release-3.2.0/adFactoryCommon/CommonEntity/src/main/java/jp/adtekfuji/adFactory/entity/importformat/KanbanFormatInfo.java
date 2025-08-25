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
 * カンバンのフォーマット情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "kanbanFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class KanbanFormatInfo {

    private String csvFileEncode;
    private String csvFileName;
    private String xlsSheetName;

    private String csvStartRow;
    private String xlsStartRow;
    private String csvHierarchyName;
    private String xlsHierarchyName;
    private String csvKanbanName;
    private String xlsKanbanName;
    private String csvWorkflowName;
    private String xlsWorkflowName;
    private String csvWorkflowRev;
    private String xlsWorkflowRev;
    private String csvModelName;
    private String xlsModelName;
    private String csvStartDateTime;
    private String xlsStartDateTime;
    private String csvProductionType;
    private String xlsProductionType;
    private String csvLotNum;
    private String xlsLotNum;
    private String csvProductionNumber; // CSV: 製造番号
    private String xlsProductionNumber; // Excel: 製造番号
    private String csvStartSerial;      // CSV: 開始シリアル番号
    private String xlsStartSerial;      // Excel: 開始シリアル番号
    private String csvEndSerial;        // CSV: 終了シリアル番号
    private String xlsEndSerial;        // Excel: 終了シリアル番号
    
    private String csvCycleTime;        // CSV: 標準作業時間
    private String xlsCycleTime;        // Excel: 標準作業時間

    private Boolean isCheckKanbanHierarchy;
    private String kanbanHierarchyName;

    private Boolean isCheckWorkflowWithModel;
    private Boolean isCheckWorkflowRegex;

    @XmlElementWrapper(name = "workflowRegexInfos")
    @XmlElement(name = "workflowRegexInfo")
    private List<WorkflowRegexInfo> workflowRegexInfos = new ArrayList();

    private String fileName;

    /**
     * コンストラクタ
     */
    public KanbanFormatInfo() {
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
     * CSV:カンバン階層(列)を取得する。
     *
     * @return CSV:カンバン階層
     */
    public String getCsvHierarchyName() {
        return this.csvHierarchyName;
    }

    /**
     * CSV:カンバン階層(列)を設定する。
     *
     * @param csvHierarchyName CSV:カンバン階層
     */
    public void setCsvHierarchyName(String csvHierarchyName) {
        this.csvHierarchyName = csvHierarchyName;
    }

    /**
     * Excel:カンバン階層(列)を取得する。
     *
     * @return Excel:カンバン階層
     */
    public String getXlsHierarchyName() {
        return this.xlsHierarchyName;
    }

    /**
     * Excel:カンバン階層(列)を設定する。
     *
     * @param xlsHierarchyName Excel:カンバン階層
     */
    public void setXlsHierarchyName(String xlsHierarchyName) {
        this.xlsHierarchyName = xlsHierarchyName;
    }

    /**
     * CSV:カンバン名(列)を取得する。
     *
     * @return CSV:カンバン名
     */
    public String getCsvKanbanName() {
        return this.csvKanbanName;
    }

    /**
     * CSV:カンバン名(列)を設定する。
     *
     * @param csvKanbanName CSV:カンバン名
     */
    public void setCsvKanbanName(String csvKanbanName) {
        this.csvKanbanName = csvKanbanName;
    }

    /**
     * Excel:カンバン名(列)を取得する。
     *
     * @return Excel:カンバン名
     */
    public String getXlsKanbanName() {
        return this.xlsKanbanName;
    }

    /**
     * Excel:カンバン名(列)を設定する。
     *
     * @param xlsKanbanName Excel:カンバン名
     */
    public void setXlsKanbanName(String xlsKanbanName) {
        this.xlsKanbanName = xlsKanbanName;
    }

    /**
     * CSV:工程順名(列)を取得する。
     *
     * @return CSV:工程順名
     */
    public String getCsvWorkflowName() {
        return this.csvWorkflowName;
    }

    /**
     * CSV:工程順名(列)を設定する。
     *
     * @param csvWorkflowName CSV:工程順名
     */
    public void setCsvWorkflowName(String csvWorkflowName) {
        this.csvWorkflowName = csvWorkflowName;
    }

    /**
     * Excel:工程順名(列)を取得する。
     *
     * @return Excel:工程順名
     */
    public String getXlsWorkflowName() {
        return this.xlsWorkflowName;
    }

    /**
     * Excel:工程順名(列)を設定する。
     *
     * @param xlsWorkflowName Excel:工程順名
     */
    public void setXlsWorkflowName(String xlsWorkflowName) {
        this.xlsWorkflowName = xlsWorkflowName;
    }

    /**
     * CSV:工程順版数(列)を取得する。
     *
     * @return CSV:工程順版数
     */
    public String getCsvWorkflowRev() {
        return this.csvWorkflowRev;
    }

    /**
     * CSV:工程順版数(列)を設定する。
     *
     * @param csvWorkflowRev CSV:工程順版数
     */
    public void setCsvWorkflowRev(String csvWorkflowRev) {
        this.csvWorkflowRev = csvWorkflowRev;
    }

    /**
     * Excel:工程順版数(列)を取得する。
     *
     * @return Excel:工程順版数
     */
    public String getXlsWorkflowRev() {
        return this.xlsWorkflowRev;
    }

    /**
     * Excel:工程順版数(列)を設定する。
     *
     * @param xlsWorkflowRev Excel:工程順版数
     */
    public void setXlsWorkflowRev(String xlsWorkflowRev) {
        this.xlsWorkflowRev = xlsWorkflowRev;
    }

    /**
     * CSV:モデル名(列)を取得する。
     *
     * @return CSV:モデル名
     */
    public String getCsvModelName() {
        return this.csvModelName;
    }

    /**
     * CSV:モデル名(列)を設定する。
     *
     * @param csvModelName CSV:モデル名
     */
    public void setCsvModelName(String csvModelName) {
        this.csvModelName = csvModelName;
    }

    /**
     * Excel:モデル名(列)を取得する。
     *
     * @return Excel:モデル名
     */
    public String getXlsModelName() {
        return this.xlsModelName;
    }

    /**
     * Excel:モデル名(列)を設定する。
     *
     * @param xlsModelName Excel:モデル名
     */
    public void setXlsModelName(String xlsModelName) {
        this.xlsModelName = xlsModelName;
    }

    /**
     * CSV:開始予定日時(列)を取得する。
     *
     * @return CSV:開始予定日時
     */
    public String getCsvStartDateTime() {
        return this.csvStartDateTime;
    }

    /**
     * CSV:開始予定日時(列)を設定する。
     *
     * @param csvStartDateTime CSV:開始予定日時
     */
    public void setCsvStartDateTime(String csvStartDateTime) {
        this.csvStartDateTime = csvStartDateTime;
    }

    /**
     * Excel:開始予定日時(列)を取得する。
     *
     * @return Excel:開始予定日時
     */
    public String getXlsStartDateTime() {
        return this.xlsStartDateTime;
    }

    /**
     * Excel:開始予定日時(列)を設定する。
     *
     * @param xlsStartDateTime Excel:開始予定日時
     */
    public void setXlsStartDateTime(String xlsStartDateTime) {
        this.xlsStartDateTime = xlsStartDateTime;
    }

    /**
     * CSV:生産タイプ(列)を取得する。
     *
     * @return CSV:生産タイプ
     */
    public String getCsvProductionType() {
        return this.csvProductionType;
    }

    /**
     * CSV:生産タイプ(列)を設定する。
     *
     * @param productionType CSV:生産タイプ
     */
    public void setCsvProductionType(String productionType) {
        this.csvProductionType = productionType;
    }

    /**
     * Excel:生産タイプ(列)を取得する。
     *
     * @return Excel:生産タイプ
     */
    public String getXlsProductionType() {
        return this.xlsProductionType;
    }

    /**
     * Excel:生産タイプ(列)を設定する。
     *
     * @param productionType Excel:生産タイプ
     */
    public void setXlsProductionType(String productionType) {
        this.xlsProductionType = productionType;
    }

    /**
     * CSV:ロット数量(列)を取得する。
     *
     * @return CSV:ロット数量
     */
    public String getCsvLotNum() {
        return this.csvLotNum;
    }

    /**
     * CSV:ロット数量(列)を設定する。
     *
     * @param csvLotNum CSV:ロット数量
     */
    public void setCsvLotNum(String csvLotNum) {
        this.csvLotNum = csvLotNum;
    }

    /**
     * Excel:ロット数量(列)を取得する。
     *
     * @return Excel:ロット数量
     */
    public String getXlsLotNum() {
        return this.xlsLotNum;
    }

    /**
     * Excel:ロット数量(列)を設定する。
     *
     * @param xlsLotNum Excel:ロット数量
     */
    public void setXlsLotNum(String xlsLotNum) {
        this.xlsLotNum = xlsLotNum;
    }

    /**
     * CSV:製造番号(列)を取得する。
     *
     * @return CSV:製造番号
     */
    public String getCsvProductionNumber() {
        return this.csvProductionNumber;
    }

    /**
     * CSV:製造番号(列)を設定する。
     *
     * @param csvProductionNumber CSV:製造番号
     */
    public void setCsvProductionNumber(String csvProductionNumber) {
        this.csvProductionNumber = csvProductionNumber;
    }

    /**
     * Excel:製造番号(列)を取得する。
     *
     * @return Excel:製造番号
     */
    public String getXlsProductionNumber() {
        return this.xlsProductionNumber;
    }

    /**
     * Excel:製造番号(列)を設定する。
     *
     * @param xlsProductionNumber Excel:製造番号
     */
    public void setXlsProductionNumber(String xlsProductionNumber) {
        this.xlsProductionNumber = xlsProductionNumber;
    }

    /**
     * CSV:開始シリアル番号(列)を取得する。
     *
     * @return CSV:開始シリアル番号
     */
    public String getCsvStartSerial() {
        return csvStartSerial;
    }

    /**
     * CSV:開始シリアル番号(列)を設定する。
     *
     * @param csvStartSerial CSV:開始シリアル番号
     */
    public void setCsvStartSerial(String csvStartSerial) {
        this.csvStartSerial = csvStartSerial;
    }

    /**
     * Excel:開始シリアル番号(列)を取得する。
     *
     * @return Excel:開始シリアル番号
     */
    public String getXlsStartSerial() {
        return xlsStartSerial;
    }

    /**
     * Excel:開始シリアル番号(列)を設定する。
     *
     * @param xlsStartSerial Excel:開始シリアル番号
     */
    public void setXlsStartSerial(String xlsStartSerial) {
        this.xlsStartSerial = xlsStartSerial;
    }

    /**
     * CSV:開始シリアル番号(列)を取得する。
     *
     * @return CSV:開始シリアル番号
     */
    public String getCsvEndSerial() {
        return csvEndSerial;
    }

    /**
     * CSV:終了シリアル番号(列)を設定する。
     *
     * @param csvEndSerial CSV:終了シリアル番号
     */
    public void setCsvEndSerial(String csvEndSerial) {
        this.csvEndSerial = csvEndSerial;
    }

    /**
     * Excel:終了シリアル番号(列)を取得する。
     *
     * @return Excel:終了シリアル番号
     */
    public String getXlsEndSerial() {
        return xlsEndSerial;
    }

    /**
     * Excel:終了シリアル番号(列)を設定する。
     *
     * @param xlsEndSerial Excel:終了シリアル番号
     */
    public void setXlsEndSerial(String xlsEndSerial) {
        this.xlsEndSerial = xlsEndSerial;
    }

    /**
     * CSV:標準作業時間を取得する。
     * 
     * @return 
     */
    public String getCsvCycleTime() {
        return csvCycleTime;
    }

    /**
     * CSV:標準作業時間を設定する。
     * 
     * @param csvCycleTime 
     */
    public void setCsvCycleTime(String csvCycleTime) {
        this.csvCycleTime = csvCycleTime;
    }

    /**
     * Excel:標準作業時間を取得する。
     * 
     * @return 
     */
    public String getXlsCycleTime() {
        return xlsCycleTime;
    }

    /**
     * Excel:標準作業時間を設定する。
     * 
     * @param xlsCycleTime 
     */
    public void setXlsCycleTime(String xlsCycleTime) {
        this.xlsCycleTime = xlsCycleTime;
    }
    
    /**
     * カンバン階層を指定するかを取得する。
     *
     * @return カンバン階層を指定するか
     */
    public Boolean getIsCheckKanbanHierarchy() {
        return this.isCheckKanbanHierarchy;
    }

    /**
     * カンバン階層を指定するかを設定する。
     *
     * @param isCheckKanbanHierarchy　カンバン階層を指定するか
     */
    public void setIsCheckKanbanHierarchy(Boolean isCheckKanbanHierarchy) {
        this.isCheckKanbanHierarchy = isCheckKanbanHierarchy;
    }

    /**
     * カンバン階層名を取得する。
     *
     * @return カンバン階層名
     */
    public String getKanbanHierarchyName() {
        return this.kanbanHierarchyName;
    }

    /**
     * カンバン階層名を設定する。
     *
     * @param kanbanHierarchyName カンバン階層名
     */
    public void setKanbanHierarchyName(String kanbanHierarchyName) {
        this.kanbanHierarchyName = kanbanHierarchyName;
    }

    /**
     * モデル名で工程順を指定するかを取得する。
     *
     * @return モデル名で工程順を指定するか
     */
    public Boolean getIsCheckWorkflowWithModel() {
        return this.isCheckWorkflowWithModel;
    }

    /**
     * モデル名で工程順を指定するかを設定する。
     *
     * @param isCheckWorkflowWithModel モデル名で工程順を指定するか
     */
    public void setIsCheckWorkflowWithModel(Boolean isCheckWorkflowWithModel) {
        this.isCheckWorkflowWithModel = isCheckWorkflowWithModel;
    }

    /**
     * モデル名と工程順名の組み合わせで工程順を指定するかを取得する。
     *
     * @return モデル名と工程順名の組み合わせで工程順を指定するか
     */
    public Boolean getIsCheckWorkflowRegex() {
        return this.isCheckWorkflowRegex;
    }

    /**
     * モデル名と工程順名の組み合わせで工程順を指定するかを設定する。
     *
     * @param isCheckWorkflowRegex モデル名と工程順名の組み合わせで工程順を指定するか
     */
    public void setIsCheckWorkflowRegex(Boolean isCheckWorkflowRegex) {
        this.isCheckWorkflowRegex = isCheckWorkflowRegex;
    }

    /**
     * モデル名の条件と工程順を取得する。
     *
     * @return モデル名の条件と工程順
     */
    public List<WorkflowRegexInfo> getWorkflowRegexInfos() {
        return this.workflowRegexInfos;
    }

    /**
     * モデル名の条件と工程順を設定する。
     *
     * @param workflowRegexInfos モデル名の条件と工程順
     */
    public void setWorkflowRegexInfos(List<WorkflowRegexInfo> workflowRegexInfos) {
        this.workflowRegexInfos = workflowRegexInfos;
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
        return new StringBuilder("KanbanFormatInfo{")
                .append("csvFileEncode=").append(this.csvFileEncode)
                .append(", csvFileName=").append(this.csvFileName)
                .append(", xlsSheetName=").append(this.xlsSheetName)
                .append(", csvStartRow=").append(this.csvStartRow)
                .append(", xlsStartRow=").append(this.xlsStartRow)
                .append(", csvHierarchyName=").append(this.csvHierarchyName)
                .append(", xlsHierarchyName=").append(this.xlsHierarchyName)
                .append(", csvKanbanName=").append(this.csvKanbanName)
                .append(", xlsKanbanName=").append(this.xlsKanbanName)
                .append(", csvWorkflowName=").append(this.csvWorkflowName)
                .append(", xlsWorkflowName=").append(this.xlsWorkflowName)
                .append(", csvWorkflowRev=").append(this.csvWorkflowRev)
                .append(", xlsWorkflowRev=").append(this.xlsWorkflowRev)
                .append(", csvModelName=").append(this.csvModelName)
                .append(", xlsModelName=").append(this.xlsModelName)
                .append(", csvStartDateTime=").append(this.csvStartDateTime)
                .append(", xlsStartDateTime=").append(this.xlsStartDateTime)
                .append(", csvProductionType=").append(this.csvProductionType)
                .append(", xlsProductionType=").append(this.xlsProductionType)
                .append(", csvLotNum=").append(this.csvLotNum)
                .append(", xlsLotNum=").append(this.xlsLotNum)
                .append(", csvProductionNumber=").append(this.csvProductionNumber)
                .append(", xlsProductionNumber=").append(this.xlsProductionNumber)
                .append(", isCheckKanbanHierarchy=").append(this.isCheckKanbanHierarchy)
                .append(", kanbanHierarchyName=").append(this.kanbanHierarchyName)
                .append(", isCheckWorkflowWithModel=").append(this.isCheckWorkflowWithModel)
                .append(", isCheckWorkflowRegex=").append(this.isCheckWorkflowRegex)
                .append(", workflowRegexInfos=").append(this.workflowRegexInfos)
                .append(", fileName=").append(this.fileName)
                .append("}")
                .toString();
    }
}
