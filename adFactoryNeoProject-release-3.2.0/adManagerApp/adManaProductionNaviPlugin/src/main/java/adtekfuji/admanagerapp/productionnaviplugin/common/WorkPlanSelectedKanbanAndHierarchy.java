/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

import adtekfuji.admanagerapp.productionnaviplugin.clientservice.WorkPlanRestAPI;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;

/**
 * @author e.mori
 */
public class WorkPlanSelectedKanbanAndHierarchy {

    private final KanbanInfoEntity kanban;
    private String hierarchyName;
    private Long hierarchyId;
    private WorkPlanScheduleTypeEnum workPlanScheduleType;
    private String compoDecisionName;
    
    private final WorkPlanRestAPI REST_API = new WorkPlanRestAPI();

    public WorkPlanSelectedKanbanAndHierarchy(KanbanInfoEntity kanban, String hierarchyName) {
        this.kanban = kanban;
        this.hierarchyName = hierarchyName;
    }
    
    public WorkPlanSelectedKanbanAndHierarchy(KanbanInfoEntity kanban, WorkPlanScheduleTypeEnum schedule) {
        this.kanban = kanban;
        this.workPlanScheduleType = schedule;
    }

    public WorkPlanSelectedKanbanAndHierarchy(Long kanbanId, WorkPlanScheduleTypeEnum schedule) {
        this.kanban = REST_API.searchKanban(kanbanId);
        this.workPlanScheduleType = schedule;
    }
    
    public WorkPlanSelectedKanbanAndHierarchy(KanbanInfoEntity kanban, String hierarchyName, String compoDecisionName, Long hierarchyId) {
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
    
    public WorkPlanScheduleTypeEnum getWorkPlanScheduleType() {
        return workPlanScheduleType;
    }
    
    public void setWorkPlanScheduleType(WorkPlanScheduleTypeEnum workPlanScheduleType) {
        this.workPlanScheduleType = workPlanScheduleType;
    }
}
