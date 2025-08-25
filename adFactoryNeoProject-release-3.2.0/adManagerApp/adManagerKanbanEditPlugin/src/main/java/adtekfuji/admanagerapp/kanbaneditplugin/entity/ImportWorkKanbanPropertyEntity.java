/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.entity;

/**
 * 工程カンバンプロパティ インポート用データ
 * @author s-maeda
 */
public class ImportWorkKanbanPropertyEntity {

    private String kanban_name;
    private String work_num;
    private String work_kanban_property_name;
    private String work_kanban_property_type;
    private String work_kanban_property_value;

    /**
     * 工程カンバンプロパティ インポート用データ
     *
     */
    public ImportWorkKanbanPropertyEntity() {

    }

    /**
     * 工程カンバンプロパティ インポート用データ
     *
     * @param kanban_name カンバン名
     * @param work_num 工程の番号
     * @param work_kanban_property_name プロパティ名
     * @param work_kanban_property_type プロパティ型
     * @param work_kanban_property_value プロパティ値
     */
    public ImportWorkKanbanPropertyEntity(String kanban_name, String work_num, String work_kanban_property_name, String work_kanban_property_type, String work_kanban_property_value) {
        this.kanban_name = kanban_name;
        this.work_num = work_num;
        this.work_kanban_property_name = work_kanban_property_name;
        this.work_kanban_property_type = work_kanban_property_type;
        this.work_kanban_property_value = work_kanban_property_value;
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
     * プロパティ名を取得する。
     *
     * @return
     */
    public String getWkKanbanPropName() {
        return this.work_kanban_property_name;
    }

    /**
     * プロパティ名を設定する。
     *
     * @param value
     */
    public void setWkKanbanPropName(String value) {
        this.work_kanban_property_name = value;
    }

    /**
     * プロパティ型を取得する。
     *
     * @return
     */
    public String getWkKanbanPropType() {
        return this.work_kanban_property_type;
    }

    /**
     * プロパティ型を設定する。
     *
     * @param value
     */
    public void setWkKanbanPropType(String value) {
        this.work_kanban_property_type = value;
    }

    /**
     * プロパティ値を取得する。
     *
     * @return
     */
    public String getWkKanbanPropValue() {
        return this.work_kanban_property_value;
    }

    /**
     * プロパティ値を設定する。
     *
     * @param value
     */
    public void setWkKanbanPropValue(String value) {
        this.work_kanban_property_value = value;
    }
    
    @Override
    public String toString() {
        return "ImportWorkKanbanPropertyEntity{" +
                "kanban_name=" + this.kanban_name +
                ", work_num=" + this.work_num +
                ", work_kanban_property_name=" + this.work_kanban_property_name +
                ", work_kanban_property_type=" + this.work_kanban_property_type +
                ", work_kanban_property_value=" + this.work_kanban_property_value +
                "}";
    }
}
