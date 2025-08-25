/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.entity;

/**
 * 工程カンバン インポート用データ
 *
 * @author nar-nakamura
 */
public class ImportWorkKanbanEntity {

    private String kanban_name;
    private String work_num;
    private String skip_flag;
    private String start_datetime;
    private String comp_datetime;
    private String organizations;
    private String equipments;

    /**
     * 工程カンバン インポート用データ
     */
    public ImportWorkKanbanEntity() {

    }

    /**
     * 工程カンバン インポート用データ
     *
     * @param kanban_name カンバン名
     * @param work_num 工程の番号
     * @param skip_flag スキップフラグ
     * @param start_datetime 開始予定日時
     * @param comp_datetime 完了予定日時
     * @param organizations 組織識別名
     * @param equipments 設備識別名
     */
    public ImportWorkKanbanEntity(String kanban_name, String work_num, String skip_flag, String start_datetime, String comp_datetime, String organizations, String equipments) {
        this.kanban_name = kanban_name;
        this.work_num = work_num;
        this.skip_flag = skip_flag;
        this.start_datetime = start_datetime;
        this.comp_datetime = comp_datetime;
        this.organizations = organizations;
        this.equipments = equipments;
    }

    /**
     * カンバン名を取得する。
     *
     * @return
     */
    public String getKanbanName() {
        return this.kanban_name;
    }

    /**
     * カンバン名を設定する。
     *
     * @param value
     */
    public void setKanbanName(String value) {
        this.kanban_name = value;
    }

    /**
     * 工程の番号を取得する。
     *
     * @return
     */
    public String getWorkNum() {
        return this.work_num;
    }

    /**
     * 工程の番号を設定する。
     *
     * @param value
     */
    public void setWorkNum(String value) {
        this.work_num = value;
    }

    /**
     * スキップフラグを取得する。
     *
     * @return
     */
    public String getSkipFlag() {
        return this.skip_flag;
    }

    /**
     * スキップフラグを設定する。
     *
     * @param value
     */
    public void setSkipFlag(String value) {
        this.skip_flag = value;
    }

    /**
     * 開始予定日時を取得する。
     *
     * @return
     */
    public String getStartDatetime() {
        return this.start_datetime;
    }

    /**
     * 開始予定日時を設定する。
     *
     * @param value
     */
    public void setStartDatetime(String value) {
        this.start_datetime = value;
    }

    /**
     * 完了予定日時を取得する。
     *
     * @return
     */
    public String getCompDatetime() {
        return this.comp_datetime;
    }

    /**
     * 完了予定日時を設定する。
     *
     * @param value
     */
    public void setCompDatetime(String value) {
        this.comp_datetime = value;
    }

    /**
     * 組織識別名を取得する。
     *
     * @return
     */
    public String getOrganizations() {
        return this.organizations;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param value
     */
    public void setOrganizations(String value) {
        this.organizations = value;
    }

    /**
     * 設備識別名を取得する。
     *
     * @return
     */
    public String getEquipments() {
        return this.equipments;
    }

    /**
     * 設備識別名を設定する。
     *
     * @param value
     */
    public void setEquipments(String value) {
        this.equipments = value;
    }
    
    @Override
    public String toString() {
        return "ImportWorkKanbanEntity{" +
                "kanban_name=" + this.kanban_name +
                ", work_num=" + this.work_num +
                ", skip_flag=" + this.skip_flag +
                ", start_datetime=" + this.start_datetime +
                ", comp_datetime=" + this.comp_datetime +
                ", organizations=" + this.organizations +
                ", equipments=" + this.equipments +
                "}";
    }
}
