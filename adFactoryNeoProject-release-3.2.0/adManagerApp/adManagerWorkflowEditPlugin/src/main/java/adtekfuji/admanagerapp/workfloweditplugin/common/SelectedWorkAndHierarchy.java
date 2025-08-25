/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.common;

import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;

/**
 * 工程詳細画面に渡すデータ
 * @author ta.ito
 */
public class SelectedWorkAndHierarchy {

    private WorkInfoEntity work;
    private String hierarchyName;

    public SelectedWorkAndHierarchy(WorkInfoEntity work, String hierarchyName) {
        this.work = work;
        this.hierarchyName = hierarchyName;
    }

    public WorkInfoEntity getWorkInfo() {
        return work;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

}
