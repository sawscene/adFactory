/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.csv;

import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 工程カンバン インポート用データ
 *
 * @author nar-nakamura
 */
public class ImportWorkKanbanCsv {

    private String kanbanName;// カンバン名
    private String workNum;// 工程の番号
    private String skipFlag;// スキップフラグ
    private String startDatetime;// 開始予定日時
    private String compDatetime;// 完了予定日時
    private String organizations;// 組織識別名
    private String equipments;// 設備識別名
    private String workName;// 工程名
    private String tactTime;// タクトタイム
    private KanbanStatusEnum workStatus = KanbanStatusEnum.PLANNED; // ステータス

    /**
     * 工程カンバン インポート用データ
     */
    public ImportWorkKanbanCsv() {
    }

    /**
     * コンストラクタ
     *
     * @param kanbanName カンバン名
     * @param workNum 工程の番号
     * @param skipFlag スキップフラグ
     * @param startDatetime 開始予定日時
     * @param compDatetime 完了予定日時
     * @param organizations 組織識別名
     * @param equipments 設備識別名
     * @param workName 工程名
     * @param tactTime タクトタイム
     */
    public ImportWorkKanbanCsv(String kanbanName, String workNum, String skipFlag, String startDatetime, String compDatetime, String organizations, String equipments, String workName, String tactTime) {
        this.kanbanName = kanbanName;
        this.workNum = workNum;
        this.skipFlag = skipFlag;
        this.startDatetime = startDatetime;
        this.compDatetime = compDatetime;
        this.organizations = organizations;
        this.equipments = equipments;
        this.workName = workName;
        this.tactTime = tactTime;
        this.workStatus = KanbanStatusEnum.PLANNED;
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
     * 工程の番号を取得する。
     *
     * @return 工程の番号
     */
    public String getWorkNum() {
        return this.workNum;
    }

    /**
     * 工程の番号を設定する。
     *
     * @param workNum 工程の番号
     */
    public void setWorkNum(String workNum) {
        this.workNum = workNum;
    }

    /**
     * スキップフラグを取得する。
     *
     * @return スキップフラグ
     */
    public String getSkipFlag() {
        return this.skipFlag;
    }

    /**
     * スキップフラグを設定する。
     *
     * @param skipFlag スキップフラグ
     */
    public void setSkipFlag(String skipFlag) {
        this.skipFlag = skipFlag;
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
     * 完了予定日時を取得する。
     *
     * @return 完了予定日時
     */
    public String getCompDatetime() {
        return this.compDatetime;
    }

    /**
     * 完了予定日時を設定する。
     *
     * @param compDatetime 完了予定日時
     */
    public void setCompDatetime(String compDatetime) {
        this.compDatetime = compDatetime;
    }

    /**
     * 組織識別名を取得する。
     *
     * @return 組織識別名
     */
    public String getOrganizations() {
        return this.organizations;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param organizations 組織識別名
     */
    public void setOrganizations(String organizations) {
        this.organizations = organizations;
    }

    /**
     * 設備識別名を取得する。
     *
     * @return 設備識別名
     */
    public String getEquipments() {
        return this.equipments;
    }

    /**
     * 設備識別名を設定する。
     *
     * @param equipments 設備識別名
     */
    public void setEquipments(String equipments) {
        this.equipments = equipments;
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
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public String getTactTime() {
        return this.tactTime;
    }

    /**
     * タクトタイムを設定する。
     *
     * @param tactTime タクトタイム
     */
    public void setTactTime(String tactTime) {
        this.tactTime = tactTime;
    }


    /**
     * 工程カンバンステータス
     * @return ステータス
     */
    public KanbanStatusEnum getWorkStatus() {
        return workStatus;
    }


    /**
     * 工程カンバンステータス
     * @param workStatus ステータス
     */
    public void setWorkStatus(KanbanStatusEnum workStatus) {
        this.workStatus = workStatus;
    }

    @Override
    public String toString() {
        return new StringBuilder("ImportWorkKanbanEntity{")
                .append("kanbanName=").append(this.kanbanName)
                .append(", workNum=").append(this.workNum)
                .append(", skipFlag=").append(this.skipFlag)
                .append(", startDatetime=").append(this.startDatetime)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", organizations=").append(this.organizations)
                .append(", equipments=").append(this.equipments)
                .append(", workName=").append(this.workName)
                .append(", tactTime=").append(this.tactTime)
                .append(", status=").append(this.workStatus)
                .append("}")
                .toString();
    }
}
