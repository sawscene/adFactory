/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.entity;

import adtekfuji.admanagerapp.productionnaviplugin.common.TreeCellInterface;
import jp.adtekfuji.adFactory.entity.work.WorkHierarchyInfoEntity;

/**
 *
 * @author ta.ito
 */
public class WorkHierarchyTreeEntity implements TreeCellInterface {

    private WorkHierarchyInfoEntity workHierarchyInfoEntity = new WorkHierarchyInfoEntity();

    public WorkHierarchyTreeEntity(WorkHierarchyInfoEntity entity) {
        this.workHierarchyInfoEntity = entity;
    }

    @Override
    public long getHierarchyId() {
        return this.workHierarchyInfoEntity.getWorkHierarchyId();
    }

    @Override
    public String getName() {
        return this.workHierarchyInfoEntity.getHierarchyName();
    }

    @Override
    public long getChildCount() {
        return this.workHierarchyInfoEntity.getChildCount();
    }

    @Override
    public void setChildCount(long count) {
        this.workHierarchyInfoEntity.setChildCount(count);
    }

    @Override
    public Object getEntity() {
        return this.workHierarchyInfoEntity;
    }
}
