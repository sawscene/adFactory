/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service.standard;

import adtekfuji.clientservice.AgendaFacade;
import java.util.List;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.entity.agenda.AgendaEntity;
import jp.adtekfuji.adFactory.entity.agenda.KanbanTopicInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanTopicSearchCondition;
import jp.adtekfuji.adfactoryforfujiserver.common.AdFactoryForFujiServerConfig;
import jp.adtekfuji.adfactoryforfujiserver.utility.ExecutionTimeLogging;

/**
 * 実績Facade(サーバー用)
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.19.Wed
 */
@Stateless
@Path("agenda")
public class AgendaEntityFacade {

    private final AgendaFacade agendaFacade = new AgendaFacade(new AdFactoryForFujiServerConfig().getAdFactoryServerAddress());

    public AgendaEntityFacade() {
    }

    /**
     * カンバンのスケジュール情報を取得
     *
     * @param kanbanId カンバンID
     * @param dateString 作業日
     * @return 予定データ
     * @throws Exception
     */
    @GET
    @Path("kanban")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public AgendaEntity findByKanban(@QueryParam("id") Long kanbanId, @QueryParam("date") String dateString) throws Exception {
        return this.agendaFacade.findByKanban(kanbanId, dateString);
    }

    /**
     * 作業者のスケジュール情報を取得
     *
     * @param organizationId 作業者ID
     * @param dateString 作業日
     * @return 予定データ
     * @throws Exception
     */
    @GET
    @Path("organization")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public AgendaEntity findByOrganization(@QueryParam("id") Long organizationId, @QueryParam("date") String dateString) throws Exception {
        return this.agendaFacade.findByOrganization(organizationId, dateString);
    }

    /**
     * 予実情報を検索する
     *
     * @param condition 条件
     * @return 予実情報
     */
    @PUT
    @Path("topic")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanTopicInfoEntity> findTopic(KanbanTopicSearchCondition condition) {
        return this.agendaFacade.findTopic(condition);
    }

    /**
     * 予実情報検索数取得
     *
     * @param condition 条件
     * @return 検索数 
     */
    @PUT
    @Path("topic/count")
    @Consumes({"application/xml", "application/json"})
    @Produces({"text/plain"})
    @ExecutionTimeLogging
    public String countTopic(KanbanTopicSearchCondition condition) {
        return String.valueOf(this.agendaFacade.countTopic(condition));
    }

    /**
     * 指定された範囲の予実情報を検索
     *
     * @param condition 条件
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲の予実情報一覧
     */
    @PUT
    @Path("topic/range")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanTopicInfoEntity> findTopic(KanbanTopicSearchCondition condition, @QueryParam("from") Long from, @QueryParam("to") Long to) {
        return this.agendaFacade.findTopic(condition, from, to);
    }
}
