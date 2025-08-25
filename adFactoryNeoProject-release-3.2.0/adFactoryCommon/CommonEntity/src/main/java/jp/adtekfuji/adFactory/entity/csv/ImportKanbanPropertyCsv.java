/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.csv;

/**
 * カンバンプロパティ インポート用データ
 *
 * @author nar-nakamura
 */
public class ImportKanbanPropertyCsv {

    private String kanbanName;// カンバン名
    private String workflowName;// 工程順名
    private String kanbanPropertyName;// プロパティ名
    private String kanbanPropertyType;// プロパティ型
    private String kanbanPropertyValue;// プロパティ値

    /**
     * カンバンプロパティ インポート用データ
     *
     */
    public ImportKanbanPropertyCsv() {
    }

    /**
     * カンバンプロパティ インポート用データ
     *
     * @param kanbanName カンバン名
     * @param kanbanPropertyName プロパティ名
     * @param kanbanPropertyType プロパティ型
     * @param kanbanPropertyValue プロパティ値
     */
    public ImportKanbanPropertyCsv(String kanbanName, String kanbanPropertyName, String kanbanPropertyType, String kanbanPropertyValue) {
        this.kanbanName = kanbanName;
        this.kanbanPropertyName = kanbanPropertyName;
        this.kanbanPropertyType = kanbanPropertyType;
        this.kanbanPropertyValue = kanbanPropertyValue;
    }

    /**
     * カンバンプロパティ インポート用データ
     *
     * @param kanbanName カンバン名
     * @param workflowName 工程順名
     * @param kanbanPropertyName プロパティ名
     * @param kanbanPropertyType プロパティ型
     * @param kanbanPropertyValue プロパティ値
     */
    public ImportKanbanPropertyCsv(String kanbanName, String workflowName, String kanbanPropertyName, String kanbanPropertyType, String kanbanPropertyValue) {
        this.kanbanName = kanbanName;
        this.workflowName = workflowName;
        this.kanbanPropertyName = kanbanPropertyName;
        this.kanbanPropertyType = kanbanPropertyType;
        this.kanbanPropertyValue = kanbanPropertyValue;
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
     * プロパティ名を取得する。
     *
     * @return プロパティ名
     */
    public String getKanbanPropertyName() {
        return this.kanbanPropertyName;
    }

    /**
     * プロパティ名を設定する。
     *
     * @param kanbanPropertyName プロパティ名
     */
    public void setKanbanPropertyName(String kanbanPropertyName) {
        this.kanbanPropertyName = kanbanPropertyName;
    }

    /**
     * プロパティ型を取得する。
     *
     * @return プロパティ型
     */
    public String getKanbanPropertyType() {
        return this.kanbanPropertyType;
    }

    /**
     * プロパティ型を設定する。
     *
     * @param kanbanPropertyType プロパティ型
     */
    public void setKanbanPropertyType(String kanbanPropertyType) {
        this.kanbanPropertyType = kanbanPropertyType;
    }

    /**
     * プロパティ値を取得する。
     *
     * @return プロパティ値
     */
    public String getKanbanPropertyValue() {
        return this.kanbanPropertyValue;
    }

    /**
     * プロパティ値を設定する。
     *
     * @param kanbanPropertyValue プロパティ値
     */
    public void setKanbanPropertyValue(String kanbanPropertyValue) {
        this.kanbanPropertyValue = kanbanPropertyValue;
    }

    @Override
    public String toString() {
        return "ImportKanbanPropertyEntity{"
                + "kanbanName=" + this.kanbanName
                + ", workflowName=" + this.workflowName
                + ", kanbanPropertyName=" + this.kanbanPropertyName
                + ", kanbanPropertyType=" + this.kanbanPropertyType
                + ", kanbanPropertyValue=" + this.kanbanPropertyValue
                + "}";
    }
}
