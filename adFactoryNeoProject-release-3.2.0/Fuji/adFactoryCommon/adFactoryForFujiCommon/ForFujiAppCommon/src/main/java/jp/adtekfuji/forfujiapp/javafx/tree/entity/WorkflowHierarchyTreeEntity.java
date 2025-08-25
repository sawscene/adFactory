/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.tree.entity;

import jp.adtekfuji.forfujiapp.javafx.tree.cell.TreeCellInterface;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;

/**
 * 工程順ツリー用エンティティ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.31.Mon
 */
public class WorkflowHierarchyTreeEntity implements TreeCellInterface {

    private WorkflowHierarchyInfoEntity entity = new WorkflowHierarchyInfoEntity();

    public WorkflowHierarchyTreeEntity(WorkflowHierarchyInfoEntity entity) {
         this.entity = entity;
    }

    @Override
    public String getName() {
        return entity.getHierarchyName();
    }

    @Override
    public Long getHierarchyId() {
        return entity.getWorkflowHierarchyId();
    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public Boolean isHierarchy() {
        return true;
    }

}
