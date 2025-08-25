/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

import adtekfuji.admanagerapp.productionnaviplugin.entity.WorkHierarchyTreeEntity;
import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
import javafx.scene.control.TreeItem;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ta.ito
 */
public class WorkflowEditPermanenceData {

    private final Logger logger = LogManager.getLogger();
    private static WorkflowEditPermanenceData instance = null;
    private final static long ROOT_ID = 0;

    private TreeItem<WorkHierarchyInfoEntity> workHierarchyRootItem;
    private TreeItem<WorkflowHierarchyInfoEntity> workflowHierarchyRootItem;
    private final TreeItem<TreeCellInterface> workAndHierarchyRootItem;

    private TreeItem<WorkHierarchyInfoEntity> selectedWorkHierarchy;
    private TreeItem<WorkflowHierarchyInfoEntity> selectedWorkflowHierarchy;

    private WorkflowEditPermanenceData() {
        WorkHierarchyInfoEntity root = new WorkHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.ProcessHierarch"));
        workAndHierarchyRootItem = new TreeItem<>(new WorkHierarchyTreeEntity(root));
        workHierarchyRootItem = new TreeItem<>(new WorkHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.ProcessHierarch")));
        workflowHierarchyRootItem = new TreeItem<>(new WorkflowHierarchyInfoEntity(ROOT_ID, LocaleUtils.getString("key.OrderProcessesHierarch")));
    }

    public static WorkflowEditPermanenceData getInstance() {
        if (Objects.isNull(instance)) {
            instance = new WorkflowEditPermanenceData();
        }
        return instance;
    }

    public void updateWorkHierarchy() {
    }

    public TreeItem<WorkHierarchyInfoEntity> getWorkHierarchyRootItem() {
        return workHierarchyRootItem;
    }

    public void setWorkHierarchyRootItem(TreeItem<WorkHierarchyInfoEntity> workRootItem) {
        this.workHierarchyRootItem = workRootItem;
    }

    public TreeItem<WorkflowHierarchyInfoEntity> getWorkflowHierarchyRootItem() {
        return workflowHierarchyRootItem;
    }

    public void setWorkflowHierarchyRootItem(TreeItem<WorkflowHierarchyInfoEntity> workflowHierarchyRootItem) {
        this.workflowHierarchyRootItem = workflowHierarchyRootItem;
    }

    public TreeItem<TreeCellInterface> getWorkAndHierarchyRootItem() {
        return workAndHierarchyRootItem;
    }

    public void setWorkAndHierarchyRootItem(TreeItem<TreeCellInterface> workRootItem) {
        this.workAndHierarchyRootItem.getChildren().clear();
        this.workAndHierarchyRootItem.getChildren().addAll(workRootItem);
    }

    public TreeItem<WorkHierarchyInfoEntity> getSelectedWorkHierarchy() {
        return selectedWorkHierarchy;
    }

    public void setSelectedWorkHierarchy(TreeItem<WorkHierarchyInfoEntity> selectedWorkHierarchy) {
        this.selectedWorkHierarchy = selectedWorkHierarchy;
    }

    public TreeItem<WorkflowHierarchyInfoEntity> getSelectedWorkflowHierarchy() {
        return selectedWorkflowHierarchy;
    }

    public void setSelectedWorkflowHierarchy(TreeItem<WorkflowHierarchyInfoEntity> selectedWorkflowHierarchy) {
        this.selectedWorkflowHierarchy = selectedWorkflowHierarchy;
    }

}
