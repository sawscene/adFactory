/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.ActualResultInfoFacade;
import java.util.List;
import javax.ejb.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;

/**
 * 実績Facade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.17.Mon
 */
@Singleton
@Path("actual")
public class ActualResultEntityFacade {

    private final ActualResultInfoFacade actualResultFacade = new ActualResultInfoFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress());

    public ActualResultEntityFacade() {
    }

    /**
     * 実績情報を検索する
     *
     * @param condition 検索条件
     * @return 実績
     * @throws java.lang.Exception
     */
    @PUT
    @Path("search")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ActualResultEntity> findSearch(ActualSearchCondition condition) throws Exception {
        return this.actualResultFacade.search(condition);
    }

    /**
     * 検索数取得
     *
     * @param condition 検索条件
     * @return 検索数
     * @throws java.lang.Exception
     */
    @PUT
    @Path("search/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public Long countSearch(ActualSearchCondition condition) throws Exception {
        return this.actualResultFacade.searchCount(condition);
    }

    /**
     * 指定された範囲の実績情報を検索
     *
     * @param condition 検索条件
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲の実績一覧
     * @throws java.lang.Exception
     */
    @PUT
    @Path("search/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ActualResultEntity> findSearchRange(ActualSearchCondition condition, @QueryParam("from") Long from, @QueryParam("to") Long to) throws Exception {
        return this.actualResultFacade.searchRange(condition, from, to);
    }
}
