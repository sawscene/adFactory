/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.KanbanInfoFacade;
import java.util.List;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.entity.kanban.KanbanExistCollection;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;

/**
 * カンバンFacade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.14.Fri
 */
@Singleton
@Path("kanban")
public class KanbanEntityFacade {

    private final KanbanInfoFacade kanbanFacade = new KanbanInfoFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress());

    public KanbanEntityFacade() {
    }

    /**
     * カンバン情報取得
     *
     * @param id カンバンID
     * @return IDに該当するカンバン
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @ExecutionTimeLogging
    @Produces({"application/xml", "application/json"})
    public KanbanInfoEntity find(@PathParam("id") Long id) throws Exception {
        return this.kanbanFacade.find(id);
    }

    /**
     * カンバンの名前検索
     *
     * @param name カンバン名
     * @return 検索結果
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public KanbanInfoEntity findName(@QueryParam("name") String name) throws Exception {
        return this.kanbanFacade.findName(name);
    }

    /**
     * 新しいカンバンの登録
     *
     * @param entity 登録するカンバン情報
     * @return 登録の成否
     * @throws java.lang.Exception
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ResponseEntity add(KanbanInfoEntity entity) throws Exception {
        return this.kanbanFacade.regist(entity);
    }

    /**
     * カンバンの更新
     *
     * @param entity 更新するカンバンの情報
     * @return 更新結果
     * @throws java.lang.Exception
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ResponseEntity update(KanbanInfoEntity entity) throws Exception {
        return this.kanbanFacade.update(entity);
    }

    /**
     * カンバンの削除
     *
     * @param id 削除するカンバンのID
     * @return 削除結果
     * @throws java.lang.Exception
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ResponseEntity remove(@PathParam("id") Long id) throws Exception {
        return this.kanbanFacade.delete(id);
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
    public String countSearch(KanbanSearchCondition condition) throws Exception {
        return String.valueOf(this.kanbanFacade.countSearch(condition));
    }

    /**
     * 指定された範囲のカンバン情報を検索
     *
     * @param condition 条件
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲のカンバン一覧
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @PUT
    @Path("search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanInfoEntity> findSearchRange(KanbanSearchCondition condition, @QueryParam("from") Long from, @QueryParam("to") Long to) throws Exception {
        return this.kanbanFacade.findSearchRange(condition, from, to);
    }

    /**
     * 指定された範囲のカンバン情報を検索(実績情報付)
     *
     * @param condition 条件
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲のカンバン一覧
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @PUT
    @Path("/results/search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanInfoEntity> searchResults(KanbanSearchCondition condition, @QueryParam("from") Integer from, @QueryParam("to") Integer to) throws Exception {
        return this.kanbanFacade.searchResults(condition, from, to);
    }

    /**
     * 実績の登録
     *
     * @param report 登録する実績情報
     * @return 登録の成否
     * @throws java.lang.Exception
     */
    @POST
    @Path("report")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ActualProductReportResult report(ActualProductReportEntity report) throws Exception {
        return this.kanbanFacade.report(report);
    }

    /**
     * カンバンの存在確認
     *
     * @param entity 存在確認用情報
     * @return 存在確認用情報(ID付与)
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @PUT
    @Path("exist")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public KanbanExistCollection exist(KanbanExistCollection entity) throws Exception {
        return this.kanbanFacade.exist(entity);
    }
}
