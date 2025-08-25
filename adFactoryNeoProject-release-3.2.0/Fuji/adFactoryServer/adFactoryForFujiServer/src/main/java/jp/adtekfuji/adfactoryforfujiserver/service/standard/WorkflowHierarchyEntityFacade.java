/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.WorkflowHierarchyInfoFacade;
import java.util.List;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;

/**
 * 工程順階層Facade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 */
@Singleton
@Path("workflow/tree")
public class WorkflowHierarchyEntityFacade {

    private final WorkflowHierarchyInfoFacade workflowHierarchyoFacade = new WorkflowHierarchyInfoFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress());

    public WorkflowHierarchyEntityFacade() {
    }

    /**
     * 工程階層を取得
     *
     * @param parentId 親階層ID
     * @return レスポンス
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkflowHierarchyInfoEntity> findTree(@QueryParam("id") Long parentId) {
        return this.workflowHierarchyoFacade.getAffilationHierarchyRange(parentId, null, null);
    }

    /**
     * 指定した工程順階層名の工程順階層を取得 (階層のみで工程順リストは取得しない)
     *
     * @param name 工程順階層名
     * @return 工程順階層
     */
    @Lock(LockType.READ)
    @GET
    @Path("hierarchy/name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public WorkflowHierarchyInfoEntity findHierarchyName(@QueryParam("name") String name) {
        return this.workflowHierarchyoFacade.findHierarchyName(name);
    }

    /**
     * 階層に含まれる工程順階層の数を取得
     *
     * @param parentId 親階層ID
     * @return 最上位階層の工程順階層数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public Long findTreeCount(@QueryParam("id") Long parentId) {
        return this.workflowHierarchyoFacade.getAffilationHierarchyCount(parentId);
    }

    /**
     * 階層に含まれる工程順階層を範囲指定で取得
     *
     * @param parentId 親階層ID
     * @param from 頭数
     * @param to 尾数
     * @return 最上位階層の工程順階層一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkflowHierarchyInfoEntity> findTreeRange(@QueryParam("id") Long parentId, @QueryParam("from") Long from, @QueryParam("to") Long to) {
        return this.workflowHierarchyoFacade.getAffilationHierarchyRange(parentId, from, to);
    }
}
