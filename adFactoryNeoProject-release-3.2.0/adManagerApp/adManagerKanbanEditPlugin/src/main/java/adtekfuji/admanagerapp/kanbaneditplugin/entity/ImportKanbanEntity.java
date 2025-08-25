/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.entity;

import java.util.Date;
import java.util.Objects;

/**
 * カンバン インポート用データ
 *
 * @author nar-nakamura
 */
public class ImportKanbanEntity {

    private String kanban_hierarchy_name;
    private String kanban_name;
    private String workflow_name;
    private Integer workflow_rev;
    private String model_name;
    private Date start_datetime;

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

    /**
     * 工程順版数を取得する
     *
     * @return
     */
    public Integer getWorkflowRev() {
        return workflow_rev;
    }

    /**
     * 工程順版数を設定する
     *
     * @param workflow_rev
     */
    public void setWorkflowRev(Integer workflow_rev) {
        this.workflow_rev = workflow_rev;
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        return model_name;
    }

    /**
     * モデル名を設定する。
     *
     * @param model_name モデル名
     */
    public void setModelName(String model_name) {
        this.model_name = model_name;
    }

    /**
     * 開始予定日時を取得する。
     *
     * @return
     */
    public Date getStartDatetime() {
        return start_datetime;
    }

    /**
     * 開始予定日時を設定する。
     *
     * @param start_datetime
     */
    public void setStartDatetime(Date start_datetime) {
        this.start_datetime = start_datetime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.kanban_hierarchy_name);
        hash = 41 * hash + Objects.hashCode(this.kanban_name);
        hash = 41 * hash + Objects.hashCode(this.workflow_name);
        hash = 41 * hash + Objects.hashCode(this.workflow_rev);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImportKanbanEntity other = (ImportKanbanEntity) obj;
        if (!Objects.equals(this.kanban_hierarchy_name, other.kanban_hierarchy_name)) {
            return false;
        }
        if (!Objects.equals(this.kanban_name, other.kanban_name)) {
            return false;
        }
        if (!Objects.equals(this.workflow_name, other.workflow_name)) {
            return false;
        }
        return Objects.equals(this.workflow_rev, other.workflow_rev);
    }

    @Override
    public String toString() {
        return "ImportKanbanEntity{" + "kanban_hierarchy_name=" + kanban_hierarchy_name + ", kanban_name=" + kanban_name + ", workflow_name=" + workflow_name +
                ", workflow_rev=" + workflow_rev + ", model_name=" + model_name + ", start_datetime=" + start_datetime + '}';
    }
}
