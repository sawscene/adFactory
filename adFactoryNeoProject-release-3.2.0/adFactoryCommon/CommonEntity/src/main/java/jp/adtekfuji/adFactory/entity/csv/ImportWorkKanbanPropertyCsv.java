/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.csv;

/**
 * 工程カンバンプロパティ インポート用データ
 *
 * @author nar-nakamura
 */
public class ImportWorkKanbanPropertyCsv {

    private String kanbanName;// カンバン名
    private String workName;// 工程名
    private String workNum;// 工程の番号
    private String workKanbanPropertyName;// プロパティ名
    private String workKanbanPropertyType;// プロパティ型
    private String workKanbanPropertyValue;// プロパティ値

    /**
     * 工程カンバンプロパティ インポート用データ
     */
    public ImportWorkKanbanPropertyCsv() {
    }

    /**
     * 工程カンバンプロパティ インポート用データ
     *
     * @param kanbanName カンバン名
     * @param workNum 工程の番号
     * @param workKanbanPropertyName プロパティ名
     * @param workKanbanPropertyType プロパティ型
     * @param workKanbanPropertyValue プロパティ値
     */
    public ImportWorkKanbanPropertyCsv(String kanbanName, String workNum, String workKanbanPropertyName, String workKanbanPropertyType, String workKanbanPropertyValue) {
        this.kanbanName = kanbanName;
        this.workNum = workNum;
        this.workKanbanPropertyName = workKanbanPropertyName;
        this.workKanbanPropertyType = workKanbanPropertyType;
        this.workKanbanPropertyValue = workKanbanPropertyValue;
    }
    
    /**
     * 工程カンバンプロパティ インポート用データ
     *
     * @param kanbanName カンバン名
     * @param workName 工程名
     * @param workNum 工程の番号
     * @param workKanbanPropertyName プロパティ名
     * @param workKanbanPropertyType プロパティ型
     * @param workKanbanPropertyValue プロパティ値
     */
    public ImportWorkKanbanPropertyCsv(String kanbanName, String workName, String workNum, String workKanbanPropertyName, String workKanbanPropertyType, String workKanbanPropertyValue) {
        this.kanbanName = kanbanName;
        this.workName = workName;
        this.workNum = workNum;
        this.workKanbanPropertyName = workKanbanPropertyName;
        this.workKanbanPropertyType = workKanbanPropertyType;
        this.workKanbanPropertyValue = workKanbanPropertyValue;
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
     * プロパティ名を取得する。
     *
     * @return プロパティ名
     */
    public String getWkKanbanPropName() {
        return this.workKanbanPropertyName;
    }

    /**
     * プロパティ名を設定する。
     *
     * @param workKanbanPropertyName プロパティ名
     */
    public void setWkKanbanPropName(String workKanbanPropertyName) {
        this.workKanbanPropertyName = workKanbanPropertyName;
    }

    /**
     * プロパティ型を取得する。
     *
     * @return プロパティ型
     */
    public String getWkKanbanPropType() {
        return this.workKanbanPropertyType;
    }

    /**
     * プロパティ型を設定する。
     *
     * @param workKanbanPropertyType プロパティ型
     */
    public void setWkKanbanPropType(String workKanbanPropertyType) {
        this.workKanbanPropertyType = workKanbanPropertyType;
    }

    /**
     * プロパティ値を取得する。
     *
     * @return プロパティ値
     */
    public String getWkKanbanPropValue() {
        return this.workKanbanPropertyValue;
    }

    /**
     * プロパティ値を設定する。
     *
     * @param workKanbanPropertyValue プロパティ値
     */
    public void setWkKanbanPropValue(String workKanbanPropertyValue) {
        this.workKanbanPropertyValue = workKanbanPropertyValue;
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

    @Override
    public String toString() {
        return "ImportWorkKanbanPropertyEntity{"
                + "kanbanName=" + this.kanbanName
                + ", workNum=" + this.workNum
                + ", workName=" + this.workName
                + ", workKanbanPropertyName=" + this.workKanbanPropertyName
                + ", workKanbanPropertyType=" + this.workKanbanPropertyType
                + ", workKanbanPropertyValue=" + this.workKanbanPropertyValue
                + "}";
    }
}
