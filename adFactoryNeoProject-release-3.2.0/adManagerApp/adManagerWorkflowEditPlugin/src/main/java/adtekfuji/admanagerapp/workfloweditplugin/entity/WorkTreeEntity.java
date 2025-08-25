/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.entity;

import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.javafxcommon.TreeCellInterface;

/**
 *
 * @author ta.ito
 */
public class WorkTreeEntity implements TreeCellInterface {

    private WorkInfoEntity workInfoEntity = new WorkInfoEntity();

    public WorkTreeEntity(WorkInfoEntity entity) {
        this.workInfoEntity = entity;
    }

    @Override
    public long getHierarchyId() {
        return workInfoEntity.getParentId();
    }

    @Override
    public String getName() {
        return workInfoEntity.getWorkName() + " : " + workInfoEntity.getWorkRev();
    }

    @Override
    public long getChildCount() {
        return 0;
    }

    @Override
    public void setChildCount(long count) {
    }

    @Override
    public Object getEntity() {
        return workInfoEntity;
    }

}
