/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.common;

import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;

/**
 * @author e.mori
 */
public class SelectedKanbanAndHierarchy {

    private final KanbanInfoEntity kanban;
    private final String hierarchyName;
    private Long hierarchyId;
    private String compoDecisionName;

    public SelectedKanbanAndHierarchy(KanbanInfoEntity kanban, String hierarchyName) {
        this.kanban = kanban;
        this.hierarchyName = hierarchyName;
    }

    public SelectedKanbanAndHierarchy(KanbanInfoEntity kanban, String hierarchyName, String compoDecisionName, Long hierarchyId) {
        this.kanban = kanban;
        this.hierarchyName = hierarchyName;
        this.compoDecisionName = compoDecisionName;
        this.hierarchyId = hierarchyId;
    }

    public KanbanInfoEntity getKanbanInfo() {
        return kanban;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public String getCompoDecisionName() {
        return compoDecisionName;
    }

    public Long getHierarchyId() {
        return hierarchyId;
    }
}
