/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.entity;

/**
 * カンバン インポート用データ
 *
 * @author nar-nakamura
 */
public class ImportKanbanEntity {

    private String kanban_hierarchy_name;
    private String kanban_name;
    private String workflow_name;

    /**
     * カンバン インポート用データ
     */
    public ImportKanbanEntity() {

    }

    /**
     * カンバン インポート用データ
     *
     * @param kanban_hierarchy_name カンバン階層名
     * @param kanban_name カンバン名
     * @param workflow_name 工程順名
     */
    public ImportKanbanEntity(String kanban_hierarchy_name, String kanban_name, String workflow_name) {
        this.kanban_hierarchy_name = kanban_hierarchy_name;
        this.kanban_name = kanban_name;
        this.workflow_name = workflow_name;
    }

    /**
     * カンバン階層名を取得する。
     *
     * @return
     */
    public String getKanbanHierarchyName() {
        return this.kanban_hierarchy_name;
    }

    /**
     * カンバン階層名を設定する。
     *
     * @param value
     */
    public void setKanbanHierarchyName(String value) {
        this.kanban_hierarchy_name = value;
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
     * 工程順名を取得する。
     *
     * @return
     */
    public String getWorkflowName() {
        return this.workflow_name;
    }

    /**
     * 工程順名を設定する。
     *
     * @param value
     */
    public void setWorkflowName(String value) {
        this.workflow_name = value;
    }
    
    @Override
    public String toString() {
        return "ImportKanbanEntity{" +
                "kanban_hierarchy_name=" + this.kanban_hierarchy_name +
                ", kanban_name=" + this.kanban_name +
                ", workflow_name=" + this.workflow_name +
                "}";
    }
}
