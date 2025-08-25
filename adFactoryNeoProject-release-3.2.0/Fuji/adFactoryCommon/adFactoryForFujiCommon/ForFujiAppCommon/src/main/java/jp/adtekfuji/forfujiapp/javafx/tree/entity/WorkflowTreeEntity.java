/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.tree.entity;

import jp.adtekfuji.forfujiapp.javafx.tree.cell.TreeCellInterface;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;

/**
 * 工程順ツリー表示用エンティティ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class WorkflowTreeEntity implements TreeCellInterface {

    private WorkflowInfoEntity entity = new WorkflowInfoEntity();

    public WorkflowTreeEntity(WorkflowInfoEntity workflowInfo) {
        this.entity = workflowInfo;
    }

    @Override
    public String getName() {
        return this.entity.getWorkflowName();
    }

    @Override
    public Long getHierarchyId() {
        return this.entity.getParentId();
    }

    @Override
    public Object getEntity() {
        return this.entity;
    }

    @Override
    public Boolean isHierarchy() {
        return false;
    }

}
