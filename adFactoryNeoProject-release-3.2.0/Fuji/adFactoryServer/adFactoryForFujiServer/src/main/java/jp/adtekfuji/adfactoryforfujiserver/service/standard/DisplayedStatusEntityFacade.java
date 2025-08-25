/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import java.util.List;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;

/**
 * ステータス表示取得Facade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.21.Fri
 */
@Singleton
@Path("mst/status")
public class DisplayedStatusEntityFacade {

    private final DisplayedStatusInfoFacade displayedStatusFacade = new DisplayedStatusInfoFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress());

    public DisplayedStatusEntityFacade() {
    }

    /**
     * 表示用ステータス全取得
     *
     * @return 表示用ステータスリスト
     */
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<DisplayedStatusInfoEntity> findAll() {
        return this.displayedStatusFacade.findAll();
    }

    /**
     * 指定のIDに該当する表示用ステータスを取得
     *
     * @param id 表示用ステータスID
     * @return IDに該当する表示用ステータス
     */
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public DisplayedStatusInfoEntity find(long id) {
        return this.displayedStatusFacade.find(id);
    }
}
