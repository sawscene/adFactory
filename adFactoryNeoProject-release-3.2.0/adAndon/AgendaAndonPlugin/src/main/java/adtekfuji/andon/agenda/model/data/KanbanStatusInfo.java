/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.model.data;

import java.util.Date;

/**
 * カンバン進捗情報
 *
 * @author nar-nakamura
 */
public class KanbanStatusInfo {

    private String kanbanName;
    private String modelName;
    private String kanbanInfo1;
    private Integer kanbanStatus;
    private Date planStartDatetime;
    private Date planCompDatetime;
    private Date startDatetime;
    private Date compDatetime;
    private Integer delaySec;
    private String currentWork;
    private String workflowName;
    private Integer workflowRev;

    /**
     * コンストラクタ
     */
    public KanbanStatusInfo() {
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
     * 付加情報1を取得する。
     *
     * @return 付加情報1
     */
    public String getKanbanInfo1() {
        return this.kanbanInfo1;
    }

    /**
     * 付加情報1を設定する。
     *
     * @param kanbanInfo1 付加情報1
     */
    public void setKanbanInfo1(String kanbanInfo1) {
        this.kanbanInfo1 = kanbanInfo1;
    }

    /**
     * ステータスを取得する。
     *
     * @return ステータス
     */
    public Integer getKanbanStatus() {
        return this.kanbanStatus;
    }

    /**
     * ステータスを設定する。
     *
     * @param kanbanStatus ステータス
     */
    public void setKanbanStatus(Integer kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
    }

    /**
     * 計画開始時間を取得する。
     *
     * @return 計画開始時間
     */
    public Date getPlanStartDatetime() {
        return this.planStartDatetime;
    }

    /**
     * 計画開始時間を設定する。
     *
     * @param planStartDatetime 計画開始時間
     */
    public void setPlanStartDatetime(Date planStartDatetime) {
        this.planStartDatetime = planStartDatetime;
    }

    /**
     * 計画完了時間を取得する。
     *
     * @return 計画完了時間
     */
    public Date getPlanCompDatetime() {
        return this.planCompDatetime;
    }

    /**
     * 計画完了時間を設定する。
     *
     * @param planCompDatetime 計画完了時間
     */
    public void setPlanCompDatetime(Date planCompDatetime) {
        this.planCompDatetime = planCompDatetime;
    }

    /**
     * 実績開始時間を取得する。
     *
     * @return 実績開始時間
     */
    public Date getStartDatetime() {
        return this.startDatetime;
    }

    /**
     * 実績開始時間を設定する。
     *
     * @param startDatetime 実績開始時間
     */
    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    /**
     * 実績完了時間を取得する。
     *
     * @return 実績完了時間
     */
    public Date getCompDatetime() {
        return this.compDatetime;
    }

    /**
     * 実績完了時間を設定する。
     *
     * @param compDatetime 実績完了時間
     */
    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    /**
     * 遅れ時間(秒)を取得する。
     *
     * @return 遅れ時間(秒)
     */
    public Integer getDelaySec() {
        return this.delaySec;
    }

    /**
     * 遅れ時間(秒)を設定する。
     *
     * @param delaySec 遅れ時間(秒)
     */
    public void setDelaySec(Integer delaySec) {
        this.delaySec = delaySec;
    }

    /**
     * 作業中の工程を取得する。
     *
     * @return 作業中の工程
     */
    public String getCurrentWork() {
        return this.currentWork;
    }

    /**
     * 作業中の工程を設定する。
     *
     * @param currentWork 作業中の工程
     */
    public void setCurrentWork(String currentWork) {
        this.currentWork = currentWork;
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
     * 工程順の版数を取得する。
     *
     * @return 工程順の版数
     */
    public Integer getWorkflowRev() {
        return this.workflowRev;
    }

    /**
     * 工程順の版数を設定する。
     *
     * @param workflowRev 工程順の版数
     */
    public void setWorkflowRev(Integer workflowRev) {
        this.workflowRev = workflowRev;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanStatusInfo{")
                .append("kanbanName=").append(this.kanbanName)
                .append(", modelName=").append(this.modelName)
                .append(", kanbanInfo1=").append(this.kanbanInfo1)
                .append(", kanbanStatus=").append(this.kanbanStatus)
                .append(", planStartDatetime=").append(this.planStartDatetime)
                .append(", planCompDatetime=").append(this.planCompDatetime)
                .append(", startDatetime=").append(this.startDatetime)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", delaySec=").append(this.delaySec)
                .append(", currentWork=").append(this.currentWork)
                .append(", workflowName=").append(this.workflowName)
                .append(", workflowRev=").append(this.workflowRev)
                .append("}")
                .toString();
    }
}
