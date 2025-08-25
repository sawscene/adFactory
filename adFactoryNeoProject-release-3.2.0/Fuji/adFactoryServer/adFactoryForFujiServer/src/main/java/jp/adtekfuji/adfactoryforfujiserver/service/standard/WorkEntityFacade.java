/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.WorkInfoFacade;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;

/**
 * 工程Facade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.18.Tsu
 */
@Singleton
@Path("work")
public class WorkEntityFacade {

    private final WorkInfoFacade workFacade = new WorkInfoFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress());

    public WorkEntityFacade() {
    }

    /**
     * 指定されたIDの工程を取得
     *
     * @param id 工程ID
     * @return 工程
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @ExecutionTimeLogging
    @Produces({"application/xml", "application/json"})
    public WorkInfoEntity find(@PathParam("id") Long id) {
        return this.workFacade.find(id);
    }

    /**
     * 指定された名前の工程を取得
     *
     * @param name 工程名
     * @return 工程
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public WorkInfoEntity findName(@QueryParam("name") String name) {
        return this.workFacade.findName(name);
    }
}
