/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.Objects;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualAditionEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程実績付加情報REST
 *
 */
@Stateless
@Path("actual/adition")
public class ActualAditionEntityFacadeREST extends AbstractFacade<ActualAditionEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public ActualAditionEntityFacadeREST() {
        super(ActualAditionEntity.class);
    }

    /**
     * 工程実績付加情報を登録する。
     *
     * @param entity 工程実績付加情報
     */
    @ExecutionTimeLogging
    public void add(ActualAditionEntity entity) {
        logger.info("add: {}", entity);
        super.create(entity);
        this.em.flush();
    }

    /**
     * カンバンIDとタグ名で実績付加情報のRAWデータを取得する
     *
     * @param kanbanId カンバンID
     * @param tag タグ名
     * @param authId 認証ID
     * @return byte[] 実績付加情報のRAWデータ
     */
    @GET
    @Path("raw")
    @Produces("application/octet-stream")
    @ExecutionTimeLogging
    public Response getRawData(@QueryParam("kanbanId") Long kanbanId, @QueryParam("tag") String tag, @QueryParam("authId") Long authId) {
        logger.info("getRawData: kanbanId={}, tag={}, authId={}", kanbanId, tag, authId);

        TypedQuery<ActualAditionEntity> query;
        try {
            if (Objects.isNull(kanbanId)) {
                logger.error("getRawData:{}", kanbanId);
                return Response.status(400).build();
            }

            if (Objects.isNull(tag)) {
                logger.error("getRawData:{}", tag);
                return Response.status(400).build();
            }

            query = this.em.createNamedQuery("ActualAditionEntity.findByTag", ActualAditionEntity.class);
            query.setParameter("kanbanId", kanbanId);
            query.setParameter("tag", tag);
            query.setMaxResults(1);

            ActualAditionEntity result = query.getSingleResult();

            return Response.ok().entity(result.getRawData()).header("Content-disposition", "attachment; filename=" + result.getDataName()).build();

        } catch (NoResultException ex) {
            logger.info("no result.");
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().build();
        }
    }

    /**
     * 工程実績IDから付加情報を取得
     * @param actualId 工程実績ID
     * @param authId 承認ID
     * @return 工程実績ID
     */
    @GET
    @Path("actual")
    @Produces("application/octet-stream")
    @ExecutionTimeLogging
    public Response findByActualId(@QueryParam("id") Long actualId, @QueryParam("tag") String tag, @QueryParam("authId") Long authId) {
        logger.info("actualId: kanbanId={}, tag={}, authId={}", actualId, tag, authId);
        try {
            // 工程実績付加情報取得
            TypedQuery<ActualAditionEntity> query = this.em.createNamedQuery("ActualAditionEntity.findByActualID", ActualAditionEntity.class);
            query.setParameter("actualID", actualId);
            query.setParameter("tag", tag);
            query.setMaxResults(1);
            ActualAditionEntity result = query.getSingleResult();
            return Response.ok().entity(result.getRawData()).header("Content-disposition", "attachment; filename=" + result.getDataName()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().build();
        }
    }


    /**
     * 付加情報IDで実績付加情報のRAWデータを取得する
     *
     * @param aditionId 付加情報ID
     * @param authId 認証ID
     * @return byte[] 実績付加情報のRAWデータ
     */
    @GET
    @Path("raw/{id}")
    @Produces("application/octet-stream")
    @ExecutionTimeLogging
    public Response getRawDataByAditionId(@PathParam("id") Long aditionId, @QueryParam("authId") Long authId) {
        logger.info("getRawDataByAditionId: aditionId={}, authId={}", aditionId, authId);

        try {
            if (Objects.isNull(aditionId)) {
                logger.error("getRawDataByAditionId:{}", aditionId);
                return Response.status(400).build();
            }
            
            ActualAditionEntity result = super.find(aditionId);

            return Response.ok().entity(result.getRawData()).header("Content-disposition", "attachment; filename=" + result.getDataName()).build();

        } catch (NoResultException ex) {
            logger.info("no result.");
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().build();
        }
    }

    /**
     * カンバンIDを指定して、工程実績付加情報を削除する。
     *
     * @param kanbanId カンバンID
     */
    public void removeKanbanActualAditions(Long kanbanId) {
        logger.info("removeKanbanActualAditions: kanbanId={}", kanbanId);

        Query query = this.em.createNamedQuery("ActualAditionEntity.removeByKanbanId");
        query.setParameter("kanbanId", kanbanId);
        query.executeUpdate();
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }
}
