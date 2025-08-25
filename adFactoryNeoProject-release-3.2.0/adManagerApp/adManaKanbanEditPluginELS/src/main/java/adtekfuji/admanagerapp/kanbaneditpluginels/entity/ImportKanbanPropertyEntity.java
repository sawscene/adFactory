/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.entity;

/**
 * カンバンプロパティ インポート用データ
 * @author nar-nakamura
 */
public class ImportKanbanPropertyEntity {

    private String kanban_name;
    private String kanban_property_name;
    private String kanban_property_type;
    private String kanban_property_value;

    /**
     * カンバンプロパティ インポート用データ
     *
     */
    public ImportKanbanPropertyEntity() {

    }

    /**
     * カンバンプロパティ インポート用データ
     *
     * @param kanban_name カンバン名
     * @param kanban_property_name プロパティ名
     * @param kanban_property_type プロパティ型
     * @param kanban_property_value プロパティ値
     */
    public ImportKanbanPropertyEntity(String kanban_name, String kanban_property_name, String kanban_property_type, String kanban_property_value) {
        this.kanban_name = kanban_name;
        this.kanban_property_name = kanban_property_name;
        this.kanban_property_type = kanban_property_type;
        this.kanban_property_value = kanban_property_value;
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
     * プロパティ名を取得する。
     *
     * @return
     */
    public String getKanbanPropertyName() {
        return this.kanban_property_name;
    }

    /**
     * プロパティ名を設定する。
     *
     * @param value
     */
    public void setKanbanPropertyName(String value) {
        this.kanban_property_name = value;
    }

    /**
     * プロパティ型を取得する。
     *
     * @return
     */
    public String getKanbanPropertyType() {
        return this.kanban_property_type;
    }

    /**
     * プロパティ型を設定する。
     *
     * @param value
     */
    public void setKanbanPropertyType(String value) {
        this.kanban_property_type = value;
    }

    /**
     * プロパティ値を取得する。
     *
     * @return
     */
    public String getKanbanPropertyValue() {
        return this.kanban_property_value;
    }

    /**
     * プロパティ値を設定する。
     *
     * @param value
     */
    public void setKanbanPropertyValue(String value) {
        this.kanban_property_value = value;
    }
    
    @Override
    public String toString() {
        return "ImportKanbanPropertyEntity{" +
                "kanban_name=" + this.kanban_name +
                ", kanban_property_name=" + this.kanban_property_name +
                ", kanban_property_type=" + this.kanban_property_type +
                ", kanban_property_value=" + this.kanban_property_value +
                "}";
    }
}
