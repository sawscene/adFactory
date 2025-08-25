/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.csv;

import adtekfuji.utility.StringUtils;
import java.util.Objects;

/**
 * カンバン インポート用データ
 *
 * @author nar-nakamura
 */
public class ImportKanbanCsv {

    private String kanbanHierarchyName; // カンバン階層名
    private String kanbanName;          // カンバン名
    private String workflowName;        // 工程順名
    private String workflowRev;         // 工程順版数
    private String modelName;           // モデル名
    private String startDatetime;       // 開始予定日時
    private String productionType;      // 生産タイプ
    private String lotQuantity;         // ロット数量
    private String productionNumber;    // 製造番号
    private String startSerial;         // 開始シリアル番号
    private String endSerial;           // 終了シリアル番号
    private String cycleTime;           // 標準作業時間

    private boolean enableConcat;

    /**
     * カンバン インポート用データ
     */
    public ImportKanbanCsv() {
    }

    /**
     * カンバン インポート用データ
     *
     * @param kanbanHierarchyName カンバン階層名
     * @param kanbanName カンバン名
     * @param workflowName 工程順名
     */
    public ImportKanbanCsv(String kanbanHierarchyName, String kanbanName, String workflowName) {
        this.kanbanHierarchyName = kanbanHierarchyName;
        this.kanbanName = kanbanName;
        this.workflowName = workflowName;
    }

    /**
     * カンバン インポート用データ
     *
     * @param kanbanHierarchyName カンバン階層名
     * @param kanbanName カンバン名
     * @param workflowName 工程順名
     * @param workflowRev 工程順版数
     * @param modelName モデル名
     * @param startDatetime 開始予定日時
     * @param productionType 生産タイプ (0:一個流し生産, 1:ロット一個流し生産, 2:ロット流し生産)
     * @param lotQuantity ロット数量
     * @param productionNumber 製造番号
     */
    public ImportKanbanCsv(String kanbanHierarchyName, String kanbanName, String workflowName, String workflowRev, String modelName, String startDatetime, String productionType, String lotQuantity, String productionNumber) {
        this.kanbanHierarchyName = kanbanHierarchyName;
        this.kanbanName = kanbanName;
        this.workflowName = workflowName;
        this.workflowRev = workflowRev;
        this.modelName = modelName;
        this.startDatetime = startDatetime;
        this.productionType = productionType;
        this.lotQuantity = lotQuantity;
        this.productionNumber = productionNumber;
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
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    /**
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
        return this.workflowName;
    }

    /**
     * 工程順名を設定する。
     *
     * @param workflowName 工程順名
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /**
     * 工程順版数を取得する。
     *
     * @return 工程順版数
     */
    public String getWorkflowRev() {
        return this.workflowRev;
    }

    /**
     * 工程順版数を設定する。
     *
     * @param workflowRev 工程順版数
     */
    public void setWorkflowRev(String workflowRev) {
        this.workflowRev = workflowRev;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 開始予定日時を取得する。
     *
     * @return 開始予定日時
     */
    public String getStartDatetime() {
        return this.startDatetime;
    }

    /**
     * 開始予定日時を設定する。
     *
     * @param startDatetime 開始予定日時
     */
    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }

    /**
     * 生産タイプを取得する。
     *
     * @return 生産タイプ (0:一個流し生産, 1:ロット一個流し生産, 2:ロット流し生産)
     */
    public String getProductionType() {
        if (StringUtils.isEmpty(this.productionType)) {
            return "0";
        }
        return this.productionType;
    }

    /**
     * 生産タイプを設定する。
     *
     * @param productionType 生産タイプ (0:一個流し生産, 1:ロット一個流し生産, 2:ロット流し生産)
     */
    public void setProductionType(String productionType) {
        this.productionType = productionType;
    }

    /**
     * ロット数量を取得する。
     *
     * @return ロット数量
     */
    public String getLotQuantity() {
        if (StringUtils.isEmpty(this.productionType)) {
            return "1";
        }
        return this.lotQuantity;
    }

    /**
     * ロット数量を設定する。
     *
     * @param lotQuantity ロット数量
     */
    public void setLotQuantity(String lotQuantity) {
        this.lotQuantity = lotQuantity;
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getProductionNumber() {
        return this.productionNumber;
    }

    /**
     * 製造番号を設定する。
     *
     * @param productionNumber 製造番号
     */
    public void setProductionNumber(String productionNumber) {
        this.productionNumber = productionNumber;
    }

    /**
     * 開始シリアル番号を取得する。
     * 
     * @return 開始シリアル番号
     */
    public String getStartSerial() {
        return startSerial;
    }

    /**
     * 開始シリアル番号を設定する。
     * 
     * @param startSerial 開始シリアル番号
     */
    public void setStartSerial(String startSerial) {
        this.startSerial = startSerial;
    }

    /**
     * 終了シリアル番号を取得する。
     * 
     * @return 
     */
    public String getEndSerial() {
        return endSerial;
    }

    /**
     * 終了シリアル番号を設定する。
     * 
     * @param endSerial 終了シリアル番号
     */
    public void setEndSerial(String endSerial) {
        this.endSerial = endSerial;
    }

    /**
     * 標準作業時間を取得する。
     * 
     * @return 
     */
    public String getCycleTime() {
        return cycleTime;
    }

    /**
     * 標準作業時間を設定する。
     * 
     * @param cycleTime 
     */
    public void setCycleTime(String cycleTime) {
        this.cycleTime = cycleTime;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現 
     */
    @Override
    public String toString() {
        return new StringBuilder("ImportKanbanCsv{")
                .append("kanbanHierarchyName=").append(this.kanbanHierarchyName)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", workflowName=").append(this.workflowName)
                .append(", workflowRev=").append(this.workflowRev)
                .append(", modelName=").append(this.modelName)
                .append(", startDatetime=").append(this.startDatetime)
                .append(", productionType=").append(this.productionType)
                .append(", lotQuantity=").append(this.lotQuantity)
                .append(", productionNumber=").append(this.productionNumber)
                .append(", startSerial=").append(this.startSerial)
                .append(", endSerial=").append(this.endSerial)
                .append("}")
                .toString();
    }

    /**
     *
     * @param isCheckWorkflowWithModel
     */
    public void setEnableConcat(Boolean isCheckWorkflowWithModel) {
        this.enableConcat = isCheckWorkflowWithModel;
    }

    /**
     * モデル名と工程順名を結合したものが工程順名となるかどうか
     *
     * @return trueの場合、検索する際にモデル名と工程順名を合体させたものを工程順名として検索する必要がある
     */
    public boolean getEnableConcat() {
        return this.enableConcat;
    }

    /**
     * 設定に応じた工程順名を構築する<br>
     * <br>
     * モデル名と工程順を組み合わせる設定が有効な場合、先頭にモデル名を付加する<br>
     * 版数が存在する場合、末尾に版数を付加する<br>
     *
     * @return
     */
    public String createWorkflowName() {
        StringBuilder sb = new StringBuilder();

        sb.append(getEnableConcat() ? getModelName() : "");
        sb.append(getWorkflowName());
        sb.append(Objects.nonNull(getWorkflowRev()) ? " : " + getWorkflowRev() : "");

        return sb.toString();
    }
}
