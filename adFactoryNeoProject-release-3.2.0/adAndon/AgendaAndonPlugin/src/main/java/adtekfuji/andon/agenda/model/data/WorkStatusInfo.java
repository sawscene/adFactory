/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.model.data;

import java.util.Date;

/**
 * 工程進捗情報
 *
 * @author nar-nakamura
 */
public class WorkStatusInfo {

    private String kanbanName;
    private Integer orderNum;
    private String workName;
    private Integer workStatus;
    private Date planStartDatetime;
    private Date planCompDatetime;
    private Date startDatetime;
    private Date compDatetime;
    private String workflowName;
    private Integer workflowRev;

    /**
     * コンストラクタ
     */
    public WorkStatusInfo() {
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
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public Integer getOrderNum() {
        return this.orderNum;
    }

    /**
     * 表示順を設定する。
     *
     * @param orderNum 表示順
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 工程名を設定する。
     *
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * ステータスを取得する。
     *
     * @return ステータス
     */
    public Integer getWorkStatus() {
        return this.workStatus;
    }

    /**
     * ステータスを設定する。
     *
     * @param workStatus ステータス
     */
    public void setWorkStatus(Integer workStatus) {
        this.workStatus = workStatus;
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
                .append(", orderNum=").append(this.orderNum)
                .append(", workName=").append(this.workName)
                .append(", workStatus=").append(this.workStatus)
                .append(", planStartDatetime=").append(this.planStartDatetime)
                .append(", planCompDatetime=").append(this.planCompDatetime)
                .append(", startDatetime=").append(this.startDatetime)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", workflowName=").append(this.workflowName)
                .append(", workflowRev=").append(this.workflowRev)
                .append("}")
                .toString();
    }
}
