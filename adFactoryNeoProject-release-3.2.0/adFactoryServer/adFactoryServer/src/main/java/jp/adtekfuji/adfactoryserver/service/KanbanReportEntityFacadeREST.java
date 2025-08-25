/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.StringUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanReportEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * カンバン帳票情報REST
 *
 * @author nar-nakamura
 */
@Stateless
@Path("report-file")
public class KanbanReportEntityFacadeREST extends AbstractFacade<KanbanReportEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public KanbanReportEntityFacadeREST() {
        super(KanbanReportEntity.class);
    }

    /**
     * カンバン帳票情報を登録する。
     *
     * @param entity カンバン帳票情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException 
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(KanbanReportEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            // カンバン帳票情報を登録する。
            super.create(entity);
            this.em.flush();

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("report-file/").append(entity.getKanbanReportId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * カンバン帳票情報を更新する。
     *
     * @param entity カンバン帳票情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(KanbanReportEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // カンバン帳票情報を更新する。
            super.edit(entity);

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定したカンバン帳票IDのカンバン帳票情報を削除する。
     *
     * ※.同じファイルパスのカンバン帳票情報も削除する。
     *
     * @param id カンバン帳票ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("remove: id={}, authId={}", id, authId);
        try {
            KanbanReportEntity entity = super.find(id);

            if (StringUtils.isEmpty(entity.getFilePath())) {
                // カンバン帳票情報を削除する。
                super.remove(entity);
            } else {
                // ファイルパスを指定して、カンバン帳票情報を削除する。
                Query query = this.em.createNamedQuery("KanbanReportEntity.removeByFilePath");
                query.setParameter("filePath", entity.getFilePath());
                query.executeUpdate();
            }

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * カンバンID一覧を指定して、カンバン帳票情報の件数を取得する。
     *
     * @param kanbanIds カンバンID一覧
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("kanban-id/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countByKanbanId(@QueryParam("id") List<Long> kanbanIds, @QueryParam("authId") Long authId) {
        logger.info("countByKanbanId: kanbanIds={}, authId={}", kanbanIds, authId);

        TypedQuery<Long> query = this.em.createNamedQuery("KanbanReportEntity.countByKanbanIds", Long.class);
        query.setParameter("kanbanIds", kanbanIds);

        return String.valueOf(query.getSingleResult());
    }

    /**
     * カンバンID一覧を指定して、カンバン帳票情報一覧を取得する。
     * (from, to のどちらかが null の場合は全件取得)
     *
     * @param kanbanIds カンバンID一覧
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return カンバン帳票情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("kanban-id/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<KanbanReportEntity> findByKanbanId(@QueryParam("id") List<Long> kanbanIds, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findByKanbanId: kanbanIds={}, from={}, to={}, authId={}", kanbanIds, from, to, authId);

        TypedQuery<KanbanReportEntity> query = this.em.createNamedQuery("KanbanReportEntity.findByKanbanIds", KanbanReportEntity.class);
        query.setParameter("kanbanIds", kanbanIds);

        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        return query.getResultList();
    }

    /**
     * カンバンID一覧を指定して、カンバン帳票情報一覧を削除する。
     *
     * @param kanbanIds カンバンID一覧
     */
    public void removeByKanbanId(List<Long> kanbanIds) {
        logger.info("removeByKanbanId: kanbanIds={}", kanbanIds);

        Query query = this.em.createNamedQuery("KanbanReportEntity.removeByKanbanIds");
        query.setParameter("kanbanIds", kanbanIds);
        query.executeUpdate();
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
