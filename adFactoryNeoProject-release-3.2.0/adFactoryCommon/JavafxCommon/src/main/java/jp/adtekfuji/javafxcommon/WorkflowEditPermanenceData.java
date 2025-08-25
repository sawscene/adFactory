/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon;


import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
import java.util.ResourceBundle;
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
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static WorkflowEditPermanenceData instance = null;
    private final static long ROOT_ID = 0;

    private TreeItem<WorkHierarchyInfoEntity> workHierarchyRootItem;
    private TreeItem<WorkflowHierarchyInfoEntity> workflowHierarchyRootItem;
    private TreeItem<WorkflowHierarchyInfoEntity> liteWorkflowHierarchyRootItem;
    private final TreeItem<TreeCellInterface> workAndHierarchyRootItem;

    private TreeItem<WorkHierarchyInfoEntity> selectedWorkHierarchy;
    private TreeItem<WorkflowHierarchyInfoEntity> selectedWorkflowHierarchy;
    private Long selectedWorkId = null;
    private Long selectedWorkflowId = null;

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

    public void createLiteWorkfrowHierarchy(Long rootId) {
        liteWorkflowHierarchyRootItem = new TreeItem<>(new WorkflowHierarchyInfoEntity(rootId, rb.getString("key.OrderProcessesHierarch")));
    }

    public void updateTitle()
    {
        if(Objects.nonNull(this.workAndHierarchyRootItem)) {
            ((WorkHierarchyInfoEntity) this.workAndHierarchyRootItem.getValue().getEntity()).setHierarchyName(LocaleUtils.getString("key.ProcessHierarch"));
        }

        if(Objects.nonNull(this.workHierarchyRootItem)) {
            this.workHierarchyRootItem.getValue().setHierarchyName(LocaleUtils.getString("key.ProcessHierarch"));
        }

        if(Objects.nonNull(this.workflowHierarchyRootItem)) {
            this.workflowHierarchyRootItem.getValue().setHierarchyName(LocaleUtils.getString("key.OrderProcessesHierarch"));
        }
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

    public TreeItem<WorkflowHierarchyInfoEntity> getLiteWorkflowHierarchyRootItem() {
        return liteWorkflowHierarchyRootItem;
    }

    public void setLiteWorkflowHierarchyRootItem(TreeItem<WorkflowHierarchyInfoEntity> liteWorkflowHierarchyRootItem) {
        this.liteWorkflowHierarchyRootItem = liteWorkflowHierarchyRootItem;
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

    public Long getSelectedWorkId() {
        return selectedWorkId;
    }

    public void setSelectedWorkId(Long selectedWorkId) {
        this.selectedWorkId = selectedWorkId;
    }

    public Long getSelectedWorkflowId() {
        return selectedWorkflowId;
    }

    public void setSelectedWorkflowId(Long selectedWorkflowId) {
        this.selectedWorkflowId = selectedWorkflowId;
    }
}
