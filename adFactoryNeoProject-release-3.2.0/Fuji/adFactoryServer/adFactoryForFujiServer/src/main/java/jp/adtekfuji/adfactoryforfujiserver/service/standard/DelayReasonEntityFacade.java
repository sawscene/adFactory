/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.DelayReasonInfoFacade;
import java.util.List;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import jp.adtekfuji.adFactory.entity.master.DelayReasonInfoEntity;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;

/**
 * 遅延理由Facade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.18.Tsu
 */
@Singleton
@Path("mst/delay_reason")
public class DelayReasonEntityFacade {

    private final DelayReasonInfoFacade delayReasonFacade = new DelayReasonInfoFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress());

    public DelayReasonEntityFacade() {
    }

    /**
     * 遅延理由全取得
     *
     * @return 遅延理由リスト
     */
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<DelayReasonInfoEntity> findAll() throws Exception {
        return this.delayReasonFacade.findAll();
    }

    /**
     * 指定のIDに該当する遅延理由を取得
     *
     * @param id 遅延理由ID
     * @return IDに該当する遅延理由
     */
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public DelayReasonInfoEntity find(@PathParam("id") Long id) throws Exception {
        return this.delayReasonFacade.find(id);
    }
}
