/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.common;

import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;

/**
 * 工程順詳細画面に渡すデータ
 *
 * @author ta.ito
 */
public class SelectedWorkflowAndHierarchy {

    public interface RefreshCallback {
        void onRefresh();
    }
        
    private WorkflowInfoEntity workflow;
    private String hierarchyName;
    private RefreshCallback refreshCallback;

    public SelectedWorkflowAndHierarchy(WorkflowInfoEntity workflow, String hierarchyName, RefreshCallback refreshCallback) {
        this.workflow = workflow;
        this.hierarchyName = hierarchyName;
        this.refreshCallback = refreshCallback;
    }

    public WorkflowInfoEntity getWorkflowInfo() {
        return workflow;
    }

    public void setWorkflow(WorkflowInfoEntity workflow) {
        this.workflow = workflow;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public RefreshCallback getRefreshCallback() {
        return refreshCallback;
    }
}
