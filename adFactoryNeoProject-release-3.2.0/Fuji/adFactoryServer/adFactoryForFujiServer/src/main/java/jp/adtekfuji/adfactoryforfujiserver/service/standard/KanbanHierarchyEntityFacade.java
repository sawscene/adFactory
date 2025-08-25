/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
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
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;

/**
 * カンバン階層Facade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.14.Fri
 */
@Singleton
@Path("kanban/tree")
public class KanbanHierarchyEntityFacade {

    private final KanbanHierarchyInfoFacade kanbanHierarchyFacade = new KanbanHierarchyInfoFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress()); 

    public KanbanHierarchyEntityFacade() {
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
    public KanbanHierarchyInfoEntity find(@PathParam("id") Long id) throws Exception {
        return this.kanbanHierarchyFacade.find(id);
    }

    /**
     * カンバン階層を取得
     *
     * @param parentId 親階層ID
     * @return レスポンス
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanHierarchyInfoEntity> findTree(@QueryParam("id") Long parentId) throws Exception {
        return this.kanbanHierarchyFacade.getAffilationHierarchyRange(parentId, null, null);
    }

    /**
     * 指定したカンバン階層名のカンバン階層を取得 (階層のみでカンバンリストは取得しない)
     *
     * @param name カンバン階層名
     * @return カンバン階層
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("hierarchy/name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public KanbanHierarchyInfoEntity findHierarchyName(@QueryParam("name") String name) throws Exception {
        return this.kanbanHierarchyFacade.findHierarchyName(name);
    }

    /**
     * 階層に含まれるカンバン階層の数を取得
     *
     * @param parentId 親階層ID
     * @return 最上位階層のカンバン階層数
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public Long findTreeCount(@QueryParam("id") Long parentId) throws Exception {
        return this.kanbanHierarchyFacade.getAffilationHierarchyCount(parentId);
    }

    /**
     * 階層に含まれるカンバン階層を範囲指定で取得
     *
     * @param parentId 親階層ID
     * @param from 頭数
     * @param to 尾数
     * @return 最上位階層のカンバン階層一覧
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanHierarchyInfoEntity> findTreeRange(@QueryParam("id") Long parentId, @QueryParam("from") Long from, @QueryParam("to") Long to) throws Exception {
        return this.kanbanHierarchyFacade.getAffilationHierarchyRange(parentId, from, to);
    }

    /**
     * 新しいカンバン階層の登録
     *
     * @param entity 登録するカンバン階層情報
     * @return 登録の成否
     * @throws java.lang.Exception
     * @deprecated
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ResponseEntity add(KanbanHierarchyInfoEntity entity) throws Exception {
        return this.kanbanHierarchyFacade.regist(entity);
    }

    /**
     * カンバン階層の更新
     *
     * @param entity 更新するカンバン階層情報
     * @return 更新結果
     * @throws java.lang.Exception
     * @deprecated
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ResponseEntity update(KanbanHierarchyInfoEntity entity) throws Exception {
        return this.kanbanHierarchyFacade.update(entity);
    }

    /**
     * カンバン階層の削除
     *
     * @param id 削除するカンバンのID
     * @return 削除結果
     * @throws java.lang.Exception
     * @deprecated
     */
    @DELETE
    @Path("{id}")
    @ExecutionTimeLogging
    public ResponseEntity delete(@PathParam("id") Long id) throws Exception {
        return this.kanbanHierarchyFacade.delete(this.find(id));
    }
}
