/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.entity;

import java.util.Date;

/**
 * 個別進捗情報
 *
 * @author nar-nakamura
 */
public class WorkProgressEntity {

    private String progressNo;      // No
    private String progressType;    // 種別
    private String progressOrder;   // 順
    private String progressDate;    // 日付
    private String startTime;       // 開始時間
    private String compTime;        // 終了時間
    private String workName;        // 工程名
    private Long kanbanId;          // カンバンID
    private String workKanbanStatus;// 工程ステータス
    private Date startDatetime;     // 開始日時
    private Date compDatetime;      // 完了日時

    /**
     * コンストラクタ
     */
    public WorkProgressEntity() {
    }

    /**
     * Noを取得する。
     *
     * @return No
     */
    public String getProgressNo() {
        return this.progressNo;
    }

    /**
     * Noを設定する。
     *
     * @param progressNo No
     */
    public void setProgressNo(String progressNo) {
        this.progressNo = progressNo;
    }

    /**
     * 種別を取得する。
     *
     * @return 種別
     */
    public String getProgressType() {
        return this.progressType;
    }

    /**
     * 種別を設定する。
     *
     * @param progressType 種別
     */
    public void setProgressType(String progressType) {
        this.progressType = progressType;
    }

    /**
     * 順を取得する。
     *
     * @return 順
     */
    public String getProgressOrder() {
        return this.progressOrder;
    }

    /**
     * 順を設定する。
     *
     * @param progressOrder 順
     */
    public void setProgressOrder(String progressOrder) {
        this.progressOrder = progressOrder;
    }

    /**
     * 日付を取得する。
     *
     * @return 日付
     */
    public String getProgressDate() {
        return this.progressDate;
    }

    /**
     * 日付を設定する。
     *
     * @param progressDate 日付
     */
    public void setProgressDate(String progressDate) {
        this.progressDate = progressDate;
    }

    /**
     * 開始時間を取得する。
     *
     * @return 開始時間
     */
    public String getStartTime() {
        return this.startTime;
    }

    /**
     * 開始時間を設定する。
     *
     * @param startTime 開始時間
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * 終了時間を取得する。
     *
     * @return 終了時間
     */
    public String getCompTime() {
        return this.compTime;
    }

    /**
     * 終了時間を設定する。
     *
     * @param compTime 終了時間
     */
    public void setCompTime(String compTime) {
        this.compTime = compTime;
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
     * カンバンIDを取得する。
     * 
     * @return 
     */
    public Long getKanbanId() {
        return kanbanId;
    }

    /**
     * カンバンIDを設定する。
     * 
     * @param kanbanId 
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * 工程ステータスを取得する。
     * 
     * @return 
     */
    public String getWorkKanbanStatus() {
        return workKanbanStatus;
    }

    /**
     * 工程ステータスを設定する。
     * 
     * @param workKanbanStatus 
     */
    public void setWorkKanbanStatus(String workKanbanStatus) {
        this.workKanbanStatus = workKanbanStatus;
    }

    /**
     * 開始日時を取得する。
     * 
     * @return 
     */
    public Date getStartDatetime() {
        return startDatetime;
    }

    /**
     * 開始日時を設定する。
     * 
     * @param startDatetime 
     */
    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    /**
     * 完了日時を取得する。
     * 
     * @return 
     */
    public Date getCompDatetime() {
        return compDatetime;
    }

    /**
     * 完了日時を設定する。
     * 
     * @param compDatetime 
     */
    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkProgressEntity{")
                .append("progressNo=").append(this.progressNo)
                .append(", ")
                .append("progressType=").append(this.progressType)
                .append(", ")
                .append("progressOrder=").append(this.progressOrder)
                .append(", ")
                .append("progressDate=").append(this.progressDate)
                .append(", ")
                .append("startTime=").append(this.startTime)
                .append(", ")
                .append("compTime=").append(this.compTime)
                .append(", ")
                .append("workName=").append(this.workName)
                .append(", ")
                .append("workKanbanStatus=").append(this.workKanbanStatus)
                .append(", ")
                .append("startDatetime=").append(this.startDatetime)
                .append(", ")
                .append("compDatetime=").append(this.compDatetime)
                .append("}")
                .toString();
    }
}
