/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.EquipmentInfoFacade;
import java.util.List;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;

/**
 * 設備Facade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.18.Tue
 */
@Singleton
@Path("equipment")
public class EquipmentEntityFacade {

    private final EquipmentInfoFacade equipmentFacade = new EquipmentInfoFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress());

    public EquipmentEntityFacade() {
    }

    /**
     * IDで指定された階層の設備個数取得
     *
     * @param parentId 設備ID
     * @return parentId->IsNull:最上位階層数取得/NonNull:指定された階層数取得
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("tree/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public Long findTreeCount(@QueryParam("id") Long parentId) throws Exception {
        return this.equipmentFacade.getAffilationHierarchyCount(parentId);
    }

    /**
     * 階層に含まれる設備階層を範囲指定で取得
     *
     * @param parentId 親階層ID
     * @param from 頭数
     * @param to 尾数
     * @return 最上位階層の設備階層一覧
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("tree/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<EquipmentInfoEntity> findTreeRange(@QueryParam("id") Long parentId, @QueryParam("from") Long from, @QueryParam("to") Long to) throws Exception {
        return this.equipmentFacade.getAffilationHierarchyRange(parentId, from, to, false);
    }

    /**
     * 指定されたIDの設備を取得
     *
     * @param id 設備ID
     * @return レスポンス
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public EquipmentInfoEntity find(@QueryParam("id") Long id) throws Exception {
        return this.equipmentFacade.find(id);
    }

    /**
     * 指定した設備名の設備を取得
     *
     * @param name 設備名
     * @return 設備
     * @throws java.lang.Exception
     */
    @Lock(LockType.READ)
    @GET
    @Path("/name")
    @Produces({"application/xml", "application/json"})
    public EquipmentInfoEntity findName(@QueryParam("name") String name) throws Exception {
        return this.equipmentFacade.findName(name);
    }

    /**
     * 全組設備取得
     *
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public Long count() {
        return this.equipmentFacade.count();
    }

    /**
     * 指定された範囲の設備情報を取得
     *
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲の設備一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<EquipmentInfoEntity> findRange(@QueryParam("from") Long from, @QueryParam("to") Long to) {
        return this.equipmentFacade.findRange(from, to);
    }
}
