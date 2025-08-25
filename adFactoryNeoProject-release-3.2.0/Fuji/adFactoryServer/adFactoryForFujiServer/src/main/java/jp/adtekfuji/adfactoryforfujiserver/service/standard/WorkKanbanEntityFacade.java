/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.WorkKanbanInfoFacade;
import java.util.List;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;

/**
 * 工程カンバンFacade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.14.Fri
 */
@Singleton
@Path("kanban/work")
public class WorkKanbanEntityFacade {

    private final WorkKanbanInfoFacade workKanbanFacade = new WorkKanbanInfoFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress());

    public WorkKanbanEntityFacade() {
    }

    /**
     * カンバンIDに該当する工程カンバンの数を取得
     *
     * @param kanbanid 検索するカンバンのID
     * @return 工程カンバンの数
     * @throws Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("flow/count")
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public Long countWorkflow(@QueryParam("kanbanid") Long kanbanid) throws Exception {
        return this.workKanbanFacade.countFlow(kanbanid);
    }

    /**
     * カンバンIDに該当する工程カンバンを取得
     *
     * @param from 頭数
     * @param to 尾数
     * @param kanbanid 検索するカンバンのID
     * @return 工程カンバンの数
     * @throws Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("flow/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkKanbanInfoEntity> findRangeWorkflow(@QueryParam("kanbanid") Long kanbanid, @QueryParam("from") Long from, @QueryParam("to") Long to) throws Exception {
        return this.workKanbanFacade.getRangeFlow(from, to, kanbanid);
    }

    /**
     * カンバンIDに該当する追加工程カンバンの数を取得
     *
     * @param kanbanid 検索するカンバンのID
     * @return 工程カンバンの数
     * @throws Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("separate/count")
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public Long countSeparateWork(@QueryParam("kanbanid") Long kanbanid) throws Exception {
        return this.workKanbanFacade.countSeparate(kanbanid);
    }

    /**
     * カンバンIDに該当する追加工程カンバンを取得
     *
     * @param from 頭数
     * @param to 尾数
     * @param kanbanid 検索するカンバンのID
     * @return 工程カンバンの数
     * @throws Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("separate/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkKanbanInfoEntity> findRangeSeparateWork(@QueryParam("kanbanid") Long kanbanid, @QueryParam("from") Long from, @QueryParam("to") Long to) throws Exception {
        return this.workKanbanFacade.getRangeSeparate(from, to, kanbanid);
    }

    /**
     * 検索数取得
     *
     * @param condition 条件
     * @return 検索数
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public Long countSearch(KanbanSearchCondition condition) throws Exception {
        return this.workKanbanFacade.countSearch(condition);
    }

    /**
     * 指定された範囲のカンバン情報を検索
     *
     * @param condition 条件
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲のカンバン一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkKanbanInfoEntity> findSearchRange(KanbanSearchCondition condition, @QueryParam("from") Long from, @QueryParam("to") Long to) throws Exception {
        return this.workKanbanFacade.findSearchRange(condition, from, to);
    }
}
