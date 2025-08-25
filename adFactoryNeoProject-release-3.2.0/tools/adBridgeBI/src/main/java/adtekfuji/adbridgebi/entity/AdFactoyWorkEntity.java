/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.entity;

import java.util.Date;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * adFactoryの工程情報
 *
 * @author nar-nakamura
 */
public class AdFactoyWorkEntity {

    private Long kanbanId;          // カンバンID
    private String kanbanName;      // カンバン名
    private KanbanStatusEnum kanbanStatus;// カンバンステータス
    private Date kanbanCompDatetime;// カンバンの完了予定日時
    private String workName;        // 工程名
    private Long workKanbanOrder;   // 工程表示順
    private KanbanStatusEnum workStatus;// 工程ステータス
    private Date startDatetime;     // 開始予定日時
    private Date compDatetime;      // 完了予定日時
    private Date actualStartDatetime;// 開始日時
    private Date actualCompDatetime;// 完了日時
    private String modelName;       // モデル名
    private Date deadLine;          // カンバンプロパティ(完了予定日)
    private Integer reserveDays;    // カンバンプロパティ(予備日数)
    private Integer groupNo;        // カンバンプロパティ(グループNo)
    private String projectNo;       // カンバンプロパティ(プロジェクトNo)
    private String userName;        // カンバンプロパティ(ユーザー名)

    /**
     * コンストラクタ
     */
    public AdFactoyWorkEntity() {
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getKanbanId() {
        return this.kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
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
     * カンバンステータスを取得する。
     *
     * @return カンバンステータス
     */
    public KanbanStatusEnum getKanbanStatus() {
        return this.kanbanStatus;
    }

    /**
     * カンバンステータスを設定する。
     *
     * @param kanbanStatus カンバンステータス
     */
    public void setKanbanStatus(KanbanStatusEnum kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
    }

    /**
     * カンバンの完了予定日時を取得する。
     * 
     * @return 
     */
    public Date getKanbanCompDatetime() {
        return kanbanCompDatetime;
    }

    /**
     * カンバンの完了予定日時を設定する。
     * 
     * @param kanbanCompDatetime 
     */
    public void setKanbanCompDatetime(Date kanbanCompDatetime) {
        this.kanbanCompDatetime = kanbanCompDatetime;
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
     * 工程表示順を取得する。
     *
     * @return 工程表示順
     */
    public Long getWorkKanbanOrder() {
        return this.workKanbanOrder;
    }

    /**
     * 工程表示順を設定する。
     *
     * @param workKanbanOrder 工程表示順
     */
    public void setWorkKanbanOrder(Long workKanbanOrder) {
        this.workKanbanOrder = workKanbanOrder;
    }

    /**
     * 工程ステータスを取得する。
     *
     * @return 工程ステータス
     */
    public KanbanStatusEnum getWorkStatus() {
        return this.workStatus;
    }

    /**
     * 工程ステータスを設定する。
     *
     * @param workStatus 工程ステータス
     */
    public void setWorkStatus(KanbanStatusEnum workStatus) {
        this.workStatus = workStatus;
    }

    /**
     * 開始予定日時を取得する。
     *
     * @return 開始予定日時
     */
    public Date getStartDatetime() {
        return this.startDatetime;
    }

    /**
     * 開始予定日時を設定する。
     *
     * @param startDatetime 開始予定日時
     */
    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    /**
     * 完了予定日時を取得する。
     *
     * @return 完了予定日時
     */
    public Date getCompDatetime() {
        return this.compDatetime;
    }

    /**
     * 完了予定日時を設定する。
     *
     * @param compDatetime 完了予定日時
     */
    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    /**
     * 開始日時を取得する。
     *
     * @return 開始日時
     */
    public Date getActualStartDatetime() {
        return this.actualStartDatetime;
    }

    /**
     * 開始日時を設定する。
     *
     * @param actualStartDatetime 開始日時
     */
    public void setActualStartDatetime(Date actualStartDatetime) {
        this.actualStartDatetime = actualStartDatetime;
    }

    /**
     * 完了日時を取得する。
     *
     * @return 完了日時
     */
    public Date getActualCompDatetime() {
        return this.actualCompDatetime;
    }

    /**
     * 完了日時を設定する。
     *
     * @param actualCompDatetime 完了日時
     */
    public void setActualCompDatetime(Date actualCompDatetime) {
        this.actualCompDatetime = actualCompDatetime;
    }

    /**
     * モデル名を取得する。
     * 
     * @return 
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * モデル名を設定する。
     * 
     * @param modelName 
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * 完了予定日を取得する。
     *
     * @return 完了予定日
     */
    public Date getDeadLine() {
        return this.deadLine;
    }

    /**
     * 完了予定日を設定する。
     *
     * @param deadLine 完了予定日
     */
    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
    }

    /**
     * 予備日数を取得する。
     *
     * @return 予備日数
     */
    public Integer getReserveDays() {
        return this.reserveDays;
    }

    /**
     * 予備日数を設定する。
     *
     * @param reserveDays 予備日数
     */
    public void setReserveDays(Integer reserveDays) {
        this.reserveDays = reserveDays;
    }

    /**
     * グループNoを取得する。
     * 
     * @return 
     */
    public Integer getGroupNo() {
        return groupNo;
    }

    /**
     * グループNoを設定する。
     * 
     * @param groupNo 
     */
    public void setGroupNo(Integer groupNo) {
        this.groupNo = groupNo;
    }

    /**
     * プロジェクトNoを取得する。
     * 
     * @return 
     */
    public String getProjectNo() {
        return projectNo;
    }

    /**
     * プロジェクトNoを設定する。
     * 
     * @param projectNo 
     */
    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    /**
     * ユーザー名を取得する。
     * 
     * @return 
     */
    public String getUserName() {
        return userName;
    }

    /**
     * ユーザー名を設定する。
     * 
     * @param userName 
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkKanbanStatusEntity{")
                .append("kanbanId=").append(this.kanbanId)
                .append(", ")
                .append("kanbanName=").append(this.kanbanName)
                .append(", ")
                .append("kanbanStatus=").append(this.kanbanStatus)
                .append(", ")
                .append("workName=").append(this.workName)
                .append(", ")
                .append("workKanbanOrder=").append(this.workKanbanOrder)
                .append(", ")
                .append("workStatus=").append(this.workStatus)
                .append(", ")
                .append("startDatetime=").append(this.startDatetime)
                .append(", ")
                .append("compDatetime=").append(this.compDatetime)
                .append(", ")
                .append("actualStartDatetime=").append(this.actualStartDatetime)
                .append(", ")
                .append("actualCompDatetime=").append(this.actualCompDatetime)
                .append(", ")
                .append("modelName=").append(this.modelName)
                .append(", ")
                .append("deadLine=").append(this.deadLine)
                .append(", ")
                .append("reserveDays=").append(this.reserveDays)
                .append(", ")
                .append("groupNo=").append(this.groupNo)
                .append(", ")
                .append("projectNo=").append(this.projectNo)
                .append(", ")
                .append("userName=").append(this.userName)
                .append("}")
                .toString();
    }
}
