/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 * サービス配置場所の定義
 * 
 * @author ek.mori 
 * @version 1.4.2
 * @since 2016.10.13.Thr
 */
@javax.ws.rs.ApplicationPath("rest")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.AccessHierarchyFujiEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.ProgressMonitorEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.TVerEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.UnitEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.UnitHierarchyEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.UnitTemplateEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.UnitTemplateHierarchyEntityFacadeREST.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.ActualResultEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.AgendaEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.BreaktimeEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.DelayReasonEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.DisplayedStatusEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.EquipmentEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.KanbanEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.KanbanHierarchyEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.OrganizationEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.WorkEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.WorkHierarchyEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.WorkKanbanEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.WorkflowEntityFacade.class);
        resources.add(jp.adtekfuji.adfactoryforfujiserver.service.standard.WorkflowHierarchyEntityFacade.class);
    }

}
