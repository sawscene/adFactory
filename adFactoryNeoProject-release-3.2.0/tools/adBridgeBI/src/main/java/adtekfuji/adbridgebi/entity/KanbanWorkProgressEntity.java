/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.adbridgebi.entity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * カンバン進捗情報 (工程基準)
 *
 * @author nar-nakamura
 */
public class KanbanWorkProgressEntity {

    public static final int MAX_WORK = 20;

    private String progressNo;// No
    private String kanbanName;// カンバン名
    
    // 工程_名称
    private final List<String> workNames = new LinkedList(Collections.nCopies(MAX_WORK, ""));
    // 工程_計画開始日付
    private final List<String> startDates = new LinkedList(Collections.nCopies(MAX_WORK, ""));
    // 工程_ステータス
    private final List<String> statuses = new LinkedList(Collections.nCopies(MAX_WORK, ""));
    // 工程_本日フラグ
    private final List<String> todayFlgs = new LinkedList(Collections.nCopies(MAX_WORK, ""));

    private Integer groupNo;    // グループNo
    private Integer seqNo;      // 連番
    private Long kanbanId;      // カンバンID
    private String kanbanStatus;// 中日程ステータス
    private String modelName;       // モデル名
    private String projectNo;       // カンバンプロパティ(プロジェクトNo)
    private String userName;        // カンバンプロパティ(ユーザー名)

    /**
     * コンストラクタ
     */
    public KanbanWorkProgressEntity() {
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
     * 工程_名称を取得する。
     *
     * @param index インデックス
     * @return 工程_名称
     */
    public String getWorkName(int index) {
        return this.workNames.get(index);
    }

    /**
     * 工程_名称を設定する。
     *
     * @param index インデックス
     * @param workName 工程_名称
     */
    public void setWorkName(int index, String workName) {
        this.workNames.set(index, workName);
    }

    /**
     * 工程_計画開始日付を取得する。
     *
     * @param index インデックス
     * @return 工程_計画開始日付
     */
    public String getStartDate(int index) {
        return this.startDates.get(index);
    }

    /**
     * 工程_計画開始日付を設定する。
     *
     * @param index インデックス
     * @param startDate 工程_計画開始日付
     */
    public void setStartDate(int index, String startDate) {
        this.startDates.set(index, startDate);
    }

    /**
     * 工程_ステータスを取得する。
     *
     * @param index インデックス
     * @return 工程_ステータス
     */
    public String getStatus(int index) {
        return this.statuses.get(index);
    }

    /**
     * 工程_ステータスを設定する。
     *
     * @param index インデックス
     * @param status 工程_ステータス
     */
    public void setStatus(int index, String status) {
        this.statuses.set(index, status);
    }

    /**
     * 工程_本日フラグを取得する。
     *
     * @param index インデックス
     * @return 工程1_本日フラグ
     */
    public String getTodayFlg(int index) {
        return this.todayFlgs.get(index);
    }

    /**
     * 工程_本日フラグを設定する。
     *
     * @param index インデックス
     * @param todayFlg 工程_本日フラグ
     */
    public void setTodayFlg(int index, String todayFlg) {
        this.todayFlgs.set(index, todayFlg);
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
     * 連番を取得する。
     * 
     * @return 
     */
    public Integer getSeqNo() {
        return seqNo;
    }

    /**
     * 連番を設定する。
     * 
     * @param seqNo 
     */
    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
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
     * 中日程ステータスを取得する。
     * 
     * @return 
     */
    public String getKanbanStatus() {
        return kanbanStatus;
    }

    /**
     * 中日程ステータスを設定する。
     * 
     * @param kanbanStatus 
     */
    public void setKanbanStatus(String kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
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
        return new StringBuilder("KanbanWorkProgressEntity{")
                .append("progressNo=").append(this.progressNo)
                .append(", ")
                .append("kanbanName=").append(this.kanbanName)
                .append(", ")
                .append("workNames=").append(this.workNames)
                .append(", ")
                .append("startDates=").append(this.startDates)
                .append(", ")
                .append("statuses=").append(this.statuses)
                .append(", ")
                .append("todayFlgs=").append(this.todayFlgs)
                .append("}")
                .toString();
    }
}
