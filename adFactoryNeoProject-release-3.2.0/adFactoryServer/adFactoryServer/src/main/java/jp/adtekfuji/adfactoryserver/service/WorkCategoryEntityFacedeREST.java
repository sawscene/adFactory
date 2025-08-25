/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
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
import jp.adtekfuji.adfactoryserver.entity.indirectwork.WorkCategoryEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業区分マスタ REST API
 *
 * @author s-heya
 */
@Stateless
@Path("workcategory")
public class WorkCategoryEntityFacedeREST extends AbstractFacade<WorkCategoryEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private IndirectWorkEntityFacadeREST indirectWorkREST;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public WorkCategoryEntityFacedeREST() {
        super(WorkCategoryEntity.class);
    }

    /**
     * コンストラクタ
     *
     * @param entityManager
     * @param indirectWorkREST
     */
    public WorkCategoryEntityFacedeREST(EntityManager entityManager, IndirectWorkEntityFacadeREST indirectWorkREST) {
        super(WorkCategoryEntity.class);
        this.em = entityManager;
        this.indirectWorkREST = indirectWorkREST;
    }

    /**
     * 作業区分を追加する。
     *
     * @param entity 作業区分情報エンティティ
     * @param authId 認証ID
     * @return 成功：200 + 追加したモノタイプのURI/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException 戻り値のURIが,文字列を URI 参照として解析できなかった場合例外を発生します
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(WorkCategoryEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            if (Objects.nonNull(entity.getWorkCategoryId())) {
                TypedQuery<Long> query1 = this.em.createNamedQuery("WorkCategoryEntity.countById", Long.class);
                query1.setParameter("workCategoryId", entity.getWorkCategoryId());
                if (query1.getSingleResult() > 0) {
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
                }
            }

            TypedQuery<Long> query2 = this.em.createNamedQuery("WorkCategoryEntity.countByName", Long.class);
            query2.setParameter("workCategoryName", entity.getWorkCategoryName());
            if (query2.getSingleResult() > 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NAME_OVERLAP)).build();
            }

            super.create(entity);
            this.em.flush();

            URI uri = new URI("workcategory/" + entity.getWorkCategoryId());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 作業区分を更新する。
     *
     * @param entity 作業区分情報エンティティ
     * @param authId 認証ID
     * @return 成功：200 失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(WorkCategoryEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            TypedQuery<WorkCategoryEntity> query = this.em.createNamedQuery("WorkCategoryEntity.findById", WorkCategoryEntity.class);
            query.setParameter("workCategoryId", entity.getWorkCategoryId());
            WorkCategoryEntity target = query.getSingleResult();
            if (Objects.isNull(target.getWorkCategoryId())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
            }

            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            TypedQuery<Long> query2 = this.em.createNamedQuery("WorkCategoryEntity.countByKey", Long.class);
            query2.setParameter("workCategoryId", entity.getWorkCategoryId());
            query2.setParameter("workCategoryName", entity.getWorkCategoryName());
            if (query2.getSingleResult() > 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NAME_OVERLAP)).build();
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 作業区分を更新する。
            super.edit(entity);
            this.em.flush();

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 作業区分を削除する。
     *
     * @param id 作業区分ID
     * @param authId 認証ID
     * @return 成功：200 失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("remove: id={}, authId={}", id, authId);
        try {
            //組織の関連付けを検索。関連付けがある場合は削除不可
            Query query = this.em.createNamedQuery("ConOrganizationWorkCategoryEntity.findByWorkCategoryId");
            query.setParameter("workCategoryId", id);
            if (query.getResultList().size() > 0) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_RELATION_DELETE)).build();
            }

            long count = Long.parseLong(this.indirectWorkREST.countCategory(Arrays.asList(id), authId));
            if (!(count > 0)) {
                WorkCategoryEntity target = super.find(id);
                if (Objects.nonNull(target)) {
                    super.remove(target);
                }
                return Response.ok().entity(ResponseEntity.success()).build();
            }

            // 削除不可
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_CHILD_DELETE)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定したIDの作業区分を取得する。
     *
     * @param id 作業区分ID
     * @param authId 認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public WorkCategoryEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);
        try {
            WorkCategoryEntity entity = super.find(id);
            if (Objects.isNull(entity)) {
                return new WorkCategoryEntity();
            }
            return entity;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 指定した名前の情報を検索する。
     *
     * @param name 検索するモノタイプの名前
     * @param authId 認証ID
     * @return 検索に該当するモノタイプ情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public WorkCategoryEntity findName(@QueryParam("name") String name, @QueryParam("authId") Long authId) {
        logger.info("findName: name={}, authId={}", name, authId);
        try {
            TypedQuery<WorkCategoryEntity> query = this.em.createNamedQuery("WorkCategoryEntity.findByName", WorkCategoryEntity.class);
            query.setParameter("workCategoryName", name);

            WorkCategoryEntity entity = query.getSingleResult();
            if (Objects.isNull(entity)) {
                return new WorkCategoryEntity();
            }
            return entity;
        } catch (Exception ex) {
            logger.fatal(ex);
            return new WorkCategoryEntity();
        }
    }

    /**
     * 作業区分を全取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkCategoryEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);
        try {
            List<WorkCategoryEntity> entities;
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                entities = super.findRange(from, to);
            } else {
                entities = super.findAll();
            }

            entities.sort(Comparator.comparing(entity -> entity.getWorkCategoryName()));
            return entities;
        } catch (Exception ex) {
            logger.fatal(ex);
            throw ex;
        }
    }

    /**
     * 登録されているモノタイプの一覧を取得する。
     *
     * @param authId 認証ID
     * @return
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countAll(@QueryParam("authId") Long authId) {
        logger.info("count: authId={}", authId);
        return String.valueOf(super.count());
    }

    /**
     * エンティティマネージャーを渡します
     *
     * @return エンティティマネージャー
     */
    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
