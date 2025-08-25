/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Application サブクラス
 * 
 * @author ke.yokoi
 */
@ApplicationPath("rest")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * リソースクラスを登録する
     * 
     * @param resources 
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(jp.adtekfuji.adfactoryserver.service.AccessHierarchyEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.ActualAditionEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.ActualResultEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.AgendaFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.AndonLineMonitorFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.ApprovalFlowFacade.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.ApprovalRouteFacade.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.BreaktimeEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.ChartFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.DefectFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.DelayReasonEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.DirectActualEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.DisplayedStatusEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.DsItemFacade.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.EquipmentEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.EquipmentTypeEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.FormFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.HolidayEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.IndirectActualEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.IndirectWorkEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.InterruptReasonEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.KanbanEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.KanbanHierarchyEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.KanbanReportEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.LabelEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.LedgerEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.LedgerFileEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.LedgerHierarchyEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.ObjectEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.ObjectTypeEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.OperationEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.OrganizationEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.PartsEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.ReasonCategoryEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.ReasonMasterEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.ResourceEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.RoleEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.ScheduleEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.SummaryReportFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.SystemResource.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.TraceabilityEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.WorkCategoryEntityFacedeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.WorkEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.WorkHierarchyEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.WorkKanbanEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.WorkReportEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.WorkflowEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.WorkflowHierarchyEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryserver.service.warehouse.WarehouseFacede.class);
    }
}
