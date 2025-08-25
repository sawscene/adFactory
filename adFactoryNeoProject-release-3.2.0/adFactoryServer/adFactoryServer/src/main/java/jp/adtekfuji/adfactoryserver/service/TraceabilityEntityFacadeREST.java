/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.List;
import java.util.Objects;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.entity.kanban.TraceabilityEntity;
import jp.adtekfuji.adFactory.entity.search.TraceabilitySearchCondition;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.model.TraceabilityJdbc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * トレーサビリティREST
 *
 * @author nar-nakamura
 */
@Singleton
@Path("traceability")
public class TraceabilityEntityFacadeREST {

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public TraceabilityEntityFacadeREST() {
    }

    /**
     * 指定されたカンバンIDのトレーサビリティ一覧を取得する。
     *
     * @param kanbanId カンバンID
     * @param isAll 全て取得？(true:全て, false:最新のみ)
     * @param authId 認証ID
     * @return トレーサビリティ一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("kanban")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<TraceabilityEntity> findKanbanTraceability(@QueryParam("id") Long kanbanId, @QueryParam("all") Boolean isAll, @QueryParam("authId") Long authId) {
        logger.info("findKanbanTraceability:{}, {}", kanbanId, isAll);
        try {
            if (Objects.isNull(isAll)) {
                isAll = false;
            }

            TraceabilityJdbc jdbc = TraceabilityJdbc.getInstance();

            return jdbc.getKanbanTraceability(kanbanId, isAll);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定された工程カンバンIDのトレーサビリティ一覧を取得する。
     *
     * @param workKanbanId 工程カンバンID
     * @param isAll 全て取得？(true:全て, false:最新のみ)
     * @param authId 認証ID
     * @return トレーサビリティ一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("kanban/work")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<TraceabilityEntity> findWorkKanbanTraceability(@QueryParam("id") Long workKanbanId, @QueryParam("all") Boolean isAll, @QueryParam("authId") Long authId) {
        logger.info("findWorkKanbanTraceability:{}, {}", workKanbanId, isAll);
        try {
            if (Objects.isNull(isAll)) {
                isAll = false;
            }

            TraceabilityJdbc jdbc = TraceabilityJdbc.getInstance();

            return jdbc.getWorkKanbanTraceability(workKanbanId, isAll);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 検索条件を指定して、トレーサビリティ一覧を取得する。
     *
     * @param condition 検索条件
     * @param authId 認証ID
     * @return トレーサビリティ一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("/search")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<TraceabilityEntity> findSearch(TraceabilitySearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("findSearch:{}", condition);
        try {
            TraceabilityJdbc jdbc = TraceabilityJdbc.getInstance();

            return jdbc.searchTraceability(condition);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 検索条件を指定して、カンバンでグルーピングしたトレーサビリティ一覧を取得する。
     *
     * @param condition 検索条件
     * @param authId 認証ID
     * @return カンバンでグルーピングしたトレーサビリティ一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("/search/kanban")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<TraceabilityEntity> findSearchKanban(TraceabilitySearchCondition condition, @QueryParam("authId") Long authId) {
        logger.info("findSearchKanban:{}", condition);
        try {
            TraceabilityJdbc jdbc = TraceabilityJdbc.getInstance();

            return jdbc.searchKanban(condition);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }
}
