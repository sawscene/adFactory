/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;

/**
 * 工程の使用状況リストデータ
 * 
 * @author s-heya
 */
public class ApplyWorkflowRow {
    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final WorkflowInfoEntity workflow;
    private final StringProperty  workflowName;
    private final StringProperty  workRev;
    private final StringProperty  existKanban;
    private final StringProperty  afterRev;
    private final BooleanProperty disabled;

    /**
     * コンストラクタ
     * 
     * @param workflow 工程順情報
     * @param workRev 対象工程の版数
     * @param latestRev
     * @param existKanban カンバンの有無
     */
    public ApplyWorkflowRow(WorkflowInfoEntity workflow, Integer workRev, Integer latestRev, Boolean existKanban) {
        boolean latest = Objects.equals(workRev, latestRev);
        this.workflow = workflow;
        // 既に工程が最新版であるか、工程順が最新版ではない場合は無効
        this.disabled = new SimpleBooleanProperty(latest || !Objects.equals(this.workflow.getWorkflowRev(), this.workflow.getLatestRev()));

        this.workflowName = new SimpleStringProperty(this.workflow.getWorkflowName() + ":" + this.workflow.getWorkflowRev());
        this.workRev = new SimpleStringProperty(String.valueOf(workRev) + (latest ? " (" + LocaleUtils.getString("latest") + ")" : ""));
        this.existKanban = new SimpleStringProperty(existKanban ? LocaleUtils.getString("exist") : LocaleUtils.getString("notExist"));
        this.afterRev = new SimpleStringProperty(this.disabled.get() ? LocaleUtils.getString("notApplicable") 
                : String.valueOf(existKanban ? this.workflow.getWorkflowRev() + 1 : this.workflow.getWorkflowRev()));
    }

    /**
     * 工程順情報を取得する。
     * 
     * @return 工程順情報
     */
    public WorkflowInfoEntity getWorkflow() {
        return this.workflow;
    }
    
    /**
     * 工程順名プロパティを取得する。
     * 
     * @return 工程順名 
     */
    public StringProperty workflowNameProperty() {
        return workflowName;
    }

    /**
     * 対象工程の版数(プロパティ)を取得する。
     * 
     * @return 対象工程の版数
     */
    public StringProperty workRevProperty() {
        return workRev;
    }

    /**
     * カンバンの有無(プロパティ)を取得する。
     * 
     * @return カンバンの有無
     */
    public StringProperty existKanbanProperty() {
        return existKanban;
    }

    /**
     * 適用後の工程順の版数(プロパティ)を取得する。
     * 
     * @return 適用後の工程順の版数
     */
    public StringProperty afterRevProperty() {
        return afterRev;
    }

    /**
     * 選択状態を返す。
     * 
     * @return 
     */
    public Boolean isSelected() {
        return this.selected.get();
    }

    /**
     * 選択を設定する。
     * 
     * @param value 
     */
    public void setSelected(Boolean value) {
        this.selected.set(value);
    }       

    /**
     * 選択状態(プロパティ)を取得する。
     * 
     * @return 
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * 無効かどうかを返す。
     * 
     * @return 
     */
    public Boolean isDisabled() {
        return this.disabled.get();
    }

    /**
     * 無効状態(プロパティ)を取得する。
     * 
     * @return 
     */
    public BooleanProperty disabledProperty() {
        return disabled;
    }
}
