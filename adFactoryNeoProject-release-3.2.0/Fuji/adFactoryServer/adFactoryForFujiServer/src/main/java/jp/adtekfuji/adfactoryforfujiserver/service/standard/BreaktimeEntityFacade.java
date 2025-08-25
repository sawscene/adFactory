/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.BreaktimeInfoFacade;
import java.util.List;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;

/**
 * 休憩Facade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.18.Tsu
 */
@Singleton
@Path("mst/breaktime")
public class BreaktimeEntityFacade {

    private final BreaktimeInfoFacade breaktimeFacade = new BreaktimeInfoFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress());

    public BreaktimeEntityFacade() {
    }

    /**
     * 休憩設定全取得
     *
     * @return 休憩設定リスト
     * @throws java.lang.Exception
     */
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<BreakTimeInfoEntity> findAll() throws Exception {
        return this.breaktimeFacade.findAll();
    }

    /**
     * 指定のIDに該当する休憩設定を取得
     *
     * @param id 休憩設定ID
     * @return IDに該当する休憩設定
     * @throws java.lang.Exception
     */
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public BreakTimeInfoEntity find(@PathParam("id") Long id) throws Exception {
        return this.breaktimeFacade.find(id);
    }
}
